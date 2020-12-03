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
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.factory.graph.GraphFactory;
import org.kie.workbench.common.stunner.core.factory.impl.AbstractElementFactory;
import org.kie.workbench.common.stunner.core.factory.impl.AbstractGraphFactory;
import org.kie.workbench.common.stunner.core.factory.impl.GraphFactoryImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;

public class Type_factory__o_k_w_c_s_c_f_i_GraphFactoryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<GraphFactoryImpl> { private class Type_factory__o_k_w_c_s_c_f_i_GraphFactoryImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends GraphFactoryImpl implements Proxy<GraphFactoryImpl> {
    private final ProxyHelper<GraphFactoryImpl> proxyHelper = new ProxyHelperImpl<GraphFactoryImpl>("Type_factory__o_k_w_c_s_c_f_i_GraphFactoryImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final GraphFactoryImpl instance) {

    }

    public GraphFactoryImpl asBeanType() {
      return this;
    }

    public void setInstance(final GraphFactoryImpl instance) {
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
        final GraphFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getFactoryType();
        return retVal;
      } else {
        return super.getFactoryType();
      }
    }

    @Override public boolean accepts(String source) {
      if (proxyHelper != null) {
        final GraphFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.accepts(source);
        return retVal;
      } else {
        return super.accepts(source);
      }
    }

    @Override protected DefinitionManager getDefinitionManager() {
      if (proxyHelper != null) {
        final GraphFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final DefinitionManager retVal = GraphFactoryImpl_getDefinitionManager(proxiedInstance);
        return retVal;
      } else {
        return super.getDefinitionManager();
      }
    }

    @Override public Graph build(String uuid, String definitionSetId) {
      if (proxyHelper != null) {
        final GraphFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final Graph retVal = proxiedInstance.build(uuid, definitionSetId);
        return retVal;
      } else {
        return super.build(uuid, definitionSetId);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final GraphFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }

    @Override protected void appendLabels(Set target, Object definition) {
      if (proxyHelper != null) {
        final GraphFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        AbstractElementFactory_appendLabels_Set_Object(proxiedInstance, target, definition);
      } else {
        super.appendLabels(target, definition);
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_f_i_GraphFactoryImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GraphFactoryImpl.class, "Type_factory__o_k_w_c_s_c_f_i_GraphFactoryImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GraphFactoryImpl.class, AbstractGraphFactory.class, AbstractElementFactory.class, Object.class, ElementFactory.class, org.kie.workbench.common.stunner.core.factory.Factory.class, GraphFactory.class });
  }

  public GraphFactoryImpl createInstance(final ContextManager contextManager) {
    final DefinitionManager _definitionManager_0 = (ClientDefinitionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_Default");
    final GraphFactoryImpl instance = new GraphFactoryImpl(_definitionManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<GraphFactoryImpl> proxyImpl = new Type_factory__o_k_w_c_s_c_f_i_GraphFactoryImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void AbstractElementFactory_appendLabels_Set_Object(AbstractElementFactory instance, Set a0, Object a1) /*-{
    instance.@org.kie.workbench.common.stunner.core.factory.impl.AbstractElementFactory::appendLabels(Ljava/util/Set;Ljava/lang/Object;)(a0, a1);
  }-*/;

  public native static DefinitionManager GraphFactoryImpl_getDefinitionManager(GraphFactoryImpl instance) /*-{
    return instance.@org.kie.workbench.common.stunner.core.factory.impl.GraphFactoryImpl::getDefinitionManager()();
  }-*/;
}