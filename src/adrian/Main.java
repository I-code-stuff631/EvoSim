package adrian;

import adrian.neuralnet.NeuralNet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main extends JPanel {
    /////// Options ///////
    final static short numberOfCreatures = 100;
    public static final short numberOfGenes/*numberOfConnections*/ = 30;
    public static final short numberOfInternalNeurons = 1;
    final static double mutationChance = 0.001;
    public static final short numberOfStepsPerCycle = 300;
    private static final short frameRate = 24;
    public static final short sizeOfGrid = 3;
    public static final short width = 600;
    public static final short height = 600;//-44;
    public static final byte defaultSinPeriod = 30;
    public static final byte defaultCosPeriod = 60;
    ///////////////////////

    ///// Semi-options /////
    public static final short numberOfSensoryNeurons = 19;
    //The number of sensory neurons can not be greater than 128
    public static final short numberOfActionNeurons = 11; //9 for moving, 2 oscillator period controllers
    //The number of action neurons can not be greater than 128
    ////////////////////////

    private final static short sizeRatio = (short) Math.pow(2, sizeOfGrid);
    public final static short /*widthDevSizeRatio*/numberOfSquaresAlongX = (short) (width / sizeRatio);
    public final static short /*heightDevSizeRatio*/numberOfSquaresAlongY = (short) (height / sizeRatio);
    private final static short totalNumberOfSquares = (short)(numberOfSquaresAlongX*numberOfSquaresAlongY);
    public final static Random rand = new Random(1110236400L); //ThreadLocalRandom rand = ThreadLocalRandom.current();
    public static short numberOfStepsPassed;
    private static short genNumber;

    public static Creature[][] creatures = new Creature[numberOfSquaresAlongX][numberOfSquaresAlongY];

    public static void main(String[] args) {
        if(numberOfCreatures >= totalNumberOfSquares){
            System.out.println("Total number of creatures is more than or equal to the total number of squares!");
            System.exit(0);
        }

        ///////////////////////////////////////////
        short creaturesToAdd = numberOfCreatures;
        while(creaturesToAdd > 0){ // Add random creatures to the board randomly
            for(short x=0; x<numberOfSquaresAlongX; x++){
                for(short y=0; y<numberOfSquaresAlongY; y++){
                    if( (creatures[x][y] == null) && (intInRange(1, totalNumberOfSquares) <= numberOfCreatures) ){
                        creatures[x][y] = new Creature(x, y); //<< Adds a creature with random genome
                        creaturesToAdd--;
                        if(creaturesToAdd <= 0){
                            System.out.println("Generation: 0");// + genNumber);
                            break;
                        }
                    }
                }
                if(creaturesToAdd <= 0){
                    break;
                }
            }
        }
        /////////////////////////////////////////

        Main window = new Main();
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(window::repaint, 0, Math.round(1000000f / frameRate), TimeUnit.MICROSECONDS);
    }

    public Main() {
        JFrame frame = new JFrame("Hello world");
        frame.add(this);
        frame.setLocationRelativeTo(null);
        setBackground(Color.GRAY);
        frame.setResizable(false);
        //frame.setSize(width, height);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                    paused = !paused;
                }else if(e.getKeyCode() == KeyEvent.VK_UP){
                    gensToSkip += 100;
                    System.out.println("Skipping...\n");
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_RIGHT){

                    if(lastGen != genNumber) {
                        gensToSkip++;
                        lastGen = genNumber;
                    }

                }
            }
        });

    }

    private int lastGen=-1;
    private int gensToSkip;
    private boolean paused;
    /////////
    private int mouseX=-1;
    private int mouseY;

    @Override
    public void paint(Graphics g) {
        super.paint(g); //Clears the last frames shit

        do {
            if (numberOfStepsPassed < numberOfStepsPerCycle) {

                for (short x = 0; x < numberOfSquaresAlongX; x++) {
                    for (short y = 0; y < numberOfSquaresAlongY; y++) {
                        if (creatures[x][y] != null && (!paused || gensToSkip != 0)) { //Update each creature
                            creatures[x][y].update();
                        }
                    }
                }

                if (gensToSkip == 0) {
                    for (short x = 0; x < numberOfSquaresAlongX; x++) {
                        for (short y = 0; y < numberOfSquaresAlongY; y++) {
                            if (creatures[x][y] != null) {
                                g.setColor(creatures[x][y].getColor()); //fill(creatures[x][y].c);
                                g.fillOval(sizeRatio * x, sizeRatio * y, sizeRatio, sizeRatio);
                                //fill(0);
                                if (mouseX != -1) {
                                    if (mouseX > (sizeRatio * x) && mouseX < (sizeRatio * x + sizeRatio) && mouseY > (sizeRatio * y) && mouseY < (sizeRatio * y + sizeRatio)) {
                                        System.out.println(Gene.separator);
                                        Arrays.stream(creatures[x][y].genes).forEach(System.out::println);
                                    }
                                }


                            }
                        }
                    }
                    mouseX = -1;
                }


                if (!paused || gensToSkip != 0) {
                    numberOfStepsPassed++;
                }
            } else { //New generation
                numberOfStepsPassed = 0;

                ///////////////////////// Apply the selection criteria /////////////////////////
                ArrayList<Gene[]> survivingCreaturesGenes = new ArrayList<>(numberOfCreatures);
                ArrayList<NeuralNet> survivingCreaturesNeuralNets = new ArrayList<>(numberOfCreatures);
                for (short x = 0; x < numberOfSquaresAlongX; x++) {
                    for (short y = 0; y < numberOfSquaresAlongY; y++) {
                        if (creatures[x][y] != null) { //y < (numberOfSquaresAlongY/64) || y > numberOfSquaresAlongY-(numberOfSquaresAlongY/64)
                            if (x < (numberOfSquaresAlongX/1.5) && x > numberOfSquaresAlongX-(numberOfSquaresAlongX/1.5) &&
                                    y < (numberOfSquaresAlongY/1.5) && y > numberOfSquaresAlongY-(numberOfSquaresAlongY/1.5)) { //genNumber <= 4 ? x > (numberOfSquaresAlong/2) : x < (numberOfSquaresAlongX/2) <<< Right section criteria to left
                                survivingCreaturesGenes.add(creatures[x][y].genes);
                                survivingCreaturesNeuralNets.add(creatures[x][y].neuralNet);
                            } // x < (numberOfSquaresAlongX/1.5) && x > numberOfSquaresAlongX-(numberOfSquaresAlongX/1.5) && y < (numberOfSquaresAlongY/1.5) && y > numberOfSquaresAlongY-(numberOfSquaresAlongY/1.5) <<< Square criteria
                            creatures[x][y] = null;
                        }
                    }
                }
                ////////////////////////////////////////////////////////////////////////////////
                if(gensToSkip == 0)
                    System.out.println("Survivors: " + survivingCreaturesGenes.size() + '\n');

                /////////////// Re-populate the world //////////////////
                //survivingCreaturesGenesAndNeuralNets << Already represents each creature once so add on to it until the
                //required number of creatures has been reached

                final short originalSize = (short)survivingCreaturesGenes.size();
                while (survivingCreaturesGenes.size() < numberOfCreatures) {
                    ////////// Copy gene array //////////
                    Gene[] geneArrayCopy = new Gene[numberOfGenes];
                    final Gene[] uncopiedGeneArray = survivingCreaturesGenes.get( rand.nextInt(originalSize) );

                    for (short x = 0; x < uncopiedGeneArray.length; x++) {
                        geneArrayCopy[x] = uncopiedGeneArray[x].clone(); //Copy each gene in the gene array
                        if (rand.nextDouble() <= mutationChance) { //Mutate the gene if necessary
                            geneArrayCopy[x].mutate();
                        }
                    }
                    /////////////////////////////////////
                    survivingCreaturesGenes.add(geneArrayCopy);
                    survivingCreaturesNeuralNets.add(new NeuralNet(geneArrayCopy)); //<< Makes a copy of the NeuralNet
                }
                ////////////////// Actually create the new creatures //////////////////

                ArrayList<Creature> newCreatures = new ArrayList<>(numberOfCreatures);
                for (short x = 0; x<originalSize; x++) { //For first half
                    boolean aGeneWasMutated=false;
                    for(final Gene gene : survivingCreaturesGenes.get(x)){
                        if(rand.nextDouble() <= mutationChance) {
                            gene.mutate();
                            aGeneWasMutated = true;
                        }
                    }
                    if(aGeneWasMutated){
                        newCreatures.add( new Creature(survivingCreaturesGenes.get(x), new NeuralNet(survivingCreaturesGenes.get(x)) ));
                    }else{
                        //survivingCreaturesNeuralNets.get(x).actionNeros.values().stream().map(a -> a.sum).forEach(System.out::println);
                        survivingCreaturesNeuralNets.get(x).actionNeros.values().forEach(actionNero -> actionNero.sum = 0);
                        survivingCreaturesNeuralNets.get(x).internalNeros.values().forEach(internalNero -> internalNero.sum = 0);
                        newCreatures.add( new Creature(survivingCreaturesGenes.get(x), survivingCreaturesNeuralNets.get(x)) );
                    }
                }

                for (short x = originalSize; x < numberOfCreatures; x++) {
                    newCreatures.add(new Creature(survivingCreaturesGenes.get(x), survivingCreaturesNeuralNets.get(x)));
                }
                ///////////////////////////////////////////////////////////////////////


                ///// Add the creatures to the board randomly /////
                while (newCreatures.size() > 0) {
                    for (short x = 0; x < numberOfSquaresAlongX; x++) {
                        for (short y = 0; y < numberOfSquaresAlongY; y++) {
                            if ((creatures[x][y] == null) && (intInRange(1, totalNumberOfSquares) <= numberOfCreatures)) {
                                creatures[x][y] = newCreatures.get(0);
                                newCreatures.get(0).tellPos(x, y);
                                newCreatures.remove(0);
                                if (newCreatures.size() <= 0) {
                                    break;
                                }
                            }

                        }
                        if (newCreatures.size() <= 0) {
                            break;
                        }

                    }
                }
                ///////////////////////////////////////////////////

                genNumber++;
                if(gensToSkip > 0)
                    gensToSkip--;

                if(gensToSkip == 0)
                    System.out.println("Generation: " + genNumber);
            }
        }while (gensToSkip > 0);







    }

    public static int intInRange(int min, int max){
        if(min >= max){
            throw new IllegalArgumentException("Min is greater or equal to max!");
        }
        return rand.nextInt((max-min)+1)+min;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }
}