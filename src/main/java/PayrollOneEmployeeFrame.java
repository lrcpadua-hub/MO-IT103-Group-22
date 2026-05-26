import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

public class PayrollOneEmployeeFrame extends JFrame {

    // ======================
    // INPUT + BUTTONS
    // ======================
    private JTextField txtEmpNo;
    private JButton btnLoad;
    private JButton btnCompute;

    // ======================
    // EMPLOYEE INFO LABELS
    // ======================
    private JLabel lblName;
    private JLabel lblPosition;
    private JLabel lblRate;

    // ======================
    // PAYROLL LABELS
    // ======================
    private JLabel lblHours;
    private JLabel lblGross;
    private JLabel lblSSS;
    private JLabel lblPhilHealth;
    private JLabel lblPagIbig;
    private JLabel lblTax;
    private JLabel lblNet;

    // Formatter for peso amounts  e.g.  ₱12,345.67
    private static final NumberFormat PESO_FORMAT = NumberFormat.getNumberInstance(new Locale("en", "PH"));

    static {
        PESO_FORMAT.setMinimumFractionDigits(2);
        PESO_FORMAT.setMaximumFractionDigits(2);
    }

    public PayrollOneEmployeeFrame() {

        setTitle("Process One Employee Payroll");
        setSize(650, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());

        // ======================
        // TOP PANEL (INPUT)
        // ======================
        JPanel top = new JPanel();

        top.add(new JLabel("Employee No:"));

        txtEmpNo = new JTextField(10);
        top.add(txtEmpNo);

        btnLoad = new JButton("Load");
        top.add(btnLoad);

        btnCompute = new JButton("Compute Payroll");
        top.add(btnCompute);

        add(top, BorderLayout.NORTH);

        // ======================
        // CENTER PANEL (EMPLOYEE INFO)
        // ======================
        JPanel center = new JPanel(new GridLayout(3, 2, 10, 10));
        center.setBorder(BorderFactory.createTitledBorder("Employee Information"));

        center.add(new JLabel("Name:"));
        lblName = new JLabel("-");
        center.add(lblName);

        center.add(new JLabel("Position:"));
        lblPosition = new JLabel("-");
        center.add(lblPosition);

        center.add(new JLabel("Hourly Rate:"));
        lblRate = new JLabel("-");
        center.add(lblRate);

        add(center, BorderLayout.CENTER);

        // ======================
        // BOTTOM PANEL (PAYROLL)
        // ======================
        JPanel payroll = new JPanel(new GridLayout(7, 2, 10, 10));
        payroll.setBorder(BorderFactory.createTitledBorder("Payroll Computation"));

        payroll.add(new JLabel("Hours Worked:"));
        lblHours = new JLabel("-");
        payroll.add(lblHours);

        payroll.add(new JLabel("Gross Pay:"));
        lblGross = new JLabel("-");
        payroll.add(lblGross);

        payroll.add(new JLabel("SSS:"));
        lblSSS = new JLabel("-");
        payroll.add(lblSSS);

        payroll.add(new JLabel("PhilHealth:"));
        lblPhilHealth = new JLabel("-");
        payroll.add(lblPhilHealth);

        payroll.add(new JLabel("Pag-IBIG:"));
        lblPagIbig = new JLabel("-");
        payroll.add(lblPagIbig);

        payroll.add(new JLabel("Tax:"));
        lblTax = new JLabel("-");
        payroll.add(lblTax);

        payroll.add(new JLabel("Net Pay:"));
        lblNet = new JLabel("-");
        payroll.add(lblNet);

        add(payroll, BorderLayout.SOUTH);

        // ======================
        // ACTIONS
        // ======================
        btnLoad.addActionListener(e -> loadEmployee());
        btnCompute.addActionListener(e -> computePayroll());

        setVisible(true);
    }

    // ======================
    // LOAD EMPLOYEE DATA
    // ======================
    private void loadEmployee() {

        String empNo = txtEmpNo.getText().trim();

        int index = MotorPHPayroll.findEmployeeIndex(empNo);

        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Employee not found");
            return;
        }

        lblName.setText(
                MotorPHPayroll.lastNames[index] + ", " +
                MotorPHPayroll.firstNames[index]
        );

        lblPosition.setText(MotorPHPayroll.positions[index]);

        lblRate.setText("₱" + PESO_FORMAT.format(MotorPHPayroll.hourlyRates[index]));

        // Clear payroll fields when a new employee is loaded
        lblHours.setText("-");
        lblGross.setText("-");
        lblSSS.setText("-");
        lblPhilHealth.setText("-");
        lblPagIbig.setText("-");
        lblTax.setText("-");
        lblNet.setText("-");
    }

    // ======================
    // COMPUTE PAYROLL
    // ======================
    private void computePayroll() {

        String empNo = txtEmpNo.getText().trim();

        int index = MotorPHPayroll.findEmployeeIndex(empNo);

        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Employee not found");
            return;
        }

        double rate = MotorPHPayroll.hourlyRates[index];
        double hours = MotorPHPayroll.calculateHoursForPeriod(empNo);
        double gross = hours * rate;

        double sss = MotorPHPayroll.calculateSSS(gross);
        double philhealth = MotorPHPayroll.calculatePhilHealth(gross);
        double pagibig = MotorPHPayroll.calculatePagIbig(gross);
        double tax = MotorPHPayroll.calculateWithholdingTax(gross);

        double net = gross - (sss + philhealth + pagibig + tax);

        // Display with 2-decimal formatting
        lblHours.setText(String.format("%.2f hrs", hours));
        lblGross.setText("₱" + PESO_FORMAT.format(gross));
        lblSSS.setText("₱" + PESO_FORMAT.format(sss));
        lblPhilHealth.setText("₱" + PESO_FORMAT.format(philhealth));
        lblPagIbig.setText("₱" + PESO_FORMAT.format(pagibig));
        lblTax.setText("₱" + PESO_FORMAT.format(tax));
        lblNet.setText("₱" + PESO_FORMAT.format(net));
    }
}