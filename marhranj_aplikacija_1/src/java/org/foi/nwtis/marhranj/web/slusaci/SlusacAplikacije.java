/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj.web.slusaci;

import java.io.File;
import java.util.Objects;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.marhranj.konfiguracije.GeneralnaKonfiguracija;
import org.foi.nwtis.marhranj.web.dretve.PreuzimanjeAviona;

/**
 *
 * @author marhranj
 * 
 * Klasa koja se pokreće prilikom pokretanja aplikacije
 * 
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {
    
    public static String KONFIGURACIJA_IME_ATRIBUTA = "Konfiguracija";
    
    private static ServletContext servletContext;
    private static PreuzimanjeAviona preuzimanjeAvionaDretva;
    
    private volatile static boolean stopirano;
    private volatile static boolean pauzirano;
    
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {        
        servletContext = servletContextEvent.getServletContext();
        String putanja = servletContext.getRealPath("/WEB-INF");
        String datoteka = putanja + File.separator + servletContext.getInitParameter("konfiguracija");
            
        GeneralnaKonfiguracija konfiguracija = new GeneralnaKonfiguracija(datoteka);
        servletContext.setAttribute(KONFIGURACIJA_IME_ATRIBUTA, konfiguracija);
        preuzimanjeAvionaDretva = new PreuzimanjeAviona(konfiguracija);    
        preuzimanjeAvionaDretva.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (Objects.nonNull(preuzimanjeAvionaDretva)) {
            preuzimanjeAvionaDretva.interrupt(); 
        }
        ServletContext sc = sce.getServletContext();
        sc.removeAttribute(KONFIGURACIJA_IME_ATRIBUTA);
    }
    
    /**
     * Metoda koja vraća ServletContext
     * 
     * @return 
     */
    public static ServletContext getServletContext() {
        return servletContext;
    }
    
    public static void setStopirano(boolean stopirano) {
        SlusacAplikacije.stopirano = stopirano;
    }
    
    public static boolean getStopirano() {
        return stopirano;
    }
    
    public static void setPauzirano(boolean pauzirano) {
        SlusacAplikacije.pauzirano = pauzirano;
    }
    
    public static boolean getPauzirano() {
        return pauzirano;
    }
    
}
