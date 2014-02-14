package com.example.generalhome;

import android.os.Bundle;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class APICallActivity extends Activity {
	private WebView mapview;
	private WebView chartview;
	private WebView newsview;
	private Button mapButton;
	private Button chartButton;
	private Button newsButton;
	private Double latitude;
	private Double longitude;
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apicall);
		mapview = (WebView) findViewById(R.id.mapview2);
		chartview = (WebView) findViewById(R.id.chartview2);
		newsview= (WebView) findViewById(R.id.newsview2);
		mapButton = (Button)findViewById(R.id.mapButton2);
		chartButton = (Button)findViewById(R.id.chartButton2);
		newsButton = (Button)findViewById(R.id.newsButton2);
		 intent = getIntent();
		
		
		mapButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				/*z is the zoom level (1-20)
t is the map type ("m" map, "k" satellite, "h" hybrid, "p" terrain, "e" GoogleEarth)
q is the search query, if it is prefixed by loc: then google assumes it is a lat lon separated by a +*/
				Bundle extras = intent.getExtras();
				latitude = extras.getDouble("lat");
				longitude = extras.getDouble("lng");
				String googleMapPath ="https://maps.google.com/?ll="+latitude+","+longitude+"&t=m&z=15";
				mapview.getSettings().setJavaScriptEnabled(true);
				mapview.loadUrl(googleMapPath);
			}
		});		
		chartButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//twitter tags : https://twitter.com/search?q=%23weather&src=typd
				String query ="weather";
				String url = "https://www.google.com/search?q="+query;
				chartview.getSettings().setJavaScriptEnabled(true);
				chartview.loadUrl(url);
			}
		});
		newsButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//twitter tags : https://twitter.com/search?q=%23weather&src=typd
				String url = "https://www.google.com/search?q=weather+news";
				newsview.getSettings().setJavaScriptEnabled(true);
				newsview.loadUrl(url);
			}
		});		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.apicall, menu);
		return true;
	}

}
