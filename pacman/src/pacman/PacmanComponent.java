package pacman;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import java.util.Random;




//Subclass of JComponent that handles the drawing 
// and collisions of our little Pacman world
public class PacmanComponent extends JComponent {
	
	private int initialX; // initial X position to start from on each game
	private int initialY; // initial Y position to start from on each game
	
	private PacmanPoint pacman; // point representing current position of player
	private PacmanPoint bunny; 	// point representing current position of bunny
	private PacmanPoint cherry;	// point representing current position of cherry can be null!
	private double cherryProb;	// if no cherry is currently present,the probability that one will appear
	
	// the Images for the bunny, pacman, and cherry
	
	private Image bunnyImage; 
	private Image pacmanImage; 
	private Image cherryImage;
	private int score; // the score in the current game
	private int[ ][ ] world;  // a 2D [x][y] array representing the world 
							  // entries are EMPTY, WALL, or PELLET
	
	
	public static final int EMPTY = 0; 
	public static final int WALL = 1; 
	public static final int YELLOW_PELLET = 2;
	public static final int PINK_PELLET = 3;
	
	// point scores for eating various things
	public static final int YELLOW_PELLET_SCORE = 5; 
	public static final int PINK_PELLET_SCORE = 10; 
	public static final int CHERRY_SCORE = 400;
	
	Random rand = new Random();
	
	/*
	* Constructor for the PacmanComponent. Sets size to 500x500,
	* loads the images and the world, and resets the game to be ready to play */
	public PacmanComponent(String world) {
		setPreferredSize(new Dimension(500, 500)); 
		pacmanImage = readImage("pacman.png");   // next slide
		cherryImage = readImage("cherry.jpg"); 
		bunnyImage = readImage("bunny.jpg"); 
		loadFile(world);                         // after next slide
		reset( );				// Resets the game state to the beginning
		
	}
	
	
	/* Provided utility method to read in an Image object.
	* If the image cannot load, prints error output and returns null. * Uses Java standard ImageIO.read( ) method
	*/
	private Image readImage(String filename) { 
		Image image = null;
		try {
			image = ImageIO.read(new File(filename));
		} 
		catch (IOException e) {
			System.out.println("Failed to load image '" + filename + "'");
			e.printStackTrace( );
		}
		return(image); 
	}
	
	/* Loads the game out of a file */
	private void loadFile(String filename) { 
		try {
	
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String line = in.readLine( );
			String[ ] info = line.split(" ");
			// first line has size of world and init location and probability
			int numCols = Integer.parseInt(info[0]); 
			int numRows = Integer.parseInt(info[1]); 
			world = new int[numCols][numRows];
			initialX = Integer.parseInt(info[3]); 
			initialY = Integer.parseInt(info[3]);
			//using Double.parseDouble to read out the probabilty
			cherryProb = Double.parseDouble(info[4]);
			
			//Taking advantage of the fact that the file's numbers 
			//correspond to the program constants
			for (int y = 0; y < numRows; y++){
				line = in.readLine( );
				String[ ] data = line.split(" ");
				for (int x = 0; x < numCols; x++){ 
					world[x][y] = Integer.parseInt(data[x]);
				
				}
			
			}
				
		
		}
		catch (Exception e){
			e.printStackTrace( );
			System.exit(1); 
		}
	}
	
	// Resets the game state to the beginning
	public void reset( ) {
		// resets the pacman to the beginning, and picks a random // start for the cherry and bunny
		pacman = new PacmanPoint(initialX, initialY);
		cherry = randomPoint( );
		bunny = randomPoint( );
		score = 0;
		// All EMPTY squares must have PELLETs in them.
		int totalBlank =0;  // keep the track of all avl.spaces
		int pinkCount = 0;  // keep the track of pink pellets
		int yellowCount = 0;// keep the track of yellow pellets
		for (int x = 0; x < world.length; x++){
			for (int y = 0; y < world[x].length; y++) {
				if (world[x][y] == EMPTY) {
					totalBlank++;
					double pelletColor = rand.nextDouble();
					// generate yellow pellets (50% total avl. space)
					if(pelletColor < 0.5 && yellowCount < totalBlank/2 ) {
						world[x][y] = YELLOW_PELLET;
						yellowCount++;
					
					// generate pink pellets (50% total avl. space)
					}else if (pinkCount < totalBlank/2) {
						world[x][y] = PINK_PELLET;
						pinkCount++;
					}else {
						world[x][y] = YELLOW_PELLET;
						yellowCount++;
					}
					
				}
			}
			
		}
		System.out.println(totalBlank);
		System.out.println(pinkCount);
		System.out.println(yellowCount);
		repaint( );
	}
	
	/* Helper method to repeatedly pick (x, y) pairs * until we find a point that isn't a WALL.
	*/
	private PacmanPoint randomPoint( ){
		while (true){
			int x = (int)(Math.random( ) * world.length);  // row position 
			int y = (int)(Math.random( ) * world[x].length); // col position
			if (world[x][y] != WALL){
				return new PacmanPoint(x, y); 
			}
		 
		} 
		
	}
	
	public void paintComponent(Graphics g) {
		int squareWidth = getWidth( ) / world.length; //figure out the pixel
		int squareHeight = getHeight( ) / world[0].length;  // size of one square
		for (int x = 0; x < world.length; x++) {
			for (int y = 0; y < world[x].length; y++) {
			//draw each square appropriately
				if (world[x][y] == EMPTY) { 
					g.setColor(Color.BLACK);
					g.fillRect(squareWidth * x, squareHeight * y, squareWidth, squareHeight);
				}
				else if (world[x][y] == WALL) { 
					g.setColor(Color.GRAY);
					g.fillRect(squareWidth * x, squareHeight * y, squareWidth, squareHeight);
				}
				else if (world[x][y] == YELLOW_PELLET) {
					g.setColor(Color.BLACK);
					g.fillRect(squareWidth * x, squareHeight * y, squareWidth, squareHeight); 
					g.setColor(Color.YELLOW);
					g.fillOval(squareWidth * x + (3 * squareWidth / 8),
								squareHeight * y + (3 * squareHeight / 8), 
								squareWidth / 4, squareHeight / 4); 
				}
				else if (world[x][y] == PINK_PELLET) {
					g.setColor(Color.BLACK);
					g.fillRect(squareWidth * x, squareHeight * y, squareWidth, squareHeight); 
					g.setColor(Color.PINK);
					g.fillOval(squareWidth * x + (3 * squareWidth / 8),
								squareHeight * y + (3 * squareHeight / 8), 
								squareWidth / 4, squareHeight / 4); 
				}
				
				/* if */
			} /*for y*/
		} /* for x */
				
		//draw the images corresponding to the objects in the world.
		g.drawImage(pacmanImage, squareWidth * pacman.getX( ), squareHeight * pacman.getY( ),squareWidth, squareHeight, null);
		g.drawImage(bunnyImage, squareWidth * bunny.getX( ), squareHeight * bunny.getY( ),squareWidth, squareHeight, null);
		
		//only print the cherry if it is onscreen
		if (cherry != null) {
			g.drawImage(cherryImage, squareWidth * cherry.getX( ),squareHeight * cherry.getY( ), squareWidth, squareHeight, null);	
		}
			
		 
		//print the score
		g.setColor(Color.YELLOW);
		g.drawString("Score: " + score, 0, squareHeight - 10);
		
	}
	
	// Method to update the game state in response to a keypress
	public void keyPress(int code) {
		//update the position of the pacman, ensuring we don't run into a wall
	
		if (code == KeyEvent.VK_UP) {
			if (world[pacman.getX( )][pacman.getY( ) - 1] != WALL) {
				pacman.moveBy(0, -1);
			} 
		} 
		else if (code == KeyEvent.VK_DOWN) {
			if (world[pacman.getX( )][pacman.getY( ) + 1] != WALL) {
				pacman.moveBy(0, 1);
			} 
		} 
		else if (code == KeyEvent.VK_LEFT) {
			if (world[pacman.getX( ) - 1][pacman.getY( )] != WALL) {
				pacman.moveBy(-1, 0);
			} 
		} 
		else if (code == KeyEvent.VK_RIGHT) {
			if (world[pacman.getX( ) + 1][pacman.getY( )] != WALL) {
				pacman.moveBy(1, 0); 
			}
		}
		// now, deal with the eating of various objects like the 
		// PELLET or the cherry
		
		// for yellow pellet gets 5 points
		if (world[pacman.getX( )][pacman.getY( )] == YELLOW_PELLET) {
			world[pacman.getX( )][pacman.getY( )] = EMPTY;
			score += YELLOW_PELLET_SCORE; 
		}
		
		// for yellow pellet gets 5 points
		if (world[pacman.getX( )][pacman.getY( )] == PINK_PELLET) {
			world[pacman.getX( )][pacman.getY( )] = EMPTY;
			score += PINK_PELLET_SCORE; 
		}
		
		if ((cherry != null) && (pacman.equals(cherry))) {
		//if we eat the cherry, then make it dissapear for a little bit. 
			score += CHERRY_SCORE;
			cherry = null;
		}
		repaint( );
	}
	
	// Tick method that is repeatedly called by a timer 
	// to update the position of the bunny and cherry
	
	
	public void tick( ) {
		randomMove(bunny); 
		if (cherry != null) {
			randomMove(cherry); // only move the cherry if it's onscreen
		
			if (pacman.equals(cherry)) { // check for collisions again
				score += CHERRY_SCORE;
				cherry = null;
			} 	
		} 
		else {
		
		// Figure out if a cherry should be generated
			if (Math.random( ) < cherryProb) { 
				cherry = randomPoint( );
			}
		}
		// if this is true, then we lose and the game restarts.
		if (pacman.equals(bunny)) { 
			reset( );
		}
		repaint( ); 
		
	}
	
	public void randomMove(PacmanPoint pt) {
		int dx, dy;
		while (true) {
			double r = Math.random( ); 
			if (r < 0.25) {
				dx=0; dy=1;
			}
			else if (r < 0.5) {
				dx=0; dy=-1;
			} 
			else if (r < 0.75) { 
				dx=1; dy=0;
			} 
			else {
				dx=-1; dy=0;
			} 
			if (world[pt.getX( ) + dx][pt.getY( ) + dy] != WALL) { 
				pt.moveBy(dx, dy);
				return;
			} 
			// otherwise loop around and try again
		}// while (true)
	}// randomMove
}// public class PacmanComponent
