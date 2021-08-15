package org.codejive.attocli;

import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import static org.codejive.attocli.ArgsTest.testOption;

public class AltArgsTest {

    @Test
    public void testOptionsAlt() {
        Args args = ArgsParser.create()
                .argParser(DefaultArgParser.create()
                        .optionNameParser(AltArgsTest::altOptionName)
                        .optionValueParser(AltArgsTest::altOptionValue)
                        .needsValue("option2")::parse)
                .parse("+flag", "+empty:", "+option1:foo", "+option2", "bar");

        Iterator<Arg> iter = args.iterator();
        testOption(iter.next(), "flag", (String)null);
        testOption(iter.next(), "empty", "");
        testOption(iter.next(), "option1", "foo");
        testOption(iter.next(), "option2", "bar");
        assertThat(iter.hasNext(), is(false));

        assertThat(args.options(), hasSize(4));
        testOption(args.options().get(0), "flag", (String)null);
        testOption(args.options().get(1), "empty", "");
        testOption(args.options().get(2), "option1", "foo");
        testOption(args.options().get(3), "option2", "bar");
        assertThat(args.optionsMap(), aMapWithSize(4));
        assertThat(args.optionsMap().keySet(), containsInAnyOrder("flag", "empty", "option1", "option2"));
        testOption(args.optionsMap().get("flag"), "flag", (String)null);
        testOption(args.optionsMap().get("empty"), "empty", "");
        testOption(args.optionsMap().get("option1"), "option1", "foo");
        testOption(args.optionsMap().get("option2"), "option2", "bar");

        assertThat(args.params(), empty());

        assertThat(args.rest(), empty());
    }

    // Returns the given argument without any leading plus sign
    private static String plusless(String arg) {
        if (arg.startsWith("+")) {
            arg = arg.substring(1);
        } else {
            return null;
        }
        return arg;
    }

    private static String altOptionName(String arg) {
        arg = plusless(arg);
        if (arg == null) {
            return null;
        }
        int p = arg.indexOf(':');
        if (p > 0) {
            return arg.substring(0, p);
        } else {
            return arg;
        }
    }

    private static String altOptionValue(String arg) {
        arg = plusless(arg);
        if (arg == null) {
            return null;
        }
        int p = arg.indexOf(':');
        if (p > 0) {
            return arg.substring(p + 1);
        }
        return null;
    }
}
