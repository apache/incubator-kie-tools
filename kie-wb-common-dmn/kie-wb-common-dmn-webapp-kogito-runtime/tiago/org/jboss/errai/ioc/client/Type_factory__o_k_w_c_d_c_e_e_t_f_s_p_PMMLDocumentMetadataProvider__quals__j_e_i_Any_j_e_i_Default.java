package org.jboss.errai.ioc.client;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml.PMMLDocumentMetadataProvider;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsPageStateProviderImpl;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.service.DMNClientServicesProxy;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.services.DMNClientServicesProxyImpl;

public class Type_factory__o_k_w_c_d_c_e_e_t_f_s_p_PMMLDocumentMetadataProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<PMMLDocumentMetadataProvider> { private class Type_factory__o_k_w_c_d_c_e_e_t_f_s_p_PMMLDocumentMetadataProvider__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends PMMLDocumentMetadataProvider implements Proxy<PMMLDocumentMetadataProvider> {
    private final ProxyHelper<PMMLDocumentMetadataProvider> proxyHelper = new ProxyHelperImpl<PMMLDocumentMetadataProvider>("Type_factory__o_k_w_c_d_c_e_e_t_f_s_p_PMMLDocumentMetadataProvider__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final PMMLDocumentMetadataProvider instance) {

    }

    public PMMLDocumentMetadataProvider asBeanType() {
      return this;
    }

    public void setInstance(final PMMLDocumentMetadataProvider instance) {
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

    @Override public void loadPMMLIncludedDocuments() {
      if (proxyHelper != null) {
        final PMMLDocumentMetadataProvider proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.loadPMMLIncludedDocuments();
      } else {
        super.loadPMMLIncludedDocuments();
      }
    }

    @Override public void onRefreshDecisionComponents(RefreshDecisionComponents events) {
      if (proxyHelper != null) {
        final PMMLDocumentMetadataProvider proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onRefreshDecisionComponents(events);
      } else {
        super.onRefreshDecisionComponents(events);
      }
    }

    @Override public List getPMMLDocumentNames() {
      if (proxyHelper != null) {
        final PMMLDocumentMetadataProvider proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getPMMLDocumentNames();
        return retVal;
      } else {
        return super.getPMMLDocumentNames();
      }
    }

    @Override public List getPMMLDocumentModels(String pmmlDocumentName) {
      if (proxyHelper != null) {
        final PMMLDocumentMetadataProvider proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getPMMLDocumentModels(pmmlDocumentName);
        return retVal;
      } else {
        return super.getPMMLDocumentModels(pmmlDocumentName);
      }
    }

    @Override public List getPMMLDocumentModelParameterNames(String pmmlDocumentName, String pmmlDocumentModelName) {
      if (proxyHelper != null) {
        final PMMLDocumentMetadataProvider proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getPMMLDocumentModelParameterNames(pmmlDocumentName, pmmlDocumentModelName);
        return retVal;
      } else {
        return super.getPMMLDocumentModelParameterNames(pmmlDocumentName, pmmlDocumentModelName);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final PMMLDocumentMetadataProvider proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_e_t_f_s_p_PMMLDocumentMetadataProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PMMLDocumentMetadataProvider.class, "Type_factory__o_k_w_c_d_c_e_e_t_f_s_p_PMMLDocumentMetadataProvider__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PMMLDocumentMetadataProvider.class, Object.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents", new AbstractCDIEventCallback<RefreshDecisionComponents>() {
      public void fireEvent(final RefreshDecisionComponents event) {
        final PMMLDocumentMetadataProvider instance = Factory.maybeUnwrapProxy((PMMLDocumentMetadataProvider) context.getInstance("Type_factory__o_k_w_c_d_c_e_e_t_f_s_p_PMMLDocumentMetadataProvider__quals__j_e_i_Any_j_e_i_Default"));
        instance.onRefreshDecisionComponents(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents []";
      }
    });
  }

  public PMMLDocumentMetadataProvider createInstance(final ContextManager contextManager) {
    final IncludedModelsPageStateProviderImpl _stateProvider_2 = (IncludedModelsPageStateProviderImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsPageStateProviderImpl__quals__j_e_i_Any_j_e_i_Default");
    final DMNGraphUtils _graphUtils_0 = (DMNGraphUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_g_DMNGraphUtils__quals__j_e_i_Any_j_e_i_Default");
    final DMNClientServicesProxy _clientServicesProxy_1 = (DMNClientServicesProxyImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_s_DMNClientServicesProxyImpl__quals__j_e_i_Any_j_e_i_Default");
    final PMMLDocumentMetadataProvider instance = new PMMLDocumentMetadataProvider(_graphUtils_0, _clientServicesProxy_1, _stateProvider_2);
    registerDependentScopedReference(instance, _graphUtils_0);
    registerDependentScopedReference(instance, _clientServicesProxy_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final PMMLDocumentMetadataProvider instance) {
    instance.loadPMMLIncludedDocuments();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<PMMLDocumentMetadataProvider> proxyImpl = new Type_factory__o_k_w_c_d_c_e_e_t_f_s_p_PMMLDocumentMetadataProvider__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}