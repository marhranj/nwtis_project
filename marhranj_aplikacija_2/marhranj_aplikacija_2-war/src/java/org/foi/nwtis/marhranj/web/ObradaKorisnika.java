/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.foi.nwtis.marhranj.web.zrna.Korisnik;
import org.foi.nwtis.marhranj.web.zrna.RestWsOdgovor;
import org.foi.nwtis.rest.klijenti.AIRP2RestKlijent;
import org.foi.nwtis.rest.klijenti.KorisniciRestKlijent;

/**
 *
 * @author marhranj
 */
@Named(value = "korisnici")
@SessionScoped
public class ObradaKorisnika implements Serializable {

    private String ulogiraniKorisnik = "";
    private String ulogiranaLozinka = "";

    private String korisnickoIme;
    private String lozinka;
    private String ponovljenaLozinka;
    private String ime;
    private String prezime;
    private String email;

    private String poruka = "";

    private KorisniciRestKlijent korisniciRestKlijent;
    private AIRP2RestKlijent airp2RestKlijent;

    private Gson gson;

    private List<Korisnik> korisnici = new ArrayList<>();

    public ObradaKorisnika() {
        korisniciRestKlijent = new KorisniciRestKlijent();
        airp2RestKlijent = new AIRP2RestKlijent();
        gson = new Gson();
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        if (sessionMap.containsKey("korisnickoIme") && sessionMap.containsKey("lozinka")) {
            ulogiraniKorisnik = sessionMap.get("korisnickoIme").toString();
            ulogiranaLozinka = sessionMap.get("lozinka").toString();
        } else {
            poruka = "Istekla sessija sa korisnickim imenom i lozinkom";
        }
    }

    public void init() {
        poruka = "";
        azurirajListuKorisnika();
        Korisnik trenutniKorisnik = korisnici.stream()
                .filter(kor -> kor.getKorisnickoIme().equals(ulogiraniKorisnik))
                .findFirst()
                .orElse(new Korisnik());
        popuniPodatkeZaAzuriranje(trenutniKorisnik);
    }

    public void azuriraj() {
        poruka = "";
        if (svaPoljaPopunjena()) {
            if (ponovljenaLozinka.equals(lozinka)) {
                Korisnik korisnik = new Korisnik(ime, prezime, korisnickoIme, lozinka, email);
                String korisnikJsonString = dajKorisnikaJsonString(korisnik);
                if (!korisnikJsonString.isEmpty()) {
                    String json = korisniciRestKlijent.azurirajKorisnika(korisnikJsonString, ulogiraniKorisnik, ulogiranaLozinka);
                    RestWsOdgovor restWsOdgovor = gson.fromJson(json, RestWsOdgovor.class);
                    String status = Objects.nonNull(restWsOdgovor) ? restWsOdgovor.getStatus() : "";
                    if (Objects.nonNull(status) && status.contains("OK")) {
                        poruka = "Korisnik uspjesno azuriran";
                        azurirajSesiju();
                    } else {
                        poruka = "Nije moguce azurirati navedenog korisnika";
                    }
                } else {
                    poruka = "Nije moguce serijalizirati objekt korisnika u json";
                }
            } else {
                poruka = "Lozinke ne odgovaraju";
            }
        } else {
            poruka = "Morate popuniti sva polja";
        }
        azurirajListuKorisnika();
    }

    public double getTablicaBrojRedaka() {
        RestWsOdgovor restWsOdgovor = gson.fromJson(airp2RestKlijent.dohvatiTablicaBrojRedaka(ulogiranaLozinka, ulogiraniKorisnik), RestWsOdgovor.class);
        if (Objects.nonNull(restWsOdgovor)) {
            return (Double) restWsOdgovor.getOdgovor();
        }
        return 5d;
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

    public List<Korisnik> getKorisnici() {
        return korisnici;
    }

    public void setKorisnici(List<Korisnik> korisnici) {
        this.korisnici = korisnici;
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

    private void azurirajSesiju() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("korisnickoIme", korisnickoIme);
        sessionMap.put("lozinka", lozinka);
        ulogiraniKorisnik = korisnickoIme;
        ulogiranaLozinka = lozinka;
    }

    private void popuniPodatkeZaAzuriranje(Korisnik korisnik) {
        korisnickoIme = korisnik.getKorisnickoIme();
        ime = korisnik.getIme();
        prezime = korisnik.getPrezime();
        email = korisnik.getEmail();
    }

    private boolean svaPoljaPopunjena() {
        return !korisnickoIme.isEmpty()
                && !lozinka.isEmpty()
                && !ponovljenaLozinka.isEmpty()
                && !ime.isEmpty()
                && !prezime.isEmpty()
                && !email.isEmpty();
    }

    private List<Korisnik> dohvatiKorisnikeIzJsona(JsonObject jsonObject) {
        List<Korisnik> korisnici = new ArrayList<>();
        if (Objects.nonNull(jsonObject)) {
            JsonArray korisniciJson = jsonObject.get("odgovor").getAsJsonArray();
            korisniciJson.forEach(korisnikJsonElement -> {
                Korisnik korisnik = gson.fromJson(korisnikJsonElement, Korisnik.class);
                korisnici.add(korisnik);
            });
        }
        return korisnici;
    }

    private void azurirajListuKorisnika() {
        String json = korisniciRestKlijent.dohvatiSveKorisnike(ulogiranaLozinka, ulogiraniKorisnik);
        JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
        korisnici = dohvatiKorisnikeIzJsona(jsonObject);
    }

}
