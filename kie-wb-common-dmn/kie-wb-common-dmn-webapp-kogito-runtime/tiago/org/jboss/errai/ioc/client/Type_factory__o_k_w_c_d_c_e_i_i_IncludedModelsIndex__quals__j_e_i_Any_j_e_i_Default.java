package org.jboss.errai.ioc.client;

import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.client.editors.included.BaseIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsIndex;

public class Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsIndex__quals__j_e_i_Any_j_e_i_Default extends Factory<IncludedModelsIndex> { private class Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsIndex__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends IncludedModelsIndex implements Proxy<IncludedModelsIndex> {
    private final ProxyHelper<IncludedModelsIndex> proxyHelper = new ProxyHelperImpl<IncludedModelsIndex>("Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsIndex__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final IncludedModelsIndex instance) {

    }

    public IncludedModelsIndex asBeanType() {
      return this;
    }

    public void setInstance(final IncludedModelsIndex instance) {
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

    @Override public void index(BaseIncludedModelActiveRecord includedModel, Import anImport) {
      if (proxyHelper != null) {
        final IncludedModelsIndex proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.index(includedModel, anImport);
      } else {
        super.index(includedModel, anImport);
      }
    }

    @Override public Import getImport(BaseIncludedModelActiveRecord includedModel) {
      if (proxyHelper != null) {
        final IncludedModelsIndex proxiedInstance = proxyHelper.getInstance(this);
        final Import retVal = proxiedInstance.getImport(includedModel);
        return retVal;
      } else {
        return super.getImport(includedModel);
      }
    }

    @Override public Collection getIndexedImports() {
      if (proxyHelper != null) {
        final IncludedModelsIndex proxiedInstance = proxyHelper.getInstance(this);
        final Collection retVal = proxiedInstance.getIndexedImports();
        return retVal;
      } else {
        return super.getIndexedImports();
      }
    }

    @Override public void clear() {
      if (proxyHelper != null) {
        final IncludedModelsIndex proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.clear();
      } else {
        super.clear();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final IncludedModelsIndex proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsIndex__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(IncludedModelsIndex.class, "Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsIndex__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { IncludedModelsIndex.class, Object.class });
  }

  public IncludedModelsIndex createInstance(final ContextManager contextManager) {
    final IncludedModelsIndex instance = new IncludedModelsIndex();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<IncludedModelsIndex> proxyImpl = new Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsIndex__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}