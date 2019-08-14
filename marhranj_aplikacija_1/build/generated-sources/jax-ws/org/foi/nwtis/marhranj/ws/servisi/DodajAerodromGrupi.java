
package org.foi.nwtis.marhranj.ws.servisi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for dodajAerodromGrupi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dodajAerodromGrupi"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="korisnickoIme" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="korisnickaLozinka" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="serodrom" type="{http://serveri.ws.dkermek.nwtis.foi.org/}aerodrom" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dodajAerodromGrupi", propOrder = {
    "korisnickoIme",
    "korisnickaLozinka",
    "serodrom"
})
public class DodajAerodromGrupi {

    protected String korisnickoIme;
    protected String korisnickaLozinka;
    protected Aerodrom serodrom;

    /**
     * Gets the value of the korisnickoIme property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    /**
     * Sets the value of the korisnickoIme property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKorisnickoIme(String value) {
        this.korisnickoIme = value;
    }

    /**
     * Gets the value of the korisnickaLozinka property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKorisnickaLozinka() {
        return korisnickaLozinka;
    }

    /**
     * Sets the value of the korisnickaLozinka property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKorisnickaLozinka(String value) {
        this.korisnickaLozinka = value;
    }

    /**
     * Gets the value of the serodrom property.
     * 
     * @return
     *     possible object is
     *     {@link Aerodrom }
     *     
     */
    public Aerodrom getSerodrom() {
        return serodrom;
    }

    /**
     * Sets the value of the serodrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link Aerodrom }
     *     
     */
    public void setSerodrom(Aerodrom value) {
        this.serodrom = value;
    }

}
