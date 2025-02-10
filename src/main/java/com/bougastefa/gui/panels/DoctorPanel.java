package com.bougastefa.gui.panels;

import com.bougastefa.models.Doctor;
import com.bougastefa.models.Specialist;
import com.bougastefa.services.DoctorService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DoctorPanel extends JPanel {

    private DoctorService doctorService;
    private DefaultTableModel tableModel;
    private JTable doctorTable;

    public DoctorPanel() {
        doctorService = new DoctorService();
        setLayout(new BorderLayout());

        // Create table with columns
        String[] columnNames = {"ID", "First Name", "Surname", "Address", "Email", "Hospital", "Specialization"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        doctorTable = new JTable(tableModel);
        doctorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        doctorTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        doctorTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        doctorTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        doctorTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        doctorTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        doctorTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        doctorTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        doctorTable.getColumnModel().getColumn(6).setPreferredWidth(100);

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(doctorTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Doctor");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.NORTH);

        // Add listeners
        addButton.addActionListener(e -> showDoctorDialog(null));
        editButton.addActionListener(e -> {
            int row = doctorTable.getSelectedRow();
            if (row != -1) {
                showDoctorDialog(getSelectedDoctor());
            } else {
                JOptionPane.showMessageDialog(this, "Please select a doctor to edit");
            }
        });
        deleteButton.addActionListener(e -> deleteSelectedDoctor());
        refreshButton.addActionListener(e -> loadDoctors());

        // Initial load
        loadDoctors();
    }

    private void showDoctorDialog(Doctor existingDoctor) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                   existingDoctor == null ? "Add Doctor" : "Edit Doctor", 
                                   true);
        dialog.setLayout(new BorderLayout(10, 10));

        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField idField = new JTextField(20);
        JTextField firstNameField = new JTextField(20);
        JTextField surnameField = new JTextField(20);
        JTextField addressField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField hospitalField = new JTextField(20);
        JTextField specializationField = new JTextField(20);

        // If editing, populate fields
        if (existingDoctor != null) {
            idField.setText(existingDoctor.getDoctorId());
            idField.setEditable(false);
            firstNameField.setText(existingDoctor.getFirstName());
            surnameField.setText(existingDoctor.getSurname());
            addressField.setText(existingDoctor.getAddress());
            emailField.setText(existingDoctor.getEmail());
            hospitalField.setText(existingDoctor.getHospital());
            if (existingDoctor instanceof Specialist) {
                specializationField.setText(((Specialist) existingDoctor).getSpecialization());
            }
        }

        formPanel.add(new JLabel("ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("First Name:"));
        formPanel.add(firstNameField);
        formPanel.add(new JLabel("Surname:"));
        formPanel.add(surnameField);
        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Hospital:"));
        formPanel.add(hospitalField);
        formPanel.add(new JLabel("Specialization (optional):"));
        formPanel.add(specializationField);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                String specialization = specializationField.getText().trim();
                Doctor doctor;
                
                // Create the appropriate type based on whether specialization is provided
                if (specialization.isEmpty()) {
                    doctor = new Doctor(
                        idField.getText(),
                        firstNameField.getText(),
                        surnameField.getText(),
                        addressField.getText(),
                        emailField.getText(),
                        hospitalField.getText()
                    );
                } else {
                    doctor = new Specialist(
                        idField.getText(),
                        firstNameField.getText(),
                        surnameField.getText(),
                        addressField.getText(),
                        emailField.getText(),
                        hospitalField.getText(),
                        specialization
                    );
                }
                
                if (existingDoctor == null) {
                    // Adding new doctor
                    doctorService.addDoctor(doctor);
                } else {
                    // Updating existing doctor
                    doctorService.updateDoctor(doctor);
                }
                
                loadDoctors();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, 
                    existingDoctor == null ? "Doctor added successfully" : "Doctor updated successfully");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private Doctor getSelectedDoctor() {
        int row = doctorTable.getSelectedRow();
        if (row != -1) {
            String id = (String) tableModel.getValueAt(row, 0);
            String firstName = (String) tableModel.getValueAt(row, 1);
            String surname = (String) tableModel.getValueAt(row, 2);
            String address = (String) tableModel.getValueAt(row, 3);
            String email = (String) tableModel.getValueAt(row, 4);
            String hospital = (String) tableModel.getValueAt(row, 5);
            String specialization = (String) tableModel.getValueAt(row, 6);

            if (specialization != null && !specialization.trim().isEmpty()) {
                return new Specialist(id, firstName, surname, address, email, hospital, specialization);
            } else {
                return new Doctor(id, firstName, surname, address, email, hospital);
            }
        }
        return null;
    }

    private void deleteSelectedDoctor() {
        int row = doctorTable.getSelectedRow();
        if (row != -1) {
            String id = (String) tableModel.getValueAt(row, 0);
            int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this doctor?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                try {
                    doctorService.deleteDoctor(id);
                    loadDoctors();
                    JOptionPane.showMessageDialog(this, "Doctor deleted successfully");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Error deleting doctor: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a doctor to delete");
        }
    }

    private void loadDoctors() {
        try {
            List<Doctor> doctors = doctorService.getAllDoctors();
            tableModel.setRowCount(0);
            for (Doctor doctor : doctors) {
                String specialization = "";
                if (doctor instanceof Specialist) {
                    specialization = ((Specialist) doctor).getSpecialization();
                }
                
                tableModel.addRow(new Object[]{
                    doctor.getDoctorId(),
                    doctor.getFirstName(),
                    doctor.getSurname(),
                    doctor.getAddress(),
                    doctor.getEmail(),
                    doctor.getHospital(),
                    specialization
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading doctors: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
