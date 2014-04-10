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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.event.HideEvent;
import com.github.gwtbootstrap.client.ui.event.HideHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.rule.InterpolationVariable;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.RuleModelVisitor;
import org.drools.workbench.models.guided.dtable.shared.model.BRLColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.rule.client.editor.ModellerWidgetFactory;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModelEditor;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModellerConfiguration;
import org.drools.workbench.screens.guided.rule.client.editor.events.TemplateVariablesChangedEvent;
import org.drools.workbench.screens.guided.template.client.editor.TemplateModellerWidgetFactory;
import org.kie.workbench.common.services.shared.rulename.RuleNamesService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.common.popups.footers.ModalFooterOKCancelButtons;

/**
 * An editor for BRL Column definitions
 */
public abstract class AbstractBRLColumnViewImpl<T, C extends BaseColumn> extends Modal
        implements
        RuleModelEditor,
        TemplateVariablesChangedEvent.Handler {

    protected int MIN_WIDTH = 500;

    @UiField(provided = true)
    RuleModeller ruleModeller;

    @UiField
    TextBox txtColumnHeader;

    @UiField
    CheckBox chkHideColumn;

    @UiField
    ScrollPanel brlEditorContainer;

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
                                      final GuidedDecisionTable52 model,
                                      final AsyncPackageDataModelOracle oracle,
                                      final Caller<RuleNamesService> ruleNameService,
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

        final ModellerWidgetFactory widgetFactory = new TemplateModellerWidgetFactory();

        this.ruleModeller = new RuleModeller( path,
                                              ruleModel,
                                              oracle,
                                              widgetFactory,
                                              getRuleModellerConfiguration(),
                                              eventBus,
                                              isReadOnly ) {
            @Override
            public void refreshWidget() {
                super.refreshWidget();
                centerVertically( AbstractBRLColumnViewImpl.this.getElement() );
            }
        };

        add( uiBinder.createAndBindUi( this ) );
        add( new ModalFooterOKCancelButtons( new Command() {
            @Override
            public void execute() {
                applyChanges();
            }
        }, new Command() {
            @Override
            public void execute() {
                hide();
            }
        }
        ) );
        setWidth( getPopupWidth() );

        ruleNameService.call( new RemoteCallback<Collection<String>>() {
            @Override
            public void callback( Collection<String> ruleNames ) {
                ruleModeller.setRuleNamesForPackage( ruleNames );
            }
        } ).getRuleNames(path, model.getPackageName());

        this.brlEditorContainer.setHeight( "100%" );
        this.brlEditorContainer.setWidth( "100%" );
        this.txtColumnHeader.setText( editingCol.getHeader() );
        this.txtColumnHeader.setEnabled( !isReadOnly );
        this.chkHideColumn.setValue( editingCol.isHideColumn() );
    }

    private native void centerVertically( Element e ) /*-{
        $wnd.jQuery(e).css("margin-top", (-1 * $wnd.jQuery(e).outerHeight() / 2) + "px");
    }-*/;

    @Override
    public void show() {
        //Hook-up events
        final HandlerRegistration registration = eventBus.addHandler( TemplateVariablesChangedEvent.TYPE,
                                                                      this );

        //Release event handlers when closed
        addHideHandler( new HideHandler() {
            @Override
            public void onHide( final HideEvent hideEvent ) {
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

    /**
     * Width of pop-up, 50% of the client width or MIN_WIDTH
     * @return
     */
    private int getPopupWidth() {
        int w = (int) ( Window.getClientWidth() * 0.5 );
        if ( w < MIN_WIDTH ) {
            w = MIN_WIDTH;
        }
        return w;
    }

    @UiHandler("txtColumnHeader")
    void columnHanderChangeHandler( ChangeEvent event ) {
        editingCol.setHeader( txtColumnHeader.getText() );
    }

    @UiHandler("chkHideColumn")
    void hideColumnClickHandler( ClickEvent event ) {
        editingCol.setHideColumn( chkHideColumn.getValue() );
    }

    private void applyChanges() {

        //Validation
        if ( null == editingCol.getHeader() || "".equals( editingCol.getHeader() ) ) {
            Window.alert( GuidedDecisionTableConstants.INSTANCE.YouMustEnterAColumnHeaderValueDescription() );
            return;
        }
        if ( isNew ) {
            if ( !isHeaderUnique( editingCol.getHeader() ) ) {
                Window.alert( GuidedDecisionTableConstants.INSTANCE.ThatColumnNameIsAlreadyInUsePleasePickAnother() );
                return;
            }
            //Ensure variables reflect (name) changes made in RuleModeller
            getDefinedVariables( this.ruleModel );
            doInsertColumn();

        } else {
            if ( !originalCol.getHeader().equals( editingCol.getHeader() ) ) {
                if ( !isHeaderUnique( editingCol.getHeader() ) ) {
                    Window.alert( GuidedDecisionTableConstants.INSTANCE.ThatColumnNameIsAlreadyInUsePleasePickAnother() );
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
