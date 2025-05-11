package com.zakatfitrah.utils;

import java.awt.*;
import java.awt.print.*;
import java.io.File;
import javax.imageio.ImageIO;
import com.zakatfitrah.models.Jamaah;

public class KwitansiPrinter {

    public static void cetakKwitansi(Jamaah jamaah) {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setJobName("Kwitansi Zakat Fitrah");

        // Menyesuaikan ukuran kertas dengan ukuran KTP (90mm x 60mm)
        PageFormat pageFormat = printerJob.defaultPage();
        Paper paper = new Paper();
        double width = 90 * 2.83465;  // 90mm in points
        double height = 60 * 2.83465; // 60mm in points
        paper.setSize(width, height);
        paper.setImageableArea(10, 10, width - 20, height - 20); // Margin 10 points
        pageFormat.setPaper(paper);

        printerJob.setPrintable((graphics, pf, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pf.getImageableX(), pf.getImageableY());

            int x = 10;
            int y = 10;
            int lineHeight = 15;
            int columnWidth = 270;

            g2d.setFont(new Font("Monospaced", Font.PLAIN, 9));

            // === HEADER: Logo and Info ===
            g2d.drawRect(x, y, columnWidth, 40);
            g2d.drawRect(x, y, 50, 40); // Logo box
            try {
                Image logo = ImageIO.read(new File("assets/logo.png"));
                g2d.drawImage(logo, x + 5, y + 5, 40, 30, null);
            } catch (Exception e) {
                g2d.drawString("Logo", x + 10, y + 25);
            }

            // Center text
            Font headerFont = new Font("Monospaced", Font.BOLD, 10);
            g2d.setFont(headerFont);
            
            int centerX = x + 50 + (columnWidth - 50) / 2;
            
            // Calculate text widths for centering
            FontMetrics fm = g2d.getFontMetrics();
            String line1 = "MASJID SALAFIYAH";
            String line2 = "LANGON ANDONGSARI AMBULU";
            String line3 = "Kabupaten Jember";
            
            int text1Width = fm.stringWidth(line1);
            int text2Width = fm.stringWidth(line2);
            int text3Width = fm.stringWidth(line3);
            
            g2d.drawString(line1, centerX - text1Width/2, y + 12);
            g2d.drawString(line2, centerX - text2Width/2, y + 24);
            g2d.setFont(new Font("Monospaced", Font.PLAIN, 9));
            g2d.drawString(line3, centerX - text3Width/2, y + 36);

            // === Title Kwitansi ===
            y += 50;
            g2d.setFont(new Font("Monospaced", Font.BOLD, 10));
            String title = "=== kwitansi zakat fitrah ===";
            int titleWidth = g2d.getFontMetrics().stringWidth(title);
            g2d.drawString(title, x + (columnWidth / 2) - (titleWidth / 2), y);
            
            // Underline "kwitansi zakat fitrah"
            int startUnderline = x + (columnWidth / 2) - (titleWidth / 2) + g2d.getFontMetrics().stringWidth("=== ");
            int endUnderline = startUnderline + g2d.getFontMetrics().stringWidth("kwitansi zakat fitrah");
            g2d.drawLine(startUnderline, y + 2, endUnderline, y + 2);

            // === Informasi Jamaah ===
            y += 20;
            g2d.setFont(new Font("Monospaced", Font.PLAIN, 9));
            
            // Left column
            g2d.drawString("Nomor", x, y);
            g2d.drawString(":", x + 80, y);
            g2d.drawString(String.valueOf(jamaah.getId()), x + 90, y);
            
            // Right column
            g2d.drawString("tanggal:", x + 160, y);
            g2d.drawString(getSafe(jamaah.tanggalProperty()), x + 210, y);

            y += lineHeight;
            g2d.drawString("Nama", x, y);
            g2d.drawString(":", x + 80, y);
            g2d.drawString(getSafe(jamaah.namaProperty()), x + 90, y);

            y += lineHeight;
            g2d.drawString("Alamat", x, y);
            g2d.drawString(":", x + 80, y);
            g2d.drawString(getSafe(jamaah.alamatProperty()), x + 90, y);

            y += lineHeight;
            g2d.drawString("Jumlah anggota", x, y);
            g2d.drawString(":", x + 80, y);
            g2d.drawString(String.valueOf(jamaah.jumlahAnggotaProperty().get()), x + 90, y);

            // === Detail Zakat ===
            double zakat = jamaah.jumlahAnggotaProperty().get() * 2.5;
            double total = jamaah.nominalProperty().get();
            double infaq = Math.max(total - zakat, 0);

            y += lineHeight;
            g2d.drawString("Zakat wajib", x, y);
            g2d.drawString(":", x + 80, y);
            g2d.drawString(String.format("%.1f kg", zakat), x + 90, y);

            y += lineHeight;
            g2d.drawString("Infaq ( lebih )", x, y);
            g2d.drawString(":", x + 80, y);
            g2d.drawString(String.format("%.1f kg", infaq), x + 90, y);
            
            y += lineHeight;
            g2d.drawString("Total beras", x, y);
            g2d.drawString(":", x + 80, y);
            g2d.drawString(String.format("%.1f kg", total), x + 90, y);
            
            // === Tanda Tangan (sejajar dengan Total beras) ===
            int ttdY = y; // posisi y sama dengan baris "Total beras"
            
            // Tulisan "Pengasuh," di atas tanda tangan
            g2d.drawString("Pengasuh,", x + 200, ttdY - 30);
            
            // Nama dengan garis bawah
            String namaPengasuh = "Muji Slamet";
            g2d.drawString(namaPengasuh, x + 200, ttdY);
            g2d.drawLine(x + 200, ttdY + 2, x + 200 + g2d.getFontMetrics().stringWidth(namaPengasuh), ttdY + 2);

            // Terbilang (lanjutan)
            y += lineHeight;
            g2d.drawString("Terbilang", x, y);
            g2d.drawString(":", x + 80, y);
            g2d.drawString(terbilang(total) + " Kilogram", x + 90, y);

            return Printable.PAGE_EXISTS;
        });

        try {
            printerJob.print();
            System.out.println("✅ Kwitansi berhasil dicetak.");
        } catch (PrinterException e) {
            e.printStackTrace();
            System.out.println("❌ Gagal mencetak kwitansi: " + e.getMessage());
        }
    }

    private static String getSafe(javafx.beans.property.StringProperty prop) {
        return prop != null && prop.get() != null ? prop.get() : "-";
    }

    private static String terbilang(double angka) {
        String hasil = terbilangBulatan((long) angka);
        int desimal = (int) Math.round((angka - (long) angka) * 10);
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
        if (angka < 12) return huruf[(int) angka];
        else if (angka < 20) return huruf[(int) angka - 10] + " Belas";
        else if (angka < 100) return huruf[(int) angka / 10] + " Puluh " + huruf[(int) angka % 10];
        else if (angka < 200) return "Seratus " + terbilangBulatan(angka - 100);
        else if (angka < 1000) return huruf[(int) angka / 100] + " Ratus " + terbilangBulatan(angka % 100);
        else if (angka < 2000) return "Seribu " + terbilangBulatan(angka - 1000);
        else if (angka < 1_000_000) return terbilangBulatan(angka / 1000) + " Ribu " + terbilangBulatan(angka % 1000);
        else return "Angka terlalu besar";
    }
}
