package be.kuleuven.gt.myapplication.group_activities;

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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;
import java.util.Map;

import be.kuleuven.gt.myapplication.main.MainActivity;
import be.kuleuven.gt.myapplication.R;
import be.kuleuven.gt.myapplication.model.Group;
import be.kuleuven.gt.myapplication.model.Groups;

public class NewGroup extends AppCompatActivity {
    private String url_insert_group = "https://studev.groept.be/api/a22pt407/insertGroup";
    private String url_get_groups = "https://studev.groept.be/api/a22pt407/getGroupByAccountid";
    private Group group;
    private List<String> groups;
    private SharedPreferences accountid_sp;
    private int accountid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        //initialize
        accountid_sp = getSharedPreferences("accountid", MODE_PRIVATE);
        accountid = accountid_sp.getInt("accountid", 0);
        url_get_groups = url_get_groups + "/" + accountid;

        Groups groupList = getIntent().getParcelableExtra("Groups");
        groups = groupList.getGroups();
    }

    public void onClick_exit(View Caller) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void onClick_new(View Caller) {
        //getting the name of group and accountID
        EditText group_name = findViewById(R.id.new_group_txt);
        String name = group_name.getText().toString();
        accountid_sp = getSharedPreferences("accountid", MODE_PRIVATE);
        int accountid = accountid_sp.getInt("accountid", 0);

        //  Check for unique name as well as for outer spaces and lower-HIGHER cases
        if (!name.isEmpty() && !name.matches("^\\s*NoGroup\\s*$") && !name.matches("^\\s*All\\s*$")) {
            group = new Group(name, accountid);

            if (checkSameGroup()) {
                Toast.makeText(NewGroup.this, "Group name already in use", Toast.LENGTH_SHORT).show();
            } else if (name.contains("-_-_-")) {
                // Extra safety step since -_-_- replaces spaces in database
                Dialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Contain -_-_-")
                        .setMessage("Your group name cannot contain \"-_-_-\" character")
                        .setPositiveButton("Confirm", (dialog1, which) -> {

                        }).setNegativeButton("Cancel", (dialog12, which) -> {
                            // TODO Auto-generated method stub
                        }).create();
                dialog.show();
            } else {
                if (name.contains(" ")) {
                    name = name.replace(" ", "-_-_-");
                    group.setName(name);
                }
                insertGroup();
            }
        } else {
            Toast.makeText(NewGroup.this, "Not Valid or Not allowed Group name", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkSameGroup() {
        boolean isSameGroup = false;
        String name = group.getName().trim().toLowerCase(); // Convert newname to lowercase

        for (String group : groups) {
            if (group.trim().toLowerCase().equals(name)) {
                isSameGroup = true;
                break;
            }
        }
        return isSameGroup;
    }

    private void insertGroup() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest submitRequest = new StringRequest(
                Request.Method.POST,
                url_insert_group,
                response -> {
                    Toast.makeText(NewGroup.this, "The new Group has been made!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(NewGroup.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                },
                error -> Toast.makeText(NewGroup.this, "Unable to create the new Group due to: " + error, Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                return group.getPostParameters();
            }
        };
        requestQueue.add(submitRequest);
    }
}