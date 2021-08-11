package org.codejive.attocli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OptParamParser {
    private Set<String> required;

    public static OptParamParser create() {
        return new OptParamParser();
    }

    public class ParseResult {
        public final Map<String, Object> options;
        public final List<String> parameters;

        ParseResult(Map<String, Object> options, List<String> parameters) {
            this.options = Collections.unmodifiableMap(options);
            this.parameters = Collections.unmodifiableList(parameters);
        }
    }

    public OptParamParser() {
        required = new HashSet<>();
    }

    public OptParamParser required(String... optionNames) {
        required.addAll(Arrays.asList(optionNames));
        return this;
    }

    public ParseResult parse(String... args) {
        return parse(Args.parse(args));
    }

    public ParseResult parse(Args args) {
        Map<String, Object> options = new HashMap<>();
        List<String> parameters = new ArrayList<>();
        while (args.hasNext()) {
            args.next();
            if (args.isOption()) {
                if (required.contains(args.name())) {
                    options.put(args.name(), args.value());
                } else {
                    options.put(args.name(), args.optionalValue());
                }
            } else {
                parameters.add(args.value());
            }
        }
        return new ParseResult(options, parameters);
    }
}
