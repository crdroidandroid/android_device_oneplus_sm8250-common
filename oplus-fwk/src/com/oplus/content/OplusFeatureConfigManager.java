package com.oplus.content;

public class OplusFeatureConfigManager {

    public static OplusFeatureConfigManager sOplusFeatureConfigManager = null;

    public static OplusFeatureConfigManager getInstance() {
        if (sOplusFeatureConfigManager == null) {
            sOplusFeatureConfigManager = new OplusFeatureConfigManager();
        }
        return sOplusFeatureConfigManager;
    }

    public boolean hasFeature(String name) {
        return false;
    }
}
