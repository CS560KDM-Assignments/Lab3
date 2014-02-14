package com.example.generalhome;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class MainActivity extends Activity implements  LocationListener{

	@SuppressLint("NewApi")
	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	private Button weatherButton;
	private Button mapButton;
	ImageView image ;
	private EditText weatherText;
	private EditText latLngText;
	private Button latLngButton;
	private Button AddressButton;
	private String Address1 = "", Address2 = "", City = "", State = "", Country = "", County = "", PIN = "";
	private EditText addressText;
	private TextView date;
	private double latitude;
	private double longitude;
	protected LocationManager locationManager;
	protected LocationListener locationListener;
	protected Context context;
	private String date_text;
	private ImageView weather_img;
	private String lat,lng;
	private int[] imageids;
	private int count;
	private WebView map;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.activity_main);

		//Initialising variables
		AddressButton = (Button) findViewById(R.id.add);
		image = (ImageView)findViewById(R.id.page_images);
		latLngButton = (Button)findViewById(R.id.latlng);
		weatherButton = (Button) findViewById(R.id.weather);
		weatherText = (EditText)findViewById(R.id.weather_text);
		latLngText = (EditText) findViewById(R.id.latLng_text);
		addressText = (EditText) findViewById(R.id.address);
		date_text = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		date = (TextView) findViewById(R.id.date);
		date.setText(date_text);
		weather_img = (ImageView)(findViewById(R.id.weather_img));
		mapButton = (Button)findViewById(R.id.mapButton);
		lat="";
		lng="";
		count =0;
		//map = (WebView)findViewById(R.id.mapview);
        
		imageids = new int[]{R.drawable.top9, R.drawable.top3,R.drawable.top7,R.drawable.top5,R.drawable.top10,
				R.drawable.top4,R.drawable.top8,R.drawable.top2,R.drawable.top6,R.drawable.top1};

		//getting the location with location manager
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		
				Criteria criteria = new Criteria();
				String bestProvider = locationManager.getBestProvider(criteria, false);
				
				Location location = locationManager.getLastKnownLocation(bestProvider);
				
				if (location == null)
					latLngText.setText("\nLocation[unknown]\n\n");
				else
					{latitude = location.getLatitude();
				longitude = location.getLongitude();
				latLngText.setText("Latitude:" + latitude + ", Longitude:" + longitude);}
			

		//Setting Date on right top corner
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattedDate = df.format(c.getTime());
		date.setText(formattedDate)	;	

		//Animation for the image
		TranslateAnimation animation = new TranslateAnimation(-200.0f, 200.0f, 0.0f, 0.0f); 
		animation.setDuration(5000);  
		animation.setRepeatCount(Animation.INFINITE);  
		animation.setRepeatMode(2);   
		animation.setFillAfter(true);      
		animation.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
			}

			public void onAnimationRepeat(Animation animation) {
				if(count == imageids.length)
					count =0;
				image.setImageResource(imageids[count++]); // images changes

			}

			public void onAnimationEnd(Animation animation) {


			}
		});
		image.startAnimation(animation);  

				
		//Address button gives the current address
		AddressButton.setOnClickListener(new View.OnClickListener(){

			public void onClick(View v){
				getlatlng();
				getAddress();
				String fullAddress = ""+Address1+Address2+"\n"+City +","+State+"\n"+Country+"-"+PIN;
				addressText.setText(fullAddress);
			}});

		//gives the current weather
		weatherButton.setOnClickListener(new View.OnClickListener(){

			public void onClick(View v){
				getlatlng();
				getAddress();
				String uriEncode="";
				
				String uri = "http://api.wunderground.com/api/36b799dc821d5836/conditions/q/" +State +"/"+City +".json";
				uri = uri.replaceAll(" ", "%20");
				System.out.println("encodedddd:"+ uri);
				String respString = getJsonFromUrl(uri);
				String weather ="";
				System.out.println(respString);
				HashMap<String, String> results = weatherFromJsonResult(respString);
				for (HashMap.Entry<String,String> entry : results.entrySet()) {
					if (entry.getKey()== "icon_url"){
						try {
							InputStream is = (InputStream) new URL(entry.getValue()).getContent();
							Drawable d = Drawable.createFromStream(is, "src name");
							weather_img.setImageDrawable(d);
						} catch (Exception e) {
						}
					}
					else{
						weather = weather + entry.getKey()+" : "+entry.getValue()+"\n";
					}
				}
				weatherText.setText(weather);
			} 
		});
		mapButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
							
				 Intent intent = new Intent(MainActivity.this, APICallActivity.class);
				 intent.putExtra("lat", latitude);
				 intent.putExtra("lng", longitude);
			        startActivity(intent);
		
				
		/*		z is the zoom level (1-20)
t is the map type ("m" map, "k" satellite, "h" hybrid, "p" terrain, "e" GoogleEarth)
q is the search query, if it is prefixed by loc: then google assumes it is a lat lon separated by a +
				*/
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	//Retriving weather data
	public HashMap<String, String> weatherFromJsonResult(String jsonString)
	{

		HashMap result= new HashMap<String, String>();
		String weather ="";
		String temp_f="";
		String temp_c="";
		String feels = "";
		String icon_url="";
		JSONObject jsonObj1;
		try {
			jsonObj1 = new JSONObject(jsonString);
			JSONObject resultClimate=jsonObj1.getJSONObject("current_observation");

			result.put("Weather",resultClimate.getString("weather"));
			result.put("Temp in F",resultClimate.getString("temp_f"));
			result.put("Temp in C",resultClimate.getString("temp_c"));
			result.put("Feels Like",resultClimate.getString("feelslike_string"));
			result.put("icon_url",resultClimate.getString("icon_url"));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	//Location manager implemented methods
	@Override
	public void onLocationChanged(Location location) {

		latitude = location.getLatitude();
		longitude = location.getLongitude();
		System.out.println("location changed");
		latLngText.setText("Latitude:" + latitude + ", Longitude:" + longitude);
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d("Latitude","disable");
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d("Latitude","enable");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d("Latitude","status");
	}

	//getting JSON from URL
	String getJsonFromUrl(String url)
	{System.out.println("url is"+url);
			HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response;
		String responseString = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			response = httpclient.execute(httpPost);
			StatusLine statusLine = response.getStatusLine();
			if(statusLine.getStatusCode() == HttpStatus.SC_OK){
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				responseString = out.toString();
			} else{
				//Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("rsponse::"+responseString);
		return responseString;
	}

	void getlatlng()
	{	
		String latlng[] = latLngText.getText().toString().split(",");
		lat =latlng[0].split(":")[1];
		lng =latlng[1].split(":")[1];
	}


	void getAddress()
	{
		String uri = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + ","+ lng + "&sensor=true";
		System.out.println(uri);
		String responseString = getJsonFromUrl(uri);
		AddressFromJsonResult(responseString);
	}

	void AddressFromJsonResult(String responseString)
	{
		try{

			JSONObject jsonObj = new JSONObject	(responseString);		
			String Status = jsonObj.getString("status");
			if (Status.equalsIgnoreCase("OK")) {
				JSONArray Results = jsonObj.getJSONArray("results");
				JSONObject zero = Results.getJSONObject(0);
				JSONArray address_components = zero.getJSONArray("address_components");

				for (int i = 0; i < address_components.length(); i++) {
					JSONObject addresses = address_components.getJSONObject(i);
					String long_name = addresses.getString("long_name");
					JSONArray mtypes = addresses.getJSONArray("types");
					String Type = mtypes.getString(0);


					if (TextUtils.isEmpty(long_name) == false || !long_name.equals(null) || long_name.length() > 0 || long_name != "") {
						if (Type.equalsIgnoreCase("street_number")) {
							Address1 = long_name + " ";
						} else if (Type.equalsIgnoreCase("route")) {
							Address1 = Address1 + long_name;
						} else if (Type.equalsIgnoreCase("sublocality")) {
							Address2 = long_name;
						} else if (Type.equalsIgnoreCase("locality")) {
							// Address2 = Address2 + long_name + ", ";
							City = long_name;
						} else if (Type.equalsIgnoreCase("administrative_area_level_2")) {
							County = long_name;
						} else if (Type.equalsIgnoreCase("administrative_area_level_1")) {
							State = long_name;
						} else if (Type.equalsIgnoreCase("country")) {
							Country = long_name;
						} else if (Type.equalsIgnoreCase("postal_code")) {
							PIN = long_name;
						}
					}

				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}




