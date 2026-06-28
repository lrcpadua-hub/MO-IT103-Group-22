import javax.swing.*;
import java.awt.*;

public class PayrollDashboard extends JFrame {

    public PayrollDashboard() {
        setTitle("MotorPH - Payroll Staff Dashboard");
        setSize(450, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(6, 1, 10, 10));

        JLabel title = new JLabel("Payroll Staff Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JButton btnOneEmployee      = new JButton("Process One Employee Payroll");
        JButton btnAllEmployees     = new JButton("View All Employees Payroll");
        JButton btnManageEmployees  = new JButton("Manage Employee Records");
        JButton btnManageAttendance = new JButton("Manage Attendance Records");
        JButton btnLogout           = new JButton("Logout");

        add(title);
        add(btnOneEmployee);
        add(btnAllEmployees);
        add(btnManageEmployees);
        add(btnManageAttendance);
        add(btnLogout);

        btnOneEmployee.addActionListener(e -> new PayrollOneEmployeeFrame());
        btnAllEmployees.addActionListener(e -> new AllEmployeesPayrollFrame());
        btnManageEmployees.addActionListener(e -> new ManageEmployeesFrame());
        btnManageAttendance.addActionListener(e -> new ManageAttendanceFrame());
        btnLogout.addActionListener(e -> logout());

        setVisible(true);
    }

    private void logout() {
        new LoginFrame().setVisible(true);
        dispose();
    }
}
