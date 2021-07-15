package Erannd.Market;

import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class SetUserinfo extends AppCompatActivity {

    EditText type_nick;
    Button signin_bt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_userinfo);

        type_nick = findViewById(R.id.type_nick);
        signin_bt = findViewById(R.id.signin_in_bt);


        SharedPreferences sharedPreferences= getSharedPreferences("user",MODE_PRIVATE);
        String UserID = sharedPreferences.getString("pn","default");//불러오기

        signin_bt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String nickname = type_nick.getText().toString();
                if (!TextUtils.isEmpty(nickname)) {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success) {
                                    SharedPreferences sharedPreferences= getSharedPreferences("user",MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("nick",nickname);
                                    editor.apply();
                                    Toast.makeText(SetUserinfo.this, "회원가입이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SetUserinfo.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(SetUserinfo.this, "중복된 닉네임입니다. 다시 입력해주세요!", Toast.LENGTH_SHORT).show();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    ValidateNickname validateRequest = new ValidateNickname(UserID, nickname, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(SetUserinfo.this);
                    queue.add(validateRequest);
                }else{
                    Toast.makeText(SetUserinfo.this, "닉네임을 입력해주세요!", Toast.LENGTH_SHORT).show();
                }
            }


        });



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