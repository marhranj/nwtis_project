/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj.rest.serveri;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.ws.WebServiceRef;
import org.foi.nwtis.marhranj.utils.SocketUtils;
import org.foi.nwtis.marhranj.web.zrna.RestWsOdgovor;
import org.foi.nwtis.marhranj.ws.serveri.AIRP2WS;
import org.foi.nwtis.marhranj.ws.serveri.AIRP2WS_Service;
import org.foi.nwtis.marhranj.ws.serveri.Korisnik;

/**
 * REST Web Service
 *
 * @author marhranj
 */
@Path("korisnici")
public class KorisniciRestAPI {

    private static final String OK_10 = "OK 10";

    private static final String STATUS_OK = "OK";
    private static final String STATUS_ERROR = "ERR";

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8084/marhranj_aplikacija_1/AIRP2WS.wsdl")
    private AIRP2WS_Service service = new AIRP2WS_Service();

    private final AIRP2WS port = service.getAIRP2WSPort();

    private Gson gson = new GsonBuilder().
            addSerializationExclusionStrategy(dajSrategijuSerijalizacijeZaSkrivanjeLozinke())
            .create();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String dohvatiSveKorisnike(@QueryParam("korisnickoIme") String korisnickoIme, @QueryParam("lozinka") String lozinka) {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();

        if (ispravanKorisnik(korisnickoIme, lozinka)) {
            restOdgovor.setStatus(STATUS_OK);
            restOdgovor.setOdgovor(port.dohvatiKorisnike(korisnickoIme, lozinka));
        } else {
            restOdgovor.setStatus(STATUS_ERROR);
            restOdgovor.setPoruka("Neuspjesna autentikacija za korisnika " + korisnickoIme);
        }

        return gson.toJson(restOdgovor);
    }

    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String dohvatiAutenticirajJednogKorisnika(@PathParam("id") String id, @QueryParam("korisnickoIme") String korisnickoIme, @QueryParam("lozinka") String lozinka, @QueryParam("auth") String auth) {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();

        if (ispravanKorisnik(korisnickoIme, lozinka)) {
            Optional<Korisnik> korisnik = port.dohvatiKorisnike(korisnickoIme, lozinka)
                    .stream()
                    .filter(kor -> String.valueOf(kor.getKorisnickoIme()).equals(id))
                    .findFirst();
            if (korisnik.isPresent()) {
                if (Objects.nonNull(auth)) {
                    restOdgovor = dohvatiRestWsOdgovorZaAutentikaciju(auth, korisnik.get());
                } else {
                    restOdgovor.setStatus(STATUS_OK);
                    restOdgovor.setOdgovor(Collections.singletonList(korisnik.get()));
                }
            } else {
                restOdgovor.setStatus(STATUS_ERROR);
                restOdgovor.setPoruka("Ne postoji korisnik sa navedenim korisnickim imenom");
            }

        } else {
            restOdgovor.setStatus(STATUS_ERROR);
            restOdgovor.setPoruka("Neuspjesna autentikacija za korisnika " + korisnickoIme);
        }

        return gson.toJson(restOdgovor);
    }

    private RestWsOdgovor dohvatiRestWsOdgovorZaAutentikaciju(String auth, Korisnik korisnik) {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();
        try {
            JsonObject jsonObject = (JsonObject) new JsonParser().parse(auth);
            String authLozinka = jsonObject.get("lozinka").getAsString();
            if (korisnik.getLozinka().equals(authLozinka)) {
                restOdgovor.setStatus(STATUS_OK);
                restOdgovor.setOdgovor(new ArrayList<>());
            } else {
                restOdgovor.setStatus(STATUS_ERROR);
                restOdgovor.setPoruka(String.format("Auth nije bila uspjesna, lozinka %s ne odgovara korisniku %s", authLozinka, korisnik.getKorisnickoIme()));
            }
        } catch (Exception e) {
            restOdgovor.setStatus(STATUS_ERROR);
            restOdgovor.setPoruka("Problem kod parsiranja ulaznih podataka");
        }
        return restOdgovor;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String dodajKorisnika(@QueryParam("korisnickoIme") String korisnickoIme, @QueryParam("lozinka") String lozinka, String json) {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();

        if (ispravanKorisnik(korisnickoIme, lozinka)) {
            try {
                Korisnik korisnik = gson.fromJson(json, Korisnik.class);
                if (port.dodajKorisnika(korisnik, korisnickoIme, lozinka)) {
                    restOdgovor.setStatus(STATUS_OK);
                    restOdgovor.setOdgovor(new ArrayList<>());
                } else {
                    restOdgovor.setStatus(STATUS_ERROR);
                    restOdgovor.setPoruka("Nije moguce dodati navedenog korisnika, mozda vec postoji u bazi podataka");
                }
            } catch (Exception e) {
                restOdgovor.setStatus(STATUS_ERROR);
                restOdgovor.setPoruka("Problem kod parsiranja ulaznih podataka");
            }

        } else {
            restOdgovor.setStatus(STATUS_ERROR);
            restOdgovor.setPoruka("Neuspjesna autentikacija za korisnika " + korisnickoIme);
        }

        return gson.toJson(restOdgovor);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String azurirajKorisnika(@QueryParam("korisnickoIme") String korisnickoIme, @QueryParam("lozinka") String lozinka, String json) {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();

        if (ispravanKorisnik(korisnickoIme, lozinka)) {
            try {
                Korisnik korisnik = gson.fromJson(json, Korisnik.class);
                if (port.azurirajKorisnika(korisnik, korisnickoIme, lozinka)) {
                    restOdgovor.setStatus(STATUS_OK);
                    restOdgovor.setOdgovor(new ArrayList<>());
                } else {
                    restOdgovor.setStatus(STATUS_ERROR);
                    restOdgovor.setPoruka("Nije moguce azurirati navedenog korisnika, mozda ne postoji u bazi podataka");
                }
            } catch (Exception e) {
                restOdgovor.setStatus(STATUS_ERROR);
                restOdgovor.setPoruka("Problem kod parsiranja ulaznih podataka");
            }

        } else {
            restOdgovor.setStatus(STATUS_ERROR);
            restOdgovor.setPoruka("Neuspjesna autentikacija za korisnika " + korisnickoIme);
        }

        return gson.toJson(restOdgovor);
    }

    private boolean ispravanKorisnik(String korisnickoIme, String lozinka) {
        String naredbaZaAutentikaciju = String.format("KORISNIK %s; LOZINKA %s;", korisnickoIme, lozinka);
        return SocketUtils.posaljiPorukuNaSocket(naredbaZaAutentikaciju, port.dohvatiPortSocketa()).contains(OK_10);
    }

    private ExclusionStrategy dajSrategijuSerijalizacijeZaSkrivanjeLozinke() {
        return new ExclusionStrategy() {

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }

            @Override
            public boolean shouldSkipField(FieldAttributes field) {
                return field.getName().startsWith("lozinka");
            }
        };
    }

}
