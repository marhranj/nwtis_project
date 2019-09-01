package org.foi.nwtis.marhranj.web.dretve;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import javax.xml.ws.WebServiceRef;
import org.foi.nwtis.marhranj.BrojacVremena;
import org.foi.nwtis.marhranj.PisacDnevnika;
import org.foi.nwtis.marhranj.konfiguracije.GeneralnaKonfiguracija;
import org.foi.nwtis.marhranj.konstante.Statusi;
import org.foi.nwtis.marhranj.utils.BPUtils;
import org.foi.nwtis.marhranj.utils.GrupeUtils;
import org.foi.nwtis.marhranj.utils.RegexChecker;
import org.foi.nwtis.marhranj.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.marhranj.ws.servisi.AerodromiWS;
import org.foi.nwtis.marhranj.ws.servisi.AerodromiWS_Service;
import org.foi.nwtis.marhranj.ws.servisi.StatusKorisnika;
import static org.foi.nwtis.marhranj.ws.servisi.StatusKorisnika.AKTIVAN;
import static org.foi.nwtis.marhranj.ws.servisi.StatusKorisnika.BLOKIRAN;

public class RadnaDretva extends Thread {
    
    private final Socket socket;
    private final GeneralnaKonfiguracija konfiguracija;
    
    private String poruka = "";
    
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/nwtis.foi.hr_8080/NWTiS_2019/AerodromiWS.wsdl")
    private AerodromiWS_Service service = new AerodromiWS_Service();
    
    private final AerodromiWS port = service.getAerodromiWSPort();
    
    private final PisacDnevnika pisacDnevnika = new PisacDnevnika();

    public RadnaDretva(GeneralnaKonfiguracija konf, Socket socket) {
        this.konfiguracija = konf;
        this.socket = socket;
    }

    @Override
    public void run() {
        String naredba = dajKlijentovuNaredbu();
        
        Matcher autentikacijaMatcher = RegexChecker.dajMatcherZaAutentikaciju(naredba);
        Matcher serverMatcher = RegexChecker.dajMatcherZaServer(naredba);
        Matcher grupaMatcher = RegexChecker.dajMatcherZaGrupu(naredba);
        
        if (autentikacijaMatcher.matches()) {
            provjeriKorisnika(autentikacijaMatcher.group(1), autentikacijaMatcher.group(2));
        } else if (serverMatcher.matches()) {
            if (provjeriKorisnika(serverMatcher.group(1), serverMatcher.group(2))) {
                obradiServer(serverMatcher.group(1), serverMatcher.group(3));
            }
        } else if (grupaMatcher.matches()) {
            if (SlusacAplikacije.getPauzirano()) {
                dodajPoruku("Poslana je naredba PAUZA za server stoga se ne koriste komande za grupu");
            } else if (provjeriKorisnika(grupaMatcher.group(1), grupaMatcher.group(2))) {
                obradiGrupu(grupaMatcher.group(1), grupaMatcher.group(3));
            }
        } else {
            dodajPoruku("Neispravna naredba");
        }
        odgovoriPutemSocketa(poruka, socket);
    }
    
    private void obradiServer(String korisnickoIme, String naredba) {
        BrojacVremena brojacVremena = new BrojacVremena();
        poruka = "";
        switch(naredba) {
            case "PAUZA;":
                obradiPauzaServer();
                break;
            case "KRENI;":
                obradiKreniServer();
                break; 
            case "PASIVNO;":
                obradiPasivnoServer();
                break;
            case "AKTIVNO;":
                obradiAktivnoServer();
                break;
            case "STANJE;":
                obradiStanjeServer();
                break; 
            case "STANI;":
                obradiStaniServer();
        }
        pisacDnevnika.upisUDnevnik(korisnickoIme, "Obrada grupe, naredba:" + naredba, "SOCKET",
                socket.getInetAddress().getHostName(), socket.getInetAddress().getHostAddress(),
                brojacVremena.dohvatiVrijemeProsloOdInicijalizacije());
    }
    
    private void obradiGrupu(String korisnickoIme, String naredba) {
        BrojacVremena brojacVremena = new BrojacVremena();
        poruka = "";
        String korisnikGrupe = konfiguracija.getKorisnik();
        String lozinka = konfiguracija.getLozinka();
        switch(naredba) {
            case "DODAJ;":
                obradiDodajGrupu(korisnikGrupe, lozinka);
                break;
            case "PREKID;":
                obradiPrekidGrupe(korisnikGrupe, lozinka);
                break; 
            case "KRENI;":
                obradiKreniGrupa(korisnikGrupe, lozinka);
                break;
            case "PAUZA;":
                obradiPauzaGrupa(korisnikGrupe, lozinka);
                break;
            case "STANJE;":
                obradiStanjeGrupa(korisnikGrupe, lozinka);
        }
        
        pisacDnevnika.upisUDnevnik(korisnickoIme, "Obrada grupe, naredba:" + naredba, "SOCKET",
                socket.getInetAddress().getHostName(), socket.getInetAddress().getHostAddress(),
                brojacVremena.dohvatiVrijemeProsloOdInicijalizacije());
    }
    
    private void obradiDodajGrupu(String korisnik, String lozinka) {
        if (!GrupeUtils.registriranaGrupa() && port.registrirajGrupu(korisnik, lozinka)) {
            dodajPoruku(Statusi.OK_20);
        } else {
            dodajPoruku(Statusi.ERR_20);
        }
    }
    
    private void obradiPrekidGrupe(String korisnik, String lozinka) {
        if (!GrupeUtils.deregistriranaGrupa() && port.deregistrirajGrupu(korisnik, lozinka)) {
            dodajPoruku(Statusi.OK_20);
        } else {
            dodajPoruku(Statusi.ERR_21);
        }
    }
    
    private void obradiKreniGrupa(String korisnik, String lozinka) {
        if (!GrupeUtils.aktiviranaGrupa() && port.aktivirajGrupu(korisnik, lozinka)) {
            dodajPoruku(Statusi.OK_20);
        } else {
            dodajPoruku(Statusi.ERR_21);
        }
    }
    
    private void obradiPauzaGrupa(String korisnik, String lozinka) {
        if (!GrupeUtils.blokiranaGrupa() && port.blokirajGrupu(korisnik, lozinka)) {
            dodajPoruku(Statusi.OK_20);
        } else {
            dodajPoruku(Statusi.ERR_21);
        }
    }
    
    private void obradiStanjeGrupa(String korisnik, String lozinka) {
        StatusKorisnika status = port.dajStatusGrupe(korisnik, lozinka);
        switch (status) {
            case AKTIVAN: 
                dodajPoruku(Statusi.OK_21);
                break;
            case BLOKIRAN:
                dodajPoruku(Statusi.OK_22);
                break;
            case NEPOSTOJI:
                dodajPoruku(Statusi.ERR_21);
        }
    }
    
    private void obradiPauzaServer() {
        if (SlusacAplikacije.getPauzirano()) {
            dodajPoruku(Statusi.ERR_12); 
        } else {
            dodajPoruku(Statusi.OK_10); 
            SlusacAplikacije.setPauzirano(true);
        }
    }
    
    private void obradiKreniServer() {
        if (SlusacAplikacije.getPauzirano()) {
            dodajPoruku(Statusi.OK_10); 
            SlusacAplikacije.setPauzirano(false);
        } else {
            dodajPoruku(Statusi.ERR_13); 
        }
    }
    
    private void obradiPasivnoServer() {
        if (SlusacAplikacije.getPasivno()) {
            dodajPoruku(Statusi.ERR_14); 
        } else {
            dodajPoruku(Statusi.OK_10); 
            SlusacAplikacije.setPasivno(true);
        }
    }
    
    private void obradiAktivnoServer() {
        if (SlusacAplikacije.getPasivno()) {
            dodajPoruku(Statusi.OK_10); 
            SlusacAplikacije.setPasivno(false);
        } else {
            dodajPoruku(Statusi.ERR_15); 
        }
    }
    
    private void obradiStaniServer() {
        if (SlusacAplikacije.getZaustavljeno()) {
            dodajPoruku(Statusi.ERR_16); 
        } else {
            dodajPoruku(Statusi.OK_10); 
            SlusacAplikacije.setZaustavljeno(true);
        }
    }
    
    private void obradiStanjeServer() {
        dodajPoruku(dajStanjeServera());
    }
    
    private String dajStanjeServera() {
        if (!SlusacAplikacije.getPasivno() && !SlusacAplikacije.getPauzirano()) {
            return Statusi.OK_11;
        } else if (SlusacAplikacije.getPasivno() && !SlusacAplikacije.getPauzirano()) {
            return Statusi.OK_12;
        } else if (!SlusacAplikacije.getPasivno() && SlusacAplikacije.getPauzirano()) {
            return Statusi.OK_13;
        }
        return Statusi.OK_14;
    }
    
    private void dodajPoruku(String por) {
        poruka += por + "; ";
    }

    private String dajKlijentovuNaredbu() {
        try { 
            return pretvoriStreamUString(socket.getInputStream());
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
        return "";
    }
    
    private String pretvoriStreamUString(InputStream is) {
        Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private boolean provjeriKorisnika(String korisnickoIme, String lozinka) {
        boolean postojiKorisnik = BPUtils.provjeriKorisnika(korisnickoIme, lozinka);
        if (postojiKorisnik) {
            dodajPoruku(Statusi.OK_10);
        } else {
            dodajPoruku(Statusi.ERR_11);
        }
        return postojiKorisnik;
    }
    
    private void odgovoriPutemSocketa(String poruka, Socket socket) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(poruka.getBytes());
            outputStream.flush();
            socket.shutdownOutput();
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

}
