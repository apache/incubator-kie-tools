package org.jboss.errai.ioc.client;

import elemental2.dom.HTMLElement;
import java.util.List;
import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.editors.contextmenu.ContextMenu;
import org.kie.workbench.common.dmn.client.editors.contextmenu.ContextMenu.View;
import org.kie.workbench.common.dmn.client.editors.contextmenu.ContextMenuView;
import org.uberfire.mvp.Command;

public class Type_factory__o_k_w_c_d_c_e_c_ContextMenu__quals__j_e_i_Any_j_e_i_Default extends Factory<ContextMenu> { private class Type_factory__o_k_w_c_d_c_e_c_ContextMenu__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ContextMenu implements Proxy<ContextMenu> {
    private final ProxyHelper<ContextMenu> proxyHelper = new ProxyHelperImpl<ContextMenu>("Type_factory__o_k_w_c_d_c_e_c_ContextMenu__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_c_e_c_ContextMenu__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null);
    }

    public void initProxyProperties(final ContextMenu instance) {

    }

    public ContextMenu asBeanType() {
      return this;
    }

    public void setInstance(final ContextMenu instance) {
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
        final ContextMenu proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public void show() {
      if (proxyHelper != null) {
        final ContextMenu proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.show();
      } else {
        super.show();
      }
    }

    @Override public void show(Consumer contextMenuConsumer) {
      if (proxyHelper != null) {
        final ContextMenu proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.show(contextMenuConsumer);
      } else {
        super.show(contextMenuConsumer);
      }
    }

    @Override public void hide() {
      if (proxyHelper != null) {
        final ContextMenu proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.hide();
      } else {
        super.hide();
      }
    }

    @Override public HTMLElement getElement() {
      if (proxyHelper != null) {
        final ContextMenu proxiedInstance = proxyHelper.getInstance(this);
        final HTMLElement retVal = proxiedInstance.getElement();
        return retVal;
      } else {
        return super.getElement();
      }
    }

    @Override public List getItems() {
      if (proxyHelper != null) {
        final ContextMenu proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getItems();
        return retVal;
      } else {
        return super.getItems();
      }
    }

    @Override public void resetMenuItems() {
      if (proxyHelper != null) {
        final ContextMenu proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.resetMenuItems();
      } else {
        super.resetMenuItems();
      }
    }

    @Override public void setHeaderMenu(String title, String iconClass) {
      if (proxyHelper != null) {
        final ContextMenu proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setHeaderMenu(title, iconClass);
      } else {
        super.setHeaderMenu(title, iconClass);
      }
    }

    @Override public void addTextMenuItem(String itemName, boolean isEnabled, Command command) {
      if (proxyHelper != null) {
        final ContextMenu proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addTextMenuItem(itemName, isEnabled, command);
      } else {
        super.addTextMenuItem(itemName, isEnabled, command);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ContextMenu proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_c_ContextMenu__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ContextMenu.class, "Type_factory__o_k_w_c_d_c_e_c_ContextMenu__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ContextMenu.class, Object.class });
  }

  public ContextMenu createInstance(final ContextManager contextManager) {
    final View _view_0 = (ContextMenuView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_c_ContextMenuView__quals__j_e_i_Any_j_e_i_Default");
    final ContextMenu instance = new ContextMenu(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ContextMenu instance) {
    instance.init();
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_c_e_c_ContextMenu__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.client.editors.contextmenu.ContextMenu an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.client.editors.contextmenu.ContextMenu ([org.kie.workbench.common.dmn.client.editors.contextmenu.ContextMenu$View])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ContextMenu> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}