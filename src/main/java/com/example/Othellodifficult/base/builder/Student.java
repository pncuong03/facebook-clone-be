package com.example.Othellodifficult.base.builder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Student {
    private Long id;
    private String name;

    public static StudentBuilder builder(){
        return new StudentBuilder();
    }

    public static class StudentBuilder implements IStudentBuilder {
        private final Student student;

        public StudentBuilder (){
            student = new Student();
        }

        @Override
        public StudentBuilder id(Long id) {
            this.student.setId(id);
            return this;
        }

        @Override
        public StudentBuilder name(String name) {
            this.student.setName(name);
            return this;
        }

        @Override
        public Student build() {
            return this.student;
        }
    }
}
