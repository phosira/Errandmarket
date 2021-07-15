package Erannd.Market;

import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WriteErrand extends AppCompatActivity {

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
    private Uri mImageCaptureUri;
    Uri photoUri;
    ArrayList<Uri> uriList = new ArrayList<>();     // 이미지의 uri를 담을 ArrayList 객체
    MultiImageAdapter adapter;  // 리사이클러뷰에 적용시킬 어댑터


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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d("TAG", "권한 설정 완료");
            } else {
                Log.d("TAG", "권한 설정 요청");
                ActivityCompat.requestPermissions(WriteErrand.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        write_errand_emergency_time.setVisibility(View.GONE);
      //  write_errand_price.addTextChangedListener(new NumberTextWatcher(write_errand_price));

        write_errand_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] items = {"카메라로 촬영하기","앨범에서 가져오기"};
                AlertDialog.Builder selectDialog = new AlertDialog.Builder(WriteErrand.this);
                selectDialog.setItems(items, new DialogInterface.OnClickListener() {
                    //dialog : 사용자가 보고있는 dialog
                    // which : 사용자가 클릭한 목록의 index값
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        switch (index) {
                            //index에 따라 실행되어야 하는 기능을 이곳에서 작성합니다.
                            case 0:
                                take_picture();
                                break;
                            case 1:
                                 get_picture();
                                break;

                        }
                    }
                });
                selectDialog.show();


            }
        });


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
                    new AlertDialog.Builder(WriteErrand.this)// Builder 호출
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
                            new AlertDialog.Builder(WriteErrand.this)// Builder 호출
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
                                new AlertDialog.Builder(WriteErrand.this)// Builder 호출
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
                            save_errand(emergency, nickname, title, category, price, village, content, latitude, longitude,emergency_time);
                        }

                        }else  {
                        save_errand(emergency, nickname, title, category, price, village, content, latitude, longitude,emergency_time);
                           }
                    }
                    }
             //   }

        });
        write_errand_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"배달•장보기","청소•집안일","설치•조립•운반","애완동물 돌봄","벌레•쥐 잡기","역할대행","운전•카풀","과외•알바","온라인","기타"};
                AlertDialog.Builder selectDialog = new AlertDialog.Builder(WriteErrand.this);
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
                AlertDialog.Builder selectDialog = new AlertDialog.Builder(WriteErrand.this);
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
                    new AlertDialog.Builder(WriteErrand.this)// Builder 호출
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
                AlertDialog.Builder selectDialog = new AlertDialog.Builder(WriteErrand.this);
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

    public void save_errand(String emergency,String nickname,String title,String category,String price,String village,String content, String latitude,String longitude,String emergency_time){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://phosira.com/SaveErrand.php";


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse=new JSONObject(response);
                            boolean success=jsonResponse.getBoolean("success");
                            if(success){
                                Toast.makeText(WriteErrand.this, "저장 성공.", Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                Toast.makeText(WriteErrand.this, "저장 실패", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(WriteErrand.this, "오류 발생", Toast.LENGTH_SHORT).show();

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

        if(requestCode==3333){
            Toast.makeText(this, data.getData()+"", Toast.LENGTH_SHORT).show();
        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        Uri imageUri = getImageUri(getApplicationContext(),imageBitmap);
        Log.v("카메라 사진",extras+"");
            uriList.add(imageUri);
        }
        if(data == null){   // 어떤 이미지도 선택하지 않은 경우
            Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
        }
        else{   // 이미지를 하나라도 선택한 경우
            if(data.getClipData() == null){     // 이미지를 하나만 선택한 경우
                Log.e("single choice: ", String.valueOf(data.getData()));
                Uri imageUri = data.getData();
                uriList.add(imageUri);

                adapter = new MultiImageAdapter(uriList, getApplicationContext());
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
                Toast.makeText(this, adapter.getItemCount()+"", Toast.LENGTH_SHORT).show();
                //write_errand_photo_cnt.setText(adapter.getItemCount()+"/10");
                Log.v("이미지개수 체크",adapter.getItemCount()+"");
            }
            else{      // 이미지를 여러장 선택한 경우
                ClipData clipData = data.getClipData();
                Log.e("clipData", String.valueOf(clipData.getItemCount()));

                if(clipData.getItemCount() > 10){   // 선택한 이미지가 11장 이상인 경우
                    Toast.makeText(getApplicationContext(), "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                }
                else{   // 선택한 이미지가 1장 이상 10장 이하인 경우
                    Log.e("TAG", "multiple choice");

                    for (int i = 0; i < clipData.getItemCount(); i++){
                        Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                        try {
                            uriList.add(imageUri);  //uri를 list에 담는다.

                        } catch (Exception e) {
                            Log.e("TAG", "File select error", e);
                        }
                    }

                    adapter = new MultiImageAdapter(uriList, getApplicationContext());
                    recyclerView.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅
                    recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                    write_errand_photo_cnt.setText(adapter.getItemCount()+"/10");
                }
            }
        }
    }
public void take_picture(){
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
  //  File photoFile = createFIle();
  //  Uri providerFileUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName(), photoFile);
 //   intent.putExtra(MediaStore.EXTRA_OUTPUT, providerFileUri);
    startActivityForResult(intent, 3333);
}

    public void get_picture(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2222);
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}