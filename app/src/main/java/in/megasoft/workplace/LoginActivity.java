package in.megasoft.workplace;

import static in.megasoft.workplace.userDetails.PublicURL;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText etusername, etuserpassword;
    private Button btnlogin, btncancel;
    private TextView tvregister, txterrormsg;
    private CheckBox cbrememberme;
    public String username, password, mId, stMassage;
    public static final String SHARED_PREFS = "sharedprefs";
    public static final String USERID = "userid";
    public static final String PASSWORD = "password";
    public static final String USER_NAME = "username";
    private final String loginurl = PublicURL +"login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etusername = findViewById(R.id.editUserText);
        etuserpassword = findViewById(R.id.editPasswordText);
        btnlogin = findViewById(R.id.btnLogin);
        btncancel = findViewById(R.id.btnCancle);
        tvregister = findViewById(R.id.tvRegister);
        cbrememberme = findViewById(R.id.checkboxRemember);
        autologin();
        if(!etusername.equals("") && !etuserpassword.equals("")){
            login();
        }
        tvregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cbrememberme.isChecked()){
                    savedata();
                    login();
                }else {
                    login();
                }
            }
        });
    }
    public void login(){
        username = etusername.getText().toString().trim();
        password = etuserpassword.getText().toString().trim();
        userDetails.UserName = username.toString();
        mId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (!username.equals("") && !password.equals("")) {
            in.megasoft.workplace.HttpsTrustManager.allowAllSSL();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, loginurl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("success")) {
                        Intent intent = new Intent(LoginActivity.this, LoadingActivity.class);
                        intent.putExtra(USER_NAME, username);
                        startActivity(intent);
                        finish();
                    } else if (response.equals("failure")) {
                    //    Toast.makeText(LoginActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                        stMassage = response.toString();
                        showAlertDialog();
                    }else{
                        stMassage = response.toString();
                        showAlertDialog();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(LoginActivity.this, error.toString().trim(), Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<>();
                    data.put("username", username);
                    data.put("password", password);
                    data.put("deviceid", mId);
                    return data;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        } else {
            Toast.makeText(LoginActivity.this, "Fields can not be empty", Toast.LENGTH_SHORT).show();
        }
    }
    public void savedata(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERID, etusername.getText().toString().trim());
        editor.putString(PASSWORD, etuserpassword.getText().toString().trim());
        editor.apply();
    }
    public void autologin(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        username = sharedPreferences.getString(USERID, "");
        password = sharedPreferences.getString(PASSWORD, "");
        etusername.setText(username);
        etuserpassword.setText(password);
    }
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Massage");
        builder.setMessage(stMassage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if("success".equals(stMassage)){
                    Intent intent = new Intent(LoginActivity.this, LoadingActivity.class);
                    intent.putExtra(USER_NAME, username);
                    startActivity(intent);
                    finish();
                }else{
                    dialog.dismiss();
                }
            }
        });
        builder.setCancelable(false);// Prevent dismissing the dialog by tapping outside

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}