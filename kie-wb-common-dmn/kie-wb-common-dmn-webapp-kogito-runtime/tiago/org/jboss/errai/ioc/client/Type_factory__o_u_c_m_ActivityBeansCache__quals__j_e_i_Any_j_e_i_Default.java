package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.ResourceTypeManagerCache;
import org.uberfire.client.util.GWTEditorNativeRegister;
import org.uberfire.client.workbench.events.NewPerspectiveEvent;
import org.uberfire.client.workbench.events.NewWorkbenchScreenEvent;

public class Type_factory__o_u_c_m_ActivityBeansCache__quals__j_e_i_Any_j_e_i_Default extends Factory<ActivityBeansCache> { private class Type_factory__o_u_c_m_ActivityBeansCache__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ActivityBeansCache implements Proxy<ActivityBeansCache> {
    private final ProxyHelper<ActivityBeansCache> proxyHelper = new ProxyHelperImpl<ActivityBeansCache>("Type_factory__o_u_c_m_ActivityBeansCache__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ActivityBeansCache instance) {

    }

    public ActivityBeansCache asBeanType() {
      return this;
    }

    public void setInstance(final ActivityBeansCache instance) {
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

    @Override public void removeActivity(String id) {
      if (proxyHelper != null) {
        final ActivityBeansCache proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.removeActivity(id);
      } else {
        super.removeActivity(id);
      }
    }

    @Override public void addNewScreenActivity(SyncBeanDef activityBean) {
      if (proxyHelper != null) {
        final ActivityBeansCache proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addNewScreenActivity(activityBean);
      } else {
        super.addNewScreenActivity(activityBean);
      }
    }

    @Override public void addNewPerspectiveActivity(SyncBeanDef activityBean) {
      if (proxyHelper != null) {
        final ActivityBeansCache proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addNewPerspectiveActivity(activityBean);
      } else {
        super.addNewPerspectiveActivity(activityBean);
      }
    }

    @Override public void addNewEditorActivity(SyncBeanDef activityBean, String priority, String resourceTypeName) {
      if (proxyHelper != null) {
        final ActivityBeansCache proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addNewEditorActivity(activityBean, priority, resourceTypeName);
      } else {
        super.addNewEditorActivity(activityBean, priority, resourceTypeName);
      }
    }

    @Override public void addNewEditorActivity(SyncBeanDef syncBeanDef, int priority, List resourceTypes) {
      if (proxyHelper != null) {
        final ActivityBeansCache proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addNewEditorActivity(syncBeanDef, priority, resourceTypes);
      } else {
        super.addNewEditorActivity(syncBeanDef, priority, resourceTypes);
      }
    }

    @Override public void addNewSplashScreenActivity(SyncBeanDef activityBean) {
      if (proxyHelper != null) {
        final ActivityBeansCache proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addNewSplashScreenActivity(activityBean);
      } else {
        super.addNewSplashScreenActivity(activityBean);
      }
    }

    @Override public boolean hasActivity(String id) {
      if (proxyHelper != null) {
        final ActivityBeansCache proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.hasActivity(id);
        return retVal;
      } else {
        return super.hasActivity(id);
      }
    }

    @Override public List getSplashScreens() {
      if (proxyHelper != null) {
        final ActivityBeansCache proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getSplashScreens();
        return retVal;
      } else {
        return super.getSplashScreens();
      }
    }

    @Override public SyncBeanDef getActivity(String id) {
      if (proxyHelper != null) {
        final ActivityBeansCache proxiedInstance = proxyHelper.getInstance(this);
        final SyncBeanDef retVal = proxiedInstance.getActivity(id);
        return retVal;
      } else {
        return super.getActivity(id);
      }
    }

    @Override public SyncBeanDef getActivity(Path path) {
      if (proxyHelper != null) {
        final ActivityBeansCache proxiedInstance = proxyHelper.getInstance(this);
        final SyncBeanDef retVal = proxiedInstance.getActivity(path);
        return retVal;
      } else {
        return super.getActivity(path);
      }
    }

    @Override public List getPerspectiveActivities() {
      if (proxyHelper != null) {
        final ActivityBeansCache proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getPerspectiveActivities();
        return retVal;
      } else {
        return super.getPerspectiveActivities();
      }
    }

    @Override public List getActivitiesById() {
      if (proxyHelper != null) {
        final ActivityBeansCache proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getActivitiesById();
        return retVal;
      } else {
        return super.getActivitiesById();
      }
    }

    @Override public void noOp() {
      if (proxyHelper != null) {
        final ActivityBeansCache proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.noOp();
      } else {
        super.noOp();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ActivityBeansCache proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_m_ActivityBeansCache__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ActivityBeansCache.class, "Type_factory__o_u_c_m_ActivityBeansCache__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ActivityBeansCache.class, Object.class });
  }

  public ActivityBeansCache createInstance(final ContextManager contextManager) {
    final Event<NewPerspectiveEvent> _newPerspectiveEventEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { NewPerspectiveEvent.class }, new Annotation[] { });
    final Event<NewWorkbenchScreenEvent> _newWorkbenchScreenEventEvent_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { NewWorkbenchScreenEvent.class }, new Annotation[] { });
    final SyncBeanManager _iocManager_0 = (SyncBeanManager) contextManager.getInstance("Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default");
    final GWTEditorNativeRegister _gwtEditorNativeRegister_4 = (GWTEditorNativeRegister) contextManager.getInstance("Type_factory__o_u_c_u_GWTEditorNativeRegister__quals__j_e_i_Any_j_e_i_Default");
    final ResourceTypeManagerCache _resourceTypeManagerCache_3 = (ResourceTypeManagerCache) contextManager.getInstance("Type_factory__o_u_c_m_ResourceTypeManagerCache__quals__j_e_i_Any_j_e_i_Default");
    final ActivityBeansCache instance = new ActivityBeansCache(_iocManager_0, _newPerspectiveEventEvent_1, _newWorkbenchScreenEventEvent_2, _resourceTypeManagerCache_3, _gwtEditorNativeRegister_4);
    registerDependentScopedReference(instance, _newPerspectiveEventEvent_1);
    registerDependentScopedReference(instance, _newWorkbenchScreenEventEvent_2);
    registerDependentScopedReference(instance, _iocManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ActivityBeansCache instance) {
    ActivityBeansCache_init(instance);
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ActivityBeansCache> proxyImpl = new Type_factory__o_u_c_m_ActivityBeansCache__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void ActivityBeansCache_init(ActivityBeansCache instance) /*-{
    instance.@org.uberfire.client.mvp.ActivityBeansCache::init()();
  }-*/;
}