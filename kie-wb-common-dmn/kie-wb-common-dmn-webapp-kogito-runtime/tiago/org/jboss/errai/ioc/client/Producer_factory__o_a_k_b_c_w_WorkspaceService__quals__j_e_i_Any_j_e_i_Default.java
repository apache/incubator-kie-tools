package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.appformer.kogito.bridge.client.workspace.WorkspaceService;
import org.appformer.kogito.bridge.client.workspace.producer.WorkspaceServiceProducer;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;

public class Producer_factory__o_a_k_b_c_w_WorkspaceService__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkspaceService> { private class Producer_factory__o_a_k_b_c_w_WorkspaceService__quals__j_e_i_Any_j_e_i_DefaultProxyImpl implements Proxy<WorkspaceService>, WorkspaceService {
    private final ProxyHelper<WorkspaceService> proxyHelper = new ProxyHelperImpl<WorkspaceService>("Producer_factory__o_a_k_b_c_w_WorkspaceService__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final WorkspaceService instance) {

    }

    public WorkspaceService asBeanType() {
      return this;
    }

    public void setInstance(final WorkspaceService instance) {
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

    @Override public void openFile(String path) {
      if (proxyHelper != null) {
        final WorkspaceService proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.openFile(path);
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final WorkspaceService proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }
  }
  public Producer_factory__o_a_k_b_c_w_WorkspaceService__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WorkspaceService.class, "Producer_factory__o_a_k_b_c_w_WorkspaceService__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WorkspaceService.class });
  }

  public WorkspaceService createInstance(final ContextManager contextManager) {
    WorkspaceServiceProducer producerInstance = contextManager.getInstance("Type_factory__o_a_k_b_c_w_p_WorkspaceServiceProducer__quals__j_e_i_Any_j_e_i_Default");
    producerInstance = Factory.maybeUnwrapProxy(producerInstance);
    final WorkspaceService instance = producerInstance.produce();
    thisInstance.setReference(instance, "producerInstance", producerInstance);
    registerDependentScopedReference(instance, producerInstance);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<WorkspaceService> proxyImpl = new Producer_factory__o_a_k_b_c_w_WorkspaceService__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}