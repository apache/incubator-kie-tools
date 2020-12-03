package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.forms.adf.engine.client.formGeneration.ClientFormGenerator;
import org.kie.workbench.common.forms.adf.engine.shared.FormBuildingService;
import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.FormGenerator;
import org.kie.workbench.common.forms.adf.engine.shared.impl.FormBuildingServiceImpl;
import org.kie.workbench.common.forms.model.FormDefinition;

public class Type_factory__o_k_w_c_f_a_e_s_i_FormBuildingServiceImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FormBuildingServiceImpl> { private class Type_factory__o_k_w_c_f_a_e_s_i_FormBuildingServiceImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends FormBuildingServiceImpl implements Proxy<FormBuildingServiceImpl> {
    private final ProxyHelper<FormBuildingServiceImpl> proxyHelper = new ProxyHelperImpl<FormBuildingServiceImpl>("Type_factory__o_k_w_c_f_a_e_s_i_FormBuildingServiceImpl__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_f_a_e_s_i_FormBuildingServiceImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null);
    }

    public void initProxyProperties(final FormBuildingServiceImpl instance) {

    }

    public FormBuildingServiceImpl asBeanType() {
      return this;
    }

    public void setInstance(final FormBuildingServiceImpl instance) {
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

    @Override public FormDefinition generateFormForModel(Object model, FormElementFilter[] filters) {
      if (proxyHelper != null) {
        final FormBuildingServiceImpl proxiedInstance = proxyHelper.getInstance(this);
        final FormDefinition retVal = proxiedInstance.generateFormForModel(model, filters);
        return retVal;
      } else {
        return super.generateFormForModel(model, filters);
      }
    }

    @Override public FormDefinition generateFormForClass(Class clazz, FormElementFilter[] filters) {
      if (proxyHelper != null) {
        final FormBuildingServiceImpl proxiedInstance = proxyHelper.getInstance(this);
        final FormDefinition retVal = proxiedInstance.generateFormForClass(clazz, filters);
        return retVal;
      } else {
        return super.generateFormForClass(clazz, filters);
      }
    }

    @Override public FormDefinition generateFormForClassName(String className, FormElementFilter[] filters) {
      if (proxyHelper != null) {
        final FormBuildingServiceImpl proxiedInstance = proxyHelper.getInstance(this);
        final FormDefinition retVal = proxiedInstance.generateFormForClassName(className, filters);
        return retVal;
      } else {
        return super.generateFormForClassName(className, filters);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final FormBuildingServiceImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_f_a_e_s_i_FormBuildingServiceImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FormBuildingServiceImpl.class, "Type_factory__o_k_w_c_f_a_e_s_i_FormBuildingServiceImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FormBuildingServiceImpl.class, Object.class, FormBuildingService.class });
  }

  public FormBuildingServiceImpl createInstance(final ContextManager contextManager) {
    final FormGenerator _formGenerator_0 = (ClientFormGenerator) contextManager.getInstance("Type_factory__o_k_w_c_f_a_e_c_f_ClientFormGenerator__quals__j_e_i_Any_j_e_i_Default");
    final FormBuildingServiceImpl instance = new FormBuildingServiceImpl(_formGenerator_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_f_a_e_s_i_FormBuildingServiceImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.forms.adf.engine.shared.impl.FormBuildingServiceImpl an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.forms.adf.engine.shared.impl.FormBuildingServiceImpl ([org.kie.workbench.common.forms.adf.engine.shared.formGeneration.FormGenerator])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<FormBuildingServiceImpl> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}