package com.dfey.cxy.listviewchoose;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dfey.cxy.listviewchoose.Adapter.MyListAdapter;
import com.dfey.cxy.listviewchoose.Bean.ItemBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements MyListAdapter.OnShowItemClickListener {

    private ListView listView;
    private List<ItemBean> dataList, selectedList;
    private List<String> paths, items;
    private MyListAdapter myAdapter;
    private RelativeLayout rootView;
    private LinearLayout menuView;
    private LinearLayout openView;
    private static boolean isShow;
    String rootPath= Environment.getExternalStorageDirectory()+"/下户宝/mRecord/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isShow = false;
        setContentView(R.layout.activity_main);
        Button delbtn = (Button) findViewById(R.id.del);
        rootView = (RelativeLayout) findViewById(R.id.rootView);
        menuView = (LinearLayout) findViewById(R.id.button_group);
        listView = (ListView) findViewById(R.id.listView1);
        dataList = new ArrayList<ItemBean>();
        selectedList = new ArrayList<ItemBean>();
        getFileDir(rootPath);
        for (int i = 0; i<items.size(); i++) {
            ItemBean item = new ItemBean();
            item.setTitle(items.get(i));
            item.setImgRes(R.mipmap.mp3);
            item.setChecked(false);
            item.setShow(isShow);
            dataList.add(item);
        }
        myAdapter = new MyListAdapter(dataList, this);
        myAdapter.setOnShowItemClickListener(MainActivity.this);
        listView.setAdapter(myAdapter);
        delbtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO 自动生成的方法存根
                showMenu();
                isShow = true;
                selectedList.clear();
                for (ItemBean item : dataList) {
                    item.setShow(isShow);
                }
                myAdapter.notifyDataSetChanged();
            }
        });
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO 自动生成的方法存根
                if (isShow) {
                    ItemBean item = dataList.get(position);
                    boolean isChecked = item.isChecked();
                    if (isChecked) {
                        item.setChecked(false);
                    } else {
                        item.setChecked(true);
                    }
                    myAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    //显示选择删除等的菜单
    private void showMenu() {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        openView = (LinearLayout) inflater.inflate(R.layout.delmenu_layout, null);
        rootView.removeView(menuView);
        rootView.addView(openView, lp);
        final Button sBtn = (Button) openView.findViewById(R.id.selectAll);
        Button dBtn = (Button) openView.findViewById(R.id.del_button);
        Button cBtn = (Button) openView.findViewById(R.id.cancel_button);
        sBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO 自动生成的方法存根
                if ("全选".equals(sBtn.getText().toString())) {
                    for (ItemBean bean : dataList) {
                        if (!bean.isChecked()) {
                            bean.setChecked(true);
                            if (!selectedList.contains(bean)) {
                                selectedList.add(bean);
                            }
                        }
                    }
                    myAdapter.notifyDataSetChanged();
                    sBtn.setText("反选");
                } else if ("反选".equals(sBtn.getText().toString())) {
                    for (ItemBean bean : dataList) {
                        bean.setChecked(false);
                        if (!selectedList.contains(bean)) {
                            selectedList.remove(bean);
                        }
                    }
                    myAdapter.notifyDataSetChanged();
                    sBtn.setText("全选");
                }
            }
        });

        dBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO 自动生成的方法存根
                if (selectedList != null && selectedList.size() > 0) {
                    dataList.removeAll(selectedList);
                    myAdapter.notifyDataSetChanged();
                    selectedList.clear();
                } else {
                    Toast.makeText(MainActivity.this, "请选择条目", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO 自动生成的方法存根
                if (isShow) {
                    selectedList.clear();
                    for (ItemBean bean : dataList) {
                        bean.setChecked(false);
                        bean.setShow(false);
                    }
                    myAdapter.notifyDataSetChanged();
                    isShow = false;
                    restoreView();
                }
            }
        });

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

    private void restoreView() {
        rootView.addView(menuView);
        if (openView != null) {
            rootView.removeView(openView);
            openView = null;
        }

    }

    public void getFileDir(String filePath) {
        try {
            items = new ArrayList<String>();
            paths = new ArrayList<String>();
            File f = new File(filePath);
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
}
