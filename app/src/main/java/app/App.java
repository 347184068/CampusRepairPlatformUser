package app;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.receiver.CallReceiver;

/**
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-09-18
 * Time: 11:31
 */
public class App extends Application{
    public static Context applicationContext;
    private CallReceiver callReceiver;
    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        EaseUI.getInstance().init(this, null);
        IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
        if(callReceiver == null){
            callReceiver = new CallReceiver();
        }
        //注册通话广播接收者
        this.registerReceiver(callReceiver, callFilter);
    }

}
