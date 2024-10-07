package be.kuleuven.gt.myapplication.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import be.kuleuven.gt.myapplication.R;

public class TaskAdapterSelect extends RecyclerView.Adapter<TaskAdapterSelect.ViewHolder>{

    //instantiation
    private List<Task> tasks;

    //Constructor
    public TaskAdapterSelect(List<Task> tasks) {
        this.tasks = tasks;
    }

    //ViewHolder

    @Override
    public TaskAdapterSelect.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View taskView = layoutInflater.inflate(R.layout.task_select, parent, false);
        ViewHolder myViewHolder = new ViewHolder(taskView);

        CheckBox task_select_cb = myViewHolder.task.findViewById(R.id.task_select_cb);

        task_select_cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                tasks.get(myViewHolder.getAdapterPosition()).setSelect(true);
            }
            else {
                tasks.get(myViewHolder.getAdapterPosition()).setSelect(false);
            }
        });

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(TaskAdapterSelect.ViewHolder holder, int position) {
        Task task = tasks.get(position);
        ((TextView) holder.task.findViewById(R.id.task_select_name)).setText(task.getName());
        ((CheckBox) holder.task.findViewById(R.id.task_select_cb)).setChecked(task.isSelect());
    }

    //Setters and Getters

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        public View task;
        public ViewHolder(View taskView) {
            super(taskView);
            task = taskView;
        }
    }

    public List<Task> getTasks() {
        return tasks;
    }
}
