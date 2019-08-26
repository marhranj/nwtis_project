/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj.web.zrna;

import org.foi.nwtis.marhranj.ws.servisi.Avion;
import org.foi.nwtis.rest.podaci.AvionLeti;

/**
 *
 * @author marhranj
 */
public class MojAvionLeti extends AvionLeti {
    
    public MojAvionLeti() {
    
    }
    
    public MojAvionLeti(Avion avion) {
        setId(avion.getId());
        setIcao24(avion.getIcao24());
        setCallsign(avion.getCallsign());
        setEstArrivalAirport(avion.getEstarrivalairport());
        setEstDepartureAirport(avion.getEstdepartureairport());
    }
    
    Integer id;
    
    public Integer getId() {
        return id;
    }
    
    public final void setId(Integer id) {
        this.id = id;
    }
    
}
