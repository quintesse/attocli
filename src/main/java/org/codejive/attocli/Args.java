package org.codejive.attocli;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Args implements Iterable<Arg> {
    private final List<Arg> args;
    private final List<String> rest;
    private final Predicate<Args> helpAsked;
    private final Runnable showHelp;

    Args(List<Arg> args, List<String> rest, Predicate<Args> helpAsked, Runnable showHelp) {
        this.args = args;
        this.rest = rest;
        this.helpAsked = helpAsked;
        this.showHelp = showHelp;
    }

    @Override
    public Iterator<Arg> iterator() {
        return args.iterator();
    }

    public List<Option> options() {
        return args.stream().filter(Arg::isOption).map(arg -> (Option) arg).collect(Collectors.toList());
    }

    public Map<String, Option> optionsMap() {
        return args.stream().filter(Arg::isOption).map(arg -> (Option) arg).collect(Collectors.toMap(Option::name, Function.identity(), (a, b) -> {
            List<String> values = new ArrayList<>(a.values());
            values.addAll(b.values());
            return new Option(a.name(), values);
        }));
    }

    public List<Param> params() {
        return args.stream().filter(arg -> !arg.isOption()).map(arg -> (Param) arg).collect(Collectors.toList());
    }

    public List<String> rest() {
        return Collections.unmodifiableList(rest);
    }

    public boolean showHelp() {
        if (helpAsked.test(this)) {
            showHelp.run();
            return true;
        } else {
            return false;
        }
    }
}
