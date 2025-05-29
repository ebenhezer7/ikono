package edu.pradita.p14.swing;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector; // Import Vector untuk JComboBox data

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox; // Import JComboBox
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn; // Import TableColumn

// --- Penambahan Kelas dan Interface Baru (OOP) ---
// (Ini adalah bagian dari rombakan unit.java sebelumnya, perlu dipastikan ada di project yang sama)

// Interface untuk Unit Konversi (OCP, DIP)
interface ConvertibleUnit {
    String getBaseUnitName(); // Nama satuan dasar
    double convertToBase(double value); // Konversi nilai ke satuan dasar
    double convertFromBase(double baseValue); // Konversi nilai dari satuan dasar
}

// Implementasi Konkret untuk Unit Gram (SRP, OCP)
class GramUnit implements ConvertibleUnit {
    @Override public String getBaseUnitName() { return "gram"; }
    @Override public double convertToBase(double value) { return value; }
    @Override public double convertFromBase(double baseValue) { return baseValue; }
    public double convertToKilogram(double grams) { return grams / 1000.0; }
}

// Implementasi Konkret untuk Unit Mililiter (SRP, OCP)
class MilliliterUnit implements ConvertibleUnit {
    @Override public String getBaseUnitName() { return "mililiter"; }
    @Override public double convertToBase(double value) { return value; }
    @Override public double convertFromBase(double baseValue) { return baseValue; }
    public double convertToLiter(double milliliters) { return milliliters / 1000.0; }
}

// Kelas Placeholder untuk Unit Sachet (SRP)
class SachetUnit implements ConvertibleUnit {
    @Override public String getBaseUnitName() { return "sachet"; }
    @Override public double convertToBase(double value) { return value; }
    @Override public double convertFromBase(double baseValue) { return baseValue; }
}

// Factory Method untuk Membuat Objek Unit (OCP, DIP)
interface UnitFactory {
    ConvertibleUnit createUnit();
}

class GramUnitFactory implements UnitFactory {
    @Override public ConvertibleUnit createUnit() { return new GramUnit(); }
}

class MilliliterUnitFactory implements UnitFactory {
    @Override public ConvertibleUnit createUnit() { return new MilliliterUnit(); }
}

class SachetUnitFactory implements UnitFactory {
    @Override public ConvertibleUnit createUnit() { return new SachetUnit(); }
}

// Utility class untuk mendapatkan unit dari database (DIP)
// Ini adalah bagian yang akan berinteraksi dengan tabel unit_master
class UnitRepository {
    public Vector<String> getAllUnitCodes() {
        Vector<String> unitCodes = new Vector<>();
        String query = "SELECT unit_code FROM unit_master ORDER BY unit_code ASC";
        try (PreparedStatement statement = MainForm.CONNECTION.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                unitCodes.add(resultSet.getString("unit_code"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching unit codes: " + e.getMessage());
        }
        return unitCodes;
    }

    public String getBaseUnitCategoryByCode(String unitCode) {
        String category = "unknown";
        String query = "SELECT base_unit_category FROM unit_master WHERE unit_code = ?";
        try (PreparedStatement statement = MainForm.CONNECTION.prepareStatement(query)) {
            statement.setString(1, unitCode);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    category = resultSet.getString("base_unit_category");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching base unit category for " + unitCode + ": " + e.getMessage());
        }
        return category;
    }
}


// --- Kelas ItemForm yang Dimodifikasi ---
public class ItemForm extends JPanel implements IForm {

    private static final long serialVersionUID = -5614073877284999275L; // Ubah serialVersionUID
    private JTable table;
    private UnitRepository unitRepository; // New: Repository untuk unit
    private JComboBox<String> unitComboBox; // New: JComboBox untuk kolom unit

    // Map untuk menyimpan Factory berdasarkan kategori yang ada di unit_master (mirip dengan UnitConverter di project unit)
    // Ini harus diinisialisasi sekali saja, bisa di UnitConverter, atau di sini jika tidak ada UnitConverter terpisah
    private static final Map<String, UnitFactory> categoryToUnitFactoryMap = new HashMap<>();

    static {
        // Inisialisasi pabrik untuk kategori yang kita pedulikan untuk konversi khusus
        categoryToUnitFactoryMap.put("sembako", new GramUnitFactory());
        categoryToUnitFactoryMap.put("bumbu", new GramUnitFactory());
        categoryToUnitFactoryMap.put("minuman", new MilliliterUnitFactory());
        categoryToUnitFactoryMap.put("liquid", new MilliliterUnitFactory());
        categoryToUnitFactoryMap.put("renceng", new SachetUnitFactory());
        // Jika ada kategori lain yang tidak punya konversi spesifik (misal 'pieces'),
        // bisa tambahkan default factory atau biarkan null dan handle di getConvertibleUnitForCode
    }


    /**
     * Create the frame.
     */
    public ItemForm(MainForm mainForm) {
        setFont(new Font("Tahoma", Font.PLAIN, 16));
        setBounds(100, 100, 450, 350); // Ukuran sedikit lebih besar untuk kolom baru
        this.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setLayout(new BorderLayout(0, 4));

        Point centerPoint = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        this.setLocation(centerPoint.x - (int) this.getSize().getWidth() / 2,
                centerPoint.y - (int) this.getSize().getHeight() / 2);

        JPanel panel = new JPanel();
        this.add(panel, BorderLayout.SOUTH);
        panel.setLayout(new GridLayout(1, 2, 0, 0));

        JScrollPane scrollPane = new JScrollPane();
        this.add(scrollPane, BorderLayout.CENTER);

        // Inisialisasi UnitRepository
        unitRepository = new UnitRepository();
        // Ambil semua unit codes dari database untuk JComboBox
        Vector<String> unitCodes = unitRepository.getAllUnitCodes();
        unitComboBox = new JComboBox<>(unitCodes);


        table = new JTable();
        table.setFont(new Font("Tahoma", Font.PLAIN, 16));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Modifikasi DefaultTableModel untuk menambahkan kolom "Unit"
        table.setModel(new DefaultTableModel(new Object[][] { { null, null, null, null, null }, }, // Tambah 1 kolom null
                new String[] { "Code", "Name", "Price", "Quantity", "Unit" }) { // Tambah "Unit"
            private static final long serialVersionUID = 77319703134386250L;
            // Tambah Class<?> untuk kolom Unit
            Class<?>[] columnTypes = new Class[] { String.class, String.class, Double.class, Double.class, String.class }; 

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }

            boolean[] lastRowColumnEditables = new boolean[] { true, true, true, true, true }; // Tambah true
            boolean[] columnEditables = new boolean[] { true, true, true, true, true }; // Tambah true

            @Override
            public boolean isCellEditable(int row, int column) {
                if (this.getRowCount() > 0 && row == this.getRowCount() - 1) {
                    return lastRowColumnEditables[column];
                } else {
                    return columnEditables[column];
                }
            }
        });
        
        // Atur CellEditor untuk kolom "Unit" agar menjadi JComboBox
        TableColumn unitColumn = table.getColumnModel().getColumn(4); // Kolom ke-4 adalah "Unit" (index 0-based)
        unitColumn.setCellEditor(new DefaultCellEditor(unitComboBox));

        table.getColumnModel().getColumn(0).setPreferredWidth(52);
        table.getColumnModel().getColumn(1).setPreferredWidth(157);
        table.getColumnModel().getColumn(2).setPreferredWidth(60); // Sesuaikan lebar jika perlu
        table.getColumnModel().getColumn(3).setPreferredWidth(60); // Sesuaikan lebar jika perlu
        table.getColumnModel().getColumn(4).setPreferredWidth(70); // Lebar untuk Unit
        
        scrollPane.setViewportView(table);

        table.addPropertyChangeListener(new PropertyChangeListener() {
            String oldCode = null;

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("tableCellEditor".equals(evt.getPropertyName()) && table.isEditing()) {
                    int selectedRow = table.getSelectedRow();
                    DefaultTableModel dtm = (DefaultTableModel) table.getModel();
                    oldCode = (String) dtm.getValueAt(selectedRow, 0);
                }

                if (!"tableCellEditor".equals(evt.getPropertyName()) || table.isEditing()) {
                    return;
                }

                DefaultCellEditor cellEditor = (DefaultCellEditor) evt.getOldValue();
                DefaultTableModel dtm = (DefaultTableModel) table.getModel();
                int selectedRow = table.getSelectedRow();
                int selectedCol = table.getSelectedColumn();

                String code = (selectedCol == 0) ? (String) cellEditor.getCellEditorValue() : oldCode;
                if (code == null || code.trim().length() == 0) {
                    return;
                }

                String name = (String) dtm.getValueAt(selectedRow, 1);
                
                Object priceObj = (selectedCol == 2) ? cellEditor.getCellEditorValue() : dtm.getValueAt(selectedRow, 2);
                if (priceObj == null) {
                    return;
                }
                double price = (priceObj instanceof Double) ? (Double) priceObj : Double.parseDouble(priceObj.toString());

                Object qtyObj = (selectedCol == 3) ? cellEditor.getCellEditorValue() : dtm.getValueAt(selectedRow, 3);
                if (qtyObj == null) {
                    return;
                }
                double quantity = (qtyObj instanceof Double) ? (Double) qtyObj : Double.parseDouble(qtyObj.toString());
                
                // --- New: Ambil unit code dari kolom "Unit" ---
                String unitCode = (selectedCol == 4) ? (String) cellEditor.getCellEditorValue() : (String) dtm.getValueAt(selectedRow, 4);
                if (unitCode == null || unitCode.trim().isEmpty()) {
                    unitCode = "PCS"; // Default jika tidak diisi
                }

                try {
                    String query;
                    PreparedStatement statement;
                    
                    // Cek apakah item dengan code ini sudah ada
                    boolean itemExists = false;
                    try (PreparedStatement checkStmt = MainForm.CONNECTION.prepareStatement("SELECT COUNT(*) FROM item WHERE code = ?")) {
                        checkStmt.setString(1, code);
                        try (ResultSet rs = checkStmt.executeQuery()) {
                            if (rs.next() && rs.getInt(1) > 0) {
                                itemExists = true;
                            }
                        }
                    }

                    if (itemExists && oldCode != null && !oldCode.isEmpty()) { // Update existing item
                        query = "UPDATE item SET code = ?, name = ?, price = ?, quantity = ?, unit_code = ? " // Tambah unit_code
                                + " WHERE code = ?";
                        statement = MainForm.CONNECTION.prepareStatement(query);
                        statement.setString(1, code);
                        statement.setString(2, name);
                        statement.setDouble(3, price);
                        statement.setDouble(4, quantity);
                        statement.setString(5, unitCode); // Set unit_code
                        statement.setString(6, oldCode);
                        if (statement.executeUpdate() > 0) {
                            System.out.println("Item " + code + " has been successfully updated.");
                        }
                    } else { // Insert new item
                        query = "INSERT INTO item(code, name, price, quantity, unit_code) VALUES(?, ?, ?, ?, ?);"; // Tambah unit_code
                        statement = MainForm.CONNECTION.prepareStatement(query);
                        statement.setString(1, code);
                        statement.setString(2, name);
                        statement.setDouble(3, price);
                        statement.setDouble(4, quantity);
                        statement.setString(5, unitCode); // Set unit_code
                        if (statement.executeUpdate() > 0) {
                            System.out.println("A new item " + code + " has been added.");
                        }
                    }
                    statement.close(); // Tutup statement setelah digunakan

                } catch (SQLException e) {
                    System.err.println("Database error: " + e.getMessage()); // Cetak pesan error yang lebih jelas
                    // Rollback tampilan tabel jika terjadi error database
                    // Re-fetch data atau tampilkan pesan error ke user
                } finally {
                    updateTable(); // Perbarui tabel untuk merefleksikan perubahan atau jika ada item baru
                }
            }
        });

        // --- Perbarui updateTable() untuk mengambil unit_code ---
        updateTable();
    }

    @Override
    public String getDocumentCode() {
        return null;
    }
    
    // --- Metode bantu untuk mendapatkan ConvertibleUnit berdasarkan unitCode ---
    private ConvertibleUnit getConvertibleUnitForCode(String unitCode) {
        String baseCategory = unitRepository.getBaseUnitCategoryByCode(unitCode);
        UnitFactory factory = categoryToUnitFactoryMap.get(baseCategory.toLowerCase());
        if (factory != null) {
            return factory.createUnit();
        }
        // Fallback atau error handling jika kategori tidak dikenal
        System.err.println("No specific unit factory found for category: " + baseCategory + " (Unit Code: " + unitCode + ")");
        return new SachetUnit(); // Default fallback
    }


    @Override
    public void updateTable() { // Ubah akses menjadi public agar bisa dipanggil dari luar jika diperlukan
        try {
            // Ambil juga unit_code dari database
            String query = "SELECT code, name, price, quantity, unit_code FROM item"; 
            PreparedStatement statement = MainForm.CONNECTION.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            DefaultTableModel dtm = (DefaultTableModel) table.getModel();
            while (dtm.getRowCount() > 0) {
                dtm.removeRow(0); // Hapus semua baris yang ada
            }

            // add the rows
            while (resultSet.next()) {
                // Tambah 1 elemen lagi untuk unit_code
                Object[] values = new Object[5]; 
                values[0] = resultSet.getString("code");
                values[1] = resultSet.getString("name");
                values[2] = resultSet.getDouble("price");
                values[3] = resultSet.getDouble("quantity");
                values[4] = resultSet.getString("unit_code"); // Ambil unit_code
                dtm.addRow(values);
            }
            // Tambahkan baris kosong untuk entri baru
            Object[] newRowValues = new Object[5]; // Sesuaikan ukuran
            dtm.addRow(newRowValues);

            resultSet.close();
            statement.close();

            // Tidak perlu setModel lagi jika sudah dtm.addRow
            // table.setModel(dtm); 
        } catch (SQLException e) {
            System.err.println("Error updating table: " + e.getMessage());
            // e.printStackTrace(); // Aktifkan ini untuk debugging lebih lanjut
        }
    }
}