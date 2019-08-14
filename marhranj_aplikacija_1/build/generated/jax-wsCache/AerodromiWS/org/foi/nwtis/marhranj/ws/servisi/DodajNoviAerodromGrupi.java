
package org.foi.nwtis.marhranj.ws.servisi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for dodajNoviAerodromGrupi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dodajNoviAerodromGrupi"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="korisnickoIme" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="korisnickaLozinka" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="idAerodrom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nazivAerodrom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="drzavaAerodrom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="latAerodrom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="lonAerodrom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dodajNoviAerodromGrupi", propOrder = {
    "korisnickoIme",
    "korisnickaLozinka",
    "idAerodrom",
    "nazivAerodrom",
    "drzavaAerodrom",
    "latAerodrom",
    "lonAerodrom"
})
public class DodajNoviAerodromGrupi {

    protected String korisnickoIme;
    protected String korisnickaLozinka;
    protected String idAerodrom;
    protected String nazivAerodrom;
    protected String drzavaAerodrom;
    protected String latAerodrom;
    protected String lonAerodrom;

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
     * Gets the value of the idAerodrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdAerodrom() {
        return idAerodrom;
    }

    /**
     * Sets the value of the idAerodrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdAerodrom(String value) {
        this.idAerodrom = value;
    }

    /**
     * Gets the value of the nazivAerodrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNazivAerodrom() {
        return nazivAerodrom;
    }

    /**
     * Sets the value of the nazivAerodrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNazivAerodrom(String value) {
        this.nazivAerodrom = value;
    }

    /**
     * Gets the value of the drzavaAerodrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDrzavaAerodrom() {
        return drzavaAerodrom;
    }

    /**
     * Sets the value of the drzavaAerodrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDrzavaAerodrom(String value) {
        this.drzavaAerodrom = value;
    }

    /**
     * Gets the value of the latAerodrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLatAerodrom() {
        return latAerodrom;
    }

    /**
     * Sets the value of the latAerodrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLatAerodrom(String value) {
        this.latAerodrom = value;
    }

    /**
     * Gets the value of the lonAerodrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLonAerodrom() {
        return lonAerodrom;
    }

    /**
     * Sets the value of the lonAerodrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLonAerodrom(String value) {
        this.lonAerodrom = value;
    }

}
