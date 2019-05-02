package com.example.user.senioritaandroid.Client.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.senioritaandroid.Service.ApiService;
import com.example.user.senioritaandroid.Client.Activity.CurrentOffersActivity;
import com.example.user.senioritaandroid.Client.Activity.DriverInfoActivity;
import com.example.user.senioritaandroid.Extra.Constant;
import com.example.user.senioritaandroid.Driver.Offer;
import com.example.user.senioritaandroid.R;
import com.example.user.senioritaandroid.User.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

public class OfferClientListAdapter extends ArrayAdapter<Offer> {

    private Context context;
    private int resource;

    public OfferClientListAdapter(Context context, int resource, List<Offer> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Long  id = getItem(position).getId();
        String pointA = getItem(position).getPointA();
        String pointB = getItem(position).getPointB();
        String price = getItem(position).getPrice();
        final User driver = getItem(position).getDriver();
        Offer offer = new Offer(id, pointA, pointB, price, driver);

        LayoutInflater inflater = LayoutInflater.from(this.context);
        convertView = inflater.inflate(this.resource, parent, false);

        TextView priceView = (TextView) convertView.findViewById(R.id.priceOffersClient);
        TextView pointAView = (TextView) convertView.findViewById(R.id.pointAOffersClient);
        TextView pointBView = (TextView) convertView.findViewById(R.id.pointBOffersClient);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageOffersClient);
        Button button = (Button) convertView.findViewById(R.id.buttonAcceptOffersClient);

        pointAView.setText(pointA);
        pointBView.setText(pointB);
        priceView.setText(price);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DriverInfoActivity.class);
                intent.putExtra("id", driver.getId());
                context.startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mview) {
                Interceptor interceptor = new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        if (chain.request().header("noToken") == "true") {
                            return chain.proceed(chain.request());
                        }
                        SharedPreferences preferences = context.getSharedPreferences("preferences", MODE_PRIVATE);
                        String token = preferences.getString("token","");
                        Log.v("Token", token);
                        okhttp3.Request newRequest = chain.request().newBuilder().addHeader("Authorization", "Bearer "+token).build();
                        return chain.proceed(newRequest);
                    }
                };
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.interceptors().add(interceptor);
                OkHttpClient client = builder.build();
                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Constant.SERVER)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .client(client)
                        .build();
                ApiService apiService = retrofit.create(ApiService.class);
                Single<String> result = apiService.acceptOffer(id);
                result.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<String>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                Log.v("disposable", d.toString());
                            }
                            @Override
                            public void onSuccess(String result) {
                                Log.d("successRequest", result);
                                if(context instanceof CurrentOffersActivity){
                                    ((CurrentOffersActivity) context).yourDesiredMethod();
                                }
                            }
                            @Override
                            public void onError(Throwable e) {
                                Log.e("error", e.toString());
                            }
                        });
            }
        });
        return convertView;
    }
}
