package activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import bean.User;
import utils.ImageLoader;
import utils.MyConstant;
import utils.SPUtils;
import view.RoundImageView;
import wfu.com.campusrepairplatformUser.R;

public class SettingsFragment extends Fragment implements OnClickListener{

    private final static String TAG = "SettingsFragment";

    private RoundImageView avatarImageView;
    private TextView logoutTextView;
    private TextView personalNameTextView;
    private TextView personalIdTextView;
    private TextView personalCollegeTextView;
    private TextView repaireRecordTextView;
    private TextView changePmsgTextView;
    private TextView changePassTextView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        logoutTextView = (TextView) getView().findViewById(R.id.tv_logout);
        avatarImageView = (RoundImageView) getView().findViewById(R.id.img_personal_avatar);
        personalNameTextView = (TextView) getView().findViewById(R.id.personal_name);
        personalIdTextView = (TextView) getView().findViewById(R.id.personal_id);
        personalCollegeTextView = (TextView) getView().findViewById(R.id.personal_college);
        repaireRecordTextView = (TextView) getView().findViewById(R.id.tv_record_repaire);
        changePmsgTextView = (TextView) getView().findViewById(R.id.tv_change_personal_info);
        changePassTextView = (TextView) getView().findViewById(R.id.tv_change_password);
        repaireRecordTextView.setOnClickListener(this);
        changePmsgTextView.setOnClickListener(this);
        changePassTextView.setOnClickListener(this);
        logoutTextView.setOnClickListener(this);
        getUserInfo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_record_repaire:
                startActivity(new Intent(getActivity(),RepaireRecordActivity.class));
                break;
            case R.id.tv_change_personal_info:
                startActivity(new Intent(getActivity(), ChangePersonMsg.class));
                break;
            case R.id.tv_change_password:
                startActivityForResult(new Intent(getActivity(), ResetPassActivity.class), 0);
                break;
            case R.id.tv_logout:
                lougOut();
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case 0:
                    lougOut();
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        getUserInfo();
        super.onResume();
    }

    /**
     * 获取用户信息
     */
    private void getUserInfo() {
        User user = (User) SPUtils.getBean(getContext(), "User");
        if(user!=null){
            personalNameTextView.setText(user.getName());
            personalIdTextView.setText(user.getUid());
            personalCollegeTextView.setText(user.getCollege());
            if(user.getAvatarUrl()==null){
                avatarImageView.setImageResource(R.drawable.default_avatar);
            }else{
                ImageLoader.getInstance().loadImage(MyConstant.BASE_URL+user.getAvatarUrl(),avatarImageView,true);
            }
        }else{
            personalNameTextView.setText(EMClient.getInstance().getCurrentUser());
            personalIdTextView.setText("获取失败");
            personalCollegeTextView.setText("获取失败");
            avatarImageView.setImageResource(R.drawable.default_avatar);
        }
    }

    /**
     * 退出登录
     */
    private void lougOut() {
        EMClient.getInstance().logout(false, new EMCallBack() {
            @Override
            public void onSuccess() {
                getActivity().finish();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
            @Override
            public void onProgress(int progress, String status) {
            }
            @Override
            public void onError(int code, String error) {
            }
        });
    }
}
