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
import org.slf4j.Logger;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityLifecycleError;
import org.uberfire.client.mvp.ActivityLifecycleError.LifecyclePhase;
import org.uberfire.client.mvp.ActivityLifecycleErrorHandler;
import org.uberfire.client.workbench.widgets.notifications.NotificationManager;

public class Type_factory__o_u_c_m_ActivityLifecycleErrorHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<ActivityLifecycleErrorHandler> { private class Type_factory__o_u_c_m_ActivityLifecycleErrorHandler__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ActivityLifecycleErrorHandler implements Proxy<ActivityLifecycleErrorHandler> {
    private final ProxyHelper<ActivityLifecycleErrorHandler> proxyHelper = new ProxyHelperImpl<ActivityLifecycleErrorHandler>("Type_factory__o_u_c_m_ActivityLifecycleErrorHandler__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ActivityLifecycleErrorHandler instance) {

    }

    public ActivityLifecycleErrorHandler asBeanType() {
      return this;
    }

    public void setInstance(final ActivityLifecycleErrorHandler instance) {
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

    @Override public void handle(Activity failedActivity, LifecyclePhase failedCall, Throwable exception) {
      if (proxyHelper != null) {
        final ActivityLifecycleErrorHandler proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.handle(failedActivity, failedCall, exception);
      } else {
        super.handle(failedActivity, failedCall, exception);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ActivityLifecycleErrorHandler proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_m_ActivityLifecycleErrorHandler__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ActivityLifecycleErrorHandler.class, "Type_factory__o_u_c_m_ActivityLifecycleErrorHandler__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ActivityLifecycleErrorHandler.class, Object.class });
  }

  public ActivityLifecycleErrorHandler createInstance(final ContextManager contextManager) {
    final ActivityLifecycleErrorHandler instance = new ActivityLifecycleErrorHandler();
    setIncompleteInstance(instance);
    final Logger ActivityLifecycleErrorHandler_logger = (Logger) contextManager.getInstance("ExtensionProvided_factory__o_s_Logger__quals__Universal_2");
    registerDependentScopedReference(instance, ActivityLifecycleErrorHandler_logger);
    ActivityLifecycleErrorHandler_Logger_logger(instance, ActivityLifecycleErrorHandler_logger);
    final NotificationManager ActivityLifecycleErrorHandler_notificationManager = (NotificationManager) contextManager.getInstance("Type_factory__o_u_c_w_w_n_NotificationManager__quals__j_e_i_Any_j_e_i_Default");
    ActivityLifecycleErrorHandler_NotificationManager_notificationManager(instance, ActivityLifecycleErrorHandler_notificationManager);
    final Event ActivityLifecycleErrorHandler_lifecycleErrorEvent = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ActivityLifecycleError.class }, new Annotation[] { });
    registerDependentScopedReference(instance, ActivityLifecycleErrorHandler_lifecycleErrorEvent);
    ActivityLifecycleErrorHandler_Event_lifecycleErrorEvent(instance, ActivityLifecycleErrorHandler_lifecycleErrorEvent);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ActivityLifecycleErrorHandler> proxyImpl = new Type_factory__o_u_c_m_ActivityLifecycleErrorHandler__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static Logger ActivityLifecycleErrorHandler_Logger_logger(ActivityLifecycleErrorHandler instance) /*-{
    return instance.@org.uberfire.client.mvp.ActivityLifecycleErrorHandler::logger;
  }-*/;

  native static void ActivityLifecycleErrorHandler_Logger_logger(ActivityLifecycleErrorHandler instance, Logger value) /*-{
    instance.@org.uberfire.client.mvp.ActivityLifecycleErrorHandler::logger = value;
  }-*/;

  native static NotificationManager ActivityLifecycleErrorHandler_NotificationManager_notificationManager(ActivityLifecycleErrorHandler instance) /*-{
    return instance.@org.uberfire.client.mvp.ActivityLifecycleErrorHandler::notificationManager;
  }-*/;

  native static void ActivityLifecycleErrorHandler_NotificationManager_notificationManager(ActivityLifecycleErrorHandler instance, NotificationManager value) /*-{
    instance.@org.uberfire.client.mvp.ActivityLifecycleErrorHandler::notificationManager = value;
  }-*/;

  native static Event ActivityLifecycleErrorHandler_Event_lifecycleErrorEvent(ActivityLifecycleErrorHandler instance) /*-{
    return instance.@org.uberfire.client.mvp.ActivityLifecycleErrorHandler::lifecycleErrorEvent;
  }-*/;

  native static void ActivityLifecycleErrorHandler_Event_lifecycleErrorEvent(ActivityLifecycleErrorHandler instance, Event<ActivityLifecycleError> value) /*-{
    instance.@org.uberfire.client.mvp.ActivityLifecycleErrorHandler::lifecycleErrorEvent = value;
  }-*/;
}