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
import android.widget.Toast;

public class YourCouponsActivity extends Activity {
	
	public class Coupon{
		private String companyName;
		private Bitmap bitmap;
		private String couponId;
		private String address;
		private String phoneNumber;
		private String expireDate;
		private String description;
		
		public Coupon(String companyName, Bitmap bitmap, String couponId, String address, String phoneNumber, String expireDate, String description){
			this.companyName = companyName;
			this.bitmap = bitmap;
			this.couponId = couponId;
			this.address = address;
			this.phoneNumber = phoneNumber;
			this.expireDate = expireDate;
			this.description = description;
		}
		
		public String[] getDisplay(){
			return  new String[]{this.companyName, this.address, this.phoneNumber, this.expireDate, this.description};
		}
	}
	
	
	
	private String username;
	private ArrayList<Coupon> coupons;
	
	private ImageView image;
	private ImageAdapter adpater;
	private ListView listView;
	private Gallery gallery;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	Intent intent = getIntent();
    	this.username = intent.getStringExtra("username");
    	this.coupons = new ArrayList<Coupon>();
    	this.adpater = new ImageAdapter(this);
    	new GetCouponsTask().execute();
    	
    	String[] testDetail = {"Name", "address", "phone no.", "Expiration date", "discounts"};
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coupon);
        
        
        this.image = (ImageView)findViewById(R.id.imageView1);
        this.listView = (ListView)findViewById(R.id.listView1);
        this.gallery = (Gallery) findViewById(R.id.gallery1);
        gallery.setSpacing(2);
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
        
        
        //this.listView.setAdapter(new ArrayAdapter<String>(this, R.layout.list_coupon, testDetail));
    }
    
    public class ImageAdapter extends BaseAdapter {
    	
    	public Bitmap getImage(int number){
    		return this.bitmaps.get(number);
    	}
    	
    	public ImageAdapter(Context c, ArrayList<Bitmap> bitmaps) {
    		mContext = c;
    		this.bitmaps = bitmaps;
    	}
    	
    	public ImageAdapter(Context c){
    		mContext = c;
    	}
    	
    	public void setBitmaps(ArrayList<Bitmap> bitmaps){
    		this.bitmaps = bitmaps;
    	}

    	@Override
		public int getCount() {
    		return bitmaps.size();
    	}


    	@Override
		public Object getItem(int position) {
    		return position;
    	}

    	@Override
		public long getItemId(int position) {
    		return position;
    	}

    	@Override
		public View getView(int position, View convertView, ViewGroup parent) {
    		ImageView i = new ImageView(mContext);
    		i.setImageBitmap(this.bitmaps.get(position));
    		i.setScaleType(ImageView.ScaleType.FIT_XY);
    		i.setLayoutParams(new Gallery.LayoutParams(136, 88));
    		return i;
    	}

    	private Context mContext;
   
    	private ArrayList<Bitmap> bitmaps;

    }
    
    private class GetCouponsTask extends AsyncTask<Void, Void, Integer> {
        @Override
		protected void onProgressUpdate(Void... params) {
            
        }

        @Override
		protected void onPostExecute(Integer result) {
            if (result.intValue() == 1){
            	ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
            	for (Coupon coupon : coupons){
            		bitmaps.add(coupon.bitmap);
            	}
            	adpater.setBitmaps(bitmaps);
                gallery.setAdapter(adpater);
            	gallery.setOnItemSelectedListener(new OnItemSelectedListener()
                {

        			@Override
        			public void onItemSelected(AdapterView<?> arg0, View arg1,
        					int arg2, long arg3) {
        				// TODO Auto-generated method stub
        				//Log.d("selected", "selected");
        				image.setImageBitmap(adpater.getImage(arg2));
        				listView.setAdapter(new ArrayAdapter<String>(YourCouponsActivity.this, R.layout.list_coupon, coupons.get(arg2).getDisplay() ));
        			}

        			@Override
        			public void onNothingSelected(AdapterView<?> arg0) {
        				// TODO Auto-generated method stub
        				
        			}
                	
                });
            	
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
				
				String url = String.format("http://"+IPaddress.getIP()+":8080/penndiscount/usercoupon?username=%s", username);
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
				int length = couponInfos.length;
				for (int i = 0; i < length; i++){
					String[] infos = couponInfos[i].split(":");
					String companyName = infos[0];
					String imageUrl = String.format("http://"+IPaddress.getIP()+":8080/penndiscount/%s", infos[1]);
					request = new HttpGet(imageUrl);
					Log.d("url", imageUrl);
					response = client.execute(request);
					Bitmap img = BitmapFactory.decodeStream(response.getEntity().getContent()); 
					String couponId = infos[2];
					
					
					StringBuffer sb2 = new StringBuffer();
					
					String url2 = String.format("http://"+IPaddress.getIP()+":8080/penndiscount/coupondetail?couponID=%s&username=%s", couponId, username);
					request = new HttpGet(url2);
					Log.d("url", url2);
					HttpResponse response2 = client.execute(request);

					// Get the response
					BufferedReader rd2 = new BufferedReader
							(new InputStreamReader(response2.getEntity().getContent()));

					String line2 = "";
					while ((line2 = rd2.readLine()) != null) {
						sb2.append(line2);
					} 
					Log.d("http2", sb2.toString());
					String[] infos2 = sb2.toString().split(":");
					coupons.add(new Coupon(companyName, img, infos[2], infos2[0], infos2[1], infos2[2], infos2[3]));
				}
				
				
				return 1;
			}
			catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}

    }
    

}
