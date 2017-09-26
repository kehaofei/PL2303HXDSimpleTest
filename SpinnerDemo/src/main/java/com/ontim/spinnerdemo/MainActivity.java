package com.ontim.spinnerdemo;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private EditText etNumber;
    private NumberAdapter mAdapter;
    private List<String> numberList;
    private PopupWindow pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNumber = (EditText) findViewById(R.id.et_number);
        
    }

    /**
     * 弹出选择号码的对话框
     *
     * @param view
     */
    public void showNumberList(View view) {
        //初始化listview控件和里边的数据
        ListView mListView = initListView();

        // 弹出一个PopupWindow的窗体, 把ListView作为其内容部分显示.
        pw = new PopupWindow(mListView, etNumber.getWidth() - 4, 300);
        //设置可以使用焦点
        pw.setFocusable(true);
        //设置点击pop外部可以被关闭
        pw.setOutsideTouchable(true);
        //设置一个pop的背景
        pw.setBackgroundDrawable(new BitmapDrawable());
        // 把popupwindow显示出来, 显示的位置是: 在输入框的下面, 和输入框是连着的.
        pw.showAsDropDown(etNumber, 2, -5);

    }

    private ListView initListView() {
        ListView mListView = new ListView(this);
        //去掉listview的下分割线
        mListView.setDividerHeight(0);
        mListView.setBackgroundResource(R.drawable.listview_background);
        //去掉右侧垂直滑动条
        mListView.setVerticalScrollBarEnabled(false);

        mListView.setOnItemClickListener(this);
        //模拟假数据
        numberList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            numberList.add("10000" + i);
        }
        mAdapter = new NumberAdapter();
        mListView.setAdapter(mAdapter);
        return mListView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String number = numberList.get(position);
        etNumber.setText(number);
        pw.dismiss();
    }


    private class NumberAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return numberList.size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            NumberHolder mHolder = null;
            if(convertView==null){
                convertView =View.inflate(MainActivity.this,R.layout.listview_item,null);
                mHolder=new NumberHolder();
                mHolder.tvNumber= (TextView) convertView.findViewById(R.id.tv_listview_item_number);
                mHolder.ibDelete= (ImageButton) convertView.findViewById(R.id.ib_listview_item_delete);
                convertView.setTag(mHolder);
            }else {
                mHolder = (NumberHolder) convertView.getTag();
            }
            mHolder.tvNumber.setText(numberList.get(position));
            mHolder.ibDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    numberList.remove(position);
                    mAdapter.notifyDataSetChanged();
                    if(numberList.size()==0){
                        pw.dismiss();
                    }
                }
            });
            return convertView;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


    }

    class NumberHolder {
        TextView tvNumber;
        ImageButton ibDelete;
    }

}
