package com.bougastefa.gui.panels;

import com.bougastefa.models.Doctor;
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

        // Top panel with three sections
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Left section: Advanced Filter and Clear Filters buttons
        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton advancedFilterButton = new JButton("Advanced Filter");
        JButton clearFiltersButton = new JButton("Clear Filters");
        leftButtonPanel.add(advancedFilterButton);
        leftButtonPanel.add(clearFiltersButton);
        
        // Center section: Add, Edit and Delete buttons
        JPanel centerButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add Doctor");
        JButton editButton = new JButton("Edit Doctor");
        JButton deleteButton = new JButton("Delete Doctor");
        centerButtonPanel.add(addButton);
        centerButtonPanel.add(editButton);
        centerButtonPanel.add(deleteButton);

        // Right section: Refresh button
        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Refresh");
        rightButtonPanel.add(refreshButton);
        
        topPanel.add(leftButtonPanel, BorderLayout.WEST);
        topPanel.add(centerButtonPanel, BorderLayout.CENTER);
        topPanel.add(rightButtonPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Setup table for Doctors
        String[] columnNames = {"Doctor ID", "First Name", "Surname", "Address", "Email", "Hospital"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        doctorTable = new JTable(tableModel);
        doctorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(doctorTable);
        add(scrollPane, BorderLayout.CENTER);

        // Listeners for top buttons
        refreshButton.addActionListener(e -> loadDoctors());
        advancedFilterButton.addActionListener(e -> showAdvancedFilterDialog());
        clearFiltersButton.addActionListener(e -> loadDoctors());

        addButton.addActionListener(e -> showDoctorDialog(null));
        editButton.addActionListener(e -> {
            Doctor selectedDoctor = getSelectedDoctor();
            if (selectedDoctor != null) {
                showDoctorDialog(selectedDoctor);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a doctor to edit");
            }
        });
        deleteButton.addActionListener(e -> deleteSelectedDoctor());

        // Initial load of doctors
        loadDoctors();
    }

    private void loadDoctors() {
        try {
            List<Doctor> doctors = doctorService.getAllDoctors();
            populateTable(doctors);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading doctors: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<Doctor> doctors) {
        tableModel.setRowCount(0);
        for (Doctor doctor : doctors) {
            tableModel.addRow(new Object[]{
                doctor.getDoctorId(),
                doctor.getFirstName(),
                doctor.getSurname(),
                doctor.getAddress(),
                doctor.getEmail(),
                doctor.getHospital()
            });
        }
    }

    private Doctor getSelectedDoctor() {
        int row = doctorTable.getSelectedRow();
        if (row != -1) {
            String doctorId = (String) tableModel.getValueAt(row, 0);
            String firstName = (String) tableModel.getValueAt(row, 1);
            String surname = (String) tableModel.getValueAt(row, 2);
            String address = (String) tableModel.getValueAt(row, 3);
            String email = (String) tableModel.getValueAt(row, 4);
            String hospital = (String) tableModel.getValueAt(row, 5);
            
            return new Doctor(doctorId, firstName, surname, address, email, hospital);
        }
        return null;
    }

    private void showDoctorDialog(Doctor existingDoctor) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                existingDoctor == null ? "Add Doctor" : "Edit Doctor",
                true);
        dialog.setLayout(new BorderLayout(10, 10));

        // Form panel for entering doctor details
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField idField = new JTextField(20);
        JTextField firstNameField = new JTextField(20);
        JTextField surnameField = new JTextField(20);
        JTextField addressField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField hospitalField = new JTextField(20);

        if (existingDoctor != null) {
            idField.setText(existingDoctor.getDoctorId());
            idField.setEditable(false);
            firstNameField.setText(existingDoctor.getFirstName());
            surnameField.setText(existingDoctor.getSurname());
            addressField.setText(existingDoctor.getAddress());
            emailField.setText(existingDoctor.getEmail());
            hospitalField.setText(existingDoctor.getHospital());
        }

        formPanel.add(new JLabel("Doctor ID:"));
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

        // Button panel for save and cancel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        saveButton.addActionListener(e -> {
            try {
                Doctor doctor = new Doctor(
                    idField.getText().trim(),
                    firstNameField.getText().trim(),
                    surnameField.getText().trim(),
                    addressField.getText().trim(),
                    emailField.getText().trim(),
                    hospitalField.getText().trim()
                );

                if (existingDoctor == null) {
                    doctorService.addDoctor(doctor);
                    JOptionPane.showMessageDialog(this, "Doctor added successfully");
                } else {
                    doctorService.updateDoctor(doctor);
                    JOptionPane.showMessageDialog(this, "Doctor updated successfully");
                }
                loadDoctors();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteSelectedDoctor() {
        int row = doctorTable.getSelectedRow();
        if (row != -1) {
            String doctorId = (String) tableModel.getValueAt(row, 0);
            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this doctor?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                try {
                    doctorService.deleteDoctor(doctorId);
                    loadDoctors();
                    JOptionPane.showMessageDialog(this, "Doctor deleted successfully");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error deleting doctor: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a doctor to delete");
        }
    }

    private void showAdvancedFilterDialog() {
        JDialog filterDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Advanced Filter", true);
        filterDialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField filterIdField = new JTextField(20);
        JTextField filterFirstNameField = new JTextField(20);
        JTextField filterSurnameField = new JTextField(20);
        JTextField filterAddressField = new JTextField(20);
        JTextField filterEmailField = new JTextField(20);
        JTextField filterHospitalField = new JTextField(20);

        formPanel.add(new JLabel("Doctor ID contains:"));
        formPanel.add(filterIdField);
        formPanel.add(new JLabel("First Name contains:"));
        formPanel.add(filterFirstNameField);
        formPanel.add(new JLabel("Surname contains:"));
        formPanel.add(filterSurnameField);
        formPanel.add(new JLabel("Address contains:"));
        formPanel.add(filterAddressField);
        formPanel.add(new JLabel("Email contains:"));
        formPanel.add(filterEmailField);
        formPanel.add(new JLabel("Hospital contains:"));
        formPanel.add(filterHospitalField);

        JPanel filterButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton filterButton = new JButton("Filter");
        JButton cancelButton = new JButton("Cancel");
        filterButtonPanel.add(filterButton);
        filterButtonPanel.add(cancelButton);

        filterButton.addActionListener(e -> {
            try {
                List<Doctor> doctors = doctorService.getAllDoctors();

                String idFilter = filterIdField.getText().trim();
                String firstNameFilter = filterFirstNameField.getText().trim();
                String surnameFilter = filterSurnameField.getText().trim();
                String addressFilter = filterAddressField.getText().trim();
                String emailFilter = filterEmailField.getText().trim();
                String hospitalFilter = filterHospitalField.getText().trim();

                doctors = doctors.stream()
                    .filter(d -> idFilter.isEmpty() || d.getDoctorId().toLowerCase().contains(idFilter.toLowerCase()))
                    .filter(d -> firstNameFilter.isEmpty() || d.getFirstName().toLowerCase().contains(firstNameFilter.toLowerCase()))
                    .filter(d -> surnameFilter.isEmpty() || d.getSurname().toLowerCase().contains(surnameFilter.toLowerCase()))
                    .filter(d -> addressFilter.isEmpty() || d.getAddress().toLowerCase().contains(addressFilter.toLowerCase()))
                    .filter(d -> emailFilter.isEmpty() || d.getEmail().toLowerCase().contains(emailFilter.toLowerCase()))
                    .filter(d -> hospitalFilter.isEmpty() || d.getHospital().toLowerCase().contains(hospitalFilter.toLowerCase()))
                    .toList();

                populateTable(doctors);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error filtering doctors: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            filterDialog.dispose();
        });

        cancelButton.addActionListener(e -> filterDialog.dispose());

        filterDialog.add(formPanel, BorderLayout.CENTER);
        filterDialog.add(filterButtonPanel, BorderLayout.SOUTH);
        filterDialog.pack();
        filterDialog.setLocationRelativeTo(this);
        filterDialog.setVisible(true);
    }
}
