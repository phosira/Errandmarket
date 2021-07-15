package Erannd.Market;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class UserProfile extends AppCompatActivity {

    ImageView profile_image_set;
    Button proifie_finish;
    Button email_certify_bt;
    Button send_certify_no;
    EditText nickname;
    EditText type_email;
    EditText type_certify_no;
    private Handler handler = new Handler();
    String check_number;
    private boolean stop;
    String pn;
    int time;
    private static final int ReQUEST_CODE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        nickname = findViewById(R.id.type_nickname);
        proifie_finish = findViewById(R.id.profile_finsh_bt);
        profile_image_set = findViewById(R.id.profile_image_set);
        email_certify_bt = findViewById(R.id.email_certify_bt);
        send_certify_no = findViewById(R.id.send_certify_no);
        type_email = findViewById(R.id.type_email);
        type_certify_no = findViewById(R.id.type_certify_no);


        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        String nick = sharedPreferences.getString("nick", "");//불러오기
        String email = sharedPreferences.getString("email","");
        pn = sharedPreferences.getString("pn","");
        String image =  sharedPreferences.getString("image","");//불러오기
        nickname.setText(nick);
        if(!image.equals("")){Bitmap profile_image = StringToBitMap(image);
        Glide.with(getApplicationContext()).asBitmap().load(profile_image).override(512,512).apply(new RequestOptions().circleCrop()).into(profile_image_set);}
        else{
            profile_image_set.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24);
        }
        if(!TextUtils.isEmpty(email)){
        type_email.setText(email);}
        type_certify_no.setVisibility(View.GONE);
        email_certify_bt.setVisibility(View.GONE);


            if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                Log.d("TAG","권한 설정 완료!");
            }else{
                Log.d("TAG","권한 설정 요청");
                ActivityCompat.requestPermissions(UserProfile.this, new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE},ReQUEST_CODE);
            }



        send_certify_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = type_email.getText().toString();
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(UserProfile.this, "이메일을 입력해주세요!", Toast.LENGTH_SHORT).show();
                }else{

                    send_email(email);
                    }
            }
        });

        email_certify_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String certify_no = type_certify_no.getText().toString();
                String email = type_email.getText().toString();
                String pn = sharedPreferences.getString("pn","");
                if(certify_no.equals(check_number)){
                    stop=true;
                    save_email(email,pn);
                    SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email",email);
                    editor.apply();
                    type_certify_no.setEnabled(false);
                    email_certify_bt.setEnabled(false);
                    type_email.setEnabled(false);
                    send_certify_no.setEnabled(false);

                }else{
                    Toast.makeText(UserProfile.this, "인증번호가 틀립니다. 다시 확인해주세요!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        proifie_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            SharedPreferences sharedPreferences= getSharedPreferences("profile",MODE_PRIVATE);
            String image = sharedPreferences.getString("image","");//불러오기
            String nick = nickname.getText().toString();
                if(TextUtils.isEmpty(nick)) {
                    Toast.makeText(UserProfile.this, "닉네임을 입력해주세요!", Toast.LENGTH_SHORT).show();
                }
                else{
                        set_profle(nick,pn,image);
                }

            }
        });
        profile_image_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, ReQUEST_CODE);
            }
        });


    }



  class timer extends Thread {

      public void run() {



              for (time = 300; time >= 0; time--) {
                  handler.post(new Runnable() {
                      final int minute = time / 60;
                      final int second = time % 60;

                      @Override
                      public void run() {
                          send_certify_no.setText("다시받기(" + minute + "분" + " " + second + "초)");
                          if (time == 0) {
                              send_certify_no.setText("인증메일 받기");
                          }else if(stop){
                              send_certify_no.setText("인증완료");
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

    public void send_email(String email){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://phosira.com/SendEmail.php";


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse=new JSONObject(response);
                            boolean success=jsonResponse.getBoolean("success");
                            String number = jsonResponse.getString("number");
                            if(success){
                                Toast.makeText(UserProfile.this, "인증메일을 전송했습니다.", Toast.LENGTH_SHORT).show();
                                check_number = number;
                                Thread timer = new UserProfile.timer();
                                timer.start();
                                type_certify_no.setVisibility(View.VISIBLE);
                                email_certify_bt.setVisibility(View.VISIBLE);
                            }else{
                                Toast.makeText(UserProfile.this, "이미 등록된 이메일입니다!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UserProfile.this, "에러", Toast.LENGTH_SHORT).show();

            }

        }) {
            // 포스트 파라미터 넣기
            @Override
            protected Map getParams()
            {

                Map params = new HashMap();
                params.put("UserEmail", email);
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    public void set_profle(String nickname,String phonenum, String image){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://phosira.com/SetProfile.php";


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse=new JSONObject(response);
                            boolean success=jsonResponse.getBoolean("success");
                            String nick = jsonResponse.getString("nick");
                            String image = jsonResponse.getString("image");
                            if(success){
                                SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                                SharedPreferences.Editor editor= sharedPreferences.edit();;
                                editor.putString("nick", nick);
                                editor.putString("image",image);
                                editor.apply();
                                finish();
                            }
                            else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);// Builder 호출
                                builder.setTitle("닉네임 중복");
                                builder.setMessage("중복된 닉네임이 있습니다" +
                                        "다시 입력해주세요");
                                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();//dialog 종료

                                    }
                                });
                                AlertDialog dialog = builder.create(); // dialog 생성
                                dialog.show(); // dialog 실행
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UserProfile.this, "에러", Toast.LENGTH_SHORT).show();

            }

        }) {
            // 포스트 파라미터 넣기
            @Override
            protected Map getParams()
            {
                Map params = new HashMap();
                params.put("Usernick", nickname);
                params.put("phonenum",phonenum);
                params.put("image",image);
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void save_email(String email,String pn){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://phosira.com/SaveEmail.php";


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse=new JSONObject(response);
                            boolean success=jsonResponse.getBoolean("success");
                            if(success){
                                Toast.makeText(UserProfile.this, "인증 완료.", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(UserProfile.this, "일시적인 오류로 전송에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UserProfile.this, "올바른 이메일을 입력해주세요!", Toast.LENGTH_SHORT).show();

            }

        }) {
            // 포스트 파라미터 넣기
            @Override
            protected Map getParams()
            {
                Map params = new HashMap();
                params.put("UserEmail", email);
                params.put("pn", pn);

                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Add the request to the RequestQueue.
        queue.add(stringRequest);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==ReQUEST_CODE){
                if(resultCode==RESULT_OK) {
                    Uri uri = data.getData();
                    Glide.with(getApplicationContext()).asBitmap().load(uri).apply(new RequestOptions().circleCrop()).into(profile_image_set);
                     try{
                          ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(),uri);
                        Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                          bitmap = resize(bitmap);
                          String image =bitmapToBase64(bitmap);
                         SharedPreferences sharedPreferences= getSharedPreferences("profile",MODE_PRIVATE);
                         SharedPreferences.Editor editor = sharedPreferences.edit();
                         editor.putString("image",image);
                         editor.apply();

                      }catch(Exception e){
                        e.printStackTrace();
                      }

                }
        }
    }// onActivityResult()..



    //비트맵 사이즈 변환
    private Bitmap resize(Bitmap bm) {
        bm = Bitmap.createScaledBitmap(bm, 512, 512, true);
        return bm;
    }


    //bitmap => Base64 변환
    public  String bitmapToBase64(Bitmap bitmap) {
        //2)비트맵 바이트 배열로 변환
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();

        //3)바이트 배열 base64로 변환
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
    //String(Base64) => bitmap 변환
    public static Bitmap StringToBitMap(String base64) {
        Log.e("StringToBitMap", "StringToBitMap");
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            //1)Base64 => byte[] 변환
            byte[] imageAsBytes = Base64.decode(base64.getBytes(), Base64.DEFAULT);
            opts.inJustDecodeBounds = false;
            //2)byte[] => bitmap 변환
            Bitmap bitmap_resize = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length, opts);

            return bitmap_resize;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

}
