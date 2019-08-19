/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj.rest.serveri;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
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
import javax.xml.ws.WebServiceRef;
import org.foi.nwtis.marhranj.konfiguracije.GeneralnaKonfiguracija;
import org.foi.nwtis.marhranj.utils.BPUtils;
import org.foi.nwtis.marhranj.web.KonektorBazePodataka;
import org.foi.nwtis.marhranj.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.marhranj.web.zrna.RestWsOdgovor;
import org.foi.nwtis.marhranj.ws.servisi.AerodromiWS;
import org.foi.nwtis.marhranj.ws.servisi.AerodromiWS_Service;
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

    private static final String NAZIV = "NAME";
    private static final String ISO_KRATICA_DRZAVE = "ISO_COUNTRY";

    private static final String STATUS_OK = "OK";
    private static final String STATUS_ERROR = "ERR";
    
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/nwtis.foi.hr_8080/NWTiS_2019/AerodromiWS.wsdl")
    private AerodromiWS_Service service = new AerodromiWS_Service();
    
    private final AerodromiWS port = service.getAerodromiWSPort();

    /**
     * GET rest metoda koja vraća sve aerodrome iz tablice MYAIRPORTS
     *
     * @param korisnickoIme
     * @param lozinka
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(@QueryParam("korisnickoIme") String korisnickoIme, @QueryParam("lozinka") String lozinka) {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();

        if (BPUtils.provjeriKorisnika(korisnickoIme, lozinka)) {
            try (Connection con = KonektorBazePodataka.dajKonekciju();
                    PreparedStatement dajSveAerodrome = con.prepareStatement("Select * from MYAIRPORTS");
                    ResultSet rezultat = dajSveAerodrome.executeQuery();) {

                restOdgovor.setStatus(STATUS_OK);
                restOdgovor.setOdgovor(BPUtils.dohvatiAerodromeIzResultSeta(rezultat));
            } catch (SQLException ex) {
                restOdgovor.setStatus(STATUS_ERROR);
                restOdgovor.setPoruka("SQL Exception");
                System.out.println("SQLException: " + ex);
            }
        } else {
            restOdgovor.setStatus(STATUS_ERROR);
            restOdgovor.setPoruka("Neuspješna autentikacija");
        }
        
        return new Gson().toJson(restOdgovor);
    }

    /**
     * GET rest metoda koja vraća sve aerodrome iz tablice MYAIRPORTS s
     * odgovarajućim ID-om
     *
     * @param id
     * @param korisnickoIme
     * @param lozinka
     * @return
     */
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJsonId(@PathParam("id") String id, @QueryParam("korisnickoIme") String korisnickoIme, @QueryParam("lozinka") String lozinka) {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();

        if (BPUtils.provjeriKorisnika(korisnickoIme, lozinka)) {
            try (Connection con = KonektorBazePodataka.dajKonekciju();
                    PreparedStatement dajAerodrom = con.prepareStatement("SELECT * from MYAIRPORTS WHERE IDENT = ?");) {

                dajAerodrom.setString(1, id);
                ResultSet rezultat = dajAerodrom.executeQuery();

                restOdgovor.setOdgovor(BPUtils.dohvatiAerodromeIzResultSeta(rezultat));
                restOdgovor.setStatus(STATUS_OK);

                rezultat.close();
            } catch (SQLException ex) {
                restOdgovor.setStatus(STATUS_ERROR);
                restOdgovor.setPoruka("SQL Exception");
                System.out.println("SQLException: " + ex);
            }
        } else {
            restOdgovor.setStatus(STATUS_ERROR);
            restOdgovor.setPoruka("Neuspješna autentikacija");
        }

        return new Gson().toJson(restOdgovor);
    }

    /**
     * GET rest metoda koja vraća sve polazišne avione za odabrani ID aerodroma
     *
     * @param id
     * @param korisnickoIme
     * @param lozinka
     * @return
     */
    @Path("{id}/avion")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJsonIdAvion(@PathParam("id") String id, @QueryParam("korisnickoIme") String korisnickoIme, @QueryParam("lozinka") String lozinka) {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();

        if (BPUtils.provjeriKorisnika(korisnickoIme, lozinka)) {
            try (Connection con = KonektorBazePodataka.dajKonekciju();
                    PreparedStatement dajAvioneZaAerodrom = con.prepareStatement("SELECT * FROM AIRPLANES WHERE ESTDEPARTUREAIRPORT = ?");) {

                dajAvioneZaAerodrom.setString(1, id);

                ResultSet rezultat = dajAvioneZaAerodrom.executeQuery();

                restOdgovor.setOdgovor(BPUtils.dohvatiAvioneIzResultSeta(rezultat));
                restOdgovor.setStatus(STATUS_OK);

                rezultat.close();
            } catch (SQLException ex) {
                restOdgovor.setStatus(STATUS_ERROR);
                restOdgovor.setPoruka("SQL Exception");
                System.out.println("SQLException: " + ex);
            }
        } else {
            restOdgovor.setStatus(STATUS_ERROR);
            restOdgovor.setPoruka("Neuspješna autentikacija");
        }

        return new Gson().toJson(restOdgovor);
    }

    /**
     * POST rest metoda koja dodaje novi aerodrom u tablicu MYAIRPORTS na
     * temelju primljenog JSON-a
     *
     * @param korisnickoIme
     * @param lozinka
     * @param json
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String postJson(@QueryParam("korisnickoIme") String korisnickoIme, @QueryParam("lozinka") String lozinka, String json) {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();

        if (BPUtils.provjeriKorisnika(korisnickoIme, lozinka)) {
            try {
                JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
                String icao = jsonObject.get("icao").getAsString();

                try (Connection con = KonektorBazePodataka.dajKonekciju();
                        PreparedStatement dajAerodrom = con.prepareStatement("SELECT name, iso_country FROM AIRPORTS WHERE ident= ?");) {

                    dajAerodrom.setString(1, icao);
                    ResultSet rezultat = dajAerodrom.executeQuery();

                    if (rezultat.next()) {
                        Lokacija lokacija = this.getLIQKlijent().getGeoLocation(rezultat.getString(NAZIV));
                        PreparedStatement dodavanjeAviona = con.prepareStatement("INSERT INTO MYAIRPORTS (IDENT, NAME, ISO_COUNTRY, COORDINATES, `STORED`) VALUES (?, ?, ?, ?, ?)");

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
                } catch (SQLException e) {
                    restOdgovor.setStatus(STATUS_ERROR);
                    restOdgovor.setPoruka("Problem kod rada s bazom podataka");
                    System.out.println("SQLException: " + e);
                }

            } catch (Exception e) {
                restOdgovor.setStatus(STATUS_ERROR);
                restOdgovor.setPoruka("Problem kod parsiranja ulaznih podataka");
                System.out.println("Exception: " + e);
            }

        } else {
            restOdgovor.setStatus(STATUS_ERROR);
            restOdgovor.setPoruka("Neuspješna autentikacija");
        }

        return new Gson().toJson(restOdgovor);
    }

    /**
     * POST rest metoda koja dodaje novi aerodrom u tablicu MYAIRPORTS na
     * temelju primljenog JSON-a
     *
     * @param id
     * @param korisnickoIme
     * @param lozinka
     * @param json
     * @return
     */
    @POST
    @Path("{id}/avion")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String postJsonIdAvion(@PathParam("id") String id, @QueryParam("korisnickoIme") String korisnickoIme, @QueryParam("lozinka") String lozinka, String json) {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();

        if (BPUtils.provjeriKorisnika(korisnickoIme, lozinka)) {

            try {
                JsonArray jsonArray = (JsonArray) new JsonParser().parse(json);

                Gson gson = new Gson();

                try (Connection con = KonektorBazePodataka.dajKonekciju();) {

                    PreparedStatement dodajAvionAerodromu = con.prepareStatement(
                                "INSERT INTO AIRPLANES (ICAO24, FIRSTSEEN, ESTDEPARTUREAIRPORT, LASTSEEN, "
                                + "ESTARRIVALAIRPORT, CALLSIGN, ESTDEPARTUREAIRPORTHORIZDISTANCE, "
                                + "ESTDEPARTUREAIRPORTVERTDISTANCE, ESTARRIVALAIRPORTHORIZDISTANCE, "
                                + "ESTARRIVALAIRPORTVERTDISTANCE, DEPARTUREAIRPORTCANDIDATESCOUNT, "
                                + "ARRIVALAIRPORTCANDIDATESCOUNT, `STORED`)"
                                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    );
                    
                    Iterator<JsonElement> iterator = jsonArray.iterator();
                    
                    while (iterator.hasNext()) {
                        AvionLeti avion = gson.fromJson(iterator.next(), AvionLeti.class);
                        dodajAvionAerodromu = azurirajInsertUpitZaDodavanjeAvionaAerodromu(id, avion, dodajAvionAerodromu);
                    }
                    
                    dodajAvionAerodromu.executeBatch();
                    dodajAvionAerodromu.close();

                    restOdgovor.setStatus(STATUS_OK);
                    restOdgovor.setOdgovor(new ArrayList<>());
                } catch (SQLException ex) {
                    restOdgovor.setStatus(STATUS_ERROR);
                    restOdgovor.setPoruka("Problem kod rada s bazom podataka");
                    System.out.println("SQLException: " + ex);
                }

            } catch (Exception e) {
                restOdgovor.setStatus(STATUS_ERROR);
                restOdgovor.setPoruka("Problem kod parsiranja ulaznih podataka");
                System.out.println("Exception: " + e);
            }

        } else {
            restOdgovor.setStatus(STATUS_ERROR);
            restOdgovor.setPoruka("Neuspješna autentikacija");
        }

        return new Gson().toJson(restOdgovor);
    }

    /**
     * PUT rest metoda koja ažurira aerodrom s navedenim ID-om iz tablice
     * MYAIRPORTS na temelju primljenog JSON-a
     *
     * @param id
     * @param korisnickoIme
     * @param lozinka
     * @param json
     * @return
     */
    @Path("{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String putJson(@PathParam("id") String id, @QueryParam("korisnickoIme") String korisnickoIme, @QueryParam("lozinka") String lozinka, String json) {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();

        try {
            JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
            String naziv = jsonObject.get("naziv").getAsString();
            String adresa = jsonObject.get("adresa").getAsString();

            if (BPUtils.provjeriKorisnika(korisnickoIme, lozinka)) {
                Lokacija lokacija = getLIQKlijent().getGeoLocation(adresa);

                try (Connection con = KonektorBazePodataka.dajKonekciju();
                        PreparedStatement azurirajAerodrom = con.prepareStatement("UPDATE MYAIRPORTS SET NAME = ?, COORDINATES = ? WHERE IDENT = ?");) {

                    azurirajAerodrom.setString(1, naziv);
                    azurirajAerodrom.setString(2, lokacija.getLatitude() + ", " + lokacija.getLongitude());
                    azurirajAerodrom.setString(3, id);
                    azurirajAerodrom.execute();

                    restOdgovor.setStatus(STATUS_OK);
                    restOdgovor.setOdgovor(new ArrayList<>());
                } catch (SQLException ex) {
                    restOdgovor.setStatus(STATUS_ERROR);
                    restOdgovor.setPoruka("Problem kod rada s bazom podataka");
                    System.out.println("SQLException: " + ex);
                }
            } else {
                restOdgovor.setStatus(STATUS_ERROR);
                restOdgovor.setPoruka("Neuspješna autentikacija");
            }

        } catch (Exception ex) {
            restOdgovor.setStatus(STATUS_ERROR);
            restOdgovor.setPoruka("Problem kod parsiranja ulaznih podataka");
            System.out.println("Exception: " + ex);
        }

        return new Gson().toJson(restOdgovor);
    }

    /**
     * DELETE rest metoda koja briše aerodrom s navedenim ID-om iz tablice
     * MYAIRPORTS
     *
     * @param id
     * @param korisnickoIme
     * @param lozinka
     * @return
     */
    @Path("{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteJson(@PathParam("id") String id, @QueryParam("korisnickoIme") String korisnickoIme, @QueryParam("lozinka") String lozinka) {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();

        if (BPUtils.provjeriKorisnika(korisnickoIme, lozinka)) {
            try (Connection con = KonektorBazePodataka.dajKonekciju();
                    PreparedStatement brisiAerodrom = con.prepareStatement("DELETE FROM MYAIRPORTS WHERE IDENT = ?");) {

                brisiAerodrom.setString(1, id);
                brisiAerodrom.execute();

                restOdgovor.setStatus(STATUS_OK);
                restOdgovor.setOdgovor(new ArrayList<>());
            } catch (SQLException ex) {
                restOdgovor.setStatus(STATUS_ERROR);
                restOdgovor.setPoruka("SQL Exception");
                System.out.println("SQLException: " + ex);
            }
        } else {
            restOdgovor.setStatus(STATUS_ERROR);
            restOdgovor.setPoruka("Neuspješna autentikacija");
        }

        return new Gson().toJson(restOdgovor);
    }
    
    /**
     * DELETE rest metoda koja briše avion/e za aerodrom sa id
     *
     * @param id
     * @param korisnickoIme
     * @param lozinka
     * @return
     */
    @Path("{id}/avion")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteJsonIdAvion(@PathParam("id") String id, @QueryParam("korisnickoIme") String korisnickoIme, @QueryParam("lozinka") String lozinka) {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();

        if (BPUtils.provjeriKorisnika(korisnickoIme, lozinka)) {
            try (Connection con = KonektorBazePodataka.dajKonekciju();
                    PreparedStatement brisiAvione = con.prepareStatement("DELETE FROM AIRPLANES WHERE ESTDEPARTUREAIRPORT = ?");) {

                brisiAvione.setString(1, id);
                brisiAvione.execute();

                restOdgovor.setStatus(STATUS_OK);
                restOdgovor.setOdgovor(new ArrayList<>());
            } catch (SQLException ex) {
                restOdgovor.setStatus(STATUS_ERROR);
                restOdgovor.setPoruka("SQL Exception");
                System.out.println("SQLException: " + ex);
            }
        } else {
            restOdgovor.setStatus(STATUS_ERROR);
            restOdgovor.setPoruka("Neuspješna autentikacija");
        }

        return new Gson().toJson(restOdgovor);
    }
    
    /**
     * DELETE rest metoda koja briše avion/e sa aid za aerodrom sa id
     *
     * @param id
     * @param aid
     * @param korisnickoIme
     * @param lozinka
     * @return
     */
    @Path("{id}/avion/{aid}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteJsonIdAvionAid(@PathParam("id") String id, @PathParam("aid") String aid, @QueryParam("korisnickoIme") String korisnickoIme, @QueryParam("lozinka") String lozinka) {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();

        if (BPUtils.provjeriKorisnika(korisnickoIme, lozinka)) {
            try (Connection con = KonektorBazePodataka.dajKonekciju();
                    PreparedStatement brisiAvione = con.prepareStatement("DELETE FROM AIRPLANES WHERE ESTDEPARTUREAIRPORT = ? AND ICAO24 = ?;");) {

                brisiAvione.setString(1, id);
                brisiAvione.setString(2, aid);

                brisiAvione.execute();

                restOdgovor.setStatus(STATUS_OK);
                restOdgovor.setOdgovor(new ArrayList<>());
            } catch (SQLException ex) {
                restOdgovor.setStatus(STATUS_ERROR);
                restOdgovor.setPoruka("SQL Exception");
                System.out.println("SQLException: " + ex);
            }
        } else {
            restOdgovor.setStatus(STATUS_ERROR);
            restOdgovor.setPoruka("Neuspješna autentikacija");
        }

        return new Gson().toJson(restOdgovor);
    }

    /**
     * Metoda koja dodaje avion u insert upit
     *
     * @param letAviona
     * @param statement
     * @throws SQLException
     */
    private PreparedStatement azurirajInsertUpitZaDodavanjeAvionaAerodromu(String aerodromId, AvionLeti letAviona, PreparedStatement statement) throws SQLException {
        statement.setString(1, letAviona.getIcao24());
        statement.setInt(2, letAviona.getFirstSeen());
        statement.setString(3, aerodromId);
        statement.setInt(4, letAviona.getLastSeen());
        statement.setString(5, letAviona.getEstArrivalAirport());
        statement.setString(6, letAviona.getCallsign());
        statement.setInt(7, letAviona.getEstDepartureAirportHorizDistance());
        statement.setInt(8, letAviona.getEstDepartureAirportVertDistance());
        statement.setInt(9, letAviona.getEstArrivalAirportHorizDistance());
        statement.setInt(10, letAviona.getEstArrivalAirportVertDistance());
        statement.setInt(11, letAviona.getDepartureAirportCandidatesCount());
        statement.setInt(12, letAviona.getArrivalAirportCandidatesCount());
        statement.setTimestamp(13, new Timestamp(System.currentTimeMillis()));

        statement.addBatch();
        return statement;
    }

    /**
     * Metoda koja vraća GeneralnaKonfiguracija
     *
     * @return
     */
    private GeneralnaKonfiguracija getKonfiguracija() {
        return (GeneralnaKonfiguracija) SlusacAplikacije.getServletContext().getAttribute(SlusacAplikacije.KONFIGURACIJA_IME_ATRIBUTA);
    }

    /**
     * Metoda koja vraća LIQKlijent
     *
     * @return
     */
    private LIQKlijent getLIQKlijent() {
        return new LIQKlijent(this.getKonfiguracija().getLocationIqToken());
    }

}
