import javax.swing.*;

public class App {
  public static void main(String[] args) {
    int larguraBorda = 360;
    int alturaBorda = 640;

    JFrame janela = new JFrame("Flappy Bird");
    janela.setSize(larguraBorda, alturaBorda);
    janela.setLocationRelativeTo(null);
    janela.setResizable(false);
    janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    FlappyBird flappyBird = new FlappyBird();
    janela.add(flappyBird);
    janela.pack();
    janela.setVisible(true);

    // Solicita foco no painel apos a janela ficar visivel.
    SwingUtilities.invokeLater(flappyBird::requestFocusInWindow);
  }
}
