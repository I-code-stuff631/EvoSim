package adrian;
import adrian.neuralnet.neurons.ActionNero;
import adrian.neuralnet.neurons.SensoryNero;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class NArray<T> { //Neron array (designed to be fast for storing neurons in the neural network)
    //ONLY FOR STORING ACTION AND SENSORY NERONS
    //private Object[] CArr; //Compressed array
    private Object[] A; //normal array

    public NArray(final short maxLength){
        A = new Object[maxLength];
    }

    void recompress(){ //Updates the compressed array, should be called after modifications are mode to the normal array
        CArr = Arrays.stream(A)
                .filter(Objects::nonNull)
                .toArray();
        System.out.println(CArr);
    }

    public T get(final byte index){ //Gets a value from the compressed array
        return (T)CArr[index];
    }





}

//    private static class ActionNeroArr {
//        private ActionNero[] CArr; //Compressed array
//        public ActionNero[] A; //Normal array
//
//        ActionNeroArr(final short maxLength){
//                A = new ActionNero[maxLength];
//        }
//
//        void recompress(){ //Updates the compressed array with the new values in the normal array
//            ArrayList<ActionNero> tmpArr = new ArrayList<>(A.length);
//            for(final ActionNero actionNero : A){
//                if(actionNero != null){
//                    tmpArr.add(actionNero);
//                }
//            }
//
//            CArr = new ActionNero[tmpArr.size()];
//            for(short x=0; x<tmpArr.size(); x++){
//                CArr[x] = tmpArr.get(x);
//            }
//        }
//
//        ActionNero get(byte index){ //Gets a value from the compressed array
//            return CArr[index];
//        }
//
//    }