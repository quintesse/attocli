package org.codejive.attocli;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class ArgsParser {
    private Function<String, String> optionNameParser = ArgsParser::defaultOptionName;
    private Function<String, String> optionValueParser = ArgsParser::defaultOptionValue;

    public static ArgsParser create() {
        return new ArgsParser();
    }

    public ArgsParser optionNameParser(Function<String, String> optionNameParser) {
        this.optionNameParser = optionNameParser;
        return this;
    }

    public ArgsParser optionValueParser(Function<String, String> optionValueParser) {
        this.optionValueParser = optionValueParser;
        return this;
    }

    public Args parse(String... args) {
        return parse(Arrays.asList(args));
    }

    public Args parse(Collection<String> args) {
        return new Args(args);
    }

    public class Args implements Iterator<String> {
        private final ArrayDeque<String> args;

        private ArrayDeque<String> options;
        private String currentArg;
        private String currentValue;

        private Args(Collection<String> args) {
            this.args =  new ArrayDeque<>(args);
        }

        @Override
        public boolean hasNext() {
            return !args.isEmpty();
        }

        public boolean hasNextOption() {
            return hasNext() && isOption(peek());
        }

        private void assertArgAvailable() {
            if (currentArg == null) {
                throw new NoSuchElementException();
            }
        }

        private String peek() {
            return args.peekFirst();
        }

        private String pop() {
            return args.removeFirst();
        }

        @Override
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
            assertArgAvailable();
            return isOption(currentArg);
        }

        public boolean isOptionWithValue() {
            return isOption() && optionalValue() != null;
        }

        public String name() {
            if (isOption()) {
                return optionNameParser.apply(currentArg);
            }
            return null;
        }

        public String optionalValue() {
            if (isOption()) {
                if (currentValue == null) {
                    currentValue = optionValueParser.apply(currentArg);
                }
                return currentValue;
            } else {
                return currentArg;
            }
        }

        public String value() {
            if (isOption()) {
                if (currentValue == null) {
                    currentValue = optionValueParser.apply(currentArg);
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

    // Returns the given argument without any leading dashes (single or double)
    private static String dashless(String arg) {
        if (arg.startsWith("--")) {
            arg = arg.substring(2);
        } else if (arg.startsWith("-")) {
            arg = arg.substring(1);
        }
        return arg;
    }

    // Returns the given argument without any leading dashes (single or double)
    private static String defaultOptionName(String arg) {
        arg = dashless(arg);
        int p = arg.indexOf('=');
        if (p > 0) {
            //TODO throw an exception when p == 0?
            return arg.substring(0, p);
        } else {
            return arg;
        }
    }

    // Returns any value that is appended directly to the option.
    // Returns `null` if no appended value was found.
    private static String defaultOptionValue(String arg) {
        arg = dashless(arg);
        int p = arg.indexOf('=');
        if (p > 0) {
            //TODO throw an exception when p == 0?
            return arg.substring(p + 1);
        }
        return null;
    }

}
