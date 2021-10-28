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

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SELinux;
import android.os.Vibrator;
import android.provider.Settings;
import androidx.preference.SwitchPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.TwoStatePreference;

import org.lineageos.device.DeviceSettings.FileUtils;
import org.lineageos.device.DeviceSettings.Constants;
import org.lineageos.device.DeviceSettings.R;

public class DeviceSettings extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {
    private static final String TAG = DeviceSettings.class.getSimpleName();

    private static final String KEY_VIBSTRENGTH = "vib_strength";

    private static final String FILE_LEVEL = "/sys/devices/platform/soc/a8c000.i2c/i2c-3/3-005a/leds/vibrator/level";
    private static final long testVibrationPattern[] = {0,5};
    private static final String DEFAULT = "3";

    private static final String KEY_CATEGORY_GRAPHICS = "graphics";
    public static final String KEY_HBM_SWITCH = "hbm";
    public static final String KEY_AUTO_HBM_SWITCH = "auto_hbm";
    public static final String KEY_AUTO_HBM_THRESHOLD = "auto_hbm_threshold";

    private static final String KEY_CATEGORY_REFRESH = "refresh";
    public static final String KEY_REFRESH_RATE = "refresh_rate";
    public static final String KEY_AUTO_REFRESH_RATE = "auto_refresh_rate";
    public static final String KEY_FPS_INFO = "fps_info";

    private static TwoStatePreference mHBMModeSwitch;
    private static TwoStatePreference mAutoHBMSwitch;
    private static TwoStatePreference mRefreshRate;
    private static SwitchPreference mAutoRefreshRate;
    private static SwitchPreference mFpsInfo;

    private CustomSeekBarPreference mVibratorStrengthPreference;
    private Vibrator mVibrator;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        addPreferencesFromResource(R.xml.main);

        mHBMModeSwitch = (TwoStatePreference) findPreference(KEY_HBM_SWITCH);
        mHBMModeSwitch.setEnabled(HBMModeSwitch.isSupported());
        mHBMModeSwitch.setChecked(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(DeviceSettings.KEY_HBM_SWITCH, false));
        mHBMModeSwitch.setOnPreferenceChangeListener(this);

        mAutoHBMSwitch = (TwoStatePreference) findPreference(KEY_AUTO_HBM_SWITCH);
        mAutoHBMSwitch.setChecked(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(DeviceSettings.KEY_AUTO_HBM_SWITCH, false));
        mAutoHBMSwitch.setOnPreferenceChangeListener(this);

        if (getResources().getBoolean(R.bool.config_deviceHasHighRefreshRate)) {
            boolean autoRefresh = AutoRefreshRateSwitch.isCurrentlyEnabled(this.getContext());
            mAutoRefreshRate = (SwitchPreference) findPreference(KEY_AUTO_REFRESH_RATE);
            mAutoRefreshRate.setChecked(autoRefresh);
            mAutoRefreshRate.setOnPreferenceChangeListener(this);

            mRefreshRate = (TwoStatePreference) findPreference(KEY_REFRESH_RATE);
            mRefreshRate.setChecked(RefreshRateSwitch.isCurrentlyEnabled(this.getContext()));
            mRefreshRate.setOnPreferenceChangeListener(this);
            updateRefreshRateState(autoRefresh);

            mFpsInfo = (SwitchPreference) findPreference(KEY_FPS_INFO);
            mFpsInfo.setChecked(prefs.getBoolean(KEY_FPS_INFO, false));
            mFpsInfo.setOnPreferenceChangeListener(this);

            mVibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

            mVibratorStrengthPreference =  (CustomSeekBarPreference) findPreference(KEY_VIBSTRENGTH);
            if (Utils.fileWritable(FILE_LEVEL)) {
                mVibratorStrengthPreference.setValue(sharedPrefs.getInt(KEY_VIBSTRENGTH,
                    Integer.parseInt(Utils.getFileValue(FILE_LEVEL, DEFAULT))));
                mVibratorStrengthPreference.setOnPreferenceChangeListener(this);
            } else {
                mVibratorStrengthPreference.setEnabled(false);
            }
        } else {
            getPreferenceScreen().removePreference((Preference) findPreference(KEY_CATEGORY_REFRESH));
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mFpsInfo) {
            boolean enabled = (Boolean) newValue;
            Intent fpsinfo = new Intent(this.getContext(),
                    org.lineageos.device.DeviceSettings.FPSInfoService.class);
            if (enabled) {
                this.getContext().startService(fpsinfo);
            } else {
                this.getContext().stopService(fpsinfo);
            }
        } else if (preference == mAutoHBMSwitch) {
            Boolean enabled = (Boolean) newValue;
            SharedPreferences.Editor prefChange = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
            prefChange.putBoolean(KEY_AUTO_HBM_SWITCH, enabled).commit();
            Utils.enableService(getContext());
        } else if (preference == mAutoRefreshRate) {
            Boolean enabled = (Boolean) newValue;
            Settings.System.putFloat(getContext().getContentResolver(),
                    Settings.System.PEAK_REFRESH_RATE, 90f);
            Settings.System.putFloat(getContext().getContentResolver(),
                    Settings.System.MIN_REFRESH_RATE, 60f);
            Settings.System.putInt(getContext().getContentResolver(),
                    AutoRefreshRateSwitch.SETTINGS_KEY, enabled ? 1 : 0);
            updateRefreshRateState(enabled);
        } else if (preference == mRefreshRate) {
            Boolean enabled = (Boolean) newValue;
            RefreshRateSwitch.setPeakRefresh(getContext(), enabled);
        } else if (preference == mVibratorStrengthPreference) {
            int value = Integer.parseInt(newValue.toString());
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            sharedPrefs.edit().putInt(KEY_VIBSTRENGTH, value).commit();
            Utils.writeValue(FILE_LEVEL, String.valueOf(value));
            mVibrator.vibrate(testVibrationPattern, -1);
        } else if (preference == mHBMModeSwitch) {
            Boolean enabled = (Boolean) newValue;
            Utils.writeValue(HBMModeSwitch.getFile(), enabled ? "5" : "0");
            Intent hbmIntent = new Intent(this.getContext(),
                    org.lineageos.device.DeviceSettings.HBMModeService.class);
            if (enabled) {
                this.getContext().startService(hbmIntent);
            } else {
                this.getContext().stopService(hbmIntent);
            }
        } else {
            Constants.setPreferenceInt(getContext(), preference.getKey(),
                    Integer.parseInt((String) newValue));
        } 
        return true;
    }

    public static boolean isHBMModeService(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(DeviceSettings.KEY_HBM_SWITCH, false);
    }

    public static boolean isAUTOHBMEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(DeviceSettings.KEY_AUTO_HBM_SWITCH, false);
    }

    public static void restoreVibStrengthSetting(Context context) {
        if (Utils.fileWritable(FILE_LEVEL)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            int value = sharedPrefs.getInt(KEY_VIBSTRENGTH,
                Integer.parseInt(Utils.getFileValue(FILE_LEVEL, DEFAULT)));
            Utils.writeValue(FILE_LEVEL, String.valueOf(value));
        }
    }

    private void updateRefreshRateState(boolean auto) {
        mRefreshRate.setEnabled(!auto);
        if (auto) mRefreshRate.setChecked(false);
    }
}
