/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import org.foi.nwtis.marhranj.konfiguracije.GeneralnaKonfiguracija;
import org.foi.nwtis.marhranj.web.slusaci.SlusacAplikacije;

/**
 *
 * @author marhranj
 * 
 * Singleton klasa koja pruža metode za povezivanje na bazu podataka
 * 
 */
public final class KonektorBazePodataka {
    
    private static Connection konekcija;
    private static GeneralnaKonfiguracija konfiguracija;
    
    /**
     * Metoda koja vraća konekciju na bazu podataka
     * @return
     * @throws java.sql.SQLException
     */
    public static Connection dajKonekciju() throws SQLException {
        if (Objects.isNull(konekcija) || konekcija.isClosed()) {
            uspostaviKonekcijuNaBazuPodataka();
        }
        return konekcija;
    }
    
    /**
     * Metoda koja uspostavlja konekciju na bazu podataka
     * 
     */
    private static void uspostaviKonekcijuNaBazuPodataka() throws SQLException {
        postaviKonfiguraciju();
        konekcija = DriverManager.getConnection(konfiguracija.getServer(), konfiguracija.getUserUsername(), konfiguracija.getUserPassword());
    }
    
    /**
     * Metoda koja postavlja generalnu konfiguraciju
     * 
     */
    private static void postaviKonfiguraciju() {
        if (Objects.isNull(konfiguracija)) {
            konfiguracija = (GeneralnaKonfiguracija) SlusacAplikacije.getServletContext().getAttribute(SlusacAplikacije.KONFIGURACIJA_IME_ATRIBUTA);
        }
    }
    
}
