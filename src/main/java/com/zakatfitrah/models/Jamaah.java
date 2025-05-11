package com.zakatfitrah.models;

import javafx.beans.property.*;

public class Jamaah {
    private final IntegerProperty id;
    private final StringProperty nama;
    private final StringProperty alamat;
    private final IntegerProperty jumlahAnggota;
    private final StringProperty jenisZakat;
    private final DoubleProperty nominal;
    private final StringProperty tanggal;

    public Jamaah(int id, String nama, String alamat, int jumlahAnggota, String jenisZakat, double nominal, String tanggal) {
        this.id = new SimpleIntegerProperty(id);
        this.nama = new SimpleStringProperty(nama);
        this.alamat = new SimpleStringProperty(alamat);
        this.jumlahAnggota = new SimpleIntegerProperty(jumlahAnggota);
        this.jenisZakat = new SimpleStringProperty(jenisZakat);
        this.nominal = new SimpleDoubleProperty(nominal);
        this.tanggal = new SimpleStringProperty(tanggal);
    }

    // Property Getters
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty namaProperty() {
        return nama;
    }

    public StringProperty alamatProperty() {
        return alamat;
    }

    public IntegerProperty jumlahAnggotaProperty() {
        return jumlahAnggota;
    }

    public StringProperty jenisZakatProperty() {
        return jenisZakat;
    }

    public DoubleProperty nominalProperty() {
        return nominal;
    }

    public StringProperty tanggalProperty() {
        return tanggal;
    }

    // Standard Getters
    public int getId() {
        return id.get();
    }

    public String getNama() {
        return nama.get();
    }

    public String getAlamat() {
        return alamat.get();
    }

    public int getJumlahAnggota() {
        return jumlahAnggota.get();
    }

    public String getJenisZakat() {
        return jenisZakat.get();
    }

    public double getNominal() {
        return nominal.get();
    }

    public String getTanggal() {
        return tanggal.get();
    }

    // Standard Setters
    public void setId(int id) {
        this.id.set(id);
    }

    public void setNama(String nama) {
        this.nama.set(nama);
    }

    public void setAlamat(String alamat) {
        this.alamat.set(alamat);
    }

    public void setJumlahAnggota(int jumlahAnggota) {
        this.jumlahAnggota.set(jumlahAnggota);
    }

    public void setJenisZakat(String jenisZakat) {
        this.jenisZakat.set(jenisZakat);
    }

    public void setNominal(double nominal) {
        this.nominal.set(nominal);
    }

    public void setTanggal(String tanggal) {
        this.tanggal.set(tanggal);
    }
}
