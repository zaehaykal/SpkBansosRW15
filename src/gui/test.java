/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;
import database.koneksi;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.*;
import java.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import org.codehaus.groovy.runtime.ArrayUtil;

import java.util.List;

/**
 *
 * @author zaeha
 */
public class test extends javax.swing.JFrame {
    private DefaultTableModel tabmode;
    private Connection conn = new koneksi().kon();
    
    /**
     * Creates new form test
     */
    public test() {
        initComponents();
//        getData();
        dataTable();
//        getNamaWarga();
    }
    public List<String> getNamaWarga() {
        List<String> namaWargaList = new ArrayList<>();
        
        try {
            String isiTeks = tfNik.getText();
        String sql = "SELECT * from warga where no_ktp like '%"+isiTeks+"%' "
                + "or nama like '%"+isiTeks+"%' order by no_ktp asc";
            Statement st = conn.createStatement();
            ResultSet res = st.executeQuery(sql);
            while (res.next()) {
                String nama = res.getString("nama");
                namaWargaList.add(nama);
                Object [] wargangeList = {namaWargaList};
                tabmode.addColumn(conn, wargangeList);
                tabmode = new DefaultTableModel(null,wargangeList);
                while (res.next()) {
                tabmode.addRow(new Object[]{
                    res.getString(1),
                    });
        }

            }
            tblWarga.setModel(tabmode);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"data gagal "+e);
        } 
        return namaWargaList;
    }

//    protected void getData(){
//            try {
//           String sql = "SELECT * from kriteria where id_kriteria order by id_kriteria ";
//        Statement stat = conn.createStatement();
//        ResultSet result = stat.executeQuery(sql);
//        while (result.next()) {
//                Object [] obj = new Object[4];
//                obj[1] = result.getString("Id_kriteria");
//                obj[2] = result.getString("Id_kriteria");
//                obj[3] = result.getString("Id_kriteria");
//                obj[4] = result.getString("Id_kriteria");
//            }
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(rootPane, e);
//        }
//    }
    
    
    
    
//    protected void dataTable (){
//        try {
//            String sqlKriteria = "SELECT * from kriteria where id_kriteria like or nama_kriteria like order by id_kriteria asc";
//            Statement st = conn.createStatement();
//            ResultSet rs = st.executeQuery(sqlKriteria);
//            while (rs.next()) {
//                String x1 = rs.getString(1);
//                String x2 = rs.getString(2);
//            }
//            
//        } catch (Exception e) {
//        }
//    Object [] Baris = {"Nama", "C1", "C2", "C3"};
////    String namaKriteria = "SELECT * from kriteria where id_kriteria or nama_kriteria order by id_kriteria asc";
//    tabmode = new DefaultTableModel(null, Baris);
//    try {
//        String isiTeks = tfNik.getText();
//        String sql = "SELECT * from kriteria where id_kriteria like '%"+isiTeks+"%' or nama_kriteria like '%"+isiTeks+"%' order by id_kriteria asc";
//        Statement stat = conn.createStatement();
//        ResultSet result = stat.executeQuery(sql);
//        while (result.next()) {
//        tabmode.addRow(new Object[]{
//            result.getString(1),
//        });
//        
//        }
//        tblWarga.setModel(tabmode);
//    } catch (Exception e) {
//        JOptionPane.showMessageDialog(null,"Data Gagal Diambil"+e);
//        System.out.println(""+e);
//    }
//}
    
    
    protected void dataTable (){
//        try {
//            String sqlKriteria = "SELECT id_kriteria from kriteria ";
//            Statement st = conn.createStatement();
//            ResultSet rs = st.executeQuery(sqlKriteria);
//                        
//            while (rs.next()) {
//                String x1 = rs.getString(1);
//                String x2 = rs.getString(2);
//                String x3 = rs.getString(3);
//                Object [] obj = {x1,x2,x3};
//                tabmode = new DefaultTableModel(null,obj);
//            }
//            
//            
//        } catch (Exception e) {
//            System.out.println("gagal"+e);
//        }
            

    
    try {
        Object [] Baris = {"Nama", "C1", "C2", "C3", "C4", "C5"};
        tabmode = new DefaultTableModel(null, Baris);
        String isiTeks = tfNik.getText();
        String sql = "SELECT * from warga where no_ktp like '%"+isiTeks+"%' or nama like '%"+isiTeks+"%' order by no_ktp asc";
        Statement stat = conn.createStatement();
        ResultSet result = stat.executeQuery(sql);
        while (result.next()) {
            int nama = result.getInt("no_ktp");
            System.out.println(nama);
        tabmode.addRow(new Object[]{
            
            result.getInt(1),
            
            });
        }
        tblWarga.setModel(tabmode);
    } catch (SQLException e) {
        System.out.println(""+e);
    }
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tfNik = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblWarga = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tfNik.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfNikActionPerformed(evt);
            }
        });

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        tblWarga.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblWarga.setToolTipText("");
        tblWarga.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblWargaKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(tblWarga);

        jScrollPane2.setViewportView(jScrollPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 659, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(81, 81, 81)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addComponent(tfNik, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(971, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 569, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(88, 88, 88)
                        .addComponent(tfNik, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(85, 85, 85)
                        .addComponent(jButton1)))
                .addContainerGap(563, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
        // SQL query
        String penilaianSql = "INSERT INTO kriteria VALUES (?, ?, ?)";
        PreparedStatement stat2 = conn.prepareStatement(penilaianSql);

        // Get the row count of the table
        int t = tblWarga.getRowCount();

        // Loop through each row
        for (int i = 0; i < t; i++) {
            String c1 = tblWarga.getValueAt(i, 2).toString();
            String c2 = tblWarga.getValueAt(i, 3).toString();
            String c3 = tblWarga.getValueAt(i, 4).toString();
            String c4 = tblWarga.getValueAt(i, 5).toString();
            String c5 = tblWarga.getValueAt(i, 6).toString();

            // Check if the values are empty
            if (c1.toString().isEmpty() || c2.toString().isEmpty() || c3.toString().isEmpty() || c4.toString().isEmpty() || c5.toString().isEmpty()) {
                System.out.println("Masukan Semua Data!");
                for (int j = 0; j < i; j++) {
                    System.out.println("nilai C1" + c1);
                    System.out.println("nilai C2" + c2);
                    System.out.println("nilai C3" + c3);
                    System.out.println("nilai C4" + c4);
                    System.out.println("nilai C5" + c5);
                }
            } else {
                stat2.setString(1, c1);
                stat2.setString(2, c2);
                stat2.setString(3, c3);
                stat2.setString(4, c4);
                stat2.setString(5, c5);

                // Execute the query
                stat2.executeUpdate();

                // Display success message
                JOptionPane.showMessageDialog(null, "Berhasil");
            }
        }
    } catch (SQLException e) {
        // Handle SQL exceptions
        JOptionPane.showMessageDialog(null, "Gagal: " + e.getMessage());
    } catch (Exception e) {
        // Handle other exceptions
        JOptionPane.showMessageDialog(null, "Gagal: " + e.getMessage());
    }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tblWargaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblWargaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tblWargaKeyPressed

    private void tfNikActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfNikActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfNikActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(test.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(test.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(test.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(test.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new test().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblWarga;
    private javax.swing.JTextField tfNik;
    // End of variables declaration//GEN-END:variables
}
