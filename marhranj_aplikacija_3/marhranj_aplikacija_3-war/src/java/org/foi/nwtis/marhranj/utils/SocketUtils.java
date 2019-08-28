/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marhranj
 */
public final class SocketUtils {
    
    public static String posaljiPorukuNaSocket(String poruka, int portSocketa) {
        try {
            Socket socket = new Socket("localhost", portSocketa);
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(poruka.getBytes());
            outputStream.flush();
            socket.shutdownOutput();
            return pretvoriStreamUString(inputStream);
        } catch (IOException ex) {
            Logger.getLogger(SocketUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    private static String pretvoriStreamUString(InputStream is) {
        Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
    
}
