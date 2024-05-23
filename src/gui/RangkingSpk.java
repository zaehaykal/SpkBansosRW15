/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import database.koneksi;
import popUp.popUpWarga;
import gui.DataWarga;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.text.DecimalFormat;

/**
 *
 * @author zaeha
 */
public class RangkingSpk extends javax.swing.JFrame {
private DefaultTableModel tabmode;
private Connection conn = new koneksi().kon();

    /**
     * Creates new form RangkingSpk
     */

    public RangkingSpk() {
        initComponents();
        dataTable();
        autoID();
        setLocationRelativeTo(this);
    }

    protected void autoID() {
        if (lblAutoId == null) {
            JOptionPane.showMessageDialog(null, "Label AutoId belum diinisialisasi.");
            return;
        }

        try {
            Statement stat = conn.createStatement();
        String sql = "SELECT id_penilaian FROM penilaianspk ORDER BY id_penilaian DESC LIMIT 1"; // Mengambil ID terbesar
        ResultSet res = stat.executeQuery(sql);

        String newId = "SPK0001"; // Default ID jika tabel kosong

        if (res.next()) {
            int id_penilaian = res.getInt("id_penilaian"); // Mengambil ID penilaian terbesar
            int AN = id_penilaian + 1;
            String NOL = "";

            if (AN < 10) {
                NOL = "000";
            } else if (AN < 100) {
                NOL = "00";
            } else if (AN < 1000) {
                NOL = "0";
            }

            newId = "SPK" + NOL + AN;
        }

            lblAutoId.setText(newId); // Set new ID to the label
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Auto Number Gagal: " + e.getMessage());
            e.printStackTrace(); // Menampilkan stack trace untuk debugging lebih lanjut
            }
    }

    
    public void getDataWarga(String no_ktp, String nama){
        try {
            lblAutoId.setText(no_ktp);
            tabmode.addRow(new Object[] {no_ktp,nama});
            tblSPK.setModel(tabmode);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Data Gagal Diambil!");
            System.out.println(e);
        }
    }
    
        protected void dataTable(){
        Object[] Baris = {"NIK","Nama","C1","C2","C3","C4","C5"};
        tabmode = new DefaultTableModel(null, Baris);
        tblSPK.setModel(tabmode);
    }

    
    protected void hapusRow(){
        int index = tblSPK.getSelectedRow();
        tabmode.removeRow(index);
        tblSPK.setModel(tabmode);
        
    }
    
//    protected void penilaianSPK (){
//        try {
//        // SQL query
//        String penilaianSql = "INSERT INTO penilaian VALUES (?, ?, ?, ?, ?, ?)";
//        PreparedStatement stat2 = conn.prepareStatement(penilaianSql);
//
//        // Get the row count of the table
//        int t = tblSPK.getRowCount();
//
//        // Loop through each row
//        for (int i = 0; i < t; i++) {
//            String c1 = tblSPK.getValueAt(i, 2).toString();
//            String c2 = tblSPK.getValueAt(i, 3).toString();
//            String c3 = tblSPK.getValueAt(i, 4).toString();
//            String c4 = tblSPK.getValueAt(i, 5).toString();
//            String c5 = tblSPK.getValueAt(i, 6).toString();
//            for (int j = 0; j < c1.length(); j++) {
//                //chat gpt tolong kau buat disini
//            }
//
//            // Check if the values are empty
//            if (c1.toString().isEmpty() || c2.toString().isEmpty() || c3.toString().isEmpty() || c4.toString().isEmpty() || c5.toString().isEmpty()) {
//                System.out.println("Masukan Semua Data!");
//                for (int j = 0; j < i; j++) {
//                    System.out.println("nilai C1" + c1);
//                    System.out.println("nilai C2" + c2);
//                    System.out.println("nilai C3" + c3);
//                    System.out.println("nilai C4" + c4);
//                    System.out.println("nilai C5" + c5);
//                }
//            } else {
//                stat2.setString(1, c1);
//                stat2.setString(2, c2);
//                stat2.setString(3, c3);
//                stat2.setString(4, c4);
//                stat2.setString(5, c5);
//
//                // Execute the query
//                stat2.executeUpdate();
//
//                // Display success message
//                JOptionPane.showMessageDialog(null, "Berhasil");
//            }
//        }
//    } catch (SQLException e) {
//        // Handle SQL exceptions
//        JOptionPane.showMessageDialog(null, "Gagal: " + e.getMessage());
//    } catch (Exception e) {
//        // Handle other exceptions
//        JOptionPane.showMessageDialog(null, "Gagal: " + e.getMessage());
//    }
//    }
    protected void penilaianSPK() {
    try {
        // SQL query
        String penilaianSql = "INSERT INTO penilaian VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement stat2 = conn.prepareStatement(penilaianSql);

        // Get the row count of the table
        int t = tblSPK.getRowCount();
        
        double sumC1= 0, sumC2 = 0, sumC3 = 0, sumC4 = 0, sumC5 = 0;
        // Loop through each row
        for (int i = 0; i < t; i++) {
            // Get values from the table and convert them to integers
            int c1 = Integer.parseInt(tblSPK.getValueAt(i, 2).toString());
            int c2 = Integer.parseInt(tblSPK.getValueAt(i, 3).toString());
            int c3 = Integer.parseInt(tblSPK.getValueAt(i, 4).toString());
            int c4 = Integer.parseInt(tblSPK.getValueAt(i, 5).toString());
            int c5 = Integer.parseInt(tblSPK.getValueAt(i, 6).toString());
            sumC1 += Math.pow(c1, 2);
            sumC2 += Math.pow(c2, 2);
            sumC3 += Math.pow(c3, 2);
            sumC4 += Math.pow(c4, 2);
            sumC5 += Math.pow(c5, 2);
            
            DecimalFormat df = new DecimalFormat("#.#####");
            
            double pembagiC1 = Math.sqrt(sumC1);
            double pembagiC2 = Math.sqrt(sumC2);
            double pembagiC3 = Math.sqrt(sumC3);
            double pembagiC4 = Math.sqrt(sumC4);
            double pembagiC5 = Math.sqrt(sumC5);

            // Set the pembagi value in tfCovba
            // Set the pembagi value in corresponding tfCovba
            tfCovba1.setText(df.format(pembagiC1));
            tfCovba2.setText(df.format(pembagiC2));
            tfCovba3.setText(df.format(pembagiC3));
            tfCovba4.setText(df.format(pembagiC4));
            tfCovba5.setText(df.format(pembagiC5));

            // Output pembagi
            System.out.println("Pembagi C1: " + pembagiC1);
            System.out.println("Pembagi C2: " + pembagiC2);
            System.out.println("Pembagi C3: " + pembagiC3);
            System.out.println("Pembagi C4: " + pembagiC4);
            System.out.println("Pembagi C5: " + pembagiC5);
            
            
            
            // Check if the values are zero (assuming empty means zero here)
            if (c1 == 0 || c2 == 0 || c3 == 0 || c4 == 0 || c5 == 0) {
                System.out.println("Masukan Semua Data!");
                for (int j = 0; j < i; j++) {
                    System.out.println("nilai C1: " + c1);
                    System.out.println("nilai C2: " + c2);
                    System.out.println("nilai C3: " + c3);
                    System.out.println("nilai C4: " + c4);
                    System.out.println("nilai C4: " + c5);
                }
            } else {
                // Set the values into the prepared statement
//                stat2.setInt(1, c1);
//                stat2.setInt(2, c2);
//                stat2.setInt(3, c3);
//                stat2.setInt(4, c4);
//                stat2.setInt(5, c5);
//
//                // Execute the query
//                stat2.executeUpdate();
            }
        }

        // Display success message
        JOptionPane.showMessageDialog(null, "Berhasil");
    } catch (SQLException e) {
        // Handle SQL exceptions
        JOptionPane.showMessageDialog(null, "Gagal: " + e.getMessage());
    } catch (NumberFormatException e) {
        // Handle number format exceptions
        JOptionPane.showMessageDialog(null, "Format angka tidak valid: " + e.getMessage());
    } catch (Exception e) {
        // Handle other exceptions
        JOptionPane.showMessageDialog(null, "Gagal: " + e.getMessage());
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

        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblAutoId = new javax.swing.JLabel();
        btnGetID = new javax.swing.JButton();
        tfCovba = new javax.swing.JTextField();
        tfCovba1 = new javax.swing.JTextField();
        tfCovba2 = new javax.swing.JTextField();
        tfCovba3 = new javax.swing.JTextField();
        tfCovba4 = new javax.swing.JTextField();
        tfCovba5 = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSPK = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        tfCari = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Data", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tw Cen MT", 1, 16))); // NOI18N
        jPanel2.setForeground(new java.awt.Color(255, 255, 255));

        lblAutoId.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        lblAutoId.setText("jLabel1");

        btnGetID.setBackground(new java.awt.Color(204, 255, 255));
        btnGetID.setText("Get ID");
        btnGetID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGetIDActionPerformed(evt);
            }
        });

        tfCovba.setText("jTextField1");

        tfCovba1.setText("jTextField1");

        tfCovba2.setText("jTextField1");

        tfCovba3.setText("jTextField1");

        tfCovba4.setText("jTextField1");

        tfCovba5.setText("jTextField1");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(lblAutoId, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                .addComponent(btnGetID)
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tfCovba5, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfCovba4, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfCovba3, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfCovba2, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfCovba1, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfCovba, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAutoId)
                    .addComponent(btnGetID))
                .addGap(77, 77, 77)
                .addComponent(tfCovba, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tfCovba1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tfCovba2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tfCovba3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tfCovba4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tfCovba5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(143, Short.MAX_VALUE))
        );

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Alternatif", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tw Cen MT", 1, 16))); // NOI18N
        jPanel1.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N

        jButton1.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jButton1.setText("Cari");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        tblSPK.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, null, null, java.awt.Color.gray));
        tblSPK.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblSPK);

        jScrollPane2.setViewportView(jScrollPane1);

        jButton3.setBackground(new java.awt.Color(0, 204, 204));
        jButton3.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jButton3.setText("Penilaian");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(255, 204, 204));
        jButton4.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jButton4.setText("Hapus");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 639, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(tfCari, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(44, 44, 44)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(tfCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 486, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Tw Cen MT", 0, 48)); // NOI18N
        jLabel1.setText("BANTUAN SOSIAL RW 15");

        jButton2.setBackground(new java.awt.Color(255, 153, 153));
        jButton2.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jButton2.setText("Exit");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(35, 35, 35)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel1)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(449, 449, 449)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(53, 53, 53)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        popUpWarga pWarga = new popUpWarga();
        pWarga.rangSPK =this;
        pWarga.setVisible(true);
        pWarga.setResizable(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnGetIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetIDActionPerformed
        // TODO add your handling code here:
        autoID();
    }//GEN-LAST:event_btnGetIDActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        penilaianSPK();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        hapusRow();
    }//GEN-LAST:event_jButton4ActionPerformed

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
            java.util.logging.Logger.getLogger(RangkingSpk.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RangkingSpk.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RangkingSpk.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RangkingSpk.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RangkingSpk().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGetID;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblAutoId;
    private javax.swing.JTable tblSPK;
    private javax.swing.JTextField tfCari;
    private javax.swing.JTextField tfCovba;
    private javax.swing.JTextField tfCovba1;
    private javax.swing.JTextField tfCovba2;
    private javax.swing.JTextField tfCovba3;
    private javax.swing.JTextField tfCovba4;
    private javax.swing.JTextField tfCovba5;
    // End of variables declaration//GEN-END:variables
}
