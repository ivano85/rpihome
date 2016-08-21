/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.ivano85.rpihome.now.cards;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

/**
 *
 * @author ivano
 */
public class TestController {
    
    @FXML
    private Label title;
    
    @FXML
    private ProgressIndicator progress;
    
    @FXML
    public void initialize() {
        title.setText("Energia attuale");
        progress.setProgress(Math.random());
    }
    
    @FXML
    public void update() {
        progress.setProgress(Math.random());
    }
    
}
