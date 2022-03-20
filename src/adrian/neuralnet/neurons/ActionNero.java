package adrian.neuralnet.neurons;

import static adrian.Main.rand;

public class ActionNero {
    final public byte actionID; //Holds the type of action neron
    // ^^ This is final because if it was not it would be possible for the ID in the hashmap to not equal
    // the ID in the neron.
    public float sum;


    public ActionNero(final byte neroNumber){
        this.actionID = neroNumber;
    }

    public void addToSum(final float addAmount){
        sum += addAmount;
    }

    public float output(){ //Could and also should be seen as output
        return (float)Math.tanh(sum);
    }



    public boolean fires(){
        final float fireProbability = (float)Math.tanh(sum);
        if(fireProbability <= 0){
            return false;
        }else{
            if(rand.nextFloat() <= fireProbability){
                return true;
            }else{
                return false;
            }
        }

    }


}
