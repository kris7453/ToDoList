package com.example.krzys.todolist;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView taskList;
    ArrayList<task> taskListArray;
    taskAdapter taskAdapter;
    taskDbHelper dbHelper;

    int selectedTaskPosition;

    MenuItem actionEdit;
    MenuItem actionDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        taskList = (ListView)findViewById(R.id.taskList);

        dbHelper = new taskDbHelper(this);
        selectedTaskPosition = -1;

        taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //changeSelect(position);
                if (selectedTaskPosition != position)
                {
                    view.setSelected(true);
                    selectedTaskPosition = position;
                }
                else
                {
                    view.setSelected(false);
                    selectedTaskPosition = -1;
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        actionEdit = menu.findItem(R.id.action_edit);
        actionDelete = menu.findItem(R.id.action_delete);
        loadTasks();

Log.d("create menu","menu");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if( selectedTaskPosition > -1)
            switch (id)
            {
                case R.id.action_delete: deleteTask(); break;
                case R.id.action_edit: editTask(taskListArray.get(selectedTaskPosition)); break;

            }
        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id)
        {
            case R.id.nav_addTask: addTask(); break;
            case R.id.nav_sortPriority: sortByPriority();taskAdapter.notifyDataSetChanged();break;
            case R.id.nav_sortDate: sortTasksByDate(); taskAdapter.notifyDataSetChanged(); break;
            case R.id.nav_sortAlphabelic: sortTasksAlphabetic(); taskAdapter.notifyDataSetChanged(); break;
            default:break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadTasks()
    {
        selectedTaskPosition = -1;

        if (taskAdapter == null)
        {
            taskListArray = dbHelper.getAllTasks();
            taskAdapter = new taskAdapter(this,R.layout.task_item,taskListArray);
            taskList.setAdapter(taskAdapter);
        }
        else
        {
            taskListArray.clear();
            taskListArray.addAll(dbHelper.getAllTasks());
            taskAdapter.notifyDataSetChanged();
        }
    }

    private void addTask()
    {
        Intent taskIntent = new Intent(this,taskActivity.class);
        taskIntent.putExtra("mode", "add");
        startActivityForResult(taskIntent, 1);
    }

    private void editTask( task task)
    {
        Intent taskIntent = new Intent(this,taskActivity.class);
        taskIntent.putExtra("mode", "edit");
        taskIntent.putExtra("task", task);
        startActivityForResult(taskIntent, 2);
    }

    private void deleteTask()
    {
        if (selectedTaskPosition > -1) {
            dbHelper.deleteTask(taskListArray.get(selectedTaskPosition).getId());
            taskListArray.remove(selectedTaskPosition);
            taskAdapter.notifyDataSetChanged();
            selectedTaskPosition = -1;
        }
    }

    private void sortTasksByDate()
    {
        Collections.sort(taskListArray, new Comparator<task>() {
            @Override
            public int compare(task o1, task o2) {
                String task1 = o1.getDate();
                String task2 = o2.getDate();

                return task1.compareTo(task2);
            }
        });
    }

    private void sortTasksAlphabetic()
    {
        Collections.sort(taskListArray, new Comparator<task>() {
            @Override
            public int compare(task o1, task o2) {
                String task1 = o1.getName();
                String task2 = o2.getName();

                return task1.compareTo(task2);
            }
        });
    }

    private void sortByPriority()
    {
        Collections.sort(taskListArray, new Comparator<task>() {
            @Override
            public int compare(task o1, task o2) {
                int task1 = o1.getPriority().getValue();
                int task2 = o2.getPriority().getValue();

                return task1 - task2;
            }
        });
    }

    private void setActionButtonsVisibility( boolean visibility)
    {
        actionEdit.setVisible(visibility);
        actionDelete.setVisible(visibility);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if( resultCode == RESULT_OK)
        {
            loadTasks();
            Log.d("loading return", "tasks loading");
        }
    }
}
