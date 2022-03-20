package adrian.neuralnet.neurons;

import adrian.neuralnet.connections.ConToAction;
import adrian.neuralnet.connections.ConToInternal;

import java.util.ArrayList;

public class InternalNero {
    public final byte neroNumber;
    public float sum;
    private float preCalculatedTanh;

    public ArrayList<ConToInternal> connectionsToInternalNerons = new ArrayList<>(); //Find the optimal size for these
    public ArrayList<ConToAction> connectionsToActionNerons = new ArrayList<>();

    public InternalNero(final byte neroNumber){
        this.neroNumber = neroNumber;
    }

    public void addToSum(final float addAmount){
        sum += addAmount;
        preCalculatedTanh = (float)Math.tanh(sum); //Re-calculate the value of the tanh with the new sum
    }

    //////////////// Methods to update the neuralNeural network ////////////////
    public void prepare(){ //This would be called from outside (Before output)
        for (final ConToInternal conToInternal : connectionsToInternalNerons){ //Call all connections to internal neurons
            conToInternal.send(preCalculatedTanh);
        }

    }

    public void output(){ //This would be called from outside (After prepare)
        for(final ConToAction connectionToActionNeron : connectionsToActionNerons){ //Call all connections to action neurons
            connectionToActionNeron.send(preCalculatedTanh);
        }

    }
    ////////////////////////////////////////////////////////////////////////////

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
