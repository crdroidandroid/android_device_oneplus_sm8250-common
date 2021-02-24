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
        enabled = sharedPrefs.getBoolean(DeviceSettings.KEY_DC_SWITCH, false);
        if (enabled) {
        restore(DCModeSwitch.getFile(), enabled);
               }
        enabled = sharedPrefs.getBoolean(DeviceSettings.KEY_FPS_INFO, false);
        if (enabled) {
            context.startService(new Intent(context, FPSInfoService.class));
       }

        mContext = context;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for(int i = 0; i < procInfos.size(); i++) {
            if(procInfos.get(i).processName.equals("com.google.android.setupwizard")) {
                mSetupRunning = true;
            }
        }

        if (DEBUG) 
            Log.d(TAG, "We are" + mSetupRunning + "running in setup");

        if(!mSetupRunning) {
            try {
                settingsContext = context.createPackageContext("com.android.settings", 0);
            } catch (Exception e) {
				Log.e(TAG, "com.android.settings Package not found", e);
			}
            SharedPreferences sharedpreferences = context.getSharedPreferences("selinux_pref", Context.MODE_PRIVATE);

            if (DEBUG) 
                Log.d(TAG, "sharedpreferences.contains(" + PREF_SELINUX_MODE + "): " + (sharedpreferences.contains(PREF_SELINUX_MODE) ? "True":"False"));

            if (sharedpreferences.contains(PREF_SELINUX_MODE)) {
                boolean currentIsSelinuxEnforcing = SELinux.isSELinuxEnforced();
                boolean isSelinuxEnforcing = sharedpreferences.getBoolean(PREF_SELINUX_MODE, currentIsSelinuxEnforcing);

                if (DEBUG) 
                    Log.d(TAG, String.format("currentIsSelinuxEnforcing: %s, isSelinuxEnforcing: %s", (currentIsSelinuxEnforcing ? "True" : "False"), (isSelinuxEnforcing ? "True" : "False")));

                try {
                    if (isSelinuxEnforcing) {
                        if (!currentIsSelinuxEnforcing) {
                            SuShell.runWithSuCheck("setenforce 1");
                            showToast(context.getString(R.string.selinux_enforcing_toast_title),
                                    context);
                        }
                    } else {
                        if (currentIsSelinuxEnforcing) {
                            SuShell.runWithSuCheck("setenforce 0");
                            showToast(context.getString(R.string.selinux_permissive_toast_title),
                                    context);
                        }
                    }
                } catch (SuShell.SuDeniedException e) {
                    showToast(context.getString(R.string.cannot_get_su), context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        VibratorStrengthPreference.restore(context);
        Utils.enableService(context);
    }

    private void showToast(String toastString, Context context) {
        Toast.makeText(context, toastString, Toast.LENGTH_SHORT)
                .show();
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
