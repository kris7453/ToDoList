package com.example.krzys.todolist;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by krzys on 02.01.2018.
 */

public class taskAdapter extends ArrayAdapter<task> {
    private ArrayList<task> tasks;
    private LayoutInflater viewInflater;
    private taskDbHelper db = new taskDbHelper(getContext());


    public taskAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<task> tasks) {
        super(context, resource, tasks);
        this.tasks = tasks;
        viewInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if ( convertView == null)
            convertView = viewInflater.inflate(R.layout.task_item, null);

        task todo = tasks.get(position);
Log.d("atask",""+position+" "+todo.toString());
        TextView priorityLabel = (TextView)convertView.findViewById(R.id.priorityLabel);


        int priorityLevel = todo.getPriority().getValue();

        priorityLabel.setText(Integer.toString(priorityLevel));

        switch(priorityLevel)
        {
            case 0: priorityLabel.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.lowPriority)); break;
            case 1: priorityLabel.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.averangePriority)); break;
            case 2: priorityLabel.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.highPriority)); break;
        }

        priorityLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task todo = tasks.get(position);
                int priority = todo.getPriority().getValue() + 1;
                todo.setPriority(priority);

                db.setTaskPriority(todo.getId(), todo.getPriority().getValue());
                notifyDataSetChanged();
            }
        });

        ImageView done = (ImageView)convertView.findViewById(R.id.checkbox);

        if (todo.isDone())
            done.setImageResource(getContext().getResources().getIdentifier("@android:drawable/checkbox_on_background",null,getContext().getPackageName()));
        else
            done.setImageResource(getContext().getResources().getIdentifier("@android:drawable/checkbox_off_background",null,getContext().getPackageName()));

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task todo = tasks.get(position);
                todo.setDone(!todo.isDone());

                db.setTaskDone(todo.getId(), todo.isDone());
                notifyDataSetChanged();
            }
        });

        ((TextView)convertView.findViewById(R.id.taskName)).setText(todo.getName());
        ((TextView)convertView.findViewById(R.id.taskDate)).setText(todo.getDate());

        if (todo.isAttachments())
            convertView.findViewById(R.id.attachmentsIcon).setVisibility(View.VISIBLE);

        return convertView;//super.getView(position, convertView, parent);
    }
}
