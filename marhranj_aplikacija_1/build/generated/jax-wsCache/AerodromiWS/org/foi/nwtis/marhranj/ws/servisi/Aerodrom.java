
package org.foi.nwtis.marhranj.ws.servisi;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for aerodrom complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="aerodrom"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="avioni" type="{http://serveri.ws.dkermek.nwtis.foi.org/}avion" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="drzava" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="icao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="lokacija" type="{http://serveri.ws.dkermek.nwtis.foi.org/}lokacija" minOccurs="0"/&gt;
 *         &lt;element name="naziv" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="status" type="{http://serveri.ws.dkermek.nwtis.foi.org/}aerodromStatus" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "aerodrom", propOrder = {
    "avioni",
    "drzava",
    "icao",
    "lokacija",
    "naziv",
    "status"
})
public class Aerodrom {

    @XmlElement(nillable = true)
    protected List<Avion> avioni;
    protected String drzava;
    protected String icao;
    protected Lokacija lokacija;
    protected String naziv;
    @XmlSchemaType(name = "string")
    protected AerodromStatus status;

    /**
     * Gets the value of the avioni property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the avioni property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAvioni().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Avion }
     * 
     * 
     */
    public List<Avion> getAvioni() {
        if (avioni == null) {
            avioni = new ArrayList<Avion>();
        }
        return this.avioni;
    }

    /**
     * Gets the value of the drzava property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDrzava() {
        return drzava;
    }

    /**
     * Sets the value of the drzava property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDrzava(String value) {
        this.drzava = value;
    }

    /**
     * Gets the value of the icao property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIcao() {
        return icao;
    }

    /**
     * Sets the value of the icao property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIcao(String value) {
        this.icao = value;
    }

    /**
     * Gets the value of the lokacija property.
     * 
     * @return
     *     possible object is
     *     {@link Lokacija }
     *     
     */
    public Lokacija getLokacija() {
        return lokacija;
    }

    /**
     * Sets the value of the lokacija property.
     * 
     * @param value
     *     allowed object is
     *     {@link Lokacija }
     *     
     */
    public void setLokacija(Lokacija value) {
        this.lokacija = value;
    }

    /**
     * Gets the value of the naziv property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNaziv() {
        return naziv;
    }

    /**
     * Sets the value of the naziv property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNaziv(String value) {
        this.naziv = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link AerodromStatus }
     *     
     */
    public AerodromStatus getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link AerodromStatus }
     *     
     */
    public void setStatus(AerodromStatus value) {
        this.status = value;
    }

}
