package be.kuleuven.gt.myapplication.group_activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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

public class EditGroup extends AppCompatActivity {
    private SharedPreferences accountid_sp;
    private int accountid;
    private String url_get_groups="https://studev.groept.be/api/a22pt407/getGroupByAccountid";
    private String url_update_group="https://studev.groept.be/api/a22pt407/updateGroup";
    private Spinner edit_group_sp;
    private List<String> groups;
    private Group group;
    private String selectedGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        initialize();
        setupSpinner();
    }

    private void initialize() {
        edit_group_sp = findViewById(R.id.edit_group_sp);
        accountid_sp = getSharedPreferences("accountid",MODE_PRIVATE);
        accountid = accountid_sp.getInt("accountid",0);
        url_get_groups = url_get_groups+"/"+accountid;

        Groups groupList = getIntent().getParcelableExtra("Groups");
        groups = groupList.getGroups();
        groups.set(0,"Select");
        groups.remove(1);
    }

    private void setupSpinner(){
        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(EditGroup.this, android.R.layout.simple_spinner_item, groups);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edit_group_sp.setAdapter(groupAdapter);

        // Spinner Listener for Group Selection
        edit_group_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGroup = groups.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedGroup = "Select";
            }
        });
    }

    public void onClick_exit(View Caller){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void onClick_save_group(View Caller) {
        EditText new_name = findViewById(R.id.editName);
        String name= new_name.getText().toString();

        // Check for unique name as well as for outer spaces and lower-HIGHER cases
        if( !(name.isEmpty()) && !checkSameGroup(name) && !name.matches("^\\s*NoGroup\\s*$") && !name.matches("^\\s*All\\s*$") )
        {
            group = new Group(name,accountid,selectedGroup);
            if(name.contains("-_-_-")) {
                // Extra safety step since -_-_- replaces spaces in database
                Dialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Contain -_-_-")
                        .setMessage("Your group name cannot contain \"-_-_-\" character")
                        .setPositiveButton("Confirm", (dialog1, which) -> {

                        }).setNegativeButton("Cancel", (dialog12, which) -> {
                            // TODO Auto-generated method stub
                        }).create();
                dialog.show();
            }
            else if(!selectedGroup.equals("Select")){
                updateGroup();
            }
            else {
                Toast.makeText(EditGroup.this, "No valid Group has been Selected", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(EditGroup.this, "Group name not valid or already existing", Toast.LENGTH_LONG).show();
        }
    }

    private void updateGroup(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest submitRequest = new StringRequest(
                Request.Method.POST,
                url_update_group,
                response -> {
                    Toast.makeText(EditGroup.this, "Group name successfully Edited", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(EditGroup.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                },
                error -> Toast.makeText(EditGroup.this,"Unable to Edit Group due to: " + error,Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                return group.getPostParametersEdit();
            }
        };
        requestQueue.add(submitRequest);
    }


    private boolean checkSameGroup(String newname) {
        boolean isSameGroup = false;
        newname = newname.trim().toLowerCase();

        for (String group : groups) {
            if (group.trim().toLowerCase().equals(newname)) {
                isSameGroup = true;
                break;
            }
        }
        if (newname.equals("all") || newname.equals("nogroup")) {
            isSameGroup = true;
        }
        return isSameGroup;
    }
}