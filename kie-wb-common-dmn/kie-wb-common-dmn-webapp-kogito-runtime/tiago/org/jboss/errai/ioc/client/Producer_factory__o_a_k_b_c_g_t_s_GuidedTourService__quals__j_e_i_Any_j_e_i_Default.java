package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.appformer.kogito.bridge.client.guided.tour.service.GuidedTourService;
import org.appformer.kogito.bridge.client.guided.tour.service.GuidedTourServiceProducer;
import org.appformer.kogito.bridge.client.guided.tour.service.api.Tutorial;
import org.appformer.kogito.bridge.client.guided.tour.service.api.UserInteraction;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;

public class Producer_factory__o_a_k_b_c_g_t_s_GuidedTourService__quals__j_e_i_Any_j_e_i_Default extends Factory<GuidedTourService> { private class Producer_factory__o_a_k_b_c_g_t_s_GuidedTourService__quals__j_e_i_Any_j_e_i_DefaultProxyImpl implements Proxy<GuidedTourService>, GuidedTourService {
    private final ProxyHelper<GuidedTourService> proxyHelper = new ProxyHelperImpl<GuidedTourService>("Producer_factory__o_a_k_b_c_g_t_s_GuidedTourService__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final GuidedTourService instance) {

    }

    public GuidedTourService asBeanType() {
      return this;
    }

    public void setInstance(final GuidedTourService instance) {
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

    @Override public void refresh(UserInteraction userInteraction) {
      if (proxyHelper != null) {
        final GuidedTourService proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.refresh(userInteraction);
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public void registerTutorial(Tutorial tutorial) {
      if (proxyHelper != null) {
        final GuidedTourService proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.registerTutorial(tutorial);
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public boolean isEnabled() {
      if (proxyHelper != null) {
        final GuidedTourService proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isEnabled();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final GuidedTourService proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }
  }
  public Producer_factory__o_a_k_b_c_g_t_s_GuidedTourService__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GuidedTourService.class, "Producer_factory__o_a_k_b_c_g_t_s_GuidedTourService__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GuidedTourService.class });
  }

  public GuidedTourService createInstance(final ContextManager contextManager) {
    GuidedTourServiceProducer producerInstance = contextManager.getInstance("Type_factory__o_a_k_b_c_g_t_s_GuidedTourServiceProducer__quals__j_e_i_Any_j_e_i_Default");
    producerInstance = Factory.maybeUnwrapProxy(producerInstance);
    final GuidedTourService instance = producerInstance.produce();
    thisInstance.setReference(instance, "producerInstance", producerInstance);
    registerDependentScopedReference(instance, producerInstance);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<GuidedTourService> proxyImpl = new Producer_factory__o_a_k_b_c_g_t_s_GuidedTourService__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}