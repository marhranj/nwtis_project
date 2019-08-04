/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj.web;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Objects;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import org.foi.nwtis.marhranj.konfiguracije.GeneralnaKonfiguracija;
import org.foi.nwtis.marhranj.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.marhranj.web.zrna.Aerodrom;
import org.foi.nwtis.rest.klijenti.LIQKlijent;
import org.foi.nwtis.rest.klijenti.OWMKlijent;
import org.foi.nwtis.rest.podaci.Lokacija;
import org.foi.nwtis.rest.podaci.MeteoPodaci;

/**
 *
 * @author marhranj
 * 
 * Klasa kojoj je glavna svrha obrada radnji odabranih na korisničkoj strani
 * 
 */
@Named(value = "obradaAerodroma")
@SessionScoped
public class ObradaAerodroma implements Serializable {

    private static final String ICAO_NIJE_POPUNJEN = "ICAO nije popunjen!";
    private static final String NIJE_PREUZET_NAZIV_AERODROMA = "Nije preuzet naziv aerodroma!";
    private static final String NIJE_PREUZETA_LOKACIJA_AERODROMA = "Nije preuzeta lokacija aerodroma!";
    private static final String PROBLEM_KOD_UPITA_U_BAZU = "SqlException!";
    private static final String AERODROM_NE_POSTOJI = "U bazi podataka ne postoji aerodrom sa ovim ICAO kodom!";
    private static final String AERODROM_DODAN_U_BAZU_PODATAKA = "Aerodrom je uspjesno dodan u bazu podataka";
    
    private static final String ICAO = "IDENT";
    private static final String NAZIV = "NAME";
    private static final String ISO_KRATICA_DRZAVE = "ISO_COUNTRY";
    
    private String icao;
    private Aerodrom aerodrom;
    private MeteoPodaci meteoPodaci;
    private String poruka;
    
    /**
     * Metoda koja preuzima naziv aerodroma
     * 
     * @return 
     */
    public String preuzmiNazivAerodroma() {
        if (!"".equals(this.icao)) {
            
            try (Connection con = KonektorBazePodataka.dajKonekciju(); 
                PreparedStatement dajAerodrom = con.prepareStatement("SELECT * from AIRPORTS WHERE IDENT = ?");) {
            
                dajAerodrom.setString(1, this.icao);
                ResultSet rezultat = dajAerodrom.executeQuery();

                if (rezultat.next()) {
                    this.aerodrom = new Aerodrom();
                    this.aerodrom.setIcao(rezultat.getString(ICAO));
                    this.aerodrom.setNaziv(rezultat.getString(NAZIV));
                    this.aerodrom.setDrzava(rezultat.getString(ISO_KRATICA_DRZAVE));
                    
                    resetirajErrorPoruku();
                } else {
                    this.poruka = AERODROM_NE_POSTOJI;
                    this.aerodrom = null;
                    this.meteoPodaci = null;
                }
                
                rezultat.close();
            } catch(SQLException ex) {
                System.out.println("SQLException: " + ex);
                this.poruka = PROBLEM_KOD_UPITA_U_BAZU;
            }
            
        } else {
            this.poruka = ICAO_NIJE_POPUNJEN;
        }
        
        return "";
    }
    
    /**
     * Metoda koja preuzima GPS lokaciju
     * 
     * @return 
     */
    public String preuzmiGPSLokaciju() {
        if (Objects.nonNull(this.aerodrom)) {
            Lokacija lokacija = getLIQKlijent().getGeoLocation(this.aerodrom.getNaziv());
            this.aerodrom.setLokacija(lokacija);
            resetirajErrorPoruku();
        } else {
            this.poruka = NIJE_PREUZET_NAZIV_AERODROMA;
        }
        
        return "";
    }
    
    /**
     * Metoda u kojoj sprema aerodrom
     * 
     * @return 
     */
    public String spremiAerodrom() {
        if (Objects.nonNull(this.aerodrom)) {
            if (Objects.nonNull(this.aerodrom.getLokacija())) {
                
                try (Connection con = KonektorBazePodataka.dajKonekciju(); 
                    PreparedStatement dodavanjeAviona = con.prepareStatement("INSERT INTO MYAIRPORTS (IDENT, NAME, ISO_COUNTRY, COORDINATES, STORED) VALUES (?, ?, ?, ?, ?)");) {

                    dodavanjeAviona.setString(1, this.aerodrom.getIcao());
                    dodavanjeAviona.setString(2, this.aerodrom.getNaziv());
                    dodavanjeAviona.setString(3, this.aerodrom.getDrzava());
                    dodavanjeAviona.setString(4, this.aerodrom.getLokacija().getLatitude() + ", " + this.aerodrom.getLokacija().getLongitude());
                    dodavanjeAviona.setTimestamp(5, new Timestamp(System.currentTimeMillis()));

                    dodavanjeAviona.execute();
                    this.poruka = AERODROM_DODAN_U_BAZU_PODATAKA;
                } catch(SQLException ex) {
                    System.out.println("SQLException: " + ex);
                    this.poruka = PROBLEM_KOD_UPITA_U_BAZU;
                }
            } else {
                this.poruka = NIJE_PREUZETA_LOKACIJA_AERODROMA;
            }
        } else {
            this.poruka = NIJE_PREUZET_NAZIV_AERODROMA;
        }
        
        return "";
    }
    
    /**
     * Metoda koja preuzima meteo podatke
     * 
     * @return 
     */
    public String preuzmiMeteoPodatke() {
        if (Objects.nonNull(this.aerodrom)) {
            if (Objects.nonNull(this.aerodrom.getLokacija())) {
                this.meteoPodaci = getOWMKlijent().getRealTimeWeather(this.aerodrom.getLokacija().getLatitude(), this.aerodrom.getLokacija().getLongitude());
                resetirajErrorPoruku();
            } else {
                this.poruka = NIJE_PREUZETA_LOKACIJA_AERODROMA;
            }
        } else {
            this.poruka = NIJE_PREUZET_NAZIV_AERODROMA;
        }
        
        return "";
    }

    /**
     * 
     * @return 
     */
    public String getIcao() {
        return icao;
    }

    /**
     * Metoda u kojoj se postavlja icao
     * @param icao
     */
    public void setIcao(String icao) {
        this.icao = icao;
    }

    /**
     * 
     * @return 
     */
    public Aerodrom getAerodrom() {
        return aerodrom;
    }

    /**
     * 
     * @return 
     */
    public MeteoPodaci getMeteoPodaci() {
        return meteoPodaci;
    }

    /**
     * 
     * @return 
     */
    public String getPoruka() {
        return poruka;
    }
    
    /**
     * 
     * @return 
     */
    private GeneralnaKonfiguracija getKonfiguracija() {
        return (GeneralnaKonfiguracija) SlusacAplikacije.getServletContext().getAttribute(SlusacAplikacije.KONFIGURACIJA_IME_ATRIBUTA);
    }
    
    /**
     * 
     * @return 
     */
    private LIQKlijent getLIQKlijent() {
        return new LIQKlijent(this.getKonfiguracija().getLocationIqToken());
    }
    
    /**
     * 
     * @return 
     */
    private OWMKlijent getOWMKlijent() {
        return new OWMKlijent(this.getKonfiguracija().getOpenWeatherApiKey());
    }
    
    /**
     * Metoda u kojoj se resetira error poruka 
     * 
     */
    private void resetirajErrorPoruku() {
        this.poruka = "";
    }
    
}
