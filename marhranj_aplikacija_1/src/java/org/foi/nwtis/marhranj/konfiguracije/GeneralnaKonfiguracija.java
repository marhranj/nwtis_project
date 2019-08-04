/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj.konfiguracije;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.foi.nwtis.marhranj.konfiguracije.KonstanteKonfiguracija.IZBORNIK_BROJ_REDAKA;
import static org.foi.nwtis.marhranj.konfiguracije.KonstanteKonfiguracija.LOCATION_IQ_TOKEN;
import static org.foi.nwtis.marhranj.konfiguracije.KonstanteKonfiguracija.OPEN_SKY_NETWORK_KORISNIK;
import static org.foi.nwtis.marhranj.konfiguracije.KonstanteKonfiguracija.OPEN_SKY_NETWORK_LOZINKA;
import static org.foi.nwtis.marhranj.konfiguracije.KonstanteKonfiguracija.OPEN_WEATHER_API_KEY;
import static org.foi.nwtis.marhranj.konfiguracije.KonstanteKonfiguracija.PREUZIMANJE_CIKLUS;
import static org.foi.nwtis.marhranj.konfiguracije.KonstanteKonfiguracija.PREUZIMANJE_POCETAK;
import static org.foi.nwtis.marhranj.konfiguracije.KonstanteKonfiguracija.PREUZIMANJE_TRAJANJE;
import static org.foi.nwtis.marhranj.konfiguracije.KonstanteKonfiguracija.TABLICA_BROJ_REDAKA;
import org.foi.nwtis.marhranj.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.marhranj.konfiguracije.bp.BP_Sucelje;

/**
 *
 * @author marhranj
 * 
 * Klasa koja objedinjuje sve konfiguracije, za bazu i za API-je
 * 
 */
public class GeneralnaKonfiguracija {
    
    BP_Sucelje bpSucelje;
    Konfiguracija konfiguracija;
    
    /**
     *
     * @param nazivDatoteke
     */
    public GeneralnaKonfiguracija(String nazivDatoteke) {
        try {
            bpSucelje = new BP_Konfiguracija(nazivDatoteke);
            konfiguracija = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija  ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     *
     * @return
     */
    public String getLocationIqToken() {
        return konfiguracija.dajPostavku(LOCATION_IQ_TOKEN);
    }
    
    /**
     *
     * @return
     */
    public String getOpenWeatherApiKey() {
        return konfiguracija.dajPostavku(OPEN_WEATHER_API_KEY);
    }
    
    /**
     *
     * @return
     */
    public String getOpenSkyNetworkKorisnik() {
        return konfiguracija.dajPostavku(OPEN_SKY_NETWORK_KORISNIK);
    }
    
    /**
     *
     * @return
     */
    public String getOpenSkyNetworkLozinku() {
        return konfiguracija.dajPostavku(OPEN_SKY_NETWORK_LOZINKA);
    }
    
    /**
     *
     * @return
     */
    public String getPreuzimanjeCiklus() {
        return konfiguracija.dajPostavku(PREUZIMANJE_CIKLUS);
    }
    
    /**
     *
     * @return
     */
    public String getPreuzimanjePocetak() {
        return konfiguracija.dajPostavku(PREUZIMANJE_POCETAK);
    }
    
    /**
     *
     * @return
     */
    public String getPreuzimanjeTrajanje() {
        return konfiguracija.dajPostavku(PREUZIMANJE_TRAJANJE);
    }
    
    /**
     *
     * @return
     */
    public String getIzbornikBrojRedaka() {
        return konfiguracija.dajPostavku(IZBORNIK_BROJ_REDAKA);
    }
    
    /**
     *
     * @return
     */
    public String getTablicakBrojRedaka() {
        return konfiguracija.dajPostavku(TABLICA_BROJ_REDAKA);
    }
    
    /**
     *
     * @return
     */
    public String getAdminDatabase() {
        return bpSucelje.getAdminDatabase();
    }

    /**
     *
     * @return
     */
    public String getAdminPassword() {
        return bpSucelje.getAdminPassword();
    }

    /**
     *
     * @return
     */
    public String getAdminUsername() {
        return bpSucelje.getAdminUsername();
    }

    /**
     *
     * @return
     */
    public String getDriverDatabase() {
        return bpSucelje.getDriverDatabase();
    }

    /**
     *
     * @return
     */
    public String getDriverDatabase(String string) {
        return bpSucelje.getDriverDatabase(string);
    }

    /**
     *
     * @return
     */
    public Properties getDriversDatabase() {
        return bpSucelje.getDriversDatabase();
    }

    /**
     *
     * @return
     */
    public String getServerDatabase() {
        return bpSucelje.getServerDatabase();
    }

    /**
     *
     * @return
     */
    public String getUserDatabase() {
        return bpSucelje.getUserDatabase();
    }

    /**
     *
     * @return
     */
    public String getUserPassword() {
        return bpSucelje.getUserPassword();
    }

    /**
     *
     * @return
     */
    public String getUserUsername() {
        return bpSucelje.getUserUsername();
    }
    
    /**
     *
     * @return
     */
    public String getServer() {
        return bpSucelje.getServerDatabase() + bpSucelje.getUserDatabase();
    }
    
}
