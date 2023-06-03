package org.example.interpreter;

import lombok.Getter;

import java.util.Hashtable;

public class BlockContext implements Scope{
    @Getter
    final Hashtable<String, Object> localVariables;

    BlockContext(){
       localVariables = new Hashtable<>();
    }

    void addVariable(String name, Object variable) {
        localVariables.put(name, variable);
    }

}
