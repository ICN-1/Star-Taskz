package com.theteam.taskz.home_pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.theteam.taskz.R;
import com.theteam.taskz.adapters.TaskDateListAdapter;
import com.theteam.taskz.models.TaskDateModel;

import java.util.ArrayList;
import java.util.Calendar;

public class TasksFragment extends Fragment {

    RecyclerView recyclerView;
    TaskDateListAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tasks_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.dates_recycler_view);
        linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        linearLayoutManager.setSmoothScrollbarEnabled(false);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        initialize();
    }

    private void initialize() {
        Calendar calendar = Calendar.getInstance();

        ArrayList<TaskDateModel> taskDateModels = new ArrayList<>();

        ArrayList<Integer> monthsWith30Days = new ArrayList<>();
        monthsWith30Days.add(4);
        monthsWith30Days.add(6);
        monthsWith30Days.add(9);
        monthsWith30Days.add(11);

        int max = monthsWith30Days.contains(calendar.get(Calendar.MONTH))? 30 : 31;

        for(int i = 1; i <= max; i++) {
            final Calendar current = (Calendar) calendar.clone();
            current.set(Calendar.DAY_OF_MONTH, i);
            taskDateModels.add(new TaskDateModel(current));
        }

        adapter = new TaskDateListAdapter(taskDateModels);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // We want to scroll to the current day if the it is the current month
        if(calendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)){
            recyclerView.smoothScrollToPosition(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)-1);
        }



    }
}
