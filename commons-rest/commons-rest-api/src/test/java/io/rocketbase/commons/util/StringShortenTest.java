package io.rocketbase.commons.util;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class StringShortenTest {

    @Test
    public void leftNull() {
        // given
        String value = null;

        // when
        String result = StringShorten.left(value, 10, "...");
        // then
        assertThat(result, notNullValue());
        assertThat(result, equalTo(""));
    }

    @Test
    public void leftShort() {
        // given
        String value = "hello";

        // when
        String result = StringShorten.left(value, 10, "...");
        // then
        assertThat(result, notNullValue());
        assertThat(result, equalTo(value));
    }

    @Test
    public void leftTooLong() {
        // given
        String value = "hello again in the wold of java and rest";

        // when
        String result = StringShorten.left(value, 10, "...");
        // then
        assertThat(result, notNullValue());
        assertThat(result.length(), equalTo(10));
        assertThat(result, equalTo("hello a..."));
    }


    @Test
    public void rightNull() {
        // given
        String value = null;

        // when
        String result = StringShorten.right(value, 10, "...");
        // then
        assertThat(result, notNullValue());
        assertThat(result.length(), equalTo(0));
        assertThat(result, equalTo(""));
    }

    @Test
    public void rightShort() {
        // given
        String value = "hello";

        // when
        String result = StringShorten.right(value, 10, "...");
        // then
        assertThat(result, notNullValue());
        assertThat(result.length(), equalTo(5));
        assertThat(result, equalTo(value));
    }

    @Test
    public void rightTooLong() {
        // given
        String value = "hello again in the wold of java and rest";

        // when
        String result = StringShorten.right(value, 10, "...");
        // then
        assertThat(result, notNullValue());
        assertThat(result.length(), equalTo(10));
        assertThat(result, equalTo("...nd rest"));
    }
}