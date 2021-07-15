package Erannd.Market;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ValidateNickname extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static  private String URL="https://phosira.com/Signin.php";
    private Map<String, String> map;

    public ValidateNickname(String UserID,String UserNick, Response.Listener<String> listener){
        super(Request.Method.POST, URL, listener,null);

        map = new HashMap<>();
        map.put("UserID", UserID);
        map.put("UserNick", UserNick);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}