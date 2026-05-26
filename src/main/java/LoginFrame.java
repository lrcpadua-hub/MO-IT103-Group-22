import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginFrame() {

        setTitle("MotorPH Payroll System");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 10, 10));

        JLabel title =
                new JLabel("MotorPH Payroll System",
                        SwingConstants.CENTER);

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();

        btnLogin = new JButton("Login");

        panel.add(title);
        panel.add(new JLabel("Username"));
        panel.add(txtUsername);
        panel.add(new JLabel("Password"));
        panel.add(txtPassword);

        add(panel, BorderLayout.CENTER);
        add(btnLogin, BorderLayout.SOUTH);

        btnLogin.addActionListener(e -> login());

        loadData();
    }

    private void loadData() {

        MotorPHPayroll.loadEmployeeData();
        MotorPHPayroll.loadAttendanceData();
        MotorPHPayroll.loadSSSContributionTable();
    }

    private void login() {

        String username =
                txtUsername.getText().trim();

        String password =
                new String(txtPassword.getPassword());

        if (username.equals("employee")
                && password.equals("12345")) {

            new EmployeeDashboard();
            dispose();

        } else if (username.equals("payroll_staff")
                && password.equals("12345")) {

            new PayrollDashboard();
            dispose();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid username or password"
            );
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() ->
                new LoginFrame().setVisible(true));
    }
}