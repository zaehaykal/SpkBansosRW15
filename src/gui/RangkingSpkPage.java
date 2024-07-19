/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import database.koneksi;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import popUp.popUpAlternatif;
import javax.swing.JFrame;
import popUp.popUpPenilaian;

/**
 *
 * @author zaeha
 */
public class RangkingSpkPage extends javax.swing.JFrame {

    private DefaultTableModel tabmode;
    private DefaultTableModel tabmodeAlt;
    private Connection conn = new koneksi().kon();
    DecimalFormat df = new DecimalFormat("#.#####");

    public popUpPenilaian penilaianPublik;
    public double[] nilaiVPublik;
    private String[] namaPublik;
    private String[] noKtpPublik;

    /**
     * Creates new form RangkingSpk
     */
    public RangkingSpkPage() {
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
            String sql = "SELECT id FROM hasil ORDER BY id DESC LIMIT 1"; // Mengambil ID terbesar
            ResultSet res = stat.executeQuery(sql);

            int newId = 1; // Default ID jika tabel kosong

            if (res.next()) {
                int id_rangking = res.getInt("id"); // Mengambil ID rangking terbesar
                newId = id_rangking + 1;
            }

            lblAutoId.setText("SPK" + String.format("%04d", newId)); // Set new ID to the label
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Auto Number Gagal: " + e.getMessage());
            e.printStackTrace(); // Menampilkan stack trace untuk debugging lebih lanjut
        }
    }

    private void resetTable() {
        dataTable();
    }

    public void getAlternatif(String no_ktp, String nama, String bobotValuesStr) {
        try {
            // Menambahkan nilai no_ktp dan nama ke tabmodeAlt
            tabmodeAlt.addRow(new Object[]{no_ktp, nama});

            // Membagi bobotValuesStr menjadi array berdasarkan pemisah koma
            String[] bobotValuesArray = bobotValuesStr.split(", ");

            // Membuat array untuk menampung nilai-nilai bobot yang akan dimasukkan ke dalam baris baru
            Object[] rowData = new Object[bobotValuesArray.length];

            // Menambahkan nilai bobot ke dalam array rowData
            for (int i = 0; i < bobotValuesArray.length; i++) {
                rowData[i] = bobotValuesArray[i];
            }

            // Menambahkan baris baru ke tabmode
            tabmode.addRow(rowData);

            // Menambahkan kolom baru jika jumlah kolom tidak cukup
            if (tabmode.getColumnCount() < bobotValuesArray.length) {
                for (int i = tabmode.getColumnCount(); i < bobotValuesArray.length; i++) {
                    tabmode.addColumn("Bobot " + (i + 1));
                }
            }

            // Mengatur model tabel untuk memperbarui tampilan
            tblAlt.setModel(tabmodeAlt);
            tblSPK.setModel(tabmode);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Data Gagal Diambil!");
            e.printStackTrace();
        }
    }

    protected void dataTable() {
        Object[] Baris = {"C1", "C2", "C3", "C4", "C5", "C6", "C7"};
        Object[] nama = {"NIK", "Nama"};
        tabmode = new DefaultTableModel(null, Baris);
        tabmodeAlt = new DefaultTableModel(null, nama) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; //To change body of generated methods, choose Tools | Templates.
            }

        };
        tblAlt.setModel(tabmodeAlt);
        tblSPK.setModel(tabmode);
    }

    protected void hapusRow() {
        try {
            int index = tblAlt.getSelectedRow();
            int indexSpk = tblSPK.getSelectedRow();

            if (index != -1 || indexSpk != -1) {
                tabmode.removeRow(index);
                tabmodeAlt.removeRow(index);
                tblSPK.setModel(tabmode);
                tblAlt.setModel(tabmodeAlt);
            } else {
                System.out.println("anjing");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Pilih Data Alternatif yang ingin dihapus");
        }
    }

    private void fetchCriteriaWeights(List<Double> bobotList, List<String> jenisKriteriaList) throws SQLException {
        String sqlBobotKriteria = "SELECT * FROM kriteria";
        Statement statBobotKriteria = conn.createStatement();
        ResultSet resBobotKriteria = statBobotKriteria.executeQuery(sqlBobotKriteria);

        while (resBobotKriteria.next()) {
            double bobotKriteria = resBobotKriteria.getDouble("bobot_kriteria");
            bobotList.add(bobotKriteria);

            String jenisKriteria = resBobotKriteria.getString("jenis_kriteria");
            jenisKriteriaList.add(jenisKriteria);

            System.out.println("Bobot dari kriteria: " + bobotKriteria);
            System.out.println("Jenis Kriteria: " + jenisKriteria);
        }
    }

    private double[] calculateNormalizedWeights(List<Double> bobotList) {
        double totalBobot = bobotList.stream().mapToDouble(Double::doubleValue).sum();
        double[] normalizedWeights = new double[bobotList.size()];

        for (int i = 0; i < bobotList.size(); i++) {
            normalizedWeights[i] = bobotList.get(i) / totalBobot;
            System.out.println("Bobot Sederhana untuk kriteria " + (i + 1) + ": " + df.format(normalizedWeights[i]));
        }
        return normalizedWeights;
    }

    private double[][] calculateNormalizedValues(int rowCount, int columnCount, int[][] nilaiC, double[] pembagiC) {
        double[][] normalized = new double[rowCount][columnCount];

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                normalized[i][j] = pembagiC[j] != 0 ? nilaiC[i][j] / pembagiC[j] : 0;
                System.out.println("Normalized C" + (j + 1) + " for row " + (i + 1) + ": " + df.format(normalized[i][j]));
            }
        }
        return normalized;
    }

    private double[][] calculateWeightedNormalizedMatrix(int rowCount, int columnCount, double[][] normalized, double[] normalizedWeights) {
        double[][] weightedNormalized = new double[rowCount][columnCount];

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                weightedNormalized[i][j] = normalized[i][j] * normalizedWeights[j];
                System.out.println("R[" + (i + 1) + "][" + (j + 1) + "]: " + df.format(weightedNormalized[i][j]));
            }
        }
        return weightedNormalized;
    }

    private void calculateIdealSolutions(int rowCount, int columnCount, double[][] weightedNormalized, double[] aPlus, double[] aMinus, List<String> jenisKriteriaList) {
        for (int j = 0; j < columnCount; j++) {
            String jenisKriteria = jenisKriteriaList.get(j);

            double max = Double.MIN_VALUE;
            double min = Double.MAX_VALUE;

            for (int i = 0; i < rowCount; i++) {
                if (weightedNormalized[i][j] > max) {
                    max = weightedNormalized[i][j];
                }
                if (weightedNormalized[i][j] < min) {
                    min = weightedNormalized[i][j];
                }
            }

            // Determine aPlus and aMinus based on the type of criterion
            if (jenisKriteria.equalsIgnoreCase("benefit")) {
                // For benefit criteria, the ideal positive solution is the maximum value
                aPlus[j] = max;
                // The ideal negative solution is the minimum value
                aMinus[j] = min;
            } else if (jenisKriteria.equalsIgnoreCase("cost")) {
                // For cost criteria, the ideal positive solution is the minimum value
                aPlus[j] = min;
                // The ideal negative solution is the maximum value
                aMinus[j] = max;
            }

            // Print the ideal solutions
            System.out.println("Ideal positive solution (aPlus) for criterion " + (j + 1) + " (" + jenisKriteria + "): " + aPlus[j]);
            System.out.println("Ideal negative solution (aMinus) for criterion " + (j + 1) + " (" + jenisKriteria + "): " + aMinus[j]);
        }
    }

    private double[] calculateDistances(int rowCount, int columnCount, double[][] weightedNormalized, double[] idealSolution, List<String> jenisKriteriaList, boolean isPositive) {
        double[] distances = new double[rowCount];

        // Menyimpan hasil jarak ke solusi ideal positif dan negatif
        double[] dPlus = new double[rowCount];
        double[] dMinus = new double[rowCount];

        for (int i = 0; i < rowCount; i++) {
            double sumPlus = 0.0;
            double sumMinus = 0.0;

            System.out.println("Menghitung jarak untuk baris " + (i + 1) + ":");

            for (int j = 0; j < columnCount; j++) {
                double diff = weightedNormalized[i][j] - idealSolution[j];

                // Cetak informasi debug
                System.out.println("  Kolom " + (j + 1) + ":");
                System.out.println("    Nilai Normalisasi yang Dibobot: " + weightedNormalized[i][j]);
                System.out.println("    Solusi Ideal: " + idealSolution[j]);
                System.out.println("    Perbedaan Awal: " + diff);

                if (jenisKriteriaList.get(j).equalsIgnoreCase("cost")) {
                    // Untuk kriteria biaya
                    if (isPositive) {
                        // Hitung jarak ke solusi ideal positif untuk kriteria biaya
                        diff = weightedNormalized[i][j] - idealSolution[j];
                        if (diff < 0) {
                            System.out.println("    Peringatan: Jarak D+ untuk kriteria biaya adalah negatif.");
                        }
                        sumPlus += Math.pow(diff, 2);
                    } else {
                        // Hitung jarak ke solusi ideal negatif untuk kriteria biaya
                        diff = idealSolution[j] - weightedNormalized[i][j];
                        if (diff < 0) {
                            System.out.println("    Peringatan: Jarak D- untuk kriteria biaya adalah negatif.");
                        }
                        sumMinus += Math.pow(diff, 2);
                    }
                } else {
                    // Untuk kriteria manfaat
                    if (isPositive) {
                        // Hitung jarak ke solusi ideal positif untuk kriteria manfaat
                        diff = idealSolution[j] - weightedNormalized[i][j];
                        if (diff < 0) {
                            System.out.println("    Peringatan: Jarak D+ untuk kriteria manfaat adalah negatif.");
                        }
                        sumPlus += Math.pow(diff, 2);
                    } else {
                        // Hitung jarak ke solusi ideal negatif untuk kriteria manfaat
                        diff = weightedNormalized[i][j] - idealSolution[j];
                        if (diff < 0) {
                            System.out.println("    Peringatan: Jarak D- untuk kriteria manfaat adalah negatif.");
                        }
                        sumMinus += Math.pow(diff, 2);
                    }
                }

                // Informasi debug tambahan
                System.out.println("    Perbedaan yang Disesuaikan: " + diff);
                System.out.println("    Kuadrat Perbedaan: " + Math.pow(diff, 2));
                System.out.println("    Jumlah Sementara: " + (isPositive ? sumPlus : sumMinus));
            }

            dPlus[i] = Math.sqrt(sumPlus);
            dMinus[i] = Math.sqrt(sumMinus);

            // Cetak jarak yang dihitung
            System.out.println("Jarak D+ untuk baris " + (i + 1) + ": " + dPlus[i]);
            System.out.println("Jarak D- untuk baris " + (i + 1) + ": " + dMinus[i]);

            distances[i] = isPositive ? dPlus[i] : dMinus[i];
        }
        return distances;
    }

    private double[] calculatePreferenceValues(int rowCount, double[] dPlus, double[] dMinus) {
        double[] v = new double[rowCount];

        for (int i = 0; i < rowCount; i++) {
            try {
                // Periksa jika dPlus[i] + dMinus[i] adalah nol untuk menghindari pembagian dengan nol
                if (dPlus[i] + dMinus[i] == 0) {
                    v[i] = 0; // Atau bisa menggunakan nilai default lain yang sesuai, seperti NaN
                    System.out.println("Nilai Preferensi untuk Alternatif " + (i + 1) + ": Tidak dapat dihitung (Pembagi nol).");
                } else {
                    // Hitung nilai preferensi
                    v[i] = dMinus[i] / (dPlus[i] + dMinus[i]);
                    System.out.println("Nilai Preferensi untuk Alternatif " + (i + 1) + ": " + df.format(v[i]));
                }
            } catch (ArithmeticException e) {
                System.out.println("Gagal menghitung nilai preferensi untuk Alternatif " + (i + 1) + ": Pembagian dengan nol.");
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("Gagal menghitung nilai preferensi untuk Alternatif " + (i + 1) + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Setelah loop selesai, masukkan nilai ke dalam nilaiVPublik
        nilaiVPublik = v.clone();
        Arrays.sort(nilaiVPublik);
        printArray(nilaiVPublik);

        return v;
    }

    private void determineBestAlternative(int rowCount, double[] preferenceValues) {
        double maxV = Double.MIN_VALUE;
        int idxSolusiTerbaik = -1;

        for (int i = 0; i < rowCount; i++) {
            if (preferenceValues[i] > maxV) {
                maxV = preferenceValues[i];
                idxSolusiTerbaik = i;
            }
        }
        System.out.println("Solusi Terbaik adalah Alternatif " + (idxSolusiTerbaik + 1) + " dengan Nilai Preferensi " + df.format(maxV));
    }

   protected void penilaianSPK() {
    try {
        // Mengambil bobot kriteria dan jenis kriteria dari database
        List<Double> bobotList = new ArrayList<>();
        List<String> jenisKriteriaList = new ArrayList<>();
        fetchCriteriaWeights(bobotList, jenisKriteriaList);

        int rowCountSPK = tblSPK.getRowCount();
        int columnCountSPK = tblSPK.getColumnCount(); // Jumlah kolom sebelum penambahan kolom "nilai V" dan "rangking"

        double[] normalizedWeights = calculateNormalizedWeights(bobotList);

        int[][] nilaiC = new int[rowCountSPK][columnCountSPK];
        double[] sumC = new double[columnCountSPK];
        for (int i = 0; i < rowCountSPK; i++) {
            for (int j = 0; j < columnCountSPK; j++) {
                try {
                    nilaiC[i][j] = Integer.parseInt(tblSPK.getValueAt(i, j).toString());
                    sumC[j] += Math.pow(nilaiC[i][j], 2);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Format angka tidak valid pada baris " + (i + 1) + ", kolom " + (j + 1) + ": " + e.getMessage());
                    return;
                }
            }
        }

        double[] pembagiC = new double[columnCountSPK];
        for (int j = 0; j < columnCountSPK; j++) {
            pembagiC[j] = Math.sqrt(sumC[j]);
        }

        // Menghitung nilai normalisasi
        double[][] normalized = calculateNormalizedValues(rowCountSPK, columnCountSPK, nilaiC, pembagiC);
        double[][] weightedNormalized = calculateWeightedNormalizedMatrix(rowCountSPK, columnCountSPK, normalized, normalizedWeights);

        double[] aPlus = new double[columnCountSPK];
        double[] aMinus = new double[columnCountSPK];
        calculateIdealSolutions(rowCountSPK, columnCountSPK, weightedNormalized, aPlus, aMinus, jenisKriteriaList);

        double[] dPlus = calculateDistances(rowCountSPK, columnCountSPK, weightedNormalized, aPlus, jenisKriteriaList, true);
        double[] dMinus = calculateDistances(rowCountSPK, columnCountSPK, weightedNormalized, aMinus, jenisKriteriaList, false);

        double[] preferenceValues = calculatePreferenceValues(rowCountSPK, dPlus, dMinus);

        // Mengisi nilai "nilai V" dan "rangking" pada tabel tblSPK
        double[] tempPreferenceValues = preferenceValues.clone();
        Arrays.sort(tempPreferenceValues);

        int altRowCount = tblAlt.getRowCount();

        // Inisialisasi array untuk menyimpan data alternatif (ID, Nama Karyawan, Tanggal)
        noKtpPublik = new String[altRowCount];
        namaPublik = new String[altRowCount];

        for (int i = 0; i < altRowCount; i++) {
            Object nama = tblAlt.getValueAt(i, 1);
            namaPublik[i] = nama.toString();
            System.out.printf("Nama [%d] = %s%n", i, namaPublik[i]);
        }

        for (int i = 0; i < altRowCount; i++) {
            Object id = tblAlt.getValueAt(i, 0);
            noKtpPublik[i] = id.toString(); // Ambil ID alternatif
            System.out.printf("No KTP [%d] = %s%n", i, noKtpPublik[i]);
        }
        System.out.println("Data dari tblAlt berhasil dimuat.");

        int[] rankings = new int[rowCountSPK];
        for (int i = 0; i < rowCountSPK; i++) {
            int rank = Arrays.binarySearch(tempPreferenceValues, preferenceValues[i]);
            rankings[i] = rowCountSPK - rank;
        }

        // Simpan hasil ke database (opsional, tergantung kebutuhan)
        // simpanKeDatabase();

        JOptionPane.showMessageDialog(null, "Berhasil");
        autoID();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Gagal: " + e.getMessage());
        e.printStackTrace();
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Format angka tidak valid: " + e.getMessage());
        e.printStackTrace();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Gagal: " + e.getMessage());
        e.printStackTrace();
    }
}

    private static void printArray(double[] array) {
        System.out.println("Array Preferensi:");
        for (double value : array) {
            System.out.printf("%.2f\t", value);
        }
        System.out.println();
    }

    protected void simpanKeDatabase() {
        String sqlHasil = "INSERT INTO hasil (tanggal) VALUES (?)";
        String sqlRangking = "INSERT INTO hasil_rangking (id_hasil, no_ktp, nama, nilaiV, rangking) VALUES (?, ?, ?, ?, ?)";

        try {
            // Ambil tanggal dari komponen UI Anda, misalnya jSpinner1
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String tanggalFormatted = sdf.format(jSpinner1.getValue());

            // Simpan ke tabel hasil
            PreparedStatement psHasil = conn.prepareStatement(sqlHasil, Statement.RETURN_GENERATED_KEYS);
            psHasil.setString(1, tanggalFormatted);
            psHasil.executeUpdate();

            ResultSet rs = psHasil.getGeneratedKeys();
            int idHasil = 0;
            if (rs.next()) {
                idHasil = rs.getInt(1);
            }

            // Simpan ke tabel hasil_rangking
            DefaultTableModel modelSPK = (DefaultTableModel) tblSPK.getModel();
            DefaultTableModel modelAlt = (DefaultTableModel) tblAlt.getModel();
            PreparedStatement psRangking = conn.prepareStatement(sqlRangking);
            for (int i = 0; i < modelSPK.getRowCount(); i++) {
                String noKtp = modelAlt.getValueAt(i, 0).toString();
                String nama = modelAlt.getValueAt(i, 1).toString();
                double nilaiV = Double.parseDouble(modelSPK.getValueAt(i, modelSPK.getColumnCount() - 2).toString().replace(",", ".")); // Ambil nilai V dari kolom yang benar
                int rangking = Integer.parseInt(modelSPK.getValueAt(i, modelSPK.getColumnCount() - 1).toString()); // Ambil rangking dari kolom yang benar
                psRangking.setInt(1, idHasil);
                psRangking.setString(2, noKtp);
                psRangking.setString(3, nama);
                psRangking.setDouble(4, nilaiV);
                psRangking.setInt(5, rangking);
                psRangking.addBatch();
            }
            psRangking.executeBatch();

            JOptionPane.showMessageDialog(null, "Data berhasil disimpan ke database");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Format angka tidak valid: " + e.getMessage());
        }
    }

// Tambahkan metode ini untuk mendapatkan peringkat berdasarkan nilai preferensi
    private int getRanking(int index, double[] preferenceValues) {
        double[] sortedValues = preferenceValues.clone();
        Arrays.sort(sortedValues);
        int rank = 1;
        for (int i = sortedValues.length - 1; i >= 0; i--) {
            if (preferenceValues[index] == sortedValues[i]) {
                return rank;
            }
            rank++;
        }
        return rank;
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
        jSpinner1 = new javax.swing.JSpinner();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSPK = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblAlt = new javax.swing.JTable();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Halaman Penilaian");
        setMinimumSize(new java.awt.Dimension(1300, 850));
        setPreferredSize(new java.awt.Dimension(1221, 800));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setMaximumSize(new java.awt.Dimension(2000, 2000));
        jPanel3.setPreferredSize(new java.awt.Dimension(1500, 2000));

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

        jSpinner1.setModel(new javax.swing.SpinnerDateModel());

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblAutoId, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                        .addComponent(btnGetID)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAutoId)
                    .addComponent(btnGetID))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        tblSPK.setMaximumSize(new java.awt.Dimension(2000, 64));
        jScrollPane1.setViewportView(tblSPK);

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
        jScrollPane2.setViewportView(tblAlt);

        jButton5.setBackground(new java.awt.Color(204, 204, 204));
        jButton5.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jButton5.setText("Reset");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(204, 204, 204));
        jButton6.setFont(new java.awt.Font("Tw Cen MT", 1, 16)); // NOI18N
        jButton6.setText("Simpan");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 200, Short.MAX_VALUE)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 388, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(37, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N
        jLabel1.setText("PENILAIAN BANTUAN SOSIAL RW 15");

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
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1))
                .addGap(750, 750, 750))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(481, 481, 481)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(53, 53, 53)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1271, 1271, 1271))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 2024, Short.MAX_VALUE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, 2002, 2002, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        popUpAlternatif pWarga = new popUpAlternatif();
        pWarga.alternatif = this;
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

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        dataTable();
        autoID();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        System.out.println(Arrays.toString(namaPublik));
//        System.out.println(Arrays.deepToString(nilaiVPublik));
        System.out.println(Arrays.toString(noKtpPublik));
        popUpPenilaian popUp = new popUpPenilaian();

        popUp.setNilai(nilaiVPublik, namaPublik, noKtpPublik);
        if (nilaiVPublik != null) {
            popUp.penilaianPublik = this;
            popUp.setVisible(true);
            popUp.setResizable(false);
        } else {
            System.out.println("nilai V Masih Kosong " + nilaiVPublik);
        }
    }//GEN-LAST:event_jButton6ActionPerformed

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
            java.util.logging.Logger.getLogger(RangkingSpkPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RangkingSpkPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RangkingSpkPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RangkingSpkPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RangkingSpkPage().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGetID;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JLabel lblAutoId;
    private javax.swing.JTable tblAlt;
    private javax.swing.JTable tblSPK;
    // End of variables declaration//GEN-END:variables
}
