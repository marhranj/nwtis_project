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
import org.foi.nwtis.marhranj.utils.PretvaracVremena;
import org.foi.nwtis.marhranj.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.marhranj.web.zrna.Dnevnik;

/**
 *
 * @author marhranj
 */
@Named(value = "obradaDnevnika")
@SessionScoped
public class ObradaDnevnika implements Serializable {

    private static final String ID = "id";
    private static final String KORISNIK = "korisnik";
    private static final String URL = "url";
    private static final String IP_ADRESA = "ipAdresa";
    private static final String TRAJANJE_OBRADE = "trajanjeObrade";
    private static final String VRIJEME_PRIJEMA = "vrijemePrijema";
    private static final String RADNJA = "radnja";
    private static final String VRSTA = "vrsta";

    private String vrstaZapisa;
    private String pocetnoVrijeme;
    private String zavrsnoVrijeme;

    private String poruka = "";

    private final List<Dnevnik> sviZapisiDnevnika;
    private List<Dnevnik> zapisiDnevnika;

    private GeneralnaKonfiguracija konfiguracija;

    public ObradaDnevnika() {
        azurirajuKonfiguraciju();
        sviZapisiDnevnika = dohvatiZapiseDnevnikaIzBaze();
        zapisiDnevnika = new ArrayList<>(sviZapisiDnevnika);
    }

    public int getTablicaBrojRedaka() {
        return konfiguracija.getTablicakBrojRedaka();
    }

    public List<Dnevnik> getZapisiDnevnika() {
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

                zapisiDnevnika = dohvatiZapiseDnevnikaIzResultSeta(rezultat);
                filtrirajDnevnike.close();
                rezultat.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }

    }

    private List<Dnevnik> dohvatiZapiseDnevnikaIzResultSeta(ResultSet rezultat) throws SQLException {
        List<Dnevnik> zapisiDnevnika = new ArrayList<>();

        while (rezultat.next()) {

            Dnevnik dnevnik = new Dnevnik(
                    rezultat.getInt(ID),
                    rezultat.getString(KORISNIK),
                    rezultat.getString(URL),
                    rezultat.getString(IP_ADRESA),
                    rezultat.getLong(TRAJANJE_OBRADE),
                    rezultat.getTimestamp(VRIJEME_PRIJEMA),
                    rezultat.getString(RADNJA),
                    rezultat.getString(VRSTA)
            );

            zapisiDnevnika.add(dnevnik);
        }
        return zapisiDnevnika;
    }

    private PreparedStatement kreirajPreparedStetement(Connection con) throws SQLException {
        PreparedStatement filtrirajDnevnike = null;
        if (svaPoljaPopunjena()) {
            filtrirajDnevnike = con.prepareStatement("SELECT * FROM dnevnik WHERE vrijemePrijema BETWEEN ? AND ? AND vrsta=?;");
            filtrirajDnevnike.setTimestamp(1, PretvaracVremena.pretvoriStringUTimeStamp(pocetnoVrijeme));
            filtrirajDnevnike.setTimestamp(2, PretvaracVremena.pretvoriStringUTimeStamp(zavrsnoVrijeme));
            filtrirajDnevnike.setString(3, vrstaZapisa);
        } else if (!vrstaZapisa.isEmpty() && ispravnaVremenskaPolja()) {
            filtrirajDnevnike = con.prepareStatement("SELECT * FROM dnevnik WHERE vrsta=?;");
            filtrirajDnevnike.setString(1, vrstaZapisa);
        } else if (!pocetnoVrijeme.isEmpty() && !zavrsnoVrijeme.isEmpty()) {
            filtrirajDnevnike = con.prepareStatement("SELECT * FROM dnevnik WHERE vrijemePrijema BETWEEN ? AND ?");
            filtrirajDnevnike.setTimestamp(1, PretvaracVremena.pretvoriStringUTimeStamp(pocetnoVrijeme));
            filtrirajDnevnike.setTimestamp(2, PretvaracVremena.pretvoriStringUTimeStamp(zavrsnoVrijeme));
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

    private List<Dnevnik> dohvatiZapiseDnevnikaIzBaze() {

        List<Dnevnik> zapisiDnevnika = new ArrayList<>();

        try (Connection con = KonektorBazePodataka.dajKonekciju();
                PreparedStatement dajSveDnevnike = con.prepareStatement("SELECT * FROM dnevnik");
                ResultSet rezultat = dajSveDnevnike.executeQuery();) {

            zapisiDnevnika = dohvatiZapiseDnevnikaIzResultSeta(rezultat);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return zapisiDnevnika;
    }

    private void azurirajuKonfiguraciju() {
        konfiguracija = (GeneralnaKonfiguracija) SlusacAplikacije.getServletContext().getAttribute(SlusacAplikacije.KONFIGURACIJA_IME_ATRIBUTA);
    }

}
