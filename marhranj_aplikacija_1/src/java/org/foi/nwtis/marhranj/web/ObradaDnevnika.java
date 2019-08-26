/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import org.foi.nwtis.marhranj.utils.BPUtils;
import org.foi.nwtis.marhranj.utils.PretvaracVremena;
import org.foi.nwtis.marhranj.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.marhranj.web.zrna.ZapisDnevnika;

/**
 *
 * @author marhranj
 */
@Named(value = "obradaDnevnika")
@SessionScoped
public class ObradaDnevnika implements Serializable {

    private String vrstaZapisa;
    private String pocetnoVrijeme;
    private String zavrsnoVrijeme;

    private String poruka = "";

    private List<ZapisDnevnika> sviZapisiDnevnika = new ArrayList<>();
    private List<ZapisDnevnika> zapisiDnevnika = new ArrayList<>();

    private GeneralnaKonfiguracija konfiguracija;

    public ObradaDnevnika() {
        azurirajuKonfiguraciju();
    }
    
    public void init() {
        sviZapisiDnevnika = dohvatiZapiseDnevnikaIzBaze();
        zapisiDnevnika = new ArrayList<>(sviZapisiDnevnika);
    }

    public int getTablicaBrojRedaka() {
        return konfiguracija.getTablicakBrojRedaka();
    }

    public List<ZapisDnevnika> getZapisiDnevnika() {
        return zapisiDnevnika;
    }

    public String getVrstaZapisa() {
        return vrstaZapisa;
    }

    public void setVrstaZapisa(String vrstaZapisa) {
        this.vrstaZapisa = vrstaZapisa;
    }

    public String getPocetnoVrijeme() {
        return pocetnoVrijeme;
    }

    public void setPocetnoVrijeme(String pocetnoVrijeme) {
        this.pocetnoVrijeme = pocetnoVrijeme;
    }

    public String getZavrsnoVrijeme() {
        return zavrsnoVrijeme;
    }

    public void setZavrsnoVrijeme(String zavrsnoVrijeme) {
        this.zavrsnoVrijeme = zavrsnoVrijeme;
    }

    public String getPoruka() {
        return poruka;
    }

    public void filtrirajZapise() {
        poruka = "";
        zapisiDnevnika = new ArrayList<>(sviZapisiDnevnika);
        try (Connection con = KonektorBazePodataka.dajKonekciju()) {
            if (!ispravnaVremenskaPolja()) {
                poruka = "Morate unijeti ili oba vremenska polja ili niti jedno, prikazat će se sva polja.";
            }

            PreparedStatement filtrirajDnevnike = kreirajPreparedStetement(con);

            if (filtrirajDnevnike != null) {
                ResultSet rezultat = filtrirajDnevnike.executeQuery();

                zapisiDnevnika = BPUtils.dohvatiZapiseDnevnikaIzResultSeta(rezultat);
                filtrirajDnevnike.close();
                rezultat.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }

    }


    private PreparedStatement kreirajPreparedStetement(Connection con) throws SQLException {
        PreparedStatement filtrirajDnevnike = null;
        if (svaPoljaPopunjena()) {
            filtrirajDnevnike = con.prepareStatement("SELECT * FROM dnevnik WHERE vrijeme BETWEEN ? AND ? AND vrsta=?;");
            filtrirajDnevnike.setTimestamp(1, PretvaracVremena.pretvoriStringUTimestamp(pocetnoVrijeme));
            filtrirajDnevnike.setTimestamp(2, PretvaracVremena.pretvoriStringUTimestamp(zavrsnoVrijeme));
            filtrirajDnevnike.setString(3, vrstaZapisa);
        } else if (!vrstaZapisa.isEmpty() && ispravnaVremenskaPolja()) {
            filtrirajDnevnike = con.prepareStatement("SELECT * FROM dnevnik WHERE vrsta=?;");
            filtrirajDnevnike.setString(1, vrstaZapisa);
        } else if (!pocetnoVrijeme.isEmpty() && !zavrsnoVrijeme.isEmpty()) {
            filtrirajDnevnike = con.prepareStatement("SELECT * FROM dnevnik WHERE vrijeme BETWEEN ? AND ?");
            filtrirajDnevnike.setTimestamp(1, PretvaracVremena.pretvoriStringUTimestamp(pocetnoVrijeme));
            filtrirajDnevnike.setTimestamp(2, PretvaracVremena.pretvoriStringUTimestamp(zavrsnoVrijeme));
        }
        return filtrirajDnevnike;
    }

    private boolean svaPoljaPopunjena() {
        return !vrstaZapisa.isEmpty() && !pocetnoVrijeme.isEmpty() && !zavrsnoVrijeme.isEmpty();
    }

    private boolean ispravnaVremenskaPolja() {
        return (!pocetnoVrijeme.isEmpty() && !zavrsnoVrijeme.isEmpty())
                || (pocetnoVrijeme.isEmpty() && zavrsnoVrijeme.isEmpty());
    }

    private List<ZapisDnevnika> dohvatiZapiseDnevnikaIzBaze() {

        List<ZapisDnevnika> zapisiDnevnika = new ArrayList<>();

        try (Connection con = KonektorBazePodataka.dajKonekciju();
                PreparedStatement dajSveDnevnike = con.prepareStatement("SELECT * FROM dnevnik");
                ResultSet rezultat = dajSveDnevnike.executeQuery();) {

            zapisiDnevnika = BPUtils.dohvatiZapiseDnevnikaIzResultSeta(rezultat);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return zapisiDnevnika;
    }

    private void azurirajuKonfiguraciju() {
        konfiguracija = (GeneralnaKonfiguracija) SlusacAplikacije.getServletContext().getAttribute(SlusacAplikacije.KONFIGURACIJA_IME_ATRIBUTA);
    }

}
