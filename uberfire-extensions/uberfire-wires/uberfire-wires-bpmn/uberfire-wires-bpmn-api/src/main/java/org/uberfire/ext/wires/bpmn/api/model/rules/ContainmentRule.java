/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.wires.bpmn.api.model.rules;

import java.util.Set;

import org.uberfire.ext.wires.bpmn.api.model.Role;

/**
 * Rule restricting the Elements that can be contained within another Element.
 */
public interface ContainmentRule extends RuleById {

    /**
     * The Roles of Elements permitted to be held within another Element.
     * @return
     */
    Set<Role> getPermittedRoles();

}
