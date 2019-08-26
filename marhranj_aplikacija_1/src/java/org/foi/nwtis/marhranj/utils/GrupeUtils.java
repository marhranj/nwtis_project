/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj.utils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.ws.WebServiceRef;
import org.foi.nwtis.marhranj.konfiguracije.GeneralnaKonfiguracija;
import org.foi.nwtis.marhranj.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.marhranj.web.zrna.Aerodrom;
import org.foi.nwtis.marhranj.web.zrna.MojAvionLeti;
import org.foi.nwtis.marhranj.ws.servisi.AerodromiWS;
import org.foi.nwtis.marhranj.ws.servisi.AerodromiWS_Service;
import org.foi.nwtis.marhranj.ws.servisi.Avion;
import static org.foi.nwtis.marhranj.ws.servisi.StatusKorisnika.AKTIVAN;
import static org.foi.nwtis.marhranj.ws.servisi.StatusKorisnika.BLOKIRAN;
import static org.foi.nwtis.marhranj.ws.servisi.StatusKorisnika.DEREGISTRIRAN;
import static org.foi.nwtis.marhranj.ws.servisi.StatusKorisnika.NEAKTIVAN;
import static org.foi.nwtis.marhranj.ws.servisi.StatusKorisnika.REGISTRIRAN;
import org.foi.nwtis.rest.podaci.AvionLeti;

/**
 *
 * @author marhranj
 */
public final class GrupeUtils {

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/nwtis.foi.hr_8080/NWTiS_2019/AerodromiWS.wsdl")
    private static AerodromiWS_Service service = new AerodromiWS_Service();

    private static final AerodromiWS port = service.getAerodromiWSPort();

    private GrupeUtils() {

    }

    public static boolean registriranaGrupa() {
        return port.dajStatusGrupe(getKonfiguracija().getKorisnik(), getKonfiguracija().getLozinka()) == REGISTRIRAN;
    }

    public static boolean deregistriranaGrupa() {
        return port.dajStatusGrupe(getKonfiguracija().getKorisnik(), getKonfiguracija().getLozinka()) == DEREGISTRIRAN;
    }

    public static boolean aktiviranaGrupa() {
        return port.dajStatusGrupe(getKonfiguracija().getKorisnik(), getKonfiguracija().getLozinka()) == AKTIVAN;
    }

    public static boolean neaktivnaGrupa() {
        return port.dajStatusGrupe(getKonfiguracija().getKorisnik(), getKonfiguracija().getLozinka()) == NEAKTIVAN;
    }

    public static boolean blokiranaGrupa() {
        return port.dajStatusGrupe(getKonfiguracija().getKorisnik(), getKonfiguracija().getLozinka()) == BLOKIRAN;
    }
    
    public static List<org.foi.nwtis.marhranj.ws.servisi.Aerodrom> dohvatiSveAerodrome() {
        if (aktivirajGrupu()) {
            return port.dajSveAerodromeGrupe(getKonfiguracija().getKorisnik(), getKonfiguracija().getLozinka());
        }
        return Collections.emptyList();
    }
    
    public static boolean dodajAerodromeGrupe(List<Aerodrom> aerodromi) {
        if (aktivirajGrupu()) {
            aerodromi.forEach(aerodrom -> port.dodajNoviAerodromGrupi(getKonfiguracija().getKorisnik(),
                getKonfiguracija().getLozinka(), aerodrom.getIcao(), aerodrom.getNaziv(), aerodrom.getDrzava(), aerodrom.getLokacija().getLatitude(), aerodrom.getLokacija().getLongitude()));
            return true;
        }
        return false;
    }
    
    public static boolean obrisiOdabraneAerodromeGrupe(List<Aerodrom> aerodromi) {
        if (aktivirajGrupu()) {
            List<String> idAerodroma = aerodromi
                    .stream()
                    .map(Aerodrom::getIcao)
                    .collect(Collectors.toList());
            port.obrisiOdabraneAerodromeGrupe(getKonfiguracija().getKorisnik(), getKonfiguracija().getLozinka(), idAerodroma);
            return true;
        }
        return false;
    }
    
    private static boolean obrisiSveAerodromeGrupe() {
        if (aktivirajGrupu()) {
            port.obrisiSveAerodromeGrupe(getKonfiguracija().getKorisnik(), getKonfiguracija().getLozinka());
            return true;
        }
        return false;
    }
    
    public static boolean azurirajAerodromeGrupe(List<Aerodrom> sviAerodromi) {
        if (aktivirajGrupu()) {
            obrisiSveAerodromeGrupe();
            dodajAerodromeGrupe(sviAerodromi);
            return true;
        }
        return false;
    }
    
    public static boolean dodajAvioneGrupi(List<MojAvionLeti> avioni) {
        if (aktivirajGrupu()) {
            List<Avion> vecDodaniAvioni = dohvatiSveAvioneAerodromaGrupe(dohvatiAerodromAviona(avioni));
            
            avioni.stream()
                .map(avion -> mapirajUWsAvion(avion))
                .filter(wsAvion -> !vecDodaniAvioni.contains(wsAvion))
                .forEach(wsAvion -> port.dodajAvionGrupi(getKonfiguracija().getKorisnik(), getKonfiguracija().getLozinka(), wsAvion));
            return true;
        }
        return false;
    }
    
    public static List<Avion> dohvatiSveAvioneAerodromaGrupe(String idAerodroma) {
        if (aktivirajGrupu()) {
            return port.dajSveAvioneAerodromaGrupe(getKonfiguracija().getKorisnik(), getKonfiguracija().getLozinka(), idAerodroma);
        }
        return Collections.emptyList();
    }
    
    public static boolean azurirajAvioneGrupe(List<MojAvionLeti> avioni) {
        if (aktivirajGrupu()) {
            List<Avion> wsAvioni = avioni.stream()
                .map(avion -> mapirajUWsAvion(avion))
                .collect(Collectors.toList());
            port.postaviAvioneGrupe(getKonfiguracija().getKorisnik(), getKonfiguracija().getLozinka(), wsAvioni);
            return true;
        }
        return false;
    }
    
    private static Avion mapirajUWsAvion(MojAvionLeti avionLeti) {
        Avion avion = new Avion();
        avion.setId(avionLeti.getId());
        avion.setEstarrivalairport(avionLeti.getEstArrivalAirport());
        avion.setEstdepartureairport(avionLeti.getEstDepartureAirport());
        avion.setCallsign(avionLeti.getCallsign());
        avion.setIcao24(avionLeti.getIcao24());
        return avion;
    }

    private static GeneralnaKonfiguracija getKonfiguracija() {
        return (GeneralnaKonfiguracija) SlusacAplikacije.getServletContext()
                .getAttribute(SlusacAplikacije.KONFIGURACIJA_IME_ATRIBUTA);
    }
    
    private static String dohvatiAerodromAviona(List<MojAvionLeti> avioni) {
        return avioni.stream()
                .findFirst()
                .map(AvionLeti::getEstDepartureAirport)
                .orElse("");
    }

    private static boolean aktivirajGrupu() {
        boolean result = false;
        if (port.autenticirajGrupu(getKonfiguracija().getKorisnik(), getKonfiguracija().getLozinka())) {
            if (deregistriranaGrupa()) {
                result = port.registrirajGrupu(getKonfiguracija().getKorisnik(), getKonfiguracija().getLozinka());
                result = result && port.aktivirajGrupu(getKonfiguracija().getKorisnik(), getKonfiguracija().getLozinka());
            } 
            if (neaktivnaGrupa() || registriranaGrupa()) {
                result = port.aktivirajGrupu(getKonfiguracija().getKorisnik(), getKonfiguracija().getLozinka());
            }
            if (aktiviranaGrupa()) {
                result = true;
            }
        }
        return result;
    }

}
