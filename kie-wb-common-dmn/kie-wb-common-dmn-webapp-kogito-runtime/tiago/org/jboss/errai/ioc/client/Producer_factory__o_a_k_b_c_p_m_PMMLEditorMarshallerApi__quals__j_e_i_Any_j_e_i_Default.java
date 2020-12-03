package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.appformer.kogito.bridge.client.pmmleditor.marshaller.PMMLEditorMarshallerApi;
import org.appformer.kogito.bridge.client.pmmleditor.marshaller.PMMLEditorMarshallerServiceProducer;
import org.appformer.kogito.bridge.client.pmmleditor.marshaller.model.PMMLDocumentData;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;

public class Producer_factory__o_a_k_b_c_p_m_PMMLEditorMarshallerApi__quals__j_e_i_Any_j_e_i_Default extends Factory<PMMLEditorMarshallerApi> { private class Producer_factory__o_a_k_b_c_p_m_PMMLEditorMarshallerApi__quals__j_e_i_Any_j_e_i_DefaultProxyImpl implements Proxy<PMMLEditorMarshallerApi>, PMMLEditorMarshallerApi {
    private final ProxyHelper<PMMLEditorMarshallerApi> proxyHelper = new ProxyHelperImpl<PMMLEditorMarshallerApi>("Producer_factory__o_a_k_b_c_p_m_PMMLEditorMarshallerApi__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final PMMLEditorMarshallerApi instance) {

    }

    public PMMLEditorMarshallerApi asBeanType() {
      return this;
    }

    public void setInstance(final PMMLEditorMarshallerApi instance) {
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

    @Override public PMMLDocumentData getPMMLDocumentData(String xmlContent) {
      if (proxyHelper != null) {
        final PMMLEditorMarshallerApi proxiedInstance = proxyHelper.getInstance(this);
        final PMMLDocumentData retVal = proxiedInstance.getPMMLDocumentData(xmlContent);
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final PMMLEditorMarshallerApi proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }
  }
  public Producer_factory__o_a_k_b_c_p_m_PMMLEditorMarshallerApi__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PMMLEditorMarshallerApi.class, "Producer_factory__o_a_k_b_c_p_m_PMMLEditorMarshallerApi__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PMMLEditorMarshallerApi.class });
  }

  public PMMLEditorMarshallerApi createInstance(final ContextManager contextManager) {
    PMMLEditorMarshallerServiceProducer producerInstance = contextManager.getInstance("Type_factory__o_a_k_b_c_p_m_PMMLEditorMarshallerServiceProducer__quals__j_e_i_Any_j_e_i_Default");
    producerInstance = Factory.maybeUnwrapProxy(producerInstance);
    final PMMLEditorMarshallerApi instance = producerInstance.produce();
    thisInstance.setReference(instance, "producerInstance", producerInstance);
    registerDependentScopedReference(instance, producerInstance);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<PMMLEditorMarshallerApi> proxyImpl = new Producer_factory__o_a_k_b_c_p_m_PMMLEditorMarshallerApi__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}