import java.awt.*;          
import java.awt.event.*;    
import javax.swing.*;       

class Ball {
  static int defaultDiameter = 10;
  static Color      defaultColor     = Color.yellow;
  static Rectangle  defaultBox       = new Rectangle(0,0,100,100);
  static Boolean    defaultExpanding = true;
  static int        maxDiameter      = 50;

  private int       x;
  private int       y;
  private int       dx;
  private int       dy;
  private int       diameter;
  private Color     color;
  private Rectangle box;
  private Boolean   isExpanding;

  public Ball(int x0, int y0, int dx0, int dy0) {
    x = x0;
    y = y0;
    dx = dx0;
    dy = dy0;

    color = defaultColor;
    diameter = defaultDiameter;
    isExpanding = defaultExpanding;
  }

  public void setColor(Color c) {
    color = c;
  }

  public void setDiameter(int d) {
    diameter = d;
  }

  public void setBoundingBox(Rectangle r) {
    box = r;
  }

  public void paint(Graphics g) {
    g.setColor(color);
    g.fillOval(x, y, diameter, diameter);
  }

  void constrain() {
    int x0 = box.x;
    int y0 = box.y;
    int x1 = x0 + box.width - diameter;
    int y1 = y0 + box.height - diameter;

    if (x < x0) {
      dx = Math.abs(dx);
    }

    if (x > x1) {
      dx = -Math.abs(dx);
    }

    if (y < y0) {
      dy = Math.abs(dy);
    }

    if (y > y1) {
      dy = -Math.abs(dy);
    }
  }

  public void action() {
    x = x + dx;
    y = y + dy;

    constrain();

    if (diameter >= maxDiameter) {
      isExpanding = false;
    }

    if (diameter <= 1) {
      isExpanding = true;
    }

    if (isExpanding) {
      diameter = diameter + 1;
    } else {
      diameter = diameter - 1;
    }

  }
}

class BallPanel extends JPanel implements ActionListener {
  private int  width;
  private int  height;
  private Ball ball;
  private Ball ballTwo;

  private Timer timer = new Timer(50, this);

  public BallPanel(int width, int height) {
    this.width = width;
    this.height = height;

    ball    = new Ball(width / 10, height / 5, 5, 5);
    ballTwo = new Ball(width / 10, height / 10, 8, -5);

    ballTwo.setColor(Color.red);
    ballTwo.setBoundingBox(new Rectangle(0, 0, width, height));

    ball.setColor(Color.white);
    ball.setBoundingBox(new Rectangle(0, 0, width, height));

    ball.setDiameter(20);

    timer.start();
  }

  public void paintComponent(Graphics g) {
    g.setColor(Color.black);
    g.fillRect(0, 0, width, height);

    ball.paint(g);
    ballTwo.paint(g);
  }

  public void actionPerformed(ActionEvent e) {
    if (width != getWidth() || height != getHeight()) {
      wasResized(getWidth(), getHeight());
    }

    ball.action();
    ballTwo.action();

    repaint();
  }

  public void wasResized(int newWidth, int newHeight) {
    width = newWidth;
    height = newHeight;

    ball.setBoundingBox(new Rectangle(0, 0, width, height));
    ballTwo.setBoundingBox(new Rectangle(0, 0, width, height));
  }
}

public class BallWorld extends JFrame {
  private BallPanel panel = new BallPanel(180, 180);

  public BallWorld() {
    Container c = getContentPane();
    c.add(panel, BorderLayout.CENTER);

    setSize(200, 200);
    setLocation(100, 100);
    setVisible(true);

    setDefaultCloseOperation(EXIT_ON_CLOSE); 
  }

  public static void main(String args[]) {
    BallWorld world = new BallWorld();
  }
}

/* vim: set ts=2 sw=2 et: */
