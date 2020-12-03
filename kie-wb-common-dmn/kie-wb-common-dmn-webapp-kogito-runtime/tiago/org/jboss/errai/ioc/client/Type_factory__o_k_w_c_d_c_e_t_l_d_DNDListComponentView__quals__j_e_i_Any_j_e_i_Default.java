package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLDivElement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListComponent.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListComponentView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_t_l_d_DNDListComponentView__quals__j_e_i_Any_j_e_i_Default extends Factory<DNDListComponentView> { public interface o_k_w_c_d_c_e_t_l_d_DNDListComponentViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/types/listview/draganddrop/DNDListComponentView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_t_l_d_DNDListComponentView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DNDListComponentView.class, "Type_factory__o_k_w_c_d_c_e_t_l_d_DNDListComponentView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DNDListComponentView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"DNDListComponentView.\"] {\n  height: 100%;\n}\n[data-i18n-prefix=\"DNDListComponentView.\"] .kie-dnd-drag-area {\n  width: 100%;\n  height: 100%;\n  position: relative;\n  background: #F7F7F7;\n  box-shadow: 0 0 0 1px inset #e2e4e3;\n  overflow: hidden;\n}\n[data-i18n-prefix=\"DNDListComponentView.\"] .kie-dnd-drag-area .kie-dnd-draggable {\n  width: 100%;\n  right: 0;\n  height: 70px;\n  position: absolute;\n  z-index: 1;\n  -webkit-touch-callout: none;\n  -webkit-user-select: none;\n  -khtml-user-select: none;\n  -moz-user-select: -moz-none;\n  -ms-user-select: none;\n  user-select: none;\n  margin: 0;\n  padding: 0;\n  background: #FFF;\n}\n[data-i18n-prefix=\"DNDListComponentView.\"] .kie-dnd-drag-area .kie-dnd-draggable.kie-level-highlight:after {\n  content: \"\";\n  background: #0270b1;\n  position: absolute;\n  top: -1px;\n  left: 0;\n  width: 4px;\n  height: 70px;\n}\n[data-i18n-prefix=\"DNDListComponentView.\"] .kie-dnd-drag-area .kie-dnd-draggable .kie-level-background-line {\n  background: #0270b1;\n  position: absolute;\n  top: -1px;\n  left: 0;\n  width: 4px;\n}\n[data-i18n-prefix=\"DNDListComponentView.\"] .kie-dnd-drag-area .kie-dnd-draggable.kie-dnd-current-dragging {\n  z-index: 9;\n  box-shadow: rgba(0, 0, 0, 0.25) 3px 3px 3px;\n}\n[data-i18n-prefix=\"DNDListComponentView.\"] .kie-dnd-drag-area .kie-dnd-draggable.kie-dnd-hover {\n  opacity: .75;\n  background-color: #dce7ef;\n}\n[data-i18n-prefix=\"DNDListComponentView.\"] .kie-dnd-drag-area .kie-dnd-draggable:hover .kie-dnd-grip,\n[data-i18n-prefix=\"DNDListComponentView.\"] .kie-dnd-drag-area .kie-dnd-draggable.kie-dnd-current-dragging .kie-dnd-grip {\n  display: block;\n}\n[data-i18n-prefix=\"DNDListComponentView.\"] .kie-dnd-drag-area .kie-dnd-draggable .kie-dnd-grip {\n  display: none;\n  position: absolute;\n  font-size: 18px;\n  height: 100%;\n  width: 30px;\n  cursor: grab;\n  z-index: 1;\n  color: #AAA;\n  left: 0;\n}\n[data-i18n-prefix=\"DNDListComponentView.\"] .kie-dnd-drag-area .kie-dnd-draggable .kie-dnd-grip .fa.fa-ellipsis-v {\n  top: calc(50% - 8px);\n  pointer-events: none;\n  position: absolute;\n  left: 11px;\n}\n[data-i18n-prefix=\"DNDListComponentView.\"] .kie-dnd-drag-area .kie-dnd-draggable .kie-dnd-grip .fa.fa-ellipsis-v + .fa.fa-ellipsis-v {\n  left: 17px;\n}\n[data-i18n-prefix=\"DNDListComponentView.\"] .kie-dnd-drag-area .kie-dnd-draggable .kie-dnd-grip:active {\n  cursor: grabbing;\n}\n\n");
  }

  public DNDListComponentView createInstance(final ContextManager contextManager) {
    final HTMLDivElement _dragArea_0 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final DNDListComponentView instance = new DNDListComponentView(_dragArea_0);
    registerDependentScopedReference(instance, _dragArea_0);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_t_l_d_DNDListComponentViewTemplateResource templateForDNDListComponentView = GWT.create(o_k_w_c_d_c_e_t_l_d_DNDListComponentViewTemplateResource.class);
    Element parentElementForTemplateOfDNDListComponentView = TemplateUtil.getRootTemplateParentElement(templateForDNDListComponentView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/types/listview/draganddrop/DNDListComponentView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/types/listview/draganddrop/DNDListComponentView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDNDListComponentView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDNDListComponentView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("drag-area", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListComponentView", "org/kie/workbench/common/dmn/client/editors/types/listview/draganddrop/DNDListComponentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DNDListComponentView_HTMLDivElement_dragArea(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "drag-area");
    templateFieldsMap.put("drag-area", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DNDListComponentView_HTMLDivElement_dragArea(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDNDListComponentView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DNDListComponentView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DNDListComponentView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLDivElement DNDListComponentView_HTMLDivElement_dragArea(DNDListComponentView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListComponentView::dragArea;
  }-*/;

  native static void DNDListComponentView_HTMLDivElement_dragArea(DNDListComponentView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListComponentView::dragArea = value;
  }-*/;
}