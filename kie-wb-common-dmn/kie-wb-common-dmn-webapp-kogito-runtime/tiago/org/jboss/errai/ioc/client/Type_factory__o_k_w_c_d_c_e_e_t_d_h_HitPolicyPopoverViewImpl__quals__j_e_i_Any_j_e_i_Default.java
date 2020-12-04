package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.KeyboardEvent;
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
import org.jboss.errai.common.client.dom.Span;
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
import org.kie.workbench.common.dmn.api.definition.model.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.model.HitPolicy;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.BuiltinAggregatorUtils;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyPopoverView;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyPopoverView.Presenter;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyPopoverViewImpl;
import org.kie.workbench.common.dmn.client.editors.types.CanBeClosedByKeyboard;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.PopoverView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.views.pfly.widgets.JQueryProducer.JQuery;
import org.uberfire.client.views.pfly.widgets.Popover;
import org.uberfire.client.views.pfly.widgets.Select;

public class Type_factory__o_k_w_c_d_c_e_e_t_d_h_HitPolicyPopoverViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<HitPolicyPopoverViewImpl> { public interface o_k_w_c_d_c_e_e_t_d_h_HitPolicyPopoverViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/expressions/types/dtable/hitpolicy/HitPolicyPopoverViewImpl.html") public TextResource getContents(); }
  private class Type_factory__o_k_w_c_d_c_e_e_t_d_h_HitPolicyPopoverViewImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends HitPolicyPopoverViewImpl implements Proxy<HitPolicyPopoverViewImpl> {
    private final ProxyHelper<HitPolicyPopoverViewImpl> proxyHelper = new ProxyHelperImpl<HitPolicyPopoverViewImpl>("Type_factory__o_k_w_c_d_c_e_e_t_d_h_HitPolicyPopoverViewImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final HitPolicyPopoverViewImpl instance) {

    }

    public HitPolicyPopoverViewImpl asBeanType() {
      return this;
    }

    public void setInstance(final HitPolicyPopoverViewImpl instance) {
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
        final HitPolicyPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init(presenter);
      } else {
        super.init(presenter);
      }
    }

    @Override public void initHitPolicies(List hitPolicies) {
      if (proxyHelper != null) {
        final HitPolicyPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.initHitPolicies(hitPolicies);
      } else {
        super.initHitPolicies(hitPolicies);
      }
    }

    @Override public void initBuiltinAggregators(List aggregators) {
      if (proxyHelper != null) {
        final HitPolicyPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.initBuiltinAggregators(aggregators);
      } else {
        super.initBuiltinAggregators(aggregators);
      }
    }

    @Override public void initSelectedHitPolicy(HitPolicy hitPolicy) {
      if (proxyHelper != null) {
        final HitPolicyPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.initSelectedHitPolicy(hitPolicy);
      } else {
        super.initSelectedHitPolicy(hitPolicy);
      }
    }

    @Override public void initSelectedBuiltinAggregator(BuiltinAggregator aggregator) {
      if (proxyHelper != null) {
        final HitPolicyPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.initSelectedBuiltinAggregator(aggregator);
      } else {
        super.initSelectedBuiltinAggregator(aggregator);
      }
    }

    @Override public void enableHitPolicies(boolean enabled) {
      if (proxyHelper != null) {
        final HitPolicyPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.enableHitPolicies(enabled);
      } else {
        super.enableHitPolicies(enabled);
      }
    }

    @Override public void enableBuiltinAggregators(boolean enabled) {
      if (proxyHelper != null) {
        final HitPolicyPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.enableBuiltinAggregators(enabled);
      } else {
        super.enableBuiltinAggregators(enabled);
      }
    }

    @Override public void show(Optional popoverTitle) {
      if (proxyHelper != null) {
        final HitPolicyPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.show(popoverTitle);
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public void hide() {
      if (proxyHelper != null) {
        final HitPolicyPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.hide();
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public HTMLElement getElement() {
      if (proxyHelper != null) {
        final HitPolicyPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final HTMLElement retVal = proxiedInstance.getElement();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final HitPolicyPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }

    @Override public void setOnClosedByKeyboardCallback(Consumer callback) {
      if (proxyHelper != null) {
        final HitPolicyPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setOnClosedByKeyboardCallback(callback);
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override protected void onShownFocus() {
      if (proxyHelper != null) {
        final HitPolicyPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        AbstractPopoverViewImpl_onShownFocus(proxiedInstance);
      } else {
        super.onShownFocus();
      }
    }

    @Override public boolean isVisible() {
      if (proxyHelper != null) {
        final HitPolicyPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isVisible();
        return retVal;
      } else {
        return super.isVisible();
      }
    }

    @Override protected void setKeyDownListeners() {
      if (proxyHelper != null) {
        final HitPolicyPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        AbstractPopoverViewImpl_setKeyDownListeners(proxiedInstance);
      } else {
        super.setKeyDownListeners();
      }
    }

    @Override protected void clearKeyDownListeners() {
      if (proxyHelper != null) {
        final HitPolicyPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        AbstractPopoverViewImpl_clearKeyDownListeners(proxiedInstance);
      } else {
        super.clearKeyDownListeners();
      }
    }

    @Override protected EventListener getKeyDownEventListener() {
      if (proxyHelper != null) {
        final HitPolicyPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final EventListener retVal = AbstractPopoverViewImpl_getKeyDownEventListener(proxiedInstance);
        return retVal;
      } else {
        return super.getKeyDownEventListener();
      }
    }

    @Override public void keyDownEventListener(Object event) {
      if (proxyHelper != null) {
        final HitPolicyPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.keyDownEventListener(event);
      } else {
        super.keyDownEventListener(event);
      }
    }

    @Override public boolean isEscapeKeyPressed(KeyboardEvent event) {
      if (proxyHelper != null) {
        final HitPolicyPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isEscapeKeyPressed(event);
        return retVal;
      } else {
        return super.isEscapeKeyPressed(event);
      }
    }

    @Override public boolean isEnterKeyPressed(KeyboardEvent event) {
      if (proxyHelper != null) {
        final HitPolicyPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isEnterKeyPressed(event);
        return retVal;
      } else {
        return super.isEnterKeyPressed(event);
      }
    }

    @Override public void onClosedByKeyboard() {
      if (proxyHelper != null) {
        final HitPolicyPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onClosedByKeyboard();
      } else {
        super.onClosedByKeyboard();
      }
    }

    @Override public Optional getClosedByKeyboardCallback() {
      if (proxyHelper != null) {
        final HitPolicyPopoverViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final Optional retVal = proxiedInstance.getClosedByKeyboardCallback();
        return retVal;
      } else {
        return super.getClosedByKeyboardCallback();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_e_t_d_h_HitPolicyPopoverViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(HitPolicyPopoverViewImpl.class, "Type_factory__o_k_w_c_d_c_e_e_t_d_h_HitPolicyPopoverViewImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { HitPolicyPopoverViewImpl.class, AbstractPopoverViewImpl.class, Object.class, PopoverView.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, CanBeClosedByKeyboard.class, HitPolicyPopoverView.class, UberElement.class, HasPresenter.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2018 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"HitPolicyPopoverViewImpl.\"] #popover-container {\n  display: inline;\n}\n[data-i18n-prefix=\"HitPolicyPopoverViewImpl.\"] #popover-container .popover {\n  min-width: 300px;\n}\n[data-i18n-prefix=\"HitPolicyPopoverViewImpl.\"] .kie-dmn-hit-policy-container #kieHitPolicy {\n  width: 100%;\n}\n[data-i18n-prefix=\"HitPolicyPopoverViewImpl.\"] .kie-dmn-hit-policy-container #kieBuiltinAggregator {\n  width: 100%;\n}\n[data-i18n-prefix=\"HitPolicyPopoverViewImpl.\"] #kieHitPolicy .bootstrap-select {\n  width: 100%;\n}\n[data-i18n-prefix=\"HitPolicyPopoverViewImpl.\"] #kieBuiltinAggregator .bootstrap-select {\n  width: 100%;\n}\n\n");
  }

  public HitPolicyPopoverViewImpl createInstance(final ContextManager contextManager) {
    final Div _popoverContentElement_4 = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final Div _popoverElement_3 = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final JQuery<Popover> _jQueryPopover_7 = (JQuery) contextManager.getInstance("Producer_factory__o_u_c_v_p_w_JQueryProducer_JQuery__quals__j_e_i_Any_j_e_i_Default");
    final Select _lstHitPolicies_0 = (Select) contextManager.getInstance("Type_factory__o_u_c_v_p_w_Select__quals__j_e_i_Any_j_e_i_Default");
    final TranslationService _translationService_8 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final BuiltinAggregatorUtils _builtinAggregatorUtils_2 = (BuiltinAggregatorUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_e_t_d_h_BuiltinAggregatorUtils__quals__j_e_i_Any_j_e_i_Default");
    final Span _hitPolicyLabel_5 = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final Span _builtinAggregatorLabel_6 = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final Select _lstBuiltinAggregator_1 = (Select) contextManager.getInstance("Type_factory__o_u_c_v_p_w_Select__quals__j_e_i_Any_j_e_i_Default");
    final HitPolicyPopoverViewImpl instance = new HitPolicyPopoverViewImpl(_lstHitPolicies_0, _lstBuiltinAggregator_1, _builtinAggregatorUtils_2, _popoverElement_3, _popoverContentElement_4, _hitPolicyLabel_5, _builtinAggregatorLabel_6, _jQueryPopover_7, _translationService_8);
    registerDependentScopedReference(instance, _popoverContentElement_4);
    registerDependentScopedReference(instance, _popoverElement_3);
    registerDependentScopedReference(instance, _jQueryPopover_7);
    registerDependentScopedReference(instance, _lstHitPolicies_0);
    registerDependentScopedReference(instance, _translationService_8);
    registerDependentScopedReference(instance, _hitPolicyLabel_5);
    registerDependentScopedReference(instance, _builtinAggregatorLabel_6);
    registerDependentScopedReference(instance, _lstBuiltinAggregator_1);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_e_t_d_h_HitPolicyPopoverViewImplTemplateResource templateForHitPolicyPopoverViewImpl = GWT.create(o_k_w_c_d_c_e_e_t_d_h_HitPolicyPopoverViewImplTemplateResource.class);
    Element parentElementForTemplateOfHitPolicyPopoverViewImpl = TemplateUtil.getRootTemplateParentElement(templateForHitPolicyPopoverViewImpl.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/expressions/types/dtable/hitpolicy/HitPolicyPopoverViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/expressions/types/dtable/hitpolicy/HitPolicyPopoverViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfHitPolicyPopoverViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfHitPolicyPopoverViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(6);
    dataFieldMetas.put("popover", new DataFieldMeta());
    dataFieldMetas.put("popover-content", new DataFieldMeta());
    dataFieldMetas.put("lstHitPolicies", new DataFieldMeta());
    dataFieldMetas.put("lstBuiltinAggregator", new DataFieldMeta());
    dataFieldMetas.put("hitPolicyLabel", new DataFieldMeta());
    dataFieldMetas.put("builtinAggregatorLabel", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyPopoverViewImpl", "org/kie/workbench/common/dmn/client/editors/expressions/types/dtable/hitpolicy/HitPolicyPopoverViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AbstractPopoverViewImpl_Div_popoverElement(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "popover");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyPopoverViewImpl", "org/kie/workbench/common/dmn/client/editors/expressions/types/dtable/hitpolicy/HitPolicyPopoverViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AbstractPopoverViewImpl_Div_popoverContentElement(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "popover-content");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyPopoverViewImpl", "org/kie/workbench/common/dmn/client/editors/expressions/types/dtable/hitpolicy/HitPolicyPopoverViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(HitPolicyPopoverViewImpl_Select_lstHitPolicies(instance).getElement());
      }
    }, dataFieldElements, dataFieldMetas, "lstHitPolicies");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyPopoverViewImpl", "org/kie/workbench/common/dmn/client/editors/expressions/types/dtable/hitpolicy/HitPolicyPopoverViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(HitPolicyPopoverViewImpl_Select_lstBuiltinAggregator(instance).getElement());
      }
    }, dataFieldElements, dataFieldMetas, "lstBuiltinAggregator");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyPopoverViewImpl", "org/kie/workbench/common/dmn/client/editors/expressions/types/dtable/hitpolicy/HitPolicyPopoverViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HitPolicyPopoverViewImpl_Span_hitPolicyLabel(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "hitPolicyLabel");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyPopoverViewImpl", "org/kie/workbench/common/dmn/client/editors/expressions/types/dtable/hitpolicy/HitPolicyPopoverViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HitPolicyPopoverViewImpl_Span_builtinAggregatorLabel(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "builtinAggregatorLabel");
    templateFieldsMap.put("popover", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AbstractPopoverViewImpl_Div_popoverElement(instance))));
    templateFieldsMap.put("popover-content", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AbstractPopoverViewImpl_Div_popoverContentElement(instance))));
    templateFieldsMap.put("lstHitPolicies", ElementWrapperWidget.getWidget(HitPolicyPopoverViewImpl_Select_lstHitPolicies(instance).getElement()));
    templateFieldsMap.put("lstBuiltinAggregator", ElementWrapperWidget.getWidget(HitPolicyPopoverViewImpl_Select_lstBuiltinAggregator(instance).getElement()));
    templateFieldsMap.put("hitPolicyLabel", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HitPolicyPopoverViewImpl_Span_hitPolicyLabel(instance))));
    templateFieldsMap.put("builtinAggregatorLabel", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HitPolicyPopoverViewImpl_Span_builtinAggregatorLabel(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfHitPolicyPopoverViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((HitPolicyPopoverViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final HitPolicyPopoverViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  public Proxy createProxy(final Context context) {
    final Proxy<HitPolicyPopoverViewImpl> proxyImpl = new Type_factory__o_k_w_c_d_c_e_e_t_d_h_HitPolicyPopoverViewImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static Span HitPolicyPopoverViewImpl_Span_builtinAggregatorLabel(HitPolicyPopoverViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyPopoverViewImpl::builtinAggregatorLabel;
  }-*/;

  native static void HitPolicyPopoverViewImpl_Span_builtinAggregatorLabel(HitPolicyPopoverViewImpl instance, Span value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyPopoverViewImpl::builtinAggregatorLabel = value;
  }-*/;

  native static Select HitPolicyPopoverViewImpl_Select_lstHitPolicies(HitPolicyPopoverViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyPopoverViewImpl::lstHitPolicies;
  }-*/;

  native static void HitPolicyPopoverViewImpl_Select_lstHitPolicies(HitPolicyPopoverViewImpl instance, Select value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyPopoverViewImpl::lstHitPolicies = value;
  }-*/;

  native static Div AbstractPopoverViewImpl_Div_popoverElement(AbstractPopoverViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl::popoverElement;
  }-*/;

  native static void AbstractPopoverViewImpl_Div_popoverElement(AbstractPopoverViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl::popoverElement = value;
  }-*/;

  native static Select HitPolicyPopoverViewImpl_Select_lstBuiltinAggregator(HitPolicyPopoverViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyPopoverViewImpl::lstBuiltinAggregator;
  }-*/;

  native static void HitPolicyPopoverViewImpl_Select_lstBuiltinAggregator(HitPolicyPopoverViewImpl instance, Select value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyPopoverViewImpl::lstBuiltinAggregator = value;
  }-*/;

  native static Span HitPolicyPopoverViewImpl_Span_hitPolicyLabel(HitPolicyPopoverViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyPopoverViewImpl::hitPolicyLabel;
  }-*/;

  native static void HitPolicyPopoverViewImpl_Span_hitPolicyLabel(HitPolicyPopoverViewImpl instance, Span value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyPopoverViewImpl::hitPolicyLabel = value;
  }-*/;

  native static Div AbstractPopoverViewImpl_Div_popoverContentElement(AbstractPopoverViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl::popoverContentElement;
  }-*/;

  native static void AbstractPopoverViewImpl_Div_popoverContentElement(AbstractPopoverViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl::popoverContentElement = value;
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