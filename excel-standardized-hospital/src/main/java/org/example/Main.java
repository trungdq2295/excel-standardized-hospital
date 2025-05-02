package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.model.Data;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

  private static final int INDEX_SO_TOA = 2;
  private static final int INDEX_FULL_NAME = 5;
  private static final int INDEX_SO_BIEN_LAI = 7;
  private static final int INDEX_SO_TIEN = 10;

  public static void main(String[] args) {
    // Create Frame
    JFrame frame = new JFrame("Check bệnh nhân trùng");
    frame.setSize(400, 200);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new FlowLayout());

    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

    // Add Button
    JButton importButton = new JButton("Chọn file ở đây");
    frame.add(importButton);

    // Button Action
    importButton.addActionListener(e -> {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      int result = fileChooser.showOpenDialog(frame);
      importButton.setEnabled(false);
      if (result == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        try {
          readExcelFile(selectedFile);
          JOptionPane.showMessageDialog(frame, "Ngon lành");
        } catch (IOException ex) {
          JOptionPane.showMessageDialog(frame, "Error reading file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
          importButton.setEnabled(true);
        }
      } else {
        importButton.setEnabled(true);
      }
    });

    // Show Frame
    frame.setVisible(true);
  }

  // Method to Read Excel File
  private static void readExcelFile(File file) throws IOException {
    try (FileInputStream fis = new FileInputStream(file);
         Workbook workbook = new XSSFWorkbook(fis)) {
      Sheet sheet = workbook.getSheetAt(0); // Read the first sheet
      List<String> headers = new ArrayList<>();
      List<Data> datas = new ArrayList<>();

      extractData(sheet, headers, datas);

      Map<String, List<Data>> map = datas.stream().collect(Collectors.groupingBy(Data::getFullName));
      removeUnDuplicatedData(map);

      List<Data> duplicatedDAta = map.entrySet().stream().flatMap(item -> item.getValue().stream()).collect(Collectors.toList());
      writeExcel(headers, duplicatedDAta);
    }
  }

  private static void extractData(Sheet sheet, List<String> headers, List<Data> datas) {
    int indexHeader = 0;
    for (Row row : sheet) {
      String soToa = getData(row.getCell(INDEX_SO_TOA));
      String fullName = getData(row.getCell(INDEX_FULL_NAME));
      String soBienLai = getData(row.getCell(INDEX_SO_BIEN_LAI));
      String soTien = getData(row.getCell(INDEX_SO_TIEN));
      if (indexHeader == 0) {
        headers.add(soToa);
        headers.add(fullName);
        headers.add(soBienLai);
        headers.add(soTien);
        indexHeader++;
      } else {
        String newFullName = cleanName(fullName);
        Data data = new Data(Integer.parseInt(soToa), newFullName, Integer.parseInt(soBienLai), Double.parseDouble(soTien));
        datas.add(data);
      }
    }
  }

  private static String cleanName(String name) {
    return name.replaceAll("\\(.*?\\)", "")
            .trim();
  }

  private static void removeUnDuplicatedData(Map<String, List<Data>> map) {
    map.entrySet().removeIf(entry -> entry.getValue().size() < 2);
  }

  private static void writeExcel(List<String> headers, List<Data> data) {
    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Data");

    // Create header row
    Row headerRow = sheet.createRow(0);
    for (int i = 0; i < headers.size(); i++) {
      Cell cell = headerRow.createCell(i);
      cell.setCellValue(headers.get(i));
    }

    // Write data rows
    int rowNum = 1;
    for (Data item : data) {
      Row row = sheet.createRow(rowNum++);
      row.createCell(0).setCellValue(item.getSoToa());
      row.createCell(1).setCellValue(item.getFullName());
      row.createCell(2).setCellValue(item.getSoBienLai());
      row.createCell(3).setCellValue(item.getSoTien());
    }

    //Get download folder
    Path filePath = Paths.get(getDownloadsFolderPath(), "excel.xlsx");

    // Write to file
    try (FileOutputStream fileOut = new FileOutputStream(filePath.toString())) {
      workbook.write(fileOut);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        workbook.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private static String getData(Cell cell) {
    DataFormatter fmt = new DataFormatter();
    return fmt.formatCellValue(cell);
  }

  private static String getDownloadsFolderPath() {
    String userProfile = System.getProperty("user.home");
    Path downloadsPath = Paths.get(userProfile, "Downloads");
    return downloadsPath.toString();
  }
}
