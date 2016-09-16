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
public class Causa {
    
    private Integer cod_causa;
    private String descripcion;

    public Causa() {
        cod_causa = 0;
        descripcion = "";
    }

    
    public Integer getCod_causa() {
        return cod_causa;
    }

    public void setCod_causa(Integer cod_causa) {
        this.cod_causa = cod_causa;
    } 
    
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String causa) {
        this.descripcion = causa;
    }
    
    public void clear()
    {
        cod_causa = 0;
        descripcion = "";
    }

    @Override
    public String toString() {
        return "Causa{" + "cod_causa=" + cod_causa + ", descripcion=" + descripcion + '}';
    }
 
}
