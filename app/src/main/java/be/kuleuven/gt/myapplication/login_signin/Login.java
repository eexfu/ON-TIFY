package be.kuleuven.gt.myapplication.login_signin;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import be.kuleuven.gt.myapplication.main.MainActivity;
import be.kuleuven.gt.myapplication.R;
import be.kuleuven.gt.myapplication.model.Account;

public class Login extends AppCompatActivity {
    private SharedPreferences accountid_sp;
    private SharedPreferences firstIn_sp;
    private SharedPreferences fromSignIn_sp;
    private SharedPreferences auto_login_sp;
    private boolean isFirstIn;
    private String input_username;
    private String input_psw;
    private EditText edt_username;
    private EditText edt_password;
    private CheckBox cb_auto;
    private boolean isAutoLogin;
    private String url_get_account="https://studev.groept.be/api/a22pt407/getAccountByUsername";
    private Account userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initialize
        edt_username = findViewById(R.id.sign_in_txtv_user);
        edt_password = findViewById(R.id.sign_in_txtv_password);
        cb_auto = findViewById(R.id.login_cb);
        Button bt_login = findViewById(R.id.sign_in_btn_sign_in);

        firstIn_sp = getSharedPreferences("firstIn_spf", MODE_PRIVATE);
        fromSignIn_sp = getSharedPreferences("fromSignIn",MODE_PRIVATE);
        auto_login_sp = getSharedPreferences("auto_login", MODE_PRIVATE);

        //A set of booleans who check the state of the app. First time ever? Sing in page until an account is made or a login happens, afterwards always login at start.
        isAutoLogin = auto_login_sp.getBoolean("isAutoLogin", false);
        isFirstIn = firstIn_sp.getBoolean("isFirstIn", true);
        boolean comeFromSignIn = fromSignIn_sp.getBoolean("fromSignIn",false);

        if(comeFromSignIn){
            SharedPreferences.Editor editor = fromSignIn_sp.edit();
            editor.putBoolean("fromSignIn",false);
            editor.commit();
        }
        if ( isFirstIn && !comeFromSignIn ) {//Determine if the program is running for the first time. If it is, skip to the Sign In. Otherwise, go to the login
            Intent intent = new Intent(Login.this, SignIn.class);
            startActivity(intent);
            Login.this.finish();
        }

        //If automatic login is true, enter MainActivity directly
        if (isAutoLogin) {
            cb_auto.setChecked(true);
            Intent intent=new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            bt_login.setOnClickListener(v -> {
                //Determine whether the automatic login checkbox is selected, and if selected, set isAutoLogin to true
                if (cb_auto.isChecked()) {
                    SharedPreferences.Editor edit=auto_login_sp.edit();
                    edit.putBoolean("isAutoLogin", true);
                    edit.commit();
                }
                else {
                    SharedPreferences.Editor edit=auto_login_sp.edit();
                    edit.putBoolean("isAutoLogin", false);
                    edit.commit();
                }

                input_username = edt_username.getText().toString();
                input_psw = edt_password.getText().toString();

                // Check for valid account as well as for outer spaces (which are not allowed in the first place)
                if(input_username.trim().equals("") || input_psw.trim().equals("") || input_username.toLowerCase().contains(" ") || input_psw.toLowerCase().contains(" ")) {
                    Toast.makeText(Login.this, "Username or password cannot be empty or contain a space", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Obtain the username and password from the saved file
                getUserInfo(url_get_account+"/"+input_username);
            });
        }
    }

    private void getUserInfo(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> checkAccount(response),
                error -> {
                    Toast.makeText(Login.this,"Unable to communicate wit the server",Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        );
        requestQueue.add(queueRequest);
    }

    private void checkAccount(JSONArray response){
        for(int i=0;i<response.length();i++){
            try {
                JSONObject object = response.getJSONObject(i);

                accountid_sp=getSharedPreferences("accountid",MODE_PRIVATE);
                SharedPreferences.Editor editor = accountid_sp.edit();
                editor.putInt("accountid",object.getInt("id"));
                editor.commit();

                userInfo = new Account(object.getString("username"),object.getString("password"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(response.length()!=0){
            String savedUsername = userInfo.getUsername();
            String savedPassword = userInfo.getPassword();

            //Check if the entered password and name match
            if(input_username.trim().equals(savedUsername) && input_psw.trim().equals(savedPassword)) {
                Toast.makeText(Login.this, "Successful Login!", Toast.LENGTH_SHORT).show();
                //set firstIn
                if(isFirstIn){
                    SharedPreferences.Editor editor = firstIn_sp.edit();
                    editor.putBoolean("isFirstIn", false);
                    editor.commit();
                }
                //if success, logged in and entered the MainActivity
                Intent intent=new Intent(Login.this,MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                //if failed
                Toast.makeText(Login.this, "The Username or Password is incorrect. Please try again or consider Singing In", Toast.LENGTH_SHORT).show();
            }
        }
        else {}
    }

    public void onClick_SignIn(View Caller){
        Intent intent =new Intent(this,SignIn.class);
        startActivity(intent);
        this.finish();
    }
}