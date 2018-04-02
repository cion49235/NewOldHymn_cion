package hymn.bible.view.cion.activity;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;
import hymn.bible.view.cion.MainFragmentActivity;
import hymn.bible.view.cion.R;
import hymn.bible.view.cion.dao.Const;
import hymn.bible.view.cion.util.PreferenceUtil;
import hymn.bible.view.cion.util.StringUtil;



public class IntroActivity extends Activity{
	public Handler handler;
	public Context context;
	public boolean retry_alert = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_intro);
        context = this;
        retry_alert = true;
        billing_process();//인앱정기결제체크
        
        handler = new Handler();
        handler.postDelayed(runnable, 2000);
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	retry_alert = false;
    	if(handler != null){
    		handler.removeCallbacks(runnable);
    	}
    	if (bp != null)
            bp.release();
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    }
    @Override
    protected void onPause() {
    	super.onPause();
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    }
    
    private BillingProcessor bp;
    private static final String SUBSCRIPTION_ID = "hymn.cion.inapp.month";
    private static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyiRNPEx3pg+PjDSe1FyLWjd0+K3D604kEzomadF1zX5JboD+7Y6kVqxTLYCiyAFsaHUIkuvtuwoyfLsiBAMDGGnZMqdGh+mEomhE7JeazdATAJ+PVtex2Nj9U3GNis89gdUKkQkevI8y63qWAKbFbI477Iv6FP2As7QskE5psx5hUb958gMrKLqYsD5ef81GzV+yfR/VnWTmzNcghMKh6ADw+kP8JGWfOk6lGAc3Gvzgra15AMdx3n4G1qkAxZo49jNb5QdcUlpngFQQ4r8nf3jhmV4vWHV7EzeqFD9/nbrugjGxMwSbtWVR9ECO1tvvgLoSHWF91ut8B4wlAQ1J7wIDAQAB";
    private void billing_process(){
        if(!BillingProcessor.isIabServiceAvailable(this)) {
        }
        bp = new BillingProcessor(this, LICENSE_KEY, new BillingProcessor.IBillingHandler() {
            @Override
            public void onBillingInitialized() {
                try{
                    bp.loadOwnedPurchasesFromGoogle();
                    Log.i("dsu", "isSubscriptionUpdateSupported : " + bp.isSubscriptionUpdateSupported());
                    Log.i("dsu", "getSubscriptionTransactionDetails : " + bp.getSubscriptionTransactionDetails(SUBSCRIPTION_ID));
                    Log.i("dsu", "isSubscribed : " + bp.isSubscribed(SUBSCRIPTION_ID));
                    Log.i("dsu", "autoRenewing : " + bp.getSubscriptionTransactionDetails(SUBSCRIPTION_ID).purchaseInfo.purchaseData.autoRenewing);
                    Log.i("dsu", "purchaseTime : " + bp.getSubscriptionTransactionDetails(SUBSCRIPTION_ID).purchaseInfo.purchaseData.purchaseTime);
                    Log.i("dsu", "purchaseState : " + bp.getSubscriptionTransactionDetails(SUBSCRIPTION_ID).purchaseInfo.purchaseData.purchaseState);
                    PreferenceUtil.setStringSharedData(context, PreferenceUtil.PREF_ISSUBSCRIBED, Boolean.toString(bp.getSubscriptionTransactionDetails(SUBSCRIPTION_ID).purchaseInfo.purchaseData.autoRenewing));
                }catch (NullPointerException e){
                }
            }
            
            @Override
            public void onPurchaseHistoryRestored() {
//            	showToast("onPurchaseHistoryRestored");
                for(String sku : bp.listOwnedProducts()){
                    Log.i("dsu", "Owned Managed Product: " + sku);
//                    showToast("Owned Managed Product: " + sku);
                }
                for(String sku : bp.listOwnedSubscriptions()){
                    Log.i("dsu", "Owned Subscription: " + sku);
//                    showToast("Owned Subscription : " + sku);
                }
            }

			@Override
			public void onProductPurchased(String arg0, TransactionDetails arg1) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onBillingError(int arg0, Throwable arg1) {

			}
        });
    }
    
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("dsu", "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            Log.i("dsu", "인앱결제 requestCode : " + requestCode);
            if (requestCode == 32459) {
                int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
                String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
                String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");//this is the signature which you want
                Log.i("dsu", "purchaseData : " +  purchaseData);
                if(TextUtils.isEmpty(purchaseData)) {
                	show_inapp_alert();
                	return;
                }
                if (resultCode == RESULT_OK) {
                    try {
                        JSONObject jo = new JSONObject(purchaseData);//this is the JSONObject which you have included in Your Question right now
                        String orderId = jo.getString("orderId");
                        String packageName = jo.getString("packageName");
                        String productId = jo.getString("productId");
                        String purchaseTime = jo.getString("purchaseTime");
                        String purchaseState = jo.getString("purchaseState");
                        String purchaseToken = jo.getString("purchaseToken");
                        String autoRenewing = jo.getString("autoRenewing");
                        String format_purchaseTime = MillToDate(Long.parseLong(purchaseTime));
                        Log.i("dsu", "구글주문아이디 " +  orderId + "\n어플리케이션 패키지이름 : " + packageName + "\n아이템 상품 식별자 : " + productId + "\n상품 구매가 이루어진 시간 : " + format_purchaseTime + "\n주문의 구매 상태 : " + purchaseState + "\n구매를 고유하게 식별하는 토큰값 : " + purchaseToken + "\n자동갱신여부 : " + autoRenewing);
                        if(!StringUtil.isEmpty(purchaseState) && autoRenewing.equals("true")) {
                        	PreferenceUtil.setStringSharedData(context, PreferenceUtil.PREF_ISSUBSCRIBED, autoRenewing);	
                        	Intent intent = new Intent(context, MainFragmentActivity.class);
            				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            				startActivity(intent);
            				finish();
                        }
                    }
                    catch (JSONException e) {
                        alert("Failed to parse purchase data.");
                        e.printStackTrace();
                    }
                }
            }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    public String MillToDate(long mills) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String date = (String) formatter.format(new Timestamp(mills));
        return date;
    }
    
    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d("dsu", "Showing alert dialog: " + message);
        bld.create().show();
    }
    
    
    private void show_inapp_alert() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(context.getString(R.string.txt_inapp_alert_title));
		builder.setMessage(context.getString(R.string.txt_inapp_alert_ment));
		builder.setInverseBackgroundForced(true);
		builder.setNeutralButton(context.getString(R.string.txt_inapp_alert_yes), new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
				bp.subscribe(IntroActivity.this,SUBSCRIPTION_ID);
			}
		});
		builder.setNegativeButton(context.getString(R.string.txt_inapp_alert_no), new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
				Intent intent = new Intent(context, MainFragmentActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
             	dialog.dismiss();
			}
		});
		AlertDialog myAlertDialog = builder.create();
		if(retry_alert) myAlertDialog.show();
    }
    
    Runnable runnable = new Runnable() {
		@Override
		public void run() {
			/*if(PreferenceUtil.getStringSharedData(context, PreferenceUtil.PREF_ISSUBSCRIBED, Const.isSubscribed).equals("true")) {
				Intent intent = new Intent(context, MainFragmentActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}else {
				show_inapp_alert();	
			}*/
			Intent intent = new Intent(context, MainFragmentActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			//fade_animation
			overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		}
	};
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(handler != null) handler.removeCallbacks(runnable);
		finish();
	}
}
