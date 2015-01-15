package com.soniq.tvmarket.model;

import com.soniq.tvmarket.data.WAPI;
import com.soniq.tvmarket.model.ClientUpgrade.ClientUpgradeCallback;

import android.content.Context;
import android.os.AsyncTask;

public class Feedback {

	public static final int SUCCESS = 0;
	private Context context;
	public Feedback(Context cxt){
		this.context = cxt;
	}
	private FeedbackCallback feedcallback;
	private void doCallback(int state)
	{
		if( feedcallback != null )
			feedcallback.onFinished(state);
	}
	
	public interface FeedbackCallback
	{
		public void onFinished(int state);
	}
	
	public void submit(FeedbackCallback callback,String urlName)
	{
		feedcallback = callback;
		
		FeedbackAsyncTask ft = new FeedbackAsyncTask();
		ft.execute(urlName, null, null);
	}	
	
	private class FeedbackAsyncTask extends AsyncTask<String,Integer,Integer>{

		
		
		@Override
		protected Integer doInBackground(String... params) {
			
			// TODO Auto-generated method stub
			String content = WAPI.http_get_content(params[0]);
			if( content == null || content.length() < 1)
				return 1;
			int iret = WAPI.parseFeedbackJSONResponse(context, content);
			return iret;
			
		}
		
		@Override
		protected void onPostExecute(Integer result)
		{
			doCallback(result);
		}
		
	}
	
	
}
