package adrian.neuralnet.neurons;

import adrian.Gene;
import adrian.neuralnet.connections.ConToAction;
import adrian.neuralnet.connections.ConToInternal;

import java.util.ArrayList;

public class SensoryNero {
    final public byte senseID; //Holds the type of sensory neron
    // ^^ This is final because if it was not it would be possible for the ID in the hashmap to not equal
    // the ID in the neron.

    public ArrayList<ConToInternal> connectionsToInternalNerons = new ArrayList<>();
    public ArrayList<ConToAction> connectionsToActionNerons = new ArrayList<>();

    public SensoryNero(final byte neronNumber){
        this.senseID = neronNumber;
    }

    public void produce(final float rawOutput){ // This would be called from outside
        for(final ConToInternal connectionToInternalNeron : connectionsToInternalNerons){
            connectionToInternalNeron.send(rawOutput);
        }
        for(final ConToAction connectionToActionNeron : connectionsToActionNerons){
            connectionToActionNeron.send(rawOutput);
        }
    }

    ////// Adder methods //////
    public void addConnection(final ConToInternal conToAdd){
        connectionsToInternalNerons.add(conToAdd);
    }
    public void addConnection(final ConToAction conToAdd){
        connectionsToActionNerons.add(conToAdd);
    }
    ///////////////////////////

    public boolean hasNoConnections(){ //For use with streaming
        return (connectionsToInternalNerons.isEmpty() && connectionsToActionNerons.isEmpty());
    }








}
