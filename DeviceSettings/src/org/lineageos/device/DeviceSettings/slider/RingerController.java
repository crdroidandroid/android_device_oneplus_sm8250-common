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
import android.media.AudioManager;
import android.util.Log;
import android.util.SparseIntArray;

import org.lineageos.device.DeviceSettings.Constants;
import org.lineageos.device.DeviceSettings.SliderControllerBase;

public final class RingerController extends SliderControllerBase {

    public static final int ID = 5;

    private static final String TAG = "RingerController";

    private static final int RINGER_NORMAL = 50;
    private static final int RINGER_VIBRATE = 51;
    private static final int RINGER_SILENT = 52;

    private static final SparseIntArray MODES = new SparseIntArray();
    static {
        MODES.put(RINGER_NORMAL, AudioManager.RINGER_MODE_NORMAL);
        MODES.put(RINGER_VIBRATE, AudioManager.RINGER_MODE_VIBRATE);
        MODES.put(RINGER_SILENT, AudioManager.RINGER_MODE_SILENT);
    }

    private final AudioManager mAudioManager;

    public RingerController(Context context) {
        super(context);
        mAudioManager = context.getSystemService(AudioManager.class);
    }

    @Override
    protected int processAction(int action) {
        Log.d(TAG, "slider action: " + action);
        if (MODES.indexOfKey(action) >= 0) {
            mAudioManager.setRingerModeInternal(MODES.get(action));
            switch (action) {
                case RINGER_NORMAL:
                    return Constants.MODE_RING;
                case RINGER_VIBRATE:
                    return Constants.MODE_VIBRATE;
                case RINGER_SILENT:
                    return Constants.MODE_SILENT;
            }
        }
        return 0;
    }

    @Override
    public void reset() {
        mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_NORMAL);
    }
}
