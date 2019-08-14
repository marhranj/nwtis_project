
package org.foi.nwtis.marhranj.ws.servisi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for avion complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="avion"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="callsign" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="estarrivalairport" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="estdepartureairport" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="icao24" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "avion", propOrder = {
    "callsign",
    "estarrivalairport",
    "estdepartureairport",
    "icao24",
    "id"
})
public class Avion {

    protected String callsign;
    protected String estarrivalairport;
    protected String estdepartureairport;
    protected String icao24;
    protected Integer id;

    /**
     * Gets the value of the callsign property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCallsign() {
        return callsign;
    }

    /**
     * Sets the value of the callsign property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCallsign(String value) {
        this.callsign = value;
    }

    /**
     * Gets the value of the estarrivalairport property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstarrivalairport() {
        return estarrivalairport;
    }

    /**
     * Sets the value of the estarrivalairport property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstarrivalairport(String value) {
        this.estarrivalairport = value;
    }

    /**
     * Gets the value of the estdepartureairport property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstdepartureairport() {
        return estdepartureairport;
    }

    /**
     * Sets the value of the estdepartureairport property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstdepartureairport(String value) {
        this.estdepartureairport = value;
    }

    /**
     * Gets the value of the icao24 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIcao24() {
        return icao24;
    }

    /**
     * Sets the value of the icao24 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIcao24(String value) {
        this.icao24 = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setId(Integer value) {
        this.id = value;
    }

}
