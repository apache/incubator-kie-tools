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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.associations.AssociationType;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentValue;

public class ParsedReassignmentsInfos {

    ReassignmentValue reassignment = new ReassignmentValue();

    public static ReassignmentValue of(String type, String body) {
        return new ParsedReassignmentsInfos(type, body).reassignment;
    }

    public static String ofCDATA(ReassignmentTypeListValue value, AssociationType type) {
        return new CDATA(value, type).get();
    }

    private ParsedReassignmentsInfos(String type, String body) {
        reassignment.setType(type);

        if (body != null && !body.isEmpty()) {
            String usersAndGroups;
            if (body.contains("@")) {
                String[] parts = body.split("@");
                parsePeriod(reassignment, parts[1]);
                usersAndGroups = parts[0];
            } else {
                usersAndGroups = body;
            }
            usersAndGroups = replaceBracket(usersAndGroups);

            getUsers(reassignment, usersAndGroups);
            getGroups(reassignment, usersAndGroups);
        }
    }

    private static void getUsers(ReassignmentValue reassignment, String usersAndGroups) {
        reassignment.setUsers(parseGroup(usersAndGroups, "users", 0));
    }

    private static void getGroups(ReassignmentValue reassignment, String usersAndGroups) {
        reassignment.setGroups(parseGroup(usersAndGroups, "groups", 1));
    }

    private static List<String> parseGroup(String group, String type, int position) {
        if (group.contains(type)) {
            String result = group
                    .split("\\|")[position]
                    .replace(type + ":", "");
            if (!result.isEmpty()) {
                return Arrays.stream(result.split(",")).collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }

    private static void parsePeriod(ReassignmentValue reassignment, String part) {
        reassignment.setDuration(replaceBracket(part));
    }

    private static String replaceBracket(String original) {
        return original.replaceFirst("\\[", "").replace("]", "");
    }

    private static class CDATA {

        private List<ReassignmentValue> reassignments;

        private AssociationType type;

        CDATA(ReassignmentTypeListValue value, AssociationType type) {
            this.type = type;
            reassignments = value.getValues();
        }

        String get() {
            return reassignments.stream().filter(m -> m.getType().equals(type.getName()))
                    .map(m -> m.toCDATAFormat())
                    .collect(Collectors.joining("^"));
        }
    }
}
