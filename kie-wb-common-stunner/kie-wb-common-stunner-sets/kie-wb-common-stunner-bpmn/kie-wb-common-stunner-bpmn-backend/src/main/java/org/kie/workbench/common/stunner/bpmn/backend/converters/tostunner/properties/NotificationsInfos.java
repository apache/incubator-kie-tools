/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.ParsedNotificationsInfos;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.associations.AssociationType;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationsInfo;

import static java.util.Arrays.asList;

public class NotificationsInfos {

    private static Set<String> RESERVED_ASSIGNMENTS = new HashSet<>(asList(
            AssociationType.NOT_COMPLETED_NOTIFY.getName(),
            AssociationType.NOT_STARTED_NOTIFY.getName()));

    public static NotificationsInfo of(List<DataInputAssociation> dataInputAssociations) {
        NotificationTypeListValue notifications = new NotificationTypeListValue();
        dataInputAssociations.forEach(din -> {
            DataInput targetRef = (DataInput) (din.getTargetRef());
            if (isReservedIdentifier(targetRef.getName())) {
                if (!din.getAssignment().isEmpty()) {
                    Assignment assignment = din.getAssignment().get(0);
                    if (assignment != null) {
                        String body = ((FormalExpression) assignment.getFrom()).getBody();
                        if (body != null) {
                            Arrays.stream(body.split("\\^")).forEach(b -> {
                                notifications.addValue(ParsedNotificationsInfos.of(targetRef.getName(), b));
                            });
                        }
                    }
                }
            }
        });

        return new NotificationsInfo(notifications);
    }

    public static boolean isReservedIdentifier(String targetName) {
        return RESERVED_ASSIGNMENTS.contains(targetName);
    }

}
