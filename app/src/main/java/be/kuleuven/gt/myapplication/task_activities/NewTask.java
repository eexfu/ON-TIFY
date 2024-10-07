package be.kuleuven.gt.myapplication.task_activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import be.kuleuven.gt.myapplication.main.MainActivity;
import be.kuleuven.gt.myapplication.R;
import be.kuleuven.gt.myapplication.model.Groups;
import be.kuleuven.gt.myapplication.model.Task;
import be.kuleuven.gt.myapplication.model.Tasks;

public class NewTask extends AppCompatActivity {
    private SharedPreferences accountid_sp;
    private String url_get_groups = "https://studev.groept.be/api/a22pt407/getGroupByAccountid";
    private String url_get_id = "https://studev.groept.be/api/a22pt407/getGroupId";
    private String url_get_id_temp = url_get_id;
    private int accountid;
    private List<Task> tasks;
    private Spinner groupSpinner;
    private List<String> groups;
    private String selectedGroup;
    private String IdGroup = "1";
    private static final String POST_URL = "https://studev.groept.be/api/a22pt407/insertTask";
    private Task newTask;
    private DatePicker date_picker;
    private Switch deadline_select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        initialize();
        setupDatePicker();
        setupSpinner();
    }
    private void initialize() {
        groupSpinner = findViewById(R.id.new_task_spinner_group);

        Tasks taskList = getIntent().getParcelableExtra("Tasks");
        Groups groupList = getIntent().getParcelableExtra("Groups");
        groupList.getGroups().remove(0);// We don't want the String ALL to be selectable

        accountid_sp = getSharedPreferences("accountid", MODE_PRIVATE);
        accountid = accountid_sp.getInt("accountid", 0);
        url_get_groups = url_get_groups + "/" + accountid;

        tasks = taskList.getTasks();
        groups = groupList.getGroups();
    }

    private void setupDatePicker() {
        //setup datePicker
        date_picker = findViewById(R.id.edit_task_date_picker);
        deadline_select = findViewById(R.id.edit_task_deadline_select);
        Calendar calendar = Calendar.getInstance();
        date_picker.setMinDate(calendar.getTimeInMillis());
        date_picker.setVisibility(DatePicker.INVISIBLE);

        deadline_select.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // if the switch is checked, set the DatePicker widget to visible
            if (isChecked) {
                date_picker.setVisibility(DatePicker.VISIBLE);
            }
            // otherwise, set the DatePicker widget to invisible
            else {
                date_picker.setVisibility(DatePicker.INVISIBLE);
            }
        });
    }

    private void setupSpinner() {
        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(NewTask.this, android.R.layout.simple_spinner_item, groups);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSpinner.setAdapter(groupAdapter);

        // Spinner Listener for Group Selection
        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGroup = groups.get(position);
                // Extra safety step in order to prevent spaces inaccuracy
                if (selectedGroup.contains(" ")) {
                    selectedGroup = selectedGroup.replace(" ", "-_-_-");
                }
                url_get_id_temp = url_get_id + "/" + accountid + "/" + selectedGroup;
                getIdOfGroup();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedGroup = "NoGroup";
            }
        });
    }

    private void getIdOfGroup() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                url_get_id_temp,
                null,
                response -> {
                    try {
                        JSONObject group = response.getJSONObject(0);
                        IdGroup = group.getString("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(
                        NewTask.this,
                        "Unable to communicate wit the server",
                        Toast.LENGTH_SHORT
                ).show()
        );
        requestQueue.add(queueRequest);
    }

    public void onClick_save_task(View Caller) {

        EditText task_name = findViewById(R.id.new_task_task_name);
        EditText comment = findViewById(R.id.new_task_comment);
        Switch pin = findViewById(R.id.new_task_pin);

        String name = task_name.getText().toString().toLowerCase(Locale.ROOT); //Lower case for SearchBar

        int day = date_picker.getDayOfMonth();
        int month = date_picker.getMonth() + 1; // add 1 to month because it's zero-based
        int year = date_picker.getYear();
        // format the selected date as a string in the format dd-mm-yyyy
        String formattedDate = String.format("%02d-%02d-%04d", day, month, year);

        // Check for unique name as well as for outer spaces and lower-HIGHER cases
        if ( name.isEmpty() ) {
            Toast.makeText(NewTask.this, "Task Name is a mandatory field!", Toast.LENGTH_SHORT).show();
        }
        else if (checkSameTask(name)) {
            Toast.makeText(NewTask.this, "Name of Task already existing", Toast.LENGTH_SHORT).show();
        } else if (name.contains("-_-_-")) {
            Dialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Contain -_-_-")
                    .setMessage("Your task name cannot contain \"-_-_-\" character")
                    .setPositiveButton("Confirm", (dialog1, which) -> {

                    }).setNegativeButton("Cancel", (dialog12, which) -> {

                    }).create();
            dialog.show();
        } else {
            if (name.contains(" ")) {
                name = name.replace(" ", "-_-_-");
            }
            if (deadline_select.isChecked()) {
                newTask = new Task(
                        name,
                        formattedDate,
                        comment.getText().toString(),
                        pin.isChecked(),
                        IdGroup + "",
                        accountid
                );
            } else {
                newTask = new Task(
                        name,
                        "",
                        comment.getText().toString(),
                        pin.isChecked(),
                        IdGroup + "",
                        accountid
                );
            }
            insertNewTask();
        }
    }

    private boolean checkSameTask(String taskName) {
        boolean isSameTask = false;
        String name = taskName.trim().toLowerCase(); // Convert newname to lowercase

        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getName().trim().toLowerCase().equals(name)) {
                isSameTask = true;
            }
        }
        return isSameTask;
    }

    public void insertNewTask() {
        ProgressDialog progressDialog = new ProgressDialog(NewTask.this);
        progressDialog.setMessage("Uploading, please wait...");

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest submitRequest = new StringRequest(
                Request.Method.POST,
                POST_URL,
                response -> {
                    progressDialog.dismiss();
                    Toast.makeText(NewTask.this, "The New Task has been created successfully!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(NewTask.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(NewTask.this, "Unable to create the new task due to: " + error, Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return newTask.getPostParameters();
            }
        };
        progressDialog.show();
        requestQueue.add(submitRequest);
    }

    public void onClick_exit(View Caller) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }
}
