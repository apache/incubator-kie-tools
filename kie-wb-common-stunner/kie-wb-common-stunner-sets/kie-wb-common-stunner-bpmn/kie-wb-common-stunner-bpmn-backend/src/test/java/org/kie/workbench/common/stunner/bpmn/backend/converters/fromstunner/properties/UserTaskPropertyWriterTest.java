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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.utils.Pair;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.PotentialOwner;
import org.eclipse.bpmn2.ResourceRole;
import org.eclipse.bpmn2.UserTask;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.assignee.Actors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class UserTaskPropertyWriterTest {

    private FlatVariableScope variableScope;

    @Before
    public void before() {
        this.variableScope = new FlatVariableScope();
    }

    @Test
    public void startsFromUnderscore() {
        UserTask userTask = bpmn2.createUserTask();
        UserTaskPropertyWriter userTaskPropertyWriter = new UserTaskPropertyWriter(userTask, variableScope);
        Actors actor = new Actors();
        actor.setValue("startsFromUnderscore");
        userTaskPropertyWriter.setActors(actor);
        assertEquals("startsFromUnderscore", getActors(userTask).get(0).a);
        assertTrue(getActors(userTask).get(0).b.startsWith("_"));
    }

    @Test
    public void testEmptyOutputSets() {
        UserTask userTask = bpmn2.createUserTask();
        UserTaskPropertyWriter userTaskPropertyWriter = new UserTaskPropertyWriter(userTask, variableScope);
        assertEquals(0, userTaskPropertyWriter.getIoSpecification().getOutputSets().size());
    }

    public List<Pair<String, String>> getActors(UserTask userTask) {
        // get the user task actors
        List<ResourceRole> roles = userTask.getResources();
        List<Pair<String, String>> actors = new ArrayList<>();
        for (ResourceRole role : roles) {
            if (role instanceof PotentialOwner) {
                FormalExpression fe = (FormalExpression)
                        role.getResourceAssignmentExpression()
                                .getExpression();
                actors.add(new Pair<>(fe.getBody(), role.getId()));
            }
        }
        return actors;
    }
}
