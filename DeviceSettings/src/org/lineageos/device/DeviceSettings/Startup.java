/*
* Copyright (C) 2013 The OmniROM Project
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

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.PreferenceManager;
import android.os.SELinux;
import android.util.Log;
import android.widget.Toast;
import java.util.List;

public class Startup extends BroadcastReceiver {

    private static final boolean DEBUG = false;

    private static final String PREF_SELINUX_MODE = "selinux_mode";
    private static final String TAG = "SettingsOnBoot";
    private boolean mSetupRunning = false;
    private boolean mHBM = false;
    private Context settingsContext = null;
    private Context mContext;

    @Override
    public void onReceive(final Context context, final Intent bootintent) {
        if (DEBUG) 
            Log.d(TAG, "Received boot completed intent");

        boolean enabled = false;
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        enabled = sharedPrefs.getBoolean(DeviceSettings.KEY_HBM_SWITCH, false);
        if (enabled) {
        restore(HBMModeSwitch.getFile(), enabled);
    }

        VibratorStrengthPreference.restore(context);
        Utils.enableService(context);
    }

    private void restore(String file, boolean enabled) {
        if (file == null) {
            return;
        }
        if (enabled) {
            Utils.writeValue(file, "1");
        }
    }

    private void restore(String file, String value) {
        if (file == null) {
            return;
        }
        Utils.writeValue(file, value);
    }
}
