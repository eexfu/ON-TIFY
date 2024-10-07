package be.kuleuven.gt.myapplication.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;
import java.util.Map;

import be.kuleuven.gt.myapplication.R;
import be.kuleuven.gt.myapplication.model.Task;
import be.kuleuven.gt.myapplication.model.TaskAdapterSelect;
import be.kuleuven.gt.myapplication.model.Tasks;

public class SelectActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private String url_delete_task = "https://studev.groept.be/api/a22pt407/deleteTask";
    private List<Task> tasks;
    private SharedPreferences accountid_sp;
    private int accountid;
    private Button btn_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_activity);

        //stop main activity
        MainActivity.instance.finish();

        initialize();
    }

    private void initialize() {
        Tasks taskList = getIntent().getParcelableExtra("Tasks");
        tasks = taskList.getTasks();

        accountid_sp = getSharedPreferences("accountid", MODE_PRIVATE);
        accountid = accountid_sp.getInt("accountid", 0);

        btn_delete = findViewById(R.id.select_delete);
        btn_delete.setOnClickListener(v -> deleteTask());

        //set RecyclerView
        recyclerView = findViewById(R.id.select_recyclerView);
        TaskAdapterSelect adapter = new TaskAdapterSelect(tasks);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    private void deleteTask() {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).isSelect()) {
                Task task = tasks.get(i);
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                StringRequest submitRequest = new StringRequest(
                        Request.Method.POST,
                        url_delete_task,
                        response -> {
                            Toast.makeText(SelectActivity.this, "The selected Tasks have been deleted successfully!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SelectActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        },
                        error -> Toast.makeText(SelectActivity.this, "Unable to place the order" + error, Toast.LENGTH_LONG).show()
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        return task.getPostParametersDelete(accountid);
                    }
                };
                requestQueue.add(submitRequest);
            }
        }
    }

    public void onClick_exit(View Caller) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }
}