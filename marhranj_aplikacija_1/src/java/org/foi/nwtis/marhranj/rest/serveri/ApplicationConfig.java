/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj.rest.serveri;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author marhranj
 * 
 * Klasa koja sluzi za konfiguraciju svih REST servisa
 * 
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Metoda koja dodaje resourcima klasu u kojoj su smješteni REST servisi
     * @param resources
     * 
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(org.foi.nwtis.marhranj.rest.serveri.AIRP2REST.class);
    }
    
}
