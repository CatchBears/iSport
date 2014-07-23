package com.netease.isport;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class SplashActivity extends Activity{
    boolean isFirstIn = false;
    // Frame����
    private AnimationDrawable animation;
    private static final int GO_HOME = 1000;
    private static final int GO_GUIDE = 1001;
    // �ӳ�3��
    private static final long SPLASH_DELAY_MILLIS = 2500;
    private ImageView imgTween1,imgTween2,imgTween3; 
    private AnimationSet as,ns,js; 
    private Intent intent;
    private View view;
    private static final String SHAREDPREFERENCES_NAME = "first_pref";

    /**
     * Handler:��ת����ͬ����
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case GO_HOME:
                goHome();
                break;
            case GO_GUIDE:
                goGuide();
                break;
            }
            super.handleMessage(msg);
        }
    };

    /** ��ʼ�� */  
    public void init(int a,int b) { 
        // ����AnimationSet  
    	intent=new Intent();
        float kuan=(float)a;
        float gao=(float)b;
    	as = new AnimationSet(true); 
    	ns=new AnimationSet(true);
    	js=new AnimationSet(true);
        AlphaAnimation aa = alphaAnim(0.5f, 1);
        TranslateAnimation ra = translateAnim(0, 0, -gao/2, gao/59); 
        TranslateAnimation ba = translateAnim(-kuan/2, 0f, 0f, 0f);
        TranslateAnimation ta = translateAnim(kuan/2, 0f, 0f, 0f);
        ns.addAnimation(ta);
        // ��Ӹ��ֶ���  
        as.addAnimation(ba); 
        js.addAnimation(ra);
        js.addAnimation(aa);
        imgTween1 = (ImageView) findViewById(R.id.imgTween1); 
        imgTween2=(ImageView) findViewById(R.id.imgTween2); 
        imgTween3=(ImageView)findViewById(R.id.imgTween3);
        imgTween2.startAnimation(ns); 
        imgTween1.startAnimation(as);
        imgTween3.startAnimation(js);
		
	}
   
	

    /** ͸���� */ 
    private AlphaAnimation alphaAnim(float x, float y) { 
        AlphaAnimation aa = new AlphaAnimation(x, y); 
        aa.setDuration(2000); 
        aa.setRepeatMode(Animation.REVERSE); 
        aa.setRepeatCount(0); 
        return aa; 
    } 
 
    /** �ƶ� */ 
    private TranslateAnimation translateAnim(float startX, float endX, 
            float startY, float endY) { 
        TranslateAnimation ta = new TranslateAnimation(startX, endX, startY, 
                endY); 
        ta.setDuration(2000); 
        ta.setRepeatMode(Animation.RESTART); 
        ta.setRepeatCount(0); 
        return ta; 
    } 
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash); 
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // ��Ļ��ȣ����أ�
        int height = metric.heightPixels;   // ��Ļ�߶ȣ����أ�

        init(width,height); 
       
        init();
        
    }

    private void init() {
        // ��ȡSharedPreferences����Ҫ������
        // ʹ��SharedPreferences����¼�����ʹ�ô���
        SharedPreferences preferences = getSharedPreferences(
                SHAREDPREFERENCES_NAME, MODE_PRIVATE);

        // ȡ����Ӧ��ֵ�����û�и�ֵ��˵����δд�룬��true��ΪĬ��ֵ
        //isFirstIn = preferences.getBoolean("isFirstIn", true);
        isFirstIn=true;
        // �жϳ�����ڼ������У�����ǵ�һ����������ת���������棬������ת��������
        if (!isFirstIn) {
            // ʹ��Handler��postDelayed������3���ִ����ת��MainActivity
            mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
        } else {
            mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
        }

    }

    private void goHome() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }

    private void goGuide() {
        Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }
}