package be.kuleuven.gt.myapplication.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;
import java.util.Map;

import be.kuleuven.gt.myapplication.task_activities.PreviewEditTask;
import be.kuleuven.gt.myapplication.R;
import be.kuleuven.gt.myapplication.main.SelectActivity;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    //instantiations

    private List<Task> tasks;
    private int accountid;
    private List<String> groups;
    private Map<Integer, String> groupMap;
    private String url_update = "https://studev.groept.be/api/a22pt407/UpdateTaskPinned";

    //Constructor
    public TaskAdapter(List<Task> tasks, List<String> groups, Map<Integer, String> groupMap) {
        this.tasks = tasks;
        this.groups = groups;
        this.groupMap = groupMap;
    }

    //ViewHolder
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View taskView = layoutInflater.inflate(R.layout.task, parent, false);
        ViewHolder myViewHolder = new ViewHolder(taskView);

        //long press listener
        myViewHolder.task.setOnLongClickListener(v -> {
            Intent intent = new Intent(parent.getContext(), SelectActivity.class);
            Tasks tasks1 = new Tasks(tasks);
            Groups groupList = new Groups(groups);
            intent.putExtra("Tasks", tasks1);
            intent.putExtra("Groups", groupList);
            parent.getContext().startActivity(intent);
            return false;
        });

        //onClick listener
        myViewHolder.task.setOnClickListener(v -> {
            int groupPosition = 0;
            for (int i = 0; i < groups.size(); i++) {
                if (groups.get(i).equals(((TextView) v.findViewById(R.id.task_group)).getText().toString())) {
                    groupPosition = i;
                }
            }
            Task task = tasks.get(myViewHolder.getAdapterPosition());
            Groups groupList = new Groups(groups, groupMap.get(task.getGroupid()), groupPosition);

            Intent intent = new Intent(parent.getContext(), PreviewEditTask.class);
            intent.putExtra("Task", task);
            intent.putExtra("Tasks", new Tasks(tasks));
            intent.putExtra("Groups", groupList);

            parent.getContext().startActivity(intent);
        });

        //checkbox done
        CheckBox task_cb_done = myViewHolder.task.findViewById(R.id.task_cb_done);
        task_cb_done.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                tasks.get(myViewHolder.getAdapterPosition()).setDone(true);
            } else {
                tasks.get(myViewHolder.getAdapterPosition()).setDone(false);
            }
        });

        //switch notification
        Switch task_pin = myViewHolder.task.findViewById(R.id.task_pin);
        task_pin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Task task;

            if (isChecked) {
                task = tasks.get(myViewHolder.getAdapterPosition());
                task.setPin(true);
            } else {
                task = tasks.get(myViewHolder.getAdapterPosition());
                task.setPin(false);
            }

            String name = task.getName();

            if (name.contains(" ")) {
                name = name.replace(" ", "-_-_-");
            }

            UpdatePinned(url_update + "/" + task.isPin() + "/" + name + "/" + accountid, parent.getContext());
        });
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(TaskAdapter.ViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.task.setBackgroundColor(Color.parseColor(task.getColor()));
        ((TextView) holder.task.findViewById(R.id.task_select_name)).setText(task.getName());

        if (!groupMap.get(task.getGroupid()).equals("NoGroup")) {
            ((TextView) holder.task.findViewById(R.id.task_group)).setText(groupMap.get(task.getGroupid()));
        } else {
            ((TextView) holder.task.findViewById(R.id.task_group)).setText("");
        }
        ((TextView) holder.task.findViewById(R.id.task_ddl)).setText(task.getDeadline());
        ((Switch) holder.task.findViewById(R.id.task_pin)).setChecked(task.isPin());
        ((CheckBox) holder.task.findViewById(R.id.task_cb_done)).setChecked(task.isDone());
    }

    //Setters and Getters

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public View task;

        public ViewHolder(View taskView) {
            super(taskView);
            task = taskView;
        }
    }

    private void UpdatePinned(String a, Context p) {
        RequestQueue requestQueue = Volley.newRequestQueue(p);
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                a,
                null,
                response -> {

                },
                error -> {

                }
        );
        requestQueue.add(queueRequest);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setAccId(int accountid) {
        this.accountid = accountid;
    }
}