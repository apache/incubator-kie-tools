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
import elemental2.dom.HTMLInputElement;
import elemental2.dom.KeyboardEvent;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.EventListener;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverView;
import org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverView.Presenter;
import org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverViewImpl;
import org.kie.workbench.common.dmn.client.editors.types.CanBeClosedByKeyboard;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.PopoverView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.views.pfly.widgets.JQueryProducer.JQuery;
import org.uberfire.client.views.pfly.widgets.Popover;

public class Type_factory__o_k_w_c_d_c_e_d_l_NameAndUrlPopoverViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<NameAndUrlPopoverViewImpl> { public interface o_k_w_c_d_c_e_d_l_NameAndUrlPopoverViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/documentation/links/NameAndUrlPopoverViewImpl.html") public TextResource getContents(); }
  private class Type_factory__o_k_w_c_d_c_e_d_l_NameAndUrlPopoverViewImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends NameAndUrlPopoverViewImpl implements Proxy<NameAndUrlPopoverViewImpl> {
    private final ProxyHelper<NameAndUrlPopoverViewImpl> proxyHelper = new ProxyHelperImpl<NameAndUrlPopoverViewImpl>("Type_factory__o_k_w_c_d_c_e_d_l_NameAndUrlPopoverViewImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final NameAndUrlPopoverViewImpl instance) {

    }

    public NameAndUrlPopoverViewImpl asBeanType() {
      return this;
    }

    public void setInstance(final NameAndUrlPopoverViewImpl instance) {
      proxyHelper.setInstance(instance);
    }

    public void clearInstance() {
      proxyHelper.clearInstance();
    }

    public void setProxyContext(final Context context) {
      proxyHelper.setProxyContext(context);
    }

    public Context getProxyContext() {
      return proxyHelper.getProxyContext();
    }

    public Object unwrap() {
      return proxyHelper.getInstance(this);
    }

    public boolean equals(Object obj) {
      obj = Factory.maybeUnwrapProxy(obj);
      return proxyHelper.getInstance(this).equals(obj);
    }

    @Override public void init() {
      if (proxyHelper != null) {
        final NameAndUrlPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public void init(Presenter presenter) {
      if (proxyHelper != null) {
        final NameAndUrlPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init(presenter);
      } else {
        super.init(presenter);
      }
    }

    @Override public void onClickOkButton(ClickEvent clickEvent) {
      if (proxyHelper != null) {
        final NameAndUrlPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onClickOkButton(clickEvent);
      } else {
        super.onClickOkButton(clickEvent);
      }
    }

    @Override public void onClickCancelButton(ClickEvent clickEvent) {
      if (proxyHelper != null) {
        final NameAndUrlPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onClickCancelButton(clickEvent);
      } else {
        super.onClickCancelButton(clickEvent);
      }
    }

    @Override protected void onShownFocus() {
      if (proxyHelper != null) {
        final NameAndUrlPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        NameAndUrlPopoverViewImpl_onShownFocus(proxiedInstance);
      } else {
        super.onShownFocus();
      }
    }

    @Override public Consumer getOnExternalLinkCreated() {
      if (proxyHelper != null) {
        final NameAndUrlPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final Consumer retVal = proxiedInstance.getOnExternalLinkCreated();
        return retVal;
      } else {
        return super.getOnExternalLinkCreated();
      }
    }

    @Override public void setOnExternalLinkCreated(Consumer onExternalLinkCreated) {
      if (proxyHelper != null) {
        final NameAndUrlPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setOnExternalLinkCreated(onExternalLinkCreated);
      } else {
        super.setOnExternalLinkCreated(onExternalLinkCreated);
      }
    }

    @Override public void show(Optional popoverTitle) {
      if (proxyHelper != null) {
        final NameAndUrlPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.show(popoverTitle);
      } else {
        super.show(popoverTitle);
      }
    }

    @Override public void hide() {
      if (proxyHelper != null) {
        final NameAndUrlPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.hide();
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public HTMLElement getElement() {
      if (proxyHelper != null) {
        final NameAndUrlPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final HTMLElement retVal = proxiedInstance.getElement();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final NameAndUrlPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }

    @Override public void setOnClosedByKeyboardCallback(Consumer callback) {
      if (proxyHelper != null) {
        final NameAndUrlPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setOnClosedByKeyboardCallback(callback);
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public boolean isVisible() {
      if (proxyHelper != null) {
        final NameAndUrlPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isVisible();
        return retVal;
      } else {
        return super.isVisible();
      }
    }

    @Override protected void setKeyDownListeners() {
      if (proxyHelper != null) {
        final NameAndUrlPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        AbstractPopoverViewImpl_setKeyDownListeners(proxiedInstance);
      } else {
        super.setKeyDownListeners();
      }
    }

    @Override protected void clearKeyDownListeners() {
      if (proxyHelper != null) {
        final NameAndUrlPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        AbstractPopoverViewImpl_clearKeyDownListeners(proxiedInstance);
      } else {
        super.clearKeyDownListeners();
      }
    }

    @Override protected EventListener getKeyDownEventListener() {
      if (proxyHelper != null) {
        final NameAndUrlPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final EventListener retVal = AbstractPopoverViewImpl_getKeyDownEventListener(proxiedInstance);
        return retVal;
      } else {
        return super.getKeyDownEventListener();
      }
    }

    @Override public void keyDownEventListener(Object event) {
      if (proxyHelper != null) {
        final NameAndUrlPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.keyDownEventListener(event);
      } else {
        super.keyDownEventListener(event);
      }
    }

    @Override public boolean isEscapeKeyPressed(KeyboardEvent event) {
      if (proxyHelper != null) {
        final NameAndUrlPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isEscapeKeyPressed(event);
        return retVal;
      } else {
        return super.isEscapeKeyPressed(event);
      }
    }

    @Override public boolean isEnterKeyPressed(KeyboardEvent event) {
      if (proxyHelper != null) {
        final NameAndUrlPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isEnterKeyPressed(event);
        return retVal;
      } else {
        return super.isEnterKeyPressed(event);
      }
    }

    @Override public void onClosedByKeyboard() {
      if (proxyHelper != null) {
        final NameAndUrlPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onClosedByKeyboard();
      } else {
        super.onClosedByKeyboard();
      }
    }

    @Override public Optional getClosedByKeyboardCallback() {
      if (proxyHelper != null) {
        final NameAndUrlPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final Optional retVal = proxiedInstance.getClosedByKeyboardCallback();
        return retVal;
      } else {
        return super.getClosedByKeyboardCallback();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_d_l_NameAndUrlPopoverViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(NameAndUrlPopoverViewImpl.class, "Type_factory__o_k_w_c_d_c_e_d_l_NameAndUrlPopoverViewImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { NameAndUrlPopoverViewImpl.class, AbstractPopoverViewImpl.class, Object.class, PopoverView.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, CanBeClosedByKeyboard.class, NameAndUrlPopoverView.class, UberElement.class, HasPresenter.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"NameAndUrlPopoverViewImpl.\"] #popover-container {\n  display: inline;\n}\n[data-i18n-prefix=\"NameAndUrlPopoverViewImpl.\"] #popover-container .popover {\n  min-width: 450px;\n}\n[data-i18n-prefix=\"NameAndUrlPopoverViewImpl.\"] .kie-dmn-name-and-url-type-container #kieName {\n  width: 100%;\n}\n[data-i18n-prefix=\"NameAndUrlPopoverViewImpl.\"] .kie-dmn-name-and-url-type-container #kieDataType {\n  width: 100%;\n}\n[data-i18n-prefix=\"NameAndUrlPopoverViewImpl.\"] .attachment-tip {\n  opacity: .75;\n  font-size: .85em;\n  font-style: italic;\n}\n[data-i18n-prefix=\"NameAndUrlPopoverViewImpl.\"] .buttons-container {\n  margin-top: 20px;\n  margin-bottom: 15px;\n  float: right;\n}\n\n");
  }

  public NameAndUrlPopoverViewImpl createInstance(final ContextManager contextManager) {
    final Div _popoverElement_0 = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLButtonElement _okButton_5 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final JQuery<Popover> _jQueryPopover_2 = (JQuery) contextManager.getInstance("Producer_factory__o_u_c_v_p_w_JQueryProducer_JQuery__quals__j_e_i_Any_j_e_i_Default");
    final HTMLInputElement _attachmentNameInput_7 = (HTMLInputElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLInputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final Div _popoverContentElement_1 = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final elemental2.dom.HTMLElement _attachmentName_9 = (elemental2.dom.HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
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
    final elemental2.dom.HTMLElement _attachmentTip_10 = (elemental2.dom.HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
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
    final HTMLInputElement _urlInput_6 = (HTMLInputElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLInputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLButtonElement _cancelButton_4 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final elemental2.dom.HTMLElement _urlLabel_8 = (elemental2.dom.HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
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
    final TranslationService _translationService_3 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final NameAndUrlPopoverViewImpl instance = new NameAndUrlPopoverViewImpl(_popoverElement_0, _popoverContentElement_1, _jQueryPopover_2, _translationService_3, _cancelButton_4, _okButton_5, _urlInput_6, _attachmentNameInput_7, _urlLabel_8, _attachmentName_9, _attachmentTip_10);
    registerDependentScopedReference(instance, _popoverElement_0);
    registerDependentScopedReference(instance, _okButton_5);
    registerDependentScopedReference(instance, _jQueryPopover_2);
    registerDependentScopedReference(instance, _attachmentNameInput_7);
    registerDependentScopedReference(instance, _popoverContentElement_1);
    registerDependentScopedReference(instance, _attachmentName_9);
    registerDependentScopedReference(instance, _attachmentTip_10);
    registerDependentScopedReference(instance, _urlInput_6);
    registerDependentScopedReference(instance, _cancelButton_4);
    registerDependentScopedReference(instance, _urlLabel_8);
    registerDependentScopedReference(instance, _translationService_3);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_d_l_NameAndUrlPopoverViewImplTemplateResource templateForNameAndUrlPopoverViewImpl = GWT.create(o_k_w_c_d_c_e_d_l_NameAndUrlPopoverViewImplTemplateResource.class);
    Element parentElementForTemplateOfNameAndUrlPopoverViewImpl = TemplateUtil.getRootTemplateParentElement(templateForNameAndUrlPopoverViewImpl.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/documentation/links/NameAndUrlPopoverViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/documentation/links/NameAndUrlPopoverViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfNameAndUrlPopoverViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfNameAndUrlPopoverViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(9);
    dataFieldMetas.put("popover", new DataFieldMeta());
    dataFieldMetas.put("popover-content", new DataFieldMeta());
    dataFieldMetas.put("urlLabel", new DataFieldMeta());
    dataFieldMetas.put("nameLabel", new DataFieldMeta());
    dataFieldMetas.put("attachmentTip", new DataFieldMeta());
    dataFieldMetas.put("cancelButton", new DataFieldMeta());
    dataFieldMetas.put("okButton", new DataFieldMeta());
    dataFieldMetas.put("urlInput", new DataFieldMeta());
    dataFieldMetas.put("attachmentNameInput", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverViewImpl", "org/kie/workbench/common/dmn/client/editors/documentation/links/NameAndUrlPopoverViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AbstractPopoverViewImpl_Div_popoverElement(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "popover");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverViewImpl", "org/kie/workbench/common/dmn/client/editors/documentation/links/NameAndUrlPopoverViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AbstractPopoverViewImpl_Div_popoverContentElement(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "popover-content");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverViewImpl", "org/kie/workbench/common/dmn/client/editors/documentation/links/NameAndUrlPopoverViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(NameAndUrlPopoverViewImpl_HTMLElement_urlLabel(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "urlLabel");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverViewImpl", "org/kie/workbench/common/dmn/client/editors/documentation/links/NameAndUrlPopoverViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(NameAndUrlPopoverViewImpl_HTMLElement_attachmentName(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "nameLabel");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverViewImpl", "org/kie/workbench/common/dmn/client/editors/documentation/links/NameAndUrlPopoverViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(NameAndUrlPopoverViewImpl_HTMLElement_attachmentTip(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "attachmentTip");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverViewImpl", "org/kie/workbench/common/dmn/client/editors/documentation/links/NameAndUrlPopoverViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(NameAndUrlPopoverViewImpl_HTMLButtonElement_cancelButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "cancelButton");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverViewImpl", "org/kie/workbench/common/dmn/client/editors/documentation/links/NameAndUrlPopoverViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(NameAndUrlPopoverViewImpl_HTMLButtonElement_okButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "okButton");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverViewImpl", "org/kie/workbench/common/dmn/client/editors/documentation/links/NameAndUrlPopoverViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(NameAndUrlPopoverViewImpl_HTMLInputElement_urlInput(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "urlInput");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverViewImpl", "org/kie/workbench/common/dmn/client/editors/documentation/links/NameAndUrlPopoverViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(NameAndUrlPopoverViewImpl_HTMLInputElement_attachmentNameInput(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "attachmentNameInput");
    templateFieldsMap.put("popover", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AbstractPopoverViewImpl_Div_popoverElement(instance))));
    templateFieldsMap.put("popover-content", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AbstractPopoverViewImpl_Div_popoverContentElement(instance))));
    templateFieldsMap.put("urlLabel", ElementWrapperWidget.getWidget(TemplateUtil.asElement(NameAndUrlPopoverViewImpl_HTMLElement_urlLabel(instance))));
    templateFieldsMap.put("nameLabel", ElementWrapperWidget.getWidget(TemplateUtil.asElement(NameAndUrlPopoverViewImpl_HTMLElement_attachmentName(instance))));
    templateFieldsMap.put("attachmentTip", ElementWrapperWidget.getWidget(TemplateUtil.asElement(NameAndUrlPopoverViewImpl_HTMLElement_attachmentTip(instance))));
    templateFieldsMap.put("cancelButton", ElementWrapperWidget.getWidget(TemplateUtil.asElement(NameAndUrlPopoverViewImpl_HTMLButtonElement_cancelButton(instance))));
    templateFieldsMap.put("okButton", ElementWrapperWidget.getWidget(TemplateUtil.asElement(NameAndUrlPopoverViewImpl_HTMLButtonElement_okButton(instance))));
    templateFieldsMap.put("urlInput", ElementWrapperWidget.getWidget(TemplateUtil.asElement(NameAndUrlPopoverViewImpl_HTMLInputElement_urlInput(instance))));
    templateFieldsMap.put("attachmentNameInput", ElementWrapperWidget.getWidget(TemplateUtil.asElement(NameAndUrlPopoverViewImpl_HTMLInputElement_attachmentNameInput(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfNameAndUrlPopoverViewImpl), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("cancelButton"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onClickCancelButton(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("okButton"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onClickOkButton(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((NameAndUrlPopoverViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final NameAndUrlPopoverViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  public void invokePostConstructs(final NameAndUrlPopoverViewImpl instance) {
    instance.init();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<NameAndUrlPopoverViewImpl> proxyImpl = new Type_factory__o_k_w_c_d_c_e_d_l_NameAndUrlPopoverViewImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static elemental2.dom.HTMLElement NameAndUrlPopoverViewImpl_HTMLElement_urlLabel(NameAndUrlPopoverViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverViewImpl::urlLabel;
  }-*/;

  native static void NameAndUrlPopoverViewImpl_HTMLElement_urlLabel(NameAndUrlPopoverViewImpl instance, elemental2.dom.HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverViewImpl::urlLabel = value;
  }-*/;

  native static HTMLInputElement NameAndUrlPopoverViewImpl_HTMLInputElement_attachmentNameInput(NameAndUrlPopoverViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverViewImpl::attachmentNameInput;
  }-*/;

  native static void NameAndUrlPopoverViewImpl_HTMLInputElement_attachmentNameInput(NameAndUrlPopoverViewImpl instance, HTMLInputElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverViewImpl::attachmentNameInput = value;
  }-*/;

  native static Div AbstractPopoverViewImpl_Div_popoverElement(AbstractPopoverViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl::popoverElement;
  }-*/;

  native static void AbstractPopoverViewImpl_Div_popoverElement(AbstractPopoverViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl::popoverElement = value;
  }-*/;

  native static elemental2.dom.HTMLElement NameAndUrlPopoverViewImpl_HTMLElement_attachmentTip(NameAndUrlPopoverViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverViewImpl::attachmentTip;
  }-*/;

  native static void NameAndUrlPopoverViewImpl_HTMLElement_attachmentTip(NameAndUrlPopoverViewImpl instance, elemental2.dom.HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverViewImpl::attachmentTip = value;
  }-*/;

  native static HTMLButtonElement NameAndUrlPopoverViewImpl_HTMLButtonElement_okButton(NameAndUrlPopoverViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverViewImpl::okButton;
  }-*/;

  native static void NameAndUrlPopoverViewImpl_HTMLButtonElement_okButton(NameAndUrlPopoverViewImpl instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverViewImpl::okButton = value;
  }-*/;

  native static HTMLInputElement NameAndUrlPopoverViewImpl_HTMLInputElement_urlInput(NameAndUrlPopoverViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverViewImpl::urlInput;
  }-*/;

  native static void NameAndUrlPopoverViewImpl_HTMLInputElement_urlInput(NameAndUrlPopoverViewImpl instance, HTMLInputElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverViewImpl::urlInput = value;
  }-*/;

  native static Div AbstractPopoverViewImpl_Div_popoverContentElement(AbstractPopoverViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl::popoverContentElement;
  }-*/;

  native static void AbstractPopoverViewImpl_Div_popoverContentElement(AbstractPopoverViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl::popoverContentElement = value;
  }-*/;

  native static elemental2.dom.HTMLElement NameAndUrlPopoverViewImpl_HTMLElement_attachmentName(NameAndUrlPopoverViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverViewImpl::attachmentName;
  }-*/;

  native static void NameAndUrlPopoverViewImpl_HTMLElement_attachmentName(NameAndUrlPopoverViewImpl instance, elemental2.dom.HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverViewImpl::attachmentName = value;
  }-*/;

  native static HTMLButtonElement NameAndUrlPopoverViewImpl_HTMLButtonElement_cancelButton(NameAndUrlPopoverViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverViewImpl::cancelButton;
  }-*/;

  native static void NameAndUrlPopoverViewImpl_HTMLButtonElement_cancelButton(NameAndUrlPopoverViewImpl instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverViewImpl::cancelButton = value;
  }-*/;

  public native static void NameAndUrlPopoverViewImpl_onShownFocus(NameAndUrlPopoverViewImpl instance) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverViewImpl::onShownFocus()();
  }-*/;

  public native static void AbstractPopoverViewImpl_setKeyDownListeners(AbstractPopoverViewImpl instance) /*-{
    instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl::setKeyDownListeners()();
  }-*/;

  public native static void AbstractPopoverViewImpl_clearKeyDownListeners(AbstractPopoverViewImpl instance) /*-{
    instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl::clearKeyDownListeners()();
  }-*/;

  public native static EventListener AbstractPopoverViewImpl_getKeyDownEventListener(AbstractPopoverViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl::getKeyDownEventListener()();
  }-*/;
}