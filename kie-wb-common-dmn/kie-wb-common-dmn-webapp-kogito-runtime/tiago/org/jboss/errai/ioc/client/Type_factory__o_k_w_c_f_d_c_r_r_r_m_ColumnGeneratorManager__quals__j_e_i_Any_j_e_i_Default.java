package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.ColumnGeneratorManager;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.columns.ColumnGenerator;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.columns.impl.StringColumnGenerator;

public class Type_factory__o_k_w_c_f_d_c_r_r_r_m_ColumnGeneratorManager__quals__j_e_i_Any_j_e_i_Default extends Factory<ColumnGeneratorManager> { private class Type_factory__o_k_w_c_f_d_c_r_r_r_m_ColumnGeneratorManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ColumnGeneratorManager implements Proxy<ColumnGeneratorManager> {
    private final ProxyHelper<ColumnGeneratorManager> proxyHelper = new ProxyHelperImpl<ColumnGeneratorManager>("Type_factory__o_k_w_c_f_d_c_r_r_r_m_ColumnGeneratorManager__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_f_d_c_r_r_r_m_ColumnGeneratorManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null);
    }

    public void initProxyProperties(final ColumnGeneratorManager instance) {

    }

    public ColumnGeneratorManager asBeanType() {
      return this;
    }

    public void setInstance(final ColumnGeneratorManager instance) {
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

    @Override protected void init() {
      if (proxyHelper != null) {
        final ColumnGeneratorManager proxiedInstance = proxyHelper.getInstance(this);
        ColumnGeneratorManager_init(proxiedInstance);
      } else {
        super.init();
      }
    }

    @Override public ColumnGenerator getGeneratorByType(String type) {
      if (proxyHelper != null) {
        final ColumnGeneratorManager proxiedInstance = proxyHelper.getInstance(this);
        final ColumnGenerator retVal = proxiedInstance.getGeneratorByType(type);
        return retVal;
      } else {
        return super.getGeneratorByType(type);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ColumnGeneratorManager proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_f_d_c_r_r_r_m_ColumnGeneratorManager__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ColumnGeneratorManager.class, "Type_factory__o_k_w_c_f_d_c_r_r_r_m_ColumnGeneratorManager__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ColumnGeneratorManager.class, Object.class });
  }

  public ColumnGeneratorManager createInstance(final ContextManager contextManager) {
    final ColumnGenerator _defaultColumnGenerator_0 = (StringColumnGenerator) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_r_r_m_c_i_StringColumnGenerator__quals__j_e_i_Any_j_e_i_Default");
    final ColumnGeneratorManager instance = new ColumnGeneratorManager(_defaultColumnGenerator_0);
    registerDependentScopedReference(instance, _defaultColumnGenerator_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ColumnGeneratorManager instance) {
    ColumnGeneratorManager_init(instance);
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_f_d_c_r_r_r_m_ColumnGeneratorManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.ColumnGeneratorManager an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.ColumnGeneratorManager ([org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.columns.ColumnGenerator])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ColumnGeneratorManager> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void ColumnGeneratorManager_init(ColumnGeneratorManager instance) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.ColumnGeneratorManager::init()();
  }-*/;
}