package org.foi.nwtis.marhranj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.marhranj.konfiguracije.GeneralnaKonfiguracija;
import org.foi.nwtis.marhranj.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.marhranj.web.zrna.PregledKorisnika;
import org.foi.nwtis.marhranj.ws.serveri.AIRP2WS;

public class DnevnikZapis {

    private static String ipAdresa;
    private static long trajanjeObrade;
    private static long vrijemePrijema;

    private String server;
    private String username;
    private String lozinka;
    private String driver;
    private String upit;

    public DnevnikZapis() {
    }
    
    public void upisUDnevnik(String korisnik, String radnja, String vrsta) {
        dohvatiPodatkeIzKonfiguracije();
        try {
            URL vlastitiUrl = new URL("http://checkip.amazonaws.com/");
            BufferedReader br = new BufferedReader(new InputStreamReader(vlastitiUrl.openStream()));
            ipAdresa = br.readLine();
            trajanjeObrade = (long) System.currentTimeMillis() - vrijemePrijema;
        } catch (MalformedURLException ex) {
            Logger.getLogger(DnevnikZapis.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DnevnikZapis.class.getName()).log(Level.SEVERE, null, ex);
        }
        Timestamp to = new Timestamp(trajanjeObrade);
        trajanjeObrade = to.getTime();

        upit = "INSERT INTO dnevnik (korisnik, url, ipAdresa, trajanjeObrade, vrijemePrijema, radnja, vrsta)"
                + " VALUES ('" + korisnik + "','" + server + "','" + ipAdresa + "','" + trajanjeObrade + "','" + new Timestamp(vrijemePrijema) + "','" + radnja + "','" + vrsta + "')";
         System.err.println("upit "+upit);
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PregledKorisnika.class.getName()).log(Level.SEVERE, null, ex);
        }
        try (Connection con = DriverManager.getConnection(server, username, lozinka);
                Statement stmt = con.createStatement();) {
            stmt.executeUpdate(upit);
            stmt.close();
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(AIRP2WS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void postaviPocetnoVrijeme() {
        vrijemePrijema = (long) System.currentTimeMillis();
    }

    private void dohvatiPodatkeIzKonfiguracije() {
        GeneralnaKonfiguracija konfiguracija = (GeneralnaKonfiguracija) SlusacAplikacije.getServletContext().getAttribute("konfiguracija");
        if (konfiguracija == null) {
            System.out.println("Dogodila se greška prilikom preuzimanja konfiguracije");
            return;
        }
        server = konfiguracija.getServerDatabase() + konfiguracija.getUserDatabase();
        username = konfiguracija.getUserUsername();
        lozinka = konfiguracija.getUserPassword();
        driver = konfiguracija.getDriverDatabase();
    }
    
}
