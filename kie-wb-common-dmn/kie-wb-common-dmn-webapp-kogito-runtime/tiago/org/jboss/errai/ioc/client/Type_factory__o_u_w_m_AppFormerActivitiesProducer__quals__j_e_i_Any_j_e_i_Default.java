package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.workbench.model.AppFormerActivities;
import org.uberfire.workbench.model.AppFormerActivitiesProducer;
import org.uberfire.workbench.model.DefaultAppFormerActivities;

public class Type_factory__o_u_w_m_AppFormerActivitiesProducer__quals__j_e_i_Any_j_e_i_Default extends Factory<AppFormerActivitiesProducer> { private class Type_factory__o_u_w_m_AppFormerActivitiesProducer__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends AppFormerActivitiesProducer implements Proxy<AppFormerActivitiesProducer> {
    private final ProxyHelper<AppFormerActivitiesProducer> proxyHelper = new ProxyHelperImpl<AppFormerActivitiesProducer>("Type_factory__o_u_w_m_AppFormerActivitiesProducer__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final AppFormerActivitiesProducer instance) {

    }

    public AppFormerActivitiesProducer asBeanType() {
      return this;
    }

    public void setInstance(final AppFormerActivitiesProducer instance) {
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

    @Override public AppFormerActivities appFormerActivitiesProducer() {
      if (proxyHelper != null) {
        final AppFormerActivitiesProducer proxiedInstance = proxyHelper.getInstance(this);
        final AppFormerActivities retVal = proxiedInstance.appFormerActivitiesProducer();
        return retVal;
      } else {
        return super.appFormerActivitiesProducer();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final AppFormerActivitiesProducer proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_w_m_AppFormerActivitiesProducer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AppFormerActivitiesProducer.class, "Type_factory__o_u_w_m_AppFormerActivitiesProducer__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AppFormerActivitiesProducer.class, Object.class });
  }

  public AppFormerActivitiesProducer createInstance(final ContextManager contextManager) {
    final AppFormerActivitiesProducer instance = new AppFormerActivitiesProducer();
    setIncompleteInstance(instance);
    final Instance AppFormerActivitiesProducer_appFormerActivities = (Instance) contextManager.getContextualInstance("ContextualProvider_factory__j_e_i_Instance__quals__Universal", new Class[] { AppFormerActivities.class }, new Annotation[] { });
    registerDependentScopedReference(instance, AppFormerActivitiesProducer_appFormerActivities);
    AppFormerActivitiesProducer_Instance_appFormerActivities(instance, AppFormerActivitiesProducer_appFormerActivities);
    final DefaultAppFormerActivities AppFormerActivitiesProducer_defaultAppFormerActivities = (DefaultAppFormerActivities) contextManager.getInstance("Type_factory__o_u_w_m_DefaultAppFormerActivities__quals__j_e_i_Any_o_u_a_FallbackImplementation");
    AppFormerActivitiesProducer_AppFormerActivities_defaultAppFormerActivities(instance, AppFormerActivitiesProducer_defaultAppFormerActivities);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<AppFormerActivitiesProducer> proxyImpl = new Type_factory__o_u_w_m_AppFormerActivitiesProducer__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static AppFormerActivities AppFormerActivitiesProducer_AppFormerActivities_defaultAppFormerActivities(AppFormerActivitiesProducer instance) /*-{
    return instance.@org.uberfire.workbench.model.AppFormerActivitiesProducer::defaultAppFormerActivities;
  }-*/;

  native static void AppFormerActivitiesProducer_AppFormerActivities_defaultAppFormerActivities(AppFormerActivitiesProducer instance, AppFormerActivities value) /*-{
    instance.@org.uberfire.workbench.model.AppFormerActivitiesProducer::defaultAppFormerActivities = value;
  }-*/;

  native static Instance AppFormerActivitiesProducer_Instance_appFormerActivities(AppFormerActivitiesProducer instance) /*-{
    return instance.@org.uberfire.workbench.model.AppFormerActivitiesProducer::appFormerActivities;
  }-*/;

  native static void AppFormerActivitiesProducer_Instance_appFormerActivities(AppFormerActivitiesProducer instance, Instance<AppFormerActivities> value) /*-{
    instance.@org.uberfire.workbench.model.AppFormerActivitiesProducer::appFormerActivities = value;
  }-*/;
}