package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
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
import org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModal.View;
import org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModalView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_i_m_IncludedModelModalView__quals__j_e_i_Any_j_e_i_Default extends Factory<IncludedModelModalView> { public interface o_k_w_c_d_c_e_i_m_IncludedModelModalViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/included/modal/IncludedModelModalView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_i_m_IncludedModelModalView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(IncludedModelModalView.class, "Type_factory__o_k_w_c_d_c_e_i_m_IncludedModelModalView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { IncludedModelModalView.class, Object.class, View.class, org.uberfire.ext.editor.commons.client.file.popups.elemental2.Elemental2Modal.View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *       http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n.kie-included-model-modal-body {\n  min-height: 230px;\n}\n.kie-included-model-modal-body p {\n  margin: 0;\n}\n.kie-included-model-modal-body label {\n  font-weight: 600;\n  color: #555;\n  margin: 15px 0 5px;\n}\n.kie-included-model-modal-footer .btn {\n  padding: 2px 15px;\n}\n\n");
  }

  public IncludedModelModalView createInstance(final ContextManager contextManager) {
    final HTMLButtonElement _includeButton_5 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _footer_2 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _body_1 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLInputElement _modelNameInput_4 = (HTMLInputElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLInputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _dropdown_3 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _header_0 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLButtonElement _cancelButton_6 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final IncludedModelModalView instance = new IncludedModelModalView(_header_0, _body_1, _footer_2, _dropdown_3, _modelNameInput_4, _includeButton_5, _cancelButton_6);
    registerDependentScopedReference(instance, _includeButton_5);
    registerDependentScopedReference(instance, _footer_2);
    registerDependentScopedReference(instance, _body_1);
    registerDependentScopedReference(instance, _modelNameInput_4);
    registerDependentScopedReference(instance, _dropdown_3);
    registerDependentScopedReference(instance, _header_0);
    registerDependentScopedReference(instance, _cancelButton_6);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_i_m_IncludedModelModalViewTemplateResource templateForIncludedModelModalView = GWT.create(o_k_w_c_d_c_e_i_m_IncludedModelModalViewTemplateResource.class);
    Element parentElementForTemplateOfIncludedModelModalView = TemplateUtil.getRootTemplateParentElement(templateForIncludedModelModalView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/included/modal/IncludedModelModalView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/included/modal/IncludedModelModalView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfIncludedModelModalView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfIncludedModelModalView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(7);
    dataFieldMetas.put("header", new DataFieldMeta());
    dataFieldMetas.put("body", new DataFieldMeta());
    dataFieldMetas.put("footer", new DataFieldMeta());
    dataFieldMetas.put("dropdown", new DataFieldMeta());
    dataFieldMetas.put("model-name", new DataFieldMeta());
    dataFieldMetas.put("include", new DataFieldMeta());
    dataFieldMetas.put("cancel", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModalView", "org/kie/workbench/common/dmn/client/editors/included/modal/IncludedModelModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(IncludedModelModalView_HTMLDivElement_header(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "header");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModalView", "org/kie/workbench/common/dmn/client/editors/included/modal/IncludedModelModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(IncludedModelModalView_HTMLDivElement_body(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "body");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModalView", "org/kie/workbench/common/dmn/client/editors/included/modal/IncludedModelModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(IncludedModelModalView_HTMLDivElement_footer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "footer");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModalView", "org/kie/workbench/common/dmn/client/editors/included/modal/IncludedModelModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(IncludedModelModalView_HTMLDivElement_dropdown(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "dropdown");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModalView", "org/kie/workbench/common/dmn/client/editors/included/modal/IncludedModelModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(IncludedModelModalView_HTMLInputElement_modelNameInput(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "model-name");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModalView", "org/kie/workbench/common/dmn/client/editors/included/modal/IncludedModelModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(IncludedModelModalView_HTMLButtonElement_includeButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "include");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModalView", "org/kie/workbench/common/dmn/client/editors/included/modal/IncludedModelModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(IncludedModelModalView_HTMLButtonElement_cancelButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "cancel");
    templateFieldsMap.put("header", ElementWrapperWidget.getWidget(TemplateUtil.asElement(IncludedModelModalView_HTMLDivElement_header(instance))));
    templateFieldsMap.put("body", ElementWrapperWidget.getWidget(TemplateUtil.asElement(IncludedModelModalView_HTMLDivElement_body(instance))));
    templateFieldsMap.put("footer", ElementWrapperWidget.getWidget(TemplateUtil.asElement(IncludedModelModalView_HTMLDivElement_footer(instance))));
    templateFieldsMap.put("dropdown", ElementWrapperWidget.getWidget(TemplateUtil.asElement(IncludedModelModalView_HTMLDivElement_dropdown(instance))));
    templateFieldsMap.put("model-name", ElementWrapperWidget.getWidget(TemplateUtil.asElement(IncludedModelModalView_HTMLInputElement_modelNameInput(instance))));
    templateFieldsMap.put("include", ElementWrapperWidget.getWidget(TemplateUtil.asElement(IncludedModelModalView_HTMLButtonElement_includeButton(instance))));
    templateFieldsMap.put("cancel", ElementWrapperWidget.getWidget(TemplateUtil.asElement(IncludedModelModalView_HTMLButtonElement_cancelButton(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfIncludedModelModalView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("cancel"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onCancelButtonClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("include"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onIncludeButtonClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("model-name"), new KeyUpHandler() {
      public void onKeyUp(KeyUpEvent event) {
        instance.onModelNameInputChanged(event);
      }
    }, KeyUpEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((IncludedModelModalView) instance, contextManager);
  }

  public void destroyInstanceHelper(final IncludedModelModalView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLDivElement IncludedModelModalView_HTMLDivElement_header(IncludedModelModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModalView::header;
  }-*/;

  native static void IncludedModelModalView_HTMLDivElement_header(IncludedModelModalView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModalView::header = value;
  }-*/;

  native static HTMLButtonElement IncludedModelModalView_HTMLButtonElement_includeButton(IncludedModelModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModalView::includeButton;
  }-*/;

  native static void IncludedModelModalView_HTMLButtonElement_includeButton(IncludedModelModalView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModalView::includeButton = value;
  }-*/;

  native static HTMLButtonElement IncludedModelModalView_HTMLButtonElement_cancelButton(IncludedModelModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModalView::cancelButton;
  }-*/;

  native static void IncludedModelModalView_HTMLButtonElement_cancelButton(IncludedModelModalView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModalView::cancelButton = value;
  }-*/;

  native static HTMLDivElement IncludedModelModalView_HTMLDivElement_footer(IncludedModelModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModalView::footer;
  }-*/;

  native static void IncludedModelModalView_HTMLDivElement_footer(IncludedModelModalView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModalView::footer = value;
  }-*/;

  native static HTMLInputElement IncludedModelModalView_HTMLInputElement_modelNameInput(IncludedModelModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModalView::modelNameInput;
  }-*/;

  native static void IncludedModelModalView_HTMLInputElement_modelNameInput(IncludedModelModalView instance, HTMLInputElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModalView::modelNameInput = value;
  }-*/;

  native static HTMLDivElement IncludedModelModalView_HTMLDivElement_body(IncludedModelModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModalView::body;
  }-*/;

  native static void IncludedModelModalView_HTMLDivElement_body(IncludedModelModalView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModalView::body = value;
  }-*/;

  native static HTMLDivElement IncludedModelModalView_HTMLDivElement_dropdown(IncludedModelModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModalView::dropdown;
  }-*/;

  native static void IncludedModelModalView_HTMLDivElement_dropdown(IncludedModelModalView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModalView::dropdown = value;
  }-*/;
}