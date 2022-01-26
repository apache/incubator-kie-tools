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

package org.kie.workbench.common.stunner.kogito.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import elemental2.core.Reflect;
import elemental2.dom.DomGlobal;
import io.crysknife.annotation.Application;
import jsinterop.annotations.JsFunction;
import org.gwtbootstrap3.client.GwtBootstrap3EntryPoint;
import org.gwtbootstrap3.extras.card.client.CardEntryPoint;
import org.gwtbootstrap3.extras.datepicker.client.DatePickerEntryPoint;
import org.gwtbootstrap3.extras.datetimepicker.client.DateTimePickerEntryPoint;
import org.gwtbootstrap3.extras.notify.client.NotifyEntryPoint;
import org.gwtbootstrap3.extras.select.client.SelectEntryPoint;
import org.gwtbootstrap3.extras.slider.client.SliderEntryPoint;
import org.gwtbootstrap3.extras.tagsinput.client.TagsInputEntryPoint;
import org.gwtbootstrap3.extras.toggleswitch.client.ToggleSwitchEntryPoint;
import org.gwtproject.resources.client.GWT3Resources;
import org.kie.workbench.common.forms.dynamic.client.DynamicRendererEntryPoint;
import org.kie.workbench.common.stunner.bpmn.StunnerBPMNEntryPoint;
import org.kie.workbench.common.stunner.client.lienzo.StunnerLienzoCore;
import org.kie.workbench.common.stunner.kogito.client.editor.BPMNDiagramEditor;
import org.kie.workbench.common.widgets.client.KieWorkbenchWidgetsCommonEntryPoint;
import org.treblereel.j2cl.processors.annotations.GWT3EntryPoint;
import org.uberfire.client.views.pfly.PatternFlyEntryPoint;
import org.uberfire.client.workbench.WorkbenchEntryPoint;
import org.uberfire.ext.layout.editor.client.LayoutEditorEntryPoint;
import org.uberfire.ext.widgets.common.client.CommonsEntryPoint;
import org.uberfire.ext.widgets.core.client.CoreEntryPoint;
import org.uberfire.ext.widgets.table.client.TableEntryPoint;

@Application
@GWT3Resources(
        cssResource = @GWT3Resources.CssResource(
                conversionMode = "strict"
        )
)
public class MainEntryPoint {

    @Inject
    private BPMNDiagramEditor bpmnDiagramEditor;

    @Inject
    private WorkbenchEntryPoint workbenchEntryPoint;

    @Inject
    private DynamicRendererEntryPoint dynamicRendererEntryPoint;

    @Inject
    private StunnerBPMNEntryPoint stunnerBPMNEntryPoint;

    @Inject
    private StunnerLienzoCore stunnerLienzoCore;

    @Inject
    private KieWorkbenchWidgetsCommonEntryPoint kieWorkbenchWidgetsCommonEntryPoint;

    @Inject
    private LayoutEditorEntryPoint layoutEditorEntryPoint;

    @Inject
    private CoreEntryPoint coreEntryPoint;

    @GWT3EntryPoint
    public void onModuleLoad() {
        DomGlobal.console.log("init BPMNDiagramEditor");
        new MainEntryPointBootstrap(this).initialize();
    }

    @PostConstruct
    public void initialize() {
        new GwtBootstrap3EntryPoint().loadJs();
        new PatternFlyEntryPoint().init();
        new CardEntryPoint().onModuleLoad();
        new DatePickerEntryPoint().onModuleLoad();
        new DateTimePickerEntryPoint().onModuleLoad();
        new NotifyEntryPoint().onModuleLoad();
        new SelectEntryPoint().onModuleLoad();
        new SliderEntryPoint().onModuleLoad();
        new TagsInputEntryPoint().onModuleLoad();
        new ToggleSwitchEntryPoint().onModuleLoad();

        new TableEntryPoint().startApp();

        new CommonsEntryPoint().startApp();

        stunnerBPMNEntryPoint.init();
        dynamicRendererEntryPoint.init();
        stunnerLienzoCore.init();
        kieWorkbenchWidgetsCommonEntryPoint.startApp();
        layoutEditorEntryPoint.init();
        coreEntryPoint.startApp();


        workbenchEntryPoint.afterInitialization();



        Reflect.set(DomGlobal.window, "serialize", (Serialize) () -> bpmnDiagramEditor.getXML());
        Reflect.set(DomGlobal.window,"deserialize", (Deserialize) xml -> bpmnDiagramEditor.setXML(xml));
    }

    @FunctionalInterface
    @JsFunction
    public interface Serialize {
        String onInvoke();
    }

    @FunctionalInterface
    @JsFunction
    public interface Deserialize {
        void onInvoke(String xml);
    }

}
