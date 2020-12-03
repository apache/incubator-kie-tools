package org.jboss.errai.ioc.client;

import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.api.definition.model.formBuilder.provider.ModuleFormGenerationResourcesProvider;
import org.kie.workbench.common.forms.adf.service.building.FormGenerationResourcesProvider;

public class Type_factory__o_k_w_c_d_a_d_m_f_p_ModuleFormGenerationResourcesProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<ModuleFormGenerationResourcesProvider> { private class Type_factory__o_k_w_c_d_a_d_m_f_p_ModuleFormGenerationResourcesProvider__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ModuleFormGenerationResourcesProvider implements Proxy<ModuleFormGenerationResourcesProvider> {
    private final ProxyHelper<ModuleFormGenerationResourcesProvider> proxyHelper = new ProxyHelperImpl<ModuleFormGenerationResourcesProvider>("Type_factory__o_k_w_c_d_a_d_m_f_p_ModuleFormGenerationResourcesProvider__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ModuleFormGenerationResourcesProvider instance) {

    }

    public ModuleFormGenerationResourcesProvider asBeanType() {
      return this;
    }

    public void setInstance(final ModuleFormGenerationResourcesProvider instance) {
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

    @Override public Map getDefinitionSettings() {
      if (proxyHelper != null) {
        final ModuleFormGenerationResourcesProvider proxiedInstance = proxyHelper.getInstance(this);
        final Map retVal = proxiedInstance.getDefinitionSettings();
        return retVal;
      } else {
        return super.getDefinitionSettings();
      }
    }

    @Override public Map getFieldModifiers() {
      if (proxyHelper != null) {
        final ModuleFormGenerationResourcesProvider proxiedInstance = proxyHelper.getInstance(this);
        final Map retVal = proxiedInstance.getFieldModifiers();
        return retVal;
      } else {
        return super.getFieldModifiers();
      }
    }

    @Override public Map getFieldModifierReferences() {
      if (proxyHelper != null) {
        final ModuleFormGenerationResourcesProvider proxiedInstance = proxyHelper.getInstance(this);
        final Map retVal = proxiedInstance.getFieldModifierReferences();
        return retVal;
      } else {
        return super.getFieldModifierReferences();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ModuleFormGenerationResourcesProvider proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_a_d_m_f_p_ModuleFormGenerationResourcesProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ModuleFormGenerationResourcesProvider.class, "Type_factory__o_k_w_c_d_a_d_m_f_p_ModuleFormGenerationResourcesProvider__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ModuleFormGenerationResourcesProvider.class, Object.class, FormGenerationResourcesProvider.class });
  }

  public ModuleFormGenerationResourcesProvider createInstance(final ContextManager contextManager) {
    final ModuleFormGenerationResourcesProvider instance = new ModuleFormGenerationResourcesProvider();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ModuleFormGenerationResourcesProvider> proxyImpl = new Type_factory__o_k_w_c_d_a_d_m_f_p_ModuleFormGenerationResourcesProvider__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}