package org.example.interpreter;

import java.util.Hashtable;

public class BlockContext implements Scope{
    final Hashtable<String, Object> localVariables;

    BlockContext(){
       localVariables = new Hashtable<>();
    }

    void addVariable(String name, Object variable) {

    }

}
