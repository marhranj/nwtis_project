package org.foi.nwtis.marhranj.web.zrna;

import java.sql.Timestamp;
import java.util.Date;


public class Dnevnik {

    private int id;
    private String korisnik;
    private String url;
    private String ipAdresa;
    private long trajanjeObrade;
    private Timestamp vrijemePrijema;
    private String radnja;
    private String vrsta;

    public Dnevnik(int id, String korisnik, String url, String ipAdresa, long trajanjeObrade, Timestamp vrijemePrijema, String sadrzaj, String vrsta) {
        this.id = id;
        this.korisnik = korisnik;
        this.url = url;
        this.ipAdresa = ipAdresa;
        this.trajanjeObrade = trajanjeObrade;
        this.vrijemePrijema = vrijemePrijema;
        this.radnja = sadrzaj;
        this.vrsta = vrsta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(String korisnik) {
        this.korisnik = korisnik;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIpAdresa() {
        return ipAdresa;
    }

    public void setIpAdresa(String ipAdresa) {
        this.ipAdresa = ipAdresa;
    }

    public long getTrajanjeObrade() {
        return trajanjeObrade;
    }

    public void setTrajanjeObrade(long vrijemeObrade) {
        this.trajanjeObrade = vrijemeObrade;
    }

    public Date getVrijemePrijema() {
        return vrijemePrijema;
    }

    public void setVrijemePrijema(Timestamp vrijemePrijema) {
        this.vrijemePrijema = vrijemePrijema;
    }

    public String getRadnja() {
        return radnja;
    }

    public void setRadnja(String sadrzaj) {
        this.radnja = sadrzaj;
    }

    public String getVrsta() {
        return vrsta;
    }

    public void setVrsta(String vrsta) {
        this.vrsta = vrsta;
    }
    
}
