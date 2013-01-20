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
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FindCustomerActivity extends Activity {

	private String username;
	private String[] coupons;
	private String customer;
	private TextView textView;
	private String[] couponIDs;
	
	private ListView listView;
	//private Gallery gallery;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	Intent intent = getIntent();
    	this.username = intent.getStringExtra("username");
    	this.customer = intent.getStringExtra("customer");
    	
    	
    	new GetCouponsTask().execute();
    	    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_coupon_offer);
   	    listView = (ListView)findViewById(R.id.listView1);
   	    textView = (TextView)findViewById(R.id.textView2);

        
       
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
        
        
        
    }
    public void sendCoupon(View view){
    	new SendCoupon().execute();
    }
    
    
    private class SendCoupon extends AsyncTask<Void, Void, Integer> {
        @Override
		protected void onProgressUpdate(Void... params) {
            
        }

        @Override
		protected void onPostExecute(Integer result) {
        	System.out.println(result);
            if (result.intValue() == 1){
            	
            	Context context = getApplicationContext();
            	CharSequence text = "Send Succeeds!";
            	int duration = Toast.LENGTH_SHORT;
            	Toast toast = Toast.makeText(context, text, duration);
            	toast.show();
            	
            	Intent intent = new Intent();  
            	intent.putExtra("username",  username);
                intent.setClass(FindCustomerActivity.this, BusinessActivity.class);  
                startActivity(intent); 
            }
            else{
            	Context context = getApplicationContext();
            	CharSequence text = "unsuccessful, please try again!";
            	int duration = Toast.LENGTH_SHORT;
            	Toast toast = Toast.makeText(context, text, duration);
            	toast.show();
            }
        }

		@Override
		protected Integer doInBackground(Void... params) {
			
			try{
				int checkedItem = listView.getCheckedItemPosition();
				String couponID = couponIDs[checkedItem];
				StringBuffer sb = new StringBuffer();
				String url = String.format("http://"+IPaddress.getIP()+":8080/penndiscount/offerCoupon?username=%s&couponID=%s&expire=%s", customer, couponID, "2013-01-30");
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
				//System.out.println("sb="+sb.toString());
				Log.d("http", sb.toString());
				return new Integer(Integer.parseInt(sb.toString()));
			}
			catch(Exception e){
				return null;
			}
		}
    }

    
    private class GetCouponsTask extends AsyncTask<Void, Void, Integer> {
        @Override
		protected void onProgressUpdate(Void... params) {
            
        }

        @Override
		protected void onPostExecute(Integer result) {
            if (result!=null && result.intValue() == 1){
                 
                 ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(FindCustomerActivity.this,
                         android.R.layout.simple_list_item_single_choice,
                        coupons);
                 
                listView.setAdapter(arrayAdapter);
                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);  
                listView.setItemChecked(0, true); 
                textView.setText(customer);
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
				couponIDs = new String[couponInfos.length];
				for( int i=0;i<couponInfos.length; i++) {
					couponIDs[i]=couponInfos[i].split(":")[0];
				}
				
				return 1;
			}
			catch(Exception e){
				return null;
			}
		}

    }
    

}
