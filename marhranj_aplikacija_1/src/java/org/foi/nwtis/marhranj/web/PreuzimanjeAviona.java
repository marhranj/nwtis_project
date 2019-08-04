/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.foi.nwtis.marhranj.web;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.foi.nwtis.marhranj.konfiguracije.GeneralnaKonfiguracija;
import org.foi.nwtis.marhranj.web.zrna.Aerodrom;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.Lokacija;

/**
 *
 * @author marhranj
 * 
 * Klasa koja se vrti u ciklusima i preuzima avione te ih sprema u bazu podataka
 * 
 */
public class PreuzimanjeAviona extends Thread {
     
    private static final String KOORDINATE = "COORDINATES";
    private static final String ICAO = "IDENT";
    private static final String NAZIV = "NAME";
    private static final String ISO_KRATICA_DRZAVE = "ISO_COUNTRY";
    
    private GeneralnaKonfiguracija konfiguracija;
    private int pocetakIntervala;
    private int krajIntervala;
    private boolean kraj = false;

    /**
     * Konstruktor
     * 
     * @param konfiguracija 
     */
    public PreuzimanjeAviona(GeneralnaKonfiguracija konfiguracija) {
        this.konfiguracija = konfiguracija;
    }
    
    @Override
    public void interrupt() {
        kraj = true;
        super.interrupt();
    }

    @Override
    public void run() {
        while(!kraj) {
            try {
                List<Aerodrom> aerodromi = new ArrayList<>();
                List<AvionLeti> letoviAviona = new ArrayList<>();

                try (Connection con = KonektorBazePodataka.dajKonekciju(); 
                        PreparedStatement dajSveAerodrome = con.prepareStatement("Select * from MYAIRPORTS");
                        ResultSet rezultat = dajSveAerodrome.executeQuery();) {
                    OSKlijent osKlijent = new OSKlijent(konfiguracija.getOpenSkyNetworkKorisnik(), konfiguracija.getOpenSkyNetworkLozinku());

                    while (rezultat.next()) {
                        dodajAerodromZaPrikupljanjePodataka(rezultat, aerodromi);
                    }

                    for (Aerodrom aerodrom : aerodromi) {
                        letoviAviona = osKlijent.getDepartures(aerodrom.getIcao(), pocetakIntervala, krajIntervala);               

                        int velicinaListe = letoviAviona.size();
                        for (int i = 0; i < velicinaListe && i < 100; i++) {
                           AvionLeti letAviona = letoviAviona.get(i);
                           if (!zapisPostojiUBaziPodataka(letAviona, con) && Objects.nonNull(letAviona.getEstArrivalAirport())) {
                                zapisiAvoinUBazuPodataka(letAviona, con);
                           }
                        }
                        
                    }
                    
                    pocetakIntervala = krajIntervala;
                    krajIntervala = pocetakIntervala + Integer.parseInt(konfiguracija.getPreuzimanjeTrajanje());
                } catch (SQLException ex) {
                    System.out.println("SQLException: " + ex);
                }
                
                Thread.sleep(Integer.parseInt(konfiguracija.getPreuzimanjeCiklus()) * 1000 * 60);
            } catch (InterruptedException ex) {
                System.out.println("InterruptedException: " + ex);
            }
        }
    }

    @Override
    public synchronized void start() {
        super.start();
        postaviIntervaleDohvacanjaPodataka();
    }
    
    /**
     * Metoda koja dodaje aerodrom u listu aerodroma
     * 
     * @param rezultat
     * @param aerodromi
     * @throws SQLException
     */
    private void dodajAerodromZaPrikupljanjePodataka(ResultSet rezultat, List<Aerodrom> aerodromi) throws SQLException {
        String[] koordinate = rezultat.getString(KOORDINATE).split(", ");
        Aerodrom aerodrom = new Aerodrom(
                rezultat.getString(ICAO), 
                rezultat.getString(NAZIV),
                rezultat.getString(ISO_KRATICA_DRZAVE),
                new Lokacija(koordinate[0], koordinate[1]));
        aerodromi.add(aerodrom);
    }
    
    /**
     * Metoda u kojoj se postavlja interval za dohvacanje podataka
     *
     */
    private void postaviIntervaleDohvacanjaPodataka() {
        pocetakIntervala = (int) (new Date().getTime() / 1000) - (Integer.parseInt(konfiguracija.getPreuzimanjePocetak()) * 60 * 60);
        krajIntervala = pocetakIntervala + Integer.parseInt(konfiguracija.getPreuzimanjeTrajanje());
    }
    
    /**
     * Metoda koja provjerava postoji li avion u bazi podataka
     * @param letAviona
     * @param con
     * @throws SQLException
     * @return 
     */
    private boolean zapisPostojiUBaziPodataka(AvionLeti letAviona, Connection con) throws SQLException {
        PreparedStatement provjeriUnosUBazu = con.prepareStatement("Select count(*) from AIRPLANES where LASTSEEN = ? and ICAO24 = ?");
        provjeriUnosUBazu.setInt(1, letAviona.getLastSeen());
        provjeriUnosUBazu.setString(2, letAviona.getIcao24());
        ResultSet rezultat = provjeriUnosUBazu.executeQuery();
        rezultat.next();
        return rezultat.getInt(1) > 0;
    }
    
    /**
     * Metoda koja zapisuje avion u bazu podataka
     * @param letAviona
     * @param con
     * @throws SQLException
     */
    private void zapisiAvoinUBazuPodataka(AvionLeti letAviona, Connection con) throws SQLException {
        PreparedStatement insertStatement = con.prepareStatement(
                "INSERT INTO AIRPLANES (ICAO24, FIRSTSEEN, ESTDEPARTUREAIRPORT, LASTSEEN, "
                        + "ESTARRIVALAIRPORT, CALLSIGN, ESTDEPARTUREAIRPORTHORIZDISTANCE, "
                        + "ESTDEPARTUREAIRPORTVERTDISTANCE, ESTARRIVALAIRPORTHORIZDISTANCE, "
                        + "ESTARRIVALAIRPORTVERTDISTANCE, DEPARTUREAIRPORTCANDIDATESCOUNT, "
                        + "ARRIVALAIRPORTCANDIDATESCOUNT, STORED)"
                        + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        );
        
        insertStatement.setString(1, letAviona.getIcao24());
        insertStatement.setInt(2, letAviona.getFirstSeen());
        insertStatement.setString(3, letAviona.getEstDepartureAirport());
        insertStatement.setInt(4, letAviona.getLastSeen());
        insertStatement.setString(5, letAviona.getEstArrivalAirport());
        insertStatement.setString(6, letAviona.getCallsign());
        insertStatement.setInt(7, letAviona.getEstDepartureAirportHorizDistance());
        insertStatement.setInt(8, letAviona.getEstDepartureAirportVertDistance());
        insertStatement.setInt(9, letAviona.getEstArrivalAirportHorizDistance());
        insertStatement.setInt(10, letAviona.getEstArrivalAirportVertDistance());
        insertStatement.setInt(11, letAviona.getDepartureAirportCandidatesCount());
        insertStatement.setInt(12, letAviona.getArrivalAirportCandidatesCount());
        insertStatement.setTimestamp(13, new Timestamp(System.currentTimeMillis()));
        
        insertStatement.execute();
        insertStatement.close();
    }
    
}