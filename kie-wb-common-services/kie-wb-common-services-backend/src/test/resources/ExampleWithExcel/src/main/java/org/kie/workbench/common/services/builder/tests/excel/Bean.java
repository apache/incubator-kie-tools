package org.kie.workbench.common.services.builder.tests.excel;

public class Bean {
    private final int value;

    public Bean(int value) {
        this.value = value;
    }

    public int getValue() {
        return value*7;
    }

}
