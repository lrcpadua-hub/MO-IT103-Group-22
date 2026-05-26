import java.io.File;
import java.io.FileInputStream;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class MotorPHPayroll {

    // ==================== DATA STORAGE USING PARALLEL ARRAYS ====================
    static String[] empNumbers;
    static String[] lastNames;
    static String[] firstNames;
    static String[] birthdays;
    static String[] positions;
    static double[] hourlyRates;
    static int employeeCount = 0;

    static String[] attEmpNumbers;
    static String[] attDates;
    static String[] attLogins;
    static String[] attLogouts;
    static int attendanceCount = 0;

    static double[] sssMinRanges;
    static double[] sssMaxRanges;
    static double[] sssContributions;
    static int sssBracketCount = 0;

    static Scanner input = new Scanner(System.in);
    static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    // ==================== MAIN PROGRAM ====================
    public static void main(String[] args) {
        loadEmployeeData();
        loadAttendanceData();
        loadSSSContributionTable();
        printBanner("By: Group 28 (Team Null)");

        System.out.print("Enter username: ");
        String username = input.nextLine();
        System.out.print("Enter password: ");
        String password = input.nextLine();

        if (!(password.equals("12345") && (username.equals("employee") || username.equals("payroll_staff")))) {
            System.out.println("Incorrect username and/or password.");
            return;
        }

        if (username.equals("employee")) {
            employeeMenu();
        } else if (username.equals("payroll_staff")) {
            payrollStaffMenu();
        }

        input.close();
    }

    // ==================== DATA LOADING ====================

    static void loadEmployeeData() {
        try {
            FileInputStream fis = new FileInputStream(new File("data/MotorPH_Employee Data.xlsx"));
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet("Employee Details");
            int validRows = 0;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Cell empNumCell = row.getCell(0);
                if (empNumCell == null) continue;
                String empNum = getCellValueAsString(empNumCell);
                if (!empNum.isEmpty() && !empNum.equals("Employee #")) {
                    validRows++;
                }
            }

            empNumbers = new String[validRows];
            lastNames = new String[validRows];
            firstNames = new String[validRows];
            birthdays = new String[validRows];
            positions = new String[validRows];
            hourlyRates = new double[validRows];

            int index = 0;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell empNumCell = row.getCell(0);
                Cell lastNameCell = row.getCell(1);
                Cell firstNameCell = row.getCell(2);
                Cell birthdayCell = row.getCell(3);
                Cell positionCell = row.getCell(11);
                Cell hourlyRateCell = row.getCell(18);

                if (empNumCell == null) continue;

                String empNum = getCellValueAsString(empNumCell);
                if (empNum.isEmpty() || empNum.equals("Employee #")) continue;

                empNumbers[index] = empNum;
                lastNames[index] = getCellValueAsString(lastNameCell);
                firstNames[index] = getCellValueAsString(firstNameCell);
                birthdays[index] = formatDateCell(birthdayCell);
                positions[index] = getCellValueAsString(positionCell);
                hourlyRates[index] = getCellValueAsDouble(hourlyRateCell);
                index++;
            }

            employeeCount = index;
            workbook.close();
            fis.close();
        } catch (Exception e) {
            System.out.println("Error loading employee data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static void loadAttendanceData() {
        try {
            FileInputStream fis = new FileInputStream(new File("data/MotorPH_Employee Data.xlsx"));
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet("Attendance Record");

            int validRows = 0;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Cell empNumCell = row.getCell(0);
                if (empNumCell == null) continue;
                String empNum = getCellValueAsString(empNumCell);
                if (!empNum.isEmpty()) validRows++;
            }

            attEmpNumbers = new String[validRows];
            attDates = new String[validRows];
            attLogins = new String[validRows];
            attLogouts = new String[validRows];

            int index = 0;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell empNumCell = row.getCell(0);
                Cell dateCell = row.getCell(3);
                Cell loginCell = row.getCell(4);
                Cell logoutCell = row.getCell(5);

                if (empNumCell == null) continue;

                String empNum = getCellValueAsString(empNumCell);
                if (empNum.isEmpty()) continue;

                attEmpNumbers[index] = empNum;
                attDates[index] = formatDateCell(dateCell);
                attLogins[index] = formatTimeCell(loginCell);
                attLogouts[index] = formatTimeCell(logoutCell);
                index++;
            }

            attendanceCount = index;
            workbook.close();
            fis.close();
        } catch (Exception e) {
            System.out.println("Error loading attendance data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static void loadSSSContributionTable() {
        try {
            FileInputStream fis = new FileInputStream(new File("data/SSS Contribution.xlsx"));
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            int validRows = 0;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Cell rangeCell = row.getCell(0);
                Cell contributionCell = row.getCell(3);
                if (rangeCell != null && contributionCell != null) validRows++;
            }

            sssMinRanges = new double[validRows];
            sssMaxRanges = new double[validRows];
            sssContributions = new double[validRows];

            int index = 0;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell rangeCell = row.getCell(0);
                Cell contributionCell = row.getCell(3);

                if (rangeCell == null || contributionCell == null) continue;

                String rangeStr = getCellValueAsString(rangeCell);
                double contribution = getCellValueAsDouble(contributionCell);

                if (rangeStr.contains("Below")) {
                    double max = extractNumber(rangeStr);
                    sssMinRanges[index] = 0.0;
                    sssMaxRanges[index] = max;
                    sssContributions[index] = contribution;
                    index++;
                } else if (rangeStr.contains("-")) {
                    String[] parts = rangeStr.split("[-]");
                    if (parts.length == 2) {
                        double min = extractNumber(parts[0]);
                        double max = extractNumber(parts[1]);
                        sssMinRanges[index] = min;
                        sssMaxRanges[index] = max;
                        sssContributions[index] = contribution;
                        index++;
                    }
                }
            }

            sssBracketCount = index;
            workbook.close();
            fis.close();
        } catch (Exception e) {
            System.out.println("Error loading SSS table: " + e.getMessage());
        }
    }

    // ==================== EMPLOYEE MENU ====================

    static void employeeMenu() {
        while (true) {
            printBanner("EMPLOYEE MENU");
            System.out.println("1. Enter your employee number");
            System.out.println("2. Exit the program");
            System.out.println("========================================");
            System.out.print("Enter choice: ");

            String choice = input.nextLine().trim();

            switch (choice) {
                case "1":
                    viewEmployeeOwnDetails();
                    break;
                case "2":
                    System.out.println("\nProgram terminated.");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    static void viewEmployeeOwnDetails() {
        System.out.print("\nEnter your employee number: ");
        String empNum = input.nextLine().trim();

        int index = findEmployeeIndex(empNum);

        if (index == -1) {
            System.out.println("\nEmployee number does not exist.");
            return;
        }

        printBanner("EMPLOYEE DETAILS:");

        System.out.println("Employee Number: " + empNumbers[index]);
        System.out.println("Employee Name: " + lastNames[index] + ", " + firstNames[index]);
        System.out.println("Birthday: " + birthdays[index]);
        System.out.println("========================================");

        System.out.print("Press Enter to continue...");
        input.nextLine();
    }

    // ==================== PAYROLL STAFF MENU ====================

    static void payrollStaffMenu() {
        while (true) {
            printBanner("PAYROLL PROCESSING MENU");
            System.out.println("1. Process Payroll");
            System.out.println("2. Exit the program");
            System.out.println("========================================");
            System.out.print("Enter choice: ");

            String choice = input.nextLine().trim();

            switch (choice) {
                case "1":
                    processPayrollMenu();
                    break;
                case "2":
                    System.out.println("\nProgram terminated.");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    static void processPayrollMenu() {
        while (true) {
            printBanner("PROCESS PAYROLL");
            System.out.println("1. One employee");
            System.out.println("2. All employees");
            System.out.println("3. Exit the program");
            System.out.println("========================================");
            System.out.print("Enter choice: ");

            String choice = input.nextLine().trim();

            switch (choice) {
                case "1":
                    processOneEmployee();
                    break;
                case "2":
                    processAllEmployees();
                    break;
                case "3":
                    System.out.println("\nProgram terminated.");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // ==================== PAYROLL PROCESSING ====================

    static void processOneEmployee() {
        System.out.print("\nEnter the employee number: ");
        String empNum = input.nextLine().trim();

        int index = findEmployeeIndex(empNum);

        if (index == -1) {
            System.out.println("\nEmployee number does not exist.");
            return;
        }

        printBanner("PAYROLL FOR " + lastNames[index] + ", " + firstNames[index]);

        displayEmployeePayroll(index);
        System.out.println("\n----------------------------------------");
        System.out.println("b. Exit the program");
        System.out.println("----------------------------------------");
        System.out.print("Enter choice (or press Enter to go back): ");
        String subChoice = input.nextLine().trim();

        if (subChoice.equalsIgnoreCase("b")) {
            System.out.println("\nProgram terminated.");
            System.exit(0);
        }
    }

    static void processAllEmployees() {
        printBanner("PAYROLL FOR ALL EMPLOYEES");

        for (int i = 0; i < employeeCount; i++) {
            displayEmployeePayroll(i);
            System.out.println("\n" + "=".repeat(60));

            if (i < employeeCount - 1) {
                System.out.print("Press enter to view next employee (" + (i + 2) + "/" + employeeCount + ")...");
                input.nextLine();
                System.out.print("\033\143");
                printBanner("PAYROLL FOR ALL EMPLOYEES");
            }
        }
        System.out.println("\n----------------------------------------");
        System.out.println("b. Exit the program");
        System.out.println("----------------------------------------");
        System.out.print("Enter choice (or press Enter to go back): ");
        String subChoice = input.nextLine().trim();

        if (subChoice.equalsIgnoreCase("b")) {
            System.out.println("\nProgram terminated.");
            System.exit(0);
        }
    }

    static void displayEmployeePayroll(int empIndex) {
        String empNum = empNumbers[empIndex];
        String fullName = lastNames[empIndex] + ", " + firstNames[empIndex];
        double hourlyRate = hourlyRates[empIndex];

        System.out.println("\n========================================");
        System.out.println("Employee #: " + empNum);
        System.out.println("Employee Name: " + fullName);
        System.out.println("Birthday: " + birthdays[empIndex]);
        System.out.println("========================================");

        String[] months = { "June", "July", "August", "September", "October", "November", "December" };
        int[] monthNumbers = { 6, 7, 8, 9, 10, 11, 12 };

        for (int m = 0; m < months.length; m++) {
            String month = months[m];
            int monthNum = monthNumbers[m];

            double hours1 = calculateHoursForPeriod(empNum, monthNum, 1, 15);
            double gross1 = hours1 * hourlyRate;

            int lastDay = getLastDayOfMonth(monthNum);
            double hours2 = calculateHoursForPeriod(empNum, monthNum, 16, lastDay);
            double gross2 = hours2 * hourlyRate;

            double monthlyGross = gross1 + gross2;

            double monthlySSS = calculateSSS(monthlyGross);
            double monthlyPhilHealth = calculatePhilHealth(monthlyGross);
            double monthlyPagIbig = calculatePagIbig(monthlyGross);
            double monthlyTaxableIncome = monthlyGross - (monthlySSS + monthlyPhilHealth + monthlyPagIbig);
            double monthlyTax = calculateWithholdingTax(monthlyTaxableIncome);
            double totalMonthlyDeductions = monthlySSS + monthlyPhilHealth + monthlyPagIbig + monthlyTax;

            System.out.println("\n----------------------------------------");
            System.out.println("Cutoff Date: " + month + " 1 to " + month + " 15 (First Cutoff - No Deductions)");
            System.out.println("Total Hours Worked: " + hours1);
            System.out.println("Gross Salary: " + gross1);
            System.out.println("Net Salary: " + gross1);

            double netSalary2 = gross2 - totalMonthlyDeductions;
            System.out.println("\n----------------------------------------");
            System.out.println("Cutoff Date: " + month + " 16 to " + month + " " + lastDay
                    + " (Second Cutoff - All Monthly Deductions Applied)");
            System.out.println("Total Hours Worked: " + hours2);
            System.out.println("Gross Salary: " + gross2);
            System.out.println("Monthly-Based Deductions:");
            System.out.println("  SSS: " + monthlySSS);
            System.out.println("  PhilHealth: " + monthlyPhilHealth);
            System.out.println("  Pag-IBIG: " + monthlyPagIbig);
            System.out.println("  Withholding Tax: " + monthlyTax);
            System.out.println("Total Monthly Deductions: " + totalMonthlyDeductions);
            System.out.println("Net Salary: " + netSalary2);
        }
    }

    // ==================== HOURS CALCULATION ====================

    /**
     * GUI-facing overload: calculates total hours worked across ALL attendance
     * records for the given employee (entire available period).
     * Called by PayrollOneEmployeeFrame.
     */
    static double calculateHoursForPeriod(String empNo) {
        double totalHours = 0.0;
        for (int i = 0; i < attendanceCount; i++) {
            if (!attEmpNumbers[i].equals(empNo)) continue;
            String dateStr = attDates[i];
            if (dateStr == null || dateStr.isEmpty()) continue;
            totalHours += calculateDailyHours(attLogins[i], attLogouts[i]);
        }
        return totalHours;
    }

    /**
     * Full overload: calculates hours for a specific month and day range.
     * Used by the CLI payroll display (semi-monthly cutoffs).
     */
    static double calculateHoursForPeriod(String empNum, int month, int startDay, int endDay) {
        double totalHours = 0.0;

        for (int i = 0; i < attendanceCount; i++) {
            if (!attEmpNumbers[i].equals(empNum)) continue;

            String dateStr = attDates[i];
            if (dateStr == null || dateStr.isEmpty()) continue;

            try {
                LocalDate date = LocalDate.parse(dateStr, dateFormatter);

                if (date.getMonthValue() != month) continue;
                if (date.getDayOfMonth() < startDay || date.getDayOfMonth() > endDay) continue;

                totalHours += calculateDailyHours(attLogins[i], attLogouts[i]);

            } catch (Exception e) {
                // skip bad date records
            }
        }

        return totalHours;
    }

    static double calculateDailyHours(String loginStr, String logoutStr) {
        if (loginStr == null || logoutStr == null || loginStr.isEmpty() || logoutStr.isEmpty()) {
            return 0.0;
        }

        try {
            LocalTime login = LocalTime.parse(loginStr, timeFormatter);
            LocalTime logout = LocalTime.parse(logoutStr, timeFormatter);

            LocalTime standardStart = LocalTime.of(8, 0);
            LocalTime gracePeriodEnd = LocalTime.of(8, 10);
            LocalTime standardEnd = LocalTime.of(17, 0);

            LocalTime effectiveStart;
            if (login.isBefore(standardStart) || login.equals(standardStart)) {
                effectiveStart = standardStart;
            } else if (!login.isAfter(gracePeriodEnd)) {
                effectiveStart = standardStart;
            } else if (login.isBefore(standardEnd)) {
                effectiveStart = login;
            } else {
                return 0.0;
            }

            LocalTime effectiveEnd;
            if (logout.isAfter(standardEnd) || logout.equals(standardEnd)) {
                effectiveEnd = standardEnd;
            } else if (logout.isBefore(standardEnd) && logout.isAfter(standardStart)) {
                effectiveEnd = logout;
            } else {
                return 0.0;
            }

            if (effectiveEnd.isBefore(effectiveStart) || effectiveEnd.equals(effectiveStart)) {
                return 0.0;
            }

            long totalMinutes = java.time.Duration.between(effectiveStart, effectiveEnd).toMinutes();
            long workingMinutes = totalMinutes - 60;

            if (workingMinutes < 0) workingMinutes = 0;

            return workingMinutes / 60.0;

        } catch (Exception e) {
            return 0.0;
        }
    }

    static int getLastDayOfMonth(int month) {
        int year = 2024;
        return java.time.YearMonth.of(year, month).lengthOfMonth();
    }

    // ==================== DEDUCTION CALCULATIONS ====================

    static double calculateSSS(double monthlySalary) {
        for (int i = 0; i < sssBracketCount; i++) {
            if (monthlySalary >= sssMinRanges[i] && monthlySalary <= sssMaxRanges[i]) {
                return sssContributions[i];
            }
        }
        if (sssBracketCount > 0) {
            return sssContributions[sssBracketCount - 1];
        }
        return 0.0;
    }

    static double calculatePhilHealth(double monthlySalary) {
        double contribution;
        if (monthlySalary <= 10000) {
            contribution = 300.0;
        } else if (monthlySalary >= 60000) {
            contribution = 1800.0;
        } else {
            contribution = monthlySalary * 0.03;
        }
        return contribution / 2;
    }

    static double calculatePagIbig(double monthlySalary) {
        double contribution;
        if (monthlySalary >= 1500) {
            contribution = monthlySalary * 0.02;
        } else if (monthlySalary > 1500) {
            contribution = monthlySalary * 0.02;
        } else {
            contribution = 0;
        }
        if (contribution > 100) {
            contribution = 100;
        }
        return contribution;
    }

    static double calculateWithholdingTax(double monthlyTaxable) {
        if (monthlyTaxable <= 20832) {
            return 0;
        } else if (monthlyTaxable < 33333) {
            return (monthlyTaxable - 20833) * 0.20;
        } else if (monthlyTaxable < 66667) {
            return 2500 + (monthlyTaxable - 33333) * 0.25;
        } else if (monthlyTaxable < 166667) {
            return 10833 + (monthlyTaxable - 66667) * 0.30;
        } else if (monthlyTaxable < 666667) {
            return 40833.33 + (monthlyTaxable - 166667) * 0.32;
        } else {
            return 200833.33 + (monthlyTaxable - 666667) * 0.35;
        }
    }

    // ==================== HELPER METHODS ====================

    static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((int) cell.getNumericCellValue());
            case BLANK:
                return "";
            default:
                return cell.toString();
        }
    }

    static double getCellValueAsDouble(Cell cell) {
        if (cell == null) return 0.0;
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue().trim());
                } catch (Exception e) {
                    return 0.0;
                }
            case FORMULA:
                try {
                    return cell.getNumericCellValue();
                } catch (Exception e) {
                    return 0.0;
                }
            default:
                return 0.0;
        }
    }

    static String formatDateCell(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            java.util.Date date = cell.getDateCellValue();
            return new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
        }
        return getCellValueAsString(cell);
    }

    static String formatTimeCell(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            java.util.Date date = cell.getDateCellValue();
            return new java.text.SimpleDateFormat("HH:mm:ss").format(date);
        }
        return getCellValueAsString(cell);
    }

    static double extractNumber(String str) {
        String numStr = str.replaceAll("[^0-9.]", "").trim();
        try {
            return Double.parseDouble(numStr);
        } catch (Exception e) {
            return 0.0;
        }
    }

    static int findEmployeeIndex(String empNum) {
        for (int i = 0; i < employeeCount; i++) {
            if (empNumbers[i].equals(empNum)) {
                return i;
            }
        }
        return -1;
    }

    static void printBanner(String title) {
        System.out.print("\033\143");
        System.out.println("===================================================");
        System.out.println("██▄  ▄██  ▄▄▄ ▄▄▄▄▄▄ ▄▄▄  ▄▄▄▄    █████▄ ██  ██ ");
        System.out.println("██ ▀▀ ██ ██▀██  ██  ██▀██ ██▄█▄   ██▄▄█▀ ██████ ");
        System.out.println("██    ██ ▀███▀  ██  ▀███▀ ██ ██   ██     ██  ██ ");
        System.out.println();
        System.out.println(title);
        System.out.println("===================================================");
    }
}