package com.example.Othellodifficult.base.factory_method;

public class Student implements IUser{
    @Override
    public void sayHello() {
        System.out.println("Student say hi");
    }

    @Override
    public void sayBye() {
        System.out.println("Student say bye");
    }
}
