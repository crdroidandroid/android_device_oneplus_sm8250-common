package com.oneplus.os;

public interface IParamService extends android.os.IInterface {
    public static class Default implements com.oneplus.os.IParamService {
        public int getParamIntSYNC(int a) throws android.os.RemoteException {
            return 0;
        }
        @Override
        public android.os.IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends android.os.Binder implements com.oneplus.os.IParamService {
        public Stub() {
        }

        public static com.oneplus.os.IParamService asInterface(android.os.IBinder obj) {
            return new com.oneplus.os.IParamService.Stub.Proxy(obj);
        }

        public int getParamIntSYNC(int a) throws android.os.RemoteException {
            return 0;
        }

        private static class Proxy implements com.oneplus.os.IParamService {
            private android.os.IBinder mRemote;
            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }
            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public int getParamIntSYNC(int a) throws android.os.RemoteException {
                return 0;
            }
        }
    }

    public int getParamIntSYNC(int a) throws android.os.RemoteException;
}
