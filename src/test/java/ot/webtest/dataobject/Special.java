package ot.webtest.dataobject;

import static ot.webtest.dataobject.Special.Type.RANDOM;
import static ot.webtest.dataobject.Special.Type.SPECIFIC;

public class Special<T> implements Comparable<Special<T>> {
    public enum Type {
        NULL, RANDOM, SPECIFIC
    }

    private final Type type;
    private final T specificValue;
    private Boolean isEditable;

    public Special() {
        this.type = Type.NULL;
        this.specificValue = null;
    }

    public Special(Type type) {
        // some hesitations about this constructor
        // in case of Type.SPECIFIC and specificValue = null
        // also throw new IllegalStateException("Type could not be specific"); is not good idea
        // because it's runtime error but (logically) this restriction should be applied on compilation time
        // still this exception is better than NullPointerException later
        if (type == SPECIFIC) {
            throw new IllegalStateException("Value is not specific");
        }
        this.type = type;
        this.specificValue = null;
    }

    public Special(T value) {
        if (value == null) {
            this.type = Type.NULL;
            this.specificValue = null;
        } else {
            this.type = SPECIFIC;
            this.specificValue = value;
        }
    }

    public static Special NULL() {
        return new Special<>();
    }

    public static Special RANDOM() {
        return new Special<>(Type.RANDOM);
    }

    public Type getType() {
        return type;
    }
    public T getValue() {
        return specificValue;
    }

    public Boolean getIsEditable() {
        return isEditable;
    }

    public Special<T> withIsEditable(Boolean isEditable) {
        this.isEditable = isEditable;
        return this;
    }

    public T getSpecificValue() {
        if (type != SPECIFIC) {
            throw new IllegalStateException("Value is not specific");
        }
        return specificValue;
    }

    @Override
    public String toString() {
        if (this.type == Type.NULL)
            return "NULL";
        if (this.type == Type.RANDOM)
            return "RANDOM";
        if (this.type == Type.SPECIFIC)
            return this.specificValue.toString();
        return super.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (obj instanceof Special) {
            Special special = (Special)obj;
            if (this.getType() == RANDOM || special.getType() == RANDOM)
                return false;
            if (this.toString().equals(special.toString()))
                return true;
        }
        return false;
    }

    public int compareTo(Special<T> a)
    {
        String temp1 = this.toString();
        String temp2 = a.toString();
        return temp1.compareTo(temp2);
    }
}
