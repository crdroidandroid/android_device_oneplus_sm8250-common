/*
* Copyright (C) 2018 The OmniROM Project
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

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import androidx.preference.PreferenceManager;

import java.lang.IllegalArgumentException;

@TargetApi(24)
public class HBMModeTileService extends TileService {

    private Intent mHbmIntent;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
    }

    @Override
    public void onTileRemoved() {
        tryStopService();
        super.onTileRemoved();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateState();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onClick() {
        super.onClick();
        boolean enabled = HBMModeSwitch.isCurrentlyEnabled(this);
        // NOTE: reverse logic, enabled reflects the state before press
        Utils.writeValue(HBMModeSwitch.getFile(), enabled ? "0" : "5");
        if (!enabled) {
            mHbmIntent = new Intent(this,
                    org.lineageos.device.DeviceSettings.HBMModeService.class);
            this.startService(mHbmIntent);
        }
        updateState();
    }

    private void updateState() {
        boolean enabled = HBMModeSwitch.isCurrentlyEnabled(this);
        if (!enabled) tryStopService();
        getQsTile().setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        getQsTile().updateTile();
    }

    private void tryStopService() {
        if (mHbmIntent == null) return;
        this.stopService(mHbmIntent);
        mHbmIntent = null;
    }
}
