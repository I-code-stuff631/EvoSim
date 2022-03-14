package adrian.neuralnet.neurons;

import static adrian.Main.rand;

public class ActionNero {
    byte actionID; //Holds the type of action neron
    float sum;

    ActionNero(final byte neroNumber){
        this.actionID = neroNumber;

    }

    public void addToSum(final float addAmount){
        sum += addAmount;
    }

    float fireProbability(){
        return (float)Math.tanh(sum);
    }

    boolean fires(){
        final float fireProbability = (float)Math.tanh(sum);
        if(fireProbability <= 0){
            return false;
        }else{
            if(rand.nextFloat() < fireProbability){
                return true;
            }else{
                return false;
            }
        }


    }



}
