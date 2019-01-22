import java.util.ArrayList;
import java.util.Random;
import tester.*;
import javalib.impworld.*;
import javalib.worldimages.*;
import java.awt.Color;

// Represents a single square of the game area
class Cell {
  // In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;
  String color;
  boolean flooded = false;
  // the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;

  Cell(int x, int y, String color, boolean flooded, Cell left, Cell top, Cell right, Cell bottom) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = flooded;
    this.left = left;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
  }

  // returns a rectangleImage of size 12 of the proper color
  WorldImage getImage() {
    return new RectangleImage(12, 12, "solid", convertStringToColor());
  }

  // converts the string color to a Color color
  public Color convertStringToColor() {
    if (this.color.equals("red")) {
      return Color.RED;
    } else if (this.color.equals("blue")) {
      return Color.BLUE;
    } else if (this.color.equals("green")) {
      return Color.GREEN;
    } else if (this.color.equals("orange")) {
      return Color.ORANGE;
    } else if (this.color.equals("gray")) {
      return Color.GRAY;
    } else {
      return Color.YELLOW;
    }
  }

  // checks if the cells match the clicked color
  public void floodColorChanger() {
    this.flooded = true;
    if (this.left != null) {

      if (this.color == this.left.color) {
        this.left.flooded = true;
      }
    }
    if (this.right != null) {
      if (this.color == this.right.color) {
        this.right.flooded = true;
      }
    }
    if (this.bottom != null) {
      if (this.color == this.bottom.color) {
        this.bottom.flooded = true;
      }
    }
    if (this.top != null) {
      if (this.color == this.top.color) {
        this.top.flooded = true;
      }
    }
  }
}

// Represents a FloodItWorld game
class FloodItWorld extends World {
  // All the cells of the game
  ArrayList<ArrayList<Cell>> board;
  // Defines an int constant
  static final int BOARD_SIZE = 22;
  private Random rand;
  public int count = 0;
  public WorldScene ws = getEmptyScene();

  FloodItWorld() {
    board = new ArrayList<ArrayList<Cell>>(BOARD_SIZE);
    this.rand = new Random();
    this.boardMaking();
  }

  // tester constructor
  FloodItWorld(Random rand, int count) {
    board = new ArrayList<ArrayList<Cell>>(BOARD_SIZE);
    this.rand = rand;
    this.boardMaking();
    this.count = count;

  }

  // makes the board of rectangle images from the board array list
  // Effect: Initializes the cells in the board arrays
  public void boardMaking() {
    // two for loops to initialize all of the cells in an arraylist without the
    // adjacent cells
    for (int i = 0; i < BOARD_SIZE; i++) {
      ArrayList<Cell> temp = new ArrayList<Cell>();
      for (int j = 0; j < BOARD_SIZE; j++) {

        temp.add(new Cell(i * 12, j * 12, getRandomColor(), false, null, null, null, null));

      }
      board.add(temp);
    }

    // initializes the adjacent cells of the cells in the gameboard
    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j < BOARD_SIZE; j++) {

        if (j == 0) {
          board.get(i).get(j).top = null;
        } else {
          board.get(i).get(j).top = board.get(i).get(j - 1);
        }

        if (j == BOARD_SIZE - 1) {
          board.get(i).get(j).bottom = null;
        } else {
          board.get(i).get(j).bottom = board.get(i).get(j + 1);
        }

        if (i == 0) {
          board.get(i).get(j).left = null;
        } else {
          board.get(i).get(j).left = board.get(i - 1).get(j);
        }

        if (i == BOARD_SIZE - 1) {
          board.get(i).get(j).right = null;
        } else {
          board.get(i).get(j).right = board.get(i + 1).get(j);
        }
      }
    }
  }

  // returns the scene of the arraylist gameboard
  public WorldScene makeScene() {
    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j < BOARD_SIZE; j++) {
        ws.placeImageXY(board.get(i).get(j).getImage(), board.get(i).get(j).x + 6,
            board.get(i).get(j).y + 6);
      }
    }
    return ws;
  }

  // when the user clicks "r" it will reset the game
  // Effect: generates a new board when "r" is hit and resets count to 0
  public void onKeyEvent(String ke) {
    if (ke.equals("r")) {
      this.board = new ArrayList<ArrayList<Cell>>(BOARD_SIZE);
      this.boardMaking();
      count = 0;
    }
  }

  // creates the end game scene
  // Effect: places text image indicating victory or defeat
  public WorldEnd worldEnds() {

    WorldImage text = new TextImage("You Win!", 50, FontStyle.BOLD_ITALIC, Color.BLACK);
    WorldImage text2 = new TextImage("You Lost!", 50, FontStyle.BOLD_ITALIC, Color.BLACK);

    WorldScene ws2 = this.lastScene("");
    if (this.endWorldHelper()) {
      ws2.placeImageXY(text, 125, 100);
      return new WorldEnd(true, ws2);
    } else if (this.count >= 1.5 * BOARD_SIZE) {
      ws2.placeImageXY(text2, 125, 100);
      return new WorldEnd(true, ws2);
    } else {
      return new WorldEnd(false, this.makeScene());
    }
  }

  // changes the color of all flooded cells and floods cells of that color
  // Effect:displays end game message or changes the cell colors
  public void flood() {
    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j < BOARD_SIZE; j++) {
        if (this.board.get(i).get(j).flooded) {
          this.board.get(i).get(j).color = this.board.get(0).get(0).color;
          this.board.get(i).get(j).floodColorChanger();
        }
      }
    }
  }

  // checks if the game has ended or if the board is flooding
  // Effect:displays end game message or changes the cell colors
  public void onTick() {
    this.flood();
  }

  // checks if the entire world is flooded
  public boolean endWorldHelper() {
    boolean checker = true;
    for (int i = 0; i < board.size(); i++) {
      for (int j = 0; j < board.size(); j++) {
        if (!(this.board.get(i).get(j).flooded)) {
          checker = false;
          break;
        }
      }
    }
    return checker;
  }

  // changes the first cell
  // Effect: changes the color of the top left most cell
  public void changeCellTop(String color) {
    this.board.get(0).get(0).flooded = true;
    this.board.get(0).get(0).color = color;
  }

  // when the user clicks on a square it will implement the flood method on the
  // game board
  // right now when the user clicks on the square it will print out the color of
  // the cell that
  // the user clicked.
  public void onMouseClicked(Posn p) {
    for (int i = 0; i < board.size(); i++) {
      for (int j = 0; j < board.size(); j++) {
        if (p.x > board.get(i).get(j).x && (p.x < board.get(i).get(j).x + 12)
            && p.y > board.get(i).get(j).y && (p.y < board.get(i).get(j).y + 12)) {
          if (board.get(i).get(j).color == board.get(0).get(0).color) {
            break;
          }
          count++;
          System.out.println(board.get(i).get(j).color + " " + count);
          this.changeCellTop(board.get(i).get(j).color);
        }
      }
    }
  }

  // gets a random color of the given four choices
  public String getRandomColor() {
    int num = this.rand.nextInt(6);
    if (num == 1) {
      return "green";
    } else if (num == 2) {
      return "blue";
    } else if (num == 3) {
      return "red";
    } else if (num == 4) {
      return "yellow";
    } else if (num == 5) {
      return "gray";
    } else {
      return "orange";
    }
  }
}

//examples class for Flood
class ExamplesFlood {
  Cell c1 = new Cell(0, 0, "red", false, null, null, null, null);
  Cell c2 = new Cell(0, 0, "blue", false, null, null, null, null);
  Cell c3 = new Cell(0, 0, "green", false, null, null, null, null);
  Cell c4 = new Cell(0, 0, "yellow", false, null, null, null, null);
  Random rand1 = new Random(22);
  Random rand2 = new Random(24);
  WorldScene empty = new WorldScene(22 * 22, 22 * 22);
  Posn p1 = new Posn(0, 0);
  FloodItWorld f3 = new FloodItWorld(rand1, 10);
  FloodItWorld f4 = new FloodItWorld();

  // test world start
  void testFlood(Tester t) {
    FloodItWorld f1 = new FloodItWorld();
    f1.bigBang(265, 265, 0.1);
  }

  // test string to color
  boolean testStringtoColor(Tester t) {
    return t.checkExpect(c1.convertStringToColor(), Color.red)
        && t.checkExpect(c2.convertStringToColor(), Color.blue)
        && t.checkExpect(c3.convertStringToColor(), Color.green)
        && t.checkExpect(c4.convertStringToColor(), Color.yellow);
  }

  // test flood Color Changer
  void testFloodColorChanger(Tester t) {
    f3.board.get(0).get(1).color = f3.board.get(0).get(0).color;
    f3.board.get(1).get(0).color = f3.board.get(0).get(0).color;
    f3.board.get(0).get(0).floodColorChanger();

    t.checkExpect(f3.board.get(0).get(0).flooded, f3.board.get(0).get(1).flooded);
    t.checkExpect(f3.board.get(0).get(0).flooded, f3.board.get(1).get(0).flooded);
  }

  // testFlood method
  void testFloodMethod(Tester t) {
    FloodItWorld f1 = new FloodItWorld(new Random(24), 0);
    f1.board.get(0).get(1).flooded = true;
    f1.board.get(1).get(0).flooded = true;
    f1.changeCellTop("blue");
    f1.flood();
    t.checkExpect(f1.board.get(0).get(0).bottom.color, "blue");
    t.checkExpect(f1.board.get(0).get(0).right.color, "blue");
  }

  // testOnKeyEvent
  void testOnKeyEvent(Tester t) {
    this.f3.onKeyEvent("r");
    t.checkExpect(this.f3.count, 0);
  }

  // test random color
  boolean testRandomColor(Tester t) {
    return t.checkOneOf(f4.getRandomColor(), f4.getRandomColor(), "green", "blue", "red", "gray",
        "yellow", "orange");
  }

  // test boardmaking
  boolean testBoardmaking(Tester t) {

    return t.checkExpect(new FloodItWorld(new Random(24), 0), new FloodItWorld(new Random(24), 0));
  }

  // test lastscene
  void testEndWorld(Tester t) {
    FloodItWorld f1 = new FloodItWorld(new Random(24), 0);
    f1.ws.placeImageXY((new TextImage("out of turns", Color.BLACK)), 100, 100);
    FloodItWorld f2 = new FloodItWorld(new Random(24), 0);
    f2.lastScene("out of turns");
    t.checkExpect(f1, f2);
  }

  // test end worldhelper
  boolean endworldhelper(Tester t) {
    return t.checkExpect(new FloodItWorld(new Random(24), 0).endWorldHelper(), false);
  }

  // test testChangecellTop
  void testChangeCellTop(Tester t) {
    FloodItWorld f1 = new FloodItWorld(new Random(24), 0);
    f1.changeCellTop("blue");
    FloodItWorld f2 = new FloodItWorld(new Random(24), 0);
    f2.board.get(0).get(0).flooded = true;
    f2.board.get(0).get(0).color = "blue";
    t.checkExpect(f1, f2);

    f1 = new FloodItWorld(new Random(24), 0);
    f1.changeCellTop("red");
    f2 = new FloodItWorld(new Random(24), 0);
    f2.board.get(0).get(0).flooded = true;
    f2.board.get(0).get(0).color = "red";
    t.checkExpect(f1, f2);

    f1 = new FloodItWorld(new Random(24), 0);
    f1.changeCellTop("yellow");
    f2 = new FloodItWorld(new Random(24), 0);
    f2.board.get(0).get(0).flooded = true;
    f2.board.get(0).get(0).color = "yellow";
    t.checkExpect(f1, f2);

    f1 = new FloodItWorld(new Random(24), 0);
    f1.changeCellTop("green");
    f2 = new FloodItWorld(new Random(24), 0);
    f2.board.get(0).get(0).flooded = true;
    f2.board.get(0).get(0).color = "green";
    t.checkExpect(f1, f2);

    f1 = new FloodItWorld(new Random(24), 0);
    f1.changeCellTop("gray");
    f2 = new FloodItWorld(new Random(24), 0);
    f2.board.get(0).get(0).flooded = true;
    f2.board.get(0).get(0).color = "gray";
    t.checkExpect(f1, f2);

    f1 = new FloodItWorld(new Random(24), 0);
    f1.changeCellTop("orange");
    f2 = new FloodItWorld(new Random(24), 0);
    f2.board.get(0).get(0).flooded = true;
    f2.board.get(0).get(0).color = "orange";
    t.checkExpect(f1, f2);
  }

}