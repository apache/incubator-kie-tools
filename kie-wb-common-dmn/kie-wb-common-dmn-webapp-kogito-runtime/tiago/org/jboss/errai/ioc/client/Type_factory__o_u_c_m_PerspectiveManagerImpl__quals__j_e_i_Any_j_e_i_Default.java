package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PerspectiveManagerImpl;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.PanelManagerImpl;
import org.uberfire.client.workbench.WorkbenchServicesProxy;
import org.uberfire.client.workbench.WorkbenchServicesProxyClientImpl;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;

public class Type_factory__o_u_c_m_PerspectiveManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<PerspectiveManagerImpl> { private class Type_factory__o_u_c_m_PerspectiveManagerImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends PerspectiveManagerImpl implements Proxy<PerspectiveManagerImpl> {
    private final ProxyHelper<PerspectiveManagerImpl> proxyHelper = new ProxyHelperImpl<PerspectiveManagerImpl>("Type_factory__o_u_c_m_PerspectiveManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final PerspectiveManagerImpl instance) {

    }

    public PerspectiveManagerImpl asBeanType() {
      return this;
    }

    public void setInstance(final PerspectiveManagerImpl instance) {
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

    @Override public void switchToPerspective(PlaceRequest placeRequest, PerspectiveActivity activity, ParameterizedCommand doWhenFinished) {
      if (proxyHelper != null) {
        final PerspectiveManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.switchToPerspective(placeRequest, activity, doWhenFinished);
      } else {
        super.switchToPerspective(placeRequest, activity, doWhenFinished);
      }
    }

    @Override public PerspectiveActivity getCurrentPerspective() {
      if (proxyHelper != null) {
        final PerspectiveManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final PerspectiveActivity retVal = proxiedInstance.getCurrentPerspective();
        return retVal;
      } else {
        return super.getCurrentPerspective();
      }
    }

    @Override public PerspectiveDefinition getLivePerspectiveDefinition() {
      if (proxyHelper != null) {
        final PerspectiveManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final PerspectiveDefinition retVal = proxiedInstance.getLivePerspectiveDefinition();
        return retVal;
      } else {
        return super.getLivePerspectiveDefinition();
      }
    }

    @Override public void savePerspectiveState(Command doWhenFinished) {
      if (proxyHelper != null) {
        final PerspectiveManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.savePerspectiveState(doWhenFinished);
      } else {
        super.savePerspectiveState(doWhenFinished);
      }
    }

    @Override public void loadPerspectiveStates(ParameterizedCommand doWhenFinished) {
      if (proxyHelper != null) {
        final PerspectiveManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.loadPerspectiveStates(doWhenFinished);
      } else {
        super.loadPerspectiveStates(doWhenFinished);
      }
    }

    @Override public void removePerspectiveState(String perspectiveId, Command doWhenFinished) {
      if (proxyHelper != null) {
        final PerspectiveManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.removePerspectiveState(perspectiveId, doWhenFinished);
      } else {
        super.removePerspectiveState(perspectiveId, doWhenFinished);
      }
    }

    @Override public void removePerspectiveStates(Command doWhenFinished) {
      if (proxyHelper != null) {
        final PerspectiveManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.removePerspectiveStates(doWhenFinished);
      } else {
        super.removePerspectiveStates(doWhenFinished);
      }
    }

    @Override public String getDefaultPerspectiveIdentifier() {
      if (proxyHelper != null) {
        final PerspectiveManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getDefaultPerspectiveIdentifier();
        return retVal;
      } else {
        return super.getDefaultPerspectiveIdentifier();
      }
    }

    @Override public PlaceRequest getCurrentPerspectivePlaceRequest() {
      if (proxyHelper != null) {
        final PerspectiveManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final PlaceRequest retVal = proxiedInstance.getCurrentPerspectivePlaceRequest();
        return retVal;
      } else {
        return super.getCurrentPerspectivePlaceRequest();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final PerspectiveManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_m_PerspectiveManagerImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PerspectiveManagerImpl.class, "Type_factory__o_u_c_m_PerspectiveManagerImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PerspectiveManagerImpl.class, Object.class, PerspectiveManager.class });
  }

  public PerspectiveManagerImpl createInstance(final ContextManager contextManager) {
    final PerspectiveManagerImpl instance = new PerspectiveManagerImpl();
    setIncompleteInstance(instance);
    final PanelManagerImpl PerspectiveManagerImpl_panelManager = (PanelManagerImpl) contextManager.getInstance("Type_factory__o_u_c_w_PanelManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    PerspectiveManagerImpl_PanelManager_panelManager(instance, PerspectiveManagerImpl_panelManager);
    final ActivityBeansCache PerspectiveManagerImpl_activityBeansCache = (ActivityBeansCache) contextManager.getInstance("Type_factory__o_u_c_m_ActivityBeansCache__quals__j_e_i_Any_j_e_i_Default");
    PerspectiveManagerImpl_ActivityBeansCache_activityBeansCache(instance, PerspectiveManagerImpl_activityBeansCache);
    final SyncBeanManager PerspectiveManagerImpl_iocManager = (SyncBeanManager) contextManager.getInstance("Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, PerspectiveManagerImpl_iocManager);
    PerspectiveManagerImpl_SyncBeanManager_iocManager(instance, PerspectiveManagerImpl_iocManager);
    final WorkbenchServicesProxyClientImpl PerspectiveManagerImpl_wbServices = (WorkbenchServicesProxyClientImpl) contextManager.getInstance("Type_factory__o_u_c_w_WorkbenchServicesProxyClientImpl__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, PerspectiveManagerImpl_wbServices);
    PerspectiveManagerImpl_WorkbenchServicesProxy_wbServices(instance, PerspectiveManagerImpl_wbServices);
    final Event PerspectiveManagerImpl_perspectiveChangeEvent = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { PerspectiveChange.class }, new Annotation[] { });
    registerDependentScopedReference(instance, PerspectiveManagerImpl_perspectiveChangeEvent);
    PerspectiveManagerImpl_Event_perspectiveChangeEvent(instance, PerspectiveManagerImpl_perspectiveChangeEvent);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<PerspectiveManagerImpl> proxyImpl = new Type_factory__o_u_c_m_PerspectiveManagerImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static ActivityBeansCache PerspectiveManagerImpl_ActivityBeansCache_activityBeansCache(PerspectiveManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.PerspectiveManagerImpl::activityBeansCache;
  }-*/;

  native static void PerspectiveManagerImpl_ActivityBeansCache_activityBeansCache(PerspectiveManagerImpl instance, ActivityBeansCache value) /*-{
    instance.@org.uberfire.client.mvp.PerspectiveManagerImpl::activityBeansCache = value;
  }-*/;

  native static WorkbenchServicesProxy PerspectiveManagerImpl_WorkbenchServicesProxy_wbServices(PerspectiveManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.PerspectiveManagerImpl::wbServices;
  }-*/;

  native static void PerspectiveManagerImpl_WorkbenchServicesProxy_wbServices(PerspectiveManagerImpl instance, WorkbenchServicesProxy value) /*-{
    instance.@org.uberfire.client.mvp.PerspectiveManagerImpl::wbServices = value;
  }-*/;

  native static SyncBeanManager PerspectiveManagerImpl_SyncBeanManager_iocManager(PerspectiveManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.PerspectiveManagerImpl::iocManager;
  }-*/;

  native static void PerspectiveManagerImpl_SyncBeanManager_iocManager(PerspectiveManagerImpl instance, SyncBeanManager value) /*-{
    instance.@org.uberfire.client.mvp.PerspectiveManagerImpl::iocManager = value;
  }-*/;

  native static Event PerspectiveManagerImpl_Event_perspectiveChangeEvent(PerspectiveManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.PerspectiveManagerImpl::perspectiveChangeEvent;
  }-*/;

  native static void PerspectiveManagerImpl_Event_perspectiveChangeEvent(PerspectiveManagerImpl instance, Event<PerspectiveChange> value) /*-{
    instance.@org.uberfire.client.mvp.PerspectiveManagerImpl::perspectiveChangeEvent = value;
  }-*/;

  native static PanelManager PerspectiveManagerImpl_PanelManager_panelManager(PerspectiveManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.PerspectiveManagerImpl::panelManager;
  }-*/;

  native static void PerspectiveManagerImpl_PanelManager_panelManager(PerspectiveManagerImpl instance, PanelManager value) /*-{
    instance.@org.uberfire.client.mvp.PerspectiveManagerImpl::panelManager = value;
  }-*/;
}