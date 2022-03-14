package adrian;

import adrian.neuralnet.NeuralNet;

class Creature {
    short x;
    short y;
    //color c = color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    //me.adrian.Gene[] genome = new me.adrian.Gene[numberOfGenes];
    NeuralNet neuralNet;

//    me.adrian.Creature(final short x, final short y){
//        neuralNet = new me.adrian.NeuralNet(); //<< Makes a neural net with random me.adrian.neuralnet.connections
//        this.x = x;
//        this.y = y;
//    }

    //me.adrian.Creature(final me.adrian.NeuralNet inheritedNeuralNet){
    //  this.neuralNet = inheritedNeuralNet;
    //  //////////////////////////////////
    //  neuralNet.mutate();

    //}

    short lastUpdateCycle;
    private byte sinPeriod=30;
    private byte cosPeriod=30;
    void update(){
        if(lastUpdateCycle != numberOfStepsPassed){
            neuralNet.update(x, y, sinPeriod, cosPeriod);
            move();
            lastUpdateCycle = numberOfStepsPassed;
        }
        //for(final ActionNero actionNero : neuralNet.ActionNeros){
        //  if(actionNero.neroNumber > 8/*If it is a oscillator nero*/)
        //}


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
                if( tryMove((byte)(Main.rand.nextInt(8)+1)) ){
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
                if( (y-1 >= 0 && x+1 < creatures.length/*<< width*/) && creatures[x+1][y-1] == null){
                    moveTo(x+1, y-1);
                    return true;
                }
                break;
            case 3: //Move right
                if( (x+1 < creatures.length) && creatures[x+1][y] == null){
                    moveTo(x+1, y);
                    return true;
                }
                break;
            case 4: //Move right, down
                if( (x+1 < creatures.length && y+1 < creatures[0].length/*<< height*/) && creatures[x+1][y+1] == null){
                    moveTo(x+1, y+1);
                    return true;
                }
                break;
            case 5: //Move down
                if( (y+1 < creatures[0].length) && creatures[x][y+1] == null ){
                    moveTo(x, y+1);
                    return true;
                }
                break;
            case 6: //Move down, left
                if( (y+1 < creatures[0].length && x-1 >= 0) && creatures[x-1][y+1] == null){
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
  
  