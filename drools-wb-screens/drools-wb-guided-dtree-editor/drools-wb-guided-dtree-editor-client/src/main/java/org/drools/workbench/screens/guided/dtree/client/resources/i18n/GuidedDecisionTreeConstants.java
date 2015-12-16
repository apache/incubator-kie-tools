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
package org.drools.workbench.screens.guided.dtree.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.AmbiguousRootParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.BindingNotFoundParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.DataTypeConversionErrorParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.DataTypeNotFoundParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.DefaultParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.InvalidRootParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.UnsupportedFieldConstraintParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.UnsupportedFieldConstraintTypeParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.UnsupportedFieldNatureTypeParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.UnsupportedIActionParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.UnsupportedIPatternParserMessage;

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

    public String gettingStartedHint();

    public String popupTitleParserMessages();

    public String parserMessagesDescription();

    public String remove();

    public String ignore();

    public String parserMessageAmbiguousRootParserMessage( final String rootClassName,
                                                           final String ambiguousClassName );

    public String parserMessageBindingNotFoundParserMessage( final String binding );

    public String parserMessageDataTypeConversionErrorParserMessage( final String value,
                                                                     final String dataTypeClassName );

    public String parserMessageDataTypeNotFoundParserMessage( final String className,
                                                              final String fieldName );

    public String parserMessageDefaultParserMessage(final String message);

    public String parserMessageInvalidRootParserMessage();

    public String parserMessageUnsupportedFieldConstraintParserMessage();

    public String parserMessageUnsupportedFieldConstraintTypeParserMessage();

    public String parserMessageUnsupportedFieldNatureTypeParserMessage();

    public String parserMessageUnsupportedIActionParserMessage();

    public String parserMessageUnsupportedIPatternParserMessage();

    public String parserMessageUnknownMessage();

}