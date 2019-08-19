/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.rule.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.RuleMetadata;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.screens.guided.rule.client.editor.events.TemplateVariablesChangedEvent;
import org.drools.workbench.screens.guided.rule.client.editor.plugin.RuleModellerActionPlugin;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.drools.workbench.screens.guided.rule.client.resources.images.GuidedRuleEditorImages508;
import org.drools.workbench.screens.guided.rule.client.widget.FactTypeKnownValueChangeEvent;
import org.drools.workbench.screens.guided.rule.client.widget.FactTypeKnownValueChangeHandler;
import org.drools.workbench.screens.guided.rule.client.widget.RuleModellerWidget;
import org.drools.workbench.screens.guided.rule.client.widget.attribute.AddAttributeWidget;
import org.drools.workbench.screens.guided.rule.client.widget.attribute.RuleAttributeWidget;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.CommonAltedImages;
import org.kie.workbench.common.widgets.client.ruleselector.RuleSelector;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.common.ClickableLabel;
import org.uberfire.ext.widgets.common.client.common.DirtyableHorizontalPane;
import org.uberfire.ext.widgets.common.client.common.DirtyableVerticalPane;
import org.uberfire.ext.widgets.common.client.common.SmallLabel;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

/**
 * This is the parent widget that contains the model based rule builder.
 */
public class RuleModeller extends Composite
        implements
        RuleModelEditor {

    private FlexTable layout;
    private RuleModel model;
    private Collection<RuleModellerActionPlugin> actionPlugins;
    private AsyncPackageDataModelOracle oracle;
    private RuleModellerConfiguration configuration;
    private Map<String, Object> serviceInvocationCache = new HashMap<>();

    private boolean showingOptions = false;
    private int currentLayoutRow = 0;
    private ModellerWidgetFactory widgetFactory;
    private EventBus eventBus;
    private boolean isReadOnly = false;
    private boolean isDSLEnabled = true;

    private List<RuleModellerWidget> lhsWidgets = new ArrayList<RuleModellerWidget>();
    private List<RuleModellerWidget> rhsWidgets = new ArrayList<RuleModellerWidget>();

    private boolean hasModifiedWidgets;

    private final Command onWidgetModifiedCommand = new Command() {

        public void execute() {
            hasModifiedWidgets = true;
        }
    };
    private final RuleSelector ruleSelector = GWT.create(RuleSelector.class);

    //used by Guided Rule (DRL + DSLR)
    public RuleModeller(final RuleModel model,
                        final Collection<RuleModellerActionPlugin> actionPlugins,
                        final AsyncPackageDataModelOracle oracle,
                        final ModellerWidgetFactory widgetFactory,
                        final EventBus eventBus,
                        final boolean isReadOnly,
                        final boolean isDSLEnabled) {
        this(model,
             actionPlugins,
             oracle,
             widgetFactory,
             RuleModellerConfiguration.getDefault(),
             eventBus,
             isReadOnly);
        this.isDSLEnabled = isDSLEnabled;
    }

    //used by Guided Templates
    public RuleModeller(final RuleModel model,
                        final AsyncPackageDataModelOracle oracle,
                        final ModellerWidgetFactory widgetFactory,
                        final EventBus eventBus,
                        final boolean isReadOnly) {
        this(model,
             Collections.emptyList(),
             oracle,
             widgetFactory,
             eventBus,
             isReadOnly,
             true);
    }

    //used by Guided Decision BRL Fragments
    public RuleModeller(final RuleModel model,
                        final Collection<RuleModellerActionPlugin> actionPlugins,
                        final AsyncPackageDataModelOracle oracle,
                        final ModellerWidgetFactory widgetFactory,
                        final RuleModellerConfiguration configuration,
                        final EventBus eventBus,
                        final boolean isReadOnly) {
        this.model = model;
        this.actionPlugins = actionPlugins;
        this.oracle = oracle;
        this.widgetFactory = widgetFactory;
        this.configuration = configuration;
        this.eventBus = eventBus;
        this.isReadOnly = isReadOnly;

        doLayout();
    }

    public void setRuleNamesForPackage(final Collection<String> ruleNames) {
        ruleSelector.setRuleNames(ruleNames,
                                  model.name);
    }

    protected void doLayout() {
        layout = new FlexTable();
        initWidget();
        layout.setStyleName("model-builder-Background");
        initWidget(layout);
        setWidth("100%");
        setHeight("100%");
    }

    /**
     * This updates the widget to reflect the state of the model.
     */
    public void initWidget() {
        layout.removeAllRows();
        currentLayoutRow = 0;

        Image addPattern = GuidedRuleEditorImages508.INSTANCE.NewItem();
        addPattern.setTitle(GuidedRuleEditorResources.CONSTANTS.AddAConditionToThisRule());
        addPattern.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                showConditionSelector(null);
            }
        });

        layout.getColumnFormatter().setWidth(0,
                                             "20px");
        layout.getColumnFormatter().setWidth(1,
                                             "20px");
        layout.getColumnFormatter().setWidth(2,
                                             "48px");
        layout.getColumnFormatter().setWidth(4,
                                             "64px");

        if (this.showExtendedRuleDropdown()) {
            addExtendedRuleDropdown();
        }

        if (this.showLHS()) {
            layout.setWidget(currentLayoutRow,
                             0,
                             new SmallLabel("<b>" + GuidedRuleEditorResources.CONSTANTS.WHEN() + "</b>"));
            layout.getFlexCellFormatter().setColSpan(currentLayoutRow,
                                                     0,
                                                     4);

            if (!lockLHS()) {
                layout.setWidget(currentLayoutRow,
                                 1,
                                 addPattern);
            }
            currentLayoutRow++;

            renderLhs(this.model);
        }

        if (this.showRHS()) {
            layout.setWidget(currentLayoutRow,
                             0,
                             new SmallLabel("<b>" + GuidedRuleEditorResources.CONSTANTS.THEN() + "</b>"));
            layout.getFlexCellFormatter().setColSpan(currentLayoutRow,
                                                     0,
                                                     4);

            Image addAction = GuidedRuleEditorImages508.INSTANCE.NewItem();
            addAction.setTitle(GuidedRuleEditorResources.CONSTANTS.AddAnActionToThisRule());
            addAction.addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent event) {
                    showActionSelector(null);
                }
            });
            if (!lockRHS()) {
                layout.setWidget(currentLayoutRow,
                                 1,
                                 addAction);
            }
            currentLayoutRow++;

            renderRhs(this.model);
        }

        if (showAttributes()) {

            final int optionsRowIndex = currentLayoutRow;
            if (!this.showingOptions) {
                ClickableLabel showMoreOptions = new ClickableLabel(GuidedRuleEditorResources.CONSTANTS.ShowOptions(),
                                                                    new ClickHandler() {

                                                                        public void onClick(ClickEvent event) {
                                                                            showingOptions = true;
                                                                            renderOptions(optionsRowIndex);
                                                                        }
                                                                    });
                layout.setWidget(optionsRowIndex,
                                 2,
                                 showMoreOptions);
            } else {
                renderOptions(optionsRowIndex);
            }
        }

        currentLayoutRow++;
        layout.setWidget(currentLayoutRow + 1,
                         3,
                         spacerWidget());
        layout.getCellFormatter().setHeight(currentLayoutRow + 1,
                                            3,
                                            "100%");
    }

    private void addExtendedRuleDropdown() {
        layout.setWidget(currentLayoutRow,
                         0,
                         new SmallLabel("<b>" + GuidedRuleEditorResources.CONSTANTS.EXTENDS() + "</b>"));

        ruleSelector.setRuleName(model.parentName);
        ruleSelector.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                model.parentName = event.getValue();
            }
        });

        layout.setWidget(currentLayoutRow,
                         3,
                         ruleSelector);
        currentLayoutRow++;
    }

    private void renderOptions(final int optionsRowIndex) {
        layout.setWidget(optionsRowIndex,
                         2,
                         new SmallLabel(GuidedRuleEditorResources.CONSTANTS.optionsRuleModeller()));
        if (!isReadOnly) {
            layout.setWidget(optionsRowIndex,
                             4,
                             getAddAttribute());
        }
        layout.setWidget(optionsRowIndex + 1,
                         3,
                         new RuleAttributeWidget(this,
                                                 this.model,
                                                 isReadOnly));
    }

    private boolean isLock(String attr) {

        if (isReadOnly()) {
            return true;
        }

        if (this.model.metadataList.length == 0) {
            return false;
        }

        for (RuleMetadata at : this.model.metadataList) {
            if (at.getAttributeName().equals(attr)) {
                return true;
            }
        }
        return false;
    }

    public boolean showRHS() {
        return !this.configuration.isHideRHS();
    }

    /**
     * return true if we should not allow unfrozen editing of the RHS
     */
    public boolean lockRHS() {
        return isLock(RuleAttributeWidget.LOCK_RHS);
    }

    public boolean showLHS() {
        return !this.configuration.isHideLHS();
    }

    /**
     * return true if we should not allow unfrozen editing of the LHS
     */
    public boolean lockLHS() {
        return isLock(RuleAttributeWidget.LOCK_LHS);
    }

    private boolean showAttributes() {
        return !this.configuration.isHideAttributes();
    }

    private boolean showExtendedRuleDropdown() {
        return !this.configuration.isHideExtendedRuleDropdown();
    }

    public void refreshWidget() {
        initWidget();
    }

    private Widget getAddAttribute() {
        final AddAttributeWidget addAttributeWidget = new AddAttributeWidget();
        addAttributeWidget.init(this);
        return addAttributeWidget.asWidget();
    }

    /**
     * Do all the widgets for the RHS.
     */
    private void renderRhs(final RuleModel model) {

        for (int i = 0; i < model.rhs.length; i++) {
            DirtyableVerticalPane widget = new DirtyableVerticalPane();
            widget.setWidth("100%");

            IAction action = model.rhs[i];

            //if lockRHS() set the widget RO, otherwise let them decide.
            Boolean readOnly = this.lockRHS() ? true : null;

            RuleModellerWidget w = getWidgetFactory().getWidget(this,
                                                                eventBus,
                                                                action,
                                                                readOnly);
            w.addOnModifiedCommand(this.onWidgetModifiedCommand);

            widget.add(wrapRHSWidget(model,
                                     i,
                                     w));
            widget.add(spacerWidget());

            layout.setWidget(currentLayoutRow,
                             0,
                             new DirtyableHorizontalPane());
            layout.setWidget(currentLayoutRow,
                             1,
                             new DirtyableHorizontalPane());

            layout.setWidget(currentLayoutRow,
                             2,
                             this.wrapLineNumber(i + 1,
                                                 false));
            layout.getFlexCellFormatter().setHorizontalAlignment(currentLayoutRow,
                                                                 2,
                                                                 HasHorizontalAlignment.ALIGN_CENTER);
            layout.getFlexCellFormatter().setVerticalAlignment(currentLayoutRow,
                                                               2,
                                                               HasVerticalAlignment.ALIGN_MIDDLE);

            layout.setWidget(currentLayoutRow,
                             3,
                             widget);
            layout.getFlexCellFormatter().setHorizontalAlignment(currentLayoutRow,
                                                                 3,
                                                                 HasHorizontalAlignment.ALIGN_LEFT);
            layout.getFlexCellFormatter().setVerticalAlignment(currentLayoutRow,
                                                               3,
                                                               HasVerticalAlignment.ALIGN_TOP);
            layout.getFlexCellFormatter().setWidth(currentLayoutRow,
                                                   3,
                                                   "100%");

            layout.getRowFormatter().addStyleName(currentLayoutRow,
                                                  (i % 2 == 0 ? GuidedRuleEditorResources.INSTANCE.css().evenRow() : GuidedRuleEditorResources.INSTANCE.css().oddRow()));

            if (!w.isFactTypeKnown()) {
                addInvalidPatternIcon();
                addFactTypeKnownValueChangeHandler(w,
                                                   currentLayoutRow);
            }

            final int index = i;
            if (!(this.lockRHS() || w.isReadOnly())) {
                this.addActionsButtonsToLayout(GuidedRuleEditorResources.CONSTANTS.AddAnActionBelow(),
                                               new ClickHandler() {

                                                   public void onClick(ClickEvent event) {
                                                       showActionSelector(index + 1);
                                                   }
                                               },
                                               new ClickHandler() {

                                                   public void onClick(ClickEvent event) {
                                                       model.moveRhsItemDown(index);
                                                       refreshWidget();
                                                   }
                                               },
                                               new ClickHandler() {

                                                   public void onClick(ClickEvent event) {
                                                       model.moveRhsItemUp(index);
                                                       refreshWidget();
                                                   }
                                               }
                );
            }

            this.rhsWidgets.add(w);
            currentLayoutRow++;
        }
    }

    /**
     * Pops up the fact selector.
     */
    protected void showConditionSelector(Integer position) {
        RuleModellerConditionSelectorPopup popup = new RuleModellerConditionSelectorPopup(model,
                                                                                          this,
                                                                                          position,
                                                                                          getDataModelOracle());
        popup.show();
    }

    protected void showActionSelector(final Integer position) {
        final RuleModellerActionSelectorPopup popup = ruleModellerActionSelectorPopup(position, actionPlugins);
        popup.show();
    }

    protected RuleModellerActionSelectorPopup ruleModellerActionSelectorPopup(final Integer position,
                                                                              final Collection<RuleModellerActionPlugin> actionPlugins) {
        return new RuleModellerActionSelectorPopup(model,
                                                   this,
                                                   actionPlugins,
                                                   position,
                                                   getDataModelOracle());
    }

    /**
     * Builds all the condition widgets.
     */
    private void renderLhs(final RuleModel model) {

        for (int i = 0; i < model.lhs.length; i++) {
            DirtyableVerticalPane vert = new DirtyableVerticalPane();
            vert.setWidth("100%");

            //if lockLHS() set the widget RO, otherwise let them decide.
            Boolean readOnly = this.lockLHS() ? true : null;

            IPattern pattern = model.lhs[i];

            final RuleModellerWidget widget = getWidgetFactory().getWidget(this,
                                                                           eventBus,
                                                                           pattern,
                                                                           readOnly);
            widget.addOnModifiedCommand(this.onWidgetModifiedCommand);

            vert.add(wrapLHSWidget(model,
                                   i,
                                   widget));
            vert.add(spacerWidget());

            layout.setWidget(currentLayoutRow,
                             0,
                             new DirtyableHorizontalPane());
            layout.setWidget(currentLayoutRow,
                             1,
                             new DirtyableHorizontalPane());

            layout.setWidget(currentLayoutRow,
                             2,
                             this.wrapLineNumber(i + 1,
                                                 true));
            layout.getFlexCellFormatter().setHorizontalAlignment(currentLayoutRow,
                                                                 2,
                                                                 HasHorizontalAlignment.ALIGN_CENTER);
            layout.getFlexCellFormatter().setVerticalAlignment(currentLayoutRow,
                                                               2,
                                                               HasVerticalAlignment.ALIGN_MIDDLE);

            layout.setWidget(currentLayoutRow,
                             3,
                             vert);
            layout.getFlexCellFormatter().setHorizontalAlignment(currentLayoutRow,
                                                                 3,
                                                                 HasHorizontalAlignment.ALIGN_LEFT);
            layout.getFlexCellFormatter().setVerticalAlignment(currentLayoutRow,
                                                               3,
                                                               HasVerticalAlignment.ALIGN_TOP);
            layout.getFlexCellFormatter().setWidth(currentLayoutRow,
                                                   3,
                                                   "100%");

            layout.getRowFormatter().addStyleName(currentLayoutRow,
                                                  (i % 2 == 0 ? GuidedRuleEditorResources.INSTANCE.css().evenRow() : GuidedRuleEditorResources.INSTANCE.css().oddRow()));

            if (!widget.isFactTypeKnown()) {
                addInvalidPatternIcon();
                addFactTypeKnownValueChangeHandler(widget,
                                                   currentLayoutRow);
            }

            final int index = i;
            if (!(this.lockLHS() || widget.isReadOnly())) {
                this.addActionsButtonsToLayout(GuidedRuleEditorResources.CONSTANTS.AddAConditionBelow(),
                                               new ClickHandler() {

                                                   public void onClick(ClickEvent event) {
                                                       showConditionSelector(index + 1);
                                                   }
                                               },
                                               new ClickHandler() {

                                                   public void onClick(ClickEvent event) {
                                                       model.moveLhsItemDown(index);
                                                       refreshWidget();
                                                   }
                                               },
                                               new ClickHandler() {

                                                   public void onClick(ClickEvent event) {
                                                       model.moveLhsItemUp(index);
                                                       refreshWidget();
                                                   }
                                               }
                );
            }

            this.lhsWidgets.add(widget);
            currentLayoutRow++;
        }
    }

    private void addFactTypeKnownValueChangeHandler(final RuleModellerWidget widget,
                                                    final int layoutRow) {
        widget.addFactTypeKnownValueChangeHandler(new FactTypeKnownValueChangeHandler() {
            @Override
            public void onValueChanged(FactTypeKnownValueChangeEvent factTypeKnownValueChangeEvent) {
                if (!widget.isFactTypeKnown()) {
                    addInvalidPatternIcon();
                } else {
                    clearLineIcons(layoutRow,
                                   0);
                }
            }
        });
    }

    private void addInvalidPatternIcon() {
        final Image image = GuidedRuleEditorImages508.INSTANCE.Error();
        image.setTitle(GuidedRuleEditorResources.CONSTANTS.InvalidPatternSectionDisabled());
        this.addLineIcon(currentLayoutRow,
                         0,
                         image);
    }

    private HTML spacerWidget() {
        HTML h = new HTML("&nbsp;"); //NON-NLS
        h.setHeight("2px"); //NON-NLS
        return h;
    }

    private Widget wrapLineNumber(int number,
                                  boolean isLHSLine) {
        String id = "rhsLine";
        if (isLHSLine) {
            id = "lhsLine";
        }
        id += number;
        DirtyableHorizontalPane horiz = new DirtyableHorizontalPane();
        horiz.add(new HTML("<div class='form-field' id='" + id + "'>" + number + ".</div>"));
        return horiz;
    }

    /**
     * This adds the widget to the UI, also adding the remove icon.
     */
    private Widget wrapLHSWidget(final RuleModel model,
                                 int i,
                                 RuleModellerWidget w) {
        final FlexTable wrapper = new FlexTable();
        final Image remove = GuidedRuleEditorImages508.INSTANCE.DeleteItemSmall();
        remove.setTitle(GuidedRuleEditorResources.CONSTANTS.RemoveThisENTIREConditionAndAllTheFieldConstraintsThatBelongToIt());
        final int idx = i;
        remove.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                if (Window.confirm(GuidedRuleEditorResources.CONSTANTS.RemoveThisEntireConditionQ())) {
                    if (model.removeLhsItem(idx)) {
                        refreshWidget();

                        //Signal possible change in Template variables
                        TemplateVariablesChangedEvent tvce = new TemplateVariablesChangedEvent(model);
                        eventBus.fireEventFromSource(tvce,
                                                     model);
                    } else {
                        ErrorPopup.showMessage(GuidedRuleEditorResources.CONSTANTS.CanTRemoveThatItemAsItIsUsedInTheActionPartOfTheRule());
                    }
                }
            }
        });

        wrapper.getColumnFormatter().setWidth(0,
                                              "100%");
        w.setWidth("100%");
        wrapper.setWidget(0,
                          0,
                          w);
        if (!(this.lockLHS() || w.isReadOnly()) || !w.isFactTypeKnown()) {
            wrapper.setWidget(0,
                              1,
                              remove);
            wrapper.getColumnFormatter().setWidth(1,
                                                  "20px");
        }

        return wrapper;
    }

    /**
     * This adds the widget to the UI, also adding the remove icon.
     */
    private Widget wrapRHSWidget(final RuleModel model,
                                 int i,
                                 RuleModellerWidget w) {
        final FlexTable wrapper = new FlexTable();
        final Image remove = GuidedRuleEditorImages508.INSTANCE.DeleteItemSmall();
        remove.setTitle(GuidedRuleEditorResources.CONSTANTS.RemoveThisAction());
        final int idx = i;
        remove.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                if (Window.confirm(GuidedRuleEditorResources.CONSTANTS.RemoveThisItem())) {
                    model.removeRhsItem(idx);
                    refreshWidget();

                    //Signal possible change in Template variables
                    TemplateVariablesChangedEvent tvce = new TemplateVariablesChangedEvent(model);
                    eventBus.fireEventFromSource(tvce,
                                                 model);
                }
            }
        });

        //        if ( !(w instanceof ActionRetractFactWidget) ) {
        //            w.setWidth( "100%" );
        //            horiz.setWidth( "100%" );
        //        }

        wrapper.getColumnFormatter().setWidth(0,
                                              "100%");
        w.setWidth("100%");
        wrapper.setWidget(0,
                          0,
                          w);

        if (!(this.lockRHS() || w.isReadOnly()) || !w.isFactTypeKnown()) {
            wrapper.setWidget(0,
                              1,
                              remove);
            wrapper.getColumnFormatter().setWidth(1,
                                                  "20px");
        }

        return wrapper;
    }

    private void addLineIcon(int row,
                             int col,
                             Image icon) {
        Widget widget = layout.getWidget(row,
                                         col);
        if (widget instanceof DirtyableHorizontalPane) {
            DirtyableHorizontalPane horiz = (DirtyableHorizontalPane) widget;
            horiz.add(icon);
        }
    }

    private void clearLineIcons(int row,
                                int col) {
        if (layout.getCellCount(row) <= col) {
            return;
        }
        Widget widget = layout.getWidget(row,
                                         col);
        if (widget instanceof DirtyableHorizontalPane) {
            DirtyableHorizontalPane horiz = (DirtyableHorizontalPane) widget;
            horiz.clear();
        }
    }

    private void clearLinesIcons(int col) {
        for (int i = 0; i < layout.getRowCount(); i++) {
            this.clearLineIcons(i,
                                col);
        }
    }

    private void addActionsButtonsToLayout(String title,
                                           ClickHandler addBelowListener,
                                           ClickHandler moveDownListener,
                                           ClickHandler moveUpListener) {

        final DirtyableHorizontalPane hp = new DirtyableHorizontalPane();

        Image addPattern = CommonAltedImages.INSTANCE.NewItemBelow();
        addPattern.setTitle(title);
        addPattern.addClickHandler(addBelowListener);

        hp.add(addPattern);
        hp.add(new MoveDownButton(moveDownListener));
        hp.add(new MoveUpButton(moveUpListener));

        layout.setWidget(currentLayoutRow,
                         4,
                         hp);
        layout.getFlexCellFormatter().setHorizontalAlignment(currentLayoutRow,
                                                             4,
                                                             HasHorizontalAlignment.ALIGN_CENTER);
        layout.getFlexCellFormatter().setVerticalAlignment(currentLayoutRow,
                                                           4,
                                                           HasVerticalAlignment.ALIGN_MIDDLE);
    }

    public RuleModel getModel() {
        return model;
    }

    public Collection<RuleModellerActionPlugin> getActionPlugins() {
        return actionPlugins;
    }

    /**
     * Returns true is a var name has already been used either by the rule, or
     * as a global.
     */
    public boolean isVariableNameUsed(String name) {
        return model.isVariableNameUsed(name) || getDataModelOracle().isGlobalVariable(name);
    }

    public AsyncPackageDataModelOracle getDataModelOracle() {
        return oracle;
    }

    public ModellerWidgetFactory getWidgetFactory() {
        return widgetFactory;
    }

    public RuleModeller getRuleModeller() {
        return this;
    }

    public Map<String, Object> getServiceInvocationCache() {
        return serviceInvocationCache;
    }

    public boolean isTemplate() {
        return widgetFactory.isTemplate();
    }

    public Path getPath() {
        return oracle.getResourcePath();
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public boolean isDSLEnabled() {
        return isDSLEnabled;
    }
}
