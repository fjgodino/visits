/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.registrodevisitas.model;

import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;

/**
 *
 * @author jgodino
 */
public class Visita {

    private final javafx.beans.property.ObjectProperty<java.time.LocalDate> fechaDeLaVisita = new SimpleObjectProperty<>();
    private final javafx.beans.property.ObjectProperty<Medico> medico = new SimpleObjectProperty<>();
    private final StringProperty turnoVisita = new SimpleStringProperty();
    private final BooleanProperty visitaAcompanadaSN = new SimpleBooleanProperty();
    private final StringProperty lugarVisita = new SimpleStringProperty();
    private final javafx.beans.property.ObjectProperty<Causa> causa = new SimpleObjectProperty<>();
    private final javafx.beans.property.ObjectProperty<Promocion> promocion = new SimpleObjectProperty<>();
    private final StringProperty observacion = new SimpleStringProperty();
    private final BooleanProperty persistida = new SimpleBooleanProperty();
    private final javafx.beans.property.ObjectProperty<Node> persistidoGraf = new SimpleObjectProperty<>();
    private final javafx.beans.property.ObjectProperty<java.time.LocalDateTime> fechaCreacion = new SimpleObjectProperty<>();

    /* ============================================= */
    /* Constructor                                   */
    /* ============================================= */
    public Visita() {
        initialize();
    }

    /* ============================================= */
    /* Getter and Setters                            */
    /* ============================================= */
    /* ------ */
    /* MEDICO */
    /* ------ */
    public Medico getMedico() {
        return medico.get();
    }

    public void setMedico(Medico value) {
        medico.set(value);
    }

    public javafx.beans.property.ObjectProperty medicoProperty() {
        return medico;
    }

    /* ------ */
    /* FECHA  */
    /* ------ */
    public java.time.LocalDate getFechaDeLaVisita() {
        return fechaDeLaVisita.get();
    }

    public void setFechaDeLaVisita(java.time.LocalDate value) {
        fechaDeLaVisita.set(value);
    }

    public javafx.beans.property.ObjectProperty fechaDeLaVisitaProperty() {
        return fechaDeLaVisita;
    }

    /* ------ */
    /* TURNO  */
    /* ------ */
    public String getTurnoVisita() {
        return turnoVisita.get();
    }

    public void setTurnoVisita(String value) {
        turnoVisita.set(value);
    }

    public StringProperty turnoVisitaProperty() {
        return turnoVisita;
    }

    /* ----------------- */
    /* VISITA ACOMPAÑADA */
    /* ----------------- */
    public Boolean getVisitaAcompanadaSN() {
        return visitaAcompanadaSN.get();
    }

    public void setVisitaAcompanadaSN(Boolean value) {
        visitaAcompanadaSN.set(value);
    }

    public BooleanProperty visitaAcompanadaSN() {
        return visitaAcompanadaSN;
    }

    /* ------ */
    /* LUGAR  */
    /* ------ */
    public String getLugarVisita() {
        return lugarVisita.get();
    }

    public void setLugarVisita(String value) {
        lugarVisita.set(value);
    }

    public StringProperty lugarVisitaProperty() {
        return lugarVisita;
    }

    /* ----------------- */
    /* CAUSA             */
    /* ----------------- */
    public Causa getCausa() {
        return causa.get();
    }

    public void setCausa(Causa value) {
        causa.set(value);
    }

    public javafx.beans.property.ObjectProperty causaProperty() {
        return causa;
    }

    /* ----------------- */
    /* PROMOCION         */
    /* ----------------- */
    public Promocion getPromocion() {
        return promocion.get();
    }

    public void setPromocion(Promocion value) {
        promocion.set(value);
    }

    public javafx.beans.property.ObjectProperty promocionProperty() {
        return promocion;
    }

    /* ----------------- */
    /* OBSERVACION       */
    /* ----------------- */
    public String getObservacion() {
        return observacion.get();
    }

    public void setObservacion(String value) {
        observacion.set(value);
    }

    public StringProperty observacionProperty() {
        return observacion;
    }

    /* ----------------- */
    /* PERSISTIDA        */
    /* ----------------- */
    public Boolean getPersistida() {
        return persistida.get();
    }

    public void setPersistida(Boolean value) {
        persistida.set(value);
    }

    public BooleanProperty persistida() {
        return persistida;
    }

    /* ----------------- */
    /* persistidoGraf    */
    /* ----------------- */
    public void setPersistidoGraf(Node value) {
        persistidoGraf.set(value);
    }

    public javafx.beans.property.ObjectProperty persistidoGrafProperty() {
        return persistidoGraf;
    }

    /* --------- */
    /* CREACION  */
    /* --------- */
    public java.time.LocalDateTime getFechaCreacion() {
        return fechaCreacion.get();
    }

    public void setFechaCreacion(java.time.LocalDateTime value) {
        fechaCreacion.set(value);
    }

    public javafx.beans.property.ObjectProperty fechaCreacionProperty() {
        return fechaCreacion;
    }

    /* ============================================= */
    /* clean                                         */
    /* ============================================= */
    public void initialize() {

        fechaDeLaVisita.set(LocalDate.now());

        if (medico.get() != null) {
            medico.get().clear();
        } else {
            medico.set(new Medico());
        }

        turnoVisita.set("");
        visitaAcompanadaSN.set(false);
        lugarVisita.set("");
        if (causa.get() != null) {
            causa.get().clear();
        } else {
            causa.set(new Causa());
        }

        if (promocion.get() != null) {
            promocion.get().clear();
        } else {
            promocion.set(new Promocion());
        }

        observacion.set("");
        persistida.set(false);
        persistidoGraf.set(MaterialDesignIcon.SYNC_PROBLEM.graphic());

        fechaCreacion.set(LocalDateTime.now());

    }

    /* ============================================= */
    /* Formater                                      */
    /* ============================================= */
    public final String obtenerFechaFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy");
        return fechaDeLaVisita.get().format(formatter);
    }

    public final String obtenerFechaFormateadaSQL() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return fechaDeLaVisita.get().format(formatter);
    }

    public final String obtenerFechaCreacionFormateadaSQL() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return fechaCreacion.get().format(formatter);
    }

    public final String obtenerHoraCreacionFormateadaSQL() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return fechaCreacion.get().format(formatter);
    }

    public final String obtenerHFCreacionFormateadaSQL() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        return fechaCreacion.get().format(formatter);
    }
    

}
