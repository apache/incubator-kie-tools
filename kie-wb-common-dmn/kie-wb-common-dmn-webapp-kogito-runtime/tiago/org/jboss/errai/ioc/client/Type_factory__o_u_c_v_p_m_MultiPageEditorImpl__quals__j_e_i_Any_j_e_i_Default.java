package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorImpl;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorSelectedPageEvent;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorViewImpl;
import org.uberfire.client.workbench.widgets.multipage.MultiPageEditor;

public class Type_factory__o_u_c_v_p_m_MultiPageEditorImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<MultiPageEditorImpl> { public Type_factory__o_u_c_v_p_m_MultiPageEditorImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MultiPageEditorImpl.class, "Type_factory__o_u_c_v_p_m_MultiPageEditorImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MultiPageEditorImpl.class, Object.class, MultiPageEditor.class, IsWidget.class });
  }

  public MultiPageEditorImpl createInstance(final ContextManager contextManager) {
    final MultiPageEditorImpl instance = new MultiPageEditorImpl();
    setIncompleteInstance(instance);
    final Event MultiPageEditorImpl_selectedPageEvent = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { MultiPageEditorSelectedPageEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, MultiPageEditorImpl_selectedPageEvent);
    MultiPageEditorImpl_Event_selectedPageEvent(instance, MultiPageEditorImpl_selectedPageEvent);
    final MultiPageEditorViewImpl MultiPageEditorImpl_view = (MultiPageEditorViewImpl) contextManager.getInstance("Type_factory__o_u_c_v_p_m_MultiPageEditorViewImpl__quals__j_e_i_Any_o_u_c_w_w_m_Multiple");
    registerDependentScopedReference(instance, MultiPageEditorImpl_view);
    MultiPageEditorImpl_MultiPageEditorViewImpl_view(instance, MultiPageEditorImpl_view);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final MultiPageEditorImpl instance) {
    instance.init();
  }

  native static Event MultiPageEditorImpl_Event_selectedPageEvent(MultiPageEditorImpl instance) /*-{
    return instance.@org.uberfire.client.views.pfly.multipage.MultiPageEditorImpl::selectedPageEvent;
  }-*/;

  native static void MultiPageEditorImpl_Event_selectedPageEvent(MultiPageEditorImpl instance, Event<MultiPageEditorSelectedPageEvent> value) /*-{
    instance.@org.uberfire.client.views.pfly.multipage.MultiPageEditorImpl::selectedPageEvent = value;
  }-*/;

  native static MultiPageEditorViewImpl MultiPageEditorImpl_MultiPageEditorViewImpl_view(MultiPageEditorImpl instance) /*-{
    return instance.@org.uberfire.client.views.pfly.multipage.MultiPageEditorImpl::view;
  }-*/;

  native static void MultiPageEditorImpl_MultiPageEditorViewImpl_view(MultiPageEditorImpl instance, MultiPageEditorViewImpl value) /*-{
    instance.@org.uberfire.client.views.pfly.multipage.MultiPageEditorImpl::view = value;
  }-*/;
}