# Hospital Management System — JavaFX, JDBC & MySQL

## Project Overview

This project is a desktop Hospital Management System developed using Java 25, JavaFX 25, Maven, JDBC, and MySQL.

The application manages fictional patient registration records and supports Create, Read, Update, Delete (CRUD), and Search operations.

## Technologies Used

- Java 25
- JavaFX 25
- Maven
- JDBC
- MySQL
- Eclipse IDE
- MySQL Workbench

## Features

**Create — Add Patient:** Validates patient information and inserts a new record into the MySQL database.

**Read — Show All:** Retrieves patient records from MySQL and displays them in a JavaFX TableView.

**Update — Edit Patient:** Allows users to select an existing patient, modify their details, and save the changes.

**Delete — Remove Patient:** Displays a confirmation dialog before deleting a selected patient.

**Search — Find Patient:** Searches for patients by name or phone number using a parameterized SQL query.

**Input Validation:** Checks required fields, age, phone number, and admission date.

## Database

Database name: `hospital_db`

Table name: `patients`

The database schema is provided in `database.sql`.

## Running the Application

1. Install Java 25, Maven, and MySQL.
2. Run `database.sql` in MySQL Workbench.
3. Open the Maven project in Eclipse.
4. Configure the `HOSPITAL_DB_PASSWORD` environment variable for the local MySQL connection.
5. Run the application using `mvn javafx:run`.

## Testing

The application was tested using fictional patient records. CRUD operations were checked through the JavaFX interface, and database changes were verified using MySQL Workbench.

## Author

Adetoun Janet Adewusi

Software Engineering Technology — Artificial Intelligence

Centennial College

## Note

This project is for educational purposes only. All patient information used for testing is fictional.
