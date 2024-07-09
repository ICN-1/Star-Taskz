package com.theteam.taskz.adapters;

import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.theteam.taskz.R;
import com.theteam.taskz.data.StateHolder;
import com.theteam.taskz.models.TaskDateModel;
import com.theteam.taskz.state.TasksState;
import com.theteam.taskz.utilities.ThemeManager;

import java.util.List;

// Replace 'TaskDate' with your actual data class for task dates
public class TaskDateListAdapter extends RecyclerView.Adapter<TaskDateListAdapter.TaskDateViewHolder> {

    private List<TaskDateModel> taskDates;

    public TaskDateListAdapter(List<TaskDateModel> taskDates) {
        this.taskDates = taskDates;
    }

    @NonNull
    @Override
    public TaskDateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_date_item_layout, parent, false); // Use your item layout
        return new TaskDateViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskDateViewHolder holder, int position) {
        final TaskDateModel taskDate = taskDates.get(holder.getAdapterPosition());
        holder.dayText.setText(taskDate.day());
        holder.dayOfWeek.setText(taskDate.dayOfWeek());

        if(taskDate.isToday()){
            TasksState.todayView = holder;
            if(TasksState.selectedView == null){
                TasksState.selectedView = holder;
                holder.linearLayout.setBackgroundColor(holder.view.getResources().getColor(R.color.themeColor));
                holder.dayText.setTextColor(holder.view.getResources().getColor(R.color.white));
                holder.dayOfWeek.setTextColor(holder.view.getResources().getColor(R.color.secondaryDark));
                animateViewPadding(holder.linearLayout, dpToPx(14),dpToPx(18), dpToPx(10), dpToPx(16));
                holder.indicator.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            }else{
                holder.indicator.setVisibility(View.VISIBLE);
                holder.indicator.setColorFilter(holder.view.getResources().getColor(R.color.themeColor), PorterDuff.Mode.SRC_IN);
            }
        }
        else{
            holder.indicator.setVisibility(View.INVISIBLE);
        }


        holder.linearLayout.setOnClickListener(view -> {
            final ThemeManager themeManager = new ThemeManager(view.getContext());
            final boolean isDarkMode = themeManager.isDarkMode();
            if(TasksState.selectedView == holder){
                return;
            }
            if(TasksState.selectedView != null){
                animateViewPadding(TasksState.selectedView.linearLayout, dpToPx(18),dpToPx(14), dpToPx(16), dpToPx(10));
            }

            if(taskDate.isToday()){
                holder.indicator.setVisibility(View.VISIBLE);
                holder.indicator.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            }
            else{
                TasksState.todayView.indicator.setColorFilter(view.getResources().getColor(R.color.themeColor), PorterDuff.Mode.SRC_IN);
                TasksState.todayView.indicator.setVisibility(View.VISIBLE);
            }

            TasksState.selectedView.linearLayout.setBackgroundColor(TasksState.selectedView.view.getResources().getColor(isDarkMode? R.color.rootBackgroundDark:R.color.rootBackgroundLight));
            TasksState.selectedView.dayText.setTextColor(TasksState.selectedView.view.getResources().getColor(isDarkMode? R.color.primaryDark:R.color.primaryLight));
            TasksState.selectedView.dayOfWeek.setTextColor(TasksState.selectedView.view.getResources().getColor(isDarkMode?R.color.secondaryDark: R.color.secondaryLight));

            holder.linearLayout.setBackgroundColor(holder.view.getResources().getColor(R.color.themeColor));
            holder.dayText.setTextColor(holder.view.getResources().getColor(R.color.white));
            holder.dayOfWeek.setTextColor(holder.view.getResources().getColor(R.color.white));
            animateViewPadding(holder.linearLayout, dpToPx(14),dpToPx(18), dpToPx(10), dpToPx(16));
            TasksState.selectedView = holder;
        });
    }

    static public void animateViewPadding(final View view, int startHorzontal, int endHorizontal, int startVertical, int endVertical) {
        ValueAnimator paddingLeftAnimator = ValueAnimator.ofInt(startHorzontal, endHorizontal);
        ValueAnimator paddingTopAnimator = ValueAnimator.ofInt(startVertical, endVertical);
        ValueAnimator paddingRightAnimator = ValueAnimator.ofInt(startHorzontal, endHorizontal);
        ValueAnimator paddingBottomAnimator = ValueAnimator.ofInt(startVertical, endVertical);

        paddingLeftAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int leftPadding = (Integer) animation.getAnimatedValue();
                view.setPadding(leftPadding, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
            }
        });

        paddingTopAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int topPadding = (Integer) animation.getAnimatedValue();
                view.setPadding(view.getPaddingLeft(), topPadding, view.getPaddingRight(), view.getPaddingBottom());
            }
        });

        paddingRightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int rightPadding = (Integer) animation.getAnimatedValue();
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), rightPadding, view.getPaddingBottom());
            }
        });

        paddingBottomAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int bottomPadding = (Integer) animation.getAnimatedValue();
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), bottomPadding);
            }
        });

        paddingLeftAnimator.setInterpolator(new DecelerateInterpolator());
        paddingTopAnimator.setInterpolator(new DecelerateInterpolator());
        paddingRightAnimator.setInterpolator(new DecelerateInterpolator());
        paddingBottomAnimator.setInterpolator(new DecelerateInterpolator());

        paddingLeftAnimator.setDuration(500); // Duration in milliseconds
        paddingTopAnimator.setDuration(500); // Duration in milliseconds
        paddingRightAnimator.setDuration(500); // Duration in milliseconds
        paddingBottomAnimator.setDuration(500); // Duration in milliseconds

        paddingLeftAnimator.start();
        paddingTopAnimator.start();
        paddingRightAnimator.start();
        paddingBottomAnimator.start();
    }
    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return taskDates.size();
    }

    public static class TaskDateViewHolder extends RecyclerView.ViewHolder {
        public TextView dayText,dayOfWeek;
        public CardView cardView;
        public View view;
        public ImageView indicator;
        public LinearLayout linearLayout;


        public TaskDateViewHolder(View itemView) {
            super(itemView);
            indicator = itemView.findViewById(R.id.indicator);
            dayText = itemView.findViewById(R.id.dayText);
            dayOfWeek = itemView.findViewById(R.id.dayOfWeekText);
            cardView = itemView.findViewById(R.id.cardView);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            view = itemView;
        }
    }
}
