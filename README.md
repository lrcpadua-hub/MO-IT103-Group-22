# MotorPH Employee App
### Computer Programming 2 – MO-IT103 | Group 22

A Java Swing-based payroll management system developed for MotorPH that automates employee payroll computation. The application calculates mandatory government deductions such as SSS, PhilHealth, Pag-IBIG, and withholding tax while providing payroll processing, employee record management, and attendance tracking features.

---

## Key Features

- Secure login system with role-based access for payroll staff and employees
- Employee profile viewing, editing, and deletion
- Attendance record management (add, edit, delete) per employee
- Payroll generation for individual employees
- Batch payroll processing for all employees
- Excel-based employee and attendance record handling with automatic saving
- Integrated 2024 SSS contribution rate calculations
- Automatic computation of PhilHealth, Pag-IBIG, and withholding tax deductions
- Input validation and confirmation dialogs for all record changes

---

## Role-Based Access

**Payroll Staff** (`payroll_staff / 12345`)
- Process payroll for one or all employees
- Add, edit, and delete any employee record
- Add, edit, and delete attendance records for any employee

**Employee** (`employee / 12345`)
- View and edit own personal information
- Delete own account
- Add, edit, and delete own attendance records

---

## How to Run the Application

1. Open the project using **Apache NetBeans IDE**
2. Make sure the following Excel files are in the `data/` folder:
   - `MotorPH_Employee Data.xlsx` (contains Employee Details and Attendance Record sheets)
   - `SSS Contribution.xlsx`
3. Right-click the project → **Properties → Run** → set Main Class to `LoginFrame`
4. Press **F6** to build and run the application

---

## Sample Login Credentials

| Username | Password | Access Level |
|---|---|---|
| `payroll_staff` | `12345` | Full payroll and record management |
| `employee` | `12345` | Own profile and attendance only |

> **Note:** After logging in as `employee`, you will be prompted to enter your Employee Number to access your profile.

---

## Technologies Used

- Java Development Kit (JDK) 25
- Java Swing for the graphical user interface
- Apache POI for reading and writing Excel files
- Apache NetBeans IDE for development

---

## Project Structure

```
src/main/java/
├── LoginFrame.java              — Login screen with role-based routing
├── MotorPHPayroll.java          — Core data engine, calculations, Excel I/O
├── EmployeeDashboard.java       — Employee self-service dashboard
├── PayrollDashboard.java        — Payroll staff dashboard
├── PayrollOneEmployeeFrame.java — Single employee payroll computation
├── AllEmployeesPayrollFrame.java— Batch payroll report for all employees
├── ManageEmployeesFrame.java    — Add, edit, delete employee records
└── ManageAttendanceFrame.java   — Add, edit, delete attendance records

data/
├── MotorPH_Employee Data.xlsx   — Employee details and attendance records
├── SSS Contribution.xlsx        — SSS contribution bracket table
├── Philhealth Contribution.xlsx — PhilHealth reference
├── Pag-ibig Contribution.xlsx   — Pag-IBIG reference
└── Witholding Tax.xlsx          — Withholding tax reference
```

---

## Team Members

- Charlene Padua
- Jose Murphy Ivan Castro
