package com.qcec.debug.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.qcec.app.CoreActivity;
import com.qcec.core.R;
import com.qcec.log.crash.CrashInfoModel;
import com.qcec.log.crash.CrashManager;

import java.util.LinkedList;
import java.util.List;


public class DebugCrashListActivity extends CoreActivity {

    private ListView crashInfoLv;

    private CrashListItemAdapter adapter;
    private LinkedList<CrashInfoModel> dataList = new LinkedList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crash_info_list_activity);

        initView();
        initListView();
    }


    private void initView() {
//        getTitleBar().setTitle("崩溃列表");
//        getTitleBar().addRightViewItem("", "清空历史", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CrashManager.clearAllCrashInfos();
//                dataList.clear();
//                adapter.notifyDataSetChanged();
//            }
//        });
        crashInfoLv = (ListView) findViewById(R.id.crash_info_list_view);
        adapter = new CrashListItemAdapter();
        crashInfoLv.setDivider(null);
        crashInfoLv.setAdapter(adapter);
        crashInfoLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(DebugCrashListActivity.this, DebugCrashDetailActivity.class);
                intent.putExtra("crashInfo", dataList.get(position).getExp());
                startActivity(intent);

            }
        });
    }

    private void initListView() {
        List<CrashInfoModel> infos = CrashManager.getAllCrashInfos();

        dataList.addAll(infos);
        adapter.notifyDataSetChanged();
    }

    class CrashListItemAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup arg2) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = DebugCrashListActivity.this.getLayoutInflater().inflate(R.layout.crash_info_item, null);
                viewHolder = new ViewHolder();
                viewHolder.crashInfoTv = (TextView) convertView.findViewById(R.id.crash_info_item_tv);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.crashInfoTv.setText(dataList.get(position).getDate());

            return convertView;
        }

    }

    class ViewHolder {
        public TextView crashInfoTv;

    }
}
