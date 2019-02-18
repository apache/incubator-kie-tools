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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ItemAwareElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.AssociationDeclaration;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.AssignmentsInfos.isReservedIdentifier;

public class InputAssignmentReader {

    private final AssociationDeclaration associationDeclaration;

    public static InputAssignmentReader fromAssociation(DataInputAssociation in) {
        List<ItemAwareElement> sourceList = in.getSourceRef();
        List<Assignment> assignmentList = in.getAssignment();
        String targetName = ((DataInput) in.getTargetRef()).getName();
        if (isReservedIdentifier(targetName)) {
            return null;
        }

        if (!sourceList.isEmpty()) {
            return new InputAssignmentReader(sourceList.get(0), targetName);
        } else if (!assignmentList.isEmpty()) {
            return new InputAssignmentReader(assignmentList.get(0), targetName);
        } else {
            throw new IllegalArgumentException("Cannot find SourceRef or Assignment for Target " + targetName);
        }
    }

    InputAssignmentReader(Assignment assignment, String targetName) {
        FormalExpression from = (FormalExpression) assignment.getFrom();
        String body = from.getBody();
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
        try {
            return URLEncoder.encode(body, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(body, e);
        }
    }

    public AssociationDeclaration getAssociationDeclaration() {
        return associationDeclaration;
    }
}
