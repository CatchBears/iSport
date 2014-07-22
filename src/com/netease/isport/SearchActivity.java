package com.netease.isport;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.netease.util.GetIntentInstance;
import com.netease.util.MyAdapter;
import com.netease.util.NetWorkUtil;
import com.netease.util.PostandGetConnectionUtil;
import com.netease.util.RoundImageUtil;
import com.netease.util.ToastUtil;

public class SearchActivity extends Activity implements OnClickListener{
   // Button button=null;
	private Bitmap mDefaultBit;
    private TextView date,time;
    private AutoCompleteTextView address;
    private ImageView imageView;
    private Button button;
    private Spinner spinner;
    private String class_act;
    private ArrayAdapter<String> adapter;
    private ArrayList<ListItem> mItemArray = new ArrayList<ListItem>();
    
    /*   ��������       */
    SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    //����һ��TextView�ؼ�����
    TextView dateAndTimeLabel,timeLabel = null;
    //��ȡһ����������
    Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);
    //�����DatePickerDialog�ؼ������ð�ťʱ�����ø÷���
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                int dayOfMonth) {
            //�޸������ؼ����꣬�£���
            //�����year,monthOfYear,dayOfMonth��ֵ��DatePickerDialog�ؼ����õ�����ֵһ��
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);   
            //��ҳ��TextView����ʾ����Ϊ����ʱ��
            updateLabel();           
        }       
    };
    
    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        
        //ͬDatePickerDialog�ؼ�
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            updateLabelTime();
            
        }
    };
    
    
    
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_search);
		spinner = (Spinner) findViewById(R.id.spinner1);   //�����
		button=(Button) findViewById(R.id.search);         //������
		date=(TextView)findViewById(R.id.date);
		time=(TextView)findViewById(R.id.time);
		address=(AutoCompleteTextView)findViewById(R.id.address);
		imageView=(ImageView) findViewById(R.id.title_bar_menu_btn);
		
		date.setOnClickListener(this);
		time.setOnClickListener(this);
		address.setOnClickListener(this);
		
		String[] mItems = getResources().getStringArray(R.array.planets_arry);
		MyAdapter _Adapter=new MyAdapter(this,mItems);
		spinner.setAdapter(_Adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
	    		@Override
	    		public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
	    			 class_act=parent.getItemAtPosition(position).toString();
	    	    	}
	    		@Override
	    		public void onNothingSelected(AdapterView<?> parent) {
	    			// TODO Auto-generated method stub
	    	    	}
	  });
		
		button.setOnClickListener(this);
		imageView.setOnClickListener(this);
		
        dateAndTimeLabel=(TextView)findViewById(R.id.date);
      //  timeLabel=(TextView)findViewById(R.id.time);
        updateLabel();
        updateLabelTime();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stubb
		Intent intent=GetIntentInstance.getIntent();
		switch(v.getId()) {
			case R.id.search :{
				//����ҵ���߼�
			/*	switch (v.getId()){
				  case R.id.cat_basketball:{
					  class_act="����";break;
				  }
				  case R.id.cat_football:{
					  class_act="����";break;
				  }
				  case R.id.cat_pingpang:{
					  class_act="ƹ����";break;
				  }
				  case R.id.cat_badminton:{
					  class_act="��ë��";break;
				  }
				  case R.id.cat_running:{
					  class_act="�ܲ�";break;
				  }
				}*/
				//class_act=
				search();
				break;
			}
			case R.id.title_bar_menu_btn : {
				SearchActivity.this.finish();
				break;
			}
			case R.id.date:{
                new DatePickerDialog(SearchActivity.this,
                        d,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)).show();               
			}
			case R.id.time:{time.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                new TimePickerDialog(SearchActivity.this,
	                        t,
	                        dateAndTime.get(Calendar.HOUR_OF_DAY),
	                        dateAndTime.get(Calendar.MINUTE),
	                        true).show();
	            }
	        });
			  break;
			}
		}
	}
	
	private void updateLabel() {
		//format.format(dateAndTime.getTime());
        dateAndTimeLabel.setText(format.format(dateAndTime.getTime()));
        }
	private void updateLabelTime(){
		//timeFormat.format(dateAndTime.getTime());
		time.setText(timeFormat.format(dateAndTime.getTime()));
	}
	private void search(){
		if( !NetWorkUtil.isNetworkConnected(this.getApplicationContext()) ) {
			ToastUtil.show(getApplicationContext(), "������񲻿��ã���������״̬��");
			return;
		}
		HttpResponse httpResponse=null;
		 String date_act=date.getText().toString();
		 String time_act=time.getText().toString();
		// String address_act=address.getText().toString();
		// List<NameValuePair> list=new ArrayList<NameValuePair>();
		 date_act=date_act.replace('/', '_');
		 /*list.add(new BasicNameValuePair("date_act",date_act));
	     list.add(new BasicNameValuePair("time_act",time_act));
		 list.add(new BasicNameValuePair("class_act",class_act));*/
		 Intent intent=GetIntentInstance.getIntent();
		 intent.putExtra("date_act", date_act);
		 intent.putExtra("time_act", time_act);
		 intent.putExtra("class_act", class_act);
		 intent.putExtra("flag", 1);  //1 ����׼ȷ����
		 intent.setClass(SearchActivity.this, ResultListActivity.class);
		 startActivity(intent);
		// list.add(new BasicNameValuePair("address_act", address_act));
		/*try {
			httpResponse = PostandGetConnectionUtil.getConnect(PostandGetConnectionUtil.searchUrl,list);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (PostandGetConnectionUtil.responseCode(httpResponse) != 200)
			return;
			String message = PostandGetConnectionUtil.GetResponseMessage(httpResponse);            
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            String json_str = PostandGetConnectionUtil.GetResponseMessage(httpResponse);
            if(json_str.length() != 0) {
    			JsonPushRet o = new DecodeJson().jsonPush(json_str);
    			mItemArray.clear();
            if(o.getRet().equals("ok")) {

				int count = o.getCount();
				for(int i = 0; i < count; i++) {
					String theme = "���⣺" + o.getList().get(i).getTheme();
					String details = "���ģ�" + o.getList().get(i).getDetails();
					String time = "ʱ�䣺" + o.getList().get(i).getTime();
					String cnt = "������"+ o.getList().get(i).getCount();
					String name = o.getList().get(i).getName();
					String img = o.getList().get(i).getImg();
					String id  = o.getList().get(i).getId();
					Bitmap bitmap = mDefaultBit;
					String image_location = PostandGetConnectionUtil.mediaUrlBase + img;
					// get the image from the url
					try{
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						URL url_image = new URL(image_location);  
						InputStream is = url_image.openStream();
						bitmap = RoundImageUtil.toRoundCorner(BitmapFactory.decodeStream(is));
						is.close();
					} catch(Exception e) {
			            e.printStackTrace();  
			        }
					mItemArray.add(new ListItem(name, theme, time, cnt, details, id, bitmap));
				}
			
            } 
           }*/
		} 
	
}

