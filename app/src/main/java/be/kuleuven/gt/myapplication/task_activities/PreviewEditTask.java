package be.kuleuven.gt.myapplication.task_activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import be.kuleuven.gt.myapplication.main.MainActivity;
import be.kuleuven.gt.myapplication.R;
import be.kuleuven.gt.myapplication.model.Groups;
import be.kuleuven.gt.myapplication.model.Task;
import be.kuleuven.gt.myapplication.model.Tasks;

public class PreviewEditTask extends AppCompatActivity {
    private TextView preview_edit_task_task_name;
    private TextView preview_edit_task_group_name;
    private TextView preview_edit_task_deadline;
    private TextView preview_edit_task_comment;
    private Switch preview_edit_task_pin;
    private Task task;
    private Tasks tasks;
    private Groups groupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_edit_task);

        MainActivity.instance.finish();
        initializeViews();
    }

    //initialize
    private void initializeViews() {
        preview_edit_task_task_name = findViewById(R.id.preview_edit_task_task_name);
        preview_edit_task_group_name = findViewById(R.id.preview_edit_task_group_name);
        preview_edit_task_deadline = findViewById(R.id.preview_edit_task_deadline);
        preview_edit_task_comment = findViewById(R.id.preview_edit_task_comment);
        preview_edit_task_pin = findViewById(R.id.preview_edit_task_pin);

        task = getIntent().getParcelableExtra("Task");
        tasks = getIntent().getParcelableExtra("Tasks");
        groupList = getIntent().getParcelableExtra("Groups");

        preview_edit_task_task_name.setText(task.getName());
        preview_edit_task_group_name.setText(groupList.getGroupName());
        preview_edit_task_deadline.setText(task.getDeadline());
        preview_edit_task_comment.setText(task.getComment());
        preview_edit_task_pin.setChecked(task.isPin());
        preview_edit_task_pin.setEnabled(false);
    }

    public void onClick_preview_edit_task_exit(View Caller) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClick_preview_edit_task_edit(View Caller) {
        Intent intent = new Intent(this, EditTask.class);
        intent.putExtra("Task", task);
        intent.putExtra("Tasks", tasks);
        intent.putExtra("Groups", groupList);
        startActivity(intent);
        finish();
    }
}