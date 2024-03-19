package com.example.Othellodifficult.base.abstract_factory;

import com.example.Othellodifficult.base.abstract_factory.concreate_factory.ArtFactory;
import com.example.Othellodifficult.base.abstract_factory.concreate_factory.ModernFactory;
import com.example.Othellodifficult.base.abstract_factory.concrete_product.Chair;

public class Main {
    public static void main(String[] args) {
//        String type = "Art";
        String type = "Modern";
        FurnitureAbstractFactory furnitureAbstractFactory = null;
        switch (type) {
            case "Art":
                furnitureAbstractFactory = new ArtFactory();
                break;
            case "Modern":
                furnitureAbstractFactory = new ModernFactory();
                break;
        }

        Chair chair = furnitureAbstractFactory.getChair();
        chair.executive();
    }
}
