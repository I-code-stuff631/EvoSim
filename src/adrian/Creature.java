package adrian;

import adrian.neuralnet.NeuralNet;
import adrian.neuralnet.connections.ConToAction;
import adrian.neuralnet.connections.ConToInternal;
import adrian.neuralnet.neurons.ActionNero;
import adrian.neuralnet.neurons.InternalNero;
import adrian.neuralnet.neurons.SensoryNero;

import java.awt.*;
import java.util.*;

import static adrian.Main.*;

class Creature {
    short x=Short.MIN_VALUE;
    short y;
    private Color c;
    final NeuralNet neuralNet;
    Gene[] genes = new Gene[numberOfGenes];
    ///Action neron controlled variables///
    //private byte sinPeriod=30;
    //private byte cosPeriod=30;
    ///////////////////////////////////////

    Creature(final short x, final short y){ //This is called when the creatures are FIRST being created (gen 0), it should initialize them with completely random values
        this.x = x;
        this.y = y;
        /////////
        for (short i=0; i<genes.length; i++){ //Fill the gene array with random genes
            genes[i] = new Gene();
        }

        setColor();

        neuralNet = new NeuralNet(genes); //Construct a new neural net based on the random genes
    }

    Creature(final Gene[] genes, final NeuralNet neuralNet){
        this.genes = genes;
        this.neuralNet = neuralNet;

        setColor();
    }

    public void tellPos(final short x, final short y){
        assert x >= 0 && y >= 0; //The pos is valid
        assert this.x == Short.MIN_VALUE; //The creature does not already know its pos (or this method has not already been called)
        ////////
        this.x = x;
        this.y = y;
    }



    short stepUpdateWasLastCalled=-1;
    void update(){
        if(stepUpdateWasLastCalled != numberOfStepsPassed){
            neuralNet.update(x, y);
            move(neuralNet.getMove(x, y));



            stepUpdateWasLastCalled = numberOfStepsPassed;
        }


    }

    private void move(final byte moveCode){
        if(moveCode == 0){ //A move code of zero means don't move
            return;
        }
        switch (moveCode) {
            case 1 -> //Move up
                    moveTo(x, y - 1);
            case 2 -> //Move up, right
                    moveTo(x + 1, y - 1);
            case 3 -> //Move right
                    moveTo(x + 1, y);
            case 4 -> //Move right, down
                    moveTo(x + 1, y + 1);
            case 5 -> //Move down
                    moveTo(x, y + 1);
            case 6 -> //Move down, left
                    moveTo(x - 1, y + 1);
            case 7 -> //Move left
                    moveTo(x - 1, y);
            case 8 -> //Move left, up
                    moveTo(x - 1, y - 1);
            default -> throw new IllegalArgumentException("Direction code invalid!");
        }
    }

    private void moveTo(final int x, final int y){ //Don't ever call this directly
        creatures[x][y] = this;
        creatures[this.x][this.y] = null;
        this.x = (short)x;
        this.y = (short)y;
    }

    private void setColor(){
        assert genes[0] != null; //Make sure the genes are not unset when this is called

        /// Set the color based on the genes ///
        double red=0;
        double firstScalingConstant/*Clamps the number between 0 and 127*/= (127d/numberOfSensoryNeurons);
        final double secScalingConstant/*Clamps the number between 0 and 127*/= (127d/numberOfInternalNeurons);
        for(Gene gene : genes){
            if(gene.isSensory){
                red += gene.parentID*firstScalingConstant + Byte.MAX_VALUE + 1;
            }else{
                red += gene.parentID*secScalingConstant;
            }
        }
        red /= genes.length; //Average red

        double blue=0;
        firstScalingConstant = (127d/numberOfActionNeurons);
        for(Gene gene : genes){
            if(gene.isAction){
                blue += gene.childID*firstScalingConstant + Byte.MAX_VALUE + 1;
            }else{
                blue += gene.childID*secScalingConstant;
            }
        }
        blue /= genes.length; //Average blue

        double green=0;
        firstScalingConstant = (127/8d);
        for(Gene gene : genes) {
            green += (gene.weight+4)*firstScalingConstant;
        }
        green /= genes.length; //Average green

        c = new Color((int)red, (int)green, (int)blue);
        ////////////////////////////////////////

    }

    public Color getColor(){
        return c;
    }




} /////////////// End of object ///////////////
  
  