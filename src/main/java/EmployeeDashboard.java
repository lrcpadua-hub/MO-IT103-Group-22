import javax.swing.*;
import java.awt.*;

public class EmployeeDashboard extends JFrame {

    private JTextField txtEmployeeNo;
    private JButton btnSearch;
    private JButton btnLogout;

    private JLabel lblEmpNoValue;
    private JLabel lblNameValue;
    private JLabel lblBirthdayValue;
    private JLabel lblPositionValue;
    private JLabel lblRateValue;

    public EmployeeDashboard() {

        setTitle("MotorPH Employee Dashboard");
        setSize(650, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());

        // ===== TOP PANEL =====

        JPanel searchPanel = new JPanel();

        searchPanel.add(new JLabel("Employee Number:"));

        txtEmployeeNo = new JTextField(10);
        searchPanel.add(txtEmployeeNo);

        btnSearch = new JButton("Search");
        searchPanel.add(btnSearch);
        
        btnLogout = new JButton("Logout");
searchPanel.add(btnLogout);

        add(searchPanel, BorderLayout.NORTH);

        // ===== CENTER PANEL =====

        JPanel detailsPanel = new JPanel();
        detailsPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Employee Information"
                )
        );

        detailsPanel.setLayout(new GridLayout(5, 2, 10, 10));

        detailsPanel.add(new JLabel("Employee Number:"));
        lblEmpNoValue = new JLabel("-");
        detailsPanel.add(lblEmpNoValue);

        detailsPanel.add(new JLabel("Employee Name:"));
        lblNameValue = new JLabel("-");
        detailsPanel.add(lblNameValue);

        detailsPanel.add(new JLabel("Birthday:"));
        lblBirthdayValue = new JLabel("-");
        detailsPanel.add(lblBirthdayValue);

        detailsPanel.add(new JLabel("Position:"));
        lblPositionValue = new JLabel("-");
        detailsPanel.add(lblPositionValue);

        detailsPanel.add(new JLabel("Hourly Rate:"));
        lblRateValue = new JLabel("-");
        detailsPanel.add(lblRateValue);

        add(detailsPanel, BorderLayout.CENTER);

        // ===== BUTTON ACTION =====

        btnSearch.addActionListener(e -> searchEmployee());
        btnLogout.addActionListener(e -> logout());

        setVisible(true);
    }

    private void searchEmployee() {

    String empNo = txtEmployeeNo.getText().trim();

    int index = MotorPHPayroll.findEmployeeIndex(empNo);

    if (index == -1) {

        JOptionPane.showMessageDialog(
                this,
                "Employee number does not exist."
        );

        return;
    }

    lblEmpNoValue.setText(
            MotorPHPayroll.empNumbers[index]
    );

    lblNameValue.setText(
            MotorPHPayroll.lastNames[index]
                    + ", "
                    + MotorPHPayroll.firstNames[index]
    );

    lblBirthdayValue.setText(
            MotorPHPayroll.birthdays[index]
    );

    lblPositionValue.setText(
            MotorPHPayroll.positions[index]
    );

    lblRateValue.setText(
            "₱" + MotorPHPayroll.hourlyRates[index]
    );
}

private void logout() {

    new LoginFrame().setVisible(true);

    dispose();
}
}
