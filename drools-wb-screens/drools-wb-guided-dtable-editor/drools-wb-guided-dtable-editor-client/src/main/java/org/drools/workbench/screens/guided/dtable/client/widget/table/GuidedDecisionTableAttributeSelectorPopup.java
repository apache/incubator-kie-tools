/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.util.Arrays;

import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.rule.client.editor.AttributeSelectorPopup;
import org.drools.workbench.screens.guided.rule.client.editor.RuleAttributeWidget;

public class GuidedDecisionTableAttributeSelectorPopup extends AttributeSelectorPopup {

    private final String[] reservedAttributeNames;
    private final GuidedDecisionTableView.Presenter presenter;

    public GuidedDecisionTableAttributeSelectorPopup( final String[] reservedAttributeNames,
                                                      final GuidedDecisionTableView.Presenter presenter ) {
        this.reservedAttributeNames = reservedAttributeNames;
        this.presenter = presenter;

        initialize();
    }

    @Override
    protected String[] getAttributes() {
        String[] attributes = RuleAttributeWidget.getAttributesList();
        attributes = Arrays.copyOf( attributes, attributes.length + 1 );
        attributes[attributes.length - 1] = GuidedDecisionTable52.NEGATE_RULE_ATTR;
        return attributes;
    }

    @Override
    protected String[] getReservedAttributes() {
        return reservedAttributeNames;
    }

    @Override
    protected void handleAttributeAddition( String attributeName ) {
        final AttributeCol52 column = new AttributeCol52();
        column.setAttribute( attributeName );
        presenter.appendColumn( column );
    }

    @Override
    protected boolean isMetadataUnique( String metadataName ) {
        return presenter.isMetaDataUnique( metadataName );
    }

    @Override
    protected String metadataNotUniqueMessage( String metadataName ) {
        return GuidedDecisionTableConstants.INSTANCE.ThatColumnNameIsAlreadyInUsePleasePickAnother();
    }

    @Override
    protected void handleMetadataAddition( String metadataName ) {
        final MetadataCol52 column = new MetadataCol52();
        column.setMetadata( metadataName );
        column.setHideColumn( true );
        presenter.appendColumn( column );
    }
}
