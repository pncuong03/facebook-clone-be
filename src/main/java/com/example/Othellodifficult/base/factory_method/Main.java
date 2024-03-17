package com.example.Othellodifficult.base.factory_method;

public class Main {
    public static void main(String[] args) {
        UserFactory creator = null;
//        String type = "STUDENT";
        String type = "TEACHER";

        switch (type){
            case "STUDENT":
                creator = new StudentCreator();
                break;
            case "TEACHER":
                creator = new TeacherCreator();
                break;
            default:
                System.out.println("WRONG!!!");
        }
        
        creator.executive();
    }
}
