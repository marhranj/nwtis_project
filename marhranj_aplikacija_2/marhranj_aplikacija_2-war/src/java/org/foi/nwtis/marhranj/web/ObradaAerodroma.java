/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj.web;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Named;
import javax.xml.ws.WebServiceRef;
import org.foi.nwtis.marhranj.web.zrna.Aerodrom;
import org.foi.nwtis.marhranj.web.zrna.RestWsOdgovor;
import org.foi.nwtis.marhranj.ws.serveri.AIRP2WS;
import org.foi.nwtis.marhranj.ws.serveri.AIRP2WS_Service;
import org.foi.nwtis.marhranj.ws.serveri.MeteoPodaci;
import org.foi.nwtis.marhranj.ws.serveri.MojAvionLeti;
import org.foi.nwtis.rest.klijenti.AIRP2RestKlijent;

/**
 *
 * @author marhranj
 */
@Named(value = "aerodromi")
@SessionScoped
public class ObradaAerodroma implements Serializable {

    private String ulogiraniKorisnik = "";
    private String ulogiranaLozinka = "";

    private AIRP2RestKlijent airp2RestKlijent;

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8084/marhranj_aplikacija_1/AIRP2WS.wsdl")
    private AIRP2WS_Service service = new AIRP2WS_Service();

    private final AIRP2WS port = service.getAIRP2WSPort();

    private Gson gson;

    private String icaoDodaj;
    private String icao24;
    
    private List<Aerodrom> aerodromi = new ArrayList<>();
    private List<MojAvionLeti> avioni = new ArrayList<>();
    
    private String icao;
    private Aerodrom aerodrom;
    private MeteoPodaci meteoPodaci;
    private String poruka;
    
    private String pocetak = "";
    private String kraj = "";

    public ObradaAerodroma() {
        poruka = "";
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
        aerodromi = preuzmiAerodrome();
        avioni = preuzmiAvione(pocetak, kraj);
    }
    
    public void dodajAerodrom() {
        if (!icaoDodaj.isEmpty()) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("icao", icaoDodaj);
            String json = airp2RestKlijent.dodajAerodrom(jsonObject.toString(), ulogiraniKorisnik, ulogiranaLozinka);
            RestWsOdgovor restWsOdgovor = gson.fromJson(json, RestWsOdgovor.class);
            String status = restWsOdgovor.getStatus();
            if (Objects.nonNull(status) && status.contains("OK")) {
                poruka = "Aerodrom uspjesno dodan";
            } else {
                poruka = "Nije moguce dodati navedeni aerodrom";
            }
        } else {
            poruka = "Morate unijeti icao kako biste dodali aerodrom";
        }
        aerodromi = preuzmiAerodrome();
    }
    
    public void obrisiAerodrom() {
        if (!icao.isEmpty()) {
            String json = airp2RestKlijent.obrisiAerodrom(icao, ulogiraniKorisnik, ulogiranaLozinka);
            RestWsOdgovor restWsOdgovor = gson.fromJson(json, RestWsOdgovor.class);
            String status = restWsOdgovor.getStatus();
            if (Objects.nonNull(status) && status.contains("OK")) {
                poruka = "Aerodrom uspjesno obrisan";
            } else {
                poruka = "Nije moguce obrisati navedeni aerodrom";
            }
        } else {
            poruka = "Morate oznaciti aerodrom kojeg zelite obrisati";
        }
        aerodromi = preuzmiAerodrome();
    }
    
    public void aktivirajAerodrom() {
        if (!icao.isEmpty()) {
            String json = airp2RestKlijent.aktivirajAerodrom(icao, ulogiraniKorisnik, ulogiranaLozinka);
            RestWsOdgovor restWsOdgovor = gson.fromJson(json, RestWsOdgovor.class);
            String status = restWsOdgovor.getStatus();
            if (Objects.nonNull(status) && status.contains("OK")) {
                poruka = "Aerodrom uspjesno aktiviran";
            } else {
                poruka = "Nije moguce aktivirati navedeni aerodrom";
            }
        } else {
            poruka = "Morate oznaciti aerodrom kojeg zelite aktivirati";
        }
    }
    
    public void blokirajAerodrom() {
        if (!icao.isEmpty()) {
            String json = airp2RestKlijent.blokirajAerodrom(icao, ulogiraniKorisnik, ulogiranaLozinka);
            RestWsOdgovor restWsOdgovor = gson.fromJson(json, RestWsOdgovor.class);
            String status = restWsOdgovor.getStatus();
            if (Objects.nonNull(status) && status.contains("OK")) {
                poruka = "Aerodrom uspjesno blokiran";
            } else {
                poruka = "Nije moguce blokirati navedeni aerodrom";
            }
        } else {
            poruka = "Morate oznaciti aerodrom kojeg zelite blokirati";
        }
    }
    
    public void dajStatusAerodroma() {
        if (!icao.isEmpty()) {
            String json = airp2RestKlijent.dohvatiStatusAerodroma(icao, ulogiranaLozinka, ulogiraniKorisnik);
            RestWsOdgovor restWsOdgovor = gson.fromJson(json, RestWsOdgovor.class);
            Object odgovor = restWsOdgovor.getOdgovor();
            if (Objects.nonNull(odgovor)) {
                poruka = "Status aerodroma: " + icao + " - " + odgovor;
            } else {
                poruka = "Nije moguce dohvatiti status navedenog aerodroma";
            }
        } else {
            poruka = "Morate oznaciti aerodrom za kojeg zelite dohvatiti status";
        }
    }
    
    public void promjeniAerodrom(AjaxBehaviorEvent event) {
        aerodrom = aerodromi.stream()
                .filter(aerodrom -> aerodrom.getIcao().equals(icao))
                .findFirst()
                .orElse(null);
        preuzmiMeteoPodatke();
        avioni = preuzmiAvione(pocetak, kraj);
    }
    
    public void filtirajAvione() {
        if (!(!pocetak.isEmpty() && !kraj.isEmpty())) {
            poruka = "Morate unijeti oba datuma ukoliko želite filtrirati, u suprotnom prikazati će se nefiltrirani avioni";
        } 
        avioni = preuzmiAvione(pocetak, kraj);
    }

    public List<Aerodrom> preuzmiAerodrome() {
        String json = airp2RestKlijent.dohvatiSveAerodrome(ulogiranaLozinka, ulogiraniKorisnik);
        JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
        return dohvatiAerodromeIzJsona(jsonObject);
    }
    
    public List<MojAvionLeti> preuzmiAvione(String pocetak, String kraj) {
        String icao = "";
        if (Objects.nonNull(aerodrom)) {
            icao = aerodrom.getIcao();
        } else if (aerodromi.size() > 0) {
            icao = aerodromi.get(0).getIcao();
        }
        String najmanjeVrijeme = pocetak.isEmpty() ? "01-01-0001 01:01:01" : pocetak;
        String najveceVrijeme = kraj.isEmpty() ? "31-12-9999 23:59:59" : kraj;
        return port.dajAvionePoletjeleSAerodroma(icao, najmanjeVrijeme, najveceVrijeme, ulogiraniKorisnik, ulogiranaLozinka);
    }
    
    public String pretvoriLongUDatum(long datumLong) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date(datumLong * 1000);
        return format.format(date);
    }

    public String getIcao24() {
        return icao24;
    }

    public String getPocetak() {
        return pocetak;
    }

    public void setPocetak(String pocetak) {
        this.pocetak = pocetak;
    }

    public String getKraj() {
        return kraj;
    }

    public void setKraj(String kraj) {
        this.kraj = kraj;
    }

    public void setIcao24(String icao24) {
        this.icao24 = icao24;
    }
    
    public String getIcaoDodaj() {
        return icaoDodaj;
    }

    public void setIcaoDodaj(String icaoDodaj) {
        this.icaoDodaj = icaoDodaj;
    }

    public String getIcao() {
        return icao;
    }

    public void setIcao(String icao) {
        this.icao = icao;
    }

    public void setAerodrom(Aerodrom aerodrom) {
        this.aerodrom = aerodrom;
    }

    public Aerodrom getAerodrom() {
        return aerodrom;
    }

    public MeteoPodaci getMeteoPodaci() {
        return meteoPodaci;
    }

    public String getPoruka() {
        return poruka;
    }

    public List<Aerodrom> getAerodromi() {
        return aerodromi;
    }

    public void setAerodromi(List<Aerodrom> aerodromi) {
        this.aerodromi = aerodromi;
    }

    public List<MojAvionLeti> getAvioni() {
        return avioni;
    }

    public void setAvioni(List<MojAvionLeti> avioni) {
        this.avioni = avioni;
    }

    private List<Aerodrom> dohvatiAerodromeIzJsona(JsonObject jsonObject) {
        List<Aerodrom> aerodromi = new ArrayList<>();
        JsonArray aerodromiJson = jsonObject.get("odgovor").getAsJsonArray();
        aerodromiJson.forEach(aerodromJsonElement -> {
            aerodromi.add(gson.fromJson(aerodromJsonElement, Aerodrom.class));
        });
        return aerodromi;
    }
    
    private void preuzmiMeteoPodatke() {
        if (Objects.nonNull(aerodrom)) {
            meteoPodaci = port.dajMeteoPodatkeZaAerodrom(aerodrom.getIcao(), ulogiraniKorisnik, ulogiranaLozinka);
        } else {
            poruka = "Niste odabrali aerodrom";
        }
    }

}
