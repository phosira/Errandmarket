package Erannd.Market;




import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Enterance extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterance);

        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 1000);

    }
    private class splashhandler implements Runnable{
        public void run(){
            SharedPreferences sharedPreferences= getSharedPreferences("user",MODE_PRIVATE);
            String id = sharedPreferences.getString("pn","default");//불러오기


           if(id.equals("default")) {

               startActivity(new Intent(getApplication(), Signin.class));
               Enterance.this.finish();
          }else{

               check_login(id);

           }
        }
    }

    public void check_login(String pn){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://phosira.com/CheckLogin.php";


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse=new JSONObject(response);
                            boolean success=jsonResponse.getBoolean("success");
                            String pn = jsonResponse.getString("pn");
                            String email = jsonResponse.getString("email");
                            String nickname = jsonResponse.getString("nickname");
                            String image = jsonResponse.getString("image");
                            if(email.equals("null")){email = "";}
                            if(image.equals("null")){image = "";}
                            if(success){
                                SharedPreferences sharedPreferences= getSharedPreferences("user",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("pn",pn);
                                editor.putString("nick",nickname);
                                editor.putString("email",email);
                                editor.putString("image",image);
                                editor.apply();
                                startActivity(new Intent(getApplication(), MainActivity.class));
                                Enterance.this.finish();
                            }else{
                                SharedPreferences sharedPreferences= getSharedPreferences("user",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.apply();
                                startActivity(new Intent(getApplication(), Signin.class));
                                Enterance.this.finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Enterance.this, "에러", Toast.LENGTH_SHORT).show();

            }

        }) {
            // 포스트 파라미터 넣기
            @Override
            protected Map getParams()
            {
                Map params = new HashMap();
                params.put("pn", pn);

                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Add the request to the RequestQueue.
        queue.add(stringRequest);


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }


}