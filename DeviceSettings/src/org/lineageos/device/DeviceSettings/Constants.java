/*
 * Copyright (C) 2015 The CyanogenMod Project
 * Copyright (C) 2017 The LineageOS Project
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

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.preference.SwitchPreference;
import androidx.preference.PreferenceManager;

import android.media.AudioManager;

public class Constants {

    // Preference keys
    public static final String NOTIF_SLIDER_TOP_KEY = "keycode_top_position";
    public static final String NOTIF_SLIDER_MIDDLE_KEY = "keycode_middle_position";
    public static final String NOTIF_SLIDER_BOTTOM_KEY = "keycode_bottom_position";

    // Button prefs
    public static final String NOTIF_SLIDER_TOP_PREF = "pref_keycode_top_position";
    public static final String NOTIF_SLIDER_MIDDLE_PREF = "pref_keycode_middle_position";
    public static final String NOTIF_SLIDER_BOTTOM_PREF = "pref_keycode_bottom_position";

    // Default values
    public static final int KEY_VALUE_TOTAL_SILENCE = 0;
    public static final int KEY_VALUE_SILENT = 1;
    public static final int KEY_VALUE_PRIORTY_ONLY = 2;
    public static final int KEY_VALUE_VIBRATE = 3;
    public static final int KEY_VALUE_NORMAL = 4;

    public static final Map<String, String> sStringKeyPreferenceMap = new HashMap<>();
    public static final Map<Integer, String> sKeyMap = new HashMap<>();
    public static final Map<String, Integer> sKeyDefaultMap = new HashMap<>();

    public static final String ACTION_UPDATE_SLIDER_POSITION
            = "org.lineageos.device.DeviceSettings.UPDATE_SLIDER_POSITION";
    public static final String EXTRA_SLIDER_POSITION = "position";

    static {
        sStringKeyPreferenceMap.put(NOTIF_SLIDER_TOP_KEY, NOTIF_SLIDER_TOP_PREF);
        sStringKeyPreferenceMap.put(NOTIF_SLIDER_MIDDLE_KEY, NOTIF_SLIDER_MIDDLE_PREF);
        sStringKeyPreferenceMap.put(NOTIF_SLIDER_BOTTOM_KEY, NOTIF_SLIDER_BOTTOM_PREF);

        sKeyMap.put(603, NOTIF_SLIDER_TOP_KEY);
        sKeyMap.put(602, NOTIF_SLIDER_MIDDLE_KEY);
        sKeyMap.put(601, NOTIF_SLIDER_BOTTOM_KEY);

        sKeyDefaultMap.put(NOTIF_SLIDER_TOP_KEY, KEY_VALUE_TOTAL_SILENCE);
        sKeyDefaultMap.put(NOTIF_SLIDER_MIDDLE_KEY, KEY_VALUE_VIBRATE);
        sKeyDefaultMap.put(NOTIF_SLIDER_BOTTOM_KEY, KEY_VALUE_NORMAL);
    }

    public static int getPreferenceInt(Context context, String key) {
        return Settings.System.getIntForUser(context.getContentResolver(),
                sStringKeyPreferenceMap.get(key), sKeyDefaultMap.get(key), UserHandle.USER_CURRENT);
    }

    public static void setPreferenceInt(Context context, String key, int value) {
        Settings.System.putIntForUser(context.getContentResolver(),
                sStringKeyPreferenceMap.get(key), value, UserHandle.USER_CURRENT);
    }
}
