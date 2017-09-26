package com.example.zq.popupwindowdemo;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.AndroidCharacter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;

public class MainActivity extends AppCompatActivity {

    private Button btnPopup;
    private String[] datas = {"选项1", "选项2", "选项3", "选项4", "选项5"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: 2016/5/17 设置该Activity使用的布局文件
        setContentView(R.layout.activity_main);
        btnPopup = (Button) findViewById(R.id.btnPopup);// TODO: 2016/5/17 获取弹出按钮控件
        // TODO: 2016/5/17 给按钮设置单击事件监听
        btnPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2016/5/17 构建一个popupwindow的布局
                View popupView = MainActivity.this.getLayoutInflater().inflate(R.layout.popupwindow, null);

                // TODO: 2016/5/17 为了演示效果，简单的设置了一些数据，实际中大家自己设置数据即可，相信大家都会。
                ListView lsvMore = (ListView) popupView.findViewById(R.id.lsvMore);
                lsvMore.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, datas));

                // TODO: 2016/5/17 创建PopupWindow对象，指定宽度和高度
                PopupWindow window = new PopupWindow(popupView, 400, 600);
                // TODO: 2016/5/17 设置动画
                window.setAnimationStyle(R.style.popup_window_anim);
                // TODO: 2016/5/17 设置背景颜色
                window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F8F8F8")));
                // TODO: 2016/5/17 设置可以获取焦点
                window.setFocusable(true);
                // TODO: 2016/5/17 设置可以触摸弹出框以外的区域
                window.setOutsideTouchable(true);
                // TODO：更新popupwindow的状态
                window.update();
                // TODO: 2016/5/17 以下拉的方式显示，并且可以设置显示的位置
                window.showAsDropDown(btnPopup, 0, 20);
            }
        });
    }
}
