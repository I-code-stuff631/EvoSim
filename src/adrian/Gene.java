package adrian;

import static adrian.Main.*;

public class Gene implements Cloneable {
    public boolean isSensory;
    public byte parentID;
    ////////
    public boolean isAction;
    public byte childID;
    ////////
    public float weight;

    Gene(boolean isSensory, byte parentID, boolean isAction, byte childID, float weight) {
        this.isSensory = isSensory;
        this.parentID = parentID;
        ////
        this.isAction = isAction;
        this.childID = childID;
        ////
        this.weight = weight;
    }

    Gene(){ //Make a random gene
        randomizeParent();
        ////
        randomizeChild();
        ////
        weight = (float)(rand.nextFloat()-.5)*8;
    }

    @Override
    public Gene clone() {
        try {
            return (Gene) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    void mutate(){ //This must actually change something (So it can't be COMPLETELY random)
        if(rand.nextBoolean()){
            if(rand.nextBoolean()){ //Randomize parent
                final boolean oldIsSensoryValue = isSensory;
                final byte oldParentIDValue = parentID;
                do {
                    randomizeParent();
                }while (oldIsSensoryValue == isSensory && oldParentIDValue == parentID);
            }else{ //Randomize child
                final boolean oldIsActionValue = isAction;
                final byte oldChildIDValue = childID;
                do {
                    randomizeChild();
                }while (oldIsActionValue == isAction && oldChildIDValue == childID);
            }
        }else{ //Randomize weight
            final float oldWeight = weight;
            do{
                weight = (float)(rand.nextFloat()-.5)*8;
            }while(weight == oldWeight);
        }
    }

    private void randomizeParent(){
        if(intInRange(1, numberOfSensoryNeurons+numberOfInternalNeurons) <= numberOfSensoryNeurons){
            //Make the parent a sensoryNero
            isSensory = true;
            parentID = (byte) rand.nextInt(numberOfSensoryNeurons);
        }else{
            //Make the parent a internalNero
            isSensory = false;
            parentID = (byte) rand.nextInt(numberOfInternalNeurons);
        }
    }

    private void randomizeChild(){
        if(intInRange(1, numberOfActionNeurons+numberOfInternalNeurons) <= numberOfInternalNeurons){
            //Make the child a internalNero
            isAction = false;
            childID = (byte) rand.nextInt(numberOfInternalNeurons);
        }else{
            //Make the child actionNero
            isAction = true;
            childID = (byte) rand.nextInt(numberOfActionNeurons);
        }
    }

    public final static String separator = "------------------------";
    @Override
    public String toString() {
        String parentInfo = "Parent info: " + (isSensory ? "Sensory neron" : "Internal neron") + '('+parentID+')';
        String childInfo = "Child info: " + (isAction ? "Action neron" : "Internal neron") + '('+childID+')';
        return parentInfo +'\n'+ childInfo +'\n'+ ("Weight: " + weight) /*+'\n'+ super.toString()*/ +'\n'+ separator;
    }
}
