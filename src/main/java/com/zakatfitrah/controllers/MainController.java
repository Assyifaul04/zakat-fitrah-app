package com.zakatfitrah.controllers;

import com.zakatfitrah.models.Jamaah;
import com.zakatfitrah.utils.DatabaseUtil;
import com.zakatfitrah.utils.KwitansiPrinter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class MainController {

    @FXML
    private TextField searchField;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private TextField namaField;
    @FXML
    private TextField alamatField;
    @FXML
    private TextField jumlahAnggotaField;
    @FXML
    private TextField jenisZakatField;
    @FXML
    private TextField nominalField;
    @FXML
    private DatePicker tanggalPicker;
    @FXML
    private TableView<Jamaah> tableView;
    @FXML
    private TableColumn<Jamaah, Integer> idColumn;
    @FXML
    private TableColumn<Jamaah, String> namaColumn;
    @FXML
    private TableColumn<Jamaah, String> alamatColumn;
    @FXML
    private TableColumn<Jamaah, Integer> jumlahAnggotaColumn;
    @FXML
    private TableColumn<Jamaah, String> jenisZakatColumn;
    @FXML
    private TableColumn<Jamaah, Double> nominalColumn;
    @FXML
    private TableColumn<Jamaah, String> tanggalColumn;

    private ObservableList<Jamaah> jamaahList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        namaColumn.setCellValueFactory(cellData -> cellData.getValue().namaProperty());
        alamatColumn.setCellValueFactory(cellData -> cellData.getValue().alamatProperty());
        jumlahAnggotaColumn.setCellValueFactory(cellData -> cellData.getValue().jumlahAnggotaProperty().asObject());
        jenisZakatColumn.setCellValueFactory(cellData -> cellData.getValue().jenisZakatProperty());
        nominalColumn.setCellValueFactory(cellData -> cellData.getValue().nominalProperty().asObject());
        tanggalColumn.setCellValueFactory(cellData -> cellData.getValue().tanggalProperty());

        tableView.setItems(jamaahList);

        loadJamaahData();
    }

    @FXML
    public void exportToPdf() {
        Document document = new Document();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                // Add title
                Paragraph title = new Paragraph("Laporan Data Jamaah", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);

                // Create table with 7 columns
                PdfPTable table = new PdfPTable(7);
                table.addCell("ID");
                table.addCell("Nama");
                table.addCell("Alamat");
                table.addCell("Jumlah Anggota");
                table.addCell("Jenis Zakat");
                table.addCell("Nominal");
                table.addCell("Tanggal Pembayaran");

                // Add rows to the table
                for (Jamaah jamaah : jamaahList) {
                    table.addCell(String.valueOf(jamaah.getId()));
                    table.addCell(jamaah.getNama());
                    table.addCell(jamaah.getAlamat());
                    table.addCell(String.valueOf(jamaah.getJumlahAnggota()));
                    table.addCell(jamaah.getJenisZakat());
                    table.addCell(String.valueOf(jamaah.getNominal()));
                    table.addCell(jamaah.getTanggal());
                }

                document.add(table);
                document.close();

                showInfo("Sukses", "Data berhasil diekspor ke PDF.");
            } catch (Exception e) {
                showError("Error", "Gagal menulis file PDF: " + e.getMessage());
            }
        }
    }

    @FXML
    public void exportToExcel() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Jamaah Data");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Nama");
        headerRow.createCell(2).setCellValue("Alamat");
        headerRow.createCell(3).setCellValue("Jumlah Anggota");
        headerRow.createCell(4).setCellValue("Jenis Zakat");
        headerRow.createCell(5).setCellValue("Nominal");
        headerRow.createCell(6).setCellValue("Tanggal Pembayaran");

        // Populate rows with data from the table
        int rowNum = 1;
        for (Jamaah jamaah : jamaahList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(jamaah.getId());
            row.createCell(1).setCellValue(jamaah.getNama());
            row.createCell(2).setCellValue(jamaah.getAlamat());
            row.createCell(3).setCellValue(jamaah.getJumlahAnggota());
            row.createCell(4).setCellValue(jamaah.getJenisZakat());
            row.createCell(5).setCellValue(jamaah.getNominal());
            row.createCell(6).setCellValue(jamaah.getTanggal());
        }

        // Save to file
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
                showInfo("Sukses", "Data berhasil diekspor ke Excel.");
            } catch (Exception e) {
                showError("Error", "Gagal menulis file Excel: " + e.getMessage());
            }
        }
    }

    @FXML
    private void importExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih File Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showOpenDialog(null);
    
        if (file != null) {
            try (FileInputStream fis = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(fis)) {
    
                Sheet sheet = workbook.getSheetAt(0);
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue;
    
                    int id = 0; // default, will be generated by DB
                    String nama = row.getCell(0).getStringCellValue();
                    String alamat = row.getCell(1).getStringCellValue();
                    int jumlah = (int) row.getCell(2).getNumericCellValue();
                    String jenis = row.getCell(3).getStringCellValue();
                    double nominal = row.getCell(4).getNumericCellValue();
                    String tanggal = row.getCell(5).getStringCellValue();
    
                    // Simpan ke database
                    try (Connection conn = DatabaseUtil.connect()) {
                        String sql = "INSERT INTO jamaah (nama, alamat, jumlah_anggota, jenis_zakat, nominal, tanggal_pembayaran) VALUES (?, ?, ?, ?, ?, ?)";
                        try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                            stmt.setString(1, nama);
                            stmt.setString(2, alamat);
                            stmt.setInt(3, jumlah);
                            stmt.setString(4, jenis);
                            stmt.setDouble(5, nominal);
                            stmt.setString(6, tanggal);
                            stmt.executeUpdate();
    
                            ResultSet keys = stmt.getGeneratedKeys();
                            if (keys.next()) {
                                int newId = keys.getInt(1);
                                Jamaah j = new Jamaah(newId, nama, alamat, jumlah, jenis, nominal, tanggal);
                                jamaahList.add(j);
                            }
                        }
                    } catch (SQLException e) {
                        showError("Database Error", "Gagal menyimpan data: " + e.getMessage());
                    }
                }
    
                showInfo("Sukses", "Data berhasil diimpor dari Excel.");
            } catch (Exception e) {
                showError("Error", "Gagal membaca file Excel: " + e.getMessage());
            }
        }
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    


    @FXML
    private void cariJamaah() {
        String keyword = searchField.getText().toLowerCase();
        if (keyword.isEmpty()) {
            tableView.setItems(jamaahList);
            return;
        }

        ObservableList<Jamaah> filteredList = FXCollections.observableArrayList();
        for (Jamaah j : jamaahList) {
            if (j.getNama().toLowerCase().contains(keyword) || j.getAlamat().toLowerCase().contains(keyword)) {
                filteredList.add(j);
            }
        }
        tableView.setItems(filteredList);
    }

    @FXML
    private void clearForm() {
        clearFields();
    }

    @FXML
    private void hapusJamaah() {
        Jamaah selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Yakin ingin menghapus data ini?",
                ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Konfirmasi Hapus");
            confirm.showAndWait();
    
            if (confirm.getResult() == ButtonType.YES) {
                try (Connection conn = DatabaseUtil.connect()) {
                    String sql = "DELETE FROM jamaah WHERE id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, selected.getId());
                        stmt.executeUpdate();
                        jamaahList.remove(selected);
                    }
                } catch (SQLException e) {
                    showError("Error", "Gagal menghapus data: " + e.getMessage());
                }
            }
        } else {
            showError("Error", "Pilih data yang ingin dihapus.");
        }
    }
    

    @FXML
    private void isiFormDariTabel() {
        Jamaah selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            namaField.setText(selected.getNama());
            alamatField.setText(selected.getAlamat());
            jumlahAnggotaField.setText(String.valueOf(selected.getJumlahAnggota()));
            jenisZakatField.setText(selected.getJenisZakat());
            nominalField.setText(String.valueOf(selected.getNominal()));
            tanggalPicker.setValue(LocalDate.parse(selected.getTanggal()));
        }
    }

    @FXML
    private void editJamaah() {
        Jamaah selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try (Connection conn = DatabaseUtil.connect()) {
                String sql = "UPDATE jamaah SET nama=?, alamat=?, jumlah_anggota=?, jenis_zakat=?, nominal=?, tanggal_pembayaran=? WHERE id=?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, namaField.getText());
                    stmt.setString(2, alamatField.getText());
                    stmt.setInt(3, Integer.parseInt(jumlahAnggotaField.getText()));
                    stmt.setString(4, jenisZakatField.getText());
                    stmt.setDouble(5, Double.parseDouble(nominalField.getText()));
                    stmt.setString(6, tanggalPicker.getValue().toString());
                    stmt.setInt(7, selected.getId());
                    stmt.executeUpdate();

                    selected.setNama(namaField.getText());
                    selected.setAlamat(alamatField.getText());
                    selected.setJumlahAnggota(Integer.parseInt(jumlahAnggotaField.getText()));
                    selected.setJenisZakat(jenisZakatField.getText());
                    selected.setNominal(Double.parseDouble(nominalField.getText()));
                    selected.setTanggal(tanggalPicker.getValue().toString());

                    tableView.refresh();
                    clearFields();
                }
            } catch (SQLException e) {
                showError("Error", "Gagal mengedit data: " + e.getMessage());
            }
        } else {
            showError("Error", "Pilih data yang ingin diedit.");
        }
    }


    @FXML
    public void tambahJamaah() {
        String nama = namaField.getText();
        String alamat = alamatField.getText();
        String jenisZakat = jenisZakatField.getText();
        String jumlahStr = jumlahAnggotaField.getText();
        String nominalStr = nominalField.getText();
    
        if (nama.isEmpty() || alamat.isEmpty() || jumlahStr.isEmpty() || nominalStr.isEmpty() || jenisZakat.isEmpty()) {
            showError("Validasi Gagal", "Harap isi semua kolom input.");
            return;
        }
    
        int jumlahAnggota;
        double totalBeras;
    
        try {
            jumlahAnggota = Integer.parseInt(jumlahStr);
            totalBeras = Double.parseDouble(nominalStr);
        } catch (NumberFormatException e) {
            showError("Input Tidak Valid", "Jumlah anggota dan nominal harus berupa angka.");
            return;
        }
    
        LocalDate tanggal = tanggalPicker.getValue();
        if (tanggal == null) {
            tanggal = LocalDate.now();
        }
    
        try (Connection connection = DatabaseUtil.connect()) {
            // Mengubah nama kolom tanggal menjadi tanggal_pembayaran
            String sql = "INSERT INTO jamaah (nama, alamat, jumlah_anggota, jenis_zakat, nominal, tanggal_pembayaran) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, nama);
                statement.setString(2, alamat);
                statement.setInt(3, jumlahAnggota);
                statement.setString(4, jenisZakat);
                statement.setDouble(5, totalBeras);
                statement.setString(6, tanggal.toString());  // Pastikan format tanggal sesuai dengan database
                statement.executeUpdate();
    
                var generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    Jamaah jamaah = new Jamaah(id, nama, alamat, jumlahAnggota, jenisZakat, totalBeras, tanggal.toString());
                    jamaahList.add(jamaah);
                }
            }
        } catch (SQLException e) {
            showError("Error", "Gagal menyimpan data jamaah: " + e.getMessage());
        }
    
        clearFields();
    }
    
    @FXML
    public void cetakKwitansi() {
        Jamaah selectedJamaah = tableView.getSelectionModel().getSelectedItem();
        if (selectedJamaah != null) {
            KwitansiPrinter.cetakKwitansi(selectedJamaah);
        } else {
            showError("Error", "Pilih jamaah untuk mencetak kwitansi.");
        }
    }

    private void clearFields() {
        namaField.clear();
        alamatField.clear();
        jumlahAnggotaField.clear();
        jenisZakatField.clear();
        nominalField.clear();
        tanggalPicker.setValue(null);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadJamaahData() {
        jamaahList.clear();
        String sql = "SELECT * FROM jamaah ORDER BY tanggal_pembayaran DESC";

        try (Connection conn = DatabaseUtil.connect();
            var statement = conn.createStatement();
            var resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Jamaah j = new Jamaah(
                    resultSet.getInt("id"),
                    resultSet.getString("nama"),
                    resultSet.getString("alamat"),
                    resultSet.getInt("jumlah_anggota"),
                    resultSet.getString("jenis_zakat"),
                    resultSet.getDouble("nominal"),
                    resultSet.getString("tanggal_pembayaran")
                );
                jamaahList.add(j);
            }
        } catch (SQLException e) {
            showError("Error", "Gagal memuat data: " + e.getMessage());
        }
    }
}
