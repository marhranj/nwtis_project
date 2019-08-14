package org.foi.nwtis.marhranj.web.dretve;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.ws.WebServiceRef;
import org.foi.nwtis.marhranj.DnevnikZapis;
import org.foi.nwtis.marhranj.konfiguracije.GeneralnaKonfiguracija;
import org.foi.nwtis.marhranj.utils.RegexChecker;
import org.foi.nwtis.marhranj.web.KonektorBazePodataka;
import org.foi.nwtis.marhranj.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.marhranj.ws.servisi.AerodromiWS;
import org.foi.nwtis.marhranj.ws.servisi.AerodromiWS_Service;
import org.foi.nwtis.marhranj.ws.servisi.StatusKorisnika;

public class RadnaDretva extends Thread {
    
    private Socket socket;
    private GeneralnaKonfiguracija konfiguracija;
    private Pattern pattern;
    private Matcher matcher;
    private boolean regexDobar;
    private InputStream inputStream;
    private OutputStream outputStream;
    private StringBuffer buffer;
    private boolean komandePosluzitelj;
    private boolean stani;
    private int brojPoruke;
    
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/nwtis.foi.hr_8080/NWTiS_2019/AerodromiWS.wsdl")
    private AerodromiWS_Service service = new AerodromiWS_Service();
    
    private AerodromiWS port = service.getAerodromiWSPort();
    
    private DnevnikZapis dnevnikZapis = new DnevnikZapis();

    public RadnaDretva(GeneralnaKonfiguracija konf, Socket socket) {
        this.konfiguracija = konf;
        this.socket = socket;
    }

    @Override
    public void run() {
        super.run();
        String komanda = dohvatiKomandu();
        System.out.println("KOMANDA: " + komanda);
        dnevnikZapis.postaviPocetnoVrijeme();
        if (RegexChecker.ispravniIzrazZaAutentikaciju(komanda)) {
            autentikacijaKorisnika(matcher.group(1), matcher.group(2));
            dnevnikZapis.upisUDnevnik(matcher.group(1), komanda, "SOCKET");
            posaljiPorukuNaSocket("OK 10;");
        } else if (RegexChecker.ispraviIzrazZaNaredbu(komanda)) {
            String korisnik = matcher.group(1);
            String lozinka = matcher.group(2);
            String kmnd = matcher.group(3);
            if (autentikacijaKorisnika(korisnik, lozinka)) {
                izvrsiKomandu(kmnd, korisnik, komanda);
                dnevnikZapis.upisUDnevnik(matcher.group(1), komanda, "SOCKET");
            }
        } else if (RegexChecker.ispraviIzrazZaGrupu(komanda)) {
            if (!komandePosluzitelj) {
                if (autentikacijaKorisnika(matcher.group(1), matcher.group(2))) {
                    String korIme = konfiguracija.getKorisnik();
                    String lozinka = konfiguracija.getLozinka();
                    if (autenticirajGrupu(korIme, lozinka)) {
                        izvrsiKomanduGrupa(korIme, lozinka, komanda);
                    } else {
                        posaljiPorukuNaSocket("ERR 11;");
                    }

                }
            } else {
                posaljiPorukuNaSocket("ERR - posluzitelj prima samo komande za posluzitelj");
            }
        } else {
            posaljiPorukuNaSocket("ERR - pogrešna komanda;");
        }
    }

    private String dohvatiKomandu() {
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            buffer = new StringBuffer();
            int znak;
            while ((znak = inputStream.read()) != -1) {
                buffer.append((char) znak);
            }
            return buffer.toString();
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean autentikacijaKorisnika(String korisnickoIme, String lozinka) {
        String upit = "SELECT COUNT(*) AS broj FROM korisnici WHERE korisnickoIme=? AND lozinka=?";
        try (Connection con = KonektorBazePodataka.dajKonekciju();
                PreparedStatement stmt = con.prepareStatement(lozinka);) {
            stmt.setString(0, korisnickoIme);
            stmt.setString(0, lozinka);
            ResultSet rezultat = stmt.executeQuery();
            rezultat.next();
            return rezultat.getInt(1) > 0;
        } catch (SQLException ex) {
            System.err.println("SQLException: " + ex);
        }
        System.err.println("ODAVDE TI ŠALJEM");
        posaljiPorukuNaSocket("ERR 11;");
        return false;
    }

    private void operacijaPauza() {
        if (!komandePosluzitelj) {
            setKomandePosluzitelj(true);
            posaljiPorukuNaSocket("OK 10;");
        } else {
            posaljiPorukuNaSocket("ERR 12;");
        }
    }

    private void operacijaKreni() {
        if (komandePosluzitelj) {
            setKomandePosluzitelj(false);
            posaljiPorukuNaSocket("OK 10;");
        } else {
            posaljiPorukuNaSocket("ERR 13;");
        }
    }

    private void operacijaPasivno() {
        if (!SlusacAplikacije.getPauzirano()) {
            SlusacAplikacije.setPauzirano(true);
            posaljiPorukuNaSocket("OK 10;");
        } else {
            posaljiPorukuNaSocket("ERR 14;");
        }
    }

    private void operacijaAktivno() {
        if (SlusacAplikacije.getPauzirano()) {
            SlusacAplikacije.setPauzirano(false);
            posaljiPorukuNaSocket("OK 10;");
        } else {
            posaljiPorukuNaSocket("ERR 15;");
        }
    }

    private void operacijaStani() {
        if (!stani) {
            SlusacAplikacije.setStopirano(true);
            posaljiPorukuNaSocket("OK 10;");
        } else {
            posaljiPorukuNaSocket("ERR 16;");
        }
    }

    private void operacijaStanje() {
        if (!komandePosluzitelj && !SlusacAplikacije.getPauzirano()) {
            posaljiPorukuNaSocket("OK 11;");
        } else if (!komandePosluzitelj && SlusacAplikacije.getPauzirano()) {
            posaljiPorukuNaSocket("OK 12;");
        } else if (komandePosluzitelj && !SlusacAplikacije.getPauzirano()) {
            posaljiPorukuNaSocket("OK 13;");
        } else if (komandePosluzitelj && SlusacAplikacije.getPauzirano()) {
            posaljiPorukuNaSocket("OK 14;");
        }
    }

    private void izvrsiKomandu(String kmnd, String korisnik, String komanda) {
        switch (kmnd) {
            case "PAUZA;":
                operacijaPauza();
                dnevnikZapis.upisUDnevnik(matcher.group(1), komanda, "SOCKET");
                break;
            case "KRENI;":
                operacijaKreni();
                dnevnikZapis.upisUDnevnik(matcher.group(1), komanda, "SOCKET");
                break;
            case "PASIVNO;":
                operacijaPasivno();
                dnevnikZapis.upisUDnevnik(matcher.group(1), komanda, "SOCKET");
                break;
            case "AKTIVNO;":
                operacijaAktivno();
                dnevnikZapis.upisUDnevnik(matcher.group(1), komanda, "SOCKET");
                ;
                break;
            case "STANI;":
                operacijaStani();
                dnevnikZapis.upisUDnevnik(matcher.group(1), komanda, "SOCKET");
                break;
            case "STANJE;":
                operacijaStanje();
                dnevnikZapis.upisUDnevnik(matcher.group(1), komanda, "SOCKET");
                break;
        }
    }

    public boolean isKomandePosluzitelj() {
        return komandePosluzitelj;
    }

    public void setKomandePosluzitelj(boolean komandePosluzitelj) {
        this.komandePosluzitelj = komandePosluzitelj;
    }

    private void operacijaDodajGrupu(String korIme, String lozinka) {
        String statusGrupe = dajStatusGrupe(korIme, lozinka).toString();
        if (!"REGISTRIRAN".equals(statusGrupe)) {
            if (registrirajGrupu(korIme, lozinka)) {
                posaljiPorukuNaSocket("OK 20;");
            } else {
                posaljiPorukuNaSocket("ERR 20;");
            }
        } else {
            posaljiPorukuNaSocket("ERR 20;");
        }
    }

   private void operacijaPrekidGrupu(String korIme, String lozinka) {
        if (dajStatusGrupe(korIme, lozinka).toString().equals("DEREGISTRIRAN")) {
            posaljiPorukuNaSocket("ERR 21;");
        } else {
            deregistrirajGrupu(korIme, lozinka);
            posaljiPorukuNaSocket("OK 20;");
        }
    }

    private void operacijaKreniGrupu(String korIme, String lozinka) {
        switch (dajStatusGrupe(korIme, lozinka).toString()) {
            case "AKTIVAN":
                posaljiPorukuNaSocket("ERR 22;");
                break;
            case "BLOKIRAN":
            case "REGISTRIRAN":
            case "NEAKTIVAN":
            case "PASIVAN":
                aktivirajGrupu(korIme, lozinka);
                posaljiPorukuNaSocket("OK 20;");
                break;
            case "DEREGISTRIRAN":
                posaljiPorukuNaSocket("ERR 21;");
                break;
            default:
                break;
        }
    }

    private void operacijaPauzaGrupu(String korIme, String lozinka) {
        switch (dajStatusGrupe(korIme, lozinka).toString()) {
            case "AKTIVAN":
            case "PASIVAN":
                blokirajGrupu(korIme, lozinka);
                posaljiPorukuNaSocket("OK 20;");
                break;
            case "DEREGISTRIRAN":
                posaljiPorukuNaSocket("ERR 21;");
                break;
            case "BLOKIRAN":
            case "REGISTRIRAN":
                posaljiPorukuNaSocket("ERR 23;");
                break;
            default:
                break;
        }
    }

    private void operacijaStanjeGrupu(String korIme, String lozinka) {
        switch (dajStatusGrupe(korIme, lozinka).toString()) {
            case "AKTIVAN":
                posaljiPorukuNaSocket("OK 21;");
                break;
            case "BLOKIRAN":
                posaljiPorukuNaSocket("OK 22;");
                break;
            case "DEREGISTRIRAN":
                posaljiPorukuNaSocket("ERR 21;");
                break;
            default:
                break;
        }

    }

    private void izvrsiKomanduGrupa(String korisnik, String lozinka, String komanda) {
        switch (matcher.group(3)) {
            case "DODAJ;":
                operacijaDodajGrupu(korisnik, lozinka);
                dnevnikZapis.upisUDnevnik(korisnik, komanda, "SOCKET");
                break;
            case "PREKID;":
                operacijaPrekidGrupu(korisnik, lozinka);
                dnevnikZapis.upisUDnevnik(korisnik, komanda, "SOCKET");
                break;
            case "KRENI;":
                operacijaKreniGrupu(korisnik, lozinka);
                dnevnikZapis.upisUDnevnik(korisnik, komanda, "SOCKET");
                break;
            case "PAUZA;":
                operacijaPauzaGrupu(korisnik, lozinka);
                dnevnikZapis.upisUDnevnik(korisnik, komanda, "SOCKET");
                break;
            case "STANJE;":
                operacijaStanjeGrupu(korisnik, lozinka);
                dnevnikZapis.upisUDnevnik(korisnik, komanda, "SOCKET");
                break;
        }
    }

    private void posaljiPorukuNaSocket(String poruka) {
        try {
            outputStream = socket.getOutputStream();
            outputStream.write(poruka.getBytes());
            outputStream.flush();
            socket.shutdownOutput();
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

     public boolean aktivirajGrupu(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        return port.aktivirajGrupu(korisnickoIme, korisnickaLozinka);
    }

    public boolean registrirajGrupu(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        return port.registrirajGrupu(korisnickoIme, korisnickaLozinka);
    }

    public StatusKorisnika dajStatusGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        return port.dajStatusGrupe(korisnickoIme, korisnickaLozinka);
    }

    public boolean deregistrirajGrupu(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        return port.deregistrirajGrupu(korisnickoIme, korisnickaLozinka);
    }

    public boolean blokirajGrupu(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        return port.blokirajGrupu(korisnickoIme, korisnickaLozinka);
    }

    public boolean autenticirajGrupu(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        return port.autenticirajGrupu(korisnickoIme, korisnickaLozinka);
    }

}
