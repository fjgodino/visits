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
public class ListaDeCausas {

    List<Causa> causas = new ArrayList<Causa>();

    public List<Causa> getCausas() {
        return causas;
    }

    public void setCausas(List<Causa> causa) {
        this.causas = causa;
    }

    public void sortCauses() {

        Collections.sort(causas, new Comparator<Causa>() {
            @Override
            public int compare(Causa p1, Causa p2) {
                return p1.getDescripcion().compareToIgnoreCase(p2.getDescripcion());
            }

        });
        
    }
    
     public Causa getCausaById(Integer id)
    {
        for(Causa causa: causas)
        {
            if(causa.getCod_causa() == id)
            {
                return causa;
            }
        }
        
        return null;
    }
     
}
