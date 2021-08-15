package org.codejive.attocli;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Args implements Iterable<Arg> {
    private final List<Arg> args;
    private final List<String> rest;

    Args(List<Arg> args, List<String> rest) {
        this.args = args;
        this.rest = rest;
    }

    @Override
    public Iterator<Arg> iterator() {
        return args.iterator();
    }

    public List<Option> options() {
        return args.stream().filter(Arg::isOption).map(arg -> (Option) arg).collect(Collectors.toList());
    }

    public Map<String, Option> optionsMap() {
        return args.stream().filter(Arg::isOption).map(arg -> (Option) arg).collect(Collectors.toMap(Option::name, Function.identity(), (a, b) -> a));
    }

    public List<Param> params() {
        return args.stream().filter(arg -> !arg.isOption()).map(arg -> (Param) arg).collect(Collectors.toList());
    }

    public List<String> rest() {
        return Collections.unmodifiableList(rest);
    }
}
