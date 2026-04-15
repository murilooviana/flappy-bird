import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
  int larguraBorda = 360;
  int alturaBorda = 640;

  // IMAGENS
  Image birdImage;
  Image backgroundImage;
  Image bottomPipeImage;
  Image topPipeImage;

  // PASSARO
  int birdX = larguraBorda / 8;
  int birdY = alturaBorda / 2;
  int birdWidth = 34;
  int birdHeight = 24;

  class Bird {
    int x = birdX;
    int y = birdY;
    int width = birdWidth;
    int height = birdHeight;
    Image image;

    Bird(Image img) {
      this.image = img;
    }
  }

  // CANOS
  int pipeX = larguraBorda;
  int pipeY = 0;
  int pipeWidth = 64;
  int pipeHeight = 512;

  class Pipe {
    int x = pipeX;
    int y = pipeY;
    int width = pipeWidth;
    int height = pipeHeight;
    Image image;
    boolean passed = false;

    Pipe(Image img) {
      this.image = img;
    }
  }

  // LOGICA DO JOGO
  Bird bird;
  int velocityX = -4;
  int velocityY = 0;
  int gravity = 1;

  ArrayList<Pipe> pipes;
  Timer gameLoop;
  Timer placePipeTimer;

  boolean gameOver = false;
  double counter = 0;

  FlappyBird() { 
    setPreferredSize(new Dimension(larguraBorda, alturaBorda)); // Define o tamanho do painel
    setFocusable(true); // Permite que o painel receba foco para capturar eventos de teclado
    addKeyListener(this); // Adiciona o KeyListener para capturar eventos de teclado

    backgroundImage = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage(); // Carrega a imagem de fundo
    birdImage = new ImageIcon(getClass().getResource("./flappybird.png")).getImage(); 
    topPipeImage = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
    bottomPipeImage = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

    bird = new Bird(birdImage);
    pipes = new ArrayList<>();

    placePipeTimer = new Timer(1500, new ActionListener() { // Timer para colocar os canos a cada 1.5 segundos
      @Override
      public void actionPerformed(ActionEvent e) { // Coloca os canos
        placePipes();
      }
    });
    placePipeTimer.start();

    gameLoop = new Timer(1000 / 60, this); // Timer para o loop do jogo, atualizando a 60 FPS
    gameLoop.start(); // Inicia o loop do jogo
  }

  public void paintComponent(Graphics g) { // Sobrescreve o método paintComponent para desenhar os elementos do jogo
    super.paintComponent(g); // Chama o método da classe pai para garantir que o painel seja limpo antes de desenhar
    draw(g);
  }

  public void placePipes() { // Método para colocar os canos
    int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2)); // Gera uma posição Y aleatória para o cano superior, garantindo que haja um espaço entre os canos

    Pipe topPipe = new Pipe(topPipeImage); 
    topPipe.y = randomPipeY;
    topPipe.x = pipeX;
    pipes.add(topPipe);

    Pipe bottomPipe = new Pipe(bottomPipeImage); // Cria o cano inferior e posiciona-o abaixo do cano superior, garantindo um espaço de 150 pixels entre eles
    bottomPipe.y = topPipe.y + pipeHeight + 150; // Espaço entre os canos
    bottomPipe.x = pipeX;
    pipes.add(bottomPipe);
  }

  public void draw(Graphics g) { // Desenha o fundo, o pássaro e os canos
    g.drawImage(backgroundImage, 0, 0, larguraBorda, alturaBorda, null);
    g.drawImage(bird.image, bird.x, bird.y, bird.width, bird.height, null);

    for (int i = 0; i < pipes.size(); i++) { // Desenha cada cano na tela
      Pipe pipe = pipes.get(i);
      g.drawImage(pipe.image, pipe.x, pipe.y, pipe.width, pipe.height, null);
    }

    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial", Font.BOLD, 24));
    g.drawString("Score: " + (int) counter, 10, 30);

    if (gameOver) {
      g.drawString("Press SPACE to restart", 45, alturaBorda / 2);
    }
  }

  public void move() { // Atualiza a posição do pássaro e dos canos, e verifica colisões
    velocityY += gravity;
    bird.y += velocityY;
    bird.y = Math.max(bird.y, 0); // Impede que o pássaro ultrapasse o topo da tela

    if (bird.y > alturaBorda) { // Verifica se o pássaro caiu para baixo da tela, o que resulta em game over
      gameOver = true;
    }

    for (int i = 0; i < pipes.size(); i++) { // Atualiza a posição de cada cano, move-os para a esquerda e verifica se o pássaro passou por eles ou colidiu
      Pipe pipe = pipes.get(i);// Move o cano para a esquerda
      pipe.x += velocityX; // Verifica se o pássaro passou pelo cano, incrementando a pontuação

      if (!pipe.passed && bird.x > pipe.x + pipe.width) { // Verifica se o pássaro passou completamente pelo cano, marcando-o como passado e incrementando a pontuação
        pipe.passed = true; // Marca o cano como passado para evitar múltiplos incrementos de pontuação
        counter += 0.5; // Incrementa a pontuação em 0.5 para cada cano, resultando em 1 ponto por par de canos (superior e inferior)
      }

      if (collision(bird, pipe)) { // Verifica se houve colisão entre o pássaro e o cano, o que resulta em game over
        gameOver = true;
      }
    }
  }

  public boolean collision(Bird a, Pipe b) { // Verifica se houve colisão entre o pássaro e um cano usando a técnica de detecção de colisão AABB (Axis-Aligned Bounding Box)
    return a.x < b.x + b.width &&
           a.x + a.width > b.x &&
           a.y < b.y + b.height &&
           a.y + a.height > b.y;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    move();
    repaint();

    if (gameOver) {
      placePipeTimer.stop();
      gameLoop.stop();
    }
  }

  private void restartGame() {
    bird.x = birdX;
    bird.y = birdY;
    velocityY = 0;
    pipes.clear();
    counter = 0;
    gameOver = false;

    placePipeTimer.start();
    gameLoop.start();
  }

  @Override
  public void keyPressed(KeyEvent e) {
    int keyCode = e.getKeyCode();

    if (keyCode == KeyEvent.VK_SPACE) {
      if (gameOver) {
        restartGame();
      } else {
        velocityY = -8;
      }
      return;
    }

    if (gameOver && keyCode == KeyEvent.VK_ENTER) {
      restartGame();
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }
}
