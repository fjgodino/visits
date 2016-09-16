/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.registrodevisitas.services;

import com.gluonhq.charm.down.common.PlatformFactory;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.connect.ConnectState;
import com.gluonhq.connect.GluonObservableList;
import com.gluonhq.connect.provider.DataProvider;
import com.gluonhq.connect.provider.RestClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.registrodevisitas.model.Causa;
import com.registrodevisitas.model.ConfiguradorGeneral;
import com.registrodevisitas.model.ListaDeCausas;
import com.registrodevisitas.model.ListaDeMedicos;
import com.registrodevisitas.model.ListaDePromociones;
import com.registrodevisitas.model.Medico;
import com.registrodevisitas.model.Promocion;
import com.registrodevisitas.model.Visita;
import com.registrodevisitas.model.VisitaDB;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javax.inject.Inject;

/**
 *
 * @author jgodino
 */
public class ListaVisitasServices {

    private static final Logger log = Logger.getLogger(ListaVisitasServices.class.getName());
    private final IntegerProperty retrieved = new SimpleIntegerProperty(0);

    private final Integer lastId = new Integer(0);

    @Inject
    private ConfiguradorGeneralService configuracion;

    @Inject
    private UserServices userService;

    @Inject
    private MedicosServices medicosServices;

    @Inject
    private CausasServices causasServices;

    @Inject
    private PromocionesServices promocionesServices;

    private GluonObservableList<Visita> listaDeVisitas;
    private GluonObservableList<VisitaDB> listaDeVisitasDb;

    public ListaVisitasServices() {

        listaDeVisitas = new GluonObservableList<Visita>();
    }

    public GluonObservableList<Visita> getListaDeVisitas() {
        return listaDeVisitas;
    }

    public void addVisita(Visita visita, int modo) {

        listaDeVisitas.add(visita);
    }

    public void iniciarLista() {

        log.info("ListaVisitasServices:iniciarLista: BEGIN");

        listaDeVisitasDb = new GluonObservableList<VisitaDB>();

        if (userService.isConnected()) {

            try {

                ConfiguradorGeneral configGral = configuracion.settingsProperty().get();

                String urlToConnect = configGral.getUrlServicios();

                log.log(Level.INFO, "UserServices:validateUser: URL" + urlToConnect);

                /* ========================================================= */
                /* Genera JSON visita                                        */
                /* ========================================================= */
                String jsonRequest = "{\"action\":\"G\",\"username\":\"" + userService.getUser().get().getUsername() + "\",\"idfrom\":" + lastId + ",\"qtyrecords\":10}";

                RestClient restClient = RestClient.create()
                        .method("POST")
                        .host(urlToConnect)
                        .path("/serviceVisitas")
                        .contentType("application/json")
                        .dataString(jsonRequest);

                listaDeVisitasDb = DataProvider.retrieveList(restClient.createListDataReader(VisitaDB.class));

                listaDeVisitasDb.stateProperty().addListener((observable, oldState, newState) -> {

                    /* Evento que se genera por cada estado del proceso */
                    if (newState == ConnectState.FAILED) {

                        if (listaDeVisitasDb.getException() != null) {
                            log.info("PromocionesServices:generarListaDePromocionesDeServer:Error Recuperando Promociones  ");

                        }
                        retrieved.set(4);
                    }

                    if (newState == ConnectState.SUCCEEDED) {

                        for (VisitaDB visitaDb : listaDeVisitasDb) {

                            Medico medico = medicosServices.getMedicoById(visitaDb.getMedico());

                            Visita visita = new Visita();
                            visita.setMedico(medico);

                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            visita.setFechaDeLaVisita(LocalDate.parse(visitaDb.getFecha_visita(), formatter));

                            visita.setTurnoVisita(visitaDb.getTurno());

                            visita.setVisitaAcompanadaSN((visitaDb.getSupervision().compareTo("true") == 0));

                            visita.setLugarVisita(visitaDb.getLugar());

                            visita.setCausa(causasServices.getCausaById(visitaDb.getCod_causa()));

                            visita.setPromocion(promocionesServices.getPromocionesById(visitaDb.getPromocion()));

                            visita.setObservacion(visitaDb.getObservacion());

                            visita.setPersistida(Boolean.TRUE);

                            visita.setPersistidoGraf(MaterialDesignIcon.DONE.graphic());

                            DateTimeFormatter formatterDateTime = DateTimeFormatter.ofPattern("yyyy-MM-ddHH:mm:ss");

                            visita.setFechaCreacion(LocalDateTime.parse(visitaDb.getuDate().concat(visitaDb.getuTime()), formatterDateTime));

                            listaDeVisitas.add(visita);

                        }
                        
                        listaDeVisitasDb.clear();

                        retrieved.set(1);
                    }

                });

            } catch (Exception e) {

                log.log(Level.SEVERE, e.getMessage(), e);

                retrieved.set(5);

            }

        }

        /* =========================== */
        /* Recuepar Visitas Pendientes */
        /* =========================== */
        try {

            File folder = PlatformFactory.getPlatform().getPrivateStorage();
            File[] listOfFiles = folder.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {
                File file = listOfFiles[i];

                if (file.isFile() && file.getName().startsWith("visita_")) {

                    InputStream input = new FileInputStream(file.getCanonicalPath());

                    JsonReader reader = new JsonReader(new InputStreamReader(input, "UTF-8"));

                    Gson gson = new GsonBuilder().create();

                    VisitaDB visitaDb = gson.fromJson(reader, VisitaDB.class);

                    listaDeVisitasDb.add(visitaDb);

                }
            }

            for (VisitaDB visitaDb : listaDeVisitasDb) {

                Medico medico = medicosServices.getMedicoById(visitaDb.getMedico());

                Visita visita = new Visita();
                visita.setMedico(medico);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                visita.setFechaDeLaVisita(LocalDate.parse(visitaDb.getFecha_visita(), formatter));

                visita.setTurnoVisita(visitaDb.getTurno());

                visita.setVisitaAcompanadaSN((visitaDb.getSupervision().compareTo("S") == 0));

                visita.setLugarVisita(visitaDb.getLugar());

                Causa causa = causasServices.getCausaById(visitaDb.getCod_causa());
 
                visita.setCausa(causa);

                Promocion promocion = promocionesServices.getPromocionesById(visitaDb.getPromocion());
                
                visita.setPromocion(promocion);

                visita.setObservacion(visitaDb.getObservacion());

                visita.setPersistida(Boolean.TRUE);

                visita.setPersistidoGraf(MaterialDesignIcon.SYNC_PROBLEM.graphic());

                DateTimeFormatter formatterDateTime = DateTimeFormatter.ofPattern("yyyy-MM-ddHH:mm:ss");

                visita.setFechaCreacion(LocalDateTime.parse(visitaDb.getuDate().concat(visitaDb.getuTime()), formatterDateTime));

                listaDeVisitas.add(visita);

            }
            
            listaDeVisitasDb.clear();

        } catch (IOException ex) {

            Logger.getLogger(ListaVisitasServices.class.getName()).log(Level.SEVERE, null, ex);
        }

        log.info("ListaVisitasServices:iniciarLista: END");
    }

     
}
