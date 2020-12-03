package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLInputElement;
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
import org.kie.workbench.common.dmn.client.editors.types.listview.common.SmallSwitchComponent.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.SmallSwitchComponentView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_SmallSwitchComponentView__quals__j_e_i_Any_j_e_i_Default extends Factory<SmallSwitchComponentView> { public interface o_k_w_c_d_c_e_t_l_c_SmallSwitchComponentViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/types/listview/common/SmallSwitchComponentView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_t_l_c_SmallSwitchComponentView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SmallSwitchComponentView.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_SmallSwitchComponentView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SmallSwitchComponentView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2018 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *       http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"SmallSwitchComponentView.\"] .kie-small-switch {\n  position: relative;\n  display: inline-block;\n  width: 32px;\n  height: 14px;\n}\n[data-i18n-prefix=\"SmallSwitchComponentView.\"] .kie-small-switch input {\n  opacity: 0;\n  width: 0;\n  height: 0;\n}\n[data-i18n-prefix=\"SmallSwitchComponentView.\"] .kie-small-switch [data-field=\"checkbox-text\"] {\n  position: absolute;\n  left: 38px;\n  font-size: 10px;\n  color: #555;\n}\n[data-i18n-prefix=\"SmallSwitchComponentView.\"] .kie-small-switch .kie-slider {\n  position: absolute;\n  cursor: pointer;\n  top: 0;\n  left: 0;\n  right: 0;\n  bottom: 0;\n  background-color: #ccc;\n  -webkit-transition: .4s;\n  transition: .4s;\n  border-radius: 14px;\n}\n[data-i18n-prefix=\"SmallSwitchComponentView.\"] .kie-small-switch .kie-slider:before {\n  position: absolute;\n  content: \"\";\n  height: 10px;\n  width: 10px;\n  left: 2px;\n  bottom: 2px;\n  background-color: white;\n  -webkit-transition: .4s;\n  transition: .4s;\n  border-radius: 50%;\n}\n[data-i18n-prefix=\"SmallSwitchComponentView.\"] .kie-small-switch input:checked + .kie-slider {\n  background-color: #0088ce;\n}\n[data-i18n-prefix=\"SmallSwitchComponentView.\"] .kie-small-switch input:focus + .kie-slider {\n  box-shadow: inset 0 1px 1px rgba(3, 3, 3, 0.075), 0 0 8px #0088ce;\n}\n[data-i18n-prefix=\"SmallSwitchComponentView.\"] .kie-small-switch input:checked + .kie-slider:before {\n  -webkit-transform: translateX(18px);\n  -ms-transform: translateX(18px);\n  transform: translateX(18px);\n}\n\n");
  }

  public SmallSwitchComponentView createInstance(final ContextManager contextManager) {
    final HTMLInputElement _inputCheckbox_0 = (HTMLInputElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLInputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final SmallSwitchComponentView instance = new SmallSwitchComponentView(_inputCheckbox_0);
    registerDependentScopedReference(instance, _inputCheckbox_0);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_t_l_c_SmallSwitchComponentViewTemplateResource templateForSmallSwitchComponentView = GWT.create(o_k_w_c_d_c_e_t_l_c_SmallSwitchComponentViewTemplateResource.class);
    Element parentElementForTemplateOfSmallSwitchComponentView = TemplateUtil.getRootTemplateParentElement(templateForSmallSwitchComponentView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/types/listview/common/SmallSwitchComponentView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/types/listview/common/SmallSwitchComponentView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSmallSwitchComponentView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSmallSwitchComponentView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("input-checkbox", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.common.SmallSwitchComponentView", "org/kie/workbench/common/dmn/client/editors/types/listview/common/SmallSwitchComponentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(SmallSwitchComponentView_HTMLInputElement_inputCheckbox(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "input-checkbox");
    templateFieldsMap.put("input-checkbox", ElementWrapperWidget.getWidget(TemplateUtil.asElement(SmallSwitchComponentView_HTMLInputElement_inputCheckbox(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSmallSwitchComponentView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("input-checkbox"), new ChangeHandler() {
      public void onChange(ChangeEvent event) {
        instance.onInputCheckBoxChange(event);
      }
    }, ChangeEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((SmallSwitchComponentView) instance, contextManager);
  }

  public void destroyInstanceHelper(final SmallSwitchComponentView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLInputElement SmallSwitchComponentView_HTMLInputElement_inputCheckbox(SmallSwitchComponentView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.common.SmallSwitchComponentView::inputCheckbox;
  }-*/;

  native static void SmallSwitchComponentView_HTMLInputElement_inputCheckbox(SmallSwitchComponentView instance, HTMLInputElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.common.SmallSwitchComponentView::inputCheckbox = value;
  }-*/;
}