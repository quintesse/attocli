package org.codejive.attocli;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;

public class OptParamTest {

    @Test
    public void testFlagArg() {
        OptParamParser.ParseResult result = OptParamParser.create().parse("--flag", "arg");
        assertThat(result.options, aMapWithSize(1));
        assertThat(result.options, hasEntry("flag", null));
        assertThat(result.parameters, hasSize(1));
        assertThat(result.parameters, contains("arg"));
    }

    @Test
    public void testOptionAppendedValueArg() {
        OptParamParser.ParseResult result = OptParamParser.create().parse("--option=foo", "arg");
        assertThat(result.options, aMapWithSize(1));
        assertThat(result.options, hasEntry("option", "foo"));
        assertThat(result.parameters, hasSize(1));
        assertThat(result.parameters, contains("arg"));
    }

    @Test
    public void testOptionAppendedEmptyArg() {
        OptParamParser.ParseResult result = OptParamParser.create().parse("--option=", "arg");
        assertThat(result.options, aMapWithSize(1));
        assertThat(result.options, hasEntry("option", ""));
        assertThat(result.parameters, hasSize(1));
        assertThat(result.parameters, contains("arg"));
    }
}
