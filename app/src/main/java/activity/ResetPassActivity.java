package activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.easeui.widget.EaseTitleBar;

import bean.User;
import utils.AlertUtilts;
import utils.HttpUtils;
import utils.MyConstant;
import utils.SPUtils;
import utils.StringsUtils;
import utils.ToastUtils;
import wfu.com.campusrepairplatformUser.R;

/**
 * Created by Lenovo on 2016/8/5.
 */
public class ResetPassActivity extends Activity {

    private final static String TAG = "ResetPassActivityy";

    private EditText olderPass;
    private EditText newPass;
    private TextView olderPassNone;
    private TextView newPassNone;
    private Button commit;
    private EaseTitleBar titleBar;
    private User user = null;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0x123:
                    Bundle bundle = msg.getData();
                    String result = bundle.getString("result");
                    if("200".equals(result)){
                        AlertUtilts.showDialog(ResetPassActivity.this, "通知", "修改成功，请重新登录", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(ResetPassActivity.this, SettingsFragment.class);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        });
                    }else if("404".equals(result)){
                        ToastUtils.show(getApplicationContext(),"旧密码不正确",Toast.LENGTH_SHORT);
                    }else{
                        ToastUtils.show(getApplicationContext(),"修改失败",Toast.LENGTH_SHORT);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initView();
        initEvent();
    }

    private void initView() {
        titleBar = (EaseTitleBar) findViewById(R.id.easeTitleBar_resetpass);
        olderPass = (EditText) findViewById(R.id.older_pass);
        olderPassNone = (TextView) findViewById(R.id.older_pass_none);
        newPass = (EditText) findViewById(R.id.new_pass);
        newPassNone = (TextView) findViewById(R.id.new_pass_none);
        commit = (Button) findViewById(R.id.reset_pass_commit);
        user = (User) SPUtils.getBean(getApplicationContext(), "User");

    }

    private void initEvent() {
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String olderpass = olderPass.getText().toString();
                String newpass = newPass.getText().toString();
                updatePass(olderpass, newpass);
            }
        });
        olderPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (olderPass.getText().toString().equals("")) {
                    olderPassNone.setVisibility(View.VISIBLE);
                } else {
                    olderPassNone.setVisibility(View.GONE);
                }
            }
        });
        newPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (newPass.getText().toString().equals("")) {
                    newPassNone.setVisibility(View.VISIBLE);
                } else {
                    newPassNone.setVisibility(View.GONE);
                }
            }
        });
        olderPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (olderPass.getText().toString().equals("")) {
                    olderPassNone.setVisibility(View.VISIBLE);
                } else {
                    olderPassNone.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        newPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (newPass.getText().toString().equals("")) {
                    newPassNone.setVisibility(View.VISIBLE);
                } else {
                    newPassNone.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
     * 修改密码
     * @param olderpass
     * @param newpass
     */
    private void updatePass(String olderpass, String newpass) {
        if (!StringsUtils.isEmpty(olderpass) && !StringsUtils.isEmpty(newpass)) {
            String param = "uid=" + user.getUid() + "&olderpass=" + olderpass + "&newpass=" + newpass;
            try {
                HttpUtils.doPostAsyn(MyConstant.BASE_URL + MyConstant.UPDATE_Pass, param, new HttpUtils.CallBack() {
                    @Override
                    public void onRequestComplete(String result) {
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("result", result);
                        message.setData(bundle);
                        message.what = 0x123;
                        handler.sendMessage(message);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            ToastUtils.show(ResetPassActivity.this, "密码不能为空", Toast.LENGTH_SHORT);
        }
    }
}
