package org.foi.nwtis.marhranj;

import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.marhranj.web.KonektorBazePodataka;

public class PisacDnevnika {

    public void upisUDnevnik(String korisnik, String naredba, String vrsta, String url, String ip, long trajanje) {
        try (Connection con = KonektorBazePodataka.dajKonekciju();
                PreparedStatement stmt = con.prepareStatement("INSERT INTO DNEVNIK (KORISNIK, VRIJEME, NAREDBA, URL, IP, VRSTA, TRAJANJE) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)");) {
            stmt.setString(1, korisnik);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setString(3, naredba);
            stmt.setString(4, url);
            stmt.setString(5, ip);
            stmt.setString(6, vrsta);
            stmt.setLong(7, trajanje);
            stmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
