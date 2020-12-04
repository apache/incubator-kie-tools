package org.jboss.errai.ioc.client;

import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.api.definition.adapter.binding.DMNDefinitionSetDefinitionAdapterImpl;
import org.kie.workbench.common.stunner.core.client.definition.adapter.binding.ClientBindableAdapterFunctions;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.definition.adapter.Adapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapterWrapper;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.definition.adapter.HasInheritance;
import org.kie.workbench.common.stunner.core.definition.adapter.PriorityAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterFunctions;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;

public class Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetDefinitionAdapterImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDefinitionSetDefinitionAdapterImpl> { private class Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetDefinitionAdapterImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DMNDefinitionSetDefinitionAdapterImpl implements Proxy<DMNDefinitionSetDefinitionAdapterImpl> {
    private final ProxyHelper<DMNDefinitionSetDefinitionAdapterImpl> proxyHelper = new ProxyHelperImpl<DMNDefinitionSetDefinitionAdapterImpl>("Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetDefinitionAdapterImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DMNDefinitionSetDefinitionAdapterImpl instance) {

    }

    public DMNDefinitionSetDefinitionAdapterImpl asBeanType() {
      return this;
    }

    public void setInstance(final DMNDefinitionSetDefinitionAdapterImpl instance) {
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

    @Override public void init() {
      if (proxyHelper != null) {
        final DMNDefinitionSetDefinitionAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public String getBaseType(Class type) {
      if (proxyHelper != null) {
        final DMNDefinitionSetDefinitionAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getBaseType(type);
        return retVal;
      } else {
        return super.getBaseType(type);
      }
    }

    @Override public String[] getTypes(String baseType) {
      if (proxyHelper != null) {
        final DMNDefinitionSetDefinitionAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final String[] retVal = proxiedInstance.getTypes(baseType);
        return retVal;
      } else {
        return super.getTypes(baseType);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNDefinitionSetDefinitionAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }

    @Override public DefinitionId getId(Object pojo) {
      if (proxyHelper != null) {
        final DMNDefinitionSetDefinitionAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final DefinitionId retVal = proxiedInstance.getId(pojo);
        return retVal;
      } else {
        return super.getId(pojo);
      }
    }

    @Override public String getCategory(Object pojo) {
      if (proxyHelper != null) {
        final DMNDefinitionSetDefinitionAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getCategory(pojo);
        return retVal;
      } else {
        return super.getCategory(pojo);
      }
    }

    @Override public String getTitle(Object pojo) {
      if (proxyHelper != null) {
        final DMNDefinitionSetDefinitionAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getTitle(pojo);
        return retVal;
      } else {
        return super.getTitle(pojo);
      }
    }

    @Override public String getDescription(Object pojo) {
      if (proxyHelper != null) {
        final DMNDefinitionSetDefinitionAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getDescription(pojo);
        return retVal;
      } else {
        return super.getDescription(pojo);
      }
    }

    @Override public String[] getLabels(Object pojo) {
      if (proxyHelper != null) {
        final DMNDefinitionSetDefinitionAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final String[] retVal = proxiedInstance.getLabels(pojo);
        return retVal;
      } else {
        return super.getLabels(pojo);
      }
    }

    @Override public String[] getPropertyFields(Object pojo) {
      if (proxyHelper != null) {
        final DMNDefinitionSetDefinitionAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final String[] retVal = proxiedInstance.getPropertyFields(pojo);
        return retVal;
      } else {
        return super.getPropertyFields(pojo);
      }
    }

    @Override public Optional getProperty(Object pojo, String propertyName) {
      if (proxyHelper != null) {
        final DMNDefinitionSetDefinitionAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final Optional retVal = proxiedInstance.getProperty(pojo, propertyName);
        return retVal;
      } else {
        return super.getProperty(pojo, propertyName);
      }
    }

    @Override public Class getGraphFactoryType(Object pojo) {
      if (proxyHelper != null) {
        final DMNDefinitionSetDefinitionAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getGraphFactoryType(pojo);
        return retVal;
      } else {
        return super.getGraphFactoryType(pojo);
      }
    }

    @Override public String getMetaPropertyField(Object pojo, PropertyMetaTypes metaType) {
      if (proxyHelper != null) {
        final DMNDefinitionSetDefinitionAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getMetaPropertyField(pojo, metaType);
        return retVal;
      } else {
        return super.getMetaPropertyField(pojo, metaType);
      }
    }

    @Override public int getPriority() {
      if (proxyHelper != null) {
        final DMNDefinitionSetDefinitionAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.getPriority();
        return retVal;
      } else {
        return super.getPriority();
      }
    }

    @Override public boolean accepts(Class type) {
      if (proxyHelper != null) {
        final DMNDefinitionSetDefinitionAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.accepts(type);
        return retVal;
      } else {
        return super.accepts(type);
      }
    }
  }
  public Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetDefinitionAdapterImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNDefinitionSetDefinitionAdapterImpl.class, "Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetDefinitionAdapterImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNDefinitionSetDefinitionAdapterImpl.class, DefinitionAdapterWrapper.class, Object.class, DefinitionAdapter.class, PriorityAdapter.class, Adapter.class, HasInheritance.class });
  }

  public DMNDefinitionSetDefinitionAdapterImpl createInstance(final ContextManager contextManager) {
    final BindableAdapterFunctions _functions_1 = (ClientBindableAdapterFunctions) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_d_a_b_ClientBindableAdapterFunctions__quals__j_e_i_Any_j_e_i_Default");
    final StunnerTranslationService _translationService_0 = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    final DMNDefinitionSetDefinitionAdapterImpl instance = new DMNDefinitionSetDefinitionAdapterImpl(_translationService_0, _functions_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DMNDefinitionSetDefinitionAdapterImpl instance) {
    instance.init();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNDefinitionSetDefinitionAdapterImpl> proxyImpl = new Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetDefinitionAdapterImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}