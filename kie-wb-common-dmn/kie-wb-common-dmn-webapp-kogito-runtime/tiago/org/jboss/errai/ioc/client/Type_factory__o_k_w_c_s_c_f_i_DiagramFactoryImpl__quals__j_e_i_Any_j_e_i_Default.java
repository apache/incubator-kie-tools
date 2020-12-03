package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.factory.diagram.DiagramFactory;
import org.kie.workbench.common.stunner.core.factory.impl.DiagramFactoryImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;

public class Type_factory__o_k_w_c_s_c_f_i_DiagramFactoryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DiagramFactoryImpl> { private class Type_factory__o_k_w_c_s_c_f_i_DiagramFactoryImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DiagramFactoryImpl implements Proxy<DiagramFactoryImpl> {
    private final ProxyHelper<DiagramFactoryImpl> proxyHelper = new ProxyHelperImpl<DiagramFactoryImpl>("Type_factory__o_k_w_c_s_c_f_i_DiagramFactoryImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DiagramFactoryImpl instance) {

    }

    public DiagramFactoryImpl asBeanType() {
      return this;
    }

    public void setInstance(final DiagramFactoryImpl instance) {
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

    @Override public Class getMetadataType() {
      if (proxyHelper != null) {
        final DiagramFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getMetadataType();
        return retVal;
      } else {
        return super.getMetadataType();
      }
    }

    @Override public Diagram build(String name, Metadata metadata, Graph graph) {
      if (proxyHelper != null) {
        final DiagramFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final Diagram retVal = proxiedInstance.build(name, metadata, graph);
        return retVal;
      } else {
        return super.build(name, metadata, graph);
      }
    }

    @Override public boolean accepts(String source) {
      if (proxyHelper != null) {
        final DiagramFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.accepts(source);
        return retVal;
      } else {
        return super.accepts(source);
      }
    }

    @Override public boolean isDefault() {
      if (proxyHelper != null) {
        final DiagramFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isDefault();
        return retVal;
      } else {
        return super.isDefault();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DiagramFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_f_i_DiagramFactoryImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DiagramFactoryImpl.class, "Type_factory__o_k_w_c_s_c_f_i_DiagramFactoryImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DiagramFactoryImpl.class, Object.class, DiagramFactory.class, org.kie.workbench.common.stunner.core.factory.Factory.class });
  }

  public DiagramFactoryImpl createInstance(final ContextManager contextManager) {
    final DiagramFactoryImpl instance = new DiagramFactoryImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DiagramFactoryImpl> proxyImpl = new Type_factory__o_k_w_c_s_c_f_i_DiagramFactoryImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}