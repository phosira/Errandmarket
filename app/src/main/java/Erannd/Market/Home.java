package Erannd.Market;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static java.lang.Math.*;

public class Home extends Fragment {

    View view;
    FloatingActionButton write_errand_bt;
    RecyclerView recyclerView;
    HomeItemAdapter adapter;
    String my_latitude;
    String my_longitude;
    String check_distance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_home, container, false);

        write_errand_bt = view.findViewById(R.id.write_errand_bt);
        recyclerView = view.findViewById(R.id.home_recycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new HomeItemAdapter(getContext());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new HomeItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                HomeItems item = adapter.getItem(position);
                if(my_latitude.isEmpty() || my_longitude.isEmpty()){
                    Toast.makeText(getContext(), "위치설정을 하지않으면 사용하실 수 없습니다", Toast.LENGTH_SHORT).show();
                }else{
                Intent intent = new Intent(getActivity(),ReadErrand.class);
                intent.putExtra("idx",item.getHome_item_idx());
                startActivity(intent);}
            }
        });

        write_errand_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(my_latitude.isEmpty() || my_longitude.isEmpty()){
                    Toast.makeText(getContext(), "위치설정을 하지않으면 사용하실 수 없습니다", Toast.LENGTH_SHORT).show();
                }else{
                Intent intent = new Intent(getActivity(),WriteErrand.class);
                startActivity(intent);}
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.clear();
        get_home_data();
        adapter.notifyDataSetChanged();

        SharedPreferences sharedPreferences= getActivity().getSharedPreferences("user",MODE_PRIVATE);
        my_latitude = sharedPreferences.getString("latitude","");//불러오기
        my_longitude = sharedPreferences.getString("longitude","");
    }

    public void get_home_data(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url ="https://phosira.com/GetHomeData.php";


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse=new JSONObject(response);
                            JSONArray jsonArray = jsonResponse.getJSONArray("result");

                            for(int i=0;i<jsonArray.length();i++){

                                JSONObject item = jsonArray.getJSONObject(i);

                                String emergency = item.getString("emergency");
                                String title = item.getString("title");
                                String price = item.getString("price");
                                String village = item.getString("village");
                                String latitude = item.getString("latitude");
                                String longitude = item.getString("longitude");
                                String idx = item.getString("idx");

                                double lati = Double.parseDouble(latitude);
                                double longi = Double.parseDouble(longitude);
                                double my_lati = Double.parseDouble(my_latitude);
                               double my_long = Double.parseDouble(my_longitude);
                                get_distance(lati,longi,my_lati,my_long);


                                String upload_time = item.getString("upload_time");
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
                                String strDate = upload_time;
                                Date date = dateFormat.parse(strDate);
                                long check_date = date.getTime();
                                String time = formatTimeString(check_date);
                                Log.v("시간확인",date.getTime()+"");
                                String emergency_time = item.getString("emergency_time");
                                String image = item.getString("image");



                                Log.v("Test",title);
                                HomeItems homeItems = new HomeItems(emergency,title,image,village,check_distance,time,price,idx);

                                adapter.addItem(homeItems);
                                adapter.notifyDataSetChanged();
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "오류 발생", Toast.LENGTH_SHORT).show();

            }

        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    public void get_distance(double lati,double longi,double my_lati,double my_long){
        Location my_location = new Location("");
        my_location.setLatitude(my_lati);
        my_location.setLongitude(my_long);

        Location board_location = new Location("");
        board_location.setLatitude(lati);
        board_location.setLongitude(longi);

        float distance = my_location.distanceTo(board_location);
        int t_distance = Math.round(distance);
        if(distance>=1000){
            check_distance = String.valueOf(t_distance/1000);
            check_distance = check_distance+"km";
        }else if(distance<10){
            check_distance = "아주 가까워요!";
        }
        else{
            check_distance = String.valueOf(t_distance);
            check_distance = check_distance+"m";
        }

        Log.v("TAG","둘사이의 거리"+check_distance);
    }
    private static class TIME_MAXIMUM{
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }
    public static String formatTimeString(long regTime) {
        long curTime = System.currentTimeMillis();
        long diffTime = (curTime - regTime) / 1000;
        String msg = null;
        if (diffTime < TIME_MAXIMUM.SEC) {
            msg = "방금 전";
        } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
            msg = diffTime + "분 전";
        } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
            msg = (diffTime) + "시간 전";
        } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
            msg = (diffTime) + "일 전";
        } else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
            msg = (diffTime) + "달 전";
        } else {
            msg = (diffTime) + "년 전";
        }
        return msg;
    }

}