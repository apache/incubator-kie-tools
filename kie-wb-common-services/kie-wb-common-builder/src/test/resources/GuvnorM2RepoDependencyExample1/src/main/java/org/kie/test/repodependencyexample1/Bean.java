package org.kie.test.repodependencyexample1;

public class Bean {
    private final int value;

    public Bean(int value) {
        this.value = value;
    }

    public int getValue() {
        return value*7;
    }

}
