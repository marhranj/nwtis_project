package org.foi.nwtis.mahranj.web;

import java.io.Serializable;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.xml.ws.WebServiceRef;
import org.foi.nwtis.marhranj.konstante.Statusi;
import org.foi.nwtis.marhranj.utils.SocketUtils;
import org.foi.nwtis.marhranj.ws.serveri.AIRP2WS;
import org.foi.nwtis.marhranj.ws.serveri.AIRP2WS_Service;

@Named(value = "serverKomunikacija")
@SessionScoped
public class ServerKomunikacija implements Serializable {

    private String korisnickoIme = "";
    private String lozinka = "";
   
    private String poruka = "";
    
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8084/marhranj_aplikacija_1/AIRP2WS.wsdl")
    private AIRP2WS_Service service = new AIRP2WS_Service();

    private final AIRP2WS port = service.getAIRP2WSPort();
    
    public void init() {
        poruka = "";
    }
    
    public boolean autenticirajKorisnika() {
        if (!korisnickoIme.isEmpty() && !lozinka.isEmpty()) {
            String naredba = String.format("KORISNIK %s; LOZINKA %s;", korisnickoIme, lozinka);
            boolean ispravanKorisnik = validirajNaredbuZaServer(naredba);
            if (ispravanKorisnik) {
                poruka = "Uspjesna autentikacija";
            } else {
                poruka = "Neuspjesna autentikacija";
            }
            return ispravanKorisnik;
        } else {
            poruka = "Morate unijeti korisnicko ime i lozinku";
        }
        return false;
    }
    
    public void pauzirajServer() {
        if (autenticirajKorisnika()) {
            String naredba = String.format("KORISNIK %s; LOZINKA %s; PAUZA;", korisnickoIme, lozinka);
            if (validirajNaredbuZaServer(naredba)) {
                poruka = "Server pauziran";
            } else {
                poruka = "Server već pauziran";
            }
        } else {
            poruka = "Neuspjesna autentikacija";
        }
    }

    public void kreniServer() {
        if (autenticirajKorisnika()) {
            String naredba = String.format("KORISNIK %s; LOZINKA %s; KRENI;", korisnickoIme, lozinka);
            if (validirajNaredbuZaServer(naredba)) {
                poruka = "Server pokrenut";
            } else {
                poruka = "Server već pokrenut";
            }
        } else {
            poruka = "Neuspjesna autentikacija";
        }
    }

    public void pasivnoServer() {
        if (autenticirajKorisnika()) {
            String naredba = String.format("KORISNIK %s; LOZINKA %s; PASIVNO;", korisnickoIme, lozinka);
            if (validirajNaredbuZaServer(naredba)) {
                poruka = "Server pasivan";
            } else {
                poruka = "Server je već pasivan";
            }
        } else {
            poruka = "Neuspjesna autentikacija";
        }
    }

    public void aktivnoServer() {
        if (autenticirajKorisnika()) {
            String naredba = String.format("KORISNIK %s; LOZINKA %s; AKTIVNO;", korisnickoIme, lozinka);
            if (validirajNaredbuZaServer(naredba)) {
                poruka = "Server aktivan";
            } else {
                poruka = "Server je već aktivan";
            }
        } else {
            poruka = "Neuspjesna autentikacija";
        }
    }

    public void staniServer() {
        if (autenticirajKorisnika()) {
            String naredba = String.format("KORISNIK %s; LOZINKA %s; STANI;", korisnickoIme, lozinka);
            if (validirajNaredbuZaServer(naredba)) {
                poruka = "Server stopiran";
            } else {
                poruka = "Server je već stopiran";
            }
        } else {
            poruka = "Neuspjesna autentikacija";
        }
    }

    public void stanjeServer() {
        if (autenticirajKorisnika()) {
            String naredba = String.format("KORISNIK %s; LOZINKA %s; STANJE;", korisnickoIme, lozinka);
            String status = dajStanje(naredba);
            if (status.contains(Statusi.OK_11)) {
                poruka = "STANJE: Preuzima sve komande i preuzima podatke za aerodrome";
            } else if (status.contains(Statusi.OK_12)) {
                poruka = "STANJE: Preuzima sve komanda i ne preuzima podatke za aerodrome";
            } else if (status.contains(Statusi.OK_13)) {
                poruka = "STANJE: Preuzima samo poslužiteljske komande i preuzima podatke za aerodrome";
            } else if (status.contains(Statusi.OK_14)) {
                poruka = "STANJE: Preuzima samo poslužiteljske komanda i ne preuzima podatke za aerodrome";
            } else {
                poruka = "Nije moguće utvrditi status servera";
            }
        } else {
            poruka = "Neuspjesna autentikacija";
        }
    }

    public void registrirajGrupu() {
        if (autenticirajKorisnika()) {
            String naredba = String.format("KORISNIK %s; LOZINKA %s; GRUPA DODAJ;", korisnickoIme, lozinka);
            if (validirajNaredbuZaGrupu(naredba)) {
                poruka = "Grupa registrirana";
            } else {
                poruka = "Grupa je već bila registrirana";
            }
        } else {
            poruka = "Neuspjesna autentikacija";
        }
    }

    public void deregistrirajGrupu() {
        if (autenticirajKorisnika()) {
            String naredba = String.format("KORISNIK %s; LOZINKA %s; GRUPA PREKID;", korisnickoIme, lozinka);
            if (validirajNaredbuZaGrupu(naredba)) {
                poruka = "Grupa deregistrirana";
            } else {
                poruka = "Grupa je već bila deregistrirana";
            }
        } else {
            poruka = "Neuspjesna autentikacija";
        }
    }

    public void aktivirajGrupu() {
        if (autenticirajKorisnika()) {
            String naredba = String.format("KORISNIK %s; LOZINKA %s; GRUPA KRENI;", korisnickoIme, lozinka);
            if (validirajNaredbuZaGrupu(naredba)) {
                poruka = "Grupa aktivirana";
            } else {
                poruka = "Grupa je već bila aktivirana ili je grupa deregistrirana";
            }
        } else {
            poruka = "Neuspjesna autentikacija";
        }
    }

    public void blokirajGrupu() {
        if (autenticirajKorisnika()) {
            String naredba = String.format("KORISNIK %s; LOZINKA %s; GRUPA PAUZA;", korisnickoIme, lozinka);
            if (validirajNaredbuZaGrupu(naredba)) {
                poruka = "Grupa blokirana";
            } else {
                poruka = "Grupa je već bila blokirana ili je grupa deregistrirana";
            }
        } else {
            poruka = "Neuspjesna autentikacija";
        }
    }

    public void stanjeGrupe() {
        if (autenticirajKorisnika()) {
            String naredba = String.format("KORISNIK %s; LOZINKA %s; GRUPA STANJE;", korisnickoIme, lozinka);
            String status = dajStanje(naredba);
            if (status.contains(Statusi.OK_21)) {
                poruka = "STANJE: Grupa je aktivna";
            } else if (status.contains(Statusi.OK_22)) {
                poruka = "STANJE: Grupa je blokirana";
            } else if (status.contains(Statusi.ERR_21)) {
                poruka = "STANJE: Grupa ne postoji";
            } else if (status.contains(Statusi.ERR_22)) {
                poruka = "STANJE: Grupa deregistrirana";
            } else if (status.contains(Statusi.OK_23)) {
                poruka = "STANJE: Grupa registrirana";
            } else {
                poruka = "Nije moguće utvrditi status grupe";
            }
        } else {
            poruka = "Neuspjesna autentikacija";
        }
    }

    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public void setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public String getPoruka() {
        return poruka;
    }

    public void setPoruka(String poruka) {
        this.poruka = poruka;
    }
    
    private boolean validirajNaredbuZaServer(String naredba) {
        return SocketUtils.posaljiPorukuNaSocket(naredba, port.dohvatiPortSocketa()).contains(Statusi.OK_10);
    }
    
    private boolean validirajNaredbuZaGrupu(String naredba) {
        return SocketUtils.posaljiPorukuNaSocket(naredba, port.dohvatiPortSocketa()).contains(Statusi.OK_20);
    }
    
    private String dajStanje(String naredba) {
        return SocketUtils.posaljiPorukuNaSocket(naredba, port.dohvatiPortSocketa());
    }
    
}
