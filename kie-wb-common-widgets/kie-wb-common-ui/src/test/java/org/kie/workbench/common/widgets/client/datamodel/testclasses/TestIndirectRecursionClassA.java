package org.kie.workbench.common.widgets.client.datamodel.testclasses;

/**
 * Test class to check recursive Fact-Fields
 */
public class TestIndirectRecursionClassA {

    private TestIndirectRecursionClassB recursiveField;

    public TestIndirectRecursionClassB getRecursiveField() {
        return recursiveField;
    }

    public void setRecursiveField( TestIndirectRecursionClassB recursiveField ) {
        this.recursiveField = recursiveField;
    }

}
