/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.marhranj.web.zrna.Aerodrom;
import org.foi.nwtis.marhranj.web.zrna.MojAvionLeti;

/**
 *
 * @author marhranj
 */
public final class JsonUtils {
    
    private JsonUtils() {
    
    }
    
    public static List<Aerodrom> dohvatiAerodromeIzJsona(JsonObject jsonObject) {
        List<Aerodrom> aerodromi = new ArrayList<>();
        Gson gson = new Gson();
        JsonArray aerodromiJson = jsonObject.get("odgovor").getAsJsonArray();
        aerodromiJson.forEach(aerodromJsonElement -> {
            aerodromi.add(gson.fromJson(aerodromJsonElement, Aerodrom.class));
        });
        return aerodromi;
    }
    
    public static List<MojAvionLeti> dohvatiAvioneIzJsona(JsonObject jsonObject) {
        List<MojAvionLeti> avioni = new ArrayList<>();
        Gson gson = new Gson();
        JsonArray avioniJson = jsonObject.get("odgovor").getAsJsonArray();
        avioniJson.forEach(avionJsonElement -> {
            MojAvionLeti avion = gson.fromJson(avionJsonElement, MojAvionLeti.class);
            avioni.add(avion);
        });
        return avioni;
    }
    
}
