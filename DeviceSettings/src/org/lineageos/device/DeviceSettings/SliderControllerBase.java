/*
 * Copyright (C) 2018-2021 crDroid Android Project
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

package org.lineageos.device.DeviceSettings;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.os.Vibrator;
import android.util.Log;

import lineageos.providers.LineageSettings;

import org.lineageos.internal.util.FileUtils;
import org.lineageos.device.DeviceSettings.Constants;

public abstract class SliderControllerBase {

    private static final String TAG = "SliderControllerBase";

    private static final int KEY_SLIDER_TOP = 601;
    private static final int KEY_SLIDER_MIDDLE = 602;
    private static final int KEY_SLIDER_BOTTOM = 603;

    protected final Context mContext;

    private Vibrator mVibrator;

    private int[] mActions = null;

    public SliderControllerBase(Context context) {
        mContext = context;
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (mVibrator == null || !mVibrator.hasVibrator()) {
            mVibrator = null;
        }
    }

    public final void update(int[] actions) {
        if (actions != null && actions.length == 3) {
            mActions = actions;
        }
    }

    public final boolean isSupported(int key) {
        return key == KEY_SLIDER_TOP ||
                key == KEY_SLIDER_MIDDLE ||
                key == KEY_SLIDER_BOTTOM;
    }

    protected abstract int processAction(int action);

    public final int processEvent(Context context, int key) {
        if (mActions == null) {
            return 0;
        }

        int result = 0;
        switch (key) {
            case KEY_SLIDER_BOTTOM:
                result = processAction(mActions[0]);
                notifySliderChange(context, result, 0);
                break;
            case KEY_SLIDER_MIDDLE:
                result = processAction(mActions[1]);
                notifySliderChange(context, result, 1);
                break;
            case KEY_SLIDER_TOP:
                result = processAction(mActions[2]);
                notifySliderChange(context, result, 2);
                break;
        }

        if (result > 0) {
            doHapticFeedback();
        }

        return result;
    }

    private void notifySliderChange(Context context, int result, int position) {
        if (result > 0)
            sendUpdateBroadcast(context, position, result);
    }

    public static void sendUpdateBroadcast(Context context, int position, int result) {
        Intent intent = new Intent(Constants.ACTION_UPDATE_SLIDER_POSITION);
        intent.putExtra(Constants.EXTRA_SLIDER_POSITION, position);
        intent.putExtra(Constants.EXTRA_SLIDER_POSITION_VALUE, result);
        context.sendBroadcastAsUser(intent, UserHandle.CURRENT);
        intent.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        Log.d(TAG, "slider change to positon " + position);
    }

    public abstract void reset();

    public final void restoreState() {
        if (mActions == null) {
            return;
        }

        try {
            int state = Integer.parseInt(FileUtils.readOneLine(Constants.SLIDER_STATE));
            processAction(mActions[state - 1]);
        } catch (Exception e) {
            Log.e(TAG, "Failed to restore slider state", e);
        }
    }

    private void doHapticFeedback() {
        if (mVibrator == null) {
            return;
        }
        boolean enabled = LineageSettings.System.getIntForUser(mContext.getContentResolver(),
                LineageSettings.System.TOUCHSCREEN_GESTURE_HAPTIC_FEEDBACK, 1, UserHandle.USER_CURRENT) != 0;
        if (enabled) {
            mVibrator.vibrate(50);
        }
    }

    protected final <T> T getSystemService(String name) {
        return (T) mContext.getSystemService(name);
    }
}
