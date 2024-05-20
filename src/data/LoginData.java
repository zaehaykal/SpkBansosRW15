/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

/**
 *
 * @author zaeha
 */
public class LoginData {
    private static String id_login;
    private static String nama_login;
    private static String izin;

    public String getId_login() {
        return id_login;
    }

    public void setId_login(String id_login) {
        this.id_login = id_login;
    }
    
    public String getNama_login() {
        return nama_login;
    }

    public void setNama_login(String nama_user) {
        this.nama_login = nama_user;
    }
    
    public String getIzin_login() {
        return izin;
    }

    public void setIzin_login(String izin) {
        this.izin = izin;
    }

}
