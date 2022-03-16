package adrian;

import adrian.neuralnet.NeuralNet;
import adrian.neuralnet.connections.ConToAction;
import adrian.neuralnet.connections.ConToInternal;
import adrian.neuralnet.neurons.ActionNero;
import adrian.neuralnet.neurons.InternalNero;
import adrian.neuralnet.neurons.SensoryNero;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

import static adrian.Main.*;

class Creature {
    short x;
    short y;
    Color c;
    NeuralNet neuralNet;
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

        neuralNet = new NeuralNet(genes); //Construct a new neural net based on the random genes
    }

    public Creature(final Gene[] genes, final NeuralNet neuralNet){
        ArrayList<Gene> genesToMutate = new ArrayList<>((int)Math.ceil(numberOfGenes*mutationChance));
        for(final Gene gene : genes){
            if(rand.nextDouble() <= mutationChance){
                genesToMutate.add(gene);
            }
        }
        ////////// Get the connections associated with the genes to be mutated ////////////
        final int lengOfHashMaps = (int) Math.ceil(genesToMutate.size()/2f);
        HashMap<Gene, ConToInternal> genesAndConToInternal = new HashMap<>(lengOfHashMaps); //ArrayList<ConToInternal> conToInternals = new ArrayList<>(lengOfArrayLists);
        HashMap<Gene, /*Adding something like a tuple here to hold the parent of the connection would be useful*/ConToAction> genesAndConToAction = new HashMap<>(lengOfHashMaps); //ArrayList<ConToAction> conToActions = new ArrayList<>(lengOfArrayLists);
        for(final Gene gene : genesToMutate){
            //Get all connections referenced by the genes
            if(gene.isSensory){ //Parent is sensoryNeron
                final SensoryNero parentOfConnection = neuralNet.sensoryNeros.get(gene.parentID); //This should never be null

                if(gene.isAction){ //Child is actionNeron
                    for (final ConToAction conToAction : parentOfConnection.connectionsToActionNerons) {
                        if(conToAction.neron.actionID == gene.childID && conToAction.weight == gene.weight){
                            //^^ If connection is one referenced by the gene ^^
                            if(!genesAndConToAction.containsValue(conToAction)) {
                                genesAndConToAction.put(gene, conToAction);
                                break;
                            }
                        }
                    }
                }else { //Child is internalNeron
                    for (final ConToInternal conToInternal : parentOfConnection.connectionsToInternalNerons) {
                        if (conToInternal.neron.neroNumber == gene.childID && conToInternal.weight == gene.weight) {
                            //^^ If connection is one referenced by the gene ^^
                            if (!genesAndConToInternal.containsValue(conToInternal)) {
                                genesAndConToInternal.put(gene, conToInternal);
                                break;
                            }
                        }
                    }
                }
            }else{ //Parent is internalNeron
                final InternalNero parentOfConnection = neuralNet.internalNeros.get(gene.parentID);

                if(gene.isAction){ //Child is actionNeron
                    for (final ConToAction conToAction : parentOfConnection.connectionsToActionNerons) {
                        if(conToAction.neron.actionID == gene.childID && conToAction.weight == gene.weight){
                            //^^ If connection is one referenced by the gene ^^
                            if(!genesAndConToAction.containsValue(conToAction)) {
                                genesAndConToAction.put(gene, conToAction);
                                break;
                            }
                        }
                    }
                }else{ //Child is internalNeron
                    for (final ConToInternal conToInternal : parentOfConnection.connectionsToInternalNerons) {
                        if(conToInternal.neron.neroNumber == gene.childID && conToInternal.weight == gene.weight){
                            //^^ If connection is one referenced by the gene ^^
                            if(!genesAndConToInternal.containsValue(conToInternal)) {
                                genesAndConToInternal.put(gene, conToInternal);
                                break;
                            }
                        }
                    }
                }
            }
        }

        ////////// Mutate the genes ////////////
        ArrayList<Byte> mutationCodes/*<< Tells you what the mutate method changed*/= new ArrayList<>(genesToMutate.size());
        genesToMutate.forEach(gene -> mutationCodes.add(gene.mutate()));

        ////////// Modify the connections to match with the new genes ////////////
        for(short x=0; x<genesToMutate.size(); x++){
            //////// Get the connection associated with the current gene ////////
            if( genesAndConToInternal.containsKey(genesToMutate.get(x)) ) { //The connection is a ConToInternal
                final ConToInternal asocConToInternal = genesAndConToInternal.get( genesToMutate.get(x) );
                final Gene mutatedGene = genesToMutate.get(x);

                switch (mutationCodes.get(x)) { //Find out what needs to changed to make the connection match with the gene
                    case 0: //The type of neron the connection connected to was changed (The parent nodes type changed)
                        ///// Check if the parent neron that now is supposed to hold this connection exists /////
                        if(mutatedGene.isSensory){
                            if(!neuralNet.sensoryNeros.containsKey(mutatedGene.parentID)){
                                asocConToInternal.


                            }else { //If it does contain the neron already


                            }

                        }

                }

            }else { //The connection is a ConToAction
                final ConToAction asocConToAction = genesAndConToAction.get( genesToMutate.get(x) );

            }











            }



        }








        ///// Remove any neurons that now do not connect to anything /////


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





} /////////////// End of object ///////////////
  
  