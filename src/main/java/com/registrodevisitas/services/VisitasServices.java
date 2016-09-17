/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.registrodevisitas.services;

import com.gluonhq.charm.down.common.PlatformFactory;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.connect.ConnectState;
import com.gluonhq.connect.GluonObservableObject;
import com.gluonhq.connect.provider.DataProvider;
import com.gluonhq.connect.provider.RestClient;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import com.registrodevisitas.model.ConfiguradorGeneral;
import com.registrodevisitas.model.Visita;
import com.registrodevisitas.model.VisitaDB;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 *
 * @author jgodino
 */
public class VisitasServices {

    private Logger log = Logger.getLogger(VisitasServices.class.getName());
    private final ObjectProperty<Visita> visita = new SimpleObjectProperty<>();
    private GluonObservableObject<VisitaDB> visitaDataBase;
    private int mode;

    @Inject
    ListaVisitasServices listaDeVisitas;

    @Inject
    UserServices user;

    @Inject
    private UserServices userService;

    @Inject
    private ConfiguradorGeneralService configuracion;

    @PostConstruct
    public void postConstruct() {
        visita.setValue(new Visita());
        mode = 0;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public ObjectProperty<Visita> getVisita() {
        return visita;
    }

    public void createVisita() {
        visita.set(new Visita());
    }

    public void setVisita(Visita visitaAAgregar) {
        visita.set(visitaAAgregar);
    }

    public void cleanVisita() {
        visita.get().initialize();

    }

    public void persistirVisita() {

        listaDeVisitas.addVisita(visita.get());

        if (userService.isConnected()) {

            saveVisita(visita.get(), "ADD");

        } else {

            persistLocally(getVisitaDBFormat(visita.get()));

            MobileApplication.getInstance().showMessage("Visita Registrada Localmente");

        }

    }

    public void actualizarVisita() {

        listaDeVisitas.removeVisita(visita.get());

        if (userService.isConnected()) {

            if (visita.get().getPersistida().booleanValue()) {

                saveVisita(visita.get(), "UPDATE");

            }

        } else {
            persistLocally(getVisitaDBFormat(visita.get()));

            MobileApplication.getInstance().showMessage("Visita Actualizada Localmente");

            Platform.runLater(() -> {

                listaDeVisitas.addVisita(visita.get());

            });
        }

    }

    public void eliminarVisita() {

        if (userService.isConnected()) {

            if (visita.get().getPersistida().booleanValue()) {

                saveVisita(visita.get(), "DELETE");

            }

        } else {

            try {

                removeLocally(getVisitaDBFormat(visita.get()));
                MobileApplication.getInstance().showMessage("Visita Eliminada Localmente");
                listaDeVisitas.removeVisita(visita.get());

            } catch (IOException ex) {
                MobileApplication.getInstance().showMessage("No se pudo Eliminar");
                log.log(Level.SEVERE, null, ex);

            }

        }

    }

    public void saveVisita(Visita visita, String mode) {

        log.info("VisitasServices:saveVisita: BEGIN");

        /* ------------------ */
 /* String de Conexión */
 /* ------------------ */
        try {

            ConfiguradorGeneral configGral = configuracion.settingsProperty().get();

            if (configGral.getUrlServicios() == null) {
                configuracion.retrieveSettings();
            }

            String urlToConnect = configGral.getUrlServicios();

            log.log(Level.INFO, "VisitasServices:saveVisita: URL" + urlToConnect);

            /* ========================================================= */
 /* Genera JSON visita                                        */
 /* ========================================================= */
            Gson gson = new Gson();

            VisitaDB visitaDb = getVisitaDBFormat(visita);

            String jsonRequest = gson.toJson(visitaDb);

            /* ========================================================= */
 /* create a RestClient to the specific URL                   */
 /* ========================================================= */
            RestClient restClient = RestClient.create()
                    .method("POST")
                    .host(urlToConnect)
                    .path("/serviceVisitas")
                    .contentType("application/json")
                    .dataString(jsonRequest);

            visitaDataBase = DataProvider.retrieveObject(restClient.createObjectDataReader(VisitaDB.class));

            visitaDataBase.initializedProperty().addListener((observable, ov, nv) -> {

                if (nv) {

                    log.info("VisitasServices:saveVisita: RestFull finalizado");

                }
            });

            visitaDataBase.stateProperty().addListener((observable, oldState, newState) -> {

                /* Evento que se genera por cada estado del proceso */
                if (newState == ConnectState.FAILED) {

                    MobileApplication.getInstance().showMessage("Error de conexión");

                    persistLocally(visitaDb);

                } else if (newState == ConnectState.SUCCEEDED) {

                    log.info("VisitasServices:saveVisita: " + (visitaDataBase.get()).getAction());

                    if ((visitaDataBase.get()).getAction().compareTo("OK") == 0) {

                        visita.setPersistidoGraf(MaterialDesignIcon.DONE.graphic());
                        MobileApplication.getInstance().showMessage("Visita Registrada");
                    }

                } else if (newState == ConnectState.READY) {

                    log.info("VisitasServices:saveVisita: ConnectState.READY ");

                } else if (newState == ConnectState.REMOVED) {

                    log.info("VisitasServices:saveVisita: ConnectState.REMOVED ");

                }

            });

        } catch (Exception e) {

            log.log(Level.SEVERE, e.getMessage(), e);

        }

        log.info("VisitasServices:saveVisita END");

    }

    private VisitaDB getVisitaDBFormat(Visita visita) {

        VisitaDB visitaDb = new VisitaDB();
        visitaDb.setAction("A");
        visitaDb.setMedico(visita.getMedico().getCodigoMedico());
        visitaDb.setApm(user.getUser().get().getApm());
        visitaDb.setLugar(visita.getLugarVisita());
        visitaDb.setFecha_visita(visita.obtenerFechaFormateadaSQL());
        visitaDb.setTurno(visita.getTurnoVisita());
        visitaDb.setSupervision((visita.getVisitaAcompanadaSN().booleanValue() ? "S" : ""));
        visitaDb.setPromocion(visita.getPromocion().getCod_promocion());
        visitaDb.setCod_causa(visita.getCausa().getCod_causa());
        visitaDb.setObservacion(visita.getObservacion());
        visitaDb.setStatus(".");
        visitaDb.setTipo_visita("M");
        visitaDb.setUsername(user.getUser().get().getUsername());
        visitaDb.setuDate(visita.obtenerFechaCreacionFormateadaSQL());
        visitaDb.setuTime(visita.obtenerHoraCreacionFormateadaSQL());

        return visitaDb;

    }

    private void removeLocally(VisitaDB visitaDb) throws IOException {

        String fileName = PlatformFactory.getPlatform().getPrivateStorage() + File.separator + "visita_" + visita.get().obtenerHFCreacionFormateadaSQL() + ".json";

        File fileLocal = new File(fileName);

        fileLocal.delete();

    }

    private void persistLocally(VisitaDB visitaDb) {

        log.log(Level.INFO, "VisitasServices:persistLocally: BEGIN ");

        try {

            String fileName = PlatformFactory.getPlatform().getPrivateStorage() + File.separator + "visita_" + visita.get().obtenerHFCreacionFormateadaSQL() + ".json";

            OutputStream out = new FileOutputStream(fileName);

            Gson gson = new Gson();

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.setIndent("  ");

            gson.toJson(visitaDb, VisitaDB.class, writer);

            writer.close();

            log.log(Level.INFO, "VisitasServices:persistLocally: file: " + fileName);

        } catch (IOException ex) {
            log.log(Level.SEVERE, null, ex);
        }

        log.log(Level.INFO, "VisitasServices:persistLocally: END ");
    }

}
