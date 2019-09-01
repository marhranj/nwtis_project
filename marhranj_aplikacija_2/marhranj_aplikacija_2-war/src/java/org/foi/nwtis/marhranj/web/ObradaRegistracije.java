/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import java.io.Serializable;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.foi.nwtis.marhranj.web.zrna.Korisnik;
import org.foi.nwtis.marhranj.web.zrna.RestWsOdgovor;
import org.foi.nwtis.rest.klijenti.KorisniciRestKlijent;

/**
 *
 * @author marhranj
 */
@Named(value = "registracija")
@SessionScoped
public class ObradaRegistracije implements Serializable {

    private String korisnickoIme;
    private String lozinka;
    private String ponovljenaLozinka;
    private String ime;
    private String prezime;
    private String email;

    private String poruka = "";

    private KorisniciRestKlijent restKlijent;
    private Gson gson;

    public ObradaRegistracije() {
        restKlijent = new KorisniciRestKlijent();
        gson = new Gson();
    }

    public String registriraj() {
        poruka = "";
        if (svaPoljaPopunjena()) {
            if (lozinka.equals(ponovljenaLozinka)) {
                Korisnik korisnik = new Korisnik(ime, prezime, korisnickoIme, lozinka, email);
                String korisnikJsonString = dajKorisnikaJsonString(korisnik);
                if (!korisnikJsonString.isEmpty()) {
                    String json = restKlijent.dodajKorisnika(korisnikJsonString);
                    RestWsOdgovor restWsOdgovor = gson.fromJson(json, RestWsOdgovor.class);
                    String status = restWsOdgovor.getStatus();
                    if (Objects.nonNull(status) && status.contains("OK")) {
                        return "prijava";
                    } else {
                        poruka = "Nije moguce dodati navedenog korisnika, mozda vec postoji korisnik s navedenim korisnickim imenom";
                    }
                } else {
                    poruka = "Nije moguce serijalizirati objekt korisnika u json";
                }
            } else {
                poruka = "Ponovljena lozinka mora odgovarati lozinci";
            }
        } else {
            poruka = "Morate popuniti sva polja";
        }
        return "";
    }

    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public void setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public String getPonovljenaLozinka() {
        return ponovljenaLozinka;
    }

    public void setPonovljenaLozinka(String ponovljenaLozinka) {
        this.ponovljenaLozinka = ponovljenaLozinka;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPoruka() {
        return poruka;
    }

    public void setPoruka(String poruka) {
        this.poruka = poruka;
    }

    private String dajKorisnikaJsonString(Korisnik korisnik) {
        try {
            ObjectWriter objectWritter = new ObjectMapper().writer().withDefaultPrettyPrinter();
            return objectWritter.writeValueAsString(korisnik);
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
        return "";
    }

    private boolean svaPoljaPopunjena() {
        return !korisnickoIme.isEmpty()
                && !lozinka.isEmpty()
                && !ponovljenaLozinka.isEmpty()
                && !ime.isEmpty()
                && !prezime.isEmpty()
                && !email.isEmpty();
    }

}
