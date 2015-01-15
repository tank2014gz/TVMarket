package com.soniq.tvmarket.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import com.soniq.tvmarket.R;
import com.soniq.tvmarket.component.CustomUI;
import com.soniq.tvmarket.data.AppConfig;
import com.soniq.tvmarket.data.WAPI;
import com.soniq.tvmarket.model.ClientUpgrade;
import com.soniq.tvmarket.model.Feedback;
import com.soniq.tvmarket.ui.widget.MetroItemView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class FeedbackActivity extends MyBaseActivity implements OnClickListener{
	private MetroItemView netinformation = null;
	private MetroItemView feedback_1 = null;
	private MetroItemView feedback_2 = null;
	private MetroItemView feedback_3 = null;
	private MetroItemView feedback_4 = null;
	private MetroItemView feedback_5 = null;
	private MetroItemView feedback_6 = null;
	private MetroItemView feedback_7 = null;
	private MetroItemView feedback_8 = null;
	private Dialog _loadingDialog = null;
	
	private static final String COMPLEX = "feedback_complex";
	private static final String SLOW = "feedback_slow";
	private static final String CRASH = "feedback_crash";
	private static final String UI = "feedback_ui";
	private static final String DOWNLOAD = "feedback_download";
	private static final String INSTALL= "feedback_install";
	private static final String UPDATE = "feedback_upgrade";
	
	private Feedback _fb = null;
	private TextView tv1;
	private TextView tv2;
	private TextView tv3;
	
	
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_feedback);
	netinformation = (MetroItemView) findViewById(R.id.feedback_net);
	feedback_1 = (MetroItemView) findViewById(R.id.feedback_1);
	feedback_2 = (MetroItemView) findViewById(R.id.feedback_2);
	feedback_3 = (MetroItemView) findViewById(R.id.feedback_3);
	feedback_4 = (MetroItemView) findViewById(R.id.feedback_4);
	feedback_5 = (MetroItemView) findViewById(R.id.feedback_5);
	feedback_6 = (MetroItemView) findViewById(R.id.feedback_6);
	feedback_7 = (MetroItemView) findViewById(R.id.feedback_7);
	feedback_8 = (MetroItemView) findViewById(R.id.feedback_8);
	tv1 = (TextView) findViewById(R.id.feedback_nettext1);
	tv2 = (TextView) findViewById(R.id.feedback_nettext2);
	tv3 = (TextView) findViewById(R.id.feedback_nettext3);
	
	
	String localip = "";
	String Macip = "";
    WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE); 
    tv1.setText("版本号："+getVersion());
    if(!isConnectInternet()){
    	tv2.setText("");
        tv3.setText("");
    }else{
    
    if (wifiManager.isWifiEnabled()) {  
    	localip = getWIFILocalIpAddress(wifiManager); 
    	Macip = getLocalMacAddressFromWifiInfo(FeedbackActivity.this);
    } else {
    	localip = getLocalIPAddress();
    	Macip = getLocalMacAddressFromBusybox();
    }
    
    tv2.setText("ip地址: "+localip);
    tv3.setText("网卡地址: "+Macip);
    
    }
    
	netinformation.setRecommendImageRes(R.drawable.netinformation);
	feedback_1.setRecommendImageRes(R.drawable.feedback1);
	feedback_2.setRecommendImageRes(R.drawable.feedback2);
	feedback_3.setRecommendImageRes(R.drawable.feedback3);
	feedback_4.setRecommendImageRes(R.drawable.feedback4);
	feedback_5.setRecommendImageRes(R.drawable.feedback5);
	feedback_6.setRecommendImageRes(R.drawable.feedback6);
	feedback_7.setRecommendImageRes(R.drawable.feedback7);
	feedback_8.setRecommendImageRes(R.drawable.feedback8);
	
	feedback_1.setOnClickListener(this);
	feedback_2.setOnClickListener(this);
	feedback_3.setOnClickListener(this);
	feedback_4.setOnClickListener(this);
	feedback_5.setOnClickListener(this);
	feedback_6.setOnClickListener(this);
	feedback_7.setOnClickListener(this);
	feedback_8.setOnClickListener(this);
	
}

public static String getLocalMacAddressFromWifiInfo(Context context){
    WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);  
    WifiInfo info = wifi.getConnectionInfo();  
    return info.getMacAddress(); 
}


public String getVersion() {
	PackageManager pm = getPackageManager();
	try {
		// 代表的就是清单文件的信息。
		PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
		return packageInfo.versionName;
	} catch (NameNotFoundException e) {
		e.printStackTrace();
		// 肯定不会发生。
		// can't reach
		return "";
	}
	
}

public boolean isConnectInternet() {
boolean netSataus = false;
ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
if (networkInfo != null) { // 注意，这个判断一定要的哦，要不然会出错
    netSataus = networkInfo.isAvailable();
 
}
return netSataus;
}


//根据busybox获取本地Mac
public static String getLocalMacAddressFromBusybox(){   
    String result = "";     
    String Mac = "";
    result = callCmd("busybox ifconfig","HWaddr");
     
    //如果返回的result == null，则说明网络不可取
    if(result==null){
        return "网络出错，请检查网络";
    }
     
    //对该行数据进行解析
    //例如：eth0      Link encap:Ethernet  HWaddr 00:16:E8:3E:DF:67
    if(result.length()>0 && result.contains("HWaddr")==true){
        Mac = result.substring(result.indexOf("HWaddr")+6, result.length()-1);
        Log.i("test","Mac:"+Mac+" Mac.length: "+Mac.length());
         
        /*if(Mac.length()>1){
            Mac = Mac.replaceAll(" ", "");
            result = "";
            String[] tmp = Mac.split(":");
            for(int i = 0;i<tmp.length;++i){
                result +=tmp[i];
            }
        }*/
        result = Mac;
        Log.i("test",result+" result.length: "+result.length());            
    }
    return result;
}   

private static String callCmd(String cmd,String filter) {   
    String result = "";   
    String line = "";   
    try {
        Process proc = Runtime.getRuntime().exec(cmd);
        InputStreamReader is = new InputStreamReader(proc.getInputStream());   
        BufferedReader br = new BufferedReader (is);   
         
        //执行命令cmd，只取结果中含有filter的这一行
        while ((line = br.readLine ()) != null && line.contains(filter)== false) {   
            //result += line;
            Log.i("test","line: "+line);
        }
         
        result = line;
        Log.i("test","result: "+result);
    }   
    catch(Exception e) {   
        e.printStackTrace();   
    }   
    return result;   
}

public static String getWIFILocalIpAddress(WifiManager wifiManager) {
	WifiInfo wifiInfo = wifiManager.getConnectionInfo();      
    int ipAddress = wifiInfo.getIpAddress();  
    String ip = String.format("%d.%d.%d.%d",
            (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
            (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff)); 
    
    return ip;  
} 

private String getLocalIPAddress() {
    String IP = null;
    StringBuilder IPStringBuilder = new StringBuilder();
    try {
      Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
      while (networkInterfaceEnumeration.hasMoreElements()) {
        NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
        Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();
        while (inetAddressEnumeration.hasMoreElements()) {
          InetAddress inetAddress = inetAddressEnumeration.nextElement();
          if (!inetAddress.isLoopbackAddress()&&
            !inetAddress.isLinkLocalAddress()&&
             inetAddress.isSiteLocalAddress()) {
             IPStringBuilder.append(inetAddress.getHostAddress().toString()+"\n");
          }
        }
      }
    } catch (SocketException ex) {
    	Log.i("MainActivity", "");
    }

    IP = IPStringBuilder.toString();
    return IP;
  }

@Override
public void onClick(View view) {
	// TODO Auto-generated method stub
	switch(view.getId()){
    case R.id.feedback_1:
    	setMyClickListener(COMPLEX);
    	break;
    case R.id.feedback_2:
    	setMyClickListener(SLOW);
    	break; 
    case R.id.feedback_3:
    	setMyClickListener(CRASH);
    	break; 
    case R.id.feedback_4:
    	setMyClickListener(UI);
    	break; 
    case R.id.feedback_5:
    	setMyClickListener(DOWNLOAD);
    	break; 
    case R.id.feedback_6:
    	setMyClickListener(INSTALL);
    	break; 
    case R.id.feedback_7:
    	setMyClickListener(UPDATE);
    	break; 
    case R.id.feedback_8:
    //	setMyClickListener();
    	CustomUI.showFeedbackTipsDialog(FeedbackActivity.this, "欢迎联系我们，QQ:123456789");
    	break; 
	}
}

public void setMyClickListener(String key){
	String feedbackURL = WAPI.getFeedbackURLString(FeedbackActivity.this, key);
	
	AppConfig.showLog(feedbackURL);
	
	_loadingDialog = CustomUI.createFeedbackDialog(FeedbackActivity.this, "正在提交反馈信息!");
	_loadingDialog.show();
	
	_fb = new Feedback(FeedbackActivity.this);
	_fb.submit(new Feedback.FeedbackCallback() {

		@Override
		public void onFinished(int state) {
			// TODO Auto-generated method stub
			_loadingDialog.dismiss();
			_loadingDialog = null;
			
			if( state == Feedback.SUCCESS )
			{
				CustomUI.showFeedbackTipsDialog(FeedbackActivity.this, "反馈已提交，感谢您的支持");
			}
			else
			{
				CustomUI.showFeedbackTipsDialog(FeedbackActivity.this, "提交失败");
			}
		}
		},feedbackURL);
}

}

