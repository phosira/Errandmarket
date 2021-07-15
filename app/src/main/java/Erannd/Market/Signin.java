package Erannd.Market;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class Signin extends AppCompatActivity {
    private EditText type_pn;
    private EditText recieve_no;
    private Button recieve_no_bt;
    private Button recieve_no_check;
    private TextView find_by_email;
    private TextView prohibit;
    private String checkid;
    private Handler handler = new Handler();
    int time;
    String certify_no_check;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        type_pn = findViewById(R.id.type_pn);
        recieve_no = findViewById(R.id.recieve_no);
        recieve_no_bt = findViewById(R.id.recieve_no_bt);
        recieve_no_check = findViewById(R.id.recieve_no_check);
        find_by_email = findViewById(R.id.find_by_email);
        prohibit = findViewById(R.id.prohibit);
        recieve_no_check.setVisibility(View.GONE);
        recieve_no.setVisibility(View.GONE);
        prohibit.setVisibility(View.GONE);

        type_pn.addTextChangedListener(new PhoneNumberFormattingTextWatcher());


        recieve_no_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String phone = type_pn.getText().toString();
                String phonenumber ="+82"+phone.replaceFirst("0","");
                phonenumber = phonenumber.replace("-","");
                if(phonenumber.length()<11){
                    Toast.makeText(getApplicationContext(),"전화번호가 잘못됐어요. 다시 입력해주세요",Toast.LENGTH_SHORT).show();
                }else{
                    send_certifi_no(phonenumber);


                }

            }
        });


        find_by_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signin.this,FindPhoneNumber.class);
                startActivity(intent);
            }
        });

        recieve_no_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = recieve_no.getText().toString().trim();
                String phone = type_pn.getText().toString();
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

                    check_certifi_no(phoneno,code,certify_no_check);

                }
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
                            recieve_no_bt.setText("인증문자 다시 받기 (" + minute + "분" + " " + second + "초)");
                            if (time == 0) {
                                recieve_no_bt.setText("인증문자 받기");
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
    public void check_certifi_no(String phoneno,String CertifyNo,String certify_no_check){

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
                            Intent intent = new Intent(Signin.this, SetUserinfo.class);
                            startActivity(intent);
                            finish();
                        }else{

                            String nick = jsonResponse.getString("nick");
                            String email = jsonResponse.getString("email");
                            if(email.equals("null")){email = "";}
                            String image = jsonResponse.getString("image");
                            if(image.equals("null")){image = "";}
                            SharedPreferences sharedPreferences= getSharedPreferences("user",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("pn",phoneno);
                            editor.putString("nick",nick);
                            editor.putString("email",email);
                            editor.putString("image",image);
                            editor.apply();
                            Intent intent = new Intent(Signin.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    else{
                        Toast.makeText(Signin.this, "인증번호를 확인해주세요!", Toast.LENGTH_SHORT).show();
                    }
                }  catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ValidateRequest validateRequest=new ValidateRequest(phoneno,CertifyNo,certify_no_check,responseListener);
        RequestQueue queue= Volley.newRequestQueue(Signin.this);
        queue.add(validateRequest);
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
                    recieve_no.setVisibility(View.VISIBLE);
                    recieve_no_check.setVisibility(View.VISIBLE);
                    prohibit.setVisibility(View.VISIBLE);
                    find_by_email.setVisibility(View.GONE);
                    Thread timer = new timer();
                    timer.start();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ValidatePhoneNumber validatePhoneNumber=new ValidatePhoneNumber(phonenumber,responseListener);
        RequestQueue queue= Volley.newRequestQueue(Signin.this);
        queue.add(validatePhoneNumber);
    }
    @Override
    public void onBackPressed() {
        SharedPreferences sharedPreferences= getSharedPreferences("user",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("pn");
        editor.apply();
        finish();
    }

}