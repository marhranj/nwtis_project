package org.foi.nwtis.marhranj;

import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.marhranj.web.KonektorBazePodataka;

public class PisacDnevnika {

    private static long vrijemePrijema;
    
    public void upisUDnevnik(String korisnik, String radnja, String vrsta, Socket socket) {
        long trajanjeObrade = (long) System.currentTimeMillis() - vrijemePrijema;

        String upit = "INSERT INTO dnevnik (korisnik, url, ipAdresa, trajanjeObrade, vrijemePrijema, radnja, vrsta)"
                + " VALUES ('?, ?, ?, ?, ?, ?, ?, ?)";
         
        try (Connection con = KonektorBazePodataka.dajKonekciju();
                PreparedStatement stmt = con.prepareStatement(upit);) {
            stmt.setString(0, korisnik);
            stmt.setString(1, socket.getInetAddress().getHostAddress());
            stmt.setString(2, socket.getInetAddress().toString());
            stmt.setLong(3, trajanjeObrade);
            stmt.setLong(4, vrijemePrijema);
            stmt.setString(5, radnja);
            stmt.setString(6, vrsta);
            stmt.executeUpdate(upit);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void postaviPocetnoVrijeme() {
        vrijemePrijema = (long) System.currentTimeMillis();
    }
    
}
