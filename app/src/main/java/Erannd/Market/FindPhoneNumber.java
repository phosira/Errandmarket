package Erannd.Market;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FindPhoneNumber extends AppCompatActivity {

    EditText find_type_old_pn;
    EditText find_pn_type_email;
    EditText find_pn_certify;
    Button find_pn_send_certify;
    Button find_pn_next_bt;
    TextView no_email;
    int time;
    boolean stop;
    private Handler handler = new Handler();
    String check_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_phone_number);

        find_type_old_pn = findViewById(R.id.find_type_old_pn);
        find_pn_type_email = findViewById(R.id.find_pn_type_email);
        find_pn_next_bt = findViewById(R.id.find_pn_next_bt);
        no_email = findViewById(R.id.no_email);
        find_pn_certify = findViewById(R.id.find_pn_certify);
        find_pn_send_certify = findViewById(R.id.find_pn_send_certify);

        find_type_old_pn.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        find_pn_certify.setVisibility(View.GONE);
        no_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:010-5594-0453")));

            }
        });

        find_pn_send_certify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = find_type_old_pn.getText().toString();
                String phonenumber ="82"+phone.replaceFirst("0","");
                String pn = phonenumber.replace("-","");
                String email = find_pn_type_email.getText().toString();
                if(TextUtils.isEmpty(pn)||TextUtils.isEmpty(email)){
                    Toast.makeText(FindPhoneNumber.this, "전화번호와 이메일을 모두 입력해주세요!", Toast.LENGTH_SHORT).show();
                }else{
                    SharedPreferences sharedPreferences = getSharedPreferences("find_pn_temp", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("pn",pn);
                    editor.putString("email",email);
                    editor.apply();
                    send_email(email,pn);

                }

            }
        });

        find_pn_next_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String certify_no = find_pn_certify.getText().toString();
                String phone = find_type_old_pn.getText().toString();
                String phonenumber ="82"+phone.replaceFirst("0","");
                String pn = phonenumber.replace("-","");
                String email = find_pn_type_email.getText().toString();
                SharedPreferences sharedPreferences= getSharedPreferences("find_pn_temp",MODE_PRIVATE);
                String pn_check= sharedPreferences.getString("pn","내용");//불러오기
                String email_check = sharedPreferences.getString("email","내용");

                if(TextUtils.isEmpty(email)||TextUtils.isEmpty(pn)){
                    Toast.makeText(FindPhoneNumber.this, "전화번호와 이메일을 입력해주세요!", Toast.LENGTH_SHORT).show();
                }
                if(pn.equals(pn_check) && email.equals(email_check)){

                    if(certify_no.equals(check_number)){
                        stop=true;
                        Intent intent = new Intent(FindPhoneNumber.this,ChangePhoneNumber.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(FindPhoneNumber.this, "인증번호가 틀립니다. 다시 확인해주세요!", Toast.LENGTH_SHORT).show();
                    }
                }else{

                    Toast.makeText(FindPhoneNumber.this, "전화번호와 이메일을 확인해주세요!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }



    public void send_email(String email,String pn){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://phosira.com/FindNumberSendEmail.php";


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse=new JSONObject(response);
                            boolean success=jsonResponse.getBoolean("success");
                            String number = jsonResponse.getString("number");
                            check_number = number;
                            if(success){
                                Toast.makeText(FindPhoneNumber.this, "인증메일을 전송했습니다.", Toast.LENGTH_SHORT).show();
                                Thread timer = new FindPhoneNumber.timer();
                                timer.start();
                                find_pn_certify.setVisibility(View.VISIBLE);
                            }else{
                                Toast.makeText(FindPhoneNumber.this, "핸드폰번호와 이메일이 일치하지않습니다.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FindPhoneNumber.this, "올바른 형식의 이메일을 입력해주세요!", Toast.LENGTH_SHORT).show();

            }

        }) {
            // 포스트 파라미터 넣기
            @Override
            protected Map getParams()
            {

                Map params = new HashMap();
                params.put("UserEmail", email);
                params.put("phonenum", pn);
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    class timer extends Thread {

        public void run() {



            for (time = 300; time >= 0; time--) {
                handler.post(new Runnable() {
                    final int minute = time / 60;
                    final int second = time % 60;

                    @Override
                    public void run() {
                        find_pn_send_certify.setText("다시받기(" + minute + "분" + " " + second + "초)");
                        if (time == 0) {
                            find_pn_send_certify.setText("인증 메일 받기");
                        }else if(stop){
                            find_pn_send_certify.setText("인증 메일 전송");
                        }
                    }
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("find_pn_temp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = getSharedPreferences("find_pn_temp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences sharedPreferences = getSharedPreferences("find_pn_temp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}