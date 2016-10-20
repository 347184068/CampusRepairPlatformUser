package activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.hyphenate.easeui.widget.EaseTitleBar;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import bean.User;
import photopicker.PhotoPickerActivity;
import photopicker.SelectModel;
import photopicker.intent.PhotoPickerIntent;
import utils.AlertUtilts;
import utils.Base64Method;
import utils.GsonUtils;
import utils.HttpUtils;
import utils.ImageLoader;
import utils.MyConstant;
import utils.SPUtils;
import utils.StringsUtils;
import utils.ToastUtils;
import view.RoundImageView;
import wfu.com.campusrepairplatformUser.R;

/**
 * Created by Lenovo on 2016/8/5.
 */
public class ChangePersonMsg extends Activity {

    private final static String TAG = "ChangePersonMsg";

    private static final int REQUEST_CAMERA_CODE = 10;
    private RoundImageView changeAvatarView;
    private EditText changeNameText;
    private EditText changePhoneNumText;
    private RadioGroup checkSexGroup;
    private RadioButton men;
    private RadioButton women;
    private Spinner changeCollegeSpinner;
    private EaseTitleBar titleBar;

    private boolean isChangeAvatar=false;

    private User olderUser = null;

    private ArrayList<String> imgpath = new ArrayList<String>();


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case 0x123:
                    String jsonResult = bundle.getString("current_user");
                    if (jsonResult.equals("5")) {
                        ToastUtils.show(getApplicationContext(), "手机号不正确", Toast.LENGTH_SHORT);
                    } else if(jsonResult.equals("")){
                        ToastUtils.show(getApplicationContext(), "修改失败", Toast.LENGTH_SHORT);
                    }
                    else{
                        User user = GsonUtils.fromJson(jsonResult, User.class);
                        saveUserBean(user);
                        ToastUtils.show(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT);
                    }
                    break;
                case 0x124:
                    ToastUtils.show(getApplicationContext(), "修改失败", Toast.LENGTH_SHORT);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_personal_msg);
        initView();
        intEvent();
    }


    private void intEvent() {
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertUtilts.showDialog(ChangePersonMsg.this, "提示！", "你确定要提交吗？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        upLoad();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        });

        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        changeAvatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoPickerIntent intent = new PhotoPickerIntent(getApplicationContext());
                intent.setSelectModel(SelectModel.SINGLE);
                intent.setShowCarema(true); // 是否显示拍照
                intent.setMaxTotal(1);
                intent.setSelectedPaths(imgpath); // 已选中的照片地址， 用于回显选中状态
                startActivityForResult(intent, REQUEST_CAMERA_CODE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private void saveUserBean(User user) {
        if (!(user instanceof Serializable) || user == null) return;
        SPUtils.putBean(getApplicationContext(), "User", user);
    }

    private void upLoad() {
        User user = new User();
        if (StringsUtils.isEmpty(changeNameText.getText().toString()) || changeNameText.getText().toString().equals(olderUser.getName())) {
            user.setName(olderUser.getName());
        } else {
            user.setName(changeNameText.getText().toString());
        }
        if (StringsUtils.isEmpty(changePhoneNumText.getText().toString()) || changePhoneNumText.getText().toString().equals(olderUser.getPhone())) {
            user.setPhone(olderUser.getPhone());
        } else {
            user.setPhone(changePhoneNumText.getText().toString());
        }
        RadioButton radioButton = (RadioButton) findViewById(checkSexGroup.getCheckedRadioButtonId());
        if (radioButton.getText().toString().equals(olderUser.getSex())) {
            user.setSex(olderUser.getSex());
        } else {
            user.setSex(radioButton.getText().toString());
        }
        if (changeCollegeSpinner.getSelectedItem().toString().equals(olderUser.getCollege())) {
            user.setCollege(olderUser.getCollege());
        }else{
            user.setCollege(changeCollegeSpinner.getSelectedItem().toString());
        }
        if (imgpath.size() != 0) {
            try {
                user.setAvatarUrl(Base64Method.encodeBase64File(Base64Method.scal(Uri.fromFile(new File(imgpath.get(0)))).getPath()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            isChangeAvatar = true;
        } else {
            user.setAvatarUrl(olderUser.getAvatarUrl());
            isChangeAvatar = false;
        }

        String jsonUser = GsonUtils.toGson(user);
        String param = "user=" + jsonUser + "&uid=" + olderUser.getUid()+"&ischangeavatar="+isChangeAvatar;
        try {
            HttpUtils.doPostAsyn(MyConstant.BASE_URL + MyConstant.UPDATE_USER, param, new HttpUtils.CallBack() {
                @Override
                public void onRequestComplete(String result) {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("current_user", result);
                    message.setData(bundle);
                    message.what = 0x123;
                    handler.sendMessage(message);
                }
            });
        } catch (Exception e) {
            handler.sendEmptyMessage(0x124);
            e.printStackTrace();
        }


    }

    private void initView() {
        changeAvatarView = (RoundImageView) findViewById(R.id.change_avatar);
        changeNameText = (EditText) findViewById(R.id.change_name);
        changePhoneNumText = (EditText) findViewById(R.id.change_phonenum);
        checkSexGroup = (RadioGroup) findViewById(R.id.change_sex);
        men = (RadioButton) findViewById(R.id.change_men);
        women = (RadioButton) findViewById(R.id.change_women);
        changeCollegeSpinner = (Spinner) findViewById(R.id.change_college);
        titleBar = (EaseTitleBar) findViewById(R.id.easeTitleBar);
        getUserInfo();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                // 选择照片
                case REQUEST_CAMERA_CODE:
                    ArrayList<String> list = data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT);
                    setAvatar(list);
            }
        }
    }

    private void setAvatar(ArrayList<String> list) {
        if (imgpath != null && imgpath.size() > 0) {
            imgpath.clear();
        }
        if (list.size() == 1) {
            imgpath.addAll(list);
            String imgUrl = imgpath.get(0);
            ImageLoader.getInstance().loadImage(imgUrl, changeAvatarView, false);
        } else {
            ImageLoader.getInstance().loadImage(olderUser.getAvatarUrl(), changeAvatarView, true);
        }
    }


    /**
     * 获取用户信息
     */
    private void getUserInfo() {
        olderUser = (User) SPUtils.getBean(this, "User");
        if (olderUser != null) {
            changeNameText.setText(olderUser.getName());
            changePhoneNumText.setText(olderUser.getPhone());
            if ("男".equals(olderUser.getSex())) {
                men.setChecked(true);
            } else {
                women.setChecked(true);
            }
            changeCollegeSpinner.setSelection(getCurrentSele(olderUser.getCollege()));
            if (olderUser.getAvatarUrl() == null) {
                changeAvatarView.setImageResource(R.drawable.default_avatar);
            } else {
                ImageLoader.getInstance().loadImage(MyConstant.BASE_URL + olderUser.getAvatarUrl(), changeAvatarView, true);
            }
        } else {
            ToastUtils.show(this, "无法获取个人信息，请重新登录后重试", Toast.LENGTH_LONG);
            finish();
            changeAvatarView.setImageResource(R.drawable.default_avatar);
        }
    }

    /**
     * 获取选择学院position
     *
     * @param college
     * @return
     */
    private int getCurrentSele(String college) {
        String[] collegeArray = getResources().getStringArray(R.array.spinner_college);
        int i;
        for (i = 0; i < collegeArray.length; i++) {
            if (college.equals(collegeArray[i])) {
                break;
            }
        }
        return i;
    }

}
