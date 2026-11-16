
package com.janet.hospital;

import java.time.LocalDate;

public class Patient {
    private final int id;
    private final String name;
    private final int age;
    private final String gender;
    private final String phone;
    private final String diagnosis;
    private final LocalDate admissionDate;

    public Patient(int id, String name, int age,
                   String gender, String phone,
                   String diagnosis, LocalDate admissionDate) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.diagnosis = diagnosis;
        this.admissionDate = admissionDate;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getPhone() { return phone; }
    public String getDiagnosis() { return diagnosis; }
    public LocalDate getAdmissionDate() { return admissionDate; }
}
