package Erannd.Market;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class SigninRequest extends StringRequest {

    final static  private String URL="https://phosira.com/Signin.php";
    private Map<String, String> map;
    //private Map<String, String>parameters;

    public SigninRequest(String UserID, String UserNickname, String UserEmail,Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("UserID", UserID);
        map.put("UserNickname", UserNickname);
        map.put("UserEmail", UserEmail);
    }

    @Override
    protected Map<String, String>getParams() throws AuthFailureError {
        return map;
    }

}
