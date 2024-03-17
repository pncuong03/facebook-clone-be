package com.example.Othellodifficult.base.factory_method;

public class StudentCreator extends UserFactory {
    @Override
    public IUser getInstance() {
        return new Student();
    }
}
