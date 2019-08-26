/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj.rest.serveri;

import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author marhranj
 */
@Path("korisnici")
public class KorisniciRestAPI {
    
     /**
     * GET rest metoda koja vraća sve aerodrome iz tablice MYAIRPORTS
     *
     * @param korisnickoIme
     * @param lozinka
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String dohvatiSveAerodrome(@QueryParam("korisnickoIme") String korisnickoIme, @QueryParam("lozinka") String lozinka) {
        
        RestWsOdgovor restOdgovor = new RestWsOdgovor();

        BrojacVremena brojacVremena = new BrojacVremena();
        
        if (BPUtils.provjeriKorisnika(korisnickoIme, lozinka)) {

            List<Aerodrom> aerodromi = mapirajWsUAerodrome(GrupeUtils.dohvatiSveAerodrome());

            if (aerodromi.isEmpty()) {
                try (Connection con = KonektorBazePodataka.dajKonekciju();
                        PreparedStatement dajSveAerodrome = con.prepareStatement("Select * from MYAIRPORTS");
                        ResultSet rezultat = dajSveAerodrome.executeQuery();) {

                    aerodromi = BPUtils.dohvatiAerodromeIzResultSeta(rezultat);

                    restOdgovor.setStatus(STATUS_OK);
                    restOdgovor.setOdgovor(aerodromi);
                    GrupeUtils.azurirajAerodromeGrupe(aerodromi);
                } catch (SQLException e) {
                    restOdgovor.setStatus(STATUS_ERROR);
                    restOdgovor.setPoruka("SQL Exception");
                    System.out.println("SQLException: " + e);
                }
            } else {
                restOdgovor.setStatus(STATUS_OK);
                restOdgovor.setOdgovor(aerodromi);
            }
        } else {
            restOdgovor.setStatus(STATUS_ERROR);
            restOdgovor.setPoruka("Neuspješna autentikacija");
        }
        
        pisacDnevnika.upisUDnevnik(korisnickoIme, "GET dohvacenje svih aerodroma", "REST", 
                request.getRemoteHost(), request.getRemoteAddr(), 
                brojacVremena.dohvatiVrijemeProsloOdInicijalizacije());

        return new Gson().toJson(restOdgovor);
    }

    
}
