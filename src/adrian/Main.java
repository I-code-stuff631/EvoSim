package adrian;

import adrian.neuralnet.NeuralNet;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main extends JPanel {
    /////// Options ///////
    final static short numberOfCreatures = 10;
    public static final short numberOfGenes/*numberOfConnections*/ = 4;
    public static final short numberOfSensoryNeurons = 17;
    public static final short numberOfInternalNeurons = 1;
    public static final short numberOfActionNeurons = 9/*< For moving*/+2/*< Oscillator period controllers*/;
    final static double mutationChance = 0.001;
    public static final short numberOfStepsPerCycle = 300;
    private static final short frameRate = 24;
    public static final short sizeOfGrid = 3;
    public static final short width = 600;
    public static final short height = 600;//-44;
    public static final byte defaultSinPeriod = 30;
    public static final byte defaultCosPeriod = 60;
    ///////////////////////

    private final static short sizeRatio = (short) Math.pow(2, sizeOfGrid);
    public final static short /*widthDevSizeRatio*/numberOfSquaresAlongX = (short) (width / sizeRatio);
    public final static short /*heightDevSizeRatio*/numberOfSquaresAlongY = (short) (height / sizeRatio);
    private final static short totalNumberOfSquares = (short)(numberOfSquaresAlongX*numberOfSquaresAlongY);
    public final static Random rand = new Random(1110236400L); //ThreadLocalRandom rand = ThreadLocalRandom.current();
    public static short numberOfStepsPassed;
    private static final short sizeRatioDev2 = (short) (sizeRatio/2);

    public static Creature[][] creatures = new Creature[numberOfSquaresAlongX][numberOfSquaresAlongY];

    public static void main(String[] args) {
        assert (numberOfSensoryNeurons <= 128) && (numberOfSensoryNeurons >= 1);
        assert (numberOfActionNeurons <= 128) && (numberOfActionNeurons >= 1);

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

        /*frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                //Do your thing in here
                System.out.println("Resized");
                sizeRatio = (short) Math.pow(2, me.adrian.Main.sizeOfGrid);
                widthDevSizeRatio = (short) (frame.getWidth() / sizeRatio);
                heightDevSizeRatio = (short) (frame.getHeight() / sizeRatio);
            }
        });*/

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g); //Clears the last frames shit

        if(numberOfStepsPassed >= numberOfStepsPerCycle) {

            for (short x = 0; x < numberOfSquaresAlongX; x++) {
                for (short y = 0; y < numberOfSquaresAlongY; y++) {
                    if (creatures[x][y] != null) { //Update each creature
                        creatures[x][y].update();
                    }
                }
            }

            for (short x = 0; x < numberOfSquaresAlongX; x++) {
                for (short y = 0; y < numberOfSquaresAlongY; y++) {
                    if (creatures[x][y] != null) {
                        g.setColor(creatures[x][y].c); //fill(creatures[x][y].c);
                        g.fillOval(sizeRatio * x, sizeRatio * y, sizeRatio, sizeRatio);
                        //fill(0);
                    }
                }
            }

            numberOfStepsPassed++;
        }else{ //New generation
            numberOfStepsPassed = 0;

            ///////////////////////// Apply the selection criteria /////////////////////////
            //ArrayList<NeuralNet> survivingCreaturesNeuralNets = new ArrayList<>((int) Math.ceil(numberOfCreatures / 2f));
            //HashMap<Gene[], NeuralNet> survivingCreaturesGenesAndNeuralNets = new HashMap<>( (int)Math.ceil(numberOfCreatures/2f) );
            ArrayList<Tuple<Gene[], NeuralNet>> survivingCreaturesGenesAndNeuralNets = new ArrayList<>(numberOfCreatures);
            for (short x = 0; x < numberOfSquaresAlongX; x++) {
                for (short y = 0; y < numberOfSquaresAlongY; y++) {
                    if (creatures[x][y] != null) {
                        if (x > (numberOfSquaresAlongX / 2) /*<< The creature is on the right half of the screen*/) {
                            //survivingCreaturesGenesAndNeuralNets.put(creatures[x][y].genes, creatures[x][y].neuralNet);
                            survivingCreaturesGenesAndNeuralNets.add(new Tuple<>(creatures[x][y].genes, creatures[x][y].neuralNet));
                        }
                        creatures[x][y] = null;
                    }
                }
            }

            /////////////// Re-populate the world //////////////////
            //survivingCreaturesGenesAndNeuralNets << Already represents each creature once so add on to it until the
            //required number of creatures has been reached

            //Note: This method adds mutated networks on (so they do not need to be mutated again by the creature)
            final int originalSize = survivingCreaturesGenesAndNeuralNets.size();
            while (survivingCreaturesGenesAndNeuralNets.size() < numberOfCreatures){
                final Tuple<Gene[], NeuralNet> randomElement = survivingCreaturesGenesAndNeuralNets.get( rand.nextInt(originalSize) );
                ////////// Copy gene array //////////
                Gene[] geneArrayCopy = new Gene[numberOfGenes];
                final Gene[] uncopiedGeneArray = randomElement.X;

                assert uncopiedGeneArray.length == numberOfGenes;

                for(short x=0; x<uncopiedGeneArray.length; x++){
                    geneArrayCopy[x] = uncopiedGeneArray[x].clone();
                    if(rand.nextDouble() <= mutationChance) {
                        geneArrayCopy[x].mutate();
                    }
                }
                /////////////////////////////////////
                survivingCreaturesGenesAndNeuralNets.add(new Tuple<>(geneArrayCopy, new NeuralNet(geneArrayCopy)/*<< Makes a copy of the NeuralNet*/));
            }

            ////////////////// Actually create the new creatures //////////////////
            ArrayList<Creature> newCreatures = new ArrayList<>(numberOfCreatures);
            for(short x=0; x<originalSize; x++){
                final Tuple<Gene[], NeuralNet> currentElement = survivingCreaturesGenesAndNeuralNets.get(x);
                newCreatures.add( new Creature(currentElement.X, currentElement.Y, true) );
            }

            assert  survivingCreaturesGenesAndNeuralNets.size() == numberOfCreatures;
            for (short x = (short)originalSize; x<numberOfCreatures; x++){
                final Tuple<Gene[], NeuralNet> currentElement = survivingCreaturesGenesAndNeuralNets.get(x);
                newCreatures.add( new Creature(currentElement.X, currentElement.Y, false));
            }
            ///////////////////////////////////////////////////////////////////////

            ///// Add the creatures to the board randomly /////


            }




            //ArrayList<Creature> newCreatures = new ArrayList<>(numberOfCreatures);






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