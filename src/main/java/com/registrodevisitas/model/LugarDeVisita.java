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
public class LugarDeVisita {
    
    private String lugarDeVisita;

    public LugarDeVisita(String lugarDeVisita) {
        this.lugarDeVisita = lugarDeVisita;
    }

    
    public String getLugarDeVisita() {
        return lugarDeVisita;
    }

    public void setLugarDeVisita(String lugarDeVisita) {
        this.lugarDeVisita = lugarDeVisita;
    }

    @Override
    public String toString() {
        return "LugarDeVisita{" + "lugarDeVisita=" + lugarDeVisita + '}';
    }
    
}
