/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationlisteditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationlisteditor.item.AnnotationListItem;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditorPopup;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.core.ElementType;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationParseRequest;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationParseResponse;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationSource;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationSourceRequest;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationSourceResponse;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class AdvancedAnnotationListEditorTest {

    private static final int MAX_ITEMS = 4;

    @Mock
    private AdvancedAnnotationListEditorView view;

    @Mock
    private DataModelerService dataModelerService;

    @Mock
    private InstanceMock<ValuePairEditorPopup> valuePairEditorInstance;

    @Mock
    private ValuePairEditorPopup valuePairEditor;

    @Mock
    private InstanceMock<AnnotationListItem> itemInstance;

    private int listItemsCount = 0;

    private List<AnnotationListItem> itemInstances = new ArrayList<AnnotationListItem>();

    private AdvancedAnnotationListEditor annotationListEditor;

    private List<Annotation> annotations = new ArrayList<Annotation>();

    @Mock
    private Annotation annotation;

    @Mock
    private AnnotationDefinition annotationDefinition;

    @Mock
    private AnnotationValuePairDefinition valuePairDefinition;

    @Mock
    private AnnotationSourceResponse sourceResponse;

    private Map<String, AnnotationSource> annotationSourcesMap = new HashMap<String, AnnotationSource>();

    @Mock
    private KieProject kieProject;

    @Mock
    private ElementType elementType;

    @Mock
    private AdvancedAnnotationListEditorView.AddAnnotationHandler addAnnotationHandler;

    @Mock
    private AdvancedAnnotationListEditorView.DeleteAnnotationHandler deleteAnnotationHandler;

    @Mock
    private AdvancedAnnotationListEditorView.ValuePairChangeHandler valuePairChangeHandler;

    @Mock
    private AdvancedAnnotationListEditorView.ClearValuePairHandler clearValuePairHandler;

    private Annotation currentAnnotation;

    private String currentAnnotationClassName;

    @Mock
    private AnnotationDefinition currentAnnotationDefinition;

    private AnnotationSource currentAnnotationSources;

    @Mock
    private AnnotationValuePairDefinition currentValuePairDefinition;

    private String currentValuePairName;

    private String currentValuePairSource;

    private Object currentValuePairValue;

    private Object newValuePairValue;

    private ArgumentCaptor<Command> yesCommandCaptor;

    private ArgumentCaptor<Command> noCommandCaptor;

    private ArgumentCaptor<Command> cancelCommandCaptor;

    private ArgumentCaptor<Callback<Annotation>> annotationCallbackCaptor;

    @Before
    public void setup() {
        yesCommandCaptor = ArgumentCaptor.forClass(Command.class);
        noCommandCaptor = ArgumentCaptor.forClass(Command.class);
        cancelCommandCaptor = ArgumentCaptor.forClass(Command.class);
        annotationCallbackCaptor = (ArgumentCaptor) ArgumentCaptor.forClass(Callback.class);

        Annotation annotation;
        AnnotationSource annotationSource;
        for (int i = 0; i < MAX_ITEMS; i++) {
            annotation = mock(Annotation.class);
            when(annotation.getClassName()).thenReturn("AnnotationClass" + i);
            annotations.add(annotation);
            annotationSource = mock(AnnotationSource.class);
            annotationSourcesMap.put(annotation.getClassName(),
                                     annotationSource);
        }
        setupItemInstances();

        annotationListEditor = new AdvancedAnnotationListEditor(view,
                                                                new CallerMock(dataModelerService),
                                                                valuePairEditorInstance,
                                                                itemInstance) {
            @Override
            protected AnnotationListItem createListItem() {
                if (listItemsCount >= itemInstances.size()) {
                    throw new RuntimeException("too many invocations");
                } else {
                    super.createListItem();
                    return itemInstances.get(listItemsCount++);
                }
            }

            @Override
            protected ValuePairEditorPopup createValuePairEditor() {
                super.createValuePairEditor();
                return valuePairEditor;
            }
        };

        annotationListEditor.addAddAnnotationHandler(addAnnotationHandler);
        annotationListEditor.addDeleteAnnotationHandler(deleteAnnotationHandler);
        annotationListEditor.addValuePairChangeHandler(valuePairChangeHandler);
        annotationListEditor.addClearValuePairHandler(clearValuePairHandler);
    }

    private void setupItemInstances() {
        listItemsCount = 0;
        itemInstances.clear();
        for (int i = 0; i < MAX_ITEMS; i++) {
            itemInstances.add(mock(AnnotationListItem.class));
        }
    }

    @Test
    public void testLoadAnnotationsWithoutSources() {
        when(dataModelerService.resolveSourceRequest(any(AnnotationSourceRequest.class))).thenReturn(sourceResponse);
        when(sourceResponse.getAnnotationSources()).thenReturn(annotationSourcesMap);
        annotationListEditor.loadAnnotations(annotations);
        verifyAnnotationsLoaded();
    }

    @Test
    public void testLoadAnnotationsWithSources() {
        annotationListEditor.loadAnnotations(annotations,
                                             annotationSourcesMap);
        verifyAnnotationsLoaded();
    }

    private void verifyAnnotationsLoaded() {
        verify(view,
               times(1)).clear();

        // the items should have been properly created and initialized.
        verify(itemInstance,
               times(itemInstances.size())).get();
        // each item should have been properly loaded with the corresponding annotation.
        Annotation annotation;
        AnnotationListItem listItem;
        for (int i = 0; i < annotations.size(); i++) {
            annotation = annotations.get(i);
            listItem = itemInstances.get(i);
            verify(listItem,
                   times(1)).loadAnnotation(annotation,
                                            annotationSourcesMap.get(annotation.getClassName()));
        }
    }

    @Test
    public void testSetReadonlyTrue() {
        annotationListEditor.loadAnnotations(annotations,
                                             annotationSourcesMap);
        annotationListEditor.setReadonly(true);

        verify(view,
               times(1)).setReadonly(true);
        assertEquals(true,
                     annotationListEditor.isReadonly());
        //the items are set to the by default state = false when the annotations are initially loaded.
        verifyItemsReadonlyStatus(1,
                                  false);
        //and finally to the desired state when setReadonly( true ) is invoked.
        verifyItemsReadonlyStatus(1,
                                  true);
    }

    @Test
    public void testSetReadonlyFalse() {
        annotationListEditor.loadAnnotations(annotations,
                                             annotationSourcesMap);
        annotationListEditor.setReadonly(false);

        verify(view,
               times(1)).setReadonly(false);
        assertEquals(false,
                     annotationListEditor.isReadonly());
        //the items are set to the by default state = false when initially loaded and finally to the desired state
        //when setReadonly( false ) is invoked.
        verifyItemsReadonlyStatus(2,
                                  false);
    }

    private void verifyItemsReadonlyStatus(int times,
                                           boolean expectedReadonlyStatus) {
        for (AnnotationListItem listItem : itemInstances) {
            verify(listItem,
                   times(times)).setReadonly(expectedReadonlyStatus);
        }
    }

    @Test
    public void testOnAddAnnotation() {
        annotationListEditor.init(kieProject,
                                  elementType);
        annotationListEditor.onAddAnnotation();
        // the create annotation wizard should have been invoked.
        verify(view,
               times(1)).invokeCreateAnnotationWizard(annotationCallbackCaptor.capture(),
                                                      eq(kieProject),
                                                      eq(elementType));
        // emulate the wizard completion.
        annotationCallbackCaptor.getValue().callback(annotation);
        verify(addAnnotationHandler,
               times(1)).onAddAnnotation(annotation);
    }

    @Test
    public void testOnDeleteAnnotation() {
        annotationListEditor.init(kieProject,
                                  elementType);
        annotationListEditor.loadAnnotations(annotations,
                                             annotationSourcesMap);
        //pick an arbitrary annotation for deletion, index < MAX_ITEMS
        Annotation selectedAnnotation = annotations.get(2);
        annotationListEditor.onDeleteAnnotation(selectedAnnotation);

        String expectedMessage =
                Constants.INSTANCE.advanced_domain_annotation_list_editor_message_confirm_annotation_deletion(
                        selectedAnnotation.getClassName(),
                        elementType.name());

        verify(view,
               times(1)).showYesNoDialog(eq(expectedMessage),
                                         yesCommandCaptor.capture(),
                                         noCommandCaptor.capture(),
                                         cancelCommandCaptor.capture());
        // emulate the delete completion
        yesCommandCaptor.getValue().execute();
        verify(deleteAnnotationHandler,
               times(1)).onDeleteAnnotation(selectedAnnotation);
    }

    @Test
    public void testOnEditSimpleValuePair() {
        prepareValuePairEdition();
        // it's the simple value case
        when(valuePairEditor.isGenericEditor()).thenReturn(false);

        // emulates that currentAnnotation an currentValuePair where selected for edition in the UI
        annotationListEditor.onEditValuePair(currentAnnotation,
                                             currentValuePairName);
        verifyValuePairEditorCreatedAndShown();
        // the value for the valuePairEditor should have been properly set with currentValuePairValue.
        verify(valuePairEditor,
               times(1)).setValue(currentValuePairValue);

        // setup the returned value.
        newValuePairValue = mock(Object.class);
        // emulate the user pressing the ok button on the valuePairEditor
        prepareValuePairEditorSubmit();
        when(valuePairEditor.isValid()).thenReturn(true);
        annotationListEditor.doValuePairChange(valuePairEditor,
                                               valuePairEditor.getValue());

        verify(valuePairChangeHandler,
               times(1)).onValuePairChange(currentAnnotationClassName,
                                           currentValuePairName,
                                           newValuePairValue);

        verifyValuePairEditorDestroyedAndHidden();
    }

    @Test
    public void testOnEditGenericValuePair() {
        prepareValuePairEdition();

        // it's the generic value case
        when(valuePairEditor.isGenericEditor()).thenReturn(true);
        when(valuePairEditor.getValuePairDefinition()).thenReturn(currentValuePairDefinition);

        // emulates that currentAnnotation an currentValuePair where selected for edition in the UI
        annotationListEditor.onEditValuePair(currentAnnotation,
                                             currentValuePairName);
        verifyValuePairEditorCreatedAndShown();
        // the value for the valuePairEditor should have been properly set with the current value pair source for
        // the generic case.
        verify(valuePairEditor,
               times(1)).setValue(currentValuePairSource);

        // setup the returned value.
        // generic case requires a parse response, and the newValuePairValue comes from server.
        newValuePairValue = mock(Object.class);
        AnnotationParseResponse parseResponse = mock(AnnotationParseResponse.class);
        Annotation parsedAnnotation = mock(Annotation.class);
        when(parseResponse.getAnnotation()).thenReturn(parsedAnnotation);
        when(parsedAnnotation.getValue(currentValuePairName)).thenReturn(newValuePairValue);
        when(parseResponse.hasErrors()).thenReturn(false);
        when(dataModelerService.resolveParseRequest(any(AnnotationParseRequest.class),
                                                    eq(kieProject))).thenReturn(parseResponse);

        // emulate the user pressing the ok button on the valuePairEditor
        prepareValuePairEditorSubmit();
        when(valuePairEditor.isValid()).thenReturn(true);
        annotationListEditor.doValuePairChange(valuePairEditor,
                                               valuePairEditor.getValue());

        verify(valuePairChangeHandler,
               times(1)).onValuePairChange(currentAnnotationClassName,
                                           currentValuePairName,
                                           newValuePairValue);

        verifyValuePairEditorDestroyedAndHidden();
    }

    private void prepareValuePairEdition() {
        // the editor was previously loaded with a set of annotations.
        annotationListEditor.init(kieProject,
                                  elementType);
        annotationListEditor.loadAnnotations(annotations,
                                             annotationSourcesMap);
        // emulates that an arbitrary annotation from the list of loaded annotations was selected for the value pair edition.
        currentAnnotation = annotations.get(0);
        currentAnnotationClassName = currentAnnotation.getClassName();
        currentAnnotationSources = annotationSourcesMap.get(currentAnnotationClassName);
        currentValuePairName = "valuePairName";
        currentValuePairSource = "valuePairSource";
        currentValuePairValue = mock(Object.class);
        when(currentAnnotation.getAnnotationDefinition()).thenReturn(currentAnnotationDefinition);
        when(currentAnnotationDefinition.getValuePair(currentValuePairName)).thenReturn(currentValuePairDefinition);
        when(currentValuePairDefinition.getName()).thenReturn(currentValuePairName);
        when(currentAnnotationSources.getValuePairSource(currentValuePairName)).thenReturn(currentValuePairSource);
        when(currentAnnotation.getValue(currentValuePairName)).thenReturn(currentValuePairValue);

        when(valuePairEditor.getValuePairDefinition()).thenReturn(currentValuePairDefinition);
    }

    private void verifyValuePairEditorCreatedAndShown() {
        // the value pair editor should have been created, initialized and shown.
        verify(valuePairEditorInstance,
               times(1)).get();
        verify(valuePairEditor,
               times(1)).init(currentAnnotation.getClassName(),
                              currentValuePairDefinition);
        verify(valuePairEditor,
               times(1)).show();
    }

    private void verifyValuePairEditorDestroyedAndHidden() {
        verify(valuePairEditor,
               times(1)).hide();
        verify(valuePairEditorInstance,
               times(1)).destroy(valuePairEditor);
    }

    private void prepareValuePairEditorSubmit() {
        // emulate the valuePairEditor status when the user presses the ok action.
        when(valuePairEditor.getAnnotationClassName()).thenReturn(currentAnnotationClassName);
        when(valuePairEditor.getValuePairDefinition()).thenReturn(currentValuePairDefinition);
        when(valuePairEditor.getValue()).thenReturn(newValuePairValue);
    }

    @Test
    public void testOnClearValuePairWithoutDefaultValue() {
        prepareValuePairClear();
        //the value pair hasn't a default value
        when(valuePairDefinition.getDefaultValue()).thenReturn(null);

        // emulate the clear invocation from the ui for the given valuePair
        annotationListEditor.onClearValuePair(annotation,
                                              "valuePairName");
        //an alert should have been raised since the valuePair has no default value.
        String expectedMessage =
                Constants.INSTANCE.advanced_domain_annotation_list_editor_message_value_pair_has_no_default_value("valuePairName",
                                                                                                                  "AnnotationClassName");
        verify(view,
               times(1)).showYesNoDialog(eq(expectedMessage),
                                         yesCommandCaptor.capture());
        yesCommandCaptor.getValue().execute();
        verify(clearValuePairHandler,
               times(0)).onClearValuePair(eq(annotation),
                                          anyString());
    }

    @Test
    public void testOnClearValuePairWithDefaultValue() {
        prepareValuePairClear();
        //the value pair has a default value
        Object someValue = mock(Object.class);
        when(valuePairDefinition.getDefaultValue()).thenReturn(someValue);

        // emulate the clear invocation from the ui for the given valuePair
        annotationListEditor.onClearValuePair(annotation,
                                              "valuePairName");
        // the configured handler should have been invoked.
        verify(clearValuePairHandler,
               times(1)).onClearValuePair(annotation,
                                          "valuePairName");
    }

    private void prepareValuePairClear() {
        when(annotation.getClassName()).thenReturn("AnnotationClassName");
        when(annotation.getAnnotationDefinition()).thenReturn(annotationDefinition);
        when(annotationDefinition.getValuePair("valuePairName")).thenReturn(valuePairDefinition);
    }

    @Test
    public void testExpandCollapseChanges() {
        // first load.
        annotationListEditor.loadAnnotations(annotations,
                                             annotationSourcesMap);
        // when loaded by the first time, all annotations are collapsed.
        for (AnnotationListItem listItem : itemInstances) {
            verify(listItem,
                   times(1)).setCollapsed(true);
            verify(listItem,
                   never()).setCollapsed(false);
        }

        // emulate some expand/collapse actions coming from the UI
        // annotation #0 is expanded
        annotationListEditor.onCollapseChange(annotations.get(0),
                                              false);

        // annotation #1 is expanded, and then collapsed again, so finally collapsed.
        annotationListEditor.onCollapseChange(annotations.get(1),
                                              false);
        annotationListEditor.onCollapseChange(annotations.get(1),
                                              true);

        // annotation #2 is expanded
        annotationListEditor.onCollapseChange(annotations.get(2),
                                              false);

        // annotation #3 is un-touched. (the by default state is collapsed)

        // reset the items prior the second load
        setupItemInstances();

        // second load.
        annotationListEditor.loadAnnotations(annotations,
                                             annotationSourcesMap);

        // after the reload we should have the following.

        // annotation #0 should be expanded
        verify(itemInstances.get(0),
               times(1)).setCollapsed(false);
        verify(itemInstances.get(0),
               never()).setCollapsed(true);

        // annotation #1 should be collapsed.
        verify(itemInstances.get(1),
               times(1)).setCollapsed(true);
        verify(itemInstances.get(1),
               never()).setCollapsed(false);

        // annotation #2 should be expanded.
        verify(itemInstances.get(2),
               times(1)).setCollapsed(false);
        verify(itemInstances.get(2),
               never()).setCollapsed(true);

        // annotation #3 should be collapsed.
        verify(itemInstances.get(3),
               times(1)).setCollapsed(true);
        verify(itemInstances.get(3),
               never()).setCollapsed(false);
    }

    @Test
    public void testClear() {
        // load the editor.
        annotationListEditor.loadAnnotations(annotations,
                                             annotationSourcesMap);
        // clear the editor.
        annotationListEditor.clear();
        // the view is cleared 1 prior annotations loading + 1 when clear is invoked.
        verify(view,
               times(2)).clear();
        verifyItemsCleared();
    }

    @Test
    public void testDestroy() {
        // load the editor.
        annotationListEditor.loadAnnotations(annotations,
                                             annotationSourcesMap);
        // the managed bean life-cycle destroy method was invoked.
        annotationListEditor.destroy();
        verifyItemsCleared();
    }

    @Test
    public void testRemoveAnnotation() {
        // load the editor
        annotationListEditor.loadAnnotations(annotations,
                                             annotationSourcesMap);
        // remove an arbitrary annotation.
        annotationListEditor.removeAnnotation(annotations.get(2));
        // the corresponding item should have been removed from the view and destroyed.
        verify(view,
               times(1)).removeItem(itemInstances.get(2));
        verify(itemInstance,
               times(1)).destroy(itemInstances.get(2));
    }

    private void verifyItemsCleared() {
        // all the listItems should have been properly destroyed.
        for (AnnotationListItem listItem : itemInstances) {
            verify(itemInstance,
                   times(1)).destroy(listItem);
        }
    }
}