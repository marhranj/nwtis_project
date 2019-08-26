/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj.ws.serveri;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import org.foi.nwtis.marhranj.BrojacVremena;
import org.foi.nwtis.marhranj.PisacDnevnika;
import org.foi.nwtis.marhranj.konfiguracije.GeneralnaKonfiguracija;
import org.foi.nwtis.marhranj.utils.BPUtils;
import org.foi.nwtis.marhranj.utils.PretvaracVremena;
import org.foi.nwtis.marhranj.web.KonektorBazePodataka;
import org.foi.nwtis.marhranj.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.marhranj.web.zrna.Korisnik;
import org.foi.nwtis.marhranj.web.zrna.MojAvionLeti;
import org.foi.nwtis.rest.klijenti.OWMKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.MeteoPodaci;

/**
 *
 * @author marhranj
 *
 * Klasa koja prima te obrađuje SOAP zahtjeve
 *
 */
@WebService(serviceName = "AIRP2WS")
public class AIRP2WS {

    private final PisacDnevnika pisacDnevnika = new PisacDnevnika();

    @Resource
    private WebServiceContext context;

    
    /**
     * SOAP metoda koja vraća zadnje podatke o avionu poletjelog s navedenog aerodroma
     *
     * @param icao
     * @param korisnickoIme
     * @param lozinka
     * @return
     */
    @WebMethod(operationName = "zadnjiPodaciOAvionuSAerodroma")
    public AvionLeti zadnjiPodaciOAvionuSAerodroma(@WebParam(name = "icao") String icao,
            @WebParam(name = "korisnickoIme") String korisnickoIme, @WebParam(name = "lozinka") String lozinka) {

        BrojacVremena brojacVremena = new BrojacVremena();

        HttpServletRequest request = (HttpServletRequest) context.getMessageContext().get(MessageContext.SERVLET_REQUEST);

        MojAvionLeti avion = new MojAvionLeti();
        if (Objects.nonNull(icao) && BPUtils.provjeriKorisnika(korisnickoIme, lozinka)) {
            try (Connection con = KonektorBazePodataka.dajKonekciju();
                    PreparedStatement dajAvion = con.prepareStatement("SELECT * FROM AIRPLANES WHERE ESTDEPARTUREAIRPORT = ? ORDER BY `STORED` DESC LIMIT 1");) {

                dajAvion.setString(1, icao);

                ResultSet rezultat = dajAvion.executeQuery();

                avion = BPUtils.dohvatiAvioneIzResultSeta(rezultat)
                        .stream()
                        .findFirst()
                        .orElse(new MojAvionLeti());

                rezultat.close();
            } catch (SQLException ex) {
                System.out.println("SQLException: " + ex);
            }
        }

        pisacDnevnika.upisUDnevnik(korisnickoIme, "SOAP servis za dohvacanje zadnjih podataka o avionu s aerodroma", "SOAP",
                request.getRemoteHost(), request.getRemoteAddr(),
                brojacVremena.dohvatiVrijemeProsloOdInicijalizacije());

        return avion;
    }

    /**
     * SOAP metoda koja vraća zadnje podatke o N aviona poletjelih s navedenog
     * aerodroma
     *
     * @param icao
     * @param korisnickoIme
     * @param lozinka
     * @return
     */
    @WebMethod(operationName = "zadnjiPodaciOAvionimaSAerodroma")
    public List<MojAvionLeti> zadnjiPodaciOAvionimaSAerodroma(@WebParam(name = "icao") String icao, @WebParam(name = "kolicinaAviona") int kolicinaAviona,
            @WebParam(name = "korisnickoIme") String korisnickoIme, @WebParam(name = "lozinka") String lozinka) {

        BrojacVremena brojacVremena = new BrojacVremena();

        HttpServletRequest request = (HttpServletRequest) context.getMessageContext().get(MessageContext.SERVLET_REQUEST);

        List<MojAvionLeti> avioni = new ArrayList<>();
        if (Objects.nonNull(icao) && BPUtils.provjeriKorisnika(korisnickoIme, lozinka)) {
            try (Connection con = KonektorBazePodataka.dajKonekciju();
                    PreparedStatement dajAvione = con.prepareStatement("SELECT * FROM AIRPLANES WHERE ESTDEPARTUREAIRPORT = ? ORDER BY `STORED` DESC LIMIT ?");) {

                dajAvione.setString(1, icao);
                dajAvione.setInt(2, kolicinaAviona);

                ResultSet rezultat = dajAvione.executeQuery();

                avioni = BPUtils.dohvatiAvioneIzResultSeta(rezultat);

                rezultat.close();
            } catch (SQLException ex) {
                System.out.println("SQLException: " + ex);
            }
        }

        pisacDnevnika.upisUDnevnik(korisnickoIme, "SOAP servis za dohvacanje zadnjih podataka o avionima s aerodroma", "SOAP",
                request.getRemoteHost(), request.getRemoteAddr(),
                brojacVremena.dohvatiVrijemeProsloOdInicijalizacije());

        return avioni;
    }

    /**
     * SOAP metoda koja vraća avione poletjele sa aerodroma prema icao kodu te
     * između odVremena i doVremena
     *
     * @param icao
     * @param odVremena
     * @param doVremena
     * @param korisnickoIme
     * @param lozinka
     * @return
     */
    @WebMethod(operationName = "dajAvionePoletjeleSAerodroma")
    public List<MojAvionLeti> dajAvionePoletjeleSAerodroma(@WebParam(name = "icao") String icao, @WebParam(name = "odVremena") String odVremena, @WebParam(name = "doVremena") String doVremena,
            @WebParam(name = "korisnickoIme") String korisnickoIme, @WebParam(name = "lozinka") String lozinka) {

        BrojacVremena brojacVremena = new BrojacVremena();

        HttpServletRequest request = (HttpServletRequest) context.getMessageContext().get(MessageContext.SERVLET_REQUEST);

        List<MojAvionLeti> poletjeliAvioni = new ArrayList<>();

        if (Objects.nonNull(icao) && Objects.nonNull(odVremena) && Objects.nonNull(doVremena) && BPUtils.provjeriKorisnika(korisnickoIme, lozinka)) {
            try (Connection con = KonektorBazePodataka.dajKonekciju();
                    PreparedStatement dajAvione = con.prepareStatement("SELECT * FROM AIRPLANES WHERE ESTDEPARTUREAIRPORT = ? AND FIRSTSEEN BETWEEN ? AND ? ORDER BY FIRSTSEEN DESC");) {

                dajAvione.setString(1, icao);
                dajAvione.setLong(2, PretvaracVremena.pretvoriTimestampStringULong(odVremena));
                dajAvione.setLong(3, PretvaracVremena.pretvoriTimestampStringULong(doVremena));

                ResultSet rezultat = dajAvione.executeQuery();

                poletjeliAvioni = BPUtils.dohvatiAvioneIzResultSeta(rezultat);

                rezultat.close();
            } catch (SQLException ex) {
                System.out.println("SQLException: " + ex);
            }
        }

        pisacDnevnika.upisUDnevnik(korisnickoIme, "SOAP servis za dohvacanje aviona poletjelih s aerodroma", "SOAP",
                request.getRemoteHost(), request.getRemoteAddr(),
                brojacVremena.dohvatiVrijemeProsloOdInicijalizacije());

        return poletjeliAvioni;
    }

    /**
     * SOAP metoda koja vraća letove aviona prema icao24 kodu između odVremena i
     * doVremena
     *
     * @param icao24
     * @param odVremena
     * @param doVremena
     * @param korisnickoIme
     * @param lozinka
     * @return
     */
    @WebMethod(operationName = "dajLetoveAvionaUVremenskomIntervalu")
    public List<MojAvionLeti> dajLetoveAvionaUVremenskomIntervalu(@WebParam(name = "icao24") String icao24, @WebParam(name = "odVremena") String odVremena, @WebParam(name = "doVremena") String doVremena,
            @WebParam(name = "korisnickoIme") String korisnickoIme, @WebParam(name = "lozinka") String lozinka) {

        BrojacVremena brojacVremena = new BrojacVremena();

        HttpServletRequest request = (HttpServletRequest) context.getMessageContext().get(MessageContext.SERVLET_REQUEST);

        List<MojAvionLeti> poletjeliAvioni = new ArrayList<>();

        if (Objects.nonNull(icao24) && Objects.nonNull(odVremena) && Objects.nonNull(doVremena) && BPUtils.provjeriKorisnika(korisnickoIme, lozinka)) {
            try (Connection con = KonektorBazePodataka.dajKonekciju();
                    PreparedStatement dajAvione = con.prepareStatement("SELECT * FROM AIRPLANES WHERE ICAO24 = ? AND FIRSTSEEN BETWEEN ? AND ? ORDER BY FIRSTSEEN DESC");) {

                dajAvione.setString(1, icao24);
                dajAvione.setLong(2, PretvaracVremena.pretvoriTimestampStringULong(odVremena));
                dajAvione.setLong(3, PretvaracVremena.pretvoriTimestampStringULong(doVremena));

                ResultSet rezultat = dajAvione.executeQuery();

                poletjeliAvioni = BPUtils.dohvatiAvioneIzResultSeta(rezultat);

                rezultat.close();
            } catch (SQLException ex) {
                System.out.println("SQLException: " + ex);
            }
        }

        pisacDnevnika.upisUDnevnik(korisnickoIme, "SOAP servis za dohvacanje skom letova aviona u vremenskom intervalu", "SOAP",
                request.getRemoteHost(), request.getRemoteAddr(),
                brojacVremena.dohvatiVrijemeProsloOdInicijalizacije());

        return poletjeliAvioni;
    }

    /**
     * SOAP metoda koja vraća nazive aerodroma kroz koje je avion prolazio u
     * vremenskom periodu
     *
     * @param icao24
     * @param odVremena
     * @param doVremena
     * @param korisnickoIme
     * @param lozinka
     * @return
     */
    @WebMethod(operationName = "dajNaziveAerodromaKrozKojeJeAvionProlazio")
    public List<String> dajNaziveAerodromaKrozKojeJeAvionProlazio(@WebParam(name = "icao24") String icao24, @WebParam(name = "odVremena") String odVremena, @WebParam(name = "doVremena") String doVremena,
            @WebParam(name = "korisnickoIme") String korisnickoIme, @WebParam(name = "lozinka") String lozinka) {

        BrojacVremena brojacVremena = new BrojacVremena();

        HttpServletRequest request = (HttpServletRequest) context.getMessageContext().get(MessageContext.SERVLET_REQUEST);

        List<String> naziviAerodroma = new ArrayList<>();

        if (Objects.nonNull(icao24) && Objects.nonNull(odVremena) && Objects.nonNull(doVremena)
                && BPUtils.provjeriKorisnika(korisnickoIme, lozinka)) {
            try (Connection con = KonektorBazePodataka.dajKonekciju();
                    PreparedStatement dajAerodrom = con.prepareStatement("SELECT NAME FROM AIRPLANES, MYAIRPORTS WHERE ICAO24 = ? AND IDENT = ESTDEPARTUREAIRPORT AND FIRSTSEEN BETWEEN ? AND ? ORDER BY FIRSTSEEN DESC");) {

                dajAerodrom.setString(1, icao24);
                dajAerodrom.setLong(2, PretvaracVremena.pretvoriTimestampStringULong(odVremena));
                dajAerodrom.setLong(3, PretvaracVremena.pretvoriTimestampStringULong(doVremena));

                ResultSet rezultat = dajAerodrom.executeQuery();

                while (rezultat.next()) {
                    naziviAerodroma.add(rezultat.getString("NAME"));
                }

                rezultat.close();
            } catch (SQLException ex) {
                System.out.println("SQLException: " + ex);
            }
        }

        pisacDnevnika.upisUDnevnik(korisnickoIme, "SOAP servis za dohvacanje naziva aeorodroma kroz koje je avion prolazio", "SOAP",
                request.getRemoteHost(), request.getRemoteAddr(),
                brojacVremena.dohvatiVrijemeProsloOdInicijalizacije());

        return naziviAerodroma;
    }

    /**
     * SOAP metoda koja vraća meteo podatke za aerodrom
     *
     * @param icao
     * @param korisnickoIme
     * @param lozinka
     * @return
     */
    @WebMethod(operationName = "dajMeteoPodatkeZaAerodrom")
    public MeteoPodaci dajMeteoPodatkeZaAerodrom(@WebParam(name = "icao") String icao,
            @WebParam(name = "korisnickoIme") String korisnickoIme, @WebParam(name = "lozinka") String lozinka) {

        BrojacVremena brojacVremena = new BrojacVremena();

        HttpServletRequest request = (HttpServletRequest) context.getMessageContext().get(MessageContext.SERVLET_REQUEST);

        MeteoPodaci meteoPodaci = new MeteoPodaci();

        if (Objects.nonNull(icao) && BPUtils.provjeriKorisnika(korisnickoIme, lozinka)) {
            try (Connection con = KonektorBazePodataka.dajKonekciju();
                    PreparedStatement dajAerodrom = con.prepareStatement("SELECT * FROM MYAIRPORTS WHERE ident=?;");) {

                dajAerodrom.setString(1, icao);

                ResultSet rezultat = dajAerodrom.executeQuery();

                if (rezultat.next()) {
                    String[] koordinate = rezultat.getString("COORDINATES").split(", ");
                    meteoPodaci = dohvatiMeteoPodatkeZaAerodrom(koordinate);
                }

                rezultat.close();
            } catch (SQLException e) {
                System.out.println("SQLException: " + e);
            }
        }

        pisacDnevnika.upisUDnevnik(korisnickoIme, "SOAP servis za dohvacanje meteo podataka o aerodromu", "SOAP",
                request.getRemoteHost(), request.getRemoteAddr(),
                brojacVremena.dohvatiVrijemeProsloOdInicijalizacije());

        return meteoPodaci;
    }

    /**
     * SOAP metoda koja dodaje korisnika
     *
     * @param korisnik
     * @param korisnickoIme
     * @param lozinka
     * @return
     */
    @WebMethod(operationName = "dodajKorisnika")
    public boolean dodajKorisnika(@WebParam(name = "korisnik") Korisnik korisnik,
            @WebParam(name = "korisnickoIme") String korisnickoIme, @WebParam(name = "lozinka") String lozinka) {

        BrojacVremena brojacVremena = new BrojacVremena();

        HttpServletRequest request = (HttpServletRequest) context.getMessageContext().get(MessageContext.SERVLET_REQUEST);

        if (Objects.nonNull(korisnik) && BPUtils.provjeriKorisnika(korisnickoIme, lozinka)) {
            try (Connection con = KonektorBazePodataka.dajKonekciju();
                    PreparedStatement dodajKorisnika = con.prepareStatement("INSERT INTO KORISNICI(`ime`, `prezime`, `korisnickoIme`,`lozinka`,`email`) VALUES (?, ?, ?, ?, ?, ?);");) {

                dodajKorisnika.setString(1, korisnik.getIme());
                dodajKorisnika.setString(2, korisnik.getPrezime());
                dodajKorisnika.setString(3, korisnik.getKorisnickoIme());
                dodajKorisnika.setString(4, korisnik.getLozinka());
                dodajKorisnika.setString(5, korisnik.getEmail());

                return dodajKorisnika.executeUpdate() > 0;
            } catch (SQLException e) {
                System.out.println("SQLException: " + e);
            }
        }

        pisacDnevnika.upisUDnevnik(korisnickoIme, "SOAP servis za dodavanje korisnika", "SOAP",
                request.getRemoteHost(), request.getRemoteAddr(),
                brojacVremena.dohvatiVrijemeProsloOdInicijalizacije());

        return false;
    }

    /**
     * SOAP metoda koja azurira korisnika
     *
     * @param korisnik
     * @param korisnickoIme
     * @param lozinka
     * @return
     */
    @WebMethod(operationName = "azurirajKorisnika")
    public boolean azurirajKorisnika(@WebParam(name = "korisnik") Korisnik korisnik,
            @WebParam(name = "korisnickoIme") String korisnickoIme, @WebParam(name = "lozinka") String lozinka) {

        BrojacVremena brojacVremena = new BrojacVremena();

        HttpServletRequest request = (HttpServletRequest) context.getMessageContext().get(MessageContext.SERVLET_REQUEST);

        if (Objects.nonNull(korisnik) && BPUtils.provjeriKorisnika(korisnickoIme, lozinka)) {
            try (Connection con = KonektorBazePodataka.dajKonekciju();
                    PreparedStatement azurirajKorisnika = con.prepareStatement("UPDATE KORISNICI SET `ime` = ?, `prezime` = ?, `korisnickoIme` = ?,`lozinka` = ?,`email`= ? WHERE id = ?;");) {

                azurirajKorisnika.setString(1, korisnik.getIme());
                azurirajKorisnika.setString(2, korisnik.getPrezime());
                azurirajKorisnika.setString(3, korisnik.getKorisnickoIme());
                azurirajKorisnika.setString(4, korisnik.getLozinka());
                azurirajKorisnika.setString(5, korisnik.getEmail());
                azurirajKorisnika.setInt(6, korisnik.getId());

                return azurirajKorisnika.executeUpdate() > 0;
            } catch (SQLException e) {
                System.out.println("SQLException: " + e);
            }
        }

        pisacDnevnika.upisUDnevnik(korisnickoIme, "SOAP servis za azuriranje korisnika", "SOAP",
                request.getRemoteHost(), request.getRemoteAddr(),
                brojacVremena.dohvatiVrijemeProsloOdInicijalizacije());

        return false;
    }

    /**
     * SOAP metoda koja vraća sve korisnike
     *
     * @param korisnickoIme
     * @param lozinka
     * @return
     */
    @WebMethod(operationName = "dohvatiKorisnike")
    public List<Korisnik> dohvatiKorisnike(@WebParam(name = "korisnickoIme") String korisnickoIme, @WebParam(name = "lozinka") String lozinka) {

        BrojacVremena brojacVremena = new BrojacVremena();

        HttpServletRequest request = (HttpServletRequest) context.getMessageContext().get(MessageContext.SERVLET_REQUEST);

        List<Korisnik> korisnici = new ArrayList<>();
        if (BPUtils.provjeriKorisnika(korisnickoIme, lozinka)) {
            try (Connection con = KonektorBazePodataka.dajKonekciju();
                    PreparedStatement dajSveKorisnike = con.prepareStatement("SELECT * FROM korisnici");
                    ResultSet rezultat = dajSveKorisnike.executeQuery();) {

                while (rezultat.next()) {

                    Korisnik korisnik = new Korisnik(
                            rezultat.getInt("id"),
                            rezultat.getString("ime"),
                            rezultat.getString("prezime"),
                            rezultat.getString("korisnickoIme"),
                            rezultat.getString("lozinka"),
                            rezultat.getString("email")
                    );

                    korisnici.add(korisnik);
                }
            } catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        pisacDnevnika.upisUDnevnik(korisnickoIme, "SOAP servis za dohvacanje svih korisnika", "SOAP", 
                request.getRemoteHost(), request.getRemoteAddr(), 
                brojacVremena.dohvatiVrijemeProsloOdInicijalizacije());

        return korisnici;
    }

    /**
     * @return
     */
    private MeteoPodaci dohvatiMeteoPodatkeZaAerodrom(String[] koordinate) {
        return getOWMKlijent().getRealTimeWeather(koordinate[0], koordinate[1]);
    }

    /**
     * @return
     */
    private GeneralnaKonfiguracija getKonfiguracija() {
        return (GeneralnaKonfiguracija) SlusacAplikacije.getServletContext().getAttribute(SlusacAplikacije.KONFIGURACIJA_IME_ATRIBUTA);
    }

    /**
     * @return
     */
    private OWMKlijent getOWMKlijent() {
        return new OWMKlijent(this.getKonfiguracija().getOpenWeatherApiKey());
    }

}
