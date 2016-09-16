/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.registrodevisitas.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jgodino
 */
public class ListaDeMedicos {
   
    List<Medico> medicos = new ArrayList<Medico>();

    public List<Medico> getMedicos() {
        return medicos;
    }

    public void setMedicos(List<Medico> medicos) {
        this.medicos = medicos;
    }
    
    
    public Medico getMedicoById(Integer id)
    {
        for(Medico medico: medicos)
        {
            if(medico.getCodigoMedico() == id)
            {
                return medico;
            }
        }
        
        return null;
    }
    
    
    
}
