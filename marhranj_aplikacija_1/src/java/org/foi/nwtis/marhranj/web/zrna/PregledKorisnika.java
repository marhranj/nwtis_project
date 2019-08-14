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
import org.foi.nwtis.marhranj.konfiguracije.GeneralnaKonfiguracija;
import org.foi.nwtis.marhranj.web.slusaci.SlusacAplikacije;

@Named(value = "pregledKorisnika")
@SessionScoped
public class PregledKorisnika implements Serializable {
    
    private String server;
    private String korisnik;
    private String lozinka;
    private String driver;
    private String upit;
    private int tablicaBrojRedaka;
    private List<Korisnik> listaKorisnika;

    public int getTablicaBrojRedaka() {
        return tablicaBrojRedaka;
    }

    public void setTablicaBrojRedaka(int tablicaBrojRedaka) {
        this.tablicaBrojRedaka = tablicaBrojRedaka;
    }

    public List<Korisnik> getListaKorisnika() {
        return listaKorisnika;
    }

    public void setListaKorisnika(List<Korisnik> listaKorisnika) {
        this.listaKorisnika = listaKorisnika;
    }
    
    

    public PregledKorisnika() {
        dohvatiKorisnike();
        dohvatiPodatkeIzKonfiguracije();
    }
    
    private List<Korisnik> dohvatiKorisnike(){
        dohvatiPodatkeIzKonfiguracije();
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PregledKorisnika.class.getName()).log(Level.SEVERE, null, ex);
        }
        listaKorisnika = new ArrayList<>();
        Korisnik kor;
        upit = "SELECT * FROM korisnici";
        
        try (Connection con = DriverManager.getConnection(server, korisnik, lozinka);
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(upit);) {
            while (rs.next()) {
                kor = new Korisnik(rs.getInt("id"), rs.getString("ime"),
                        rs.getString("prezime"), rs.getString("korisnickoIme"), rs.getString("lozinka"), rs.getString("email"));
                listaKorisnika.add(kor);
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(PregledKorisnika.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaKorisnika;
    }
    

    private void dohvatiPodatkeIzKonfiguracije() {
        GeneralnaKonfiguracija konfiguracija = (GeneralnaKonfiguracija) SlusacAplikacije.getServletContext().getAttribute("konfiguracija");
        if (konfiguracija == null) {
            System.out.println("Dogodila se greška prilikom preuzimanja konfiguracije");
            return;
        }
        server = konfiguracija.getServerDatabase() + konfiguracija.getUserDatabase();
        korisnik = konfiguracija.getUserUsername();
        lozinka = konfiguracija.getUserPassword();
        driver = konfiguracija.getDriverDatabase();
        tablicaBrojRedaka = Integer.parseInt(konfiguracija.getTablicakBrojRedaka());
    }
    
}
