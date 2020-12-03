package org.jboss.errai.ioc.client;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.Event.Type;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.client.mvp.PlaceManagerImpl;

public class Producer_factory__c_g_w_b_e_s_EventBus__quals__j_e_i_Any_j_e_i_Default extends Factory<EventBus> { private class Producer_factory__c_g_w_b_e_s_EventBus__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends EventBus implements Proxy<EventBus> {
    private final ProxyHelper<EventBus> proxyHelper = new ProxyHelperImpl<EventBus>("Producer_factory__c_g_w_b_e_s_EventBus__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final EventBus instance) {

    }

    public EventBus asBeanType() {
      return this;
    }

    public void setInstance(final EventBus instance) {
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

    @Override public HandlerRegistration addHandler(Type type, Object handler) {
      if (proxyHelper != null) {
        final EventBus proxiedInstance = proxyHelper.getInstance(this);
        final HandlerRegistration retVal = proxiedInstance.addHandler(type, handler);
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public HandlerRegistration addHandlerToSource(Type type, Object source, Object handler) {
      if (proxyHelper != null) {
        final EventBus proxiedInstance = proxyHelper.getInstance(this);
        final HandlerRegistration retVal = proxiedInstance.addHandlerToSource(type, source, handler);
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public void fireEvent(Event event) {
      if (proxyHelper != null) {
        final EventBus proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.fireEvent(event);
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public void fireEventFromSource(Event event, Object source) {
      if (proxyHelper != null) {
        final EventBus proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.fireEventFromSource(event, source);
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final EventBus proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Producer_factory__c_g_w_b_e_s_EventBus__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(EventBus.class, "Producer_factory__c_g_w_b_e_s_EventBus__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { EventBus.class, Object.class });
  }

  public EventBus createInstance(final ContextManager contextManager) {
    PlaceManagerImpl producerInstance = contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    producerInstance = Factory.maybeUnwrapProxy(producerInstance);
    final EventBus instance = PlaceManagerImpl_produceEventBus(producerInstance);
    thisInstance.setReference(instance, "producerInstance", producerInstance);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<EventBus> proxyImpl = new Producer_factory__c_g_w_b_e_s_EventBus__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static EventBus PlaceManagerImpl_produceEventBus(PlaceManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.PlaceManagerImpl::produceEventBus()();
  }-*/;
}