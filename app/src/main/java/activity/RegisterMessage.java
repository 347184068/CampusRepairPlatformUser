package activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import bean.User;
import utils.AlertUtilts;
import utils.GsonUtils;
import utils.HttpUtils;
import utils.MyConstant;
import utils.StringsUtils;
import utils.ToastUtils;
import wfu.com.campusrepairplatformUser.R;

/**
 * Created by Lenovo on 2016/8/2.
 */
public class RegisterMessage extends Activity {

    private final static String TAG = "RegisterMessage";

    private String account;
    private String password;
    private EditText name;
    private EditText phoneNum;
    private RadioGroup sex;
    private Spinner college;
    private ProgressDialog dialog;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                dialog.dismiss();
                Bundle bundle = msg.getData();
                String result = bundle.getString("result");
                switch (result) {
                    case "200":
                        AlertUtilts.showDialog(RegisterMessage.this, "通知！", "注册成功,点击返回登录", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                        break;
                    case "404":
                        AlertUtilts.showDialog(RegisterMessage.this, "通知！", "注册失败,点击重新注册", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                startActivity(new Intent(RegisterMessage.this, RegisterActivity.class));
                                finish();
                            }
                        });
                        break;
                    case "5":
                        ToastUtils.show(getApplicationContext(), "手机号格式不正确", Toast.LENGTH_SHORT);
                        break;
                    case "4":
                        AlertUtilts.showDialog(RegisterMessage.this, "通知！", "用户已存在，请重新注册", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                startActivity(new Intent(RegisterMessage.this, RegisterActivity.class));
                                finish();
                            }
                        });
                        break;
                    default:
                        AlertUtilts.showDialog(RegisterMessage.this, "通知！", "注册失败，点击重新注册", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                startActivity(new Intent(RegisterMessage.this, RegisterActivity.class));
                                finish();
                            }
                        });
                        break;
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_message);
        Intent intent = getIntent();
        account = intent.getStringExtra("account");
        password = intent.getStringExtra("password");
        dialog = new ProgressDialog(this);
        dialog.setMessage("注册中...");
        dialog.setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView() {
        name = (EditText) findViewById(R.id.name_mess);
        phoneNum = (EditText) findViewById(R.id.phone_num);
        sex = (RadioGroup) findViewById(R.id.radio_mess);
        college = (Spinner) findViewById(R.id.msg_college);
    }


    public void register_msg(View view) {
        RadioButton radioButton = (RadioButton) findViewById(sex.getCheckedRadioButtonId());
        String sex = radioButton.getText().toString();
        String coll = college.getSelectedItem().toString();
        String n = name.getText().toString();
        String phone = phoneNum.getText().toString();
        if (!StringsUtils.isEmpty(n) && !StringsUtils.isEmpty(phone)) {
            User user = new User();
            //设置属性
            user.setUid(account);
            user.setPassword(password);
            user.setName(n);
            user.setPhone(phone);
            user.setSex(sex);
            user.setCollege(coll);
            registerUser(user);
        } else {
            ToastUtils.show(getApplicationContext(), "请检查是否有未填项", Toast.LENGTH_SHORT);
        }

    }

    /**
     * 注册用户
     * @param user
     */
    private void registerUser(User user) {
        String jsonUser = GsonUtils.toGson(user);
        String param = "user=" + jsonUser;
        try {
            dialog.show();
            HttpUtils.doPostAsyn(MyConstant.BASE_URL + MyConstant.REGISTER_URL, param, new HttpUtils.CallBack() {
                @Override
                public void onRequestComplete(String result) {
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("result", result);
                    msg.setData(bundle);
                    msg.what = 0x123;
                    handler.sendMessage(msg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertUtilts.showDialog(RegisterMessage.this, "通知！", "注册失败，点击重新注册", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startActivity(new Intent(RegisterMessage.this, RegisterActivity.class));
                            finish();
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        AlertUtilts.showDialog(RegisterMessage.this, "注意！", "你确定要放弃注册吗？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

    }


}
