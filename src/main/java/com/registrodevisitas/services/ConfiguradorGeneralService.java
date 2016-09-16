/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.registrodevisitas.services;

import com.gluonhq.charm.down.common.PlatformFactory;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.registrodevisitas.model.Configuracion;
import com.registrodevisitas.model.ConfiguradorGeneral;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author jgodino
 */
public class ConfiguradorGeneralService {
    
    private static final String VISITAS_SETTINGS = "reg-visitas-settings";
    private final ObjectProperty<ConfiguradorGeneral> settings = new SimpleObjectProperty<>(new ConfiguradorGeneral());
    private final Logger logger = Logger.getLogger(ConfiguradorGeneralService.class.getName());
     
    // ==========================================
    // Recuperacion del Setting
    // ==========================================
    
    public void retrieveSettings() {

        logger.log(Level.INFO, "ConfiguradorGeneralService:retrieveSettings: BEGIN ");

        Configuracion configuracion = new Configuracion();

        try {
            
            String fileName = PlatformFactory.getPlatform().getPrivateStorage() + File.separator + VISITAS_SETTINGS +".json";
            
            File configFile = new File(fileName);

            logger.log(Level.INFO, "ConfiguradorGeneralService:retrieveSettings: folder: " + configFile.getAbsolutePath());

            if (configFile.exists()) {

                Gson gson = new Gson();

                JsonReader jsonReader = new JsonReader(new FileReader(fileName));

                final Configuracion respuesta = gson.fromJson(jsonReader, Configuracion.class);

                settings.get().setUrlServicios(respuesta.getUrlServicios());
                settings.get().setUltimoUser(respuesta.getUltimoUser());
                settings.get().setUltimaPassword(respuesta.getUltimaPassword());
                
                 jsonReader.close();

            } else {
                settings.get().setUrlServicios("");
            }
            
            logger.log(Level.INFO, "ConfiguradorGeneralService:retrieveSettings: Config: " + settings.toString());
 

        } catch (IOException ex) {
            settings.get().setUrlServicios("");
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }

        logger.log(Level.INFO, "ConfiguradorGeneralService:retrieveSettings: END ");

    }
    
    public void storeSettings() {

        logger.log(Level.INFO, "ConfiguradorGeneralService:storeSettings: BEGIN ");

        try {
            
            String fileName = PlatformFactory.getPlatform().getPrivateStorage() + File.separator + "configuracion_visitas.json";
             
            Configuracion configuracion = new Configuracion();
            configuracion.setUrlServicios(settings.get().getUrlServicios());
            configuracion.setUltimoUser(settings.get().getUltimoUser());
            configuracion.setUltimaPassword(settings.get().getUltimaPassword());
            
            Gson gson = new Gson();
            
            OutputStream out = new FileOutputStream(fileName);
            
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.setIndent("  ");

            gson.toJson(configuracion,Configuracion.class,writer);

            writer.close();
            
            logger.log(Level.INFO, "ConfiguradorGeneralService:storeSettings: folder: " +PlatformFactory.getPlatform().getPrivateStorage().getAbsolutePath());
 
          

        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        logger.log(Level.INFO, "ConfiguradorGeneralService:storeSettings: END ");
    }
    
    public ObjectProperty<ConfiguradorGeneral> settingsProperty() {
        return settings;
    }
    
      

}
