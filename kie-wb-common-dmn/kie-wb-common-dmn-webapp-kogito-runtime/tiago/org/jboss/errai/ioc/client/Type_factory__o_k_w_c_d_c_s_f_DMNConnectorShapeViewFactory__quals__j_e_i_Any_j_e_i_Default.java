package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.shape.factory.DMNConnectorShapeViewFactory;
import org.kie.workbench.common.dmn.client.shape.view.AssociationView;
import org.kie.workbench.common.dmn.client.shape.view.AuthorityRequirementView;
import org.kie.workbench.common.dmn.client.shape.view.InformationRequirementView;
import org.kie.workbench.common.dmn.client.shape.view.KnowledgeRequirementView;

public class Type_factory__o_k_w_c_d_c_s_f_DMNConnectorShapeViewFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNConnectorShapeViewFactory> { private class Type_factory__o_k_w_c_d_c_s_f_DMNConnectorShapeViewFactory__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DMNConnectorShapeViewFactory implements Proxy<DMNConnectorShapeViewFactory> {
    private final ProxyHelper<DMNConnectorShapeViewFactory> proxyHelper = new ProxyHelperImpl<DMNConnectorShapeViewFactory>("Type_factory__o_k_w_c_d_c_s_f_DMNConnectorShapeViewFactory__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DMNConnectorShapeViewFactory instance) {

    }

    public DMNConnectorShapeViewFactory asBeanType() {
      return this;
    }

    public void setInstance(final DMNConnectorShapeViewFactory instance) {
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

    @Override public AssociationView association(double x1, double y1, double x2, double y2) {
      if (proxyHelper != null) {
        final DMNConnectorShapeViewFactory proxiedInstance = proxyHelper.getInstance(this);
        final AssociationView retVal = proxiedInstance.association(x1, y1, x2, y2);
        return retVal;
      } else {
        return super.association(x1, y1, x2, y2);
      }
    }

    @Override public InformationRequirementView informationRequirement(double x1, double y1, double x2, double y2) {
      if (proxyHelper != null) {
        final DMNConnectorShapeViewFactory proxiedInstance = proxyHelper.getInstance(this);
        final InformationRequirementView retVal = proxiedInstance.informationRequirement(x1, y1, x2, y2);
        return retVal;
      } else {
        return super.informationRequirement(x1, y1, x2, y2);
      }
    }

    @Override public KnowledgeRequirementView knowledgeRequirement(double x1, double y1, double x2, double y2) {
      if (proxyHelper != null) {
        final DMNConnectorShapeViewFactory proxiedInstance = proxyHelper.getInstance(this);
        final KnowledgeRequirementView retVal = proxiedInstance.knowledgeRequirement(x1, y1, x2, y2);
        return retVal;
      } else {
        return super.knowledgeRequirement(x1, y1, x2, y2);
      }
    }

    @Override public AuthorityRequirementView authorityRequirement(double x1, double y1, double x2, double y2) {
      if (proxyHelper != null) {
        final DMNConnectorShapeViewFactory proxiedInstance = proxyHelper.getInstance(this);
        final AuthorityRequirementView retVal = proxiedInstance.authorityRequirement(x1, y1, x2, y2);
        return retVal;
      } else {
        return super.authorityRequirement(x1, y1, x2, y2);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNConnectorShapeViewFactory proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_s_f_DMNConnectorShapeViewFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNConnectorShapeViewFactory.class, "Type_factory__o_k_w_c_d_c_s_f_DMNConnectorShapeViewFactory__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNConnectorShapeViewFactory.class, Object.class });
  }

  public DMNConnectorShapeViewFactory createInstance(final ContextManager contextManager) {
    final DMNConnectorShapeViewFactory instance = new DMNConnectorShapeViewFactory();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNConnectorShapeViewFactory> proxyImpl = new Type_factory__o_k_w_c_d_c_s_f_DMNConnectorShapeViewFactory__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}