package penn.apps.penndiscount;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PennDiscountActivity extends Activity {
	private String username;
	private EditText loginText;
	private EditText passwordText;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        this.loginText = (EditText) findViewById(R.id.logintext);
        this.passwordText = (EditText) findViewById(R.id.passwordtext);
    }
    
    public void login(View view){
    	new LoginTask().execute();
    }
    
    
    private class LoginTask extends AsyncTask<Void, Void, Integer> {
        @Override
		protected void onProgressUpdate(Void... params) {
            
        }

        @Override
		protected void onPostExecute(Integer result) {
        	System.out.println(result);
            if (result.intValue() == 1){
            	Intent intent = new Intent();  
            	intent.putExtra("username",  username);
                intent.setClass(PennDiscountActivity.this, YourCouponsActivity.class);  
                startActivity(intent); 
            }
            else if (result.intValue() == 2){
            	Intent intent = new Intent();  
            	intent.putExtra("username",  username);
                intent.setClass(PennDiscountActivity.this, BusinessActivity.class);  
                startActivity(intent); 
            	
            	
            }
            else{
            	Context context = getApplicationContext();
            	CharSequence text = "Invalid combination of username and password!";
            	int duration = Toast.LENGTH_SHORT;

            	Toast toast = Toast.makeText(context, text, duration);
            	toast.show();
            }
        }

		@Override
		protected Integer doInBackground(Void... params) {
			
			try{
				StringBuffer sb = new StringBuffer();
				
				String login = loginText.getText().toString();
				username = login;
				String password = passwordText.getText().toString();
				String url = String.format("http://"+IPaddress.getIP()+":8080/penndiscount/login?username=%s&password=%s", login,password);
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet(url);
				//Log.d("url", url);
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
    
}