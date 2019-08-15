/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.marhranj.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marhranj
 */
public class PretvaracVremena {
    
    public static Timestamp pretvoriStringUTimeStamp(String stringDatum) {
        try {
          DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          Date date = formatter.parse(stringDatum);
          Timestamp timeStampDate = new Timestamp(date.getTime());

          return timeStampDate;
        } catch (ParseException ex) {
          Logger.getLogger(PretvaracVremena.class.getName()).log(Level.SEVERE, null, ex);
          return null;
        }
  }
    
}
