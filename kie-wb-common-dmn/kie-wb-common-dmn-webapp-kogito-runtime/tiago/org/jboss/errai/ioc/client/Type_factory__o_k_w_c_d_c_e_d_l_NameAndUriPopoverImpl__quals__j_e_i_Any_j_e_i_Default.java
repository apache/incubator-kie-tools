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
import org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUriPopoverImpl;
import org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverView;
import org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverView.Presenter;
import org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverViewImpl;
import org.kie.workbench.common.dmn.client.editors.types.CanBeClosedByKeyboard;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls.Editor;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.PopupEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverImpl;

public class Type_factory__o_k_w_c_d_c_e_d_l_NameAndUriPopoverImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<NameAndUriPopoverImpl> { private class Type_factory__o_k_w_c_d_c_e_d_l_NameAndUriPopoverImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends NameAndUriPopoverImpl implements Proxy<NameAndUriPopoverImpl> {
    private final ProxyHelper<NameAndUriPopoverImpl> proxyHelper = new ProxyHelperImpl<NameAndUriPopoverImpl>("Type_factory__o_k_w_c_d_c_e_d_l_NameAndUriPopoverImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final NameAndUriPopoverImpl instance) {

    }

    public NameAndUriPopoverImpl asBeanType() {
      return this;
    }

    public void setInstance(final NameAndUriPopoverImpl instance) {
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
        final NameAndUriPopoverImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public HTMLElement getElement() {
      if (proxyHelper != null) {
        final NameAndUriPopoverImpl proxiedInstance = proxyHelper.getInstance(this);
        final HTMLElement retVal = proxiedInstance.getElement();
        return retVal;
      } else {
        return super.getElement();
      }
    }

    @Override public void setOnClosedByKeyboardCallback(Consumer callback) {
      if (proxyHelper != null) {
        final NameAndUriPopoverImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setOnClosedByKeyboardCallback(callback);
      } else {
        super.setOnClosedByKeyboardCallback(callback);
      }
    }

    @Override public void show() {
      if (proxyHelper != null) {
        final NameAndUriPopoverImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.show();
      } else {
        super.show();
      }
    }

    @Override public void hide() {
      if (proxyHelper != null) {
        final NameAndUriPopoverImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.hide();
      } else {
        super.hide();
      }
    }

    @Override public void setOnExternalLinkCreated(Consumer onExternalLinkCreated) {
      if (proxyHelper != null) {
        final NameAndUriPopoverImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setOnExternalLinkCreated(onExternalLinkCreated);
      } else {
        super.setOnExternalLinkCreated(onExternalLinkCreated);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final NameAndUriPopoverImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }

    @Override public void bind(Presenter bound, int uiRowIndex, int uiColumnIndex) {
      if (proxyHelper != null) {
        final NameAndUriPopoverImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.bind(bound, uiRowIndex, uiColumnIndex);
      } else {
        super.bind(bound, uiRowIndex, uiColumnIndex);
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_d_l_NameAndUriPopoverImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(NameAndUriPopoverImpl.class, "Type_factory__o_k_w_c_d_c_e_d_l_NameAndUriPopoverImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { NameAndUriPopoverImpl.class, AbstractPopoverImpl.class, Object.class, Editor.class, PopupEditorControls.class, IsElement.class, CanBeClosedByKeyboard.class, Presenter.class });
  }

  public NameAndUriPopoverImpl createInstance(final ContextManager contextManager) {
    final NameAndUrlPopoverView _view_0 = (NameAndUrlPopoverViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_d_l_NameAndUrlPopoverViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final NameAndUriPopoverImpl instance = new NameAndUriPopoverImpl(_view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final NameAndUriPopoverImpl instance) {
    instance.init();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<NameAndUriPopoverImpl> proxyImpl = new Type_factory__o_k_w_c_d_c_e_d_l_NameAndUriPopoverImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}