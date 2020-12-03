package org.jboss.errai.ioc.client;

import elemental2.promise.Promise;
import javax.enterprise.context.ApplicationScoped;
import org.appformer.kogito.bridge.client.resource.ResourceContentService;
import org.appformer.kogito.bridge.client.resource.interop.ResourceContentOptions;
import org.appformer.kogito.bridge.client.resource.interop.ResourceListOptions;
import org.appformer.kogito.bridge.client.resource.producer.ResourceContentServiceProducer;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;

public class Producer_factory__o_a_k_b_c_r_ResourceContentService__quals__j_e_i_Any_j_e_i_Default extends Factory<ResourceContentService> { private class Producer_factory__o_a_k_b_c_r_ResourceContentService__quals__j_e_i_Any_j_e_i_DefaultProxyImpl implements Proxy<ResourceContentService>, ResourceContentService {
    private final ProxyHelper<ResourceContentService> proxyHelper = new ProxyHelperImpl<ResourceContentService>("Producer_factory__o_a_k_b_c_r_ResourceContentService__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ResourceContentService instance) {

    }

    public ResourceContentService asBeanType() {
      return this;
    }

    public void setInstance(final ResourceContentService instance) {
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

    @Override public Promise get(String uri) {
      if (proxyHelper != null) {
        final ResourceContentService proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.get(uri);
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public Promise get(String uri, ResourceContentOptions options) {
      if (proxyHelper != null) {
        final ResourceContentService proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.get(uri, options);
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public Promise list(String pattern) {
      if (proxyHelper != null) {
        final ResourceContentService proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.list(pattern);
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public Promise list(String pattern, ResourceListOptions options) {
      if (proxyHelper != null) {
        final ResourceContentService proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.list(pattern, options);
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ResourceContentService proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }
  }
  public Producer_factory__o_a_k_b_c_r_ResourceContentService__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ResourceContentService.class, "Producer_factory__o_a_k_b_c_r_ResourceContentService__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ResourceContentService.class });
  }

  public ResourceContentService createInstance(final ContextManager contextManager) {
    ResourceContentServiceProducer producerInstance = contextManager.getInstance("Type_factory__o_a_k_b_c_r_p_ResourceContentServiceProducer__quals__j_e_i_Any_j_e_i_Default");
    producerInstance = Factory.maybeUnwrapProxy(producerInstance);
    final ResourceContentService instance = producerInstance.produce();
    thisInstance.setReference(instance, "producerInstance", producerInstance);
    registerDependentScopedReference(instance, producerInstance);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ResourceContentService> proxyImpl = new Producer_factory__o_a_k_b_c_r_ResourceContentService__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}