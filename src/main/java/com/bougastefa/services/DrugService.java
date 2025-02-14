package com.bougastefa.services;

import com.bougastefa.database.DrugDAO;
import com.bougastefa.models.Drug;
import java.sql.SQLException;
import java.util.List;

public class DrugService {
  private DrugDAO drugDAO;

  public DrugService() {
    drugDAO = new DrugDAO();
  }

  public void addDrug(Drug drug) throws SQLException {
    drugDAO.addDrug(drug);
  }

  public List<Drug> getAllDrugs() throws SQLException {
    return drugDAO.getAllDrugs();
  }

  public Drug getDrugById(String drugId) throws SQLException {
    return drugDAO.getDrugById(drugId);
  }

  public List<Drug> getDrugsByName(String name) throws SQLException {
    return drugDAO.getDrugsByName(name);
  }

  public List<Drug> getDrugsBySideEffects(String sideEffects) throws SQLException {
    return drugDAO.getDrugsBySideEffects(sideEffects);
  }

  public List<Drug> getDrugsByBenefits(String benefits) throws SQLException {
    return drugDAO.getDrugsByBenefits(benefits);
  }

  public void updateDrug(Drug drug) throws SQLException {
    drugDAO.updateDrug(drug);
  }

  public void deleteDrug(String drugId) throws SQLException {
    drugDAO.deleteDrug(drugId);
  }
}
