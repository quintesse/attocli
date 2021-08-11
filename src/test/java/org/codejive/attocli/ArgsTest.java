package org.codejive.attocli;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

public class ArgsTest {

    @Test
    public void testFlagArg() {
        boolean hasFlag = false;
        Args args = Args.parse("--flag", "arg");
        while (args.hasOption()) {
            args.next();
            assertThat(args.isOption(), is(true));
            switch (args.name()) {
                case "flag":
                    hasFlag = true;
            }
        }
        assertThat(hasFlag, is(true));

        assertThat(args.hasNext(), is(true));
        args.next();
        assertThat(args.isOption(), not(true));
        assertThat(args.name(), nullValue());
        assertThat(args.value(), equalTo("arg"));
    }

    @Test
    public void testOptionAppendedValueArg() {
        boolean hasOpt = false;
        String val = null;
        String optionalVal = null;
        Args args = Args.parse("--option=foo", "arg");
        while (args.hasOption()) {
            args.next();
            assertThat(args.isOption(), is(true));
            switch (args.name()) {
                case "option":
                    hasOpt = true;
                    val = args.value();
                    optionalVal = args.optionalValue();
            }
        }
        assertThat(hasOpt, is(true));
        assertThat(val, equalTo("foo"));
        assertThat(optionalVal, equalTo("foo"));

        assertThat(args.hasNext(), is(true));
        args.next();
        assertThat(args.isOption(), not(true));
        assertThat(args.name(), nullValue());
        assertThat(args.value(), equalTo("arg"));
    }

    @Test
    public void testOptionAppendedEmptyArg() {
        boolean hasOpt = false;
        String val = null;
        String optionalVal = null;
        Args args = Args.parse("--option=", "arg");
        while (args.hasOption()) {
            args.next();
            assertThat(args.isOption(), is(true));
            switch (args.name()) {
                case "option":
                    hasOpt = true;
                    val = args.value();
                    optionalVal = args.optionalValue();
            }
        }
        assertThat(hasOpt, is(true));
        assertThat(val, equalTo(""));
        assertThat(optionalVal, equalTo(""));

        assertThat(args.hasNext(), is(true));
        args.next();
        assertThat(args.isOption(), not(true));
        assertThat(args.name(), nullValue());
        assertThat(args.value(), equalTo("arg"));
    }

    @Test
    public void testOptionValueArg() {
        boolean hasOpt = false;
        String val = null;
        String optionalVal = null;
        Args args = Args.parse("--option", "foo", "arg");
        while (args.hasOption()) {
            args.next();
            assertThat(args.isOption(), is(true));
            switch (args.name()) {
                case "option":
                    hasOpt = true;
                    optionalVal = args.optionalValue();
                    val = args.value();
            }
        }
        assertThat(hasOpt, is(true));
        assertThat(val, equalTo("foo"));
        assertThat(optionalVal, nullValue());

        assertThat(args.hasNext(), is(true));
        args.next();
        assertThat(args.isOption(), not(true));
        assertThat(args.name(), nullValue());
        assertThat(args.value(), equalTo("arg"));
    }

    @Test
    public void testRestEmpty() {
        boolean hasFlag = false;
        Args args = Args.parse("--flag");
        args.next();
        List<String> rest = args.rest();
        assertThat(rest, empty());
    }

    @Test
    public void testRestOne() {
        boolean hasFlag = false;
        Args args = Args.parse("--flag", "one");
        args.next();
        List<String> rest = args.rest();
        assertThat(rest.size(), is(1));
        assertThat(rest, contains("one"));
    }

    @Test
    public void testRestMulti() {
        boolean hasFlag = false;
        Args args = Args.parse("--flag", "one", "two", "three");
        args.next();
        List<String> rest = args.rest();
        assertThat(rest.size(), is(3));
        assertThat(rest, contains("one", "two", "three"));
    }
}
