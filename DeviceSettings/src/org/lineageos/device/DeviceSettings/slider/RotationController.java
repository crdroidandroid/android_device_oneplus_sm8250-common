/*
 * Copyright (C) 2018-2024 crDroid Android Project
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
import android.os.RemoteException;
import android.util.Log;
import android.view.IWindowManager;
import android.view.Surface;
import android.view.WindowManagerGlobal;

import org.lineageos.device.DeviceSettings.Constants;
import org.lineageos.device.DeviceSettings.SliderControllerBase;

public final class RotationController extends SliderControllerBase {

    public static final int ID = 4;

    private static final String TAG = "RotationController";

    private static final int ROTATION_AUTO = 40;
    private static final int ROTATION_0 = 41;
    private static final int ROTATION_90 = 42;
    private static final int ROTATION_270 = 43;

    public RotationController(Context context) {
        super(context);
    }

    @Override
    protected int processAction(int action) {
        Log.d(TAG, "slider action: " + action);
        switch (action) {
            case ROTATION_AUTO:
                return setRotation(false, 0) ? Constants.MODE_ROTATION_AUTO : 0;
            case ROTATION_0:
                return setRotation(true, Surface.ROTATION_0) ? Constants.MODE_ROTATION_0 : 0;
            case ROTATION_90:
                return setRotation(true, Surface.ROTATION_90) ? Constants.MODE_ROTATION_90 : 0;
            case ROTATION_270:
                return setRotation(true, Surface.ROTATION_270) ? Constants.MODE_ROTATION_270 : 0;
        }
        return 0;
    }

    @Override
    public void reset() {
        setRotation(false, 0);
    }

    private boolean setRotation(boolean locked, int rotation) {
        try {
            IWindowManager wm = WindowManagerGlobal.getWindowManagerService();
            if (locked) {
                wm.freezeRotation(rotation, TAG);
            } else {
                wm.thawRotation(TAG);
            }
            return true;
        } catch (RemoteException exc) {
            Log.w(TAG, "Unable to save auto-rotate setting");
            return false;
        }
    }
}
