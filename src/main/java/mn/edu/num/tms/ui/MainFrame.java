package mn.edu.num.tms.ui;

import mn.edu.num.tms.core.application.ThesisDTO;
import mn.edu.num.tms.core.application.ThesisService;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MAIN VIEW: MainFrame (Swing UI)
 * 
 * Hexagonal Architecture - Inbound Adapter:
 * - UI зөвхөн ThesisService (use case) дуудна
 * - Repository, Database шууд дуудаж болохгүй
 * - Бизнесийн дүрэм энд биш, core-д байна
 * 
 * Pattern:
 * - BorderLayout: NORTH=form, CENTER=table, SOUTH=workflow buttons
 * - Observer Pattern: ActionListener (saveBtn дарахад)
 * - SwingWorker: DB операцийг EDT-аас гадна ажиллуулна (UI freeze болохгүй)
 */
public class MainFrame extends JFrame {

    private final ThesisService thesisService;

    // ---- Table (CENTER) ----
    private JTable table;
    private ThesisTableModel tableModel;

    // ---- Form (NORTH) ----
    private JTextField titleField;
    private JTextField studentField;
    private JTextField supervisorField;
    private JTextField rejectReasonField;

    // ---- Buttons ----
    private JButton saveBtn;
    private JButton updateBtn;
    private JButton deleteBtn;
    private JButton submitBtn;
    private JButton approveBtn;
    private JButton rejectBtn;

    // ---- Status label ----
    private JLabel statusLabel;

    // ----------------------------------------------------------------
    // CONSTRUCTOR
    // ----------------------------------------------------------------

    public MainFrame(ThesisService thesisService) {
        this.thesisService = thesisService;

        setTitle("NUM Thesis Management System - Sprint 05");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        initComponents();
        loadData();          // Програм нээгдэхэд DB-аас өгөгдөл авна

        setLocationRelativeTo(null); // Дэлгэцийн төвд гарна
        setVisible(true);
    }

    // ----------------------------------------------------------------
    // initComponents(): UI бүрэлдэхүүнийг үүсгэнэ
    // ----------------------------------------------------------------

    private void initComponents() {

        // ==== CENTER: Thesis жагсаалтын хүснэгт ====
        tableModel = new ThesisTableModel(new ArrayList<>());
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        // Хүснэгтийн баганын өргөн
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);

        // Мөр сонгохад form-д мэдээллийг дүүргэнэ
        table.getSelectionModel().addListSelectionListener(this::onRowSelected);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ==== NORTH: Оруулах форм (2 мөр: дээр=талбарууд, доор=товчнууд) ====
        // GridLayout(2,1) -> 2 мөр, 1 багана: хэзээ ч таслагдахгүй
        JPanel formPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thesis мэдээлэл"));

        titleField = new JTextField(18);
        studentField = new JTextField(12);
        supervisorField = new JTextField(12);
        rejectReasonField = new JTextField(14);

        saveBtn = new JButton("Нэмэх");
        updateBtn = new JButton("Шинэчлэх");
        deleteBtn = new JButton("Устгах");
        JButton refreshBtn = new JButton("Refresh");

        // --- Дээд мөр: үндсэн input талбарууд ---
        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        inputRow.add(new JLabel("Гарчиг:"));
        inputRow.add(titleField);
        inputRow.add(new JLabel("Оюутан ID:"));
        inputRow.add(studentField);
        inputRow.add(new JLabel("Ментор ID:"));
        inputRow.add(supervisorField);

        // --- Доод мөр: товчнууд + Reject шалтгаан (нэг мөрт) ---
        // Reject шалтгааныг энд байрлуулснаар хэзээ ч таслагдахгүй
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        btnRow.add(saveBtn);
        btnRow.add(updateBtn);
        btnRow.add(deleteBtn);
        btnRow.add(refreshBtn);
        btnRow.add(Box.createHorizontalStrut(10)); // зай
        btnRow.add(new JLabel("Reject шалтгаан:"));
        btnRow.add(rejectReasonField);

        formPanel.add(inputRow);
        formPanel.add(btnRow);

        add(formPanel, BorderLayout.NORTH);

        // ==== SOUTH: Workflow товчнууд + статус ====
        JPanel southPanel = new JPanel(new BorderLayout());

        JPanel workflowPanel = new JPanel(new FlowLayout());
        workflowPanel.setBorder(BorderFactory.createTitledBorder("Workflow"));

        submitBtn = new JButton("Submit");
        approveBtn = new JButton("Approve");
        rejectBtn = new JButton("Reject");

        // Товчнуудын өнгийг ялгаатай болгоно
        submitBtn.setBackground(new Color(173, 216, 230));  // Цэнхэр
        approveBtn.setBackground(new Color(144, 238, 144)); // Ногоон
        rejectBtn.setBackground(new Color(255, 182, 193));  // Улаан

        workflowPanel.add(submitBtn);
        workflowPanel.add(approveBtn);
        workflowPanel.add(rejectBtn);
        southPanel.add(workflowPanel, BorderLayout.NORTH);

        statusLabel = new JLabel("Бэлэн.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        southPanel.add(statusLabel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        // ==== ACTION LISTENERS (Observer Pattern) ====

        // Нэмэх
        saveBtn.addActionListener(e -> onSave());

        // Шинэчлэх
        updateBtn.addActionListener(e -> onUpdate());

        // Устгах
        deleteBtn.addActionListener(e -> onDelete());

        // Refresh
        refreshBtn.addActionListener(e -> loadData());

        // Workflow
        submitBtn.addActionListener(e -> onSubmit());
        approveBtn.addActionListener(e -> onApprove());
        rejectBtn.addActionListener(e -> onReject());

        // Эхэндээ workflow товчнуудыг идэвхгүй болгоно
        setWorkflowButtonsEnabled(false);
    }

    // ----------------------------------------------------------------
    // loadData(): SwingWorker ашиглан DB-аас өгөгдөл авна
    // ----------------------------------------------------------------

    /**
     * SwingWorker: DB операцийг background thread-д ажиллуулна.
     * UI freeze болохгүй! (EDT-аас тусад нь)
     */
    private void loadData() {
        statusLabel.setText("Уншиж байна...");

        // SwingWorker<T, V>:
        //   T = doInBackground()-ийн буцаах утга (List<ThesisDTO>)
        //   V = process()-д дамжуулах завсрын утга (энд ашиглахгүй)
        new SwingWorker<List<ThesisDTO>, Void>() {
            @Override
            protected List<ThesisDTO> doInBackground() {
                // ЭНЭ КОД BACKGROUND THREAD-Д АЖИЛЛАНА (EDT биш)
                // DB операц энд байна -> UI freeze болохгүй
                return thesisService.getAllTheses();
            }

            @Override
            protected void done() {
                // ЭНЭ КОД EDT-Д АЖИЛЛАНА (UI шинэчлэх зөвхөн EDT-д!)
                try {
                    List<ThesisDTO> theses = get();
                    tableModel.setTheses(theses); // Observer: UI автоматаар refresh
                    statusLabel.setText("Нийт: " + theses.size() + " thesis.");
                } catch (Exception ex) {
                    showError("Өгөгдөл авахад алдаа: " + ex.getMessage());
                    statusLabel.setText("Алдаа гарлаа!");
                }
            }
        }.execute();
    }

    // ----------------------------------------------------------------
    // Form Event Handlers
    // ----------------------------------------------------------------

    private void onSave() {
        String title = titleField.getText().trim();
        String student = studentField.getText().trim();
        String supervisor = supervisorField.getText().trim();

        // Validation
        if (title.isEmpty() || student.isEmpty()) {
            showError("Гарчиг болон Оюутан ID заавал оруулна!");
            return;
        }

        // DB операцийг SwingWorker-д
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                thesisService.createThesis(title, student,
                    supervisor.isEmpty() ? null : supervisor);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // Exception шидсэн бол энд гарна
                    clearForm();
                    loadData();
                    statusLabel.setText("Thesis амжилттай нэмэгдлээ.");
                } catch (Exception ex) {
                    showError("Нэмэхэд алдаа: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void onUpdate() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            showError("Эхлээд thesis сонгоно уу!");
            return;
        }

        ThesisDTO selected = tableModel.getThesisAt(selectedRow);
        String title = titleField.getText().trim();
        String student = studentField.getText().trim();

        if (title.isEmpty() || student.isEmpty()) {
            showError("Гарчиг болон Оюутан ID хоосон байж болохгүй!");
            return;
        }

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                thesisService.updateThesis(selected.id(), title, student);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    clearForm();
                    loadData();
                    statusLabel.setText("Thesis шинэчлэгдлээ.");
                } catch (Exception ex) {
                    showError("Шинэчлэхэд алдаа: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void onDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            showError("Эхлээд thesis сонгоно уу!");
            return;
        }

        ThesisDTO selected = tableModel.getThesisAt(selectedRow);

        // Устгахаас өмнө баталгаажуулах диалог
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "\"" + selected.title() + "\" thesis-ийг устгах уу?",
            "Баталгаажуулах",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                thesisService.deleteThesis(selected.id());
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    clearForm();
                    loadData();
                    statusLabel.setText("Thesis устгагдлаа.");
                } catch (Exception ex) {
                    showError("Устгахад алдаа: " + ex.getMessage());
                }
            }
        }.execute();
    }

    // ----------------------------------------------------------------
    // Workflow Event Handlers
    // ----------------------------------------------------------------

    private void onSubmit() {
        int id = getSelectedId();
        if (id < 0) return;

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                thesisService.submitThesis(id);
                return null;
            }
            @Override
            protected void done() {
                try {
                    get();
                    loadData();
                    statusLabel.setText("Thesis SUBMITTED болов.");
                } catch (Exception ex) {
                    // Core-оос IllegalStateException ирнэ (буруу шилжилт)
                    showError(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
                }
            }
        }.execute();
    }

    private void onApprove() {
        int id = getSelectedId();
        if (id < 0) return;

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                thesisService.approveThesis(id);
                return null;
            }
            @Override
            protected void done() {
                try {
                    get();
                    loadData();
                    statusLabel.setText("Thesis APPROVED болов.");
                } catch (Exception ex) {
                    showError(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
                }
            }
        }.execute();
    }

    private void onReject() {
        int id = getSelectedId();
        if (id < 0) return;

        String reason = rejectReasonField.getText().trim();
        if (reason.isEmpty()) {
            showError("Reject хийхэд шалтгаан (Reject шалтгаан талбар) заавал оруулна!");
            return;
        }

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                thesisService.rejectThesis(id, reason);
                return null;
            }
            @Override
            protected void done() {
                try {
                    get();
                    clearForm();
                    loadData();
                    statusLabel.setText("Thesis REJECTED болов.");
                } catch (Exception ex) {
                    showError(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
                }
            }
        }.execute();
    }

    // ----------------------------------------------------------------
    // HELPERS
    // ----------------------------------------------------------------

    /**
     * Хүснэгтийн мөр сонгохад form-д мэдээллийг дүүргэнэ.
     * Workflow товчнуудыг идэвхжүүлнэ.
     */
    private void onRowSelected(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return; // drag дуусаагүй байхад дуудахгүй

        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            ThesisDTO thesis = tableModel.getThesisAt(selectedRow);
            if (thesis != null) {
                titleField.setText(thesis.title());
                studentField.setText(thesis.studentId());
                supervisorField.setText(thesis.supervisorId() != null ? thesis.supervisorId() : "");
                rejectReasonField.setText(thesis.rejectReason() != null ? thesis.rejectReason() : "");
                setWorkflowButtonsEnabled(true);
            }
        } else {
            setWorkflowButtonsEnabled(false);
        }
    }

    private int getSelectedId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showError("Эхлээд thesis сонгоно уу!");
            return -1;
        }
        ThesisDTO thesis = tableModel.getThesisAt(row);
        return thesis != null ? thesis.id() : -1;
    }

    private void clearForm() {
        titleField.setText("");
        studentField.setText("");
        supervisorField.setText("");
        rejectReasonField.setText("");
        table.clearSelection();
        setWorkflowButtonsEnabled(false);
    }

    private void setWorkflowButtonsEnabled(boolean enabled) {
        submitBtn.setEnabled(enabled);
        approveBtn.setEnabled(enabled);
        rejectBtn.setEnabled(enabled);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Алдаа", JOptionPane.ERROR_MESSAGE);
    }
}