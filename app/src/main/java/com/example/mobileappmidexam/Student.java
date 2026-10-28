package com.example.mobileappmidexam;

public class Student {
    private int id;
    private String studentId;
    private String name;
    private int age;

    public Student() {
    }

    public Student(int id, String studentId, String name, int age) {
        this.id = id;
        this.studentId = studentId;
        this.name = name;
        this.age = age;
    }

    public Student(String studentId, String name, int age) {
        this.studentId = studentId;
        this.name = name;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "ID: " + studentId + " | Name: " + name + " | Age: " + age;
    }
}
