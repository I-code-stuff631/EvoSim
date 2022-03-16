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
        isSensory = rand.nextBoolean();
        parentID = (byte)(isSensory ? rand.nextInt(numberOfSensoryNeurons) : rand.nextInt(numberOfInternalNeurons));
        ////
        isAction = rand.nextBoolean();
        childID = (byte)(isAction ? rand.nextInt(numberOfActionNeurons) : rand.nextInt(numberOfInternalNeurons));
        ////
        weight = rand.nextFloat();
    }

    @Override
    public Gene clone() {
        try {
            return (Gene) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
