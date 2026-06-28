import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginFrame() {
        setTitle("MotorPH Payroll System");
        setSize(400, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        JLabel title = new JLabel("MotorPH Payroll System", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        btnLogin    = new JButton("Login");

        panel.add(title);
        panel.add(new JLabel("Username:"));
        panel.add(txtUsername);
        panel.add(new JLabel("Password:"));
        panel.add(txtPassword);
        panel.add(btnLogin);

        add(panel);

        btnLogin.addActionListener(e -> login());
        txtPassword.addActionListener(e -> login());

        loadData();
    }

    private void loadData() {
        MotorPHPayroll.loadEmployeeData();
        MotorPHPayroll.loadAttendanceData();
        MotorPHPayroll.loadSSSContributionTable();
    }

    private void login() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter both username and password.",
                "Missing Fields", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (username.equals("employee") && password.equals("12345")) {
            // Employee must enter their employee number
            String empNo = JOptionPane.showInputDialog(this,
                "Enter your Employee Number:", "Employee Login",
                JOptionPane.QUESTION_MESSAGE);
            if (empNo == null || empNo.trim().isEmpty()) return;
            empNo = empNo.trim();
            int index = MotorPHPayroll.findEmployeeIndex(empNo);
            if (index == -1) {
                JOptionPane.showMessageDialog(this,
                    "Employee number not found.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            new EmployeeDashboard(empNo);
            dispose();

        } else if (username.equals("payroll_staff") && password.equals("12345")) {
            new PayrollDashboard();
            dispose();

        } else {
            JOptionPane.showMessageDialog(this,
                "Invalid username or password.",
                "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
