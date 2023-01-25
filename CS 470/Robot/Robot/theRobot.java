import java.awt.event.*;
import java.awt.Color;
import java.awt.Graphics;
import java.lang.*;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.io.*;
import java.net.*;


// This class draws the probability map and value iteration map that you create to the window
// You need only call updateProbs() and updateValues() from your theRobot class to update these maps
class mySmartMap extends JComponent implements KeyListener {
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public static final int STAY = 4;

    int currentKey;

    int winWidth, winHeight;
    double sqrWdth, sqrHght;
    Color gris = new Color(170,170,170);
    Color myWhite = new Color(220, 220, 220);
    World mundo;
    
    int gameStatus;

    double[][] probs;
    double[][] vals;
    
    public mySmartMap(int w, int h, World wld) {
        mundo = wld;
        probs = new double[mundo.width][mundo.height];
        vals = new double[mundo.width][mundo.height];
        winWidth = w;
        winHeight = h;
        
        sqrWdth = (double)w / mundo.width;
        sqrHght = (double)h / mundo.height;
        currentKey = -1;
        
        addKeyListener(this);
        
        gameStatus = 0;
    }
    
    public void addNotify() {
        super.addNotify();
        requestFocus();
    }
    
    public void setWin() {
        gameStatus = 1;
        repaint();
    }
    
    public void setLoss() {
        gameStatus = 2;
        repaint();
    }
    
    public void updateProbs(double[][] _probs) {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                probs[x][y] = _probs[x][y];
            }
        }
        
        repaint();
    }
    
    public void updateValues(double[][] _vals) {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                vals[x][y] = _vals[x][y];
            }
        }
        
        repaint();
    }

    public void paint(Graphics g) {
        paintProbs(g);
        //paintValues(g);
    }

    public void paintProbs(Graphics g) {
        double maxProbs = 0.0;
        int mx = 0, my = 0;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (probs[x][y] > maxProbs) {
                    maxProbs = probs[x][y];
                    mx = x;
                    my = y;
                }
                if (mundo.grid[x][y] == 1) {
                    g.setColor(Color.black);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 0) {
                    //g.setColor(myWhite);
                    
                    int col = (int)(255 * Math.sqrt(probs[x][y]));
                    if (col > 255)
                        col = 255;
                    g.setColor(new Color(255-col, 255-col, 255));
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 2) {
                    g.setColor(Color.red);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 3) {
                    g.setColor(Color.green);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
            
            }
            if (y != 0) {
                g.setColor(gris);
                g.drawLine(0, (int)(y * sqrHght), (int)winWidth, (int)(y * sqrHght));
            }
        }
        for (int x = 0; x < mundo.width; x++) {
                g.setColor(gris);
                g.drawLine((int)(x * sqrWdth), 0, (int)(x * sqrWdth), (int)winHeight);
        }
        
        //System.out.println("repaint maxProb: " + maxProbs + "; " + mx + ", " + my);
        
        g.setColor(Color.green);
        g.drawOval((int)(mx * sqrWdth)+1, (int)(my * sqrHght)+1, (int)(sqrWdth-1.4), (int)(sqrHght-1.4));
        
        if (gameStatus == 1) {
            g.setColor(Color.green);
            g.drawString("You Won!", 8, 25);
        }
        else if (gameStatus == 2) {
            g.setColor(Color.red);
            g.drawString("You're a Loser!", 8, 25);
        }
    }
    
    public void paintValues(Graphics g) {
        double maxVal = -99999, minVal = 99999;
        int mx = 0, my = 0;
        
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] != 0)
                    continue;
                
                if (vals[x][y] > maxVal)
                    maxVal = vals[x][y];
                if (vals[x][y] < minVal)
                    minVal = vals[x][y];
            }
        }
        if (minVal == maxVal) {
            maxVal = minVal+1;
        }

        int offset = winWidth+20;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 1) {
                    g.setColor(Color.black);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 0) {
                    //g.setColor(myWhite);
                    
                    //int col = (int)(255 * Math.sqrt((vals[x][y]-minVal)/(maxVal-minVal)));
                    int col = (int)(255 * (vals[x][y]-minVal)/(maxVal-minVal));
                    if (col > 255)
                        col = 255;
                    g.setColor(new Color(255-col, 255-col, 255));
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 2) {
                    g.setColor(Color.red);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 3) {
                    g.setColor(Color.green);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
            
            }
            if (y != 0) {
                g.setColor(gris);
                g.drawLine(offset, (int)(y * sqrHght), (int)winWidth+offset, (int)(y * sqrHght));
            }
        }
        for (int x = 0; x < mundo.width; x++) {
                g.setColor(gris);
                g.drawLine((int)(x * sqrWdth)+offset, 0, (int)(x * sqrWdth)+offset, (int)winHeight);
        }
    }

    
    public void keyPressed(KeyEvent e) {
        //System.out.println("keyPressed");
    }
    public void keyReleased(KeyEvent e) {
        //System.out.println("keyReleased");
    }
    public void keyTyped(KeyEvent e) {
        char key = e.getKeyChar();
        //System.out.println(key);
        
        switch (key) {
            case 'i':
                currentKey = NORTH;
                break;
            case ',':
                currentKey = SOUTH;
                break;
            case 'j':
                currentKey = WEST;
                break;
            case 'l':
                currentKey = EAST;
                break;
            case 'k':
                currentKey = STAY;
                break;
        }
    }
}


// This is the main class that you will add to in order to complete the lab
public class theRobot extends JFrame {
    // Mapping of actions to integers
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public static final int STAY = 4;

    Color bkgroundColor = new Color(230,230,230);
    
    static mySmartMap myMaps; // instance of the class that draw everything to the GUI
    String mundoName;
    
    World mundo; // mundo contains all the information about the world.  See World.java
    double moveProb, sensorAccuracy;  // stores probabilities that the robot moves in the intended direction
                                      // and the probability that a sonar reading is correct, respectively
    
    // variables to communicate with the Server via sockets
    public Socket s;
	public BufferedReader sin;
	public PrintWriter sout;
    
    // variables to store information entered through the command-line about the current scenario
    boolean isManual = false; // determines whether you (manual) or the AI (automatic) controls the robots movements
    boolean knownPosition = false;
    int startX = -1;
    int startY = -1;
    int decisionDelay = 250;
    
    // store your probability map (for position of the robot in this array)
    double[][] probs;
    
    // store your computed value of being in each state (x, y)
    double[][] Vs;
    
    public theRobot(String _manual, int _decisionDelay) {
        // initialize variables as specified from the command-line
        isManual = !_manual.equals("automatic");
        decisionDelay = _decisionDelay;
        
        // get a connection to the server and get initial information about the world
        initClient();
    
        // Read in the world
        mundo = new World(mundoName);
        
        // set up the GUI that displays the information you compute
        int width = 500;
        int height = 500;
        int bar = 20;
        setSize(width,height+bar);
        getContentPane().setBackground(bkgroundColor);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, width, height+bar);
        myMaps = new mySmartMap(width, height, mundo);
        getContentPane().add(myMaps);
        
        setVisible(true);
        setTitle("Probability and Value Maps");
        
        doStuff(); // Function to have the robot move about its world until it gets to its goal or falls in a stairwell
    }
    
    // this function establishes a connection with the server and learns
    //   1 -- which world it is in
    //   2 -- its transition model (specified by moveProb)
    //   3 -- its sensor model (specified by sensorAccuracy)
    //   4 -- whether it's initial position is known.  If known, its position is stored in (startX, startY)
    public void initClient() {
        int portNumber = 3333;
        String host = "localhost";
        
        try {
			s = new Socket(host, portNumber);
            sout = new PrintWriter(s.getOutputStream(), true);
			sin = new BufferedReader(new InputStreamReader(s.getInputStream()));
            
            mundoName = sin.readLine();
            moveProb = Double.parseDouble(sin.readLine());
            sensorAccuracy = Double.parseDouble(sin.readLine());
            System.out.println("Need to open the mundo: " + mundoName);
            System.out.println("moveProb: " + moveProb);
            System.out.println("sensorAccuracy: " + sensorAccuracy);
            
            // find out of the robots position is know
            String _known = sin.readLine();
            if (_known.equals("known")) {
                knownPosition = true;
                startX = Integer.parseInt(sin.readLine());
                startY = Integer.parseInt(sin.readLine());
                System.out.println("Robot's initial position is known: " + startX + ", " + startY);
            }
            else {
                System.out.println("Robot's initial position is unknown");
            }
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }

    // function that gets human-specified actions
    // 'i' specifies the movement up
    // ',' specifies the movement down
    // 'l' specifies the movement right
    // 'j' specifies the movement left
    // 'k' specifies the movement stay
    int getHumanAction() {
        System.out.println("Reading the action selected by the user");
        while (myMaps.currentKey < 0) {
            try {
                Thread.sleep(50);
            }
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        int a = myMaps.currentKey;
        myMaps.currentKey = -1;
        
        System.out.println("Action: " + a);
        
        return a;
    }
    
    // initializes the probabilities of where the AI is
    void initializeProbabilities() {
        probs = new double[mundo.width][mundo.height];
        // if the robot's initial position is known, reflect that in the probability map
        if (knownPosition) {
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if ((x == startX) && (y == startY))
                        probs[x][y] = 1.0;
                    else
                        probs[x][y] = 0.0;
                }
            }
        }
        // otherwise, set up a uniform prior over all the positions in the world that are open spaces
        else {
            int count = 0;
            
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 0)
                        count++;
                }
            }
            
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 0)
                        probs[x][y] = 1.0 / count;
                    else
                        probs[x][y] = 0;
                }
            }
        }
        
        myMaps.updateProbs(probs);
    }

    // initializes the values of the states using value iteration
    void valueIteration() {
        Vs = new double[mundo.width][mundo.height];

        float epsilon = 0.01f;
        float discountFactor = 0.95f;
        double notMoveProb = (1 - moveProb) / 4;

        // set initial vals
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 0) // empty
                    Vs[x][y] = -1.0;
                else if (mundo.grid[x][y] == 1) // wall
                    Vs[x][y] = 0.0;
                else if (mundo.grid[x][y] == 2) // stairwell
                    Vs[x][y] = -100.0;
                else if (mundo.grid[x][y] == 3) // goal
                    Vs[x][y] = 100.0;
            }
        }

        // iterate until convergence
        boolean changed = true;
        int iterations = 0;
        while (changed) {
            iterations += 1;
            changed = false;
            double[][] newVs = new double[mundo.width][mundo.height];
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    // only values in the empty spaces need to be updated
                    if (mundo.grid[x][y] == 0) {
                        // stay action
                        double moveValue = moveProb*Vs[x][y];
                        moveValue += (mundo.grid[x][y-1] == 1) ? notMoveProb*Vs[x][y] : notMoveProb*Vs[x][y-1];
                        moveValue += (mundo.grid[x][y+1] == 1) ? notMoveProb*Vs[x][y] : notMoveProb*Vs[x][y+1];
                        moveValue += (mundo.grid[x-1][y] == 1) ? notMoveProb*Vs[x][y] : notMoveProb*Vs[x-1][y];
                        moveValue += (mundo.grid[x+1][y] == 1) ? notMoveProb*Vs[x][y] : notMoveProb*Vs[x+1][y];
                        double bestMoveValue = moveValue;
                        // move up action
                        moveValue = notMoveProb*Vs[x][y];
                        moveValue += (mundo.grid[x][y-1] == 1) ? moveProb*Vs[x][y] : moveProb*Vs[x][y-1];
                        moveValue += (mundo.grid[x][y+1] == 1) ? notMoveProb*Vs[x][y] : notMoveProb*Vs[x][y+1];
                        moveValue += (mundo.grid[x-1][y] == 1) ? notMoveProb*Vs[x][y] : notMoveProb*Vs[x-1][y];
                        moveValue += (mundo.grid[x+1][y] == 1) ? notMoveProb*Vs[x][y] : notMoveProb*Vs[x+1][y];
                        bestMoveValue = Math.max(moveValue, bestMoveValue);
                        // move down action
                        moveValue = notMoveProb*Vs[x][y];
                        moveValue += (mundo.grid[x][y-1] == 1) ? notMoveProb*Vs[x][y] : notMoveProb*Vs[x][y-1];
                        moveValue += (mundo.grid[x][y+1] == 1) ? moveProb*Vs[x][y] : moveProb*Vs[x][y+1];
                        moveValue += (mundo.grid[x-1][y] == 1) ? notMoveProb*Vs[x][y] : notMoveProb*Vs[x-1][y];
                        moveValue += (mundo.grid[x+1][y] == 1) ? notMoveProb*Vs[x][y] : notMoveProb*Vs[x+1][y];
                        bestMoveValue = Math.max(moveValue, bestMoveValue);
                        // move left action
                        moveValue = notMoveProb*Vs[x][y];
                        moveValue += (mundo.grid[x][y-1] == 1) ? notMoveProb*Vs[x][y] : notMoveProb*Vs[x][y-1];
                        moveValue += (mundo.grid[x][y+1] == 1) ? notMoveProb*Vs[x][y] : notMoveProb*Vs[x][y+1];
                        moveValue += (mundo.grid[x-1][y] == 1) ? moveProb*Vs[x][y] : moveProb*Vs[x-1][y];
                        moveValue += (mundo.grid[x+1][y] == 1) ? notMoveProb*Vs[x][y] : notMoveProb*Vs[x+1][y];
                        bestMoveValue = Math.max(moveValue, bestMoveValue);
                        // move right action
                        moveValue = notMoveProb*Vs[x][y];
                        moveValue += (mundo.grid[x][y-1] == 1) ? notMoveProb*Vs[x][y] : notMoveProb*Vs[x][y-1];
                        moveValue += (mundo.grid[x][y+1] == 1) ? notMoveProb*Vs[x][y] : notMoveProb*Vs[x][y+1];
                        moveValue += (mundo.grid[x-1][y] == 1) ? notMoveProb*Vs[x][y] : notMoveProb*Vs[x-1][y];
                        moveValue += (mundo.grid[x+1][y] == 1) ? moveProb*Vs[x][y] : moveProb*Vs[x+1][y];

                        // -1 is the reward of an empty space at each iteration
                        newVs[x][y] = (discountFactor * Math.max(moveValue, bestMoveValue)) - 1;
                        if (Math.abs(newVs[x][y] - Vs[x][y]) > epsilon) changed = true;
                    }
                    else
                        // terminal states and walls don't need their values updated
                        newVs[x][y] = Vs[x][y];
                }
            }
            Vs = newVs;
        }

        System.out.println("Value iteration done in " + iterations + " iterations.");
        print2dArray(Vs);

        myMaps.updateValues(Vs);
    }

    void print2dArray(double[][] valuesToPrint) {
        for (int y = 0; y < mundo.height; y++) {
            System.out.print('[');
            for (int x = 0; x < mundo.width; x++) {
//                double num = Math.round(valuesToPrint[x][y] * 10d) / 10d;
                System.out.printf("%7.2f", valuesToPrint[x][y]);
                if (x != mundo.width-1) System.out.print(", ");
                else System.out.println(']');
            }
        }
    }
    
    // update the probabilities of where the AI thinks it is based on the action selected and the new sonar readings
    //       To do this, you should update the 2D-array "probs"
    // Note: sonars is a bit string with four characters, specifying the sonar reading in the direction of North, South, East, and West
    //       For example, the sonar string 1001, specifies that the sonars found a wall in the North and West directions, but not in the South and East directions
    // Grid Tip: 0: empty square; 1: wall; 2: stairwell; 3: goal
    // Action tip: 0: north, 1: south, 2: east, 3: west, 4: stay
    void updateProbabilities(int action, String sonars) {
        boolean DEBUG = false;
        if (DEBUG) {
            System.out.println("Initial");
            print2dArray(probs);
        }

        // transition/move step
        double[][] tProbs = new double[mundo.width][mundo.height];
        double notMoveProb = (1 - moveProb) / 4;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 0) {
                    // move prob up
                    double probMoveUp = (action == 0) ? probs[x][y]*moveProb : probs[x][y]*notMoveProb;
                    if (mundo.grid[x][y-1] != 1) tProbs[x][y-1] += probMoveUp;
                    else tProbs[x][y] += probMoveUp;
                    // move prob down
                    double probMoveDown = (action == 1) ? probs[x][y]*moveProb : probs[x][y]*notMoveProb;
                    if (mundo.grid[x][y+1] != 1) tProbs[x][y+1] += probMoveDown;
                    else tProbs[x][y] += probMoveDown;
                    // move prob right
                    double probMoveRight = (action == 2) ? probs[x][y]*moveProb : probs[x][y]*notMoveProb;
                    if (mundo.grid[x+1][y] != 1) tProbs[x+1][y] += probMoveRight;
                    else tProbs[x][y] += probMoveRight;
                    // move prob left
                    double probMoveLeft = (action == 3) ? probs[x][y]*moveProb : probs[x][y]*notMoveProb;
                    if (mundo.grid[x-1][y] != 1) tProbs[x-1][y] += probMoveLeft;
                    else tProbs[x][y] += probMoveLeft;
                    // stay
                    tProbs[x][y] += (action == 4) ? probs[x][y]*moveProb : probs[x][y]*notMoveProb;
                }
            }
        }

        if (DEBUG) {
            System.out.println("After T step");
            print2dArray(tProbs);
        }

        // sensor step
        double[][] sProbs = new double[mundo.width][mundo.height];
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] != 1) {
                    int numCorrect = 0;  // determine how close the sonar reading is what would be expected here
                    if ((mundo.grid[x][y-1] == 1) && (sonars.charAt(0) == '1')) numCorrect += 1;
                    else if ((mundo.grid[x][y-1] != 1) && (sonars.charAt(0) == '0')) numCorrect += 1;
                    if ((mundo.grid[x][y+1] == 1) && (sonars.charAt(1) == '1')) numCorrect += 1;
                    else if ((mundo.grid[x][y+1] != 1) && (sonars.charAt(1) == '0')) numCorrect += 1;
                    if ((mundo.grid[x+1][y] == 1) && (sonars.charAt(2) == '1')) numCorrect += 1;
                    else if ((mundo.grid[x+1][y] != 1) && (sonars.charAt(2) == '0')) numCorrect += 1;
                    if ((mundo.grid[x-1][y] == 1) && (sonars.charAt(3) == '1')) numCorrect += 1;
                    else if ((mundo.grid[x-1][y] != 1) && (sonars.charAt(3) == '0')) numCorrect += 1;

                    sProbs[x][y] = tProbs[x][y] * Math.pow(sensorAccuracy, numCorrect) * Math.pow(1.0-sensorAccuracy, 4-numCorrect);
                }
            }
        }

        if (DEBUG) {
            System.out.println("After s step");
            System.out.println(sonars);
            print2dArray(sProbs);
        }

        // normalize
        double nFactor = 0;
        if (!sonars.contains("winner") && !sonars.contains("loser")) {  // if the game isn't over, zero out probs in goals, walls, and stairs
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] != 0)
                        sProbs[x][y] = 0;
                }
            }
        }
        for (int y = 0; y < mundo.height; y++) {  // determine normalization factor
            for (int x = 0; x < mundo.width; x++) {
                nFactor += sProbs[x][y];
            }
        }
        for (int y = 0; y < mundo.height; y++) {  // normalize
            for (int x = 0; x < mundo.width; x++) {
                probs[x][y] = sProbs[x][y] / nFactor;
            }
        }

        if (DEBUG) {
            System.out.println("After normalize");
            print2dArray(probs);
        }

        myMaps.updateProbs(probs); // call this function after updating your probabilities so that the
                                   //  new probabilities will show up in the probability map on the GUI
    }
    
    // This is the function you'd need to write to make the robot move using your AI;
    // You do NOT need to write this function for this lab; it can remain as is
    int automaticAction() {
        // maximum expected utility approach
        // EV(a) = sum( bel(s_t) * sum( prob(s'|s_t,a) * V(s') ) )

        // set to true to use classic MEU
        // set to false to use my version
        // my version forces the bot to not select the STAY action
        boolean MEU = false;

        double notMoveProb = (1 - moveProb) / 4;
        double Q;  // represents the inner sum of the EV equation
        double expValue;
        double bestExpValue;
        int bestAction = STAY;

        if (MEU) {
            // expected value of stay action
            expValue = 0.0;
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 0) {
                        Q = moveProb * Vs[x][y];
                        Q += (mundo.grid[x][y-1] == 1) ? notMoveProb * Vs[x][y] : notMoveProb * Vs[x][y-1];
                        Q += (mundo.grid[x][y+1] == 1) ? notMoveProb * Vs[x][y] : notMoveProb * Vs[x][y+1];
                        Q += (mundo.grid[x-1][y] == 1) ? notMoveProb * Vs[x][y] : notMoveProb * Vs[x-1][y];
                        Q += (mundo.grid[x+1][y] == 1) ? notMoveProb * Vs[x][y] : notMoveProb * Vs[x+1][y];
                        expValue += probs[x][y] * Q;
                    }
                }
            }
            bestExpValue = expValue;
        }
        else {
            bestExpValue = -10000;
            bestAction = NORTH;
        }

        // expected value of up action
        expValue = 0.0;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 0) {
                    Q = notMoveProb * Vs[x][y];
                    Q += (mundo.grid[x][y-1] == 1) ? moveProb * Vs[x][y] : moveProb * Vs[x][y-1];
                    Q += (mundo.grid[x][y+1] == 1) ? notMoveProb * Vs[x][y] : notMoveProb * Vs[x][y+1];
                    Q += (mundo.grid[x-1][y] == 1) ? notMoveProb * Vs[x][y] : notMoveProb * Vs[x-1][y];
                    Q += (mundo.grid[x+1][y] == 1) ? notMoveProb * Vs[x][y] : notMoveProb * Vs[x+1][y];
                    expValue += probs[x][y]*Q;
                }
            }
        }
        if (expValue > bestExpValue) {
            bestExpValue = expValue;
            bestAction = NORTH;
        }

        // expected value of down action
        expValue = 0.0;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 0) {
                    Q = notMoveProb * Vs[x][y];
                    Q += (mundo.grid[x][y-1] == 1) ? notMoveProb * Vs[x][y] : notMoveProb * Vs[x][y-1];
                    Q += (mundo.grid[x][y+1] == 1) ? moveProb * Vs[x][y] : moveProb * Vs[x][y+1];
                    Q += (mundo.grid[x-1][y] == 1) ? notMoveProb * Vs[x][y] : notMoveProb * Vs[x-1][y];
                    Q += (mundo.grid[x+1][y] == 1) ? notMoveProb * Vs[x][y] : notMoveProb * Vs[x+1][y];
                    expValue += probs[x][y]*Q;
                }
            }
        }
        if (expValue > bestExpValue) {
            bestExpValue = expValue;
            bestAction = SOUTH;
        }

        // expected value of left action
        expValue = 0.0;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 0) {
                    Q = notMoveProb * Vs[x][y];
                    Q += (mundo.grid[x][y-1] == 1) ? notMoveProb * Vs[x][y] : notMoveProb * Vs[x][y-1];
                    Q += (mundo.grid[x][y+1] == 1) ? notMoveProb * Vs[x][y] : notMoveProb * Vs[x][y+1];
                    Q += (mundo.grid[x-1][y] == 1) ? moveProb * Vs[x][y] : moveProb * Vs[x-1][y];
                    Q += (mundo.grid[x+1][y] == 1) ? notMoveProb * Vs[x][y] : notMoveProb * Vs[x+1][y];
                    expValue += probs[x][y]*Q;
                }
            }
        }
        if (expValue > bestExpValue) {
            bestExpValue = expValue;
            bestAction = WEST;
        }

        // expected value of down action
        expValue = 0.0;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 0) {
                    Q = notMoveProb * Vs[x][y];
                    Q += (mundo.grid[x][y-1] == 1) ? notMoveProb * Vs[x][y] : notMoveProb * Vs[x][y-1];
                    Q += (mundo.grid[x][y+1] == 1) ? notMoveProb * Vs[x][y] : notMoveProb * Vs[x][y+1];
                    Q += (mundo.grid[x-1][y] == 1) ? notMoveProb * Vs[x][y] : notMoveProb * Vs[x-1][y];
                    Q += (mundo.grid[x+1][y] == 1) ? moveProb * Vs[x][y] : moveProb * Vs[x+1][y];
                    expValue += probs[x][y]*Q;
                }
            }
        }
        if (expValue > bestExpValue) {
            bestAction = EAST;
        }
        
        return bestAction;
    }
    
    void doStuff() {
        int action;
        
        valueIteration();  // function you will write in Part II of the lab
        initializeProbabilities();  // Initializes the location (probability) map
        
        while (true) {
            try {
                if (isManual)
                    action = getHumanAction();  // get the action selected by the user (from the keyboard)
                else
                    action = automaticAction(); // get the action selected by your AI
                                                // you'll need to write this function for part III
                
                sout.println(action); // send the action to the Server
                
                // get sonar readings after the robot moves
                String sonars = sin.readLine();
                //System.out.println("Sonars: " + sonars);
            
                updateProbabilities(action, sonars); // this function should update the probabilities of where the AI thinks it is
                
                if (sonars.length() > 4) {  // check to see if the robot has reached its goal or fallen down stairs
                    if (sonars.charAt(4) == 'w') {
                        System.out.println("I won!");
                        myMaps.setWin();
                        break;
                    }
                    else if (sonars.charAt(4) == 'l') {
                        System.out.println("I lost!");
                        myMaps.setLoss();
                        break;
                    }
                }
                // else {
                    // here, you'll want to update the position probabilities
                    // since you know that the result of the move as that the robot
                    // was not at the goal or in a stairwell
                // }
                Thread.sleep(decisionDelay);  // delay that is useful to see what is happening when the AI selects actions
                                              // decisionDelay is specified by the send command-line argument, which is given in milliseconds
            }
            catch (IOException e) {
                System.out.println(e);
            }
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // java theRobot [manual/automatic] [delay]
    public static void main(String[] args) {
        theRobot robot = new theRobot(args[0], Integer.parseInt(args[1]));  // starts up the robot
    }
}