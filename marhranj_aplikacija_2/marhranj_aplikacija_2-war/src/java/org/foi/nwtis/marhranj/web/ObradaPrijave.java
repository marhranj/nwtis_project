/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj.web;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.foi.nwtis.marhranj.web.zrna.RestWsOdgovor;
import org.foi.nwtis.rest.klijenti.KorisniciRestKlijent;

/**
 *
 * @author marhranj
 */
@Named(value = "prijava")
@SessionScoped
public class ObradaPrijave implements Serializable {
    
    private String korisnickoIme;
    private String lozinka;
    
    private String poruka = "";
    
    private KorisniciRestKlijent restKlijent;
    private Gson gson;
    
    public ObradaPrijave() {
        restKlijent = new KorisniciRestKlijent();
        gson = new Gson();
    }
    
    public String prijava() {
        poruka = "";
        if (!korisnickoIme.isEmpty() && !lozinka.isEmpty()) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("lozinka", lozinka);
            String authJson = jsonObject.toString().replaceAll("\\{", "%7B").replaceAll("\\}", "%7D");
            String json = restKlijent.dohvatiAutenticirajJednogKorisnika(korisnickoIme, lozinka, authJson, korisnickoIme);  
            RestWsOdgovor restWsOdgovor = gson.fromJson(json, RestWsOdgovor.class);
            String status = Objects.nonNull(restWsOdgovor) ? restWsOdgovor.getStatus() : "";
            if (Objects.nonNull(status) && status.contains("OK")) {
                Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
                sessionMap.put("korisnickoIme", korisnickoIme);
                sessionMap.put("lozinka", lozinka);
                return "izbornik";
            } else {
                poruka = "Neispravna autentikacija";
            }
        } else {
            poruka = "Morate unijeti sva polja";
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

    public String getPoruka() {
        return poruka;
    }

    public void setPoruka(String poruka) {
        this.poruka = poruka;
    }
     
}
