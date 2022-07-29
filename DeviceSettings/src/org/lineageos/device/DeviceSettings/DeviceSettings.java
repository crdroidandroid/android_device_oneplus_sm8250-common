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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import java.util.Arrays;

import org.lineageos.device.DeviceSettings.Constants;
import org.lineageos.internal.util.FileUtils;

public class DeviceSettings extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {
    private static final String TAG = DeviceSettings.class.getSimpleName();

    private static final String KEY_GAME_SWITCH = "game_mode";
    private static final String KEY_EDGE_TOUCH = "edge_touch";

    private static final String FILE_GAME = "/proc/touchpanel/game_switch_enable";
    private static final String FILE_EDGE = "/proc/touchpanel/oplus_tp_direction";

    private static final String KEY_USB2_SWITCH = "usb2_fast_charge";

    private static final String FILE_FAST_CHARGE = "/sys/module/oplus_chg/parameters/force_fast_charge";

    private ListPreference mTopKeyPref;
    private ListPreference mMiddleKeyPref;
    private ListPreference mBottomKeyPref;

    private SwitchPreference mGameModeSwitch;
    private SwitchPreference mEdgeTouchSwitch;
    private SwitchPreference mUSB2FastChargeModeSwitch;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.main);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        mGameModeSwitch = (SwitchPreference) findPreference(KEY_GAME_SWITCH);
        if (Utils.fileWritable(FILE_GAME)) {
            mGameModeSwitch.setEnabled(true);
            mGameModeSwitch.setChecked(sharedPrefs.getBoolean(KEY_GAME_SWITCH,
                Utils.getFileValueAsBoolean(FILE_GAME, false)));
            mGameModeSwitch.setOnPreferenceChangeListener(this);
        } else {
            mGameModeSwitch.setEnabled(false);
        }

        mEdgeTouchSwitch = (SwitchPreference) findPreference(KEY_EDGE_TOUCH);
        if (Utils.fileWritable(FILE_EDGE)) {
            mEdgeTouchSwitch.setEnabled(true);
            mEdgeTouchSwitch.setChecked(sharedPrefs.getBoolean(KEY_EDGE_TOUCH,
                Utils.getFileValueAsBoolean(FILE_EDGE, false)));
            mEdgeTouchSwitch.setOnPreferenceChangeListener(this);
        } else {
            mEdgeTouchSwitch.setEnabled(false);
        }

        mUSB2FastChargeModeSwitch = (SwitchPreference) findPreference(KEY_USB2_SWITCH);
        if (Utils.fileWritable(FILE_FAST_CHARGE)) {
            mUSB2FastChargeModeSwitch.setEnabled(true);
            mUSB2FastChargeModeSwitch.setChecked(sharedPrefs.getBoolean(KEY_USB2_SWITCH,
                Utils.getFileValueAsBoolean(FILE_FAST_CHARGE, false)));
            mUSB2FastChargeModeSwitch.setOnPreferenceChangeListener(this);
        } else {
            mUSB2FastChargeModeSwitch.setEnabled(false);
        }

        initNotificationSliderPreference();
    }

    private void initNotificationSliderPreference() {
        registerPreferenceListener(Constants.NOTIF_SLIDER_USAGE_KEY);
        registerPreferenceListener(Constants.NOTIF_SLIDER_ACTION_TOP_KEY);
        registerPreferenceListener(Constants.NOTIF_SLIDER_ACTION_MIDDLE_KEY);
        registerPreferenceListener(Constants.NOTIF_SLIDER_ACTION_BOTTOM_KEY);

        ListPreference usagePref = (ListPreference) findPreference(
                Constants.NOTIF_SLIDER_USAGE_KEY);
        handleSliderUsageChange(usagePref.getValue());
    }

    private void registerPreferenceListener(String key) {
        Preference p = findPreference(key);
        p.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mGameModeSwitch) {
            boolean enabled = (Boolean) newValue;
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            sharedPrefs.edit().putBoolean(KEY_GAME_SWITCH, enabled).commit();
    	    Utils.writeValue(FILE_GAME, enabled ? "1" : "0");
            return true;
        } else if (preference == mEdgeTouchSwitch) {
            boolean enabled = (Boolean) newValue;
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            sharedPrefs.edit().putBoolean(KEY_EDGE_TOUCH, enabled).commit();
    	    Utils.writeValue(FILE_EDGE, enabled ? "1" : "0");
            return true;
        } else if (preference == mUSB2FastChargeModeSwitch) {
            boolean enabled = (Boolean) newValue;
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            sharedPrefs.edit().putBoolean(KEY_USB2_SWITCH, enabled).commit();
    	    Utils.writeValue(FILE_FAST_CHARGE, enabled ? "1" : "0");
            return true;
        }

        String key = preference.getKey();
        switch (key) {
            case Constants.NOTIF_SLIDER_USAGE_KEY:
                return handleSliderUsageChange((String) newValue) &&
                        handleSliderUsageDefaultsChange((String) newValue) &&
                        notifySliderUsageChange((String) newValue);
            case Constants.NOTIF_SLIDER_ACTION_TOP_KEY:
                return notifySliderActionChange(0, (String) newValue);
            case Constants.NOTIF_SLIDER_ACTION_MIDDLE_KEY:
                return notifySliderActionChange(1, (String) newValue);
            case Constants.NOTIF_SLIDER_ACTION_BOTTOM_KEY:
                return notifySliderActionChange(2, (String) newValue);
            default:
                break;
        }

        String node = Constants.sBooleanNodePreferenceMap.get(key);
        if (!TextUtils.isEmpty(node) && FileUtils.isFileWritable(node)) {
            Boolean value = (Boolean) newValue;
            FileUtils.writeLine(node, value ? "1" : "0");
            return true;
        }
        node = Constants.sStringNodePreferenceMap.get(key);
        if (!TextUtils.isEmpty(node) && FileUtils.isFileWritable(node)) {
            FileUtils.writeLine(node, (String) newValue);
            return true;
        }

        return false;
    }

    @Override
    public void addPreferencesFromResource(int preferencesResId) {
        super.addPreferencesFromResource(preferencesResId);
        // Initialize node preferences
        for (String pref : Constants.sBooleanNodePreferenceMap.keySet()) {
            SwitchPreference b = (SwitchPreference) findPreference(pref);
            if (b == null) continue;
            String node = Constants.sBooleanNodePreferenceMap.get(pref);
            if (FileUtils.isFileReadable(node)) {
                String curNodeValue = FileUtils.readOneLine(node);
                b.setChecked(curNodeValue.equals("1"));
                b.setOnPreferenceChangeListener(this);
            } else {
                removePref(b);
            }
        }
        for (String pref : Constants.sStringNodePreferenceMap.keySet()) {
            ListPreference l = (ListPreference) findPreference(pref);
            if (l == null) continue;
            String node = Constants.sStringNodePreferenceMap.get(pref);
            if (FileUtils.isFileReadable(node)) {
                l.setValue(FileUtils.readOneLine(node));
                l.setOnPreferenceChangeListener(this);
            } else {
                removePref(l);
            }
        }
    }

    private void removePref(Preference pref) {
        PreferenceGroup parent = pref.getParent();
        if (parent == null) {
            return;
        }
        parent.removePreference(pref);
        if (parent.getPreferenceCount() == 0) {
            removePref(parent);
        }
    }

    private boolean handleSliderUsageChange(String newValue) {
        switch (newValue) {
            case Constants.NOTIF_SLIDER_FOR_NOTIFICATION:
                return updateSliderActions(
                        R.array.notification_slider_mode_entries,
                        R.array.notification_slider_mode_entry_values);
            case Constants.NOTIF_SLIDER_FOR_FLASHLIGHT:
                return updateSliderActions(
                        R.array.notification_slider_flashlight_entries,
                        R.array.notification_slider_flashlight_entry_values);
            case Constants.NOTIF_SLIDER_FOR_BRIGHTNESS:
                return updateSliderActions(
                        R.array.notification_slider_brightness_entries,
                        R.array.notification_slider_brightness_entry_values);
            case Constants.NOTIF_SLIDER_FOR_ROTATION:
                return updateSliderActions(
                        R.array.notification_slider_rotation_entries,
                        R.array.notification_slider_rotation_entry_values);
            case Constants.NOTIF_SLIDER_FOR_RINGER:
                return updateSliderActions(
                        R.array.notification_slider_ringer_entries,
                        R.array.notification_slider_ringer_entry_values);
            case Constants.NOTIF_SLIDER_FOR_NOTIFICATION_RINGER:
                return updateSliderActions(
                        R.array.notification_ringer_slider_mode_entries,
                        R.array.notification_ringer_slider_mode_entry_values);
            default:
                return false;
        }
    }

    private boolean handleSliderUsageDefaultsChange(String newValue) {
        int defaultsResId = getDefaultResIdForUsage(newValue);
        if (defaultsResId == 0) {
            return false;
        }
        return updateSliderActionDefaults(defaultsResId);
    }

    private boolean updateSliderActions(int entriesResId, int entryValuesResId) {
        String[] entries = getResources().getStringArray(entriesResId);
        String[] entryValues = getResources().getStringArray(entryValuesResId);
        return updateSliderPreference(Constants.NOTIF_SLIDER_ACTION_TOP_KEY,
                entries, entryValues) &&
            updateSliderPreference(Constants.NOTIF_SLIDER_ACTION_MIDDLE_KEY,
                    entries, entryValues) &&
            updateSliderPreference(Constants.NOTIF_SLIDER_ACTION_BOTTOM_KEY,
                    entries, entryValues);
    }

    private boolean updateSliderActionDefaults(int defaultsResId) {
        String[] defaults = getResources().getStringArray(defaultsResId);
        if (defaults.length != 3) {
            return false;
        }

        return updateSliderPreferenceValue(Constants.NOTIF_SLIDER_ACTION_TOP_KEY,
                defaults[0]) &&
            updateSliderPreferenceValue(Constants.NOTIF_SLIDER_ACTION_MIDDLE_KEY,
                    defaults[1]) &&
            updateSliderPreferenceValue(Constants.NOTIF_SLIDER_ACTION_BOTTOM_KEY,
                    defaults[2]);
    }

    private boolean updateSliderPreference(CharSequence key,
            String[] entries, String[] entryValues) {
        ListPreference pref = (ListPreference) findPreference(key);
        if (pref == null) {
            return false;
        }
        pref.setEntries(entries);
        pref.setEntryValues(entryValues);
        return true;
    }

    private boolean updateSliderPreferenceValue(CharSequence key,
            String value) {
        ListPreference pref = (ListPreference) findPreference(key);
        if (pref == null) {
            return false;
        }
        pref.setValue(value);
        return true;
    }

    private int[] getCurrentSliderActions() {
        int[] actions = new int[3];
        ListPreference p;

        p = (ListPreference) findPreference(
                Constants.NOTIF_SLIDER_ACTION_TOP_KEY);
        actions[0] = Integer.parseInt(p.getValue());

        p = (ListPreference) findPreference(
                Constants.NOTIF_SLIDER_ACTION_MIDDLE_KEY);
        actions[1] = Integer.parseInt(p.getValue());

        p = (ListPreference) findPreference(
                Constants.NOTIF_SLIDER_ACTION_BOTTOM_KEY);
        actions[2] = Integer.parseInt(p.getValue());

        return actions;
    }

    private boolean notifySliderUsageChange(String usage) {
        sendUpdateBroadcast(getActivity().getApplicationContext(), Integer.parseInt(usage),
                getCurrentSliderActions());
        return true;
    }

    private boolean notifySliderActionChange(int index, String value) {
        ListPreference p = (ListPreference) findPreference(
                Constants.NOTIF_SLIDER_USAGE_KEY);
        int usage = Integer.parseInt(p.getValue());

        int[] actions = getCurrentSliderActions();
        actions[index] = Integer.parseInt(value);

        sendUpdateBroadcast(getActivity().getApplicationContext(), usage, actions);
        return true;
    }

    public static void sendUpdateBroadcast(Context context,
            int usage, int[] actions) {
        Intent intent = new Intent(Constants.ACTION_UPDATE_SLIDER_SETTINGS);
        intent.putExtra(Constants.EXTRA_SLIDER_USAGE, usage);
        intent.putExtra(Constants.EXTRA_SLIDER_ACTIONS, actions);
        intent.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        context.sendBroadcastAsUser(intent, UserHandle.CURRENT);
        Log.d(TAG, "update slider usage " + usage + " with actions: " +
                Arrays.toString(actions));
    }

    public static void restoreSliderStates(Context context) {
        Resources res = context.getResources();
        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName() + "_preferences", Context.MODE_PRIVATE);

        String usage = prefs.getString(Constants.NOTIF_SLIDER_USAGE_KEY,
                res.getString(R.string.config_defaultNotificationSliderUsage));

        int defaultsResId = getDefaultResIdForUsage(usage);
        if (defaultsResId == 0) {
            return;
        }

        String[] defaults = res.getStringArray(defaultsResId);
        if (defaults.length != 3) {
            return;
        }

        String actionTop = prefs.getString(
                Constants.NOTIF_SLIDER_ACTION_TOP_KEY, defaults[0]);

        String actionMiddle = prefs.getString(
                Constants.NOTIF_SLIDER_ACTION_MIDDLE_KEY, defaults[1]);

        String actionBottom = prefs.getString(
                Constants.NOTIF_SLIDER_ACTION_BOTTOM_KEY, defaults[2]);

        prefs.edit()
            .putString(Constants.NOTIF_SLIDER_USAGE_KEY, usage)
            .putString(Constants.NOTIF_SLIDER_ACTION_TOP_KEY, actionTop)
            .putString(Constants.NOTIF_SLIDER_ACTION_MIDDLE_KEY, actionMiddle)
            .putString(Constants.NOTIF_SLIDER_ACTION_BOTTOM_KEY, actionBottom)
            .commit();

        sendUpdateBroadcast(context, Integer.parseInt(usage), new int[] {
            Integer.parseInt(actionTop),
            Integer.parseInt(actionMiddle),
            Integer.parseInt(actionBottom)
        });
    }

    public static void restoreFastChargeSetting(Context context) {
        if (Utils.fileWritable(FILE_FAST_CHARGE)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean value = sharedPrefs.getBoolean(KEY_USB2_SWITCH,
                Utils.getFileValueAsBoolean(FILE_FAST_CHARGE, false));
            Utils.writeValue(FILE_FAST_CHARGE, value ? "1" : "0");
        }
    }

    private static int getDefaultResIdForUsage(String usage) {
        switch (usage) {
            case Constants.NOTIF_SLIDER_FOR_NOTIFICATION:
                return R.array.config_defaultSliderActionsForNotification;
            case Constants.NOTIF_SLIDER_FOR_FLASHLIGHT:
                return R.array.config_defaultSliderActionsForFlashlight;
            case Constants.NOTIF_SLIDER_FOR_BRIGHTNESS:
                return R.array.config_defaultSliderActionsForBrightness;
            case Constants.NOTIF_SLIDER_FOR_ROTATION:
                return R.array.config_defaultSliderActionsForRotation;
            case Constants.NOTIF_SLIDER_FOR_RINGER:
                return R.array.config_defaultSliderActionsForRinger;
            case Constants.NOTIF_SLIDER_FOR_NOTIFICATION_RINGER:
                return R.array.config_defaultSliderActionsForNotificationRinger;
            default:
                return 0;
        }
    }
}
