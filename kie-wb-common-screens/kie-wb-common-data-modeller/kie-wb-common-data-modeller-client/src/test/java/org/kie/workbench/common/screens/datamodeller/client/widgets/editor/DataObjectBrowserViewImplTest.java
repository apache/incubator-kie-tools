/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.editor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.enterprise.inject.Instance;

import com.google.gwt.user.cellview.client.Column;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.model.editor.FieldMetadata;
import org.kie.workbench.common.screens.datamodeller.model.editor.FieldMetadataProvider;
import org.kie.workbench.common.screens.datamodeller.model.editor.ImageWrapper;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.impl.ObjectPropertyImpl;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.mocks.MockInstanceImpl;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DataObjectBrowserViewImplTest {

    @Mock
    private ValidationPopup validationPopup;

    private DataObjectBrowserViewImpl view;

    @Before
    public void setUp() {
        this.view = new DataObjectBrowserViewImpl(
                validationPopup,
                new MockInstanceImpl<>());
    }

    @Test
    public void showValidationPopupForDeletion() {
        List<ValidationMessage> validationMessages = Collections.EMPTY_LIST;
        Command yesCommand = () -> {
        };
        Command noCommand = () -> {
        };

        view.showValidationPopupForDeletion(validationMessages,
                                            yesCommand,
                                            noCommand);

        verify(validationPopup,
               Mockito.times(1)).showDeleteValidationMessages(
                yesCommand,
                noCommand,
                validationMessages);
    }

    @Test
    public void addPropertyTypeBrowseColumn() {
        FieldMetadataProvider fieldMetadataProvider = objectProperty -> {
            if ("testField".equals(objectProperty.getName())) {
                ImageWrapper imageWrapper = new ImageWrapper("testUri",
                                                             "testDescription");
                FieldMetadata fieldMetadata = new FieldMetadata(imageWrapper);
                return Optional.of(fieldMetadata);
            }
            return Optional.empty();
        };
        Instance<FieldMetadataProvider> fieldMetadataProviderInstance = new MockInstanceImpl<>(fieldMetadataProvider);
        view = new DataObjectBrowserViewImpl(validationPopup,
                                             fieldMetadataProviderInstance);

        Column<ObjectProperty, List<ImageWrapper>> column = view.createPropertyTypeBrowseColumn();

        ObjectProperty matchingObjectProperty = new ObjectPropertyImpl("testField",
                                                                       "className",
                                                                       false);
        List<ImageWrapper> imageWrapperList = column.getValue(matchingObjectProperty);

        assertEquals(1,
                     imageWrapperList.size());

        ObjectProperty nonMatchingObjectProperty = new ObjectPropertyImpl("nonMatchingTestField",
                                                                          "className",
                                                                          false);
        imageWrapperList = column.getValue(nonMatchingObjectProperty);

        assertEquals(0,
                     imageWrapperList.size());
    }
}
