/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj.rest.serveri;

import com.google.gson.Gson;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.marhranj.konfiguracije.GeneralnaKonfiguracija;
import org.foi.nwtis.marhranj.web.KonektorBazePodataka;
import org.foi.nwtis.marhranj.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.marhranj.web.zrna.Aerodrom;
import org.foi.nwtis.marhranj.web.zrna.RestWsOdgovor;
import org.foi.nwtis.rest.klijenti.LIQKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.Lokacija;

/**
 * REST Web Service
 *
 * @author marhranj
 * 
 * Klasa koja prima te obrađuje REST zahtjeve
 * 
 */
@Path("aerodromi")
public class AIRP2REST {
    
    private static final String KOORDINATE = "COORDINATES";
    private static final String ICAO = "IDENT";
    private static final String NAZIV = "NAME";
    private static final String ISO_KRATICA_DRZAVE = "ISO_COUNTRY";
    
    private static final String STATUS_OK = "OK";
    private static final String STATUS_ERROR = "ERR";

    /**
     * GET rest metoda koja vraća sve aerodrome iz tablice MYAIRPORTS
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();
        
        try (Connection con = KonektorBazePodataka.dajKonekciju(); 
                PreparedStatement dajSveAerodrome = con.prepareStatement("Select * from MYAIRPORTS");
                ResultSet rezultat = dajSveAerodrome.executeQuery();) {
            
            List<Aerodrom> aerodromi = new ArrayList<>();
            while(rezultat.next()) {
                String[] koordinate = rezultat.getString(KOORDINATE).split(", ");
                Lokacija lokacija = new Lokacija(koordinate[0], koordinate[1]);

                Aerodrom aerodrom = new Aerodrom(
                    rezultat.getString(ICAO), 
                    rezultat.getString(NAZIV),
                    rezultat.getString(ISO_KRATICA_DRZAVE),
                    lokacija);

                aerodromi.add(aerodrom);
            }
            
            restOdgovor.setStatus(STATUS_OK);
            restOdgovor.setOdgovor(aerodromi);
        } catch(SQLException ex) {
            restOdgovor.setStatus(STATUS_ERROR);
            restOdgovor.setPoruka("SQL Exception");
            System.out.println("SQLException: " + ex);
        }
        
        return new Gson().toJson(restOdgovor);
    }
    
    /**
     * GET rest metoda koja vraća sve aerodrome iz tablice MYAIRPORTS s odgovarajućim ID-om
     * @param id
     * @return
     */
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJsonId(@PathParam("id") String id) {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();
        
        try (Connection con = KonektorBazePodataka.dajKonekciju(); 
                PreparedStatement dajAerodrom = con.prepareStatement("SELECT * from MYAIRPORTS WHERE IDENT = ?");) {
            
            dajAerodrom.setString(1, id);
            ResultSet rezultat = dajAerodrom.executeQuery();
            
            List<Aerodrom> aerodromi = new ArrayList<>();
            while(rezultat.next()) {
                String[] koordinate = rezultat.getString(KOORDINATE).split(", ");
                Lokacija lokacija = new Lokacija(koordinate[0], koordinate[1]);

                Aerodrom aerodrom = new Aerodrom(
                    rezultat.getString(ICAO), 
                    rezultat.getString(NAZIV),
                    rezultat.getString(ISO_KRATICA_DRZAVE),
                    lokacija);

                aerodromi.add(aerodrom);
            }
            
            rezultat.close();
            restOdgovor.setStatus(STATUS_OK);
            restOdgovor.setOdgovor(aerodromi);
        } catch(SQLException ex) {
            restOdgovor.setStatus(STATUS_ERROR);
            restOdgovor.setPoruka("SQL Exception");
            System.out.println("SQLException: " + ex);
        }
        
        return new Gson().toJson(restOdgovor);
    } 
    
    /**
     * GET rest metoda koja vraća sve polazišne avione za odabrani ID aerodroma 
     * @param id
     * @param odVremena
     * @param doVremena
     * @return
     */
    @Path("{id}/avioni")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJsonIdAvioni(@PathParam("id") String id, @QueryParam("odVremena") int odVremena, @QueryParam("doVremena") int doVremena) {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();
        List<AvionLeti> poletjeliAvioni = new ArrayList<>();

        try (Connection con = KonektorBazePodataka.dajKonekciju(); 
            PreparedStatement dajAvioneZaAerodrom = con.prepareStatement("SELECT * FROM AIRPLANES WHERE ESTDEPARTUREAIRPORT = ? AND LASTSEEN BETWEEN ? AND ?");) {

            dajAvioneZaAerodrom.setString(1, id);
            dajAvioneZaAerodrom.setInt(2, odVremena);
            dajAvioneZaAerodrom.setInt(3, doVremena);

            ResultSet rezultat = dajAvioneZaAerodrom.executeQuery();

            while (rezultat.next()) {
                AvionLeti avionLeti = new AvionLeti();

                avionLeti.setIcao24(rezultat.getString("ICAO24"));
                avionLeti.setFirstSeen(rezultat.getInt("FIRSTSEEN"));
                avionLeti.setEstDepartureAirport(rezultat.getString("ESTDEPARTUREAIRPORT"));
                avionLeti.setLastSeen(rezultat.getInt("LASTSEEN"));
                avionLeti.setEstArrivalAirport(rezultat.getString("ESTARRIVALAIRPORT"));
                avionLeti.setCallsign(rezultat.getString("CALLSIGN"));
                avionLeti.setEstDepartureAirportHorizDistance(rezultat.getInt("ESTDEPARTUREAIRPORTHORIZDISTANCE"));
                avionLeti.setEstDepartureAirportVertDistance(rezultat.getInt("ESTDEPARTUREAIRPORTVERTDISTANCE"));
                avionLeti.setEstArrivalAirportHorizDistance(rezultat.getInt("ESTARRIVALAIRPORTHORIZDISTANCE"));
                avionLeti.setEstArrivalAirportVertDistance(rezultat.getInt("ESTARRIVALAIRPORTVERTDISTANCE"));
                avionLeti.setDepartureAirportCandidatesCount(rezultat.getInt("DEPARTUREAIRPORTCANDIDATESCOUNT"));
                avionLeti.setArrivalAirportCandidatesCount(rezultat.getInt("ARRIVALAIRPORTCANDIDATESCOUNT"));

                poletjeliAvioni.add(avionLeti);
            }

            rezultat.close();
            restOdgovor.setStatus(STATUS_OK);
            restOdgovor.setOdgovor(poletjeliAvioni);
        } catch(SQLException ex) {
            restOdgovor.setStatus(STATUS_ERROR);
            restOdgovor.setPoruka("SQL Exception");
            System.out.println("SQLException: " + ex);
        }
        
        return new Gson().toJson(restOdgovor);
    }
    
    /**
     * POST rest metoda koja dodaje novi aerodrom u tablicu MYAIRPORTS na temelju primljenog JSON-a
     * @param content
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String postJson(String content) {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();

        try {
            JsonReader jsonReader = Json.createReader(new StringReader(content));
            JsonObject reply = jsonReader.readObject();
            String icao = reply.getString("icao");
            
            try (Connection con = KonektorBazePodataka.dajKonekciju(); 
                PreparedStatement dajAerodrom = con.prepareStatement("SELECT name, iso_country FROM AIRPORTS WHERE ident= ?");) {

                dajAerodrom.setString(1, icao);
                ResultSet rezultat = dajAerodrom.executeQuery();

                if (rezultat.next()) {
                    Lokacija lokacija = this.getLIQKlijent().getGeoLocation(rezultat.getString(NAZIV));
                    PreparedStatement dodavanjeAviona = con.prepareStatement("INSERT INTO MYAIRPORTS (IDENT, NAME, ISO_COUNTRY, COORDINATES, STORED) VALUES (?, ?, ?, ?, ?)");

                    dodavanjeAviona.setString(1, icao);
                    dodavanjeAviona.setString(2, rezultat.getString(NAZIV));
                    dodavanjeAviona.setString(3, rezultat.getString(ISO_KRATICA_DRZAVE));
                    dodavanjeAviona.setString(4, lokacija.getLatitude() + ", " + lokacija.getLongitude());
                    dodavanjeAviona.setTimestamp(5, new Timestamp(System.currentTimeMillis()));

                    dodavanjeAviona.execute();
                    
                    restOdgovor.setStatus(STATUS_OK);
                    restOdgovor.setOdgovor(new ArrayList<>());
                } else {
                    restOdgovor.setStatus(STATUS_ERROR);
                    restOdgovor.setPoruka("U bazi podataka ne postoji aerodrom sa ovim ICAO kodom!");
                }
                
                rezultat.close();
            } catch(SQLException ex) {
                restOdgovor.setStatus(STATUS_ERROR);
                restOdgovor.setPoruka("Problem kod rada s bazom podataka");
                System.out.println("SQLException: " + ex);
            }
        } catch (Exception ex) {
            restOdgovor.setStatus(STATUS_ERROR);
            restOdgovor.setPoruka("Problem kod parsiranja ulaznih podataka");
            System.out.println("Exception: " + ex);
        }
        
        return new Gson().toJson(restOdgovor);
    }

    /**
     * PUT rest metoda koja ažurira aerodrom s navedenim ID-om iz tablice MYAIRPORTS na temelju primljenog JSON-a
     * @param id
     * @param content
     * @return
     */
    @Path("{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String putJson(@PathParam("id") String id, String content) {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();

        try {
            JsonReader jsonReader = Json.createReader(new StringReader(content));
            JsonObject reply = jsonReader.readObject();
            String naziv = reply.getString("naziv");
            String adresa = reply.getString("adresa");
            
            Lokacija lokacija = getLIQKlijent().getGeoLocation(adresa);
            
            try (Connection con = KonektorBazePodataka.dajKonekciju(); 
                PreparedStatement azurirajAerodrom = con.prepareStatement("UPDATE MYAIRPORTS SET NAME = ?, COORDINATES = ? WHERE IDENT = ?");) {

                azurirajAerodrom.setString(1, naziv);
                azurirajAerodrom.setString(2, lokacija.getLatitude() + ", " + lokacija.getLongitude());
                azurirajAerodrom.setString(3, id);
                azurirajAerodrom.execute();

                restOdgovor.setStatus(STATUS_OK);
                restOdgovor.setOdgovor(new ArrayList<>());
            } catch(SQLException ex) {
                restOdgovor.setStatus(STATUS_ERROR);
                restOdgovor.setPoruka("Problem kod rada s bazom podataka");
                System.out.println("SQLException: " + ex);
            }
        } catch (Exception ex) {
            restOdgovor.setStatus(STATUS_ERROR);
            restOdgovor.setPoruka("Problem kod parsiranja ulaznih podataka");
            System.out.println("Exception: " + ex);
        }
        
        return new Gson().toJson(restOdgovor);
    }
    
    /**
     * DELETE rest metoda koja briše aerodrom s navedenim ID-om iz tablice MYAIRPORTS
     * @param id
     * @return
     */
    @Path("{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteJson(@PathParam("id") String id) {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();

        try (Connection con = KonektorBazePodataka.dajKonekciju(); 
            PreparedStatement brisiAerodrom = con.prepareStatement("DELETE FROM MYAIRPORTS WHERE IDENT = ?");) {
           
            brisiAerodrom.setString(1, id);
            brisiAerodrom.execute();
            
            restOdgovor.setStatus(STATUS_OK);
            restOdgovor.setOdgovor(new ArrayList<>());
        } catch(SQLException ex) {
            restOdgovor.setStatus(STATUS_ERROR);
            restOdgovor.setPoruka("SQL Exception");
            System.out.println("SQLException: " + ex);
        }
        
        return new Gson().toJson(restOdgovor);
    }
    
    /**
     * Metoda koja vraća GeneralnaKonfiguracija
     * @return
     */
    private GeneralnaKonfiguracija getKonfiguracija() {
        return (GeneralnaKonfiguracija) SlusacAplikacije.getServletContext().getAttribute(SlusacAplikacije.KONFIGURACIJA_IME_ATRIBUTA);
    }
    
    /**
     * Metoda koja vraća LIQKlijent
     * @return
     */
    private LIQKlijent getLIQKlijent() {
        return new LIQKlijent(this.getKonfiguracija().getLocationIqToken());
    }
    
}
