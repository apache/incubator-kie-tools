package org.drools.workbench.screens.guided.dtree.client.editor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.guided.dtree.shared.model.GuidedDecisionTree;
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
