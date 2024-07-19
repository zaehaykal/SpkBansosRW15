/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import database.koneksi;
import static gui.DataWargaPage.convertUtilDateToSqlDate;
import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import popUp.popUpWarga;

/**
 *
 * @author zaeha
 */
public class DataAlternatifPage extends javax.swing.JFrame {

    private DefaultTableModel tabmodeAlt;
    private DefaultTableModel tabmodeBobot;
    private Connection conn = new koneksi().kon();
    DecimalFormat df = new DecimalFormat("#.#####");
    int jmlKriteria = 0;
    int[] kriteria;

    /**
     * Creates new form DataAlternatif
     */
    public DataAlternatifPage() {
        initComponents();
//        setdataTabelKriteria();
        setKriteriaLabels();
        dataTable();
        autoID();
        nonEditableTf();
//        keyListner();
        // Tambahkan KeyListener ke tfNik

    }

    protected void nonEditableTf() {
        tfId.setEditable(false);
    }

    protected void fieldKosong() {
        tfIdStaff.setText("");
        tfNama.setText("");
        tfNik.setText("");
        tfTTL.setText("");
        tfTLahir.setText("");
        resetComboBoxes();
    }

    protected void autoID() {
        if (tfId == null) {
            JOptionPane.showMessageDialog(null, "Label AutoId belum diinisialisasi.");
            return;
        }

        try {
            Statement stat = conn.createStatement();
            String sql = "SELECT id FROM alternatif ORDER BY id DESC LIMIT 1"; // Mengambil ID terbesar
            ResultSet res = stat.executeQuery(sql);

            int newId = 1; // Default ID jika tabel kosong

            if (res.next()) {
                int id_rangking = res.getInt("id"); // Mengambil ID rangking terbesar
                newId = id_rangking + 1;
            }

            tfId.setText("" + String.valueOf(newId)); // Set new ID to the label
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Auto Number Gagal: " + e.getMessage());
            e.printStackTrace(); // Menampilkan stack trace untuk debugging lebih lanjut
        }
    }

    public void getWarga(String no_ktp, String nama, String tempat, String tanggal) {
        try {

            tfNik.setText(no_ktp);
            tfNama.setText(nama);
            tfTLahir.setText(tempat);
            tfTTL.setText(tanggal);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Data Gagal Diambil!");
            System.out.println(e);
        }
    }

    private void deleteAlternatifById(int id) {
        String deleteAlternatifSubQuery = "DELETE FROM alternatif_sub WHERE id_alternatif = ?";
        String deleteAlternatifQuery = "DELETE FROM alternatif WHERE id = ?";

        try (PreparedStatement deleteAlternatifSubStmt = conn.prepareStatement(deleteAlternatifSubQuery);
                PreparedStatement deleteAlternatifStmt = conn.prepareStatement(deleteAlternatifQuery)) {

            // Mulai transaksi
            conn.setAutoCommit(false);

            // Hapus entri dari tabel alternatif_sub
            deleteAlternatifSubStmt.setInt(1, id);
            deleteAlternatifSubStmt.executeUpdate();

            // Hapus entri dari tabel alternatif
            deleteAlternatifStmt.setInt(1, id);
            deleteAlternatifStmt.executeUpdate();

            // Commit transaksi
            conn.commit();
            JOptionPane.showMessageDialog(null, "Data berhasil dihapus.");

            // Refresh tabel setelah penghapusan
            dataTable();

        } catch (SQLException e) {
            try {
                // Rollback transaksi jika terjadi kesalahan
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(null, "Gagal menghapus data: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                // Kembali ke autocommit mode
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteSelectedAlternatif() {
        int selectedRow = tblAlt.getSelectedRow();
        if (selectedRow >= 0) {
            int id = Integer.parseInt(tabmodeAlt.getValueAt(selectedRow, 0).toString());
            int confirm = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Penghapusan", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                deleteAlternatifById(id);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Pilih baris yang ingin dihapus.");
        }
    }

    private void setKriteriaLabels() {
        // Query untuk mendapatkan data kriteria
        try {
            Statement stat = conn.createStatement();

            String sqlCount = "SELECT COUNT(*) AS count FROM kriteria";
            ResultSet resCount = stat.executeQuery(sqlCount);
            if (resCount.next()) {
                jmlKriteria = resCount.getInt("count");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String queryKriteria = "SELECT nama_kriteria FROM kriteria";

        try (PreparedStatement stmtKriteria = conn.prepareStatement(queryKriteria);
                ResultSet rsKriteria = stmtKriteria.executeQuery()) {

            // Array untuk menyimpan nama kriteria
            ArrayList<String> namaKriteriaList = new ArrayList<>();

            // Tambahkan nama kriteria ke dalam list
            while (rsKriteria.next()) {
                String namaKriteria = rsKriteria.getString("nama_kriteria");
                namaKriteriaList.add(namaKriteria);
            }

            System.out.println("jumlah kriteria" + jmlKriteria);
            System.out.println("jumlah kriteria" + kriteria);
            // Setel teks JLabel dengan nama kriteria
            if (namaKriteriaList.size() > 0) {
                C1lbl.setText(namaKriteriaList.get(0));
            }
            if (namaKriteriaList.size() > 1) {
                C2lbl.setText(namaKriteriaList.get(1));
            }
            if (namaKriteriaList.size() > 2) {
                C3lbl.setText(namaKriteriaList.get(2));
            }
            if (namaKriteriaList.size() > 3) {
                C4lbl.setText(namaKriteriaList.get(3));
            }
            if (namaKriteriaList.size() > 4) {
                C5lbl.setText(namaKriteriaList.get(4));
            }
            if (namaKriteriaList.size() > 5) {
                C6lbl.setText(namaKriteriaList.get(5));
            }
            if (namaKriteriaList.size() > 6) {
                C7lbl.setText(namaKriteriaList.get(6));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void dataTable() {
        // Pastikan tabmodeAlt diinisialisasi dengan benar
        if (tabmodeAlt == null) {
            tabmodeAlt = new DefaultTableModel();
            tabmodeAlt.addColumn("ID");
            tabmodeAlt.addColumn("NIK");
            tabmodeAlt.addColumn("NAMA WARGA");
            tabmodeAlt.addColumn("ID STAFF");
            tabmodeAlt.addColumn("NAMA STAFF");
            tabmodeAlt.addColumn("GOLONGAN");
            tblAlt.setModel(tabmodeAlt); // Atur model tabel ke tblAlt
        } else {
            // Bersihkan baris yang ada
            tabmodeAlt.setRowCount(0);
        }
        String isiTeks = tfCari.getText().trim();
        // Query untuk mendapatkan data kriteria
        String queryKriteria = "SELECT nama_kriteria FROM kriteria";
        // Query untuk mendapatkan data alternatif dan bobot

        String queryData = "SELECT a.id, a.no_ktp, a.nama_warga, a.id_staff, a.nama_staff, a.golongan, "
                + "GROUP_CONCAT(asub.nilai_bobot ORDER BY asub.id_kriteria) AS nilai_bobot "
                + "FROM alternatif a "
                + "JOIN alternatif_sub asub ON a.id = asub.id_alternatif "
                + "WHERE a.id LIKE '%" + isiTeks + "%' OR a.no_ktp LIKE '%" + isiTeks + "%' OR a.nama_warga LIKE '%" + isiTeks + "%' "
                + "OR a.golongan LIKE '%" + isiTeks + "%' "
                + "GROUP BY a.id, a.no_ktp, a.nama_warga, a.id_staff, a.nama_staff, a.golongan "
                + "ORDER BY a.id";

        System.out.println("Query SQL yang digunakan: " + queryData);

        try (PreparedStatement stmtKriteria = conn.prepareStatement(queryKriteria);
                ResultSet rsKriteria = stmtKriteria.executeQuery();
                PreparedStatement stmtData = conn.prepareStatement(queryData);
                ResultSet rsData = stmtData.executeQuery()) {

            // Buat model tabel untuk tabmodeBobot
            DefaultTableModel modelBobot = new DefaultTableModel();
            tblBobot.setModel(modelBobot); // Atur model tabel ke tblBobot

            // Tambahkan kolom ke model tabel berdasarkan data kriteria dari database
            while (rsKriteria.next()) {
                String namaKriteria = rsKriteria.getString("nama_kriteria");
                modelBobot.addColumn(namaKriteria);
            }

            // Dapatkan jumlah kolom dalam model tabel
            int numColumns = modelBobot.getColumnCount();

            // Isi data ke dalam model tabel tabmodeAlt dan tabmodeBobot
            while (rsData.next()) {
                int id = rsData.getInt("id");
                String noKtp = rsData.getString("no_ktp");
                String namaWarga = rsData.getString("nama_warga");
                String idStaff = rsData.getString("id_staff");
                String namaStaff = rsData.getString("nama_staff");
                String golongan = rsData.getString("golongan");
                String[] nilaiBobotArray = rsData.getString("nilai_bobot").split(",");

                // Tambahkan baris ke tabmodeAlt
                tabmodeAlt.addRow(new Object[]{id, noKtp, namaWarga, idStaff, namaStaff, golongan});

                // Buat baris untuk tabmodeBobot dengan nilai_bobot
                Object[] rowDataBobot = new Object[numColumns];
                for (int i = 0; i < nilaiBobotArray.length; i++) {
                    rowDataBobot[i] = Integer.parseInt(nilaiBobotArray[i]);
                }

                modelBobot.addRow(rowDataBobot);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertData() {
        if (tfNik.getText().isEmpty() || tfNama.getText().isEmpty() || tfIdStaff.getText().isEmpty() || tfNamaStaff.getText().isEmpty() || cbBoxC1.getSelectedItem() == null || jComboBox2.getSelectedItem() == null || jComboBox3.getSelectedItem() == null || jComboBox4.getSelectedItem() == null || jComboBox5.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null, "Harap masukkan semua data");
        } else if (tfNik.getText().length() < 8 || tfNik.getText().length() > 16) {
            JOptionPane.showMessageDialog(null, "ID Staff harus antara 8 hingga 16 karakter");
        } else {
            try {
                // Calculate total score for golongan
                int totalScore = 0;
                for (int i = 1; i <= 5; i++) {
                    JComboBox comboBox = getComboBox(i);
                    totalScore += comboBox.getSelectedIndex() + 1; // Sum the indices (adding 1 because indices start at 0)
                }

                String golongan;
                if (totalScore >= 20) {
                    golongan = "Mampu";
                } else {
                    golongan = "Tidak Mampu";
                }

                // Insert into 'alternatif' table
                String sqlAlternatif = "INSERT INTO alternatif(no_ktp, nama_warga, id_staff, nama_staff, golongan) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement statAlternatif = conn.prepareStatement(sqlAlternatif, Statement.RETURN_GENERATED_KEYS);
                statAlternatif.setString(1, tfNik.getText()); // Set no_ktp
                statAlternatif.setString(2, tfNama.getText()); // Set nama_warga
                statAlternatif.setString(3, tfIdStaff.getText()); // Set id_staff
                statAlternatif.setString(4, tfNamaStaff.getText()); // Set nama_staff
                statAlternatif.setString(5, golongan); // Set golongan with total score

                int iAlternatif = statAlternatif.executeUpdate();
                if (iAlternatif > 0) {
                    // Retrieve the generated id for 'alternatif'
                    ResultSet generatedKeys = statAlternatif.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);

                        // Insert into 'alternatif_sub' table
                        String sqlSub = "INSERT INTO alternatif_sub(id_alternatif, id_kriteria, nilai_bobot) VALUES (?, ?, ?)";
                        PreparedStatement statSub = conn.prepareStatement(sqlSub);

                        for (int i = 1; i <= jmlKriteria; i++) {
                            JComboBox comboBox = getComboBox(i);
                            statSub.setInt(1, generatedId); // Set generated ID Alternatif
                            statSub.setInt(2, i); // Set ID Kriteria (using the order number)
                            statSub.setInt(3, comboBox.getSelectedIndex() + 1); // Set nilai bobot (selected index + 1 because index starts at 0)
                            statSub.addBatch(); // Add to batch for simultaneous execution
                        }

                        // Execute batch to save data into 'alternatif_sub' table
                        int[] batchResult = statSub.executeBatch();
                        boolean success = true;
                        for (int result : batchResult) {
                            if (result <= 0) {
                                success = false;
                                break;
                            }
                        }

                        if (success) {
                            JOptionPane.showMessageDialog(null, "Data berhasil disimpan");
                            // Call method to retrieve nilai bobot after successful save
                            dataTable();
                        } else {
                            JOptionPane.showMessageDialog(null, "Gagal menyimpan data ke dalam tabel alternatif_sub");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Gagal mendapatkan ID yang dihasilkan dari tabel alternatif");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Gagal menyimpan data ke dalam tabel alternatif");
                }
                autoID(); // Call method to generate auto ID after saving data
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Data gagal disimpan!");
                System.out.println(e);
                e.printStackTrace();
            }
        }
    }

    private void updateData() {
    if (tfNik.getText().isEmpty() || tfNama.getText().isEmpty() || tfIdStaff.getText().isEmpty() || tfNamaStaff.getText().isEmpty() || cbBoxC1.getSelectedItem() == null || jComboBox2.getSelectedItem() == null || jComboBox3.getSelectedItem() == null || jComboBox4.getSelectedItem() == null || jComboBox5.getSelectedItem() == null) {
        JOptionPane.showMessageDialog(null, "Harap masukkan semua data");
    } else if (tfNik.getText().length() < 8 || tfNik.getText().length() > 16) {
        JOptionPane.showMessageDialog(null, "ID Staff harus antara 8 hingga 16 karakter");
    } else {
        try {
            conn.setAutoCommit(false); // Mulai transaksi

            // Calculate total score for golongan
            int totalScore = 0;
            for (int i = 1; i <= 5; i++) {
                JComboBox comboBox = getComboBox(i);
                totalScore += comboBox.getSelectedIndex() + 1; // Sum the indices (adding 1 because indices start at 0)
            }

            String golongan;
            if (totalScore >= 25) {
                golongan = "Mampu";
            } else {
                golongan = "Tidak Mampu";
            }

            // Dapatkan id_alternatif dari tfId
            int idAlternatif = Integer.parseInt(tfId.getText());

            // Update 'alternatif_sub' table
            String sqlUpdateSub = "UPDATE alternatif_sub SET nilai_bobot = ? WHERE id_alternatif = ? AND id_kriteria = ?";
            boolean success = true;

            for (int i = 1; i <= jmlKriteria; i++) {
                try (PreparedStatement statUpdateSub = conn.prepareStatement(sqlUpdateSub)) {
                    JComboBox comboBox = getComboBox(i);
                    statUpdateSub.setInt(1, comboBox.getSelectedIndex() + 1); // Set nilai bobot (selected index + 1 because index starts at 0)
                    statUpdateSub.setInt(2, idAlternatif); // Set id_alternatif
                    statUpdateSub.setInt(3, i); // Set ID Kriteria (using the order number)

                    int updateResult = statUpdateSub.executeUpdate();
                    if (updateResult <= 0) {
                        success = false;
                        break;
                    }
                }
            }

            if (success) {
                // Update 'alternatif' table
                String sqlUpdateAlternatif = "UPDATE alternatif SET id_staff = ?, nama_staff = ?, golongan = ? WHERE id = ?";
                try (PreparedStatement statUpdateAlternatif = conn.prepareStatement(sqlUpdateAlternatif)) {
                    statUpdateAlternatif.setString(1, tfIdStaff.getText()); // Set id_staff
                    statUpdateAlternatif.setString(2, tfNamaStaff.getText()); // Set nama_staff
                    statUpdateAlternatif.setString(3, golongan); // Set golongan
                    statUpdateAlternatif.setInt(4, idAlternatif); // Set id (key for update)

                    int iUpdateAlternatif = statUpdateAlternatif.executeUpdate();
                    if (iUpdateAlternatif > 0) {
                        conn.commit(); // Commit transaction
                        JOptionPane.showMessageDialog(null, "Data berhasil diperbarui");
                        // Call method to retrieve nilai bobot after successful update
                        dataTable();
                    } else {
                        conn.rollback(); // Rollback transaction in case of failure
                        JOptionPane.showMessageDialog(null, "Gagal memperbarui data di tabel alternatif");
                    }
                } catch (SQLException e) {
                    conn.rollback(); // Rollback transaction in case of failure
                    JOptionPane.showMessageDialog(null, "Data gagal diperbarui!");
                    System.out.println(e);
                    e.printStackTrace();
                }
            } else {
                conn.rollback(); // Rollback transaction in case of failure
                JOptionPane.showMessageDialog(null, "Gagal memperbarui data di tabel alternatif_sub");
            }

            conn.setAutoCommit(true); // Reset to default auto-commit behavior
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Data gagal diperbarui!");
            System.out.println(e);
            e.printStackTrace();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID Alternatif tidak valid!");
            e.printStackTrace();
        }
    }
}

    private void klikMouse() {
        int row = tblAlt.getSelectedRow();

        if (row >= 0) {
            tfNik.setText(tabmodeAlt.getValueAt(row, 1).toString());
            tfNama.setText(tabmodeAlt.getValueAt(row, 2).toString());
            tfIdStaff.setText(tabmodeAlt.getValueAt(row, 3).toString());
            tfNamaStaff.setText(tabmodeAlt.getValueAt(row, 4).toString());
            tfId.setText(tabmodeAlt.getValueAt(row, 0).toString());

            String nik = tfNik.getText();
            if (!nik.isEmpty()) {
                fetchDataByNik(nik);
            }

            if (tfNik.getKeyListeners().length == 0) {
                tfNik.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent e) {
                        String nik = tfNik.getText();
                        if (!nik.isEmpty()) {
                            fetchDataByNik(nik);
                        }
                    }
                });
            }

            fetchAlternatifSubDataFromTable(row);
        } else {
            JOptionPane.showMessageDialog(null, "Pilih baris yang valid dari tabel.");
        }
    }

    private void fetchAlternatifSubDataFromTable(int row) {
        if (row < 0) {
            JOptionPane.showMessageDialog(null, "Pilih baris yang valid dari tabel Bobot.");
            return;
        }

        // Reset comboboxes to default state
        resetComboBoxes();

        try {
            // Ambil nilai bobot dari tabel tblBobot berdasarkan row yang dipilih
            int numColumns = tblBobot.getColumnCount();

            for (int col = 0; col < numColumns; col++) {
                Object value = tblBobot.getValueAt(row, col);
                if (value != null) {
                    try {
                        int nilaiBobot = Integer.parseInt(value.toString());
                        JComboBox comboBox = getComboBox(col + 1); // col + 1 karena id_kriteria dimulai dari 1
                        if (comboBox != null && isValidComboBoxIndex(comboBox, nilaiBobot)) {
                            comboBox.setSelectedIndex(nilaiBobot - 1);
                        } else {
                            showInvalidValueMessage(col + 1, nilaiBobot);
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Gagal mengonversi nilai bobot: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private JComboBox getComboBox(int index) {
        switch (index) {
            case 1:
                return cbBoxC1;
            case 2:
                return jComboBox2;
            case 3:
                return jComboBox3;
            case 4:
                return jComboBox4;
            case 5:
                return jComboBox5;
            case 6:
                return jComboBox6;
            case 7:
                return jComboBox7;
            default:
                return null;
        }
    }

    private boolean isValidComboBoxIndex(JComboBox comboBox, int nilaiBobot) {
        return nilaiBobot > 0 && nilaiBobot <= comboBox.getItemCount();
    }

    private void showInvalidValueMessage(int idKriteria, int nilaiBobot) {
        JOptionPane.showMessageDialog(null, "Nilai bobot untuk kriteria " + idKriteria + " tidak valid: " + nilaiBobot);
    }

    private void resetComboBoxes() {
        cbBoxC1.setSelectedIndex(0);
        jComboBox2.setSelectedIndex(0);
        jComboBox3.setSelectedIndex(0);
        jComboBox4.setSelectedIndex(0);
        jComboBox5.setSelectedIndex(0);
        jComboBox6.setSelectedIndex(0);
        jComboBox7.setSelectedIndex(0);
    }

    private void fetchDataByNik(String no_ktp) {
        String query = "SELECT t_lahir, tanggal_lahir FROM warga WHERE no_ktp = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, no_ktp);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String tempatLahir = rs.getString("t_lahir");
                String tanggalLahir = rs.getString("tanggal_lahir");

                tfTLahir.setText(tempatLahir);
                tfTTL.setText(tanggalLahir);
            } else {
                // Jika tidak ditemukan data, kosongkan field
                tfTLahir.setText("");
                tfTTL.setText("");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data: " + e.getMessage());
            e.printStackTrace();
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
        jPanel2 = new javax.swing.JPanel();
        jIdAlt = new javax.swing.JLabel();
        jNoKTP = new javax.swing.JLabel();
        jNama = new javax.swing.JLabel();
        jTLahir = new javax.swing.JLabel();
        tfId = new javax.swing.JTextField();
        tfCariWarga = new javax.swing.JButton();
        btnGetId = new javax.swing.JButton();
        jTtl = new javax.swing.JLabel();
        tfNik = new javax.swing.JTextField();
        tfNama = new javax.swing.JTextField();
        tfTLahir = new javax.swing.JTextField();
        tfTTL = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBobot = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblAlt = new javax.swing.JTable();
        tfCari = new javax.swing.JTextField();
        cari = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        C1lbl = new javax.swing.JLabel();
        C2lbl = new javax.swing.JLabel();
        C3lbl = new javax.swing.JLabel();
        C4lbl = new javax.swing.JLabel();
        C5lbl = new javax.swing.JLabel();
        C6lbl = new javax.swing.JLabel();
        cbBoxC1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jComboBox3 = new javax.swing.JComboBox<>();
        jComboBox4 = new javax.swing.JComboBox<>();
        jComboBox5 = new javax.swing.JComboBox<>();
        jComboBox6 = new javax.swing.JComboBox<>();
        C7lbl = new javax.swing.JLabel();
        jComboBox7 = new javax.swing.JComboBox<>();
        jPanel6 = new javax.swing.JPanel();
        jLa1231 = new javax.swing.JLabel();
        jLabel234 = new javax.swing.JLabel();
        tfIdStaff = new javax.swing.JTextField();
        tfNamaStaff = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Penilaian Alternatif");
        setMaximumSize(new java.awt.Dimension(1903, 815));
        setMinimumSize(new java.awt.Dimension(1903, 815));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Data Warga", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tw Cen MT", 1, 16))); // NOI18N

        jIdAlt.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jIdAlt.setText("ID Alternatif");

        jNoKTP.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jNoKTP.setText("NIK");

        jNama.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jNama.setText("Nama");

        jTLahir.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jTLahir.setText("Tanggal Lahir");

        tfCariWarga.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        tfCariWarga.setText("Get ID");
        tfCariWarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfCariWargaActionPerformed(evt);
            }
        });

        btnGetId.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        btnGetId.setText("Get ID");
        btnGetId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGetIdActionPerformed(evt);
            }
        });

        jTtl.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jTtl.setText("Tempat Lahir");

        tfNik.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfNikKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jNama, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfNama, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jIdAlt, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jNoKTP, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tfId, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tfNik, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 81, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnGetId)
                            .addComponent(tfCariWarga)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jTLahir, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfTTL, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jTtl, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfTLahir, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jIdAlt)
                            .addComponent(tfId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jNoKTP)
                            .addComponent(tfNik, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jNama)
                            .addComponent(tfNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(btnGetId)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfCariWarga)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTtl)
                    .addComponent(tfTLahir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTLahir)
                    .addComponent(tfTTL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(39, Short.MAX_VALUE))
        );

        jButton1.setBackground(new java.awt.Color(255, 153, 153));
        jButton1.setText("jButton1");
        jButton1.setMaximumSize(new java.awt.Dimension(150, 50));
        jButton1.setMinimumSize(new java.awt.Dimension(150, 50));
        jButton1.setPreferredSize(new java.awt.Dimension(150, 50));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Data Warga", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tw Cen MT", 0, 16))); // NOI18N

        tblBobot.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        tblBobot.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblBobot);

        tblAlt.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        tblAlt.setModel(new javax.swing.table.DefaultTableModel(
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
        tblAlt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblAltMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblAlt);

        tfCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfCariActionPerformed(evt);
            }
        });

        cari.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        cari.setText("Cari Warga");
        cari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cariActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(jScrollPane1))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(tfCari, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cari)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cari))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(58, 58, 58))
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel4.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N

        jButton5.setBackground(new java.awt.Color(255, 102, 102));
        jButton5.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jButton5.setText("Hapus");
        jButton5.setMaximumSize(new java.awt.Dimension(150, 50));
        jButton5.setMinimumSize(new java.awt.Dimension(150, 50));
        jButton5.setPreferredSize(new java.awt.Dimension(150, 50));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton7.setBackground(new java.awt.Color(204, 204, 204));
        jButton7.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jButton7.setText("Reset");
        jButton7.setMaximumSize(new java.awt.Dimension(150, 50));
        jButton7.setMinimumSize(new java.awt.Dimension(150, 50));
        jButton7.setPreferredSize(new java.awt.Dimension(150, 50));
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Input Alternatif Bobot", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tw Cen MT", 1, 16))); // NOI18N
        jPanel5.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N

        C1lbl.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        C1lbl.setText("jLabel1");
        C1lbl.setMaximumSize(new java.awt.Dimension(49, 26));
        C1lbl.setMinimumSize(new java.awt.Dimension(49, 26));
        C1lbl.setPreferredSize(new java.awt.Dimension(49, 26));

        C2lbl.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        C2lbl.setText("jLabel2");
        C2lbl.setMaximumSize(new java.awt.Dimension(49, 26));
        C2lbl.setMinimumSize(new java.awt.Dimension(49, 26));
        C2lbl.setPreferredSize(new java.awt.Dimension(49, 26));

        C3lbl.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        C3lbl.setText("jLabel3");
        C3lbl.setMaximumSize(new java.awt.Dimension(49, 26));
        C3lbl.setMinimumSize(new java.awt.Dimension(49, 26));
        C3lbl.setPreferredSize(new java.awt.Dimension(49, 26));

        C4lbl.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        C4lbl.setText("jLabel4");
        C4lbl.setMaximumSize(new java.awt.Dimension(49, 26));
        C4lbl.setMinimumSize(new java.awt.Dimension(49, 26));
        C4lbl.setPreferredSize(new java.awt.Dimension(49, 26));

        C5lbl.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        C5lbl.setText("jLabel5");
        C5lbl.setMaximumSize(new java.awt.Dimension(49, 26));
        C5lbl.setMinimumSize(new java.awt.Dimension(49, 26));
        C5lbl.setPreferredSize(new java.awt.Dimension(49, 26));

        C6lbl.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        C6lbl.setText("jLabel6");

        cbBoxC1.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        cbBoxC1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Kayu/bambu/anyaman/seng", "Kamprot", "Plester" }));

        jComboBox2.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tanah", "Cor", "Ubin/Marmer" }));

        jComboBox3.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tidak Punya", "Jamban", "Jongkok", "Duduk" }));

        jComboBox4.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Lebih dari 5jt", "Kurang dari 4jt", "Kurang dari 3jt", "Kurang dari 2jt", "Kurang dari 1jt" }));

        jComboBox5.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1 Anak", "2 Anak", "3 Anak", "4 Anak", "5 Anak atau Lebih" }));

        jComboBox6.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tidak Memiliki Tempat Tinggal", "Kontrak", "Rumah Orang Tua", "Memiliki Rumah" }));

        C7lbl.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        C7lbl.setText("jLabel6");

        jComboBox7.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jComboBox7.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tidak Menggunakan Listrik", "<= 450 VA", "<= 900 VA", "> 900 VA" }));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(C5lbl, javax.swing.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
                            .addComponent(C4lbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(C6lbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(15, 15, 15)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(C3lbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(C2lbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(C1lbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbBoxC1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(C7lbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(15, 15, 15)
                        .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbBoxC1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(C1lbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(C2lbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(C3lbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(C4lbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(C5lbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(C6lbl)
                    .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(C7lbl)
                    .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Data Staff", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tw Cen MT", 1, 16))); // NOI18N

        jLa1231.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jLa1231.setText("ID Staff");

        jLabel234.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jLabel234.setText("Nama");

        tfIdStaff.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N

        tfNamaStaff.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLa1231)
                    .addComponent(jLabel234))
                .addGap(72, 72, 72)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfNamaStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfIdStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLa1231)
                    .addComponent(tfIdStaff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel234)
                    .addComponent(tfNamaStaff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setText("Data Alternatif");

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jButton4.setBackground(new java.awt.Color(204, 255, 204));
        jButton4.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jButton4.setText("Simpan");
        jButton4.setMaximumSize(new java.awt.Dimension(150, 50));
        jButton4.setMinimumSize(new java.awt.Dimension(150, 50));
        jButton4.setPreferredSize(new java.awt.Dimension(150, 50));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(153, 204, 255));
        jButton6.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jButton6.setText("Ubah");
        jButton6.setMaximumSize(new java.awt.Dimension(150, 50));
        jButton6.setMinimumSize(new java.awt.Dimension(150, 50));
        jButton6.setPreferredSize(new java.awt.Dimension(150, 50));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(872, 872, 872)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(101, 101, 101)
                .addComponent(jLabel1)
                .addGap(79, 79, 79)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(197, 197, 197))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(263, 263, 263))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 9, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 770, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tfCariWargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfCariWargaActionPerformed
        // TODO add your handling code here:
        popUpWarga pWarga = new popUpWarga();
        pWarga.warga = this;
        pWarga.setVisible(true);
        pWarga.setResizable(false);
    }//GEN-LAST:event_tfCariWargaActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        insertData();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void btnGetIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetIdActionPerformed
        // TODO add your handling code here:
        autoID();
    }//GEN-LAST:event_btnGetIdActionPerformed

    private void tblAltMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblAltMouseClicked
        // TODO add your handling code here:
        klikMouse();
    }//GEN-LAST:event_tblAltMouseClicked

    private void tfNikKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfNikKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_tfNikKeyReleased

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        deleteSelectedAlternatif();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        fieldKosong();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        updateData();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void cariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cariActionPerformed
        // TODO add your handling code here:
        dataTable();
    }//GEN-LAST:event_cariActionPerformed

    private void tfCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfCariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfCariActionPerformed

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
            java.util.logging.Logger.getLogger(DataAlternatifPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DataAlternatifPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DataAlternatifPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DataAlternatifPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DataAlternatifPage().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel C1lbl;
    private javax.swing.JLabel C2lbl;
    private javax.swing.JLabel C3lbl;
    private javax.swing.JLabel C4lbl;
    private javax.swing.JLabel C5lbl;
    private javax.swing.JLabel C6lbl;
    private javax.swing.JLabel C7lbl;
    private javax.swing.JButton btnGetId;
    private javax.swing.JButton cari;
    private javax.swing.JComboBox<String> cbBoxC1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JComboBox<String> jComboBox7;
    private javax.swing.JLabel jIdAlt;
    private javax.swing.JLabel jLa1231;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel234;
    private javax.swing.JLabel jNama;
    private javax.swing.JLabel jNoKTP;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel jTLahir;
    private javax.swing.JLabel jTtl;
    private javax.swing.JTable tblAlt;
    private javax.swing.JTable tblBobot;
    private javax.swing.JTextField tfCari;
    private javax.swing.JButton tfCariWarga;
    private javax.swing.JTextField tfId;
    private javax.swing.JTextField tfIdStaff;
    private javax.swing.JTextField tfNama;
    private javax.swing.JTextField tfNamaStaff;
    private javax.swing.JTextField tfNik;
    private javax.swing.JTextField tfTLahir;
    private javax.swing.JTextField tfTTL;
    // End of variables declaration//GEN-END:variables
}
