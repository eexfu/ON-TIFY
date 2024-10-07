package be.kuleuven.gt.myapplication.group_activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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

public class DeleteGroup extends AppCompatActivity {
    private String url_delete_group = "https://studev.groept.be/api/a22pt407/deleteGroup";
    private String url_get_groups = "https://studev.groept.be/api/a22pt407/getGroupByAccountid";
    private SharedPreferences accountid_sp;
    private int accountid;
    private Group group;
    private Spinner delete_group_sp;
    private String selectedGroup;
    private List<String> groups;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_group);

        initialize();
        setSpinnerGroup();
    }

    private void initialize() {
        accountid_sp = getSharedPreferences("accountid", MODE_PRIVATE);
        accountid = accountid_sp.getInt("accountid", 0);

        url_get_groups = url_get_groups + "/" + accountid;

        delete_group_sp = findViewById(R.id.delete_group_sp);
        Groups groupList = getIntent().getParcelableExtra("Groups");
        groups = groupList.getGroups();
        groups.set(0, "Select"); // Safety String for default selection
        groups.remove(1); //We don't want NoGroup to be deleted
    }

    private void setSpinnerGroup() {
        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(DeleteGroup.this, android.R.layout.simple_spinner_item, groups);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        delete_group_sp.setAdapter(groupAdapter);
        // Spinner Listener for Group Selection
        delete_group_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGroup = groups.get(position);

                // Extra safety step in order to prevent spaces inaccuracy
                if (selectedGroup.contains(" ")) {
                    selectedGroup = selectedGroup.replace(" ", "-_-_-");
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedGroup = "Select";
            }

        });
    }

    public void onClick_exit(View Caller) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void onClick_delete(View Caller) {
        group = new Group(accountid, selectedGroup);

        if (!selectedGroup.equals("Select")) {
            deleteGroup();
        } else {
            Toast.makeText(DeleteGroup.this, "No valid Group has been Selected", Toast.LENGTH_LONG).show();
        }
    }

    private void deleteGroup() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest submitRequest = new StringRequest(
                Request.Method.POST,
                url_delete_group,
                response -> {
                    Toast.makeText(DeleteGroup.this, "Group has been successfully Deleted", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(DeleteGroup.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                },
                error -> Toast.makeText(DeleteGroup.this, "Unable to delete " + group.getName() + ": " + error, Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                return group.getPostParametersDelete();
            }
        };
        requestQueue.add(submitRequest);
    }
}