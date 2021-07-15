package Erannd.Market;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePhoneNumber extends AppCompatActivity {

    EditText find_type_pn;
    EditText find_pn_type_certify;
    Button find_pn_certify_bt;
    Button find_pn_set_up;
    private Handler handler = new Handler();
    int time;
    String certify_no_check;
    String checkid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone_number);
        find_type_pn = findViewById(R.id.find_type_pn);
        find_pn_type_certify = findViewById(R.id.find_pn_type_certify);
        find_pn_certify_bt = findViewById(R.id.find_pn_certify_bt);
        find_pn_set_up = findViewById(R.id.find_pn_set_up);

        find_pn_type_certify.setVisibility(View.GONE);
        find_pn_set_up.setVisibility(View.GONE);
        find_type_pn.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        find_pn_certify_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = find_type_pn.getText().toString();
                String phonenumber ="+82"+phone.replaceFirst("0","");
                phonenumber = phonenumber.replace("-","");
                if(phonenumber.length()<11){
                    Toast.makeText(getApplicationContext(),"전화번호가 잘못됐어요. 다시 입력해주세요",Toast.LENGTH_SHORT).show();
                }else{
                    find_pn_type_certify.setVisibility(View.VISIBLE);
                    find_pn_set_up.setVisibility(View.VISIBLE);
                    Thread timer = new ChangePhoneNumber.timer();
                    timer.start();
                    send_certifi_no(phonenumber);
                }
            }
        });

        find_pn_set_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences= getSharedPreferences("find_pn_temp",MODE_PRIVATE);
                String code = find_pn_type_certify.getText().toString().trim();
                String phone = find_type_pn.getText().toString();
                String email = sharedPreferences.getString("email","");
                String phonenumber ="+82"+phone.replaceFirst("0","");
                phonenumber = phonenumber.replace("-","");
                if(!phonenumber.equals(checkid)){
                    Toast.makeText(getApplicationContext(),"전화번호가 잘못됐어요. 다시 입력해주세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(code)){
                    Toast.makeText(getApplicationContext(),"인증번호를 입력해주세요",Toast.LENGTH_SHORT).show();
                }else{
                    String phoneno = phonenumber.replace("+","");

                    check_certifi_no(phoneno,code,certify_no_check,email);}

            }
        });
    }

    class timer extends Thread {

        public void run() {

            for (time = 300; time >= 0; time--) {
                handler.post(new Runnable() {
                    int minute = time / 60;
                    int second = time % 60;

                    @Override
                    public void run() {
                        find_pn_certify_bt.setText("인증문자 다시 받기 (" + minute + "분" + " " + second + "초)");
                        if (time == 0) {
                            find_pn_certify_bt.setText("인증문자 받기");
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

    public void send_certifi_no(String phonenumber){

        Response.Listener<String> responseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String check = jsonObject.getString("UserID");
                    certify_no_check = jsonObject.getString("certify_no_check");
                    checkid = check;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ChangePhoneNumValidate changePhoneNumber=new ChangePhoneNumValidate(phonenumber,responseListener);
        RequestQueue queue= Volley.newRequestQueue(ChangePhoneNumber.this);
        queue.add(changePhoneNumber);
    }

    public void check_certifi_no(String phoneno,String CertifyNo,String certify_no_check,String Email){

        Response.Listener<String> responseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse=new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");

                    if(certify_no_check.equals(CertifyNo)){

                        if(success){

                            SharedPreferences sharedPreferences= getSharedPreferences("user",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("pn",phoneno);
                            editor.apply();
                            Intent intent = new Intent(ChangePhoneNumber.this, SetUserinfo.class);
                            startActivity(intent);
                            finish();
                        }else{

                            String nick = jsonResponse.getString("nick");
                            String email = jsonResponse.getString("email");
                            SharedPreferences sharedPreferences= getSharedPreferences("user",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("pn",phoneno);
                            editor.putString("nick",nick);
                            editor.putString("email",email);
                            editor.apply();
                            Intent intent = new Intent(ChangePhoneNumber.this,Enterance.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    else{
                        Toast.makeText(ChangePhoneNumber.this, "인증번호를 확인해주세요!", Toast.LENGTH_SHORT).show();
                    }
                }  catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        SaveEditInfo saveEditInfo=new SaveEditInfo(phoneno,CertifyNo,certify_no_check,Email,responseListener);
        RequestQueue queue= Volley.newRequestQueue(ChangePhoneNumber.this);
        queue.add(saveEditInfo);
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