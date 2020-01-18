package kr.ac.kumoh.s20161376.myimdb;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.LruCache;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

public class Item extends AppCompatActivity {
    protected ImageLoader mImageLoader = null;
    protected RequestQueue mQueue = null;
    public static final String SERVER_URL = "http://125.185.23.23/movie/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Intent intent = getIntent();
        TextView movie = (TextView)findViewById(R.id.movie);
        movie.setText(intent.getStringExtra("movie"));
        TextView year = (TextView)findViewById(R.id.date);
        year.setText(intent.getStringExtra("year"));
        TextView note = (TextView)findViewById(R.id.note);
        note.setText(intent.getStringExtra("note"));

        String temp = intent.getStringExtra("movie");
        final String query = temp;
        temp = temp+".jpg";

        mQueue = Volley.newRequestQueue(this);
        mImageLoader = new ImageLoader(mQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);
                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }
                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
        NetworkImageView image = findViewById(R.id.img);
        image.setImageUrl(SERVER_URL + temp, mImageLoader);

        image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String link = "https://www.google.co.kr/search?q="+ query;
                Uri uri = Uri.parse(link);
                Intent intent =  new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });
    }
}
