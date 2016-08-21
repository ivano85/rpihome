/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.ivano85.rpihome.now;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

/**
 *
 * @author ivano
 */
public class Card {
    
    private final String fxmlName;
    private final ClassLoader classLoader;
    private final Node node;
    
    public Card(ClassLoader classLoader, String fxmlName) throws IOException {
        
        this.fxmlName = fxmlName;
        this.classLoader = classLoader;
        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(classLoader.getResource(fxmlName));
        loader.setClassLoader(classLoader);
        node = loader.load();
        
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
        
        node.setEffect(dropShadow);
        
        
    }

    public String getFxmlName() {
        return fxmlName;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public Node getNode() {
        return node;
    }
    
}
