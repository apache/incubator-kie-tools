package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.KeyboardEvent;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.EventListener;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.selector.UndefinedExpressionSelectorPopoverView;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.selector.UndefinedExpressionSelectorPopoverView.Presenter;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.selector.UndefinedExpressionSelectorPopoverViewImpl;
import org.kie.workbench.common.dmn.client.editors.types.CanBeClosedByKeyboard;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorTextItemView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.PopoverView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.views.pfly.widgets.JQueryProducer.JQuery;
import org.uberfire.client.views.pfly.widgets.Popover;

public class Type_factory__o_k_w_c_d_c_e_e_t_u_s_UndefinedExpressionSelectorPopoverViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<UndefinedExpressionSelectorPopoverViewImpl> { public interface o_k_w_c_d_c_e_e_t_u_s_UndefinedExpressionSelectorPopoverViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/expressions/types/undefined/selector/UndefinedExpressionSelectorPopoverViewImpl.html") public TextResource getContents(); }
  private class Type_factory__o_k_w_c_d_c_e_e_t_u_s_UndefinedExpressionSelectorPopoverViewImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends UndefinedExpressionSelectorPopoverViewImpl implements Proxy<UndefinedExpressionSelectorPopoverViewImpl> {
    private final ProxyHelper<UndefinedExpressionSelectorPopoverViewImpl> proxyHelper = new ProxyHelperImpl<UndefinedExpressionSelectorPopoverViewImpl>("Type_factory__o_k_w_c_d_c_e_e_t_u_s_UndefinedExpressionSelectorPopoverViewImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final UndefinedExpressionSelectorPopoverViewImpl instance) {

    }

    public UndefinedExpressionSelectorPopoverViewImpl asBeanType() {
      return this;
    }

    public void setInstance(final UndefinedExpressionSelectorPopoverViewImpl instance) {
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

    @Override public void init(Presenter presenter) {
      if (proxyHelper != null) {
        final UndefinedExpressionSelectorPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init(presenter);
      } else {
        super.init(presenter);
      }
    }

    @Override public void setExpressionEditorDefinitions(List definitions) {
      if (proxyHelper != null) {
        final UndefinedExpressionSelectorPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setExpressionEditorDefinitions(definitions);
      } else {
        super.setExpressionEditorDefinitions(definitions);
      }
    }

    @Override public void show(Optional popoverTitle) {
      if (proxyHelper != null) {
        final UndefinedExpressionSelectorPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.show(popoverTitle);
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public void hide() {
      if (proxyHelper != null) {
        final UndefinedExpressionSelectorPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.hide();
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public HTMLElement getElement() {
      if (proxyHelper != null) {
        final UndefinedExpressionSelectorPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final HTMLElement retVal = proxiedInstance.getElement();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final UndefinedExpressionSelectorPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }

    @Override public void setOnClosedByKeyboardCallback(Consumer callback) {
      if (proxyHelper != null) {
        final UndefinedExpressionSelectorPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setOnClosedByKeyboardCallback(callback);
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override protected void onShownFocus() {
      if (proxyHelper != null) {
        final UndefinedExpressionSelectorPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        AbstractPopoverViewImpl_onShownFocus(proxiedInstance);
      } else {
        super.onShownFocus();
      }
    }

    @Override public boolean isVisible() {
      if (proxyHelper != null) {
        final UndefinedExpressionSelectorPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isVisible();
        return retVal;
      } else {
        return super.isVisible();
      }
    }

    @Override protected void setKeyDownListeners() {
      if (proxyHelper != null) {
        final UndefinedExpressionSelectorPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        AbstractPopoverViewImpl_setKeyDownListeners(proxiedInstance);
      } else {
        super.setKeyDownListeners();
      }
    }

    @Override protected void clearKeyDownListeners() {
      if (proxyHelper != null) {
        final UndefinedExpressionSelectorPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        AbstractPopoverViewImpl_clearKeyDownListeners(proxiedInstance);
      } else {
        super.clearKeyDownListeners();
      }
    }

    @Override protected EventListener getKeyDownEventListener() {
      if (proxyHelper != null) {
        final UndefinedExpressionSelectorPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final EventListener retVal = AbstractPopoverViewImpl_getKeyDownEventListener(proxiedInstance);
        return retVal;
      } else {
        return super.getKeyDownEventListener();
      }
    }

    @Override public void keyDownEventListener(Object event) {
      if (proxyHelper != null) {
        final UndefinedExpressionSelectorPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.keyDownEventListener(event);
      } else {
        super.keyDownEventListener(event);
      }
    }

    @Override public boolean isEscapeKeyPressed(KeyboardEvent event) {
      if (proxyHelper != null) {
        final UndefinedExpressionSelectorPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isEscapeKeyPressed(event);
        return retVal;
      } else {
        return super.isEscapeKeyPressed(event);
      }
    }

    @Override public boolean isEnterKeyPressed(KeyboardEvent event) {
      if (proxyHelper != null) {
        final UndefinedExpressionSelectorPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isEnterKeyPressed(event);
        return retVal;
      } else {
        return super.isEnterKeyPressed(event);
      }
    }

    @Override public void onClosedByKeyboard() {
      if (proxyHelper != null) {
        final UndefinedExpressionSelectorPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onClosedByKeyboard();
      } else {
        super.onClosedByKeyboard();
      }
    }

    @Override public Optional getClosedByKeyboardCallback() {
      if (proxyHelper != null) {
        final UndefinedExpressionSelectorPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final Optional retVal = proxiedInstance.getClosedByKeyboardCallback();
        return retVal;
      } else {
        return super.getClosedByKeyboardCallback();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_e_t_u_s_UndefinedExpressionSelectorPopoverViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(UndefinedExpressionSelectorPopoverViewImpl.class, "Type_factory__o_k_w_c_d_c_e_e_t_u_s_UndefinedExpressionSelectorPopoverViewImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { UndefinedExpressionSelectorPopoverViewImpl.class, AbstractPopoverViewImpl.class, Object.class, PopoverView.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, CanBeClosedByKeyboard.class, UndefinedExpressionSelectorPopoverView.class, UberElement.class, HasPresenter.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2018 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"UndefinedExpressionSelectorPopoverViewImpl.\"] #popover-container {\n  display: inline;\n}\n[data-i18n-prefix=\"UndefinedExpressionSelectorPopoverViewImpl.\"] #popover-container .popover {\n  min-width: 200px;\n}\n[data-i18n-prefix=\"UndefinedExpressionSelectorPopoverViewImpl.\"] .dropdown-menu-overrides {\n  display: inline;\n  position: initial;\n  border: 0;\n  box-shadow: 0 0 0 0;\n  margin: 0 0 0 0;\n  padding: 0 0 8px 0;\n  min-width: 100%;\n}\n\n");
  }

  public UndefinedExpressionSelectorPopoverViewImpl createInstance(final ContextManager contextManager) {
    final ManagedInstance<ListSelectorTextItemView> _listSelectorTextItemViews_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ListSelectorTextItemView.class }, new Annotation[] { });
    final Div _popoverContentElement_3 = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final Div _popoverElement_2 = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final JQuery<Popover> _jQueryPopover_4 = (JQuery) contextManager.getInstance("Producer_factory__o_u_c_v_p_w_JQueryProducer_JQuery__quals__j_e_i_Any_j_e_i_Default");
    final UnorderedList _definitionsContainer_0 = (UnorderedList) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_UnorderedList__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final UndefinedExpressionSelectorPopoverViewImpl instance = new UndefinedExpressionSelectorPopoverViewImpl(_definitionsContainer_0, _listSelectorTextItemViews_1, _popoverElement_2, _popoverContentElement_3, _jQueryPopover_4);
    registerDependentScopedReference(instance, _listSelectorTextItemViews_1);
    registerDependentScopedReference(instance, _popoverContentElement_3);
    registerDependentScopedReference(instance, _popoverElement_2);
    registerDependentScopedReference(instance, _jQueryPopover_4);
    registerDependentScopedReference(instance, _definitionsContainer_0);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_e_t_u_s_UndefinedExpressionSelectorPopoverViewImplTemplateResource templateForUndefinedExpressionSelectorPopoverViewImpl = GWT.create(o_k_w_c_d_c_e_e_t_u_s_UndefinedExpressionSelectorPopoverViewImplTemplateResource.class);
    Element parentElementForTemplateOfUndefinedExpressionSelectorPopoverViewImpl = TemplateUtil.getRootTemplateParentElement(templateForUndefinedExpressionSelectorPopoverViewImpl.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/expressions/types/undefined/selector/UndefinedExpressionSelectorPopoverViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/expressions/types/undefined/selector/UndefinedExpressionSelectorPopoverViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfUndefinedExpressionSelectorPopoverViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfUndefinedExpressionSelectorPopoverViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(3);
    dataFieldMetas.put("popover", new DataFieldMeta());
    dataFieldMetas.put("popover-content", new DataFieldMeta());
    dataFieldMetas.put("definitions-container", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.selector.UndefinedExpressionSelectorPopoverViewImpl", "org/kie/workbench/common/dmn/client/editors/expressions/types/undefined/selector/UndefinedExpressionSelectorPopoverViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AbstractPopoverViewImpl_Div_popoverElement(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "popover");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.selector.UndefinedExpressionSelectorPopoverViewImpl", "org/kie/workbench/common/dmn/client/editors/expressions/types/undefined/selector/UndefinedExpressionSelectorPopoverViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AbstractPopoverViewImpl_Div_popoverContentElement(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "popover-content");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.selector.UndefinedExpressionSelectorPopoverViewImpl", "org/kie/workbench/common/dmn/client/editors/expressions/types/undefined/selector/UndefinedExpressionSelectorPopoverViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(UndefinedExpressionSelectorPopoverViewImpl_UnorderedList_definitionsContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "definitions-container");
    templateFieldsMap.put("popover", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AbstractPopoverViewImpl_Div_popoverElement(instance))));
    templateFieldsMap.put("popover-content", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AbstractPopoverViewImpl_Div_popoverContentElement(instance))));
    templateFieldsMap.put("definitions-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(UndefinedExpressionSelectorPopoverViewImpl_UnorderedList_definitionsContainer(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfUndefinedExpressionSelectorPopoverViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((UndefinedExpressionSelectorPopoverViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final UndefinedExpressionSelectorPopoverViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  public Proxy createProxy(final Context context) {
    final Proxy<UndefinedExpressionSelectorPopoverViewImpl> proxyImpl = new Type_factory__o_k_w_c_d_c_e_e_t_u_s_UndefinedExpressionSelectorPopoverViewImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static Div AbstractPopoverViewImpl_Div_popoverElement(AbstractPopoverViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl::popoverElement;
  }-*/;

  native static void AbstractPopoverViewImpl_Div_popoverElement(AbstractPopoverViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl::popoverElement = value;
  }-*/;

  native static Div AbstractPopoverViewImpl_Div_popoverContentElement(AbstractPopoverViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl::popoverContentElement;
  }-*/;

  native static void AbstractPopoverViewImpl_Div_popoverContentElement(AbstractPopoverViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl::popoverContentElement = value;
  }-*/;

  native static UnorderedList UndefinedExpressionSelectorPopoverViewImpl_UnorderedList_definitionsContainer(UndefinedExpressionSelectorPopoverViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.selector.UndefinedExpressionSelectorPopoverViewImpl::definitionsContainer;
  }-*/;

  native static void UndefinedExpressionSelectorPopoverViewImpl_UnorderedList_definitionsContainer(UndefinedExpressionSelectorPopoverViewImpl instance, UnorderedList value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.selector.UndefinedExpressionSelectorPopoverViewImpl::definitionsContainer = value;
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

  public native static void AbstractPopoverViewImpl_onShownFocus(AbstractPopoverViewImpl instance) /*-{
    instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl::onShownFocus()();
  }-*/;
}