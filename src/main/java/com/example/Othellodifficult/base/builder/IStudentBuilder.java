package com.example.Othellodifficult.base.builder;

public interface IStudentBuilder {
    Student.StudentBuilder id(Long id);
    Student.StudentBuilder name(String name);
    Student build();
}
