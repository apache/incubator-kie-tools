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
import org.uberfire.backend.vfs.impl.ForceUnlockEvent;
import org.uberfire.client.mvp.ForceUnlockEventObserver;
import org.uberfire.client.workbench.VFSLockServiceProxy;
import org.uberfire.client.workbench.VFSLockServiceProxyClientImpl;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;

public class Type_factory__o_u_c_m_ForceUnlockEventObserver__quals__j_e_i_Any_j_e_i_Default extends Factory<ForceUnlockEventObserver> { private class Type_factory__o_u_c_m_ForceUnlockEventObserver__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ForceUnlockEventObserver implements Proxy<ForceUnlockEventObserver> {
    private final ProxyHelper<ForceUnlockEventObserver> proxyHelper = new ProxyHelperImpl<ForceUnlockEventObserver>("Type_factory__o_u_c_m_ForceUnlockEventObserver__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ForceUnlockEventObserver instance) {

    }

    public ForceUnlockEventObserver asBeanType() {
      return this;
    }

    public void setInstance(final ForceUnlockEventObserver instance) {
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

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ForceUnlockEventObserver proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_m_ForceUnlockEventObserver__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ForceUnlockEventObserver.class, "Type_factory__o_u_c_m_ForceUnlockEventObserver__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ForceUnlockEventObserver.class, Object.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.uberfire.backend.vfs.impl.ForceUnlockEvent", new AbstractCDIEventCallback<ForceUnlockEvent>() {
      public void fireEvent(final ForceUnlockEvent event) {
        final ForceUnlockEventObserver instance = Factory.maybeUnwrapProxy((ForceUnlockEventObserver) context.getInstance("Type_factory__o_u_c_m_ForceUnlockEventObserver__quals__j_e_i_Any_j_e_i_Default"));
        ForceUnlockEventObserver_onForceUnlock_ForceUnlockEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.backend.vfs.impl.ForceUnlockEvent []";
      }
    });
  }

  public ForceUnlockEventObserver createInstance(final ContextManager contextManager) {
    final ForceUnlockEventObserver instance = new ForceUnlockEventObserver();
    setIncompleteInstance(instance);
    final VFSLockServiceProxyClientImpl ForceUnlockEventObserver_lockService = (VFSLockServiceProxyClientImpl) contextManager.getInstance("Type_factory__o_u_c_w_VFSLockServiceProxyClientImpl__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ForceUnlockEventObserver_lockService);
    ForceUnlockEventObserver_VFSLockServiceProxy_lockService(instance, ForceUnlockEventObserver_lockService);
    final ErrorPopupPresenter ForceUnlockEventObserver_errorPopupPresenter = (ErrorPopupPresenter) contextManager.getInstance("Type_factory__o_u_c_w_w_c_ErrorPopupPresenter__quals__j_e_i_Any_j_e_i_Default");
    ForceUnlockEventObserver_ErrorPopupPresenter_errorPopupPresenter(instance, ForceUnlockEventObserver_errorPopupPresenter);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ForceUnlockEventObserver> proxyImpl = new Type_factory__o_u_c_m_ForceUnlockEventObserver__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static ErrorPopupPresenter ForceUnlockEventObserver_ErrorPopupPresenter_errorPopupPresenter(ForceUnlockEventObserver instance) /*-{
    return instance.@org.uberfire.client.mvp.ForceUnlockEventObserver::errorPopupPresenter;
  }-*/;

  native static void ForceUnlockEventObserver_ErrorPopupPresenter_errorPopupPresenter(ForceUnlockEventObserver instance, ErrorPopupPresenter value) /*-{
    instance.@org.uberfire.client.mvp.ForceUnlockEventObserver::errorPopupPresenter = value;
  }-*/;

  native static VFSLockServiceProxy ForceUnlockEventObserver_VFSLockServiceProxy_lockService(ForceUnlockEventObserver instance) /*-{
    return instance.@org.uberfire.client.mvp.ForceUnlockEventObserver::lockService;
  }-*/;

  native static void ForceUnlockEventObserver_VFSLockServiceProxy_lockService(ForceUnlockEventObserver instance, VFSLockServiceProxy value) /*-{
    instance.@org.uberfire.client.mvp.ForceUnlockEventObserver::lockService = value;
  }-*/;

  public native static void ForceUnlockEventObserver_onForceUnlock_ForceUnlockEvent(ForceUnlockEventObserver instance, ForceUnlockEvent a0) /*-{
    instance.@org.uberfire.client.mvp.ForceUnlockEventObserver::onForceUnlock(Lorg/uberfire/backend/vfs/impl/ForceUnlockEvent;)(a0);
  }-*/;
}