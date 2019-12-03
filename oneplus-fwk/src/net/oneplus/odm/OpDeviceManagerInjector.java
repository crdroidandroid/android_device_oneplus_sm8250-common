package net.oneplus.odm;

import android.content.Context;
import java.util.Map;

public class OpDeviceManagerInjector {

    public void preserveAppData(Context a, String b, Map c, Map d) {
    }

    public void preserveAssistantData(Context c) {
    }

    public static OpDeviceManagerInjector getInstance() {
        return new OpDeviceManagerInjector();
    }
}
