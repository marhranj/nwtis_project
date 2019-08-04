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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.foi.nwtis.marhranj.konfiguracije.GeneralnaKonfiguracija;
import org.foi.nwtis.marhranj.web.KonektorBazePodataka;
import org.foi.nwtis.marhranj.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.marhranj.web.zrna.Aerodrom;
import org.foi.nwtis.rest.klijenti.LIQKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.Lokacija;

/**
 *
 * @author marhranj
 * 
 * Klasa koja prima te obrađuje SOAP zahtjeve
 * 
 */
@WebService(serviceName = "AIRP2WS")
public class AIRP2WS {
 
    private static final String KOORDINATE = "COORDINATES";
    private static final String ICAO = "IDENT";
    private static final String NAZIV = "NAME";
    private static final String ISO_KRATICA_DRZAVE = "ISO_COUNTRY";
        
    /**
     * SOAP metoda koja vraća sve aerodrome
     * @return 
     */
    @WebMethod(operationName = "dajSveAerodrome")
    public List<Aerodrom> dajSveAerodrome() {
        List<Aerodrom> aerodromi = new ArrayList<>();
        
        try (Connection con = KonektorBazePodataka.dajKonekciju(); 
                PreparedStatement dajSveAerodrome = con.prepareStatement("Select * from MYAIRPORTS");
                ResultSet rezultat = dajSveAerodrome.executeQuery();) {
            
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
        } catch(SQLException ex) {
            System.out.println("SQLException: " + ex);
        }
        
        return aerodromi;
    }
    
    /**
     * SOAP metoda koja vraća aerodrom prema icao
     * @param icao
     * @return 
     */
    @WebMethod(operationName = "dajAerodrom")
    public Aerodrom dajAerodrom(@WebParam(name = "icao") String icao) {
        Aerodrom aerodrom = new Aerodrom();
        
        if (Objects.nonNull(icao)) {
            try (Connection con = KonektorBazePodataka.dajKonekciju(); 
                PreparedStatement dajAerodrom = con.prepareStatement("SELECT name, iso_country FROM MYAIRPORTS WHERE ident= ?");) {
            
                dajAerodrom.setString(1, icao);
                ResultSet rezultat = dajAerodrom.executeQuery();
                
                if (rezultat.next()) {
                    aerodrom.setIcao(icao);
                    aerodrom.setNaziv(rezultat.getString(NAZIV));
                    aerodrom.setDrzava(rezultat.getString(ISO_KRATICA_DRZAVE));
                    aerodrom.setLokacija(this.getLIQKlijent().getGeoLocation(rezultat.getString(NAZIV)));
                }
                
                rezultat.close();
            } catch(SQLException ex) {
                System.out.println("SQLException: " + ex);
            }
        }
        
        return aerodrom;
    }

    /**
     * SOAP metoda koja dodaje aerodrom s navedenim icao
     * @param icao
     * @return 
     */
    @WebMethod(operationName = "dodajAerodrom")
    public boolean dodajAerodrom(@WebParam(name = "icao") String icao) {
        boolean aerodromDodanUBazuPodataka = false;
        
        if (Objects.nonNull(icao)) {
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
                    aerodromDodanUBazuPodataka = true;
                }
                
                rezultat.close();
            } catch(SQLException ex) {
                System.out.println("SQLException: " + ex);
            }
        }
        
        return aerodromDodanUBazuPodataka;
    }
    
    /**
     * SOAP metoda koja vraća avione poletjele sa aerodroma 
     * prema icao kodu te imeđu odVremena i doVremena
     * @param icao
     * @param odVremena
     * @param doVremena
     * @return 
     */
    @WebMethod(operationName = "dajAvionePoletjeleSAerodroma")
    public List<AvionLeti> dajAvionePoletjeleSAerodroma(@WebParam(name = "icao") String icao, @WebParam(name = "odVremena") int odVremena, @WebParam(name = "doVremena") int doVremena) {
        List<AvionLeti> poletjeliAvioni = new ArrayList<>();
        
        if (Objects.nonNull(icao) && Objects.nonNull(odVremena) && Objects.nonNull(doVremena)) {
            try (Connection con = KonektorBazePodataka.dajKonekciju(); 
                PreparedStatement dajAerodrom = con.prepareStatement("SELECT * FROM AIRPLANES WHERE ESTDEPARTUREAIRPORT = ? AND LASTSEEN BETWEEN ? AND ?");) {

                dajAerodrom.setString(1, icao);
                dajAerodrom.setInt(2, odVremena);
                dajAerodrom.setInt(3, doVremena);

                ResultSet rezultat = dajAerodrom.executeQuery();

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
            } catch(SQLException ex) {
                System.out.println("SQLException: " + ex);
            }
        }
        
        return poletjeliAvioni;
    }

    /**
     * SOAP metoda koja provjerava je li avion - icao24 
     * poletio s aerodroma - icao izmedu ovVremena i doVremena
     * @param icao24
     * @param icao
     * @param odVremena
     * @param doVremena
     * @return 
     */
    @WebMethod(operationName = "provjeriAvionPoletioSAerodroma")
    public boolean provjeriAvionPoletioSAerodroma(@WebParam(name = "icao24") String icao24, @WebParam(name = "icao") String icao, @WebParam(name = "odVremena") int odVremena, @WebParam(name = "doVremena") int doVremena) {
        boolean avionPoletio = false;
        
        if (Objects.nonNull(icao24) && Objects.nonNull(icao) && Objects.nonNull(odVremena) && Objects.nonNull(doVremena)) {
            try (Connection con = KonektorBazePodataka.dajKonekciju(); 
                PreparedStatement dajAerodrom = con.prepareStatement("SELECT COUNT(*) FROM AIRPLANES WHERE ESTDEPARTUREAIRPORT = ? AND ICAO24= ? AND LASTSEEN BETWEEN ? AND ?");) {

                dajAerodrom.setString(1, icao);
                dajAerodrom.setString(2, icao24);
                dajAerodrom.setInt(3, odVremena);
                dajAerodrom.setInt(4, doVremena);

                ResultSet rezultat = dajAerodrom.executeQuery();
                rezultat.next();
                
                avionPoletio = rezultat.getInt(1) > 0;
                
                rezultat.close();
            } catch(SQLException ex) {
                System.out.println("SQLException: " + ex);
            }
        }
        
        return avionPoletio;
    }

    /**
     * SOAP metoda koja vraća broj redaka za izbornik na temelju konfiguracije
     * @return 
     */
    @WebMethod(operationName = "izbornikBrojRedaka")
    public int izbornikBrojRedaka() {
        return Integer.parseInt(this.getKonfiguracija().getIzbornikBrojRedaka());
    }

    /**
     * SOAP metoda koja vraća broj redaka za tablicu na temelju konfiguracije
     * @return 
     */
    @WebMethod(operationName = "tablicaBrojRedaka")
    public int tablicaBrojRedaka() {
        return Integer.parseInt(this.getKonfiguracija().getTablicakBrojRedaka());
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
    private LIQKlijent getLIQKlijent() {
        return new LIQKlijent(this.getKonfiguracija().getLocationIqToken());
    }
    
}
