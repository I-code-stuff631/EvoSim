package adrian.neuralnet.neurons;

import adrian.neuralnet.connections.ConToAction;
import adrian.neuralnet.connections.ConToInternal;

import java.util.ArrayList;

public class InternalNero {
    //byte neroNumber; Use the pos in the array as the number, the actual identifier should be unnecessary
    public float sum;

    public ArrayList<ConToInternal> connectionsToInternalNerons = new ArrayList<>();
    public ArrayList<ConToAction> connectionsToActionNerons = new ArrayList<>();

    public void addToSum(final float addAmount){
        sum += addAmount;
    }

    public void prepare(){ //This would be called from outside (Before output)
        //////////// Call all connections that loopback to this neron ////////////
        float tmpSum = sum;
        for(final ConToInternal connectionToInternalNeron : connectionsToInternalNerons){
            if(connectionToInternalNeron.neron == this){
                connectionToInternalNeron.send( (float)Math.tanh(tmpSum) );
            }
        }
        ///////////// Call all other internal neron connections /////////////
        for(final ConToInternal connectionToInternalNeron : connectionsToInternalNerons){
            if(connectionToInternalNeron.neron != this){
                connectionToInternalNeron.send( (float)Math.tanh(sum) );
            }
        }
        /////////////////////////////////////////////////////////////////////

    }

    public void output(){ //This would be called from outside (After prepare)
        for(final ConToAction connectionToActionNeron : connectionsToActionNerons){
            connectionToActionNeron.send( (float)Math.tanh(sum) );
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

    public boolean hasNoConnections(){ //For use via stream
        return (connectionsToInternalNerons.isEmpty() && connectionsToActionNerons.isEmpty());
    }


}
