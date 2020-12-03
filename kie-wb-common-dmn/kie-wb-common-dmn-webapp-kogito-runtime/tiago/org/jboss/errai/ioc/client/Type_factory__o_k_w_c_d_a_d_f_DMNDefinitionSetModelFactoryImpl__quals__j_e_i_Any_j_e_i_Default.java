package org.jboss.errai.ioc.client;

import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.api.definition.factory.DMNDefinitionSetModelFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.definition.AbstractTypeDefinitionFactory;
import org.kie.workbench.common.stunner.core.factory.definition.DefinitionFactory;
import org.kie.workbench.common.stunner.core.factory.definition.TypeDefinitionFactory;

public class Type_factory__o_k_w_c_d_a_d_f_DMNDefinitionSetModelFactoryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDefinitionSetModelFactoryImpl> { private class Type_factory__o_k_w_c_d_a_d_f_DMNDefinitionSetModelFactoryImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DMNDefinitionSetModelFactoryImpl implements Proxy<DMNDefinitionSetModelFactoryImpl> {
    private final ProxyHelper<DMNDefinitionSetModelFactoryImpl> proxyHelper = new ProxyHelperImpl<DMNDefinitionSetModelFactoryImpl>("Type_factory__o_k_w_c_d_a_d_f_DMNDefinitionSetModelFactoryImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DMNDefinitionSetModelFactoryImpl instance) {

    }

    public DMNDefinitionSetModelFactoryImpl asBeanType() {
      return this;
    }

    public void setInstance(final DMNDefinitionSetModelFactoryImpl instance) {
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

    @Override public Set getAcceptedClasses() {
      if (proxyHelper != null) {
        final DMNDefinitionSetModelFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final Set retVal = proxiedInstance.getAcceptedClasses();
        return retVal;
      } else {
        return super.getAcceptedClasses();
      }
    }

    @Override public Object build(Class clazz) {
      if (proxyHelper != null) {
        final DMNDefinitionSetModelFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final Object retVal = proxiedInstance.build(clazz);
        return retVal;
      } else {
        return super.build(clazz);
      }
    }

    @Override public boolean accepts(Class type) {
      if (proxyHelper != null) {
        final DMNDefinitionSetModelFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.accepts(type);
        return retVal;
      } else {
        return super.accepts(type);
      }
    }

    @Override public boolean accepts(String id) {
      if (proxyHelper != null) {
        final DMNDefinitionSetModelFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.accepts(id);
        return retVal;
      } else {
        return super.accepts(id);
      }
    }

    @Override public Object build(String id) {
      if (proxyHelper != null) {
        final DMNDefinitionSetModelFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final Object retVal = proxiedInstance.build(id);
        return retVal;
      } else {
        return super.build(id);
      }
    }

    @Override protected Class getClass(String id) {
      if (proxyHelper != null) {
        final DMNDefinitionSetModelFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = AbstractTypeDefinitionFactory_getClass_String(proxiedInstance, id);
        return retVal;
      } else {
        return super.getClass(id);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNDefinitionSetModelFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_a_d_f_DMNDefinitionSetModelFactoryImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNDefinitionSetModelFactoryImpl.class, "Type_factory__o_k_w_c_d_a_d_f_DMNDefinitionSetModelFactoryImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNDefinitionSetModelFactoryImpl.class, AbstractTypeDefinitionFactory.class, Object.class, TypeDefinitionFactory.class, DefinitionFactory.class, org.kie.workbench.common.stunner.core.factory.Factory.class });
  }

  public DMNDefinitionSetModelFactoryImpl createInstance(final ContextManager contextManager) {
    final DMNDefinitionSetModelFactoryImpl instance = new DMNDefinitionSetModelFactoryImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNDefinitionSetModelFactoryImpl> proxyImpl = new Type_factory__o_k_w_c_d_a_d_f_DMNDefinitionSetModelFactoryImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static Class AbstractTypeDefinitionFactory_getClass_String(AbstractTypeDefinitionFactory instance, String a0) /*-{
    return instance.@org.kie.workbench.common.stunner.core.factory.definition.AbstractTypeDefinitionFactory::getClass(Ljava/lang/String;)(a0);
  }-*/;
}