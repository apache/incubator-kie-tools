/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtree.client.widget.palette;

import javax.inject.Inject;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionInsertNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionRetractNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionUpdateNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ConstraintNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.TypeNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.ActionInsertNodeImpl;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.ActionRetractNodeImpl;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.ActionUpdateNodeImpl;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.ConstraintNodeImpl;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.TypeNodeImpl;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.StringValue;
import org.drools.workbench.screens.guided.dtree.client.resources.i18n.GuidedDecisionTreeConstants;
import org.drools.workbench.screens.guided.dtree.client.widget.factories.ActionInsertFactoryHelper;
import org.drools.workbench.screens.guided.dtree.client.widget.factories.ActionInsertNodeFactory;
import org.drools.workbench.screens.guided.dtree.client.widget.factories.ActionRetractFactoryHelper;
import org.drools.workbench.screens.guided.dtree.client.widget.factories.ActionRetractNodeFactory;
import org.drools.workbench.screens.guided.dtree.client.widget.factories.ActionUpdateFactoryHelper;
import org.drools.workbench.screens.guided.dtree.client.widget.factories.ActionUpdateNodeFactory;
import org.drools.workbench.screens.guided.dtree.client.widget.factories.ConstraintFactoryHelper;
import org.drools.workbench.screens.guided.dtree.client.widget.factories.ConstraintNodeFactory;
import org.drools.workbench.screens.guided.dtree.client.widget.factories.TypeFactoryHelper;
import org.drools.workbench.screens.guided.dtree.client.widget.factories.TypeNodeFactory;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.commons.validation.PortablePreconditions;

public class GuidedDecisionTreePalette extends Panel {

    @Inject
    private TypeNodeFactory typeNodeFactory;

    @Inject
    private ConstraintNodeFactory constraintNodeFactory;

    @Inject
    private ActionInsertNodeFactory actionInsertNodeFactory;

    @Inject
    private ActionUpdateNodeFactory actionUpdateNodeFactory;

    @Inject
    private ActionRetractNodeFactory actionRetractNodeFactory;

    @Inject
    private GuidedDecisionTreeStencilPaletteBuilder stencilBuilder;

    private AsyncPackageDataModelOracle oracle;

    protected void onAttach() {
        super.onAttach();
        this.setId( DOM.createUniqueId() );
    }

    public void setDataModelOracle( final AsyncPackageDataModelOracle oracle,
                                    final boolean isReadOnly ) {
        this.oracle = PortablePreconditions.checkNotNull( "oracle",
                                                          oracle );
        clear();

        //Add types and constraints
        for ( String className : oracle.getFactTypes() ) {
            add( makePanelGroup( className,
                                 isReadOnly ) );
        }

        //Add actions
        final GuidedDecisionTreePaletteGroup paletteGroup = new GuidedDecisionTreePaletteGroup();

        if ( oracle.getFactTypes().length > 0 ) {
            final String className = oracle.getFactTypes()[ 0 ];
            final ActionInsertNode an1 = new ActionInsertNodeImpl( className );
            paletteGroup.addStencil( actionInsertNodeFactory,
                                     stencilBuilder,
                                     new ActionInsertFactoryHelper( an1,
                                                                    isReadOnly ),
                                     isReadOnly );
        }

        final ActionUpdateNode an2 = new ActionUpdateNodeImpl();
        paletteGroup.addStencil( actionUpdateNodeFactory,
                                 stencilBuilder,
                                 new ActionUpdateFactoryHelper( an2,
                                                                isReadOnly ),
                                 isReadOnly );

        final ActionRetractNode an3 = new ActionRetractNodeImpl();
        paletteGroup.addStencil( actionRetractNodeFactory,
                                 stencilBuilder,
                                 new ActionRetractFactoryHelper( an3,
                                                                 isReadOnly ),
                                 isReadOnly );

        add( new PanelGroup() {{
            final PanelCollapse collapse = new PanelCollapse() {{
                add( new PanelBody() {{
                    add( paletteGroup );
                }} );
            }};
            add( new PanelHeader() {{
                setDataToggle( Toggle.COLLAPSE );
                setDataParent( getId() );
                setDataTargetWidget( collapse );
                add( new Heading( HeadingSize.H4 ) {{
                    setText( GuidedDecisionTreeConstants.INSTANCE.actionsPaletteGroup() );
                }} );
            }} );
            add( collapse );
        }} );
    }

    private PanelGroup makePanelGroup( final String className,
                                       final boolean isReadOnly ) {
        return new PanelGroup() {{
            final PanelCollapse collapse = new PanelCollapse() {{
                add( new PanelBody() {{
                    add( makeStencils( className, isReadOnly ) );
                }} );
            }};
            add( new PanelHeader() {{
                setDataToggle( Toggle.COLLAPSE );
                setDataParent( getId() );
                setDataTargetWidget( collapse );
                add( new Heading( HeadingSize.H4 ) {{
                    setText( className );
                }} );
            }} );
            add( collapse );
        }};
    }

    private Widget makeStencils( final String className,
                                 final boolean isReadOnly ) {
        final GuidedDecisionTreePaletteGroup paletteGroup = new GuidedDecisionTreePaletteGroup();
        if ( className == null ) {
            return paletteGroup;
        }

        oracle.getFieldCompletions( className,
                                    new Callback<ModelField[]>() {
                                        @Override
                                        public void callback( final ModelField[] mfs ) {
                                            if ( mfs == null || mfs.length == 0 ) {
                                                return;
                                            }
                                            final TypeNode tn = new TypeNodeImpl( className );
                                            paletteGroup.addStencil( typeNodeFactory,
                                                                     stencilBuilder,
                                                                     new TypeFactoryHelper( tn,
                                                                                            isReadOnly ),
                                                                     isReadOnly );

                                            for ( ModelField mf : mfs ) {
                                                final String fieldName = mf.getName();
                                                if ( !fieldName.equals( DataType.TYPE_THIS ) ) {
                                                    final ConstraintNode cn = new ConstraintNodeImpl( className,
                                                                                                      fieldName,
                                                                                                      "",
                                                                                                      new StringValue( "" ) );
                                                    paletteGroup.addStencil( constraintNodeFactory,
                                                                             stencilBuilder,
                                                                             new ConstraintFactoryHelper( cn,
                                                                                                          isReadOnly ),
                                                                             isReadOnly );
                                                }
                                            }
                                        }
                                    } );
        return paletteGroup;
    }

}