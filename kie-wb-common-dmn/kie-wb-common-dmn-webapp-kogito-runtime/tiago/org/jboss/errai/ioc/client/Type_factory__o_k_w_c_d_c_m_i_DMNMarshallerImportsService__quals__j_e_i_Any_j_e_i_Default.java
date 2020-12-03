package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.marshaller.included.DMNMarshallerImportsService;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes.NodeEntriesFactory;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;

public class Type_factory__o_k_w_c_d_c_m_i_DMNMarshallerImportsService__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNMarshallerImportsService> { private class Type_factory__o_k_w_c_d_c_m_i_DMNMarshallerImportsService__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DMNMarshallerImportsService implements Proxy<DMNMarshallerImportsService> {
    private final ProxyHelper<DMNMarshallerImportsService> proxyHelper = new ProxyHelperImpl<DMNMarshallerImportsService>("Type_factory__o_k_w_c_d_c_m_i_DMNMarshallerImportsService__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_c_m_i_DMNMarshallerImportsService__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null);
    }

    public void initProxyProperties(final DMNMarshallerImportsService instance) {

    }

    public DMNMarshallerImportsService asBeanType() {
      return this;
    }

    public void setInstance(final DMNMarshallerImportsService instance) {
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

    @Override public void getDRGElements(String dmnXml, ServiceCallback callback) {
      if (proxyHelper != null) {
        final DMNMarshallerImportsService proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.getDRGElements(dmnXml, callback);
      } else {
        super.getDRGElements(dmnXml, callback);
      }
    }

    @Override public void getDMNDefinitions(String dmnXml, ServiceCallback callback) {
      if (proxyHelper != null) {
        final DMNMarshallerImportsService proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.getDMNDefinitions(dmnXml, callback);
      } else {
        super.getDMNDefinitions(dmnXml, callback);
      }
    }

    @Override public void getWbDefinitions(String dmnXml, ServiceCallback callback) {
      if (proxyHelper != null) {
        final DMNMarshallerImportsService proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.getWbDefinitions(dmnXml, callback);
      } else {
        super.getWbDefinitions(dmnXml, callback);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNMarshallerImportsService proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_m_i_DMNMarshallerImportsService__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNMarshallerImportsService.class, "Type_factory__o_k_w_c_d_c_m_i_DMNMarshallerImportsService__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNMarshallerImportsService.class, Object.class });
  }

  public DMNMarshallerImportsService createInstance(final ContextManager contextManager) {
    final NodeEntriesFactory _modelToStunnerConverter_0 = (NodeEntriesFactory) contextManager.getInstance("Type_factory__o_k_w_c_d_c_m_u_n_NodeEntriesFactory__quals__j_e_i_Any_j_e_i_Default");
    final DMNMarshallerImportsService instance = new DMNMarshallerImportsService(_modelToStunnerConverter_0);
    registerDependentScopedReference(instance, _modelToStunnerConverter_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_c_m_i_DMNMarshallerImportsService__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.client.marshaller.included.DMNMarshallerImportsService an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.client.marshaller.included.DMNMarshallerImportsService ([org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes.NodeEntriesFactory])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNMarshallerImportsService> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}