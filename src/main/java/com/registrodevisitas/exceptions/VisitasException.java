/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.registrodevisitas.exceptions;

import java.util.concurrent.ExecutionException;

/**
 *
 * @author jgodino
 */
public class VisitasException extends Exception{

    
    public VisitasException() {
        super();

    }
    
    public VisitasException(String arg0) {
        super(arg0);

    }
    
    public VisitasException(String message, Exception ex) {
        super(message,ex);
    }
 
}
