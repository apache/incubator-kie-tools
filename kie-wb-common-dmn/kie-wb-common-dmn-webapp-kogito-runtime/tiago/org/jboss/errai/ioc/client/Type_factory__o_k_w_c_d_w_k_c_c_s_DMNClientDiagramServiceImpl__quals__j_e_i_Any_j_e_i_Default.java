package org.jboss.errai.ioc.client;

import elemental2.promise.Promise;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.marshaller.DMNMarshallerService;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.services.DMNClientDiagramServiceImpl;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.kogito.api.editor.impl.KogitoDiagramResourceImpl;
import org.kie.workbench.common.stunner.kogito.client.service.AbstractKogitoClientDiagramService;
import org.kie.workbench.common.stunner.kogito.client.service.KogitoClientDiagramService;
import org.uberfire.client.promise.Promises;

public class Type_factory__o_k_w_c_d_w_k_c_c_s_DMNClientDiagramServiceImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNClientDiagramServiceImpl> { private class Type_factory__o_k_w_c_d_w_k_c_c_s_DMNClientDiagramServiceImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DMNClientDiagramServiceImpl implements Proxy<DMNClientDiagramServiceImpl> {
    private final ProxyHelper<DMNClientDiagramServiceImpl> proxyHelper = new ProxyHelperImpl<DMNClientDiagramServiceImpl>("Type_factory__o_k_w_c_d_w_k_c_c_s_DMNClientDiagramServiceImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DMNClientDiagramServiceImpl instance) {

    }

    public DMNClientDiagramServiceImpl asBeanType() {
      return this;
    }

    public void setInstance(final DMNClientDiagramServiceImpl instance) {
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

    @Override public void transform(String fileName, String xml, ServiceCallback callback) {
      if (proxyHelper != null) {
        final DMNClientDiagramServiceImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.transform(fileName, xml, callback);
      } else {
        super.transform(fileName, xml, callback);
      }
    }

    @Override public void transform(String xml, ServiceCallback callback) {
      if (proxyHelper != null) {
        final DMNClientDiagramServiceImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.transform(xml, callback);
      } else {
        super.transform(xml, callback);
      }
    }

    @Override public Promise transform(KogitoDiagramResourceImpl resource) {
      if (proxyHelper != null) {
        final DMNClientDiagramServiceImpl proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.transform(resource);
        return retVal;
      } else {
        return super.transform(resource);
      }
    }

    @Override public String generateDefaultId() {
      if (proxyHelper != null) {
        final DMNClientDiagramServiceImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.generateDefaultId();
        return retVal;
      } else {
        return super.generateDefaultId();
      }
    }

    @Override public String createDiagramTitleFromFilePath(String filePath) {
      if (proxyHelper != null) {
        final DMNClientDiagramServiceImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.createDiagramTitleFromFilePath(filePath);
        return retVal;
      } else {
        return super.createDiagramTitleFromFilePath(filePath);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNClientDiagramServiceImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_w_k_c_c_s_DMNClientDiagramServiceImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNClientDiagramServiceImpl.class, "Type_factory__o_k_w_c_d_w_k_c_c_s_DMNClientDiagramServiceImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNClientDiagramServiceImpl.class, AbstractKogitoClientDiagramService.class, Object.class, KogitoClientDiagramService.class });
  }

  public DMNClientDiagramServiceImpl createInstance(final ContextManager contextManager) {
    final FactoryManager _factoryManager_0 = (ClientFactoryManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientFactoryManager__quals__j_e_i_Any_j_e_i_Default");
    final DMNMarshallerService _marshallerService_3 = (DMNMarshallerService) contextManager.getInstance("Type_factory__o_k_w_c_d_c_m_DMNMarshallerService__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionManager _definitionManager_1 = (ClientDefinitionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_Default");
    final Promises _promises_2 = (Promises) contextManager.getInstance("Type_factory__o_u_c_p_Promises__quals__j_e_i_Any_j_e_i_Default");
    final DMNClientDiagramServiceImpl instance = new DMNClientDiagramServiceImpl(_factoryManager_0, _definitionManager_1, _promises_2, _marshallerService_3);
    registerDependentScopedReference(instance, _marshallerService_3);
    registerDependentScopedReference(instance, _promises_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNClientDiagramServiceImpl> proxyImpl = new Type_factory__o_k_w_c_d_w_k_c_c_s_DMNClientDiagramServiceImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}