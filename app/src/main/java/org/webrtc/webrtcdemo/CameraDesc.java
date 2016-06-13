package org.webrtc.webrtcdemo;

/**
 * Created by Boria on 2016/5/5.
 */
public class CameraDesc {
    private final long nativeCameraDesc;

    // CameraDesc can only be created from the native layer.
    private CameraDesc(long nativeCameraDesc) {
        this.nativeCameraDesc = nativeCameraDesc;
    }

    // Dispose must be called before all references to CameraDesc are lost as it
    // will free memory allocated in the native layer.
    public native void dispose();
}
