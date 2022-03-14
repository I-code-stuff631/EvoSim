package adrian.neuralnet.neurons;

import adrian.neuralnet.connections.ConToAction;
import adrian.neuralnet.connections.ConToInternal;

import java.util.ArrayList;

class SensoryNero {
    byte senseID; //Holds the type of sensory neron

    ArrayList<ConToInternal> connectionsToInternalNerons = new ArrayList<>();
    ArrayList<ConToAction> connectionsToActionNerons = new ArrayList<>();

    SensoryNero(final byte neronNumber){
        this.senseID = neronNumber;
    }

    void produce(final float rawOutput){ // This would be called from outside
        for(final ConToInternal connectionToInternalNeron : connectionsToInternalNerons){
            connectionToInternalNeron.send(rawOutput);
        }
        for(final ConToAction connectionToActionNeron : connectionsToActionNerons){
            connectionToActionNeron.send(rawOutput);
        }
    }

    ////// Adder methods //////
    void addConnection(final ConToInternal conToAdd){
        connectionsToInternalNerons.add(conToAdd);
    }
    void addConnection(final ConToAction conToAdd){
        connectionsToActionNerons.add(conToAdd);
    }
    ///////////////////////////









}
