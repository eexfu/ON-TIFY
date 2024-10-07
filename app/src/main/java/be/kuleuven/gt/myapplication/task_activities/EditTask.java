package be.kuleuven.gt.myapplication.task_activities;

import androidx.appcompat.app.AppCompatActivity;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import be.kuleuven.gt.myapplication.main.MainActivity;
import be.kuleuven.gt.myapplication.R;
import be.kuleuven.gt.myapplication.model.Groups;
import be.kuleuven.gt.myapplication.model.Task;
import be.kuleuven.gt.myapplication.model.Tasks;

public class EditTask extends AppCompatActivity {
    private SharedPreferences accountid_sp;
    private int accountid;
    private String url_update_task = "https://studev.groept.be/api/a22pt407/updateTask";
    private String url_get_id = "https://studev.groept.be/api/a22pt407/getGroupId";
    private String url_get_all_id = "https://studev.groept.be/api/a22pt407/getAllGroupId";
    private String url_get_id_temp = url_get_id;
    private Task task;
    private String original_name;
    private EditText task_name;
    private Spinner group_spinner;
    private EditText task_comment;
    private Switch task_pin;
    private String selectedGroup;
    private List<String> groups;
    private int groupId;
    private Switch edit_task_deadline_select;
    private DatePicker edit_task_date_picker;
    private List<Task> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        initialize();
        initializeViews();
        setupDatePicker();
        setupViews();
    }
    private void initializeViews() {
        //initialize
        task_name = findViewById(R.id.edit_task_task_name);
        group_spinner = findViewById(R.id.edit_task_spinner_group);
        task_comment = findViewById(R.id.edit_task_comment);
        task_pin = findViewById(R.id.edit_task_pin);
        edit_task_date_picker = findViewById(R.id.edit_task_date_picker);
        edit_task_deadline_select = findViewById(R.id.edit_task_deadline_select);
    }

    private void setupDatePicker() {
        //initialize datePicker
        Calendar calendar = Calendar.getInstance();
        edit_task_date_picker.setMinDate(calendar.getTimeInMillis());

        System.out.println(task);
        if ( !task.getDeadline().equals("") ) {
            edit_task_date_picker.setVisibility(DatePicker.VISIBLE);
            edit_task_date_picker.init(Integer.parseInt(task.getDeadline().substring(6, 10)), Integer.parseInt(task.getDeadline().substring(3, 5)) - 1, Integer.parseInt(task.getDeadline().substring(0, 2)), null);
            edit_task_deadline_select.setChecked(true);
        }
        // otherwise, set the DatePicker widget to invisible
        else {
            edit_task_date_picker.setVisibility(DatePicker.INVISIBLE);
        }

        //setting deadlineSelect Listener
        edit_task_deadline_select.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // if the switch is checked, set the DatePicker widget to visible
            if (isChecked) {
                edit_task_date_picker.setVisibility(DatePicker.VISIBLE);
            }
            // otherwise, set the DatePicker widget to invisible
            else {
                edit_task_date_picker.setVisibility(DatePicker.INVISIBLE);
            }
        });
    }

    private void initialize() {
        accountid_sp = getSharedPreferences("accountid", MODE_PRIVATE);
        accountid = accountid_sp.getInt("accountid", 0);
        url_get_all_id = url_get_all_id + "/" + accountid;

        task = getIntent().getParcelableExtra("Task");
        original_name = task.getName();
        Tasks taskList = getIntent().getParcelableExtra("Tasks");
        tasks = taskList.getTasks();
        Groups groupList = getIntent().getParcelableExtra("Groups");
        groups = groupList.getGroups();
        groups.remove(0);
        groupId = groupList.getGroupId() - 1;
    }

    public void setupViews() {
        task_name.setText(task.getName());
        task_comment.setText(task.getComment());
        task_pin.setChecked(task.isPin());

        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(EditTask.this, android.R.layout.simple_spinner_item, groups);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        group_spinner.setAdapter(groupAdapter);
        group_spinner.setSelection(groupId);
        group_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                        groupId = group.getInt("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(
                        EditTask.this,
                        "Unable to communicate wit the server",
                        Toast.LENGTH_SHORT
                ).show()
        );
        requestQueue.add(queueRequest);
    }

    public void onClick_exit_editTask(View Caller) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void onClick_save_editTask(View Caller) {
        String name = task_name.getText().toString();
        String deadline = "";
        String comment = task_comment.getText().toString();
        boolean pin = task_pin.isChecked();

        int day = edit_task_date_picker.getDayOfMonth();
        int month = edit_task_date_picker.getMonth() + 1; // add 1 to month because it's zero-based
        int year = edit_task_date_picker.getYear();

        // format the selected date as a string in the format dd-mm-yyyy
        String formattedDate = String.format("%02d-%02d-%04d", day, month, year);

        if (edit_task_deadline_select.isChecked()) {
            deadline = formattedDate;
        }

        if(name.isEmpty()){
            Toast.makeText(EditTask.this, "Task Name cannot be empty!", Toast.LENGTH_SHORT).show();
        }
        else if (checkSameTask(name)) {
            task.setName(name);
            task.setGroupid(groupId);
            task.setDeadline(deadline);
            task.setComment(comment);
            task.setPin(pin);
            updateTask();
        } else {
            Toast.makeText(EditTask.this, "Task Name already in use!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkSameTask(String taskName) {
        boolean isSameTask = true;
        String name = taskName.trim().toLowerCase(); // Convert newname to lowercase

        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getName().trim().toLowerCase().equals(name) && !taskName.equals(task.getName())) {
                isSameTask = false;
            }
        }
        return isSameTask;
    }

    public void updateTask() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest submitRequest = new StringRequest(
                Request.Method.POST,
                url_update_task,
                response -> {
                    //Prompt for successful registration
                    Toast.makeText(EditTask.this, "Task has been Edited Successfully!", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(EditTask.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                },
                error -> Toast.makeText(EditTask.this, "Unable to edit the Task due to: " + error, Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                return task.getPostParametersEdit(original_name, accountid);
            }
        };
        requestQueue.add(submitRequest);
    }
}