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
import org.kie.workbench.common.forms.adf.rendering.FieldRendererTypesProvider;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.ModuleFieldRendererTypesProvider;

public class Type_factory__o_k_w_c_f_d_c_r_r_ModuleFieldRendererTypesProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<ModuleFieldRendererTypesProvider> { private class Type_factory__o_k_w_c_f_d_c_r_r_ModuleFieldRendererTypesProvider__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ModuleFieldRendererTypesProvider implements Proxy<ModuleFieldRendererTypesProvider> {
    private final ProxyHelper<ModuleFieldRendererTypesProvider> proxyHelper = new ProxyHelperImpl<ModuleFieldRendererTypesProvider>("Type_factory__o_k_w_c_f_d_c_r_r_ModuleFieldRendererTypesProvider__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ModuleFieldRendererTypesProvider instance) {

    }

    public ModuleFieldRendererTypesProvider asBeanType() {
      return this;
    }

    public void setInstance(final ModuleFieldRendererTypesProvider instance) {
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

    @Override public Map getFieldTypeRenderers() {
      if (proxyHelper != null) {
        final ModuleFieldRendererTypesProvider proxiedInstance = proxyHelper.getInstance(this);
        final Map retVal = proxiedInstance.getFieldTypeRenderers();
        return retVal;
      } else {
        return super.getFieldTypeRenderers();
      }
    }

    @Override public Map getFieldDefinitionRenderers() {
      if (proxyHelper != null) {
        final ModuleFieldRendererTypesProvider proxiedInstance = proxyHelper.getInstance(this);
        final Map retVal = proxiedInstance.getFieldDefinitionRenderers();
        return retVal;
      } else {
        return super.getFieldDefinitionRenderers();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ModuleFieldRendererTypesProvider proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_f_d_c_r_r_ModuleFieldRendererTypesProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ModuleFieldRendererTypesProvider.class, "Type_factory__o_k_w_c_f_d_c_r_r_ModuleFieldRendererTypesProvider__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ModuleFieldRendererTypesProvider.class, Object.class, FieldRendererTypesProvider.class });
  }

  public ModuleFieldRendererTypesProvider createInstance(final ContextManager contextManager) {
    final ModuleFieldRendererTypesProvider instance = new ModuleFieldRendererTypesProvider();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ModuleFieldRendererTypesProvider> proxyImpl = new Type_factory__o_k_w_c_f_d_c_r_r_ModuleFieldRendererTypesProvider__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}