package com.projekatjavav2.classes.terminals;

public class Terminal {
    private String name;



    private boolean isTurnedOn =true;

    public enum state{
        BUSY,FREE
    }



    public state terminalState;

    public Terminal(String name){

        this.name=name;
        terminalState=state.FREE;

    }
    public Terminal(){}

    public String getName() {
        return name;
    }
    public void setTerminalState(state st){

        terminalState = st;

    }
    public state getTerminalState() {
        return terminalState;
    }

    public void setName(String name) {
        this.name = name;
    }
    public boolean isTurnedOn() {
        return isTurnedOn;
    }

    public void setTurnedOn(boolean turnedOn) {
        isTurnedOn = turnedOn;
    }

}
