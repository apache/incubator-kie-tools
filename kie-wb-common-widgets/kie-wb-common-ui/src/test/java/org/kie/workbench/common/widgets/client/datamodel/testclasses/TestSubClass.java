package org.kie.workbench.common.widgets.client.datamodel.testclasses;

/**
 * Test class to check data-types are extracted correctly by ProjectDataModelOracleBuilder for subclasses and delegated classes
 */
public class TestSubClass extends TestSuperClass {

    private String field2;

    public String getField2() {
        return field2;
    }

    public void setField2( final String field2 ) {
        this.field2 = field2;
    }

}
