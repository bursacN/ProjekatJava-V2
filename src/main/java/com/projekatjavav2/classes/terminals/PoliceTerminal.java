package com.projekatjavav2.classes.terminals;

import com.projekatjavav2.classes.terminals.Terminal;

public class PoliceTerminal extends Terminal {

    public enum name{
        t1,t2,t3
    }
    public PoliceTerminal(String name) {
        super(name);
    }
    public PoliceTerminal(name tmp) {
        super(tmp);
    }
}
