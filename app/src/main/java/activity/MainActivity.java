package activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.hyphenate.easeui.ui.EaseConversationListFragment.EaseConversationListItemClickListener;
import com.hyphenate.util.NetUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bean.Repaireman;
import bean.User;
import utils.AlertUtilts;
import utils.GsonUtils;
import utils.HttpUtils;
import utils.LogUtil;
import utils.MyConstant;
import utils.SPUtils;
import wfu.com.campusrepairplatformUser.R;

/**
 * Created by Lenovo on 2016/7/21.
 */
public class MainActivity extends EaseBaseActivity {

    private final static String TAG = "MainActivity";

    private TextView unreadLabel;
    private Button[] mTabs;
    private EaseConversationListFragment conversationListFragment;
    private SettingsFragment settingFragment;
    private RepairRequireFragment repairFragment;
    private EaseContactListFragment contactListFragment;
    private Fragment[] fragments;
    private int index;
    private int currentTabIndex;
    private ArrayList<Repaireman> repairemans;

    private EaseUI easeUI;
    private Map<String, EaseUser> allList = new HashMap<String, EaseUser>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x123:
                    Bundle bundle = msg.getData();
                    String jsonContact = bundle.getString("online_contact");
                    LogUtil.e(TAG, jsonContact);
                    if (!"404".equals(jsonContact) && jsonContact != "") {
                        repairemans = GsonUtils.toList(jsonContact);
                        contactListFragment.setContactsMap(getContacts());
                        setUserProvider();
                        conversationListFragment.refresh();
                    }
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();

    }


    private void initEvent() {
        //注册一个监听连接状态的listener
        EMClient.getInstance().addConnectionListener(new MyConnectionListener());
        //注册一个设置提供器
        EaseUI.getInstance().setSettingsProvider(new MySettingProvider());
    }



    private class MySettingProvider implements EaseUI.EaseSettingsProvider{

        @Override
        public boolean isMsgNotifyAllowed(EMMessage message) {
            return true;
        }

        @Override
        public boolean isMsgSoundAllowed(EMMessage message) {
            return true;
        }

        @Override
        public boolean isMsgVibrateAllowed(EMMessage message) {
            return false;
        }

        @Override
        public boolean isSpeakerOpened() {
            return false;
        }
    }

    private void setUserProvider() {
        easeUI = EaseUI.getInstance();
        easeUI.setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {
            @Override
            public EaseUser getUser(String username) {
                return getUserInfo(username);
            }
        });
    }

    private EaseUser getUserInfo(String username) {
        allList = getContacts();
        User user = (User) SPUtils.getBean(getApplicationContext(), "User");
        EaseUser currentUser = new EaseUser(user.getUid());
        currentUser.setAvatar(MyConstant.BASE_URL + user.getAvatarUrl());
        currentUser.setNick(user.getName());
        allList.put(user.getUid(), currentUser);
        EaseUser easeUser = allList.get(username);
        LogUtil.e(TAG, easeUser.toString());
        return easeUser;
    }

    private void initView() {
        unreadLabel = (TextView) findViewById(R.id.unread_msg_number);
        mTabs = new Button[4];
        mTabs[0] = (Button) findViewById(R.id.btn_conversation);
        mTabs[1] = (Button) findViewById(R.id.btn_address_list);
        mTabs[2] = (Button) findViewById(R.id.btn_repaire);
        mTabs[3] = (Button) findViewById(R.id.btn_setting);
        mTabs[0].setSelected(true);
        conversationListFragment = new EaseConversationListFragment();
        settingFragment = new SettingsFragment();
        contactListFragment = new EaseContactListFragment();
        repairFragment = new RepairRequireFragment();
        //给contactlist设置数据

        conversationListFragment.setConversationListItemClickListener(new EaseConversationListItemClickListener() {
            @Override
            public void onListItemClicked(EMConversation conversation) {
                startActivity(new Intent(MainActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, conversation.getUserName()));
            }
        });
        contactListFragment.setContactListItemClickListener(new EaseContactListFragment.EaseContactListItemClickListener() {

            @Override
            public void onListItemClicked(EaseUser user) {
                startActivity(new Intent(MainActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername()));
            }
        });
        fragments = new Fragment[]{conversationListFragment, contactListFragment, repairFragment, settingFragment};
        // add and show first fragment
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, conversationListFragment)
                .add(R.id.fragment_container, contactListFragment).hide(contactListFragment).show(conversationListFragment)
                .commit();
    }

    private Map<String, EaseUser> getContacts() {
        Map<String, EaseUser> contacts = new HashMap<String, EaseUser>();
        for (int i = 0; i < repairemans.size(); i++) {
            EaseUser user = new EaseUser(repairemans.get(i).getId());
            user.setAvatar(MyConstant.BASE_URL + repairemans.get(i).getAvatarUrl());
            user.setNick(repairemans.get(i).getNickName());
            contacts.put(repairemans.get(i).getId(), user);
        }
        return contacts;
    }

    /**
     * 获取在线维修员
     */
    private void getContactsFromNet() {
        try {
            HttpUtils.doPostAsyn(MyConstant.BASE_URL + MyConstant.GET_CONTACT, "", new HttpUtils.CallBack() {
                @Override
                public void onRequestComplete(String result) {
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("online_contact", result);
                    msg.setData(bundle);
                    msg.what = 0x123;
                    handler.sendMessage(msg);
                }
            });
        } catch (Exception e) {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("online_contact", "404");
            msg.setData(bundle);
            msg.what = 0x123;
            handler.sendMessage(msg);
            e.printStackTrace();
        }
    }

    /**
     * onTabClicked
     *
     * @param view
     */
    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_conversation:
                index = 0;
                break;
            case R.id.btn_address_list:
                index = 1;
                getContactsFromNet();
                break;
            case R.id.btn_repaire:
                index = 2;
                break;
            case R.id.btn_setting:
                index = 3;
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        mTabs[currentTabIndex].setSelected(false);
        // set current tab as selected.
        mTabs[index].setSelected(true);
        currentTabIndex = index;
    }

    //实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
        }

        @Override
        public void onDisconnected(final int error) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                        showRemoveDialog();
                    } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        // 显示帐号在其他设备登录
                        showConflictDialog();
                    } else {
                        if (NetUtils.hasNetwork(MainActivity.this)) {
                            //连接不到聊天服务器
                        } else {

                        }
                    }
                }
            });
        }
    }

    /**
     * 显示冲突提示
     */
    private void showConflictDialog() {
        if (!MainActivity.this.isFinishing()) {
            AlertUtilts.showDialog(MainActivity.this, "下线通知！", "同一帐号在其他设备登录", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EMClient.getInstance().logout(false, null);
                    dialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });


        }
    }

    /**
     * 显示账号移除提示
     */
    private void showRemoveDialog() {
        if (!MainActivity.this.isFinishing()) {
            AlertUtilts.showDialog(MainActivity.this, "通知！", "帐号已经被移除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EMClient.getInstance().logout(false, null);
                    dialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }


}
