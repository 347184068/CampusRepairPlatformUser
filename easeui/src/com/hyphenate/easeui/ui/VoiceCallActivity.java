/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hyphenate.easeui.ui;

import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.R;
import com.hyphenate.exceptions.EMNoActiveCallException;
import com.hyphenate.exceptions.EMServiceNotReadyException;

import java.util.UUID;


/**
 * 语音通话页面
 * 
 */
public class VoiceCallActivity extends CallActivity implements OnClickListener {
	private LinearLayout comingBtnContainer;
	private Button hangupBtn;
	private Button refuseBtn;
	private Button answerBtn;
	private ImageView muteImage;
	private ImageView handsFreeImage;

	private boolean isMuteState;
	private boolean isHandsfreeState;
	
	private TextView callStateTextView;
	private int streamID;
	private boolean endCallTriggerByMe = false;
	private Handler handler = new Handler();
	private TextView nickTextView;
	private TextView durationTextView;
	private Chronometer chronometer;
	String st1;
	private boolean isAnswered;
	private LinearLayout voiceContronlLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
        	finish();
        	return;
        }
		setContentView(R.layout.activity_voice_call);
		
		//HXSDKHelper.getInstance().isVoiceCalling = true;

		comingBtnContainer = (LinearLayout) findViewById(R.id.ll_coming_call);
		refuseBtn = (Button) findViewById(R.id.btn_refuse_call);
		answerBtn = (Button) findViewById(R.id.btn_answer_call);
		hangupBtn = (Button) findViewById(R.id.btn_hangup_call);
		muteImage = (ImageView) findViewById(R.id.iv_mute);
		handsFreeImage = (ImageView) findViewById(R.id.iv_handsfree);
		callStateTextView = (TextView) findViewById(R.id.tv_call_state);
		nickTextView = (TextView) findViewById(R.id.tv_nick);
		durationTextView = (TextView) findViewById(R.id.tv_calling_duration);
		chronometer = (Chronometer) findViewById(R.id.chronometer);
		voiceContronlLayout = (LinearLayout) findViewById(R.id.ll_voice_control);

		refuseBtn.setOnClickListener(this);
		answerBtn.setOnClickListener(this);
		hangupBtn.setOnClickListener(this);
		muteImage.setOnClickListener(this);
		handsFreeImage.setOnClickListener(this);

		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
						| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		// 注册语音电话的状态的监听
		addCallStateListener();
		msgid = UUID.randomUUID().toString();

		username = getIntent().getStringExtra("username");
		// 语音电话是否为接收的
		isInComingCall = getIntent().getBooleanExtra("isComingCall", false);

		// 设置通话人
		nickTextView.setText(username);
		if (!isInComingCall) {// 拨打电话
			soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
			outgoing = soundPool.load(this, R.raw.outgoing, 1);

			comingBtnContainer.setVisibility(View.INVISIBLE);
			hangupBtn.setVisibility(View.VISIBLE);
			st1 = getResources().getString(R.string.are_connected_to_each_other);
			callStateTextView.setText(st1);
			handler.postDelayed(new Runnable() {
				public void run() {
					streamID = playMakeCallSounds();
				}
			}, 300);
			try {
				// 拨打语音电话
				EMClient.getInstance().callManager().makeVoiceCall(username);
			} catch (EMServiceNotReadyException e) {
				e.printStackTrace();
				final String st2 = getResources().getString(R.string.is_not_yet_connected_to_the_server);
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(VoiceCallActivity.this, st2, Toast.LENGTH_LONG).show();
					}
				});
			}
		} else { // 有电话进来
			voiceContronlLayout.setVisibility(View.INVISIBLE);
			Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			audioManager.setMode(AudioManager.MODE_RINGTONE);
			audioManager.setSpeakerphoneOn(true);
			ringtone = RingtoneManager.getRingtone(this, ringUri);
			ringtone.play();
		}
	}

	/**
	 * 设置电话监听
	 */
	void addCallStateListener() {
	    callStateListener = new EMCallStateChangeListener() {
            
            @Override
            public void onCallStateChanged(CallState callState, CallError error) {
                // Message msg = handler.obtainMessage();
                switch (callState) {
                
                case CONNECTING: // 正在连接对方
                    runOnUiThread(new Runnable() {
                        
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            callStateTextView.setText(st1);
                        }

                    });
                    break;
                case CONNECTED: // 双方已经建立连接
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            String st3 = getResources().getString(R.string.have_connected_with);
                            callStateTextView.setText(st3);
                        }

                    });
                    break;

                case ACCEPTED: // 电话接通成功
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                if (soundPool != null)
                                    soundPool.stop(streamID);
                            } catch (Exception e) {
                            }
                            if(!isHandsfreeState)
                                closeSpeakerOn();
                            //显示是否为直连，方便测试
                            ((TextView)findViewById(R.id.tv_is_p2p)).setText(EMClient.getInstance().callManager().isDirectCall()
                                    ? R.string.direct_call : R.string.relay_call);
                            chronometer.setVisibility(View.VISIBLE);
                            chronometer.setBase(SystemClock.elapsedRealtime());
                            // 开始记时
                            chronometer.start();
                            String str4 = getResources().getString(R.string.in_the_call);
                            callStateTextView.setText(str4);
                            callingState = CallingState.NORMAL;
                        }

                    });
                    break;
                case DISCONNNECTED: // 电话断了
                    final CallError fError = error;
                    runOnUiThread(new Runnable() {
                        private void postDelayedCloseMsg() {
                            handler.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    saveCallRecord(0);
                                    Animation animation = new AlphaAnimation(1.0f, 0.0f);
                                    animation.setDuration(800);
                                    findViewById(R.id.root_layout).startAnimation(animation);
                                    finish();
                                }

                            }, 200);
                        }

                        @Override
                        public void run() {
                            chronometer.stop();
                            callDruationText = chronometer.getText().toString();
                            String st2 = getResources().getString(R.string.the_other_party_refused_to_accept);
                            String st3 = getResources().getString(R.string.connection_failure);
                            String st4 = getResources().getString(R.string.the_other_party_is_not_online);
                            String st5 = getResources().getString(R.string.the_other_is_on_the_phone_please);
                            
                            String st6 = getResources().getString(R.string.the_other_party_did_not_answer);
                            String st7 = getResources().getString(R.string.hang_up);
                            String st8 = getResources().getString(R.string.the_other_is_hang_up);
                            
                            String st9 = getResources().getString(R.string.did_not_answer);
                            String st10 = getResources().getString(R.string.has_been_cancelled);
                            String st11 = getResources().getString(R.string.hang_up);
                            
                            if (fError == CallError.REJECTED) {
                                callingState = CallingState.BEREFUESD;
                                callStateTextView.setText(st2);
                            } else if (fError == CallError.ERROR_TRANSPORT) {
                                callStateTextView.setText(st3);
                            } else if (fError == CallError.ERROR_INAVAILABLE) {
                                callingState = CallingState.OFFLINE;
                                callStateTextView.setText(st4);
                            } else if (fError == CallError.ERROR_BUSY) {
                                callingState = CallingState.BUSY;
                                callStateTextView.setText(st5);
                            } else if (fError == CallError.ERROR_NORESPONSE) {
                                callingState = CallingState.NORESPONSE;
                                callStateTextView.setText(st6);
                            } else {
                                if (isAnswered) {
                                    callingState = CallingState.NORMAL;
                                    if (endCallTriggerByMe) {
//                                        callStateTextView.setText(st7);
                                    } else {
                                        callStateTextView.setText(st8);
                                    }
                                } else {
                                    if (isInComingCall) {
                                        callingState = CallingState.UNANSWERED;
                                        callStateTextView.setText(st9);
                                    } else {
                                        if (callingState != CallingState.NORMAL) {
                                            callingState = CallingState.CANCED;
                                            callStateTextView.setText(st10);
                                        }else {
                                            callStateTextView.setText(st11);
                                        }
                                    }
                                }
                            }
                            postDelayedCloseMsg();
                        }

                    });

                    break;

                default:
                    break;
                }

            }
        };
		EMClient.getInstance().callManager().addCallStateChangeListener(callStateListener);
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.btn_refuse_call) {
			refuseBtn.setEnabled(false);
			if (ringtone != null)
				ringtone.stop();
			try {
				EMClient.getInstance().callManager().rejectCall();
			} catch (Exception e1) {
				e1.printStackTrace();
				saveCallRecord(0);
				finish();
			}
			callingState = CallingState.REFUESD;

		} else if (i == R.id.btn_answer_call) {
			answerBtn.setEnabled(false);
			if (ringtone != null)
				ringtone.stop();
			if (isInComingCall) {
				try {
					callStateTextView.setText("正在接听...");
					EMClient.getInstance().callManager().answerCall();
					isAnswered = true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					saveCallRecord(0);
					finish();
					return;
				}
			}
			comingBtnContainer.setVisibility(View.INVISIBLE);
			hangupBtn.setVisibility(View.VISIBLE);
			voiceContronlLayout.setVisibility(View.VISIBLE);
			closeSpeakerOn();

		} else if (i == R.id.btn_hangup_call) {
			hangupBtn.setEnabled(false);
			if (soundPool != null)
				soundPool.stop(streamID);
			chronometer.stop();
			endCallTriggerByMe = true;
			callStateTextView.setText(getResources().getString(R.string.hanging_up));
			try {
				EMClient.getInstance().callManager().endCall();
			} catch (Exception e) {
				e.printStackTrace();
				saveCallRecord(0);
				finish();
			}

		} else if (i == R.id.iv_mute) {
			if (isMuteState) {
				// 关闭静音
				muteImage.setImageResource(R.drawable.icon_mute_normal);
				audioManager.setMicrophoneMute(false);
				isMuteState = false;
			} else {
				// 打开静音
				muteImage.setImageResource(R.drawable.icon_mute_on);
				audioManager.setMicrophoneMute(true);
				isMuteState = true;
			}

		} else if (i == R.id.iv_handsfree) {
			if (isHandsfreeState) {
				// 关闭免提
				handsFreeImage.setImageResource(R.drawable.icon_speaker_normal);
				closeSpeakerOn();
				isHandsfreeState = false;
			} else {
				handsFreeImage.setImageResource(R.drawable.icon_speaker_on);
				openSpeakerOn();
				isHandsfreeState = true;
			}

		} else {
		}
	}

	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//HXSDKHelper.getInstance().isVoiceCalling = false;
	}

	@Override
	public void onBackPressed() {
		try {
			EMClient.getInstance().callManager().endCall();
		} catch (EMNoActiveCallException e) {
			e.printStackTrace();
		}
		callDruationText = chronometer.getText().toString();
		saveCallRecord(0);
		finish();
	}

	

	

	
}
