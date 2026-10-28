package com.example.mobileappmidexam;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MIN_AGE = 17;
    private static final int MAX_AGE = 50;

    private EditText etStudentName;
    private EditText etStudentId;
    private TextView tvAgeValue;
    private SeekBar sbStudentAge;

    private MaterialButton btnAdd;
    private MaterialButton btnEdit;
    private MaterialButton btnDelete;
    private MaterialButton btnView;
    private MaterialButton btnClearForm;

    private RecyclerView rvStudents;
    private TextView tvEmptyState;

    private DatabaseHelper dbHelper;
    private StudentAdapter adapter;
    private List<Student> studentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initDatabaseAndAdapter();
        setupListeners();
        loadStudents();
    }

    private void initViews() {
        etStudentName = findViewById(R.id.etStudentName);
        etStudentId = findViewById(R.id.etStudentId);
        tvAgeValue = findViewById(R.id.tvAgeValue);
        sbStudentAge = findViewById(R.id.sbStudentAge);

        btnAdd = findViewById(R.id.btnAdd);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnView = findViewById(R.id.btnView);
        btnClearForm = findViewById(R.id.btnClearForm);

        rvStudents = findViewById(R.id.rvStudents);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        // Max progress for age is 50 - 17 = 33
        sbStudentAge.setMax(MAX_AGE - MIN_AGE);
        sbStudentAge.setProgress(0);
        updateAgeDisplay(MIN_AGE);
    }

    private void initDatabaseAndAdapter() {
        dbHelper = new DatabaseHelper(this);

        rvStudents.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentAdapter(studentList, student -> {
            etStudentName.setText(student.getName());
            etStudentId.setText(student.getStudentId());
            int progress = student.getAge() - MIN_AGE;
            if (progress >= 0 && progress <= sbStudentAge.getMax()) {
                sbStudentAge.setProgress(progress);
            }
            Toast.makeText(MainActivity.this, "Selected: " + student.getName(), Toast.LENGTH_SHORT).show();
        });
        rvStudents.setAdapter(adapter);
    }

    private void setupListeners() {
        sbStudentAge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int selectedAge = progress + MIN_AGE;
                updateAgeDisplay(selectedAge);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        btnAdd.setOnClickListener(v -> handleAddStudent());
        btnEdit.setOnClickListener(v -> handleEditStudent());
        btnDelete.setOnClickListener(v -> handleDeleteStudent());
        btnView.setOnClickListener(v -> handleViewStudents());
        btnClearForm.setOnClickListener(v -> clearForm());
    }

    private void updateAgeDisplay(int age) {
        tvAgeValue.setText(String.format(java.util.Locale.getDefault(), "%d years old", age));
    }

    private void handleAddStudent() {
        String name = etStudentName.getText().toString().trim();
        String studentId = etStudentId.getText().toString().trim();
        int age = sbStudentAge.getProgress() + MIN_AGE;

        if (TextUtils.isEmpty(name)) {
            etStudentName.setError("Please enter student name");
            etStudentName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(studentId)) {
            etStudentId.setError("Please enter student ID");
            etStudentId.requestFocus();
            return;
        }

        if (dbHelper.checkStudentExists(studentId)) {
            Toast.makeText(this, "Student ID " + studentId + " already exists!", Toast.LENGTH_LONG).show();
            return;
        }

        Student student = new Student(studentId, name, age);
        boolean success = dbHelper.addStudent(student);

        if (success) {
            Toast.makeText(this, "Student added successfully!", Toast.LENGTH_SHORT).show();
            clearForm();
            loadStudents();
        } else {
            Toast.makeText(this, "Failed to add student.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleEditStudent() {
        String name = etStudentName.getText().toString().trim();
        String studentId = etStudentId.getText().toString().trim();
        int age = sbStudentAge.getProgress() + MIN_AGE;

        if (TextUtils.isEmpty(studentId)) {
            etStudentId.setError("Please enter student ID to edit");
            etStudentId.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(name)) {
            etStudentName.setError("Please enter student name");
            etStudentName.requestFocus();
            return;
        }

        if (!dbHelper.checkStudentExists(studentId)) {
            Toast.makeText(this, "Student ID " + studentId + " not found!", Toast.LENGTH_LONG).show();
            return;
        }

        Student student = new Student(studentId, name, age);
        boolean success = dbHelper.updateStudent(student);

        if (success) {
            Toast.makeText(this, "Student updated successfully!", Toast.LENGTH_SHORT).show();
            clearForm();
            loadStudents();
        } else {
            Toast.makeText(this, "Failed to update student.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleDeleteStudent() {
        String studentId = etStudentId.getText().toString().trim();

        if (TextUtils.isEmpty(studentId)) {
            etStudentId.setError("Please enter student ID to delete");
            etStudentId.requestFocus();
            return;
        }

        if (!dbHelper.checkStudentExists(studentId)) {
            Toast.makeText(this, "Student ID " + studentId + " not found!", Toast.LENGTH_LONG).show();
            return;
        }

        boolean success = dbHelper.deleteStudent(studentId);

        if (success) {
            Toast.makeText(this, "Student deleted successfully!", Toast.LENGTH_SHORT).show();
            clearForm();
            loadStudents();
        } else {
            Toast.makeText(this, "Failed to delete student.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleViewStudents() {
        loadStudents();
        if (studentList.isEmpty()) {
            Toast.makeText(this, "No records found in database.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Showing " + studentList.size() + " student record(s).", Toast.LENGTH_SHORT).show();
            rvStudents.post(() -> rvStudents.requestFocus());
        }
    }

    private void loadStudents() {
        studentList = dbHelper.getAllStudents();
        adapter.updateData(studentList);

        if (studentList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvStudents.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvStudents.setVisibility(View.VISIBLE);
        }
    }

    private void clearForm() {
        etStudentName.setText("");
        etStudentId.setText("");
        etStudentName.setError(null);
        etStudentId.setError(null);
        sbStudentAge.setProgress(0);
        updateAgeDisplay(MIN_AGE);
    }
}
