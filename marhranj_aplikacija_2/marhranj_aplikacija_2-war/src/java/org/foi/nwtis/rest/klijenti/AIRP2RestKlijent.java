/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.rest.klijenti;

import java.text.MessageFormat;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 * Jersey REST client generated for REST resource:AIRP2REST [aerodromi]<br>
 * USAGE:
 * <pre>
 *        AIRP2RestKlijent client = new AIRP2RestKlijent();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author at059201
 */
public class AIRP2RestKlijent {

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://localhost:8084/marhranj_aplikacija_1/webresources";

    public AIRP2RestKlijent() {
        client = ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("aerodromi");
    }

    public String dohvatiAvionePremaIdAerodroma(String id, String lozinka, String korisnickoIme) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (lozinka != null) {
            resource = resource.queryParam("lozinka", lozinka);
        }
        if (korisnickoIme != null) {
            resource = resource.queryParam("korisnickoIme", korisnickoIme);
        }
        resource = resource.path(MessageFormat.format("{0}/avion", new Object[]{id}));
        return resource.request(MediaType.APPLICATION_JSON).get(String.class);
    }

    public String dodajAerodrom(Object requestEntity) throws ClientErrorException {
        return webTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(requestEntity, MediaType.APPLICATION_JSON), String.class);
    }

    public String obrisiOdabraneAvioneAerodroma(String id, String aid) throws ClientErrorException {
        return webTarget.path(MessageFormat.format("{0}/avion/{1}", new Object[]{id, aid})).request().delete(String.class);
    }

    public String azurirajAerodrom(Object requestEntity, String id) throws ClientErrorException {
        return webTarget.path(MessageFormat.format("{0}", new Object[]{id})).request(MediaType.APPLICATION_JSON).put(Entity.entity(requestEntity, MediaType.APPLICATION_JSON), String.class);
    }

    public String dohvatiSveAerodrome(String lozinka, String korisnickoIme) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (lozinka != null) {
            resource = resource.queryParam("lozinka", lozinka);
        }
        if (korisnickoIme != null) {
            resource = resource.queryParam("korisnickoIme", korisnickoIme);
        }
        return resource.request(MediaType.APPLICATION_JSON).get(String.class);
    }

    public String dohvatiAerodromPremaId(String id, String lozinka, String korisnickoIme) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (lozinka != null) {
            resource = resource.queryParam("lozinka", lozinka);
        }
        if (korisnickoIme != null) {
            resource = resource.queryParam("korisnickoIme", korisnickoIme);
        }
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id}));
        return resource.request(MediaType.APPLICATION_JSON).get(String.class);
    }

    public String dodajAvioneAerodromu(Object requestEntity, String id) throws javax.ws.rs.ClientErrorException {
        return webTarget.path(MessageFormat.format("{0}/avion", new Object[]{id})).request(MediaType.APPLICATION_JSON).post(Entity.entity(requestEntity, MediaType.APPLICATION_JSON), String.class);
    }

    public String obrisiAerodrom(String id) throws javax.ws.rs.ClientErrorException {
        return webTarget.path(MessageFormat.format("{0}", new Object[]{id})).request().delete(String.class);
    }

    public String obrisiSveAvioneAerodroma(String id) throws javax.ws.rs.ClientErrorException {
        return webTarget.path(MessageFormat.format("{0}/avion", new Object[]{id})).request().delete(String.class);
    }

    public void close() {
        client.close();
    }
    
}
