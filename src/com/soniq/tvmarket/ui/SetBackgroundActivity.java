package com.soniq.tvmarket.ui;


import com.soniq.tvmarket.R;
import com.soniq.tvmarket.data.MyProfile;
import com.soniq.tvmarket.ui.widget.MetroItemView;
import com.soniq.tvmarket.utils.ImageUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;

public class SetBackgroundActivity extends Activity {
	private MetroItemView set_bg1 = null;
	private MetroItemView set_bg2 = null;
	private MetroItemView set_bg3 = null;
	private MetroItemView set_bg4 = null;
	private MetroItemView set_bg5 = null;
	private MetroItemView set_bg6 = null;
	private MetroItemView set_bg7 = null;
	private MetroItemView set_bg8 = null;
	private MetroItemView set_bg9 = null;
	private MetroItemView set_bg10 = null;
	private MetroItemView set_bg11 = null;
	private MetroItemView set_bg12 = null;
	private RelativeLayout rl = null;
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_set_background);

	set_bg1 = (MetroItemView) findViewById(R.id.set_bg1);
	set_bg2 = (MetroItemView) findViewById(R.id.set_bg2);
	set_bg3 = (MetroItemView) findViewById(R.id.set_bg3);
	set_bg4 = (MetroItemView) findViewById(R.id.set_bg4);
	set_bg5 = (MetroItemView) findViewById(R.id.set_bg5);
	set_bg6 = (MetroItemView) findViewById(R.id.set_bg6);
	set_bg7 = (MetroItemView) findViewById(R.id.set_bg7);
	set_bg8 = (MetroItemView) findViewById(R.id.set_bg8);
	set_bg9 = (MetroItemView) findViewById(R.id.set_bg9);
	set_bg10 = (MetroItemView) findViewById(R.id.set_bg10);
	set_bg11= (MetroItemView) findViewById(R.id.set_bg11);
	set_bg12= (MetroItemView) findViewById(R.id.set_bg12);
	rl = (RelativeLayout) findViewById(R.id.rl);

    int i = MyProfile.get_profile_int_value(getApplicationContext(), "backgroundimg", R.drawable.bghigh1);
    if( i > 0 )
    	rl.setBackgroundDrawable(ImageUtils.loadDrawableFromResource(SetBackgroundActivity.this, i));

	switch(i){
	case R.drawable.bghigh1:
		set_bg1.requestFocus();
		break;
	case R.drawable.bghigh2:
		set_bg2.requestFocus();
		break;
	case R.drawable.bghigh3:
		set_bg3.requestFocus();
		break;
	case R.drawable.bghigh4:
		set_bg4.requestFocus();
		break;
	case R.drawable.bghigh5:
		set_bg5.requestFocus();
		break;
	case R.drawable.bghigh6:
		set_bg6.requestFocus();
		break;
	case R.drawable.bghigh7:
		set_bg7.requestFocus();
		break;
	case R.drawable.bghigh8:
		set_bg8.requestFocus();
		break;
	case R.drawable.bghigh9:
		set_bg9.requestFocus();
		break;
	case R.drawable.bghigh10:
		set_bg10.requestFocus();
		break;
	case R.drawable.bghigh11:
		set_bg11.requestFocus();
		break;
	case R.drawable.bghigh12:
		set_bg12.requestFocus();
		break;
		default:
			set_bg1.requestFocus();
			break;
		
	}
	set_bg1.setRecommendImageRes(R.drawable.bglow1);
	set_bg2.setRecommendImageRes(R.drawable.bglow2);
	set_bg3.setRecommendImageRes(R.drawable.bglow3);
	set_bg4.setRecommendImageRes(R.drawable.bglow4);
	set_bg5.setRecommendImageRes(R.drawable.bglow5);
	set_bg6.setRecommendImageRes(R.drawable.bglow6);
	set_bg7.setRecommendImageRes(R.drawable.bglow7);
	set_bg8.setRecommendImageRes(R.drawable.bglow8);
	set_bg9.setRecommendImageRes(R.drawable.bglow9);
	set_bg10.setRecommendImageRes(R.drawable.bglow10);
	set_bg11.setRecommendImageRes(R.drawable.bglow11);
	set_bg12.setRecommendImageRes(R.drawable.bglow12);
	
	set_bg1.setOnClickListener(mylistener);
	set_bg2.setOnClickListener(mylistener);
	set_bg3.setOnClickListener(mylistener);
	set_bg4.setOnClickListener(mylistener);
	set_bg5.setOnClickListener(mylistener);
	set_bg6.setOnClickListener(mylistener);
	set_bg7.setOnClickListener(mylistener);
	set_bg8.setOnClickListener(mylistener);
	set_bg9.setOnClickListener(mylistener);
	set_bg10.setOnClickListener(mylistener);
	set_bg11.setOnClickListener(mylistener);
	set_bg12.setOnClickListener(mylistener);
	
	set_bg1.setNextFocusUpId(-1);
	set_bg1.setNextFocusDownId(R.id.set_bg5);
	set_bg1.setNextFocusLeftId(-1);
	set_bg1.setNextFocusRightId(R.id.set_bg2);
	
	set_bg2.setNextFocusUpId(-1);
	set_bg2.setNextFocusDownId(R.id.set_bg6);
	set_bg2.setNextFocusLeftId(R.id.set_bg1);
	set_bg2.setNextFocusRightId(R.id.set_bg3);
	
	set_bg3.setNextFocusUpId(-1);
	set_bg3.setNextFocusDownId(R.id.set_bg7);
	set_bg3.setNextFocusLeftId(R.id.set_bg2);
	set_bg3.setNextFocusRightId(R.id.set_bg4);
	
	set_bg4.setNextFocusUpId(-1);
	set_bg4.setNextFocusDownId(R.id.set_bg8);
	set_bg4.setNextFocusLeftId(R.id.set_bg3);
	set_bg4.setNextFocusRightId(-1);
	
	set_bg5.setNextFocusUpId(R.id.set_bg1);
	set_bg5.setNextFocusDownId(R.id.set_bg9);
	set_bg5.setNextFocusLeftId(-1);
	set_bg5.setNextFocusRightId(R.id.set_bg6);
	
	set_bg6.setNextFocusUpId(R.id.set_bg2);
	set_bg6.setNextFocusDownId(R.id.set_bg10);
	set_bg6.setNextFocusLeftId(R.id.set_bg5);
	set_bg6.setNextFocusRightId(R.id.set_bg7);
	
	set_bg7.setNextFocusUpId(R.id.set_bg3);
	set_bg7.setNextFocusDownId(R.id.set_bg11);
	set_bg7.setNextFocusLeftId(R.id.set_bg6);
	set_bg7.setNextFocusRightId(R.id.set_bg8);
	
	set_bg8.setNextFocusUpId(R.id.set_bg4);
	set_bg8.setNextFocusDownId(R.id.set_bg12);
	set_bg8.setNextFocusLeftId(R.id.set_bg7);
	set_bg8.setNextFocusRightId(-1);
	
	set_bg9.setNextFocusUpId(R.id.set_bg5);
	set_bg9.setNextFocusDownId(-1);
	set_bg9.setNextFocusLeftId(-1);
	set_bg9.setNextFocusRightId(R.id.set_bg10);
	
	set_bg10.setNextFocusUpId(R.id.set_bg6);
	set_bg10.setNextFocusDownId(-1);
	set_bg10.setNextFocusLeftId(R.id.set_bg9);
	set_bg10.setNextFocusRightId(R.id.set_bg11);
	
	set_bg11.setNextFocusUpId(R.id.set_bg7);
	set_bg11.setNextFocusDownId(-1);
	set_bg11.setNextFocusLeftId(R.id.set_bg10);
	set_bg11.setNextFocusRightId(R.id.set_bg12);
	
	set_bg12.setNextFocusUpId(R.id.set_bg8);
	set_bg12.setNextFocusDownId(-1);
	set_bg12.setNextFocusLeftId(R.id.set_bg11);
	set_bg12.setNextFocusRightId(-1);
	
}

private OnClickListener mylistener = new OnClickListener() {
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.set_bg1:
			rl.setBackgroundDrawable(ImageUtils.loadDrawableFromResource(SetBackgroundActivity.this, R.drawable.bghigh1));
			MyProfile.save_profile_int_value(getApplicationContext(), "backgroundimg", R.drawable.bghigh1);
			break;
		case R.id.set_bg2:
			rl.setBackgroundDrawable(ImageUtils.loadDrawableFromResource(SetBackgroundActivity.this, R.drawable.bghigh2));
			MyProfile.save_profile_int_value(getApplicationContext(), "backgroundimg", R.drawable.bghigh2);
			break;
		case R.id.set_bg3:
			rl.setBackgroundDrawable(ImageUtils.loadDrawableFromResource(SetBackgroundActivity.this, R.drawable.bghigh3));
			MyProfile.save_profile_int_value(getApplicationContext(), "backgroundimg", R.drawable.bghigh3);
			break;
		case R.id.set_bg4:
			rl.setBackgroundDrawable(ImageUtils.loadDrawableFromResource(SetBackgroundActivity.this, R.drawable.bghigh4));
			MyProfile.save_profile_int_value(getApplicationContext(), "backgroundimg", R.drawable.bghigh4);
			break;
		case R.id.set_bg5:
			rl.setBackgroundDrawable(ImageUtils.loadDrawableFromResource(SetBackgroundActivity.this, R.drawable.bghigh5));
			MyProfile.save_profile_int_value(getApplicationContext(), "backgroundimg", R.drawable.bghigh5);
			break;
		case R.id.set_bg6:
			rl.setBackgroundDrawable(ImageUtils.loadDrawableFromResource(SetBackgroundActivity.this, R.drawable.bghigh6));
			MyProfile.save_profile_int_value(getApplicationContext(), "backgroundimg", R.drawable.bghigh6);
			break;
		case R.id.set_bg7:
			rl.setBackgroundDrawable(ImageUtils.loadDrawableFromResource(SetBackgroundActivity.this, R.drawable.bghigh7));
			MyProfile.save_profile_int_value(getApplicationContext(), "backgroundimg", R.drawable.bghigh7);
			break;
		case R.id.set_bg8:
			rl.setBackgroundDrawable(ImageUtils.loadDrawableFromResource(SetBackgroundActivity.this, R.drawable.bghigh8));
			MyProfile.save_profile_int_value(getApplicationContext(), "backgroundimg", R.drawable.bghigh8);
			break;
		case R.id.set_bg9:
			rl.setBackgroundDrawable(ImageUtils.loadDrawableFromResource(SetBackgroundActivity.this, R.drawable.bghigh9));
			MyProfile.save_profile_int_value(getApplicationContext(), "backgroundimg", R.drawable.bghigh9);
			break;
		case R.id.set_bg10:
			rl.setBackgroundDrawable(ImageUtils.loadDrawableFromResource(SetBackgroundActivity.this, R.drawable.bghigh10));
			MyProfile.save_profile_int_value(getApplicationContext(), "backgroundimg", R.drawable.bghigh10);
			break;
		case R.id.set_bg11:
			rl.setBackgroundDrawable(ImageUtils.loadDrawableFromResource(SetBackgroundActivity.this, R.drawable.bghigh11));
			MyProfile.save_profile_int_value(getApplicationContext(), "backgroundimg", R.drawable.bghigh11);
			break;
		case R.id.set_bg12:
			rl.setBackgroundDrawable(ImageUtils.loadDrawableFromResource(SetBackgroundActivity.this, R.drawable.bghigh12));
			MyProfile.save_profile_int_value(getApplicationContext(), "backgroundimg", R.drawable.bghigh12);
			break;
		}
	}
};

}
