package com.projekatjavav2.classes.terminals;

import com.projekatjavav2.classes.terminals.Terminal;

public class CustomsTerminal extends Terminal {

    public enum name{
        k1,k2
    }
    public CustomsTerminal(String name){
        super(name);
    }
    public CustomsTerminal(name tmp){
        super(tmp);
    }
    public CustomsTerminal(){
        super();
    }
}
