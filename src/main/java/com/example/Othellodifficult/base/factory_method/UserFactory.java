package com.example.Othellodifficult.base.factory_method;

public abstract class UserFactory {
    private IUser iUser;

    public void executive(){
        iUser = getInstance();
        iUser.sayHello();
        iUser.sayBye();
    }

    public abstract IUser getInstance(); // factory method
}
