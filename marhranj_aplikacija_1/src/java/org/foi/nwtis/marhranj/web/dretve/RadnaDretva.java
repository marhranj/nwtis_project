package org.foi.nwtis.marhranj.web.dretve;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.lucbagic.DnevnikZapis;
import org.foi.nwtis.lucbagic.konfiguracije.Konfiguracija;
import org.foi.nwtis.lucbagic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.marhranj.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.lucbagic.ws.servisi.StatusKorisnika;

public class RadnaDretva extends Thread {

    private Socket socket;
    private Konfiguracija konf;
    private BP_Konfiguracija bpk;
    private Pattern pattern;
    private Matcher matcher;
    private boolean regexDobar;
    private InputStream inputStream;
    private OutputStream outputStream;
    private StringBuffer buffer;
    private static boolean komandePosluzitelj = false;
    private static boolean stani = false;
    private static int brojPoruke = 0;
    private String autentikacijaRegex = "^KORISNIK ([a-zA-Z0-9_-]+); LOZINKA ([a-zA-Z0-9_-]+);";
    private String komandaRegex = "^KORISNIK ([a-zA-Z0-9_-]+); LOZINKA ([a-zA-Z0-9_-]+); (PAUZA;|KRENI;|PASIVNO;|AKTIVNO;|STANI;|STANJE;)";
    private String grupaRegex = "^KORISNIK ([a-zA-Z0-9_-]+); LOZINKA ([a-zA-Z0-9_-]+); GRUPA (DODAJ;|PREKID;|KRENI;|PAUZA;|STANJE;)";
    private DnevnikZapis dnevnikZapis;

    public RadnaDretva(Konfiguracija konf, BP_Konfiguracija bpk, Socket socket) {
        this.konf = konf;
        this.bpk = bpk;
        this.socket = socket;
        this.dnevnikZapis = new DnevnikZapis();
    }

    @Override
    public void run() {
        super.run();
        String komanda = dohvatiKomandu();
        System.out.println("KOMANDA: " + komanda);
        dnevnikZapis.postaviPocetnoVrijeme();
        if (provjeriRegex(komanda, autentikacijaRegex)) {
            autentikacijaKorisnika(matcher.group(1), matcher.group(2));
            dnevnikZapis.upisUDnevnik(matcher.group(1), komanda, "SOCKET");
            posaljiPorukuNaSocket("OK 10;");
        } else if (provjeriRegex(komanda, komandaRegex)) {
            String korisnik = matcher.group(1);
            String lozinka = matcher.group(2);
            String kmnd = matcher.group(3);
            if (autentikacijaKorisnika(korisnik, lozinka)) {
                izvrsiKomandu(kmnd, korisnik, komanda);
                dnevnikZapis.upisUDnevnik(matcher.group(1), komanda, "SOCKET");
            }
        } else if (provjeriRegex(komanda, grupaRegex)) {
            if (!komandePosluzitelj) {
                if (autentikacijaKorisnika(matcher.group(1), matcher.group(2))) {
                    String korIme = konf.dajPostavku("korisnik");
                    String lozinka = konf.dajPostavku("lozinka");
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

    @Override
    public synchronized void start() {
        super.start();
    }
    
    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    private boolean provjeriRegex(String komanda, String regex) {
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(komanda);
        regexDobar = matcher.matches();
        if (regexDobar) {
            return true;
        }
        return false;
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
        String url = bpk.getServerDatabase() + bpk.getUserDatabase();
        String user = bpk.getUserUsername();
        String password = bpk.getUserPassword();
        String driver = bpk.getDriverDatabase(url);
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            System.err.println("Ne može se spojit s ovim driverom: " + ex.getMessage());
        }
        try (Connection con = DriverManager.getConnection(url, user, password);
                Statement stmt = con.createStatement();) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS broj FROM korisnici WHERE korisnickoIme='" + korisnickoIme + "' AND lozinka='" + lozinka + "'");
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
        System.err.println("ODAVDE TI ŠALJEm");
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
        if (!SlusacAplikacije.isPauzirano()) {
            SlusacAplikacije.setPauzirano(true);
            posaljiPorukuNaSocket("OK 10;");
        } else {
            posaljiPorukuNaSocket("ERR 14;");
        }
    }

    private void operacijaAktivno() {
        if (SlusacAplikacije.isPauzirano()) {
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
        if (!komandePosluzitelj && !SlusacAplikacije.isPauzirano()) {
            posaljiPorukuNaSocket("OK 11;");
        } else if (!komandePosluzitelj && SlusacAplikacije.isPauzirano()) {
            posaljiPorukuNaSocket("OK 12;");
        } else if (komandePosluzitelj && !SlusacAplikacije.isPauzirano()) {
            posaljiPorukuNaSocket("OK 13;");
        } else if (komandePosluzitelj && SlusacAplikacije.isPauzirano()) {
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
        if (dajStatusGrupe(korIme, lozinka).toString().equals("AKTIVAN")) {
            posaljiPorukuNaSocket("ERR 22;");
        } else if (dajStatusGrupe(korIme, lozinka).toString().equals("BLOKIRAN") || dajStatusGrupe(korIme, lozinka).toString().equals("REGISTRIRAN")
                || dajStatusGrupe(korIme, lozinka).toString().equals("NEAKTIVAN") || dajStatusGrupe(korIme, lozinka).toString().equals("PASIVAN")) {
            aktivirajGrupu(korIme, lozinka);
            posaljiPorukuNaSocket("OK 20;");
        } else if (dajStatusGrupe(korIme, lozinka).toString().equals("DEREGISTRIRAN")) {
            posaljiPorukuNaSocket("ERR 21;");
        }
    }

    private void operacijaPauzaGrupu(String korIme, String lozinka) {
        if (dajStatusGrupe(korIme, lozinka).toString().equals("AKTIVAN") || dajStatusGrupe(korIme, lozinka).toString().equals("PASIVAN")) {
            blokirajGrupu(korIme, lozinka);
            posaljiPorukuNaSocket("OK 20;");
        } else if (dajStatusGrupe(korIme, lozinka).toString().equals("DEREGISTRIRAN")) {
            posaljiPorukuNaSocket("ERR 21;");
        } else if (dajStatusGrupe(korIme, lozinka).toString().equals("BLOKIRAN") || dajStatusGrupe(korIme, lozinka).toString().equals("REGISTRIRAN")) {
            posaljiPorukuNaSocket("ERR 23;");
        }
    }

    private void operacijaStanjeGrupu(String korIme, String lozinka) {
        if (dajStatusGrupe(korIme, lozinka).toString().equals("AKTIVAN")) {
            posaljiPorukuNaSocket("OK 21;");
        } else if (dajStatusGrupe(korIme, lozinka).toString().equals("BLOKIRAN")) {
            posaljiPorukuNaSocket("OK 22;");
        } else if (dajStatusGrupe(korIme, lozinka).toString().equals("DEREGISTRIRAN")) {
            posaljiPorukuNaSocket("ERR 21;");
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

    public static Boolean aktivirajGrupu(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.lucbagic.ws.servisi.AerodromiWS_Service service = new org.foi.nwtis.lucbagic.ws.servisi.AerodromiWS_Service();
        org.foi.nwtis.lucbagic.ws.servisi.AerodromiWS port = service.getAerodromiWSPort();
        return port.aktivirajGrupu(korisnickoIme, korisnickaLozinka);
    }

    public static Boolean registrirajGrupu(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.lucbagic.ws.servisi.AerodromiWS_Service service = new org.foi.nwtis.lucbagic.ws.servisi.AerodromiWS_Service();
        org.foi.nwtis.lucbagic.ws.servisi.AerodromiWS port = service.getAerodromiWSPort();
        return port.registrirajGrupu(korisnickoIme, korisnickaLozinka);
    }

    public static StatusKorisnika dajStatusGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.lucbagic.ws.servisi.AerodromiWS_Service service = new org.foi.nwtis.lucbagic.ws.servisi.AerodromiWS_Service();
        org.foi.nwtis.lucbagic.ws.servisi.AerodromiWS port = service.getAerodromiWSPort();
        return port.dajStatusGrupe(korisnickoIme, korisnickaLozinka);
    }

    public static Boolean deregistrirajGrupu(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.lucbagic.ws.servisi.AerodromiWS_Service service = new org.foi.nwtis.lucbagic.ws.servisi.AerodromiWS_Service();
        org.foi.nwtis.lucbagic.ws.servisi.AerodromiWS port = service.getAerodromiWSPort();
        return port.deregistrirajGrupu(korisnickoIme, korisnickaLozinka);
    }

    public static Boolean blokirajGrupu(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.lucbagic.ws.servisi.AerodromiWS_Service service = new org.foi.nwtis.lucbagic.ws.servisi.AerodromiWS_Service();
        org.foi.nwtis.lucbagic.ws.servisi.AerodromiWS port = service.getAerodromiWSPort();
        return port.blokirajGrupu(korisnickoIme, korisnickaLozinka);
    }

    public static Boolean autenticirajGrupu(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.lucbagic.ws.servisi.AerodromiWS_Service service = new org.foi.nwtis.lucbagic.ws.servisi.AerodromiWS_Service();
        org.foi.nwtis.lucbagic.ws.servisi.AerodromiWS port = service.getAerodromiWSPort();
        return port.autenticirajGrupu(korisnickoIme, korisnickaLozinka);
    }

}
