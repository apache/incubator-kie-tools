package org.jboss.errai.ioc.client;

import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition.Kind;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.kindselector.HasKindSelectControl;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.kindselector.KindPopoverImpl;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.kindselector.KindPopoverView;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.kindselector.KindPopoverView.Presenter;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.kindselector.KindPopoverViewImpl;
import org.kie.workbench.common.dmn.client.editors.types.CanBeClosedByKeyboard;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls.Editor;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.PopupEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverImpl;

public class Type_factory__o_k_w_c_d_c_e_e_t_f_k_KindPopoverImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<KindPopoverImpl> { private class Type_factory__o_k_w_c_d_c_e_e_t_f_k_KindPopoverImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends KindPopoverImpl implements Proxy<KindPopoverImpl> {
    private final ProxyHelper<KindPopoverImpl> proxyHelper = new ProxyHelperImpl<KindPopoverImpl>("Type_factory__o_k_w_c_d_c_e_e_t_f_k_KindPopoverImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final KindPopoverImpl instance) {

    }

    public KindPopoverImpl asBeanType() {
      return this;
    }

    public void setInstance(final KindPopoverImpl instance) {
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

    @Override public String getPopoverTitle() {
      if (proxyHelper != null) {
        final KindPopoverImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getPopoverTitle();
        return retVal;
      } else {
        return super.getPopoverTitle();
      }
    }

    @Override public void onFunctionKindSelected(Kind kind) {
      if (proxyHelper != null) {
        final KindPopoverImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onFunctionKindSelected(kind);
      } else {
        super.onFunctionKindSelected(kind);
      }
    }

    @Override public void bind(HasKindSelectControl bound, int uiRowIndex, int uiColumnIndex) {
      if (proxyHelper != null) {
        final KindPopoverImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.bind(bound, uiRowIndex, uiColumnIndex);
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public void show() {
      if (proxyHelper != null) {
        final KindPopoverImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.show();
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public void hide() {
      if (proxyHelper != null) {
        final KindPopoverImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.hide();
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public HTMLElement getElement() {
      if (proxyHelper != null) {
        final KindPopoverImpl proxiedInstance = proxyHelper.getInstance(this);
        final HTMLElement retVal = proxiedInstance.getElement();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final KindPopoverImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }

    @Override public void setOnClosedByKeyboardCallback(Consumer callback) {
      if (proxyHelper != null) {
        final KindPopoverImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setOnClosedByKeyboardCallback(callback);
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_e_t_f_k_KindPopoverImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KindPopoverImpl.class, "Type_factory__o_k_w_c_d_c_e_e_t_f_k_KindPopoverImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { KindPopoverImpl.class, AbstractPopoverImpl.class, Object.class, Editor.class, PopupEditorControls.class, IsElement.class, CanBeClosedByKeyboard.class, Presenter.class });
  }

  public KindPopoverImpl createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_1 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final KindPopoverView _view_0 = (KindPopoverViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_e_t_f_k_KindPopoverViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final KindPopoverImpl instance = new KindPopoverImpl(_view_0, _translationService_1);
    registerDependentScopedReference(instance, _translationService_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<KindPopoverImpl> proxyImpl = new Type_factory__o_k_w_c_d_c_e_e_t_f_k_KindPopoverImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}