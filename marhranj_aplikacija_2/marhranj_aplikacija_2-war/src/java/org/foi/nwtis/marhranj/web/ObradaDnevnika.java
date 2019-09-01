/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj.web;

import com.google.gson.Gson;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.foi.nwtis.marhranj.ejb.eb.Dnevnik;
import org.foi.nwtis.marhranj.ejb.sb.DnevnikFacade;
import org.foi.nwtis.marhranj.web.zrna.RestWsOdgovor;
import org.foi.nwtis.rest.klijenti.AIRP2RestKlijent;

/**
 *
 * @author marhranj
 */
@Named(value = "dnevnik")
@SessionScoped
public class ObradaDnevnika implements Serializable {

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @EJB
    private DnevnikFacade dnevnikFacade;

    private String ulogiraniKorisnik = "";
    private String ulogiranaLozinka = "";

    private String vrstaZapisa;
    private String pocetnoVrijeme;
    private String zavrsnoVrijeme;

    private String poruka = "";

    private List<Dnevnik> sviZapisiDnevnika = new ArrayList<>();
    private List<Dnevnik> zapisiDnevnika = new ArrayList<>();

    private AIRP2RestKlijent airp2RestKlijent;

    private Gson gson = new Gson();

    public ObradaDnevnika() {
        airp2RestKlijent = new AIRP2RestKlijent();
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        if (sessionMap.containsKey("korisnickoIme") && sessionMap.containsKey("lozinka")) {
            ulogiraniKorisnik = sessionMap.get("korisnickoIme").toString();
            ulogiranaLozinka = sessionMap.get("lozinka").toString();
        } else {
            poruka = "Istekla sessija sa korisnickim imenom i lozinkom";
        }
    }

    public void init() {
        sviZapisiDnevnika = dnevnikFacade.findAll();
        zapisiDnevnika = new ArrayList<>(sviZapisiDnevnika);
    }

    public double getTablicaBrojRedaka() {
        RestWsOdgovor restWsOdgovor = gson.fromJson(airp2RestKlijent.dohvatiTablicaBrojRedaka(ulogiranaLozinka, ulogiraniKorisnik), RestWsOdgovor.class);
        return (Double) restWsOdgovor.getOdgovor();
    }

    public List<Dnevnik> getZapisiDnevnika() {
        return zapisiDnevnika;
    }

    public String getVrstaZapisa() {
        return vrstaZapisa;
    }

    public void setVrstaZapisa(String vrstaZapisa) {
        this.vrstaZapisa = vrstaZapisa;
    }

    public String getPocetnoVrijeme() {
        return pocetnoVrijeme;
    }

    public void setPocetnoVrijeme(String pocetnoVrijeme) {
        this.pocetnoVrijeme = pocetnoVrijeme;
    }

    public String getZavrsnoVrijeme() {
        return zavrsnoVrijeme;
    }

    public void setZavrsnoVrijeme(String zavrsnoVrijeme) {
        this.zavrsnoVrijeme = zavrsnoVrijeme;
    }

    public String getPoruka() {
        return poruka;
    }

    public void filtrirajZapise() {
        if (!ispravnaVremenskaPolja()) {
            poruka = "Morate unijeti ili oba vremenska polja ili niti jedno, prikazat će se sva polja.";
            zapisiDnevnika = new ArrayList<>(sviZapisiDnevnika);
        } else {
            if (svaPoljaPopunjena()) {
                Date pocetak = dohvatiDateIzStringa(pocetnoVrijeme);
                Date kraj = dohvatiDateIzStringa(zavrsnoVrijeme);
                zapisiDnevnika = sviZapisiDnevnika.stream()
                        .filter(dnevnik -> dnevnik.getVrsta().equals(vrstaZapisa))
                        .filter(dnevnik -> dnevnik.getVrijeme().after(pocetak) && dnevnik.getVrijeme().before(kraj))
                        .collect(Collectors.toList());
            } else if (!vrstaZapisa.isEmpty() && ispravnaVremenskaPolja()) {
                zapisiDnevnika = sviZapisiDnevnika.stream()
                        .filter(dnevnik -> dnevnik.getVrsta().equals(vrstaZapisa))
                        .collect(Collectors.toList());
            } else if (!pocetnoVrijeme.isEmpty() && !zavrsnoVrijeme.isEmpty()) {
                Date pocetak = dohvatiDateIzStringa(pocetnoVrijeme);
                Date kraj = dohvatiDateIzStringa(zavrsnoVrijeme);
                zapisiDnevnika = sviZapisiDnevnika.stream()
                        .filter(dnevnik -> dnevnik.getVrijeme().after(pocetak) && dnevnik.getVrijeme().before(kraj))
                        .collect(Collectors.toList());
            } else {
                zapisiDnevnika = new ArrayList<>(sviZapisiDnevnika);
            }
        }

    }

    private Date dohvatiDateIzStringa(String dateString) {
        try {
            return formatter.parse(dateString);
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
        return new Date();
    }

    private boolean svaPoljaPopunjena() {
        return !vrstaZapisa.isEmpty() && !pocetnoVrijeme.isEmpty() && !zavrsnoVrijeme.isEmpty();
    }

    private boolean ispravnaVremenskaPolja() {
        return (!pocetnoVrijeme.isEmpty() && !zavrsnoVrijeme.isEmpty())
                || (pocetnoVrijeme.isEmpty() && zavrsnoVrijeme.isEmpty());
    }

}
