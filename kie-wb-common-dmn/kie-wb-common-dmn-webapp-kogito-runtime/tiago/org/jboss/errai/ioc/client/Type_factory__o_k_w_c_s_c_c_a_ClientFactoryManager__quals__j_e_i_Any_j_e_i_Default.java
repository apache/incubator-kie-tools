package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.api.AbstractFactoryManager;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.client.registry.impl.ClientRegistryFactoryImpl;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.factory.definition.DefinitionFactory;
import org.kie.workbench.common.stunner.core.factory.diagram.DiagramFactory;
import org.kie.workbench.common.stunner.core.factory.graph.EdgeFactory;
import org.kie.workbench.common.stunner.core.factory.graph.GraphFactory;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;
import org.kie.workbench.common.stunner.core.registry.factory.FactoryRegistry;

public class Type_factory__o_k_w_c_s_c_c_a_ClientFactoryManager__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientFactoryManager> { private class Type_factory__o_k_w_c_s_c_c_a_ClientFactoryManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ClientFactoryManager implements Proxy<ClientFactoryManager> {
    private final ProxyHelper<ClientFactoryManager> proxyHelper = new ProxyHelperImpl<ClientFactoryManager>("Type_factory__o_k_w_c_s_c_c_a_ClientFactoryManager__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ClientFactoryManager instance) {

    }

    public ClientFactoryManager asBeanType() {
      return this;
    }

    public void setInstance(final ClientFactoryManager instance) {
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

    @Override public void init() {
      if (proxyHelper != null) {
        final ClientFactoryManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public Object newDefinition(String id) {
      if (proxyHelper != null) {
        final ClientFactoryManager proxiedInstance = proxyHelper.getInstance(this);
        final Object retVal = proxiedInstance.newDefinition(id);
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public Element newElement(String uuid, String id) {
      if (proxyHelper != null) {
        final ClientFactoryManager proxiedInstance = proxyHelper.getInstance(this);
        final Element retVal = proxiedInstance.newElement(uuid, id);
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public Diagram newDiagram(String name, String definitionSetId, Metadata metadata) {
      if (proxyHelper != null) {
        final ClientFactoryManager proxiedInstance = proxyHelper.getInstance(this);
        final Diagram retVal = proxiedInstance.newDiagram(name, definitionSetId, metadata);
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public FactoryRegistry registry() {
      if (proxyHelper != null) {
        final ClientFactoryManager proxiedInstance = proxyHelper.getInstance(this);
        final FactoryRegistry retVal = proxiedInstance.registry();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ClientFactoryManager proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }

    @Override protected DefinitionManager getDefinitionManager() {
      if (proxyHelper != null) {
        final ClientFactoryManager proxiedInstance = proxyHelper.getInstance(this);
        final DefinitionManager retVal = AbstractFactoryManager_getDefinitionManager(proxiedInstance);
        return retVal;
      } else {
        return super.getDefinitionManager();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_a_ClientFactoryManager__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClientFactoryManager.class, "Type_factory__o_k_w_c_s_c_c_a_ClientFactoryManager__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClientFactoryManager.class, AbstractFactoryManager.class, Object.class, FactoryManager.class });
  }

  public ClientFactoryManager createInstance(final ContextManager contextManager) {
    final DefinitionManager _definitionManager_1 = (ClientDefinitionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<DiagramFactory> _diagramFactoryInstances_3 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DiagramFactory.class }, new Annotation[] { });
    final ManagedInstance<NodeFactory> _nodeFactoryInstances_5 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { NodeFactory.class }, new Annotation[] { });
    final ManagedInstance<GraphFactory> _graphFactoryInstances_4 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { GraphFactory.class }, new Annotation[] { });
    final RegistryFactory _registryFactory_0 = (ClientRegistryFactoryImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_r_i_ClientRegistryFactoryImpl__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<DefinitionFactory> _definitionFactoryInstances_2 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DefinitionFactory.class }, new Annotation[] { });
    final ManagedInstance<EdgeFactory> _edgeFactoryInstances_6 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { EdgeFactory.class }, new Annotation[] { });
    final ClientFactoryManager instance = new ClientFactoryManager(_registryFactory_0, _definitionManager_1, _definitionFactoryInstances_2, _diagramFactoryInstances_3, _graphFactoryInstances_4, _nodeFactoryInstances_5, _edgeFactoryInstances_6);
    registerDependentScopedReference(instance, _diagramFactoryInstances_3);
    registerDependentScopedReference(instance, _nodeFactoryInstances_5);
    registerDependentScopedReference(instance, _graphFactoryInstances_4);
    registerDependentScopedReference(instance, _definitionFactoryInstances_2);
    registerDependentScopedReference(instance, _edgeFactoryInstances_6);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ClientFactoryManager instance) {
    instance.init();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ClientFactoryManager> proxyImpl = new Type_factory__o_k_w_c_s_c_c_a_ClientFactoryManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static DefinitionManager AbstractFactoryManager_getDefinitionManager(AbstractFactoryManager instance) /*-{
    return instance.@org.kie.workbench.common.stunner.core.api.AbstractFactoryManager::getDefinitionManager()();
  }-*/;
}