package org.jboss.errai.ioc.client;

import java.util.Collection;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.ActivityBeansInfo;

public class Type_factory__o_u_c_m_ActivityBeansInfo__quals__j_e_i_Any_j_e_i_Default extends Factory<ActivityBeansInfo> { private class Type_factory__o_u_c_m_ActivityBeansInfo__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ActivityBeansInfo implements Proxy<ActivityBeansInfo> {
    private final ProxyHelper<ActivityBeansInfo> proxyHelper = new ProxyHelperImpl<ActivityBeansInfo>("Type_factory__o_u_c_m_ActivityBeansInfo__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_u_c_m_ActivityBeansInfo__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null);
    }

    public void initProxyProperties(final ActivityBeansInfo instance) {

    }

    public ActivityBeansInfo asBeanType() {
      return this;
    }

    public void setInstance(final ActivityBeansInfo instance) {
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

    @Override public List getAvailableWorkbenchScreensIds() {
      if (proxyHelper != null) {
        final ActivityBeansInfo proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getAvailableWorkbenchScreensIds();
        return retVal;
      } else {
        return super.getAvailableWorkbenchScreensIds();
      }
    }

    @Override public List getAvailablePerspectivesIds() {
      if (proxyHelper != null) {
        final ActivityBeansInfo proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getAvailablePerspectivesIds();
        return retVal;
      } else {
        return super.getAvailablePerspectivesIds();
      }
    }

    @Override public List getAvailableSplashScreensIds() {
      if (proxyHelper != null) {
        final ActivityBeansInfo proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getAvailableSplashScreensIds();
        return retVal;
      } else {
        return super.getAvailableSplashScreensIds();
      }
    }

    @Override public List getAvailableWorkbenchEditorsIds() {
      if (proxyHelper != null) {
        final ActivityBeansInfo proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getAvailableWorkbenchEditorsIds();
        return retVal;
      } else {
        return super.getAvailableWorkbenchEditorsIds();
      }
    }

    @Override public void addActivityBean(List activityBeans, String newBean) {
      if (proxyHelper != null) {
        final ActivityBeansInfo proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addActivityBean(activityBeans, newBean);
      } else {
        super.addActivityBean(activityBeans, newBean);
      }
    }

    @Override public Collection lookupBeans(Class activityClass) {
      if (proxyHelper != null) {
        final ActivityBeansInfo proxiedInstance = proxyHelper.getInstance(this);
        final Collection retVal = proxiedInstance.lookupBeans(activityClass);
        return retVal;
      } else {
        return super.lookupBeans(activityClass);
      }
    }

    @Override public String getId(IOCBeanDef beanDef) {
      if (proxyHelper != null) {
        final ActivityBeansInfo proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getId(beanDef);
        return retVal;
      } else {
        return super.getId(beanDef);
      }
    }

    @Override public SyncBeanManager getBeanManager() {
      if (proxyHelper != null) {
        final ActivityBeansInfo proxiedInstance = proxyHelper.getInstance(this);
        final SyncBeanManager retVal = proxiedInstance.getBeanManager();
        return retVal;
      } else {
        return super.getBeanManager();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ActivityBeansInfo proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_m_ActivityBeansInfo__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ActivityBeansInfo.class, "Type_factory__o_u_c_m_ActivityBeansInfo__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ActivityBeansInfo.class, Object.class });
  }

  public ActivityBeansInfo createInstance(final ContextManager contextManager) {
    final SyncBeanManager _beanManager_0 = (SyncBeanManager) contextManager.getInstance("Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default");
    final ActivityBeansCache _activityBeansCache_1 = (ActivityBeansCache) contextManager.getInstance("Type_factory__o_u_c_m_ActivityBeansCache__quals__j_e_i_Any_j_e_i_Default");
    final ActivityBeansInfo instance = new ActivityBeansInfo(_beanManager_0, _activityBeansCache_1);
    registerDependentScopedReference(instance, _beanManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_u_c_m_ActivityBeansInfo__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.uberfire.client.mvp.ActivityBeansInfo an exception was thrown from this constructor: @javax.inject.Inject()  public org.uberfire.client.mvp.ActivityBeansInfo ([org.jboss.errai.ioc.client.container.SyncBeanManager, org.uberfire.client.mvp.ActivityBeansCache])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ActivityBeansInfo> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}