/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import data.LoginData;
import java.text.SimpleDateFormat;
import database.koneksi;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.JOptionPane;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.util.Date;
import java.io.File;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zaeha
 */
public class DataWargaPage extends javax.swing.JFrame {

    private Connection conn = new koneksi().kon();
    private DefaultTableModel tabmode;
    LoginData Id = new LoginData();

    /**
     * Creates new form DataWarga
     */
    public DataWargaPage() {
        initComponents();
        dataTable();
        letakKursor();
        fieldKosong();
        setLocationRelativeTo(this);
    }

    public static java.sql.Date convertUtilDateToSqlDate(java.util.Date date) {
        if (date != null) {
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            return sqlDate;
        }
        return null;
    }

    protected void fieldKosong() {
        tfNik.setText("");
        tfCari.setText("");
        tfHP.setText("");
        tfaAlamat.setText("");
        jDateChooser1.setDate(null);
        cbAgama.setSelectedIndex(0);
        cbRT.setSelectedIndex(0);
        tfNama.setText("");
        tfTLahir.setText("");
    }

    protected void letakKursor() {
        tfNik.requestFocus();
    }

    private void updateData() {
        String sql = "UPDATE warga set nama = ?,agama = ?,no_hp = ?, jenis_kelamin = ?, tanggal_lahir = ?,rt = ?, alamat = ? where no_ktp= '" + tfNik.getText() + "'";
        try {
            PreparedStatement stat = conn.prepareStatement(sql);
            stat.setString(1, tfNama.getText());
            stat.setString(2, cbAgama.getSelectedItem().toString());
            stat.setString(3, tfHP.getText());
            String jenisKelamin = "";
            if (rbtnL.isSelected()) {
                jenisKelamin = "L";
            } else {
                jenisKelamin = "P";
            }
            stat.setString(4, jenisKelamin);
            stat.setDate(5, convertUtilDateToSqlDate(jDateChooser1.getDate()));
            stat.setString(6, cbRT.getSelectedItem().toString());
            stat.setString(7, tfaAlamat.getText());

            JOptionPane.showMessageDialog(null, "Data Berhasil Diubah!");

            stat.executeUpdate();
            fieldKosong();
            letakKursor();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
            System.out.println(e);
        }
        dataTable();
    }

    private void insertData() {
        if (tfNama.getText().equals("") || tfHP.getText().equals("") || (!rbtnL.isSelected() && !rbtnP.isSelected()) || tfaAlamat.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Harap masukan semua data");
        } else if (tfNik.getText().length() != 16) {
            JOptionPane.showMessageDialog(null, "Panjang NIK harus 16");
        } else {
            try {
                // Check if the NIK already exists
                String checkSql = "SELECT COUNT(*) FROM warga WHERE no_ktp = ?";
                PreparedStatement checkStat = conn.prepareStatement(checkSql);
                checkStat.setLong(1, Long.parseLong(tfNik.getText()));
                ResultSet rs = checkStat.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(null, "NIK sudah ada, masukkan NIK yang berbeda");
                    return;
                }

                // If NIK does not exist, proceed with insertion
                String sql = "INSERT INTO warga(nama, agama, no_hp, jenis_kelamin, t_lahir, tanggal_lahir, rt, alamat, no_ktp) VALUES (?,?,?,?,?,?,?,?,?)";
                PreparedStatement stat = conn.prepareStatement(sql);
                stat.setString(1, tfNama.getText());
                stat.setString(2, cbAgama.getSelectedItem().toString());
                try {
                    stat.setLong(3, Long.parseLong(tfHP.getText()));
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Nomor HP harus berupa angka!");
                }
                String jenisKelamin = rbtnL.isSelected() ? "L" : "P";
                stat.setString(4, jenisKelamin);
                stat.setString(5, tfTLahir.getText());
                stat.setDate(6, convertUtilDateToSqlDate(jDateChooser1.getDate()));
                stat.setString(7, cbRT.getSelectedItem().toString());
                stat.setString(8, tfaAlamat.getText());
                stat.setLong(9, Long.parseLong(tfNik.getText()));
                int i = stat.executeUpdate();
                if (i > 0) {
                    JOptionPane.showMessageDialog(null, "Berhasil");
                    dataTable();
                } else {
                    JOptionPane.showMessageDialog(null, "Gagal");
                }
                fieldKosong();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Data Gagal Disimpan!");
                System.out.println(e);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "NIK harus berupa angka");
                System.out.println(e);
            }
        }
    }

    private void deleteData() {
        if (tfNama.getText().trim().isEmpty() || tfNik.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Data tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int option = JOptionPane.showConfirmDialog(null, "Yakin ?", "Hapus '" + tfNama.getText() + "' ", JOptionPane.YES_NO_OPTION);
        if (option == 0) {
            String sql = "DELETE FROM warga WHERE no_ktp = ?";
            try {
                PreparedStatement stat = conn.prepareStatement(sql);
                stat.setString(1, tfNik.getText().trim());
                stat.executeUpdate();
                fieldKosong();
                letakKursor();
                JOptionPane.showMessageDialog(null, "Data Berhasil Dihapus!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            dataTable();
        }
    }

    protected void dataTable() {
        Object[] Baris = {"NIK", "Nama", "Agama", "NoHP", " Jenkel", "TTL", "Tempat Lahir", "RT", "Alamat"};
        tabmode = new DefaultTableModel(null, Baris);
        try {
            String isiTeks = tfCari.getText();
            String sql = "SELECT * from warga where no_ktp like '%" + isiTeks + "%' or nama like '%" + isiTeks + "%' order by no_ktp asc";
            Statement stat = conn.createStatement();
            ResultSet result = stat.executeQuery(sql);
            while (result.next()) {
                tabmode.addRow(new Object[]{
                    result.getString(1),
                    result.getString(2),
                    result.getString(3),
                    result.getString(4),
                    result.getString(5),
                    result.getString(6),
                    result.getString(7),
                    result.getString(8),
                    result.getString(9)
                });
            }
            tblWarga.setModel(tabmode);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Data Gagal Diambil" + e);
            System.out.println("" + e);
        }
    }

    private void cetakData() {
        String reportSrcFile = "src/laporan/LapDataWarga.jrxml"; // Path ke file jrxml Anda
        String userHome = System.getProperty("user.home"); // Mendapatkan direktori home pengguna
        String reportDestDir = userHome + "\\Downloads\\";  // Directory untuk output file PDF
        String baseFileName = "LaporanDataWargaRW15";
        String fileExtension = ".pdf";

        // Mengambil nilai dari tfNik
        String nik = tfNik.getText();
        if (nik == null || nik.trim().isEmpty()) {
            nik = null; // Atur parameter menjadi null jika kosong
        }

        try {
            Connection conn = koneksi.kon();

            JasperReport jasperReport = JasperCompileManager.compileReport(reportSrcFile);

            // Membuat parameter map
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("p1", nik);  // Mengatur parameter "p1"

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);

            // Cek apakah laporan memiliki halaman
            if (jasperPrint.getPages().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Tidak ada data untuk ditampilkan.");
                return;
            }

            JasperViewer.viewReport(jasperPrint, false);

            // Generate the destination file path
            String reportDestFile = generateUniqueFileName(reportDestDir, baseFileName, fileExtension);
            JasperExportManager.exportReportToPdfFile(jasperPrint, reportDestFile);

            JOptionPane.showMessageDialog(null, "Laporan berhasil dibuat di: " + reportDestFile);

        } catch (JRException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal membuat laporan: " + e.getMessage());
        }
    }

    private String generateUniqueFileName(String dir, String baseName, String extension) {
        File file = new File(dir + baseName + extension);
        int count = 1;
        while (file.exists()) {
            file = new File(dir + baseName + "(" + count + ")" + extension);
            count++;
        }
        return file.getAbsolutePath();
    }

    private void klikMouse() {
        int row = tblWarga.getSelectedRow();
//        DefaultTableModel model = (DefaultTableModel) tblWarga.getModel();

        tfNama.setText(tabmode.getValueAt(row, 1).toString());
        String aGama = tabmode.getValueAt(row, 2).toString();
        switch (aGama) {
            case "Islam":
                cbAgama.setSelectedIndex(0);
                break;
            case "Kristen":
                cbAgama.setSelectedIndex(1);
                break;
            case "Khatolik":
                cbAgama.setSelectedIndex(2);
                break;
            case "Item 4":
                cbAgama.setSelectedIndex(3);
                break;
        }
        tfHP.setText(tabmode.getValueAt(row, 3).toString());

        String jenKel = tabmode.getValueAt(row, 4).toString();
        if (jenKel.equals("L")) {
            rbtnL.setSelected(true);
        } else {
            rbtnP.setSelected(true);
        };
        tfTLahir.setText(tabmode.getValueAt(row, 5).toString());
        try {
            Date dateKlik = new SimpleDateFormat("yyyy-MM-dd").parse((String) tabmode.getValueAt(row, 6));
            jDateChooser1.setDate(dateKlik);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ga bisa masuk tanggal nya" + e);
        }
        String rT = tabmode.getValueAt(row, 7).toString();
        switch (rT) {
            case "1":
                cbRT.setSelectedIndex(0);
                break;
            case "2":
                cbRT.setSelectedIndex(1);
                break;
            case "3":
                cbRT.setSelectedIndex(2);
                break;
            case "4":
                cbRT.setSelectedIndex(3);
                break;
            case "5":
                cbRT.setSelectedIndex(3);
                break;
            case "6":
                cbRT.setSelectedIndex(3);
                break;
            case "7":
                cbRT.setSelectedIndex(3);
                break;
        }
        tfaAlamat.setText(tabmode.getValueAt(row, 8).toString());
        tfNik.setText(tabmode.getValueAt(row, 0).toString());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tfaAlamat = new javax.swing.JTextArea();
        tfNik = new javax.swing.JTextField();
        tfNama = new javax.swing.JTextField();
        tfHP = new javax.swing.JTextField();
        rbtnL = new javax.swing.JRadioButton();
        rbtnP = new javax.swing.JRadioButton();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        cbAgama = new javax.swing.JComboBox<>();
        cbRT = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        tfTLahir = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblWarga = new javax.swing.JTable();
        tfCari = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Rukun Warga 15\n");
        setMaximumSize(new java.awt.Dimension(1032, 1145));
        setMinimumSize(new java.awt.Dimension(1032, 1145));
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(1032, 1145));
        getContentPane().setLayout(null);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));
        jPanel1.setPreferredSize(new java.awt.Dimension(1032, 1145));
        jPanel1.setLayout(null);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Input Data Warga", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tw Cen MT", 1, 16))); // NOI18N
        jPanel2.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 1, 16)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 18)); // NOI18N
        jLabel1.setText("NIK");

        jLabel2.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 18)); // NOI18N
        jLabel2.setText("Nama");

        jLabel3.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 18)); // NOI18N
        jLabel3.setText("Agama");

        jLabel4.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 18)); // NOI18N
        jLabel4.setText("No HP");

        jLabel5.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 18)); // NOI18N
        jLabel5.setText("JenKel");

        jLabel6.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 18)); // NOI18N
        jLabel6.setText("Tgl Lahir");

        jLabel7.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 18)); // NOI18N
        jLabel7.setText("RT");

        jLabel8.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 18)); // NOI18N
        jLabel8.setText("Alamat");

        tfaAlamat.setBackground(new java.awt.Color(204, 231, 231));
        tfaAlamat.setColumns(20);
        tfaAlamat.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 14)); // NOI18N
        tfaAlamat.setRows(5);
        tfaAlamat.setBorder(null);
        jScrollPane1.setViewportView(tfaAlamat);

        tfNik.setBackground(new java.awt.Color(204, 231, 231));
        tfNik.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 16)); // NOI18N
        tfNik.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfNikActionPerformed(evt);
            }
        });

        tfNama.setBackground(new java.awt.Color(204, 231, 231));
        tfNama.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 16)); // NOI18N
        tfNama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfNamaActionPerformed(evt);
            }
        });

        tfHP.setBackground(new java.awt.Color(204, 231, 231));
        tfHP.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 16)); // NOI18N
        tfHP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfHPActionPerformed(evt);
            }
        });

        rbtnL.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(rbtnL);
        rbtnL.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 16)); // NOI18N
        rbtnL.setText("L");
        rbtnL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtnLActionPerformed(evt);
            }
        });

        rbtnP.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(rbtnP);
        rbtnP.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 16)); // NOI18N
        rbtnP.setText("P");
        rbtnP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtnPActionPerformed(evt);
            }
        });

        jDateChooser1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jDateChooser1PropertyChange(evt);
            }
        });

        cbAgama.setBackground(new java.awt.Color(204, 231, 231));
        cbAgama.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 16)); // NOI18N
        cbAgama.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Islam", "Kristen", "Khatolik", "Buddha", "Hindhu" }));

        cbRT.setBackground(new java.awt.Color(204, 231, 231));
        cbRT.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 16)); // NOI18N
        cbRT.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7" }));

        jLabel11.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 18)); // NOI18N
        jLabel11.setText("Tempat Lahir");

        tfTLahir.setBackground(new java.awt.Color(204, 231, 231));
        tfTLahir.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 16)); // NOI18N
        tfTLahir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfTLahirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel11)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addGap(34, 34, 34)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tfTLahir)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tfNama)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(rbtnL)
                        .addGap(18, 18, 18)
                        .addComponent(rbtnP))
                    .addComponent(cbAgama, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbRT, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfHP)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                    .addComponent(tfNik))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tfNik, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(tfNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(cbAgama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(tfHP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbtnL)
                    .addComponent(rbtnP)
                    .addComponent(jLabel5))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfTLahir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(cbRT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );

        jPanel1.add(jPanel2);
        jPanel2.setBounds(20, 140, 417, 460);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Data Warga", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tw Cen MT", 1, 16))); // NOI18N
        jPanel4.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 16)); // NOI18N

        tblWarga.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        tblWarga.setModel(new javax.swing.table.DefaultTableModel(
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
        tblWarga.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblWargaMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblWarga);

        tfCari.setBackground(new java.awt.Color(204, 231, 231));
        tfCari.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 16)); // NOI18N
        tfCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfCariActionPerformed(evt);
            }
        });

        jButton6.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 16)); // NOI18N
        jButton6.setText("Cari");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 946, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(tfCari, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel4);
        jPanel4.setBounds(18, 606, 988, 520);

        jLabel9.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 48)); // NOI18N
        jLabel9.setText("FORM DATA WARGA");
        jPanel1.add(jLabel9);
        jLabel9.setBounds(18, 49, 361, 53);

        jButton4.setBackground(new java.awt.Color(255, 102, 102));
        jButton4.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 16)); // NOI18N
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/icons8-close-32.png"))); // NOI18N
        jButton4.setMaximumSize(new java.awt.Dimension(30, 30));
        jButton4.setMinimumSize(new java.awt.Dimension(30, 30));
        jButton4.setPreferredSize(new java.awt.Dimension(30, 30));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton4);
        jButton4.setBounds(2385, 19, 30, 30);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jButton1.setBackground(new java.awt.Color(153, 204, 255));
        jButton1.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 16)); // NOI18N
        jButton1.setText("Ubah");
        jButton1.setMaximumSize(new java.awt.Dimension(150, 50));
        jButton1.setMinimumSize(new java.awt.Dimension(150, 50));
        jButton1.setPreferredSize(new java.awt.Dimension(150, 50));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton1);

        btnSimpan.setBackground(new java.awt.Color(204, 255, 204));
        btnSimpan.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 16)); // NOI18N
        btnSimpan.setText("Simpan");
        btnSimpan.setMaximumSize(new java.awt.Dimension(150, 50));
        btnSimpan.setMinimumSize(new java.awt.Dimension(150, 50));
        btnSimpan.setPreferredSize(new java.awt.Dimension(150, 50));
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        jPanel3.add(btnSimpan);

        btnReset.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 16)); // NOI18N
        btnReset.setText("Reset");
        btnReset.setMaximumSize(new java.awt.Dimension(150, 50));
        btnReset.setMinimumSize(new java.awt.Dimension(150, 50));
        btnReset.setPreferredSize(new java.awt.Dimension(150, 50));
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });
        jPanel3.add(btnReset);

        jButton7.setBackground(new java.awt.Color(255, 204, 153));
        jButton7.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 16)); // NOI18N
        jButton7.setText("Cetak");
        jButton7.setMaximumSize(new java.awt.Dimension(150, 50));
        jButton7.setMinimumSize(new java.awt.Dimension(150, 50));
        jButton7.setPreferredSize(new java.awt.Dimension(150, 50));
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton7);

        jPanel1.add(jPanel3);
        jPanel3.setBounds(460, 150, 180, 320);

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/backWarga.png"))); // NOI18N
        jPanel1.add(jLabel10);
        jLabel10.setBounds(650, 130, 350, 460);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jButton5.setBackground(new java.awt.Color(255, 102, 102));
        jButton5.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 16)); // NOI18N
        jButton5.setText("Hapus");
        jButton5.setMaximumSize(new java.awt.Dimension(150, 50));
        jButton5.setMinimumSize(new java.awt.Dimension(150, 50));
        jButton5.setPreferredSize(new java.awt.Dimension(150, 50));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel5);
        jPanel5.setBounds(450, 490, 190, 100);

        jButton2.setBackground(new java.awt.Color(255, 153, 153));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/icons8-close-32.png"))); // NOI18N
        jButton2.setMaximumSize(new java.awt.Dimension(30, 30));
        jButton2.setMinimumSize(new java.awt.Dimension(30, 30));
        jButton2.setPreferredSize(new java.awt.Dimension(30, 30));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2);
        jButton2.setBounds(980, 20, 30, 30);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 1032, 1145);

        getAccessibleContext().setAccessibleName("Rukun Warga 15");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        cetakData();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        deleteData();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        // TODO add your handling code here:
        fieldKosong();
        letakKursor();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        // TODO add your handling code here:
        insertData();
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        updateData();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        dataTable();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void tfCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfCariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfCariActionPerformed

    private void tblWargaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblWargaMouseClicked
        // TODO add your handling code here:
        klikMouse();
    }//GEN-LAST:event_tblWargaMouseClicked

    private void tfTLahirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfTLahirActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfTLahirActionPerformed

    private void jDateChooser1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jDateChooser1PropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_jDateChooser1PropertyChange

    private void rbtnPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbtnPActionPerformed

    private void rbtnLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnLActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbtnLActionPerformed

    private void tfHPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfHPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfHPActionPerformed

    private void tfNamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfNamaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfNamaActionPerformed

    private void tfNikActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfNikActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfNikActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        Timer timer = new Timer(50, null);
        timer.addActionListener(new ActionListener() {
            private float opacity = 1.0f;

            @Override
            public void actionPerformed(ActionEvent e) {
                opacity -= 0.1f;
                if (opacity <= 0) {
                    timer.stop();
                    dispose();
                } else {
                    setOpacity(Math.max(opacity, 0));
                }
            }
        });
        timer.start();
    }//GEN-LAST:event_jButton2ActionPerformed

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
            java.util.logging.Logger.getLogger(DataWargaPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DataWargaPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DataWargaPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DataWargaPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DataWargaPage().setVisible(true);

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSimpan;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cbAgama;
    private javax.swing.JComboBox<String> cbRT;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JRadioButton rbtnL;
    private javax.swing.JRadioButton rbtnP;
    private javax.swing.JTable tblWarga;
    private javax.swing.JTextField tfCari;
    private javax.swing.JTextField tfHP;
    private javax.swing.JTextField tfNama;
    private javax.swing.JTextField tfNik;
    private javax.swing.JTextField tfTLahir;
    private javax.swing.JTextArea tfaAlamat;
    // End of variables declaration//GEN-END:variables
}
