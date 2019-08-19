/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.marhranj.web.KonektorBazePodataka;
import org.foi.nwtis.marhranj.web.zrna.Aerodrom;
import org.foi.nwtis.marhranj.web.zrna.ZapisDnevnika;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.Lokacija;

/**
 *
 * @author marhranj
 */
public final class BPUtils {

    private BPUtils() {
    }

    public static boolean provjeriKorisnika(String korisnickoIme, String lozinka) {
        if (Objects.nonNull(korisnickoIme) && Objects.nonNull(lozinka)) {
            try (Connection con = KonektorBazePodataka.dajKonekciju();
                    PreparedStatement stmt = con.prepareStatement("SELECT COUNT(*) FROM korisnici WHERE korisnickoIme=? AND lozinka=?");) {
                stmt.setString(1, korisnickoIme);
                stmt.setString(2, lozinka);
                ResultSet rezultat = stmt.executeQuery();
                rezultat.next();
                return rezultat.getInt(1) > 0;
            } catch (SQLException e) {
                Logger.getLogger(BPUtils.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return false;
    }

    public static List<AvionLeti> dohvatiAvioneIzResultSeta(ResultSet rezultat) throws SQLException {
        List<AvionLeti> poletjeliAvioni = new ArrayList<>();

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

        return poletjeliAvioni;
    }

    public static List<Aerodrom> dohvatiAerodromeIzResultSeta(ResultSet rezultat) throws SQLException {
        List<Aerodrom> aerodromi = new ArrayList<>();
        while (rezultat.next()) {
            String[] koordinate = rezultat.getString("COORDINATES").split(", ");
            Lokacija lokacija = new Lokacija(koordinate[0], koordinate[1]);

            Aerodrom aerodrom = new Aerodrom(
                    rezultat.getString("IDENT"),
                    rezultat.getString("NAME"),
                    rezultat.getString("ISO_COUNTRY"),
                    lokacija);

            aerodromi.add(aerodrom);
        }
        return aerodromi;
    }
    
    public  static List<ZapisDnevnika> dohvatiZapiseDnevnikaIzResultSeta(ResultSet rezultat) throws SQLException {
        List<ZapisDnevnika> zapisiDnevnika = new ArrayList<>();

        while (rezultat.next()) {

            ZapisDnevnika dnevnik = new ZapisDnevnika(
                    rezultat.getInt("id"),
                    rezultat.getString("korisnik"),
                    rezultat.getTimestamp("vrijeme"),
                    rezultat.getString("naredba"),
                    rezultat.getString("vrsta")
            );

            zapisiDnevnika.add(dnevnik);
        }
        return zapisiDnevnika;
    }

}
