package org.foi.nwtis.marhranj.web.dretve;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.marhranj.konfiguracije.GeneralnaKonfiguracija;
import org.foi.nwtis.marhranj.web.slusaci.SlusacAplikacije;

public class Server extends Thread {
    
    private RadnaDretva radnaDretva;
    
    private final GeneralnaKonfiguracija konfiguracija;
    private final int port;
    private final int maksCekaca;
    
    private ServerSocket serverSocket;
    
    public Server(GeneralnaKonfiguracija konfiguracija) {
        this.konfiguracija = konfiguracija;
        this.port = konfiguracija.getPort();
        this.maksCekaca = konfiguracija.getMaksCekaca();
    }

    @Override
    public void run() {
        try {
            if (!portJeZauzet(port, "localhost")) {
                serverSocket = new ServerSocket(port, maksCekaca);
                Socket klijentSocket = null;
                while (!SlusacAplikacije.getZaustavljeno()) {
                    klijentSocket = serverSocket.accept();
                    if (!SlusacAplikacije.getZaustavljeno()) {
                        radnaDretva = new RadnaDretva(konfiguracija, klijentSocket);
                        radnaDretva.start(); 
                    }                
                }
                if (klijentSocket != null) {
                    klijentSocket.close();
                }
                serverSocket.close();
            }            
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean portJeZauzet(int port, String imePosluzitelja) {
        try {
            Socket s = new Socket(imePosluzitelja, port);
            s.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    @Override
    public void interrupt() {
        super.interrupt();
        if (Objects.nonNull(radnaDretva)) {
            radnaDretva.interrupt();
        }
        try {
            serverSocket.close();
        } catch (Exception e) {
             Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, e);
        } 
    }
    
}