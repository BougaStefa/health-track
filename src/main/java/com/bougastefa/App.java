package com.bougastefa;

import com.bougastefa.gui.MainFrame;
import javax.swing.SwingUtilities;

public class App {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(
        () -> {
          MainFrame mainFrame = new MainFrame();
        });
  }
}
