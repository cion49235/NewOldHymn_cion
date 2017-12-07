package hymn.bible.view.cion;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.admixer.AdAdapter;
import com.admixer.AdInfo;
import com.admixer.AdMixerManager;
import com.admixer.AdView;
import com.admixer.AdViewListener;
import com.admixer.CustomPopup;
import com.admixer.CustomPopupListener;
import com.admixer.InterstitialAd;
import com.admixer.InterstitialAdListener;
import com.admixer.PopupInterstitialAdOption;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import hymn.bible.view.cion.fragment.FragmentActivity1;
import hymn.bible.view.cion.fragment.FragmentActivity2;
import kr.co.inno.autocash.service.AutoServiceActivity;

public class MainFragmentActivity extends SherlockFragmentActivity implements AdViewListener, CustomPopupListener, InterstitialAdListener{
	private ActionBar actionbar;
	private ViewPager viewpager;
	private Tab tab;
	public static Context context;
	private Handler handler = new Handler();
	private boolean flag;
	private NativeExpressAdView admobNative;
	private InterstitialAd interstialAd;
	public boolean retry_alert = false;
	private AdView adView;
	public static RelativeLayout ad_layout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		setContentView(R.layout.fragment_main);
		context = this;
		retry_alert = true;
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMIXER, "3n6wqou5");
    	AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMOB, "ca-app-pub-4637651494513698/6897809762");
    	AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMOB_FULL, "ca-app-pub-4637651494513698/8374542962");
    	
    	addBannerView();
		
		actionbar = getSupportActionBar();
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowTitleEnabled(false);
		viewpager = (ViewPager)findViewById(R.id.pager);
		
		FragmentManager fm = getSupportFragmentManager();
		ViewPager.SimpleOnPageChangeListener ViewPagerListener = new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				actionbar.setSelectedNavigationItem(position);
			}
		};
		viewpager.setOnPageChangeListener(ViewPagerListener);
		viewpager.setOnPageChangeListener(ViewPagerListener);
		TabContentAdapter adapter = new TabContentAdapter(fm);
		viewpager.setAdapter(adapter);
		
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				viewpager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
			}
		};
		
		tab = actionbar.newTab().setText(context.getString(R.string.tab_menu_1)).setTabListener(tabListener);
		actionbar.addTab(tab);

		tab = actionbar.newTab().setText(context.getString(R.string.tab_menu_2)).setTabListener(tabListener);
		actionbar.addTab(tab);
		CustomPopup.setCustomPopupListener(this);
        CustomPopup.startCustomPopup(this, "3n6wqou5");
//		init_admob_naive();
		exit_handler();
		auto_service();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
//		admobNative.pause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
//		admobNative.resume();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		retry_alert = false;
		CustomPopup.stopCustomPopup();
//		admobNative.destroy();
	}
	
	private void auto_service() {
        Intent intent = new Intent(context, AutoServiceActivity.class);
        context.stopService(intent);
        context.startService(intent);
    }
	
	public void addBannerView() {
    	AdInfo adInfo = new AdInfo("3n6wqou5");
    	adInfo.setTestMode(false);
        AdView adView = new AdView(this);
        adView.setAdInfo(adInfo, this);
        adView.setAdViewListener(this);
        ad_layout = (RelativeLayout)findViewById(R.id.ad_layout);
        if(ad_layout != null){
        	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            ad_layout.addView(adView, params);	
        }
    }
	
	public class TabContentAdapter extends FragmentPagerAdapter {
		private int PAGE_COUNT = 2;

		public TabContentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				FragmentActivity1 fa1 = new FragmentActivity1();
				return fa1;
			case 1:
				FragmentActivity2 fa2 = new FragmentActivity2();
				return fa2;
				
			}
			return null;
		}

		@Override
		public int getCount() {
			return PAGE_COUNT;
		}
	}
	
	private void init_admob_naive(){
		RelativeLayout nativeContainer = (RelativeLayout) findViewById(R.id.admob_native);
		AdRequest adRequest = new AdRequest.Builder().build();	    
		admobNative = new NativeExpressAdView(this);
		admobNative.setAdSize(new AdSize(360, 100));
		admobNative.setAdUnitId("ca-app-pub-4637651494513698/9851276161");
		nativeContainer.addView(admobNative);
		admobNative.loadAd(adRequest);
	}
	
	private void exit_handler(){
    	handler = new Handler(){
    		@Override
    		public void handleMessage(Message msg) {
    			if(msg.what == 0){
    				flag = false;
    			}
    		}
    	};
    }
	
	public void addInterstitialView() {
		AdInfo adInfo = new AdInfo("3n6wqou5");
		adInfo.setInterstitialTimeout(0); 
		adInfo.setUseRTBGPSInfo(false);
		adInfo.setMaxRetryCountInSlot(-1);
		adInfo.setBackgroundAlpha(true); 

		PopupInterstitialAdOption adConfig = new PopupInterstitialAdOption();

		adConfig.setDisableBackKey(true);

		adConfig.setButtonLeft(context.getString(R.string.txt_finish_no), "#234234");

		adConfig.setButtonRight(context.getString(R.string.txt_finish_yes), "#234234");

		adConfig.setButtonFrameColor(null);

		adInfo.setInterstitialAdType(AdInfo.InterstitialAdType.Popup, adConfig);
		
		interstialAd = new InterstitialAd(this);
		interstialAd.setAdInfo(adInfo, this);
		interstialAd.setInterstitialAdListener(this);
		interstialAd.startInterstitial();
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			addInterstitialView();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//** CustomPopup �̺�Ʈ�� *************
	@Override
	public void onCloseCustomPopup(String arg0) {
	
	}

	@Override
	public void onHasNoCustomPopup() {
	
	}

	@Override
	public void onShowCustomPopup(String arg0) {
	
	}

	@Override
	public void onStartedCustomPopup() {
	
	}

	@Override
	public void onWillCloseCustomPopup(String arg0) {
	
	}

	@Override
	public void onWillShowCustomPopup(String arg0) {
	
	}
	
	@Override
	public void onInterstitialAdClosed(InterstitialAd arg0) {
		interstialAd = null;
	}

	@Override
	public void onInterstitialAdFailedToReceive(int arg0, String arg1,InterstitialAd arg2) {
		interstialAd = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(context.getString(R.string.app_name));
		builder.setMessage(context.getString(R.string.txt_finish_ment));
		builder.setInverseBackgroundForced(true);
		builder.setNeutralButton(context.getString(R.string.txt_finish_yes), new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
				finish();
			}
		});
		builder.setNegativeButton(context.getString(R.string.txt_finish_no), new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
             	 dialog.dismiss();
			}
		});
		AlertDialog myAlertDialog = builder.create();
		if(retry_alert) myAlertDialog.show();
	}

	@Override
	public void onInterstitialAdReceived(String arg0, InterstitialAd arg1) {
		interstialAd = null;
	}

	@Override
	public void onInterstitialAdShown(String arg0, InterstitialAd arg1) {
	}

	@Override
	public void onLeftClicked(String arg0, InterstitialAd arg1) {
	}

	@Override
	public void onRightClicked(String arg0, InterstitialAd arg1) {
		finish();
	}
	
	@Override
	public void onClickedAd(String arg0, AdView arg1) {
	}

	@Override
	public void onFailedToReceiveAd(int arg0, String arg1, AdView arg2) {
		
	}
	@Override
	public void onReceivedAd(String arg0, AdView arg1) {
	}
}
