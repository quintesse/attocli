package org.codejive.attocli;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ArgsParser {
    private DefaultArgParser defaultArgParser = DefaultArgParser.create();
    private BiFunction<String, Supplier<String>, Arg> argParser = defaultArgParser::parse;
    private Function<Iterator<Arg>, Iterator<Arg>> until = Function.identity();

    public static ArgsParser create() {
        return new ArgsParser();
    }

    public ArgsParser needsValue(String... options) {
        defaultArgParser.needsValue(DefaultArgParser.of(options));
        return this;
    }

    public ArgsParser needsValue(Function<String, Boolean> needsValue) {
        defaultArgParser.needsValue(needsValue);
        return this;
    }

    public ArgsParser argParser(Function<String, Arg> argParser) {
        this.argParser = (arg, pop) -> argParser.apply(arg);
        return this;
    }

    public ArgsParser argParser(BiFunction<String, Supplier<String>, Arg> argParser) {
        this.argParser = argParser;
        return this;
    }

    public ArgsParser commandMode() {
        return until(ArgsParser::stopAtFirstParameter);
    }

    public ArgsParser until(Function<Iterator<Arg>, Iterator<Arg>> until) {
        this.until = until;
        return this;
    }

    public Args parse(String... args) {
        return parse(Arrays.asList(args));
    }

    public Args parse(Collection<String> args) {
        ArrayDeque<String> argsDeque = new ArrayDeque<>(args);
        Iterator<Arg> iter = new Iterator<Arg>() {
            @Override
            public boolean hasNext() {
                return !argsDeque.isEmpty();
            }

            @Override
            public Arg next() {
                return argParser.apply(argsDeque.removeFirst(), argsDeque::pollFirst);
            }
        };
        iter = until.apply(iter);
        Spliterator<Arg> spliter = Spliterators.spliteratorUnknownSize(iter, Spliterator.NONNULL | Spliterator.ORDERED);
        List<Arg> argsList = StreamSupport.stream(spliter, false).collect(Collectors.toList());
        List<String> rest = new ArrayList<>(argsDeque);
        return new Args(argsList, rest);
    }

    public static Iterator<Arg> stopAtFirstParameter(Iterator<Arg> iter) {
        return new Iterator<Arg>() {
            private boolean paramFound;

            @Override
            public boolean hasNext() {
                return iter.hasNext() && !paramFound;
            }

            @Override
            public Arg next() {
                Arg arg = iter.next();
                paramFound |= !arg.isOption();
                return arg;
            }
        };
    }
}
