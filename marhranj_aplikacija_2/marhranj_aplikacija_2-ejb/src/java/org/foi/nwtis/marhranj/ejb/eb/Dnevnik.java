/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj.ejb.eb;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author at059201
 */
@Entity
@Table(name = "dnevnik")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Dnevnik.findAll", query = "SELECT d FROM Dnevnik d")
    , @NamedQuery(name = "Dnevnik.findById", query = "SELECT d FROM Dnevnik d WHERE d.id = :id")
    , @NamedQuery(name = "Dnevnik.findByKorisnik", query = "SELECT d FROM Dnevnik d WHERE d.korisnik = :korisnik")
    , @NamedQuery(name = "Dnevnik.findByVrijeme", query = "SELECT d FROM Dnevnik d WHERE d.vrijeme = :vrijeme")
    , @NamedQuery(name = "Dnevnik.findByNaredba", query = "SELECT d FROM Dnevnik d WHERE d.naredba = :naredba")
    , @NamedQuery(name = "Dnevnik.findByUrl", query = "SELECT d FROM Dnevnik d WHERE d.url = :url")
    , @NamedQuery(name = "Dnevnik.findByIp", query = "SELECT d FROM Dnevnik d WHERE d.ip = :ip")
    , @NamedQuery(name = "Dnevnik.findByVrsta", query = "SELECT d FROM Dnevnik d WHERE d.vrsta = :vrsta")
    , @NamedQuery(name = "Dnevnik.findByTrajanje", query = "SELECT d FROM Dnevnik d WHERE d.trajanje = :trajanje")})
public class Dnevnik implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 99)
    @Column(name = "korisnik")
    private String korisnik;
    @Basic(optional = false)
    @NotNull
    @Column(name = "vrijeme")
    @Temporal(TemporalType.TIMESTAMP)
    private Date vrijeme;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "naredba")
    private String naredba;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "url")
    private String url;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "ip")
    private String ip;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "vrsta")
    private String vrsta;
    @Basic(optional = false)
    @NotNull
    @Column(name = "trajanje")
    private int trajanje;

    public Dnevnik() {
    }

    public Dnevnik(Integer id) {
        this.id = id;
    }

    public Dnevnik(Integer id, String korisnik, Date vrijeme, String naredba, String url, String ip, String vrsta, int trajanje) {
        this.id = id;
        this.korisnik = korisnik;
        this.vrijeme = vrijeme;
        this.naredba = naredba;
        this.url = url;
        this.ip = ip;
        this.vrsta = vrsta;
        this.trajanje = trajanje;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(String korisnik) {
        this.korisnik = korisnik;
    }

    public Date getVrijeme() {
        return vrijeme;
    }

    public void setVrijeme(Date vrijeme) {
        this.vrijeme = vrijeme;
    }

    public String getNaredba() {
        return naredba;
    }

    public void setNaredba(String naredba) {
        this.naredba = naredba;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getVrsta() {
        return vrsta;
    }

    public void setVrsta(String vrsta) {
        this.vrsta = vrsta;
    }

    public int getTrajanje() {
        return trajanje;
    }

    public void setTrajanje(int trajanje) {
        this.trajanje = trajanje;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Dnevnik)) {
            return false;
        }
        Dnevnik other = (Dnevnik) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.foi.nwtis.marhranj.ejb.eb.Dnevnik[ id=" + id + " ]";
    }
    
}
