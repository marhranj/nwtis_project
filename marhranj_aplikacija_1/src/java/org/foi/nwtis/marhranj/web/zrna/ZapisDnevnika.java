package org.foi.nwtis.marhranj.web.zrna;

import java.sql.Timestamp;
import java.util.Date;


public class ZapisDnevnika {

    private int id;
    private String korisnik;
    private Timestamp vrijeme;
    private String naredba;
    private String url;
    private String ip;
    private String vrsta;
    private long trajanje;
    
    public ZapisDnevnika(int id, String korisnik, Timestamp vrijeme, String naredba, String url, String ip, String vrsta, long trajanje) {
        this.id = id;
        this.korisnik = korisnik;
        this.vrijeme = vrijeme;
        this.naredba = naredba;
        this.url = url;
        this.ip = ip;
        this.vrsta = vrsta;
        this.trajanje = trajanje;
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

    public Date getVrijeme() {
        return vrijeme;
    }

    public void setVrijeme(Timestamp vrijeme) {
        this.vrijeme = vrijeme;
    }

    public String getNaredba() {
        return naredba;
    }

    public void setNaredba(String naredba) {
        this.naredba = naredba;
    }
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getVrsta() {
        return vrsta;
    }

    public void setVrsta(String vrsta) {
        this.vrsta = vrsta;
    }
    
    public long getTrajanje() {
        return trajanje;
    }

    public void setTrajanje(long trajanje) {
        this.trajanje = trajanje;
    }
    
}
