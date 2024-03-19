package com.example.Othellodifficult.base.factory_method;

public class Teacher implements IUser{
    @Override
    public void sayHello() {
        System.out.println("Teacher say hi");
    }

    @Override
    public void sayBye() {
        System.out.println("Teacher say bye");
    }
}
