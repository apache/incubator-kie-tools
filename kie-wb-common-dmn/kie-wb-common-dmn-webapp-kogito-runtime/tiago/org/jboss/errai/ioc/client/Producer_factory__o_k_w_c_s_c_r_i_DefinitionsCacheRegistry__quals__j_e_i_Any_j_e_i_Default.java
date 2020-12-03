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
import org.kie.workbench.common.stunner.core.client.registry.impl.ClientDefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.registry.DynamicRegistry;
import org.kie.workbench.common.stunner.core.registry.Registry;
import org.kie.workbench.common.stunner.core.registry.definition.DefinitionRegistry;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;

public class Producer_factory__o_k_w_c_s_c_r_i_DefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_Default extends Factory<DefinitionsCacheRegistry> { private class Producer_factory__o_k_w_c_s_c_r_i_DefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_DefaultProxyImpl implements Proxy<DefinitionsCacheRegistry>, DefinitionsCacheRegistry {
    private final ProxyHelper<DefinitionsCacheRegistry> proxyHelper = new ProxyHelperImpl<DefinitionsCacheRegistry>("Producer_factory__o_k_w_c_s_c_r_i_DefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DefinitionsCacheRegistry instance) {

    }

    public DefinitionsCacheRegistry asBeanType() {
      return this;
    }

    public void setInstance(final DefinitionsCacheRegistry instance) {
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

    @Override public Set getLabels(String id) {
      if (proxyHelper != null) {
        final DefinitionsCacheRegistry proxiedInstance = proxyHelper.getInstance(this);
        final Set retVal = proxiedInstance.getLabels(id);
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public Object getDefinitionById(String id) {
      if (proxyHelper != null) {
        final DefinitionsCacheRegistry proxiedInstance = proxyHelper.getInstance(this);
        final Object retVal = proxiedInstance.getDefinitionById(id);
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public void clear() {
      if (proxyHelper != null) {
        final DefinitionsCacheRegistry proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.clear();
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public void register(Object item) {
      if (proxyHelper != null) {
        final DefinitionsCacheRegistry proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.register(item);
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public boolean remove(Object item) {
      if (proxyHelper != null) {
        final DefinitionsCacheRegistry proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.remove(item);
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public boolean contains(Object item) {
      if (proxyHelper != null) {
        final DefinitionsCacheRegistry proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.contains(item);
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public boolean isEmpty() {
      if (proxyHelper != null) {
        final DefinitionsCacheRegistry proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isEmpty();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DefinitionsCacheRegistry proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }
  }
  public Producer_factory__o_k_w_c_s_c_r_i_DefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefinitionsCacheRegistry.class, "Producer_factory__o_k_w_c_s_c_r_i_DefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefinitionsCacheRegistry.class, DefinitionRegistry.class, DynamicRegistry.class, Registry.class });
  }

  public DefinitionsCacheRegistry createInstance(final ContextManager contextManager) {
    ClientDefinitionsCacheRegistry producerInstance = contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_r_i_ClientDefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_Default");
    producerInstance = Factory.maybeUnwrapProxy(producerInstance);
    final DefinitionsCacheRegistry instance = producerInstance.getRegistry();
    thisInstance.setReference(instance, "producerInstance", producerInstance);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DefinitionsCacheRegistry> proxyImpl = new Producer_factory__o_k_w_c_s_c_r_i_DefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}