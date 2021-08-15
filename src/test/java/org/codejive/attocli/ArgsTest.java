package org.codejive.attocli;

import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

public class ArgsTest {

    @Test
    public void testFlagArg() {
        Args args = ArgsParser.create().parse("--flag", "arg");

        Iterator<Arg> iter = args.iterator();
        assertThat(iter.hasNext(), is(true));
        testOption(iter.next(), "flag", null);
        assertThat(iter.hasNext(), is(true));
        testParam(iter.next(), "arg");
        assertThat(iter.hasNext(), is(false));

        assertThat(args.options(), hasSize(1));
        testOption(args.options().get(0), "flag", null);
        assertThat(args.optionsMap(), aMapWithSize(1));
        assertThat(args.optionsMap(), hasKey("flag"));

        assertThat(args.params(), hasSize(1));
        testParam(args.params().get(0), "arg");
    }

    @Test
    public void testOptionAppendedValueArg() {
        Args args = ArgsParser.create().parse("--option=foo", "arg");

        Iterator<Arg> iter = args.iterator();
        assertThat(iter.hasNext(), is(true));
        testOption(iter.next(), "option", "foo");
        assertThat(iter.hasNext(), is(true));
        testParam(iter.next(), "arg");
        assertThat(iter.hasNext(), is(false));

        assertThat(args.options(), hasSize(1));
        testOption(args.options().get(0), "option", "foo");
        assertThat(args.optionsMap(), aMapWithSize(1));
        assertThat(args.optionsMap(), hasKey("option"));

        assertThat(args.params(), hasSize(1));
        testParam(args.params().get(0), "arg");
    }

    @Test
    public void testOptionAppendedEmptyArg() {
        Args args = ArgsParser.create().parse("--option=", "arg");

        Iterator<Arg> iter = args.iterator();
        assertThat(iter.hasNext(), is(true));
        testOption(iter.next(), "option", "");
        assertThat(iter.hasNext(), is(true));
        testParam(iter.next(), "arg");
        assertThat(iter.hasNext(), is(false));

        assertThat(args.options(), hasSize(1));
        testOption(args.options().get(0), "option", "");
        assertThat(args.optionsMap(), aMapWithSize(1));
        assertThat(args.optionsMap(), hasKey("option"));

        assertThat(args.params(), hasSize(1));
        testParam(args.params().get(0), "arg");
    }

    @Test
    public void testOptionValueArg() {
        Args args = ArgsParser.create().needsValue("option").parse("--option", "foo", "arg");

        Iterator<Arg> iter = args.iterator();
        assertThat(iter.hasNext(), is(true));
        testOption(iter.next(), "option", "foo");
        assertThat(iter.hasNext(), is(true));
        testParam(iter.next(), "arg");
        assertThat(iter.hasNext(), is(false));

        assertThat(args.options(), hasSize(1));
        testOption(args.options().get(0), "option", "foo");
        assertThat(args.optionsMap(), aMapWithSize(1));
        assertThat(args.optionsMap(), hasKey("option"));

        assertThat(args.params(), hasSize(1));
        testParam(args.params().get(0), "arg");
    }

    @Test
    public void testMixed() {
        Args args = ArgsParser.create().parse("--flag1", "arg1", "--flag2", "arg2", "--flag1");

        Iterator<Arg> iter = args.iterator();
        testOption(iter.next(), "flag1", null);
        testParam(iter.next(), "arg1");
        testOption(iter.next(), "flag2", null);
        testParam(iter.next(), "arg2");
        testOption(iter.next(), "flag1", null);
        assertThat(iter.hasNext(), is(false));

        assertThat(args.options(), hasSize(3));
        testOption(args.options().get(0), "flag1", null);
        testOption(args.options().get(1), "flag2", null);
        testOption(args.options().get(2), "flag1", null);
        assertThat(args.optionsMap(), aMapWithSize(2));
        assertThat(args.optionsMap().keySet(), containsInAnyOrder("flag1", "flag2"));

        assertThat(args.params(), hasSize(2));
        testParam(args.params().get(0), "arg1");
        testParam(args.params().get(1), "arg2");

        assertThat(args.rest(), empty());
    }

    @Test
    public void testMixed2() {
        Args args = ArgsParser.create().needsValue("option").parse("--option", "foo", "--flag2", "arg2", "--flag1");

        Iterator<Arg> iter = args.iterator();
        testOption(iter.next(), "option", "foo");
        testOption(iter.next(), "flag2", null);
        testParam(iter.next(), "arg2");
        testOption(iter.next(), "flag1", null);
        assertThat(iter.hasNext(), is(false));

        assertThat(args.options(), hasSize(3));
        testOption(args.options().get(0), "option", "foo");
        testOption(args.options().get(1), "flag2", null);
        testOption(args.options().get(2), "flag1", null);
        assertThat(args.optionsMap(), aMapWithSize(3));
        assertThat(args.optionsMap().keySet(), containsInAnyOrder("flag1", "flag2", "option"));

        assertThat(args.params(), hasSize(1));
        testParam(args.params().get(0), "arg2");

        assertThat(args.rest(), empty());
    }

    @Test
    public void testNonMixed() {
        Args args = ArgsParser.create().mixedArgs(false).parse("--flag1", "arg1", "--flag2", "arg2", "--flag1");

        Iterator<Arg> iter = args.iterator();
        testOption(iter.next(), "flag1", null);
        testParam(iter.next(), "arg1");
        assertThat(iter.hasNext(), is(false));

        assertThat(args.options(), hasSize(1));
        testOption(args.options().get(0), "flag1", null);
        assertThat(args.optionsMap(), aMapWithSize(1));
        assertThat(args.optionsMap(), hasKey("flag1"));

        assertThat(args.params(), hasSize(1));
        testParam(args.params().get(0), "arg1");

        assertThat(args.rest(), hasSize(3));
        assertThat(args.rest(), contains("--flag2", "arg2", "--flag1"));
    }

    @Test
    public void testNonMixed2() {
        Args args = ArgsParser.create().needsValue("option").mixedArgs(false).parse("--option", "foo", "--flag2", "arg2", "--flag1");

        Iterator<Arg> iter = args.iterator();
        testOption(iter.next(), "option", "foo");
        testOption(iter.next(), "flag2", null);
        testParam(iter.next(), "arg2");
        assertThat(iter.hasNext(), is(false));

        assertThat(args.options(), hasSize(2));
        testOption(args.options().get(0), "option", "foo");
        testOption(args.options().get(1), "flag2", null);
        assertThat(args.optionsMap(), aMapWithSize(2));
        assertThat(args.optionsMap().keySet(), containsInAnyOrder("flag2", "option"));

        assertThat(args.params(), hasSize(1));
        testParam(args.params().get(0), "arg2");

        assertThat(args.rest(), hasSize(1));
        assertThat(args.rest(), contains("--flag1"));
    }

    @Test
    public void testOptions() {
        Args args = ArgsParser.create().needsValue("option2").parse("--flag", "--empty=", "--option1=foo", "--option2", "bar");

        Iterator<Arg> iter = args.iterator();
        testOption(iter.next(), "flag", null);
        testOption(iter.next(), "empty", "");
        testOption(iter.next(), "option1", "foo");
        testOption(iter.next(), "option2", "bar");
        assertThat(iter.hasNext(), is(false));

        assertThat(args.options(), hasSize(4));
        testOption(args.options().get(0), "flag", null);
        testOption(args.options().get(1), "empty", "");
        testOption(args.options().get(2), "option1", "foo");
        testOption(args.options().get(3), "option2", "bar");
        assertThat(args.optionsMap(), aMapWithSize(4));
        assertThat(args.optionsMap().keySet(), containsInAnyOrder("flag", "empty", "option1", "option2"));

        assertThat(args.params(), empty());

        assertThat(args.rest(), empty());
    }

    private void testOption(Arg arg, String name, String value) {
        assertThat(arg.isOption(), is(true));
        assertThat(arg, instanceOf(Option.class));
        Option opt = (Option)arg;
        assertThat(opt.name(), equalTo(name));
        if (value != null) {
            assertThat(opt.value(), equalTo(value));
        } else {
            assertThat(opt.value(), nullValue());
        }
    }

    private void testParam(Arg arg, String value) {
        assertThat(arg.isOption(), not(true));
        assertThat(arg, instanceOf(Param.class));
        Param param = (Param)arg;
        assertThat(param.value(), equalTo(value));
    }
}
