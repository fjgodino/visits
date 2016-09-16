/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.registrodevisitas.services;

import com.gluonhq.connect.GluonObservableList;
import com.registrodevisitas.model.LugarDeVisita;

/**
 *
 * @author jgodino
 */
public class LugaresDeVisitasServices {

    private GluonObservableList<LugarDeVisita> lugarDeVisitas;
    
    public LugaresDeVisitasServices() {
        
        lugarDeVisitas = new GluonObservableList<LugarDeVisita>();
        
         lugarDeVisitas.add(new LugarDeVisita("Hospital"));
         lugarDeVisitas.add(new LugarDeVisita("Consultorio"));
         lugarDeVisitas.add(new LugarDeVisita("Instituto"));
        
    }
    
    public GluonObservableList<LugarDeVisita> obtenerLugaresDeVisitas()
    {        
        return lugarDeVisitas;
        
    }
    
}
