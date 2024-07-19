/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.sql.*;
import javax.swing.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import database.koneksi;
import static gui.DataWargaPage.convertUtilDateToSqlDate;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author zaeha
 */
public class DataStaffPage extends javax.swing.JFrame {

    private Connection conn = new koneksi().kon();
    private DefaultTableModel tabmode;

    /**
     * Creates new form DataStaff
     */
    public DataStaffPage() {
        initComponents();
        dataTable();
        letakKursor();
        autoID();

    }

    protected void fieldKosong() {
        tfIdStaff.setText("");
        tfNama.setText("");
        tfUsername.setText("");
        tfUsername.setText("");
        tfPassword.setText("");
        cbIzin.setSelectedIndex(0);
        jClok.setSelectedIndex(0);
        tfPassword2.setText("");
    }

    protected void letakKursor() {
        tfNama.requestFocus();
    }

    protected void autoID() {
        if (tfIdStaff == null) {
            JOptionPane.showMessageDialog(null, "Label AutoId belum diinisialisasi.");
            return;
        }

        try {
            Statement stat = conn.createStatement();
            String sql = "SELECT id FROM staff_rw ORDER BY id DESC LIMIT 1"; // Mengambil ID terbesar
            ResultSet res = stat.executeQuery(sql);

            String newId = "0001"; // Default ID jika tabel kosong

            if (res.next()) {
                String id_staff = res.getString("id");
                int idNumber = Integer.parseInt(id_staff) + 1; // Mengambil ID terakhir dan menambahkannya

                // Format ID dengan leading zero
                newId = String.format("%04d", idNumber);
            }

            tfIdStaff.setText(newId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Auto Number Gagal: " + e);
            e.printStackTrace();
        }
    }

    protected void dataTable() {
        Object[] Baris = {"ID Staff", "Nama", "Role", "Gender", "No_HP", "Lokasi Bertugas", "Username", "Password"};
        tabmode = new DefaultTableModel(null, Baris);
        try {
            String isiTeks = tfCari.getText();
            String sql = "SELECT * from staff_rw where id like '%" + isiTeks + "%' or nama like '%" + isiTeks + "%' order by id asc";
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
                    result.getString(8),});
            }
            jTable1.setModel(tabmode);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Data Gagal Diambil" + e);
            System.out.println("" + e);
            e.printStackTrace();
        }
    }

    private void insertData() {
        if (tfNama.getText().equals("") || tfIdStaff.getText().equals("") || (!rbtnL.isSelected() && !rbtnP.isSelected()) || tfUsername.getText().equals("") || tfPassword.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Harap masukan semua data");
        } else if (tfUsername.getText().length() < 8 || tfUsername.getText().length() > 16) {
            JOptionPane.showMessageDialog(null, "Username harus 8 karakter atau kurang dari 16 karakter");
        } else if (!tfUsername.getText().matches(".*[A-Z].*")) {
            JOptionPane.showMessageDialog(null, "Username harus mengandung minimal satu huruf besar");
        } else {
            try {
                // Check if the username already exists
                String checkSql = "SELECT COUNT(*) FROM staff_rw WHERE username = ?";
                PreparedStatement checkStat = conn.prepareStatement(checkSql);
                checkStat.setString(1, tfUsername.getText());
                ResultSet rs = checkStat.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(null, "Username sudah ada, masukkan Username yang berbeda.");
                    return;
                }

                // If username does not exist, proceed with insertion
                String sql = "INSERT INTO staff_rw (id, nama, role, jenis_kelamin, no_hp, lokasi, username, password) VALUES (?,?,?,?,?,?,?,?)";
                PreparedStatement stat = conn.prepareStatement(sql);
                stat.setString(1, tfIdStaff.getText());
                stat.setString(2, tfNama.getText());
                stat.setString(3, cbIzin.getSelectedItem().toString());
                String jenisKelamin = rbtnL.isSelected() ? "L" : "P";
                stat.setString(4, jenisKelamin);
                stat.setString(5, tfPassword2.getText());
                stat.setString(6, jClok.getSelectedItem().toString());
                stat.setString(7, tfUsername.getText());
                stat.setString(8, tfPassword.getText());

                int i = stat.executeUpdate();
                if (i > 0) {
                    JOptionPane.showMessageDialog(null, "Berhasil");
                    dataTable();
                } else {
                    JOptionPane.showMessageDialog(null, "Gagal");
                }
                fieldKosong();
                autoID();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Data Gagal Disimpan!");
                e.printStackTrace();
                System.out.println(e);
            }
        }
    }

    private void deleteData() {
        int option = JOptionPane.showConfirmDialog(null, "Yakin ?", "Hapus '" + tfNama.getText() + "' ", JOptionPane.YES_NO_OPTION);
        if (option == 0) {
            String sql = "DELETE FROM staff_rw where id = '" + tfIdStaff.getText() + "' ";
            try {
                PreparedStatement stat = conn.prepareStatement(sql);
                stat.executeUpdate();
                fieldKosong();
                letakKursor();
                JOptionPane.showMessageDialog(null, "Data Berhasil Dihapus!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }
            dataTable();
            autoID();
        }
    }

    private void updateData() {
        if (tfNama.getText().equals("") || tfIdStaff.getText().equals("") || (!rbtnL.isSelected() && !rbtnP.isSelected()) || tfUsername.getText().equals("") || tfPassword.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Harap masukan semua data");
        } else if (tfUsername.getText().length() < 8 || tfUsername.getText().length() > 16) {
            JOptionPane.showMessageDialog(null, "Username harus 8 karakter atau kurang dari 16 karakter");
        } else if (!tfUsername.getText().matches(".*[A-Z].*")) {
            JOptionPane.showMessageDialog(null, "Username harus mengandung minimal satu huruf besar");
        } else {
            String sql = "UPDATE staff_rw SET nama = ?, jenis_kelamin = ?, izin = ?, username = ?, password = ? WHERE id = ?";
            try {
                PreparedStatement stat = conn.prepareStatement(sql);
                stat.setString(1, tfNama.getText());
                String jenisKelamin = rbtnL.isSelected() ? "L" : "P";
                stat.setString(2, jenisKelamin);
                stat.setString(3, cbIzin.getSelectedItem().toString());
                stat.setString(4, tfUsername.getText());
                stat.setString(5, tfPassword.getText());
                stat.setString(6, tfIdStaff.getText());

                int i = stat.executeUpdate();
                if (i > 0) {
                    JOptionPane.showMessageDialog(null, "Data Berhasil Diubah!");
                    dataTable();
                } else {
                    JOptionPane.showMessageDialog(null, "Gagal mengubah data");
                }
                fieldKosong();
                letakKursor();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Data Gagal Diubah!");
                System.out.println(e);
            }
        }
    }

    void cetakData() {
        String reportSrcFile = "src/laporan/dataStaff.jrxml"; // Path ke file jrxml Anda
        String userHome = System.getProperty("user.home"); // Mendapatkan direktori home pengguna
        String reportDestDir = userHome + "\\Downloads\\";  // Directory untuk output file PDF
        String baseFileName = "LaporanTugasStaffRW15";
        String fileExtension = ".pdf";

        // Mengambil nilai dari tfNik
        String nik = tfIdStaff.getText();
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
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tfIdStaff = new javax.swing.JTextField();
        tfNama = new javax.swing.JTextField();
        tfUsername = new javax.swing.JTextField();
        cbIzin = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        tfPassword = new javax.swing.JTextField();
        rbtnL = new javax.swing.JRadioButton();
        rbtnP = new javax.swing.JRadioButton();
        jButton1 = new javax.swing.JButton();
        tfLok = new javax.swing.JLabel();
        tfHp = new javax.swing.JLabel();
        tfPassword2 = new javax.swing.JTextField();
        jClok = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        tblStaff = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        tfCari = new javax.swing.JTextField();
        btnCari = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        btnSimpan = new javax.swing.JButton();
        btnUbah = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        btnCetak = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Halaman Data Staff");
        setMinimumSize(new java.awt.Dimension(1200, 400));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setMaximumSize(new java.awt.Dimension(2000, 2000));
        jPanel1.setPreferredSize(new java.awt.Dimension(1500, 2000));

        jLabel1.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N
        jLabel1.setText("DATA STAFF");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Input Data", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tw Cen MT", 1, 16))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jLabel2.setText("ID Staff");

        jLabel3.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jLabel3.setText("Nama Staff");

        jLabel4.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jLabel4.setText("Role");

        jLabel5.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jLabel5.setText("JenKel");

        jLabel6.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jLabel6.setText("Username");

        tfIdStaff.setBackground(new java.awt.Color(204, 231, 231));
        tfIdStaff.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N

        tfNama.setBackground(new java.awt.Color(204, 231, 231));
        tfNama.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N

        tfUsername.setBackground(new java.awt.Color(204, 231, 231));
        tfUsername.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N

        cbIzin.setBackground(new java.awt.Color(204, 231, 231));
        cbIzin.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        cbIzin.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Staff", "Ketua RW" }));

        jLabel7.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jLabel7.setText("Password");

        tfPassword.setBackground(new java.awt.Color(204, 231, 231));
        tfPassword.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        tfPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfPasswordActionPerformed(evt);
            }
        });

        rbtnL.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(rbtnL);
        rbtnL.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        rbtnL.setText("L");

        rbtnP.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(rbtnP);
        rbtnP.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        rbtnP.setText("P");

        jButton1.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jButton1.setText("GetID");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        tfLok.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        tfLok.setText("Lokasi RT");

        tfHp.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        tfHp.setText("No HP");

        tfPassword2.setBackground(new java.awt.Color(204, 231, 231));
        tfPassword2.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        tfPassword2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfPassword2ActionPerformed(evt);
            }
        });

        jClok.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jClok.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addGap(21, 21, 21)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tfNama, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(tfIdStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton1)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cbIzin, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfHp)
                    .addComponent(tfLok)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(rbtnL)
                                .addGap(18, 18, 18)
                                .addComponent(rbtnP))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(tfPassword, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(tfUsername)
                                    .addComponent(tfPassword2)
                                    .addComponent(jClok, 0, 224, Short.MAX_VALUE))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tfIdStaff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(tfNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cbIzin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(rbtnL)
                    .addComponent(rbtnP))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfPassword2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfHp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfLok)
                    .addComponent(jClok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(tfUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Data Staff", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tw Cen MT", 1, 16))); // NOI18N

        tblStaff.setViewportBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tblStaff.setFont(new java.awt.Font("Trebuchet MS", 1, 16)); // NOI18N

        jTable1.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
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
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        tblStaff.setViewportView(jTable1);

        tfCari.setBackground(new java.awt.Color(204, 231, 231));
        tfCari.setMaximumSize(new java.awt.Dimension(250, 30));

        btnCari.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        btnCari.setText("Cari");
        btnCari.setMaximumSize(new java.awt.Dimension(100, 29));
        btnCari.setPreferredSize(new java.awt.Dimension(150, 50));
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });

        btnHapus.setBackground(new java.awt.Color(255, 102, 102));
        btnHapus.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        btnHapus.setText("Hapus");
        btnHapus.setMaximumSize(new java.awt.Dimension(100, 29));
        btnHapus.setPreferredSize(new java.awt.Dimension(150, 50));
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tblStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 326, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(tfCari, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnHapus, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tblStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setMaximumSize(new java.awt.Dimension(180, 330));
        jPanel4.setPreferredSize(new java.awt.Dimension(180, 330));
        jPanel4.setRequestFocusEnabled(false);

        btnSimpan.setBackground(new java.awt.Color(204, 255, 204));
        btnSimpan.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        btnSimpan.setText("Simpan");
        btnSimpan.setMaximumSize(new java.awt.Dimension(100, 29));
        btnSimpan.setPreferredSize(new java.awt.Dimension(150, 50));
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        btnUbah.setBackground(new java.awt.Color(153, 204, 255));
        btnUbah.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        btnUbah.setText("Ubah");
        btnUbah.setMaximumSize(new java.awt.Dimension(100, 29));
        btnUbah.setPreferredSize(new java.awt.Dimension(150, 50));
        btnUbah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbahActionPerformed(evt);
            }
        });

        btnBatal.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        btnBatal.setText("Batal");
        btnBatal.setMaximumSize(new java.awt.Dimension(100, 29));
        btnBatal.setPreferredSize(new java.awt.Dimension(150, 50));
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });

        btnCetak.setBackground(new java.awt.Color(255, 204, 153));
        btnCetak.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        btnCetak.setText("Cetak");
        btnCetak.setMaximumSize(new java.awt.Dimension(100, 29));
        btnCetak.setPreferredSize(new java.awt.Dimension(150, 50));
        btnCetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCetakActionPerformed(evt);
            }
        });

        btnClose.setBackground(new java.awt.Color(255, 102, 102));
        btnClose.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        btnClose.setText("Close");
        btnClose.setMaximumSize(new java.awt.Dimension(100, 29));
        btnClose.setPreferredSize(new java.awt.Dimension(150, 50));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnUbah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCetak, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnUbah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCetak, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(71, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(68, 68, 68)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(46, 46, 46)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(73, 73, 73))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addGap(30, 30, 30)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 415, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(85, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        // TODO add your handling code here:
        insertData();
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbahActionPerformed
        // TODO add your handling code here:
        updateData();
    }//GEN-LAST:event_btnUbahActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        // TODO add your handling code here:
        fieldKosong();
    }//GEN-LAST:event_btnBatalActionPerformed

    private void btnCetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCetakActionPerformed
        // TODO add your handling code here:
        cetakData();
    }//GEN-LAST:event_btnCetakActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // TODO add your handling code here:
        deleteData();
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariActionPerformed
        // TODO add your handling code here:
        dataTable();
    }//GEN-LAST:event_btnCariActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void tfPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfPasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfPasswordActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        int row = jTable1.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

        tfIdStaff.setText(model.getValueAt(row, 0).toString());
        tfNama.setText(model.getValueAt(row, 1).toString());
        String role = tabmode.getValueAt(row, 2).toString();
        switch (role) {
            case "Staff":
                cbIzin.setSelectedIndex(0);
                break;
            case "Ketua":
                cbIzin.setSelectedIndex(1);
                break;

        }

        String jenKel = model.getValueAt(row, 3).toString();
        if (jenKel.equals("L")) {
            rbtnL.setSelected(true);
        } else {
            rbtnP.setSelected(true);
        }

        tfPassword2.setText(model.getValueAt(row, 4).toString());
        String lokasi = tabmode.getValueAt(row, 5).toString();
        switch (lokasi) {
            case "1":
                jClok.setSelectedIndex(0);
                break;
            case "2":
                jClok.setSelectedIndex(1);
                break;
            case "3":
                jClok.setSelectedIndex(2);
                break;
            case "4":
                jClok.setSelectedIndex(3);
                break;
            case "5":
                jClok.setSelectedIndex(3);
                break;
            case "6":
                jClok.setSelectedIndex(3);
                break;
            case "7":
                jClok.setSelectedIndex(3);
                break;
        }
        tfUsername.setText(model.getValueAt(row, 6).toString());
        tfPassword.setText(model.getValueAt(row, 7).toString());
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        autoID();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tfPassword2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfPassword2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfPassword2ActionPerformed

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
            java.util.logging.Logger.getLogger(DataStaffPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DataStaffPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DataStaffPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DataStaffPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DataStaffPage().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnCetak;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnUbah;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cbIzin;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jClok;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTable jTable1;
    private javax.swing.JRadioButton rbtnL;
    private javax.swing.JRadioButton rbtnP;
    private javax.swing.JScrollPane tblStaff;
    private javax.swing.JTextField tfCari;
    private javax.swing.JLabel tfHp;
    private javax.swing.JTextField tfIdStaff;
    private javax.swing.JLabel tfLok;
    private javax.swing.JTextField tfNama;
    private javax.swing.JTextField tfPassword;
    private javax.swing.JTextField tfPassword2;
    private javax.swing.JTextField tfUsername;
    // End of variables declaration//GEN-END:variables
}
