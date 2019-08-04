/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj.web;

import java.io.Serializable;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author marhranj
 * 
 * Klasa koja se koristi za lokalizaciju
 * 
 */
@Named(value = "lokalizacija")
@SessionScoped
public class Lokalizacija implements Serializable {
    
    private Locale locale;
    
    /**
     * Postavljanje standardnog jezika
     * 
     */
    @PostConstruct
    public void setUp() {
        promjeniJezik("hr");
    }
 
    /**
     * Metoda koja dodaje resourcima klasu u kojoj su smješteni REST servisi
     * 
     * @return 
     */
    public Locale getLocale() {
        return locale;
    }
 
    /**
     * Metoda u kojoj se mijenja jezik na temelju parametra
     * @param language
     * 
     */
    public void promjeniJezik(String language) {
        locale = new Locale(language);
    }
    
}
