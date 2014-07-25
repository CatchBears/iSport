package com.netease.isport;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.util.GetIntentInstance;
import com.netease.util.MD5util;
import com.netease.util.NetWorkUtil;
import com.netease.util.PostandGetConnectionUtil;
import com.netease.util.SharedPreferenceUtil;
import com.netease.util.ToastUtil;

public class LoginActivity extends Activity implements OnClickListener{
	TextView textView,register=null;
	ImageView preStep=null;
	private ProgressDialog pd;
	static final private int regOk = 5;
	
	Intent intent = GetIntentInstance.getIntent();
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_login);
		textView=(TextView)findViewById(R.id.login);
		preStep=(ImageView)findViewById(R.id.title_bar_menu_btn);
		register=(TextView)findViewById(R.id.register);
		
		preStep.setOnClickListener(this);
		textView.setOnClickListener(this);
		register.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		  case R.id.login :{ 
				login();  break;
		  }
		  case R.id.title_bar_menu_btn :{  //return
			  LoginActivity.this.finish();
			  break;
		  }
		  
		  case R.id.register :{
			  intent.setClass(LoginActivity.this, RegisterActivity.class);
			  //startActivity(intent);
			  startActivityForResult(intent, regOk);
			  break;
		  }
		}
	}
	
	Handler handler = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {// handler���յ���Ϣ��ͻ�ִ�д˷���  
            pd.dismiss();// �ر�ProgressDialog
            String out = (String)msg.obj;
            if(out.equals("ok")) {
            	ToastUtil.show(getApplicationContext(), "��¼�ɹ���");
            	setResult(RESULT_OK, intent);
            	LoginActivity.this.finish();
            } else if (out.equals("not_exist")) {
            	ToastUtil.show(getApplicationContext(), "�ʺŲ����ڣ������µ�¼");	
            } else if (out.equals("password_error")) {
            	ToastUtil.show(getApplicationContext(), "���벻��ȷ������������");
            } else {
            	ToastUtil.show(getApplicationContext(), "��¼ʧ���˰���");
            }
        }  
    }; 
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(regOk == requestCode && RESULT_OK == resultCode) {
    		setResult(RESULT_OK, intent);
        	LoginActivity.this.finish();
    	}
    }
    
	public void login(){
		if( !NetWorkUtil.isNetworkConnected(this.getApplicationContext()) ) {
			ToastUtil.show(getApplicationContext(), "������񲻿��ã���������״̬��");
			return;
		}
		
		pd = ProgressDialog.show(LoginActivity.this, "��¼��", "�����У����Ժ󡭡�");
		new Thread(new Runnable() {  
            @Override  
            public void run() {
            	AutoCompleteTextView user_name=(AutoCompleteTextView)findViewById(R.id.username);
        		AutoCompleteTextView user_password=(AutoCompleteTextView)findViewById(R.id.password);
        		String username=user_name.getText().toString();
        		String password=user_password.getText().toString();
            	HttpResponse httpResponse=null;
            	List<NameValuePair> list=new ArrayList<NameValuePair>();
        	    String passwordMd5 = MD5util.encryptToMD5(password);
        		list.add(new BasicNameValuePair("username",username));
        		list.add(new BasicNameValuePair("password",passwordMd5));
        		if(username.length() == 0 || password.length() == 0) {
        			ToastUtil.show(getApplicationContext(), "�������û���������");
        			return;
        		}
        		PostandGetConnectionUtil.setParm(list);
        		try {
        			httpResponse = PostandGetConnectionUtil.postConnect(PostandGetConnectionUtil.loginUrl);
        		} catch (URISyntaxException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		if(httpResponse != null && PostandGetConnectionUtil.responseCode(httpResponse) == 200){
        			String message = PostandGetConnectionUtil.GetResponseMessage(httpResponse);            
                    //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    JsonRet o = new DecodeJson().jsonRet(message);
                    if(o.getRet().equals("ok")) {
                    	SharedPreferenceUtil.setLogin(true);
                    	String o1 = "ok";
                    	Message msg = new Message();
                    	msg.obj = o1;
                    	handler.sendMessage(msg);
                    }
                    else if(o.getRet().equals("not_exist")){
                    	String o1 = "not_exist";
                    	Message msg = new Message();
                    	msg.obj = o1;
                    	handler.sendMessage(msg);
                    	//ToastUtil.show(getApplicationContext(), "�ʺŲ����ڣ������µ�¼");	
                    }
                    else if(o.getRet().equals("password_error")){
                    	String o1 = "password_error";
                    	Message msg = new Message();
                    	msg.obj = o1;
                    	handler.sendMessage(msg);
                    	//ToastUtil.show(getApplicationContext(), "���벻��ȷ������������");
                    }
                    else {
                    	String o1 = "error";
                    	Message msg = new Message();
                    	msg.obj = o1;
                    	handler.sendMessage(msg);
                    	//ToastUtil.show(getApplicationContext(), "��¼ʧ���˰���");
                    }
        		} else {
        			String o1 = "error";
                	Message msg = new Message();
                	msg.obj = o1;
                	handler.sendMessage(msg);
        			//ToastUtil.show(getApplicationContext(), "������������⣬��Ҳ��֪����ô��Ŷ��");
        		}
                //handler.sendEmptyMessage(0);// ִ�к�ʱ�ķ���֮��������handler  
            }  

        }).start();
		
	}
}
