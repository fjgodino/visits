/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.registrodevisitas.views;

import com.gluonhq.charm.glisten.control.CharmListCell;
import com.gluonhq.charm.glisten.control.ListTile;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.registrodevisitas.model.Visita;
import java.util.function.Consumer;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

/**
 *
 * @author jgodino
 */
public class VisitaCell extends CharmListCell<Visita> {

    private final ListTile tile;
    private Visita currentItem;

    public VisitaCell(Consumer<Visita> edit, Consumer<Visita> remove) {

        tile = new ListTile();
        tile.setPrimaryGraphic(MaterialDesignIcon.DESCRIPTION.graphic());

        Button btnEdit = MaterialDesignIcon.EDIT.button(e -> edit.accept(currentItem));

        Button btnRemove = MaterialDesignIcon.DELETE.button(e -> remove.accept(currentItem));
        HBox buttonBar = new HBox(0, btnEdit, btnRemove);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);

        tile.setSecondaryGraphic(buttonBar);

    }

    @Override
    public void updateItem(Visita item, boolean empty) {
        super.updateItem(item, empty);
        currentItem = item;
        if (!empty && item != null) {
            update();
            tile.primaryGraphicProperty().bind(item.persistidoGrafProperty());
            setGraphic(tile);
        } else {
            setGraphic(null);
        }
    }

    private void update() {
        if (currentItem != null) {
            tile.textProperty().setAll(currentItem.getMedico().getNombreMedico(),
                    currentItem.getLugarVisita(),
                    currentItem.obtenerFechaFormateada());
        } else {
            tile.textProperty().clear();
        }
    }

}
