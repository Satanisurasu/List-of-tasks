package com.example.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class TaskAdapter extends CursorAdapter {

    private MainActivity mainActivity;

    public TaskAdapter(MainActivity activity, Cursor cursor) {
        super(activity, cursor, 0);
        this.mainActivity = activity;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView taskTitle = view.findViewById(R.id.taskTitle);
        CheckBox taskCheckbox = view.findViewById(R.id.taskCheckbox);
        Button deleteButton = view.findViewById(R.id.deleteButton);

        // Используем _id вместо id
        long taskId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
        String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        int status = cursor.getInt(cursor.getColumnIndexOrThrow("status"));

        taskTitle.setText(title);
        taskCheckbox.setChecked(status == 1);

        deleteButton.setOnClickListener(v -> {
            mainActivity.deleteTask(taskId);
        });

        taskCheckbox.setOnClickListener(v -> {
            boolean isChecked = taskCheckbox.isChecked();
            mainActivity.updateTaskStatus(taskId, isChecked);
        });
    }
}
