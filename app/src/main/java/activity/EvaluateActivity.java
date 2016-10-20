package activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.easeui.widget.EaseTitleBar;

import utils.AlertUtilts;
import utils.HttpUtils;
import utils.MyConstant;
import utils.ToastUtils;
import wfu.com.campusrepairplatformUser.R;

/**
 * Created by Lenovo on 2016/8/12.
 */
public class EvaluateActivity extends Activity {

    private EaseTitleBar backTitleBar;
    private EditText evaluate;
    private Button commit;

    private String rid;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final Bundle bundle = msg.getData();
            switch (msg.what) {
                case 0x123:
                    String result = bundle.getString("result");
                    if ("200".equals(result)) {
                        AlertUtilts.showDialog(EvaluateActivity.this, "通知！", "评价成功，点击返回", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(EvaluateActivity.this,RepaireDetailsActivity.class);
                                Bundle ridBundle = new Bundle();
                                ridBundle.putString("rid",rid);
                                intent.putExtra("rid",ridBundle);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        });
                    } else {
                        ToastUtils.show(getApplicationContext(), "评价失败，请稍后再试", Toast.LENGTH_SHORT);
                    }
                    break;
                case 0x124:
                    String error = bundle.getString("result");
                    if ("404".equals(error)) {
                        ToastUtils.show(getApplicationContext(), "评价失败，请稍后再试", Toast.LENGTH_SHORT);
                    } else {
                        ToastUtils.show(getApplicationContext(), "评价失败，请稍后再试", Toast.LENGTH_SHORT);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate);
        Intent intent = getIntent();
        rid = intent.getStringExtra("rid");
        initView();
        initEvent();
    }

    private void initEvent() {
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!evaluate.getText().toString().isEmpty()) {
                    setTableEvaluate(evaluate.getText().toString());
                } else {
                    ToastUtils.show(getApplicationContext(), "请填写评价", Toast.LENGTH_SHORT);
                }
            }
        });
        backTitleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setTableEvaluate(String s) {
        String param = "rid=" + rid+"&evaluate="+s;
        try {
            HttpUtils.doPostAsyn(MyConstant.BASE_URL+MyConstant.ADD_EVALUATE, param, new HttpUtils.CallBack() {
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
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("result", "404");
            message.setData(bundle);
            message.what = 0x124;
            handler.sendMessage(message);
            e.printStackTrace();
        }
    }

    private void initView() {
        backTitleBar = (EaseTitleBar) findViewById(R.id.evaluate_easeTitleBar);
        evaluate = (EditText) findViewById(R.id.et_evaluate);
        commit = (Button) findViewById(R.id.btn_evaluate_commit);
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
