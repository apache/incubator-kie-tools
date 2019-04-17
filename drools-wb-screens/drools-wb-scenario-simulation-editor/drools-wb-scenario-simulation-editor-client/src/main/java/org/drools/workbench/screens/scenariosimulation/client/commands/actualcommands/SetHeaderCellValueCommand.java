/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands;

import javax.enterprise.context.Dependent;

import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;

/**
 * <code>Command</code> to to set the <i>value</i> of a header' cell
 */
@Dependent
public class SetHeaderCellValueCommand extends AbstractScenarioSimulationCommand {

    protected boolean isInstanceHeader;
    protected boolean isPropertyHeader;

    public SetHeaderCellValueCommand() {
        this(false, false);
    }

    public SetHeaderCellValueCommand(boolean isInstanceHeader, boolean isPropertyHeader) {
        super(true);
        this.isInstanceHeader = isInstanceHeader;
        this.isPropertyHeader = isPropertyHeader;
    }

    @Override
    protected void internalExecute(ScenarioSimulationContext context) throws Exception {
        final ScenarioSimulationContext.Status status = context.getStatus();
        final String headerValue = status.getCellValue();
        boolean valid = false;
        if (isInstanceHeader) {
            valid = validateInstanceHeader(context, headerValue, status.getColumnIndex());
        }  else if (isPropertyHeader) {
            valid = validatePropertyHeader(context, headerValue, status.getColumnIndex());
        }
        if (valid) {
            context.getModel().updateHeader(status.getColumnIndex(), status.getRowIndex(), headerValue);
        } else {
            throw new Exception("Name \"" + headerValue + "\" cannot be used");
        }
    }

    protected boolean validateInstanceHeader(ScenarioSimulationContext context, String headerValue, int columnIndex) {
        boolean isADataType = context.getDataObjectFieldsMap().containsKey(headerValue);
        return context.getModel().validateInstanceHeaderUpdate(headerValue, columnIndex, isADataType);
    }

    protected boolean validatePropertyHeader(ScenarioSimulationContext context, String headerValue, int columnIndex) {
        final FactMapping factMappingByIndex = context.getStatus().getSimulation().getSimulationDescriptor().getFactMappingByIndex(columnIndex);
        String className = factMappingByIndex.getFactIdentifier().getClassName();
        if (className.contains(".")) {
            className = className.substring(className.lastIndexOf(".")+1);
        }
        final FactModelTree factModelTree = context.getDataObjectFieldsMap().get(className);
        boolean isPropertyType = factModelTree != null && recursivelyFindIsPropertyType(context, factModelTree, headerValue);
        return context.getModel().validatePropertyHeaderUpdate(headerValue, columnIndex, isPropertyType);
    }

    protected boolean recursivelyFindIsPropertyType(ScenarioSimulationContext context, FactModelTree factModelTree, String propertyName) {
        boolean toReturn = factModelTree.getSimpleProperties().containsKey(propertyName) || factModelTree.getExpandableProperties().containsKey(propertyName);
        if (!toReturn && propertyName.contains(".")) {
            String propertyParent = propertyName.substring(0, propertyName.indexOf("."));
            if (factModelTree.getExpandableProperties().containsKey(propertyParent)) {
                String nestedHeaderValue = propertyName.substring(propertyName.lastIndexOf(".") + 1);
                String className = factModelTree.getExpandableProperties().get(propertyParent);
                final FactModelTree nestedFactModelTree = context.getDataObjectFieldsMap().get(className);
                toReturn =  recursivelyFindIsPropertyType(context, nestedFactModelTree, nestedHeaderValue);
            }
        }
        return toReturn;
    }
}
