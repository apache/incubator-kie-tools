package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.views.pfly.menu.PartContextMenusView;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.widgets.menu.PartContextMenusPresenter;
import org.uberfire.client.workbench.widgets.menu.PartContextMenusPresenter.View;

public class Type_factory__o_u_c_w_w_m_PartContextMenusPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<PartContextMenusPresenter> { private class Type_factory__o_u_c_w_w_m_PartContextMenusPresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends PartContextMenusPresenter implements Proxy<PartContextMenusPresenter> {
    private final ProxyHelper<PartContextMenusPresenter> proxyHelper = new ProxyHelperImpl<PartContextMenusPresenter>("Type_factory__o_u_c_w_w_m_PartContextMenusPresenter__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final PartContextMenusPresenter instance) {

    }

    public PartContextMenusPresenter asBeanType() {
      return this;
    }

    public void setInstance(final PartContextMenusPresenter instance) {
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

    @Override public View getView() {
      if (proxyHelper != null) {
        final PartContextMenusPresenter proxiedInstance = proxyHelper.getInstance(this);
        final View retVal = proxiedInstance.getView();
        return retVal;
      } else {
        return super.getView();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final PartContextMenusPresenter proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_w_w_m_PartContextMenusPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PartContextMenusPresenter.class, "Type_factory__o_u_c_w_w_m_PartContextMenusPresenter__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PartContextMenusPresenter.class, Object.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.uberfire.client.workbench.events.PlaceGainFocusEvent", new AbstractCDIEventCallback<PlaceGainFocusEvent>() {
      public void fireEvent(final PlaceGainFocusEvent event) {
        final PartContextMenusPresenter instance = Factory.maybeUnwrapProxy((PartContextMenusPresenter) context.getInstance("Type_factory__o_u_c_w_w_m_PartContextMenusPresenter__quals__j_e_i_Any_j_e_i_Default"));
        PartContextMenusPresenter_onWorkbenchPartOnFocus_PlaceGainFocusEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.events.PlaceGainFocusEvent []";
      }
    });
  }

  public PartContextMenusPresenter createInstance(final ContextManager contextManager) {
    final PartContextMenusPresenter instance = new PartContextMenusPresenter();
    setIncompleteInstance(instance);
    final PlaceManagerImpl PartContextMenusPresenter_placeManager = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    PartContextMenusPresenter_PlaceManager_placeManager(instance, PartContextMenusPresenter_placeManager);
    final PartContextMenusView PartContextMenusPresenter_view = (PartContextMenusView) contextManager.getInstance("Type_factory__o_u_c_v_p_m_PartContextMenusView__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, PartContextMenusPresenter_view);
    PartContextMenusPresenter_View_view(instance, PartContextMenusPresenter_view);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<PartContextMenusPresenter> proxyImpl = new Type_factory__o_u_c_w_w_m_PartContextMenusPresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static PlaceManager PartContextMenusPresenter_PlaceManager_placeManager(PartContextMenusPresenter instance) /*-{
    return instance.@org.uberfire.client.workbench.widgets.menu.PartContextMenusPresenter::placeManager;
  }-*/;

  native static void PartContextMenusPresenter_PlaceManager_placeManager(PartContextMenusPresenter instance, PlaceManager value) /*-{
    instance.@org.uberfire.client.workbench.widgets.menu.PartContextMenusPresenter::placeManager = value;
  }-*/;

  native static View PartContextMenusPresenter_View_view(PartContextMenusPresenter instance) /*-{
    return instance.@org.uberfire.client.workbench.widgets.menu.PartContextMenusPresenter::view;
  }-*/;

  native static void PartContextMenusPresenter_View_view(PartContextMenusPresenter instance, View value) /*-{
    instance.@org.uberfire.client.workbench.widgets.menu.PartContextMenusPresenter::view = value;
  }-*/;

  public native static void PartContextMenusPresenter_onWorkbenchPartOnFocus_PlaceGainFocusEvent(PartContextMenusPresenter instance, PlaceGainFocusEvent a0) /*-{
    instance.@org.uberfire.client.workbench.widgets.menu.PartContextMenusPresenter::onWorkbenchPartOnFocus(Lorg/uberfire/client/workbench/events/PlaceGainFocusEvent;)(a0);
  }-*/;
}