package com.example.samples1navermovie;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.begentgroup.xmlparser.XMLParser;
import com.example.samples1navermovie.NetworkManager.OnResultListener;

public class MainActivity extends ActionBarActivity {

	ListView listView;
//	ArrayAdapter<MovieItem> mAdapter;
	MyAdapter mAdapter;
	EditText keywordView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listView = (ListView) findViewById(R.id.listView1);
//		mAdapter = new ArrayAdapter<MovieItem>(this,
//				android.R.layout.simple_list_item_1);
		mAdapter = new MyAdapter();
		listView.setAdapter(mAdapter);
		keywordView = (EditText) findViewById(R.id.edit_keyword);
		Button btn = (Button) findViewById(R.id.btn_search);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String keyword = keywordView.getText().toString();
				if (keyword != null && !keyword.equals("")) {
//					new MovieTask().execute(keyword);
					NetworkManager.getInstance().getNaverMovies(MainActivity.this, keyword, new OnResultListener<NaverMovies>() {
						
						@Override
						public void onSuccess(NaverMovies result) {
							for (MovieItem item : result.items) {
								mAdapter.add(item);
							}
						}
						
						@Override
						public void onFail(int code) {
							Toast.makeText(MainActivity.this, "fail", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		});
	}

	class MovieTask extends AsyncTask<String, Integer, NaverMovies> {
		@Override
		protected NaverMovies doInBackground(String... params) {
			String keyword = params[0];
			try {
				String urlString = String
						.format("http://openapi.naver.com/search?key=55f1e342c5bce1cac340ebb6032c7d9a&query=%s&display=10&start=1&target=movie",
								URLEncoder.encode(keyword, "utf-8"));
				URL url = new URL(urlString);
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				int code = conn.getResponseCode();
				if (code == HttpURLConnection.HTTP_OK) {
					InputStream is = conn.getInputStream();
					XMLParser parser = new XMLParser();
					NaverMovies result = parser.fromXml(is, "channel", NaverMovies.class);
					return result;
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}
		
		@Override
		protected void onPostExecute(NaverMovies result) {
			super.onPostExecute(result);
			if (result != null) {
				for (MovieItem item : result.items) {
					mAdapter.add(item);
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
