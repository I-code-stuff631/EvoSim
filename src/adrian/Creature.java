package adrian;

import adrian.neuralnet.NeuralNet;

import java.awt.*;

import static adrian.Main.*;

class Creature {
    short x;
    short y;
    Color c = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    //me.adrian.Gene[] genome = new me.adrian.Gene[numberOfGenes];
    NeuralNet neuralNet;
    Gene[] genes = new Gene[numberOfGenes];

    Creature(final short x, final short y){ //This is called when the creatures are first being created, it should initialize them with completely random values
        this.x = x;
        this.y = y;
        /////////
        for (short i=0; i<genes.length; i++){ //Fill the gene array with random genes
            genes[i] = new Gene();
        }
        neuralNet = new NeuralNet(genes); //Construct a new neural net based on the random genes
    }

    short lastUpdateCycle;
    private byte sinPeriod=30;
    private byte cosPeriod=30;
    void update(){
        if(lastUpdateCycle != numberOfStepsPassed){
            neuralNet.update(x, y, sinPeriod, cosPeriod);
            move();
            lastUpdateCycle = numberOfStepsPassed;
        }


    }

    private void move(){
        ///// Get possible directions the creature can move this cycle /////
        ArrayList<ActionNero> PossibleMoveActionNeros = new ArrayList<>(9);
        for(final ActionNero actionNero : neuralNet.ActionNeros){
            if(actionNero != null){
                if(actionNero.neroNumber >= 0 && actionNero.neroNumber <= 8){ //If it is a MOVE action neron
                    if(actionNero.fires()){
                        PossibleMoveActionNeros.add(actionNero);
                    }
                }
            }
        }
        assert PossibleMoveActionNeros.size() <= 9;
        ////////////////////////////////////////////////////////////////////
        //////// Sort all the action me.adrian.neuralnet.connections.nerons in possibleMoveNerons by probability ////////
        for(short m=0; m<PossibleMoveActionNeros.size(); m++){
            if( m+1 < PossibleMoveActionNeros.size() ){
                break;
            }
            if( m+1 < PossibleMoveActionNeros.size()/*<< For null pointer execpt*/ && (PossibleMoveActionNeros.get(m).fireProbability() < PossibleMoveActionNeros.get(m+1).fireProbability()) ){
                ActionNero biggerElement = PossibleMoveActionNeros.get(m+1);
                final float biggerElementValue = PossibleMoveActionNeros.get(m+1).fireProbability();
                for(short n=m; n>0; n--){
                    if(PossibleMoveActionNeros.get(m-1).fireProbability() > biggerElementValue){
                        PossibleMoveActionNeros.add(m, biggerElement);
                        biggerElement = null;
                        break;
                    }
                }
                if(biggerElement != null){
                    PossibleMoveActionNeros.add(0, biggerElement);
                }
            }
        }
        /////////////////////////////////////////////////////////////////////////////////
        ////////////// Try moving with each possible neron in order //////////////
        for(final ActionNero possibleMoveActionNero : PossibleMoveActionNeros){
            if(possibleMoveActionNero.neroNumber == 0){ //Then its a move random neron (so you have to generate a random move value)
                if( tryMove((byte)(rand.nextInt(8)+1)) ){
                    break;
                    //return;
                }
            }else{
                if( tryMove(possibleMoveActionNero.neroNumber) ){
                    break;
                    //return;
                }
            }
        }
        //////////////////////////////////////////////////////////////////////////



    }

    private boolean tryMove(final byte direction){
        switch(direction){
            case 1: //Move up
                if( (y-1 >= 0/*<< For out of bounds execpt*/) && creatures[x][y-1] == null){
                    moveTo(x, y-1);
                    return true;
                }
                break;
            case 2: //Move up, right
                if( (y-1 >= 0 && x+1 < numberOfSquaresAlongX/*<< width*/) && creatures[x+1][y-1] == null){
                    moveTo(x+1, y-1);
                    return true;
                }
                break;
            case 3: //Move right
                if( (x+1 < numberOfSquaresAlongX) && creatures[x+1][y] == null){
                    moveTo(x+1, y);
                    return true;
                }
                break;
            case 4: //Move right, down
                if( (x+1 < numberOfSquaresAlongX && y+1 < numberOfSquaresAlongY/*<< height*/) && creatures[x+1][y+1] == null){
                    moveTo(x+1, y+1);
                    return true;
                }
                break;
            case 5: //Move down
                if( (y+1 < numberOfSquaresAlongY) && creatures[x][y+1] == null ){
                    moveTo(x, y+1);
                    return true;
                }
                break;
            case 6: //Move down, left
                if( (y+1 < numberOfSquaresAlongY && x-1 >= 0) && creatures[x-1][y+1] == null){
                    moveTo(x-1, y+1);
                    return true;
                }
                break;
            case 7: //Move left
                if( (x-1 >= 0) && creatures[x-1][y] == null){
                    moveTo(x-1, y);
                    return true;
                }
                break;
            case 8: //Move left, up
                if( (x-1 >= 0 && y-1 >= 0) && creatures[x-1][y-1] == null){
                    moveTo(x-1, y-1);
                    return true;
                }
                break;
            default:
                println("What the actual fuck just happened? (switch statement invalid value)");
                System.exit(0);
        }
        return false;
    }

    private void moveTo(final int x, final int y){
        //assert (Math.abs(this.x-1) >= 0 && Math.abs(this.x-1) <= 1) && (Math.abs(this.y-1) >= 0 && Math.abs(this.y-1) <= 1);
        //println(x + " " + y + ": " + this.x + " " + this.y);
        creatures[x][y] = this;
        creatures[this.x][this.y] = null;
        this.x = (short)x;
        this.y = (short)y;
    }





} /////////////// End of object ///////////////
  
  