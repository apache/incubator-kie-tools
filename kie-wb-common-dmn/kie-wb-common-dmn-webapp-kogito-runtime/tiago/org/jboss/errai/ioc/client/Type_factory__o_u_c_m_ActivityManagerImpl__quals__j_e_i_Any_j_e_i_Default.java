package org.jboss.errai.ioc.client;

import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.ActivityLifecycleErrorHandler;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.ActivityManagerImpl;
import org.uberfire.client.mvp.SplashScreenActivity;
import org.uberfire.experimental.client.service.auth.ExperimentalActivitiesAuthorizationManagerImpl;
import org.uberfire.experimental.service.auth.ExperimentalActivitiesAuthorizationManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.impl.authz.DefaultAuthorizationManager;

public class Type_factory__o_u_c_m_ActivityManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ActivityManagerImpl> { private class Type_factory__o_u_c_m_ActivityManagerImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ActivityManagerImpl implements Proxy<ActivityManagerImpl> {
    private final ProxyHelper<ActivityManagerImpl> proxyHelper = new ProxyHelperImpl<ActivityManagerImpl>("Type_factory__o_u_c_m_ActivityManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ActivityManagerImpl instance) {

    }

    public ActivityManagerImpl asBeanType() {
      return this;
    }

    public void setInstance(final ActivityManagerImpl instance) {
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

    @Override public Set getActivities(Class clazz) {
      if (proxyHelper != null) {
        final ActivityManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final Set retVal = proxiedInstance.getActivities(clazz);
        return retVal;
      } else {
        return super.getActivities(clazz);
      }
    }

    @Override public Set getActivities(PlaceRequest placeRequest) {
      if (proxyHelper != null) {
        final ActivityManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final Set retVal = proxiedInstance.getActivities(placeRequest);
        return retVal;
      } else {
        return super.getActivities(placeRequest);
      }
    }

    @Override public Set getActivities(PlaceRequest placeRequest, boolean secure) {
      if (proxyHelper != null) {
        final ActivityManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final Set retVal = proxiedInstance.getActivities(placeRequest, secure);
        return retVal;
      } else {
        return super.getActivities(placeRequest, secure);
      }
    }

    @Override public SplashScreenActivity getSplashScreenInterceptor(PlaceRequest placeRequest) {
      if (proxyHelper != null) {
        final ActivityManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final SplashScreenActivity retVal = proxiedInstance.getSplashScreenInterceptor(placeRequest);
        return retVal;
      } else {
        return super.getSplashScreenInterceptor(placeRequest);
      }
    }

    @Override public boolean containsActivity(PlaceRequest placeRequest) {
      if (proxyHelper != null) {
        final ActivityManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.containsActivity(placeRequest);
        return retVal;
      } else {
        return super.containsActivity(placeRequest);
      }
    }

    @Override public Activity getActivity(PlaceRequest placeRequest) {
      if (proxyHelper != null) {
        final ActivityManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final Activity retVal = proxiedInstance.getActivity(placeRequest);
        return retVal;
      } else {
        return super.getActivity(placeRequest);
      }
    }

    @Override public Activity getActivity(PlaceRequest placeRequest, boolean secure) {
      if (proxyHelper != null) {
        final ActivityManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final Activity retVal = proxiedInstance.getActivity(placeRequest, secure);
        return retVal;
      } else {
        return super.getActivity(placeRequest, secure);
      }
    }

    @Override public Activity getActivity(Class clazz, PlaceRequest placeRequest) {
      if (proxyHelper != null) {
        final ActivityManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final Activity retVal = proxiedInstance.getActivity(clazz, placeRequest);
        return retVal;
      } else {
        return super.getActivity(clazz, placeRequest);
      }
    }

    @Override public Activity getActivity(Class clazz, PlaceRequest placeRequest, boolean secure) {
      if (proxyHelper != null) {
        final ActivityManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final Activity retVal = proxiedInstance.getActivity(clazz, placeRequest, secure);
        return retVal;
      } else {
        return super.getActivity(clazz, placeRequest, secure);
      }
    }

    @Override public void destroyActivity(Activity activity) {
      if (proxyHelper != null) {
        final ActivityManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.destroyActivity(activity);
      } else {
        super.destroyActivity(activity);
      }
    }

    @Override public boolean isStarted(Activity activity) {
      if (proxyHelper != null) {
        final ActivityManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isStarted(activity);
        return retVal;
      } else {
        return super.isStarted(activity);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ActivityManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_m_ActivityManagerImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ActivityManagerImpl.class, "Type_factory__o_u_c_m_ActivityManagerImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ActivityManagerImpl.class, Object.class, ActivityManager.class });
  }

  public ActivityManagerImpl createInstance(final ContextManager contextManager) {
    final ActivityManagerImpl instance = new ActivityManagerImpl();
    setIncompleteInstance(instance);
    final ExperimentalActivitiesAuthorizationManagerImpl ActivityManagerImpl_activitiesAuthorizationManager = (ExperimentalActivitiesAuthorizationManagerImpl) contextManager.getInstance("Type_factory__o_u_e_c_s_a_ExperimentalActivitiesAuthorizationManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    ActivityManagerImpl_ExperimentalActivitiesAuthorizationManager_activitiesAuthorizationManager(instance, ActivityManagerImpl_activitiesAuthorizationManager);
    final SyncBeanManager ActivityManagerImpl_iocManager = (SyncBeanManager) contextManager.getInstance("Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ActivityManagerImpl_iocManager);
    ActivityManagerImpl_SyncBeanManager_iocManager(instance, ActivityManagerImpl_iocManager);
    final DefaultAuthorizationManager ActivityManagerImpl_authzManager = (DefaultAuthorizationManager) contextManager.getInstance("Type_factory__o_u_s_i_a_DefaultAuthorizationManager__quals__j_e_i_Any_j_e_i_Default");
    ActivityManagerImpl_AuthorizationManager_authzManager(instance, ActivityManagerImpl_authzManager);
    final ActivityBeansCache ActivityManagerImpl_activityBeansCache = (ActivityBeansCache) contextManager.getInstance("Type_factory__o_u_c_m_ActivityBeansCache__quals__j_e_i_Any_j_e_i_Default");
    ActivityManagerImpl_ActivityBeansCache_activityBeansCache(instance, ActivityManagerImpl_activityBeansCache);
    final User ActivityManagerImpl_identity = (User) contextManager.getInstance("Producer_factory__o_j_e_s_s_a_i_User__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ActivityManagerImpl_identity);
    ActivityManagerImpl_User_identity(instance, ActivityManagerImpl_identity);
    final ActivityLifecycleErrorHandler ActivityManagerImpl_lifecycleErrorHandler = (ActivityLifecycleErrorHandler) contextManager.getInstance("Type_factory__o_u_c_m_ActivityLifecycleErrorHandler__quals__j_e_i_Any_j_e_i_Default");
    ActivityManagerImpl_ActivityLifecycleErrorHandler_lifecycleErrorHandler(instance, ActivityManagerImpl_lifecycleErrorHandler);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ActivityManagerImpl> proxyImpl = new Type_factory__o_u_c_m_ActivityManagerImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static AuthorizationManager ActivityManagerImpl_AuthorizationManager_authzManager(ActivityManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.ActivityManagerImpl::authzManager;
  }-*/;

  native static void ActivityManagerImpl_AuthorizationManager_authzManager(ActivityManagerImpl instance, AuthorizationManager value) /*-{
    instance.@org.uberfire.client.mvp.ActivityManagerImpl::authzManager = value;
  }-*/;

  native static ExperimentalActivitiesAuthorizationManager ActivityManagerImpl_ExperimentalActivitiesAuthorizationManager_activitiesAuthorizationManager(ActivityManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.ActivityManagerImpl::activitiesAuthorizationManager;
  }-*/;

  native static void ActivityManagerImpl_ExperimentalActivitiesAuthorizationManager_activitiesAuthorizationManager(ActivityManagerImpl instance, ExperimentalActivitiesAuthorizationManager value) /*-{
    instance.@org.uberfire.client.mvp.ActivityManagerImpl::activitiesAuthorizationManager = value;
  }-*/;

  native static ActivityLifecycleErrorHandler ActivityManagerImpl_ActivityLifecycleErrorHandler_lifecycleErrorHandler(ActivityManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.ActivityManagerImpl::lifecycleErrorHandler;
  }-*/;

  native static void ActivityManagerImpl_ActivityLifecycleErrorHandler_lifecycleErrorHandler(ActivityManagerImpl instance, ActivityLifecycleErrorHandler value) /*-{
    instance.@org.uberfire.client.mvp.ActivityManagerImpl::lifecycleErrorHandler = value;
  }-*/;

  native static SyncBeanManager ActivityManagerImpl_SyncBeanManager_iocManager(ActivityManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.ActivityManagerImpl::iocManager;
  }-*/;

  native static void ActivityManagerImpl_SyncBeanManager_iocManager(ActivityManagerImpl instance, SyncBeanManager value) /*-{
    instance.@org.uberfire.client.mvp.ActivityManagerImpl::iocManager = value;
  }-*/;

  native static ActivityBeansCache ActivityManagerImpl_ActivityBeansCache_activityBeansCache(ActivityManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.ActivityManagerImpl::activityBeansCache;
  }-*/;

  native static void ActivityManagerImpl_ActivityBeansCache_activityBeansCache(ActivityManagerImpl instance, ActivityBeansCache value) /*-{
    instance.@org.uberfire.client.mvp.ActivityManagerImpl::activityBeansCache = value;
  }-*/;

  native static User ActivityManagerImpl_User_identity(ActivityManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.ActivityManagerImpl::identity;
  }-*/;

  native static void ActivityManagerImpl_User_identity(ActivityManagerImpl instance, User value) /*-{
    instance.@org.uberfire.client.mvp.ActivityManagerImpl::identity = value;
  }-*/;
}