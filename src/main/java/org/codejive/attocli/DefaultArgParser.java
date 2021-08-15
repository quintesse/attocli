package org.codejive.attocli;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class DefaultArgParser {
    private Function<String, String> optionNameParser = DefaultArgParser::defaultOptionName;
    private Function<String, String> optionValueParser = DefaultArgParser::defaultOptionValue;
    private Function<String, Boolean> needsValue = (String) -> false;

    public static DefaultArgParser create() {
        return new DefaultArgParser();
    }

    public DefaultArgParser optionNameParser(Function<String, String> optionNameParser) {
        this.optionNameParser = optionNameParser;
        return this;
    }

    public DefaultArgParser optionValueParser(Function<String, String> optionValueParser) {
        this.optionValueParser = optionValueParser;
        return this;
    }

    public DefaultArgParser needsValue(String... options) {
        this.needsValue = of(options);
        return this;
    }

    public DefaultArgParser needsValue(Function<String, Boolean> needsValue) {
        this.needsValue = needsValue;
        return this;
    }

    public Arg parse(String arg, Supplier<String> pop) {
        String name = optionNameParser.apply(arg);
        if (name != null) {
            String value = optionValueParser.apply(arg);
            if (value == null && needsValue.apply(name)) {
                value = pop.get();
            }
            return new Option(name, value);
        } else {
            return new Param(arg);
        }
    }

    // Returns the given argument without any leading dashes (single or double)
    private static boolean isOption(String arg) {
        if (arg.startsWith("--") && arg.length() > 2) {
            return true;
        } else if (arg.startsWith("-") && arg.length() > 1) {
            return true;
        } else {
            return false;
        }
    }

    // Returns the given argument without any leading dashes (single or double)
    private static String defaultOptionName(String arg) {
        if (!isOption(arg)) {
            return null;
        }
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
        if (!isOption(arg)) {
            return null;
        }
        int p = arg.indexOf('=');
        if (p > 0) {
            //TODO throw an exception when p == 0?
            return arg.substring(p + 1);
        }
        return null;
    }

    public static Function<String, Boolean> of(String... values) {
        return new HashSet(Arrays.asList(values))::contains;
    }
}
