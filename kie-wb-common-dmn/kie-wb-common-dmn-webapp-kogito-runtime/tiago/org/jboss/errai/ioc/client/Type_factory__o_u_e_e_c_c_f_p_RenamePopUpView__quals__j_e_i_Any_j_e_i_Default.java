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
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
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
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpPresenter.View;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView;

public class Type_factory__o_u_e_e_c_c_f_p_RenamePopUpView__quals__j_e_i_Any_j_e_i_Default extends Factory<RenamePopUpView> { public interface o_u_e_e_c_c_f_p_RenamePopUpViewTemplateResource extends Template, ClientBundle { @Source("org/uberfire/ext/editor/commons/client/file/popups/RenamePopUpView.html") public TextResource getContents(); }
  public Type_factory__o_u_e_e_c_c_f_p_RenamePopUpView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(RenamePopUpView.class, "Type_factory__o_u_e_e_c_c_f_p_RenamePopUpView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { RenamePopUpView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2018 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *       http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n.modal-dialog .kie-btn[hidden] {\n  display: none;\n}\n\n");
  }

  public RenamePopUpView createInstance(final ContextManager contextManager) {
    final HTMLButtonElement _cancel_2 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLElement _errorMessage_8 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
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
    final HTMLDivElement _body_0 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final TranslationService _translationService_9 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final HTMLInputElement _newNameTextBox_6 = (HTMLInputElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLInputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLButtonElement _rename_3 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _footer_5 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _view_1 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _error_7 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLButtonElement _saveAndRename_4 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final RenamePopUpView instance = new RenamePopUpView(_body_0, _view_1, _cancel_2, _rename_3, _saveAndRename_4, _footer_5, _newNameTextBox_6, _error_7, _errorMessage_8, _translationService_9);
    registerDependentScopedReference(instance, _cancel_2);
    registerDependentScopedReference(instance, _errorMessage_8);
    registerDependentScopedReference(instance, _body_0);
    registerDependentScopedReference(instance, _translationService_9);
    registerDependentScopedReference(instance, _newNameTextBox_6);
    registerDependentScopedReference(instance, _rename_3);
    registerDependentScopedReference(instance, _footer_5);
    registerDependentScopedReference(instance, _view_1);
    registerDependentScopedReference(instance, _error_7);
    registerDependentScopedReference(instance, _saveAndRename_4);
    setIncompleteInstance(instance);
    o_u_e_e_c_c_f_p_RenamePopUpViewTemplateResource templateForRenamePopUpView = GWT.create(o_u_e_e_c_c_f_p_RenamePopUpViewTemplateResource.class);
    Element parentElementForTemplateOfRenamePopUpView = TemplateUtil.getRootTemplateParentElement(templateForRenamePopUpView.getContents().getText(), "org/uberfire/ext/editor/commons/client/file/popups/RenamePopUpView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/editor/commons/client/file/popups/RenamePopUpView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfRenamePopUpView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfRenamePopUpView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(9);
    dataFieldMetas.put("modal-view", new DataFieldMeta());
    dataFieldMetas.put("modal-body", new DataFieldMeta());
    dataFieldMetas.put("modal-footer", new DataFieldMeta());
    dataFieldMetas.put("cancel", new DataFieldMeta());
    dataFieldMetas.put("rename", new DataFieldMeta());
    dataFieldMetas.put("saveAndRename", new DataFieldMeta());
    dataFieldMetas.put("newNameTextBox", new DataFieldMeta());
    dataFieldMetas.put("error", new DataFieldMeta());
    dataFieldMetas.put("errorMessage", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView", "org/uberfire/ext/editor/commons/client/file/popups/RenamePopUpView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(RenamePopUpView_HTMLDivElement_view(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "modal-view");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView", "org/uberfire/ext/editor/commons/client/file/popups/RenamePopUpView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(RenamePopUpView_HTMLDivElement_body(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "modal-body");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView", "org/uberfire/ext/editor/commons/client/file/popups/RenamePopUpView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(RenamePopUpView_HTMLDivElement_footer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "modal-footer");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView", "org/uberfire/ext/editor/commons/client/file/popups/RenamePopUpView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(RenamePopUpView_HTMLButtonElement_cancel(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "cancel");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView", "org/uberfire/ext/editor/commons/client/file/popups/RenamePopUpView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(RenamePopUpView_HTMLButtonElement_rename(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "rename");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView", "org/uberfire/ext/editor/commons/client/file/popups/RenamePopUpView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(RenamePopUpView_HTMLButtonElement_saveAndRename(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "saveAndRename");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView", "org/uberfire/ext/editor/commons/client/file/popups/RenamePopUpView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(RenamePopUpView_HTMLInputElement_newNameTextBox(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "newNameTextBox");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView", "org/uberfire/ext/editor/commons/client/file/popups/RenamePopUpView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(RenamePopUpView_HTMLDivElement_error(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "error");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView", "org/uberfire/ext/editor/commons/client/file/popups/RenamePopUpView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(RenamePopUpView_HTMLElement_errorMessage(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "errorMessage");
    templateFieldsMap.put("modal-view", ElementWrapperWidget.getWidget(TemplateUtil.asElement(RenamePopUpView_HTMLDivElement_view(instance))));
    templateFieldsMap.put("modal-body", ElementWrapperWidget.getWidget(TemplateUtil.asElement(RenamePopUpView_HTMLDivElement_body(instance))));
    templateFieldsMap.put("modal-footer", ElementWrapperWidget.getWidget(TemplateUtil.asElement(RenamePopUpView_HTMLDivElement_footer(instance))));
    templateFieldsMap.put("cancel", ElementWrapperWidget.getWidget(TemplateUtil.asElement(RenamePopUpView_HTMLButtonElement_cancel(instance))));
    templateFieldsMap.put("rename", ElementWrapperWidget.getWidget(TemplateUtil.asElement(RenamePopUpView_HTMLButtonElement_rename(instance))));
    templateFieldsMap.put("saveAndRename", ElementWrapperWidget.getWidget(TemplateUtil.asElement(RenamePopUpView_HTMLButtonElement_saveAndRename(instance))));
    templateFieldsMap.put("newNameTextBox", ElementWrapperWidget.getWidget(TemplateUtil.asElement(RenamePopUpView_HTMLInputElement_newNameTextBox(instance))));
    templateFieldsMap.put("error", ElementWrapperWidget.getWidget(TemplateUtil.asElement(RenamePopUpView_HTMLDivElement_error(instance))));
    templateFieldsMap.put("errorMessage", ElementWrapperWidget.getWidget(TemplateUtil.asElement(RenamePopUpView_HTMLElement_errorMessage(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfRenamePopUpView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("cancel"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        RenamePopUpView_cancelCommand_ClickEvent(instance, event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("newNameTextBox"), new KeyUpHandler() {
      public void onKeyUp(KeyUpEvent event) {
        instance.onNewFileNameChange(event);
      }
    }, KeyUpEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("saveAndRename"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        RenamePopUpView_onSaveAndRename_ClickEvent(instance, event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("rename"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        RenamePopUpView_onRename_ClickEvent(instance, event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((RenamePopUpView) instance, contextManager);
  }

  public void destroyInstanceHelper(final RenamePopUpView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLDivElement RenamePopUpView_HTMLDivElement_view(RenamePopUpView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView::view;
  }-*/;

  native static void RenamePopUpView_HTMLDivElement_view(RenamePopUpView instance, HTMLDivElement value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView::view = value;
  }-*/;

  native static HTMLButtonElement RenamePopUpView_HTMLButtonElement_cancel(RenamePopUpView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView::cancel;
  }-*/;

  native static void RenamePopUpView_HTMLButtonElement_cancel(RenamePopUpView instance, HTMLButtonElement value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView::cancel = value;
  }-*/;

  native static HTMLButtonElement RenamePopUpView_HTMLButtonElement_saveAndRename(RenamePopUpView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView::saveAndRename;
  }-*/;

  native static void RenamePopUpView_HTMLButtonElement_saveAndRename(RenamePopUpView instance, HTMLButtonElement value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView::saveAndRename = value;
  }-*/;

  native static HTMLInputElement RenamePopUpView_HTMLInputElement_newNameTextBox(RenamePopUpView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView::newNameTextBox;
  }-*/;

  native static void RenamePopUpView_HTMLInputElement_newNameTextBox(RenamePopUpView instance, HTMLInputElement value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView::newNameTextBox = value;
  }-*/;

  native static HTMLElement RenamePopUpView_HTMLElement_errorMessage(RenamePopUpView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView::errorMessage;
  }-*/;

  native static void RenamePopUpView_HTMLElement_errorMessage(RenamePopUpView instance, HTMLElement value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView::errorMessage = value;
  }-*/;

  native static HTMLDivElement RenamePopUpView_HTMLDivElement_body(RenamePopUpView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView::body;
  }-*/;

  native static void RenamePopUpView_HTMLDivElement_body(RenamePopUpView instance, HTMLDivElement value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView::body = value;
  }-*/;

  native static HTMLDivElement RenamePopUpView_HTMLDivElement_footer(RenamePopUpView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView::footer;
  }-*/;

  native static void RenamePopUpView_HTMLDivElement_footer(RenamePopUpView instance, HTMLDivElement value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView::footer = value;
  }-*/;

  native static HTMLButtonElement RenamePopUpView_HTMLButtonElement_rename(RenamePopUpView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView::rename;
  }-*/;

  native static void RenamePopUpView_HTMLButtonElement_rename(RenamePopUpView instance, HTMLButtonElement value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView::rename = value;
  }-*/;

  native static HTMLDivElement RenamePopUpView_HTMLDivElement_error(RenamePopUpView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView::error;
  }-*/;

  native static void RenamePopUpView_HTMLDivElement_error(RenamePopUpView instance, HTMLDivElement value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView::error = value;
  }-*/;

  public native static void RenamePopUpView_onRename_ClickEvent(RenamePopUpView instance, ClickEvent a0) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView::onRename(Lcom/google/gwt/event/dom/client/ClickEvent;)(a0);
  }-*/;

  public native static void RenamePopUpView_cancelCommand_ClickEvent(RenamePopUpView instance, ClickEvent a0) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView::cancelCommand(Lcom/google/gwt/event/dom/client/ClickEvent;)(a0);
  }-*/;

  public native static void RenamePopUpView_onSaveAndRename_ClickEvent(RenamePopUpView instance, ClickEvent a0) /*-{
    instance.@org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView::onSaveAndRename(Lcom/google/gwt/event/dom/client/ClickEvent;)(a0);
  }-*/;
}