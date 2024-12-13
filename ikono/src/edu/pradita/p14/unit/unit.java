package edu.pradita.p14.unit;

import java.util.Scanner;

public class unit {
    private String category;
    private String baseUnit;

    public unit(String category) {
        this.category = category;
        setBaseUnit();
    }

    private void setBaseUnit() {
        switch (category.toLowerCase()) {
            case "sembako":
            case "bumbu":
                baseUnit = "gram";
                break;
            case "minuman":
            case "liquid":
                baseUnit = "mililiter";
                break;
            case "renceng":
                baseUnit = "sachet";
                break;
            default:
                baseUnit = "unknown";
                break;
        }
    }

    public String getBaseUnit() {
        return baseUnit;
    }

    public double convertGramToKilogram(double grams) {
        return grams / 1000;
    }

    public double convertMilliliterToLiter(double milliliters) {
        return milliliters / 1000;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Masukkan kategori (sembako, bumbu, minuman, liquid, renceng): ");
        String category = scanner.nextLine();

        unit unit = new unit(category);
        System.out.println("Base unit untuk kategori " + category + " adalah: " + unit.getBaseUnit());

        if (unit.getBaseUnit().equals("gram")) {
            System.out.print("Masukkan jumlah gram: ");
            double grams = scanner.nextDouble();
            System.out.println(grams + " gram = " + unit.convertGramToKilogram(grams) + " kilogram");
        } else if (unit.getBaseUnit().equals("mililiter")) {
            System.out.print("Masukkan jumlah mililiter: ");
            double milliliters = scanner.nextDouble();
            System.out.println(milliliters + " mililiter = " + unit.convertMilliliterToLiter(milliliters) + " liter");
        }

        scanner.close();
    }
}