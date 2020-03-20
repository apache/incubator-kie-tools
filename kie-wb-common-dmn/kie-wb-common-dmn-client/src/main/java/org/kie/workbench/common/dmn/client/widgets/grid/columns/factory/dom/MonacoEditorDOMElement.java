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

package org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.SimplePanel;
import elemental2.dom.Element;
import jsinterop.base.Js;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoPropertiesFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.uberfire.client.views.pfly.monaco.MonacoEditorInitializer;
import org.uberfire.client.views.pfly.monaco.jsinterop.Monaco;
import org.uberfire.client.views.pfly.monaco.jsinterop.MonacoStandaloneCodeEditor;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.BaseDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static com.google.gwt.dom.client.Style.Unit.PCT;
import static com.google.gwt.dom.client.Style.Unit.PX;

public class MonacoEditorDOMElement extends BaseDOMElement<String, MonacoEditorWidget> implements TakesValue<String>,
                                                                                                  Focusable {

    private final SessionManager sessionManager;
    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final Function<GridCellTuple, Command> hasNoValueCommand;
    private final Function<GridCellValueTuple, Command> hasValueCommand;

    private String originalValue;

    public MonacoEditorDOMElement(final MonacoEditorWidget widget,
                                  final GridLayer gridLayer,
                                  final GridWidget gridWidget,
                                  final SessionManager sessionManager,
                                  final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                  final Function<GridCellTuple, Command> hasNoValueCommand,
                                  final Function<GridCellValueTuple, Command> hasValueCommand) {
        super(widget,
              gridLayer,
              gridWidget);
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.hasNoValueCommand = hasNoValueCommand;
        this.hasValueCommand = hasValueCommand;
    }

    public void setupElements() {
        setupContainerComponent();
        setupInternalComponent();
    }

    void setupContainerComponent() {

        final SimplePanel container = getContainer();
        final Style style = container.getElement().getStyle();

        style.setPadding(5, PX);

        container.setWidget(widget);
    }

    void setupInternalComponent() {

        final Style style = widget.getElement().getStyle();

        style.setWidth(100, PCT);
        style.setHeight(100, PCT);

        makeMonacoEditorInitializer().require(onMonacoLoaded());
    }

    Consumer<Monaco> onMonacoLoaded() {
        final MonacoPropertiesFactory properties = makeMonacoPropertiesFactory();
        return monaco -> {
            final MonacoStandaloneCodeEditor codeEditor = monaco.editor.create(uncheckedCast(widget.getElement()), properties.getConstructionOptions());

            codeEditor.onKeyDown(getOnKeyDown(codeEditor));
            codeEditor.onDidBlurEditorWidget(getWidgetTrigger(getBlurEvent()));

            widget.setCodeEditor(codeEditor);
            widget.setFocus(true);
        };
    }

    MonacoStandaloneCodeEditor.CallbackFunction getOnKeyDown(final MonacoStandaloneCodeEditor codeEditor) {
        return event -> {
            boolean isSuggestWidgetVisible = codeEditor.isSuggestWidgetVisible();
            final boolean isEsc = event.getKeyCode() == 9;
            if (isSuggestWidgetVisible && isEsc) {
                codeEditor.trigger("keyboard", "cursorHome");
                codeEditor.trigger("keyboard", "cursorEnd");

                event.stopPropagation();
                event.preventDefault();
            }
        };
    }

    @Override
    public void initialise(final GridBodyCellRenderContext context) {
        transform(context);
    }

    @Override
    public void setValue(final String value) {
        getWidget().setValue(value);
        this.originalValue = value;
    }

    @Override
    public String getValue() {
        return getWidget().getValue();
    }

    @Override
    public int getTabIndex() {
        return getWidget().getTabIndex();
    }

    @Override
    public void setAccessKey(final char key) {
        getWidget().setAccessKey(key);
    }

    @Override
    public void setFocus(final boolean focused) {
        getWidget().setFocus(focused);
    }

    @Override
    public void setTabIndex(final int index) {
        getWidget().setTabIndex(index);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void flush(final String value) {

        widget.getCodeEditor().ifPresent(c -> c.dispose());

        if (Objects.equals(value, originalValue)) {
            return;
        }

        final int rowIndex = context.getRowIndex();
        final int columnIndex = context.getColumnIndex();

        if (StringUtils.isEmpty(value)) {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          hasNoValueCommand.apply(new GridCellTuple(rowIndex,
                                                                                    columnIndex,
                                                                                    gridWidget)));
        } else {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          hasValueCommand.apply(new GridCellValueTuple<>(rowIndex,
                                                                                         columnIndex,
                                                                                         gridWidget,
                                                                                         new BaseGridCellValue<>(value))));
        }
    }

    MonacoStandaloneCodeEditor.CallbackFunction getWidgetTrigger(final NativeEvent nativeEvent) {
        return (e) -> fireNativeEvent(nativeEvent, widget);
    }

    void fireNativeEvent(final NativeEvent nativeEvent,
                         final HasHandlers handlerSource) {
        DomEvent.fireNativeEvent(nativeEvent, handlerSource);
    }

    NativeEvent getBlurEvent() {
        return Document.get().createBlurEvent();
    }

    Element uncheckedCast(final com.google.gwt.user.client.Element element) {
        return Js.uncheckedCast(element);
    }

    MonacoPropertiesFactory makeMonacoPropertiesFactory() {
        return new MonacoPropertiesFactory();
    }

    MonacoEditorInitializer makeMonacoEditorInitializer() {
        return new MonacoEditorInitializer();
    }
}
