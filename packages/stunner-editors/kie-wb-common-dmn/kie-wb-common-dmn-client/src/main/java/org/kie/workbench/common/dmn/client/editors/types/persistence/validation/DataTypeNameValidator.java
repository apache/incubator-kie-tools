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

package org.kie.workbench.common.dmn.client.editors.types.persistence.validation;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.editors.types.BuiltInTypeUtils;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.errors.DataTypeNameIsBlankErrorMessage;
import org.kie.workbench.common.dmn.client.editors.types.common.errors.DataTypeNameIsDefaultTypeMessage;
import org.kie.workbench.common.dmn.client.editors.types.common.errors.DataTypeNameIsNotUniqueErrorMessage;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;

import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

public class DataTypeNameValidator {

    private final Event<FlashMessage> flashMessageEvent;

    private final DataTypeNameIsBlankErrorMessage blankErrorMessage;

    private final DataTypeNameIsNotUniqueErrorMessage notUniqueErrorMessage;

    private final DataTypeNameIsDefaultTypeMessage nameIsDefaultTypeMessage;

    private final DataTypeStore dataTypeStore;

    @Inject
    public DataTypeNameValidator(final Event<FlashMessage> flashMessageEvent,
                                 final DataTypeNameIsBlankErrorMessage blankErrorMessage,
                                 final DataTypeNameIsNotUniqueErrorMessage notUniqueErrorMessage,
                                 final DataTypeNameIsDefaultTypeMessage nameIsDefaultTypeMessage,
                                 final DataTypeStore dataTypeStore) {
        this.flashMessageEvent = flashMessageEvent;
        this.blankErrorMessage = blankErrorMessage;
        this.notUniqueErrorMessage = notUniqueErrorMessage;
        this.nameIsDefaultTypeMessage = nameIsDefaultTypeMessage;
        this.dataTypeStore = dataTypeStore;
    }

    public boolean isValid(final DataType dataType) {

        if (isBlank(dataType)) {
            flashMessageEvent.fire(blankErrorMessage.getFlashMessage(dataType));
            return false;
        }

        if (isNotUnique(dataType)) {
            flashMessageEvent.fire(notUniqueErrorMessage.getFlashMessage(dataType));
            return false;
        }

        if (isDefault(dataType)) {
            flashMessageEvent.fire(nameIsDefaultTypeMessage.getFlashMessage(dataType));
            return false;
        }

        return true;
    }

    boolean isDefault(final DataType dataType) {
        return BuiltInTypeUtils.isBuiltInType(dataType.getName());
    }

    public boolean isNotUnique(final DataType dataType) {

        final List<DataType> siblings = siblings(dataType);
        return siblings.stream().anyMatch(sibling -> {

            final boolean isNameEquals = Objects.equals(sibling.getName(), dataType.getName());
            final boolean isOtherDataType = !Objects.equals(sibling.getUUID(), dataType.getUUID());

            return isNameEquals && isOtherDataType;
        });
    }

    boolean isBlank(final DataType dataType) {
        return isEmpty(dataType.getName());
    }

    public List<DataType> siblings(final DataType dataType) {

        final Optional<DataType> parent = Optional.ofNullable(dataTypeStore.get(dataType.getParentUUID()));

        if (parent.isPresent()) {
            return parent.get().getSubDataTypes();
        } else {
            return dataTypeStore.getTopLevelDataTypes();
        }
    }
}
