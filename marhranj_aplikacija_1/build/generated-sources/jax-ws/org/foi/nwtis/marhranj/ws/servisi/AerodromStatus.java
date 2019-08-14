
package org.foi.nwtis.marhranj.ws.servisi;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for aerodromStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="aerodromStatus"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="PASIVAN"/&gt;
 *     &lt;enumeration value="AKTIVAN"/&gt;
 *     &lt;enumeration value="BLOKIRAN"/&gt;
 *     &lt;enumeration value="NEPOSTOJI"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "aerodromStatus")
@XmlEnum
public enum AerodromStatus {

    PASIVAN,
    AKTIVAN,
    BLOKIRAN,
    NEPOSTOJI;

    public String value() {
        return name();
    }

    public static AerodromStatus fromValue(String v) {
        return valueOf(v);
    }

}
