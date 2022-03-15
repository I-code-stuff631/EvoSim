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
    final static short numberOfSteps = 300;
    private static final short frameRate = 24;
    public static final short sizeOfGrid = 3;
    public static final short width = 600;
    public static final short height = 600;//-44;
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

        short cc=0;
        for(short x=0; x<numberOfSquaresAlongX; x++){
            for(short y=0; y<numberOfSquaresAlongY; y++) {
                if(creatures[x][y] != null){
                    cc++;
                }
            }
        }
        System.out.println(cc);


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

        for(short x=0; x<numberOfSquaresAlongX; x++){
            for(short y=0; y<numberOfSquaresAlongY; y++){
                if(creatures[x][y] != null){ //Update each creature
                    creatures[x][y].update();
                }
            }
        }

        for(short x=0; x<numberOfSquaresAlongX; x++){
            for(short y=0; y<numberOfSquaresAlongY; y++){
                //g.setColor(Color.GREEN);
                //g.drawRect(x*sizeRatio, y*sizeRatio, sizeRatio, sizeRatio);
                if(creatures[x][y] != null){
                    g.setColor(creatures[x][y].c); //fill(creatures[x][y].c);
                    g.fillOval(sizeRatio*x, sizeRatio*y, sizeRatio, sizeRatio);
                    //fill(0);
                }
            }
        }
        //g.drawRect(width-16,0,sizeRatio,sizeRatio);

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