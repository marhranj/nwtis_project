/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.lucbagic.konfiguracije.Konfiguracija;
import org.foi.nwtis.lucbagic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.marhranj.web.slusaci.SlusacAplikacije;

/**
 *
 * @author Lucija
 */
@Named(value = "pregledDnevnika")
@SessionScoped
public class PregledDnevnika implements Serializable {

    private String server;
    private String korisnik;
    private String lozinka;
    private String driver;
    private String upit;
    private int tablicaBrojRedaka;
    private List<Dnevnik> listaDnevnik;
    private String vrstaZapisa;
    private String odVrijeme;
    private String doVrijeme;

    public String getVrstaZapisa() {
        return vrstaZapisa;
    }

    public void setVrstaZapisa(String vrstaZapisa) {
        this.vrstaZapisa = vrstaZapisa;
    }

    public String getOdVrijeme() {
        return odVrijeme;
    }

    public void setOdVrijeme(String odVrijeme) {
        this.odVrijeme = odVrijeme;
    }

    public String getDoVrijeme() {
        return doVrijeme;
    }

    public void setDoVrijeme(String doVrijeme) {
        this.doVrijeme = doVrijeme;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(String korisnik) {
        this.korisnik = korisnik;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUpit() {
        return upit;
    }

    public void setUpit(String upit) {
        this.upit = upit;
    }

    public int getTablicaBrojRedaka() {
        return tablicaBrojRedaka;
    }

    public void setTablicaBrojRedaka(int tablicaBrojRedaka) {
        this.tablicaBrojRedaka = tablicaBrojRedaka;
    }

    public List<Dnevnik> getListaDnevnik() {
        return listaDnevnik;
    }

    public void setListaDnevnik(List<Dnevnik> listaDnevnik) {
        this.listaDnevnik = listaDnevnik;
    }

    public PregledDnevnika() {
        dohvatiDnevnik();
        dohvatiPodatkeIzKonfiguracije();
    }

    private List<Dnevnik> dohvatiDnevnik() {
        dohvatiPodatkeIzKonfiguracije();
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PregledKorisnika.class.getName()).log(Level.SEVERE, null, ex);
        }
        listaDnevnik = new ArrayList<>();
        Dnevnik dnevnik;
        upit = "SELECT * FROM dnevnik";

        try (Connection con = DriverManager.getConnection(server, korisnik, lozinka);
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(upit);) {
            while (rs.next()) {
                dnevnik = new Dnevnik(rs.getInt("id"), rs.getString("korisnik"),
                        rs.getString("url"), rs.getString("ipAdresa"), rs.getLong("trajanjeObrade"),
                        rs.getTimestamp("vrijemePrijema"), rs.getString("radnja"), rs.getString("vrsta"));
                listaDnevnik.add(dnevnik);
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(PregledKorisnika.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaDnevnik;
    }

    public List<Dnevnik> filtrirajVrstaZapisa() {
        dohvatiPodatkeIzKonfiguracije();
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PregledKorisnika.class.getName()).log(Level.SEVERE, null, ex);
        }
        listaDnevnik = new ArrayList<>();
        Dnevnik dnevnik;

        if (!odVrijeme.equals("") && !doVrijeme.equals("") && !vrstaZapisa.equals("")) {
            upit = "SELECT * FROM dnevnik WHERE vrijemePrijema BETWEEN '" + odVrijeme + "' AND '" + doVrijeme + "' AND vrsta='" + vrstaZapisa + "'";
        } else if (!odVrijeme.equals("") && !doVrijeme.equals("")) {
            upit = "SELECT * FROM dnevnik WHERE vrijemePrijema BETWEEN '" + odVrijeme + "' AND '" + doVrijeme + "'";
        } else if (!vrstaZapisa.equals("")) {
            upit = "SELECT * FROM dnevnik WHERE vrsta='" + vrstaZapisa + "'";
        }
        try (Connection con = DriverManager.getConnection(server, korisnik, lozinka);
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(upit);) {
            while (rs.next()) {
                dnevnik = new Dnevnik(rs.getInt("id"), rs.getString("korisnik"),
                        rs.getString("url"), rs.getString("ipAdresa"), rs.getLong("trajanjeObrade"),
                        rs.getTimestamp("vrijemePrijema"), rs.getString("radnja"), rs.getString("vrsta"));
                listaDnevnik.add(dnevnik);
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(PregledKorisnika.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaDnevnik;
    }

    private void dohvatiPodatkeIzKonfiguracije() {
        BP_Konfiguracija bp_konfiguracija = (BP_Konfiguracija) SlusacAplikacije.getSc().getAttribute("BP_Konfig");
        Konfiguracija konf = (Konfiguracija) SlusacAplikacije.getSc().getAttribute("konfiguracija");
        if (bp_konfiguracija == null || konf == null) {
            System.out.println("Dogodila se greška prilikom preuzimanja konfiguracije");
            return;
        }
        server = bp_konfiguracija.getServerDatabase() + bp_konfiguracija.getUserDatabase();
        korisnik = bp_konfiguracija.getUserUsername();
        lozinka = bp_konfiguracija.getUserPassword();
        driver = konf.dajPostavku("driver.database.mysql");
        tablicaBrojRedaka = Integer.parseInt(konf.dajPostavku("tablica.brojRedaka"));
    }
    
    private boolean autentikacijaKorisnika(String korisnickoIme, String loz) {
        dohvatiPodatkeIzKonfiguracije();
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            System.err.println("Ne može se spojit s ovim driverom: " + ex.getMessage());
        }
        try (Connection con = DriverManager.getConnection(server, korisnik, lozinka);
                Statement stmt = con.createStatement();) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS broj FROM korisnici WHERE korisnickoIme='" + korisnickoIme + "' AND lozinka='" + loz + "'");
            rs.next();
            int broj = rs.getInt("broj");
            if (broj == 1) {
                return true;
            }
            stmt.close();
            con.close();
        } catch (SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
        }
        return false;
    }

}
