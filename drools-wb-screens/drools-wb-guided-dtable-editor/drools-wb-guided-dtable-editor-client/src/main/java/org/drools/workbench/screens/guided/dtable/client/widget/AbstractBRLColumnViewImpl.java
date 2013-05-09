/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.commons.shared.rule.InterpolationVariable;
import org.drools.workbench.models.commons.shared.rule.RuleModel;
import org.drools.workbench.models.commons.shared.rule.RuleModelVisitor;
import org.drools.workbench.models.guided.dtable.shared.model.BRLColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.kie.guvnor.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.Constants;
import org.drools.workbench.screens.guided.rule.client.editor.ModellerWidgetFactory;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModelEditor;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModellerConfiguration;
import org.drools.workbench.screens.guided.rule.client.editor.events.TemplateVariablesChangedEvent;
import org.drools.workbench.screens.guided.template.client.editor.TemplateModellerWidgetFactory;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.common.Popup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An editor for BRL Column definitions
 */
public abstract class AbstractBRLColumnViewImpl<T, C extends BaseColumn> extends Popup
        implements
        RuleModelEditor,
        TemplateVariablesChangedEvent.Handler {

    protected int MIN_WIDTH = 500;
    protected int MIN_HEIGHT = 200;

    @UiField(provided = true)
    RuleModeller ruleModeller;

    @UiField
    TextBox txtColumnHeader;

    @UiField
    CheckBox chkHideColumn;

    @UiField
    ScrollPanel brlEditorContainer;

    @UiField
    Button cmdApplyChanges;

    Widget popupContent;

    @SuppressWarnings("rawtypes")
    interface AbstractBRLColumnEditorBinder
            extends
            UiBinder<Widget, AbstractBRLColumnViewImpl> {

    }

    private static AbstractBRLColumnEditorBinder uiBinder = GWT.create( AbstractBRLColumnEditorBinder.class );

    protected final GuidedDecisionTable52 model;
    protected final EventBus eventBus;
    protected final boolean isNew;

    protected final BRLColumn<T, C> editingCol;
    protected final BRLColumn<T, C> originalCol;

    protected final RuleModel ruleModel;

    public AbstractBRLColumnViewImpl( final Path path,
                                      final PackageDataModelOracle oracle,
                                      final GuidedDecisionTable52 model,
                                      final BRLColumn<T, C> column,
                                      final EventBus eventBus,
                                      final boolean isNew,
                                      final boolean isReadOnly ) {
        this.model = model;
        this.isNew = isNew;
        this.eventBus = eventBus;
        this.originalCol = column;
        this.editingCol = cloneBRLColumn( column );
        this.ruleModel = getRuleModel( editingCol );

        setModal( false );

        ModellerWidgetFactory widgetFactory = new TemplateModellerWidgetFactory();

        this.ruleModeller = new RuleModeller( path,
                                              ruleModel,
                                              oracle,
                                              widgetFactory,
                                              getRuleModellerConfiguration(),
                                              eventBus,
                                              isReadOnly );

        this.popupContent = uiBinder.createAndBindUi( this );

        setHeight( getPopupHeight() + "px" );
        setWidth( getPopupWidth() + "px" );
        this.brlEditorContainer.setHeight( ( getPopupHeight() - 120 ) + "px" );
        this.brlEditorContainer.setWidth( getPopupWidth() + "px" );
        this.txtColumnHeader.setText( editingCol.getHeader() );
        this.txtColumnHeader.setEnabled( !isReadOnly );
        this.chkHideColumn.setValue( editingCol.isHideColumn() );
    }

    @Override
    public void show() {
        //Hook-up events
        final HandlerRegistration registration = eventBus.addHandler( TemplateVariablesChangedEvent.TYPE,
                                                                      this );

        //Release event handlers when closed
        addCloseHandler( new CloseHandler<PopupPanel>() {

            public void onClose( CloseEvent<PopupPanel> event ) {
                registration.removeHandler();
            }

        } );
        super.show();
    }

    protected abstract boolean isHeaderUnique( String header );

    protected abstract RuleModel getRuleModel( BRLColumn<T, C> column );

    protected abstract RuleModellerConfiguration getRuleModellerConfiguration();

    protected abstract void doInsertColumn();

    protected abstract void doUpdateColumn();

    protected abstract List<C> convertInterpolationVariables( Map<InterpolationVariable, Integer> ivs );

    protected abstract BRLColumn<T, C> cloneBRLColumn( BRLColumn<T, C> col );

    public RuleModeller getRuleModeller() {
        return this.ruleModeller;
    }

    @Override
    public Widget getContent() {
        return popupContent;
    }

    /**
     * Width of pop-up, 75% of the client width or MIN_WIDTH
     * @return
     */
    private int getPopupWidth() {
        int w = (int) ( Window.getClientWidth() * 0.75 );
        if ( w < MIN_WIDTH ) {
            w = MIN_WIDTH;
        }
        return w;
    }

    /**
     * Height of pop-up, 75% of the client height or MIN_HEIGHT
     * @return
     */
    protected int getPopupHeight() {
        int h = (int) ( Window.getClientHeight() * 0.75 );
        if ( h < MIN_HEIGHT ) {
            h = MIN_HEIGHT;
        }
        return h;
    }

    @UiHandler("txtColumnHeader")
    void columnHanderChangeHandler( ChangeEvent event ) {
        editingCol.setHeader( txtColumnHeader.getText() );
    }

    @UiHandler("chkHideColumn")
    void hideColumnClickHandler( ClickEvent event ) {
        editingCol.setHideColumn( chkHideColumn.getValue() );
    }

    @UiHandler("cmdApplyChanges")
    void applyChangesClickHandler( ClickEvent event ) {

        //Validation
        if ( null == editingCol.getHeader() || "".equals( editingCol.getHeader() ) ) {
            Window.alert( Constants.INSTANCE.YouMustEnterAColumnHeaderValueDescription() );
            return;
        }
        if ( isNew ) {
            if ( !isHeaderUnique( editingCol.getHeader() ) ) {
                Window.alert( Constants.INSTANCE.ThatColumnNameIsAlreadyInUsePleasePickAnother() );
                return;
            }
            //Ensure variables reflect (name) changes made in RuleModeller
            getDefinedVariables( this.ruleModel );
            doInsertColumn();

        } else {
            if ( !originalCol.getHeader().equals( editingCol.getHeader() ) ) {
                if ( !isHeaderUnique( editingCol.getHeader() ) ) {
                    Window.alert( Constants.INSTANCE.ThatColumnNameIsAlreadyInUsePleasePickAnother() );
                    return;
                }
            }
            //Ensure variables reflect (name) changes made in RuleModeller
            getDefinedVariables( this.ruleModel );
            doUpdateColumn();
        }

        hide();
    }

    //Fired when a Template Key is added or removed
    public void onTemplateVariablesChanged( TemplateVariablesChangedEvent event ) {
        if ( event.getSource() == this.ruleModel ) {
            getDefinedVariables( event.getModel() );
        }
    }

    //Extract Template Keys from RuleModel
    private boolean getDefinedVariables( RuleModel ruleModel ) {
        Map<InterpolationVariable, Integer> ivs = new HashMap<InterpolationVariable, Integer>();
        RuleModelVisitor rmv = new RuleModelVisitor( ivs );
        rmv.visit( ruleModel );

        //Update column and UI
        editingCol.setChildColumns( convertInterpolationVariables( ivs ) );

        return ivs.size() > 0;
    }

}
