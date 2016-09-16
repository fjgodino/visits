/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.registrodevisitas.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 *
 * @author jgodino
 */
public class Medico implements Cloneable{
    
    private Integer codigoMedico;
    private String nombreMedico;

    public Medico() {
        clear();
    }
    
 
    public Medico(Integer codigoMedico, String nombreMedico) {
        this.codigoMedico = codigoMedico;
        this.nombreMedico = nombreMedico; 
    }

    public Integer getCodigoMedico() {
        return codigoMedico;
    }

    public void setCodigoMedico(Integer codigoMedico) {
        this.codigoMedico = codigoMedico;
    }

    public String getNombreMedico() {
        return nombreMedico;
    }

    public void setNombreMedico(String nombreMedico) {
        this.nombreMedico = nombreMedico;
    }
    
    public void clear() {
        this.nombreMedico = "";
        this.codigoMedico = 0;
    }

    @Override
    public String toString() {
        return   nombreMedico ;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
}
