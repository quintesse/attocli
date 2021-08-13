package org.codejive.attocli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codejive.attocli.ArgsParser.Args;

public class OptsParamsParser {
    private Set<String> required;

    public static OptsParamsParser create() {
        return new OptsParamsParser();
    }

    public class OptsParams {
        public final Map<String, Object> options;
        public final List<String> parameters;

        OptsParams(Map<String, Object> options, List<String> parameters) {
            this.options = Collections.unmodifiableMap(options);
            this.parameters = Collections.unmodifiableList(parameters);
        }
    }

    public OptsParamsParser() {
        required = new HashSet<>();
    }

    public OptsParamsParser required(String... optionNames) {
        required.addAll(Arrays.asList(optionNames));
        return this;
    }

    public OptsParams parse(String... args) {
        return parse(ArgsParser.create().parse(args));
    }

    public OptsParams parse(Args args) {
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
        return new OptsParams(options, parameters);
    }
}
