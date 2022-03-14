package adrian.neuralnet.connections;

import adrian.neuralnet.neurons.InternalNero;

import static adrian.Main.rand;

public class ConToInternal {
    public InternalNero neron;
    float weight;

    ConToInternal(final InternalNero neron, final float weight){
      this.weight = weight;
      this.neron = neron;
    }

    public ConToInternal(final InternalNero neron){
        this.neron = neron;
        weight = (float)(Math.nextUp(rand.nextFloat())-.5)*8;

    }

    public void send(final float rawInput){
        neron.addToSum( weight*rawInput );
    }

}