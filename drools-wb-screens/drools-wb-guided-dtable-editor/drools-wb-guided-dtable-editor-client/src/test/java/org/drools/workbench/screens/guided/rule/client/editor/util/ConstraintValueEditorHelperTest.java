package org.drools.workbench.screens.guided.rule.client.editor.util;

import java.util.List;

import org.drools.workbench.models.datamodel.oracle.DropDownData;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.ArgumentCaptor;
import org.uberfire.client.callbacks.Callback;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ConstraintValueEditorHelperTest {

    private RuleModel model;

    @Before
    public void setUp() throws Exception {
        model = new RuleModel();
    }

    @Test
    public void testSimplePattern() throws Exception {
        AsyncPackageDataModelOracle oracle = mock(AsyncPackageDataModelOracle.class);

        FactPattern pattern = new FactPattern();
        pattern.setBoundName("pp");
        pattern.setFactType("House");
        model.addLhsItem(pattern);

        FactPattern pattern2 = new FactPattern();
        SingleFieldConstraint constraint = new SingleFieldConstraint();
        constraint.setFactType("House");
        constraint.setFieldName("this");
        constraint.setFieldName("org.mortgages.House");
        pattern2.addConstraint(constraint);
        model.addLhsItem(pattern);

        when(
                oracle.getFieldClassName("House", "this")
        ).thenReturn(
                "org.mortgages.House"
        );

        ConstraintValueEditorHelper helper = new ConstraintValueEditorHelper(
                model,
                oracle,
                "House",
                "this",
                constraint,
                "House",
                new DropDownData());

        helper.isApplicableBindingsInScope("pp", new Callback<Boolean>() {
            @Override
            public void callback(Boolean result) {
                assertTrue(result);
            }
        });

    }
    @Test
    public void testSimpleField() throws Exception {
        AsyncPackageDataModelOracle oracle = mock(AsyncPackageDataModelOracle.class);

        FactPattern pattern = new FactPattern();
        SingleFieldConstraint constraint = new SingleFieldConstraint();
        constraint.setFieldBinding("pp");
        constraint.setFactType("House");
        constraint.setFieldName("parent");
        constraint.setFieldType("org.mortgages.Parent");
        pattern.addConstraint(constraint);
        model.addLhsItem(pattern);

        when(
                oracle.getFieldClassName("House", "parent")
        ).thenReturn(
                "org.mortgages.Parent"
        );

        ConstraintValueEditorHelper helper = new ConstraintValueEditorHelper(
                model,
                oracle,
                "House",
                "parent",
                constraint,
                "Parent",
                new DropDownData());

        helper.isApplicableBindingsInScope("pp", new Callback<Boolean>() {
            @Override
            public void callback(Boolean result) {
                assertTrue(result);
            }
        });

    }

}
