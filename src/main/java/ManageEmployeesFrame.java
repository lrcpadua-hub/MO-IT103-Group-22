import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

public class ManageEmployeesFrame extends JFrame {

    private DefaultTableModel tableModel;
    private JTable table;

    private static final NumberFormat PESO = NumberFormat.getNumberInstance(new Locale("en", "PH"));
    static { PESO.setMinimumFractionDigits(2); PESO.setMaximumFractionDigits(2); }

    private static final String[] COLUMNS = {
        "Emp #", "Last Name", "First Name", "Birthday", "Position", "Hourly Rate"
    };

    public ManageEmployeesFrame() {
        setTitle("Manage Employee Records");
        setSize(880, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ===== SEARCH BAR =====
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtSearch = new JTextField(15);
        JButton btnSearch  = new JButton("Search");
        JButton btnShowAll = new JButton("Show All");
        searchPanel.add(new JLabel("Search Emp #:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnShowAll);
        add(searchPanel, BorderLayout.NORTH);

        // ===== TABLE =====
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== BUTTONS =====
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnAdd    = new JButton("Add Employee");
        JButton btnEdit   = new JButton("Edit Employee");
        JButton btnDelete = new JButton("Delete Employee");
        JButton btnClose  = new JButton("Close");
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClose);
        add(btnPanel, BorderLayout.SOUTH);

        btnSearch.addActionListener(e -> searchEmployee(txtSearch.getText().trim()));
        btnShowAll.addActionListener(e -> { txtSearch.setText(""); refreshTable(); });
        btnAdd.addActionListener(e -> addEmployee());
        btnEdit.addActionListener(e -> editEmployee());
        btnDelete.addActionListener(e -> deleteEmployee());
        btnClose.addActionListener(e -> dispose());

        refreshTable();
        setVisible(true);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (int i = 0; i < MotorPHPayroll.employeeCount; i++) {
            tableModel.addRow(new Object[]{
                MotorPHPayroll.empNumbers[i],
                MotorPHPayroll.lastNames[i],
                MotorPHPayroll.firstNames[i],
                MotorPHPayroll.birthdays[i],
                MotorPHPayroll.positions[i],
                "₱" + PESO.format(MotorPHPayroll.hourlyRates[i])
            });
        }
    }

    private void searchEmployee(String empNo) {
        if (empNo.isEmpty()) { refreshTable(); return; }
        tableModel.setRowCount(0);
        int i = MotorPHPayroll.findEmployeeIndex(empNo);
        if (i == -1) {
            JOptionPane.showMessageDialog(this, "Employee not found.",
                "Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }
        tableModel.addRow(new Object[]{
            MotorPHPayroll.empNumbers[i],
            MotorPHPayroll.lastNames[i],
            MotorPHPayroll.firstNames[i],
            MotorPHPayroll.birthdays[i],
            MotorPHPayroll.positions[i],
            "₱" + PESO.format(MotorPHPayroll.hourlyRates[i])
        });
    }

    private void addEmployee() {
        JTextField fEmpNo    = new JTextField();
        JTextField fLast     = new JTextField();
        JTextField fFirst    = new JTextField();
        JTextField fBirthday = new JTextField();
        JTextField fPosition = new JTextField();
        JTextField fRate     = new JTextField();

        Object[] fields = {
            "Employee Number:",       fEmpNo,
            "Last Name:",             fLast,
            "First Name:",            fFirst,
            "Birthday (yyyy-MM-dd):", fBirthday,
            "Position:",              fPosition,
            "Hourly Rate:",           fRate
        };

        int result = JOptionPane.showConfirmDialog(this, fields,
            "Add New Employee", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        if (fEmpNo.getText().trim().isEmpty() || fLast.getText().trim().isEmpty()
                || fFirst.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Employee number and name are required.",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double rate;
        try { rate = Double.parseDouble(fRate.getText().trim()); }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid hourly rate.",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean added = MotorPHPayroll.addEmployee(
            fEmpNo.getText().trim(), fLast.getText().trim(),
            fFirst.getText().trim(), fBirthday.getText().trim(),
            fPosition.getText().trim(), rate);

        if (!added) {
            JOptionPane.showMessageDialog(this,
                "Employee number already exists.", "Duplicate", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Employee added successfully.");
        refreshTable();
    }

    private void editEmployee() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to edit.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String empNo = (String) tableModel.getValueAt(row, 0);
        int i = MotorPHPayroll.findEmployeeIndex(empNo);
        if (i == -1) return;

        JTextField fLast     = new JTextField(MotorPHPayroll.lastNames[i]);
        JTextField fFirst    = new JTextField(MotorPHPayroll.firstNames[i]);
        JTextField fBirthday = new JTextField(MotorPHPayroll.birthdays[i]);
        JTextField fPosition = new JTextField(MotorPHPayroll.positions[i]);
        JTextField fRate     = new JTextField(String.valueOf(MotorPHPayroll.hourlyRates[i]));

        Object[] fields = {
            "Last Name:",             fLast,
            "First Name:",            fFirst,
            "Birthday (yyyy-MM-dd):", fBirthday,
            "Position:",              fPosition,
            "Hourly Rate:",           fRate
        };

        int result = JOptionPane.showConfirmDialog(this, fields,
            "Edit Employee: " + empNo, JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        if (fLast.getText().trim().isEmpty() || fFirst.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name fields cannot be empty.",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double rate;
        try { rate = Double.parseDouble(fRate.getText().trim()); }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid hourly rate.",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MotorPHPayroll.updateEmployee(empNo,
            fLast.getText().trim(), fFirst.getText().trim(),
            fBirthday.getText().trim(), fPosition.getText().trim(), rate);
        JOptionPane.showMessageDialog(this, "Employee updated successfully.");
        refreshTable();
    }

    private void deleteEmployee() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String empNo = (String) tableModel.getValueAt(row, 0);
        String name  = tableModel.getValueAt(row, 1) + ", " + tableModel.getValueAt(row, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete employee " + name + " (" + empNo + ")?\n" +
            "All their attendance records will also be deleted.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        MotorPHPayroll.deleteEmployee(empNo);
        JOptionPane.showMessageDialog(this, "Employee deleted successfully.");
        refreshTable();
    }
}
