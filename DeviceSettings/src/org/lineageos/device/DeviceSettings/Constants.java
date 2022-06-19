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

    public static final String SLIDER_STATE
            = "/proc/tristatekey/tri_state";

    public static final String NOTIF_SLIDER_PANEL_KEY = "notification_slider";
    public static final String NOTIF_SLIDER_USAGE_KEY = "slider_usage";
    public static final String NOTIF_SLIDER_ACTION_TOP_KEY = "action_top_position";
    public static final String NOTIF_SLIDER_ACTION_MIDDLE_KEY = "action_middle_position";
    public static final String NOTIF_SLIDER_ACTION_BOTTOM_KEY = "action_bottom_position";

    public static final String EXTRA_SLIDER_USAGE = "usage";
    public static final String EXTRA_SLIDER_ACTIONS = "actions";

    public static final String NOTIF_SLIDER_FOR_NOTIFICATION = "1";
    public static final String NOTIF_SLIDER_FOR_FLASHLIGHT = "2";
    public static final String NOTIF_SLIDER_FOR_BRIGHTNESS = "3";
    public static final String NOTIF_SLIDER_FOR_ROTATION = "4";
    public static final String NOTIF_SLIDER_FOR_RINGER = "5";
    public static final String NOTIF_SLIDER_FOR_NOTIFICATION_RINGER = "6";

    public static final String ACTION_UPDATE_SLIDER_POSITION
            = "org.lineageos.device.DeviceSettings.UPDATE_SLIDER_POSITION";
    public static final String ACTION_UPDATE_SLIDER_SETTINGS
            = "org.lineageos.device.DeviceSettings.UPDATE_SLIDER_SETTINGS";
    public static final String EXTRA_SLIDER_POSITION = "position";
    public static final String EXTRA_SLIDER_POSITION_VALUE = "position_value";

    public static final int MODE_TOTAL_SILENCE = 600;
    public static final int MODE_ALARMS_ONLY = 601;
    public static final int MODE_PRIORITY_ONLY = 602;
    public static final int MODE_NONE = 603;
    public static final int MODE_VIBRATE = 604;
    public static final int MODE_RING = 605;
    public static final int MODE_SILENT = 620;
    public static final int MODE_FLASHLIGHT_ON = 621;
    public static final int MODE_FLASHLIGHT_OFF = 622;
    public static final int MODE_FLASHLIGHT_BLINK = 623;
    public static final int MODE_BRIGHTNESS_BRIGHT = 630;
    public static final int MODE_BRIGHTNESS_DARK = 631;
    public static final int MODE_BRIGHTNESS_AUTO = 632;
    public static final int MODE_ROTATION_AUTO = 640;
    public static final int MODE_ROTATION_0 = 641;
    public static final int MODE_ROTATION_90 = 642;
    public static final int MODE_ROTATION_270 = 643;

    // Holds <preference_key> -> <proc_node> mapping
    public static final Map<String, String> sBooleanNodePreferenceMap = new HashMap<>();
    public static final Map<String, String> sStringNodePreferenceMap = new HashMap<>();
}
