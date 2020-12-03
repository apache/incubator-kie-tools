package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.ShapeSetsMenuItemsBuilder;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.ShapeSetsMenuItemsBuilder.Callback;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManagerImpl;
import org.uberfire.workbench.model.menu.MenuItem;

public class Type_factory__o_k_w_c_s_c_w_m_d_ShapeSetsMenuItemsBuilder__quals__j_e_i_Any_j_e_i_Default extends Factory<ShapeSetsMenuItemsBuilder> { private class Type_factory__o_k_w_c_s_c_w_m_d_ShapeSetsMenuItemsBuilder__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ShapeSetsMenuItemsBuilder implements Proxy<ShapeSetsMenuItemsBuilder> {
    private final ProxyHelper<ShapeSetsMenuItemsBuilder> proxyHelper = new ProxyHelperImpl<ShapeSetsMenuItemsBuilder>("Type_factory__o_k_w_c_s_c_w_m_d_ShapeSetsMenuItemsBuilder__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_s_c_w_m_d_ShapeSetsMenuItemsBuilder__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null);
    }

    public void initProxyProperties(final ShapeSetsMenuItemsBuilder instance) {

    }

    public ShapeSetsMenuItemsBuilder asBeanType() {
      return this;
    }

    public void setInstance(final ShapeSetsMenuItemsBuilder instance) {
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

    @Override public MenuItem build(String title, String prefix, Callback callback) {
      if (proxyHelper != null) {
        final ShapeSetsMenuItemsBuilder proxiedInstance = proxyHelper.getInstance(this);
        final MenuItem retVal = proxiedInstance.build(title, prefix, callback);
        return retVal;
      } else {
        return super.build(title, prefix, callback);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ShapeSetsMenuItemsBuilder proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_w_m_d_ShapeSetsMenuItemsBuilder__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ShapeSetsMenuItemsBuilder.class, "Type_factory__o_k_w_c_s_c_w_m_d_ShapeSetsMenuItemsBuilder__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ShapeSetsMenuItemsBuilder.class, Object.class });
  }

  public ShapeSetsMenuItemsBuilder createInstance(final ContextManager contextManager) {
    final ShapeManager _shapeManager_0 = (ShapeManagerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ShapeManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final ShapeSetsMenuItemsBuilder instance = new ShapeSetsMenuItemsBuilder(_shapeManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_s_c_w_m_d_ShapeSetsMenuItemsBuilder__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.stunner.client.widgets.menu.dev.ShapeSetsMenuItemsBuilder an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.stunner.client.widgets.menu.dev.ShapeSetsMenuItemsBuilder ([org.kie.workbench.common.stunner.core.client.api.ShapeManager])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ShapeSetsMenuItemsBuilder> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}