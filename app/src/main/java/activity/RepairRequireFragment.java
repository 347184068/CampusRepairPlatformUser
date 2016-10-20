package activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.widget.EaseTitleBar;

import java.util.ArrayList;
import java.util.List;

import adapter.GridViewAdapter;
import bean.RepaireTable;
import bean.User;
import photopicker.PhotoPickerActivity;
import photopicker.PhotoPreviewActivity;
import photopicker.SelectModel;
import photopicker.intent.PhotoPickerIntent;
import photopicker.intent.PhotoPreviewIntent;
import utils.AlertUtilts;
import utils.Base64Method;
import utils.GsonUtils;
import utils.HttpUtils;
import utils.MyConstant;
import utils.SPUtils;
import utils.ToastUtils;
import wfu.com.campusrepairplatformUser.R;

/**
 * Created by Lenovo on 2016/7/23.
 */
public class RepairRequireFragment extends Fragment  {

    private final static String TAG = "RepairRequireFragment";

    private static final int REQUEST_CAMERA_CODE = 10;
    private static final int REQUEST_PREVIEW_CODE = 20;
    private GridView gridView;
    private EaseTitleBar titleBar;
    private GridViewAdapter gridAdapter;
    private Spinner repaireType;
    private Spinner nextType;
    private Spinner teaching_building;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> buildingAdapter;
    private LinearLayout chooselayout;
    private LinearLayout goodsOne;
    private LinearLayout goodsTwo;
    private LinearLayout goodsThree;
    private ProgressDialog progressDialog;
    private EditText roomNumber;
    private EditText repaireDes;
    private TextView repaireName;
    private User user=null;
    private ArrayList<CheckBox> cbList = new ArrayList<CheckBox>();
    private ArrayList<String> typeItems = new ArrayList<String>();
    private ArrayList<String> buildingItems = new ArrayList<String>();
    private ArrayList<String> imagePaths = new ArrayList<String>();
    @Nullable

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0x001){
                progressDialog.dismiss();
                ToastUtils.show(getContext(), "提交成功", Toast.LENGTH_SHORT);
                clearAlldata();
            }else if(msg.what==0x002){
                progressDialog.dismiss();
                ToastUtils.show(getContext(), "提交失败", Toast.LENGTH_SHORT);
            }else if(msg.what==0x003){
                progressDialog.dismiss();
                ToastUtils.show(getContext(), "提交失败", Toast.LENGTH_SHORT);
            }
        }
    };
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_repaire, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setGridView();
        initView();
        intiEvent();
        findAllCb();
    }

    private void initView() {
        titleBar = (EaseTitleBar) getView().findViewById(R.id.easeTitleBar1);
        repaireType = (Spinner) getView().findViewById(R.id.repaire_type);
        nextType = (Spinner) getView().findViewById(R.id.repaire_next_type);
        teaching_building = (Spinner) getView().findViewById(R.id.teaching_building_id);
        chooselayout = (LinearLayout) getView().findViewById(R.id.type_choose_layout);
        goodsOne = (LinearLayout) getView().findViewById(R.id.goods_checkbox_one);
        goodsTwo = (LinearLayout) getView().findViewById(R.id.goods_checkbox_two);
        goodsThree = (LinearLayout) getView().findViewById(R.id.goods_checkbox_three);
        roomNumber = (EditText) getView().findViewById(R.id.room_number);
        repaireDes = (EditText) getView().findViewById(R.id.repaire_describe);
        repaireName = (TextView) getView().findViewById(R.id.tv_repairer_name);
        adapter = new ArrayAdapter<String>(getContext(),R.layout.support_simple_spinner_dropdown_item,typeItems);
        buildingAdapter = new ArrayAdapter<String>(getContext(),R.layout.support_simple_spinner_dropdown_item,buildingItems);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("提交中...");
        progressDialog.setCanceledOnTouchOutside(false);
        user = (User) SPUtils.getBean(getContext(), "User");
        if(user!=null){
            repaireName.setText(user.getName());
        }else{
            repaireName.setText(EMClient.getInstance().getCurrentUser());
        }
    }


    private void intiEvent() {
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertUtilts.showDialog(getActivity(), "提示", "你确定要提交吗？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        progressDialog.show();
                        commitData();
//                        //提交成功后
//                        clearAlldata();
//                        progressDialog.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

            }
        });
        nextType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = parent.getItemAtPosition(position).toString();
                if (type.equals(getString(R.string.teaching_building_goodsrepaire))) {
                    cbClear();
                    goodsThree.setVisibility(View.VISIBLE);
                    goodsOne.setVisibility(View.GONE);
                    goodsTwo.setVisibility(View.GONE);
                } else if (type.equals(getString(R.string.apartment_building_goodsrepaire))) {
                    cbClear();
                    goodsOne.setVisibility(View.VISIBLE);
                    goodsTwo.setVisibility(View.GONE);
                    goodsThree.setVisibility(View.GONE);
                } else if (type.equals(getString(R.string.apartment_building_netrepaire))) {
                    cbClear();
                    goodsTwo.setVisibility(View.VISIBLE);
                    goodsOne.setVisibility(View.GONE);
                    goodsThree.setVisibility(View.GONE);
                } else {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        repaireType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = parent.getItemAtPosition(position).toString();
                if (type.equals(getString(R.string.apartment_repaire))) {
                    //如果是公寓报修 setdata
                    if (typeItems.size() != 0) typeItems.clear();
                    if (buildingItems.size() != 0) buildingItems.clear();
                    setNextTypeData(type);

                } else if (type.equals(getString(R.string.teaching_building_repaire))) {
                    //如果是教学楼报修 1.showbuildingid2.setdata
                    if (typeItems.size() != 0) typeItems.clear();
                    if (buildingItems.size() != 0) buildingItems.clear();
                    setNextTypeData(type);
                } else {
                    chooselayout.setVisibility(View.GONE);
                    goodsThree.setVisibility(View.GONE);
                    goodsOne.setVisibility(View.GONE);
                    goodsTwo.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String imgs = (String) parent.getItemAtPosition(position);
                if ("000000".equals(imgs)) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(getContext());
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(6); // 最多选择照片数量，默认为6
                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                    startActivityForResult(intent, REQUEST_CAMERA_CODE);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(getContext());
                    intent.setCurrentItem(position);
                    intent.setPhotoPaths(imagePaths);
                    intent.setClickDelete("true");
                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                }
            }
        });
    }

    /**
     * 提交报修数据
     */
    private void commitData() {
        if(!"".equals(repaireType.getSelectedItem().toString())){
            if(!"".equals(roomNumber.getText().toString())){
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String mainType = repaireType.getSelectedItem().toString();
                        String bulidingId = teaching_building.getSelectedItem().toString();
                        String type = nextType.getSelectedItem().toString();
                        String roomNum = roomNumber.getText().toString();
                        String repaired = repaireDes.getText().toString();
                        RepaireTable table= new RepaireTable();
                        table.setMainType(mainType);
                        table.setBudlingId(bulidingId);
                        table.setType(type);
                        table.setRoomNum(roomNum);
                        table.setRepaireDes(repaired);
                        table.setGoods(getGoodsList());
                        table.setRepaireName(user.getName());
                        table.setImgUrl(Base64Method.transToBase64(imagePaths));
                        registerTable(table);
                    }
                }).start();

            }else{
                ToastUtils.show(getContext(),"房间号不能为空", Toast.LENGTH_SHORT);
            }
        }else{
            ToastUtils.show(getContext(),"类型不能为空", Toast.LENGTH_SHORT);
        }
    }

    /**
     * 像服务器发送报修表
     * @param table
     */
    private void registerTable(RepaireTable table) {
        String jsonTable = GsonUtils.toGson(table);
        String param = "repaire="+jsonTable+"&uid="+user.getUid();
        try {
            HttpUtils.doPostAsyn(MyConstant.BASE_URL+MyConstant.REPAIRE_URL,param, new HttpUtils.CallBack() {
                @Override
                public void onRequestComplete(String result) {
                    if("200".equals(result)){
                        handler.sendEmptyMessage(0x001);
                    }else{
                        handler.sendEmptyMessage(0x003);
                    }
                }
            });
        } catch (Exception e) {
            handler.sendEmptyMessage(0x002);
            e.printStackTrace();
        }
    }

    /**
     * 获取保修物品列表
     * @return
     */
    private List<String> getGoodsList() {
        List<String> list = new ArrayList<String>();
        for(int i = 0 ; i <cbList.size();i++){
            CheckBox cb = cbList.get(i);
            if(cb.isChecked()) list.add(cb.getText().toString());
        }
        return list;
    }

    /**
     * 提交成功后清空所有数据
     */
    private void clearAlldata() {
        roomNumber.setText(null);
        repaireDes.setText(null);
        typeItems.clear();
        buildingItems.clear();
        buildingAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
        cbClear();
        imagePaths.clear();
        imagePaths.add("000000");
        gridAdapter.notifyDataSetChanged();
        repaireType.setSelection(0);
    }

    /**
     * 清空checkbox的值
     */
    private void cbClear() {
        for(int i= 0 ;i <cbList.size();i++){
            CheckBox cb = cbList.get(i);
            if(cb.isChecked()) cb.setChecked(false);
        }
    }

    private void setNextTypeData(String type) {
        if(type.equals(getString(R.string.apartment_repaire))){
            //设置公寓物品数据
            setApartmentData();
        }
        else if(type.equals(getString(R.string.teaching_building_repaire))){
            //设置教学楼数据
            setBulidingData();
        }else{

        }
        adapter.notifyDataSetChanged();
        chooselayout.setVisibility(View.VISIBLE);
    }

    private void setApartmentData() {
        buildingItems.add("1号公寓");
        buildingItems.add("2号公寓");
        buildingItems.add("3号公寓");
        buildingItems.add("4号公寓");
        buildingItems.add("5号公寓");
        buildingItems.add("6号公寓");
        buildingItems.add("7号公寓");
        buildingItems.add("8号公寓");
        buildingItems.add("9号公寓");
        buildingItems.add("10号公寓");
        buildingItems.add("11号A公寓");
        buildingItems.add("11号B公寓");
        buildingItems.add("12号A公寓");
        buildingItems.add("12号B公寓");
        teaching_building.setAdapter(buildingAdapter);
        typeItems.add(getString(R.string.apartment_building_goodsrepaire));
        typeItems.add(getString(R.string.apartment_building_netrepaire));
        nextType.setAdapter(adapter);
    }

    private void setBulidingData() {
        buildingItems.add("1号教学楼");
        buildingItems.add("2号教学楼");
        buildingItems.add("3号教学楼");
        buildingItems.add("4号教学楼");
        buildingItems.add("5号教学楼");
        buildingItems.add("6号教学楼");
        buildingItems.add("7号教学楼");
        buildingItems.add("8号教学楼");
        teaching_building.setAdapter(buildingAdapter);
        typeItems.add("教学楼物品报修");
        nextType.setAdapter(adapter);
    }

    /**
     * 设置gridview参数
     */
    private void setGridView() {
        imagePaths.add("000000");
        gridView = (GridView) getView().findViewById(R.id.repaire_img);
        gridAdapter = new GridViewAdapter(getContext(),imagePaths);
        gridView.setHorizontalSpacing(0);
        gridView.setAdapter(gridAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                // 选择照片
                case REQUEST_CAMERA_CODE:
                    ArrayList<String> list = data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT);
                    loadAdpater(list);
                    break;
                // 预览
                case REQUEST_PREVIEW_CODE:
                    ArrayList<String> ListExtra = data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT);
                    loadAdpater(ListExtra);
                    break;
            }
        }
    }

    /**
     * 加载图片适配器
     * @param paths
     */
    private void loadAdpater(ArrayList<String> paths){
        if (imagePaths!=null&& imagePaths.size()>0){
            imagePaths.clear();
        }
        if (paths.contains("000000")){
            paths.remove("000000");
        }
        paths.add("000000");
        imagePaths.addAll(paths);
        gridAdapter = new GridViewAdapter(getContext(),imagePaths);
        gridView.setAdapter(gridAdapter);
    }

    private void findAllCb() {
        CheckBox cb1 = (CheckBox) getView().findViewById(R.id.goods1);
        CheckBox cb2 = (CheckBox) getView().findViewById(R.id.goods2);
        CheckBox cb3 = (CheckBox) getView().findViewById(R.id.goods3);
        CheckBox cb4 = (CheckBox) getView().findViewById(R.id.goods4);
        CheckBox cb5 = (CheckBox) getView().findViewById(R.id.goods5);
        CheckBox cb6 = (CheckBox) getView().findViewById(R.id.goods6);
        CheckBox cb7 = (CheckBox) getView().findViewById(R.id.goods7);
        CheckBox cb8 = (CheckBox) getView().findViewById(R.id.goods8);
        CheckBox cb9 = (CheckBox) getView().findViewById(R.id.goods9);
        CheckBox cb10 = (CheckBox) getView().findViewById(R.id.goods10);
        CheckBox cb11 = (CheckBox) getView().findViewById(R.id.goods11);
        CheckBox cb12 = (CheckBox) getView().findViewById(R.id.goods12);
        CheckBox cb13 = (CheckBox) getView().findViewById(R.id.goods13);
        CheckBox cb14 = (CheckBox) getView().findViewById(R.id.goods14);
        CheckBox cb15 = (CheckBox) getView().findViewById(R.id.goods15);
        CheckBox cb16 = (CheckBox) getView().findViewById(R.id.goods16);
        CheckBox cb17 = (CheckBox) getView().findViewById(R.id.goods17);
        CheckBox cb18 = (CheckBox) getView().findViewById(R.id.goods18);
        CheckBox cb19 = (CheckBox) getView().findViewById(R.id.goods19);
        cbList.add(cb1);
        cbList.add(cb2);
        cbList.add(cb3);
        cbList.add(cb4);
        cbList.add(cb5);
        cbList.add(cb6);
        cbList.add(cb7);
        cbList.add(cb8);
        cbList.add(cb9);
        cbList.add(cb10);
        cbList.add(cb11);
        cbList.add(cb12);
        cbList.add(cb13);
        cbList.add(cb14);
        cbList.add(cb15);
        cbList.add(cb16);
        cbList.add(cb17);
        cbList.add(cb18);
        cbList.add(cb19);
    }


}
