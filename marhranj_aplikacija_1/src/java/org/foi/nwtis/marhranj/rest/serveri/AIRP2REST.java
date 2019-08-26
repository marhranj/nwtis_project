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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.marhranj.BrojacVremena;
import org.foi.nwtis.marhranj.PisacDnevnika;
import org.foi.nwtis.marhranj.konfiguracije.GeneralnaKonfiguracija;
import org.foi.nwtis.marhranj.utils.BPUtils;
import org.foi.nwtis.marhranj.utils.GrupeUtils;
import org.foi.nwtis.marhranj.utils.JsonUtils;
import org.foi.nwtis.marhranj.web.KonektorBazePodataka;
import org.foi.nwtis.marhranj.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.marhranj.web.zrna.Aerodrom;
import org.foi.nwtis.marhranj.web.zrna.MojAvionLeti;
import org.foi.nwtis.marhranj.web.zrna.RestWsOdgovor;
import org.foi.nwtis.marhranj.ws.servisi.Avion;
import org.foi.nwtis.rest.klijenti.LIQKlijent;
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

    private final PisacDnevnika pisacDnevnika = new PisacDnevnika();
    
    @Context 
    private HttpServletRequest request;

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
    public String dohvatiAerodromPremaId(@PathParam("id") String id, @QueryParam("korisnickoIme") String korisnickoIme, @QueryParam("lozinka") String lozinka) {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();

        BrojacVremena brojacVremena = new BrojacVremena();

        if (BPUtils.provjeriKorisnika(korisnickoIme, lozinka)) {

            List<Aerodrom> aerodromi = mapirajWsUAerodrome(GrupeUtils.dohvatiSveAerodrome());

            if (aerodromi.isEmpty()) {
                dohvatiSveAerodrome(korisnickoIme, lozinka);

                try (Connection con = KonektorBazePodataka.dajKonekciju();
                        PreparedStatement dajAerodrom = con.prepareStatement("SELECT * from MYAIRPORTS WHERE IDENT = ?");) {

                    dajAerodrom.setString(1, id);
                    ResultSet rezultat = dajAerodrom.executeQuery();

                    aerodromi = BPUtils.dohvatiAerodromeIzResultSeta(rezultat);

                    rezultat.close();

                    restOdgovor.setStatus(STATUS_OK);
                    restOdgovor.setOdgovor(aerodromi);
                } catch (SQLException e) {
                    restOdgovor.setStatus(STATUS_ERROR);
                    restOdgovor.setPoruka("SQL Exception");
                    System.out.println("SQLException: " + e);
                }
            } else {
                aerodromi = aerodromi.stream()
                        .filter(aerodrom -> aerodrom.getIcao().equals(id))
                        .collect(Collectors.toList());

                restOdgovor.setStatus(STATUS_OK);
                restOdgovor.setOdgovor(aerodromi);
            }

        } else {
            restOdgovor.setStatus(STATUS_ERROR);
            restOdgovor.setPoruka("Neuspješna autentikacija");
        }
        
        pisacDnevnika.upisUDnevnik(korisnickoIme, "GET dohvacenje aerodroma prema id", "REST", 
                request.getRemoteHost(), request.getRemoteAddr(), 
                brojacVremena.dohvatiVrijemeProsloOdInicijalizacije());

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
    public String dohvatiAvionePremaIdAerodroma(@PathParam("id") String id, @QueryParam("korisnickoIme") String korisnickoIme, @QueryParam("lozinka") String lozinka) {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();
        
        BrojacVremena brojacVremena = new BrojacVremena();

        if (BPUtils.provjeriKorisnika(korisnickoIme, lozinka)) {

            List<MojAvionLeti> avioni = mapirajUMojAvionLetiListu(GrupeUtils.dohvatiSveAvioneAerodromaGrupe(id));

            if (avioni.isEmpty()) {
                try (Connection con = KonektorBazePodataka.dajKonekciju();
                        PreparedStatement dajAvioneZaAerodrom = con.prepareStatement("SELECT * FROM AIRPLANES WHERE ESTDEPARTUREAIRPORT = ?");) {

                    dajAvioneZaAerodrom.setString(1, id);

                    ResultSet rezultat = dajAvioneZaAerodrom.executeQuery();

                    avioni = BPUtils.dohvatiAvioneIzResultSeta(rezultat);

                    rezultat.close();

                    restOdgovor.setStatus(STATUS_OK);
                    restOdgovor.setOdgovor(avioni);
                    GrupeUtils.dodajAvioneGrupi(avioni);
                } catch (SQLException e) {
                    restOdgovor.setStatus(STATUS_ERROR);
                    restOdgovor.setPoruka("SQL Exception");
                    System.out.println("SQLException: " + e);
                }
            } else {
                restOdgovor.setStatus(STATUS_OK);
                restOdgovor.setOdgovor(avioni);
            }

        } else {
            restOdgovor.setStatus(STATUS_ERROR);
            restOdgovor.setPoruka("Neuspješna autentikacija");
        }
        
        pisacDnevnika.upisUDnevnik(korisnickoIme, "GET dohvacenje aviona prema id aerodroma", "REST", 
                request.getRemoteHost(), request.getRemoteAddr(), 
                brojacVremena.dohvatiVrijemeProsloOdInicijalizacije());

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
    public String dodajAerodrom(@QueryParam("korisnickoIme") String korisnickoIme, @QueryParam("lozinka") String lozinka, String json) {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();
        
        BrojacVremena brojacVremena = new BrojacVremena();

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

                        Aerodrom aerodrom = new Aerodrom(
                                icao,
                                rezultat.getString(NAZIV),
                                rezultat.getString(ISO_KRATICA_DRZAVE),
                                lokacija
                        );

                        dodavanjeAviona.setString(1, aerodrom.getIcao());
                        dodavanjeAviona.setString(2, aerodrom.getNaziv());
                        dodavanjeAviona.setString(3, aerodrom.getDrzava());
                        dodavanjeAviona.setString(4, aerodrom.getLokacija().getLatitude() + ", " + aerodrom.getLokacija().getLongitude());
                        dodavanjeAviona.setTimestamp(5, new Timestamp(System.currentTimeMillis()));

                        dodavanjeAviona.execute();

                        restOdgovor.setStatus(STATUS_OK);
                        restOdgovor.setOdgovor(new ArrayList<>());
                        GrupeUtils.dodajAerodromeGrupe(Collections.singletonList(aerodrom));
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
        
        pisacDnevnika.upisUDnevnik(korisnickoIme, "POST dodavanje aerodroma na temelju JSON-a", "REST", 
                request.getRemoteHost(), request.getRemoteAddr(), 
                brojacVremena.dohvatiVrijemeProsloOdInicijalizacije());

        return new Gson().toJson(restOdgovor);
    }

    /**
     * POST rest metoda koja dodaje nove avione aerodromu sa navedenim id
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
    public String dodajAvioneAerodromu(@PathParam("id") String id, @QueryParam("korisnickoIme") String korisnickoIme, @QueryParam("lozinka") String lozinka, String json) {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();
        
        BrojacVremena brojacVremena = new BrojacVremena();

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

                    List<MojAvionLeti> avioni = new ArrayList<>();

                    while (iterator.hasNext()) {
                        MojAvionLeti avion = gson.fromJson(iterator.next(), MojAvionLeti.class);
                        avion.setEstDepartureAirport(id);
                        dodajAvionAerodromu = azurirajInsertUpitZaDodavanjeAvionaAerodromu(avion, dodajAvionAerodromu);
                        avioni.add(avion);
                    }

                    dodajAvionAerodromu.executeBatch();
                    dodajAvionAerodromu.close();

                    restOdgovor.setStatus(STATUS_OK);
                    restOdgovor.setOdgovor(new ArrayList<>());

                    GrupeUtils.dodajAvioneGrupi(avioni);
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
        
        pisacDnevnika.upisUDnevnik(korisnickoIme, "POST dodavanje aviona aerodromu", "REST", 
                request.getRemoteHost(), request.getRemoteAddr(), 
                brojacVremena.dohvatiVrijemeProsloOdInicijalizacije());

        return new Gson().toJson(restOdgovor);
    }

    /**
     * PUT rest metoda koja ažurira aerodrom s navedenim ID-om u tablici
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
    public String azurirajAerodrom(@PathParam("id") String id, @QueryParam("korisnickoIme") String korisnickoIme, @QueryParam("lozinka") String lozinka, String json) {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();
        
        BrojacVremena brojacVremena = new BrojacVremena();

        try {
            JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
            String naziv = jsonObject.get("naziv").getAsString();
            String adresa = jsonObject.get("adresa").getAsString();

            Lokacija lokacija = getLIQKlijent().getGeoLocation(adresa);

            if (BPUtils.provjeriKorisnika(korisnickoIme, lozinka)) {

                try (Connection con = KonektorBazePodataka.dajKonekciju();
                        PreparedStatement azurirajAerodrom = con.prepareStatement("UPDATE MYAIRPORTS SET NAME = ?, COORDINATES = ? WHERE IDENT = ?");) {

                    azurirajAerodrom.setString(1, naziv);
                    azurirajAerodrom.setString(2, lokacija.getLatitude() + ", " + lokacija.getLongitude());
                    azurirajAerodrom.setString(3, id);
                    azurirajAerodrom.execute();

                    restOdgovor.setStatus(STATUS_OK);
                    restOdgovor.setOdgovor(new ArrayList<>());
                } catch (SQLException e) {
                    restOdgovor.setStatus(STATUS_ERROR);
                    restOdgovor.setPoruka("Problem kod rada s bazom podataka");
                    System.out.println("SQLException: " + e);
                }
            } else {
                restOdgovor.setStatus(STATUS_ERROR);
                restOdgovor.setPoruka("Neuspješna autentikacija");
            }

            if (Objects.nonNull(restOdgovor.getOdgovor())) {
                JsonObject jsonOdgovor = (JsonObject) new JsonParser().parse(dohvatiSveAerodrome(korisnickoIme, lozinka));
                List<Aerodrom> sviAerodromi = JsonUtils.dohvatiAerodromeIzJsona(jsonOdgovor)
                        .stream()
                        .map(aerodrom -> azurirajOdgovarajuciAerodrom(aerodrom, id, naziv, lokacija))
                        .collect(Collectors.toList());
                GrupeUtils.azurirajAerodromeGrupe(sviAerodromi);
            }

        } catch (Exception ex) {
            restOdgovor.setStatus(STATUS_ERROR);
            restOdgovor.setPoruka("Problem kod parsiranja ulaznih podataka");
            System.out.println("Exception: " + ex);
        }
        
        pisacDnevnika.upisUDnevnik(korisnickoIme, "PUT azuriranje aerodroma", "REST", 
                request.getRemoteHost(), request.getRemoteAddr(), 
                brojacVremena.dohvatiVrijemeProsloOdInicijalizacije());

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
    public String obrisiAerodrom(@PathParam("id") String id, @QueryParam("korisnickoIme") String korisnickoIme, @QueryParam("lozinka") String lozinka) {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();
        
        BrojacVremena brojacVremena = new BrojacVremena();

        if (BPUtils.provjeriKorisnika(korisnickoIme, lozinka)) {

            JsonObject jsonObject = (JsonObject) new JsonParser().parse(dohvatiAerodromPremaId(id, korisnickoIme, lozinka));
            List<Aerodrom> aerodromi = JsonUtils.dohvatiAerodromeIzJsona(jsonObject);

            try (Connection con = KonektorBazePodataka.dajKonekciju();
                    PreparedStatement brisiAerodrom = con.prepareStatement("DELETE FROM MYAIRPORTS WHERE IDENT = ?");) {

                brisiAerodrom.setString(1, id);
                brisiAerodrom.execute();

                restOdgovor.setStatus(STATUS_OK);
                restOdgovor.setOdgovor(new ArrayList<>());

                GrupeUtils.obrisiOdabraneAerodromeGrupe(aerodromi);
            } catch (SQLException e) {
                restOdgovor.setStatus(STATUS_ERROR);
                restOdgovor.setPoruka("SQL Exception");
                System.out.println("SQLException: " + e);
            }
        } else {
            restOdgovor.setStatus(STATUS_ERROR);
            restOdgovor.setPoruka("Neuspješna autentikacija");
        }
        
        pisacDnevnika.upisUDnevnik(korisnickoIme, "DELETE brisanje aerodroma prema id", "REST", 
                request.getRemoteHost(), request.getRemoteAddr(), 
                brojacVremena.dohvatiVrijemeProsloOdInicijalizacije());

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
    public String obrisiSveAvioneAerodroma(@PathParam("id") String id, @QueryParam("korisnickoIme") String korisnickoIme, @QueryParam("lozinka") String lozinka) {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();
        
        BrojacVremena brojacVremena = new BrojacVremena();

        if (BPUtils.provjeriKorisnika(korisnickoIme, lozinka)) {

            JsonObject jsonObject = (JsonObject) new JsonParser().parse(dohvatiAvionePremaIdAerodroma(id, korisnickoIme, lozinka));
            List<MojAvionLeti> avioni = JsonUtils.dohvatiAvioneIzJsona(jsonObject);

            try (Connection con = KonektorBazePodataka.dajKonekciju();
                    PreparedStatement brisiAvione = con.prepareStatement("DELETE FROM AIRPLANES WHERE ESTDEPARTUREAIRPORT = ?");) {

                brisiAvione.setString(1, id);
                brisiAvione.execute();

                restOdgovor.setStatus(STATUS_OK);
                restOdgovor.setOdgovor(new ArrayList<>());

                avioni = avioni.stream()
                        .filter(avion -> !avion.getEstDepartureAirport().equals(id))
                        .collect(Collectors.toList());
                GrupeUtils.azurirajAvioneGrupe(avioni);
            } catch (SQLException e) {
                restOdgovor.setStatus(STATUS_ERROR);
                restOdgovor.setPoruka("SQL Exception");
                System.out.println("SQLException: " + e);
            }
        } else {
            restOdgovor.setStatus(STATUS_ERROR);
            restOdgovor.setPoruka("Neuspješna autentikacija");
        }
        
        pisacDnevnika.upisUDnevnik(korisnickoIme, "DELETE brisanje svih aviona aerodroma", "REST", 
                request.getRemoteHost(), request.getRemoteAddr(), 
                brojacVremena.dohvatiVrijemeProsloOdInicijalizacije());

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
    public String obrisiOdabraneAvioneAerodroma(@PathParam("id") String id, @PathParam("aid") String aid, @QueryParam("korisnickoIme") String korisnickoIme, @QueryParam("lozinka") String lozinka) {
        RestWsOdgovor restOdgovor = new RestWsOdgovor();
        
        BrojacVremena brojacVremena = new BrojacVremena();

        if (BPUtils.provjeriKorisnika(korisnickoIme, lozinka)) {

            JsonObject jsonObject = (JsonObject) new JsonParser().parse(dohvatiAvionePremaIdAerodroma(id, korisnickoIme, lozinka));
            List<MojAvionLeti> avioni = JsonUtils.dohvatiAvioneIzJsona(jsonObject);

            try (Connection con = KonektorBazePodataka.dajKonekciju();
                    PreparedStatement brisiAvione = con.prepareStatement("DELETE FROM AIRPLANES WHERE ESTDEPARTUREAIRPORT = ? AND ICAO24 = ?;");) {

                brisiAvione.setString(1, id);
                brisiAvione.setString(2, aid);

                brisiAvione.execute();

                restOdgovor.setStatus(STATUS_OK);
                restOdgovor.setOdgovor(new ArrayList<>());

                avioni = avioni.stream()
                        .filter(avion -> !(avion.getIcao24().equals(aid) && avion.getEstDepartureAirport().equals(id)))
                        .collect(Collectors.toList());

                GrupeUtils.azurirajAvioneGrupe(avioni);
            } catch (SQLException e) {
                restOdgovor.setStatus(STATUS_ERROR);
                restOdgovor.setPoruka("SQL Exception");
                System.out.println("SQLException: " + e);
            }
        } else {
            restOdgovor.setStatus(STATUS_ERROR);
            restOdgovor.setPoruka("Neuspješna autentikacija");
        }
        
        pisacDnevnika.upisUDnevnik(korisnickoIme, "DELETE brisanje odabranih aviona aerodroma", "REST", 
                request.getRemoteHost(), request.getRemoteAddr(), 
                brojacVremena.dohvatiVrijemeProsloOdInicijalizacije());

        return new Gson().toJson(restOdgovor);
    }

    /**
     * Metoda koja dodaje avion u insert upit
     *
     * @param letAviona
     * @param statement
     * @throws SQLException
     */
    private PreparedStatement azurirajInsertUpitZaDodavanjeAvionaAerodromu(MojAvionLeti letAviona, PreparedStatement statement) throws SQLException {
        statement.setString(1, letAviona.getIcao24());
        statement.setInt(2, letAviona.getFirstSeen());
        statement.setString(3, letAviona.getEstDepartureAirport());
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

    private List<Aerodrom> mapirajWsUAerodrome(List<org.foi.nwtis.marhranj.ws.servisi.Aerodrom> wsAerodromi) {
        return wsAerodromi.stream()
                .map(this::mapirajWsUAerodrom)
                .collect(Collectors.toList());
    }

    private Aerodrom mapirajWsUAerodrom(org.foi.nwtis.marhranj.ws.servisi.Aerodrom wsAerodrom) {
        Lokacija lokacija = new Lokacija(wsAerodrom.getLokacija().getLatitude(), wsAerodrom.getLokacija().getLongitude());
        return new Aerodrom(
                wsAerodrom.getIcao(),
                wsAerodrom.getNaziv(),
                wsAerodrom.getDrzava(),
                lokacija
        );
    }

    private List<MojAvionLeti> mapirajUMojAvionLetiListu(List<Avion> avioni) {
        return avioni.stream()
                .map(this::mapirajUMojAvionLeti)
                .collect(Collectors.toList());
    }

    private MojAvionLeti mapirajUMojAvionLeti(Avion avion) {
        return new MojAvionLeti(avion);
    }
    
    private Aerodrom azurirajOdgovarajuciAerodrom(Aerodrom aerodrom, String id, String naziv, Lokacija lokacija) {
        if (aerodrom.getIcao().equals(id)) {
            aerodrom.setNaziv(naziv);
            aerodrom.setLokacija(lokacija);
        }
        return aerodrom;
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
