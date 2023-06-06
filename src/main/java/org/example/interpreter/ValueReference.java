package org.example.interpreter;

import lombok.Getter;
import lombok.Setter;

public class ValueReference {
    private Value value;

    public Value getReference(){
        return value;
    }

    public Object getValue(){
        return value.getValue();
    }

    public void setReference(Value value){
        this.value = value;
    }
    public void setValue(Object value){
        this.value.setValue(value);
    }
}
