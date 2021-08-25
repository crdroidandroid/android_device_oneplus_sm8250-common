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

public class RefreshRateSwitch {

    public static boolean isCurrentlyEnabled(Context context) {
        boolean peak = Settings.System.getFloat(context.getContentResolver(),
                Settings.System.PEAK_REFRESH_RATE, 120f) == 120f;
        boolean min = Settings.System.getFloat(context.getContentResolver(),
                Settings.System.MIN_REFRESH_RATE, 60f) == 120f;
        return peak && min;
    }

    public static void setPeakRefresh(Context context, boolean enabled) {
        Settings.System.putFloat(context.getContentResolver(),
                Settings.System.PEAK_REFRESH_RATE, enabled ? 120f : 60f);
        Settings.System.putFloat(context.getContentResolver(),
                Settings.System.MIN_REFRESH_RATE, enabled ? 120f : 60f);
    }
}
