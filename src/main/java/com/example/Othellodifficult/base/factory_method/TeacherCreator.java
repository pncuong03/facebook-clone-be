package com.example.Othellodifficult.base.factory_method;

public class TeacherCreator extends UserFactory {
    @Override
    public IUser getInstance() {
        return new Teacher();
    }
}
