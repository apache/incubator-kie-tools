package org.drools.workbench.screens.guided.dtree.client.editor;

import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.Well;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.guided.dtree.shared.model.GuidedDecisionTree;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ConstraintNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.Node;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.TypeNode;
import org.drools.workbench.models.guided.dtree.shared.model.values.Value;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.IntegerValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.StringValue;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.services.shared.rulename.RuleNamesService;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;
import org.uberfire.backend.vfs.Path;

/**
 * The Guided Decision Tree Editor View implementation
 */
public class GuidedDecisionTreeEditorViewImpl
        extends KieEditorViewImpl
        implements GuidedDecisionTreeEditorView {

    interface GuidedDecisionTreeEditorViewBinder
            extends
            UiBinder<Widget, GuidedDecisionTreeEditorViewImpl> {

    }

    @UiField
    SimplePanel container;

    private static GuidedDecisionTreeEditorViewBinder uiBinder = GWT.create( GuidedDecisionTreeEditorViewBinder.class );

    private boolean isDirty = false;
    private boolean isReadOnly = false;
    private GuidedDecisionTree model;

    public GuidedDecisionTreeEditorViewImpl() {
        setup();
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    private void setup() {
    }

    @Override
    public void setContent( final Path path,
                            final GuidedDecisionTree model,
                            final AsyncPackageDataModelOracle oracle,
                            final Caller<RuleNamesService> ruleNamesService,
                            final boolean isReadOnly ) {
        this.model = model;
        this.isReadOnly = isReadOnly;
        setNotDirty();

        //Dump model
        final Well dtreeWidget = new Well();
        dtreeWidget.add( new Label( "Decision Tree" ) );
        dtreeWidget.add( new Paragraph( "Package: " + model.getPackageName() ) );
        dtreeWidget.add( new Paragraph( "Name: " + model.getTreeName() ) );

        final TypeNode root = model.getRoot();
        final Well typeWidget = new Well();
        typeWidget.add( new Label( "Type" ) );
        typeWidget.add( new Paragraph( "Class Name: " + root.getClassName() ) );
        dtreeWidget.add( typeWidget );

        for ( Node child : root.getChildren() ) {
            final Well childWidget = new Well();
            if ( child instanceof ConstraintNode ) {
                childWidget.add( new Label( "Constraint" ) );
                childWidget.add( new Paragraph( "Field: " + ( (ConstraintNode) child ).getFieldName() ) );
                childWidget.add( new Paragraph( "Operator: " + ( (ConstraintNode) child ).getOperator() ) );
                final Value value = ( (ConstraintNode) child ).getValue();
                if ( value instanceof StringValue ) {
                    childWidget.add( new Paragraph( "Value: " + ( (StringValue) value ).getValue() ) );
                } else if ( value instanceof IntegerValue ) {
                    childWidget.add( new Paragraph( "Value: " + ( (IntegerValue) value ).getValue() ) );
                }
                typeWidget.add( childWidget );
            }
        }
        container.getElement().getStyle().setPadding( 10,
                                                      Style.Unit.PX );
        container.add( dtreeWidget );
    }

    @Override
    public GuidedDecisionTree getModel() {
        return this.model;
    }

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    @Override
    public void setNotDirty() {
        isDirty = false;
    }

    @Override
    public boolean confirmClose() {
        return Window.confirm( CommonConstants.INSTANCE.DiscardUnsavedData() );
    }

}
