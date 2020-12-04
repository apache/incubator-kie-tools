package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.widgets.client.menu.ResetPerspectivesMenuBuilder;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PerspectiveManagerImpl;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.workbench.model.menu.MenuFactory.CustomMenuBuilder;
import org.uberfire.workbench.model.menu.MenuItem;

public class Type_factory__o_k_w_c_w_c_m_ResetPerspectivesMenuBuilder__quals__j_e_i_Any_j_e_i_Default extends Factory<ResetPerspectivesMenuBuilder> { private class Type_factory__o_k_w_c_w_c_m_ResetPerspectivesMenuBuilder__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ResetPerspectivesMenuBuilder implements Proxy<ResetPerspectivesMenuBuilder> {
    private final ProxyHelper<ResetPerspectivesMenuBuilder> proxyHelper = new ProxyHelperImpl<ResetPerspectivesMenuBuilder>("Type_factory__o_k_w_c_w_c_m_ResetPerspectivesMenuBuilder__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ResetPerspectivesMenuBuilder instance) {

    }

    public ResetPerspectivesMenuBuilder asBeanType() {
      return this;
    }

    public void setInstance(final ResetPerspectivesMenuBuilder instance) {
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

    @Override public void push(CustomMenuBuilder element) {
      if (proxyHelper != null) {
        final ResetPerspectivesMenuBuilder proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.push(element);
      } else {
        super.push(element);
      }
    }

    @Override public MenuItem build() {
      if (proxyHelper != null) {
        final ResetPerspectivesMenuBuilder proxiedInstance = proxyHelper.getInstance(this);
        final MenuItem retVal = proxiedInstance.build();
        return retVal;
      } else {
        return super.build();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ResetPerspectivesMenuBuilder proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_w_c_m_ResetPerspectivesMenuBuilder__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ResetPerspectivesMenuBuilder.class, "Type_factory__o_k_w_c_w_c_m_ResetPerspectivesMenuBuilder__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ResetPerspectivesMenuBuilder.class, Object.class, CustomMenuBuilder.class });
  }

  public ResetPerspectivesMenuBuilder createInstance(final ContextManager contextManager) {
    final ResetPerspectivesMenuBuilder instance = new ResetPerspectivesMenuBuilder();
    setIncompleteInstance(instance);
    final PlaceManagerImpl ResetPerspectivesMenuBuilder_placeManager = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    ResetPerspectivesMenuBuilder_PlaceManager_placeManager(instance, ResetPerspectivesMenuBuilder_placeManager);
    final PerspectiveManagerImpl ResetPerspectivesMenuBuilder_perspectiveManager = (PerspectiveManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PerspectiveManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    ResetPerspectivesMenuBuilder_PerspectiveManager_perspectiveManager(instance, ResetPerspectivesMenuBuilder_perspectiveManager);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ResetPerspectivesMenuBuilder> proxyImpl = new Type_factory__o_k_w_c_w_c_m_ResetPerspectivesMenuBuilder__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static PlaceManager ResetPerspectivesMenuBuilder_PlaceManager_placeManager(ResetPerspectivesMenuBuilder instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.menu.ResetPerspectivesMenuBuilder::placeManager;
  }-*/;

  native static void ResetPerspectivesMenuBuilder_PlaceManager_placeManager(ResetPerspectivesMenuBuilder instance, PlaceManager value) /*-{
    instance.@org.kie.workbench.common.widgets.client.menu.ResetPerspectivesMenuBuilder::placeManager = value;
  }-*/;

  native static PerspectiveManager ResetPerspectivesMenuBuilder_PerspectiveManager_perspectiveManager(ResetPerspectivesMenuBuilder instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.menu.ResetPerspectivesMenuBuilder::perspectiveManager;
  }-*/;

  native static void ResetPerspectivesMenuBuilder_PerspectiveManager_perspectiveManager(ResetPerspectivesMenuBuilder instance, PerspectiveManager value) /*-{
    instance.@org.kie.workbench.common.widgets.client.menu.ResetPerspectivesMenuBuilder::perspectiveManager = value;
  }-*/;
}