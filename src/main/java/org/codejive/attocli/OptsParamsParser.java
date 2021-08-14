package org.codejive.attocli;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.codejive.attocli.ArgsParser.Args;

public class OptsParamsParser {
    private Function<String, Boolean> needsValue = (String) -> false;

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
    }

    public OptsParamsParser needsValue(String... options) {
        this.needsValue = of(options);
        return this;
    }

    public OptsParamsParser needsValue(Function<String, Boolean> needsValue) {
        this.needsValue = needsValue;
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
                if (args.isOptionWithValue() || needsValue.apply(args.name())) {
                    options.put(args.name(), args.value());
                } else {
                    options.put(args.name(), null);
                }
            } else {
                parameters.add(args.value());
            }
        }
        return new OptsParams(options, parameters);
    }

    public static Function<String, Boolean> of(String... values) {
        return Set.of(values)::contains;
    }
}
