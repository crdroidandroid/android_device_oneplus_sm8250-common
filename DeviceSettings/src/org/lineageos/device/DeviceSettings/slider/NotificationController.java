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
import android.util.SparseIntArray;

import org.lineageos.device.DeviceSettings.Constants;
import org.lineageos.device.DeviceSettings.SliderControllerBase;

public final class NotificationController extends SliderControllerBase {

    public static final int ID = 1;

    private static final String TAG = "NotificationController";

    private static final int NOTIFICATION_TOTAL_SILENCE = 10;
    private static final int NOTIFICATION_PRIORITY_ONLY = 12;
    private static final int NOTIFICATION_ALL = 13;
    private static final int CHANGE_DELAY = 100;

    private static final SparseIntArray MODES = new SparseIntArray();
    static {
        MODES.put(NOTIFICATION_TOTAL_SILENCE,
                Settings.Global.ZEN_MODE_NO_INTERRUPTIONS);
        MODES.put(NOTIFICATION_PRIORITY_ONLY,
                Settings.Global.ZEN_MODE_IMPORTANT_INTERRUPTIONS);
        MODES.put(NOTIFICATION_ALL,
                Settings.Global.ZEN_MODE_OFF);
    }

    private final NotificationManager mNotificationManager;
    private final AudioManager mAudioManager;
    private Handler mHandler;
    private int mZenMode;

    public NotificationController(Context context) {
        super(context);
        mHandler = new Handler();
        mNotificationManager = context.getSystemService(NotificationManager.class);
        mAudioManager = context.getSystemService(AudioManager.class);
    }

    @Override
    protected int processAction(int action) {
        Log.d(TAG, "slider action: " + action);
        if (MODES.indexOfKey(action) >= 0) {
            mZenMode = MODES.indexOfKey(action);
            mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_NORMAL);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mZenMode != MODES.indexOfKey(action)) return;
                    mNotificationManager.setZenMode(MODES.get(action), null, TAG);
                }
            }, CHANGE_DELAY);
            switch (action) {
                case NOTIFICATION_TOTAL_SILENCE:
                    return Constants.MODE_TOTAL_SILENCE;
                case NOTIFICATION_PRIORITY_ONLY:
                    return Constants.MODE_PRIORITY_ONLY;
                case NOTIFICATION_ALL:
                    return Constants.MODE_NONE;
            }
        }
        return 0;
    }

    @Override
    public void reset() {
        mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_NORMAL);
        mNotificationManager.setZenMode(Settings.Global.ZEN_MODE_OFF, null, TAG);
    }
}
