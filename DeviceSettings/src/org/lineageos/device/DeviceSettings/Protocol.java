/*
 * Copyright (C) 2021 LibXZR <i@xzr.moe>
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

import android.os.RemoteException;

import com.qualcomm.qcrilmsgtunnel.IQcrilMsgTunnel;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Protocol {
    private static final String QOEMHOOK = "QOEMHOOK";
    private static final int HEADER_SIZE = 16;
    private static final int QCRILHOOK_SET_SA_NSA_CMD = 13;
    private static final int REQUEST_ID = 561158;
    private static final int REQUEST_SIZE = 8;
    IQcrilMsgTunnel mService;

    public Protocol(IQcrilMsgTunnel service) {
        mService = service;
    }

    public enum NR_5G_DISABLE_MODE_TYPE {
        NAS_NR5G_DISABLE_MODE_NONE,
        NAS_NR5G_DISABLE_MODE_SA,
        NAS_NR5G_DISABLE_MODE_NSA
    }

    private static ByteBuffer createBufferWithNativeByteOrder(byte[] bArr) {
        ByteBuffer wrap = ByteBuffer.wrap(bArr);
        wrap.order(ByteOrder.nativeOrder());
        return wrap;
    }

    private static void addQcRilHookHeader(ByteBuffer buf, int requestId, int requestSize) {
        buf.put(QOEMHOOK.getBytes());
        buf.putInt(requestId);
        buf.putInt(requestSize);
    }

    public void setNrMode(int slot, NR_5G_DISABLE_MODE_TYPE mode) {
        int realMode = mode.ordinal();
        byte[] data = new byte[HEADER_SIZE + 3 * Integer.SIZE / 8];
        ByteBuffer buf = createBufferWithNativeByteOrder(data);
        addQcRilHookHeader(buf, REQUEST_ID, REQUEST_SIZE);
        buf.putInt(QCRILHOOK_SET_SA_NSA_CMD);
        buf.putInt(slot);
        buf.putInt(realMode);
        try {
            mService.sendOemRilRequestRaw(data, new byte[2048], slot);
        } catch (RemoteException ignored) {
        }
    }
}
