package penn.apps.penndiscount;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

//import penn.apps.penndiscount.PennDiscountActivity.LoginTask;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class BusinessActivity extends Activity {
	   private EditText editText;
	   private String username;
	   private String customer;
	
	   @Override
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.business);
	        this.editText = (EditText)findViewById(R.id.editText1);
	        
	        Intent intent = getIntent();
	    	this.username = intent.getStringExtra("username");
	    	
	   }
	   
	   
	   public void findcustomer(View view){
	    	new FindCustomerTask().execute();
	    }
	   
	   private class FindCustomerTask extends AsyncTask<Void, Void, Integer> {
		    @Override
			protected void onPostExecute(Integer result) {
		    	if(result == 1) {
					   Intent intent = new Intent();
					   intent.putExtra("username", username);
					   intent.putExtra("customer", customer);
					   intent.setClass(BusinessActivity.this, FindCustomerActivity.class);  
			           startActivity(intent); 
				   }else {
					   Context context = getApplicationContext();
		           		CharSequence text = "Customer does not exist!";
			           	int duration = Toast.LENGTH_SHORT;
			
			           	Toast toast = Toast.makeText(context, text, duration);
			           	toast.show();
				   }
		   }

			@Override
			protected Integer doInBackground(Void... params) {
				  
				   try{
					   customer = editText.getText().toString();
						 Log.d("customer:", customer);
						 int result=0;
						   
						StringBuffer sb = new StringBuffer();
				
						String url = String.format("http://"+IPaddress.getIP()+":8080/penndiscount/findCustomer?username=%s", customer);
						
						
						HttpClient client = new DefaultHttpClient();
						HttpGet request = new HttpGet(url);
						Log.d("url", url);
						HttpResponse response = client.execute(request);
						Log.d("succeed", "succeed");
						
						//System.out.println(url);

						// Get the response
						BufferedReader rd = new BufferedReader
							(new InputStreamReader(response.getEntity().getContent()));
						
						String line = "";
						while ((line = rd.readLine()) != null) {
							sb.append(line);
						} 
						Log.d("http", sb.toString());
						result = Integer.parseInt(sb.toString());
						System.out.println("result="+result);
						return result;
				}
				catch(Exception e){
					Log.d("error", e.getMessage());;
					return 0;
				}	
			}
	   
	   }
	   
	   
	   //show coupons
	   public void manageCoupons(View view){
	    	new ShowCouponsTask().execute();
	    }
	    
	    
	    private class ShowCouponsTask extends AsyncTask<Void, Void, Integer> {
	        @Override
			protected void onProgressUpdate(Void... params) {
	            
	        }

	        @Override
			protected void onPostExecute(Integer result) {
	            	Intent intent = new Intent();  
	            	intent.putExtra("username",  username);
	                intent.setClass(BusinessActivity.this, BusinessCouponsActivity.class);  
	                startActivity(intent); 

	        }

			@Override
			protected Integer doInBackground(Void... params) {
//				
//				try{
//					StringBuffer sb = new StringBuffer();
//					
//					String login = loginText.getText().toString();
//					username = login;
//					String password = passwordText.getText().toString();
//					String url = String.format("http://165.123.210.28:8080/penndiscount/login?username=%s&password=%s", login,password);
//					HttpClient client = new DefaultHttpClient();
//					HttpGet request = new HttpGet(url);
//					//Log.d("url", url);
//					HttpResponse response = client.execute(request);
//
//					// Get the response
//					BufferedReader rd = new BufferedReader
//							(new InputStreamReader(response.getEntity().getContent()));
//
//					String line = "";
//					while ((line = rd.readLine()) != null) {
//						sb.append(line);
//					} 
//					//Log.d("http", sb.toString());
//					return new Integer(Integer.parseInt(sb.toString()));
//				}
//				catch(Exception e){
//					return null;
//				}
				return 1;
			}

	    }
}
