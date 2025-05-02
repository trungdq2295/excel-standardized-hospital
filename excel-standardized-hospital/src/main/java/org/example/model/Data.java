package org.example.model;

public class Data {

  int soToa;
  String fullName;
  int soBienLai;
  double soTien;

  public Data(int soToa, String fullName, int soBienLai, double soTien) {
    this.soToa = soToa;
    this.fullName = fullName;
    this.soBienLai = soBienLai;
    this.soTien = soTien;
  }

  public int getSoToa() {
    return soToa;
  }

  public String getFullName() {
    return fullName;
  }

  public int getSoBienLai() {
    return soBienLai;
  }

  public double getSoTien() {
    return soTien;
  }

  @Override
  public String toString() {
    return "Data{" +
      "soToa='" + soToa + '\'' +
      ", fullName='" + fullName + '\'' +
      ", soBienLai='" + soBienLai + '\'' +
      ", soTien='" + soTien + '\'' +
      '}';
  }
}
