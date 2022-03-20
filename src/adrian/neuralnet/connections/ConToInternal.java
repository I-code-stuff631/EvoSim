package adrian.neuralnet.connections;

import adrian.neuralnet.neurons.InternalNero;

import static adrian.Main.rand;

public class ConToInternal {
    public InternalNero neron;
    public float weight;

    public ConToInternal(final InternalNero childNeron, final float weight){
      this.weight = weight;
      this.neron = childNeron;
    }

    public void send(final float rawInput){
        neron.addToSum( weight*rawInput );
    }

}