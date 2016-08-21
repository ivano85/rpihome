/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.ivano85.rpihome.now;

import javafx.fxml.FXML;
import net.objectof.actof.widgets.masonry.MasonryPane;

/**
 *
 * @author ivano
 */
public class CardsController {
    
    private Main main;
    
    @FXML
    private MasonryPane cardsContainer;
    
    @FXML
    public void initialize() {
        
    }
    
    public void addCard(Card card) {
        cardsContainer.getChildren().add(card.getNode());
        cardsContainer.layout();
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }
    
}
