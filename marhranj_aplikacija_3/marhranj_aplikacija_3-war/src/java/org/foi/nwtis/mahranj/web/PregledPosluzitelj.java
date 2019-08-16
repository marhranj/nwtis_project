package org.foi.nwtis.mahranj.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

@Named(value = "pregledPosluzitelj")
@RequestScoped
public class PregledPosluzitelj {

    private String korisnickoIme = "1010Korisnik1010";
    private String lozinka = "Lozinka10";
    private InputStream is;
    private OutputStream os;
    private String odgovorKomanda;
    private String komentarKomanda;
    private String odgovorGrupa;
    private String komentarGrupa;

    public PregledPosluzitelj() {
    }

    public void pauzirajServer() throws IOException {
        String komanda = "KORISNIK " + korisnickoIme + "; LOZINKA " + lozinka + "; PAUZA;";
        odgovorKomanda = vezaPosluzitelj(komanda);

        if (odgovorKomanda.contains("OK")) {
            komentarKomanda = "Server je u pauzi";
        } else {
            komentarKomanda = "Server nije pauzi";
        }
    }

    public void kreniServer() throws IOException {
        String komanda = "KORISNIK " + korisnickoIme + "; LOZINKA " + lozinka + "; KRENI;";
        odgovorKomanda = vezaPosluzitelj(komanda);

        if (odgovorKomanda.contains("OK")) {
            komentarKomanda = "Server radi.";
        } else {
            komentarKomanda = "Server nije bio u pauzi.";
        }
    }

    public void pasivnoServer() throws IOException {
        String komanda = "KORISNIK " + korisnickoIme + "; LOZINKA " + lozinka + "; PASIVNO;";
        odgovorKomanda = vezaPosluzitelj(komanda);
        if (odgovorKomanda.contains("OK")) {
            komentarKomanda = "Prekinuto preuzimanje podataka do sljedećeg ciklusa.";
        } else {
            komentarKomanda = "Server je već u pasivnom radu.";
        }
    }

    public void aktivnoServer() throws IOException {
        String komanda = "KORISNIK " + korisnickoIme + "; LOZINKA " + lozinka + "; AKTIVNO;";
        odgovorKomanda = vezaPosluzitelj(komanda);
        if (odgovorKomanda.contains("OK")) {
            komentarKomanda = "Nastavljeno preuzimanje podataka.";
        } else {
            komentarKomanda = "Server je već u aktivnom radu.";
        }
    }

    public void staniServer() throws IOException {
        String komanda = "KORISNIK " + korisnickoIme + "; LOZINKA " + lozinka + "; STANI;";
        odgovorKomanda = vezaPosluzitelj(komanda);
        if (odgovorKomanda.contains("OK")) {
            komentarKomanda = "Prekinuto preuzimanje podataka i komandi.";
        } else {
            komentarKomanda = "Server je već u postupku prekida.";
        }
    }

    public void stanjeServer() throws IOException {
        String komanda = "KORISNIK " + korisnickoIme + "; LOZINKA " + lozinka + "; STANJE;";
        odgovorKomanda = vezaPosluzitelj(komanda);
        if (odgovorKomanda.contains("11")) {
            komentarKomanda = "Preuzima komande, preuzima aerodrome.";
        } else if (odgovorKomanda.contains("12")) {
            komentarKomanda = "Preuzima komande, ne preuzima aerodrome.";
        } else if (odgovorKomanda.contains("13")) {
            komentarKomanda = "Preuzima samo poslužiteljske komande i preuzima aerodrome.";
        } else if (odgovorKomanda.contains("14")) {
            komentarKomanda = "Preuzima samo poslužiteljske komande i ne preuzima aerodrome.";
        }
    }

    public void dodajGrupu() throws IOException {
        String komanda = "KORISNIK " + korisnickoIme + "; LOZINKA " + lozinka + "; GRUPA DODAJ;";
        odgovorGrupa = vezaPosluzitelj(komanda);
        if (odgovorGrupa.contains("OK")) {
            komentarGrupa = "Registrirana.";
        } else {
            komentarGrupa = "Već je registrirana.";
        }
    }

    public void deregistrirajGrupu() throws IOException {
        String komanda = "KORISNIK " + korisnickoIme + "; LOZINKA " + lozinka + "; GRUPA PREKID;";
        odgovorGrupa = vezaPosluzitelj(komanda);
        if (odgovorGrupa.contains("OK")) {
            komentarGrupa = "Odjavljena.";
        } else {
            komentarGrupa = "Nije bila registrirana.";
        }
    }

    public void aktivirajGrupu() throws IOException {
        String komanda = "KORISNIK " + korisnickoIme + "; LOZINKA " + lozinka + "; GRUPA KRENI;";
        odgovorGrupa = vezaPosluzitelj(komanda);
        if (odgovorGrupa.contains("OK")) {
            komentarGrupa = "Aktivirana.";
        } else {
            komentarGrupa = "Ne postoji.";
        }
    }

    public void blokirajGrupu() throws IOException {
        String komanda = "KORISNIK " + korisnickoIme + "; LOZINKA " + lozinka + "; GRUPA PAUZA;";
        odgovorGrupa = vezaPosluzitelj(komanda);
        if (odgovorGrupa.contains("OK")) {
            komentarGrupa = "Blokirana.";
        } else {
            komentarGrupa = "Ne postoji.";
        }
    }

    public void stanjeGrupe() throws IOException {
        String komanda = "KORISNIK " + korisnickoIme + "; LOZINKA " + lozinka + "; GRUPA STANJE;";
        odgovorGrupa = vezaPosluzitelj(komanda);
        if (odgovorGrupa.contains("21")) {
            komentarGrupa = "Grupa je aktivna.";
        } else if(odgovorGrupa.contains("22")){
            komentarGrupa = "Grupa blokirana.";
        }else if(odgovorGrupa.contains("ERR")){
            komentarGrupa = "Grupa ne postoji.";
        }
    }

    public String vezaPosluzitelj(String komanda) throws IOException {
        int znak;
        Socket socket = new Socket("localhost", 8000);
        is = socket.getInputStream();
        os = socket.getOutputStream();
        os.write(komanda.getBytes());
        os.flush();
        socket.shutdownOutput();
        StringBuffer buffer = new StringBuffer();
        while ((znak = is.read()) != -1) {
            buffer.append((char) znak);
        }
        String odgovor = buffer.toString();
        return odgovor;
    }

    public String getKomentarKomanda() {
        return komentarKomanda;
    }

    public void setKomentarKomanda(String komentarKomanda) {
        this.komentarKomanda = komentarKomanda;
    }

    public String getOdgovorKomanda() {
        return odgovorKomanda;
    }

    public void setOdgovorKomanda(String odgovorKomanda) {
        this.odgovorKomanda = odgovorKomanda;
    }

    public String getOdgovorGrupa() {
        return odgovorGrupa;
    }

    public void setOdgovorGrupa(String odgovorGrupa) {
        this.odgovorGrupa = odgovorGrupa;
    }

    public String getKomentarGrupa() {
        return komentarGrupa;
    }

    public void setKomentarGrupa(String komentarGrupa) {
        this.komentarGrupa = komentarGrupa;
    }

}
