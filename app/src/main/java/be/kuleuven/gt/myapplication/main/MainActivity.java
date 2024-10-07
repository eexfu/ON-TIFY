package be.kuleuven.gt.myapplication.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.kuleuven.gt.myapplication.R;
import be.kuleuven.gt.myapplication.group_activities.DeleteGroup;
import be.kuleuven.gt.myapplication.group_activities.EditGroup;
import be.kuleuven.gt.myapplication.group_activities.NewGroup;
import be.kuleuven.gt.myapplication.login_signin.Login;
import be.kuleuven.gt.myapplication.login_signin.SignIn;
import be.kuleuven.gt.myapplication.model.Groups;
import be.kuleuven.gt.myapplication.model.Task;
import be.kuleuven.gt.myapplication.model.TaskAdapter;
import be.kuleuven.gt.myapplication.model.Tasks;
import be.kuleuven.gt.myapplication.task_activities.NewTask;

import android.view.View;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private SharedPreferences accountid_sp;
    private int accountid;
    private String url_get_tasks="https://studev.groept.be/api/a22pt407/getTasksByAccountId";
    private String url_get_tasks_by_group = "https://studev.groept.be/api/a22pt407/getTaskByGroup";
    private String url_get_groups="https://studev.groept.be/api/a22pt407/getGroupByAccountid";
    private String url_get_id="https://studev.groept.be/api/a22pt407/getGroupId";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView accdisplay;
    private TextView iddisplay;
    private RecyclerView recyclerView;
    private List<Task> tasks;
    private List<Task> tasks_temp;
    private List<Task> tasks_backup;
    private Spinner sp_group;
    private Spinner sp_priority;
    private List<String> groups;
    private Map<Integer,String> groupMap;
    private String selectedGroup;
    private String selectedPriority = "Deadline";
    private String IdGroup;private List<String> priority = (Arrays.asList("Deadline","A-Z", "Old-New", "New-Old"));
    private int checkedNumber;
    private String url_delete_task="https://studev.groept.be/api/a22pt407/deleteTask";
    private boolean taskChangeFlag = true;
    public static MainActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        //setting url
        accountid_sp = getSharedPreferences("accountid",MODE_PRIVATE);
        accountid = accountid_sp.getInt("accountid",0);
        url_get_tasks = url_get_tasks+"/"+accountid;
        url_get_groups = url_get_groups+"/"+accountid;
        url_get_id = url_get_id+"/"+accountid;
        url_get_tasks_by_group = url_get_tasks_by_group+"/"+accountid;

        //initialize
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);;
        tasks = new ArrayList<>();
        tasks_temp = new ArrayList<>();
        tasks_backup = new ArrayList<>();
        sp_group = findViewById(R.id.sp_group);
        sp_priority = findViewById(R.id.sp_priority);
        groups = new ArrayList<>();
        groupMap = new HashMap<>();

        //setting toolbar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //setting navigationView and drawerLayout
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //Hide and show items
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_logout).setVisible(true);

        //set RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        TaskAdapter adapter = new TaskAdapter(tasks,groups,groupMap);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setAccId(accountid);

        //setting group spinner
        getGroups();

        //add tasks
        getNameAccount(String.valueOf(accountid));

        //setup searchBar
        setupSearchBar();
    }

    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.nav_new_task:
                Intent intent_new_task = new Intent(this, NewTask.class);
                intent_new_task.putExtra("Tasks",new Tasks(tasks_backup));
                intent_new_task.putExtra("Groups",new Groups(groups));
                startActivity(intent_new_task);
                finish();
                break;
            case R.id.nav_new_group:
                Intent intent_new_group = new Intent(this, NewGroup.class);
                intent_new_group.putExtra("Groups",new Groups(groups));
                startActivity(intent_new_group);
                finish();
                break;
            case R.id.nav_del_group:
                Intent intent_del_group = new Intent(this, DeleteGroup.class);
                intent_del_group.putExtra("Groups",new Groups(groups));
                startActivity(intent_del_group);
                finish();
                break;
            case R.id.nav_edit_group:
                Intent intent_edit_group = new Intent(this, EditGroup.class);
                intent_edit_group.putExtra("Groups",new Groups(groups));
                startActivity(intent_edit_group);
                finish();
                break;
            case R.id.color_code:
                Intent intent_colorcode = new Intent(this, ColorCode.class);
                startActivity(intent_colorcode);
                finish();
                break;
            case R.id.nav_logout:
                logout();
                Intent intent_logout = new Intent(this, Login.class);
                startActivity(intent_logout);
                finish();
                break;
            case R.id.nav_delete_acc:
                Dialog dialog=new AlertDialog.Builder(this)
                        .setTitle("Delete Your Account")
                        .setMessage("Are you sure to delete your account?")
                        .setPositiveButton("Confirm", (dialog1, which) -> {
                            deleteAccount();
                        }).setNegativeButton("Cancel", (dialog12, which) -> {

                        }).create();
                dialog.show();
                break;
            default:
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onClick_refresh(View Caller){
        updateCheckedNumber();
        deleteTask();
    }

    private void updateCheckedNumber(){
        checkedNumber = 0;
        for(int i=0;i<tasks.size();i++){
            if(tasks.get(i).isDone()){
                checkedNumber++;
            }
        }
    }

    private void deleteTask() {
        if(checkedNumber==0){
            getTasks(url_get_tasks);
        }
        else {
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).isDone()) {
                    taskChangeFlag = true;
                    Task task = tasks.get(i);
                    RequestQueue requestQueue = Volley.newRequestQueue(this);
                    StringRequest submitRequest = new StringRequest(
                            Request.Method.POST,
                            url_delete_task,
                            response -> getTasks(url_get_tasks),
                            error -> Toast.makeText(MainActivity.this, "Unable to communicate with server due to: " + error, Toast.LENGTH_LONG).show()
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            return task.getPostParametersDelete(accountid);
                        }
                    };
                    requestQueue.add(submitRequest);
                }
            }
            Toast.makeText(MainActivity.this, "The selected Tasks have been Completed! Keep It Going!", Toast.LENGTH_LONG).show();
        }
    }

    private void getTasks(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    processJSONResponse(response);
                    if (taskChangeFlag){
                        taskChangeFlag = false;
                        initialize_tasks_backup(response);
                    }
                    recyclerView.getAdapter().notifyDataSetChanged();
                },
                error -> Toast.makeText(
                        MainActivity.this,
                        "Unable to communicate wit the server",
                        Toast.LENGTH_SHORT
                ).show()
        );
        requestQueue.add(queueRequest);
    }

    private void processJSONResponse(JSONArray array){
        tasks.clear();
        for(int i=0;i<array.length();i++){
            try{
                JSONObject object = array.getJSONObject(i);
                tasks.add(new Task(object));
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        sortTasks(selectedPriority, tasks);
        setColorBasedOnDeadline();
    }

    private void initialize_tasks_backup(JSONArray array){
        tasks_backup.clear();
        for(int i=0;i<array.length();i++){
            try{
                JSONObject object = array.getJSONObject(i);
                tasks_backup.add(new Task(object));
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private void getNameAccount(String accountId){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                "https://studev.groept.be/api/a22pt407/getAccById/"+accountId,
                null,
                response -> {
                    JSONObject account;
                    try {
                        account = response.getJSONObject(0);
                        SetName(account.getString("username"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> Toast.makeText(
                        MainActivity.this,
                        "Unable to communicate wit the server",
                        Toast.LENGTH_SHORT
                ).show()
        );
        requestQueue.add(queueRequest);
    }

    private void SetName(String name){
        accdisplay = findViewById(R.id.name);
        iddisplay = findViewById(R.id.id);
        accdisplay.setText("Username: "+name);
        iddisplay.setText("ID: "+accountid);
    }

    private void setupSearchBar(){
        Menu menu = navigationView.getMenu();
        SearchView searchView = (SearchView) menu.findItem(R.id.nav_search_bar).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tasks.clear();
                for(int i=0;i<tasks_temp.size();i++){
                    if(tasks_temp.get(i).getName().contains(query)){
                        tasks.add(tasks_temp.get(i));
                    }
                }
                recyclerView.getAdapter().notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()){
                    tasks.clear();
                    for(int i=0;i<tasks_temp.size();i++){
                        tasks.add(tasks_temp.get(i));
                    }
                }
                recyclerView.getAdapter().notifyDataSetChanged();
                return false;
            }
        });
    }

    private void getGroups(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                url_get_groups,
                null,
                response -> {
                    setupSpinner(response);
                },
                error -> Toast.makeText(
                        MainActivity.this,
                        "Unable to communicate wit the server",
                        Toast.LENGTH_SHORT
                ).show()
        );
        requestQueue.add(queueRequest);
    }

    private void setupSpinner(JSONArray response) {
        addGroups(response);
        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, groups);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_group.setAdapter(groupAdapter);

        sp_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGroup = groups.get(position);
                if( !selectedGroup.equals("All") )
                {
                    for(Integer i:groupMap.keySet()){
                        if(groupMap.get(i).equals(selectedGroup)){
                            IdGroup=i+"";
                        }
                    }
                    getTasks(url_get_tasks_by_group+"/"+IdGroup);
                }
                else
                {
                    getTasks(url_get_tasks);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedGroup = "All";
            }
        });

        ArrayAdapter<String> groupAdapter2 = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, priority);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_priority.setAdapter(groupAdapter2);
        sp_priority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPriority = priority.get(position);
                sortTasks(selectedPriority, tasks);
                recyclerView.getAdapter().notifyDataSetChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPriority="Deadline";
            }
        });
    }

    private void addGroups(JSONArray array){
        groups.add("All");
        groupMap.put(1,"All");
        for(int i=0;i<array.length();i++){
            try{
                JSONObject object = array.getJSONObject(i);
                String name = object.getString("name");
                int id = object.getInt("id");
                if(name.contains("-_-_-")){
                    name = name.replace("-_-_-"," ");
                }
                groups.add(name);
                groupMap.put(id,name);
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private void logout(){
        SharedPreferences auto_login_sp = getSharedPreferences("auto_login", MODE_PRIVATE);
        accountid_sp = getSharedPreferences("accountid", MODE_PRIVATE);
        SharedPreferences.Editor editor = auto_login_sp.edit();
        editor.putBoolean("isAutoLogin", false);
        editor.commit();

        editor = accountid_sp.edit();
        editor.putInt("accountid",0);
        editor.commit();
    }

    private void deleteAccount(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                "https://studev.groept.be/api/a22pt407/DeleteAccByID/"+accountid,
                null,
                response -> {
                    logout();
                    Intent intent = new Intent(MainActivity.this, SignIn.class);
                    startActivity(intent);
                    finish();
                },
                error -> Toast.makeText(
                        MainActivity.this,
                        "Unable to communicate wit the server",
                        Toast.LENGTH_SHORT
                ).show()
        );
        requestQueue.add(queueRequest);
    }

    public void sortTasks(String input, List<Task> tasks) {
        List<Task> pinnedTasks = new ArrayList<>();
        List<Task> unpinnedTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.isPin()) {
                pinnedTasks.add(task);
            } else {
                unpinnedTasks.add(task);
            }
        }

        switch (input) {
            case "Deadline":
                // Sort unpinned tasks by deadline
                Collections.sort(unpinnedTasks, this::compareDeadline);
                break;
            case "A-Z":
                // Sort unpinned tasks by name
                Collections.sort(unpinnedTasks, (t1, t2) -> t1.getName().compareTo(t2.getName()));
                break;
            case "Old-New":
                // Sort unpinned tasks by creation date
                Collections.sort(unpinnedTasks, (t1, t2) -> t1.getCreationDate().compareTo(t2.getCreationDate()));
                break;
            case "New-Old":
                // Sort unpinned tasks by creation date
                Collections.sort(unpinnedTasks, (t1, t2) -> t2.getCreationDate().compareTo(t1.getCreationDate()));
                break;
            default:
                throw new IllegalArgumentException("Invalid input: " + input);
        }

        // Combine pinned and unpinned tasks
        List<Task> sortedTasks = new ArrayList<>();
        sortedTasks.addAll(pinnedTasks);
        sortedTasks.addAll(unpinnedTasks);

        // Replace original list with sorted list
        tasks.clear();
        tasks.addAll(sortedTasks);
    }

    private int compareDeadline(Task t1, Task t2){
        {
            String deadline1 = t1.getDeadline();
            String deadline2 = t2.getDeadline();

            if (deadline1.isEmpty()) {
                return 1; // move t1 to end of list
            } else if (deadline2.isEmpty()) {
                return -1; // move t2 to end of list
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    Date date1 = sdf.parse(deadline1);
                    Date date2 = sdf.parse(deadline2);
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    // Handle parsing exception
                    e.printStackTrace();
                    return 0;
                }
            }
        }
    }

    public void setColorBasedOnDeadline() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        for (Task task : tasks) {
            Date deadline = null;
            try {
                deadline = dateFormat.parse(task.getDeadline());
            } catch (ParseException e) {
                e.printStackTrace();
                task.setColor("#ACB4AC");//gray
                continue;
            }

            Calendar today = Calendar.getInstance();
            Calendar deadlineCalendar = Calendar.getInstance();
            deadlineCalendar.setTime(deadline);

            long differenceInMillis = deadlineCalendar.getTimeInMillis() - today.getTimeInMillis();
            long differenceInDays = differenceInMillis / (24 * 60 * 60 * 1000);

            if (differenceInDays <= -1) {
                task.setColor("#BE5750");//red
            } else if ( -1 < differenceInDays && differenceInDays < 1) {
                task.setColor("#E57C0E");//red
            } else if ( 1 <= differenceInDays && differenceInDays <= 7) {
                task.setColor("#D6CD41");//yellow
            } else {
                task.setColor("#6EA96E");//green
            }
        }

        tasks_temp.clear();
        for(int i=0;i<tasks.size();i++){
            tasks_temp.add(tasks.get(i));
        }
    }
}