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
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.factory.impl.AbstractElementFactory;
import org.kie.workbench.common.stunner.core.factory.impl.NodeFactoryImpl;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_f_i_NodeFactoryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<NodeFactoryImpl> { private class Type_factory__o_k_w_c_s_c_f_i_NodeFactoryImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends NodeFactoryImpl implements Proxy<NodeFactoryImpl> {
    private final ProxyHelper<NodeFactoryImpl> proxyHelper = new ProxyHelperImpl<NodeFactoryImpl>("Type_factory__o_k_w_c_s_c_f_i_NodeFactoryImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final NodeFactoryImpl instance) {

    }

    public NodeFactoryImpl asBeanType() {
      return this;
    }

    public void setInstance(final NodeFactoryImpl instance) {
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
        final NodeFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getFactoryType();
        return retVal;
      } else {
        return super.getFactoryType();
      }
    }

    @Override public Node build(String uuid, Object definition) {
      if (proxyHelper != null) {
        final NodeFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final Node retVal = proxiedInstance.build(uuid, definition);
        return retVal;
      } else {
        return super.build(uuid, definition);
      }
    }

    @Override public boolean accepts(Object source) {
      if (proxyHelper != null) {
        final NodeFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.accepts(source);
        return retVal;
      } else {
        return super.accepts(source);
      }
    }

    @Override protected DefinitionManager getDefinitionManager() {
      if (proxyHelper != null) {
        final NodeFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final DefinitionManager retVal = NodeFactoryImpl_getDefinitionManager(proxiedInstance);
        return retVal;
      } else {
        return super.getDefinitionManager();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final NodeFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }

    @Override protected void appendLabels(Set target, Object definition) {
      if (proxyHelper != null) {
        final NodeFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        AbstractElementFactory_appendLabels_Set_Object(proxiedInstance, target, definition);
      } else {
        super.appendLabels(target, definition);
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_f_i_NodeFactoryImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(NodeFactoryImpl.class, "Type_factory__o_k_w_c_s_c_f_i_NodeFactoryImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { NodeFactoryImpl.class, AbstractElementFactory.class, Object.class, ElementFactory.class, org.kie.workbench.common.stunner.core.factory.Factory.class, NodeFactory.class });
  }

  public NodeFactoryImpl createInstance(final ContextManager contextManager) {
    final DefinitionUtils _definitionUtils_0 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final NodeFactoryImpl instance = new NodeFactoryImpl(_definitionUtils_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<NodeFactoryImpl> proxyImpl = new Type_factory__o_k_w_c_s_c_f_i_NodeFactoryImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static DefinitionManager NodeFactoryImpl_getDefinitionManager(NodeFactoryImpl instance) /*-{
    return instance.@org.kie.workbench.common.stunner.core.factory.impl.NodeFactoryImpl::getDefinitionManager()();
  }-*/;

  public native static void AbstractElementFactory_appendLabels_Set_Object(AbstractElementFactory instance, Set a0, Object a1) /*-{
    instance.@org.kie.workbench.common.stunner.core.factory.impl.AbstractElementFactory::appendLabels(Ljava/util/Set;Ljava/lang/Object;)(a0, a1);
  }-*/;
}