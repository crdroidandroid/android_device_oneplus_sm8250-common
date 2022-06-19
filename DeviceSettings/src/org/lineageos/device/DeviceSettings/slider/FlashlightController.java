/*
 * Copyright (C) 2018-2023 crDroid Android Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lineageos.device.DeviceSettings.slider;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

import org.lineageos.device.DeviceSettings.Constants;
import org.lineageos.device.DeviceSettings.SliderControllerBase;

public final class FlashlightController extends SliderControllerBase {

    public static final int ID = 2;

    private static final String TAG = "FlashlightController";

    private static final int FLASHLIGHT_OFF = 20;
    private static final int FLASHLIGHT_ON = 21;
    private static final int FLASHLIGHT_BLINK = 22;

    private static final long BLINK_INTERVAL = 250L;

    private final CameraManager mCameraManager;

    private String mCameraId;
    private boolean mTorchEnabled = false;

    private PowerManager.WakeLock mWakeLock;

    private final Handler mBlinkHandler = new Handler();
    private final Runnable mBlinkRunnble = new Runnable() {
        @Override
        public void run() {
            if (setTorchMode(!mTorchEnabled)) {
                mBlinkHandler.postDelayed(this, BLINK_INTERVAL);
            } else {
                mWakeLock.release();
            }
        }
    };

    public FlashlightController(Context context) {
        super(context);
        mCameraManager = context.getSystemService(CameraManager.class);
        PowerManager pm = context.getSystemService(PowerManager.class);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
    }

    @Override
    protected int processAction(int action) {
        Log.d(TAG, "slider action: " + action);
        boolean succeed;
        switch (action) {
            case FLASHLIGHT_OFF:
                succeed = setTorchMode(false);
                mBlinkHandler.removeCallbacksAndMessages(null);
                if (mWakeLock.isHeld()) {
                    mWakeLock.release();
                }
                return succeed ? Constants.MODE_FLASHLIGHT_OFF : 0;
            case FLASHLIGHT_ON:
                mCameraId = getCameraId();
                succeed = setTorchMode(true);
                mBlinkHandler.removeCallbacksAndMessages(null);
                if (mWakeLock.isHeld()) {
                    mWakeLock.release();
                }
                return succeed ? Constants.MODE_FLASHLIGHT_ON : 0;
            case FLASHLIGHT_BLINK:
                mBlinkHandler.removeCallbacksAndMessages(null);
                mCameraId = getCameraId();
                if (setTorchMode(true)) {
                    mWakeLock.acquire();
                    mBlinkHandler.postDelayed(mBlinkRunnble, BLINK_INTERVAL);
                    return Constants.MODE_FLASHLIGHT_BLINK;
                } else {
                    return 0;
                }
            default:
                return 0;
        }
    }

    @Override
    public void reset() {
        setTorchMode(false);
        mBlinkHandler.removeCallbacksAndMessages(null);
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    private boolean setTorchMode(boolean enabled) {
        if (mCameraId == null) {
            Log.e(TAG, "Camera is not available");
            return false;
        }

        try {
            mCameraManager.setTorchMode(mCameraId, enabled);
            mTorchEnabled = enabled;
            return true;
        } catch (CameraAccessException e) {
            Log.e(TAG, "Failed to turn flashlight " + (enabled ? "on" : "off"), e);
            return false;
        }
    }

    private String getCameraId() {
        try {
            for (final String cameraId : mCameraManager.getCameraIdList()) {
                final CameraCharacteristics characteristics =
                        mCameraManager.getCameraCharacteristics(cameraId);
                final boolean hasFlash = characteristics.get(
                        CameraCharacteristics.FLASH_INFO_AVAILABLE);
                final int facing = characteristics.get(
                        CameraCharacteristics.LENS_FACING);
                if (hasFlash && facing == CameraCharacteristics.LENS_FACING_BACK) {
                    return cameraId;
                }
            }
            Log.e(TAG, "No camera with flashlight found");
        } catch (CameraAccessException e) {
            Log.e(TAG, "Failed to get camera", e);
        }

        return null;
    }
}
