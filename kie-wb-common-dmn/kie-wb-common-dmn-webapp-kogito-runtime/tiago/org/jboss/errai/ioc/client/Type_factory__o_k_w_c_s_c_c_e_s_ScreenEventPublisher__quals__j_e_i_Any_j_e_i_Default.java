package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenEventPublisher;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenMaximizedEvent;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenMinimizedEvent;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.workbench.events.PlaceMaximizedEvent;
import org.uberfire.client.workbench.events.PlaceMinimizedEvent;

public class Type_factory__o_k_w_c_s_c_c_e_s_ScreenEventPublisher__quals__j_e_i_Any_j_e_i_Default extends Factory<ScreenEventPublisher> { private class Type_factory__o_k_w_c_s_c_c_e_s_ScreenEventPublisher__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ScreenEventPublisher implements Proxy<ScreenEventPublisher> {
    private final ProxyHelper<ScreenEventPublisher> proxyHelper = new ProxyHelperImpl<ScreenEventPublisher>("Type_factory__o_k_w_c_s_c_c_e_s_ScreenEventPublisher__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_s_c_c_e_s_ScreenEventPublisher__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null, null);
    }

    public void initProxyProperties(final ScreenEventPublisher instance) {

    }

    public ScreenEventPublisher asBeanType() {
      return this;
    }

    public void setInstance(final ScreenEventPublisher instance) {
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

    @Override protected void onPlaceMaximizedEvent(PlaceMaximizedEvent event) {
      if (proxyHelper != null) {
        final ScreenEventPublisher proxiedInstance = proxyHelper.getInstance(this);
        ScreenEventPublisher_onPlaceMaximizedEvent_PlaceMaximizedEvent(proxiedInstance, event);
      } else {
        super.onPlaceMaximizedEvent(event);
      }
    }

    @Override protected void onPlaceMinimizedEvent(PlaceMinimizedEvent event) {
      if (proxyHelper != null) {
        final ScreenEventPublisher proxiedInstance = proxyHelper.getInstance(this);
        ScreenEventPublisher_onPlaceMinimizedEvent_PlaceMinimizedEvent(proxiedInstance, event);
      } else {
        super.onPlaceMinimizedEvent(event);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ScreenEventPublisher proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_e_s_ScreenEventPublisher__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ScreenEventPublisher.class, "Type_factory__o_k_w_c_s_c_c_e_s_ScreenEventPublisher__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ScreenEventPublisher.class, Object.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.uberfire.client.workbench.events.PlaceMaximizedEvent", new AbstractCDIEventCallback<PlaceMaximizedEvent>() {
      public void fireEvent(final PlaceMaximizedEvent event) {
        final ScreenEventPublisher instance = Factory.maybeUnwrapProxy((ScreenEventPublisher) context.getInstance("Type_factory__o_k_w_c_s_c_c_e_s_ScreenEventPublisher__quals__j_e_i_Any_j_e_i_Default"));
        ScreenEventPublisher_onPlaceMaximizedEvent_PlaceMaximizedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.events.PlaceMaximizedEvent []";
      }
    });
    CDI.subscribeLocal("org.uberfire.client.workbench.events.PlaceMinimizedEvent", new AbstractCDIEventCallback<PlaceMinimizedEvent>() {
      public void fireEvent(final PlaceMinimizedEvent event) {
        final ScreenEventPublisher instance = Factory.maybeUnwrapProxy((ScreenEventPublisher) context.getInstance("Type_factory__o_k_w_c_s_c_c_e_s_ScreenEventPublisher__quals__j_e_i_Any_j_e_i_Default"));
        ScreenEventPublisher_onPlaceMinimizedEvent_PlaceMinimizedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.events.PlaceMinimizedEvent []";
      }
    });
  }

  public ScreenEventPublisher createInstance(final ContextManager contextManager) {
    final Event<ScreenMinimizedEvent> _diagramEditorMinimizedEventEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ScreenMinimizedEvent.class }, new Annotation[] { });
    final ActivityBeansCache _activityBeansCache_2 = (ActivityBeansCache) contextManager.getInstance("Type_factory__o_u_c_m_ActivityBeansCache__quals__j_e_i_Any_j_e_i_Default");
    final Event<ScreenMaximizedEvent> _diagramEditorMaximizedEventEvent_0 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ScreenMaximizedEvent.class }, new Annotation[] { });
    final ScreenEventPublisher instance = new ScreenEventPublisher(_diagramEditorMaximizedEventEvent_0, _diagramEditorMinimizedEventEvent_1, _activityBeansCache_2);
    registerDependentScopedReference(instance, _diagramEditorMinimizedEventEvent_1);
    registerDependentScopedReference(instance, _diagramEditorMaximizedEventEvent_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_s_c_c_e_s_ScreenEventPublisher__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.stunner.core.client.event.screen.ScreenEventPublisher an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.stunner.core.client.event.screen.ScreenEventPublisher ([javax.enterprise.event.Event, javax.enterprise.event.Event, org.uberfire.client.mvp.ActivityBeansCache])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ScreenEventPublisher> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void ScreenEventPublisher_onPlaceMaximizedEvent_PlaceMaximizedEvent(ScreenEventPublisher instance, PlaceMaximizedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.event.screen.ScreenEventPublisher::onPlaceMaximizedEvent(Lorg/uberfire/client/workbench/events/PlaceMaximizedEvent;)(a0);
  }-*/;

  public native static void ScreenEventPublisher_onPlaceMinimizedEvent_PlaceMinimizedEvent(ScreenEventPublisher instance, PlaceMinimizedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.event.screen.ScreenEventPublisher::onPlaceMinimizedEvent(Lorg/uberfire/client/workbench/events/PlaceMinimizedEvent;)(a0);
  }-*/;
}