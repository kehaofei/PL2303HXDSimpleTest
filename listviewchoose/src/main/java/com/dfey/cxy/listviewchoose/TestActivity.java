package com.dfey.cxy.listviewchoose;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dfey.cxy.listviewchoose.Adapter.MyListAdapter;
import com.dfey.cxy.listviewchoose.Bean.ItemBean;
import com.dfey.cxy.listviewchoose.permission.PermissionHelper;
import com.dfey.cxy.listviewchoose.permission.callback.OnPermissionCallback;
import com.dfey.cxy.listviewchoose.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TestActivity extends Activity implements MyListAdapter.OnShowItemClickListener,OnPermissionCallback{

    private ListView listView;
    private List<ItemBean> dataList, selectedList;
    private List<String> paths, items;
    private List<File> fileList;
    private MyListAdapter myAdapter;
    private static boolean isShow;
    private boolean isSel = true;
    private String rootPath= Environment.getExternalStorageDirectory().getPath()+"/com.dfey.cxy.listviewchoose/UPDATE/";//"/下户宝/mRecord/";

    private TextView tv_sel;
    private ImageView iv_del;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        isShow = false;
        setContentView(R.layout.activity_record_lib);
        intiView();

        doPermissionCheck();

        getFileDir(rootPath);

        Log.d("打印信息：", this.getPackageName());

        for (int i = 0; i<items.size(); i++) {
            ItemBean item = new ItemBean();
            item.setTitle(items.get(i));
            item.setImgRes(R.mipmap.mp3);
            item.setChecked(false);
            item.setShow(isShow);
            dataList.add(item);
            file = new File(rootPath + dataList.get(i).getTitle());
            fileList.add(file);
        }
        myAdapter = new MyListAdapter(dataList, this);
        myAdapter.setOnShowItemClickListener(TestActivity.this);
        listView.setAdapter(myAdapter);
        iv_del.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO 自动生成的方法存根
                if (selectedList != null && selectedList.size() > 0) {
                    for (int i = 0;i<selectedList.size();i++){
                        file = new File(rootPath+selectedList.get(i).getTitle());
                        if (file.exists()){
                            fileList.add(file);
                            file.delete();
                        }else{
                            Toast.makeText(TestActivity.this, "文件已删除", Toast.LENGTH_SHORT).show();
                        }
                    }
                    dataList.removeAll(selectedList);
                    myAdapter.notifyDataSetChanged();
                    selectedList.clear();
                } else {
                    Toast.makeText(TestActivity.this, "请选择条目", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //选择的监听
        tv_sel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isShow = true;
                if (isSel){
                    tv_sel.setText("取消");
                }else{
                    tv_sel.setText("选择");
                    selectedList.clear();
                    for (ItemBean item : dataList) {
                        isShow = false;
                    }
                    myAdapter.notifyDataSetChanged();
                }
                selectedList.clear();
                for (ItemBean item : dataList) {
                    item.setShow(isShow);
                }
                myAdapter.notifyDataSetChanged();
                isSel = !isSel;
            }
        });
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (isShow) {
                    ItemBean item = dataList.get(position);
                    boolean isChecked = item.isChecked();
                    if (isChecked) {
                        item.setChecked(false);
                    } else {
                        item.setChecked(true);
                    }
                    myAdapter.notifyDataSetChanged();
                }else{
                    file = fileList.get(position);
                    Log.i("test","====fileName==="+file.getName());
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), "audio/*");
                    startActivity(intent);
                }
            }
        });
    }

    private void intiView() {
        listView = (ListView) findViewById(R.id.list);
        tv_sel = (TextView) findViewById(R.id.id_user_recordSub);
        iv_del = (ImageView) findViewById(R.id.id_user_recordLib_del);
        dataList = new ArrayList<ItemBean>();
        selectedList = new ArrayList<ItemBean>();
        fileList = new ArrayList<File>();
    }

    public void getFileDir(String filePath) {
        try {
            items = new ArrayList<String>();
            paths = new ArrayList<String>();


            File f = new File(filePath);

            if(!f.exists()){
                try {
                    boolean flag = f.mkdir();
                    if(flag){
                        f.canRead();
                        f.canWrite();
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            File [] files = f.listFiles();// 列出所有文件
            // 将所有文件存入list中
            if (files != null) {
                int count = files.length;// 文件个数
                for (int i = 0; i < count; i++) {
                    File file = files[i];
                    items.add(file.getName());
                    paths.add(file.getPath());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onShowItemClick(ItemBean bean) {
        // TODO 自动生成的方法存根
        if (bean.isChecked() && !selectedList.contains(bean)) {
            selectedList.add(bean);
        } else if (!bean.isChecked() && selectedList.contains(bean)) {
            selectedList.remove(bean);
        }
    }





    /*************************************************************统一权限管理*******************************************************************/

    private PermissionHelper permissionHelper;

    //值唯一即可,这是为了返回做标识使用
    private final int REQUEST_SETTING = -1;
    final String[] permissionArrays = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    final int permissionSize = permissionArrays.length;
    final int[] permissionInfo = {
            R.string.open_storage_permit};
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        permissionHelper.onActivityForResult(requestCode);
        //返回时重新进行检查
        if (requestCode == REQUEST_SETTING){
            doPermissionCheck();
        }
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
        getFileDir(rootPath);

        Toast.makeText(this,"权限全部打开完毕",Toast.LENGTH_SHORT).show();
    }




}
