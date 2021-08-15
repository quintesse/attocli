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
        testOption(iter.next(), "flag", (String)null);
        assertThat(iter.hasNext(), is(true));
        testParam(iter.next(), "arg");
        assertThat(iter.hasNext(), is(false));

        assertThat(args.options(), hasSize(1));
        testOption(args.options().get(0), "flag", (String)null);
        assertThat(args.optionsMap(), aMapWithSize(1));
        assertThat(args.optionsMap(), hasKey("flag"));
        testOption(args.optionsMap().get("flag"), "flag", (String)null);

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
        testOption(args.optionsMap().get("option"), "option", "foo");

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
        testOption(args.optionsMap().get("option"), "option", "");

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
        testOption(args.optionsMap().get("option"), "option", "foo");

        assertThat(args.params(), hasSize(1));
        testParam(args.params().get(0), "arg");
    }

    @Test
    public void testMixed() {
        Args args = ArgsParser.create().parse("--flag1", "arg1", "--flag2", "arg2", "--flag1");

        Iterator<Arg> iter = args.iterator();
        testOption(iter.next(), "flag1", (String)null);
        testParam(iter.next(), "arg1");
        testOption(iter.next(), "flag2", (String)null);
        testParam(iter.next(), "arg2");
        testOption(iter.next(), "flag1", (String)null);
        assertThat(iter.hasNext(), is(false));

        assertThat(args.options(), hasSize(3));
        testOption(args.options().get(0), "flag1", (String)null);
        testOption(args.options().get(1), "flag2", (String)null);
        testOption(args.options().get(2), "flag1", (String)null);
        assertThat(args.optionsMap(), aMapWithSize(2));
        assertThat(args.optionsMap().keySet(), containsInAnyOrder("flag1", "flag2"));
        testOption(args.optionsMap().get("flag1"), "flag1", (String)null, (String)null);
        testOption(args.optionsMap().get("flag2"), "flag2", (String)null);

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
        testOption(iter.next(), "flag2", (String)null);
        testParam(iter.next(), "arg2");
        testOption(iter.next(), "flag1", (String)null);
        assertThat(iter.hasNext(), is(false));

        assertThat(args.options(), hasSize(3));
        testOption(args.options().get(0), "option", "foo");
        testOption(args.options().get(1), "flag2", (String)null);
        testOption(args.options().get(2), "flag1", (String)null);
        assertThat(args.optionsMap(), aMapWithSize(3));
        assertThat(args.optionsMap().keySet(), containsInAnyOrder("flag1", "flag2", "option"));
        testOption(args.optionsMap().get("option"), "option", "foo");
        testOption(args.optionsMap().get("flag2"), "flag2", (String)null);
        testOption(args.optionsMap().get("flag1"), "flag1", (String)null);

        assertThat(args.params(), hasSize(1));
        testParam(args.params().get(0), "arg2");

        assertThat(args.rest(), empty());
    }

    @Test
    public void testNonMixed() {
        Args args = ArgsParser.create().mixedArgs(false).parse("--flag1", "arg1", "--flag2", "arg2", "--flag1");

        Iterator<Arg> iter = args.iterator();
        testOption(iter.next(), "flag1", (String)null);
        testParam(iter.next(), "arg1");
        assertThat(iter.hasNext(), is(false));

        assertThat(args.options(), hasSize(1));
        testOption(args.options().get(0), "flag1", (String)null);
        assertThat(args.optionsMap(), aMapWithSize(1));
        assertThat(args.optionsMap(), hasKey("flag1"));
        testOption(args.optionsMap().get("flag1"), "flag1", (String)null);

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
        testOption(iter.next(), "flag2", (String)null);
        testParam(iter.next(), "arg2");
        assertThat(iter.hasNext(), is(false));

        assertThat(args.options(), hasSize(2));
        testOption(args.options().get(0), "option", "foo");
        testOption(args.options().get(1), "flag2", (String)null);
        assertThat(args.optionsMap(), aMapWithSize(2));
        assertThat(args.optionsMap().keySet(), containsInAnyOrder("flag2", "option"));
        testOption(args.optionsMap().get("option"), "option", "foo");
        testOption(args.optionsMap().get("flag2"), "flag2", (String)null);

        assertThat(args.params(), hasSize(1));
        testParam(args.params().get(0), "arg2");

        assertThat(args.rest(), hasSize(1));
        assertThat(args.rest(), contains("--flag1"));
    }

    @Test
    public void testOptions() {
        Args args = ArgsParser.create().needsValue("option2").parse("--flag", "--empty=", "--option1=foo", "--option2", "bar");

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

    @Test
    public void testMultiOption() {
        Args args = ArgsParser.create().needsValue("option").parse("--option=", "--option=foo", "--option", "bar");

        Iterator<Arg> iter = args.iterator();
        testOption(iter.next(), "option", "");
        testOption(iter.next(), "option", "foo");
        testOption(iter.next(), "option", "bar");
        assertThat(iter.hasNext(), is(false));

        assertThat(args.options(), hasSize(3));
        testOption(args.options().get(0), "option", "");
        testOption(args.options().get(1), "option", "foo");
        testOption(args.options().get(2), "option", "bar");
        assertThat(args.optionsMap(), aMapWithSize(1));
        assertThat(args.optionsMap().keySet(), contains("option"));
        testOption(args.optionsMap().get("option"), "option", "", "foo", "bar");

        assertThat(args.params(), empty());

        assertThat(args.rest(), empty());
    }

    @Test
    public void testMultiOption2() {
        Args args = ArgsParser.create().parse("--option=", "--option=foo", "--option");

        Iterator<Arg> iter = args.iterator();
        testOption(iter.next(), "option", "");
        testOption(iter.next(), "option", "foo");
        testOption(iter.next(), "option", (String)null);
        assertThat(iter.hasNext(), is(false));

        assertThat(args.options(), hasSize(3));
        testOption(args.options().get(0), "option", "");
        testOption(args.options().get(1), "option", "foo");
        testOption(args.options().get(2), "option", (String)null);
        assertThat(args.optionsMap(), aMapWithSize(1));
        assertThat(args.optionsMap().keySet(), contains("option"));
        testOption(args.optionsMap().get("option"), "option", "", "foo", (String)null);

        assertThat(args.params(), empty());

        assertThat(args.rest(), empty());
    }

    static void testOption(Arg arg, String name, String... values) {
        assertThat(arg.isOption(), is(true));
        assertThat(arg, instanceOf(Option.class));
        Option opt = (Option)arg;
        assertThat(opt.name(), equalTo(name));
        assertThat(opt.values().size(), equalTo(values.length));
        for (int i = 0; i < values.length; i++) {
            if (values[i] != null) {
                assertThat(opt.values().get(i), equalTo(values[i]));
            } else {
                assertThat(opt.values().get(i), nullValue());
            }
        }
    }

    static void testParam(Arg arg, String value) {
        assertThat(arg.isOption(), not(true));
        assertThat(arg, instanceOf(Param.class));
        Param param = (Param)arg;
        assertThat(param.value(), equalTo(value));
    }
}
