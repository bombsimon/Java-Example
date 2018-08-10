import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class PhotoViewer extends JFrame {
  private static final int DEFAULT_WIDTH = 960;
  private static final int DEFAULT_HEIGHT = 640;

  // PhotoViewer will create a new JFrame to display the view.
  public PhotoViewer() {
    setTitle("PhotoViewer v0.1");
    add(new PhotoContainer(DEFAULT_WIDTH, DEFAULT_HEIGHT));

    // Set size, location and visibility
    setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    setLocation(100, 100);
    setVisible(true);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  // Create a PhotoViewer window
  public static void main(String[] args) {
    new PhotoViewer();
  }
}

class PhotoContainer extends JPanel {
  // Create an ArrayList that will hold all images drawn in the container
  // This is an ArrayList to be able to expand, remove and add objects in specific positions
  private ArrayList<Photo> images = new ArrayList<Photo>();

  // An easier Array with just URLs so it's clearer where to add more images
  private String[] imageUrls = {
    "http://icons.iconarchive.com/icons/danleech/simple/96/skype-icon.png",
    "http://icons.iconarchive.com/icons/danleech/simple/96/picasa-icon.png",
    "http://icons.iconarchive.com/icons/designbolts/flat-social-media/96/Reddit-icon.png",
    "http://icons.iconarchive.com/icons/dakirby309/windows-8-metro/96/Web-Gmail-Metro-icon.png",
    "http://icons.iconarchive.com/icons/designbolts/free-instagram/96/Active-Instagram-1-icon.png",
    "http://icons.iconarchive.com/icons/dakirby309/windows-8-metro/96/Web-Youtube-alt-2-Metro-icon.png",
    "http://icons.iconarchive.com/icons/danleech/simple/96/twitter-icon.png",
    "http://icons.iconarchive.com/icons/danleech/simple/96/facebook-icon.png",
  };

  // The PhotoContainer constructor.
  public PhotoContainer(int width, int height) {
    setSize(width, height);

    // Will add Photo-objects created of all images in the ArrayList
    for (int i = 0; i < imageUrls.length; i++) {
      images.add(new Photo(imageUrls[i], getWidth(), getHeight()));
    }

    // Setup mouse adapter and add listener
    MouseMoveAdapter mma = new MouseMoveAdapter();
    addMouseMotionListener(mma);
    addMouseListener(mma);

    // Set double buffered to draw the images off screen
    setDoubleBuffered(true);
  }

  // Override paint method which is called each repaint
  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    // Loop over the ArrayList with images and draw them in current order
    for (Photo p : images) {
      // A Photo can draw itself so call that method
      p.paint(g);
    }
  }

  // Extend MouseAdapter to handle mouse actions. Contains mousePressed (get
  // coordinates/active image), mouseReleased (deactivate image), mouseClicked
  // (flip image) and mouseDragged (move image)
  class MouseMoveAdapter extends MouseAdapter {
    private int x;
    private int y;
    // imageActive will be true every time a mouse event detects an image as hit
    private boolean imageActive = false;

    // Get the last index of the ArrayList to know what image to act on and
    // where to add the image hit so it will be rendered last (on top)
    private int lastIndex = images.size() - 1;

    // Get coordinates each time the mouse button is pressed
    // This will be used to calculate how to move the images and if an image is hit
    @Override
    public void mousePressed(MouseEvent e) {
      x = e.getX();
      y = e.getY();

      checkIfImageIsClicked();
    }

    // Every time the mouse is released (e.g. after dragging an image, an image is no longer active
    @Override
    public void mouseReleased(MouseEvent e) {
      imageActive = false;
    }

    // If the mouse was just clicked (not dragged), check what was clicked.
    // If an image is clicked, flip that image over
    @Override
    public void mouseClicked(MouseEvent e) {
      // mouseClicked is called AFTER mouseReleased so we need a new check if an image was clicked
      checkIfImageIsClicked();

      if (!imageActive) {
        return;
      }

      // Clicking should flip the image. If it's already flipped, change to not
      // flipped, otherwise flip it
      Photo p = images.get(lastIndex);
      Boolean newFlip = p.getFlipped() ? false : true;
      p.setFlipped(newFlip);

      // Must tell that an image is no longer actvie (again, mouseReleased is
      // called before mouseClicked so this would be true otherwise
      imageActive = false;

      // Draw the image again
      repaint();
    }

    // If the mouse was/is dragged, get current position and remove the staring position
    // (x and y, got from mousePressed) to re-calculate where the image should be
    @Override
    public void mouseDragged(MouseEvent e) {
      if (!imageActive) {
        return;
      }

      int dx = e.getX() - x;
      int dy = e.getY() - y;

      Photo p = images.get(lastIndex);

      p.addX(dx);
      p.addY(dy);

      repaint();

      x += dx;
      y += dy;
    }

    // Called on mousePressed an mouseClicked.
    private void checkIfImageIsClicked() {
      Collections.reverse(images);

      for (Photo p : images) {
        if (wasClicked(p)) {
          imageActive = true;

          images.remove(p);
          images.add(0, p);

          // No more images should be checked, break out of the loop
          break;
        }
      }

      // Must reset the ArrayList so the images render as they should regarding z-order
      Collections.reverse(images);
    }

    // If x and y coordinates from the mouse is inside an image, return true (otherwise false)
    private boolean wasClicked(Photo i) {
      return (
          x >= i.getX()
          && x <= (i.getX() + i.getImage().getIconWidth())
          && y >= i.getY()
          && y <= (i.getY() + i.getImage().getIconHeight())
          );
    }
  }
}

// Each image will be of this class. The class knows if it's flipped, it's
// coordinates and what image to draw
class Photo {
  private static final ImageIcon back = getImageFromUrl("http://icons.iconarchive.com/icons/dakirby309/windows-8-metro/96/Folders-OS-Exit-Full-Screen-Metro-icon.png");

  private int x;
  private int y;
  private boolean isFlipped = false;
  private ImageIcon front;

  public int getX() {
    return x;
  }

  public void addX(int n) {
    x += n;
  }

  public int getY() {
    return y;
  }

  public void addY(int n) {
    y += n;
  }

  public boolean getFlipped() {
    return isFlipped;
  }

  public void setFlipped(Boolean value) {
    isFlipped = value;
  }

  public ImageIcon getImage() {
    return front;
  }

  public Photo(String url, int maxX, int maxY) {
    front = Photo.getImageFromUrl(url);

    // Put the image at a random position
    Random rand = new Random();
    x = rand.nextInt(maxX - (front.getIconWidth() * 2));
    y = rand.nextInt(maxY - (front.getIconHeight() * 2));
  }

  private static ImageIcon getImageFromUrl(String inUrl) {
    URL url = null;
    BufferedImage img = null;

    try {
      url = new URL(inUrl);
      img = ImageIO.read(url);
    } catch (java.net.MalformedURLException e) {
      System.err.printf("Could not create URL: %s%n", e.getMessage());
    } catch (java.io.IOException e) {
      System.err.printf("Could not read ImageIO (you must be online): %s%n", e.getMessage());
      System.exit(0);
    }

    return new ImageIcon(img);
  }

  // Paint the image (stored as ImageIcon)
  public void paint(Graphics g) {
    // We need to check if the image is flipped or not so first we get the correct image
    ImageIcon toShow = isFlipped == true ? back : front;

    // Draw what to show (might be back or front)
    g.drawImage(toShow.getImage(), x, y, null);
  }
}

/* vim: set ts=2 sw=2 et: */
