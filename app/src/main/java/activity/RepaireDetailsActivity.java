package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.easeui.widget.EaseTitleBar;

import java.util.ArrayList;
import java.util.List;

import adapter.GridViewAdapter;
import bean.RepaireTable;
import photopicker.intent.PhotoPreviewIntent;
import utils.GsonUtils;
import utils.HttpUtils;
import utils.LogUtil;
import utils.MyConstant;
import utils.TimeUtils;
import utils.ToastUtils;
import wfu.com.campusrepairplatformUser.R;

/**
 * Created by Lenovo on 2016/8/11.
 */
public class RepaireDetailsActivity extends Activity {

    private final static String TAG = "RepaireDetailsActivity";

    private static final int REQUEST_PREVIEW_CODE = 20;

    private String rid;  //报修单唯一id

    private TextView repaireStatus;
    private TextView repaireType;
    private TextView repaireName;
    private TextView repaireDes;
    private TextView repaireGoods;
    private TextView repairePosition;
    private TextView repairPersonnel;
    private TextView getTableTime;
    private TextView completeTableTime;
    private TextView finalStatus;
    private TextView repaireEvaluate;

    private Button confirm;
    private EaseTitleBar detailes_easeTitleBar;
    private LinearLayout tableLayout;
    private LinearLayout imgDesLayout;
    private LinearLayout evaluateLayout;
    private GridView repaireImg;
    private GridViewAdapter imgAdapter;

    private ArrayList<String> imagePaths = new ArrayList<String>();
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what){
                case 0x123:
                    String tableJson = bundle.getString("result");
                    if("404".equals(tableJson)){
                        ToastUtils.show(getApplicationContext(),"获取报修单详情失败", Toast.LENGTH_SHORT);
                    }else {
                        RepaireTable repaireTable = GsonUtils.fromJson(tableJson,RepaireTable.class);
                        LogUtil.e("1",repaireTable.toString());
                        LogUtil.e("1",tableJson);
                        refreshTableData(repaireTable);
                    }
                    break;
                case 0x124:
                    if("404".equals(bundle.getString("result"))){
                        ToastUtils.show(getApplicationContext(),"获取报修单详情失败", Toast.LENGTH_SHORT);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repaire_details);
        Intent intent = getIntent();
        rid = intent.getStringExtra("rid");
        initView();
        getRepaireData();
        initEvent();
    }

    private void initEvent() {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RepaireDetailsActivity.this,EvaluateActivity.class);
                intent.putExtra("rid",rid);
                startActivityForResult(intent, 0);
            }
        });
        repaireImg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PhotoPreviewIntent intent = new PhotoPreviewIntent(getApplicationContext());
                intent.setCurrentItem(position);
                intent.setPhotoPaths(imagePaths);
                intent.setClickDelete("false");
                startActivity(intent);
            }
        });
        detailes_easeTitleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RepaireDetailsActivity.this,RepaireRecordActivity.class);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }


    /**
     * 更新界面
     * @param repaireTable
     */
    private void refreshTableData(RepaireTable repaireTable) {
        setStatus(repaireTable);
        repaireType.setText(repaireTable.getType());
        repaireName.setText(repaireTable.getRepaireName());
        repaireDes.setText(repaireTable.getRepaireDes());
        repaireGoods.setText(goodsListToString(repaireTable.getGoods()));
        repairePosition.setText(repaireTable.getBudlingId() + repaireTable.getRoomNum());
        if(0==repaireTable.getRepaireStatus()){
            tableLayout.setVisibility(View.GONE);
        }else if(1==repaireTable.getRepaireStatus()){
            tableLayout.setVisibility(View.VISIBLE);
            repairPersonnel.setText(repaireTable.getRepairman().getNickName());
            getTableTime.setText(TimeUtils.transDate1(repaireTable.getGet_repaire_time()));
            completeTableTime.setText("无");
            finalStatus.setText("维修中");
        }else{
            tableLayout.setVisibility(View.VISIBLE);
            repairPersonnel.setText(repaireTable.getRepairman().getNickName());
            getTableTime.setText(TimeUtils.transDate1(repaireTable.getGet_repaire_time()));
            completeTableTime.setText(TimeUtils.transDate1(repaireTable.getOver_time()));
            finalStatus.setText("维修成功");
            if("".equals(repaireTable.getEvaluate())){
                repaireEvaluate.setText("未做出评价");
            }else{
                repaireEvaluate.setText(repaireTable.getEvaluate());
            }
        }
        if(repaireTable.getImgUrl().isEmpty()){
            imgDesLayout.setVisibility(View.GONE);
        }else{
            imgDesLayout.setVisibility(View.VISIBLE);
            imagePaths = getImgUrl(repaireTable.getImgUrl());
            imgAdapter = new GridViewAdapter(getApplicationContext(),imagePaths,true);
            repaireImg.setAdapter(imgAdapter);
            imgAdapter.notifyDataSetChanged();
        }
    }

    private ArrayList<String> getImgUrl(ArrayList<String> imgUrl) {
        ArrayList<String> arrayList = new ArrayList<String>();
        for(int i = 0 ;  i < imgUrl.size();i++){
            arrayList.add(MyConstant.BASE_URL+imgUrl.get(i));
        }
        return arrayList;
    }

    /**
     * 根据报修表状态，改变ui
     * @param table
     */
    private void setStatus(RepaireTable table) {
        switch (table.getRepaireStatus()){
            case 0:
                repaireStatus.setText("[待维修]");
                repaireStatus.setTextColor(getResources().getColor(R.color.red));
                confirm.setEnabled(false);
                confirm.setVisibility(View.VISIBLE);
                confirm.setBackgroundResource(R.drawable.btn_unclickable_bg);
                confirm.setText("待维修,无法进行操作");
                evaluateLayout.setVisibility(View.GONE);
                break;
            case 1:
                repaireStatus.setText("[维修中]");
                repaireStatus.setTextColor(getResources().getColor(R.color.gray));
                confirm.setEnabled(false);
                confirm.setBackgroundResource(R.drawable.btn_unclickable_bg);
                confirm.setVisibility(View.VISIBLE);
                evaluateLayout.setVisibility(View.GONE);
                confirm.setText("维修中,无法进行操作");
                break;
            case 2:
                if(table.getConfirmStatus().equals("0")){
                    repaireStatus.setText("[维修完成待确认]");
                    repaireStatus.setTextColor(getResources().getColor(R.color.orange));
                    confirm.setVisibility(View.VISIBLE);
                    evaluateLayout.setVisibility(View.GONE);
                    confirm.setEnabled(true);
                    confirm.setText("确认完成");
                }else{
                    repaireStatus.setText("[维修完成已确认]");
                    repaireStatus.setTextColor(getResources().getColor(R.color.green));
                    confirm.setVisibility(View.GONE);
                    evaluateLayout.setVisibility(View.VISIBLE);
                }
                break;

        }
    }


    private void initView() {
        repaireStatus = (TextView) findViewById(R.id.tv_repaire_status);
        repaireType = (TextView) findViewById(R.id.tv_repaire_type);
        repaireName = (TextView) findViewById(R.id.tv_repaire_man);
        repaireDes = (TextView) findViewById(R.id.tv_repaire_des);
        repaireGoods = (TextView) findViewById(R.id.tv_repaire_goods);
        repairePosition = (TextView) findViewById(R.id.tv_repaire_position);
        repaireImg = (GridView) findViewById(R.id.repaire_details_img);
        repairPersonnel = (TextView) findViewById(R.id.tv_repair_personnel);
        getTableTime = (TextView) findViewById(R.id.tv_get_tabletime);
        completeTableTime = (TextView) findViewById(R.id.tv_complete_tabletime);
        finalStatus = (TextView) findViewById(R.id.tv_repaire_final_status);
        confirm = (Button) findViewById(R.id.btn_confirm);
        tableLayout = (LinearLayout) findViewById(R.id.layout_table_status);
        imgDesLayout = (LinearLayout) findViewById(R.id.layout_img_des);
        detailes_easeTitleBar = (EaseTitleBar) findViewById(R.id.detailes_easeTitleBar);
        evaluateLayout = (LinearLayout) findViewById(R.id.layout_evaluate);
        repaireEvaluate = (TextView) findViewById(R.id.tv_repaire_evaluate);
    }

    /**
     * 获取报修详情
     */
    private void getRepaireData() {
        String param = "rid="+rid;
        try {
            HttpUtils.doPostAsyn(MyConstant.BASE_URL+MyConstant.REPAIRE_RECORD_BYRID, param, new HttpUtils.CallBack() {
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
            e.printStackTrace();
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("result","404");
            message.setData(bundle);
            message.what = 0x124;
            handler.sendMessage(message);
        }
    }

    /**
     * 规范显示报修物品
     * @param goods
     * @return
     */
    private String goodsListToString(List<String> goods) {
        StringBuilder sb = new StringBuilder();
        for(String temp : goods){
            sb.append(temp+" ");
        }
        return sb.toString();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            ToastUtils.show(getApplicationContext(),"操作成功",Toast.LENGTH_SHORT);
            rid = data.getBundleExtra("rid").getString("rid");
            getRepaireData();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RepaireDetailsActivity.this,RepaireRecordActivity.class);
        setResult(RESULT_OK,intent);
        finish();
        super.onBackPressed();
    }

}
