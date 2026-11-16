
package com.janet.hospital;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

public class HospitalApp extends Application {

    private static final String URL =
        "jdbc:mysql://localhost:3306/hospital_db";
    private static final String USER = "root";
    private static final String PASSWORD =
    System.getenv("HOSPITAL_DB_PASSWORD");

    private final TextField idField = new TextField();
    private final TextField nameField = new TextField();
    private final TextField ageField = new TextField();
    private final ComboBox<String> genderBox = new ComboBox<>();
    private final TextField phoneField = new TextField();
    private final TextField diagnosisField = new TextField();
    private final DatePicker datePicker = new DatePicker();
    private final TextField searchField = new TextField();
    private final Label status = new Label("Ready");

    private final TableView<Patient> table = new TableView<>();
    private final ObservableList<Patient> records =
        FXCollections.observableArrayList();

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        Label title = new Label("Hospital Management System");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Label subtitle = new Label("Patient Registration and Records");

        VBox header = new VBox(5, title, subtitle);
        root.setTop(header);

        idField.setEditable(false);
        genderBox.getItems().addAll(
            "Female", "Male", "Non-binary", "Prefer not to say"
        );

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        String[] labels = {
            "Patient ID", "Full Name", "Age", "Gender",
            "Phone", "Diagnosis", "Admission Date"
        };

        Control[] fields = {
            idField, nameField, ageField, genderBox,
            phoneField, diagnosisField, datePicker
        };

        for (int i = 0; i < labels.length; i++) {
            form.add(new Label(labels[i]), 0, i);
            form.add(fields[i], 1, i);
        }

        Button add = new Button("Add Patient");
        Button update = new Button("Update");
        Button delete = new Button("Delete");
        Button clear = new Button("Clear");

        add.setOnAction(e -> addPatient());
        update.setOnAction(e -> updatePatient());
        delete.setOnAction(e -> deletePatient());
        clear.setOnAction(e -> clearForm());

        HBox actions1 = new HBox(10, add, update);
        HBox actions2 = new HBox(10, delete, clear);

        VBox left = new VBox(
            15, new Label("Patient Details"),
            form, actions1, actions2
        );

        createColumn("ID", "id");
        createColumn("Name", "name");
        createColumn("Age", "age");
        createColumn("Gender", "gender");
        createColumn("Phone", "phone");
        createColumn("Diagnosis", "diagnosis");
        createColumn("Admission Date", "admissionDate");

        table.setItems(records);
        table.setPrefWidth(750);

        table.getSelectionModel().selectedItemProperty()
            .addListener((obs, old, patient) -> {
                if (patient != null) populateForm(patient);
            });

        Button search = new Button("Search");
        Button showAll = new Button("Show All");

        search.setOnAction(e -> searchPatients());
        showAll.setOnAction(e -> loadPatients());

        HBox searchBar = new HBox(
            10, searchField, search, showAll
        );

        VBox right = new VBox(
            10, new Label("Patient Records"), searchBar, table
        );

        HBox center = new HBox(25, left, right);
        center.setPadding(new Insets(20, 0, 20, 0));
        root.setCenter(center);

        root.setBottom(status);

        stage.setScene(new Scene(root, 1150, 600));
        stage.setTitle("Hospital Management System");
        stage.show();

        loadPatients();
    }

    private void createColumn(String title, String property) {
        TableColumn<Patient, Object> column =
            new TableColumn<>(title);
        column.setCellValueFactory(
            new PropertyValueFactory<>(property)
        );
        column.setPrefWidth(105);
        table.getColumns().add(column);
    }

    private boolean valid() {
        try {
            if (nameField.getText().trim().isEmpty() ||
                phoneField.getText().trim().isEmpty() ||
                diagnosisField.getText().trim().isEmpty() ||
                genderBox.getValue() == null ||
                datePicker.getValue() == null) {
                status.setText("Error: All fields are required.");
                return false;
            }

            int age = Integer.parseInt(ageField.getText().trim());

            if (age < 0 || age > 125) {
                status.setText("Error: Age must be 0-125.");
                return false;
            }

            if (!phoneField.getText().trim()
                    .matches("[+0-9()\\-\\s]{7,20}")) {
                status.setText("Error: Invalid phone number.");
                return false;
            }

            if (datePicker.getValue().isAfter(LocalDate.now())) {
                status.setText("Error: Future admission date.");
                return false;
            }

            return true;

        } catch (NumberFormatException ex) {
            status.setText("Error: Age must be a whole number.");
            return false;
        }
    }

    private void addPatient() {
        if (!valid()) return;

        String sql = "INSERT INTO patients " +
            "(name, age, gender, phone, diagnosis, admission_date) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            fillStatement(ps);
            ps.executeUpdate();
            loadPatients();
            clearForm();
            status.setText("Patient added successfully.");

        } catch (SQLException ex) {
            status.setText("Database error: " + ex.getMessage());
        }
    }

    private void loadPatients() {
        records.clear();

        String sql = "SELECT * FROM patients ORDER BY patient_id";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                records.add(readPatient(rs));
            }

            status.setText("Loaded " + records.size() + " patients.");

        } catch (SQLException ex) {
            status.setText("Database error: " + ex.getMessage());
        }
    }

    private void updatePatient() {
        Patient selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            status.setText("Select a patient to update.");
            return;
        }

        if (!valid()) return;

        String sql = "UPDATE patients SET name=?, age=?, " +
            "gender=?, phone=?, diagnosis=?, admission_date=? " +
            "WHERE patient_id=?";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            fillStatement(ps);
            ps.setInt(7, selected.getId());
            int count = ps.executeUpdate();

            loadPatients();
            clearForm();
            status.setText(count > 0 ?
                "Patient updated successfully." : "Record not found.");

        } catch (SQLException ex) {
            status.setText("Database error: " + ex.getMessage());
        }
    }

    private void deletePatient() {
        Patient selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            status.setText("Select a patient to delete.");
            return;
        }

        Alert alert = new Alert(
            Alert.AlertType.CONFIRMATION,
            "Delete patient " + selected.getName() + "?",
            ButtonType.YES, ButtonType.NO
        );

        Optional<ButtonType> response = alert.showAndWait();

        if (response.isEmpty() ||
            response.get() != ButtonType.YES) return;

        String sql = "DELETE FROM patients WHERE patient_id=?";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, selected.getId());
            int count = ps.executeUpdate();

            loadPatients();
            clearForm();
            status.setText(count > 0 ?
                "Patient deleted successfully." : "Record not found.");

        } catch (SQLException ex) {
            status.setText("Database error: " + ex.getMessage());
        }
    }

    private void searchPatients() {
        String sql = "SELECT * FROM patients " +
            "WHERE name LIKE ? OR phone LIKE ?";

        records.clear();

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String keyword = "%" + searchField.getText().trim() + "%";
            ps.setString(1, keyword);
            ps.setString(2, keyword);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    records.add(readPatient(rs));
                }
            }

            status.setText("Found " + records.size() + " patients.");

        } catch (SQLException ex) {
            status.setText("Database error: " + ex.getMessage());
        }
    }

    private Patient readPatient(ResultSet rs) throws SQLException {
        return new Patient(
            rs.getInt("patient_id"),
            rs.getString("name"),
            rs.getInt("age"),
            rs.getString("gender"),
            rs.getString("phone"),
            rs.getString("diagnosis"),
            rs.getDate("admission_date").toLocalDate()
        );
    }

    private void fillStatement(PreparedStatement ps)
            throws SQLException {
        ps.setString(1, nameField.getText().trim());
        ps.setInt(2, Integer.parseInt(ageField.getText().trim()));
        ps.setString(3, genderBox.getValue());
        ps.setString(4, phoneField.getText().trim());
        ps.setString(5, diagnosisField.getText().trim());
        ps.setDate(6, Date.valueOf(datePicker.getValue()));
    }

    private void populateForm(Patient p) {
        idField.setText(String.valueOf(p.getId()));
        nameField.setText(p.getName());
        ageField.setText(String.valueOf(p.getAge()));
        genderBox.setValue(p.getGender());
        phoneField.setText(p.getPhone());
        diagnosisField.setText(p.getDiagnosis());
        datePicker.setValue(p.getAdmissionDate());
    }

    private void clearForm() {
        table.getSelectionModel().clearSelection();
        idField.clear();
        nameField.clear();
        ageField.clear();
        genderBox.setValue(null);
        phoneField.clear();
        diagnosisField.clear();
        datePicker.setValue(null);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
