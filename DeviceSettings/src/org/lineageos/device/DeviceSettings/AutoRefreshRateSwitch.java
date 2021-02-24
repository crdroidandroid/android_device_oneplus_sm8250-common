/*
* Copyright (C) 2016 The OmniROM Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package org.lineageos.device.DeviceSettings;

import android.content.Context;
import android.provider.Settings;

import org.lineageos.device.DeviceSettings.DeviceSettings;

public class AutoRefreshRateSwitch {

    public static final String SETTINGS_KEY = DeviceSettings.KEY_SETTINGS_PREFIX + DeviceSettings.KEY_AUTO_REFRESH_RATE;

    public AutoRefreshRateSwitch() { }

    public static boolean isCurrentlyEnabled(Context context) {
        return Settings.System.getInt(context.getContentResolver(), SETTINGS_KEY, 1) == 1;
    }
}
