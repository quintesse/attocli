package org.codejive.attocli;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Args implements Iterator<String> {
    private final ArrayDeque<String> args;

    private String currentArg;
    private String currentValue;

    public static Args parse(String... args) {
        return new Args(args);
    }

    private Args(String... args) {
        this.args =  new ArrayDeque<>(Arrays.asList(args));
    }

    @Override
    public boolean hasNext() {
        return !args.isEmpty();
    }

    private String peek() {
        return args.peekFirst();
    }

    private String pop() {
        return args.removeFirst();
    }

    public String next() {
        currentArg = pop();
        currentValue = null;
        return currentArg;
    }

    public List<String> rest() {
        List<String> rest = new ArrayList<>(args);
        args.clear();
        return rest;
    }

    private boolean isOption(String arg) {
        return arg.startsWith("-");
    }

    public boolean isOption() {
        return isOption(currentArg);
    }

    public boolean hasOption() {
        return hasNext() && isOption(peek());
    }

    // Returns the given argument without any leading dashes (single or double)
    private String dashless(String arg) {
        if (arg.startsWith("--")) {
            arg = arg.substring(2);
        } else if (arg.startsWith("-")) {
            arg = arg.substring(1);
        }
        return arg;
    }

    // Returns any value that is appended directly to the option.
    // Returns `null` if no appended value was found.
    private String appendedValue(String arg) {
        int p = arg.indexOf('=');
        if (p > 0) {
            //TODO throw an exception when p == 0?
            return arg.substring(p + 1);
        }
        return null;
    }

    public String name() {
        if (isOption()) {
            String arg = dashless(currentArg);
            int p = arg.indexOf('=');
            if (p > 0) {
                return arg.substring(0, p);
            } else {
                //TODO throw an exception when p == 0?
                return arg;
            }
        }
        return null;
    }

    public String optionalValue() {
        if (isOption()) {
            if (currentValue == null) {
                currentValue = appendedValue(dashless(currentArg));
            }
            return currentValue;
        } else {
            return currentArg;
        }
    }

    public String value() {
        if (isOption()) {
            if (currentValue == null) {
                currentValue = appendedValue(dashless(currentArg));
                if (currentValue == null && hasNext()) {
                    currentValue = pop();
                }
            }
            return currentValue;
        } else {
            return currentArg;
        }
    }
}
