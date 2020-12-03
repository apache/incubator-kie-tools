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
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.workbench.WorkbenchLayoutInfo;
import org.uberfire.client.workbench.WorkbenchLayoutInfoImpl;
import org.uberfire.client.workbench.events.ClosePlaceEvent;
import org.uberfire.client.workbench.events.PlaceLostFocusEvent;
import org.uberfire.client.workbench.widgets.notifications.NotificationManager;
import org.uberfire.workbench.events.NotificationEvent;

public class Type_factory__o_u_c_w_w_n_NotificationManager__quals__j_e_i_Any_j_e_i_Default extends Factory<NotificationManager> { private class Type_factory__o_u_c_w_w_n_NotificationManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends NotificationManager implements Proxy<NotificationManager> {
    private final ProxyHelper<NotificationManager> proxyHelper = new ProxyHelperImpl<NotificationManager>("Type_factory__o_u_c_w_w_n_NotificationManager__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final NotificationManager instance) {

    }

    public NotificationManager asBeanType() {
      return this;
    }

    public void setInstance(final NotificationManager instance) {
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

    @Override public void addNotification(NotificationEvent event) {
      if (proxyHelper != null) {
        final NotificationManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addNotification(event);
      } else {
        super.addNotification(event);
      }
    }

    @Override public void onClosePlaceEvent(ClosePlaceEvent event) {
      if (proxyHelper != null) {
        final NotificationManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onClosePlaceEvent(event);
      } else {
        super.onClosePlaceEvent(event);
      }
    }

    @Override public void onPlaceLostFocus(PlaceLostFocusEvent event) {
      if (proxyHelper != null) {
        final NotificationManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onPlaceLostFocus(event);
      } else {
        super.onPlaceLostFocus(event);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final NotificationManager proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_w_w_n_NotificationManager__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(NotificationManager.class, "Type_factory__o_u_c_w_w_n_NotificationManager__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { NotificationManager.class, Object.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.uberfire.workbench.events.NotificationEvent", new AbstractCDIEventCallback<NotificationEvent>() {
      public void fireEvent(final NotificationEvent event) {
        final NotificationManager instance = Factory.maybeUnwrapProxy((NotificationManager) context.getInstance("Type_factory__o_u_c_w_w_n_NotificationManager__quals__j_e_i_Any_j_e_i_Default"));
        instance.addNotification(event);
      }
      public String toString() {
        return "Observer: org.uberfire.workbench.events.NotificationEvent []";
      }
    });
    CDI.subscribeLocal("org.uberfire.client.workbench.events.ClosePlaceEvent", new AbstractCDIEventCallback<ClosePlaceEvent>() {
      public void fireEvent(final ClosePlaceEvent event) {
        final NotificationManager instance = Factory.maybeUnwrapProxy((NotificationManager) context.getInstance("Type_factory__o_u_c_w_w_n_NotificationManager__quals__j_e_i_Any_j_e_i_Default"));
        instance.onClosePlaceEvent(event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.events.ClosePlaceEvent []";
      }
    });
    CDI.subscribeLocal("org.uberfire.client.workbench.events.PlaceLostFocusEvent", new AbstractCDIEventCallback<PlaceLostFocusEvent>() {
      public void fireEvent(final PlaceLostFocusEvent event) {
        final NotificationManager instance = Factory.maybeUnwrapProxy((NotificationManager) context.getInstance("Type_factory__o_u_c_w_w_n_NotificationManager__quals__j_e_i_Any_j_e_i_Default"));
        instance.onPlaceLostFocus(event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.events.PlaceLostFocusEvent []";
      }
    });
  }

  public NotificationManager createInstance(final ContextManager contextManager) {
    final WorkbenchLayoutInfo _workbenchLayoutInfo_2 = (WorkbenchLayoutInfoImpl) contextManager.getInstance("Type_factory__o_u_c_w_WorkbenchLayoutInfoImpl__quals__j_e_i_Any_j_e_i_Default");
    final SyncBeanManager _iocManager_0 = (SyncBeanManager) contextManager.getInstance("Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default");
    final PlaceManager _placeManager_1 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final NotificationManager instance = new NotificationManager(_iocManager_0, _placeManager_1, _workbenchLayoutInfo_2);
    registerDependentScopedReference(instance, _iocManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<NotificationManager> proxyImpl = new Type_factory__o_u_c_w_w_n_NotificationManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}