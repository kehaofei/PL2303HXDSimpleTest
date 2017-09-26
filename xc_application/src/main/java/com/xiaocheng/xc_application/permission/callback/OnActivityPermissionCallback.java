package com.xiaocheng.xc_application.permission.callback;

import android.support.annotation.NonNull;

public interface OnActivityPermissionCallback {

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

}
