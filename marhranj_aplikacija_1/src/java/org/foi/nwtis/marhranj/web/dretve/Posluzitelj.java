package org.foi.nwtis.marhranj.web.dretve;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.marhranj.konfiguracije.GeneralnaKonfiguracija;
import org.foi.nwtis.marhranj.web.slusaci.SlusacAplikacije;

public class Posluzitelj extends Thread {
    
    private ServerSocket serverSocket;
    private GeneralnaKonfiguracija konfiguracija;
    private int port;
    private int maksCekaca;
    
    public Posluzitelj(GeneralnaKonfiguracija konfiguracija) {
        this.konfiguracija = konfiguracija;
        this.port = konfiguracija.getPort();
        this.maksCekaca = konfiguracija.getMaksCekaca();
        try {
            serverSocket = new ServerSocket(port, maksCekaca);
        } catch (IOException ex) {
            Logger.getLogger(Posluzitelj.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void interrupt() {
        try {
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(Posluzitelj.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.interrupt(); 
    }

    @Override
    public void run() {
        super.run();
        try {
            Socket socket = null;
            while (!SlusacAplikacije.getStopirano()) {
                socket = serverSocket.accept();
                if (SlusacAplikacije.getStopirano()) {
                    System.out.println("Server je stopirao sa radom!");
                } else {
                    System.out.println("Korisnik se spojio");
                    RadnaDretva radnaDretva = new RadnaDretva(konfiguracija, socket);
                    radnaDretva.start();
                    radnaDretva.interrupt();
                }
            }
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Posluzitelj.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public synchronized void start() {
        super.start();
        System.out.println("Poslužitelj je započeo rad.");

    }
    
}