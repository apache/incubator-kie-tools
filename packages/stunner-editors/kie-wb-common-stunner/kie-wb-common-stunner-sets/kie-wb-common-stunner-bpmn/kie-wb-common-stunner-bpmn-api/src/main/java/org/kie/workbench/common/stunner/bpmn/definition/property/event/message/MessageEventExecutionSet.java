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

package org.kie.workbench.common.stunner.bpmn.definition.property.event.message;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.bpmn.forms.model.ComboBoxFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@FormDefinition(startElement = "messageRef")
public class MessageEventExecutionSet implements BPMNPropertySet {

    @Property
    @FormField(type = ComboBoxFieldType.class)
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.ProcessMessageRefProvider"
    )
    @Valid
    private MessageRef messageRef;

    public MessageEventExecutionSet() {
        this(new MessageRef());
    }

    public MessageEventExecutionSet(final @MapsTo("messageRef") MessageRef messageRef) {
        this.messageRef = messageRef;
    }

    public MessageRef getMessageRef() {
        return messageRef;
    }

    public void setMessageRef(MessageRef messageRef) {
        this.messageRef = messageRef;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(messageRef.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MessageEventExecutionSet) {
            MessageEventExecutionSet other = (MessageEventExecutionSet) o;
            return (other.messageRef == null ? messageRef == null : other.messageRef.equals(messageRef));
        }
        return false;
    }
}
