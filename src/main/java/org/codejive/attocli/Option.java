package org.codejive.attocli;

public class Option implements Arg {
    private final String name;
    private final String value;

    public Option(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String name() {
        return name;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean isOption() {
        return true;
    }
}
