/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.*;
import java.sql.Statement;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

/**
 *
 * @author zaeha
 */
public class koneksi {

    public static Connection kon() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection konek = DriverManager.getConnection("jdbc:mysql://localhost/rw15", "root", "");
            return konek;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            return null;
        }
    }
}
