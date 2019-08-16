/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj.web.dretve;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.marhranj.konfiguracije.GeneralnaKonfiguracija;
import org.foi.nwtis.marhranj.web.KonektorBazePodataka;
import org.foi.nwtis.marhranj.web.slusaci.SlusacAplikacije;
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

    private static final String POCETAK_INTERVALA = "pocetakIntervala";
    private static final String REDNI_BROJ_CIKLUSA = "redniBrojCiklusa";

    private final GeneralnaKonfiguracija konfiguracija;

    private int pocetakIntervala;
    private int krajIntervala;
    private int brojacCiklusa;

    private File datoteka;

    /**
     * Konstruktor
     *
     * @param konfiguracija
     */
    public PreuzimanjeAviona(GeneralnaKonfiguracija konfiguracija) {
        this.konfiguracija = konfiguracija;
    }

    @Override
    public void run() {
        while (!SlusacAplikacije.getZaustavljeno()) {
            if (!SlusacAplikacije.getPasivno()) {
                obaviPreuzimanjeAviona();
            }
        }
    }

    @Override
    public synchronized void start() {
        postaviIntervaleDohvacanjaPodatakaIzKonfiguracije();
        kreirajDatotekuDretvePreuzimanjaAviona();
        super.start();
    }

    private void obaviPreuzimanjeAviona() {
        try {
            postaviPodatkeIzDatoteke();
            
            zapisiAvioneUBazuPodataka();
            
            Thread.sleep(konfiguracija.getPreuzimanjeCiklus() * 1000 * 60);
            brojacCiklusa++;
            azurirajDatotekuZaEvidencijuDretve();
        } catch (InterruptedException ex) {
            System.out.println("InterruptedException: " + ex);
        }
    }

    private void zapisiAvioneUBazuPodataka() {
        try (Connection con = KonektorBazePodataka.dajKonekciju();
                PreparedStatement dajSveAerodrome = con.prepareStatement("Select * from MYAIRPORTS");
                ResultSet rezultat = dajSveAerodrome.executeQuery();) {
            OSKlijent osKlijent = new OSKlijent(konfiguracija.getOpenSkyNetworkKorisnik(), konfiguracija.getOpenSkyNetworkLozinku());

            List<Aerodrom> aerodromi = new ArrayList<>();

            while (rezultat.next()) {
                dodajAerodromZaPrikupljanjePodataka(rezultat, aerodromi);
            }

            for (Aerodrom aerodrom : aerodromi) {
                List<AvionLeti> letoviAviona = osKlijent.getDepartures(aerodrom.getIcao(), pocetakIntervala, krajIntervala);

                for (int i = 0; i < letoviAviona.size() && i < 100; i++) {
                    AvionLeti letAviona = letoviAviona.get(i);
                    if (!zapisPostojiUBaziPodataka(letAviona, con) && Objects.nonNull(letAviona.getEstArrivalAirport())) {
                        zapisiAvoinUBazuPodataka(letAviona, con);
                    }
                }

            }

            pocetakIntervala = krajIntervala;
            krajIntervala += konfiguracija.getPreuzimanjeTrajanje();
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex);
        }
    }

    private void postaviPodatkeIzDatoteke() {
        try {
            JsonObject jsonObject = new JsonParser().parse(new FileReader(datoteka)).getAsJsonObject();
            pocetakIntervala = jsonObject.get(POCETAK_INTERVALA).getAsInt();
            brojacCiklusa = jsonObject.get(REDNI_BROJ_CIKLUSA).getAsInt();

            int trenutnoVrijeme = (int) (new Date().getTime() / 1000);
            if (pocetakIntervala >= trenutnoVrijeme) {
                pocetakIntervala = trenutnoVrijeme - (konfiguracija.getPreuzimanjePocetak() * 60 * 60);
                azurirajDatotekuZaEvidencijuDretve();
            }
            krajIntervala = pocetakIntervala + konfiguracija.getPreuzimanjeTrajanje();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void kreirajDatotekuDretvePreuzimanjaAviona() {
        try {
            datoteka = new File(SlusacAplikacije.getServletContext().getRealPath("/WEB-INF") + "/" + konfiguracija.getDatotekaDretvePreuzimanjaAviona());
            if (!datoteka.exists()) {
                datoteka.createNewFile();
            }
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        azurirajDatotekuZaEvidencijuDretve();
    }

    private void azurirajDatotekuZaEvidencijuDretve() {
        try {
            zapisiUDatoteku(kreirajJsonStringZaEvidencijuDretve(pocetakIntervala, brojacCiklusa), datoteka);
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void zapisiUDatoteku(String zapis, File datoteka) throws IOException {
        try (FileWriter fileWriter = new FileWriter(datoteka)) {
            fileWriter.write(zapis);
        }
    }

    private String kreirajJsonStringZaEvidencijuDretve(int pocetakIntervala, int brojacCiklusa) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(POCETAK_INTERVALA, pocetakIntervala);
        jsonObject.addProperty(REDNI_BROJ_CIKLUSA, brojacCiklusa);
        return jsonObject.toString();
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
    private void postaviIntervaleDohvacanjaPodatakaIzKonfiguracije() {
        pocetakIntervala = (int) (new Date().getTime() / 1000) - (konfiguracija.getPreuzimanjePocetak() * 60 * 60);
        krajIntervala = pocetakIntervala + konfiguracija.getPreuzimanjeTrajanje();
    }

    /**
     * Metoda koja provjerava postoji li avion u bazi podataka
     *
     * @param letAviona
     * @param con
     * @throws SQLException
     * @return
     */
    private boolean zapisPostojiUBaziPodataka(AvionLeti letAviona, Connection con) throws SQLException {
        PreparedStatement provjeriUnosUBazu = con.prepareStatement("Select COUNT(*) from AIRPLANES where FIRSTSEEN = ? and ICAO24 = ?");
        provjeriUnosUBazu.setInt(1, letAviona.getFirstSeen());
        provjeriUnosUBazu.setString(2, letAviona.getIcao24());
        ResultSet rezultat = provjeriUnosUBazu.executeQuery();
        rezultat.next();
        return rezultat.getInt(1) > 0;
    }

    /**
     * Metoda koja zapisuje avion u bazu podataka
     *
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
                + "ARRIVALAIRPORTCANDIDATESCOUNT, `STORED`)"
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
