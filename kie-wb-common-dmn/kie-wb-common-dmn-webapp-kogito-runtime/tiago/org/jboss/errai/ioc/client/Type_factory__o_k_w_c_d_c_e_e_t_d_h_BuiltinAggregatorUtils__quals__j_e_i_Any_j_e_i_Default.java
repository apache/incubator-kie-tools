package org.jboss.errai.ioc.client;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.model.BuiltinAggregator;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.BuiltinAggregatorUtils;

public class Type_factory__o_k_w_c_d_c_e_e_t_d_h_BuiltinAggregatorUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<BuiltinAggregatorUtils> { private class Type_factory__o_k_w_c_d_c_e_e_t_d_h_BuiltinAggregatorUtils__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends BuiltinAggregatorUtils implements Proxy<BuiltinAggregatorUtils> {
    private final ProxyHelper<BuiltinAggregatorUtils> proxyHelper = new ProxyHelperImpl<BuiltinAggregatorUtils>("Type_factory__o_k_w_c_d_c_e_e_t_d_h_BuiltinAggregatorUtils__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final BuiltinAggregatorUtils instance) {

    }

    public BuiltinAggregatorUtils asBeanType() {
      return this;
    }

    public void setInstance(final BuiltinAggregatorUtils instance) {
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

    @Override public List getAllValues() {
      if (proxyHelper != null) {
        final BuiltinAggregatorUtils proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getAllValues();
        return retVal;
      } else {
        return super.getAllValues();
      }
    }

    @Override public String toString(BuiltinAggregator aggregator) {
      if (proxyHelper != null) {
        final BuiltinAggregatorUtils proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.toString(aggregator);
        return retVal;
      } else {
        return super.toString(aggregator);
      }
    }

    @Override public BuiltinAggregator toEnum(String value) {
      if (proxyHelper != null) {
        final BuiltinAggregatorUtils proxiedInstance = proxyHelper.getInstance(this);
        final BuiltinAggregator retVal = proxiedInstance.toEnum(value);
        return retVal;
      } else {
        return super.toEnum(value);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final BuiltinAggregatorUtils proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_e_t_d_h_BuiltinAggregatorUtils__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(BuiltinAggregatorUtils.class, "Type_factory__o_k_w_c_d_c_e_e_t_d_h_BuiltinAggregatorUtils__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { BuiltinAggregatorUtils.class, Object.class });
  }

  public BuiltinAggregatorUtils createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_0 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final BuiltinAggregatorUtils instance = new BuiltinAggregatorUtils(_translationService_0);
    registerDependentScopedReference(instance, _translationService_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<BuiltinAggregatorUtils> proxyImpl = new Type_factory__o_k_w_c_d_c_e_e_t_d_h_BuiltinAggregatorUtils__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}