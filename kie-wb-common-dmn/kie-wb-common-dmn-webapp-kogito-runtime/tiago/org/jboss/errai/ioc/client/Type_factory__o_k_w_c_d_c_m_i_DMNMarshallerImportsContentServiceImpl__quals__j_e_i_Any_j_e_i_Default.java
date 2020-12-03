package org.jboss.errai.ioc.client;

import elemental2.promise.Promise;
import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.api.DMNContentService;
import org.kie.workbench.common.dmn.client.marshaller.included.DMNMarshallerImportsContentService;
import org.kie.workbench.common.dmn.client.marshaller.included.DMNMarshallerImportsContentServiceImpl;
import org.uberfire.client.promise.Promises;

public class Type_factory__o_k_w_c_d_c_m_i_DMNMarshallerImportsContentServiceImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNMarshallerImportsContentServiceImpl> { private class Type_factory__o_k_w_c_d_c_m_i_DMNMarshallerImportsContentServiceImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DMNMarshallerImportsContentServiceImpl implements Proxy<DMNMarshallerImportsContentServiceImpl> {
    private final ProxyHelper<DMNMarshallerImportsContentServiceImpl> proxyHelper = new ProxyHelperImpl<DMNMarshallerImportsContentServiceImpl>("Type_factory__o_k_w_c_d_c_m_i_DMNMarshallerImportsContentServiceImpl__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_c_m_i_DMNMarshallerImportsContentServiceImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null, null);
    }

    public void initProxyProperties(final DMNMarshallerImportsContentServiceImpl instance) {

    }

    public DMNMarshallerImportsContentServiceImpl asBeanType() {
      return this;
    }

    public void setInstance(final DMNMarshallerImportsContentServiceImpl instance) {
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

    @Override public Promise loadFile(String fileUri) {
      if (proxyHelper != null) {
        final DMNMarshallerImportsContentServiceImpl proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.loadFile(fileUri);
        return retVal;
      } else {
        return super.loadFile(fileUri);
      }
    }

    @Override public Promise getModelsURIs() {
      if (proxyHelper != null) {
        final DMNMarshallerImportsContentServiceImpl proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.getModelsURIs();
        return retVal;
      } else {
        return super.getModelsURIs();
      }
    }

    @Override public Promise getModelsDMNFilesURIs() {
      if (proxyHelper != null) {
        final DMNMarshallerImportsContentServiceImpl proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.getModelsDMNFilesURIs();
        return retVal;
      } else {
        return super.getModelsDMNFilesURIs();
      }
    }

    @Override public Promise getModelsPMMLFilesURIs() {
      if (proxyHelper != null) {
        final DMNMarshallerImportsContentServiceImpl proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.getModelsPMMLFilesURIs();
        return retVal;
      } else {
        return super.getModelsPMMLFilesURIs();
      }
    }

    @Override public Promise getPMMLDocumentMetadata(String fileUri) {
      if (proxyHelper != null) {
        final DMNMarshallerImportsContentServiceImpl proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.getPMMLDocumentMetadata(fileUri);
        return retVal;
      } else {
        return super.getPMMLDocumentMetadata(fileUri);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNMarshallerImportsContentServiceImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_m_i_DMNMarshallerImportsContentServiceImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNMarshallerImportsContentServiceImpl.class, "Type_factory__o_k_w_c_d_c_m_i_DMNMarshallerImportsContentServiceImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNMarshallerImportsContentServiceImpl.class, Object.class, DMNMarshallerImportsContentService.class });
  }

  public DMNMarshallerImportsContentServiceImpl createInstance(final ContextManager contextManager) {
    final Promises _promises_1 = (Promises) contextManager.getInstance("Type_factory__o_u_c_p_Promises__quals__j_e_i_Any_j_e_i_Default");
    final WorkspaceProjectContext _projectContext_2 = (WorkspaceProjectContext) contextManager.getInstance("Type_factory__o_g_c_s_p_c_c_WorkspaceProjectContext__quals__j_e_i_Any_j_e_i_Default");
    final Caller<DMNContentService> _dmnContentServiceCaller_0 = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { DMNContentService.class }, new Annotation[] { });
    final DMNMarshallerImportsContentServiceImpl instance = new DMNMarshallerImportsContentServiceImpl(_dmnContentServiceCaller_0, _promises_1, _projectContext_2);
    registerDependentScopedReference(instance, _promises_1);
    registerDependentScopedReference(instance, _dmnContentServiceCaller_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_c_m_i_DMNMarshallerImportsContentServiceImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.client.marshaller.included.DMNMarshallerImportsContentServiceImpl an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.client.marshaller.included.DMNMarshallerImportsContentServiceImpl ([org.jboss.errai.common.client.api.Caller, org.uberfire.client.promise.Promises, org.guvnor.common.services.project.client.context.WorkspaceProjectContext])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNMarshallerImportsContentServiceImpl> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}