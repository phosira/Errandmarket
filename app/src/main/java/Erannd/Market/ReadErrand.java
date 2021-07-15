package Erannd.Market;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static Erannd.Market.MyPage.StringToBitMap;

public class ReadErrand extends AppCompatActivity {
Intent intent;
    String my_latitude;
    String my_longitude;
    String check_distance;
    ImageView read_errand_pic;
    ImageButton read_errand_menu;
    ImageView read_errand_profile_pic;
    TextView read_errand_nickname;
    TextView read_errand_village;
    TextView read_errand_title;
    TextView read_errand_category;
    TextView read_errand_distance;
    TextView read_errand_time;
    TextView read_errand_content;
    EditText read_errand_price;
    TextView read_errand_emergency;
    TextView errand_read_won;
    TextView read_errand_emergency_time;
    String idx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_errand);

        intent = getIntent();
        idx = intent.getStringExtra("idx");
        read_errand_pic = findViewById(R.id.read_errand_pic);
        read_errand_menu  =findViewById(R.id.read_errand_menu);
        read_errand_profile_pic= findViewById(R.id.read_errand_profile_pic);
        read_errand_nickname = findViewById(R.id.read_errand_nickname);
        read_errand_village = findViewById(R.id.read_errand_village);
        read_errand_title = findViewById(R.id.read_errand_title);
        read_errand_category = findViewById(R.id.read_errand_category);
        read_errand_distance = findViewById(R.id.read_errand_distance);
        read_errand_time = findViewById(R.id.read_errand_time);
        read_errand_content = findViewById(R.id.read_errand_content);
        read_errand_price = findViewById(R.id.read_errand_price);
        read_errand_emergency = findViewById(R.id.read_errand_emergency);
        read_errand_emergency_time = findViewById(R.id.read_errand_emergency_time);
        read_errand_content.setFocusable(false);
        read_errand_content.setClickable(false);
        read_errand_price.addTextChangedListener(new NumberTextWatcher(read_errand_price));
        errand_read_won = findViewById(R.id.errand_read_won);


        read_errand_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"수정","삭제"};
                AlertDialog.Builder selectDialog = new AlertDialog.Builder(ReadErrand.this);
                selectDialog.setItems(items, new DialogInterface.OnClickListener() {
                    //dialog : 사용자가 보고있는 dialog
                    // which : 사용자가 클릭한 목록의 index값
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        switch (index) {
                            //index에 따라 실행되어야 하는 기능을 이곳에서 작성합니다.
                            case 0:
                                Intent intent1 = new Intent(ReadErrand.this,EditErrand.class);
                                intent1.putExtra("emergency",read_errand_emergency.getText().toString());
                                intent1.putExtra("title",read_errand_title.getText().toString());
                                intent1.putExtra("emergency_time",read_errand_emergency_time.getText().toString());
                                intent1.putExtra("category",read_errand_category.getText().toString());
                                intent1.putExtra("price",read_errand_price.getText().toString());
                                intent1.putExtra("content",read_errand_content.getText().toString());
                                intent1.putExtra("idx",idx);
                                startActivity(intent1);
                                break;
                            case 1:
                                new AlertDialog.Builder(ReadErrand.this)// Builder 호출
                                        .setMessage("게시글을 정말 삭제하시겠어요?")
                                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                              remove_errand(idx);
                                              finish();
                                                //어플 설정으로 이동
                                            }
                                        })
                                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                dialog.dismiss();//dialog 종료
                                            }

                                        })
                                        .create()
                                        .show();

                                break;

                        }
                    }
                });
                selectDialog.show();

            }

        });
    }

    public void errand_read(String idx){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://phosira.com/ErrandRead.php";


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse=new JSONObject(response);
                                String emergency = jsonResponse.getString("emergency");
                                String emergency_time = jsonResponse.getString("emergency_time");
                                String title = jsonResponse.getString("title");
                                String price = jsonResponse.getString("price");
                                String village = jsonResponse.getString("village");
                                String latitude = jsonResponse.getString("latitude");
                                String longitude = jsonResponse.getString("longitude");
                                double lati = Double.parseDouble(latitude);
                                double longi = Double.parseDouble(longitude);
                                double my_lati = Double.parseDouble(my_latitude);
                                double my_long = Double.parseDouble(my_longitude);
                                get_distance(lati,longi,my_lati,my_long);
                                String upload_time = jsonResponse.getString("upload_time");
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
                                String strDate = upload_time;
                                Date date = dateFormat.parse(strDate);
                                long check_date = date.getTime();
                                String time = formatTimeString(check_date);
                                Log.v("시간확인",date.getTime()+"");
                             //   String emergency_time = jsonResponse.getString("emergency_time");
                                String nickname = jsonResponse.getString("nickname");
                                String content = jsonResponse.getString("content");
                                String category = jsonResponse.getString("category");
                                String account_image = jsonResponse.getString("profile_image");
                                String image = jsonResponse.getString("image");
                                if (image.equals("null")){
                                    read_errand_pic.setVisibility(View.GONE);
                                }
                            if(!account_image.equals("")){
                                Bitmap profile_image = StringToBitMap(account_image);
                                Glide.with(ReadErrand.this).asBitmap().load(profile_image).override(512,512).apply(new RequestOptions().circleCrop()).into(read_errand_profile_pic);}
                            else{
                                read_errand_profile_pic.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24);
                            }
                            read_errand_nickname.setText(nickname);
                            read_errand_village.setText(village);
                            read_errand_title.setText(title);
                            read_errand_category.setText(category);
                            read_errand_distance.setText(check_distance);
                            read_errand_time.setText(time);
                            read_errand_content.setText(content);
                            if(price.equals("0")){
                                read_errand_price.setText("가격흥정");
                                errand_read_won.setVisibility(View.GONE);
                            }else{
                                read_errand_price.setText(price);
                            }
                            read_errand_emergency.setText(emergency);
                            if(!emergency_time.equals("시간 설정")){
                                read_errand_emergency_time.setText(emergency_time);
                            }else{
                                read_errand_emergency_time.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ReadErrand.this, "에러", Toast.LENGTH_SHORT).show();

            }

        }) {
            // 포스트 파라미터 넣기
            @Override
            protected Map getParams()
            {

                Map params = new HashMap();
                params.put("idx", idx);
                return params;
            }

        };
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
        if (diffTime < ReadErrand.TIME_MAXIMUM.SEC) {
            msg = "방금 전";
        } else if ((diffTime /= ReadErrand.TIME_MAXIMUM.SEC) < ReadErrand.TIME_MAXIMUM.MIN) {
            msg = diffTime + "분 전";
        } else if ((diffTime /= ReadErrand.TIME_MAXIMUM.MIN) < ReadErrand.TIME_MAXIMUM.HOUR) {
            msg = (diffTime) + "시간 전";
        } else if ((diffTime /= ReadErrand.TIME_MAXIMUM.HOUR) < ReadErrand.TIME_MAXIMUM.DAY) {
            msg = (diffTime) + "일 전";
        } else if ((diffTime /= ReadErrand.TIME_MAXIMUM.DAY) < ReadErrand.TIME_MAXIMUM.MONTH) {
            msg = (diffTime) + "달 전";
        } else {
            msg = (diffTime) + "년 전";
        }
        return msg;
    }

    @Override
    protected void onResume() {
        super.onResume();
        errand_read(idx);
        SharedPreferences sharedPreferences= getSharedPreferences("user",MODE_PRIVATE);
        my_latitude = sharedPreferences.getString("latitude","");//불러오기
        my_longitude = sharedPreferences.getString("longitude","");
    }
    public void remove_errand(String idx){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://phosira.com/RemoveErrand.php";


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse=new JSONObject(response);
                           boolean success = jsonResponse.getBoolean("success");
                           if(success){
                               Toast.makeText(ReadErrand.this, "데이터 삭제 성공", Toast.LENGTH_SHORT).show();
                           }else{
                               Toast.makeText(ReadErrand.this, "데이터 삭제 실패", Toast.LENGTH_SHORT).show();
                           }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ReadErrand.this, "에러", Toast.LENGTH_SHORT).show();

            }

        }) {
            // 포스트 파라미터 넣기
            @Override
            protected Map getParams()
            {

                Map params = new HashMap();
                params.put("idx", idx);
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }
}