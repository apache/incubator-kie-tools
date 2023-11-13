/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.http.client.URL;
import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ItemAwareElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingMessage;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.AssociationDeclaration;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.FormalExpressionBodyHandler;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.AssignmentsInfos.isReservedIdentifier;

public class InputAssignmentReader {

    private final AssociationDeclaration associationDeclaration;

    private static Logger logger = Logger.getLogger(InputAssignmentReader.class.getName());

    public static Optional<InputAssignmentReader> fromAssociation(DataInputAssociation in) {
        List<ItemAwareElement> sourceList = in.getSourceRef();
        List<Assignment> assignmentList = in.getAssignment();
        String targetName = ((DataInput) in.getTargetRef()).getName();
        if (isReservedIdentifier(targetName)) {
            return Optional.empty();
        }

        if (!sourceList.isEmpty()) {
            return Optional.of(new InputAssignmentReader(sourceList.get(0), targetName));
        } else if (!assignmentList.isEmpty()) {
            return Optional.of(new InputAssignmentReader(assignmentList.get(0), targetName));
        } else {
            logger.log(Level.SEVERE, MarshallingMessage.builder().message("Cannot find SourceRef or Assignment for Target ").toString() + targetName);
            return Optional.empty();
        }
    }

    InputAssignmentReader(Assignment assignment, String targetName) {
        FormalExpression from = (FormalExpression) assignment.getFrom();
        String body = FormalExpressionBodyHandler.of(from).getBody();
        String encodedBody = encode(body);
        this.associationDeclaration = new AssociationDeclaration(
                AssociationDeclaration.Direction.Input,
                AssociationDeclaration.Type.FromTo,
                encodedBody,
                targetName);
    }

    InputAssignmentReader(ItemAwareElement source, String targetName) {
        this.associationDeclaration = new AssociationDeclaration(
                AssociationDeclaration.Direction.Input,
                AssociationDeclaration.Type.SourceTarget,
                ItemNameReader.from(source).getName(),
                targetName);
    }

    private String encode(String body) {
        return Optional
                .ofNullable(body)
                .filter(b -> !"null".equals(b))
                .map(URL::encode)
                .orElse("");
    }

    public AssociationDeclaration getAssociationDeclaration() {
        return associationDeclaration;
    }
}
