package adrian;

import adrian.neuralnet.NeuralNet;
import adrian.neuralnet.connections.ConToAction;
import adrian.neuralnet.connections.ConToInternal;
import adrian.neuralnet.neurons.InternalNero;
import adrian.neuralnet.neurons.SensoryNero;

import java.awt.*;
import java.util.ArrayList;
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

    public Creature(final Gene[] genes, final NeuralNet neuralNet, final boolean needsMutation) {
        if (needsMutation) {
            ArrayList<Gene> genesToMutate = new ArrayList<>((int) Math.ceil(numberOfGenes * mutationChance));
            for (final Gene gene : genes) {
                if (rand.nextDouble() <= mutationChance) {
                    genesToMutate.add(gene);
                }
            }

            /////////////////////// Get the connections associated with the genes to be mutated /////////////////////////
            ArrayList<Tuple<ConToInternal, ConToAction>> connections = new ArrayList<>(genesToMutate.size());
            ArrayList<Tuple<SensoryNero, InternalNero>> parentsOfConnections = new ArrayList<>(genesToMutate.size());
            for (final Gene gene : genesToMutate) {
                //Get all connections referenced by the genes
                if (gene.isSensory) { //Parent is sensoryNeron
                    final SensoryNero parentOfConnection = neuralNet.sensoryNeros.get(gene.parentID); //This should never be null

                    if (gene.isAction) { //Child is actionNeron
                        for (final ConToAction conToAction : parentOfConnection.connectionsToActionNerons) {
                            if (conToAction.neron.actionID == gene.childID && conToAction.weight == gene.weight) {
                                //^^ If connection is one referenced by the gene ^^
                                final boolean containsCurrentConnection = connections.stream().map(t -> t.Y/*<< Get all conToActions*/).anyMatch(con -> con == conToAction);
                                if (!containsCurrentConnection) {
                                    connections.add(new Tuple<>(null, conToAction));
                                    parentsOfConnections.add(new Tuple<>(parentOfConnection, null));
                                    break;
                                }

                            }
                        }
                    } else { //Child is internalNeron
                        for (final ConToInternal conToInternal : parentOfConnection.connectionsToInternalNerons) {
                            if (conToInternal.neron.neroNumber == gene.childID && conToInternal.weight == gene.weight) {
                                //^^ If connection is one referenced by the gene ^^
                                final boolean containsCurrentConnection = connections.stream().map(t -> t.X/*<< Get all conToInternal*/).anyMatch(con -> con == conToInternal);
                                if (!containsCurrentConnection) {
                                    connections.add(new Tuple<>(conToInternal, null));
                                    parentsOfConnections.add(new Tuple<>(parentOfConnection, null));
                                    break;
                                }

                            }
                        }
                    }
                } else { //Parent is internalNeron
                    final InternalNero parentOfConnection = neuralNet.internalNeros.get(gene.parentID);

                    if (gene.isAction) { //Child is actionNeron
                        for (final ConToAction conToAction : parentOfConnection.connectionsToActionNerons) {
                            if (conToAction.neron.actionID == gene.childID && conToAction.weight == gene.weight) {
                                //^^ If connection is one referenced by the gene ^^
                                final boolean containsCurrentConnection = connections.stream().map(t -> t.Y/*<< Get all conToActions*/).anyMatch(con -> con == conToAction);
                                if (!containsCurrentConnection) {
                                    connections.add(new Tuple<>(null, conToAction));
                                    parentsOfConnections.add(new Tuple<>(null, parentOfConnection));
                                    break;
                                }

                            }
                        }
                    } else { //Child is internalNeron
                        for (final ConToInternal conToInternal : parentOfConnection.connectionsToInternalNerons) {
                            if (conToInternal.neron.neroNumber == gene.childID && conToInternal.weight == gene.weight) {
                                //^^ If connection is one referenced by the gene ^^
                                final boolean containsCurrentConnection = connections.stream().map(t -> t.X/*<< Get all conToInternal*/).anyMatch(con -> con == conToInternal);
                                if (!containsCurrentConnection) {
                                    connections.add(new Tuple<>(conToInternal, null));
                                    parentsOfConnections.add(new Tuple<>(null, parentOfConnection));
                                    break;
                                }

                            }
                        }
                    }
                }
            }
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////

            ////////// Mutate the genes ////////////
            ArrayList<Byte> mutationCodes/*<< Tells you what the mutate method changed*/ = new ArrayList<>(genesToMutate.size());
            genesToMutate.forEach(gene -> mutationCodes.add(gene.mutate()));
            ////////////////////////////////////////

            ////////// Modify the connections to match with the new genes ////////////
            for (short currentGeneIndex = 0; currentGeneIndex < genesToMutate.size(); currentGeneIndex++) {

                final Tuple<ConToInternal, ConToAction> connection = connections.get(currentGeneIndex);
                final Gene mutatedGene = genesToMutate.get(currentGeneIndex);
                final Tuple<SensoryNero, InternalNero> parentOfConnection = parentsOfConnections.get(currentGeneIndex);
                switch (mutationCodes.get(currentGeneIndex)/*<< What mutation was preformed?*/){
                    case 0: //The type of the parent of the connection was changed
                        if(mutatedGene.isSensory){ //New parent is sensoryNero
                            //// Find new parent creating it if it does not exist ////
                            SensoryNero newParent = neuralNet.sensoryNeros.get(mutatedGene.parentID);
                            if(newParent == null/*If new parent does not exist*/){
                                newParent = new SensoryNero(mutatedGene.parentID);
                                neuralNet.sensoryNeros.put(mutatedGene.parentID, newParent);
                            }
                            //////////////////////////////////////////////////////////

                            final InternalNero oldParent = parentOfConnection.Y; //Old parent was internalNeron since new parent is sensoryNeron

                            ///////// Get the associated connection and move it to its proper place /////////
                            if(connection.Y != null){ //The connection is a ConToAction
                                final ConToAction conToAction = connection.Y;

                                //// Move the connection ////
                                oldParent.connectionsToActionNerons.remove(conToAction);
                                newParent.connectionsToActionNerons.add(conToAction);
                                /////////////////////////////

                            }else{ //The connection is a ConToInternal
                                final ConToInternal conToInternal = connection.X;

                                //// Move the connection ////
                                oldParent.connectionsToInternalNerons.remove(conToInternal);
                                newParent.connectionsToInternalNerons.add(conToInternal);
                                /////////////////////////////
                            }
                            /////////////////////////////////////////////////////////////////////////////////

                        }else{ //New parent is internalNero
                            InternalNero newParent = neuralNet.internalNeros.get(mutatedGene.parentID);
                            if(newParent == null){
                                newParent = new InternalNero(mutatedGene.parentID);
                                neuralNet.internalNeros.put(mutatedGene.parentID, newParent);
                            }

                            final SensoryNero oldParent = parentOfConnection.X; //Old parent was sensoryNeron since new parent is internalNeron

                            ///////// Get the associated connection and move it to its proper place /////////
                            if(connection.Y != null){ //The connection is a ConToAction
                                final ConToAction conToAction = connection.Y;

                                //// Move the connection ////
                                oldParent.connectionsToActionNerons.remove(conToAction);
                                newParent.connectionsToActionNerons.add(conToAction);
                                /////////////////////////////

                            }else{ //The connection is a ConToInternal
                                final ConToInternal conToInternal = connection.X;

                                //// Move the connection ////
                                oldParent.connectionsToInternalNerons.remove(conToInternal);
                                newParent.connectionsToInternalNerons.add(conToInternal);
                                /////////////////////////////
                            }
                            /////////////////////////////////////////////////////////////////////////////////

                        }
                        break;
                    case 1: //Parent neurons ID changed (type remains the same)
                        if(mutatedGene.isSensory){ //The parent is a sensoryNeron
                            //// Find new parent creating it if it does not exist ////
                            SensoryNero newParent = neuralNet.sensoryNeros.get(mutatedGene.parentID);
                            if(newParent == null/*If new parent does not exist*/){
                                newParent = new SensoryNero(mutatedGene.parentID);
                                neuralNet.sensoryNeros.put(mutatedGene.parentID, newParent);
                            }
                            //////////////////////////////////////////////////////////

                            final SensoryNero oldParent = parentOfConnection.X;

                            ///////////////////////////////////////////////////////////

                            ///////// Get the associated connection and move it to its proper place /////////
                            if(connection.Y != null){ //The connection is a ConToAction
                                final ConToAction conToAction = connection.Y;

                                //// Move the connection ////
                                oldParent.connectionsToActionNerons.remove(conToAction);
                                newParent.connectionsToActionNerons.add(conToAction);
                                /////////////////////////////

                            }else{ //The connection is a ConToInternal
                                final ConToInternal conToInternal = connection.X;

                                //// Move the connection ////
                                oldParent.connectionsToInternalNerons.remove(conToInternal);
                                newParent.connectionsToInternalNerons.add(conToInternal);
                                /////////////////////////////
                            }
                            /////////////////////////////////////////////////////////////////////////////////

                        }else{ //The parent is a internalNero
                            //// Find new parent creating it if it does not exist ////
                            InternalNero newParent = neuralNet.internalNeros.get(mutatedGene.parentID);
                            if(newParent == null){
                                newParent = new InternalNero(mutatedGene.parentID);
                                neuralNet.internalNeros.put(mutatedGene.parentID, newParent);
                            }
                            //////////////////////////////////////////////////////////

                            final InternalNero oldParent = parentOfConnection.Y;

                            //////////////////////////////////////////////////////////

                            ///////// Get the associated connection and move it to its proper place /////////
                            if(connection.Y != null){ //The connection is a ConToAction
                                final ConToAction conToAction = connection.Y;

                                //// Move the connection ////
                                oldParent.connectionsToActionNerons.remove(conToAction);
                                newParent.connectionsToActionNerons.add(conToAction);
                                /////////////////////////////

                            }else{ //The connection is a ConToInternal
                                final ConToInternal conToInternal = connection.X;

                                //// Move the connection ////
                                oldParent.connectionsToInternalNerons.remove(conToInternal);
                                newParent.connectionsToInternalNerons.add(conToInternal);
                                /////////////////////////////
                            }
                            /////////////////////////////////////////////////////////////////////////////////

                        }
                        break;
                    case 2:




                }

            }






        }
        this.neuralNet = neuralNet;
        this.genes = genes;
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
  
  