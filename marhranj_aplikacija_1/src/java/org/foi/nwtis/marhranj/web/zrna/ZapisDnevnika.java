package org.foi.nwtis.marhranj.web.zrna;

import java.sql.Timestamp;
import java.util.Date;


public class ZapisDnevnika {

    private int id;
    private String korisnik;
    private Timestamp vrijeme;
    private String naredba;
    private String vrsta;

    public ZapisDnevnika(int id, String korisnik, Timestamp vrijeme, String sadrzaj, String vrsta) {
        this.id = id;
        this.korisnik = korisnik;
        this.vrijeme = vrijeme;
        this.naredba = sadrzaj;
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

    public String getVrsta() {
        return vrsta;
    }

    public void setVrsta(String vrsta) {
        this.vrsta = vrsta;
    }
    
}
