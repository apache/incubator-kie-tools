package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
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
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModal.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraintModalView__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeConstraintModalView> { public interface o_k_w_c_d_c_e_t_l_c_DataTypeConstraintModalViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/types/listview/constraint/DataTypeConstraintModalView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraintModalView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeConstraintModalView.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraintModalView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeConstraintModalView.class, Object.class, View.class, org.uberfire.ext.editor.commons.client.file.popups.elemental2.Elemental2Modal.View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n.constraint-modal-body > .bootstrap-select:not([class*=col-]):not([class*=form-control]):not(.input-group-btn) {\n  width: 100%;\n}\n.constraint-modal-body .select-constraint {\n  background-color: #f1f2f1;\n  border: 1px solid #DDD;\n  padding: 15px;\n  color: #777;\n  margin: 15px;\n}\n.constraint-modal-body .select-constraint .fa,\n.constraint-modal-body .select-constraint span {\n  display: inline-block;\n  vertical-align: middle;\n}\n.constraint-modal-body .select-constraint .fa {\n  font-size: 2em;\n  margin-right: 10px;\n}\n.constraint-modal-body [data-field=\"type\"] {\n  font-weight: bold;\n  opacity: .8;\n}\n.constraint-modal-body [data-field=\"constraint-component-container\"] {\n  border: 1px solid #DDD;\n  margin-top: 15px;\n  min-height: 200px;\n}\n.constraint-modal-body input::-webkit-input-placeholder,\n.constraint-modal-body textarea::-webkit-input-placeholder {\n  font-style: italic;\n}\n.constraint-modal-body input::-moz-placeholder,\n.constraint-modal-body textarea::-moz-placeholder {\n  font-style: italic;\n}\n.constraint-modal-body input:-moz-placeholder,\n.constraint-modal-body textarea:-moz-placeholder {\n  font-style: italic;\n}\n.constraint-modal-body input:-ms-input-placeholder,\n.constraint-modal-body textarea:-ms-input-placeholder {\n  font-style: italic;\n}\n.constraint-modal-body input.fluid-input,\n.constraint-modal-body textarea.fluid-input {\n  width: 100%;\n}\n.constraint-modal-footer {\n  margin-top: -30px;\n  margin-bottom: 10px;\n}\n.constraint-modal-footer [data-field=\"clear-all-anchor\"] {\n  float: left;\n  padding-left: 5px;\n}\n.constraint-modal-footer button.btn {\n  width: 60px;\n}\n\n");
  }

  public DataTypeConstraintModalView createInstance(final ContextManager contextManager) {
    final HTMLDivElement _selectConstraint_8 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _constraintWarningMessage_9 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLButtonElement _closeConstraintWarningMessage_10 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _header_0 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _footer_2 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _body_1 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _componentContainer_3 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLElement _type_7 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
        public Class annotationType() {
          return Named.class;
        }
        public String toString() {
          return "@javax.inject.Named(value=span)";
        }
        public String value() {
          return "span";
        }
    } });
    final HTMLAnchorElement _clearAllAnchor_6 = (HTMLAnchorElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLAnchorElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLButtonElement _okButton_4 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLButtonElement _cancelButton_5 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final DataTypeConstraintModalView instance = new DataTypeConstraintModalView(_header_0, _body_1, _footer_2, _componentContainer_3, _okButton_4, _cancelButton_5, _clearAllAnchor_6, _type_7, _selectConstraint_8, _constraintWarningMessage_9, _closeConstraintWarningMessage_10);
    registerDependentScopedReference(instance, _selectConstraint_8);
    registerDependentScopedReference(instance, _constraintWarningMessage_9);
    registerDependentScopedReference(instance, _closeConstraintWarningMessage_10);
    registerDependentScopedReference(instance, _header_0);
    registerDependentScopedReference(instance, _footer_2);
    registerDependentScopedReference(instance, _body_1);
    registerDependentScopedReference(instance, _componentContainer_3);
    registerDependentScopedReference(instance, _type_7);
    registerDependentScopedReference(instance, _clearAllAnchor_6);
    registerDependentScopedReference(instance, _okButton_4);
    registerDependentScopedReference(instance, _cancelButton_5);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_t_l_c_DataTypeConstraintModalViewTemplateResource templateForDataTypeConstraintModalView = GWT.create(o_k_w_c_d_c_e_t_l_c_DataTypeConstraintModalViewTemplateResource.class);
    Element parentElementForTemplateOfDataTypeConstraintModalView = TemplateUtil.getRootTemplateParentElement(templateForDataTypeConstraintModalView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/DataTypeConstraintModalView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/types/listview/constraint/DataTypeConstraintModalView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypeConstraintModalView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypeConstraintModalView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(11);
    dataFieldMetas.put("header", new DataFieldMeta());
    dataFieldMetas.put("body", new DataFieldMeta());
    dataFieldMetas.put("footer", new DataFieldMeta());
    dataFieldMetas.put("constraint-component-container", new DataFieldMeta());
    dataFieldMetas.put("ok-button", new DataFieldMeta());
    dataFieldMetas.put("cancel-button", new DataFieldMeta());
    dataFieldMetas.put("clear-all-anchor", new DataFieldMeta());
    dataFieldMetas.put("type", new DataFieldMeta());
    dataFieldMetas.put("select-constraint", new DataFieldMeta());
    dataFieldMetas.put("constraint-warning-message", new DataFieldMeta());
    dataFieldMetas.put("close-constraint-warning-message", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/DataTypeConstraintModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintModalView_HTMLDivElement_header(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "header");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/DataTypeConstraintModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintModalView_HTMLDivElement_body(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "body");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/DataTypeConstraintModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintModalView_HTMLDivElement_footer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "footer");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/DataTypeConstraintModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintModalView_HTMLDivElement_componentContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "constraint-component-container");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/DataTypeConstraintModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintModalView_HTMLButtonElement_okButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "ok-button");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/DataTypeConstraintModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintModalView_HTMLButtonElement_cancelButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "cancel-button");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/DataTypeConstraintModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintModalView_HTMLAnchorElement_clearAllAnchor(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "clear-all-anchor");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/DataTypeConstraintModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintModalView_HTMLElement_type(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "type");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/DataTypeConstraintModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintModalView_HTMLDivElement_selectConstraint(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "select-constraint");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/DataTypeConstraintModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintModalView_HTMLDivElement_constraintWarningMessage(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "constraint-warning-message");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/DataTypeConstraintModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintModalView_HTMLButtonElement_closeConstraintWarningMessage(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "close-constraint-warning-message");
    templateFieldsMap.put("header", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintModalView_HTMLDivElement_header(instance))));
    templateFieldsMap.put("body", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintModalView_HTMLDivElement_body(instance))));
    templateFieldsMap.put("footer", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintModalView_HTMLDivElement_footer(instance))));
    templateFieldsMap.put("constraint-component-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintModalView_HTMLDivElement_componentContainer(instance))));
    templateFieldsMap.put("ok-button", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintModalView_HTMLButtonElement_okButton(instance))));
    templateFieldsMap.put("cancel-button", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintModalView_HTMLButtonElement_cancelButton(instance))));
    templateFieldsMap.put("clear-all-anchor", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintModalView_HTMLAnchorElement_clearAllAnchor(instance))));
    templateFieldsMap.put("type", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintModalView_HTMLElement_type(instance))));
    templateFieldsMap.put("select-constraint", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintModalView_HTMLDivElement_selectConstraint(instance))));
    templateFieldsMap.put("constraint-warning-message", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintModalView_HTMLDivElement_constraintWarningMessage(instance))));
    templateFieldsMap.put("close-constraint-warning-message", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintModalView_HTMLButtonElement_closeConstraintWarningMessage(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypeConstraintModalView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("ok-button"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onOkButtonClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("clear-all-anchor"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onClearAllAnchorClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("close-constraint-warning-message"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onCloseConstraintWarningClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("cancel-button"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onCancelButtonClick(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DataTypeConstraintModalView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DataTypeConstraintModalView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  public void invokePostConstructs(final DataTypeConstraintModalView instance) {
    instance.init();
  }

  native static HTMLButtonElement DataTypeConstraintModalView_HTMLButtonElement_closeConstraintWarningMessage(DataTypeConstraintModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView::closeConstraintWarningMessage;
  }-*/;

  native static void DataTypeConstraintModalView_HTMLButtonElement_closeConstraintWarningMessage(DataTypeConstraintModalView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView::closeConstraintWarningMessage = value;
  }-*/;

  native static HTMLDivElement DataTypeConstraintModalView_HTMLDivElement_footer(DataTypeConstraintModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView::footer;
  }-*/;

  native static void DataTypeConstraintModalView_HTMLDivElement_footer(DataTypeConstraintModalView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView::footer = value;
  }-*/;

  native static HTMLButtonElement DataTypeConstraintModalView_HTMLButtonElement_cancelButton(DataTypeConstraintModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView::cancelButton;
  }-*/;

  native static void DataTypeConstraintModalView_HTMLButtonElement_cancelButton(DataTypeConstraintModalView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView::cancelButton = value;
  }-*/;

  native static HTMLDivElement DataTypeConstraintModalView_HTMLDivElement_selectConstraint(DataTypeConstraintModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView::selectConstraint;
  }-*/;

  native static void DataTypeConstraintModalView_HTMLDivElement_selectConstraint(DataTypeConstraintModalView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView::selectConstraint = value;
  }-*/;

  native static HTMLDivElement DataTypeConstraintModalView_HTMLDivElement_header(DataTypeConstraintModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView::header;
  }-*/;

  native static void DataTypeConstraintModalView_HTMLDivElement_header(DataTypeConstraintModalView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView::header = value;
  }-*/;

  native static HTMLDivElement DataTypeConstraintModalView_HTMLDivElement_body(DataTypeConstraintModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView::body;
  }-*/;

  native static void DataTypeConstraintModalView_HTMLDivElement_body(DataTypeConstraintModalView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView::body = value;
  }-*/;

  native static HTMLElement DataTypeConstraintModalView_HTMLElement_type(DataTypeConstraintModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView::type;
  }-*/;

  native static void DataTypeConstraintModalView_HTMLElement_type(DataTypeConstraintModalView instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView::type = value;
  }-*/;

  native static HTMLDivElement DataTypeConstraintModalView_HTMLDivElement_componentContainer(DataTypeConstraintModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView::componentContainer;
  }-*/;

  native static void DataTypeConstraintModalView_HTMLDivElement_componentContainer(DataTypeConstraintModalView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView::componentContainer = value;
  }-*/;

  native static HTMLButtonElement DataTypeConstraintModalView_HTMLButtonElement_okButton(DataTypeConstraintModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView::okButton;
  }-*/;

  native static void DataTypeConstraintModalView_HTMLButtonElement_okButton(DataTypeConstraintModalView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView::okButton = value;
  }-*/;

  native static HTMLAnchorElement DataTypeConstraintModalView_HTMLAnchorElement_clearAllAnchor(DataTypeConstraintModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView::clearAllAnchor;
  }-*/;

  native static void DataTypeConstraintModalView_HTMLAnchorElement_clearAllAnchor(DataTypeConstraintModalView instance, HTMLAnchorElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView::clearAllAnchor = value;
  }-*/;

  native static HTMLDivElement DataTypeConstraintModalView_HTMLDivElement_constraintWarningMessage(DataTypeConstraintModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView::constraintWarningMessage;
  }-*/;

  native static void DataTypeConstraintModalView_HTMLDivElement_constraintWarningMessage(DataTypeConstraintModalView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView::constraintWarningMessage = value;
  }-*/;
}