package com.kutsubogeras.shifts.help;
import com.kutsubogeras.shifts.R;
import com.kutsubogeras.shifts.R.id;
import com.kutsubogeras.shifts.R.layout;
import com.kutsubogeras.shifts.R.menu;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class DisplayWebInfo extends Activity {
   //Variables class
	private Uri url;
	private WebView webView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_web_info);
		
		// ������� �� ���� �� �� Intent
		url = getIntent().getData();
		webView = (WebView)findViewById(R.id.webView_developer_facebook);
		
		// ����������� ��� Javascript ��� �� ����
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		//webView.setWebChromeClient(new WebChromeClient()); //� ���� � ��� ������� ������
		webView.setWebViewClient(new CallBack());
		webView.loadUrl(url.toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_web_info, menu);
		return true;
	}
	
	//--------------------INNER CLASS ----------------------------------
	private class CallBack extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url){			
			return false;
		}//end method
		
	}//end inner class
	//------------------------------------------------------------------

}//end class
