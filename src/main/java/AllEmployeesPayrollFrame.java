import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

public class AllEmployeesPayrollFrame extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblStatus;
    private JButton btnGenerate;
    private JButton btnClose;

    private static final NumberFormat PESO_FORMAT = NumberFormat.getNumberInstance(new Locale("en", "PH"));

    static {
        PESO_FORMAT.setMinimumFractionDigits(2);
        PESO_FORMAT.setMaximumFractionDigits(2);
    }

    private static final String[] COLUMNS = {
        "Emp #", "Last Name", "First Name", "Position",
        "Hours", "Gross Pay", "SSS", "PhilHealth",
        "Pag-IBIG", "Tax", "Net Pay"
    };

    public AllEmployeesPayrollFrame() {

        setTitle("All Employees Payroll");
        setSize(1000, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ======================
        // TOP PANEL
        // ======================
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel title = new JLabel("All Employees Payroll Report");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        top.add(title);

        btnGenerate = new JButton("Generate Payroll");
        top.add(btnGenerate);

        add(top, BorderLayout.NORTH);

        // ======================
        // TABLE
        // ======================
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false; // read-only table
            }
        };

        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setRowHeight(24);
        table.getTableHeader().setReorderingAllowed(false);

        // Right-align the numeric columns (Hours through Net Pay)
        for (int i = 4; i < COLUMNS.length; i++) {
            table.getColumnModel().getColumn(i)
                 .setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
                     { setHorizontalAlignment(JLabel.RIGHT); }
                 });
        }

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // ======================
        // BOTTOM PANEL
        // ======================
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblStatus = new JLabel(" ");
        bottom.add(lblStatus);

        btnClose = new JButton("Close");
        bottom.add(btnClose);

        add(bottom, BorderLayout.SOUTH);

        // ======================
        // ACTIONS
        // ======================
        btnGenerate.addActionListener(e -> generatePayroll());
        btnClose.addActionListener(e -> dispose());

        setVisible(true);
    }

    // ======================
    // GENERATE PAYROLL TABLE
    // ======================
    private void generatePayroll() {

        tableModel.setRowCount(0); // clear existing rows
        lblStatus.setText("Generating...");
        btnGenerate.setEnabled(false);

        // Run computation in background so the UI doesn't freeze
        SwingWorker<Void, Object[]> worker = new SwingWorker<>() {

            @Override
            protected Void doInBackground() {

                for (int i = 0; i < MotorPHPayroll.employeeCount; i++) {

                    String empNo = MotorPHPayroll.empNumbers[i];
                    double rate = MotorPHPayroll.hourlyRates[i];

                    double hours = MotorPHPayroll.calculateHoursForPeriod(empNo);
                    double gross = hours * rate;

                    double sss = MotorPHPayroll.calculateSSS(gross);
                    double philhealth = MotorPHPayroll.calculatePhilHealth(gross);
                    double pagibig = MotorPHPayroll.calculatePagIbig(gross);
                    double tax = MotorPHPayroll.calculateWithholdingTax(gross);
                    double net = gross - (sss + philhealth + pagibig + tax);

                    Object[] row = {
                        empNo,
                        MotorPHPayroll.lastNames[i],
                        MotorPHPayroll.firstNames[i],
                        MotorPHPayroll.positions[i],
                        String.format("%.2f", hours),
                        "₱" + PESO_FORMAT.format(gross),
                        "₱" + PESO_FORMAT.format(sss),
                        "₱" + PESO_FORMAT.format(philhealth),
                        "₱" + PESO_FORMAT.format(pagibig),
                        "₱" + PESO_FORMAT.format(tax),
                        "₱" + PESO_FORMAT.format(net)
                    };

                    publish(row);
                }

                return null;
            }

            @Override
            protected void process(java.util.List<Object[]> chunks) {
                for (Object[] row : chunks) {
                    tableModel.addRow(row);
                }
            }

            @Override
            protected void done() {
                lblStatus.setText("Showing " + tableModel.getRowCount() + " employee(s).");
                btnGenerate.setEnabled(true);
            }
        };

        worker.execute();
    }
}