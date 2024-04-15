package com.example.Othellodifficult.base;


import java.util.ArrayList;
import java.util.List;

public class GenericTest {
    public static void main(String[] args) {
        List<C> c = new ArrayList<>();
        c.add(new C());
        c.add(new C());
        c.add(new C());
        c.add(new C());

        FakeData.get(c);
    }
}

class FakeData {
    public static <T extends A> void get(List<T> a){
        for (T t : a){
            t.feed();
        }
    }
}

class A {
    public void feed() {
        System.out.println("A feed");
    }
}

class B extends A {
    @Override
    public void feed() {
        System.out.println("B feed");
    }
}

class C extends B {
    @Override
    public void feed() {
        System.out.println("C feed");
    }
}
