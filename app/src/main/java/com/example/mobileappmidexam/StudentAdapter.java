package com.example.mobileappmidexam;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    public interface OnStudentClickListener {
        void onStudentClick(Student student);
    }

    private List<Student> studentList;
    private final OnStudentClickListener listener;

    public StudentAdapter(List<Student> studentList, OnStudentClickListener listener) {
        this.studentList = studentList;
        this.listener = listener;
    }

    public void updateData(List<Student> newStudents) {
        this.studentList = newStudents;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.bind(student, listener);
    }

    @Override
    public int getItemCount() {
        return studentList != null ? studentList.size() : 0;
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvStudentId;
        private final TextView tvAge;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvStudentId = itemView.findViewById(R.id.tvItemId);
            tvAge = itemView.findViewById(R.id.tvItemAge);
        }

        public void bind(final Student student, final OnStudentClickListener listener) {
            tvName.setText(student.getName());
            tvStudentId.setText(String.format(java.util.Locale.getDefault(), "ID: %s", student.getStudentId()));
            tvAge.setText(String.format(java.util.Locale.getDefault(), "Age: %d", student.getAge()));

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onStudentClick(student);
                }
            });
        }
    }
}
