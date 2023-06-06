package org.example.interpreter;

import lombok.Getter;

import java.util.Hashtable;

public class BlockContext implements Scope{
    @Getter
    final Hashtable<String, Value> localVariables;

    BlockContext(){
       localVariables = new Hashtable<>();
    }

    void addVariable(String name, Value variable) {
        localVariables.put(name, variable);
    }

}
