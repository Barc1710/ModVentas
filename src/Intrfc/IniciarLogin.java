package Intrfc;

import javax.swing.*;
import java.awt.*;

public class IniciarLogin extends JFrame {
    public IniciarLogin() {
        super("Login");
        setSize(400,320);
        setResizable(false);
        setLayout(new BorderLayout());
        add(new Login());

        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

}
