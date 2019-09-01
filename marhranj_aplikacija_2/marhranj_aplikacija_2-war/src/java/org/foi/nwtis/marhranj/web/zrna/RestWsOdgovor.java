/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj.web.zrna;

/**
 *
 * @author marhranj
 * 
 * Klasa koja reprezentira odgovor servisa
 * 
 */
public class RestWsOdgovor {
    
    private Object odgovor;
    private String status;
    private String poruka;

    /**
     *
     * @param odgovor
     */
    public void setOdgovor(Object odgovor) {
        this.odgovor = odgovor;
    }
    
    /**
     *
     * @return 
     */
    public Object getOdgovor() {
        return odgovor;
    }

    /**
     *
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     *
     * @return 
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param poruka
     */
    public void setPoruka(String poruka) {
        this.poruka = poruka;
    }
    
    /**
     *
     * @return 
     */
    public String getPoruka() {
        return poruka;
    }
    
}
