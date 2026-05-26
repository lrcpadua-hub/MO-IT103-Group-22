import javax.swing.*;
import java.awt.*;

public class PayrollDashboard extends JFrame {

    private JButton btnOneEmployee;
    private JButton btnAllEmployees;
    private JButton btnLogout;

    public PayrollDashboard() {

        setTitle("MotorPH Payroll Dashboard");
        setSize(450, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new GridLayout(4, 1, 10, 10));

        JLabel title = new JLabel(
                "Payroll Staff Dashboard",
                SwingConstants.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 18));

        btnOneEmployee = new JButton("Process One Employee Payroll");
        btnAllEmployees = new JButton("View All Employees Payroll");
        btnLogout = new JButton("Logout");

        add(title);
        add(btnOneEmployee);
        add(btnAllEmployees);
        add(btnLogout);

        // Actions
        btnLogout.addActionListener(e -> logout());
        btnOneEmployee.addActionListener(e -> openOneEmployee());
        btnAllEmployees.addActionListener(e -> openAllEmployees());

        setVisible(true);
    }

    private void logout() {
        new LoginFrame().setVisible(true);
        dispose();
    }

    private void openOneEmployee() {
        new PayrollOneEmployeeFrame();
    }

    private void openAllEmployees() {
        new AllEmployeesPayrollFrame();
    }
}