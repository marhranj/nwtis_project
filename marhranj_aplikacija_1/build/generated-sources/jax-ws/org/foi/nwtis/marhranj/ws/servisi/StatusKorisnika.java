
package org.foi.nwtis.marhranj.ws.servisi;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for statusKorisnika.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="statusKorisnika"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="PASIVAN"/&gt;
 *     &lt;enumeration value="AKTIVAN"/&gt;
 *     &lt;enumeration value="NEAKTIVAN"/&gt;
 *     &lt;enumeration value="REGISTRIRAN"/&gt;
 *     &lt;enumeration value="DEREGISTRIRAN"/&gt;
 *     &lt;enumeration value="BLOKIRAN"/&gt;
 *     &lt;enumeration value="NEPOSTOJI"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "statusKorisnika")
@XmlEnum
public enum StatusKorisnika {

    PASIVAN,
    AKTIVAN,
    NEAKTIVAN,
    REGISTRIRAN,
    DEREGISTRIRAN,
    BLOKIRAN,
    NEPOSTOJI;

    public String value() {
        return name();
    }

    public static StatusKorisnika fromValue(String v) {
        return valueOf(v);
    }

}
