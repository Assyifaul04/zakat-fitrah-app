package com.zakatfitrah.utils;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;

import javax.imageio.ImageIO;

import com.zakatfitrah.models.Jamaah;

public class KwitansiPrinter {

    public static void cetakKwitansi(Jamaah jamaah) {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setJobName("Kwitansi Zakat Fitrah");

        PageFormat pageFormat = printerJob.defaultPage();
        Paper paper = new Paper();
        double pageWidth = 21.5 * 28.35;   // ≈ 610.53 pt
        double pageHeight = 7.5 * 28.35;   // ≈ 212.63 pt
        paper.setSize(pageWidth, pageHeight);
        paper.setImageableArea(20, 20, pageWidth - 40, pageHeight - 40);
        pageFormat.setPaper(paper);

        printerJob.setPrintable((g, pf, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
        
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
        
            int x = 30;
            int y = 20;
            int width = 350;
            int height = 40;
            int lineHeight = 16;
        
            // ===== Header (Logo + Judul + Nomor) =====
            // Header border
            g2d.drawRect(x, y, width, height);
            
            // Logo section
            g2d.drawRect(x, y, 60, height);
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2d.drawString("Logo", x + 20, y + 25);
            
            try {
                Image logo = ImageIO.read(new File("assets/logo.png"));
                g2d.drawImage(logo, x + 5, y + 2, 50, 35, null);
            } catch (Exception ex) {
                g2d.drawString("Logo", x + 20, y + 25);
            }
            
            // Title section
            g2d.drawRect(x + 60, y, 200, height);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 11));
            g2d.drawString("MASJID SALAFIYAH", x + 110, y + 12);
            g2d.drawString("LANGON ANDONGSARI AMBULU", x + 70, y + 24);
            g2d.drawString("Kabupaten Jember", x + 110, y + 36);
            
            // Number section
            g2d.drawRect(x + 260, y, 90, height);
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2d.drawString("Nomer", x + 285, y + 20);
            if (jamaah.idProperty() != null) {
                g2d.drawString(String.valueOf(jamaah.idProperty().get()), x + 285, y + 30);
            }
            
            // ===== Main Title =====
            y += height + 2;
            g2d.drawRect(x, y, width, 25);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2d.drawString("=== KWITANSI ZAKAT FITRAH ===", x + 90, y + 16);
            
            // ===== Content Section =====
            y += 25;
            g2d.drawRect(x, y, width, 110);
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
            
            // First row: Nama and Tanggal
            y += lineHeight;
            g2d.drawString("Nama", x + 10, y);
            g2d.drawString(":", x + 100, y);
            if (jamaah.namaProperty() != null) {
                g2d.drawString(jamaah.namaProperty().get(), x + 110, y);
            }
            
            g2d.drawString("Tanggal", x + 240, y);
            g2d.drawString(":", x + 280, y);
            if (jamaah.tanggalProperty() != null) {
                g2d.drawString(jamaah.tanggalProperty().get(), x + 290, y);
            }
            
            // Second row: Alamat
            y += lineHeight;
            g2d.drawString("Alamat", x + 10, y);
            g2d.drawString(":", x + 100, y);
            if (jamaah.alamatProperty() != null) {
                g2d.drawString(jamaah.alamatProperty().get(), x + 110, y);
            }
            
            // Third row: Jumlah Anggota
            y += lineHeight;
            g2d.drawString("Jumlah Anggota", x + 10, y);
            g2d.drawString(":", x + 100, y);
            if (jamaah.jumlahAnggotaProperty() != null) {
                g2d.drawString(String.valueOf(jamaah.jumlahAnggotaProperty().get()), x + 110, y);
            }
            
            // ===== Calculation Section =====
            double totalBeras = jamaah.nominalProperty().get();
            int jumlahAnggota = jamaah.jumlahAnggotaProperty().get();
            double zakatWajib = jumlahAnggota * 2.7;
            double infaq = Math.max(0, totalBeras - zakatWajib);
            
            // Zakat Wajib
            y += lineHeight;
            g2d.drawString("Zakat Wajib", x + 10, y);
            g2d.drawString(":", x + 100, y);
            g2d.drawString(zakatWajib + " kg", x + 110, y);
            
            // Infaq (lebih)
            y += lineHeight;
            Font boldFont = new Font("SansSerif", Font.BOLD, 10);
            g2d.setFont(boldFont);
            g2d.drawString("Infaq ( lebih )", x + 10, y);
            g2d.drawString(":", x + 100, y);
            g2d.drawString(infaq + " kg", x + 110, y);
            
            // Underline for addition
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
            y += lineHeight - 4;
            g2d.drawString("_______________ +", x + 100, y);
            
            // Total beras
            y += lineHeight;
            g2d.setFont(boldFont);
            g2d.drawString("Total beras", x + 10, y);
            g2d.drawString(":", x + 100, y);
            g2d.drawString(totalBeras + " kg", x + 110, y);
            
            // Terbilang
            y += lineHeight;
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2d.drawString("Terbilang", x + 10, y);
            g2d.drawString(":", x + 100, y);
            g2d.drawString(terbilang(totalBeras) + " Kilogram", x + 110, y);
            
            return Printable.PAGE_EXISTS;
        });

        try {
            printerJob.print(); // langsung print
            System.out.println("✅ Kwitansi berhasil dicetak.");
        } catch (PrinterException e) {
            e.printStackTrace();
            System.out.println("❌ Gagal mencetak kwitansi: " + e.getMessage());
        }
    }

    private static String terbilang(double angka) {
        String[] huruf = {
            "", "Satu", "Dua", "Tiga", "Empat", "Lima",
            "Enam", "Tujuh", "Delapan", "Sembilan", "Sepuluh", "Sebelas"
        };
    
        long bilanganUtama = (long) angka;
        int desimal = (int) Math.round((angka - bilanganUtama) * 100);
    
        String hasil = terbilangBulatan(bilanganUtama);
    
        if (desimal > 0) {
            hasil += " Koma " + terbilangBulatan(desimal);
        }
    
        return hasil;
    }

    private static String terbilangBulatan(long angka) {
        String[] huruf = {
            "", "Satu", "Dua", "Tiga", "Empat", "Lima",
            "Enam", "Tujuh", "Delapan", "Sembilan", "Sepuluh", "Sebelas"
        };
    
        if (angka < 12) {
            return huruf[(int) angka];
        } else if (angka < 20) {
            return huruf[(int) angka - 10] + " Belas";
        } else if (angka < 100) {
            return huruf[(int) angka / 10] + " Puluh " + huruf[(int) angka % 10];
        } else if (angka < 200) {
            return "Seratus " + terbilangBulatan(angka - 100);
        } else if (angka < 1000) {
            return huruf[(int) angka / 100] + " Ratus " + terbilangBulatan(angka % 100);
        } else if (angka < 2000) {
            return "Seribu " + terbilangBulatan(angka - 1000);
        } else if (angka < 1000000) {
            return terbilangBulatan(angka / 1000) + " Ribu " + terbilangBulatan(angka % 1000);
        } else if (angka < 1000000000) {
            return terbilangBulatan(angka / 1000000) + " Juta " + terbilangBulatan(angka % 1000000);
        } else {
            return "Angka terlalu besar";
        }
    }
}