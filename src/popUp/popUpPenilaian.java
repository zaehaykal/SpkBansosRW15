/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package popUp;

import gui.RangkingSpkPage;
import database.koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;
import gui.RangkingSpkPage;
import java.util.Arrays;
import javax.swing.JOptionPane;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author zaeha
 */
public class popUpPenilaian extends javax.swing.JFrame {

    public RangkingSpkPage penilaianPublik;
    private double[] nilaiVPublik;
    private String[] namaPublik;
    private String[] noKtpPublik;
    private DefaultTableModel tabmode;
    PreparedStatement ps;

    public void setNilai(double[] nilai, String[] nama, String[] alternatif) {
        System.out.println("nilai terima :" + Arrays.toString(nilai));
        this.nilaiVPublik = nilai;
        this.namaPublik = alternatif;
        this.noKtpPublik = nama;
        System.out.println("nilai alternatif :" + Arrays.toString(alternatif));
        System.out.println("nilai karyawan :" + Arrays.toString(noKtpPublik));
        System.out.println("panjang :" + nilaiVPublik.length);
        setTable();
    }
    Connection conn;

    public popUpPenilaian() {
        conn = koneksi.kon();
        tabmode = new DefaultTableModel();
        setLocationRelativeTo(this);
        initComponents();
        tabel();
    }
    
    private void tabel() {
        tabmode.addColumn("Nama Warga");
        tabmode.addColumn("NIK");
        tabmode.addColumn("Nilai Preferensi");
        tabmode.addColumn("Rangking");
        jTable1.setModel(tabmode);
    
    }
    public void setTable() {

        if (noKtpPublik == null || noKtpPublik.length == 0) {
            System.out.println("Alternatif belum diinisialisasi atau tidak memiliki data.");
            return;
        }

        int[] rank = getRanks(nilaiVPublik);
        for (int i = 0; i < nilaiVPublik.length; i++) {
            Object[] obj = new Object[6];
            if (noKtpPublik[i] != null) {
                obj[0] = noKtpPublik[i];
            } else {
                obj[0] = "";
            }
            obj[1] = namaPublik[i];
            obj[2] = nilaiVPublik[i];
            obj[3] = rank[i];
            System.out.println("Nama Nilai "+ (namaPublik[i])+" dengan nilai Preferensi: " + (nilaiVPublik[i]));
            tabmode.addRow(obj);
        }
    }

    public static int[] getRanks(double[] values) {
        int n = values.length;
        double[] sortedValues = values.clone();
        Arrays.sort(sortedValues);

        Map<Double, Integer> valueToRank = new HashMap<>();
        for (int i = 0; i < n; i++) {
            valueToRank.put(sortedValues[i], n - i);
        }

        int[] ranks = new int[n];
        for (int i = 0; i < n; i++) {
            ranks[i] = valueToRank.get(values[i]);
        }

        return ranks;
    }
    
    
    public int getMaxId() {
        int max = 0;
        try {
            Statement stat = (Statement) koneksi.kon().createStatement();
            String sql = "SELECT MAX(id) as id FROM penilaian";
            ResultSet res = stat.executeQuery(sql);

            while (res.next()) {
                max = res.getInt("id");
            }
        } catch (SQLException err) {
            JOptionPane.showMessageDialog(null, err.getMessage());
        }

        return max;
    }

    public void insertData() {
        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedNow = now.format(formatter);

        String sql = "INSERT INTO hasil (tanggal) VALUES (?)";
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, formattedNow);
            int i = ps.executeUpdate();
            if (i > 0) {
                int[] rank = getRanks(nilaiVPublik);
                for (int j = 0; j < nilaiVPublik.length; j++) {
                    String sqli = "INSERT INTO hasil_rangking (id_rangking, id_hasil, no_ktp, nama, nilaiV, ranking) VALUES (?,?,?,?,?,?)";

                    PreparedStatement psi = conn.prepareStatement(sqli);
                    psi.setInt(1, getMaxId());
                    System.out.println("max id nya adalah :" + getMaxId());
                    psi.setString(2, noKtpPublik[j]);
                    psi.setString(2, namaPublik[j]);
                    psi.setDouble(3, nilaiVPublik[j]);
                    psi.setDouble(4, rank[j]);
                    int k = psi.executeUpdate();

                    if (k > 0) {
                        System.out.println("Nilai " + j + " Berhasil di input");
                    }
                }

                if (0 == 0) {
                    JOptionPane.showMessageDialog(null, "Berhasil input data");
                } else {
                    JOptionPane.showMessageDialog(null, "Gagal input data Sub");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Gagal input data");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            System.out.println(e);
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

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 204, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Hasil Rangking", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tw Cen MT", 1, 16))); // NOI18N

        jTable1.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTable1);

        jButton1.setBackground(new java.awt.Color(204, 255, 204));
        jButton1.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        jButton1.setText("Simpan");
        jButton1.setMaximumSize(new java.awt.Dimension(150, 50));
        jButton1.setMinimumSize(new java.awt.Dimension(150, 50));
        jButton1.setPreferredSize(new java.awt.Dimension(150, 50));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(374, 374, 374)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(335, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(28, 28, 28))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        insertData();
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(popUpPenilaian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(popUpPenilaian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(popUpPenilaian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(popUpPenilaian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new popUpPenilaian().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
