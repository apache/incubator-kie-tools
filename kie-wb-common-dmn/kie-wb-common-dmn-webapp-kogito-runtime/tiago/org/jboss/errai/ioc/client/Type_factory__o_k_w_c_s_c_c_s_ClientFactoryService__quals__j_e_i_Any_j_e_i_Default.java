package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.service.FactoryService;

public class Type_factory__o_k_w_c_s_c_c_s_ClientFactoryService__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientFactoryService> { private class Type_factory__o_k_w_c_s_c_c_s_ClientFactoryService__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ClientFactoryService implements Proxy<ClientFactoryService> {
    private final ProxyHelper<ClientFactoryService> proxyHelper = new ProxyHelperImpl<ClientFactoryService>("Type_factory__o_k_w_c_s_c_c_s_ClientFactoryService__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ClientFactoryService instance) {

    }

    public ClientFactoryService asBeanType() {
      return this;
    }

    public void setInstance(final ClientFactoryService instance) {
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

    @Override public void newDefinition(String definitionId, ServiceCallback callback) {
      if (proxyHelper != null) {
        final ClientFactoryService proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.newDefinition(definitionId, callback);
      } else {
        super.newDefinition(definitionId, callback);
      }
    }

    @Override public void newElement(String uuid, String definitionId, ServiceCallback callback) {
      if (proxyHelper != null) {
        final ClientFactoryService proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.newElement(uuid, definitionId, callback);
      } else {
        super.newElement(uuid, definitionId, callback);
      }
    }

    @Override public void newDiagram(String uuid, String id, Metadata metadata, ServiceCallback callback) {
      if (proxyHelper != null) {
        final ClientFactoryService proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.newDiagram(uuid, id, metadata, callback);
      } else {
        super.newDiagram(uuid, id, metadata, callback);
      }
    }

    @Override public ClientFactoryManager getClientFactoryManager() {
      if (proxyHelper != null) {
        final ClientFactoryService proxiedInstance = proxyHelper.getInstance(this);
        final ClientFactoryManager retVal = proxiedInstance.getClientFactoryManager();
        return retVal;
      } else {
        return super.getClientFactoryManager();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ClientFactoryService proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_s_ClientFactoryService__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClientFactoryService.class, "Type_factory__o_k_w_c_s_c_c_s_ClientFactoryService__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClientFactoryService.class, Object.class });
  }

  public ClientFactoryService createInstance(final ContextManager contextManager) {
    final ClientFactoryManager _clientFactoryManager_0 = (ClientFactoryManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientFactoryManager__quals__j_e_i_Any_j_e_i_Default");
    final Caller<FactoryService> _factoryServiceCaller_1 = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { FactoryService.class }, new Annotation[] { });
    final ClientFactoryService instance = new ClientFactoryService(_clientFactoryManager_0, _factoryServiceCaller_1);
    registerDependentScopedReference(instance, _factoryServiceCaller_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ClientFactoryService> proxyImpl = new Type_factory__o_k_w_c_s_c_c_s_ClientFactoryService__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}