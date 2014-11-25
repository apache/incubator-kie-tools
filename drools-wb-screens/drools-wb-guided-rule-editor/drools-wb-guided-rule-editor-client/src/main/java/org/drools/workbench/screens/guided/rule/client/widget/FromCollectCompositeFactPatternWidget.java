/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.rule.client.widget;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.FromAccumulateCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCollectCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromEntryPointFactPattern;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.HumanReadable;
import org.kie.workbench.common.widgets.client.resources.i18n.HumanReadableConstants;
import org.uberfire.ext.widgets.common.client.common.ClickableLabel;
import com.google.gwt.user.client.ui.FlexTable;
import org.uberfire.ext.widgets.common.client.common.popups.FormStylePopup;

public class FromCollectCompositeFactPatternWidget extends FromCompositeFactPatternWidget {

    private Map<String, String> extraLeftSidePatternFactTypes = null;

    public FromCollectCompositeFactPatternWidget( RuleModeller modeller,
                                                  EventBus eventBus,
                                                  FromCollectCompositeFactPattern pattern ) {
        super( modeller,
               eventBus,
               pattern );
    }

    public FromCollectCompositeFactPatternWidget( RuleModeller modeller,
                                                  EventBus eventBus,
                                                  FromCollectCompositeFactPattern pattern,
                                                  Boolean readOnly ) {
        super( modeller,
               eventBus,
               pattern,
               readOnly );
    }

    private void initExtraLeftSidePatternFactTypes() {
        extraLeftSidePatternFactTypes = new HashMap<String, String>();
        if ( modelImportsClass( "java.util.Collection" ) ) {
            extraLeftSidePatternFactTypes.put( "Collection",
                                               "Collection" );
        }
        if ( modelImportsClass( "java.util.List" ) ) {
            extraLeftSidePatternFactTypes.put( "List",
                                               "List" );
        }
        if ( modelImportsClass( "java.util.Set" ) ) {
            extraLeftSidePatternFactTypes.put( "Set",
                                               "Set" );
        }
    }

    private boolean modelImportsClass( final String fullyQualifiedClassName ) {
        for ( Import i : modeller.getModel().getImports().getImports() ) {
            if ( i.getType().equals( fullyQualifiedClassName ) ) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected Widget getCompositeLabel() {
        ClickHandler leftPatternclick = new ClickHandler() {

            public void onClick( ClickEvent event ) {
                Widget w = (Widget) event.getSource();
                showFactTypeSelector( w );

            }
        };
        ClickHandler rightPatternclick = new ClickHandler() {

            public void onClick( ClickEvent event ) {
                Widget w = (Widget) event.getSource();
                showRightPatternSelector( w );
            }
        };

        String lbl = "<div class='form-field'>" + HumanReadable.getCEDisplayName( "from collect" ) + "</div>";

        FlexTable panel = new FlexTable();

        int r = 0;

        if ( pattern.getFactPattern() == null ) {
            panel.setWidget( r++,
                             0,
                             new ClickableLabel( "<br> <font color='red'>" + GuidedRuleEditorResources.CONSTANTS.clickToAddPatterns() + "</font>",
                                                 leftPatternclick,
                                                 !this.readOnly ) );
        }

        panel.setWidget( r++,
                         0,
                         new HTML( lbl ) );

        if ( this.getFromCollectPattern().getRightPattern() == null ) {
            panel.setWidget( r++,
                             0,
                             new ClickableLabel( "<br> <font color='red'>" + GuidedRuleEditorResources.CONSTANTS.clickToAddPatterns() + "</font>",
                                                 rightPatternclick,
                                                 !this.readOnly ) );
        } else {
            IPattern rPattern = this.getFromCollectPattern().getRightPattern();

            RuleModellerWidget patternWidget = null;
            if ( rPattern instanceof FactPattern ) {
                patternWidget = new FactPatternWidget( this.getModeller(),
                                                       this.getEventBus(),
                                                       rPattern,
                                                       true,
                                                       true,
                                                       this.readOnly );

            } else if ( rPattern instanceof FromAccumulateCompositeFactPattern ) {
                patternWidget = new FromAccumulateCompositeFactPatternWidget( this.getModeller(),
                                                                              this.getEventBus(),
                                                                              (FromAccumulateCompositeFactPattern) rPattern,
                                                                              this.readOnly );

            } else if ( rPattern instanceof FromCollectCompositeFactPattern ) {
                patternWidget = new FromCollectCompositeFactPatternWidget( this.getModeller(),
                                                                           this.getEventBus(),
                                                                           (FromCollectCompositeFactPattern) rPattern,
                                                                           this.readOnly );

            } else if ( rPattern instanceof FromEntryPointFactPattern ) {
                patternWidget = new FromEntryPointFactPatternWidget( this.getModeller(),
                                                                     this.getEventBus(),
                                                                     (FromEntryPointFactPattern) rPattern,
                                                                     this.readOnly );

            } else if ( rPattern instanceof FromCompositeFactPattern ) {
                patternWidget = new FromCompositeFactPatternWidget( this.getModeller(),
                                                                    this.getEventBus(),
                                                                    (FromCompositeFactPattern) rPattern,
                                                                    this.readOnly );

            } else if ( rPattern instanceof FreeFormLine ) {
                patternWidget = new FreeFormLineWidget( this.getModeller(),
                                                        this.getEventBus(),
                                                        (FreeFormLine) rPattern,
                                                        this.readOnly );
            } else {
                throw new IllegalArgumentException( "Unsupported pattern " + rPattern + " for right side of FROM COLLECT" );
            }

            patternWidget.addOnModifiedCommand( new Command() {
                public void execute() {
                    setModified( true );
                }
            } );

            panel.setWidget( r++,
                             0,
                             addRemoveButton( patternWidget,
                                              new ClickHandler() {

                                                  public void onClick( ClickEvent event ) {
                                                      if ( Window.confirm( GuidedRuleEditorResources.CONSTANTS.RemoveThisBlockOfData() ) ) {
                                                          setModified( true );
                                                          getFromCollectPattern().setRightPattern( null );
                                                          getModeller().refreshWidget();
                                                      }
                                                  }
                                              } ) );
        }

        return panel;
    }

    @Override
    protected void showFactTypeSelector( final Widget w ) {

        final FormStylePopup popup = new FormStylePopup( GuidedRuleEditorResources.CONSTANTS.NewFactPattern() );
        popup.setTitle( GuidedRuleEditorResources.CONSTANTS.NewFactPattern() );

        final ListBox box = new ListBox();

        box.addItem( GuidedRuleEditorResources.CONSTANTS.Choose() );

        for ( Map.Entry<String, String> entry : this.getExtraLeftSidePatternFactTypes().entrySet() ) {
            box.addItem( entry.getKey(),
                         entry.getValue() );
        }

        //TODO: Add Facts that extends Collection
        //        box.addItem("...");
        //        box.addItem("...");

        box.setSelectedIndex( 0 );
        box.addChangeHandler( new ChangeHandler() {
            public void onChange( ChangeEvent event ) {
                pattern.setFactPattern( new FactPattern( box.getValue( box.getSelectedIndex() ) ) );
                setModified( true );
                getModeller().refreshWidget();
                popup.hide();
            }
        } );

        popup.addAttribute( GuidedRuleEditorResources.CONSTANTS.chooseFactType(),
                            box );

        popup.show();
    }

    /**
     * Pops up the fact selector.
     */
    protected void showRightPatternSelector( final Widget w ) {
        final ListBox box = new ListBox();
        AsyncPackageDataModelOracle oracle = this.getModeller().getDataModelOracle();
        String[] facts = oracle.getFactTypes();

        box.addItem( GuidedRuleEditorResources.CONSTANTS.Choose() );
        for ( int i = 0; i < facts.length; i++ ) {
            box.addItem( facts[ i ] );
        }
        box.setSelectedIndex( 0 );

        final FormStylePopup popup = new FormStylePopup( GuidedRuleEditorResources.CONSTANTS.NewFactPattern() );
        popup.addAttribute( GuidedRuleEditorResources.CONSTANTS.chooseFactType(),
                            box );
        box.addChangeHandler( new ChangeHandler() {

            public void onChange( ChangeEvent event ) {
                getFromCollectPattern().setRightPattern( new FactPattern( box.getItemText( box.getSelectedIndex() ) ) );
                setModified( true );
                getModeller().refreshWidget();
                popup.hide();

            }
        } );

        final Button freeFormDRLBtn = new Button( GuidedRuleEditorResources.CONSTANTS.FreeFormDrl() );
        final Button fromBtn = new Button( HumanReadableConstants.INSTANCE.From() );
        final Button fromAccumulateBtn = new Button( HumanReadableConstants.INSTANCE.FromAccumulate() );
        final Button fromCollectBtn = new Button( HumanReadableConstants.INSTANCE.FromCollect() );
        final Button fromEntryPointBtn = new Button( HumanReadableConstants.INSTANCE.FromEntryPoint() );

        ClickHandler btnsClickHandler = new ClickHandler() {

            public void onClick( ClickEvent event ) {
                Widget sender = (Widget) event.getSource();
                if ( sender == fromBtn ) {
                    getFromCollectPattern().setRightPattern( new FromCompositeFactPattern() );
                } else if ( sender == fromAccumulateBtn ) {
                    getFromCollectPattern().setRightPattern( new FromAccumulateCompositeFactPattern() );
                } else if ( sender == fromCollectBtn ) {
                    getFromCollectPattern().setRightPattern( new FromCollectCompositeFactPattern() );
                } else if ( sender == freeFormDRLBtn ) {
                    getFromCollectPattern().setRightPattern( new FreeFormLine() );
                } else if ( sender == fromEntryPointBtn ) {
                    getFromCollectPattern().setRightPattern( new FromEntryPointFactPattern() );
                } else {
                    throw new IllegalArgumentException( "Unknown sender: " + sender );
                }
                setModified( true );
                getModeller().refreshWidget();
                popup.hide();

            }
        };

        freeFormDRLBtn.addClickHandler( btnsClickHandler );
        fromBtn.addClickHandler( btnsClickHandler );
        fromAccumulateBtn.addClickHandler( btnsClickHandler );
        fromCollectBtn.addClickHandler( btnsClickHandler );
        fromEntryPointBtn.addClickHandler( btnsClickHandler );

        popup.addAttribute( "",
                            freeFormDRLBtn );
        popup.addAttribute( "",
                            fromBtn );
        popup.addAttribute( "",
                            fromAccumulateBtn );
        popup.addAttribute( "",
                            fromCollectBtn );
        popup.addAttribute( "",
                            fromEntryPointBtn );

        popup.show();
    }

    private FromCollectCompositeFactPattern getFromCollectPattern() {
        return (FromCollectCompositeFactPattern) this.pattern;
    }

    @Override
    protected void calculateReadOnly() {
        if ( this.pattern.getFactPattern() != null ) {
            String factType = this.pattern.getFactPattern().getFactType();

            // We allow the use of Set, List or Collection, even when they are not added as imports
            // Because of this, we also need to add them as known fact types
            if ( getExtraLeftSidePatternFactTypes().values().contains( factType ) ) {
                this.isFactTypeKnown = true;
            } else {
                this.isFactTypeKnown = this.getModeller().getDataModelOracle().isFactTypeRecognized( factType );
            }

            if ( this.pattern.getFactPattern() != null ) {
                this.readOnly = !( this.getExtraLeftSidePatternFactTypes().containsValue( this.pattern.getFactPattern().getFactType() )
                        || this.getModeller().getDataModelOracle().isFactTypeRecognized( this.pattern.getFactPattern().getFactType() ) );
            }
        }
    }

    private Map<String, String> getExtraLeftSidePatternFactTypes() {
        if ( this.extraLeftSidePatternFactTypes == null ) {
            this.initExtraLeftSidePatternFactTypes();
        }
        return this.extraLeftSidePatternFactTypes;
    }
}
