package com.example.practical_megha;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public class MainActivity extends AppCompatActivity {

    class Get_Set {
        /*
        INSTANCE FIELDS
         */

        @SerializedName("method")
        private String method;
        @SerializedName("userid")
        private String userid;
        @SerializedName("pageid")
        private int pageid;


        public Get_Set(String method, String userid, int pageid) {
            this.method = method;
            this.userid = userid;
            this.pageid = pageid;
        }

        /*
         *GETTERS AND SETTERS
         */

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public int getPageid() {
            return pageid;
        }

        public void setPageid(int pageid) {
            this.pageid = pageid;
        }
        /*
        TOSTRING
         */
    }


    interface MyAPIService {
        @Headers({"api-version: P1M1",
                "Authorization: Token 63ca7104caf546f08a8d5cb500ab7248"})
        @GET("/")
        Call<List<Get_Set>> getGetSetValue();
       /* Gson gson = new GsonBuilder()
                .setLenient()
                .create();*/

    }

    static class RetrofitClientInstance {

        private static Retrofit retrofit;
        private static final String BASE_URL = "https://plutomen-spaceforce.com/py-api/";

        public static Retrofit getRetrofitInstance() {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        }
    }

    class GridViewAdapter extends BaseAdapter {

        private List<Get_Set> get_set;
        private Context context;

        public GridViewAdapter(Context context, List<Get_Set> spacecrafts) {
            this.context = context;
            this.get_set = spacecrafts;
        }

        @Override
        public int getCount() {
            return get_set.size();
        }

        @Override
        public Object getItem(int pos) {
            return get_set.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.model, viewGroup, false);
            }

            TextView methodtxt = view.findViewById(R.id.nameTextView);
            TextView txtuserid = view.findViewById(R.id.userID);
            CheckBox chkTechExists = view.findViewById(R.id.myCheckBox);
            ImageView ImageView = view.findViewById(R.id.ImageView);

            final Get_Set thisdemoapp = get_set.get(position);

            methodtxt.setText(thisdemoapp.getMethod());
            txtuserid.setText(thisdemoapp.getUserid());
            //chkTechExists.setChecked( thisSpacecraft.getTechnologyExists()==1);
            //chkTechExists.setEnabled(false);

            if (thisdemoapp.getMethod() != null && thisdemoapp.getMethod().length() > 0) {
                Picasso.get().load(thisdemoapp.getPageid()).placeholder(R.drawable.placeholder).into(ImageView);
            } else {
                Toast.makeText(context, "Empty Image URL", Toast.LENGTH_LONG).show();
                Picasso.get().load(R.drawable.placeholder).into(ImageView);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, thisdemoapp.getPageid(), Toast.LENGTH_SHORT).show();
                }
            });

            return view;
        }
    }

    private GridViewAdapter adapter;
    private GridView mGridView;
    ProgressBar myProgressBar;


    private void populateGridView(List<Get_Set> get_setList) {
        mGridView = findViewById(R.id.mGridView);
        adapter = new GridViewAdapter(this, get_setList);
        mGridView.setAdapter(adapter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ProgressBar myProgressBar = findViewById(R.id.myProgressBar);
        myProgressBar.setIndeterminate(true);
        myProgressBar.setVisibility(View.VISIBLE);

        /*Create handle for the RetrofitInstance interface*/
        MyAPIService myAPIService = RetrofitClientInstance.getRetrofitInstance().create(MyAPIService.class);

        Call<List<Get_Set>> call = myAPIService.getGetSetValue();
        call.enqueue(new Callback<List<Get_Set>>() {

            @Override
            public void onResponse(Call<List<Get_Set>> call, Response<List<Get_Set>> response) {
                myProgressBar.setVisibility(View.GONE);
                Log.e("response", new Gson().toJson(response.body()));
                populateGridView(response.body());
            }

            @Override
            public void onFailure(Call<List<Get_Set>> call, Throwable throwable) {
                myProgressBar.setVisibility(View.GONE);
                Log.e("error", throwable.getMessage());
                Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }
}