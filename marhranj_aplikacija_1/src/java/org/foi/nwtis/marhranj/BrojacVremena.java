/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj;

import java.sql.Timestamp;

/**
 *
 * @author marhranj
 */
public class BrojacVremena {
    
    private long vrijemeInicijalizacije;
    
    public BrojacVremena() {
        vrijemeInicijalizacije = System.currentTimeMillis();
    }
    
    public long dohvatiVrijemeProsloOdInicijalizacije() {
        return System.currentTimeMillis() - vrijemeInicijalizacije;
    }
    
    
}
