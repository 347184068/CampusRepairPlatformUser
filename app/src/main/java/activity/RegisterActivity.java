package activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import utils.AlertUtilts;
import utils.StringsUtils;
import utils.ToastUtils;
import wfu.com.campusrepairplatformUser.R;

/**
 * Created by Lenovo on 2016/7/24.
 */
public class RegisterActivity extends Activity {

    private final static String TAG = "RegisterActivity";

    private EditText account;
    private EditText passWord;
    private EditText confirmPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        account = (EditText) findViewById(R.id.register_activtity_account);
        passWord = (EditText) findViewById(R.id.register_activity_password);
        confirmPass = (EditText) findViewById(R.id.register_activity_confirm_password);
//        dialog = new ProgressDialog(this);
//        dialog.setMessage("注册中...");
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                progressShow = false;
//            }
//        });
    }


    public void register(View view) {
        if (!StringsUtils.isEmpty(account.getText().toString()) &&
                !StringsUtils.isEmpty(passWord.getText().toString()) &&
                !StringsUtils.isEmpty(confirmPass.getText().toString())) {
            if(!StringsUtils.isChinese(account.getText().toString())){
                if (passWord.getText().toString().equals(confirmPass.getText().toString())) {
                    //下一步
                    Intent intent = new Intent(this, RegisterMessage.class);
                    intent.putExtra("account", account.getText().toString());
                    intent.putExtra("password", passWord.getText().toString());
                    startActivity(intent);
                    finish();
                } else {
                    ToastUtils.show(this, "两次密码输入不一致", Toast.LENGTH_SHORT);
                }
            }else{
                ToastUtils.show(this, "账号不可以是汉字", Toast.LENGTH_SHORT);
            }


        } else {
            ToastUtils.show(this, "请检查注册信息有未填项", Toast.LENGTH_SHORT);
        }
    }

    public void return_register(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
        AlertUtilts.showDialog(this, "注意！", "你确定要放弃注册吗？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

    }


    //    private void registerAccount() {
//        progressShow = true;
//        user.setName(userName.getText().toString());
//        user.setPassword(passWord.getText().toString());
//        user.setSid(account.getText().toString());
//        user.setPhone(phoneNum.getText().toString());
//        String jsonUser = GsonUtils.toGson(user);
//        String param = "user="+jsonUser;
//        try {
//            dialog.show();
//            HttpUtils.doPostAsyn(MyConstant.BASE_URL+MyConstant.REGISTER_URL,param, new HttpUtils.CallBack() {
//                @Override
//                public void onRequestComplete(String result) {
//                    if (!progressShow) {
//                        return;
//                    }
//                    dialog.dismiss();
//                    switch (result){
//                      case "200":
//                          runOnUiThread(new Runnable() {
//                              @Override
//                              public void run() {
//                                  AlertUtilts.showDialog(RegisterActivity.this, "通知！", "注册成功,点击返回登录", new DialogInterface.OnClickListener() {
//                                      @Override
//                                      public void onClick(DialogInterface dialog, int which) {
//                                          dialog.dismiss();
//                                          finish();
//
//                                      }
//                                  });
//                              }
//                          });
//                          break;
//                        case "404":
//
//
//                            break;
//                  }
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    AlertUtilts.showDialog(RegisterActivity.this, "通知！", "注册失败，点击重新注册", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//                }
//            });
//        }
//    }


}
