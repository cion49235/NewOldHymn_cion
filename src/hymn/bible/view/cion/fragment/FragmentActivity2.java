package hymn.bible.view.cion.fragment;



import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import hymn.bible.view.cion.HymnViewActivity;
import hymn.bible.view.cion.R;
import hymn.bible.view.cion.dao.DBOpenHelper_Fragment2;
import hymn.bible.view.cion.dao.Fragment_Data2;
import hymn.bible.view.cion.dao.Fragment_Data2;
import hymn.bible.view.cion.util.KoreanTextMatch;
import hymn.bible.view.cion.util.KoreanTextMatcher;

public class FragmentActivity2 extends Fragment implements OnClickListener, OnItemClickListener, OnScrollListener{
	private EditText edit_searcher;
	private DBOpenHelper_Fragment2 mydb;
	private SQLiteDatabase mdb;
	private Cursor cursor;
	private ArrayList<Fragment_Data2> list;
	private FragmentAdapter adapter;
	private ListView listview;
	private LinearLayout layout_nodata;
	private String searchKeyword;
	private ImageButton btn_close;
	private KoreanTextMatch match1, match2;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_2,container, false);
		init_ui(view);
		display_list();
		seacher_start();
		listview.setOnScrollListener(this);
		listview.setOnItemClickListener(this);
		btn_close.setOnClickListener(this);
		return view;
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		edit_searcher.setText("");
	}
	
	private void seacher_start(){
		edit_searcher.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable arg0) {
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				try {
					searchKeyword = s.toString();
					display_list();
					if(adapter != null){
						adapter.notifyDataSetChanged();
					}
					if(s.length() == 0){
						btn_close.setVisibility(View.INVISIBLE);
					}else{
						btn_close.setVisibility(View.VISIBLE);
					}
				} catch (Exception e) {
				}
			}
		});
	}
	
	private void display_list(){
		list = new ArrayList<Fragment_Data2>();
		mydb = new DBOpenHelper_Fragment2(getActivity());
		mdb = mydb.getReadableDatabase();
		cursor = mdb.rawQuery("select * from twentyone_hymn", null);
		while(cursor.moveToNext()){
			if(searchKeyword != null && "".equals(searchKeyword.trim()) == false){
				KoreanTextMatcher matcher1 = new KoreanTextMatcher(searchKeyword.toLowerCase());
				KoreanTextMatcher matcher2 = new KoreanTextMatcher(searchKeyword.toUpperCase());
				match1 = matcher1.match(cursor.getString(cursor.getColumnIndex("title")).toLowerCase());
				match2 = matcher2.match(cursor.getString(cursor.getColumnIndex("title")).toUpperCase());
				if(match1.success()){
					list.add(new Fragment_Data2(cursor.getInt(cursor.getColumnIndex("_id")), cursor.getString(cursor.getColumnIndex("title")), cursor.getInt(cursor.getColumnIndex("description"))));
				}else if (match2.success()){
					list.add(new Fragment_Data2(cursor.getInt(cursor.getColumnIndex("_id")), cursor.getString(cursor.getColumnIndex("title")), cursor.getInt(cursor.getColumnIndex("description"))));
				}
			}else{
				list.add(new Fragment_Data2(cursor.getInt(cursor.getColumnIndex("_id")), cursor.getString(cursor.getColumnIndex("title")), cursor.getInt(cursor.getColumnIndex("description"))));
			}
		}
		cursor.close();
		
		adapter = new FragmentAdapter();
		listview.setAdapter(adapter);
		if(listview.getCount() > 0){
			layout_nodata.setVisibility(View.GONE);
		}else{
			layout_nodata.setVisibility(View.VISIBLE);
		}
	}
	
	private void init_ui(View view){
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		layout_nodata = (LinearLayout)view.findViewById(R.id.layout_nodata);
		edit_searcher = (EditText)view.findViewById(R.id.edit_searcher);
		btn_close = (ImageButton)view.findViewById(R.id.btn_close);
		listview = (ListView)view.findViewById(R.id.listview);
	}
	
	public class FragmentAdapter extends BaseAdapter{
		public FragmentAdapter() {
		}
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			try{
				if(view == null){	
					LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
					view = layoutInflater.inflate(R.layout.fragment_listrow, parent, false);
				}
				ImageView img_more = (ImageView)view.findViewById(R.id.img_more);
				img_more.setFocusable(false);
				img_more.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						int _id = list.get(position)._id;
						int description = list.get(position).description;
						String title = list.get(position).title;
						
						Intent intent = new Intent(getActivity(), HymnViewActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra("id", _id);
						intent.putExtra("title", title);
						intent.putExtra("description", description);
						startActivity(intent);
					}
				});
				TextView txt_subject = (TextView)view.findViewById(R.id.txt_name);
//				txt_subject.setText(list.get(position).title);
				setTextViewColorPartial(txt_subject, list.get(position).title, searchKeyword, Color.RED);
				
			}catch (Exception e) {
			}
			return view;
		}
		
		private void setTextViewColorPartial(TextView view, String fulltext, String subtext, int color) {
			try{
				view.setText(fulltext, TextView.BufferType.SPANNABLE);
				Spannable str = (Spannable) view.getText();
				int i = fulltext.indexOf(subtext);
				str.setSpan(new ForegroundColorSpan(color), i, i + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}catch (IndexOutOfBoundsException e) {
			}
		}
	}

	@Override
	public void onClick(View v) {
		if(v == btn_close){
			edit_searcher.setText("");
			display_list();
			if(adapter != null){
				adapter.notifyDataSetChanged();
			}
			InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);  
    		inputMethodManager.hideSoftInputFromWindow(edit_searcher.getWindowToken(), 0);
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Fragment_Data2 data = (Fragment_Data2)adapter.getItem(position);
		int _id = data._id;
		int description = data.description;
		String title = data.title;
		
		Intent intent = new Intent(getActivity(), HymnViewActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("id", _id);
		intent.putExtra("title", title);
		intent.putExtra("description", description);
		startActivity(intent);
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if(scrollState == OnScrollListener.SCROLL_STATE_FLING){
			InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);  
    		inputMethodManager.hideSoftInputFromWindow(edit_searcher.getWindowToken(), 0);
    		listview.setFastScrollEnabled(true);
		}else{
			listview.setFastScrollEnabled(false);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		
	}
}
