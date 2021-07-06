import java.io.IOException;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
 
// Testing the MIDP Game API's Sprite class: animation and collision detection.
public class SpriteTest extends MIDlet {
 
   // Allocate a GameCanvas, set it to the current display, and start the game thread
   public void startApp() {
      GameMain game = new GameMain(); // GameMain extends GameCanvas for the game UI
      Display.getDisplay(this).setCurrent(game);
      new Thread(game).start();       // GameMain implements Runnable to run the game thread
   }
 
   public void pauseApp() { }
 
   public void destroyApp(boolean unconditional) { }
 
   // The GameMain class is designed as an inner class, which extends GameCanvas for drawing
   // the game graphics, and implements Runnable to run the game logic in its own thread.
   class GameMain extends GameCanvas implements Runnable {
 
      // Avatar - Pacman with animation
      private Sprite pacman;
      private String pacmanImageFilename = "PacmanFrames.png";
      private int pacmanXCenter,  pacmanYCenter;  // (x,y) of the center of the Pacman
      private int pacmanSpeed = 7;                // speed of move, in pixels
      private int score = 0;
 
      // Avatar - Ghost with animation
      private Sprite ghost;
      private String ghostImageFilename = "GhostFrames.png";
      private String ghostImageFilename2;
      private String ghostImageFilename3;
      private String ghostImageFilename4;
      private int ghostXCenter,  ghostYCenter;
 
      private static final int FRAME_WIDTH = 25;
      private static final int FRAME_HEIGHT = 25;
      private static final int FRAME_RADIUS = FRAME_WIDTH / 2 + 1;      
       
      // Collision Detection
      private boolean hasCollided;  // flag indicating collision
 
      private static final int UPDATE_INTERVAL = 100; // milliseconds
      private static final int INFO_AREA_HEIGHT = 38;  // height of the info display area
 
      // Constructor
      public GameMain() {
         super(true);
      }
      
      public void ghostColorSwitch(int Color){
          switch (Color){
              case 2: ghostImageFilename = "GhostFramesGreen.png"; break;
              case 3: ghostImageFilename = "GhostFramesPink.png"; break;
              case 4: ghostImageFilename = "GhostFramesRed.png"; break;
              case 5: ghostImageFilename = "GhostFramesYellow.png"; break;
              default: ghostImageFilename = "GhostFrames.png"; break;
          }
          
          try {
            Image imgPacman = Image.createImage(pacmanImageFilename);
            pacman = new Sprite(imgPacman, FRAME_WIDTH, FRAME_HEIGHT);
            pacman.setRefPixelPosition(FRAME_RADIUS, FRAME_RADIUS);  // set rotation center
 
            Image imgGhost = Image.createImage(ghostImageFilename);
            ghost = new Sprite(imgGhost, FRAME_WIDTH, FRAME_HEIGHT);
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      
 
      // Run the game loop in its own thread
      public void run() {
         int canvasWidth = getWidth();
         int canvasHeight = getHeight();
 
         // Construct the sprites
         try {
            Image imgPacman = Image.createImage(pacmanImageFilename);
            pacman = new Sprite(imgPacman, FRAME_WIDTH, FRAME_HEIGHT);
            pacman.setRefPixelPosition(FRAME_RADIUS, FRAME_RADIUS);  // set rotation center
 
            Image imgGhost = Image.createImage(ghostImageFilename);
            ghost = new Sprite(imgGhost, FRAME_WIDTH, FRAME_HEIGHT);
         } catch (IOException e) {
            e.printStackTrace();
         }
 
         // Position pacman at the center
         pacmanXCenter = canvasWidth / 2;
         pacmanYCenter = canvasHeight / 2;
         // Position ghost at the corner
         ghostXCenter = canvasWidth / 2;
         ghostYCenter = canvasHeight / 4;
         // Pacman's bounds
         int pacmanXMin = FRAME_RADIUS;
         int pacmanXMax = canvasWidth - FRAME_RADIUS;
         int pacmanYMin = FRAME_RADIUS + INFO_AREA_HEIGHT;
         int pacmanYMax = canvasHeight - FRAME_RADIUS;
 
         // Retrieve the off-screen graphics buffer for graphics drawing
         Graphics g = getGraphics();
 
         // Game loop
         while (true) {
            // Check key state for user input
            int keyState = getKeyStates();
            if ((keyState & RIGHT_PRESSED) != 0) {
               pacmanXCenter += pacmanSpeed;
               if (pacmanXCenter > pacmanXMax) {
                  pacmanXCenter = pacmanXMax;
               }
               pacman.setTransform(Sprite.TRANS_NONE);
            } else if ((keyState & UP_PRESSED) != 0) {
               pacmanYCenter -= pacmanSpeed;
               if (pacmanYCenter < pacmanYMin) {
                  pacmanYCenter = pacmanYMin;
               }
               pacman.setTransform(Sprite.TRANS_ROT270); // clockwise
            } else if ((keyState & LEFT_PRESSED) != 0) {
               pacmanXCenter -= pacmanSpeed;
               if (pacmanXCenter < pacmanXMin) {
                  pacmanXCenter = pacmanXMin;
               }
               pacman.setTransform(Sprite.TRANS_MIRROR);
            } else if ((keyState & DOWN_PRESSED) != 0) {
               pacmanYCenter += pacmanSpeed;
               if (pacmanYCenter > pacmanYMax) {
                  pacmanYCenter = pacmanYMax;
               }
               pacman.setTransform(Sprite.TRANS_ROT90); // clockwise
            }
 
            // Clear screen by filling a rectangle over the entire screen
            g.setColor(0x007fcf);
            g.fillRect(0, 0, canvasWidth, canvasHeight);
 
            // Draw the sprites
            pacman.setPosition(pacmanXCenter - FRAME_RADIUS, pacmanYCenter - FRAME_RADIUS);
            pacman.paint(g);
            pacman.nextFrame();  // use next frame for the next refresh
 
            ghost.setPosition(ghostXCenter - FRAME_RADIUS, ghostYCenter - FRAME_RADIUS);
            ghost.paint(g);
            ghost.nextFrame();
 
            // Collision detection
            hasCollided = pacman.collidesWith(ghost, true);
 
            // Display info
            g.setColor(0x000000);
            g.fillRect(0, 0, canvasWidth, INFO_AREA_HEIGHT);
            g.setColor(0xffffff);                       
            g.drawString("RAFAEL VILARUEL", 0, 0, Graphics.TOP | Graphics.LEFT);
            g.drawString("Score: "+score, 0, 20, Graphics.TOP | Graphics.LEFT);
            if (hasCollided) {
                ghostXCenter = canvasWidth / (new java.util.Random().nextInt(9)+1);
                ghostYCenter = canvasHeight / (new java.util.Random().nextInt(6)+1);          
                score++;      
                ghostColorSwitch(new java.util.Random().nextInt(4)+2);
            }
                      
 
            // flush the off-screen buffer to the display
            flushGraphics();
 
            // Provide delay to achieve the targeted refresh rate,
            // also yield for other threads to perform their tasks.
            try {
               Thread.sleep(UPDATE_INTERVAL);
            } catch (InterruptedException e) {
            }
         }
      }
   }
}