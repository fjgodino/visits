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
public class Configuracion {
    
    private String urlServicios;
    private String ultimoUser;
    private String ultimaPassword;

    public Configuracion() {
        
        urlServicios =  "";
        ultimoUser =  "";
        ultimaPassword =  "";
    }

    
    public String getUrlServicios() {
        return urlServicios;
    }

    public void setUrlServicios(String urlServicios) {
        this.urlServicios = urlServicios;
    }

    public String getUltimoUser() {
        return ultimoUser;
    }

    public void setUltimoUser(String ultimoUser) {
        this.ultimoUser = ultimoUser;
    }

    public String getUltimaPassword() {
        return ultimaPassword;
    }

    public void setUltimaPassword(String ultimaPassword) {
        this.ultimaPassword = ultimaPassword;
    }

    @Override
    public String toString() {
        return "Configuracion{" + "urlServicios=" + urlServicios + ", ultimoUser=" + ultimoUser + ", ultimaPassword=" + ultimaPassword + '}';
    }
    
    
}
