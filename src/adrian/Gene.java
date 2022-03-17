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
        byte mutationCode = (byte)rand.nextInt(5);

       if(numberOfInternalNeurons == 1) { //Patch for internalNeron's for number of given neron == 1 crash

           if (!isAction && !isSensory) {
               while (mutationCode == 1 || mutationCode == 3) {
                   mutationCode = (byte) rand.nextInt(5);
               }
           }else if(!isAction){
               while (mutationCode == 3) {
                   mutationCode = (byte) rand.nextInt(5);
               }
           }else if(!isSensory){
               while (mutationCode == 1) {
                   mutationCode = (byte) rand.nextInt(5);
               }
           }

       }

        switch(mutationCode){
            case 0:
                isSensory = !isSensory;
                if(isSensory ? (parentID >= numberOfSensoryNeurons) : (parentID >= numberOfInternalNeurons) ){ //If parentID is now out of bounds
                    parentID = (byte)(isSensory ? rand.nextInt(numberOfSensoryNeurons) : rand.nextInt(numberOfInternalNeurons)); //Correct it
                }
                break;
            case 1:
                while(true){
                    final byte randomParentNeron = (byte)(isSensory ? rand.nextInt(numberOfSensoryNeurons) : rand.nextInt(numberOfInternalNeurons));
                    if(randomParentNeron != parentID){
                        break;
                    }
                }
                break;
            case 2:
                isAction = !isAction;
                if(isAction ? (childID >= numberOfActionNeurons) : (childID >= numberOfInternalNeurons)){ //If childID is now out of bounds
                    childID = (byte)(isAction ? rand.nextInt(numberOfActionNeurons) : rand.nextInt(numberOfInternalNeurons)); //Correct it
                }
                break;
            case 3:
                while(true){
                    final byte randomChildNeron = (byte)(isAction ? rand.nextInt(numberOfActionNeurons) : rand.nextInt(numberOfInternalNeurons));
                    if(randomChildNeron != parentID){
                        break;
                    }
                }
                break;
            case 4:
                while(true) {
                    final float randomWeight = (float)(rand.nextFloat()-.5)*8;
                    if(randomWeight != weight){
                        break;
                    }
                }
                break;
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
