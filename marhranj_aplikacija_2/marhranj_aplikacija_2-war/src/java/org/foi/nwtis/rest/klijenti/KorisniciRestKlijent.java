/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.rest.klijenti;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

/**
 * Jersey REST client generated for REST resource:KorisniciRestAPI
 * [korisnici]<br>
 * USAGE:
 * <pre>
 *        KorisniciRestKlijent client = new KorisniciRestKlijent();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author marhranj
 */
public class KorisniciRestKlijent {

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://localhost:8084/marhranj_aplikacija_3-war/webresources";

    public KorisniciRestKlijent() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("korisnici");
    }

    public String azurirajKorisnika(Object requestEntity, String korisnickoIme, String lozinka) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (korisnickoIme != null) {
            resource = resource.queryParam("korisnickoIme", korisnickoIme);
        }
        if (lozinka != null) {
            resource = resource.queryParam("lozinka", lozinka);
        }
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
    }

    public String dodajKorisnika(Object requestEntity) throws ClientErrorException {
        return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
    }

    public String dohvatiSveKorisnike(String lozinka, String korisnickoIme) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (lozinka != null) {
            resource = resource.queryParam("lozinka", lozinka);
        }
        if (korisnickoIme != null) {
            resource = resource.queryParam("korisnickoIme", korisnickoIme);
        }
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
    }

    public String dohvatiAutenticirajJednogKorisnika(String id, String lozinka, String auth, String korisnickoIme) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (lozinka != null) {
            resource = resource.queryParam("lozinka", lozinka);
        }
        if (auth != null) {
            resource = resource.queryParam("auth", auth);
        }
        if (korisnickoIme != null) {
            resource = resource.queryParam("korisnickoIme", korisnickoIme);
        }
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
    }

    public void close() {
        client.close();
    }
    
}
