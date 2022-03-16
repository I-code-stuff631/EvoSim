package adrian.neuralnet.connections;

import adrian.neuralnet.neurons.ActionNero;

import static adrian.Main.rand;

public class ConToAction {
    public ActionNero neron;
    public float weight;

    public ConToAction(final ActionNero childNeron, final float weight){
      this.weight = weight;
      this.neron = childNeron;
    }

    /*public ConToAction(final ActionNero neron){
        this.neron = neron;
        weight = (float)(Math.nextUp(rand.nextFloat())-.5)*8; //Generate a random weight
    }*/

    public void send(final float /*rawOutput*/rawInput){
        neron.addToSum( weight*rawInput );
    }

}