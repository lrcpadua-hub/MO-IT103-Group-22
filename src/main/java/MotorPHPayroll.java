import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class MotorPHPayroll {

    // ==================== DATA STORAGE ====================
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

    static final int MAX_EMPLOYEES  = 10000;
    static final int MAX_ATTENDANCE = 100000;

    static Scanner input = new Scanner(System.in);
    static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    static final String EMP_EXCEL = "data/MotorPH_Employee Data.xlsx";
    static final String SSS_EXCEL = "data/SSS Contribution.xlsx";

    // ==================== LOAD EMPLOYEE DATA (Excel) ====================
    static void loadEmployeeData() {
        empNumbers  = new String[MAX_EMPLOYEES];
        lastNames   = new String[MAX_EMPLOYEES];
        firstNames  = new String[MAX_EMPLOYEES];
        birthdays   = new String[MAX_EMPLOYEES];
        positions   = new String[MAX_EMPLOYEES];
        hourlyRates = new double[MAX_EMPLOYEES];
        employeeCount = 0;

        try (FileInputStream fis = new FileInputStream(new File(EMP_EXCEL));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet("Employee Details");
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Cell empNumCell = row.getCell(0);
                if (empNumCell == null) continue;
                String empNum = getCellValueAsString(empNumCell);
                if (empNum.isEmpty() || empNum.equals("Employee #")) continue;

                empNumbers [employeeCount] = empNum;
                lastNames  [employeeCount] = getCellValueAsString(row.getCell(1));
                firstNames [employeeCount] = getCellValueAsString(row.getCell(2));
                birthdays  [employeeCount] = formatDateCell(row.getCell(3));
                positions  [employeeCount] = getCellValueAsString(row.getCell(11));
                hourlyRates[employeeCount] = getCellValueAsDouble(row.getCell(18));
                employeeCount++;
            }
        } catch (Exception e) {
            System.out.println("Error loading employee data: " + e.getMessage());
        }
    }

    // ==================== LOAD ATTENDANCE DATA (Excel) ====================
    static void loadAttendanceData() {
        attEmpNumbers  = new String[MAX_ATTENDANCE];
        attDates       = new String[MAX_ATTENDANCE];
        attLogins      = new String[MAX_ATTENDANCE];
        attLogouts     = new String[MAX_ATTENDANCE];
        attendanceCount = 0;

        try (FileInputStream fis = new FileInputStream(new File(EMP_EXCEL));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet("Attendance Record");
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Cell empNumCell = row.getCell(0);
                if (empNumCell == null) continue;
                String empNum = getCellValueAsString(empNumCell);
                if (empNum.isEmpty()) continue;

                attEmpNumbers [attendanceCount] = empNum;
                attDates      [attendanceCount] = formatDateCell(row.getCell(3));
                attLogins     [attendanceCount] = formatTimeCell(row.getCell(4));
                attLogouts    [attendanceCount] = formatTimeCell(row.getCell(5));
                attendanceCount++;
            }
        } catch (Exception e) {
            System.out.println("Error loading attendance data: " + e.getMessage());
        }
    }

    // ==================== LOAD SSS TABLE (Excel) ====================
    static void loadSSSContributionTable() {
        try (FileInputStream fis = new FileInputStream(new File(SSS_EXCEL));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            int validRows = 0;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null && row.getCell(0) != null && row.getCell(3) != null) validRows++;
            }

            sssMinRanges     = new double[validRows];
            sssMaxRanges     = new double[validRows];
            sssContributions = new double[validRows];
            int index = 0;

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Cell rangeCell = row.getCell(0);
                Cell contribCell = row.getCell(3);
                if (rangeCell == null || contribCell == null) continue;

                String rangeStr = getCellValueAsString(rangeCell);
                double contribution = getCellValueAsDouble(contribCell);

                if (rangeStr.contains("Below")) {
                    sssMinRanges[index] = 0.0;
                    sssMaxRanges[index] = extractNumber(rangeStr);
                    sssContributions[index] = contribution;
                    index++;
                } else if (rangeStr.contains("-")) {
                    String[] parts = rangeStr.split("[-]");
                    if (parts.length == 2) {
                        sssMinRanges[index] = extractNumber(parts[0]);
                        sssMaxRanges[index] = extractNumber(parts[1]);
                        sssContributions[index] = contribution;
                        index++;
                    }
                }
            }
            sssBracketCount = index;
        } catch (Exception e) {
            System.out.println("Error loading SSS table: " + e.getMessage());
        }
    }

    // ==================== SAVE EMPLOYEE DATA (Excel) ====================
    static void saveEmployeeData() {
        try (FileInputStream fis = new FileInputStream(new File(EMP_EXCEL));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet("Employee Details");

            // Clear existing data rows (keep header)
            for (int i = sheet.getLastRowNum(); i >= 1; i--) {
                Row row = sheet.getRow(i);
                if (row != null) sheet.removeRow(row);
            }

            // Write updated employee data
            for (int i = 0; i < employeeCount; i++) {
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(empNumbers[i]);
                row.createCell(1).setCellValue(lastNames[i]);
                row.createCell(2).setCellValue(firstNames[i]);
                row.createCell(3).setCellValue(birthdays[i]);
                row.createCell(11).setCellValue(positions[i]);
                row.createCell(18).setCellValue(hourlyRates[i]);
            }

            try (FileOutputStream fos = new FileOutputStream(new File(EMP_EXCEL))) {
                workbook.write(fos);
            }
        } catch (Exception e) {
            System.out.println("Error saving employee data: " + e.getMessage());
        }
    }

    // ==================== SAVE ATTENDANCE DATA (Excel) ====================
    static void saveAttendanceData() {
        try (FileInputStream fis = new FileInputStream(new File(EMP_EXCEL));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet("Attendance Record");

            // Clear existing data rows (keep header)
            for (int i = sheet.getLastRowNum(); i >= 1; i--) {
                Row row = sheet.getRow(i);
                if (row != null) sheet.removeRow(row);
            }

            // Write updated attendance data
            for (int i = 0; i < attendanceCount; i++) {
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(attEmpNumbers[i]);
                row.createCell(3).setCellValue(attDates[i]);
                row.createCell(4).setCellValue(attLogins[i]);
                row.createCell(5).setCellValue(attLogouts[i]);
            }

            try (FileOutputStream fos = new FileOutputStream(new File(EMP_EXCEL))) {
                workbook.write(fos);
            }
        } catch (Exception e) {
            System.out.println("Error saving attendance data: " + e.getMessage());
        }
    }

    // ==================== ADD EMPLOYEE ====================
    static boolean addEmployee(String empNo, String lastName, String firstName,
                               String birthday, String position, double hourlyRate) {
        if (findEmployeeIndex(empNo) != -1) return false; // duplicate
        empNumbers [employeeCount] = empNo;
        lastNames  [employeeCount] = lastName;
        firstNames [employeeCount] = firstName;
        birthdays  [employeeCount] = birthday;
        positions  [employeeCount] = position;
        hourlyRates[employeeCount] = hourlyRate;
        employeeCount++;
        saveEmployeeData();
        return true;
    }

    // ==================== UPDATE EMPLOYEE ====================
    static boolean updateEmployee(String empNo, String lastName, String firstName,
                                  String birthday, String position, double hourlyRate) {
        int i = findEmployeeIndex(empNo);
        if (i == -1) return false;
        lastNames  [i] = lastName;
        firstNames [i] = firstName;
        birthdays  [i] = birthday;
        positions  [i] = position;
        hourlyRates[i] = hourlyRate;
        saveEmployeeData();
        return true;
    }

    // ==================== DELETE EMPLOYEE ====================
    static boolean deleteEmployee(String empNo) {
        int i = findEmployeeIndex(empNo);
        if (i == -1) return false;
        for (int j = i; j < employeeCount - 1; j++) {
            empNumbers [j] = empNumbers [j + 1];
            lastNames  [j] = lastNames  [j + 1];
            firstNames [j] = firstNames [j + 1];
            birthdays  [j] = birthdays  [j + 1];
            positions  [j] = positions  [j + 1];
            hourlyRates[j] = hourlyRates[j + 1];
        }
        employeeCount--;
        deleteAttendanceForEmployee(empNo);
        saveEmployeeData();
        return true;
    }

    // ==================== ADD ATTENDANCE ====================
    static void addAttendance(String empNo, String date, String login, String logout) {
        attEmpNumbers [attendanceCount] = empNo;
        attDates      [attendanceCount] = date;
        attLogins     [attendanceCount] = login;
        attLogouts    [attendanceCount] = logout;
        attendanceCount++;
        saveAttendanceData();
    }

    // ==================== UPDATE ATTENDANCE ====================
    static boolean updateAttendance(int index, String date, String login, String logout) {
        if (index < 0 || index >= attendanceCount) return false;
        attDates  [index] = date;
        attLogins [index] = login;
        attLogouts[index] = logout;
        saveAttendanceData();
        return true;
    }

    // ==================== DELETE ATTENDANCE ====================
    static boolean deleteAttendance(int index) {
        if (index < 0 || index >= attendanceCount) return false;
        for (int j = index; j < attendanceCount - 1; j++) {
            attEmpNumbers[j] = attEmpNumbers[j + 1];
            attDates     [j] = attDates     [j + 1];
            attLogins    [j] = attLogins    [j + 1];
            attLogouts   [j] = attLogouts   [j + 1];
        }
        attendanceCount--;
        saveAttendanceData();
        return true;
    }

    // Delete all attendance for a given employee
    static void deleteAttendanceForEmployee(String empNo) {
        int write = 0;
        for (int i = 0; i < attendanceCount; i++) {
            if (!attEmpNumbers[i].equals(empNo)) {
                attEmpNumbers[write] = attEmpNumbers[i];
                attDates     [write] = attDates     [i];
                attLogins    [write] = attLogins    [i];
                attLogouts   [write] = attLogouts   [i];
                write++;
            }
        }
        attendanceCount = write;
        saveAttendanceData();
    }

    // ==================== HOURS CALCULATION ====================

    // GUI overload — total hours across all records
    static double calculateHoursForPeriod(String empNo) {
        double total = 0.0;
        for (int i = 0; i < attendanceCount; i++) {
            if (!attEmpNumbers[i].equals(empNo)) continue;
            total += calculateDailyHours(attLogins[i], attLogouts[i]);
        }
        return total;
    }

    // Full overload — specific month & day range
    static double calculateHoursForPeriod(String empNum, int month, int startDay, int endDay) {
        double total = 0.0;
        for (int i = 0; i < attendanceCount; i++) {
            if (!attEmpNumbers[i].equals(empNum)) continue;
            String dateStr = attDates[i];
            if (dateStr == null || dateStr.isEmpty()) continue;
            try {
                LocalDate date = LocalDate.parse(dateStr, dateFormatter);
                if (date.getMonthValue() != month) continue;
                if (date.getDayOfMonth() < startDay || date.getDayOfMonth() > endDay) continue;
                total += calculateDailyHours(attLogins[i], attLogouts[i]);
            } catch (Exception ignored) {}
        }
        return total;
    }

    static double calculateDailyHours(String loginStr, String logoutStr) {
        if (loginStr == null || logoutStr == null ||
            loginStr.isEmpty() || logoutStr.isEmpty()) return 0.0;
        try {
            LocalTime login  = LocalTime.parse(loginStr, timeFormatter);
            LocalTime logout = LocalTime.parse(logoutStr, timeFormatter);

            LocalTime standardStart = LocalTime.of(8, 0);
            LocalTime gracePeriodEnd = LocalTime.of(8, 10);
            LocalTime standardEnd   = LocalTime.of(17, 0);

            LocalTime effectiveStart;
            if (login.isBefore(standardStart) || login.equals(standardStart))
                effectiveStart = standardStart;
            else if (!login.isAfter(gracePeriodEnd))
                effectiveStart = standardStart;
            else if (login.isBefore(standardEnd))
                effectiveStart = login;
            else return 0.0;

            LocalTime effectiveEnd;
            if (logout.isAfter(standardEnd) || logout.equals(standardEnd))
                effectiveEnd = standardEnd;
            else if (logout.isBefore(standardEnd) && logout.isAfter(standardStart))
                effectiveEnd = logout;
            else return 0.0;

            if (!effectiveEnd.isAfter(effectiveStart)) return 0.0;

            long mins = java.time.Duration.between(effectiveStart, effectiveEnd).toMinutes() - 60;
            return Math.max(mins, 0) / 60.0;
        } catch (Exception e) { return 0.0; }
    }

    static int getLastDayOfMonth(int month) {
        return java.time.YearMonth.of(2024, month).lengthOfMonth();
    }

    // ==================== EMPLOYEE MENU (CLI) ====================

    static void employeeMenu() {
        while (true) {
            printBanner("EMPLOYEE MENU");
            System.out.println("1. Enter your employee number");
            System.out.println("2. Exit the program");
            System.out.println("========================================");
            System.out.print("Enter choice: ");
            String choice = input.nextLine().trim();
            switch (choice) {
                case "1": viewEmployeeOwnDetails(); break;
                case "2": System.out.println("\nProgram terminated."); return;
                default: System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    static void viewEmployeeOwnDetails() {
        System.out.print("\nEnter your employee number: ");
        String empNum = input.nextLine().trim();
        int index = findEmployeeIndex(empNum);
        if (index == -1) { System.out.println("\nEmployee number does not exist."); return; }
        printBanner("EMPLOYEE DETAILS:");
        System.out.println("Employee Number: " + empNumbers[index]);
        System.out.println("Employee Name: " + lastNames[index] + ", " + firstNames[index]);
        System.out.println("Birthday: " + birthdays[index]);
        System.out.println("========================================");
        System.out.print("Press Enter to continue...");
        input.nextLine();
    }

    // ==================== PAYROLL STAFF MENU (CLI) ====================

    static void payrollStaffMenu() {
        while (true) {
            printBanner("PAYROLL PROCESSING MENU");
            System.out.println("1. Process Payroll");
            System.out.println("2. Exit the program");
            System.out.println("========================================");
            System.out.print("Enter choice: ");
            String choice = input.nextLine().trim();
            switch (choice) {
                case "1": processPayrollMenu(); break;
                case "2": System.out.println("\nProgram terminated."); return;
                default: System.out.println("Invalid choice. Please try again.");
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
                case "1": processOneEmployee(); break;
                case "2": processAllEmployees(); break;
                case "3": System.out.println("\nProgram terminated."); System.exit(0);
                default: System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    static void processOneEmployee() {
        System.out.print("\nEnter the employee number: ");
        String empNum = input.nextLine().trim();
        int index = findEmployeeIndex(empNum);
        if (index == -1) { System.out.println("\nEmployee number does not exist."); return; }
        printBanner("PAYROLL FOR " + lastNames[index] + ", " + firstNames[index]);
        displayEmployeePayroll(index);
    }

    static void processAllEmployees() {
        printBanner("PAYROLL FOR ALL EMPLOYEES");
        for (int i = 0; i < employeeCount; i++) {
            displayEmployeePayroll(i);
            System.out.println("\n" + "=".repeat(60));
            if (i < employeeCount - 1) {
                System.out.print("Press enter to view next employee...");
                input.nextLine();
            }
        }
    }

    static void displayEmployeePayroll(int empIndex) {
        String empNum = empNumbers[empIndex];
        double hourlyRate = hourlyRates[empIndex];
        System.out.println("\nEmployee #: " + empNum);
        System.out.println("Name: " + lastNames[empIndex] + ", " + firstNames[empIndex]);

        String[] months = {"June","July","August","September","October","November","December"};
        int[] monthNums = {6,7,8,9,10,11,12};

        for (int m = 0; m < months.length; m++) {
            int monthNum = monthNums[m];
            double hours1 = calculateHoursForPeriod(empNum, monthNum, 1, 15);
            double gross1 = hours1 * hourlyRate;
            int lastDay = getLastDayOfMonth(monthNum);
            double hours2 = calculateHoursForPeriod(empNum, monthNum, 16, lastDay);
            double gross2 = hours2 * hourlyRate;
            double monthlyGross = gross1 + gross2;
            double sss = calculateSSS(monthlyGross);
            double ph  = calculatePhilHealth(monthlyGross);
            double pi  = calculatePagIbig(monthlyGross);
            double tax = calculateWithholdingTax(monthlyGross - sss - ph - pi);
            double totalDed = sss + ph + pi + tax;

            System.out.println("\n--- " + months[m] + " 1-15 (No Deductions) ---");
            System.out.println("Hours: " + hours1 + " | Gross: " + gross1 + " | Net: " + gross1);
            System.out.println("\n--- " + months[m] + " 16-" + lastDay + " (All Deductions) ---");
            System.out.println("Hours: " + hours2 + " | Gross: " + gross2);
            System.out.println("SSS: " + sss + " | PhilHealth: " + ph + " | Pag-IBIG: " + pi + " | Tax: " + tax);
            System.out.println("Net: " + (gross2 - totalDed));
        }
    }

    // ==================== DEDUCTION CALCULATIONS ====================

    static double calculateSSS(double monthlySalary) {
        for (int i = 0; i < sssBracketCount; i++)
            if (monthlySalary >= sssMinRanges[i] && monthlySalary <= sssMaxRanges[i])
                return sssContributions[i];
        return sssBracketCount > 0 ? sssContributions[sssBracketCount - 1] : 0.0;
    }

    static double calculatePhilHealth(double monthlySalary) {
        double c;
        if      (monthlySalary <= 10000) c = 300.0;
        else if (monthlySalary >= 60000) c = 1800.0;
        else                             c = monthlySalary * 0.03;
        return c / 2;
    }

    static double calculatePagIbig(double monthlySalary) {
        double c = monthlySalary >= 1500 ? monthlySalary * 0.02 : 0;
        return Math.min(c, 100);
    }

    static double calculateWithholdingTax(double taxable) {
        if      (taxable <= 20832)  return 0;
        else if (taxable < 33333)   return (taxable - 20833) * 0.20;
        else if (taxable < 66667)   return 2500  + (taxable - 33333)  * 0.25;
        else if (taxable < 166667)  return 10833 + (taxable - 66667)  * 0.30;
        else if (taxable < 666667)  return 40833.33 + (taxable - 166667) * 0.32;
        else                        return 200833.33 + (taxable - 666667) * 0.35;
    }

    // ==================== HELPER METHODS ====================

    static int findEmployeeIndex(String empNum) {
        for (int i = 0; i < employeeCount; i++)
            if (empNumbers[i].equals(empNum)) return i;
        return -1;
    }

    static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:  return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell))
                    return cell.getDateCellValue().toString();
                return String.valueOf((int) cell.getNumericCellValue());
            case BLANK:   return "";
            default:      return cell.toString();
        }
    }

    static double getCellValueAsDouble(Cell cell) {
        if (cell == null) return 0.0;
        switch (cell.getCellType()) {
            case NUMERIC: return cell.getNumericCellValue();
            case STRING:
                try { return Double.parseDouble(cell.getStringCellValue().trim()); }
                catch (Exception e) { return 0.0; }
            case FORMULA:
                try { return cell.getNumericCellValue(); }
                catch (Exception e) { return 0.0; }
            default: return 0.0;
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
        try { return Double.parseDouble(numStr); }
        catch (Exception e) { return 0.0; }
    }

    static void printBanner(String title) {
        System.out.print("\033\143");
        System.out.println("===================================================");
        System.out.println("MotorPH Payroll System - By: Group 22");
        System.out.println(title);
        System.out.println("===================================================");
    }
}
