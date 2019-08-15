package org.foi.nwtis.marhranj.web.zrna;

import org.foi.nwtis.rest.podaci.Lokacija;

/**
 * Klasa Aerodrom služi za stvaranje objekata tipa aerodrom.
 */
public class Aerodrom {
    
    private String icao;
    private String naziv;
    private String drzava;
    private Lokacija lokacija;

    public Aerodrom(String icao, String naziv, String drzava, Lokacija lokacija) {
        this.icao = icao;
        this.naziv = naziv;
        this.drzava = drzava;
        this.lokacija = lokacija;
    }

    public Aerodrom() {
    }
    
    /**
     * Služi za dohvaćanje vrijednosti icao.
     */
    public String getIcao() {
        return icao;
    }
    
    /**
     * Služi za postavljanje  icao.
     */
    public void setIcao(String icao) {
        this.icao = icao;
    }
    
    /**
     * Služi za dohvaćanje vrijednosti naziv.
     */
    public String getNaziv() {
        return naziv;
    }
    
    /**
     * Služi za postavljanje vrijednosti naziv.
     */
    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }
    
    /**
     * Služi za dohvaćanje vrijednosti drzava.
     */
    public String getDrzava() {
        return drzava;
    }
    
    /**
     * Služi za postavljanje vrijednosti drzava.
     */
    public void setDrzava(String drzava) {
        this.drzava = drzava;
    }

    /**
     * Služi za dohvaćanje vrijednosti lokacija.
     */
    public Lokacija getLokacija() {
        return lokacija;
    }

    /**
     * Služi za postavljanje vrijednosti lokacija.
     */
    public void setLokacija(Lokacija lokacija) {
        this.lokacija = lokacija;
    }
    
}