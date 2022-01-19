// IQcrilMsgTunnel.aidl
package com.qualcomm.qcrilmsgtunnel;

interface IQcrilMsgTunnel {
    int sendOemRilRequestRaw(in byte[] request, out byte[] response, in int sub);
}
