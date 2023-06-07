package org.example.interpreter;

import lombok.Getter;

import java.util.Hashtable;

public class BlockContext implements Scope{
    @Getter
    final Hashtable<String, ValueReference> localVariables;

    BlockContext(){
       localVariables = new Hashtable<>();
    }

    void addVariable(String name, ValueReference variable) {
        localVariables.put(name, variable);
    }

}
