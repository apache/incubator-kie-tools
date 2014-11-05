package org.kie.workbench.common.widgets.client.datamodel.testclasses;

/**
 * Test class to check recursive Fact-Fields
 */
public class TestIndirectRecursionClassB {

    private TestIndirectRecursionClassA recursiveField;

    public TestIndirectRecursionClassA getRecursiveField() {
        return recursiveField;
    }

    public void setRecursiveField( TestIndirectRecursionClassA recursiveField ) {
        this.recursiveField = recursiveField;
    }

}
