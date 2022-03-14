package adrian;

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main extends JPanel {
    /////// Options ///////
    final static short numberOfCreatures = 10;
    public static final short numberOfGenes/*numberOfConnections*/ = 4;
    public static final short numberOfSensoryNeurons = 11;
    public static final short numberOfInternalNeurons = 1;
    public static final short numberOfActionNeurons = 4;
    //final static float mutationChance = 0.001;
    //final static short numberOfSteps = 300;
    private static final short frameRate = 24;
    public static final short sizeOfGrid = 3;
    public static final short width = 600;
    public static final short height = 600;
    ///////////////////////

    final private JFrame frame;
    private final static short sizeRatio = (short) Math.pow(2, sizeOfGrid);
    private final static short /*widthDevSizeRatio*/numberOfSquaresAlongX = (short) (width / sizeRatio);
    private final static short /*heightDevSizeRatio*/numberOfSquaresAlongY = (short) (height / sizeRatio);
    public final static Random rand = new Random(1110236400L); //ThreadLocalRandom rand = ThreadLocalRandom.current();

    //Creature[][] creatures;

    public static void main(String[] args) {
        assert (numberOfSensoryNeurons <= 128) && (numberOfSensoryNeurons >= 1);
        assert (numberOfActionNeurons <= 128) && (numberOfActionNeurons >= 1);

        Main window = new Main();
        //Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(window::repaint, 0, Math.round(1000000f / frameRate), TimeUnit.MICROSECONDS);
    }

    public Main() {
        frame = new JFrame("Hello world");
        frame.add(this);
        frame.setLocationRelativeTo(null);
        setBackground(Color.GRAY);
        frame.setSize(width, height);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final short totalNumberOfSquares = (short)(numberOfSquaresAlongX*numberOfSquaresAlongY);
        if(numberOfCreatures >= totalNumberOfSquares){
            System.out.println("Total number of creatures is more than or equal to the total number of squares!");
            System.exit(0);
        }

       // creatures = new me.adrian.Creature[numberOfSquaresAlongX][numberOfSquaresAlongY];

        short creaturesToAdd = numberOfCreatures;
        while(creaturesToAdd > 0){ // Add random creatures to the board randomly
            for(short x=0; x<numberOfSquaresAlongX; x++){
                for(short y=0; y<numberOfSquaresAlongY; y++){
                    if( (creatures[x][y] == null) && (intInRange(1, totalNumberOfSquares) <= numberOfCreatures) ){
                        creatures[x][y] = new Creature(x, y); //<< Adds a creature with random genome
                        creaturesToAdd--;
                        if(creaturesToAdd <= 0){
                            println("Generation: 0");// + genNumber);
                            return;
                        }
                    }
                }
            }
        }


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
        super.paint(g);
        g.setColor(Color.BLACK);
        //for(short x=0; x<)
    }

    public int intInRange(int min, int max){
        if(min <= max){
            throw new IllegalArgumentException("Min is greater or equal to max!");
        }
        return rand.nextInt((max-min)+1)+min;
    }
}