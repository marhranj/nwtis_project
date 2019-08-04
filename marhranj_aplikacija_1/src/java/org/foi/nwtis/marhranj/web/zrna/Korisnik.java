package org.foi.nwtis.marhranj.web.zrna;

public class Korisnik {
    String ime, prezime, korisnickoIme, lozinka, email;
    int id;
    
    public Korisnik(int id, String ime, String prezime, String korisnickoIme, String lozinka, String email) {
        this.id = id;
        this.ime = ime;
        this.prezime = prezime;
        this.korisnickoIme = korisnickoIme;
        this.lozinka = lozinka;
        this.email = email;
    }

    public Korisnik() {
       
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIme() {
        return ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public String getLozinka() {
        return lozinka;
    }
    
    public int getId(){
        return id;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public void setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public void setId(int id) {
        this.id = id;
    }
}
