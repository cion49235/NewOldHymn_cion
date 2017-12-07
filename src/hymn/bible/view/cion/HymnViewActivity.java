package hymn.bible.view.cion;

import java.io.InputStream;

import com.admixer.AdAdapter;
import com.admixer.AdInfo;
import com.admixer.AdMixerManager;
import com.admixer.AdView;
import com.admixer.AdViewListener;
import com.admixer.InterstitialAd;
import com.admixer.InterstitialAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import hymn.bible.view.cion.fragment.DBOpenHelper_Fragment1;
import hymn.bible.view.cion.fragment.DBOpenHelper_Fragment2;
import hymn.bible.view.cion.util.NetworkHelper;
import hymn.bible.view.cion.util.PreferenceUtil;
import hymn.bible.view.cion.util.SimpleCrypto;
import hymn.bible.view.cion.util.TimeUtil;
import hymn.bible.view.cion.util.Utils;
import uk.co.senab.photoview.PhotoViewAttacher;
public class HymnViewActivity extends Activity implements AdViewListener, OnClickListener, InterstitialAdListener{
	public static ImageView img_hymn;
	public static Context context;
	public DownloadImageAsync downloadImageAsync = null;
	public static LinearLayout layout_img_biblesong, layout_progress, layout_nodata;
	public static int id;
	public static String title;
	public static int description;
	public static RelativeLayout ad_layout;
	public static Handler navigator_handler = new Handler();
	public static MediaPlayer mediaPlayer;
	public int seekBackwardTime = 5000; // 5000 milliseconds
	public int seekForwardtime = 5000; // 5000 milliseconds
	public int duration_check = 0;
	public static boolean CALL_STATE_OFFHOOK = false;
	public static boolean CALL_STATE_RINGING = false;
	public static TextView txt_hymn_title, current_time;
	public static ImageButton bt_pause;
	public static RelativeLayout hymn_control_panel_layout;
	public MediaPlayAsync mediaPlayAsync = null;
	public Handler handler = new Handler();
	private com.admixer.InterstitialAd interstialAd;
//	private final NetworkHelper mNetHelper = NetworkHelper.getInstance();
	private ImageView bt_hymn_continue, bt_hymn_background;
	private boolean action_background = false;
	public static boolean hymn_continue = false;
	private NativeExpressAdView admobNative;
	public static boolean ad_view = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hymn_view);
		ad_view = true;
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMIXER, "3n6wqou5");
    	AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMOB, "ca-app-pub-4637651494513698/6897809762");
    	AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMOB_FULL, "ca-app-pub-4637651494513698/8374542962");
		context = this;
		addBannerView();
//		init_admob_naive();
		init_ui();
		telephony_manager();
		get_data();
		play_start();
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
		ad_view = false;
//		admobNative.destroy();
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		action_background = false;
		if(downloadImageAsync != null){
			downloadImageAsync.cancel(true);
		}
		if(mediaPlayAsync != null){
			mediaPlayAsync.cancel(true);
		}
		navigator_handler.removeCallbacks(UpdateTimetask);
		if(mediaPlayer.isPlaying()){
         	mediaPlayer.stop();
		}
		Log.i("dsu","onDestroy===>");
		finish();
	}
	
	@Override
	protected void onUserLeaveHint() {
		// TODO Auto-generated method stub
		super.onUserLeaveHint();
		ad_view = true;
		Log.i("dsu","onUserLeaveHint===>");
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		try{
			updateProgressBar();
	    	if(!mediaPlayer.isPlaying()){
	    		if(duration_check > 0){
	    			mediaPlayer.seekTo(duration_check);
	    			mediaPlayer.start();
	    		}
	    		return;
	    	}
	    }catch (IllegalStateException localIllegalStateException){
	    }
	    catch (IllegalArgumentException localIllegalArgumentException){
	    }
	    catch (NullPointerException localNullPointerException){
	    }
	}
	
	private void get_data(){
		id = getIntent().getIntExtra("id", id);
		title = getIntent().getStringExtra("title");
		description = getIntent().getIntExtra("description", description);
	}
	
	private void telephony_manager(){
		TelephonyManager telephonymanager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephonymanager.listen(new PhoneStateListener() {
			public void onCallStateChanged(int state, String incomingNumber) {
				switch (state) {
				case TelephonyManager.CALL_STATE_IDLE: 
//					if ((duration_check > 0) && (mediaPlayer != null) && (!mediaPlayer.isPlaying())){
//						if(duration_check > 0){
//							mediaPlayer.seekTo(duration_check);						
//							mediaPlayer.start();
//						}
//					}
				case TelephonyManager.CALL_STATE_OFFHOOK:
					if ((mediaPlayer != null) && (mediaPlayer.isPlaying())){
						mediaPlayer.pause();
						duration_check = mediaPlayer.getCurrentPosition();
					}
				case TelephonyManager.CALL_STATE_RINGING:
					if ((mediaPlayer != null) && (mediaPlayer.isPlaying())){
						mediaPlayer.pause();
						duration_check = mediaPlayer.getCurrentPosition();
					}
				default: break;
				} 
			}
		}, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	private void init_ui(){
		mediaPlayer = new MediaPlayer();
		hymn_control_panel_layout = (RelativeLayout)findViewById(R.id.hymn_control_panel_layout);
		layout_img_biblesong = (LinearLayout)findViewById(R.id.layout_img_biblesong);
		layout_nodata = (LinearLayout)findViewById(R.id.layout_nodata);
		layout_progress = (LinearLayout)findViewById(R.id.layout_progress);
		img_hymn = (ImageView)findViewById(R.id.img_hymn);
		bt_pause = (ImageButton)findViewById(R.id.bt_pause);
		bt_pause.setOnClickListener(this);
		txt_hymn_title = (TextView)findViewById(R.id.txt_hymn_title);
		current_time = (TextView)findViewById(R.id.current_time);
		bt_hymn_continue = (ImageView)findViewById(R.id.bt_hymn_continue);
		if(PreferenceUtil.getBooleanSharedData(context, PreferenceUtil.PREF_HYMN_CONTINUE, hymn_continue) == true){
			bt_hymn_continue.setSelected(true);
			bt_hymn_continue.setImageResource(R.drawable.ic_action_repeat);
		}else{
			bt_hymn_continue.setSelected(false);
			bt_hymn_continue.setImageResource(R.drawable.ic_action_repeat_cancel);
		}
		bt_hymn_continue.setOnClickListener(this);
		bt_hymn_background = (ImageView)findViewById(R.id.bt_hymn_background);
		bt_hymn_background.setOnClickListener(this);
	}
	
	public class DownloadImageAsync extends AsyncTask<String, Void, Bitmap> {
	    private ImageView img_hymn;
	    private String url;
	    private PhotoViewAttacher mAttacher;
	    public DownloadImageAsync(ImageView img_hymn, String url) {
	        this.img_hymn = img_hymn;
	        this.url = url;
	    }
	    
	    @Override
	    protected void onPreExecute() {
	    	super.onPreExecute();
	    	layout_progress.setVisibility(View.VISIBLE);
	    }
	    @Override
	    protected Bitmap doInBackground(String... params) {
	    	Bitmap bimap = null;
	    	try {
	    		InputStream in = new java.net.URL(url).openStream();
	    		BitmapFactory.Options options = new BitmapFactory.Options();
	    		options.inPreferredConfig = Config.RGB_565;
	    		bimap = BitmapFactory.decodeStream(in, null, options);
	    	} catch (Exception e) {
	    	}
	    	return bimap;
	    }
	    
	   @Override
	   protected void onPostExecute(Bitmap Response) {
		   super.onPostExecute(Response);
		   if(downloadImageAsync != null){
				downloadImageAsync.cancel(true);
			}
		   layout_progress.setVisibility(View.GONE);
		   try{
	    		if(Response != null){
	    			Toast.makeText(context, context.getString(R.string.txt_hymn_toast), Toast.LENGTH_SHORT).show();
	    			img_hymn.setImageBitmap(Response);
	    			 mAttacher = new PhotoViewAttacher(img_hymn);
//	    			 mAttacher.setScaleType(ScaleType.FIT_XY); 
	    			layout_nodata.setVisibility(View.GONE);
	    		}else{
	    			layout_nodata.setVisibility(View.VISIBLE);
	    		}
	    	}catch(NullPointerException e){
	    	}
	   }
	}
	
	public class MediaPlayAsync extends AsyncTask<String, Long, Integer> implements OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener,android.widget.SeekBar.OnSeekBarChangeListener, OnErrorListener {
		public int result = -1;
		public MediaPlayAsync() {
		}
		@Override
        protected void onPreExecute() {
			try{
				txt_hymn_title.setText(context.getString(R.string.txt_hymn_ready));
	            navigator_handler.removeCallbacks(UpdateTimetask);
	            if(mediaPlayer.isPlaying()){
	            	mediaPlayer.stop();
	            }
			}catch(Exception e) {
			}
            super.onPreExecute();
            
		}

		@Override
		protected Integer doInBackground(String... params) {
			try{
				mediaPlayer.setOnBufferingUpdateListener(this);
				mediaPlayer.setOnCompletionListener(this);
				mediaPlayer.setOnErrorListener(this);
				mediaPlayer.setOnPreparedListener(this);
				
				mediaPlayer.reset();
	            mediaPlayer.setDataSource(params[0]);
	            mediaPlayer.prepare();
	            
				mediaPlayer.seekTo(0);
				updateProgressBar();
				return result = 1;
			}catch (Exception e) {
			}
			return result;
		}
		
		@Override
        protected void onProgressUpdate(Long... values) {
        	super.onProgressUpdate(values);
        }
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if(result == 1){
				if(mediaPlayAsync != null){
					mediaPlayAsync.cancel(true);
				}
				mediaPlayer.start();
				handler.postDelayed(new Runnable() {
					 @Override
					 public void run() {
						 if(PreferenceUtil.getBooleanSharedData(context, PreferenceUtil.PREF_HYMN_CONTINUE, hymn_continue) == true){
							 return;
						 }
						 if(mediaPlayer.isPlaying() == true){
							 mediaPlayer.pause();
//							 Toast.makeText(context, context.getString(R.string.txt_hymn_ready2), Toast.LENGTH_LONG).show();
						 }
					 }
				 },300);
			}else{
				mediaPlayAsync = new MediaPlayAsync();
				mediaPlayAsync.execute();
			}
		}

		@Override
		public void onPrepared(MediaPlayer mp) {
        	txt_hymn_title.setText(title);
		}
        
		@Override
		public void onBufferingUpdate(MediaPlayer mediaPlayer, int buffering) {
		}
		@Override
		public void onCompletion(MediaPlayer mp) {
			if(mediaPlayer != null && mediaPlayer.isPlaying() ){
				mediaPlayer.seekTo(0);
				mediaPlayer.stop();
			}
			if(PreferenceUtil.getBooleanSharedData(context, PreferenceUtil.PREF_HYMN_CONTINUE, hymn_continue) == true){
				action_background = false;
				addInterstitialView();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						play_next();
					}
				},1000);
			}
		}
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			navigator_handler.removeCallbacks(UpdateTimetask);
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			navigator_handler.removeCallbacks(UpdateTimetask);
			int totalDuration = mediaPlayer.getDuration();
			int currentPosition = TimeUtil.progressToTimer(seekBar.getProgress(), totalDuration);
			// forward or backward to certain seconds
			mediaPlayer.seekTo(currentPosition);
			if (mediaPlayer.isPlaying()){
		    // update timer progress again
		      updateProgressBar();
		      return;
		    }
		}

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			layout_progress.setVisibility(View.VISIBLE);
			navigator_handler.removeCallbacks(UpdateTimetask);
//			finish();
			return false;
		}
	}
	
	private void play_start(){
		/*if(!mNetHelper.is3GConnected() && !mNetHelper.isWIFIConneced()){
			Toast.makeText(context, context.getString(R.string.download_data_connection_ment), Toast.LENGTH_LONG).show();
			return;
		}*/
		hymn_control_panel_layout.setVisibility(View.VISIBLE);
		if(description == 1){
			try{
				String hymnsong_old = SimpleCrypto.decrypt(Utils.get_data, context.getString(R.string.txt_hymnsong_url_old));
				String hymnlyrics_old = SimpleCrypto.decrypt(Utils.get_data, context.getString(R.string.txt_hymnlyrics_url_old));
				hymn_control_panel_layout.setVisibility(View.VISIBLE);
				downloadImageAsync = new DownloadImageAsync(img_hymn, hymnlyrics_old + Integer.toString(id) + ".gif");
				downloadImageAsync.execute();
				mediaPlayAsync = new MediaPlayAsync();
				mediaPlayAsync.execute(hymnsong_old + Integer.toString(id) + context.getString(R.string.txt_hymn_type));
			}catch(Exception e){
			}
			}else if(description == 2){
				try{
					String hymnsong_new = SimpleCrypto.decrypt(Utils.get_data, context.getString(R.string.txt_hymnsong_url_new));
					String hymnlyrics_new = SimpleCrypto.decrypt(Utils.get_data, context.getString(R.string.txt_hymnlyrics_url_new));
					hymn_control_panel_layout.setVisibility(View.VISIBLE);
					downloadImageAsync = new DownloadImageAsync(img_hymn, hymnlyrics_new + Integer.toString(id) + ".JPG");
					downloadImageAsync.execute();
					mediaPlayAsync = new MediaPlayAsync();
					mediaPlayAsync.execute(hymnsong_new + Integer.toString(id) + context.getString(R.string.txt_hymn_type));
				}catch(Exception e){
				}
			}
		}
	
	private void play_next(){
		/*if(!mNetHelper.is3GConnected() && !mNetHelper.isWIFIConneced()){
			Toast.makeText(context, context.getString(R.string.download_data_connection_ment), Toast.LENGTH_LONG).show();
			return;
		}*/
		int current_id = id;
		int next_id = current_id+1;
		if(description == 1){
			if(current_id > DBOpenHelper_Fragment1.title.length-1){
				Toast.makeText(context, R.string.txt_play_next_cancel, Toast.LENGTH_SHORT).show();
				return;
			}
			try{
				String hymnsong_old = SimpleCrypto.decrypt(Utils.get_data, context.getString(R.string.txt_hymnsong_url_old));
				String hymnlyrics_old = SimpleCrypto.decrypt(Utils.get_data, context.getString(R.string.txt_hymnlyrics_url_old));
				hymn_control_panel_layout.setVisibility(View.VISIBLE);
				downloadImageAsync = new DownloadImageAsync(img_hymn, hymnlyrics_old + Integer.toString(next_id) + ".gif");
				downloadImageAsync.execute();
				title = DBOpenHelper_Fragment1.title[next_id-1];
				mediaPlayAsync = new MediaPlayAsync();
				mediaPlayAsync.execute(hymnsong_old + Integer.toString(next_id) + context.getString(R.string.txt_hymn_type));
			}catch(Exception e){
			}
			}else if(description == 2){
				if(current_id > DBOpenHelper_Fragment2.title.length-1){
					Toast.makeText(context, R.string.txt_play_next_cancel, Toast.LENGTH_SHORT).show();
					return;
				}
				try{
					String hymnsong_new = SimpleCrypto.decrypt(Utils.get_data, context.getString(R.string.txt_hymnsong_url_new));
					String hymnlyrics_new = SimpleCrypto.decrypt(Utils.get_data, context.getString(R.string.txt_hymnlyrics_url_new));
					hymn_control_panel_layout.setVisibility(View.VISIBLE);
					downloadImageAsync = new DownloadImageAsync(img_hymn, hymnlyrics_new + Integer.toString(next_id) + ".JPG");
					downloadImageAsync.execute();
					title = DBOpenHelper_Fragment2.title[next_id-1];
					mediaPlayAsync = new MediaPlayAsync();
					mediaPlayAsync.execute(hymnsong_new + Integer.toString(next_id) + context.getString(R.string.txt_hymn_type));
				}catch(Exception e){
				}
			}
			id = next_id;
		}

	public static void updateProgressBar(){
		navigator_handler.postDelayed(UpdateTimetask, 100);
	}
	
	public static Runnable UpdateTimetask = new Runnable() {
		@Override
		public void run() {
			if(mediaPlayer != null){
				if(mediaPlayer.isPlaying()){
					txt_hymn_title.setText(title);
					bt_pause.setImageResource(R.drawable.ic_action_pause);
				}else{
					txt_hymn_title.setText(title);
					bt_pause.setImageResource(R.drawable.ic_action_play);
				}
				long currentDuration = mediaPlayer.getCurrentPosition();
				// Displaying Total Duration time
				current_time.setText(""+TimeUtil.milliSecondsToTimer(currentDuration));
				// Updating progress bar
				navigator_handler.postDelayed(this, 100);	
			}
		}
	};
	
	@Override
	public void onClick(View view) {
		if(view == bt_pause){
			if(mediaPlayer.isPlaying()){
				mediaPlayer.pause();
				bt_pause.setImageResource(R.drawable.ic_action_play);
			}else{
				mediaPlayer.start();
				bt_pause.setImageResource(R.drawable.ic_action_pause);
			}
		}else if(view == bt_hymn_continue){
			if(PreferenceUtil.getBooleanSharedData(context, PreferenceUtil.PREF_HYMN_CONTINUE, hymn_continue) == true){
				Toast.makeText(context, context.getString(R.string.txt_hymn_continue_false), Toast.LENGTH_LONG).show();
				bt_hymn_continue.setSelected(false);
				bt_hymn_continue.setImageResource(R.drawable.ic_action_repeat_cancel);
				hymn_continue = false;
				PreferenceUtil.setBooleanSharedData(context, PreferenceUtil.PREF_HYMN_CONTINUE, hymn_continue);
			}else{
				Toast.makeText(context, context.getString(R.string.txt_hymn_continue_true), Toast.LENGTH_LONG).show();
				bt_hymn_continue.setSelected(true);
				bt_hymn_continue.setImageResource(R.drawable.ic_action_repeat);
				hymn_continue = true;
				PreferenceUtil.setBooleanSharedData(context, PreferenceUtil.PREF_HYMN_CONTINUE, hymn_continue);
			}
		}else if(view == bt_hymn_background){
			if(mediaPlayer != null && mediaPlayer.isPlaying() ){
				action_background = true;
				Toast.makeText(context, context.getString(R.string.txt_background_play), Toast.LENGTH_LONG).show();
				addInterstitialView();
			}
		}else{
			return;
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
    
    public void addInterstitialView() {
    	if(interstialAd == null) {
        	AdInfo adInfo = new AdInfo("3n6wqou5");
//        	adInfo.setTestMode(false);
        	interstialAd = new com.admixer.InterstitialAd(this);
        	interstialAd.setAdInfo(adInfo, this);
        	interstialAd.setInterstitialAdListener(this);
        	interstialAd.startInterstitial();
    	}
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
	
	private void mediaplayer_stop(){
		if(mediaPlayer.isPlaying()){
         	mediaPlayer.stop();
		}
	}
	
	private void home_action(){
		if(mediaPlayer.isPlaying()){
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
//			NotificationUtil.setNotification_hymn(context);
		}
	}
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
			Toast.makeText(context, context.getString(R.string.txt_after_ad), Toast.LENGTH_LONG).show();
			mediaplayer_stop();
			addInterstitialView();
			 handler.postDelayed(new Runnable() {
				 @Override
				 public void run() {
					 mediaplayer_stop();
					 onDestroy();
				 }
			 },2000);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onInterstitialAdClosed(InterstitialAd arg0) {
		interstialAd = null;
		if(action_background == true){
			home_action();
		}
	}

	@Override
	public void onInterstitialAdFailedToReceive(int arg0, String arg1, InterstitialAd arg2) {
		interstialAd = null;
		if(action_background == true){
			home_action();
		}
	}

	@Override
	public void onInterstitialAdReceived(String arg0, InterstitialAd arg1) {
		interstialAd = null;
	}

	@Override
	public void onInterstitialAdShown(String arg0, InterstitialAd arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLeftClicked(String arg0, InterstitialAd arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRightClicked(String arg0, InterstitialAd arg1) {
		// TODO Auto-generated method stub
		
	}
}
		
