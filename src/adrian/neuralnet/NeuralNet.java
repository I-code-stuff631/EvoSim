package adrian.neuralnet;

import adrian.Gene;
import adrian.neuralnet.connections.ConToAction;
import adrian.neuralnet.connections.ConToInternal;
import adrian.neuralnet.neurons.ActionNero;
import adrian.neuralnet.neurons.InternalNero;
import adrian.neuralnet.neurons.SensoryNero;

import java.util.*;
import java.util.stream.Collectors;

import static adrian.Main.*;

public class NeuralNet {
    HashMap<Byte, SensoryNero> sensoryNeros = new HashMap<>(numberOfSensoryNeurons);
    HashMap<Byte, InternalNero> internalNeros = new HashMap<>(numberOfInternalNeurons); //ArrayList<InternalNero> internalNeros = new ArrayList<>(numberOfInternalNeurons);
    HashMap<Byte, ActionNero> actionNeros = new HashMap<>(numberOfActionNeurons);

    public NeuralNet(final Gene[] genes){ //Constructs a new neural net based on the genes that are passed in
        //////////////////////////////////// Add the neurons to the network ////////////////////////////////////
        for (final Gene neron : genes){
            ///// Parent neurons /////
            if(neron.isSensory){
                sensoryNeros.put(neron.parentID, new SensoryNero());
            }else { //Is Internal
                internalNeros.put(neron.parentID, new InternalNero());
            }
            ///// Child neurons /////
            if(neron.isAction){
                actionNeros.put(neron.childID, new ActionNero());
            }else { //Is Internal
                internalNeros.put(neron.childID, new InternalNero());
            }
        }
        //////////////////////////////////// Add the connections between the neurons ////////////////////////////////////

        ////////////////////// For each neron that can have a connection //////////////////////

        sensoryNeros.forEach((aByte, sensoryNero) -> { //Sensory neurons
            Gene[] rawAsocConnections = Arrays.stream(genes) //Get all the connections associated with the current sensory neron
                    .filter(g -> g.isSensory)
                    .filter(g -> g.parentID == aByte)
                    .toArray(Gene[]::new);
            //////////////////////////////////////
            for (final Gene connec : rawAsocConnections) {
                //// Identifies the type of child node, then constructs a connection and adds it ////

                /// Identify the type of child node ///
                if (connec.isAction) { //The child node is a actionNeron
                    sensoryNero.addConnection(new ConToAction(actionNeros.get(connec.childID), connec.weight));
                } else { //The child node is a internalNeron
                    sensoryNero.addConnection(new ConToInternal(internalNeros.get(connec.childID), connec.weight));
                }
            }
        });









    }



//    void update(final short x, final short y, byte sinPeriod, byte cosPeriod/*Creature pos along the x and y axises*/) {
//        for (final ActionNero actionNero : ActionNeros) {
//            if (actionNero != null) {
//                actionNero.sum = 0; //Reset all the action neros
//            }
//        }
//
//        for (final SensoryNero sensoryNero : SensoryNeros) {
//            if (sensoryNero != null) {
//                switch (sensoryNero.neronNumber) {
//                    case 0: //Distance to either of the y-borders (small distance small output, large distance large output) (large distance defined as the middle, small distance defined by being as close to one of the sides as possible)
//                        //distance from the middle: Math.abs((creatures[0].length/2f)-y)
//                        sensoryNero.produce(1f - (Math.abs((creatures[0].length / 2f) - y) / (creatures[0].length / 2f)));
//                        break;
//                    case 1: //Absoulte y location (Farthest from the middle max value, closests to the middle min value)
//                        sensoryNero.produce(Math.abs((creatures[0].length / 2f) - y) / (creatures[0].length / 2f));
//                        break;
//                    case 2: //Distance to either of the x-borders (same behavior as case 0)
//                        sensoryNero.produce(1f - (Math.abs((creatures.length / 2f) - x) / (creatures.length / 2f)));
//                        break;
//                    case 3: //Absoulte x location (same behavior as case 1)
//                        sensoryNero.produce((Math.abs((creatures.length / 2f) - x) / (creatures.length / 2f)));
//                        break;
//                    case 4: //The creatures age (outputs highest at last cycle, lowest at first)
//                        sensoryNero.produce(numberOfStepsPassed / numberOfSteps);
//                        break;
//                    case 5: //Random input
//                        sensoryNero.produce((float) rand.nextDouble()/*By truncating the output to a float you can actually get values between 1 and 0*/);
//                        break;
//                    ////// Blockage me.adrian.neuralnet.connections.nerons //////
//                    case 6: //Blockage above
//                        sensoryNero.produce(((y - 1 < 0) || (creatures[x][y - 1] != null)) ? (1f) : (0f));
//                        break;
//                    case 7: //Blockage above and to the right
//                        sensoryNero.produce(!((y - 1 >= 0 && x + 1 < creatures.length) && (creatures[x + 1][y - 1] == null)) ? (1f) : (0f));
//                        break;
//                    case 8: //Blockage to the right
//                        sensoryNero.produce(!((x + 1 < creatures.length) && (creatures[x + 1][y] == null)) ? (1f) : (0f));
//                        break;
//                    case 9: //Blockage to the right and down
//                        sensoryNero.produce(!((x + 1 < creatures.length && y + 1 < creatures[0].length) && creatures[x + 1][y + 1] == null) ? (1f) : (0f));
//                        break;
//                    case 10: //Blockage below
//                        sensoryNero.produce(!((y + 1 < creatures[0].length) && creatures[x][y + 1] == null) ? (1f) : (0f));
//                        break;
//                    case 11: //Blockage below and to the left
//                        sensoryNero.produce(!((y + 1 < creatures[0].length && x - 1 >= 0) && creatures[x - 1][y + 1] == null) ? (1f) : (0f));
//                        break;
//                    case 12: //Blockage to the left
//                        sensoryNero.produce(!((x - 1 >= 0) && creatures[x - 1][y] == null) ? (1f) : (0f));
//                        break;
//                    case 13: //Blockage to the left and up
//                        sensoryNero.produce(!((x - 1 >= 0 && y - 1 >= 0) && creatures[x - 1][y - 1] == null) ? (1f) : (0f));
//                        break;
//                    case 14: //Blockage all around the creature (if the eight spaces around it are blocked then it outputs a 1, if only 4 then it outputs a .5)
//                        byte blockedSpaces = 0;
//                        if (((y - 1 < 0) || (creatures[x][y - 1] != null))) //Square above is blocked (6)
//                            blockedSpaces++;
//                        if (!((y - 1 >= 0 && x + 1 < creatures.length) && (creatures[x + 1][y - 1] == null))) //Square above and to the right is blocked (7)
//                            blockedSpaces++;
//                        if (!((x + 1 < creatures.length) && (creatures[x + 1][y] == null))) //Square to the right is blocked (8)
//                            blockedSpaces++;
//                        if (!((x + 1 < creatures.length && y + 1 < creatures[0].length) && creatures[x + 1][y + 1] == null)) //Square to the right and down is blocked (9)
//                            blockedSpaces++;
//                        if (!((y + 1 < creatures[0].length) && creatures[x][y + 1] == null)) //Square below is blocked (10)
//                            blockedSpaces++;
//                        if (!((y + 1 < creatures[0].length && x - 1 >= 0) && creatures[x - 1][y + 1] == null)) //Square below and to the left is blocked (11)
//                            blockedSpaces++;
//                        if (!((x - 1 >= 0) && creatures[x - 1][y] == null)) //Square to the left is blocked (12)
//                            blockedSpaces++;
//                        if (!((x - 1 >= 0 && y - 1 >= 0) && creatures[x - 1][y - 1] == null)) //Square to the left and up is blocked (13)
//                            blockedSpaces++;
//                        sensoryNero.produce(blockedSpaces / 8);
//                        break;
//                    /////////////////////////////
//                    /////// Oscillators ///////
//                    case 15: //Sine wave
//                        sensoryNero.produce((float) (.5 * Math.sin(numberOfStepsPassed * (Math.PI / sinPeriod)) + .5));
//                        break;
//                    case 16: //Cos wave
//                        sensoryNero.produce((float) (.5 * Math.sin(numberOfStepsPassed * ((Math.PI * 2) / cosPeriod)) + .5));
//                        break;
//                    ///////////////////////////
//                }
//            }
//        }
//
//        //////////////////////////////////////////// Internal neros ////////////////////////////////////////////
//        for (final InternalNero internalNero : InternalNeros) {
//            if (internalNero != null) {
//                if (internalNero.sum == Float.NaN) {
//                    println("Found NaN internal neron");
//                }
//                internalNero.prepare();
//            }
//        }
//        for (final InternalNero internalNero : InternalNeros) {
//            if (internalNero != null) {
//                internalNero.output();
//            }
//        }
//    }



} /////////////// End of object /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//    NeuralNet() {
//        //////////////// Make random connections between the nerons (the number of conns are equal to the number of genes) ////////////////
//        for (short x = 0; x < numberOfGenes; x++) {
//            Tuple<SensoryNero, InternalNero> randomParrentNodeOutputToUnwrap = getRandomParentNode();
//            if (randomParrentNodeOutputToUnwrap.x != null) {
//                final SensoryNero parrent = randomParrentNodeOutputToUnwrap.x;
//
//                randomParrentNodeOutputToUnwrap = null;
//
//                Tuple<InternalNero, ActionNero> randomChildNodeOutputToUnwrap = getRandomChildNode();
//                if (randomChildNodeOutputToUnwrap.x != null) {
//                    final InternalNero child = randomChildNodeOutputToUnwrap.x;
//
//                    randomChildNodeOutputToUnwrap = null;
//
//                    parrent.addConnection(new ConToInternal(child));
//                } else {
//                    final ActionNero child = randomChildNodeOutputToUnwrap.y;
//
//                    randomChildNodeOutputToUnwrap = null;
//
//                    parrent.addConnection(new ConToAction(child));
//                }
//            } else {
//                final InternalNero parrent = randomParrentNodeOutputToUnwrap.y;
//
//                randomParrentNodeOutputToUnwrap = null;
//
//                Tuple<InternalNero, ActionNero> randomChildNodeOutputToUnwrap = getRandomChildNode();
//                if (randomChildNodeOutputToUnwrap.x != null) {
//                    final InternalNero child = randomChildNodeOutputToUnwrap.x;
//
//                    randomChildNodeOutputToUnwrap = null;
//
//                    parrent.addConnection(new ConToInternal(child));
//                } else {
//                    final ActionNero child = randomChildNodeOutputToUnwrap.y;
//
//                    randomChildNodeOutputToUnwrap = null;
//
//                    parrent.addConnection(new ConToAction(child));
//                }
//            }
//        }
//        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        //println("Neural net built!");
//        /////////////////////////////////// Mark nerons that should not be computed for one reason or another as such ///////////////////////////////////
//        for (final InternalNero internalNero : InternalNeros) {
//            if (internalNero != null) { //Marks dead end internal nerons as should not be computed
//                if (internalNero.connectionsToInternalNerons.size() == 0 && internalNero.connectionsToActionNerons.size() == 0) {
//                    internalNero.sum = Float.NaN;
//                }
//            }
//        }
//        //////////////
//        //for(final SensoryNero sensoryNero : SensoryNeros){
//        //  if(sensoryNero != null){
//        //    for(final ConToInternal conToInternal : sensoryNero.connectionsToInternalNerons){
//        //      if(conToInternal.neron.sum == Float.NaN/*<< If the neron is a dead end*/){
//        //        continue;
//        //      }
//        //      //If you get here then the neron HAS to have a connection to something
//        //      exploreInternalNerons(conToInternal.neron);
//        //      safeNeros = null;
//        //      for(final InternalNero internalNero : InternalNeros){
//        //        if(internalNero != null && !fromNerons.contains(internalNero)){ //Invalidate all internal me.adrian.neuralnet.connections.nerons that you have not been to
//        //          internalNero.sum = Float.NaN;
//        //        }
//        //      }
//        //    }
//
//        //  }
//        //}
//        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
//
//
//    }

//    private Tuple<SensoryNero, InternalNero> getRandomParentNode() {
//        short parrentIndex = (short) rand.nextInt(InternalNeros.length + SensoryNeros.length);
//        if (InternalNeros.length > parrentIndex) {
//            if (InternalNeros[parrentIndex] == null) {
//                InternalNeros[parrentIndex] = new InternalNero();
//            }
//            return new Tuple<>(null, InternalNeros[parrentIndex]);
//        } else {
//            parrentIndex = (short) (parrentIndex - InternalNeros.length);
//            if (SensoryNeros[parrentIndex] == null) {
//                SensoryNeros[parrentIndex] = new SensoryNero((byte) parrentIndex);
//            }
//            return new Tuple<>(SensoryNeros[parrentIndex], null);
//        }
//    }
//
//    private Tuple<InternalNero, ActionNero> getRandomChildNode() {
//        short childIndex = (short) rand.nextInt(InternalNeros.length + ActionNeros.length);
//        if (InternalNeros.length > childIndex) {
//            if (InternalNeros[childIndex] == null) {
//                InternalNeros[childIndex] = new InternalNero();
//            }
//            return new Tuple<>(InternalNeros[childIndex], null);
//        } else {
//            childIndex = (short) (childIndex - InternalNeros.length);
//            if (ActionNeros[childIndex] == null) {
//                ActionNeros[childIndex] = new ActionNero((byte) childIndex);
//            }
//            return new Tuple<>(null, ActionNeros[childIndex]);
//        }
//    }

//ArrayList<InternalNero> safeNeros = new ArrayList<>(InternalNeros.length);
//ArrayList<InternalNero> fromNerons/*All nerons that have you have been to*/ = new ArrayList<>(InternalNeros.length);
//private void exploreInternalNerons(final InternalNero thisNeron){
//  fromNerons.add(thisNeron);
//  for(final ConToInternal conToInternal : thisNeron.connectionsToInternalNerons){
//    ///// Inspect all me.adrian.neuralnet.connections that are not outright dead ends or me.adrian.neuralnet.connections.nerons that you have been to /////
//    if(conToInternal.neron.sum != Float.NaN /*<< If the neron is not a dead end*/&& !fromNerons.contains(conToInternal.neron)){
//      exploreInternalNerons(conToInternal.neron);
//      if(!safeNeros.contains(conToInternal.neron)){
//        thisNeron.sum = Float.NaN;
//      }
//    }
//    ///////////////////////////////////////////////////////////////////////////////////////////////////
//  }
//  ////////////////////// Decide if thisNeron should be a safe neron //////////////////////
//  if(thisNeron.connectionsToActionNerons.size() != 0 /*Then this neron has a connection to an action neron and any neron connecting to it (or connecting to one connecting to it) is safe*/){
//    safeNeros.add(thisNeron);
//  }else{
//    ///////////// Check if the neron has a connection to any known safe internal me.adrian.neuralnet.connections.nerons and add it to safe me.adrian.neuralnet.connections.nerons if it does /////////////
//    for(final ConToInternal conToInternal : thisNeron.connectionsToInternalNerons){
//      if(conToInternal.neron.sum != Float.NaN/*<< If it is not a dead end*/ && safeNeros.contains(conToInternal.neron)){
//         safeNeros.add(thisNeron);
//         break;
//        }
//     }
//  }
//  ////////////////////////////////////////////////////////////////////////////////////////
//}