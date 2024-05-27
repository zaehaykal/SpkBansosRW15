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
import java.util.ArrayList;

/**
 *
 * @author zaeha
 */
public class RangkingSpk extends javax.swing.JFrame {
private DefaultTableModel tabmode;
private DefaultTableModel tabmodeAlt;
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
            tabmodeAlt.addRow(new Object[] {no_ktp,nama});
            tabmode.addRow(new Object[] {});
            
            tblSPK.setModel(tabmode);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Data Gagal Diambil!");
            System.out.println(e);
        }
    }
    
        protected void dataTable(){
        Object[] Baris = {"C1","C2","C3","C4","C5"};
        Object[] nama = {"NIK","Nama"};
        tabmode = new DefaultTableModel(null, Baris);
        tabmodeAlt = new DefaultTableModel(null, nama){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; //To change body of generated methods, choose Tools | Templates.
            }
            
        };
        tblAlt.setModel(tabmodeAlt);
        tblSPK.setModel(tabmode);
    }
    
    protected void hapusRow(){
        int index = tblSPK.getSelectedRow();
        tabmode.removeRow(index);
        tabmodeAlt.removeRow(index);
        tblSPK.setModel(tabmode);
        tblAlt.setModel(tabmodeAlt);
        
    }
    

//    
//    protected void penilaianSPK() {
//        try {
//            // Query SQL
//            String penilaianSql = "INSERT INTO penilaian VALUES (?, ?, ?, ?, ?, ?)";
//            PreparedStatement stat2 = conn.prepareStatement(penilaianSql);
//
//            // Mendapatkan jumlah baris dari tabel
//            int tbSPK = tblSPK.getRowCount();
//            int tbAlt = tblAlt.getRowCount();
//
//            double sumC1 = 0, sumC2 = 0, sumC3 = 0, sumC4 = 0, sumC5 = 0;
//            DecimalFormat df = new DecimalFormat("#.#####");
//            
//            // Loop melalui setiap baris
//            for (int i = 0; i < tbSPK; i++) {
//                // Mendapatkan nilai dari tabel dan mengonversinya menjadi integer
//                int c1 = Integer.parseInt(tblSPK.getValueAt(i, 0).toString());
//                int c2 = Integer.parseInt(tblSPK.getValueAt(i, 1).toString());
//                int c3 = Integer.parseInt(tblSPK.getValueAt(i, 2).toString());
//                int c4 = Integer.parseInt(tblSPK.getValueAt(i, 3).toString());
//                int c5 = Integer.parseInt(tblSPK.getValueAt(i, 4).toString());
//
//                // Menghitung jumlah kuadrat untuk setiap c
//                sumC1 += Math.pow(c1, 2);
//                sumC2 += Math.pow(c2, 2);
//                sumC3 += Math.pow(c3, 2);
//                sumC4 += Math.pow(c4, 2);
//                sumC5 += Math.pow(c5, 2);
//
//                // Menghitung pembagi untuk setiap c
//                double pembagiC1 = Math.sqrt(sumC1);
//                double pembagiC2 = Math.sqrt(sumC2);
//                double pembagiC3 = Math.sqrt(sumC3);
//                double pembagiC4 = Math.sqrt(sumC4);
//                double pembagiC5 = Math.sqrt(sumC5);
//
//                // Mengatur nilai pembagi di tfCovba yang sesuai
//                tfCovba1.setText(df.format(pembagiC1));
//                tfCovba2.setText(df.format(pembagiC2));
//                tfCovba3.setText(df.format(pembagiC3));
//                tfCovba4.setText(df.format(pembagiC4));
//                tfCovba5.setText(df.format(pembagiC5));
//
//                // Output pembagi ke konsol
//                System.out.println("Pembagi C1: " + pembagiC1);
//                System.out.println("Pembagi C2: " + pembagiC2);
//                System.out.println("Pembagi C3: " + pembagiC3);
//                System.out.println("Pembagi C4: " + pembagiC4);
//                System.out.println("Pembagi C5: " + pembagiC5);
//
//
//            }
//
//        // Menampilkan pesan sukses
//        JOptionPane.showMessageDialog(null, "Berhasil");
//    } catch (SQLException e) {
//        // Menangani exceptions SQL
//        JOptionPane.showMessageDialog(null, "Gagal: " + e.getMessage());
//    } catch (NumberFormatException e) {
//        // Menangani exceptions format angka
//        JOptionPane.showMessageDialog(null, "Format angka tidak valid: " + e.getMessage());
//    } catch (Exception e) {
//        // Menangani exceptions lainnya
//        JOptionPane.showMessageDialog(null, "Gagal: " + e.getMessage());
//    }
//}
//    
//    protected void penilaianSPK() {
//    try {
//        // Query SQL untuk mengambil bobot dari tabel bobot_kriteria
//        String sqlBobotKriteria = "SELECT * FROM kriteria";
//        Statement statBobotKriteria = conn.createStatement();
//        ResultSet resBobotKriteria = statBobotKriteria.executeQuery(sqlBobotKriteria);
//
//        // Mendapatkan jumlah baris dari tabel
//        int tbSPK = tblSPK.getRowCount();
//        int tbAlt = tblAlt.getRowCount();
//
//        double totalBobot = 0.0; // Variabel untuk menyimpan total bobot
//        ArrayList<Double> bobotList = new ArrayList<>(); // ArrayList untuk menyimpan nilai bobot
//
//        double[] sumC = new double[5]; // Array untuk menyimpan nilai sumC
//        double[] pembagiC = new double[5]; // Array untuk menyimpan nilai pembagiC
//        double[][] normalized = new double[tbSPK][5]; // Array untuk menyimpan nilai normalized
//        int[][] nilaiC = new int[tbSPK][5]; // Array untuk menyimpan nilai c1, c2, c3, c4, c5
//        DecimalFormat df = new DecimalFormat("#.#####");
//
//        // Mengambil bobot dari tabel bobot_kriteria dan menghitung total bobot
//        while (resBobotKriteria.next()) {
//            double bobotKriteria = resBobotKriteria.getDouble("bobot_kriteria");
//            
//            bobotList.add(bobotKriteria);
//            totalBobot += bobotKriteria;
//            System.out.println("Bobot dari kriteria: " + bobotKriteria);
//        }
//
//        // Membuat array untuk bobot yang disederhanakan
//        double[] sederhanaBobot = new double[bobotList.size()];
//
//        // Melakukan perhitungan bobot yang disederhanakan
//        for (int i = 0; i < bobotList.size(); i++) {
//            sederhanaBobot[i] = totalBobot / bobotList.get(i);
//            
//            System.out.println("Bobot Sederhana untuk kriteria " + (i+1) + ": " + df.format(sederhanaBobot[i]));
//        }
//
//        // Loop melalui setiap baris
//        for (int i = 0; i < tbSPK; i++) {
//            // Mendapatkan nilai dari tabel dan mengonversinya menjadi integer
//            nilaiC[i][0] = Integer.parseInt(tblSPK.getValueAt(i, 0).toString());
//            nilaiC[i][1] = Integer.parseInt(tblSPK.getValueAt(i, 1).toString());
//            nilaiC[i][2] = Integer.parseInt(tblSPK.getValueAt(i, 2).toString());
//            nilaiC[i][3] = Integer.parseInt(tblSPK.getValueAt(i, 3).toString());
//            nilaiC[i][4] = Integer.parseInt(tblSPK.getValueAt(i, 4).toString());
//
//            // Menghitung jumlah kuadrat untuk setiap c
//            sumC[0] += Math.pow(nilaiC[i][0], 2);
//            sumC[1] += Math.pow(nilaiC[i][1], 2);
//            sumC[2] += Math.pow(nilaiC[i][2], 2);
//            sumC[3] += Math.pow(nilaiC[i][3], 2);
//            sumC[4] += Math.pow(nilaiC[i][4], 2);
//        }
//
//        // Menghitung pembagi untuk setiap c
//        for (int j = 0; j < 5; j++) {
//            pembagiC[j] = Math.sqrt(sumC[j]);
//        }
//
//        // Loop melalui setiap baris untuk menghitung nilai normalized
//        for (int i = 0; i < tbSPK; i++) {
//            // Menghitung nilai normalized untuk setiap c
//            for (int k = 0; k < 5; k++) {
//                normalized[i][k] = pembagiC[k] != 0 ? nilaiC[i][k] / pembagiC[k] : 0;
//                System.out.println("Normalized C" + (k+1) + " for row " + (i+1) + ": " + df.format(normalized[i][k]));
//            }
//        }
//
//        // Langkah 1: Menghitung Matrix Normalisasi Terbobot (R)
//        double[][] terbobot = new double[tbSPK][5];
//        for (int i = 0; i < tbSPK; i++) {
//            for (int j = 0; j < 5; j++) {
//                terbobot[i][j] = normalized[i][j] * sederhanaBobot[j];
//            }
//        }
//
//        // Langkah 2: Menghitung Solusi Ideal Positif (A+) dan Solusi Ideal Negatif (A-)
//        double[] aPlus = new double[5];
//        double[] aMinus = new double[5];
//        for (int j = 0; j < 5; j++) {
//            double max = Double.MIN_VALUE;
//            double min = Double.MAX_VALUE;
//            for (int i = 0; i < tbSPK; i++) {
//                if (terbobot[i][j] > max) {
//                    max = terbobot[i][j];
//                }
//                if (terbobot[i][j] < min) {
//                    min = terbobot[i][j];
//                }
//            }
//            aPlus[j] = max;
//            aMinus[j] = min;
//        }
//
//        // Langkah 3: Menghitung Jarak Alternatif Terhadap Solusi Ideal Positif (D+)
//        double[] dPlus = new double[tbSPK];
//        for (int i = 0; i < tbSPK; i++) {
//            double sum = 0.0;
//            for (int j = 0; j < 5; j++) {
//                sum += Math.pow(terbobot[i][j] - aPlus[j], 2);
//            }
//            dPlus[i] = Math.sqrt(sum);
//        }
//
//        // Langkah 4: Menghitung Jarak Alternatif Terhadap Solusi Ideal Negatif (D-)
//        double[] dMinus = new double[tbSPK];
//        for (int i = 0; i < tbSPK; i++) {
//            double sum = 0.0;
//            for (int j = 0; j < 5; j++) {
//                sum += Math.pow(terbobot[i][j] - aMinus[j], 2);
//            }
//            dMinus[i] = Math.sqrt(sum);
//        }
//
//        // Langkah 5: Menghitung Nilai Preferensi (V)
//        double[] v = new double[tbSPK];
//        for (int i = 0; i < tbSPK; i++) {
//            v[i] = dMinus[i] / (dPlus[i] + dMinus[i]);
//        System.out.println("Nilai Preferensi untuk Alternatif " + (i + 1) + ": " + df.format(v[i]));
//        }
//
//        // Menampilkan alternatif dengan nilai preferensi tertinggi sebagai solusi terbaik
//        double maxV = Double.MIN_VALUE;
//        int idxSolusiTerbaik = -1;
//        for (int i = 0; i < tbSPK; i++) {
//            if (v[i] > maxV) {
//                maxV = v[i];
//                idxSolusiTerbaik = i;
//            }
//        }
//        System.out.println("Solusi Terbaik adalah Alternatif " + (idxSolusiTerbaik + 1) + " dengan Nilai Preferensi " + df.format(maxV));
//
//        // Menampilkan pesan sukses
//        JOptionPane.showMessageDialog(null, "Berhasil");
//
//    } catch (SQLException e) {
//        // Menangani exceptions SQL
//        JOptionPane.showMessageDialog(null, "Gagal: " + e.getMessage());
//    } catch (NumberFormatException e) {
//        // Menangani exceptions format angka
//        JOptionPane.showMessageDialog(null, "Format angka tidak valid: " + e.getMessage());
//    } catch (Exception e) {
//        // Menangani exceptions lainnya
//        JOptionPane.showMessageDialog(null, "Gagal: " + e.getMessage());
//    }
//}
//
protected void penilaianSPK() {
    try {
        // Query SQL untuk mengambil bobot dari tabel bobot_kriteria
        String sqlBobotKriteria = "SELECT * FROM kriteria";
        Statement statBobotKriteria = conn.createStatement();
        ResultSet resBobotKriteria = statBobotKriteria.executeQuery(sqlBobotKriteria);

        // Mendapatkan jumlah baris dari tabel
        int tbcSPK =tblSPK.getColumnCount();
        int tbSPK = tblSPK.getRowCount();
        int tbAlt = tblAlt.getRowCount();

        double totalBobot = 0.0; // Variabel untuk menyimpan total bobot
        ArrayList<Double> bobotList = new ArrayList<>(); // ArrayList untuk menyimpan nilai bobot
        ArrayList<String> jenisKriteriaList = new ArrayList<>(); // ArrayList untuk menyimpan jenis kriteria


        double[] sumC = new double[tbcSPK]; // Array untuk menyimpan nilai sumC
        double[] pembagiC = new double[tbcSPK]; // Array untuk menyimpan nilai pembagiC
        double[][] normalized = new double[tbSPK][tbcSPK]; // Array untuk menyimpan nilai normalized
        int[][] nilaiC = new int[tbSPK][tbcSPK]; // Array untuk menyimpan nilai c1, c2, c3, c4, c5
        DecimalFormat df = new DecimalFormat("#.#####");

        // Mengambil bobot dari tabel bobot_kriteria dan menghitung total bobot
        while (resBobotKriteria.next()) {
            double bobotKriteria = resBobotKriteria.getDouble("bobot_kriteria");
            
            bobotList.add(bobotKriteria);
            totalBobot += bobotKriteria;

            String jenisKriteria = resBobotKriteria.getString("jenis_kriteria");
            jenisKriteriaList.add(jenisKriteria);

            System.out.println("Bobot dari kriteria: " + bobotKriteria);
            System.out.println("Jenis Kriteria: " + jenisKriteria);
            System.out.println(totalBobot);
        }

        // Membuat array untuk bobot yang disederhanakan
        double[] sederhanaBobot = new double[bobotList.size()];

        // Melakukan perhitungan bobot yang disederhanakan
        for (int i = 0; i < bobotList.size(); i++) {
            sederhanaBobot[i] = bobotList.get(i)/totalBobot;
            System.out.println("total bobot");
            System.out.println("peler kuda = "+sederhanaBobot[i]);
            
            System.out.println("Bobot Sederhana untuk kriteria " + (i+1) + ": " + df.format(sederhanaBobot[i]));
        }

        // Loop melalui setiap baris
        for (int i = 0; i < tbSPK; i++) {
            // Mendapatkan nilai dari tabel dan mengonversinya menjadi integer
            nilaiC[i][0] = Integer.parseInt(tblSPK.getValueAt(i, 0).toString());
            nilaiC[i][1] = Integer.parseInt(tblSPK.getValueAt(i, 1).toString());
            nilaiC[i][2] = Integer.parseInt(tblSPK.getValueAt(i, 2).toString());
            nilaiC[i][3] = Integer.parseInt(tblSPK.getValueAt(i, 3).toString());
            nilaiC[i][4] = Integer.parseInt(tblSPK.getValueAt(i, 4).toString());

            // Menghitung jumlah kuadrat untuk setiap c
            sumC[0] += Math.pow(nilaiC[i][0], 2);
            sumC[1] += Math.pow(nilaiC[i][1], 2);
            sumC[2] += Math.pow(nilaiC[i][2], 2);
            sumC[3] += Math.pow(nilaiC[i][3], 2);
            sumC[4] += Math.pow(nilaiC[i][4], 2);
        }

        // Menghitung pembagi untuk setiap c
        for (int j = 0; j < tbcSPK; j++) {
            pembagiC[j] = Math.sqrt(sumC[j]);
        }

        // Loop melalui setiap baris untuk menghitung nilai normalized
        for (int i = 0; i < tbSPK; i++) {
            // Menghitung nilai normalized untuk setiap c
            for (int k = 0; k < 5; k++) {
                normalized[i][k] = pembagiC[k] != 0 ? nilaiC[i][k] / pembagiC[k] : 0;
                System.out.println("Normalized C" + (k+1) + " for row " + (i+1) + ": " + df.format(normalized[i][k]));
            }
        }

        // Langkah 1: Menghitung Matrix Normalisasi Terbobot (R)
        double[][] terbobot = new double[tbSPK][tbcSPK];
        for (int i = 0; i < tbSPK; i++) {
            for (int j = 0; j < tbcSPK; j++) {
                terbobot[i][j] = normalized[i][j] * sederhanaBobot[j];
                System.out.println("R[" + (i+1) + "][" + (j+1) + "]: " + df.format(terbobot[i][j]));
            }
        }

        // Langkah 2: Menghitung Solusi Ideal Positif (A+) dan Solusi Ideal Negatif (A-)
        double[] aPlus = new double[tbcSPK];
        double[] aMinus = new double[tbcSPK];
        for (int j = 0; j < tbcSPK; j++) {
            double max = Double.MIN_VALUE;
            double min = Double.MAX_VALUE;
            for (int i = 0; i < tbSPK; i++) {
                if (terbobot[i][j] > max) {
                    max = terbobot[i][j];
                }
                if (terbobot[i][j] < min) {
                    min = terbobot[i][j];
                }
            }
            aPlus[j] = max;
            aMinus[j] = min;
        }

        System.out.println("Solusi Ideal Positif (A+):");
        for (int j = 0; j < tbcSPK; j++) {
            System.out.println("A+[" + (j+1) + "]: " + df.format(aPlus[j]));
        }

        System.out.println("Solusi Ideal Negatif (A-):");
        for (int j = 0; j < tbcSPK; j++) {
            System.out.println("A-[" + (j+1) + "]: " + df.format(aMinus[j]));
        }

        // Langkah 3: Menghitung Jarak Alternatif Terhadap Solusi Ideal Positif (D+)
double[] dPlus = new double[tbSPK];
for (int i = 0; i < tbSPK; i++) {
    double sum = 0.0;
    for (int j = 0; j < tbcSPK; j++) {
        if (jenisKriteriaList.get(j).equalsIgnoreCase("benefit")) {
            sum += Math.pow((terbobot[i][j] - aPlus[j]), 2);
        } else if (jenisKriteriaList.get(j).equalsIgnoreCase("cost")) {
            sum += Math.pow((terbobot[i][j] - aMinus[j]), 2);
        }
    }
    dPlus[i] = Math.sqrt(sum);
    System.out.println("Jarak Alternatif Terhadap Solusi Ideal Positif (D+) untuk Alternatif " + (i + 1) + ": " + df.format(dPlus[i]));
}

// Langkah 4: Menghitung Jarak Alternatif Terhadap Solusi Ideal Negatif (D-)
double[] dMinus = new double[tbSPK];
for (int i = 0; i < tbSPK; i++) {
    double sum = 0.0;
    for (int j = 0; j < tbcSPK; j++) {
        if (jenisKriteriaList.get(j).equalsIgnoreCase("benefit")) {
            sum += Math.pow((terbobot[i][j] - aMinus[j]), 2);
        } else if (jenisKriteriaList.get(j).equalsIgnoreCase("cost")) {
            sum += Math.pow((terbobot[i][j] - aPlus[j]), 2);
        }
    }
    dMinus[i] = Math.sqrt(sum);
    System.out.println("Jarak Alternatif Terhadap Solusi Ideal Negatif (D-) untuk Alternatif " + (i + 1) + ": " + df.format(dMinus[i]));
}

        // Langkah 5: Menghitung Nilai Preferensi (V)
        double[] v = new double[tbSPK];
        for (int i = 0; i < tbSPK; i++) {
            try {
                v[i] = dMinus[i] / (dPlus[i] + dMinus[i]);
                System.out.println("Nilai Preferensi untuk Alternatif " + (i + 1) + ": " + df.format(v[i]));
            } catch (ArithmeticException e) {
                System.out.println("Gagal menghitung nilai preferensi untuk Alternatif " + (i + 1) + ": Pembagian dengan nol.");
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("Gagal menghitung nilai preferensi untuk Alternatif " + (i + 1) + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Menampilkan alternatif dengan nilai preferensi tertinggi sebagai solusi terbaik
        double maxV = Double.MIN_VALUE;
        int idxSolusiTerbaik = -1;
        for (int i = 0; i < tbSPK; i++) {
            if (v[i] > maxV) {
                maxV = v[i];
                idxSolusiTerbaik = i;
            }
        }
        System.out.println("Solusi Terbaik adalah Alternatif " + (idxSolusiTerbaik + 1) + " dengan Nilai Preferensi " + df.format(maxV));

        // Menampilkan pesan sukses
        JOptionPane.showMessageDialog(null, "Berhasil");

    } catch (SQLException e) {
        // Menangani exceptions SQL
        JOptionPane.showMessageDialog(null, "Gagal: " + e.getMessage());
    } catch (NumberFormatException e) {
        // Menangani exceptions format angka
        JOptionPane.showMessageDialog(null, "Format angka tidak valid: " + e.getMessage());
    } catch (Exception e) {
        // Menangani exceptions lainnya
        JOptionPane.showMessageDialog(null, "Gagal: " + e.getMessage());
    }
}


//protected void penilaianSPK() {
//    try {
//        // Query SQL untuk mengambil bobot dari tabel bobot_kriteria
//        String sqlBobotKriteria = "SELECT * FROM kriteria";
//        Statement statBobotKriteria = conn.createStatement();
//        ResultSet resBobotKriteria = statBobotKriteria.executeQuery(sqlBobotKriteria);
//
//        // Mendapatkan jumlah baris dari tabel
//        int tbSPK = tblSPK.getRowCount();
//        int tbAlt = tblAlt.getRowCount();
//
//        double totalBobot = 0.0; // Variabel untuk menyimpan total bobot
//        ArrayList<Double> bobotList = new ArrayList<>(); // ArrayList untuk menyimpan nilai bobot
//
//        double[] sumC = new double[5]; // Array untuk menyimpan nilai sumC
//        double[] pembagiC = new double[5]; // Array untuk menyimpan nilai pembagiC
//        double[][] normalized = new double[tbSPK][5]; // Array untuk menyimpan nilai normalized
//        int[][] nilaiC = new int[tbSPK][5]; // Array untuk menyimpan nilai c1, c2, c3, c4, c5
//        DecimalFormat df = new DecimalFormat("#.#####");
//
//        // Mengambil bobot dari tabel bobot_kriteria dan menghitung total bobot
//        while (resBobotKriteria.next()) {
//            double bobotKriteria = resBobotKriteria.getDouble("bobot_kriteria");
//            
//            bobotList.add(bobotKriteria);
//            totalBobot += bobotKriteria;
//            System.out.println("Bobot dari kriteria: " + bobotKriteria);
//        }
//
//        // Membuat array untuk bobot yang disederhanakan
//        double[] sederhanaBobot = new double[bobotList.size()];
//
//        // Melakukan perhitungan bobot yang disederhanakan
//        for (int i = 0; i < bobotList.size(); i++) {
//            sederhanaBobot[i] = totalBobot / bobotList.get(i);
//            
//            System.out.println("Bobot Sederhana untuk kriteria " + (i+1) + ": " + df.format(sederhanaBobot[i]));
//        }
//
//        // Loop melalui setiap baris
//        for (int i = 0; i < tbSPK; i++) {
//            // Mendapatkan nilai dari tabel dan mengonversinya menjadi integer
//            nilaiC[i][0] = Integer.parseInt(tblSPK.getValueAt(i, 0).toString());
//            nilaiC[i][1] = Integer.parseInt(tblSPK.getValueAt(i, 1).toString());
//            nilaiC[i][2] = Integer.parseInt(tblSPK.getValueAt(i, 2).toString());
//            nilaiC[i][3] = Integer.parseInt(tblSPK.getValueAt(i, 3).toString());
//            nilaiC[i][4] = Integer.parseInt(tblSPK.getValueAt(i, 4).toString());
//
//            // Menghitung jumlah kuadrat untuk setiap c
//            sumC[0] += Math.pow(nilaiC[i][0], 2);
//            sumC[1] += Math.pow(nilaiC[i][1], 2);
//            sumC[2] += Math.pow(nilaiC[i][2], 2);
//            sumC[3] += Math.pow(nilaiC[i][3], 2);
//            sumC[4] += Math.pow(nilaiC[i][4], 2);
//        }
//
//        // Menghitung pembagi untuk setiap c
//        for (int j = 0; j < 5; j++) {
//            pembagiC[j] = Math.sqrt(sumC[j]);
//        }
//
//        // Loop melalui setiap baris untuk menghitung nilai normalized
//        for (int i = 0; i < tbSPK; i++) {
//            // Menghitung nilai normalized untuk setiap c
//            for (int k = 0; k < 5; k++) {
//                normalized[i][k] = pembagiC[k] != 0 ? nilaiC[i][k] / pembagiC[k] : 0;
//                System.out.println("Normalized C" + (k+1) + " for row " + (i+1) + ": " + df.format(normalized[i][k]));
//            }
//        }
//
//        // Menampilkan total bobot
//        System.out.println("Total Bobot: " + totalBobot);
//
//        // Menampilkan pesan sukses
//        JOptionPane.showMessageDialog(null, "Berhasil");
//    } catch (SQLException e) {
//        // Menangani exceptions SQL
//        JOptionPane.showMessageDialog(null, "Gagal: " + e.getMessage());
//    } catch (NumberFormatException e) {
//        // Menangani exceptions format angka
//        JOptionPane.showMessageDialog(null, "Format angka tidak valid: " + e.getMessage());
//    } catch (Exception e) {
//        // Menangani exceptions lainnya
//        JOptionPane.showMessageDialog(null, "Gagal: " + e.getMessage());
//    }
//}
//


   
//protected void penilaianSPK() {
//    try {
//        // Query SQL
//        String penilaianSQL = "INSERT INTO penilaian VALUES (?, ?, ?, ?, ?, ?)";
//        String kriteriaSQL = "SELECT * FROM kriteria";
//        PreparedStatement stat2 = conn.prepareStatement(penilaianSQL);
//        PreparedStatement statKriteria = conn.prepareStatement(kriteriaSQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
//        ResultSet kriteriaRes = statKriteria.executeQuery();
//        
//        // Mendapatkan jumlah baris dari tabel
//        int tbSPK = tblSPK.getRowCount();
//        int tbAlt = tblAlt.getRowCount();
//
//        double[] sumC = new double[5];
//        double[] pembagiC = new double[5];
//        double[] nilaiC = new double[5];
//        DecimalFormat df = new DecimalFormat("#.#####");
//
//        // Loop melalui setiap baris
//        for (int i = 0; i < tbSPK; i++) {
//            // Mendapatkan nilai dari tabel dan mengonversinya menjadi integer
//            for (int j = 0; j < 5; j++) {
//                nilaiC[j] = Integer.parseInt(tblSPK.getValueAt(i, j).toString());
//                sumC[j] += Math.pow(nilaiC[j], 2);
//            }
//
//            // Menghitung pembagi untuk setiap c
//            for (int j = 0; j < 5; j++) {
//                pembagiC[j] = Math.sqrt(sumC[j]);
//            }
//
//            // Mengatur nilai pembagi di tfCovba yang sesuai
//            tfCovba1.setText(df.format(pembagiC[0]));
//            tfCovba2.setText(df.format(pembagiC[1]));
//            tfCovba3.setText(df.format(pembagiC[2]));
//            tfCovba4.setText(df.format(pembagiC[3]));
//            tfCovba5.setText(df.format(pembagiC[4]));
//
//            // Output pembagi ke konsol
//            for (int j = 0; j < 5; j++) {
//                System.out.println("Pembagi C" + (j + 1) + ": " + pembagiC[j]);
//            }
//
//            // Membagi setiap nilai dengan pembaginya
//            double[] normalizedC = new double[5];
//            for (int j = 0; j < 5; j++) {
//                normalizedC[j] = nilaiC[j] / pembagiC[j];
//                System.out.println("C" + (j + 1) + " setelah normalisasi: " + df.format(normalizedC[j]));
//            }
//            
//            tfCovba1.setText(df.format(normalizedC[0]));
//            tfCovba2.setText(df.format(normalizedC[1]));
//            tfCovba3.setText(df.format(normalizedC[2]));
//            tfCovba4.setText(df.format(normalizedC[3]));
//            tfCovba5.setText(df.format(normalizedC[4]));
//            
//            // Inisialisasi array untuk menyimpan hasil perkalian normalized dengan bobot
//            double[][] normalizedBobot = new double[tbSPK][tblSPK.getColumnCount()];
//
//           // Hitung total bobot dari semua kriteria
//            double totalBobot = 0;
//            while (kriteriaRes.next()) {
//                totalBobot += kriteriaRes.getDouble("bobot_kriteria");
//                System.out.println("nilai total bobot"+totalBobot);
//            }
//
//            // Reset ulang ResultSet
//            kriteriaRes.beforeFirst();
//
//            // Loop melalui setiap baris alternatif
//            for (int x = 0; x < tbAlt; x++) {
//                while (kriteriaRes.next()) {
//                    String nama_kriteria = kriteriaRes.getString("nama_kriteria");
//
//                    double bobot_kriteria = kriteriaRes.getDouble("bobot_kriteria");
//
//                    // Bagi bobot_kriteria dengan total bobot
//                    double bobot_sederhana = bobot_kriteria / totalBobot;
//
//                    // Loop melalui setiap kolom tblSPK
//                    for (int j = 0; j < tblSPK.getColumnCount(); j++) {
//                        // Ambil nilai normalized dari tblSPK sesuai indeks kolom (j)
//                        double normalized = Double.parseDouble(tblSPK.getValueAt(x, j).toString());
//                        // Hitung hasil perkalian normalized dengan bobot_kriteria yang disederhanakan
//                        normalizedBobot[x][j] = normalized * bobot_sederhana;
//                        // Gunakan hasil perkalian sesuai kebutuhan aplikasi Anda
//                        System.out.println("Hasil Perkalian untuk kriteria " + nama_kriteria + " di kolom ke-" + j + ": " + normalizedBobot[x][j]);
//                        tblSPK.setValueAt(normalizedBobot[x][j], x, j);
//                    }
//                }
//            }
//
//
//        }
//
//        // Menampilkan pesan sukses
//        JOptionPane.showMessageDialog(null, "Berhasil");
//    } catch (SQLException e) {
//        // Menangani exceptions SQL
//        JOptionPane.showMessageDialog(null, "Gagal: " + e.getMessage());
//    } catch (NumberFormatException e) {
//        // Menangani exceptions format angka
//        JOptionPane.showMessageDialog(null, "Format angka tidak valid: " + e.getMessage());
//    } catch (Exception e) {
//        // Menangani exceptions lainnya
//        JOptionPane.showMessageDialog(null, "Gagal: ");
//    }
//}



    
//    protected void penilaianSPK() {
//        try {
//            // Query SQL
//            String penilaianSql = "INSERT INTO penilaian VALUES (?, ?, ?, ?, ?, ?)";
//            PreparedStatement stat2 = conn.prepareStatement(penilaianSql);
//
//            // Mendapatkan jumlah baris dari tabel
//            int t = tblSPK.getRowCount();
//
//            // Inisialisasi array untuk nilai C dan sum kuadrat
//            double[] sumC = new double[5];
//            double[] pembagiC = new double[5];
//            double[] nilaiC = new double[5];
//            DecimalFormat df = new DecimalFormat("#.#####");
//
//            // Loop melalui setiap baris
//            for (int i = 0; i < t; i++) {
//                // Mendapatkan nilai dari tabel dan mengonversinya menjadi integer
//                for (int j = 0; j < 5; j++) {
//                    nilaiC[j] = Integer.parseInt(tblSPK.getValueAt(i, j + 2).toString());
//                    sumC[j] += Math.pow(nilaiC[j], 2);
//                }
//
//                // Menghitung pembagi untuk setiap C
//                for (int j = 0; j < 5; j++) {
//                    pembagiC[j] = Math.sqrt(sumC[j]);
//                }
//
//                // Mengatur nilai pembagi di tfCovba yang sesuai
//                tfCovba1.setText(df.format(pembagiC[0]));
//                tfCovba2.setText(df.format(pembagiC[1]));
//                tfCovba3.setText(df.format(pembagiC[2]));
//                tfCovba4.setText(df.format(pembagiC[3]));
//                tfCovba5.setText(df.format(pembagiC[4]));
//
//                // Output pembagi ke konsol
//                for (int j = 0; j < 5; j++) {
//                    System.out.println("Pembagi C" + (j + 1) + ": " + pembagiC[j]);
//                }
//
//                // Membagi setiap nilai dengan pembaginya
//                double[] normalizedC = new double[5];
//                for (int j = 0; j < 5; j++) {
//                    normalizedC[j] = nilaiC[j] / pembagiC[j];
//                    System.out.println("C" + (j + 1) + " setelah normalisasi: " + df.format(normalizedC[j]));
//                }
//
//                // Memeriksa apakah nilai-nilai adalah nol (mengasumsikan kosong berarti nol di sini)
//                boolean adaNol = false;
//                for (int j = 0; j < 5; j++) {
//                    if (nilaiC[j] == 0) {
//                        adaNol = true;
//                        break;
//                    }
//                }
//
//                if (adaNol) {
//                    System.out.println("Masukan Semua Data!");
//                    for (int j = 0; j < 5; j++) {
//                        System.out.println("nilai C" + (j + 1) + ": " + nilaiC[j]);
//                    }
//                } else {
//                    // Mengatur nilai yang telah dinormalisasi ke dalam prepared statement
//                    for (int j = 0; j < 5; j++) {
//                        stat2.setDouble(j + 2, normalizedC[j]);
//                    }
//                    // Misalnya: id atau primary key
//                    stat2.setInt(1, Integer.parseInt(tblSPK.getValueAt(i, 0).toString()));
//
//                    // Menjalankan query
//                    stat2.executeUpdate();
//                }
//            }
//
//            // Menampilkan pesan sukses
//            JOptionPane.showMessageDialog(null, "Berhasil");
//        } catch (SQLException e) {
//            // Menangani exceptions SQL
//            JOptionPane.showMessageDialog(null, "Gagal: " + e.getMessage());
//        } catch (NumberFormatException e) {
//            // Menangani exceptions format angka
//            JOptionPane.showMessageDialog(null, "Format angka tidak valid: " + e.getMessage());
//        } catch (Exception e) {
//            // Menangani exceptions lainnya
//            JOptionPane.showMessageDialog(null, "Gagal: " + e.getMessage());
//        }
//    }

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
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        tfCari = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSPK = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblAlt = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addContainerGap(24, Short.MAX_VALUE))
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
                .addContainerGap(154, Short.MAX_VALUE))
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tfCari, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(44, 44, 44)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(394, 394, 394))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(301, 301, 301)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
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
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 900, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 2000, Short.MAX_VALUE)
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
    private javax.swing.JTable tblAlt;
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
