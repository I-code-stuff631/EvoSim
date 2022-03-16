package adrian.neuralnet;

import adrian.Gene;
import adrian.neuralnet.connections.ConToAction;
import adrian.neuralnet.connections.ConToInternal;
import adrian.neuralnet.neurons.ActionNero;
import adrian.neuralnet.neurons.InternalNero;
import adrian.neuralnet.neurons.SensoryNero;

import java.util.*;

import static adrian.Main.*;

public class NeuralNet {
    public HashMap<Byte, SensoryNero> sensoryNeros = new HashMap<>(numberOfSensoryNeurons);
    public HashMap<Byte, InternalNero> internalNeros = new HashMap<>(numberOfInternalNeurons); //ArrayList<InternalNero> internalNeros = new ArrayList<>(numberOfInternalNeurons);
    public HashMap<Byte, ActionNero> actionNeros = new HashMap<>(numberOfActionNeurons);

    public NeuralNet(final Gene[] genes){ //Constructs a new neural net based on the genes that are passed in
        //////////////////////////////////// Add the neurons to the network ////////////////////////////////////
        for (final Gene neron : genes){
            ///// Parent neurons /////
            if(neron.isSensory){
                sensoryNeros.put(neron.parentID, new SensoryNero(neron.parentID));
            }else { //Is Internal
                internalNeros.put(neron.parentID, new InternalNero(neron.parentID));
            }
            ///// Child neurons /////
            if(neron.isAction){
                actionNeros.put(neron.childID, new ActionNero(neron.childID));
            }else { //Is Internal
                internalNeros.put(neron.childID, new InternalNero(neron.childID));
            }
        }
        //////////////////////////////////// Add the connections between the neurons ////////////////////////////////////

        ////////////////////// For each neron that can have a connection (ie. parent neurons) //////////////////////

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

        internalNeros.forEach((aByte, internalNero) -> { //Internal neurons
            Gene[] rawAsocConnections = Arrays.stream(genes) //Get all the connections associated with the current internal neron
                    .filter(g -> !g.isSensory)
                    .filter(g -> g.parentID == aByte)
                    .toArray(Gene[]::new);
            //////////////////////////////////////
            for (final Gene connec : rawAsocConnections) {
                //// Identifies the type of child node, then constructs a connection and adds it ////

                /// Identify the type of child node ///
                if (connec.isAction) { //The child node is a actionNeron
                    internalNero.addConnection(new ConToAction(actionNeros.get(connec.childID), connec.weight));
                } else { //The child node is a internalNeron
                    internalNero.addConnection(new ConToInternal(internalNeros.get(connec.childID), connec.weight));
                }
            }
        });

        ////////////////////// Optimize network (remove neurons that contribute nothing to save processing power) //////////////////////

        internalNeros.values().stream().filter(InternalNero::hasNoConnections).forEach(internalNero ->
                internalNero.sum = Float.NEGATIVE_INFINITY //Marks all internal neurons that have no connection to anything as useless
        );

        sensoryNeros.values()
                .stream()
                .map(s -> s.connectionsToActionNerons) //Gets all the connectionToInternalNeron arrays
                .forEach(conToActions -> { //For each array of connections
                    for(short x=0; x<conToActions.size(); x++){
                       if(conToActions.get(x).neron.sum == Float.NEGATIVE_INFINITY){
                           conToActions.remove(x);
                       }
                    }
                });

        sensoryNeros.forEach((aByte, sensoryNero) -> {
            if(sensoryNero.hasNoConnections()){ //If the sensory neron is useless
                sensoryNeros.remove(aByte); //Remove it
            }
        });

    }

    public void update(final short x, final short y) {

        ////////////////// Gets cos and sin periods //////////////////
        //Action ID 9 is sine period controller and ID 10 is cos period controller
        double sinPeriod = defaultSinPeriod;
        ActionNero sinPeriodController = actionNeros.get((byte)9);
        if(sinPeriodController != null){
            sinPeriod = ( (defaultSinPeriod-0.01) * sinPeriodController.fireProbability()); //Sin period can at min be 0.01
        }

        double cosPeriod = defaultCosPeriod;
        ActionNero cosPeriodController = actionNeros.get((byte)10);
        if(cosPeriodController != null){
            cosPeriod = ( (defaultCosPeriod-0.01) * cosPeriodController.fireProbability()); //Cos period can at min be 0.01
        }
        //////////////////////////////////////////////////////////////

        actionNeros.values().forEach(actionNero -> actionNero.sum = 0); //Reset all the action neros

        //////////////////////////////////////////////////////////////

        for(final SensoryNero sensoryNero : sensoryNeros.values()){
            float presentDistanceFromMiddleAlongX = Math.abs((numberOfSquaresAlongX/2f)-x)/(numberOfSquaresAlongX/2f);
            float presentDistanceFromMiddleAlongY = Math.abs((numberOfSquaresAlongY/2f)-y)/(numberOfSquaresAlongY/2f);
            switch (sensoryNero.senseID) {
                case 0: //Distance to either of the y-borders (small distance small output, large distance large output) (large distance defined as the middle, small distance defined by being as close to one of the sides as possible)
                    //distance from the middle: Math.abs((creatures[0].length/2f)-y)
                    sensoryNero.produce(1f - presentDistanceFromMiddleAlongY);
                    break;
                case 1: //Absolute y location (Farthest from the middle max value, closest to the middle min value)
                    sensoryNero.produce(presentDistanceFromMiddleAlongY);
                    break;
                case 2: //Distance to either of the x-borders (same behavior as case 0)
                    sensoryNero.produce(1f - presentDistanceFromMiddleAlongX);
                    break;
                case 3: //Absolute x location (same behavior as case 1)
                    sensoryNero.produce(presentDistanceFromMiddleAlongX);
                    break;
                case 4: //The creatures age (outputs highest at last cycle, lowest at first)
                    sensoryNero.produce((float) numberOfStepsPassed/ numberOfStepsPerCycle);
                    break;
                case 5: //Random input
                    sensoryNero.produce((float) rand.nextDouble()/*By truncating the output to a float you can actually get values between 1 and 0*/);
                    break;
                ////// Blockage nerons //////
                case 6: //Blockage above
                    sensoryNero.produce(((y - 1 < 0) || (creatures[x][y - 1] != null)) ? (1f) : (0f));
                    break;
                case 7: //Blockage above and to the right
                    sensoryNero.produce(!((y - 1 >= 0 && x + 1 < numberOfSquaresAlongX) && (creatures[x + 1][y - 1] == null)) ? (1f) : (0f));
                    break;
                case 8: //Blockage to the right
                    sensoryNero.produce(!((x + 1 < numberOfSquaresAlongX) && (creatures[x + 1][y] == null)) ? (1f) : (0f));
                    break;
                case 9: //Blockage to the right and down
                    sensoryNero.produce(!((x + 1 < numberOfSquaresAlongX && y + 1 < numberOfSquaresAlongY) && creatures[x + 1][y + 1] == null) ? (1f) : (0f));
                    break;
                case 10: //Blockage below
                    sensoryNero.produce(!((y + 1 < numberOfSquaresAlongY) && creatures[x][y + 1] == null) ? (1f) : (0f));
                    break;
                case 11: //Blockage below and to the left
                    sensoryNero.produce(!((y + 1 < numberOfSquaresAlongY && x - 1 >= 0) && creatures[x - 1][y + 1] == null) ? (1f) : (0f));
                    break;
                case 12: //Blockage to the left
                    sensoryNero.produce(!((x - 1 >= 0) && creatures[x - 1][y] == null) ? (1f) : (0f));
                    break;
                case 13: //Blockage to the left and up
                    sensoryNero.produce(!((x - 1 >= 0 && y - 1 >= 0) && creatures[x - 1][y - 1] == null) ? (1f) : (0f));
                    break;
                case 14: //Blockage all around the creature (if the eight spaces around it are blocked then it outputs a 1, if only 4 then it outputs a .5 etc)
                    byte blockedSpaces = 0;
                    if (((y - 1 < 0) || (creatures[x][y - 1] != null))) //Square above is blocked (6)
                        blockedSpaces++;
                    if (!((y - 1 >= 0 && x + 1 < numberOfSquaresAlongX) && (creatures[x + 1][y - 1] == null))) //Square above and to the right is blocked (7)
                        blockedSpaces++;
                    if (!((x + 1 < numberOfSquaresAlongX) && (creatures[x + 1][y] == null))) //Square to the right is blocked (8)
                        blockedSpaces++;
                    if (!((x + 1 < numberOfSquaresAlongX && y + 1 < numberOfSquaresAlongY) && creatures[x + 1][y + 1] == null)) //Square to the right and down is blocked (9)
                        blockedSpaces++;
                    if (!((y + 1 < numberOfSquaresAlongY) && creatures[x][y + 1] == null)) //Square below is blocked (10)
                        blockedSpaces++;
                    if (!((y + 1 < numberOfSquaresAlongY && x - 1 >= 0) && creatures[x - 1][y + 1] == null)) //Square below and to the left is blocked (11)
                        blockedSpaces++;
                    if (!((x - 1 >= 0) && creatures[x - 1][y] == null)) //Square to the left is blocked (12)
                        blockedSpaces++;
                    if (!((x - 1 >= 0 && y - 1 >= 0) && creatures[x - 1][y - 1] == null)) //Square to the left and up is blocked (13)
                        blockedSpaces++;
                    sensoryNero.produce(blockedSpaces/8f);
                    break;
                /////////////////////////////
                /////// Oscillators ///////
                case 15: //Sine wave
                    sensoryNero.produce((float) (.5 * Math.sin(numberOfStepsPassed * (Math.PI / sinPeriod)) + .5));
                    break;
                case 16: //Cos wave
                    sensoryNero.produce((float) (.5 * Math.sin(numberOfStepsPassed * ((Math.PI * 2) / cosPeriod)) + .5));
                    break;
                ///////////////////////////
            }
        }

        //////////////////////////////////////////// Internal neros ////////////////////////////////////////////

        //Call prepare and output on each internal neron
        internalNeros.values().forEach(InternalNero::prepare);
        internalNeros.values().forEach(InternalNero::output);

    }

    public byte getMove(final short x, final short y){
        ///// Get possible directions the creature can move this cycle /////
        ArrayList<ActionNero> possibleMoveActionNeros = new ArrayList<>(9);
        actionNeros.forEach((actionID, actionNero) -> {
            if(actionID >= 0 && actionID <= 8) { //It is a move neron
                if(actionNero.fires()) {
                    possibleMoveActionNeros.add(actionNero);
                }
            }
        });
        assert possibleMoveActionNeros.size() <= 9;

        //////// Sort all the action nerons in possibleMoveNerons by probability (greatest first) ////////
        possibleMoveActionNeros.sort(Comparator.comparing(ActionNero::fireProbability).reversed());
        //If you get errors check this is sorting right ^^

        ////////////// Try moving with each possible neron in order //////////////
        for(final ActionNero possibleMoveActionNero : possibleMoveActionNeros){
            if(possibleMoveActionNero.actionID == 0) { //Then its a move random neron (so you have to generate a random move direction)
                byte moveDir = (byte)(rand.nextInt(8)+1);
                if(canMove(moveDir, x, y)){
                    return moveDir;
                }
            }else{
                if( canMove(possibleMoveActionNero.actionID, x, y) ) {
                    return possibleMoveActionNero.actionID;
                }
            }
        }
        return 0; //Zero means that you can't move any direction
    }


    private boolean canMove(final byte direction, final short x, final short y){
        switch(direction){
            case 1: //Move up
                if( (y-1 >= 0/*<< For out of bounds execpt*/) && creatures[x][y-1] == null){
                    return true;
                }
                break;
            case 2: //Move up, right
                if( (y-1 >= 0 && x+1 < numberOfSquaresAlongX/*<< width*/) && creatures[x+1][y-1] == null){
                    return true;
                }
                break;
            case 3: //Move right
                if( (x+1 < numberOfSquaresAlongX) && creatures[x+1][y] == null){
                    return true;
                }
                break;
            case 4: //Move right, down
                if( (x+1 < numberOfSquaresAlongX && y+1 < numberOfSquaresAlongY/*<< height*/) && creatures[x+1][y+1] == null){
                    return true;
                }
                break;
            case 5: //Move down
                if( (y+1 < numberOfSquaresAlongY) && creatures[x][y+1] == null ){
                    return true;
                }
                break;
            case 6: //Move down, left
                if( (y+1 < numberOfSquaresAlongY && x-1 >= 0) && creatures[x-1][y+1] == null){
                    return true;
                }
                break;
            case 7: //Move left
                if( (x-1 >= 0) && creatures[x-1][y] == null){
                    return true;
                }
                break;
            case 8: //Move left, up
                if( (x-1 >= 0 && y-1 >= 0) && creatures[x-1][y-1] == null){
                    return true;
                }
                break;
            default:
                throw new IllegalArgumentException("Direction code invalid!");
        }
        return false;
    }





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