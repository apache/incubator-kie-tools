package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsFactory;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsIndex;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.ImportRecordEngine;

public class Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<IncludedModelsFactory> { private class Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsFactory__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends IncludedModelsFactory implements Proxy<IncludedModelsFactory> {
    private final ProxyHelper<IncludedModelsFactory> proxyHelper = new ProxyHelperImpl<IncludedModelsFactory>("Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsFactory__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsFactory__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null);
    }

    public void initProxyProperties(final IncludedModelsFactory instance) {

    }

    public IncludedModelsFactory asBeanType() {
      return this;
    }

    public void setInstance(final IncludedModelsFactory instance) {
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

    @Override protected String uuidWrapper() {
      if (proxyHelper != null) {
        final IncludedModelsFactory proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = IncludedModelsFactory_uuidWrapper(proxiedInstance);
        return retVal;
      } else {
        return super.uuidWrapper();
      }
    }

    @Override public IncludedModelsIndex getIncludedModelsIndex() {
      if (proxyHelper != null) {
        final IncludedModelsFactory proxiedInstance = proxyHelper.getInstance(this);
        final IncludedModelsIndex retVal = proxiedInstance.getIncludedModelsIndex();
        return retVal;
      } else {
        return super.getIncludedModelsIndex();
      }
    }

    @Override public ImportRecordEngine getRecordEngine() {
      if (proxyHelper != null) {
        final IncludedModelsFactory proxiedInstance = proxyHelper.getInstance(this);
        final ImportRecordEngine retVal = proxiedInstance.getRecordEngine();
        return retVal;
      } else {
        return super.getRecordEngine();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final IncludedModelsFactory proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(IncludedModelsFactory.class, "Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsFactory__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { IncludedModelsFactory.class, Object.class });
  }

  public IncludedModelsFactory createInstance(final ContextManager contextManager) {
    final ImportRecordEngine _recordEngine_0 = (ImportRecordEngine) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_i_p_ImportRecordEngine__quals__j_e_i_Any_j_e_i_Default");
    final IncludedModelsIndex _includedModelsIndex_1 = (IncludedModelsIndex) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsIndex__quals__j_e_i_Any_j_e_i_Default");
    final IncludedModelsFactory instance = new IncludedModelsFactory(_recordEngine_0, _includedModelsIndex_1);
    registerDependentScopedReference(instance, _recordEngine_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsFactory__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsFactory an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsFactory ([org.kie.workbench.common.dmn.client.editors.included.imports.persistence.ImportRecordEngine, org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsIndex])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<IncludedModelsFactory> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static String IncludedModelsFactory_uuidWrapper(IncludedModelsFactory instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsFactory::uuidWrapper()();
  }-*/;
}