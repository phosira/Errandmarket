package Erannd.Market;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Handler;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
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

public class Test extends AppCompatActivity {
    private EditText type_pn;
    private EditText recieve_no;
    private Button recieve_no_bt;
    private Button recieve_no_check;
    private TextView find_by_email;
    private TextView prohibit;
    private Handler handler = new Handler();
    int time;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private static final String TAG = "MAIN_TAG";
    private FirebaseAuth firebaseAuth;
    private boolean validate=false;

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

        firebaseAuth = FirebaseAuth.getInstance();

        //Intent intent = new Intent(Signin.this,Test.class);
        //startActivity(intent);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signinwithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken  token) {
                super.onCodeSent(verificationId, forceResendingToken);
                Log.d(TAG, "onCodeSent: "+verificationId);

                mVerificationId = verificationId;
                forceResendingToken = token;
            }
        };
        type_pn.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        recieve_no_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String phone = "+82"+type_pn.getText().toString().trim();
                if(phone.length()<14){
                    Toast.makeText(getApplicationContext(),"전화번호가 잘못됐어요. 다시 입력해주세요",Toast.LENGTH_SHORT).show();
                }else{
                    recieve_no.setVisibility(View.VISIBLE);
                    recieve_no_check.setVisibility(View.VISIBLE);
                    prohibit.setVisibility(View.VISIBLE);
                    find_by_email.setVisibility(View.GONE);
                    Thread timer = new timer();
                    timer.start();
                    startPhoneNumberVerification(phone);


                }

            }
        });



        recieve_no_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = recieve_no.getText().toString().trim();
                if(TextUtils.isEmpty(code)){
                    Toast.makeText(getApplicationContext(),"인증번호를 입력해주세요",Toast.LENGTH_SHORT).show();
                }else{
                    verifyPhoneNumberwithCode(mVerificationId, code);
                    String UserID= firebaseAuth.getCurrentUser().getPhoneNumber();
                    UserID = UserID.replace("+","");
                    check_pn(UserID);
                }
            }
        });


    }

    private void startPhoneNumberVerification(String phone) {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(120L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyPhoneNumberwithCode(String mVerificationId, String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signinwithPhoneAuthCredential(credential);


    }


    private void signinwithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                if(validate=true){
                    Intent intent = new Intent(Test.this, SetUserinfo.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(Test.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"인증번호를 다시 확인해주세요.",Toast.LENGTH_SHORT).show();
                return;
            }
        });


    }
    class timer extends Thread {

        public void run() {

            for (time = 120; time >= 0; time--) {
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
    public void check_pn(String UserID){

        Response.Listener<String> responseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse=new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");
                    if(success){

                        validate=true;
                    }
                    else{

                        validate=false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        //ValidateRequest validateRequest=new ValidateRequest(UserID,responseListener);
        //RequestQueue queue= Volley.newRequestQueue(Test.this);
        //queue.add(validateRequest);
    }
}