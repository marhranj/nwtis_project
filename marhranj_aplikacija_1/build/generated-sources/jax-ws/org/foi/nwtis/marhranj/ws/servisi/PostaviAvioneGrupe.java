
package org.foi.nwtis.marhranj.ws.servisi;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for postaviAvioneGrupe complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="postaviAvioneGrupe"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="korisnickoIme" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="korisnickaLozinka" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="avioniNovi" type="{http://serveri.ws.dkermek.nwtis.foi.org/}avion" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "postaviAvioneGrupe", propOrder = {
    "korisnickoIme",
    "korisnickaLozinka",
    "avioniNovi"
})
public class PostaviAvioneGrupe {

    protected String korisnickoIme;
    protected String korisnickaLozinka;
    protected List<Avion> avioniNovi;

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
     * Gets the value of the avioniNovi property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the avioniNovi property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAvioniNovi().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Avion }
     * 
     * 
     */
    public List<Avion> getAvioniNovi() {
        if (avioniNovi == null) {
            avioniNovi = new ArrayList<Avion>();
        }
        return this.avioniNovi;
    }

}
