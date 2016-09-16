/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.registrodevisitas.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author jgodino
 */
public class ListaDePromociones {
   
    List<Promocion> promociones = new ArrayList<Promocion>();

    public List<Promocion> getPromociones() {
        return promociones;
    }

    public void setPromociones(List<Promocion> promocion) {
        this.promociones = promocion;
    }
    
    public void sortPromociones() {

        Collections.sort(promociones, new Comparator<Promocion>() {
            @Override
            public int compare(Promocion p1, Promocion p2) {
                return p1.getDescripcion().compareToIgnoreCase(p2.getDescripcion());
            }

        });
        
    }

    public void sortPromociones(ListaDePromociones lista) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public Promocion getPromocionById(String id)
    {
        for(Promocion promocion: promociones)
        {
            if(promocion.getCod_promocion().compareTo(id)==0)
            {
                return promocion;
            }
        }
        
        return null;
    }
}
