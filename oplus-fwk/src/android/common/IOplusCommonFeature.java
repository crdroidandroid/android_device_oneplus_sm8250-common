package android.common;

import android.common.OplusFeatureList;

public interface IOplusCommonFeature {

    default OplusFeatureList.OplusIndex index() {
        return OplusFeatureList.OplusIndex.End;
    }

    default IOplusCommonFeature getDefault() {
        return null;
    }
}
