package com.dataincloud;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ImpossibleTest {
    @Test
    void failing() {
        assertThat(0).isEqualTo(1);
    }
}
