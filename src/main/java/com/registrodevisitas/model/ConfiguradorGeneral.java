/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.registrodevisitas.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author jgodino
 */
public class ConfiguradorGeneral {
    
    private final StringProperty urlServicios = new SimpleStringProperty();
    private final StringProperty ultimoUser = new SimpleStringProperty();
    private final StringProperty ultimaPassword = new SimpleStringProperty();

   /* ------ */
    /* URL  */
    /* ------ */
    
    public String getUrlServicios() {
        return urlServicios.get();
    }

    public void setUrlServicios(String value) {
        urlServicios.set(value);
    }

    public StringProperty urlServiciosProperty() {
        return urlServicios;
    }

    /* ------ */
    /* USER  */
    /* ------ */
    
    public String getUltimoUser() {
        return ultimoUser.get();
    }

    public void setUltimoUser(String value) {
        ultimoUser.set(value);
    }

    public StringProperty ultimoUserServiciosProperty() {
        return ultimoUser;
    }
    
    /* ------ */
    /* USER  */
    /* ------ */
    
    public String getUltimaPassword() {
        return ultimaPassword.get();
    }

    public void setUltimaPassword(String value) {
        ultimaPassword.set(value);
    }

    public StringProperty ultimaPasswordServiciosProperty() {
        return ultimaPassword;
    }
    
    
}
