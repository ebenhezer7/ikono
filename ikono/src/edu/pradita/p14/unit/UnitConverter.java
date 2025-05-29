package edu.pradita.p14.unit;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// 1. Antarmuka (Interface) untuk Unit Konversi (OCP, DIP)
interface ConvertibleUnit {
    String getBaseUnitName(); // Nama satuan dasar
    double convertToBase(double value); // Konversi nilai ke satuan dasar
    double convertFromBase(double baseValue); // Konversi nilai dari satuan dasar
}

// 2. Implementasi Konkret untuk Unit Gram (SRP, OCP)
class GramUnit implements ConvertibleUnit {
    @Override
    public String getBaseUnitName() {
        return "gram";
    }

    @Override
    public double convertToBase(double value) {
        return value; // Gram sudah satuan dasar, tidak perlu konversi
    }

    @Override
    public double convertFromBase(double baseValue) {
        return baseValue; // Gram sudah satuan dasar, tidak perlu konversi
    }

    // Metode konversi spesifik untuk Gram
    public double convertToKilogram(double grams) {
        return grams / 1000.0;
    }
}

// 3. Implementasi Konkret untuk Unit Mililiter (SRP, OCP)
class MilliliterUnit implements ConvertibleUnit {
    @Override
    public String getBaseUnitName() {
        return "mililiter";
    }

    @Override
    public double convertToBase(double value) {
        return value; // Mililiter sudah satuan dasar, tidak perlu konversi
    }

    @Override
    public double convertFromBase(double baseValue) {
        return baseValue; // Mililiter sudah satuan dasar, tidak perlu konversi
    }

    // Metode konversi spesifik untuk Mililiter
    public double convertToLiter(double milliliters) {
        return milliliters / 1000.0;
    }
}

// 4. Kelas Placeholder untuk Unit Sachet (SRP)
class SachetUnit implements ConvertibleUnit {
    @Override
    public String getBaseUnitName() {
        return "sachet";
    }

    @Override
    public double convertToBase(double value) {
        return value; // Sachet sudah satuan dasar
    }

    @Override
    public double convertFromBase(double baseValue) {
        return baseValue; // Sachet sudah satuan dasar
    }
    // Tidak ada konversi spesifik di sini karena 'sachet' tidak memiliki unit turunan yang umum seperti kg/liter
}

// 5. Factory Method untuk Membuat Objek Unit (OCP, DIP)
// Ini adalah Abstract Factory atau bisa juga dianggap Factory Method jika hanya satu factory method
// Tapi karena kita mengelompokkan pembuatan unit berdasarkan kategori, ini lebih dekat ke Abstract Factory
interface UnitFactory {
    ConvertibleUnit createUnit();
}

class GramUnitFactory implements UnitFactory {
    @Override
    public ConvertibleUnit createUnit() {
        return new GramUnit();
    }
}

class MilliliterUnitFactory implements UnitFactory {
    @Override
    public ConvertibleUnit createUnit() {
        return new MilliliterUnit();
    }
}

class SachetUnitFactory implements UnitFactory {
    @Override
    public ConvertibleUnit createUnit() {
        return new SachetUnit();
    }
}

// 6. UnitConverter - Kelas utama untuk manajemen dan konversi (SRP, DIP)
// Kelas ini mirip dengan 'unit' yang asli, tapi sekarang jauh lebih ramping dan fokus
public class UnitConverter {
    // Memetakan kategori ke Factory yang sesuai (DIP)
    private static final Map<String, UnitFactory> categoryFactories = new HashMap<>();

    static {
        // Inisialisasi pabrik berdasarkan kategori
        categoryFactories.put("sembako", new GramUnitFactory());
        categoryFactories.put("bumbu", new GramUnitFactory());
        categoryFactories.put("minuman", new MilliliterUnitFactory());
        categoryFactories.put("liquid", new MilliliterUnitFactory());
        categoryFactories.put("renceng", new SachetUnitFactory());
    }

    public ConvertibleUnit getUnitForCategory(String category) {
        UnitFactory factory = categoryFactories.get(category.toLowerCase());
        if (factory != null) {
            return factory.createUnit(); // Menggunakan Factory Method
        }
        // Jika kategori tidak ditemukan, bisa return unit default atau throw exception
        System.out.println("Kategori tidak dikenal. Menggunakan unit default (sachet).");
        return new SachetUnit(); // Contoh fallback
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UnitConverter converterApp = new UnitConverter(); // Membuat instance converter utama

        System.out.print("Masukkan kategori (sembako, bumbu, minuman, liquid, renceng): ");
        String categoryInput = scanner.nextLine();

        ConvertibleUnit unit = converterApp.getUnitForCategory(categoryInput);
        System.out.println("Base unit untuk kategori " + categoryInput + " adalah: " + unit.getBaseUnitName());

        if (unit instanceof GramUnit) { // Cek tipe objek untuk konversi spesifik
            GramUnit gramUnit = (GramUnit) unit;
            System.out.print("Masukkan jumlah gram: ");
            double grams = scanner.nextDouble();
            System.out.println(grams + " gram = " + gramUnit.convertToKilogram(grams) + " kilogram");
        } else if (unit instanceof MilliliterUnit) { // Cek tipe objek untuk konversi spesifik
            MilliliterUnit milliliterUnit = (MilliliterUnit) unit;
            System.out.print("Masukkan jumlah mililiter: ");
            double milliliters = scanner.nextDouble();
            System.out.println(milliliters + " mililiter = " + milliliterUnit.convertToLiter(milliliters) + " liter");
        } else {
            System.out.println("Tidak ada konversi spesifik untuk unit " + unit.getBaseUnitName());
        }

        scanner.close();
    }
}