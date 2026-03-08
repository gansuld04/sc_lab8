package mn.edu.num.tms.ui;

import mn.edu.num.tms.core.application.ThesisDTO;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * UI ADAPTER: ThesisTableModel
 * 
 * Зорилго:
 * - JTable-д өгөгдөл харуулахын тулд AbstractTableModel extend хийнэ
 * - ThesisDTO жагсаалтыг хүснэгтийн мөр/багана болгон хөрвүүлнэ
 * 
 * Observer Pattern:
 * - fireTableDataChanged() дуудахад Swing автоматаар хүснэгтийг refresh хийнэ
 */
public class ThesisTableModel extends AbstractTableModel {

    // Баганы гарчгууд
    private final String[] columnNames = {"ID", "Гарчиг", "Оюутан", "Ментор", "Төлөв"};

    // Харуулах өгөгдөл
    private List<ThesisDTO> theses;

    public ThesisTableModel(List<ThesisDTO> theses) {
        this.theses = theses != null ? theses : new ArrayList<>();
    }

    /**
     * Өгөгдлийг шинэчлэж, UI-г мэдэгдэнэ.
     * fireTableDataChanged() дуудахад Swing JTable автоматаар дахин зурна.
     */
    public void setTheses(List<ThesisDTO> theses) {
        this.theses = theses != null ? theses : new ArrayList<>();
        fireTableDataChanged(); // Observer pattern: Swing-д "өгөгдөл өөрчлөгдлөө" мэдэгдэнэ
    }

    // ----------------------------------------------------------------
    // AbstractTableModel-ийн заавал хэрэгжүүлэх методууд
    // ----------------------------------------------------------------

    @Override
    public int getRowCount() {
        return theses.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ThesisDTO thesis = theses.get(rowIndex);

        // Багана бүрийн утгыг буцаана
        return switch (columnIndex) {
            case 0 -> thesis.id();
            case 1 -> thesis.title();
            case 2 -> thesis.studentId();
            case 3 -> thesis.supervisorId() != null ? thesis.supervisorId() : "-";
            case 4 -> thesis.status().name();
            default -> null;
        };
    }

    // ----------------------------------------------------------------
    // HELPER: Сонгосон мөрийн ThesisDTO-г буцаана
    // ----------------------------------------------------------------

    /**
     * JTable-д сонгосон мөрийн ThesisDTO-г буцаана.
     * MainFrame-д хэрэглэнэ (сонгосон thesis-ийн ID авах гэх мэт).
     */
    public ThesisDTO getThesisAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < theses.size()) {
            return theses.get(rowIndex);
        }
        return null;
    }
}
