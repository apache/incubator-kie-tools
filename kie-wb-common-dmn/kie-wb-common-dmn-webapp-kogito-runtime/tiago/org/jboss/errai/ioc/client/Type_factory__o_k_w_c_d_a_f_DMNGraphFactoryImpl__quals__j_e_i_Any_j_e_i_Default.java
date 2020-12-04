package org.jboss.errai.ioc.client;

import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.api.factory.DMNFactory;
import org.kie.workbench.common.dmn.api.factory.DMNGraphFactory;
import org.kie.workbench.common.dmn.api.factory.DMNGraphFactoryImpl;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.factory.graph.GraphFactory;
import org.kie.workbench.common.stunner.core.factory.impl.AbstractElementFactory;
import org.kie.workbench.common.stunner.core.factory.impl.AbstractGraphFactory;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManagerImpl;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.processing.index.map.MapIndexBuilder;

public class Type_factory__o_k_w_c_d_a_f_DMNGraphFactoryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNGraphFactoryImpl> { private class Type_factory__o_k_w_c_d_a_f_DMNGraphFactoryImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DMNGraphFactoryImpl implements Proxy<DMNGraphFactoryImpl> {
    private final ProxyHelper<DMNGraphFactoryImpl> proxyHelper = new ProxyHelperImpl<DMNGraphFactoryImpl>("Type_factory__o_k_w_c_d_a_f_DMNGraphFactoryImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DMNGraphFactoryImpl instance) {

    }

    public DMNGraphFactoryImpl asBeanType() {
      return this;
    }

    public void setInstance(final DMNGraphFactoryImpl instance) {
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

    @Override public Class getFactoryType() {
      if (proxyHelper != null) {
        final DMNGraphFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getFactoryType();
        return retVal;
      } else {
        return super.getFactoryType();
      }
    }

    @Override public Graph build(String uuid, String definitionSetId) {
      if (proxyHelper != null) {
        final DMNGraphFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final Graph retVal = proxiedInstance.build(uuid, definitionSetId);
        return retVal;
      } else {
        return super.build(uuid, definitionSetId);
      }
    }

    @Override public boolean accepts(String source) {
      if (proxyHelper != null) {
        final DMNGraphFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.accepts(source);
        return retVal;
      } else {
        return super.accepts(source);
      }
    }

    @Override protected DefinitionManager getDefinitionManager() {
      if (proxyHelper != null) {
        final DMNGraphFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final DefinitionManager retVal = DMNGraphFactoryImpl_getDefinitionManager(proxiedInstance);
        return retVal;
      } else {
        return super.getDefinitionManager();
      }
    }

    @Override protected List buildInitialisationCommands() {
      if (proxyHelper != null) {
        final DMNGraphFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = DMNGraphFactoryImpl_buildInitialisationCommands(proxiedInstance);
        return retVal;
      } else {
        return super.buildInitialisationCommands();
      }
    }

    @Override protected GraphCommandExecutionContext createGraphContext(Graph graph) {
      if (proxyHelper != null) {
        final DMNGraphFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final GraphCommandExecutionContext retVal = DMNGraphFactoryImpl_createGraphContext_Graph(proxiedInstance, graph);
        return retVal;
      } else {
        return super.createGraphContext(graph);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNGraphFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }

    @Override protected void appendLabels(Set target, Object definition) {
      if (proxyHelper != null) {
        final DMNGraphFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        AbstractElementFactory_appendLabels_Set_Object(proxiedInstance, target, definition);
      } else {
        super.appendLabels(target, definition);
      }
    }
  }
  public Type_factory__o_k_w_c_d_a_f_DMNGraphFactoryImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNGraphFactoryImpl.class, "Type_factory__o_k_w_c_d_a_f_DMNGraphFactoryImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNGraphFactoryImpl.class, AbstractGraphFactory.class, AbstractElementFactory.class, Object.class, ElementFactory.class, org.kie.workbench.common.stunner.core.factory.Factory.class, GraphFactory.class, DMNGraphFactory.class, DMNFactory.class });
  }

  public DMNGraphFactoryImpl createInstance(final ContextManager contextManager) {
    final FactoryManager _factoryManager_1 = (ClientFactoryManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientFactoryManager__quals__j_e_i_Any_j_e_i_Default");
    final GraphCommandManager _graphCommandManager_2 = (GraphCommandManagerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_c_GraphCommandManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionManager _definitionManager_0 = (ClientDefinitionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_Default");
    final GraphCommandFactory _graphCommandFactory_3 = (GraphCommandFactory) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_c_i_GraphCommandFactory__quals__j_e_i_Any_j_e_i_Default");
    final GraphIndexBuilder _indexBuilder_4 = (MapIndexBuilder) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_p_i_m_MapIndexBuilder__quals__j_e_i_Any_j_e_i_Default");
    final DMNGraphFactoryImpl instance = new DMNGraphFactoryImpl(_definitionManager_0, _factoryManager_1, _graphCommandManager_2, _graphCommandFactory_3, _indexBuilder_4);
    registerDependentScopedReference(instance, _graphCommandManager_2);
    registerDependentScopedReference(instance, _indexBuilder_4);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNGraphFactoryImpl> proxyImpl = new Type_factory__o_k_w_c_d_a_f_DMNGraphFactoryImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void AbstractElementFactory_appendLabels_Set_Object(AbstractElementFactory instance, Set a0, Object a1) /*-{
    instance.@org.kie.workbench.common.stunner.core.factory.impl.AbstractElementFactory::appendLabels(Ljava/util/Set;Ljava/lang/Object;)(a0, a1);
  }-*/;

  public native static DefinitionManager DMNGraphFactoryImpl_getDefinitionManager(DMNGraphFactoryImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.api.factory.DMNGraphFactoryImpl::getDefinitionManager()();
  }-*/;

  public native static GraphCommandExecutionContext DMNGraphFactoryImpl_createGraphContext_Graph(DMNGraphFactoryImpl instance, Graph a0) /*-{
    return instance.@org.kie.workbench.common.dmn.api.factory.DMNGraphFactoryImpl::createGraphContext(Lorg/kie/workbench/common/stunner/core/graph/Graph;)(a0);
  }-*/;

  public native static List DMNGraphFactoryImpl_buildInitialisationCommands(DMNGraphFactoryImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.api.factory.DMNGraphFactoryImpl::buildInitialisationCommands()();
  }-*/;
}