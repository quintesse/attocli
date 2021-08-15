package org.codejive.attocli;

import java.util.ArrayList;
import java.util.List;

public class Option implements Arg {
    private final String name;
    private final List<String> values;

    public Option(String name, String value) {
        List<String> values = new ArrayList<String>();
        values.add(value);
        this.name = name;
        this.values = values;
    }

    public Option(String name, List<String> values) {
        this.name = name;
        this.values = values;
    }

    public String name() {
        return name;
    }

    public List<String> values() {
        return values;
    }

    @Override
    public boolean isOption() {
        return true;
    }
}
