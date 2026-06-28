import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

public class EmployeeDashboard extends JFrame {

    private final String empNo;
    private static final NumberFormat PESO = NumberFormat.getNumberInstance(new Locale("en", "PH"));
    static { PESO.setMinimumFractionDigits(2); PESO.setMaximumFractionDigits(2); }

    private JLabel lblEmpNo, lblName, lblBirthday, lblPosition, lblRate;
    private DefaultTableModel attModel;
    private JTable attTable;

    public EmployeeDashboard(String empNo) {
        this.empNo = empNo;

        setTitle("MotorPH - Employee Dashboard");
        setSize(780, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ===== TOP: Employee Info =====
        JPanel infoPanel = new JPanel(new GridLayout(5, 2, 8, 8));
        infoPanel.setBorder(BorderFactory.createTitledBorder("My Information"));
        infoPanel.add(new JLabel("Employee Number:")); lblEmpNo    = new JLabel(); infoPanel.add(lblEmpNo);
        infoPanel.add(new JLabel("Name:"));            lblName     = new JLabel(); infoPanel.add(lblName);
        infoPanel.add(new JLabel("Birthday:"));        lblBirthday = new JLabel(); infoPanel.add(lblBirthday);
        infoPanel.add(new JLabel("Position:"));        lblPosition = new JLabel(); infoPanel.add(lblPosition);
        infoPanel.add(new JLabel("Hourly Rate:"));     lblRate     = new JLabel(); infoPanel.add(lblRate);

        // ===== MIDDLE: Attendance Table =====
        attModel = new DefaultTableModel(
            new String[]{"#", "Date", "Login", "Logout", "Hours"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        attTable = new JTable(attModel);
        attTable.setRowHeight(22);
        attTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(attTable);
        scroll.setBorder(BorderFactory.createTitledBorder("My Attendance Records"));

        // ===== BOTTOM: Buttons =====
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnEditMe    = new JButton("Edit My Info");
        JButton btnDeleteMe  = new JButton("Delete My Account");
        JButton btnAddAtt    = new JButton("Add Attendance");
        JButton btnEditAtt   = new JButton("Edit Attendance");
        JButton btnDeleteAtt = new JButton("Delete Attendance");
        JButton btnLogout    = new JButton("Logout");

        btnPanel.add(btnEditMe);
        btnPanel.add(btnDeleteMe);
        btnPanel.add(btnAddAtt);
        btnPanel.add(btnEditAtt);
        btnPanel.add(btnDeleteAtt);
        btnPanel.add(btnLogout);

        add(infoPanel, BorderLayout.NORTH);
        add(scroll,    BorderLayout.CENTER);
        add(btnPanel,  BorderLayout.SOUTH);

        btnEditMe.addActionListener(e -> editMyInfo());
        btnDeleteMe.addActionListener(e -> deleteMyAccount());
        btnAddAtt.addActionListener(e -> addAttendance());
        btnEditAtt.addActionListener(e -> editAttendance());
        btnDeleteAtt.addActionListener(e -> deleteAttendance());
        btnLogout.addActionListener(e -> logout());

        refreshInfo();
        refreshAttendance();
        setVisible(true);
    }

    private void refreshInfo() {
        int i = MotorPHPayroll.findEmployeeIndex(empNo);
        if (i == -1) { logout(); return; }
        lblEmpNo.setText(MotorPHPayroll.empNumbers[i]);
        lblName.setText(MotorPHPayroll.lastNames[i] + ", " + MotorPHPayroll.firstNames[i]);
        lblBirthday.setText(MotorPHPayroll.birthdays[i]);
        lblPosition.setText(MotorPHPayroll.positions[i]);
        lblRate.setText("₱" + PESO.format(MotorPHPayroll.hourlyRates[i]));
    }

    private void refreshAttendance() {
        attModel.setRowCount(0);
        int rowNum = 1;
        for (int i = 0; i < MotorPHPayroll.attendanceCount; i++) {
            if (!MotorPHPayroll.attEmpNumbers[i].equals(empNo)) continue;
            double hrs = MotorPHPayroll.calculateDailyHours(
                MotorPHPayroll.attLogins[i], MotorPHPayroll.attLogouts[i]);
            attModel.addRow(new Object[]{
                rowNum++,
                MotorPHPayroll.attDates[i],
                MotorPHPayroll.attLogins[i],
                MotorPHPayroll.attLogouts[i],
                String.format("%.2f", hrs)
            });
        }
    }

    private void editMyInfo() {
        int i = MotorPHPayroll.findEmployeeIndex(empNo);
        if (i == -1) return;

        JTextField fLastName  = new JTextField(MotorPHPayroll.lastNames[i]);
        JTextField fFirstName = new JTextField(MotorPHPayroll.firstNames[i]);
        JTextField fBirthday  = new JTextField(MotorPHPayroll.birthdays[i]);
        JTextField fPosition  = new JTextField(MotorPHPayroll.positions[i]);
        JTextField fRate      = new JTextField(String.valueOf(MotorPHPayroll.hourlyRates[i]));

        Object[] fields = {
            "Last Name:",             fLastName,
            "First Name:",            fFirstName,
            "Birthday (yyyy-MM-dd):", fBirthday,
            "Position:",              fPosition,
            "Hourly Rate:",           fRate
        };

        int result = JOptionPane.showConfirmDialog(this, fields,
            "Edit My Information", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        if (fLastName.getText().trim().isEmpty() || fFirstName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name fields cannot be empty.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double rate;
        try { rate = Double.parseDouble(fRate.getText().trim()); }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid hourly rate.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MotorPHPayroll.updateEmployee(empNo,
            fLastName.getText().trim(), fFirstName.getText().trim(),
            fBirthday.getText().trim(), fPosition.getText().trim(), rate);
        JOptionPane.showMessageDialog(this, "Your information has been updated.");
        refreshInfo();
    }

    private void deleteMyAccount() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete your account?\nThis cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        MotorPHPayroll.deleteEmployee(empNo);
        JOptionPane.showMessageDialog(this, "Your account has been deleted.");
        logout();
    }

    private void addAttendance() {
        JTextField fDate   = new JTextField();
        JTextField fLogin  = new JTextField();
        JTextField fLogout = new JTextField();

        Object[] fields = {
            "Date (yyyy-MM-dd):", fDate,
            "Login (HH:mm:ss):", fLogin,
            "Logout (HH:mm:ss):", fLogout
        };

        int result = JOptionPane.showConfirmDialog(this, fields,
            "Add Attendance Record", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        if (fDate.getText().trim().isEmpty() || fLogin.getText().trim().isEmpty()
                || fLogout.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MotorPHPayroll.addAttendance(empNo,
            fDate.getText().trim(), fLogin.getText().trim(), fLogout.getText().trim());
        JOptionPane.showMessageDialog(this, "Attendance record added.");
        refreshAttendance();
    }

    private void editAttendance() {
        int selectedRow = attTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a record to edit.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int globalIndex = getGlobalAttendanceIndex(selectedRow);
        if (globalIndex == -1) return;

        JTextField fDate   = new JTextField(MotorPHPayroll.attDates[globalIndex]);
        JTextField fLogin  = new JTextField(MotorPHPayroll.attLogins[globalIndex]);
        JTextField fLogout = new JTextField(MotorPHPayroll.attLogouts[globalIndex]);

        Object[] fields = {
            "Date (yyyy-MM-dd):", fDate,
            "Login (HH:mm:ss):", fLogin,
            "Logout (HH:mm:ss):", fLogout
        };

        int result = JOptionPane.showConfirmDialog(this, fields,
            "Edit Attendance Record", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        if (fDate.getText().trim().isEmpty() || fLogin.getText().trim().isEmpty()
                || fLogout.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MotorPHPayroll.updateAttendance(globalIndex,
            fDate.getText().trim(), fLogin.getText().trim(), fLogout.getText().trim());
        JOptionPane.showMessageDialog(this, "Attendance record updated.");
        refreshAttendance();
    }

    private void deleteAttendance() {
        int selectedRow = attTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a record to delete.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete this attendance record?", "Confirm Delete",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        int globalIndex = getGlobalAttendanceIndex(selectedRow);
        if (globalIndex == -1) return;

        MotorPHPayroll.deleteAttendance(globalIndex);
        JOptionPane.showMessageDialog(this, "Attendance record deleted.");
        refreshAttendance();
    }

    private int getGlobalAttendanceIndex(int tableRow) {
        int count = 0;
        for (int i = 0; i < MotorPHPayroll.attendanceCount; i++) {
            if (MotorPHPayroll.attEmpNumbers[i].equals(empNo)) {
                if (count == tableRow) return i;
                count++;
            }
        }
        return -1;
    }

    private void logout() {
        new LoginFrame().setVisible(true);
        dispose();
    }
}
