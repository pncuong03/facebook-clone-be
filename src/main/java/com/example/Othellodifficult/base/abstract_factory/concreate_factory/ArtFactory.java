package com.example.Othellodifficult.base.abstract_factory.concreate_factory;

import com.example.Othellodifficult.base.abstract_factory.FurnitureAbstractFactory;
import com.example.Othellodifficult.base.abstract_factory.concrete_product.ArtChair;
import com.example.Othellodifficult.base.abstract_factory.concrete_product.Chair;

// Concrete Factory (Art)
public class ArtFactory implements FurnitureAbstractFactory {
    @Override
    public Chair getChair() {
        return new ArtChair();
    }
}
