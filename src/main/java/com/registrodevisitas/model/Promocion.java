/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.registrodevisitas.model;

/**
 *
 * @author jgodino
 */
public class Promocion {
    
    private String cod_promocion;
    private String descripcion;

    public Promocion() {
        cod_promocion = "";
        descripcion = "";
    }
 
    
    public String getCod_promocion() {
        return cod_promocion;
    }

    public void setCod_promocion(String cod_promocion) {
        this.cod_promocion = cod_promocion;
    }
 
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String promocion) {
        this.descripcion = promocion;
    }

    public void clear()
    {
        cod_promocion = "";
        descripcion = "";
    }

    @Override
    public String toString() {
        return "Promocion{" + "cod_promocion=" + cod_promocion + ", descripcion=" + descripcion + '}';
    }
    
    
}
