import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManageAttendanceFrame extends JFrame {

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtFilterEmp;

    private static final String[] COLUMNS = {
        "#", "Emp #", "Date", "Login", "Logout", "Hours"
    };

    public ManageAttendanceFrame() {
        setTitle("Manage Attendance Records");
        setSize(820, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ===== FILTER BAR =====
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtFilterEmp = new JTextField(12);
        JButton btnFilter  = new JButton("Filter by Emp #");
        JButton btnShowAll = new JButton("Show All");
        searchPanel.add(new JLabel("Employee #:"));
        searchPanel.add(txtFilterEmp);
        searchPanel.add(btnFilter);
        searchPanel.add(btnShowAll);
        add(searchPanel, BorderLayout.NORTH);

        // ===== TABLE =====
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== BUTTONS =====
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnAdd    = new JButton("Add Record");
        JButton btnEdit   = new JButton("Edit Record");
        JButton btnDelete = new JButton("Delete Record");
        JButton btnClose  = new JButton("Close");
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClose);
        add(btnPanel, BorderLayout.SOUTH);

        btnFilter.addActionListener(e -> filterByEmp(txtFilterEmp.getText().trim()));
        btnShowAll.addActionListener(e -> { txtFilterEmp.setText(""); refreshTable(""); });
        btnAdd.addActionListener(e -> addRecord());
        btnEdit.addActionListener(e -> editRecord());
        btnDelete.addActionListener(e -> deleteRecord());
        btnClose.addActionListener(e -> dispose());

        refreshTable("");
        setVisible(true);
    }

    private void refreshTable(String empFilter) {
        tableModel.setRowCount(0);
        int rowNum = 1;
        for (int i = 0; i < MotorPHPayroll.attendanceCount; i++) {
            if (!empFilter.isEmpty() && !MotorPHPayroll.attEmpNumbers[i].equals(empFilter)) continue;
            double hrs = MotorPHPayroll.calculateDailyHours(
                MotorPHPayroll.attLogins[i], MotorPHPayroll.attLogouts[i]);
            tableModel.addRow(new Object[]{
                rowNum++,
                MotorPHPayroll.attEmpNumbers[i],
                MotorPHPayroll.attDates[i],
                MotorPHPayroll.attLogins[i],
                MotorPHPayroll.attLogouts[i],
                String.format("%.2f", hrs)
            });
        }
    }

    private void filterByEmp(String empNo) {
        if (empNo.isEmpty()) { refreshTable(""); return; }
        if (MotorPHPayroll.findEmployeeIndex(empNo) == -1) {
            JOptionPane.showMessageDialog(this, "Employee not found.",
                "Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }
        refreshTable(empNo);
    }

    // Map visible table row to global attendance array index
    private int getGlobalIndex(int tableRow) {
        String empFilter = txtFilterEmp.getText().trim();
        int count = 0;
        for (int i = 0; i < MotorPHPayroll.attendanceCount; i++) {
            if (!empFilter.isEmpty() && !MotorPHPayroll.attEmpNumbers[i].equals(empFilter)) continue;
            if (count == tableRow) return i;
            count++;
        }
        return -1;
    }

    private void addRecord() {
        JTextField fEmpNo  = new JTextField(txtFilterEmp.getText());
        JTextField fDate   = new JTextField();
        JTextField fLogin  = new JTextField();
        JTextField fLogout = new JTextField();

        Object[] fields = {
            "Employee Number:",    fEmpNo,
            "Date (yyyy-MM-dd):",  fDate,
            "Login (HH:mm:ss):",   fLogin,
            "Logout (HH:mm:ss):",  fLogout
        };

        int result = JOptionPane.showConfirmDialog(this, fields,
            "Add Attendance Record", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        String empNo = fEmpNo.getText().trim();
        if (empNo.isEmpty() || fDate.getText().trim().isEmpty()
                || fLogin.getText().trim().isEmpty() || fLogout.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (MotorPHPayroll.findEmployeeIndex(empNo) == -1) {
            JOptionPane.showMessageDialog(this, "Employee number does not exist.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MotorPHPayroll.addAttendance(empNo,
            fDate.getText().trim(), fLogin.getText().trim(), fLogout.getText().trim());
        JOptionPane.showMessageDialog(this, "Attendance record added.");
        refreshTable(txtFilterEmp.getText().trim());
    }

    private void editRecord() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a record to edit.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int globalIndex = getGlobalIndex(row);
        if (globalIndex == -1) return;

        JTextField fDate   = new JTextField(MotorPHPayroll.attDates[globalIndex]);
        JTextField fLogin  = new JTextField(MotorPHPayroll.attLogins[globalIndex]);
        JTextField fLogout = new JTextField(MotorPHPayroll.attLogouts[globalIndex]);

        Object[] fields = {
            "Date (yyyy-MM-dd):", fDate,
            "Login (HH:mm:ss):",  fLogin,
            "Logout (HH:mm:ss):", fLogout
        };

        int result = JOptionPane.showConfirmDialog(this, fields,
            "Edit Attendance Record", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        if (fDate.getText().trim().isEmpty() || fLogin.getText().trim().isEmpty()
                || fLogout.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MotorPHPayroll.updateAttendance(globalIndex,
            fDate.getText().trim(), fLogin.getText().trim(), fLogout.getText().trim());
        JOptionPane.showMessageDialog(this, "Attendance record updated.");
        refreshTable(txtFilterEmp.getText().trim());
    }

    private void deleteRecord() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a record to delete.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete this attendance record?", "Confirm Delete",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        int globalIndex = getGlobalIndex(row);
        if (globalIndex == -1) return;

        MotorPHPayroll.deleteAttendance(globalIndex);
        JOptionPane.showMessageDialog(this, "Attendance record deleted.");
        refreshTable(txtFilterEmp.getText().trim());
    }
}
