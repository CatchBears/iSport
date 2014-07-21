package com.netease.isport;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.netease.util.ToastUtil;


/**
 * AMapV2��ͼ�м򵥽���poisearch����
 */
public class PoiKeywordSearchActivity extends FragmentActivity implements
		OnMarkerClickListener, InfoWindowAdapter, TextWatcher,
		OnPoiSearchListener{
	private AMap aMap;
	private AutoCompleteTextView searchText;// ���������ؼ���
	private String keyWord = "";// Ҫ�����poi�����ؼ���
	private ProgressDialog progDialog = null;// ����ʱ������
	private String editCity;// Ҫ����ĳ������ֻ��߳�������
	private PoiResult poiResult; // poi���صĽ��
	private int currentPage = 0;// ��ǰҳ�棬��0��ʼ����
	private PoiSearch.Query query;// Poi��ѯ������
	private PoiSearch poiSearch;// POI����

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poikeywordsearch_activity);
		
		Bundle bundle = getIntent().getExtras();
		keyWord=bundle.getString("position");
		//Toast.makeText(getApplicationContext(), "���" ,Toast.LENGTH_LONG).show();
		//Toast.makeText(getApplicationContext(), keyWord ,Toast.LENGTH_LONG).show();
		editCity="����";
        /*
         * �������ߵ�ͼ�洢Ŀ¼�����������ߵ�ͼ���ʼ����ͼ����;
         * ʹ�ù����п���������, ���������������ߵ�ͼ�洢��·����
         * ����Ҫ�����ߵ�ͼ���غ�ʹ�õ�ͼҳ�涼����·������
         * */
	    //Demo��Ϊ�������������ʹ�����ص����ߵ�ͼ��ʹ��Ĭ��λ�ô洢���������Զ�������
//        MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
		init();
	}

	/**
	 * ��ʼ��AMap����
	 */
	private void init() {
		if (aMap == null) {
			aMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			setUpMap();
		}
	}

	/**
	 * ����ҳ�����
	 */
	private void setUpMap() {
		//Button searButton = (Button) findViewById(R.id.searchButton);
		//searButton.setOnClickListener(this);
		//Button nextButton = (Button) findViewById(R.id.nextButton);
		//nextButton.setOnClickListener(this);
		//searchText = (AutoCompleteTextView) findViewById(R.id.keyWord);
		//searchText.addTextChangedListener(this);// ����ı����������¼�
		//editCity = (EditText) findViewById(R.id.city);
		aMap.setOnMarkerClickListener(this);// ��ӵ��marker�����¼�
		aMap.setInfoWindowAdapter(this);// �����ʾinfowindow�����¼�
		//Toast.makeText(getApplicationContext(), "Test",Toast.LENGTH_LONG).show();
		doSearchQuery();
	}

	/**
	 * ���������ť
	 */
	public void searchButton() {
		//keyWord = AMapUtil.checkEditText(searchText);
		if ("".equals(keyWord)) {
			ToastUtil.show(PoiKeywordSearchActivity.this, "�����������ؼ���");
			return;
		} else {
			doSearchQuery();
		}
	}

	/**
	 * �����һҳ��ť
	 */
	public void nextButton() {
		if (query != null && poiSearch != null && poiResult != null) {
			if (poiResult.getPageCount() - 1 > currentPage) {
				currentPage++;
				query.setPageNum(currentPage);// ���ò��һҳ
				poiSearch.searchPOIAsyn();
			} else {
				ToastUtil.show(PoiKeywordSearchActivity.this,
						R.string.no_result);
			}
		}
	}

	/**
	 * ��ʾ���ȿ�
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(false);
		progDialog.setMessage("��������:\n" + keyWord);
		progDialog.show();
	}

	/**
	 * ���ؽ��ȿ�
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	/**
	 * ��ʼ����poi����
	 */
	protected void doSearchQuery() {
	//	Toast.makeText(getApplicationContext(), "test",Toast.LENGTH_LONG).show();
		showProgressDialog();// ��ʾ���ȿ�
		currentPage = 0;
		query = new PoiSearch.Query(keyWord, "", editCity);// ��һ��������ʾ�����ַ������ڶ���������ʾpoi�������ͣ�������������ʾpoi�������򣨿��ַ�������ȫ����
		query.setPageSize(1);// ����ÿҳ��෵�ض�����poiitem
		query.setPageNum(currentPage);// ���ò��һҳ

		poiSearch = new PoiSearch(this, query);
		poiSearch.setOnPoiSearchListener(this);
		poiSearch.searchPOIAsyn();
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		marker.showInfoWindow();
		return false;
	}

	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	@Override
	public View getInfoWindow(final Marker marker) {
		View view = getLayoutInflater().inflate(R.layout.poikeywordsearch_uri,
				null);
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setText(marker.getTitle());

		TextView snippet = (TextView) view.findViewById(R.id.snippet);
		snippet.setText(marker.getSnippet());
		ImageButton button = (ImageButton) view
				.findViewById(R.id.start_amap_app);
		// ����ߵµ�ͼapp
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startURI(marker);
			}
		});
		return view;
	}

	/**
	 * ͨ��URI��ʽ����ߵµ�ͼapp
	 */
	public void startURI(Marker marker) {
		if (getAppIn()) {
			Intent intent = new Intent(
					"android.intent.action.VIEW",
					android.net.Uri
							.parse("androidamap://viewMap?sourceApplication="
									+ getApplicationName() + "&poiname="
									+ marker.getTitle() + "&lat="
									+ marker.getPosition().latitude + "&lon="
									+ marker.getPosition().longitude + "&dev=0"));
			intent.setPackage("com.autonavi.minimap");
			startActivity(intent);
		} else {
			String url = "http://mo.amap.com/?dev=0&q="
					+ marker.getPosition().latitude + ","
					+ marker.getPosition().longitude + "&name="
					+ marker.getTitle();
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			startActivity(intent);
		}

	}

	/**
	 * �жϸߵµ�ͼapp�Ƿ��Ѿ���װ
	 */
	public boolean getAppIn() {
		PackageInfo packageInfo = null;
		try {
			packageInfo = this.getPackageManager().getPackageInfo(
					"com.autonavi.minimap", 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		// ���ֻ�û�а�װ�ߵµ�ͼapp
		if (packageInfo != null) {
			return true;
		}
		// ���ֻ��ɹ���װ�иߵµ�ͼapp
		else {
			return false;
		}
	}

	/**
	 * ��ȡ��ǰapp��Ӧ������
	 */
	public String getApplicationName() {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		try {
			packageManager = getApplicationContext().getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(
					getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			applicationInfo = null;
		}
		String applicationName = (String) packageManager
				.getApplicationLabel(applicationInfo);
		return applicationName;
	}

	/**
	 * poiû�����������ݣ�����һЩ�Ƽ����е���Ϣ
	 */
	private void showSuggestCity(List<SuggestionCity> cities) {
		String infomation = "�Ƽ�����\n";
		for (int i = 0; i < cities.size(); i++) {
			infomation += "��������:" + cities.get(i).getCityName() + "��������:"
					+ cities.get(i).getCityCode() + "���б���:"
					+ cities.get(i).getAdCode() + "\n";
		}
		ToastUtil.show(PoiKeywordSearchActivity.this, infomation);

	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String newText = s.toString().trim();
		Inputtips inputTips = new Inputtips(PoiKeywordSearchActivity.this,
				new InputtipsListener() {

					@Override
					public void onGetInputtips(List<Tip> tipList, int rCode) {
						if (rCode == 0) {// ��ȷ����
							List<String> listString = new ArrayList<String>();
							for (int i = 0; i < tipList.size(); i++) {
								listString.add(tipList.get(i).getName());
							}
							ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
									getApplicationContext(),
									R.layout.route_inputs, listString);
							searchText.setAdapter(aAdapter);
							aAdapter.notifyDataSetChanged();
						}
					}
				});
		try {
			inputTips.requestInputtips(newText, editCity.toString());// ��һ��������ʾ��ʾ�ؼ��֣��ڶ�������Ĭ�ϴ���ȫ����Ҳ����Ϊ��������

		} catch (AMapException e) {
			e.printStackTrace();
		}
	}

	/**
	 * POI�����ѯ�ص�����
	 */
	@Override
	public void onPoiItemDetailSearched(PoiItemDetail arg0, int rCode) {

	}

	/**
	 * POI��Ϣ��ѯ�ص�����
	 */
	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		dissmissProgressDialog();// ���ضԻ���
		if (rCode == 0) {
			if (result != null && result.getQuery() != null) {// ����poi�Ľ��
				if (result.getQuery().equals(query)) {// �Ƿ���ͬһ��
					poiResult = result;
					// ȡ����������poiitems�ж���ҳ
					List<PoiItem> poiItems = poiResult.getPois();// ȡ�õ�һҳ��poiitem���ݣ�ҳ��������0��ʼ
					List<SuggestionCity> suggestionCities = poiResult
							.getSearchSuggestionCitys();// ����������poiitem����ʱ���᷵�غ��������ؼ��ֵĳ�����Ϣ

					if (poiItems != null && poiItems.size() > 0) {
						aMap.clear();// ����֮ǰ��ͼ��
						PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
						poiOverlay.removeFromMap();
						poiOverlay.addToMap();
						poiOverlay.zoomToSpan();
					} else if (suggestionCities != null
							&& suggestionCities.size() > 0) {
						showSuggestCity(suggestionCities);
					} else {
						ToastUtil.show(PoiKeywordSearchActivity.this,
								R.string.no_result);
					}
				}
			} else {
				ToastUtil.show(PoiKeywordSearchActivity.this,
						R.string.no_result);
			}
		} else if (rCode == 27) {
			ToastUtil.show(PoiKeywordSearchActivity.this,
					R.string.error_network);
		} else if (rCode == 32) {
			ToastUtil.show(PoiKeywordSearchActivity.this, R.string.error_key);
		} else {
			ToastUtil.show(PoiKeywordSearchActivity.this, getString(R.string.error_other) + rCode);
		}

	}

	
}
