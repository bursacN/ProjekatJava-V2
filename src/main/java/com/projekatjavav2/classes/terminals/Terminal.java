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
    public Terminal(PoliceTerminal.name tmp){
        if(tmp== PoliceTerminal.name.t1){
            this.name="t1";
        }
        else if(tmp== PoliceTerminal.name.t2){
            this.name="t2";
        }
        else if(tmp== PoliceTerminal.name.t3){
            this.name="t3";
        }
        terminalState=state.FREE;

    }
    public Terminal(CustomsTerminal.name tmp){
        if(tmp== CustomsTerminal.name.k1){
            this.name="k1";
        }
        else if(tmp== CustomsTerminal.name.k2){
            this.name="k2";
        }

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
