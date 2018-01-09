package com.example.krzys.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class taskActivity extends AppCompatActivity {

    private taskDbHelper dbHelper;

    private EditText taskName;
    private CheckBox isDone;
    private RadioGroup priorityRadio;
    private DatePicker datePicker;
    private Button acceptBtn;

    private String mode;

    private int id;
    private String taskNameS, date;
    private boolean done, attachments;
    private task.priorityLevel priority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Intent intent = getIntent();

        mode = intent.getStringExtra("mode");

        dbHelper = new taskDbHelper(getApplicationContext());

        taskName = (EditText)findViewById(R.id.taskName);
        isDone = (CheckBox)findViewById(R.id.isDoneCheckbox);
        priorityRadio = (RadioGroup)findViewById(R.id.priorityGroup);
        datePicker = (DatePicker)findViewById(R.id.datePicker);
        acceptBtn = (Button)findViewById(R.id.acceptBtn);
        Log.d("mode", mode);
        if (mode.compareTo("add") == 0)
            acceptBtn.setText("Dodaj");
        else
        {
            acceptBtn.setText("Edytuj");
            setTaskValues(intent);
        }

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( modifyTask() )
                {
                        finishTask();
                }
                else
                    Toast.makeText(taskActivity.this, "Uzupełnij wszyskie pola!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean getTaskValues()
    {
        priority = task.priorityLevel.low;

        int day, month;
        month = datePicker.getMonth()+1;
        day = datePicker.getDayOfMonth();

        taskNameS = taskName.getText().toString();

        if (taskNameS.isEmpty())
            return false;

        date = String.format( "%d-%s-%s",
                datePicker.getYear(),
                (month < 10 ? "0" : "") + Integer.toString(month),
                (day < 10 ? "0" : "") + Integer.toString(day));

        switch (priorityRadio.getCheckedRadioButtonId())
        {
            case R.id.radio_low: priority = task.priorityLevel.low; break;
            case R.id.radio_averange: priority = task.priorityLevel.averange; break;
            case R.id.radio_high: priority = task.priorityLevel.high; break;
        }

        done = isDone.isChecked();
        attachments = true;
        return true;
    }

    private void setTaskValues(Intent intent)
    {
        task todo = intent.getParcelableExtra("task");

        id = todo.getId();
        taskNameS = todo.getName();
        date = todo.getDate();
        done = todo.isDone();
        priority = todo.getPriority();

        taskName.setText(taskNameS);
        isDone.setChecked(done);

        switch (priority.getValue())
        {
            case 0: ((RadioButton)findViewById(R.id.radio_low)).setChecked(true); break;
            case 1: ((RadioButton)findViewById(R.id.radio_averange)).setChecked(true); break;
            case 2: ((RadioButton)findViewById(R.id.radio_high)).setChecked(true); break;
        }

        String splitedDate [] = date.split("-");
        datePicker.updateDate(Integer.parseInt(splitedDate[0]), Integer.parseInt(splitedDate[1]), Integer.parseInt(splitedDate[2]));

    }
    
    private boolean modifyTask()
    {
        if (!getTaskValues())
            return false;

        if( mode.compareTo("add") == 0)
            dbHelper.addTask(taskNameS, priority, date, attachments, done);
        else
            dbHelper.modifyTask(id, taskNameS, priority, date, attachments, done);

        return true;
    }

    private void finishTask()
    {
        setResult(RESULT_OK);
        finish();
    }

}
