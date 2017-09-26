/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.xiaocheng.xc_application;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.xiaocheng.xc_application.permission.PermissionHelper;
import com.xiaocheng.xc_application.permission.callback.OnPermissionCallback;


/**
 * The main activity of the API library demo gallery.
 * <p>
 * The main layout lists the demonstrated features, with buttons to launch them.
 */
public final class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener, OnPermissionCallback {


    /**
     * A custom array adapter that shows a {@link FeatureView} containing details about the demo.
     */
    private static class CustomArrayAdapter extends ArrayAdapter<DemoDetails> {

        /**
         * @param demos An array containing the details of the demos to be displayed.
         */
        public CustomArrayAdapter(Context context, DemoDetails[] demos) {
            super(context, R.layout.feature, R.id.title, demos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FeatureView featureView;
            if (convertView instanceof FeatureView) {
                featureView = (FeatureView) convertView;
            } else {
                featureView = new FeatureView(getContext());
            }

            DemoDetails demo = getItem(position);

            featureView.setTitleId(demo.titleId);
            featureView.setDescriptionId(demo.descriptionId);

            Resources resources = getContext().getResources();
            String title = resources.getString(demo.titleId);
            String description = resources.getString(demo.descriptionId);
            featureView.setContentDescription(title + ". " + description);

            return featureView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ListView list = (ListView) findViewById(R.id.list);

        ListAdapter adapter = new CustomArrayAdapter(this, DemoDetailsList.DEMOS);

        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        list.setEmptyView(findViewById(R.id.empty));

        doPermissionCheck();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.menu_legal) {
            startActivity(new Intent(this, LegalInfoActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DemoDetails demo = (DemoDetails) parent.getAdapter().getItem(position);
        startActivity(new Intent(this, demo.activityClass));
    }

    /*************************************************************统一权限管理*******************************************************************/

    private PermissionHelper permissionHelper;

    //值唯一即可,这是为了返回做标识使用
    private final int REQUEST_SETTING = -11;
    final String[] permissionArrays = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
    final int permissionSize = permissionArrays.length;
    final int[] permissionInfo = {R.string.open_location_permit,
            R.string.open_storage_permit,
            R.string.open_bluetooth_permit};
    final int infoSize = permissionInfo.length;

    /**
     * 检查是否拥有权限
     */
    private void doPermissionCheck(){
        permissionHelper = PermissionHelper.getInstance(this);
        permissionHelper
                .setForceAccepting(true) // default is false. its here so you know that it exists.
                .request(permissionArrays);
    }

    @Override
    public void onPermissionGranted(@NonNull String[] permissionName) {
        //权限点击允许
        String lastPermission = permissionName[permissionName.length-1];
        if (lastPermission.equals(permissionArrays[permissionSize-1])){
            //权限点击允许
            doYourThings();
        }else{
            doPermissionCheck();
        }
    }

    @Override
    public void onPermissionDeclined(@NonNull String[] permissionName) {

    }

    @Override
    public void onPermissionPreGranted(@NonNull String permissionsName) {
        //权限已经打开了
        doYourThings();
    }

    @Override
    public void onPermissionNeedExplanation(@NonNull String permissionName) {
        //需要申请权限
        permissionHelper.requestAfterExplanation(permissionName);
    }

    @Override
    public void onPermissionReallyDeclined(@NonNull String permissionName) {
        boolean admitAppend = false;
        StringBuilder sb = new StringBuilder();
        for(int i=0,length=permissionArrays.length;i<length;i++){
            if (permissionArrays[i].equals(permissionName) || admitAppend){
                if (i<infoSize){
                    sb.append(getString(permissionInfo[i]));
                    admitAppend = true;
                }
            }
            if (i == length - 1){
                if ("".equals(sb.toString())){
                    Toast.makeText(this,R.string.open_permit,Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,sb.toString(),Toast.LENGTH_SHORT).show();
                }
            }else{
                if (!"".equals(sb.toString())){
                    sb.append("\n");
                }
            }
        }
        //禁止权限
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.parse("package:" + getPackageName());
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_SETTING);
    }

    //android6.0以下会触发
    @Override
    public void onNoPermissionNeeded() {
        doYourThings();
    }


    /**
     * 记得手动重写这个方法
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void doYourThings(){
        Toast.makeText(this,"权限全部打开完毕",Toast.LENGTH_SHORT).show();
    }

/*************************************************************统一权限管理*******************************************************************/

}
