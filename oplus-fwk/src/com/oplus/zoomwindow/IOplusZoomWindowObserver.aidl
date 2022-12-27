package com.oplus.zoomwindow;

import com.oplus.zoomwindow.OplusZoomWindowInfo;

interface IOplusZoomWindowObserver {

    void onInputMethodChanged(boolean z);
    void onZoomWindowDied(String str);
    void onZoomWindowHide(inout OplusZoomWindowInfo oplusZoomWindowInfo);
    void onZoomWindowShow(inout OplusZoomWindowInfo oplusZoomWindowInfo);
}
