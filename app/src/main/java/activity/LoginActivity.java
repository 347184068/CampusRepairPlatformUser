package activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import java.io.Serializable;

import bean.User;
import utils.GsonUtils;
import utils.HttpUtils;
import utils.MyConstant;
import utils.NetWorkUtils;
import utils.SPUtils;
import utils.StringsUtils;
import utils.ToastUtils;
import wfu.com.campusrepairplatformUser.R;

/**
 * Created by Lenovo on 2016/7/22.
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private final static String TGA = "LoginActivity";

    private EditText userName;
    private EditText userPass;
    private Button login;
    private Button register;
    private CheckBox isRememberPass;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private ProgressDialog dialog;
    private boolean autoLogin = false;
    private boolean progressShow;
    private boolean isremenber;
    private String userJson;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case 0x123:
                    String jsonResult = bundle.getString("user_info");
                    User user = GsonUtils.fromJson(jsonResult, User.class);
                    saveUserBean(user);
                    break;
                case 0x124:
                    String isAdmin = bundle.getString("is_admin_user");
                    if("true".equals(isAdmin)){
                        dialog.dismiss();
                        ToastUtils.show(getApplicationContext(),"用户端无法登陆维修员账号",Toast.LENGTH_SHORT);
                    }else if("false".equals(isAdmin)){
                        loginSystem(userName.getText().toString(),userPass.getText().toString());
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        EaseUI.getInstance().init(this, null);
        // 如果用户名密码都有，直接进入主页面
        if (EMClient.getInstance().isLoggedInBefore()) {
            autoLogin = true;
            getUser(EMClient.getInstance().getCurrentUser());
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }
        initView();
    }


    private void initView() {
        userName = (EditText) findViewById(R.id.username);
        userPass = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.system_login);
        register = (Button) findViewById(R.id.system_register);
        isRememberPass = (CheckBox) findViewById(R.id.rememberPass);
        sp = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        isremenber = sp.getBoolean("remember_password", false);
        dialog = new ProgressDialog(this);
        dialog.setMessage("登录中...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                progressShow = false;
            }
        });
        register.setOnClickListener(this);
        login.setOnClickListener(this);
        if (isremenber) {
            userName.setText(sp.getString("account", ""));
            isRememberPass.setChecked(true);
        }
        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = sp.getString("account", "");
                String pass = sp.getString("password", "");
                if (userName.getText().toString().equals(name) && userName.getText().toString() != null) {
                    userPass.setText(pass);
                } else {
                    userPass.setText(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (EMClient.getInstance().getCurrentUser() != null) {
            userName.setText(EMClient.getInstance().getCurrentUser());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.system_login:
                if (!NetWorkUtils.isNetWorkConnected(this)) {
                    Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.show();
                checkIsAdmin(userName.getText().toString());
                break;
            case R.id.system_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }

    /**
     * 登陆系统
     * @param name
     * @param passWord
     */
    private void loginSystem(final String name, String passWord) {
        if (!StringsUtils.isEmpty(name) && !StringsUtils.isEmpty(passWord)) {
            progressShow = true;
            editor = sp.edit();
            if (isRememberPass.isChecked()) {
                editor.putString("account", name);
                editor.putString("password", passWord);
                editor.putBoolean("remember_password", true);
            } else {
                editor.clear();
            }
            editor.commit();
            EMClient.getInstance().login(name, passWord, new EMCallBack() {//回调
                @Override
                public void onSuccess() {
                    Log.d("main", "登录聊天服务器成功！");
                    if (!progressShow) {
                        return;
                    }
                    getUser(name);
                    EMClient.getInstance().groupManager().loadAllGroups();
                    EMClient.getInstance().chatManager().loadAllConversations();
                    if (!LoginActivity.this.isFinishing() && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onProgress(int progress, String status) {

                }

                @Override
                public void onError(int code, final String message) {
                    Log.d("main", "登录聊天服务器失败！");
                    if (!progressShow) {
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            dialog.dismiss();
                            ToastUtils.show(getApplicationContext(), "登录失败，用户名密码错误",
                                    Toast.LENGTH_SHORT);
                        }
                    });
                }
            });
        } else {
            dialog.dismiss();
            ToastUtils.show(this, "用户名或密码不能为空", Toast.LENGTH_SHORT);
        }
    }

    /**
     * 检查是否是维修员账号
     * 用户端无法登陆维修员账号
     * @param name
     */
    private void checkIsAdmin(String name) {
        String param = "id=" + name;
        if(!StringsUtils.isEmpty(name) && !StringsUtils.isEmpty(userPass.getText().toString())){
            try {
                HttpUtils.doPostAsyn(MyConstant.BASE_URL + MyConstant.CHECK_IS_ADMIN, param, new HttpUtils.CallBack() {
                    @Override
                    public void onRequestComplete(String result) {
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("is_admin_user", result);
                        message.setData(bundle);
                        message.what = 0x124;
                        handler.sendMessage(message);
                    }
                });
            } catch (Exception e) {
                dialog.dismiss();
                ToastUtils.show(getApplicationContext(),"连接服务器失败",Toast.LENGTH_SHORT);
            }
        }else{
            ToastUtils.show(getApplicationContext(),"用户名或密码不能为空",Toast.LENGTH_SHORT);

        }

    }

    /**
     * 将获取到的用户信息存储到本地
     * @param user
     */
    private void saveUserBean(User user) {
        if (!(user instanceof Serializable) || user == null) return;
        SPUtils.putBean(getApplicationContext(), "User", user);
    }

    /**
     * 从数据库获取用户信息
     * @param account
     */
    private void getUser(String account) {

        try {
            HttpUtils.doPostAsyn(MyConstant.BASE_URL + MyConstant.GET_USER_INFO_URL, "uid=" + account, new HttpUtils.CallBack() {
                @Override
                public void onRequestComplete(String result) {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("user_info", result);
                    message.setData(bundle);
                    message.what = 0x123;
                    handler.sendMessage(message);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    @Override
    protected void onResume() {
        super.onResume();
        if (autoLogin) {
            return;
        }
    }
}
