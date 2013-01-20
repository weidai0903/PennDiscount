package penn.apps.penndiscount;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BusinessCouponsActivity extends Activity {

	private String username;
	private String[] coupons;
	private TextView textView1;
	private ListView listView;
	//private Gallery gallery;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	Intent intent = getIntent();
    	this.username = intent.getStringExtra("username");
    	
    	
        
    	new GetCouponsTask().execute();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_coupons);
        this.listView = (ListView)findViewById(R.id.listView1);
        this.textView1 = (TextView)findViewById(R.id.textView1);
        Log.d("success","succeed");
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
//
//                android.R.layout.simple_list_item_1,
//
//               coupons);
        //this.gallery = (Gallery) findViewById(R.id.gallery1);
        //gallery.setSpacing(2);
        /*
        g.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3)
            {
                // TODO Auto-generated method stub
            	image.setImageResource(adpater.getImage(position));
            }
        });*/
        
        
        //this.listView.setAdapter(arrayAdapter);
    }
    
    public void returnHome(View view){
    	new ReturnTask().execute();
    }
    
    private class ReturnTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return 1;
		}
		 @Override
			protected void onPostExecute(Integer result) {
			 Intent intent = new Intent();  
         	 intent.putExtra("username",  username);
             intent.setClass(BusinessCouponsActivity.this, BusinessActivity.class);  
             startActivity(intent); 
		 }
		
    	
    }
    
    private class GetCouponsTask extends AsyncTask<Void, Void, Integer> {
        @Override
		protected void onProgressUpdate(Void... params) {
            
        }

        @Override
		protected void onPostExecute(Integer result) {
            if (result!=null && result.intValue() == 1){
            	 ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(BusinessCouponsActivity.this,
            			 android.R.layout.simple_list_item_1, coupons);
            	 listView.setAdapter(arrayAdapter);
            	 textView1.setText(username+"'s existing Coupons");
            	 
            }
            else{
            	Context context = getApplicationContext();
            	CharSequence text = "Error!";
            	int duration = Toast.LENGTH_SHORT;

            	Toast toast = Toast.makeText(context, text, duration);
            	toast.show();
            }
        }

		@Override
		protected Integer doInBackground(Void... params) {
			
			try{
				StringBuffer sb = new StringBuffer();
				
				String url = String.format("http://"+IPaddress.getIP()+":8080/penndiscount/couponsByCompany?username=%s", username);
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet(url);
				Log.d("url", url);
				HttpResponse response = client.execute(request);

				// Get the response
				BufferedReader rd = new BufferedReader
						(new InputStreamReader(response.getEntity().getContent()));

				String line = "";
				while ((line = rd.readLine()) != null) {
					sb.append(line);
				} 
				Log.d("http", sb.toString());
				
				String[] couponInfos = sb.toString().split(";");
				coupons = new String[couponInfos.length];
				for( int i=0;i<couponInfos.length; i++) {
					coupons[i]=couponInfos[i].split(":")[1];
				}
				
				return 1;
			}
			catch(Exception e){
				Log.d("error:",e.getMessage());
				return 0;
			}
		}

    }
    

}
