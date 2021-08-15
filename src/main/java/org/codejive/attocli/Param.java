package org.codejive.attocli;

public class Param implements Arg {
    private final String value;

    public Param(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean isOption() {
        return false;
    }
}
