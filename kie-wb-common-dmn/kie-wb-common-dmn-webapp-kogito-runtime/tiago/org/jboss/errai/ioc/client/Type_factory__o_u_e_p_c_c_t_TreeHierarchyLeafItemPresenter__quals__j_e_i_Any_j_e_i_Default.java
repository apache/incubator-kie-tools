package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.preferences.client.central.hierarchy.HierarchyItemPresenter;
import org.uberfire.ext.preferences.client.central.hierarchy.HierarchyLeafItemPresenter;
import org.uberfire.ext.preferences.client.central.tree.TreeHierarchyLeafItemPresenter;
import org.uberfire.ext.preferences.client.central.tree.TreeHierarchyLeafItemPresenter.View;
import org.uberfire.ext.preferences.client.central.tree.TreeHierarchyLeafItemView;
import org.uberfire.ext.preferences.client.event.HierarchyItemSelectedEvent;

public class Type_factory__o_u_e_p_c_c_t_TreeHierarchyLeafItemPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeHierarchyLeafItemPresenter> { public Type_factory__o_u_e_p_c_c_t_TreeHierarchyLeafItemPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TreeHierarchyLeafItemPresenter.class, "Type_factory__o_u_e_p_c_c_t_TreeHierarchyLeafItemPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TreeHierarchyLeafItemPresenter.class, Object.class, HierarchyLeafItemPresenter.class, HierarchyItemPresenter.class });
  }

  public TreeHierarchyLeafItemPresenter createInstance(final ContextManager contextManager) {
    final View _view_0 = (TreeHierarchyLeafItemView) contextManager.getInstance("Type_factory__o_u_e_p_c_c_t_TreeHierarchyLeafItemView__quals__j_e_i_Any_j_e_i_Default");
    final Event<HierarchyItemSelectedEvent> _hierarchyItemSelectedEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { HierarchyItemSelectedEvent.class }, new Annotation[] { });
    final TreeHierarchyLeafItemPresenter instance = new TreeHierarchyLeafItemPresenter(_view_0, _hierarchyItemSelectedEvent_1);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _hierarchyItemSelectedEvent_1);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "itemSelectedEventSubscription", CDI.subscribeLocal("org.uberfire.ext.preferences.client.event.HierarchyItemSelectedEvent", new AbstractCDIEventCallback<HierarchyItemSelectedEvent>() {
      public void fireEvent(final HierarchyItemSelectedEvent event) {
        instance.itemSelectedEvent(event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.preferences.client.event.HierarchyItemSelectedEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((TreeHierarchyLeafItemPresenter) instance, contextManager);
  }

  public void destroyInstanceHelper(final TreeHierarchyLeafItemPresenter instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "itemSelectedEventSubscription", Subscription.class)).remove();
  }
}