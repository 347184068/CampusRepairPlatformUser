package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hyphenate.easeui.widget.EaseTitleBar;

import java.util.ArrayList;

import adapter.RepaireListAdapter;
import bean.RepaireTable;
import bean.User;
import utils.GsonUtils;
import utils.HttpUtils;
import utils.MyConstant;
import utils.NetWorkUtils;
import utils.SPUtils;
import utils.ToastUtils;
import wfu.com.campusrepairplatformUser.R;

/**
 * Created by Lenovo on 2016/8/5.
 */
public class RepaireRecordActivity extends Activity {

    private final static String TAG = "RepaireRecordActivity";


    private PullToRefreshListView repaireTableListView;
    private RepaireListAdapter repaireListAdapter;
    private ArrayList<RepaireTable> tableList;
    private EaseTitleBar titleBar;
    private User user = null;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what){
                case 0x123:
                    String result = bundle.getString("result");
                    if(result!=null){
                        tableList.clear();
                        tableList.addAll(GsonUtils.toRepaireTableList(result));
//                        repiareRecordDB.saveRepaireRecorde(tableList,user.getUid());
                        repaireListAdapter.notifyDataSetChanged();
                        repaireTableListView.onRefreshComplete();
                    }else {
                        ToastUtils.show(RepaireRecordActivity.this,"获取报修记录失败", Toast.LENGTH_SHORT);
                    }
                    break;
                case 0x124:
                    String error = bundle.getString("error");
                    if("404".equals(error)){
                        ToastUtils.show(RepaireRecordActivity.this,"获取报修记录失败", Toast.LENGTH_SHORT);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repaire_record);
        initView();
        initEvent();
    }

    private void initEvent() {
        repaireTableListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if(NetWorkUtils.isNetWorkConnected(RepaireRecordActivity.this)){
                    getData(false);
                }else{
                    refreshView.onRefreshComplete();
                    ToastUtils.show(RepaireRecordActivity.this,"网络不可用",Toast.LENGTH_SHORT);
                }
            }
        });
        repaireTableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RepaireTable table = tableList.get(position-1);
                Intent intent = new Intent(RepaireRecordActivity.this,RepaireDetailsActivity.class);
                intent.putExtra("rid",table.getRid());
                startActivityForResult(intent, 0);

            }
        });

        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 获取维修记录
     * @param isFromLocal
     */
    private void getData(boolean isFromLocal) {
        if(isFromLocal){
            tableList.clear();
//            tableList.addAll(repiareRecordDB.loadRepaireRecorde(user.getUid()));
            if (tableList.size()==0){
                getData(false);
                return;
            }
            repaireListAdapter.notifyDataSetChanged();
        }else{
            String param  = "uid="+user.getUid();
            try {
                HttpUtils.doPostAsyn(MyConstant.BASE_URL+MyConstant.REPAIRE_RECORD, param, new HttpUtils.CallBack() {
                    @Override
                    public void onRequestComplete(String result) {
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("result",result);
                        message.setData(bundle);
                        message.what = 0x123;
                        handler.sendMessage(message);
                    }
                });
            } catch (Exception e) {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("error","404");
                message.setData(bundle);
                message.what = 0x124;
                handler.sendMessage(message);
                e.printStackTrace();
            }
        }

    }

    private void initView() {
        repaireTableListView = (PullToRefreshListView) findViewById(R.id.repaire_record_listview);
        titleBar = (EaseTitleBar) findViewById(R.id.easeTitleBar_record);
        tableList = new ArrayList<RepaireTable>();
        repaireListAdapter = new RepaireListAdapter(getApplicationContext(),R.layout.item_repaire_record,tableList);
        repaireTableListView.setAdapter(repaireListAdapter);
        repaireTableListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
//        repiareRecordDB = RepiareRecordDB.getInstance(getApplication());
        user = (User) SPUtils.getBean(getApplicationContext(), "User");
        getData(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            getData(false);
        }
    }
}
