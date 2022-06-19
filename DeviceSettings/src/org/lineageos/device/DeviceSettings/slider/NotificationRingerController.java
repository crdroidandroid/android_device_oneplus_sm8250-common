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

import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import org.lineageos.device.DeviceSettings.Constants;
import org.lineageos.device.DeviceSettings.SliderControllerBase;

public final class NotificationRingerController extends SliderControllerBase {

    public static final int ID = 6;

    private static final String TAG = "NotificationRingerController";

    private static final int NOTIFICATION_TOTAL_SILENCE = 60;
    private static final int NOTIFICATION_PRIORITY_ONLY = 62;
    private static final int NOTIFICATION_ALL = 63;
    private static final int RINGER_VIBRATE = 64;
    private static final int RINGER_SILENT = 65;
    private static final int CHANGE_DELAY = 100;

    private final NotificationManager mNotificationManager;
    private final AudioManager mAudioManager;
    private Handler mHandler;
    private int mRingMode;
    private int mZenMode;

    public NotificationRingerController(Context context) {
        super(context);
        mHandler = new Handler();
        mNotificationManager = context.getSystemService(NotificationManager.class);
        mAudioManager = context.getSystemService(AudioManager.class);
    }

    @Override
    protected int processAction(int action) {
        Log.d(TAG, "slider action: " + action);

        switch (action) {
            case RINGER_VIBRATE:
                mRingMode = RINGER_VIBRATE;
                mNotificationManager.setZenMode(Settings.Global.ZEN_MODE_OFF, null, TAG);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mRingMode != RINGER_VIBRATE) return;
                        mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_VIBRATE);
                    }
                }, CHANGE_DELAY);
                return Constants.MODE_VIBRATE;
            case RINGER_SILENT:
                mRingMode = RINGER_SILENT;
                mNotificationManager.setZenMode(Settings.Global.ZEN_MODE_OFF, null, TAG);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mRingMode != RINGER_SILENT) return;
                        mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_SILENT);
                    }
                }, CHANGE_DELAY);
                return Constants.MODE_SILENT;
            case NOTIFICATION_TOTAL_SILENCE:
                mZenMode = NOTIFICATION_TOTAL_SILENCE;
                mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_SILENT);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mZenMode != NOTIFICATION_TOTAL_SILENCE) return;
                        mNotificationManager.setZenMode(Settings.Global.ZEN_MODE_NO_INTERRUPTIONS, null, TAG);
                    }
                }, CHANGE_DELAY);
                return Constants.MODE_TOTAL_SILENCE;
            case NOTIFICATION_PRIORITY_ONLY:
                mZenMode = NOTIFICATION_PRIORITY_ONLY;
                mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_NORMAL);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mZenMode != NOTIFICATION_PRIORITY_ONLY) return;
                        mNotificationManager.setZenMode(Settings.Global.ZEN_MODE_IMPORTANT_INTERRUPTIONS, null, TAG);
                    }
                }, CHANGE_DELAY);
                return Constants.MODE_PRIORITY_ONLY;
            case NOTIFICATION_ALL:
                mRingMode = NOTIFICATION_ALL;
                mNotificationManager.setZenMode(Settings.Global.ZEN_MODE_OFF, null, TAG);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mRingMode != NOTIFICATION_ALL) return;
                        mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_NORMAL);
                    }
                }, CHANGE_DELAY);
                return Constants.MODE_NONE;
        }
        return 0;
    }

    @Override
    public void reset() {
        mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_NORMAL);
        mNotificationManager.setZenMode(Settings.Global.ZEN_MODE_OFF, null, TAG);
    }
}
