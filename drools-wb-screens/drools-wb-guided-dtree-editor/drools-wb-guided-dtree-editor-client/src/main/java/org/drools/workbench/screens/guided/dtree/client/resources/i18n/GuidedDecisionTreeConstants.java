/*
* Copyright 2014 JBoss Inc
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
package org.drools.workbench.screens.guided.dtree.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * Guided Decision Tree I18N constants
 */
public interface GuidedDecisionTreeConstants
        extends
        Messages {

    public static final GuidedDecisionTreeConstants INSTANCE = GWT.create( GuidedDecisionTreeConstants.class );

    public String guidedDecisionTreeResourceTypeDescription();

    public String newGuidedDecisionTreeDescription();

    public String confirmDeleteDecisionTree();

    public String confirmDeleteDecisionTreeNode();

    public String popupTitleEditConstraint();

    public String popupTitleEditType();

    public String popupTitleEditActionRetract();

    public String popupTitleEditActionUpdate();

    public String popupTitleEditActionInsert();

    public String className();

    public String fieldName();

    public String binding();

    public String operator();

    public String value();

    public String actionRetract();

    public String actionUpdate();

    public String actionFieldValues();

    public String actionUpdateEngineWithChanges();

    public String actionUpdateHeader();

    public String actionUpdateDescription();

    public String addFieldValue();

    public String removeFieldValue();

    public String actionInsert();

    public String actionInsertLogical();

    public String actionInsertLogicalHeader();

    public String actionInsertLogicalDescription();

    public String noFields();

    public String noBindings();

    public String bindingHeader();

    public String bindingDescription();

    public String bindingIsNotUnique();

    public String bindingIsUsed();

    public String noOperator();

    public String dataTypeNotSupported0( final String dataType );

    public String actionsPaletteGroup();

}
