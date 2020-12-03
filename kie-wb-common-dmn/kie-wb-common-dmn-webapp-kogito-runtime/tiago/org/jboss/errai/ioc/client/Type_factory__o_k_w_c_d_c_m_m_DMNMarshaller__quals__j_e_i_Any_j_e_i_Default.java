package org.jboss.errai.ioc.client;

import java.util.List;
import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.marshaller.marshall.DMNMarshaller;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Node;

public class Type_factory__o_k_w_c_d_c_m_m_DMNMarshaller__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNMarshaller> { private class Type_factory__o_k_w_c_d_c_m_m_DMNMarshaller__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DMNMarshaller implements Proxy<DMNMarshaller> {
    private final ProxyHelper<DMNMarshaller> proxyHelper = new ProxyHelperImpl<DMNMarshaller>("Type_factory__o_k_w_c_d_c_m_m_DMNMarshaller__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DMNMarshaller instance) {

    }

    public DMNMarshaller asBeanType() {
      return this;
    }

    public void setInstance(final DMNMarshaller instance) {
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
        final DMNMarshaller proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public JSITDefinitions marshall() {
      if (proxyHelper != null) {
        final DMNMarshaller proxiedInstance = proxyHelper.getInstance(this);
        final JSITDefinitions retVal = proxiedInstance.marshall();
        return retVal;
      } else {
        return super.marshall();
      }
    }

    @Override public List getNodeStream(Diagram diagram) {
      if (proxyHelper != null) {
        final DMNMarshaller proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getNodeStream(diagram);
        return retVal;
      } else {
        return super.getNodeStream(diagram);
      }
    }

    @Override public JSITDRGElement stunnerToDMN(Node node, Consumer componentWidthsConsumer) {
      if (proxyHelper != null) {
        final DMNMarshaller proxiedInstance = proxyHelper.getInstance(this);
        final JSITDRGElement retVal = proxiedInstance.stunnerToDMN(node, componentWidthsConsumer);
        return retVal;
      } else {
        return super.stunnerToDMN(node, componentWidthsConsumer);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNMarshaller proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_m_m_DMNMarshaller__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNMarshaller.class, "Type_factory__o_k_w_c_d_c_m_m_DMNMarshaller__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNMarshaller.class, Object.class });
  }

  public DMNMarshaller createInstance(final ContextManager contextManager) {
    final FactoryManager _factoryManager_0 = (ClientFactoryManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientFactoryManager__quals__j_e_i_Any_j_e_i_Default");
    final DMNMarshaller instance = new DMNMarshaller(_factoryManager_0);
    setIncompleteInstance(instance);
    final DMNDiagramsSession DMNMarshaller_dmnDiagramsSession = (DMNDiagramsSession) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSession__quals__j_e_i_Any_j_e_i_Default");
    DMNMarshaller_DMNDiagramsSession_dmnDiagramsSession(instance, DMNMarshaller_dmnDiagramsSession);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DMNMarshaller instance) {
    instance.init();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNMarshaller> proxyImpl = new Type_factory__o_k_w_c_d_c_m_m_DMNMarshaller__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static DMNDiagramsSession DMNMarshaller_DMNDiagramsSession_dmnDiagramsSession(DMNMarshaller instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.marshaller.marshall.DMNMarshaller::dmnDiagramsSession;
  }-*/;

  native static void DMNMarshaller_DMNDiagramsSession_dmnDiagramsSession(DMNMarshaller instance, DMNDiagramsSession value) /*-{
    instance.@org.kie.workbench.common.dmn.client.marshaller.marshall.DMNMarshaller::dmnDiagramsSession = value;
  }-*/;
}