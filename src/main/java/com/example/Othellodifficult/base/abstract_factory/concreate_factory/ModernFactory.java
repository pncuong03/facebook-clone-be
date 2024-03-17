package com.example.Othellodifficult.base.abstract_factory.concreate_factory;

import com.example.Othellodifficult.base.abstract_factory.FurnitureAbstractFactory;
import com.example.Othellodifficult.base.abstract_factory.concrete_product.Chair;
import com.example.Othellodifficult.base.abstract_factory.concrete_product.ModernChair;

public class ModernFactory implements FurnitureAbstractFactory {
    @Override
    public Chair getChair() {
        return new ModernChair();
    }
}
