package org.codejive.attocli;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;

import org.codejive.attocli.OptsParamsParser.OptsParams;

public class OptParamTest {

    @Test
    public void testFlagArg() {
        OptsParams result = OptsParamsParser.create().parse("--flag", "arg");
        assertThat(result.options, aMapWithSize(1));
        assertThat(result.options, hasEntry("flag", null));
        assertThat(result.parameters, hasSize(1));
        assertThat(result.parameters, contains("arg"));
    }

    @Test
    public void testOptionAppendedValueArg() {
        OptsParams result = OptsParamsParser.create().parse("--option=foo", "arg");
        assertThat(result.options, aMapWithSize(1));
        assertThat(result.options, hasEntry("option", "foo"));
        assertThat(result.parameters, hasSize(1));
        assertThat(result.parameters, contains("arg"));
    }

    @Test
    public void testOptionAppendedEmptyArg() {
        OptsParams result = OptsParamsParser.create().parse("--option=", "arg");
        assertThat(result.options, aMapWithSize(1));
        assertThat(result.options, hasEntry("option", ""));
        assertThat(result.parameters, hasSize(1));
        assertThat(result.parameters, contains("arg"));
    }

    @Test
    public void testOptionNeededArg() {
        OptsParams result = OptsParamsParser
                .create()
                .needsValue("option")
                .parse("--option", "foo", "arg");
        assertThat(result.options, aMapWithSize(1));
        assertThat(result.options, hasEntry("option", "foo"));
        assertThat(result.parameters, hasSize(1));
        assertThat(result.parameters, contains("arg"));
    }
}
