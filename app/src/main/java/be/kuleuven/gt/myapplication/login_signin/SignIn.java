package be.kuleuven.gt.myapplication.login_signin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import be.kuleuven.gt.myapplication.R;
import be.kuleuven.gt.myapplication.model.Account;
import be.kuleuven.gt.myapplication.model.Group;

public class SignIn extends AppCompatActivity {
    private SharedPreferences firstIn_sp;
    private SharedPreferences fromSignIn_sp;
    private String username;
    private String password;
    private String password2;
    private String url_insert_account = "https://studev.groept.be/api/a22pt407/insertAccount";
    private String url_insert_group = "https://studev.groept.be/api/a22pt407/insertGroup";
    private Account account;
    private int id;
    private boolean unique;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //initialize
        firstIn_sp = getSharedPreferences("firstIn_spf", MODE_PRIVATE);
        fromSignIn_sp = getSharedPreferences("fromSignIn", MODE_PRIVATE);
    }

    public void onClick_Login(View v) {
        SharedPreferences.Editor editor = fromSignIn_sp.edit();
        editor.putBoolean("fromSignIn", true);
        editor.commit();

        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    public void onClick_SignIn(View v) {
        EditText edt_username = findViewById(R.id.sign_in_txtv_user);
        EditText edt_password = findViewById(R.id.sign_in_txtv_password);
        EditText edt_password2 = findViewById(R.id.sign_in_txtv_password2);

        username = edt_username.getText().toString();
        password = edt_password.getText().toString();
        password2 = edt_password2.getText().toString();

        //  Check for unique and valid name, outer spaces, lower-HIGHER cases and equal passwords
        if (username.trim().equals("") || password.trim().equals("") || password2.trim().equals("")) {
            Toast.makeText(this, "Username or Password cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        } else if (!(password.trim().equals(password2.trim()))) {
            Toast.makeText(this, "Entered Passwords differ!", Toast.LENGTH_SHORT).show();
            return;
        } else if (username.toLowerCase().contains(" ") || password.toLowerCase().contains(" ") || password.toLowerCase().contains(" ")) {
            Toast.makeText(this, "One or multiple fields contain a space, which is not allowed!", Toast.LENGTH_SHORT).show();
            return;
        }
        IsUnique(username);
    }

    private void IsUnique(String name) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                "https://studev.groept.be/api/a22pt407/getIdByAccName/" + name,
                null,
                response -> {
                    if (response.length() == 0) {
                        unique = true;
                    } else {
                        unique = false;
                    }

                    checkUnique();
                },
                error -> Toast.makeText(
                        SignIn.this,
                        "Unable to communicate wit the server",
                        Toast.LENGTH_SHORT
                ).show()
        );
        requestQueue.add(queueRequest);
    }

    private void checkUnique() {
        if (!(unique)) {
            Toast.makeText(SignIn.this, "Entered Username already in use", Toast.LENGTH_SHORT).show();
        } else {
            Dialog dialog = new AlertDialog.Builder(SignIn.this)
                    .setTitle("Sign In")
                    .setMessage("Are you sure about the registration information?")
                    .setPositiveButton("Confirm", (dialog12, which) -> {

                        //Send account into database
                        boolean isFirstIn = firstIn_sp.getBoolean("isFirstIn", true);
                        System.out.println(isFirstIn);

                        if (isFirstIn) {
                            SharedPreferences.Editor editor = firstIn_sp.edit();
                            editor.putBoolean("isFirstIn", false);
                            editor.commit();
                        }

                        account = new Account(username, password);
                        insertAccount();

                    }).setNegativeButton("Cancel", (dialog1, which) -> {
                        // TODO Auto-generated method stub
                    }).create();
            dialog.show();
        }
    }

    public void insertAccount() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest submitRequest = new StringRequest(
                Request.Method.POST,
                url_insert_account,
                response -> getId(),
                error -> Toast.makeText(SignIn.this, "Unable to create the new account due to: " + error, Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                return account.getPostParameters();
            }
        };
        requestQueue.add(submitRequest);
    }

    private void getId() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                "https://studev.groept.be/api/a22pt407/getIdByAccName/" + username,
                null,
                response -> {
                    JSONObject account;
                    try {
                        account = response.getJSONObject(0);
                        id = account.getInt("id");

                        Group group = new Group("NoGroup", id);
                        makeNewGroup(group);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(
                        SignIn.this,
                        "Unable to communicate wit the server",
                        Toast.LENGTH_SHORT
                ).show()
        );
        requestQueue.add(queueRequest);
    }

    private void makeNewGroup(Group group) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest submitRequest = new StringRequest(
                Request.Method.POST,
                url_insert_group,
                response -> {
                    //Prompt for successful registration
                    Toast.makeText(SignIn.this, "Successful Sign In!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SignIn.this, Login.class);

                    startActivity(intent);
                    finish();
                },
                error -> Toast.makeText(SignIn.this, "Unable to finalize the Account creation due to a NoGroup related error: " + error, Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                return group.getPostParameters();
            }
        };
        requestQueue.add(submitRequest);
    }
}