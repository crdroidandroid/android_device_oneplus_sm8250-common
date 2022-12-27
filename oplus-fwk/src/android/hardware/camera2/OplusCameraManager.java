package android.hardware.camera2;

import android.content.Context;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.util.Log;

public class OplusCameraManager {

    private static OplusCameraManager sOplusCameraManager = null;

    public static OplusCameraManager getInstance() {
        if (sOplusCameraManager == null) {
            sOplusCameraManager = new OplusCameraManager();
        }
        return sOplusCameraManager;
    }

    public void preOpenCamera(Context context) {
        if (context != null) {
            try {
                OplusCameraManagerGlobal.get().preOpenCamera(context.getOpPackageName());
            } catch (CameraAccessException e) {
                e.printStackTrace();
            } catch (RemoteException e2) {
                e2.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("context was null");
        }
    }

    public void setCallInfo() {
        try {
            OplusCameraManagerGlobal.get().setCallInfo();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (RemoteException e2) {
            e2.printStackTrace();
        }
    }

    public void setDeathRecipient(IBinder client) {
        if (client != null) {
            try {
                OplusCameraManagerGlobal.get().setDeathRecipient(client);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            } catch (RemoteException e2) {
                e2.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("client was null");
        }
    }

    private static final class OplusCameraManagerGlobal implements IBinder.DeathRecipient {

        private static final String TAG = "OplusCameraManagerGlobal";
        private static final String CAMERA_SERVICE_BINDER_NAME = "media.camera";
        private static final String DESCRIPTOR = "android.hardware.media";
        private static final int SET_DEATH_RECIPIENT = 10002;
        private static final int SET_CALL_INFO = 10006;
        private static final int PRE_OPEN_CAMERA = 10012;
        private static final OplusCameraManagerGlobal gOplusCameraManagerGlobal =
                new OplusCameraManagerGlobal();
        private static final boolean sCameraServiceDisabled =
                SystemProperties.getBoolean("config.disable_cameraservice", false);
        private final Object mLock = new Object();
        private IBinder mRemote = null;

        public static OplusCameraManagerGlobal get() {
            return gOplusCameraManagerGlobal;
        }

        private void connectCameraServiceLocked() {
            if (mRemote == null && !sCameraServiceDisabled) {
                mRemote = ServiceManager.getService(CAMERA_SERVICE_BINDER_NAME);
                if (mRemote != null) {
                    try {
                        mRemote.linkToDeath(this, 0);
                    } catch (RemoteException e) {
                    }
                }
            }
        }

        public IBinder getCameraServiceRemote() {
            synchronized (mLock) {
                connectCameraServiceLocked();
                if (mRemote == null && !sCameraServiceDisabled) {
                    Log.e(TAG, "Camera service is unavailable");
                }
            }
            return mRemote;
        }

        public void preOpenCamera(String packageName) throws CameraAccessException, RemoteException {
            final IBinder remote = getCameraServiceRemote();
            if (remote != null) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeInt(1);
                    mRemote.transact(PRE_OPEN_CAMERA, data, reply, 0);
                    reply.readException();
                    data.recycle();
                    reply.recycle();
                    Log.i(TAG, "preOpenCamera, the " + packageName + " preOpenSend");
                } catch (Throwable th) {
                    data.recycle();
                    reply.recycle();
                    throw th;
                }
            } else {
                throw new CameraAccessException(2, "Camera service is currently unavailable");
            }
        }

        public void setCallInfo() throws CameraAccessException, RemoteException {
            final IBinder remote = getCameraServiceRemote();
            if (remote != null) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(SET_CALL_INFO, data, reply, 0);
                    reply.readException();
                } finally {
                    data.recycle();
                    reply.recycle();
                }
            } else {
                throw new CameraAccessException(2, "Camera service is currently unavailable");
            }
        }

        public void setDeathRecipient(IBinder client) throws CameraAccessException, RemoteException {
            final IBinder remote = getCameraServiceRemote();
            if (remote != null) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeStrongBinder(client);
                    mRemote.transact(SET_DEATH_RECIPIENT, data, reply, 0);
                    reply.readException();
                } finally {
                    data.recycle();
                    reply.recycle();
                }
            } else {
                throw new CameraAccessException(2, "Camera service is currently unavailable");
            }
        }

        @Override
        public void binderDied() {
            mRemote = null;
        }
    }
}
