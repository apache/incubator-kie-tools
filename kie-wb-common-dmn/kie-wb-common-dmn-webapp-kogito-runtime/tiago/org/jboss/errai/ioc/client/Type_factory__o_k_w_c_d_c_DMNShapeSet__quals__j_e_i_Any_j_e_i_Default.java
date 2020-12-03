package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.DMNShapeSet;
import org.kie.workbench.common.dmn.client.shape.factory.DMNShapeFactory;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.AbstractBindableShapeSet;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;

public class Type_factory__o_k_w_c_d_c_DMNShapeSet__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNShapeSet> { private class Type_factory__o_k_w_c_d_c_DMNShapeSet__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DMNShapeSet implements Proxy<DMNShapeSet> {
    private final ProxyHelper<DMNShapeSet> proxyHelper = new ProxyHelperImpl<DMNShapeSet>("Type_factory__o_k_w_c_d_c_DMNShapeSet__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DMNShapeSet instance) {

    }

    public DMNShapeSet asBeanType() {
      return this;
    }

    public void setInstance(final DMNShapeSet instance) {
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

    @Override protected Class getDefinitionSetClass() {
      if (proxyHelper != null) {
        final DMNShapeSet proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = DMNShapeSet_getDefinitionSetClass(proxiedInstance);
        return retVal;
      } else {
        return super.getDefinitionSetClass();
      }
    }

    @Override protected DefinitionManager getDefinitionManager() {
      if (proxyHelper != null) {
        final DMNShapeSet proxiedInstance = proxyHelper.getInstance(this);
        final DefinitionManager retVal = DMNShapeSet_getDefinitionManager(proxiedInstance);
        return retVal;
      } else {
        return super.getDefinitionManager();
      }
    }

    @Override public DMNShapeFactory getShapeFactory() {
      if (proxyHelper != null) {
        final DMNShapeSet proxiedInstance = proxyHelper.getInstance(this);
        final DMNShapeFactory retVal = proxiedInstance.getShapeFactory();
        return retVal;
      } else {
        return super.getShapeFactory();
      }
    }

    @Override public String getId() {
      if (proxyHelper != null) {
        final DMNShapeSet proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getId();
        return retVal;
      } else {
        return super.getId();
      }
    }

    @Override public String getDescription() {
      if (proxyHelper != null) {
        final DMNShapeSet proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getDescription();
        return retVal;
      } else {
        return super.getDescription();
      }
    }

    @Override public String getDefinitionSetId() {
      if (proxyHelper != null) {
        final DMNShapeSet proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getDefinitionSetId();
        return retVal;
      } else {
        return super.getDefinitionSetId();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNShapeSet proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_DMNShapeSet__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNShapeSet.class, "Type_factory__o_k_w_c_d_c_DMNShapeSet__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNShapeSet.class, AbstractBindableShapeSet.class, Object.class, ShapeSet.class });
  }

  public DMNShapeSet createInstance(final ContextManager contextManager) {
    final DMNShapeFactory _factory_1 = (DMNShapeFactory) contextManager.getInstance("Type_factory__o_k_w_c_d_c_s_f_DMNShapeFactory__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionManager _definitionManager_0 = (ClientDefinitionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_Default");
    final DMNShapeSet instance = new DMNShapeSet(_definitionManager_0, _factory_1);
    registerDependentScopedReference(instance, _factory_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNShapeSet> proxyImpl = new Type_factory__o_k_w_c_d_c_DMNShapeSet__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static Class DMNShapeSet_getDefinitionSetClass(DMNShapeSet instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.DMNShapeSet::getDefinitionSetClass()();
  }-*/;

  public native static DefinitionManager DMNShapeSet_getDefinitionManager(DMNShapeSet instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.DMNShapeSet::getDefinitionManager()();
  }-*/;
}