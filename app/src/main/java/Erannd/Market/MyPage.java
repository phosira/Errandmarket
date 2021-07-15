package Erannd.Market;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class MyPage extends Fragment {

    View view;
    Button profile;
    Button logout_bt;
    Button delete_id;
    Button shared_bt;
    TextView nickname;
    TextView email_text;
    String pn;
    String nick;
    String email;
    String image;
    ImageView profile_image_set;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    Button gps_bt;
    private GpsTracker gpsTracker;
    TextView location_text;

    @Override
    public void onResume() {
        super.onResume();
        Refresh();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_my_page, container, false);

        profile = view.findViewById(R.id.profile_button);
        nickname = view.findViewById(R.id.login);
        logout_bt = view.findViewById(R.id.logout_bt);
        delete_id = view.findViewById(R.id.delete_id);
        shared_bt = view.findViewById(R.id.shared_bt);
        email_text = view.findViewById(R.id.email);
        profile_image_set = view.findViewById(R.id.profile_image);
        gps_bt = view.findViewById(R.id.gps_bt);
        location_text = view.findViewById(R.id.location_text);

        SharedPreferences sharedPreferences= getActivity().getSharedPreferences("user",MODE_PRIVATE);
        nick = sharedPreferences.getString("nick","");//불러오기
        pn = sharedPreferences.getString("pn","");
        email = sharedPreferences.getString("email","");
        image = sharedPreferences.getString("image","");
        nickname.setText(nick);
        if(TextUtils.isEmpty(email)) {
            email_text.setText("프로필 설정에서 이메일을 등록해주세요!\n핸드폰번호가 변경된 경우 필요합니다!");
        }else{
            email_text.setText(email);
        }
        if(!image.equals("")){Bitmap profile_image = StringToBitMap(image);
            Glide.with(getActivity()).asBitmap().load(profile_image).override(512,512).apply(new RequestOptions().circleCrop()).into(profile_image_set);}
        else{
            profile_image_set.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24);
        }

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),UserProfile.class);
                startActivity(intent);
            }
        });


        gps_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkLocationServicesStatus()) {

                    showDialogForLocationServiceSetting();
                }else {

                    checkRunTimePermission();
                }

                        gpsTracker = new GpsTracker(getContext());

                        double latitude = gpsTracker.getLatitude();
                        double longitude = gpsTracker.getLongitude();

                        String save_latitude = String.valueOf(latitude);
                        String save_longitude = String.valueOf(longitude);

                        String address = getCurrentAddress(latitude, longitude);
                        address = address.replaceFirst("대한민국","");
                        location_text.setText(address);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("location",address);
                        editor.putString("latitude",save_latitude);
                        editor.putString("longitude",save_longitude);
                        editor.apply();

                        save_gps(address,pn,save_latitude,save_longitude);
            }
        });

        shared_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/html");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, "심부름부터 동네 정보까지, 이웃과 함께 해요.");
                startActivity(Intent.createChooser(sharingIntent,"Share using text"));
            }
        });


        logout_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              SharedPreferences sharedPreferences= getActivity().getSharedPreferences("user",MODE_PRIVATE);
              SharedPreferences.Editor editor = sharedPreferences.edit();
              editor.clear();
              editor.apply();
                getActivity().finish();
            Intent intent = new Intent(getContext(),Signin.class);
            startActivity(intent);

            }
        });


        delete_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());// Builder 호출
                builder.setTitle("탈퇴");
                builder.setMessage("탈퇴하면 모든 개인 정보가 즉각 삭제되어요." +
                        "정말 탈퇴하시겠어요?");
                builder.setPositiveButton("탈퇴하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setDelete_id(pn);
                        //탈퇴하기
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();//dialog 종료
                    }
                });

                AlertDialog dialog = builder.create(); // dialog 생성
                dialog.show(); // dialog 실행

            }
        });
        return view;
    }

    public void setDelete_id(String phoneno){

        Response.Listener<String> responseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse=new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");
                    if(success){
                        Toast.makeText(getContext(), "탈퇴하였습니다", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences= getActivity().getSharedPreferences("user",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        Intent intent = new Intent(getActivity(), Signin.class);
                        startActivity(intent);
                        getActivity().finish();
                        }else{
                        Toast.makeText(getContext(), "오류로 인한 탈퇴실패!", Toast.LENGTH_SHORT).show();
                        }

                }  catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        DeleteID deleteID=new DeleteID(phoneno,responseListener);
        RequestQueue queue= Volley.newRequestQueue(getActivity());
        queue.add(deleteID);
    }
    public void Refresh(){
        SharedPreferences sharedPreferences= getActivity().getSharedPreferences("user",MODE_PRIVATE);
        nick = sharedPreferences.getString("nick","");//불러오기
        pn = sharedPreferences.getString("pn","");
        email = sharedPreferences.getString("email","");
        image = sharedPreferences.getString("image","");
        String gps = sharedPreferences.getString("location","");
        nickname.setText(nick);
        location_text.setText(gps);
        if(TextUtils.isEmpty(email)) {
            email_text.setText("프로필 설정에서 이메일을 등록해주세요!\n핸드폰번호가 변경된 경우 필요합니다!");
        }else{
            email_text.setText(email);
        }
        if(!TextUtils.isEmpty(image)){Bitmap profile_image = StringToBitMap(image);
            Glide.with(getActivity()).asBitmap().load(profile_image).override(512,512).apply(new RequestOptions().circleCrop()).into(profile_image_set);}
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
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                //위치 값을 가져올 수 있음

            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(getContext(), "위치 권한 설정을 거부하셨습니다.\n[권한] 설정에서 위치권한을 허용해야 합니다.", Toast.LENGTH_LONG).show();
                    return;

                }else {

                    new AlertDialog.Builder(getContext())// Builder 호출
                            .setMessage("위치 권한이 꺼져있습니다.\n[권한] 설정에서 위치권한을 허용해야 합니다.")
                            .setPositiveButton("설정으로가기", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                                    startActivity(intent);

                                    //어플 설정으로 이동
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                    //dialog.dismiss();//dialog 종료
                                }

                            })
                            .setCancelable(false)
                            .create()
                            .show();


                }
            }

        }
    }

    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음



        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])) {



                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(getContext(), "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    10);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(getContext(), "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(getContext(), "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }



        if (addresses == null || addresses.size() == 0) {
           // Toast.makeText(getContext(), "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }




        Address address = addresses.get(2);
        System.out.println(address);

        return address.getAddressLine(0);

    }


    //여기부터는 GPS 활성화를 위한 메소드들
    public void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void save_gps (String gps,String pn,String save_latitude,String save_longitude){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url ="https://phosira.com/SaveGPS.php";


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse=new JSONObject(response);
                            boolean success=jsonResponse.getBoolean("success");

                            if(success){
                                Toast.makeText(getContext(), "주소 저장 성공", Toast.LENGTH_SHORT).show();

                            }else{
                                Toast.makeText(getContext(), "주소 저장 실패", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "서버 통신 오류", Toast.LENGTH_SHORT).show();

            }

        }) {
            // 포스트 파라미터 넣기
            @Override
            protected Map getParams()
            {

                Map params = new HashMap();
                params.put("GPS", gps);
                params.put("UserID", pn);
                params.put("latitude",save_latitude);
                params.put("longitude",save_longitude);
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
} 