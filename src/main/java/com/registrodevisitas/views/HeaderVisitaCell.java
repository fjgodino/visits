/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.registrodevisitas.views;

import com.gluonhq.charm.glisten.control.CharmListCell;
import com.registrodevisitas.model.Visita;
import javafx.scene.control.Label;
 

/**
 *
 * @author jgodino
 */
public class HeaderVisitaCell  extends CharmListCell<Visita> {
    
    private final Label label;
    private Visita currentItem;
   

    public HeaderVisitaCell() {
        label = new Label();
         
    }

    @Override
    public void updateItem(Visita item, boolean empty) {
        super.updateItem(item, empty);
        currentItem = item;
        if (!empty && item != null) {
            update();
            setGraphic(label);
        } else {
            setGraphic(null);
        }
    }

    private void update() {
        if (currentItem != null) {
            label.setText(currentItem.obtenerFechaFormateada());
        } else {
            label.setText("");
        }
    }
    
}
