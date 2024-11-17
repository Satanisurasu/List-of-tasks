package com.example.myapplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TaskDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new TaskDbHelper(this);

        // Настройка вкладок
        TabHost tabHost = findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("Active");
        spec.setContent(R.id.activeTasksTab);
        spec.setIndicator("Активные задачи");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Completed");
        spec.setContent(R.id.completedTasksTab);
        spec.setIndicator("Выполненные задачи");
        tabHost.addTab(spec);

        // Настройка UI
        EditText titleInput = findViewById(R.id.taskTitle);
        EditText descriptionInput = findViewById(R.id.taskDescription);
        Button addButton = findViewById(R.id.addTaskButton);
        ListView activeTaskList = findViewById(R.id.activeTaskList);
        ListView completedTaskList = findViewById(R.id.completedTaskList);

        // Добавление новой задачи
        addButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "Введите заголовок задачи", Toast.LENGTH_SHORT).show();
                return;
            }

            addTask(title, description);
            titleInput.setText("");
            descriptionInput.setText("");
            loadTasks();
        });

        // Загрузка задач при запуске
        loadTasks();
    }

    public void addTask(String title, String description) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskDbHelper.COLUMN_TITLE, title);
        values.put(TaskDbHelper.COLUMN_DESCRIPTION, description);
        values.put(TaskDbHelper.COLUMN_STATUS, 0); // Активная задача

        long result = db.insert(TaskDbHelper.TABLE_NAME, null, values);
        if (result == -1) {
            Toast.makeText(this, "Ошибка добавления задачи", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteTask(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted = db.delete(TaskDbHelper.TABLE_NAME, TaskDbHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        if (rowsDeleted > 0) {
            Toast.makeText(this, "Задача удалена", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ошибка удаления задачи", Toast.LENGTH_SHORT).show();
        }
        loadTasks();
    }

    public void updateTaskStatus(long id, boolean isCompleted) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskDbHelper.COLUMN_STATUS, isCompleted ? 1 : 0);

        db.update(TaskDbHelper.TABLE_NAME, values, TaskDbHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        loadTasks();
    }

    private void loadTasks() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Загрузка активных задач
        Cursor activeCursor = db.query(
                TaskDbHelper.TABLE_NAME,
                null,
                TaskDbHelper.COLUMN_STATUS + " = 0",
                null, null, null, null
        );

        TaskAdapter activeAdapter = new TaskAdapter(this, activeCursor);
        ListView activeTaskList = findViewById(R.id.activeTaskList);
        activeTaskList.setAdapter(activeAdapter);

        // Загрузка выполненных задач
        Cursor completedCursor = db.query(
                TaskDbHelper.TABLE_NAME,
                null,
                TaskDbHelper.COLUMN_STATUS + " = 1",
                null, null, null, null
        );

        TaskAdapter completedAdapter = new TaskAdapter(this, completedCursor);
        ListView completedTaskList = findViewById(R.id.completedTaskList);
        completedTaskList.setAdapter(completedAdapter);
    }
}
