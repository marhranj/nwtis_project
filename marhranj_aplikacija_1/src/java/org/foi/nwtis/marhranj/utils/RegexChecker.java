/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author marhranj
 */
public final class RegexChecker {
    
    private static final String REGEX_ZA_AUTENTIKACIJU = "^KORISNIK ([a-zA-Z0-9_-]+); LOZINKA ([a-zA-Z0-9_-]+);";
    private static final String REGEX_ZA_SERVER = "^KORISNIK ([a-zA-Z0-9_-]+); LOZINKA ([a-zA-Z0-9_-]+); (PAUZA;|KRENI;|PASIVNO;|AKTIVNO;|STANI;|STANJE;)";
    private static final String REGEX_ZA_GRUPU = "^KORISNIK ([a-zA-Z0-9_-]+); LOZINKA ([a-zA-Z0-9_-]+); GRUPA (DODAJ;|PREKID;|KRENI;|PAUZA;|STANJE;)";
    
    private RegexChecker() {
    
    }
    
    public static Matcher dajMatcherZaAutentikaciju(String izraz) {
        return kreirajOdgovarajuciMatcher(izraz, REGEX_ZA_AUTENTIKACIJU);
    }
    
    public static Matcher dajMatcherZaServer(String izraz) {
        return kreirajOdgovarajuciMatcher(izraz, REGEX_ZA_SERVER);
    }
    
    public static Matcher dajMatcherZaGrupu(String izraz) {
        return kreirajOdgovarajuciMatcher(izraz, REGEX_ZA_GRUPU);
    }
    
    private static Matcher kreirajOdgovarajuciMatcher(String izraz, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(izraz);
    }
    
}
