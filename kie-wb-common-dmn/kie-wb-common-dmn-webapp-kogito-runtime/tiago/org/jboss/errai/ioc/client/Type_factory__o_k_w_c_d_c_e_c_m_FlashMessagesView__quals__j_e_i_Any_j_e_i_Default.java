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
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessages.View;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_c_m_FlashMessagesView__quals__j_e_i_Any_j_e_i_Default extends Factory<FlashMessagesView> { public interface o_k_w_c_d_c_e_c_m_FlashMessagesViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/common/messages/FlashMessagesView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_c_m_FlashMessagesView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FlashMessagesView.class, "Type_factory__o_k_w_c_d_c_e_c_m_FlashMessagesView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FlashMessagesView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2018 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *       http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\ndiv.tab-content div.kie-dmn-page .kie-flash-message-error-element {\n  border: 1px solid #c00;\n  outline-color: #c00;\n}\ndiv.tab-content div.kie-dmn-page .kie-flash-message-warning-element {\n  border: 1px solid #ec7a08;\n  outline-color: #ec7a08;\n}\ndiv.tab-content div.kie-dmn-page .alert {\n  max-height: 0;\n  opacity: 0;\n  overflow: hidden;\n  transition: all 0.15s linear;\n  padding: 0 0 0 47px;\n  margin: 5px;\n}\ndiv.tab-content div.kie-dmn-page .alert.opened.alert-warning {\n  padding-right: 120px;\n}\ndiv.tab-content div.kie-dmn-page .action-buttons {\n  position: absolute;\n  right: 10px;\n  top: 10px;\n}\ndiv.tab-content div.kie-dmn-page .action-buttons .btn {\n  width: 50px;\n  margin: 0 1px;\n}\ndiv.tab-content div.kie-dmn-page .action-buttons .fa {\n  background: transparent;\n  border: none;\n}\ndiv.tab-content div.kie-dmn-page .alert.opened {\n  opacity: 1;\n  max-height: 100px;\n  padding: 11px 14px 10px 47px;\n  margin-bottom: 20px;\n}\n\n");
  }

  public FlashMessagesView createInstance(final ContextManager contextManager) {
    final HTMLElement _regularSuccessMessage_11 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
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
    final HTMLButtonElement _cancelWarningButton_7 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLButtonElement _closeSuccessButton_8 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLElement _regularWarningMessage_5 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
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
    final HTMLDivElement _warningContainer_1 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLElement _strongErrorMessage_2 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
        public Class annotationType() {
          return Named.class;
        }
        public String toString() {
          return "@javax.inject.Named(value=strong)";
        }
        public String value() {
          return "strong";
        }
    } });
    final HTMLElement _strongWarningMessage_4 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
        public Class annotationType() {
          return Named.class;
        }
        public String toString() {
          return "@javax.inject.Named(value=strong)";
        }
        public String value() {
          return "strong";
        }
    } });
    final HTMLDivElement _errorContainer_0 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLElement _regularErrorMessage_3 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
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
    final HTMLDivElement _successContainer_9 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLElement _strongSuccessMessage_10 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
        public Class annotationType() {
          return Named.class;
        }
        public String toString() {
          return "@javax.inject.Named(value=strong)";
        }
        public String value() {
          return "strong";
        }
    } });
    final HTMLButtonElement _okWarningButton_6 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final FlashMessagesView instance = new FlashMessagesView(_errorContainer_0, _warningContainer_1, _strongErrorMessage_2, _regularErrorMessage_3, _strongWarningMessage_4, _regularWarningMessage_5, _okWarningButton_6, _cancelWarningButton_7, _closeSuccessButton_8, _successContainer_9, _strongSuccessMessage_10, _regularSuccessMessage_11);
    registerDependentScopedReference(instance, _regularSuccessMessage_11);
    registerDependentScopedReference(instance, _cancelWarningButton_7);
    registerDependentScopedReference(instance, _closeSuccessButton_8);
    registerDependentScopedReference(instance, _regularWarningMessage_5);
    registerDependentScopedReference(instance, _warningContainer_1);
    registerDependentScopedReference(instance, _strongErrorMessage_2);
    registerDependentScopedReference(instance, _strongWarningMessage_4);
    registerDependentScopedReference(instance, _errorContainer_0);
    registerDependentScopedReference(instance, _regularErrorMessage_3);
    registerDependentScopedReference(instance, _successContainer_9);
    registerDependentScopedReference(instance, _strongSuccessMessage_10);
    registerDependentScopedReference(instance, _okWarningButton_6);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_c_m_FlashMessagesViewTemplateResource templateForFlashMessagesView = GWT.create(o_k_w_c_d_c_e_c_m_FlashMessagesViewTemplateResource.class);
    Element parentElementForTemplateOfFlashMessagesView = TemplateUtil.getRootTemplateParentElement(templateForFlashMessagesView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/common/messages/FlashMessagesView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/common/messages/FlashMessagesView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfFlashMessagesView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfFlashMessagesView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(12);
    dataFieldMetas.put("error-container", new DataFieldMeta());
    dataFieldMetas.put("warning-container", new DataFieldMeta());
    dataFieldMetas.put("success-container", new DataFieldMeta());
    dataFieldMetas.put("strong-error-message", new DataFieldMeta());
    dataFieldMetas.put("regular-error-message", new DataFieldMeta());
    dataFieldMetas.put("strong-warning-message", new DataFieldMeta());
    dataFieldMetas.put("regular-warning-message", new DataFieldMeta());
    dataFieldMetas.put("strong-success-message", new DataFieldMeta());
    dataFieldMetas.put("regular-success-message", new DataFieldMeta());
    dataFieldMetas.put("ok-warning-button", new DataFieldMeta());
    dataFieldMetas.put("cancel-warning-button", new DataFieldMeta());
    dataFieldMetas.put("close-success-message-button", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView", "org/kie/workbench/common/dmn/client/editors/common/messages/FlashMessagesView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(FlashMessagesView_HTMLDivElement_errorContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "error-container");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView", "org/kie/workbench/common/dmn/client/editors/common/messages/FlashMessagesView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(FlashMessagesView_HTMLDivElement_warningContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "warning-container");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView", "org/kie/workbench/common/dmn/client/editors/common/messages/FlashMessagesView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(FlashMessagesView_HTMLDivElement_successContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "success-container");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView", "org/kie/workbench/common/dmn/client/editors/common/messages/FlashMessagesView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(FlashMessagesView_HTMLElement_strongErrorMessage(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "strong-error-message");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView", "org/kie/workbench/common/dmn/client/editors/common/messages/FlashMessagesView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(FlashMessagesView_HTMLElement_regularErrorMessage(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "regular-error-message");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView", "org/kie/workbench/common/dmn/client/editors/common/messages/FlashMessagesView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(FlashMessagesView_HTMLElement_strongWarningMessage(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "strong-warning-message");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView", "org/kie/workbench/common/dmn/client/editors/common/messages/FlashMessagesView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(FlashMessagesView_HTMLElement_regularWarningMessage(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "regular-warning-message");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView", "org/kie/workbench/common/dmn/client/editors/common/messages/FlashMessagesView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(FlashMessagesView_HTMLElement_strongSuccessMessage(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "strong-success-message");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView", "org/kie/workbench/common/dmn/client/editors/common/messages/FlashMessagesView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(FlashMessagesView_HTMLElement_regularSuccessMessage(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "regular-success-message");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView", "org/kie/workbench/common/dmn/client/editors/common/messages/FlashMessagesView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(FlashMessagesView_HTMLButtonElement_okWarningButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "ok-warning-button");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView", "org/kie/workbench/common/dmn/client/editors/common/messages/FlashMessagesView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(FlashMessagesView_HTMLButtonElement_cancelWarningButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "cancel-warning-button");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView", "org/kie/workbench/common/dmn/client/editors/common/messages/FlashMessagesView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(FlashMessagesView_HTMLButtonElement_closeSuccessButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "close-success-message-button");
    templateFieldsMap.put("error-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(FlashMessagesView_HTMLDivElement_errorContainer(instance))));
    templateFieldsMap.put("warning-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(FlashMessagesView_HTMLDivElement_warningContainer(instance))));
    templateFieldsMap.put("success-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(FlashMessagesView_HTMLDivElement_successContainer(instance))));
    templateFieldsMap.put("strong-error-message", ElementWrapperWidget.getWidget(TemplateUtil.asElement(FlashMessagesView_HTMLElement_strongErrorMessage(instance))));
    templateFieldsMap.put("regular-error-message", ElementWrapperWidget.getWidget(TemplateUtil.asElement(FlashMessagesView_HTMLElement_regularErrorMessage(instance))));
    templateFieldsMap.put("strong-warning-message", ElementWrapperWidget.getWidget(TemplateUtil.asElement(FlashMessagesView_HTMLElement_strongWarningMessage(instance))));
    templateFieldsMap.put("regular-warning-message", ElementWrapperWidget.getWidget(TemplateUtil.asElement(FlashMessagesView_HTMLElement_regularWarningMessage(instance))));
    templateFieldsMap.put("strong-success-message", ElementWrapperWidget.getWidget(TemplateUtil.asElement(FlashMessagesView_HTMLElement_strongSuccessMessage(instance))));
    templateFieldsMap.put("regular-success-message", ElementWrapperWidget.getWidget(TemplateUtil.asElement(FlashMessagesView_HTMLElement_regularSuccessMessage(instance))));
    templateFieldsMap.put("ok-warning-button", ElementWrapperWidget.getWidget(TemplateUtil.asElement(FlashMessagesView_HTMLButtonElement_okWarningButton(instance))));
    templateFieldsMap.put("cancel-warning-button", ElementWrapperWidget.getWidget(TemplateUtil.asElement(FlashMessagesView_HTMLButtonElement_cancelWarningButton(instance))));
    templateFieldsMap.put("close-success-message-button", ElementWrapperWidget.getWidget(TemplateUtil.asElement(FlashMessagesView_HTMLButtonElement_closeSuccessButton(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfFlashMessagesView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("ok-warning-button"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onOkWarningButtonClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("cancel-warning-button"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onCancelWarningButtonClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("close-success-message-button"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onCloseSuccessButtonClick(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((FlashMessagesView) instance, contextManager);
  }

  public void destroyInstanceHelper(final FlashMessagesView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLButtonElement FlashMessagesView_HTMLButtonElement_closeSuccessButton(FlashMessagesView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView::closeSuccessButton;
  }-*/;

  native static void FlashMessagesView_HTMLButtonElement_closeSuccessButton(FlashMessagesView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView::closeSuccessButton = value;
  }-*/;

  native static HTMLDivElement FlashMessagesView_HTMLDivElement_warningContainer(FlashMessagesView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView::warningContainer;
  }-*/;

  native static void FlashMessagesView_HTMLDivElement_warningContainer(FlashMessagesView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView::warningContainer = value;
  }-*/;

  native static HTMLElement FlashMessagesView_HTMLElement_regularErrorMessage(FlashMessagesView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView::regularErrorMessage;
  }-*/;

  native static void FlashMessagesView_HTMLElement_regularErrorMessage(FlashMessagesView instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView::regularErrorMessage = value;
  }-*/;

  native static HTMLElement FlashMessagesView_HTMLElement_strongErrorMessage(FlashMessagesView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView::strongErrorMessage;
  }-*/;

  native static void FlashMessagesView_HTMLElement_strongErrorMessage(FlashMessagesView instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView::strongErrorMessage = value;
  }-*/;

  native static HTMLElement FlashMessagesView_HTMLElement_regularSuccessMessage(FlashMessagesView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView::regularSuccessMessage;
  }-*/;

  native static void FlashMessagesView_HTMLElement_regularSuccessMessage(FlashMessagesView instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView::regularSuccessMessage = value;
  }-*/;

  native static HTMLElement FlashMessagesView_HTMLElement_regularWarningMessage(FlashMessagesView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView::regularWarningMessage;
  }-*/;

  native static void FlashMessagesView_HTMLElement_regularWarningMessage(FlashMessagesView instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView::regularWarningMessage = value;
  }-*/;

  native static HTMLButtonElement FlashMessagesView_HTMLButtonElement_okWarningButton(FlashMessagesView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView::okWarningButton;
  }-*/;

  native static void FlashMessagesView_HTMLButtonElement_okWarningButton(FlashMessagesView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView::okWarningButton = value;
  }-*/;

  native static HTMLDivElement FlashMessagesView_HTMLDivElement_successContainer(FlashMessagesView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView::successContainer;
  }-*/;

  native static void FlashMessagesView_HTMLDivElement_successContainer(FlashMessagesView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView::successContainer = value;
  }-*/;

  native static HTMLElement FlashMessagesView_HTMLElement_strongSuccessMessage(FlashMessagesView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView::strongSuccessMessage;
  }-*/;

  native static void FlashMessagesView_HTMLElement_strongSuccessMessage(FlashMessagesView instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView::strongSuccessMessage = value;
  }-*/;

  native static HTMLDivElement FlashMessagesView_HTMLDivElement_errorContainer(FlashMessagesView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView::errorContainer;
  }-*/;

  native static void FlashMessagesView_HTMLDivElement_errorContainer(FlashMessagesView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView::errorContainer = value;
  }-*/;

  native static HTMLElement FlashMessagesView_HTMLElement_strongWarningMessage(FlashMessagesView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView::strongWarningMessage;
  }-*/;

  native static void FlashMessagesView_HTMLElement_strongWarningMessage(FlashMessagesView instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView::strongWarningMessage = value;
  }-*/;

  native static HTMLButtonElement FlashMessagesView_HTMLButtonElement_cancelWarningButton(FlashMessagesView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView::cancelWarningButton;
  }-*/;

  native static void FlashMessagesView_HTMLButtonElement_cancelWarningButton(FlashMessagesView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView::cancelWarningButton = value;
  }-*/;
}