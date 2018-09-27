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

package org.kie.workbench.common.dmn.client.editors.types;

import java.util.Optional;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class NameAndDataTypePopoverViewImplTest {

    private static final String NAME = "name";

    @Mock
    private Input nameEditor;

    @Mock
    private DataTypePickerWidget dataTypeEditor;

    @Mock
    private Div popoverElement;

    @Mock
    private Div popoverContentElement;

    @Mock
    private Span nameLabel;

    @Mock
    private Span dataTypeLabel;

    @Mock
    private JQueryProducer.JQuery<Popover> jQueryProducer;

    @Mock
    private NameAndDataTypePopoverView.Presenter presenter;

    @Mock
    private HTMLElement element;

    @Mock
    private Decision decision;

    @Mock
    private QName typeRef;

    @Mock
    private ValueChangeEvent<QName> valueChangeEvent;

    @Mock
    private BlurEvent blurEvent;

    @Mock
    private Popover popover;

    @Mock
    private TranslationService translationService;

    @Captor
    private ArgumentCaptor<ValueChangeHandler<QName>> valueChangeHandlerCaptor;

    private NameAndDataTypePopoverViewImpl view;

    @Before
    public void setUp() throws Exception {
        view = spy(new NameAndDataTypePopoverViewImpl(nameEditor,
                                                      dataTypeEditor,
                                                      popoverElement,
                                                      popoverContentElement,
                                                      nameLabel,
                                                      dataTypeLabel,
                                                      jQueryProducer,
                                                      translationService));
        view.init(presenter);

        doReturn(element).when(view).getElement();
        when(valueChangeEvent.getValue()).thenReturn(typeRef);
        when(jQueryProducer.wrap(element)).thenReturn(popover);

        doAnswer(i -> i.getArguments()[0]).when(translationService).getTranslation(anyString());
    }

    @Test
    public void testInit() {
        verify(dataTypeEditor).addValueChangeHandler(valueChangeHandlerCaptor.capture());

        valueChangeHandlerCaptor.getValue().onValueChange(valueChangeEvent);

        verify(presenter).setTypeRef(eq(typeRef));
    }

    @Test
    public void testSetDMNModel() {
        view.setDMNModel(decision);

        verify(dataTypeEditor).setDMNModel(eq(decision));
    }

    @Test
    public void testInitName() {
        view.initName(NAME);

        verify(nameEditor).setValue(eq(NAME));
    }

    @Test
    public void testInitSelectedTypeRef() {
        view.initSelectedTypeRef(typeRef);

        verify(dataTypeEditor).setValue(eq(typeRef), eq(false));
    }

    @Test
    public void testShow() {
        view.show(Optional.empty());

        verify(popover).show();
    }

    @Test
    public void testHideBeforeShown() {
        view.hide();

        verify(popover, never()).hide();
        verify(popover, never()).destroy();
    }

    @Test
    public void testHideAfterShown() {
        view.show(Optional.empty());
        view.hide();

        verify(popover).hide();
        verify(popover).destroy();
    }

    @Test
    public void testOnNameChange() {
        when(nameEditor.getValue()).thenReturn(NAME);

        view.onNameChange(blurEvent);

        verify(presenter).setName(eq(NAME));
    }
}
