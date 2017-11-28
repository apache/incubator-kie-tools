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
package org.dashbuilder.client.widgets.dataset.editor.bean;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.client.widgets.resources.i18n.DataSetEditorConstants;
import org.dashbuilder.common.client.editor.ValueBoxEditor;
import org.dashbuilder.common.client.editor.map.MapEditor;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.uberfire.client.mvp.UberView;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * <p>Bean Data Set specific attributes editor presenter.</p>
 * 
 * @since 0.4.0 
 */
@Dependent
public class BeanDataSetDefAttributesEditor implements IsWidget, org.dashbuilder.dataset.client.editor.BeanDataSetDefAttributesEditor {

    public interface View extends UberView<BeanDataSetDefAttributesEditor> {
        /**
         * <p>Specify the views to use for each sub-editor before calling <code>initWidget</code>.</p>
         */
        void initWidgets(ValueBoxEditor.View generatorClassView, IsWidget parameterMapView);
        
    }

    ValueBoxEditor<String> generatorClass;
    MapEditor paramaterMap;
    public View view;

    @Inject
    public BeanDataSetDefAttributesEditor(final ValueBoxEditor<String> generatorClass,
                                          final MapEditor paramaterMap,
                                          final View view) {
        this.generatorClass = generatorClass;
        this.paramaterMap = paramaterMap;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        // Initialize the Bean specific attributes editor view.
        view.init(this);
        view.initWidgets(generatorClass.view, paramaterMap.asWidget());
        generatorClass.addHelpContent(DataSetEditorConstants.INSTANCE.bean_generator_class(),
                DataSetEditorConstants.INSTANCE.bean_generator_class_description(),
                Placement.BOTTOM);
    }

    /*************************************************************
     ** GWT EDITOR CONTRACT METHODS **
     *************************************************************/

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
    
    @Override
    public ValueBoxEditor<String> generatorClass() {
        return generatorClass;
    }

    @Override
    public MapEditor paramaterMap() {
        return paramaterMap;
    }

}
