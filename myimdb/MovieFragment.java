package kr.ac.kumoh.s20161376.myimdb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;


public class MovieFragment extends Fragment {

    protected String genre;

    public MovieFragment() {

    }

    @SuppressLint("ValidFragment")
    public MovieFragment(String php) {
        this.genre = php;
    }

    public class Movie {
        int id;
        String engName;
        String korName;
        String director;
        String year;
        String imageUrl;
        String metascore;
        String note;


        public Movie(int id, String engName, String korName, String director, String year, String imageUrl, String metascore, String note) {
            this.id = id;
            this.engName = engName;
            this.korName = korName;
            this.director = director;
            this.year = year;
            this.imageUrl = imageUrl;
            this.metascore = metascore;
            this.note = note;
        }

        public int getId() {
            return id;
        }

        public String getEngName() {
            return engName;
        }

        public String getKorName() {
            return korName;
        }

        public String getDirector() {
            return director;
        }

        public String getYear() {
            return year;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getMetascore() {
            return metascore;
        }

        public String getNote() {
            return note;
        }

    }

    public static final String QUEUE_TAG = "ActionRequest";
    public static final String SERVER_URL = "http://125.185.23.23/";
    protected ImageLoader mImageLoader = null;
    protected RequestQueue mQueue = null;
    protected JSONObject mResult = null;
    protected ArrayList<Movie> mArray = new ArrayList<Movie>();
    protected MovieAdapter mAdapter = new MovieAdapter();
    protected RecyclerView mRecyclerView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        CookieHandler.setDefault(new CookieManager());
        mQueue = Volley.newRequestQueue(getContext());
        mImageLoader = new ImageLoader(mQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });

        requestMovie();
        return rootView;

    }

    protected void requestMovie() {
        String url = SERVER_URL + genre;
        JsonObjectRequest request =
                //Logcat 확인용
                new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.i("result", response.toString());
                                mResult = response;
                                parseJSON();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                                Log.i("result", error.toString());
                            }
                        });
        request.setTag(QUEUE_TAG);
        mQueue.add(request);
    }

    protected void parseJSON() {
        mArray.clear();
        try {
            JSONArray array = mResult.getJSONArray("list");
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = ((JSONObject) array.get(i));
                int id = item.getInt(("id"));
                String engName = item.getString("engname");
                String korName = item.getString("korname");
                String directior = item.getString("director");
                String year = item.getString("year");
                String imageUrl = item.getString("imageurl");
                String metascore = item.getString("metascore");
                String note = item.getString("note");
                mArray.add(new Movie(id, engName, korName, directior, year, imageUrl, metascore, note));
            }
        } catch (JSONException | NullPointerException e) {
            Toast.makeText(getContext(), "Error " + e.toString(), Toast.LENGTH_LONG).show();
            mResult = null;
        }
        mAdapter.notifyDataSetChanged();
    }


    public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieView> {
        public class MovieView extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView txtEngName;
            public TextView txtKorName;
            public TextView txtDirector;
            public TextView txtYear;
            public NetworkImageView imageUrl;
            public String txtNote;
            public TextView txtMetascore;

            public MovieView(View root) {
                super(root);
                root.setOnClickListener(this);
                txtEngName = (TextView) root.findViewById(R.id.engName);
                txtKorName = (TextView) root.findViewById(R.id.korName);
                txtYear = (TextView) root.findViewById(R.id.year);
                txtDirector = (TextView) root.findViewById(R.id.dir);
                txtMetascore = (TextView) root.findViewById(R.id.metaScore);
                imageUrl = (NetworkImageView) root.findViewById(R.id.imageUrl);
            }


            @Override
            public void onClick(View view) {
                Intent result = new Intent(getContext(), Item.class);
                String movie = txtEngName.getText().toString();
                String note = txtNote;
                String year = txtYear.getText().toString();
                result.putExtra("movie", movie);
                result.putExtra("year", year);
                result.putExtra("note",note);
                startActivity(result);
            }
        }

        public MovieAdapter() {

        }

        @Override
        public MovieView onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
            return new MovieView(root);
        }

        @Override
        public void onBindViewHolder(MovieView holder, int position) {
            Movie g = mArray.get(position);
            holder.txtEngName.setText(g.getEngName());
            holder.txtKorName.setText(g.getKorName());
            holder.imageUrl.setImageUrl(SERVER_URL + g.getImageUrl(), mImageLoader);
            holder.txtDirector.setText("감독: "+g.getDirector());
            holder.txtYear.setText("개봉년도: " + g.getYear());
            holder.txtMetascore.setText("메타스코어: " + g.getMetascore());
            holder.txtNote = g.getNote();
        }

        @Override
        public int getItemCount() {
            return mArray.size();
        }
    }

}
