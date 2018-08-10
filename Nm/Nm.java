import java.lang.Math;
import java.util.Random;
import java.util.Scanner;

public class Nm {
  public static void main(String[] args) {
    // Create two players. Can be two humans or two computers
    Human p1 = new Human("Simon");
    Computer p2 = new Computer("HAL 9000");

    // Create a board with a chosen number of matches
    int noMatches = parseInput(args);
    Board gameBoard = new Board(noMatches);

    gameBoard.play(p1, p2);
  }

  private static int parseInput(String[] args) {
    int noMatches = 0;

    // Check if the first argument actually was an integer
    try {
      noMatches = Integer.parseInt(args[0]);
    } catch (NumberFormatException e) {
      System.out.println("Not a valid number of matches as first argument");
      System.exit(0);
    } catch (java.lang.ArrayIndexOutOfBoundsException e) {
      System.out.println("You have to enter an integer as argument to this program");
      System.exit(0);
    }

    if (noMatches < 1) {
      System.out.println("You can not create a game without any matches!");
      System.exit(0);
    }

    return noMatches;
  }
}

class Board {
  private Boolean gameOver = false;
  private String lastMoveResult;
  private String winner;
  private int matches = 10;

  public Board(int n) {
    matches = n;
  }

  public String getLastMoveResult() {
    return lastMoveResult;
  }

  public int getMatches() {
    return matches;
  }

  // Every time you try to remove matches from the board, this method will check
  // if it's a legal move If it's a correct move, remove the matches from the
  // board object
  public Boolean removeMatches(int n) {
    if (n < 1) {
      lastMoveResult = "Invalid move, a player have to draw more than 0 matches!";

      return false;
    }

    if (n > getMatches()) {
      lastMoveResult = "Invalid move, a player is not allowed remove more matches than what's"
        + "in the game!";

      return false;
    }

    if (n > (getMatches() / 2)) {
      lastMoveResult = "Invalid move, a player can't remove more than half the matches!";

      return false;
    }

    matches -= n;

    // If only one match is left, the game is over!
    if (matches == 1) {
      gameOver = true;
    }

    lastMoveResult = "Valid move";

    return true;
  }

  public void play(Player p1, Player p2) {
    System.out.printf("This is a game between %s (%s) and %s (%s), let's go!%n",
        p1.getName(), p1.getType(), p2.getName(), p2.getType());

    System.out.printf("%s is starting this round%n%n", p1.getName());

    while (!gameOver) {
      p1.makeMove(this);
      if (wasWinningMove(p1)) {
        break;
      }

      p2.makeMove(this);
      if (wasWinningMove(p2)) {
        break;
      }
    }

    System.out.printf("Game ended, %s won!%n", winner);
  }

  private Boolean wasWinningMove(Player p) {
    if (gameOver) {
      winner = p.getName();
      return true;
    }

    return false;
  }
}

abstract class Player {
  private String name;
  private String type;

  protected abstract void makeMove(Board b);

  public Player(String playerName, String playerType) {
    name = playerName;
    type = playerType;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }
}

class Human extends Player {
  private Scanner scanner;

  public Human(String playerName) {
    super(playerName, "Human");
  }

  protected void makeMove(Board b) {
    System.out.printf("%s is making a move, there are %d matches left%n",
        getName(), b.getMatches());

    scanner = new Scanner(System.in);

    while (true) {
      int toRemove;

      try {
        System.out.print("Matches to remove: ");
        toRemove = Integer.parseInt(scanner.nextLine());
      } catch (Exception e) {
        System.out.println("Not a valid number! Try again");
        continue;
      }

      if (!b.removeMatches(toRemove)) {
        System.out.println(b.getLastMoveResult());
        continue;
      }

      System.out.printf("%s removed %d matches!%n", getName(), toRemove);
      break;
    }
  }
}

class Computer extends Player {
  public Computer(String playerName) {
    super(playerName, "Computer");
  }

  protected void makeMove(Board b) {
    System.out.printf("%s is making a move, there are %d matches left%n",
        getName(), b.getMatches());

    Random rand = new Random();

    int max = (int) Math.floor(b.getMatches() / 2);
    int toRemove = rand.nextInt((max - 1) + 1) + 1;

    b.removeMatches(toRemove);
    System.out.printf("%s removed %d matches!%n", getName(), toRemove);
  }
}

/* vim: set ts=2 sw=2 et: */
