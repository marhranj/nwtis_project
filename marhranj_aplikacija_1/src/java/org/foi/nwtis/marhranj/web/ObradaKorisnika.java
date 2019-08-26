package org.foi.nwtis.marhranj.web;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.marhranj.konfiguracije.GeneralnaKonfiguracija;
import org.foi.nwtis.marhranj.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.marhranj.web.zrna.Korisnik;

@Named(value = "obradaKorisnika")
@SessionScoped
public class ObradaKorisnika implements Serializable {
    
    private static final String ID = "id";
    private static final String IME = "ime";
    private static final String PREZIME = "prezime";
    private static final String KORISNICKO_IME = "korisnickoIme";
    private static final String LOZINKA = "lozinka";
    private static final String EMAIL = "email";
    
    private final List<Korisnik> korisnici = new ArrayList<>();
    private GeneralnaKonfiguracija konfiguracija;
    
    public ObradaKorisnika() {
        azurirajuKonfiguraciju();
    }
    
    public void init() {
        dohvatiKorisnikeIzBaze();
    }

    public int getTablicaBrojRedaka() {
        return konfiguracija.getTablicakBrojRedaka();
    }

    public List<Korisnik> getKorisnici() {
        return korisnici;
    }
    
    private List<Korisnik> dohvatiKorisnikeIzBaze(){
        
        try (Connection con = KonektorBazePodataka.dajKonekciju(); 
                PreparedStatement dajSveKorisnike = con.prepareStatement("SELECT * FROM korisnici");
                ResultSet rezultat = dajSveKorisnike.executeQuery();) {
            
            while(rezultat.next()) {
                
                Korisnik korisnik = new Korisnik(
                   rezultat.getInt(ID), 
                   rezultat.getString(IME), 
                   rezultat.getString(PREZIME),
                   rezultat.getString(KORISNICKO_IME),
                   rezultat.getString(LOZINKA),
                   rezultat.getString(EMAIL)
                );

                korisnici.add(korisnik);
           }
        } catch(SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return korisnici;
    }
    
    private void azurirajuKonfiguraciju() {
        konfiguracija = (GeneralnaKonfiguracija) SlusacAplikacije.getServletContext().getAttribute(SlusacAplikacije.KONFIGURACIJA_IME_ATRIBUTA);
    }
   
}
