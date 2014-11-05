package org.kie.workbench.common.widgets.client.datamodel.testclasses;

/**
 * Test class to check recursive Fact-Fields
 */
public class TestDirectRecursionClass {

    private TestDirectRecursionClass recursiveField;

    public TestDirectRecursionClass getRecursiveField() {
        return recursiveField;
    }

    public void setRecursiveField( TestDirectRecursionClass recursiveField ) {
        this.recursiveField = recursiveField;
    }

}
