package Erannd.Market;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditErrand extends AppCompatActivity {

    TextView write_errand_save;
    EditText write_errand_title;
    TextView write_errand_category;
    EditText write_errand_price;
    EditText write_errand_content;
    ImageButton write_errand_photo;
    TextView write_errand_photo_cnt;
    TextView write_errand_emergency;
    RecyclerView recyclerView;
    TextView write_errand_emergency_time;
    int price_check;
    Intent intent;
    String idx;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_errand);


        write_errand_save = findViewById(R.id.write_errand_save);
        write_errand_title = findViewById(R.id.write_errand_title);
        write_errand_category = findViewById(R.id.write_errand_category);
        write_errand_price = findViewById(R.id.write_errand_price);
        write_errand_content = findViewById(R.id.write_errand_content);
        write_errand_photo = findViewById(R.id.write_errand_photo);
        write_errand_photo_cnt = findViewById(R.id.write_errand_photo_cnt);
        recyclerView = findViewById(R.id.write_errand_recyclerView);
        write_errand_emergency = findViewById(R.id.write_errand_emergency);
        write_errand_emergency_time = findViewById(R.id.write_errand_emergency_time);



        //  write_errand_price.addTextChangedListener(new NumberTextWatcher(write_errand_price));
        intent = getIntent();
        String emergency = intent.getStringExtra("emergency");
        String title = intent.getStringExtra("title");
        String emergency_time = intent.getStringExtra("emergency_time");
        String category = intent.getStringExtra("category");
        String price = intent.getStringExtra("price");
        String content = intent.getStringExtra("content");
        idx = intent.getStringExtra("idx");
        write_errand_emergency_time.setVisibility(View.GONE);

        write_errand_emergency.setText(emergency);
        write_errand_title.setText(title);
        if(emergency.equals("긴급 심부름") ){
            write_errand_emergency_time.setVisibility(View.VISIBLE);
        write_errand_emergency_time.setText(emergency_time);}
        write_errand_category.setText(category);
        if(price.equals("가격흥정")){
            write_errand_price.setText("");
        }else{
        write_errand_price.setText(price);}
        write_errand_content.setText(content);

        write_errand_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences= getSharedPreferences("user",MODE_PRIVATE);
                String nickname = sharedPreferences.getString("nick","");//불러오기
                String latitude = sharedPreferences.getString("latitude","");;
                String longitude = sharedPreferences.getString("longitude","");;
                String village = sharedPreferences.getString("location","");
                String title = write_errand_title.getText().toString();
                String category = write_errand_category.getText().toString();
                String price = write_errand_price.getText().toString();
                String content = write_errand_content.getText().toString();
                String emergency = write_errand_emergency.getText().toString();
                String emergency_time = write_errand_emergency_time.getText().toString();
                if(price.isEmpty()){
                    price="0";
                }
                price_check = Integer.parseInt(price);
                if(TextUtils.equals(emergency,"긴급도")||TextUtils.isEmpty(title)||TextUtils.isEmpty(content)||TextUtils.equals(category,"카테고리")){
                    new AlertDialog.Builder(EditErrand.this)// Builder 호출
                            .setMessage("제목,중요도,카테고리,내용은 필수 입력 항목이에요!")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();
                }else{
                    if(TextUtils.equals(emergency,"긴급 심부름") ){
                        if(TextUtils.equals(emergency_time,"시간 설정") ) {
                            new AlertDialog.Builder(EditErrand.this)// Builder 호출
                                    .setMessage("긴급 심부름은 시간 설정이 필수입니다!")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                        if (TextUtils.isEmpty(price) || price_check < 50000) {
                            new AlertDialog.Builder(EditErrand.this)// Builder 호출
                                    .setMessage("긴급 심부름 최소 금액은 50,000원 이상입니다!")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .create()
                                    .show();
                        }else if(!TextUtils.equals(emergency_time,"시간 설정") && !TextUtils.isEmpty(price) && price_check >= 50000){
                            save_errand(emergency, nickname, title, category, price, village, content, latitude, longitude,emergency_time,idx);
                        }

                    }else  {
                        save_errand(emergency, nickname, title, category, price, village, content, latitude, longitude,emergency_time,idx);
                    }
                }
            }
            //   }

        });
        write_errand_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"배달•장보기","청소•집안일","설치•조립•운반","애완동물 돌봄","벌레•쥐 잡기","역할대행","운전•카풀","과외•알바","온라인","기타"};
                AlertDialog.Builder selectDialog = new AlertDialog.Builder(EditErrand.this);
                selectDialog.setItems(items, new DialogInterface.OnClickListener() {
                    //dialog : 사용자가 보고있는 dialog
                    // which : 사용자가 클릭한 목록의 index값
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        switch (index) {
                            //index에 따라 실행되어야 하는 기능을 이곳에서 작성합니다.
                            case 0:
                                write_errand_category.setText("배달•장보기");
                                break;
                            case 1:
                                write_errand_category.setText("청소•집안일");
                                break;
                            case 2:
                                write_errand_category.setText("설치•조립•운반");
                                break;
                            case 3:
                                write_errand_category.setText("애완동물 돌봄");
                                break;
                            case 4:
                                write_errand_category.setText("벌레•쥐 잡기");
                                break;
                            case 5:
                                write_errand_category.setText("역할대행");
                                break;
                            case 6:
                                write_errand_category.setText("운전•카풀");
                                break;
                            case 7:
                                write_errand_category.setText("과외•알바");
                                break;
                            case 8:
                                write_errand_category.setText("온라인");
                                break;
                            case 9:
                                write_errand_category.setText("기타");
                                break;
                        }
                    }
                });
                selectDialog.show();

            }
        });

        write_errand_emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"일반 심부름","긴급 심부름"};
                AlertDialog.Builder selectDialog = new AlertDialog.Builder(EditErrand.this);
                selectDialog.setItems(items, new DialogInterface.OnClickListener() {
                    //dialog : 사용자가 보고있는 dialog
                    // which : 사용자가 클릭한 목록의 index값
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        switch (index) {
                            //index에 따라 실행되어야 하는 기능을 이곳에서 작성합니다.
                            case 0:
                                write_errand_emergency.setText("일반 심부름");
                                write_errand_emergency_time.setText("시간 설정");
                                write_errand_emergency_time.setVisibility(View.GONE);
                                break;
                            case 1:
                                write_errand_emergency.setText("긴급 심부름");
                                write_errand_emergency_time.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                });
                selectDialog.show();

            }

        });
        write_errand_emergency.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String emergency = write_errand_emergency.getText().toString();
                if (TextUtils.equals(emergency,"긴급 심부름")) {
                    new AlertDialog.Builder(EditErrand.this)// Builder 호출
                            .setMessage("긴급 심부름은 최소 50,000원 이상 지불해야돼요!\n가격을 꼭 입력해 주세요.")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();

                }
            }
        });

        write_errand_emergency_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] items = {"30초","5분","10분","20분","30분","1시간","2시간","3시간","5시간"};
                AlertDialog.Builder selectDialog = new AlertDialog.Builder(EditErrand.this);
                selectDialog.setItems(items, new DialogInterface.OnClickListener() {
                    //dialog : 사용자가 보고있는 dialog
                    // which : 사용자가 클릭한 목록의 index값
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        switch (index) {
                            //index에 따라 실행되어야 하는 기능을 이곳에서 작성합니다.
                            case 0:
                                write_errand_emergency_time.setText("30초");
                                break;
                            case 1:
                                write_errand_emergency_time.setText("5분");
                                break;
                            case 2:
                                write_errand_emergency_time.setText("10분");
                                break;
                            case 3:
                                write_errand_emergency_time.setText("20분");
                                break;
                            case 4:
                                write_errand_emergency_time.setText("30분");
                                break;
                            case 5:
                                write_errand_emergency_time.setText("1시간");
                                break;
                            case 6:
                                write_errand_emergency_time.setText("2시간");
                                break;
                            case 7:
                                write_errand_emergency_time.setText("3시간");
                                break;
                            case 8:
                                write_errand_emergency_time.setText("5시간");
                                break;
                        }
                    }
                });
                selectDialog.show();

            }
        });
    }

    public void save_errand(String emergency,String nickname,String title,String category,String price,String village,String content, String latitude,String longitude,String emergency_time,String idx){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://phosira.com/EditErrand.php";


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse=new JSONObject(response);
                            boolean success=jsonResponse.getBoolean("success");
                            if(success){
                                Toast.makeText(EditErrand.this, "저장 성공.", Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                Toast.makeText(EditErrand.this, "저장 실패", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditErrand.this, "오류 발생", Toast.LENGTH_SHORT).show();

            }

        }) {
            // 포스트 파라미터 넣기
            @Override
            protected Map getParams()
            {
                Map params = new HashMap();
                params.put("emergency", emergency);
                params.put("nickname", nickname);
                params.put("title", title);
                params.put("category", category);
                params.put("price", price);
                params.put("village", village);
                params.put("latitude", latitude);
                params.put("longitude", longitude);
                params.put("content", content);
                params.put("emergency_time",emergency_time);
                params.put("idx",idx);

                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Add the request to the RequestQueue.
        queue.add(stringRequest);


    }
}