package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.api.definition.DMNDefinition;
import org.kie.workbench.common.dmn.client.shape.def.DMNShapeDef;
import org.kie.workbench.common.dmn.client.shape.factory.DMNConnectorShapeFactory;
import org.kie.workbench.common.dmn.client.shape.factory.DMNConnectorShapeViewFactory;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefFactory;

public class Type_factory__o_k_w_c_d_c_s_f_DMNConnectorShapeFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNConnectorShapeFactory> { private class Type_factory__o_k_w_c_d_c_s_f_DMNConnectorShapeFactory__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DMNConnectorShapeFactory implements Proxy<DMNConnectorShapeFactory> {
    private final ProxyHelper<DMNConnectorShapeFactory> proxyHelper = new ProxyHelperImpl<DMNConnectorShapeFactory>("Type_factory__o_k_w_c_d_c_s_f_DMNConnectorShapeFactory__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DMNConnectorShapeFactory instance) {

    }

    public DMNConnectorShapeFactory asBeanType() {
      return this;
    }

    public void setInstance(final DMNConnectorShapeFactory instance) {
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

    @Override public Shape newShape(DMNDefinition instance, DMNShapeDef shapeDef) {
      if (proxyHelper != null) {
        final DMNConnectorShapeFactory proxiedInstance = proxyHelper.getInstance(this);
        final Shape retVal = proxiedInstance.newShape(instance, shapeDef);
        return retVal;
      } else {
        return super.newShape(instance, shapeDef);
      }
    }

    @Override public DMNConnectorShapeViewFactory getDMNConnectorShapeViewFactory() {
      if (proxyHelper != null) {
        final DMNConnectorShapeFactory proxiedInstance = proxyHelper.getInstance(this);
        final DMNConnectorShapeViewFactory retVal = proxiedInstance.getDMNConnectorShapeViewFactory();
        return retVal;
      } else {
        return super.getDMNConnectorShapeViewFactory();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNConnectorShapeFactory proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_s_f_DMNConnectorShapeFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNConnectorShapeFactory.class, "Type_factory__o_k_w_c_d_c_s_f_DMNConnectorShapeFactory__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNConnectorShapeFactory.class, Object.class, ShapeDefFactory.class });
  }

  public DMNConnectorShapeFactory createInstance(final ContextManager contextManager) {
    final DMNConnectorShapeViewFactory _dmnConnectorShapeViewFactory_0 = (DMNConnectorShapeViewFactory) contextManager.getInstance("Type_factory__o_k_w_c_d_c_s_f_DMNConnectorShapeViewFactory__quals__j_e_i_Any_j_e_i_Default");
    final DMNConnectorShapeFactory instance = new DMNConnectorShapeFactory(_dmnConnectorShapeViewFactory_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNConnectorShapeFactory> proxyImpl = new Type_factory__o_k_w_c_d_c_s_f_DMNConnectorShapeFactory__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}