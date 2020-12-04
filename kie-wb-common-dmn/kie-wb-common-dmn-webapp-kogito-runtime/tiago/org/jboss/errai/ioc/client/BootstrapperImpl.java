package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.EventBus;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.HTMLImageElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLLIElement;
import elemental2.dom.HTMLLabelElement;
import elemental2.dom.HTMLOListElement;
import elemental2.dom.HTMLOptGroupElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLParagraphElement;
import elemental2.dom.HTMLSelectElement;
import elemental2.dom.HTMLTextAreaElement;
import elemental2.dom.HTMLUListElement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.validation.Validator;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import org.appformer.client.context.DefaultEditorContextProviderImpl;
import org.appformer.client.keyboardShortcuts.KeyboardShortcutsApiOpts;
import org.appformer.client.stateControl.registry.Registry;
import org.appformer.client.stateControl.registry.impl.DefaultRegistryImpl;
import org.appformer.kogito.bridge.client.context.impl.KogitoEditorContextProviderImpl;
import org.appformer.kogito.bridge.client.guided.tour.GuidedTourBridge;
import org.appformer.kogito.bridge.client.guided.tour.GuidedTourCustomSelectorPositionProvider;
import org.appformer.kogito.bridge.client.guided.tour.observers.GlobalHTMLObserver;
import org.appformer.kogito.bridge.client.guided.tour.service.GuidedTourService;
import org.appformer.kogito.bridge.client.guided.tour.service.GuidedTourServiceProducer;
import org.appformer.kogito.bridge.client.i18n.I18nApi;
import org.appformer.kogito.bridge.client.i18n.I18nServiceProducer;
import org.appformer.kogito.bridge.client.keyboardshortcuts.KeyboardShortcutsApi;
import org.appformer.kogito.bridge.client.keyboardshortcuts.KeyboardShortcutsServiceProducer;
import org.appformer.kogito.bridge.client.pmmleditor.marshaller.PMMLEditorMarshallerApi;
import org.appformer.kogito.bridge.client.pmmleditor.marshaller.PMMLEditorMarshallerServiceProducer;
import org.appformer.kogito.bridge.client.resource.ResourceContentService;
import org.appformer.kogito.bridge.client.resource.producer.ResourceContentServiceProducer;
import org.appformer.kogito.bridge.client.stateControl.registry.producer.CommandRegistryProducer;
import org.appformer.kogito.bridge.client.workspace.WorkspaceService;
import org.appformer.kogito.bridge.client.workspace.producer.WorkspaceServiceProducer;
import org.guvnor.common.services.project.categories.Decision;
import org.guvnor.common.services.project.categories.Form;
import org.guvnor.common.services.project.categories.Model;
import org.guvnor.common.services.project.categories.Optimization;
import org.guvnor.common.services.project.categories.Process;
import org.guvnor.common.services.project.client.GAVEditor;
import org.guvnor.common.services.project.client.GAVEditorViewImpl;
import org.guvnor.common.services.project.client.POMEditorPanel;
import org.guvnor.common.services.project.client.POMEditorPanelViewImpl;
import org.guvnor.common.services.project.client.ProjectEntryPoint;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.preferences.ProjectScopedResolutionStrategySupplier;
import org.guvnor.common.services.project.client.preferences.SpaceScopedResolutionStrategySupplier;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopupViewImpl;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.client.type.POMResourceType;
import org.guvnor.common.services.project.client.util.POMDefaultOptions;
import org.guvnor.common.services.project.editor.type.POMResourceTypeDefinition;
import org.guvnor.common.services.project.preferences.GAVPreferencesBeanGeneratedImpl;
import org.guvnor.common.services.project.preferences.GeneralPreferencesBeanGeneratedImpl;
import org.guvnor.messageconsole.client.console.AlertsEntryPoint;
import org.guvnor.messageconsole.client.console.MessageConsoleScreen;
import org.guvnor.messageconsole.client.console.MessageConsoleScreenActivity;
import org.guvnor.messageconsole.client.console.MessageConsoleService;
import org.guvnor.messageconsole.client.console.MessageConsoleViewImpl;
import org.guvnor.messageconsole.client.console.widget.button.AlertsButtonMenuItemBuilder;
import org.guvnor.messageconsole.client.console.widget.button.ViewHideAlertsButtonPresenter;
import org.guvnor.messageconsole.client.console.widget.button.ViewHideAlertsButtonView;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.SimpleCheckBox;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.Heading;
import org.jboss.errai.common.client.dom.Image;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.dom.Label;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.dom.NumberInput;
import org.jboss.errai.common.client.dom.Paragraph;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.databinding.client.DataBinderProvider;
import org.jboss.errai.databinding.client.DataBindingModuleBootstrapper;
import org.jboss.errai.databinding.client.ListComponentProvider;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.jboss.errai.enterprise.client.cdi.CDIEventTypeLookup;
import org.jboss.errai.enterprise.client.cdi.EventProvider;
import org.jboss.errai.enterprise.client.cdi.InstanceProvider;
import org.jboss.errai.enterprise.client.cdi.JsTypeEventObserver;
import org.jboss.errai.enterprise.client.cdi.WindowEventObservers;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.api.Disposer;
import org.jboss.errai.ioc.client.api.InitBallot;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.api.builtin.CallerProvider;
import org.jboss.errai.ioc.client.api.builtin.DisposerProvider;
import org.jboss.errai.ioc.client.api.builtin.IOCBeanManagerProvider;
import org.jboss.errai.ioc.client.api.builtin.InitBallotProvider;
import org.jboss.errai.ioc.client.api.builtin.ManagedInstanceProvider;
import org.jboss.errai.ioc.client.api.builtin.RootPanelProvider;
import org.jboss.errai.ioc.client.container.ApplicationScopedContext;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.ContextManagerImpl;
import org.jboss.errai.ioc.client.container.DependentScopeContext;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.JsTypeProvider;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ioc.client.lifecycle.impl.AccessImpl;
import org.jboss.errai.ioc.client.lifecycle.impl.CreationImpl;
import org.jboss.errai.ioc.client.lifecycle.impl.DestructionImpl;
import org.jboss.errai.ioc.client.lifecycle.impl.LifecycleListenerRegistrarImpl;
import org.jboss.errai.ioc.client.lifecycle.impl.StateChangeImpl;
import org.jboss.errai.security.shared.api.identity.BasicUserCache;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.ui.client.local.producer.Elemental2HTMLElementProvider;
import org.jboss.errai.ui.client.local.producer.HTMLElementProvider;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.client.local.spi.TranslationServiceProvider;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.client.widget.ListWidgetProvider;
import org.jboss.errai.ui.client.widget.LocaleListBox;
import org.jboss.errai.ui.client.widget.LocaleSelector;
import org.jboss.errai.ui.shared.ServerTemplateProvider;
import org.jboss.errai.validation.client.ValidatorProvider;
import org.jboss.errai.validation.client.dynamic.DynamicValidator;
import org.kie.workbench.common.dmn.api.DMNAPIEntryPoint;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.adapter.binding.DMNDefinitionSetDefinitionAdapterImpl;
import org.kie.workbench.common.dmn.api.definition.adapter.binding.DMNDefinitionSetDefinitionSetAdapterImpl;
import org.kie.workbench.common.dmn.api.definition.adapter.binding.DMNDefinitionSetDefinitionSetProxyImpl;
import org.kie.workbench.common.dmn.api.definition.adapter.binding.DMNDefinitionSetPropertyAdapterImpl;
import org.kie.workbench.common.dmn.api.definition.adapter.binding.DMNDefinitionSetRuleAdapterImpl;
import org.kie.workbench.common.dmn.api.definition.factory.DMNDefinitionSetModelFactoryImpl;
import org.kie.workbench.common.dmn.api.definition.model.formBuilder.provider.ModuleFormGenerationResourcesProvider;
import org.kie.workbench.common.dmn.api.factory.DMNDiagramFactoryImpl;
import org.kie.workbench.common.dmn.api.factory.DMNGraphFactoryImpl;
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;
import org.kie.workbench.common.dmn.api.property.dmn.dataproviders.ConstraintTypeDataProvider;
import org.kie.workbench.common.dmn.api.resource.DMNDefinitionSetResourceType;
import org.kie.workbench.common.dmn.api.rules.AcyclicDirectedGraphRule;
import org.kie.workbench.common.dmn.api.rules.NoInputNodesInImportedDecisionRule;
import org.kie.workbench.common.dmn.api.rules.SingleConnectorPerTypeGraphRule;
import org.kie.workbench.common.dmn.client.DMNClientEntryPoint;
import org.kie.workbench.common.dmn.client.DMNShapeSet;
import org.kie.workbench.common.dmn.client.api.ReadOnlyProviderImpl;
import org.kie.workbench.common.dmn.client.api.dataobjects.DMNDataObjectsClient;
import org.kie.workbench.common.dmn.client.api.included.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.dmn.client.canvas.controls.actions.DMNDiagramTextPropertyProviderImpl;
import org.kie.workbench.common.dmn.client.canvas.controls.actions.DRGElementTextPropertyProviderImpl;
import org.kie.workbench.common.dmn.client.canvas.controls.actions.TextAnnotationTextPropertyProviderImpl;
import org.kie.workbench.common.dmn.client.canvas.controls.inlineeditor.DMNCanvasInlineTextEditorControl;
import org.kie.workbench.common.dmn.client.canvas.controls.keyboard.DMNCanvasShortcutsControlImpl;
import org.kie.workbench.common.dmn.client.canvas.controls.keyboard.shortcut.AppendDecisionShortcut;
import org.kie.workbench.common.dmn.client.canvas.controls.resize.DecisionServiceMoveDividerControl;
import org.kie.workbench.common.dmn.client.canvas.controls.selection.DomainObjectAwareLienzoMultipleSelectionControl;
import org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNCommonActionsToolboxFactory;
import org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNDeleteNodeToolboxAction;
import org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNEditBusinessKnowledgeModelToolboxAction;
import org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNEditDRDToolboxAction;
import org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNEditDecisionToolboxAction;
import org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNFlowActionsToolboxFactory;
import org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNToolboxControl;
import org.kie.workbench.common.dmn.client.commands.clone.DMNDeepCloneProcess;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.common.BoxedExpressionHelper;
import org.kie.workbench.common.dmn.client.components.palette.DMNPaletteDefinitionBuilder;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItemsProvider;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorObserver;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenter;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenterActivity;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorView;
import org.kie.workbench.common.dmn.client.docks.navigator.common.CanvasFocusUtils;
import org.kie.workbench.common.dmn.client.docks.navigator.common.LazyCanvasFocusUtils;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSessionState;
import org.kie.workbench.common.dmn.client.docks.navigator.factories.DecisionNavigatorBaseItemFactory;
import org.kie.workbench.common.dmn.client.docks.navigator.factories.DecisionNavigatorItemFactory;
import org.kie.workbench.common.dmn.client.docks.navigator.factories.DecisionNavigatorNestedItemFactory;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentFilter;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponents;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsItem;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsItemView;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsView;
import org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreePresenter;
import org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView;
import org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView.TreeItem;
import org.kie.workbench.common.dmn.client.docks.preview.PreviewDiagramScreen;
import org.kie.workbench.common.dmn.client.docks.preview.PreviewDiagramScreenActivity;
import org.kie.workbench.common.dmn.client.docks.preview.PreviewDiagramScreenView;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessages;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView;
import org.kie.workbench.common.dmn.client.editors.common.page.DMNPageView;
import org.kie.workbench.common.dmn.client.editors.contextmenu.ContextMenu;
import org.kie.workbench.common.dmn.client.editors.contextmenu.ContextMenuView;
import org.kie.workbench.common.dmn.client.editors.documentation.DMNDocumentationView;
import org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinkItem;
import org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinksWidget;
import org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationDRDsFactory;
import org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationServiceImpl;
import org.kie.workbench.common.dmn.client.editors.documentation.common.HTMLDownloadHelper;
import org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUriPopoverImpl;
import org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverViewImpl;
import org.kie.workbench.common.dmn.client.editors.drd.DRDContextMenu;
import org.kie.workbench.common.dmn.client.editors.drd.DRDContextMenuService;
import org.kie.workbench.common.dmn.client.editors.drd.DRDNameChangerView;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorControlImpl;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorViewImpl;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitionsProducer;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableEditorDefinitionEnricher;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.BuiltinAggregatorUtils;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyPopoverImpl;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyPopoverViewImpl;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.FunctionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.kindselector.KindPopoverImpl;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.kindselector.KindPopoverViewImpl;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.ParameterViewImpl;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.ParametersPopoverImpl;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.ParametersPopoverViewImpl;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.FunctionSupplementaryGridEditorDefinitionsProducer;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.java.JavaFunctionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml.PMMLDocumentMetadataProvider;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml.PMMLFunctionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml.document.LiteralExpressionPMMLDocumentEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml.model.LiteralExpressionPMMLDocumentModelEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.invocation.InvocationEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.list.ListEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.relation.RelationEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.selector.UndefinedExpressionSelectorPopoverImpl;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.selector.UndefinedExpressionSelectorPopoverViewImpl;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPage;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPagePresenter;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPageState;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPageView;
import org.kie.workbench.common.dmn.client.editors.included.common.IncludedModelsContext;
import org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardComponent;
import org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardComponentContentView;
import org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardsGridComponent;
import org.kie.workbench.common.dmn.client.editors.included.grid.DefaultCardComponent;
import org.kie.workbench.common.dmn.client.editors.included.grid.DefaultCardComponentContentView;
import org.kie.workbench.common.dmn.client.editors.included.grid.PMMLCardComponent;
import org.kie.workbench.common.dmn.client.editors.included.grid.PMMLCardComponentContentView;
import org.kie.workbench.common.dmn.client.editors.included.grid.empty.DMNCardsEmptyStateView;
import org.kie.workbench.common.dmn.client.editors.included.imports.ImportFactory;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsFactory;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsIndex;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsPageStateProviderImpl;
import org.kie.workbench.common.dmn.client.editors.included.imports.messages.IncludedModelErrorMessageFactory;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.DMNIncludedModelHandler;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.DefinitionsHandler;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.ImportRecordEngine;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.ItemDefinitionHandler;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.PMMLIncludedModelHandler;
import org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModal;
import org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModalView;
import org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdown;
import org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdownItemsProvider;
import org.kie.workbench.common.dmn.client.editors.search.DMNDataTypesSubIndex;
import org.kie.workbench.common.dmn.client.editors.search.DMNEditorSearchIndex;
import org.kie.workbench.common.dmn.client.editors.search.DMNGraphSubIndex;
import org.kie.workbench.common.dmn.client.editors.search.DMNGridHelper;
import org.kie.workbench.common.dmn.client.editors.search.DMNGridSubIndex;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePickerWidget;
import org.kie.workbench.common.dmn.client.editors.types.DataTypesPage;
import org.kie.workbench.common.dmn.client.editors.types.QNameConverter;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverImpl;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverViewImpl;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManagerStackStore;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeUtils;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.editors.types.common.ScrollHelper;
import org.kie.workbench.common.dmn.client.editors.types.common.errors.DataTypeNameIsBlankErrorMessage;
import org.kie.workbench.common.dmn.client.editors.types.common.errors.DataTypeNameIsDefaultTypeMessage;
import org.kie.workbench.common.dmn.client.editors.types.common.errors.DataTypeNameIsInvalidErrorMessage;
import org.kie.workbench.common.dmn.client.editors.types.common.errors.DataTypeNameIsNotUniqueErrorMessage;
import org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModal;
import org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView;
import org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeList;
import org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItem;
import org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItemView;
import org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListSubItem;
import org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListSubItemView;
import org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListView;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListHighlightHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeSelect;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeSelectView;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeStackHash;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.SmallSwitchComponent;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.SmallSwitchComponentView;
import org.kie.workbench.common.dmn.client.editors.types.listview.confirmation.DataTypeConfirmation;
import org.kie.workbench.common.dmn.client.editors.types.listview.confirmation.DataTypeHasFieldsWarningMessage;
import org.kie.workbench.common.dmn.client.editors.types.listview.confirmation.ReferencedDataTypeWarningMessage;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraint;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModal;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintView;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.ConstraintPlaceholderHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.TypedValueComponentSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common.BaseSelectorView;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.DateSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.DateSelectorView;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.DateValueFormatter;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.time.DateTimeSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.time.DateTimeSelectorView;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.time.DateTimeValueConverter;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time.DayTimeSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time.DayTimeSelectorView;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time.DayTimeValueConverter;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.generic.GenericSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.number.NumberSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.string.StringSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelectorView;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePicker;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeValueFormatter;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeZoneProvider;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.years.months.YearsMonthsSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.years.months.YearsMonthsSelectorView;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.years.months.YearsMonthsValueConverter;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.DataTypeConstraintEnumeration;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.DataTypeConstraintEnumerationView;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItem;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.expression.DataTypeConstraintExpression;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.expression.DataTypeConstraintExpressionView;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range.DataTypeConstraintRange;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range.DataTypeConstraintRangeView;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandler;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListComponent;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListComponentView;
import org.kie.workbench.common.dmn.client.editors.types.listview.validation.DataTypeNameFormatValidator;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionRecordEngine;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.DataTypeCreateHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.DataTypeDestroyHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.DataTypeUpdateHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.ItemDefinitionCreateHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.ItemDefinitionDestroyHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.ItemDefinitionUpdateHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.common.PropertiesPanelNotifier;
import org.kie.workbench.common.dmn.client.editors.types.persistence.validation.DataTypeNameValidator;
import org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBar;
import org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBarView;
import org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchEngine;
import org.kie.workbench.common.dmn.client.editors.types.shortcuts.DataTypeListShortcuts;
import org.kie.workbench.common.dmn.client.editors.types.shortcuts.DataTypeListShortcutsView;
import org.kie.workbench.common.dmn.client.editors.types.shortcuts.DataTypeShortcuts;
import org.kie.workbench.common.dmn.client.graph.DMNElementsSynchronizer;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.marshaller.DMNMarshallerService;
import org.kie.workbench.common.dmn.client.marshaller.common.DMNDiagramElementsUtils;
import org.kie.workbench.common.dmn.client.marshaller.included.DMNIncludedNodeFactory;
import org.kie.workbench.common.dmn.client.marshaller.included.DMNMarshallerImportsClientHelper;
import org.kie.workbench.common.dmn.client.marshaller.included.DMNMarshallerImportsContentServiceImpl;
import org.kie.workbench.common.dmn.client.marshaller.included.DMNMarshallerImportsService;
import org.kie.workbench.common.dmn.client.marshaller.marshall.DMNMarshaller;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.DMNUnmarshaller;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes.NodeConnector;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes.NodeEntriesFactory;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes.StunnerConverter;
import org.kie.workbench.common.dmn.client.property.dmn.DocumentationLinksFieldProvider;
import org.kie.workbench.common.dmn.client.property.dmn.DocumentationLinksFieldRenderer;
import org.kie.workbench.common.dmn.client.property.dmn.NameFieldProvider;
import org.kie.workbench.common.dmn.client.property.dmn.NameFieldRenderer;
import org.kie.workbench.common.dmn.client.property.dmn.QNameFieldProvider;
import org.kie.workbench.common.dmn.client.property.dmn.QNameFieldRenderer;
import org.kie.workbench.common.dmn.client.resources.DMNDecisionServiceSVGViewFactoryImpl;
import org.kie.workbench.common.dmn.client.resources.DMNSVGViewFactoryImpl;
import org.kie.workbench.common.dmn.client.session.CommandRegistryHolder;
import org.kie.workbench.common.dmn.client.session.DMNCanvasHandler;
import org.kie.workbench.common.dmn.client.session.DMNCanvasShortcutsControlProxy;
import org.kie.workbench.common.dmn.client.session.DMNEditorSession;
import org.kie.workbench.common.dmn.client.session.DMNViewerSession;
import org.kie.workbench.common.dmn.client.session.NodeTextSetter;
import org.kie.workbench.common.dmn.client.session.RegistryProvider;
import org.kie.workbench.common.dmn.client.session.presenters.impl.DMNSessionPreview;
import org.kie.workbench.common.dmn.client.shape.DMNPictureProvider;
import org.kie.workbench.common.dmn.client.shape.factory.DMNConnectorShapeFactory;
import org.kie.workbench.common.dmn.client.shape.factory.DMNConnectorShapeViewFactory;
import org.kie.workbench.common.dmn.client.shape.factory.DMNDecisionServiceShapeFactory;
import org.kie.workbench.common.dmn.client.shape.factory.DMNShapeFactory;
import org.kie.workbench.common.dmn.client.validation.DiagramElementNameProviderImpl;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELInitializer;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELVariableSuggestions;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCacheImpl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlImpl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsViewImpl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelector;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorDividerItemViewImpl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorHeaderItemViewImpl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorTextItemViewImpl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorViewImpl;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayerControlImpl;
import org.kie.workbench.common.dmn.client.widgets.layer.MousePanMediatorControlImpl;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanelContainer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanelControlImpl;
import org.kie.workbench.common.dmn.client.widgets.toolbar.DMNEditorToolbar;
import org.kie.workbench.common.dmn.client.widgets.toolbar.DMNGraphProcessor;
import org.kie.workbench.common.dmn.client.widgets.toolbar.DMNLayoutHelper;
import org.kie.workbench.common.dmn.client.widgets.toolbar.DMNPerformAutomaticLayoutCommand;
import org.kie.workbench.common.dmn.client.widgets.toolbar.DMNPerformAutomaticLayoutToolbarCommand;
import org.kie.workbench.common.dmn.client.widgets.toolbar.DMNSugiyamaLayoutService;
import org.kie.workbench.common.dmn.client.widgets.toolbar.DMNViewerToolbar;
import org.kie.workbench.common.dmn.showcase.client.editor.DMNDiagramEditor;
import org.kie.workbench.common.dmn.showcase.client.editor.DMNDiagramEditorActivity;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.DMNKogitoCommonEntryPoint;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.docks.KogitoDecisionNavigatorDock;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.docks.KogitoPreviewDiagramDock;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.docks.KogitoPropertiesDock;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.DMNEditorMenuItemsBuilder;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.DMNEditorMenuSessionItems;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.editors.documentation.DMNDocumentationViewButtonsVisibilitySupplier;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.editors.documentation.common.DMNDocumentationFactory;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.included.DMNMarshallerImportsContentServiceKogitoImpl;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.services.DMNClientDiagramServiceImpl;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.services.DMNClientServicesProxyImpl;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.services.TimeZonesProvider;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.session.DMNEditorSessionCommands;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.session.KogitoDMNCanvasShortcutsControlImpl;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.session.command.SaveDiagramSessionCommand;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.GuidedTourBridgeInitializer;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.common.GuidedTourUtils;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.observers.GuidedTourGraphObserver;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.observers.GuidedTourGridObserver;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.providers.GraphElementsPositionProviderFactory;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.providers.HTMLElementsPositionProviderFactory;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.tutorial.DMNTutorial;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.tutorial.DMNTutorialView;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIAlignmentKind;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITAssociationDirection;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITBuiltinAggregator;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecisionTableOrientation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITFunctionKind;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITHitPolicy;
import org.kie.workbench.common.forms.adf.engine.client.formGeneration.ClientFieldElementProcessor;
import org.kie.workbench.common.forms.adf.engine.client.formGeneration.ClientFormGenerator;
import org.kie.workbench.common.forms.adf.engine.client.formGeneration.ClientMetaDataEntryManager;
import org.kie.workbench.common.forms.adf.engine.client.formGeneration.util.ClientPropertuValueExtractor;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.layout.LayoutGenerator;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.HasMaxLengthFieldInitializer;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.HasPlaceHolderFieldInitializer;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.HasRowsFieldInitializer;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.TextAreaFieldInitializer;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.MultipleSubFormFieldInitializer;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.selectors.ListBoxFieldInitializer;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.selectors.SelectorFieldInitilizer;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.slider.DoubleSliderFieldInitializer;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.slider.IntegerSliderFieldInitializer;
import org.kie.workbench.common.forms.adf.engine.shared.impl.FormBuildingServiceImpl;
import org.kie.workbench.common.forms.common.rendering.client.FormWidgetsEntryPoint;
import org.kie.workbench.common.forms.common.rendering.client.widgets.decimalBox.DecimalBox;
import org.kie.workbench.common.forms.common.rendering.client.widgets.decimalBox.DecimalBoxViewImpl;
import org.kie.workbench.common.forms.common.rendering.client.widgets.integerBox.IntegerBox;
import org.kie.workbench.common.forms.common.rendering.client.widgets.integerBox.IntegerBoxViewImpl;
import org.kie.workbench.common.forms.common.rendering.client.widgets.picture.PictureInput;
import org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidget;
import org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidgetViewImpl;
import org.kie.workbench.common.forms.common.rendering.client.widgets.typeahead.BindableTypeAhead;
import org.kie.workbench.common.forms.common.rendering.client.widgets.typeahead.BindableTypeAheadViewImpl;
import org.kie.workbench.common.forms.crud.client.CrudComponentEntryPoint;
import org.kie.workbench.common.forms.crud.client.component.CrudComponent;
import org.kie.workbench.common.forms.crud.client.component.CrudComponentViewImpl;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.embedded.EmbeddedFormDisplayer;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.embedded.EmbeddedFormDisplayerViewImpl;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.modal.ModalFormDisplayer;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.modal.ModalFormDisplayerViewImpl;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRendererViewImpl;
import org.kie.workbench.common.forms.dynamic.client.DynamicRendererEntryPoint;
import org.kie.workbench.common.forms.dynamic.client.config.ClientSelectorDataProviderManager;
import org.kie.workbench.common.forms.dynamic.client.helper.MapModelBindingHelper;
import org.kie.workbench.common.forms.dynamic.client.init.FormHandlerGeneratorManager;
import org.kie.workbench.common.forms.dynamic.client.init.impl.DynamicFormHandlerGenerator;
import org.kie.workbench.common.forms.dynamic.client.init.impl.StaticFormHandlerGenerator;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldLayoutComponent;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRendererManagerImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.FormGeneratorDriver;
import org.kie.workbench.common.forms.dynamic.client.rendering.FormLayoutGenerator;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.checkbox.CheckBoxFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.checkbox.CheckBoxFormGroupViewImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.configError.ConfigErrorDisplayer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.configError.ConfigErrorDisplayerViewImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroupViewImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupViewImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroupViewImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.slider.SliderFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.slider.SliderFormGroupViewImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.help.FieldHelp;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.help.FieldHelpViewImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.label.FieldLabel;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.label.FieldLabelViewImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.required.FieldRequired;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.required.FieldRequiredViewImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.CheckBoxFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.DecimalBoxFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.IntegerBoxFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.SliderFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.TextAreaFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.TextBoxFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.DatePickerFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.input.DatePickerWrapper;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.input.DatePickerWrapperViewImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.MultipleInputFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.MultipleInput;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.MultipleInputViewImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponent;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponentViewImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.BooleanEditableColumnGenerator;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.CharacterEditableColumnGenerator;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.DateEditableColumnGenerator;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.DateTimePickerCell;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.DateTimePickerPresenter;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.DateTimePickerPresenterViewImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.EditableColumnGeneratorManagerImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.StringEditableColumnGenerator;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.numeric.decimals.BigDecimalEditableColumnGenerator;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.numeric.decimals.DoubleEditableColumnGenerator;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.numeric.decimals.FloatEditableColumnGenerator;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.numeric.integers.BigIntegerEditableColumnGenerator;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.numeric.integers.ByteEditableColumnGenerator;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.numeric.integers.IntegerEditableColumnGenerator;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.numeric.integers.LongEditableColumnGenerator;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.numeric.integers.ShortEditableColumnGenerator;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.selector.MultipleSelectorFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.selector.input.MultipleSelectorInput;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.selector.input.MultipleSelectorInputViewImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.picture.PictureFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.ColumnGeneratorManager;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.MultipleSubFormFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.MultipleSubFormWidget;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.binding.DynamicBindingHelper;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.binding.StaticBindingHelper;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.columns.impl.BooleanColumnGenerator;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.columns.impl.StringColumnGenerator;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.subform.SubFormFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.subform.widget.SubFormWidget;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.listBox.CharacterListBoxFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.listBox.DecimalListBoxFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.listBox.EnumListBoxFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.listBox.IntegerListBoxFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.listBox.StringListBoxFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.radioGroup.CharacterRadioGroupFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.radioGroup.DecimalRadioGroupFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.radioGroup.IntegerRadioGroupFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.radioGroup.StringRadioGroupFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.impl.FormsElementWrapperWidgetUtilImpl;
import org.kie.workbench.common.forms.dynamic.client.service.ClientFieldManagerImpl;
import org.kie.workbench.common.forms.dynamic.service.shared.adf.DynamicFormModelGenerator;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.provider.CheckBoxFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.provider.DatePickerFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.provider.DecimalBoxFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.image.provider.PictureFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.provider.IntegerBoxFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.MultipleInputProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.MultipleSelectorProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.provider.ListBoxFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.provider.RadioGroupFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.provider.SliderFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.provider.TextAreaFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.provider.TextBoxFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.provider.MultipleSubFormFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.provider.SubFormFieldProvider;
import org.kie.workbench.common.forms.processing.engine.handling.ProcessingEngineEntryPoint;
import org.kie.workbench.common.forms.processing.engine.handling.impl.DefaultModelValidator;
import org.kie.workbench.common.forms.processing.engine.handling.impl.FieldChangeHandlerManagerImpl;
import org.kie.workbench.common.forms.processing.engine.handling.impl.FieldStateValidatorImpl;
import org.kie.workbench.common.forms.processing.engine.handling.impl.FormHandlerImpl;
import org.kie.workbench.common.forms.processing.engine.handling.impl.FormValidatorImpl;
import org.kie.workbench.common.forms.service.shared.meta.processing.impl.processors.FieldLabelEntryProcessor;
import org.kie.workbench.common.forms.service.shared.meta.processing.impl.processors.FieldPlaceHolderEntryProcessor;
import org.kie.workbench.common.forms.service.shared.meta.processing.impl.processors.FieldReadOnlyEntryProcessor;
import org.kie.workbench.common.forms.service.shared.meta.processing.impl.processors.FieldRequiredEntryProcessor;
import org.kie.workbench.common.forms.service.shared.meta.processing.impl.processors.FieldTypeEntryProcessor;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerViewImpl;
import org.kie.workbench.common.kogito.webapp.base.client.perspectives.AuthoringPerspective;
import org.kie.workbench.common.kogito.webapp.base.client.perspectives.AuthoringPerspectiveActivity;
import org.kie.workbench.common.kogito.webapp.base.client.perspectives.PerspectiveConfiguration;
import org.kie.workbench.common.kogito.webapp.base.client.workarounds.IsKogito;
import org.kie.workbench.common.kogito.webapp.base.client.workarounds.KogitoResourceContentService;
import org.kie.workbench.common.kogito.webapp.base.shared.PreferenceScopeResolutionStrategyMock;
import org.kie.workbench.common.kogito.webapp.base.shared.PreferenceScopeTypesMock;
import org.kie.workbench.common.profile.api.preferences.ProfilePreferencesBeanGeneratedImpl;
import org.kie.workbench.common.services.shared.messageconsole.MessageConsoleWhiteListImpl;
import org.kie.workbench.common.services.shared.preferences.config.WorkbenchPreferenceScopeResolutionStrategiesImpl;
import org.kie.workbench.common.services.shared.preferences.config.WorkbenchPreferenceScopeResolutionStrategy;
import org.kie.workbench.common.services.shared.preferences.config.WorkbenchPreferenceScopeTypes;
import org.kie.workbench.common.services.shared.resources.WorkbenchActivities;
import org.kie.workbench.common.stunner.client.lienzo.StunnerLienzoCore;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.client.lienzo.canvas.command.LienzoCanvasCommandFactory;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.ConnectionAcceptorControlImpl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.ContainmentAcceptorControlImpl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.ControlPointControlImpl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.DockingAcceptorControlImpl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.LienzoMediatorsControl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.LienzoMultipleSelectionControl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.LienzoSelectionControl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.LocationControlImpl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.ResizeControlImpl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.export.LienzoCanvasExport;
import org.kie.workbench.common.stunner.client.lienzo.canvas.index.bounds.CanvasBoundsIndexerImpl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvasView;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresLayer;
import org.kie.workbench.common.stunner.client.lienzo.components.LienzoImageStripLoader;
import org.kie.workbench.common.stunner.client.lienzo.components.LienzoImageStrips;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderers;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoImageStripGlyphRenderer;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoPictureGlyphRenderer;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoShapeGlyphRenderer;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoSvgDataUriGlyphRenderer;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.ShapeGlyphDragHandler;
import org.kie.workbench.common.stunner.client.lienzo.components.mediators.LienzoCanvasMediators;
import org.kie.workbench.common.stunner.client.lienzo.components.mediators.LienzoPanelMediators;
import org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelector;
import org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorItem;
import org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorPresenter;
import org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView;
import org.kie.workbench.common.stunner.client.lienzo.components.proxies.LienzoConnectorProxyView;
import org.kie.workbench.common.stunner.client.lienzo.components.proxies.LienzoNodeProxyView;
import org.kie.workbench.common.stunner.client.lienzo.components.toolbox.actions.CommonActionsToolboxView;
import org.kie.workbench.common.stunner.client.lienzo.components.toolbox.actions.FlowActionsToolboxView;
import org.kie.workbench.common.stunner.client.lienzo.components.toolbox.actions.MorphActionsToolboxView;
import org.kie.workbench.common.stunner.client.lienzo.components.views.AlertView;
import org.kie.workbench.common.stunner.client.lienzo.components.views.LienzoCanvasNotification;
import org.kie.workbench.common.stunner.client.lienzo.components.views.LienzoCanvasNotificationView;
import org.kie.workbench.common.stunner.client.lienzo.components.views.LienzoTextTooltip;
import org.kie.workbench.common.stunner.client.lienzo.util.LienzoPanelUtils;
import org.kie.workbench.common.stunner.client.lienzo.wires.StunnerWiresControlFactory;
import org.kie.workbench.common.stunner.client.lienzo.wires.StunnerWiresHandlerFactory;
import org.kie.workbench.common.stunner.client.lienzo.wires.WiresManagerFactoryImpl;
import org.kie.workbench.common.stunner.client.widgets.StunnerWidgetsEntryPoint;
import org.kie.workbench.common.stunner.client.widgets.canvas.PreviewLienzoPanel;
import org.kie.workbench.common.stunner.client.widgets.canvas.ScalableLienzoPanel;
import org.kie.workbench.common.stunner.client.widgets.canvas.ScrollableLienzoPanel;
import org.kie.workbench.common.stunner.client.widgets.canvas.StunnerLienzoBoundsPanel;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.BS3IconTypeGlyphRenderer;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.DOMGlyphRenderers;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.ElementShapeGlyphRenderer;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.ImageElementGlyphRenderer;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.ImageStripDOMGlyphRenderer;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.SvgElementGlyphRenderer;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.diagrams.DiagramNavigatorItemImpl;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.diagrams.DiagramsNavigatorImpl;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.item.NavigatorThumbnailItemView;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.view.BootstrapNavigatorView;
import org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorer;
import org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorerView;
import org.kie.workbench.common.stunner.client.widgets.inlineeditor.InlineTextEditorBoxImpl;
import org.kie.workbench.common.stunner.client.widgets.inlineeditor.InlineTextEditorBoxViewImpl;
import org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopup;
import org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.MenuDevCommandsBuilder;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.ShapeSetsMenuItemsBuilder;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.impl.ChangeProfileDevCommand;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.impl.ClearCommandHistoryDevCommand;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.impl.LogBoundingBoxDevCommand;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.impl.LogBoundsDevCommand;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.impl.LogCommandHistoryDevCommand;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.impl.LogDefinitionDevCommand;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.impl.LogGraphDevCommand;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.impl.LogMagnetsDevCommand;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.impl.LogNodeEdgesDevCommand;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.impl.LogSelectedEdgeDevCommand;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.impl.LogSelectedItemsDevCommand;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.impl.LogSessionDevCommand;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.impl.SwitchLogLevelDevCommand;
import org.kie.workbench.common.stunner.client.widgets.notification.NotificationsObserver;
import org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidgetImpl;
import org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidgetViewImpl;
import org.kie.workbench.common.stunner.client.widgets.palette.DefaultPaletteFactory;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidgetViewImpl;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.group.DefinitionPaletteGroupWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.group.DefinitionPaletteGroupWidgetViewImpl;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidgetViewImpl;
import org.kie.workbench.common.stunner.client.widgets.palette.collapsed.CollapsedDefinitionPaletteItemWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.collapsed.CollapsedDefinitionPaletteItemWidgetViewImpl;
import org.kie.workbench.common.stunner.client.widgets.popups.PopupUtil;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl.DefaultDiagramEditor;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl.DefaultDiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl.DiagramLoader;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionCardinalityStateHandler;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionContainer;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorImpl;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPresenterView;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPreviewImpl;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerImpl;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerPresenter;
import org.kie.workbench.common.stunner.client.widgets.profile.ProfileSelector;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ClearToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.CopyToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.CutToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.DeleteSelectionToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToBpmnToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToJpgToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToPdfToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToPngToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToSvgToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.PasteToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.PerformAutomaticLayoutToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.RedoToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.SaveToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.SwitchGridToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.UndoToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ValidateToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.VisitGraphToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.DefaultToolbarView;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.ManagedEditorToolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.ManagedToolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.ManagedViewerToolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.item.ToolbarItem;
import org.kie.workbench.common.stunner.client.widgets.toolbar.item.ToolbarItemView;
import org.kie.workbench.common.stunner.client.widgets.views.AnimatedFloatingWidgetView;
import org.kie.workbench.common.stunner.client.widgets.views.ImageElementRendererViewImpl;
import org.kie.workbench.common.stunner.client.widgets.views.LoadingBox;
import org.kie.workbench.common.stunner.client.widgets.views.LoadingBoxView;
import org.kie.workbench.common.stunner.client.widgets.views.SelectorImpl;
import org.kie.workbench.common.stunner.client.widgets.views.SelectorView;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetElementRendererViewImpl;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperViewImpl;
import org.kie.workbench.common.stunner.client.widgets.views.session.EmptyStateView;
import org.kie.workbench.common.stunner.client.widgets.views.session.NoCanvasView;
import org.kie.workbench.common.stunner.client.widgets.views.session.ScreenErrorViewImpl;
import org.kie.workbench.common.stunner.client.widgets.views.session.ScreenPanelViewImpl;
import org.kie.workbench.common.stunner.core.StunnerCoreEntryPoint;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ClientProfileManager;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManagerImpl;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandlerImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.DefaultTextPropertyProviderImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactoryImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.EdgeBuilderControlImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.ElementBuilderControlImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.NodeBuilderControlImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.ObserverBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.ClipboardControlCleaner;
import org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.LocalClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor.CanvasInlineTextEditorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyEventHandlerImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControlImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.DefaultToolboxControl;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasHighlight;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.command.ApplicationCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManagerImpl;
import org.kie.workbench.common.stunner.core.client.command.MouseRequestLifecycle;
import org.kie.workbench.common.stunner.core.client.command.RedoCommandHandler;
import org.kie.workbench.common.stunner.core.client.command.RegistryAwareCommandManager;
import org.kie.workbench.common.stunner.core.client.components.layout.LayoutHelper;
import org.kie.workbench.common.stunner.core.client.components.layout.OpenDiagramLayoutExecutor;
import org.kie.workbench.common.stunner.core.client.components.palette.CollapsedPaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.ExpandedPaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.client.components.proxies.ConnectorProxy;
import org.kie.workbench.common.stunner.core.client.components.proxies.ElementProxy;
import org.kie.workbench.common.stunner.core.client.components.proxies.NodeProxy;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CommonActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CreateConnectorToolboxAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CreateNodeToolboxAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.DeleteNodeToolboxAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.FlowActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.GeneralCreateNodeAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.MorphActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.MorphNodeToolboxAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxDomainLookups;
import org.kie.workbench.common.stunner.core.client.components.views.CanvasDefinitionTooltip;
import org.kie.workbench.common.stunner.core.client.definition.adapter.binding.ClientBindableAdapterFunctions;
import org.kie.workbench.common.stunner.core.client.definition.adapter.binding.ClientBindableMorphAdapter;
import org.kie.workbench.common.stunner.core.client.definition.adapter.binding.ClientDefinitionBindablePropertyAdapter;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenEventPublisher;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationMessages;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.preferences.DefaultPreferencesRegistry;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesEntryPoint;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistries;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistryLoader;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerTextPreferences;
import org.kie.workbench.common.stunner.core.client.registry.impl.ClientDefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.client.registry.impl.ClientRegistryFactoryImpl;
import org.kie.workbench.common.stunner.core.client.resources.StunnerCommonIconsStrip;
import org.kie.workbench.common.stunner.core.client.rule.ClientRuleManager;
import org.kie.workbench.common.stunner.core.client.service.ClientDiagramServiceImpl;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.session.command.ManagedClientSessionCommands;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ClearSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.CopySelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.CutSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.DeleteSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToJpgSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToPdfSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToPngSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToRawFormatSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToSvgSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.PasteSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.PerformAutomaticLayoutCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SessionSingletonCommandsFactory;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SwitchGridSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ValidateSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.VisitGraphSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.event.SessionEventObserver;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultEditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultViewerSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ManagedSession;
import org.kie.workbench.common.stunner.core.client.session.impl.SessionLoader;
import org.kie.workbench.common.stunner.core.client.shape.ImageStripRegistry;
import org.kie.workbench.common.stunner.core.client.shape.factory.DelegateShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefFunctionalFactory;
import org.kie.workbench.common.stunner.core.client.util.PrintHelper;
import org.kie.workbench.common.stunner.core.client.util.SvgDataUriGenerator;
import org.kie.workbench.common.stunner.core.client.validation.ClientDiagramValidator;
import org.kie.workbench.common.stunner.core.client.validation.ClientModelValidator;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasDiagramValidator;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManagerImpl;
import org.kie.workbench.common.stunner.core.definition.adapter.bootstrap.BootstrapAdapterFactory;
import org.kie.workbench.common.stunner.core.definition.clone.CloneManagerImpl;
import org.kie.workbench.common.stunner.core.definition.clone.DeepCloneProcess;
import org.kie.workbench.common.stunner.core.definition.clone.DefaultCloneProcess;
import org.kie.workbench.common.stunner.core.definition.clone.NoneCloneProcess;
import org.kie.workbench.common.stunner.core.documentation.DefaultDiagramDocumentationView;
import org.kie.workbench.common.stunner.core.factory.impl.DiagramFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.EdgeFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.GraphFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.NodeFactoryImpl;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManagerImpl;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexerImpl;
import org.kie.workbench.common.stunner.core.graph.processing.index.map.MapIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.GraphProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.SugiyamaLayoutService;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step01.ReverseEdgesCycleBreaker;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step02.LongestPathVertexLayerer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step03.DefaultVertexOrdering;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step03.LayerCrossingCount;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step03.MedianVertexLayerPositioning;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step03.VerticesTransposer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04.DefaultVertexPositioning;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.FullContentTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ViewTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.lookup.definition.DefinitionLookupManagerImpl;
import org.kie.workbench.common.stunner.core.lookup.domain.CommonDomainLookups;
import org.kie.workbench.common.stunner.core.lookup.rule.RuleLookupManagerImpl;
import org.kie.workbench.common.stunner.core.preferences.StunnerDiagramEditorPreferencesBeanGeneratedImpl;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferencesBeanGeneratedImpl;
import org.kie.workbench.common.stunner.core.profile.DomainProfileManager;
import org.kie.workbench.common.stunner.core.profile.FullProfile;
import org.kie.workbench.common.stunner.core.registry.impl.DefaultDefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.rule.CachedRuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleManagerImpl;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtensionMultiHandler;
import org.kie.workbench.common.stunner.core.rule.ext.impl.ConnectorParentsMatchConnectionHandler;
import org.kie.workbench.common.stunner.core.rule.ext.impl.ConnectorParentsMatchContainmentHandler;
import org.kie.workbench.common.stunner.core.rule.ext.impl.ConnectorParentsMatchHandler;
import org.kie.workbench.common.stunner.core.rule.handler.impl.CardinalityEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.handler.impl.ConnectionEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.handler.impl.ConnectorCardinalityEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.handler.impl.ContainmentEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.handler.impl.DockingEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.handler.impl.EdgeCardinalityEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.handler.impl.ElementCardinalityEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.handler.impl.GraphConnectionEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.handler.impl.NodeContainmentEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.handler.impl.NodeDockingEvaluationHandler;
import org.kie.workbench.common.stunner.core.util.ClassUtils;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.validation.impl.GraphValidatorImpl;
import org.kie.workbench.common.stunner.forms.client.FormsClientEntryPoint;
import org.kie.workbench.common.stunner.forms.client.components.toolbox.FormGenerationToolboxAction;
import org.kie.workbench.common.stunner.forms.client.fields.colorPicker.ColorPickerFieldRenderer;
import org.kie.workbench.common.stunner.forms.client.fields.colorPicker.ColorPickerWidget;
import org.kie.workbench.common.stunner.forms.client.fields.colorPicker.ModuleFieldRendererTypesProvider;
import org.kie.workbench.common.stunner.forms.client.gen.ClientFormGenerationManager;
import org.kie.workbench.common.stunner.forms.client.notifications.FormGenerationNotifier;
import org.kie.workbench.common.stunner.forms.client.session.StunnerFormsHandler;
import org.kie.workbench.common.stunner.forms.client.session.command.GenerateDiagramFormsSessionCommand;
import org.kie.workbench.common.stunner.forms.client.session.command.GenerateProcessFormsSessionCommand;
import org.kie.workbench.common.stunner.forms.client.session.command.GenerateSelectedFormsSessionCommand;
import org.kie.workbench.common.stunner.forms.client.widgets.FormPropertiesWidget;
import org.kie.workbench.common.stunner.forms.client.widgets.FormPropertiesWidgetViewImpl;
import org.kie.workbench.common.stunner.forms.client.widgets.FormsCanvasSessionHandler;
import org.kie.workbench.common.stunner.forms.client.widgets.FormsFlushManager;
import org.kie.workbench.common.stunner.forms.client.widgets.container.FormsContainer;
import org.kie.workbench.common.stunner.forms.client.widgets.container.FormsContainerViewImpl;
import org.kie.workbench.common.stunner.forms.client.widgets.container.displayer.FormDisplayer;
import org.kie.workbench.common.stunner.forms.client.widgets.container.displayer.FormDisplayerViewImpl;
import org.kie.workbench.common.stunner.forms.service.fieldProviders.ColorPickerFieldProvider;
import org.kie.workbench.common.stunner.kogito.client.KogitoClientEntryPoint;
import org.kie.workbench.common.stunner.kogito.client.docks.DiagramEditorPreviewAndExplorerDock;
import org.kie.workbench.common.stunner.kogito.client.editor.DiagramEditorView;
import org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorExplorerScreen;
import org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorExplorerScreenActivity;
import org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorExplorerScreenView;
import org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorPropertiesScreen;
import org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorPropertiesScreenActivity;
import org.kie.workbench.common.stunner.kogito.client.session.EditorSessionCommands;
import org.kie.workbench.common.stunner.kogito.client.view.DiagramEditorScreenViewImpl;
import org.kie.workbench.common.stunner.kogito.runtime.client.session.command.impl.KogitoKeyEventHandlerImpl;
import org.kie.workbench.common.stunner.kogito.runtime.client.session.command.impl.KogitoRedoSessionCommand;
import org.kie.workbench.common.stunner.kogito.runtime.client.session.command.impl.KogitoUndoSessionCommand;
import org.kie.workbench.common.stunner.shapes.client.ConnectorGlyphLienzoRenderer;
import org.kie.workbench.common.stunner.shapes.client.factory.BasicShapesFactory;
import org.kie.workbench.common.stunner.shapes.client.factory.PictureProvidersManager;
import org.kie.workbench.common.stunner.shapes.client.view.ShapeViewFactory;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeFactory;
import org.kie.workbench.common.widgets.client.KieWorkbenchWidgetsCommonEntryPoint;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownView;
import org.kie.workbench.common.widgets.client.assets.dropdown.KogitoKieAssetsDropdownView;
import org.kie.workbench.common.widgets.client.cards.CardsGridComponent;
import org.kie.workbench.common.widgets.client.cards.CardsGridComponentView;
import org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponent;
import org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleImpl;
import org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetPresenter;
import org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetViewImpl;
import org.kie.workbench.common.widgets.client.docks.DefaultEditorDock;
import org.kie.workbench.common.widgets.client.docks.DockPlaceHolder;
import org.kie.workbench.common.widgets.client.docks.DockPlaceHolderActivity;
import org.kie.workbench.common.widgets.client.docks.DockPlaceHolderBaseViewImpl;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl;
import org.kie.workbench.common.widgets.client.handlers.NewResourcesMenu;
import org.kie.workbench.common.widgets.client.handlers.PackageListBox;
import org.kie.workbench.common.widgets.client.handlers.PackageListBoxViewImpl;
import org.kie.workbench.common.widgets.client.handlers.PathLabel;
import org.kie.workbench.common.widgets.client.handlers.workbench.configuration.ConfigurationComboBoxItemWidget;
import org.kie.workbench.common.widgets.client.handlers.workbench.configuration.LanguageConfigurationHandler;
import org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationMenu;
import org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationPresenter;
import org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationViewImpl;
import org.kie.workbench.common.widgets.client.menu.AboutCommand;
import org.kie.workbench.common.widgets.client.menu.AppLauncherMenuBuilder;
import org.kie.workbench.common.widgets.client.menu.CurrentBranchImpl;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
import org.kie.workbench.common.widgets.client.menu.RepositoryMenu;
import org.kie.workbench.common.widgets.client.menu.ResetPerspectivesMenuBuilder;
import org.kie.workbench.common.widgets.client.popups.about.AboutPopup;
import org.kie.workbench.common.widgets.client.popups.about.AboutPopupView;
import org.kie.workbench.common.widgets.client.popups.alert.AlertPopupViewImpl;
import org.kie.workbench.common.widgets.client.popups.copy.CopyPopupWithPackageView;
import org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherItemView;
import org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherPresenter;
import org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherView;
import org.kie.workbench.common.widgets.client.popups.list.FormListPopup;
import org.kie.workbench.common.widgets.client.popups.list.FormListPopupViewImpl;
import org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopup;
import org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopupViewImpl;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationMessageTranslatorUtils;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopupViewImpl;
import org.kie.workbench.common.widgets.client.search.component.SearchBarComponent;
import org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView;
import org.kie.workbench.common.widgets.client.source.ViewDRLSourceWidget;
import org.kie.workbench.common.widgets.client.source.ViewSourceViewImpl;
import org.kie.workbench.common.widgets.client.widget.KSessionSelector;
import org.kie.workbench.common.widgets.client.widget.KSessionSelectorViewImpl;
import org.kie.workbench.common.widgets.client.widget.KieMultipleSelectElement;
import org.kie.workbench.common.widgets.client.widget.KieMultipleSelectElementView;
import org.kie.workbench.common.widgets.client.widget.KieSelectElement;
import org.kie.workbench.common.widgets.client.widget.KieSelectElementView;
import org.kie.workbench.common.widgets.client.widget.KieSelectOptionElement;
import org.kie.workbench.common.widgets.client.widget.KieSelectOptionView;
import org.kie.workbench.common.widgets.client.widget.KieSelectOptionsListPresenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.impl.ObservablePathImpl;
import org.uberfire.client.DocksEntryPoint;
import org.uberfire.client.authz.DefaultWorkbenchController;
import org.uberfire.client.authz.EditorTreeProvider;
import org.uberfire.client.authz.PerspectiveTreeProvider;
import org.uberfire.client.docks.UberfireDocksImpl;
import org.uberfire.client.docks.view.DocksBars;
import org.uberfire.client.docks.view.menu.MenuBuilder;
import org.uberfire.client.menu.CustomSplashHelp;
import org.uberfire.client.menu.SplashScreenMenuPresenter;
import org.uberfire.client.menu.WorkbenchViewModeSwitcherMenuBuilder;
import org.uberfire.client.menu.WorkbenchViewModeSwitcherPresenter;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.ActivityBeansInfo;
import org.uberfire.client.mvp.ActivityLifecycleErrorHandler;
import org.uberfire.client.mvp.ActivityManagerImpl;
import org.uberfire.client.mvp.CategoriesManagerCache;
import org.uberfire.client.mvp.ForceUnlockEventObserver;
import org.uberfire.client.mvp.LockDemandDetector;
import org.uberfire.client.mvp.LockManager;
import org.uberfire.client.mvp.LockManagerImpl;
import org.uberfire.client.mvp.LockTarget;
import org.uberfire.client.mvp.LockTarget.TitleProvider;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManagerImpl;
import org.uberfire.client.mvp.PlaceHistoryHandler;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.mvp.PlaceRequestHistoryMapperImpl;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.mvp.ResourceTypeManagerCache;
import org.uberfire.client.mvp.WorkbenchActivity;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.resources.i18n.PermissionTreeI18NImpl;
import org.uberfire.client.util.Clipboard;
import org.uberfire.client.util.Cookie;
import org.uberfire.client.util.GWTEditorNativeRegister;
import org.uberfire.client.views.pfly.HTML5DndSeleniumSupport;
import org.uberfire.client.views.pfly.PatternFlyEntryPoint;
import org.uberfire.client.views.pfly.dnd.CompassWidgetImpl;
import org.uberfire.client.views.pfly.listbar.ListBarWidgetImpl;
import org.uberfire.client.views.pfly.menu.PartContextMenusView;
import org.uberfire.client.views.pfly.menu.SplashScreenMenuView;
import org.uberfire.client.views.pfly.menu.UserMenu;
import org.uberfire.client.views.pfly.menu.UserMenuViewImpl;
import org.uberfire.client.views.pfly.menu.UtilityMenuBarView;
import org.uberfire.client.views.pfly.menu.WorkbenchMenuBarView;
import org.uberfire.client.views.pfly.menu.WorkbenchMenuCompactNavBarView;
import org.uberfire.client.views.pfly.menu.WorkbenchMenuStandardNavBarView;
import org.uberfire.client.views.pfly.menu.WorkbenchViewModeSwitcherView;
import org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView;
import org.uberfire.client.views.pfly.menu.megamenu.contextmenuitem.ChildContextMenuItemView;
import org.uberfire.client.views.pfly.menu.megamenu.contextmenuitem.GroupContextMenuItemView;
import org.uberfire.client.views.pfly.menu.megamenu.menuitem.ChildMenuItemView;
import org.uberfire.client.views.pfly.menu.megamenu.menuitem.GroupMenuItemView;
import org.uberfire.client.views.pfly.menu.megamenu.menuitem.IconMenuItemView;
import org.uberfire.client.views.pfly.modal.Bs3Modal;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorImpl;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorViewImpl;
import org.uberfire.client.views.pfly.multiscreen.MultiScreenMenuBuilder;
import org.uberfire.client.views.pfly.multiscreen.MultiScreenPartWidget;
import org.uberfire.client.views.pfly.multiscreen.MultiScreenView;
import org.uberfire.client.views.pfly.notifications.NotificationPopupsManagerView;
import org.uberfire.client.views.pfly.popup.PopupViewImpl;
import org.uberfire.client.views.pfly.splash.SplashModalFooter;
import org.uberfire.client.views.pfly.splash.SplashViewImpl;
import org.uberfire.client.views.pfly.tab.MultiTabWorkbenchPanelView;
import org.uberfire.client.views.pfly.tab.ResizeTabPanel;
import org.uberfire.client.views.pfly.tab.TabPanelWithDropdowns;
import org.uberfire.client.views.pfly.tab.UberTabPanel;
import org.uberfire.client.views.pfly.toolbar.WorkbenchToolBarView;
import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.client.views.pfly.widgets.ConfirmPopup;
import org.uberfire.client.views.pfly.widgets.DateRangePicker;
import org.uberfire.client.views.pfly.widgets.DropdownMenu;
import org.uberfire.client.views.pfly.widgets.DropdownMenuItem;
import org.uberfire.client.views.pfly.widgets.Elemental2Producer;
import org.uberfire.client.views.pfly.widgets.ErrorPopup;
import org.uberfire.client.views.pfly.widgets.ErrorPopupView;
import org.uberfire.client.views.pfly.widgets.FormGroup;
import org.uberfire.client.views.pfly.widgets.FormLabel;
import org.uberfire.client.views.pfly.widgets.InlineNotification;
import org.uberfire.client.views.pfly.widgets.InputAutocomplete;
import org.uberfire.client.views.pfly.widgets.JQueryElementalProducer.JQuery;
import org.uberfire.client.views.pfly.widgets.KebabMenu;
import org.uberfire.client.views.pfly.widgets.KebabMenuItem;
import org.uberfire.client.views.pfly.widgets.Modal;
import org.uberfire.client.views.pfly.widgets.SanitizedNumberInput;
import org.uberfire.client.views.pfly.widgets.Select;
import org.uberfire.client.workbench.DefaultBeanFactory;
import org.uberfire.client.workbench.LayoutSelection;
import org.uberfire.client.workbench.PanelManagerImpl;
import org.uberfire.client.workbench.StandaloneEditorPerspective;
import org.uberfire.client.workbench.VFSLockServiceProxyClientImpl;
import org.uberfire.client.workbench.VFSServiceProxyClientImpl;
import org.uberfire.client.workbench.Workbench;
import org.uberfire.client.workbench.WorkbenchLayoutImpl;
import org.uberfire.client.workbench.WorkbenchLayoutInfoImpl;
import org.uberfire.client.workbench.WorkbenchServicesProxyClientImpl;
import org.uberfire.client.workbench.docks.UberfireDocksContainer;
import org.uberfire.client.workbench.panels.impl.AdaptiveWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.AdaptiveWorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.ClosableSimpleWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.ClosableSimpleWorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.ImmutableWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.ImmutableWorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.LayoutPanelPresenter;
import org.uberfire.client.workbench.panels.impl.LayoutPanelView;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.MultiScreenWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.MultiScreenWorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.SimpleDnDWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.SimpleDnDWorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.SimpleNoExpandWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.SimpleNoExpandWorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.SplitLayoutPanelPresenter;
import org.uberfire.client.workbench.panels.impl.SplitLayoutPanelView;
import org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.TemplatedWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.TemplatedWorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.UnanchoredStaticWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.UnanchoredStaticWorkbenchPanelView;
import org.uberfire.client.workbench.panels.support.PartManager;
import org.uberfire.client.workbench.pmgr.nswe.part.WorkbenchPartPresenterDefault;
import org.uberfire.client.workbench.pmgr.nswe.part.WorkbenchPartView;
import org.uberfire.client.workbench.pmgr.unanchored.part.UnanchoredWorkbenchPartPresenter;
import org.uberfire.client.workbench.pmgr.unanchored.part.UnanchoredWorkbenchPartView;
import org.uberfire.client.workbench.type.AnyResourceType;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.type.DotResourceType;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.client.workbench.widgets.dnd.CompassDropController;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchPickupDragController;
import org.uberfire.client.workbench.widgets.listbar.ResizeFlowPanel;
import org.uberfire.client.workbench.widgets.menu.PartContextMenusPresenter;
import org.uberfire.client.workbench.widgets.menu.UtilityMenuBarPresenter;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarProducer;
import org.uberfire.client.workbench.widgets.menu.megamenu.WorkbenchMegaMenuPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.WorkbenchMegaMenuProducer;
import org.uberfire.client.workbench.widgets.menu.megamenu.contextmenuitem.ChildContextMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.contextmenuitem.GroupContextMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.menuitem.ChildMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.menuitem.GroupMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.menuitem.IconMenuItemPresenter;
import org.uberfire.client.workbench.widgets.notifications.NotificationManager;
import org.uberfire.client.workbench.widgets.panel.StaticFocusedResizePanel;
import org.uberfire.client.workbench.widgets.toolbar.WorkbenchToolBarPresenter;
import org.uberfire.experimental.client.UberfireExperimentalEntryPoint;
import org.uberfire.experimental.client.disabled.component.DisabledFeatureComponent;
import org.uberfire.experimental.client.disabled.component.DisabledFeatureComponentViewImpl;
import org.uberfire.experimental.client.disabled.screen.DisabledFeatureActivity;
import org.uberfire.experimental.client.editor.ExperimentalFeaturesEditorScreen;
import org.uberfire.experimental.client.editor.ExperimentalFeaturesEditorScreenActivity;
import org.uberfire.experimental.client.editor.ExperimentalFeaturesEditorScreenViewImpl;
import org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroup;
import org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroupViewImpl;
import org.uberfire.experimental.client.editor.group.feature.ExperimentalFeatureEditor;
import org.uberfire.experimental.client.editor.group.feature.ExperimentalFeatureEditorViewImpl;
import org.uberfire.experimental.client.perspective.ExperimentalFeaturesPerspective;
import org.uberfire.experimental.client.perspective.ExperimentalFeaturesPerspectiveActivity;
import org.uberfire.experimental.client.service.auth.ExperimentalActivitiesAuthorizationManagerImpl;
import org.uberfire.experimental.client.service.impl.CDIClientFeatureDefRegistry;
import org.uberfire.experimental.client.service.impl.ClientExperimentalFeaturesRegistryServiceImpl;
import org.uberfire.experimental.client.workbench.type.ExperimentalAwareClientTypeRegistryImpl;
import org.uberfire.ext.editor.commons.client.BaseEditorEntryPoint;
import org.uberfire.ext.editor.commons.client.file.CopyPopUpViewProducer;
import org.uberfire.ext.editor.commons.client.file.RestoreUtil;
import org.uberfire.ext.editor.commons.client.file.exports.FileExportProducer;
import org.uberfire.ext.editor.commons.client.file.exports.ImageFileExport;
import org.uberfire.ext.editor.commons.client.file.exports.PdfFileExport;
import org.uberfire.ext.editor.commons.client.file.exports.TextFileExport;
import org.uberfire.ext.editor.commons.client.file.exports.jso.FileExportScriptInjector;
import org.uberfire.ext.editor.commons.client.file.exports.svg.SvgFileExport;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter.View;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpView;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpView;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpView;
import org.uberfire.ext.editor.commons.client.file.popups.RestorePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.RestorePopUpView;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpView;
import org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentView;
import org.uberfire.ext.editor.commons.client.history.SaveButton;
import org.uberfire.ext.editor.commons.client.history.VersionHistoryPresenter;
import org.uberfire.ext.editor.commons.client.history.VersionHistoryPresenterViewImpl;
import org.uberfire.ext.editor.commons.client.history.VersionMenuDropDownButton;
import org.uberfire.ext.editor.commons.client.history.VersionMenuDropDownButtonViewImpl;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditor;
import org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorActivity;
import org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorLibraryLoader;
import org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorPresenter;
import org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView;
import org.uberfire.ext.editor.commons.client.htmleditor.HtmlResourceType;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilderImpl;
import org.uberfire.ext.editor.commons.client.menu.DownloadMenuItemBuilder;
import org.uberfire.ext.editor.commons.client.menu.RestoreVersionCommandProvider;
import org.uberfire.ext.editor.commons.client.menu.common.CurrentBranchProducer;
import org.uberfire.ext.editor.commons.client.menu.common.SaveAndRenameCommandBuilder;
import org.uberfire.ext.editor.commons.client.template.mustache.ClientMustacheTemplateRenderer;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.ext.editor.commons.file.exports.FileExportsPreferencesBeanGeneratedImpl;
import org.uberfire.ext.editor.commons.version.CurrentBranch;
import org.uberfire.ext.layout.editor.client.LayoutComponentPaletteScreen;
import org.uberfire.ext.layout.editor.client.LayoutEditorEntryPoint;
import org.uberfire.ext.layout.editor.client.LayoutEditorPluginImpl;
import org.uberfire.ext.layout.editor.client.LayoutEditorPresenter;
import org.uberfire.ext.layout.editor.client.LayoutEditorPropertiesScreen;
import org.uberfire.ext.layout.editor.client.LayoutEditorView;
import org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponents;
import org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponentsView;
import org.uberfire.ext.layout.editor.client.components.columns.ComponentColumn;
import org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnPart;
import org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView;
import org.uberfire.ext.layout.editor.client.components.container.Container;
import org.uberfire.ext.layout.editor.client.components.container.ContainerView;
import org.uberfire.ext.layout.editor.client.components.rows.EmptyDropRow;
import org.uberfire.ext.layout.editor.client.components.rows.EmptyDropRowView;
import org.uberfire.ext.layout.editor.client.components.rows.Row;
import org.uberfire.ext.layout.editor.client.components.rows.RowView;
import org.uberfire.ext.layout.editor.client.generator.BootstrapLayoutGenerator;
import org.uberfire.ext.layout.editor.client.generator.BootstrapLayoutGeneratorDriver;
import org.uberfire.ext.layout.editor.client.infra.DnDManager;
import org.uberfire.ext.layout.editor.client.infra.DragHelperComponentColumn;
import org.uberfire.ext.layout.editor.client.infra.LayoutDragComponentHelper;
import org.uberfire.ext.layout.editor.client.infra.LayoutEditorCssHelper;
import org.uberfire.ext.layout.editor.client.infra.LayoutEditorFocusController;
import org.uberfire.ext.layout.editor.client.infra.experimental.DisabledExperimentalLayoutComponent;
import org.uberfire.ext.layout.editor.client.widgets.KebabWidget;
import org.uberfire.ext.layout.editor.client.widgets.LayoutComponentPalettePresenter;
import org.uberfire.ext.layout.editor.client.widgets.LayoutComponentPaletteView;
import org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentGroupPresenter;
import org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentGroupView;
import org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentWidget;
import org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesPresenter;
import org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView;
import org.uberfire.ext.layout.editor.client.widgets.LayoutElementPropertiesPresenter;
import org.uberfire.ext.layout.editor.client.widgets.LayoutElementPropertiesView;
import org.uberfire.ext.preferences.client.PreferencesEntryPoint;
import org.uberfire.ext.preferences.client.admin.AdminPagePerspective;
import org.uberfire.ext.preferences.client.admin.AdminPagePerspectiveActivity;
import org.uberfire.ext.preferences.client.admin.AdminPagePresenter;
import org.uberfire.ext.preferences.client.admin.AdminPagePresenterActivity;
import org.uberfire.ext.preferences.client.admin.AdminPageView;
import org.uberfire.ext.preferences.client.admin.category.AdminPageCategoryPresenter;
import org.uberfire.ext.preferences.client.admin.category.AdminPageCategoryView;
import org.uberfire.ext.preferences.client.admin.item.AdminPageItemPresenter;
import org.uberfire.ext.preferences.client.admin.item.AdminPageItemView;
import org.uberfire.ext.preferences.client.admin.page.AdminPageImpl;
import org.uberfire.ext.preferences.client.central.PreferencesCentralNavBarScreen;
import org.uberfire.ext.preferences.client.central.PreferencesCentralNavBarScreenActivity;
import org.uberfire.ext.preferences.client.central.PreferencesCentralPerspective;
import org.uberfire.ext.preferences.client.central.PreferencesCentralPerspectiveActivity;
import org.uberfire.ext.preferences.client.central.actions.PreferencesCentralActionsScreen;
import org.uberfire.ext.preferences.client.central.actions.PreferencesCentralActionsScreenActivity;
import org.uberfire.ext.preferences.client.central.actions.PreferencesCentralActionsView;
import org.uberfire.ext.preferences.client.central.form.DefaultPreferenceForm;
import org.uberfire.ext.preferences.client.central.form.DefaultPreferenceFormActivity;
import org.uberfire.ext.preferences.client.central.form.DefaultPreferenceFormView;
import org.uberfire.ext.preferences.client.central.screen.PreferencesRootScreen;
import org.uberfire.ext.preferences.client.central.screen.PreferencesRootScreenActivity;
import org.uberfire.ext.preferences.client.central.screen.PreferencesRootView;
import org.uberfire.ext.preferences.client.central.tree.TreeHierarchyInternalItemPresenter;
import org.uberfire.ext.preferences.client.central.tree.TreeHierarchyInternalItemView;
import org.uberfire.ext.preferences.client.central.tree.TreeHierarchyLeafItemPresenter;
import org.uberfire.ext.preferences.client.central.tree.TreeHierarchyLeafItemView;
import org.uberfire.ext.preferences.client.central.tree.TreeHierarchyStructurePresenter;
import org.uberfire.ext.preferences.client.central.tree.TreeHierarchyStructureView;
import org.uberfire.ext.preferences.client.utils.PreferenceFormBeansInfo;
import org.uberfire.ext.properties.editor.client.PropertyEditorWidget;
import org.uberfire.ext.properties.editor.client.fields.BooleanField;
import org.uberfire.ext.properties.editor.client.fields.ColorField;
import org.uberfire.ext.properties.editor.client.fields.ComboField;
import org.uberfire.ext.properties.editor.client.fields.SecretTextField;
import org.uberfire.ext.properties.editor.client.fields.TextField;
import org.uberfire.ext.widgets.common.client.CommonsEntryPoint;
import org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbs;
import org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbsView;
import org.uberfire.ext.widgets.common.client.breadcrumbs.header.UberfireBreadcrumbsContainerImpl;
import org.uberfire.ext.widgets.common.client.breadcrumbs.widget.DefaultBreadcrumbsPresenter;
import org.uberfire.ext.widgets.common.client.breadcrumbs.widget.DefaultBreadcrumbsView;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDown;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView;
import org.uberfire.ext.widgets.common.client.dropdown.PerspectiveDropDown;
import org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooter;
import org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooterViewImpl;
import org.uberfire.ext.widgets.common.client.dropdown.items.LiveSearchSelectorDropDownItem;
import org.uberfire.ext.widgets.common.client.dropdown.items.LiveSearchSelectorDropDownItemViewImpl;
import org.uberfire.ext.widgets.common.client.dropdown.noItems.NoItemsComponent;
import org.uberfire.ext.widgets.common.client.dropdown.noItems.NoItemsComponentViewImpl;
import org.uberfire.ext.widgets.common.client.menu.MenuItemDividerView;
import org.uberfire.ext.widgets.common.client.menu.MenuItemFactory;
import org.uberfire.ext.widgets.common.client.menu.MenuItemHeaderView;
import org.uberfire.ext.widgets.common.client.menu.MenuItemWithIconView;
import org.uberfire.ext.widgets.common.client.select.SelectComponent;
import org.uberfire.ext.widgets.common.client.select.SelectOptionComponent;
import org.uberfire.ext.widgets.common.client.select.SelectOptionView;
import org.uberfire.ext.widgets.common.client.select.SelectView;
import org.uberfire.ext.widgets.common.client.tables.FilterPagedTable;
import org.uberfire.ext.widgets.common.client.tables.PagedTable;
import org.uberfire.ext.widgets.common.client.tables.SimpleTable;
import org.uberfire.ext.widgets.common.client.tables.popup.NewFilterPopup;
import org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup;
import org.uberfire.ext.widgets.core.client.CoreEntryPoint;
import org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorPresenter;
import org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorPresenterActivity;
import org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorView;
import org.uberfire.ext.widgets.core.client.editors.metafile.MetaFileEditorPresenter;
import org.uberfire.ext.widgets.core.client.editors.metafile.MetaFileEditorPresenterActivity;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorWorkbenchEditor;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorWorkbenchEditorActivity;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextResourceType;
import org.uberfire.ext.widgets.core.client.screens.iframe.IFrameScreenPresenter;
import org.uberfire.ext.widgets.core.client.screens.iframe.IFrameScreenPresenterActivity;
import org.uberfire.ext.widgets.core.client.screens.iframe.IFrameScreenView;
import org.uberfire.ext.widgets.core.client.tree.Tree;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageTitle;
import org.uberfire.ext.widgets.core.client.wizards.WizardViewImpl;
import org.uberfire.ext.widgets.core.client.workbench.widgets.popups.activities.notfound.ActivityNotFoundPresenter;
import org.uberfire.ext.widgets.core.client.workbench.widgets.popups.activities.notfound.ActivityNotFoundPresenterActivity;
import org.uberfire.ext.widgets.core.client.workbench.widgets.popups.activities.notfound.ActivityNotFoundView;
import org.uberfire.ext.widgets.table.client.TableEntryPoint;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.ExternalPathPlaceRequest;
import org.uberfire.preferences.client.scope.ClientUsernameProvider;
import org.uberfire.preferences.client.scope.PreferenceScopeResolutionStrategyIOCProvider;
import org.uberfire.preferences.client.store.PreferenceBeanStoreClientImpl;
import org.uberfire.preferences.client.store.PreferenceStore;
import org.uberfire.preferences.client.store.PreferenceStoreIOCProvider;
import org.uberfire.preferences.shared.PreferenceScopeResolutionStrategy;
import org.uberfire.preferences.shared.PreferenceScopeTypes;
import org.uberfire.preferences.shared.bean.mock.PortablePreferenceMockBeanGeneratedImpl;
import org.uberfire.preferences.shared.impl.DefaultPreferenceScopeTypes;
import org.uberfire.preferences.shared.impl.PreferenceScopeFactoryImpl;
import org.uberfire.preferences.shared.impl.PreferenceScopeTypesProducer;
import org.uberfire.preferences.shared.impl.PreferenceScopeValidatorImpl;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.Resource;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceType;
import org.uberfire.security.authz.ResourceActionRef;
import org.uberfire.security.authz.RuntimeResource;
import org.uberfire.security.client.authz.AuthorizationManagerHelper;
import org.uberfire.security.client.authz.tree.impl.DefaultPermissionTreeFactory;
import org.uberfire.security.impl.authz.DefaultAuthorizationManager;
import org.uberfire.security.impl.authz.DefaultPermissionManager;
import org.uberfire.security.impl.authz.DefaultPermissionTypeRegistry;
import org.uberfire.workbench.category.Others;
import org.uberfire.workbench.category.Undefined;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.AppFormerActivities;
import org.uberfire.workbench.model.AppFormerActivitiesProducer;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.ContextDefinition;
import org.uberfire.workbench.model.ContextDisplayMode;
import org.uberfire.workbench.model.CustomPanelDefinition;
import org.uberfire.workbench.model.DefaultAppFormerActivities;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.menu.HasEnabledStateChangeListeners;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;
import org.uberfire.workbench.model.menu.MenuItemPerspective;
import org.uberfire.workbench.model.menu.MenuItemPlain;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.MenuVisitor;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.DefaultMenuGroup;
import org.uberfire.workbench.model.menu.impl.DefaultMenus;
import org.uberfire.workbench.model.toolbar.ToolBar;
import org.uberfire.workbench.model.toolbar.ToolBarIcon;
import org.uberfire.workbench.model.toolbar.ToolBarItem;
import org.uberfire.workbench.type.ResourceTypeDefinition;

public class BootstrapperImpl implements Bootstrapper {
  {
    addLookups_0();
    new CDI().initLookupTable(CDIEventTypeLookup.get());
    new DataBindingModuleBootstrapper().run();
  }

  private ContextManager contextManager = new ContextManagerImpl();
  private static final Logger logger = LoggerFactory.getLogger(Bootstrapper.class);
  public abstract class Type_factory__o_g_m_c_c_MessageConsoleScreen__quals__j_e_i_Any_j_e_i_Default extends Factory<MessageConsoleScreen> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_n_i_BigIntegerEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default extends Factory<BigIntegerEditableColumnGenerator> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_DataTypePickerWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypePickerWidget> { }
  public abstract class Type_factory__o_u_c_u_Cookie__quals__j_e_i_Any_j_e_i_Default extends Factory<Cookie> { }
  public abstract class Type_factory__o_k_w_c_p_a_p_ProfilePreferencesBeanGeneratedImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ProfilePreferencesBeanGeneratedImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_i_m_d_DMNAssetsDropdownItemsProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNAssetsDropdownItemsProvider> { }
  public abstract class Type_factory__o_k_w_c_w_c_s_c_SearchBarComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<SearchBarComponent> { }
  public abstract class ExtensionProvided_factory__o_g_e_t_c_u_ToggleSwitch__quals__j_e_i_Any_j_e_i_Default extends Factory<ToggleSwitch> { }
  public abstract class Type_factory__o_k_w_c_s_c_g_p_l_s_s_LayerCrossingCount__quals__j_e_i_Any_j_e_i_Default extends Factory<LayerCrossingCount> { }
  public abstract class Type_factory__o_k_w_c_f_c_c_c_f_e_EmbeddedFormDisplayerViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<EmbeddedFormDisplayerViewImpl> { }
  public abstract class Type_factory__o_u_c_w_Workbench__quals__j_e_i_Any_j_e_i_Default extends Factory<Workbench> { }
  public abstract class Type_factory__o_u_c_w_p_i_MultiListWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<MultiListWorkbenchPanelPresenter> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_p_h_DataTypeDestroyHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeDestroyHandler> { }
  public abstract class JsType_factory__o_u_s_ResourceType__quals__Universal extends Factory<ResourceType> { }
  public abstract class Type_factory__o_k_w_c_s_c_g_p_l_s_SugiyamaLayoutService__quals__j_e_i_Any_j_e_i_Default extends Factory<SugiyamaLayoutService> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_g_LienzoPictureGlyphRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoPictureGlyphRenderer> { }
  public abstract class Type_factory__o_k_w_c_d_c_g_DMNElementsSynchronizer__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNElementsSynchronizer> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_c_LienzoMultipleSelectionControl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_c_c_s_MultipleSelection extends Factory<LienzoMultipleSelectionControl> { }
  public abstract class Type_factory__o_u_c_m_ActivityBeansInfo__quals__j_e_i_Any_j_e_i_Default extends Factory<ActivityBeansInfo> { }
  public abstract class Type_factory__o_u_c_w_p_u_p_UnanchoredWorkbenchPartView__quals__j_e_i_Any_j_e_i_Default extends Factory<UnanchoredWorkbenchPartView> { }
  public abstract class Type_factory__o_k_w_c_d_w_k_c_c_t_c_GuidedTourUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<GuidedTourUtils> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_h_MapModelBindingHelper__quals__j_e_i_Any_j_e_i_Default extends Factory<MapModelBindingHelper> { }
  public abstract class Type_factory__o_u_e_c_d_c_DisabledFeatureComponentViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DisabledFeatureComponentViewImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_g_p_l_s_GraphProcessorImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<GraphProcessorImpl> { }
  public abstract class ExtensionProvided_factory__o_g_c_u_TextBox__quals__j_e_i_Any_j_e_i_Default extends Factory<TextBox> { }
  public abstract class Type_factory__o_u_c_v_p_w_Select__quals__j_e_i_Any_j_e_i_Default extends Factory<Select> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_c_g_ImageStripDOMGlyphRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<ImageStripDOMGlyphRenderer> { }
  public abstract class Type_factory__o_k_w_c_s_s_c_s_f_SVGShapeFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<SVGShapeFactory> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_i_m_IncludedModelModalView__quals__j_e_i_Any_j_e_i_Default extends Factory<IncludedModelModalView> { }
  public abstract class Type_factory__o_k_w_c_w_c_p_c_CopyPopupWithPackageView__quals__j_e_i_Any_j_e_i_Default extends Factory<CopyPopupWithPackageView> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_t_c_VisitGraphToolbarCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<VisitGraphToolbarCommand> { }
  public abstract class Type_factory__o_u_e_p_c_a_AdminPagePresenterActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<AdminPagePresenterActivity> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_s_ClientFactoryService__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientFactoryService> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_TextAreaFieldRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<TextAreaFieldRenderer> { }
  public abstract class Type_factory__o_k_w_c_d_c_a_i_l_DMNIncludeModelsClient__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNIncludeModelsClient> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_c_g_BS3IconTypeGlyphRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<BS3IconTypeGlyphRenderer> { }
  public abstract class Type_factory__o_u_c_v_p_w_ErrorPopupView__quals__j_e_i_Any_j_e_i_Default extends Factory<ErrorPopupView> { }
  public abstract class Type_factory__o_k_w_c_d_w_k_c_c_d_KogitoDecisionNavigatorDock__quals__j_e_i_Any_j_e_i_Default extends Factory<KogitoDecisionNavigatorDock> { }
  public abstract class Type_factory__o_k_w_c_d_c_d_n_f_DecisionNavigatorNestedItemFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionNavigatorNestedItemFactory> { }
  public abstract class Type_factory__o_k_w_c_d_c_c_c_a_DMNDiagramTextPropertyProviderImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDiagramTextPropertyProviderImpl> { }
  public abstract class Type_factory__o_k_w_c_d_w_k_c_c_d_KogitoPreviewDiagramDock__quals__j_e_i_Any_j_e_i_Default extends Factory<KogitoPreviewDiagramDock> { }
  public abstract class Type_factory__o_g_m_c_c_AlertsEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<AlertsEntryPoint> { }
  public abstract class Type_factory__o_k_w_c_d_c_c_c_k_DMNCanvasShortcutsControlImpl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNCanvasShortcutsControlImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_c_c_t_DMNDeleteNodeToolboxAction__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNDeleteNodeToolboxAction> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_n_NumberSelector__quals__j_e_i_Any_j_e_i_Default extends Factory<NumberSelector> { }
  public abstract class Type_factory__o_k_w_c_s_c_d_DefaultDiagramDocumentationView__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultDiagramDocumentationView> { }
  public abstract class Type_factory__o_k_w_c_s_f_c_f_c_ColorPickerWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<ColorPickerWidget> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_t_f_s_p_PMMLFunctionEditorDefinition__quals__j_e_i_Any_o_k_w_c_d_c_e_e_t_f_FunctionGridSupplementaryEditor extends Factory<PMMLFunctionEditorDefinition> { }
  public abstract class Type_factory__o_k_w_c_s_c_r_h_i_NodeDockingEvaluationHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<NodeDockingEvaluationHandler> { }
  public abstract class JsType_factory__o_u_w_m_m_MenuGroup__quals__Universal extends Factory<MenuGroup> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListHighlightHelper__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeListHighlightHelper> { }
  public abstract class Type_factory__o_j_e_s_s_a_i_BasicUserCache__quals__j_e_i_Any_j_e_i_Default extends Factory<BasicUserCache> { }
  public abstract class Type_factory__o_g_m_c_c_w_b_AlertsButtonMenuItemBuilder__quals__j_e_i_Any_j_e_i_Default extends Factory<AlertsButtonMenuItemBuilder> { }
  public abstract class Type_factory__o_k_w_c_s_k_c_KogitoClientEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<KogitoClientEntryPoint> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_r_RuleLookupManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<RuleLookupManagerImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_c_StunnerLienzoBoundsPanel__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerLienzoBoundsPanel> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_MultipleInputComponentViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<MultipleInputComponentViewImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_n_NotificationsObserver__quals__j_e_i_Any_j_e_i_Default extends Factory<NotificationsObserver> { }
  public abstract class Type_factory__o_u_c_w_w_c_ErrorPopupPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<ErrorPopupPresenter> { }
  public abstract class ContextualProvider_factory__j_e_e_Event__quals__Universal extends Factory<Event> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_s_r_CharacterRadioGroupFieldRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<CharacterRadioGroupFieldRenderer> { }
  public abstract class Type_factory__o_k_w_c_f_f_s_f_b_t_p_TextBoxFieldProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<TextBoxFieldProvider> { }
  public abstract class Type_factory__o_g_c_s_p_c_r_ConflictingRepositoriesPopup__quals__j_e_i_Any_j_e_i_Default extends Factory<ConflictingRepositoriesPopup> { }
  public abstract class JsType_factory__o_k_w_c_d_w_k_m_j_m_d_JSIAlignmentKind__quals__Universal extends Factory<JSIAlignmentKind> { }
  public abstract class Type_factory__o_k_w_c_d_c_c_c_i_DMNCanvasInlineTextEditorControl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor_o_k_w_c_s_c_c_c_c_b_i_Observer extends Factory<DMNCanvasInlineTextEditorControl> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_s_i_ManagedSession__quals__j_e_i_Any_j_e_i_Default extends Factory<ManagedSession> { }
  public abstract class Type_factory__o_k_w_c_d_c_s_DMNViewerSession__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNViewerSession> { }
  public abstract class Type_factory__o_a_k_b_c_c_i_KogitoEditorContextProviderImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<KogitoEditorContextProviderImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_i_ImportDataObjectModalView__quals__j_e_i_Any_j_e_i_Default extends Factory<ImportDataObjectModalView> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_n_i_ShortEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default extends Factory<ShortEditableColumnGenerator> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_ValueAndDataTypePopoverViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ValueAndDataTypePopoverViewImpl> { }
  public abstract class Type_factory__o_g_c_s_p_c_POMEditorPanelViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<POMEditorPanelViewImpl> { }
  public abstract class Type_factory__o_u_w_c_Others__quals__j_e_i_Any_j_e_i_Default extends Factory<Others> { }
  public abstract class Type_factory__o_k_w_c_d_w_k_c_c_e_d_DMNDocumentationViewButtonsVisibilitySupplier__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDocumentationViewButtonsVisibilitySupplier> { }
  public abstract class Type_factory__o_j_e_c_c_d_e_Elemental2DomUtil__quals__j_e_i_Any_j_e_i_Default extends Factory<Elemental2DomUtil> { }
  public abstract class Type_factory__o_u_e_e_c_c_h_VersionRecordManager__quals__j_e_i_Any_j_e_i_Default extends Factory<VersionRecordManager> { }
  public abstract class Type_factory__o_u_c_w_WorkbenchServicesProxyClientImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchServicesProxyClientImpl> { }
  public abstract class Type_factory__o_u_c_w_p_i_StaticWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<StaticWorkbenchPanelPresenter> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_t_u_s_UndefinedExpressionSelectorPopoverViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<UndefinedExpressionSelectorPopoverViewImpl> { }
  public abstract class Type_factory__o_k_w_c_w_c_h_w_c_ConfigurationComboBoxItemWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<ConfigurationComboBoxItemWidget> { }
  public abstract class Type_factory__o_u_c_v_p_w_Button__quals__j_e_i_Any_j_e_i_Default extends Factory<Button> { }
  public abstract class Type_factory__o_u_e_w_t_c_TableEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<TableEntryPoint> { }
  public abstract class Type_factory__o_u_e_w_c_c_e_d_DefaultFileEditorPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultFileEditorPresenter> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_l_s_i_MultipleSelectorInputViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<MultipleSelectorInputViewImpl> { }
  public abstract class Type_factory__o_k_w_c_w_c_w_KieSelectOptionView__quals__j_e_i_Any_j_e_i_Default extends Factory<KieSelectOptionView> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_p_ItemDefinitionStore__quals__j_e_i_Any_j_e_i_Default extends Factory<ItemDefinitionStore> { }
  public abstract class Type_factory__o_k_w_c_w_c_d_DockPlaceHolderBaseViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DockPlaceHolderBaseViewImpl> { }
  public abstract class JsType_factory__o_u_w_m_t_ToolBarIcon__quals__Universal extends Factory<ToolBarIcon> { }
  public abstract class Type_factory__o_k_w_c_w_c_a_d_KieAssetsDropdownView__quals__j_e_i_Any_j_e_i_Default extends Factory<KieAssetsDropdownView> { }
  public abstract class Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponentsItem__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionComponentsItem> { }
  public abstract class Type_factory__o_k_w_c_f_f_s_f_r_m_p_MultipleSubFormFieldProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<MultipleSubFormFieldProvider> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_p_ElementProxy__quals__j_e_i_Any_j_e_i_Default extends Factory<ElementProxy> { }
  public abstract class JsType_factory__o_u_c_m_LockManager__quals__Universal extends Factory<LockManager> { }
  public abstract class ExtensionProvided_factory__o_j_e_c_c_d_NumberInput__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<NumberInput> { }
  public abstract class Type_factory__o_u_c_w_p_i_SimpleWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<SimpleWorkbenchPanelPresenter> { }
  public abstract class Type_factory__o_k_w_c_d_a_r_NoInputNodesInImportedDecisionRule__quals__j_e_i_Any_j_e_i_Default extends Factory<NoInputNodesInImportedDecisionRule> { }
  public abstract class ExtensionProvided_factory__e_d_HTMLOptGroupElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<HTMLOptGroupElement> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_t_f_p_ParametersPopoverViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ParametersPopoverViewImpl> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_c_ClientSelectorDataProviderManager__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientSelectorDataProviderManager> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_w_WiresCanvasView__quals__j_e_i_Any_j_e_i_Default extends Factory<WiresCanvasView> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_CanvasHandlerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<CanvasHandlerImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_w_g_c_c_CellEditorControlImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<CellEditorControlImpl> { }
  public abstract class Type_factory__o_u_c_v_p_w_FormLabel__quals__j_e_i_Any_j_e_i_Default extends Factory<FormLabel> { }
  public abstract class Type_factory__o_u_e_w_c_c_e_t_TextEditorWorkbenchEditor__quals__j_e_i_Any_j_e_i_Default extends Factory<TextEditorWorkbenchEditor> { }
  public abstract class Type_factory__o_u_e_l_e_c_LayoutComponentPaletteScreen__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutComponentPaletteScreen> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_i_i_m_IncludedModelErrorMessageFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<IncludedModelErrorMessageFactory> { }
  public abstract class Producer_factory__o_a_k_b_c_k_KeyboardShortcutsApi__quals__j_e_i_Any_j_e_i_Default extends Factory<KeyboardShortcutsApi> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_p_DefaultPaletteFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultPaletteFactory> { }
  public abstract class Type_factory__o_k_w_c_d_w_k_c_c_s_c_SaveDiagramSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<SaveDiagramSessionCommand> { }
  public abstract class Type_factory__o_k_w_c_d_a_f_DMNGraphFactoryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNGraphFactoryImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_p_LienzoNodeProxyView__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoNodeProxyView> { }
  public abstract class Type_factory__o_u_s_i_a_DefaultPermissionManager__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultPermissionManager> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_e_n_d_DiagramNavigatorItemImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DiagramNavigatorItemImpl> { }
  public abstract class Type_factory__o_u_e_p_e_c_f_BooleanField__quals__j_e_i_Any_j_e_i_Default extends Factory<BooleanField> { }
  public abstract class Producer_factory__o_a_k_b_c_g_t_s_GuidedTourService__quals__j_e_i_Any_j_e_i_Default extends Factory<GuidedTourService> { }
  public abstract class JsType_factory__o_u_w_m_m_MenuPosition__quals__Universal extends Factory<MenuPosition> { }
  public abstract class Type_factory__o_k_w_c_s_k_c_s_DiagramEditorPropertiesScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<DiagramEditorPropertiesScreenActivity> { }
  public abstract class Type_factory__o_u_e_w_c_c_w_w_p_a_n_ActivityNotFoundPresenterActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<ActivityNotFoundPresenterActivity> { }
  public abstract class Type_factory__o_k_w_c_s_k_c_s_DiagramEditorExplorerScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<DiagramEditorExplorerScreenActivity> { }
  public abstract class Type_factory__o_u_c_v_p_m_m_WorkbenchMegaMenuView__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchMegaMenuView> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_e_n_d_DiagramsNavigatorImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DiagramsNavigatorImpl> { }
  public abstract class Type_factory__o_u_c_v_p_m_MultiScreenMenuBuilder__quals__j_e_i_Any_j_e_i_Default extends Factory<MultiScreenMenuBuilder> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_t_f_k_KindPopoverViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<KindPopoverViewImpl> { }
  public abstract class Type_factory__o_u_e_w_c_c_s_SelectOptionView__quals__j_e_i_Any_j_e_i_Default extends Factory<SelectOptionView> { }
  public abstract class Producer_factory__o_u_w_m_AppFormerActivities__quals__j_e_i_Any_o_u_a_Customizable extends Factory<AppFormerActivities> { }
  public abstract class Type_factory__o_u_c_v_p_PatternFlyEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<PatternFlyEntryPoint> { }
  public abstract class Type_factory__o_u_c_v_p_m_WorkbenchMenuStandardNavBarView__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchMenuStandardNavBarView> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_p_c_CollapsedDefinitionPaletteItemWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<CollapsedDefinitionPaletteItemWidget> { }
  public abstract class Type_factory__o_k_w_c_f_a_e_s_f_l_LayoutGenerator__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutGenerator> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_d_DNDListComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<DNDListComponent> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_v_SelectorView__quals__j_e_i_Any_j_e_i_Default extends Factory<SelectorView> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_c_i_CanvasInlineTextEditorControl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_c_c_i_InlineTextEditorBox extends Factory<CanvasInlineTextEditorControl> { }
  public abstract class Type_factory__o_u_e_w_c_c_m_MenuItemHeaderView__quals__j_e_i_Any_o_u_e_w_c_c_m_MenuItemHeader extends Factory<MenuItemHeaderView> { }
  public abstract class Type_factory__o_u_c_w_DefaultBeanFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultBeanFactory> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_d_DNDDataTypesHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<DNDDataTypesHandler> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_f_i_n_f_FieldSetFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldSetFormGroupViewImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_t_c_PasteToolbarCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<PasteToolbarCommand> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<CollapsibleFormGroupViewImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_c_c_s_DomainObjectAwareLienzoMultipleSelectionControl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor_o_k_w_c_s_c_c_c_c_s_MultipleSelection extends Factory<DomainObjectAwareLienzoMultipleSelectionControl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_i_ImportDataObjectModal__quals__j_e_i_Any_j_e_i_Default extends Factory<ImportDataObjectModal> { }
  public abstract class Type_factory__o_g_c_s_p_c_s_ProjectController__quals__j_e_i_Any_j_e_i_Default extends Factory<ProjectController> { }
  public abstract class Type_factory__o_k_w_c_w_c_h_PathLabel__quals__j_e_i_Any_j_e_i_Default extends Factory<PathLabel> { }
  public abstract class Type_factory__o_u_c_w_p_s_PartManager__quals__j_e_i_Any_j_e_i_Default extends Factory<PartManager> { }
  public abstract class Type_factory__o_u_w_c_Undefined__quals__j_e_i_Any_j_e_i_Default extends Factory<Undefined> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_d_i_DatePickerWrapper__quals__j_e_i_Any_j_e_i_Default extends Factory<DatePickerWrapper> { }
  public abstract class Type_factory__o_k_w_c_d_c_s_CommandRegistryHolder__quals__j_e_i_Any_j_e_i_Default extends Factory<CommandRegistryHolder> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_v_ClientDiagramValidator__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientDiagramValidator> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_SmallSwitchComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<SmallSwitchComponent> { }
  public abstract class Type_factory__o_g_m_c_c_MessageConsoleScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<MessageConsoleScreenActivity> { }
  public abstract class Type_factory__o_k_w_c_d_c_s_p_i_DMNSessionPreview__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNSessionPreview> { }
  public abstract class Type_factory__o_k_w_c_w_c_c_f_CardFrameComponentView__quals__j_e_i_Any_j_e_i_Default extends Factory<CardFrameComponentView> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_p_c_g_DefinitionPaletteGroupWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<DefinitionPaletteGroupWidget> { }
  public abstract class Type_factory__o_k_w_c_d_c_c_c_t_DMNCommonActionsToolboxFactory__quals__j_e_i_Any_o_k_w_c_d_c_c_c_t_DMNCommonActionsToolbox extends Factory<DMNCommonActionsToolboxFactory> { }
  public abstract class Type_factory__o_k_w_c_s_c_p_FullProfile__quals__j_e_i_Any_j_e_i_Default extends Factory<FullProfile> { }
  public abstract class Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetDefinitionSetProxyImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDefinitionSetDefinitionSetProxyImpl> { }
  public abstract class Type_factory__o_u_e_c_s_i_CDIClientFeatureDefRegistry__quals__j_e_i_Any_j_e_i_Default extends Factory<CDIClientFeatureDefRegistry> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_p_d_i_DefaultDiagramEditor__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultDiagramEditor> { }
  public abstract class Type_factory__o_u_e_e_c_c_h_HtmlEditor__quals__j_e_i_Any_j_e_i_Default extends Factory<HtmlEditor> { }
  public abstract class Type_factory__o_u_c_w_VFSLockServiceProxyClientImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<VFSLockServiceProxyClientImpl> { }
  public abstract class Type_factory__o_k_w_c_w_c_w_KSessionSelectorViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<KSessionSelectorViewImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_w_l_DMNGridLayerControlImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNGridLayerControlImpl> { }
  public abstract class Type_factory__o_k_w_c_w_c_p_l_AppLauncherView__quals__j_e_i_Any_j_e_i_Default extends Factory<AppLauncherView> { }
  public abstract class Type_factory__o_k_w_c_s_f_c_c_t_FormGenerationToolboxAction__quals__j_e_i_Any_j_e_i_Default extends Factory<FormGenerationToolboxAction> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_CheckBoxFieldRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<CheckBoxFieldRenderer> { }
  public abstract class Type_factory__o_k_w_c_w_c_KieWorkbenchWidgetsCommonEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<KieWorkbenchWidgetsCommonEntryPoint> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_i_i_p_DefinitionsHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<DefinitionsHandler> { }
  public abstract class Type_factory__o_k_w_c_d_c_d_n_c_LazyCanvasFocusUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<LazyCanvasFocusUtils> { }
  public abstract class Type_factory__o_u_c_v_p_m_PartContextMenusView__quals__j_e_i_Any_j_e_i_Default extends Factory<PartContextMenusView> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_FieldLayoutComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldLayoutComponent> { }
  public abstract class Type_factory__o_k_w_c_f_a_e_c_f_ClientMetaDataEntryManager__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientMetaDataEntryManager> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_w_WiresLayer__quals__j_e_i_Any_j_e_i_Default extends Factory<WiresLayer> { }
  public abstract class Type_factory__o_u_c_v_p_t_ResizeTabPanel__quals__j_e_i_Any_o_u_c_v_p_t_Resize extends Factory<ResizeTabPanel> { }
  public abstract class Type_factory__o_k_w_c_d_c_c_c_a_TextAnnotationTextPropertyProviderImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<TextAnnotationTextPropertyProviderImpl> { }
  public abstract class ExtensionProvided_factory__c_g_g_d_c_DivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<DivElement> { }
  public abstract class Type_factory__o_k_w_c_d_a_r_SingleConnectorPerTypeGraphRule__quals__j_e_i_Any_j_e_i_Default extends Factory<SingleConnectorPerTypeGraphRule> { }
  public abstract class JsType_factory__o_u_w_m_PartDefinition__quals__Universal extends Factory<PartDefinition> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_i_m_IncludedModelModal__quals__j_e_i_Any_j_e_i_Default extends Factory<IncludedModelModal> { }
  public abstract class Type_factory__o_u_c_w_p_i_UnanchoredStaticWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<UnanchoredStaticWorkbenchPanelPresenter> { }
  public abstract class Type_factory__o_k_w_c_s_f_c_w_FormsFlushManager__quals__j_e_i_Any_j_e_i_Default extends Factory<FormsFlushManager> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DateTimeValueConverter__quals__j_e_i_Any_j_e_i_Default extends Factory<DateTimeValueConverter> { }
  public abstract class Type_factory__o_u_e_p_c_c_f_DefaultPreferenceForm__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultPreferenceForm> { }
  public abstract class ExtensionProvided_factory__o_j_e_c_c_d_UnorderedList__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<UnorderedList> { }
  public abstract class Type_factory__o_k_w_c_f_s_s_m_p_i_p_FieldReadOnlyEntryProcessor__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldReadOnlyEntryProcessor> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_s_l_IntegerListBoxFieldRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<IntegerListBoxFieldRenderer> { }
  public abstract class Producer_factory__o_k_w_c_s_c_r_i_DefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_Default extends Factory<DefinitionsCacheRegistry> { }
  public abstract class Type_factory__o_u_c_v_p_HTML5DndSeleniumSupport__quals__j_e_i_Any_j_e_i_Default extends Factory<HTML5DndSeleniumSupport> { }
  public abstract class ExtensionProvided_factory__o_g_c_u_HelpBlock__quals__j_e_i_Any_j_e_i_Default extends Factory<HelpBlock> { }
  public abstract class Type_factory__o_k_w_c_s_c_p_StunnerPreferencesBeanGeneratedImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerPreferencesBeanGeneratedImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_t_a_CreateNodeToolboxAction__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_c_t_a_FlowActionsToolbox extends Factory<CreateNodeToolboxAction> { }
  public abstract class Type_factory__o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItemViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<LiveSearchSelectorDropDownItemViewImpl> { }
  public abstract class Type_factory__o_k_w_c_f_a_e_s_f_p_f_f_HasRowsFieldInitializer__quals__j_e_i_Any_j_e_i_Default extends Factory<HasRowsFieldInitializer> { }
  public abstract class Type_factory__o_k_w_c_s_c_p_StunnerDiagramEditorPreferencesBeanGeneratedImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerDiagramEditorPreferencesBeanGeneratedImpl> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_s_l_DecimalListBoxFieldRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<DecimalListBoxFieldRenderer> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_t_d_h_HitPolicyPopoverViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<HitPolicyPopoverViewImpl> { }
  public abstract class Type_factory__o_g_c_s_p_c_GAVEditor__quals__j_e_i_Any_j_e_i_Default extends Factory<GAVEditor> { }
  public abstract class Type_factory__o_k_w_c_d_c_s_DMNCanvasHandler__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNCanvasHandler> { }
  public abstract class Producer_factory__o_u_c_v_p_w_JQueryElementalProducer_JQuery__quals__j_e_i_Any_j_e_i_Default extends Factory<JQuery> { }
  public abstract class Type_factory__o_k_w_c_w_c_m_AppLauncherMenuBuilder__quals__j_e_i_Any_j_e_i_Default extends Factory<AppLauncherMenuBuilder> { }
  public abstract class JsType_factory__o_k_w_c_d_w_k_m_j_m_d_JSITHitPolicy__quals__Universal extends Factory<JSITHitPolicy> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_d_c_DMNDocumentationServiceImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDocumentationServiceImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_g_p_i_m_MapIndexBuilder__quals__j_e_i_Any_j_e_i_Default extends Factory<MapIndexBuilder> { }
  public abstract class Type_factory__o_k_w_c_s_c_r_h_i_NodeContainmentEvaluationHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<NodeContainmentEvaluationHandler> { }
  public abstract class Type_factory__o_u_c_v_p_m_WorkbenchViewModeSwitcherView__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchViewModeSwitcherView> { }
  public abstract class Type_factory__o_u_c_a_PerspectiveTreeProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<PerspectiveTreeProvider> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimeZoneProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<TimeZoneProvider> { }
  public abstract class Type_factory__o_u_c_w_w_m_PartContextMenusPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<PartContextMenusPresenter> { }
  public abstract class Type_factory__o_k_w_c_d_c_s_f_DMNShapeFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNShapeFactory> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<DefinitionPaletteItemWidget> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_p_c_g_DefinitionPaletteGroupWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DefinitionPaletteGroupWidgetViewImpl> { }
  public abstract class Type_factory__o_u_e_w_c_c_b_UberfireBreadcrumbsView__quals__j_e_i_Any_j_e_i_Default extends Factory<UberfireBreadcrumbsView> { }
  public abstract class Type_factory__o_k_w_c_f_p_e_h_i_DefaultModelValidator__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultModelValidator> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeSelectorView__quals__j_e_i_Any_j_e_i_Default extends Factory<DayTimeSelectorView> { }
  public abstract class Type_factory__o_u_e_l_e_c_g_BootstrapLayoutGenerator__quals__j_e_i_Any_j_e_i_Default extends Factory<BootstrapLayoutGenerator> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_v_ImageElementRendererViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ImageElementRendererViewImpl> { }
  public abstract class Type_factory__o_k_w_c_w_c_h_NewResourceViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<NewResourceViewImpl> { }
  public abstract class Type_factory__o_k_w_c_w_c_p_l_AppLauncherItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<AppLauncherItemView> { }
  public abstract class Type_factory__o_u_c_v_p_w_FormGroup__quals__j_e_i_Any_j_e_i_Default extends Factory<FormGroup> { }
  public abstract class Type_factory__o_u_e_p_c_c_s_PreferencesRootScreen__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferencesRootScreen> { }
  public abstract class Type_factory__o_k_w_c_s_c_r_i_DefaultDefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultDefinitionsCacheRegistry> { }
  public abstract class Type_factory__o_k_w_c_d_c_d_n_t_DecisionNavigatorTreeView_TreeItem__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeItem> { }
  public abstract class Type_factory__o_u_e_p_c_c_t_TreeHierarchyStructureView__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeHierarchyStructureView> { }
  public abstract class Type_factory__o_u_c_v_p_t_MultiTabWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<MultiTabWorkbenchPanelView> { }
  public abstract class Type_factory__o_k_w_c_f_f_s_f_r_s_p_SubFormFieldProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<SubFormFieldProvider> { }
  public abstract class Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetRuleAdapterImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDefinitionSetRuleAdapterImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_w_g_ExpressionGridCacheImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ExpressionGridCacheImpl> { }
  public abstract class Type_factory__o_k_w_c_d_w_k_c_c_e_d_c_DMNDocumentationFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDocumentationFactory> { }
  public abstract class Type_factory__o_k_w_c_d_c_s_f_DMNConnectorShapeViewFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNConnectorShapeViewFactory> { }
  public abstract class Type_factory__o_u_e_c_e_g_f_ExperimentalFeatureEditor__quals__j_e_i_Any_j_e_i_Default extends Factory<ExperimentalFeatureEditor> { }
  public abstract class Type_factory__o_k_w_c_s_c_r_RuleManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<RuleManagerImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DateTimeSelector__quals__j_e_i_Any_j_e_i_Default extends Factory<DateTimeSelector> { }
  public abstract class Type_factory__o_j_e_i_c_a_b_InitBallotProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<InitBallotProvider> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_c_c_ClipboardControlCleaner__quals__j_e_i_Any_j_e_i_Default extends Factory<ClipboardControlCleaner> { }
  public abstract class Type_factory__o_k_w_c_s_f_c_g_ClientFormGenerationManager__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientFormGenerationManager> { }
  public abstract class Type_factory__o_k_w_c_d_c_m_m_DMNMarshaller__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNMarshaller> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_i_t_TreeListSubItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeListSubItemView> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_t_i_InvocationEditorDefinition__quals__j_e_i_Any_j_e_i_Default extends Factory<InvocationEditorDefinition> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_s_DataTypeListShortcutsView__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeListShortcutsView> { }
  public abstract class Type_factory__o_j_e_u_c_w_LocaleListBox__quals__j_e_i_Any_j_e_i_Default extends Factory<LocaleListBox> { }
  public abstract class Type_factory__o_u_c_w_p_i_ImmutableWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<ImmutableWorkbenchPanelPresenter> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_v_s_ScreenErrorViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ScreenErrorViewImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_d_l_NameAndUrlPopoverViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<NameAndUrlPopoverViewImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_t_a_FlowActionsToolboxFactory__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_FlowActionsToolbox extends Factory<FlowActionsToolboxFactory> { }
  public abstract class Type_factory__o_u_e_w_c_c_d_LiveSearchDropDownView__quals__j_e_i_Any_j_e_i_Default extends Factory<LiveSearchDropDownView> { }
  public abstract class Type_factory__o_u_c_w_w_m_UtilityMenuBarPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<UtilityMenuBarPresenter> { }
  public abstract class Type_factory__o_u_e_p_e_c_f_TextField__quals__j_e_i_Any_j_e_i_Default extends Factory<TextField> { }
  public abstract class JsType_factory__o_u_m_PlaceRequest__quals__Universal extends Factory<PlaceRequest> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_c_DockingAcceptorControlImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DockingAcceptorControlImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSession__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDiagramsSession> { }
  public abstract class Type_factory__o_k_w_c_s_c_g_p_l_s_s_LongestPathVertexLayerer__quals__j_e_i_Any_j_e_i_Default extends Factory<LongestPathVertexLayerer> { }
  public abstract class Type_factory__o_k_w_c_d_c_c_c_t_DMNEditDRDToolboxAction__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNEditDRDToolboxAction> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_t_c_ExportToPngToolbarCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<ExportToPngToolbarCommand> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_m_d_i_LogSelectedEdgeDevCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<LogSelectedEdgeDevCommand> { }
  public abstract class Type_factory__o_a_k_b_c_r_p_ResourceContentServiceProducer__quals__j_e_i_Any_j_e_i_Default extends Factory<ResourceContentServiceProducer> { }
  public abstract class Type_factory__o_k_w_c_f_a_e_s_f_p_f_f_s_SelectorFieldInitilizer__quals__j_e_i_Any_j_e_i_Default extends Factory<SelectorFieldInitilizer> { }
  public abstract class Type_factory__o_u_e_e_c_c_f_p_DeletePopUpPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<DeletePopUpPresenter> { }
  public abstract class Type_factory__o_k_w_c_d_c_w_p_DMNGridPanelContainer__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNGridPanelContainer> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_i_i_ImportFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<ImportFactory> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraint__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeConstraint> { }
  public abstract class Type_factory__o_a_k_b_c_w_p_WorkspaceServiceProducer__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkspaceServiceProducer> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_t_l_LiteralExpressionEditorDefinition__quals__j_e_i_Any_j_e_i_Default extends Factory<LiteralExpressionEditorDefinition> { }
  public abstract class Type_factory__o_u_c_w_p_i_MultiListWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<MultiListWorkbenchPanelView> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_m_d_i_ClearCommandHistoryDevCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<ClearCommandHistoryDevCommand> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConfirmation__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeConfirmation> { }
  public abstract class Type_factory__o_u_e_l_e_c_c_c_ColumnWithComponentsView__quals__j_e_i_Any_j_e_i_Default extends Factory<ColumnWithComponentsView> { }
  public abstract class Type_factory__o_k_w_c_s_c_r_h_i_EdgeCardinalityEvaluationHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<EdgeCardinalityEvaluationHandler> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchBarView__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeSearchBarView> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_i_t_TreeList__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeList> { }
  public abstract class Type_factory__o_u_c_m_LockDemandDetector__quals__j_e_i_Any_j_e_i_Default extends Factory<LockDemandDetector> { }
  public abstract class Type_factory__o_u_e_p_e_c_f_ColorField__quals__j_e_i_Any_j_e_i_Default extends Factory<ColorField> { }
  public abstract class Type_factory__o_u_e_w_c_c_CommonsEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<CommonsEntryPoint> { }
  public abstract class Type_factory__o_k_w_c_f_f_s_f_b_d_p_DecimalBoxFieldProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<DecimalBoxFieldProvider> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_u_CanvasLayoutUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<CanvasLayoutUtils> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_LienzoImageStripLoader__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoImageStripLoader> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_s_i_DefaultEditorSession__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultEditorSession> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_m_ZoomLevelSelectorPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<ZoomLevelSelectorPresenter> { }
  public abstract class Type_factory__o_u_c_w_p_i_SimpleWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<SimpleWorkbenchPanelView> { }
  public abstract class Producer_factory__o_k_w_c_d_c_e_e_t_ExpressionEditorDefinitions__quals__j_e_i_Any_j_e_i_Default_1 extends Factory<ExpressionEditorDefinitions> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_m_ZoomLevelSelectorView__quals__j_e_i_Any_j_e_i_Default extends Factory<ZoomLevelSelectorView> { }
  public abstract class ContextualProvider_factory__e_d_HTMLElement__quals__Universal extends Factory<HTMLElement> { }
  public abstract class Type_factory__o_k_w_c_d_c_d_n_f_DecisionNavigatorBaseItemFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionNavigatorBaseItemFactory> { }
  public abstract class Type_factory__o_u_p_s_i_PreferenceScopeFactoryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferenceScopeFactoryImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_r_DataTypeConstraintRangeView__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeConstraintRangeView> { }
  public abstract class Type_factory__o_k_w_c_d_c_m_c_DMNDiagramElementsUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDiagramElementsUtils> { }
  public abstract class JsType_factory__o_u_w_m_m_MenuItem__quals__Universal extends Factory<MenuItem> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_ReferencedDataTypeWarningMessage__quals__j_e_i_Any_j_e_i_Default extends Factory<ReferencedDataTypeWarningMessage> { }
  public abstract class ContextualProvider_factory__j_e_i_Instance__quals__Universal extends Factory<Instance> { }
  public abstract class DynamicValidatorFactory extends Factory<DynamicValidator> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_d_l_NameAndUriPopoverImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<NameAndUriPopoverImpl> { }
  public abstract class ExtensionProvided_factory__c_g_g_u_c_u_HeaderPanel__quals__j_e_i_Any_j_e_i_Default extends Factory<HeaderPanel> { }
  public abstract class Type_factory__o_k_w_c_d_c_c_c_t_DMNFlowActionsToolboxFactory__quals__j_e_i_Any_o_k_w_c_d_c_c_c_t_DMNFlowActionsToolbox extends Factory<DMNFlowActionsToolboxFactory> { }
  public abstract class Producer_factory__c_g_w_b_e_s_EventBus__quals__j_e_i_Any_j_e_i_Default extends Factory<EventBus> { }
  public abstract class Type_factory__o_g_c_s_p_c_POMEditorPanel__quals__j_e_i_Any_j_e_i_Default extends Factory<POMEditorPanel> { }
  public abstract class Type_factory__o_u_c_v_p_m_MultiPageEditorViewImpl__quals__j_e_i_Any_o_u_c_w_w_m_Multiple extends Factory<MultiPageEditorViewImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_m_d_i_SwitchLogLevelDevCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<SwitchLogLevelDevCommand> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_f_i_d_DefaultFormGroup__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultFormGroup> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_t_c_DeleteSelectionToolbarCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<DeleteSelectionToolbarCommand> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_l_s_MultipleSelectorFieldRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<MultipleSelectorFieldRenderer> { }
  public abstract class Type_factory__o_k_w_c_s_c_g_p_l_s_s_DefaultVertexPositioning__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultVertexPositioning> { }
  public abstract class Producer_factory__o_u_c_w_w_m_m_WorkbenchMegaMenuPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchMegaMenuPresenter> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_p_d_i_DiagramLoader__quals__j_e_i_Any_j_e_i_Default extends Factory<DiagramLoader> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeHasFieldsWarningMessage__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeHasFieldsWarningMessage> { }
  public abstract class Type_factory__o_k_w_c_f_c_r_c_w_t_BindableTypeAheadViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<BindableTypeAheadViewImpl> { }
  public abstract class Type_factory__o_j_e_i_c_a_b_CallerProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<CallerProvider> { }
  public abstract class Type_factory__o_u_c_r_i_PermissionTreeI18NImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<PermissionTreeI18NImpl> { }
  public abstract class Type_factory__o_k_w_c_d_a_DMNDefinitionSet__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDefinitionSet> { }
  public abstract class Type_factory__o_k_w_c_k_c_e_MultiPageEditorContainerViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<MultiPageEditorContainerViewImpl> { }
  public abstract class Type_factory__o_k_w_c_f_a_e_s_f_p_f_f_n_SubFormFieldInitializer__quals__j_e_i_Any_j_e_i_Default extends Factory<SubFormFieldInitializer> { }
  public abstract class Type_factory__o_k_w_c_d_c_m_i_DMNMarshallerImportsService__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNMarshallerImportsService> { }
  public abstract class Type_factory__o_u_c_v_p_w_DropdownMenu__quals__j_e_i_Any_j_e_i_Default extends Factory<DropdownMenu> { }
  public abstract class Type_factory__o_k_w_c_f_a_e_s_f_p_f_f_n_MultipleSubFormFieldInitializer__quals__j_e_i_Any_j_e_i_Default extends Factory<MultipleSubFormFieldInitializer> { }
  public abstract class ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<Span> { }
  public abstract class ExtensionProvided_factory__e_d_HTMLInputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<HTMLInputElement> { }
  public abstract class Type_factory__o_k_w_c_d_w_k_c_c_s_KogitoDMNCanvasShortcutsControlImpl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<KogitoDMNCanvasShortcutsControlImpl> { }
  public abstract class Type_factory__o_u_b_v_i_ObservablePathImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ObservablePathImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_y_m_YearsMonthsSelectorView__quals__j_e_i_Any_j_e_i_Default extends Factory<YearsMonthsSelectorView> { }
  public abstract class Type_factory__o_k_w_c_d_w_k_c_c_s_DMNClientServicesProxyImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNClientServicesProxyImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_r_h_i_GraphConnectionEvaluationHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<GraphConnectionEvaluationHandler> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_c_t_DefaultToolboxControl__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultToolboxControl> { }
  public abstract class Type_factory__o_k_w_c_d_a_f_DMNDiagramFactoryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDiagramFactoryImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponentsItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionComponentsItemView> { }
  public abstract class Type_factory__o_k_w_c_w_c_s_c_SearchBarComponentView__quals__j_e_i_Any_j_e_i_Default extends Factory<SearchBarComponentView> { }
  public abstract class Type_factory__o_u_c_v_p_t_UberTabPanel__quals__j_e_i_Any_j_e_i_Default extends Factory<UberTabPanel> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_f_i_c_CheckBoxFormGroup__quals__j_e_i_Any_j_e_i_Default extends Factory<CheckBoxFormGroup> { }
  public abstract class ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<HTMLButtonElement> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_t_i_ManagedToolbar__quals__j_e_i_Any_j_e_i_Default extends Factory<ManagedToolbar> { }
  public abstract class Type_factory__o_k_w_c_d_c_s_DMNPictureProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNPictureProvider> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_t_f_s_FunctionSupplementaryGridEditorDefinitionsProducer__quals__j_e_i_Any_o_k_w_c_d_c_e_e_t_f_FunctionGridSupplementaryEditor extends Factory<FunctionSupplementaryGridEditorDefinitionsProducer> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_i_InlineTextEditorBoxViewImpl__quals__j_e_i_Any_o_k_w_c_s_c_c_c_c_i_InlineTextEditorBox extends Factory<InlineTextEditorBoxViewImpl> { }
  public abstract class Type_factory__o_u_e_l_e_c_w_LayoutComponentPaletteView__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutComponentPaletteView> { }
  public abstract class Type_factory__o_k_w_c_d_c_d_p_PreviewDiagramScreen__quals__j_e_i_Any_j_e_i_Default extends Factory<PreviewDiagramScreen> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_c_DataTypeUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeUtils> { }
  public abstract class Type_factory__o_k_w_c_w_c_c_CardsGridComponentView__quals__j_e_i_Any_j_e_i_Default extends Factory<CardsGridComponentView> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_DecimalBoxFieldRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<DecimalBoxFieldRenderer> { }
  public abstract class Type_factory__o_j_e_d_c_ListComponentProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<ListComponentProvider> { }
  public abstract class Type_factory__o_k_w_c_s_f_c_w_FormPropertiesWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<FormPropertiesWidget> { }
  public abstract class Type_factory__o_u_e_p_c_a_AdminPagePerspectiveActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<AdminPagePerspectiveActivity> { }
  public abstract class JsType_factory__o_u_w_m_m_MenuItemPerspective__quals__Universal extends Factory<MenuItemPerspective> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_p_s_i_SessionEditorImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<SessionEditorImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_t_f_p_ParametersPopoverImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ParametersPopoverImpl> { }
  public abstract class Type_factory__o_u_e_w_c_c_b_UberfireBreadcrumbs__quals__j_e_i_Any_j_e_i_Default extends Factory<UberfireBreadcrumbs> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_TimeSelector__quals__j_e_i_Any_j_e_i_Default extends Factory<TimeSelector> { }
  public abstract class Type_factory__o_k_w_c_d_a_DMNAPIEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNAPIEntryPoint> { }
  public abstract class Type_factory__o_u_s_i_a_DefaultPermissionTypeRegistry__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultPermissionTypeRegistry> { }
  public abstract class JsType_factory__o_j_e_e_c_c_JsTypeEventObserver__quals__Universal extends Factory<JsTypeEventObserver> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_c_m_FlashMessagesView__quals__j_e_i_Any_j_e_i_Default extends Factory<FlashMessagesView> { }
  public abstract class Producer_factory__o_a_k_b_c_p_m_PMMLEditorMarshallerApi__quals__j_e_i_Any_j_e_i_Default extends Factory<PMMLEditorMarshallerApi> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_c_e_DataTypeNameIsNotUniqueErrorMessage__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeNameIsNotUniqueErrorMessage> { }
  public abstract class Type_factory__o_u_e_e_c_c_h_HtmlEditorPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<HtmlEditorPresenter> { }
  public abstract class Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelector__quals__j_e_i_Any_j_e_i_Default extends Factory<ListSelector> { }
  public abstract class Type_factory__o_k_w_c_f_f_s_f_b_c_p_CheckBoxFieldProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<CheckBoxFieldProvider> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_l_c_MultipleInputFieldRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<MultipleInputFieldRenderer> { }
  public abstract class Type_factory__o_k_w_c_s_c_f_i_DiagramFactoryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DiagramFactoryImpl> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_MultipleInputComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<MultipleInputComponent> { }
  public abstract class Type_factory__o_k_w_c_f_c_c_CrudComponentEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<CrudComponentEntryPoint> { }
  public abstract class Type_factory__o_k_w_c_f_f_s_f_b_l_i_MultipleInputProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<MultipleInputProvider> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_CanvasCommandManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<CanvasCommandManagerImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_g_p_l_s_s_DefaultVertexOrdering__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultVertexOrdering> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_d_CommonDomainLookups__quals__j_e_i_Any_j_e_i_Default extends Factory<CommonDomainLookups> { }
  public abstract class Type_factory__o_u_c_d_UberfireDocksImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<UberfireDocksImpl> { }
  public abstract class Type_factory__o_u_e_e_c_c_h_HtmlEditorActivity__quals__j_e_i_Any_j_i_Named_o_u_c_w_a_AssociatedResources_o_u_c_w_a_Priority extends Factory<HtmlEditorActivity> { }
  public abstract class Type_factory__o_u_c_w_p_i_SimpleDnDWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<SimpleDnDWorkbenchPanelPresenter> { }
  public abstract class Type_factory__o_u_p_c_s_PreferenceStoreIOCProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferenceStoreIOCProvider> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_MultipleInputViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<MultipleInputViewImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientDefinitionManager> { }
  public abstract class Type_factory__o_k_w_c_d_c_w_g_c_c_CellEditorControls__quals__j_e_i_Any_j_e_i_Default extends Factory<CellEditorControls> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_ExpressionEditorControlImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ExpressionEditorControlImpl> { }
  public abstract class Type_factory__o_u_e_w_c_c_d_LiveSearchDropDown__quals__j_e_i_Any_j_e_i_Default extends Factory<LiveSearchDropDown> { }
  public abstract class Type_factory__o_k_w_c_d_c_c_BoxedExpressionHelper__quals__j_e_i_Any_j_e_i_Default extends Factory<BoxedExpressionHelper> { }
  public abstract class Type_factory__o_k_w_c_f_a_e_c_f_ClientFieldElementProcessor__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientFieldElementProcessor> { }
  public abstract class Type_factory__o_a_k_b_c_p_m_PMMLEditorMarshallerServiceProducer__quals__j_e_i_Any_j_e_i_Default extends Factory<PMMLEditorMarshallerServiceProducer> { }
  public abstract class Producer_factory__o_u_e_e_c_c_f_e_TextFileExport__quals__j_e_i_Any_j_e_i_Default extends Factory<TextFileExport> { }
  public abstract class Type_factory__o_u_e_l_e_c_w_LayoutDragComponentGroupPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutDragComponentGroupPresenter> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_n_i_LongEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default extends Factory<LongEditableColumnGenerator> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_s_c_i_ExportToSvgSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<ExportToSvgSessionCommand> { }
  public abstract class Type_factory__o_j_e_v_c_ValidatorProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<ValidatorProvider> { }
  public abstract class Type_factory__o_u_c_w_p_i_MultiTabWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<MultiTabWorkbenchPanelPresenter> { }
  public abstract class JsType_factory__o_u_w_m_CustomPanelDefinition__quals__Universal extends Factory<CustomPanelDefinition> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_v_LienzoTextTooltip__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoTextTooltip> { }
  public abstract class JsType_factory__o_u_b_v_Path__quals__Universal extends Factory<Path> { }
  public abstract class Type_factory__o_k_w_c_w_c_d_AsyncPackageDataModelOracleFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<AsyncPackageDataModelOracleFactory> { }
  public abstract class Type_factory__o_u_c_v_p_w_Elemental2Producer__quals__j_e_i_Any_j_e_i_Default extends Factory<Elemental2Producer> { }
  public abstract class Type_factory__o_k_w_c_s_c_f_i_EdgeFactoryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<EdgeFactoryImpl> { }
  public abstract class ContextualProvider_factory__o_j_e_i_c_a_Disposer__quals__Universal extends Factory<Disposer> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_t_d_DecisionTableEditorDefinition__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionTableEditorDefinition> { }
  public abstract class ContextualProvider_factory__o_j_e_d_c_c_ListComponent__quals__Universal extends Factory<ListComponent> { }
  public abstract class Type_factory__o_u_c_w_p_i_SplitLayoutPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<SplitLayoutPanelView> { }
  public abstract class Type_factory__o_u_e_e_c_c_m_BasicFileMenuBuilderImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<BasicFileMenuBuilderImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraintModalView__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeConstraintModalView> { }
  public abstract class ExtensionProvided_factory__e_d_HTMLHeadingElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<HTMLHeadingElement> { }
  public abstract class Type_factory__o_u_e_w_c_c_w_w_p_a_n_ActivityNotFoundView__quals__j_e_i_Any_j_e_i_Default extends Factory<ActivityNotFoundView> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_RedoCommandHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<RedoCommandHandler> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_p_ExpandedPaletteDefinitionBuilder__quals__j_e_i_Any_j_e_i_Default extends Factory<ExpandedPaletteDefinitionBuilder> { }
  public abstract class Type_factory__o_k_w_c_s_f_c_w_FormPropertiesWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FormPropertiesWidgetViewImpl> { }
  public abstract class JsType_factory__o_u_w_m_m_HasEnabledStateChangeListeners__quals__Universal extends Factory<HasEnabledStateChangeListeners> { }
  public abstract class Type_factory__o_k_w_c_d_c_m_i_DMNIncludedNodeFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNIncludedNodeFactory> { }
  public abstract class Type_factory__o_k_w_c_k_c_KogitoClientEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<org.kie.workbench.common.kogito.client.KogitoClientEntryPoint> { }
  public abstract class Type_factory__o_k_w_c_s_c_g_p_t_c_FullContentTraverseProcessorImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FullContentTraverseProcessorImpl> { }
  public abstract class Type_factory__o_u_c_m_PlaceHistoryHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<PlaceHistoryHandler> { }
  public abstract class Type_factory__o_g_c_s_p_c_Form__quals__j_e_i_Any_j_e_i_Default extends Factory<Form> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_s_f_ShapeDefFunctionalFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<ShapeDefFunctionalFactory> { }
  public abstract class ExtensionProvided_factory__o_j_e_c_c_d_Select__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<org.jboss.errai.common.client.dom.Select> { }
  public abstract class Type_factory__o_k_w_c_d_w_k_c_c_t_o_GuidedTourGridObserver__quals__j_e_i_Any_j_e_i_Default extends Factory<GuidedTourGridObserver> { }
  public abstract class Type_factory__o_u_e_l_e_c_c_c_ComponentColumn__quals__j_e_i_Any_j_e_i_Default extends Factory<ComponentColumn> { }
  public abstract class Type_factory__o_k_w_c_d_c_d_n_t_DecisionNavigatorTreePresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionNavigatorTreePresenter> { }
  public abstract class Type_factory__o_k_w_c_s_s_c_ConnectorGlyphLienzoRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<ConnectorGlyphLienzoRenderer> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_i_t_TreeListSubItem__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeListSubItem> { }
  public abstract class Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorPresenterActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<DecisionNavigatorPresenterActivity> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_p_CollapsedPaletteDefinitionBuilder__quals__j_e_i_Any_j_e_i_Default extends Factory<CollapsedPaletteDefinitionBuilder> { }
  public abstract class Producer_factory__o_j_e_c_c_d_Document__quals__j_e_i_Any_j_e_i_Default extends Factory<Document> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_v_c_CanvasDiagramValidator__quals__j_e_i_Any_j_e_i_Default extends Factory<CanvasDiagramValidator> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_t_c_PerformAutomaticLayoutToolbarCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<PerformAutomaticLayoutToolbarCommand> { }
  public abstract class Type_factory__o_u_e_c_s_a_ExperimentalActivitiesAuthorizationManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ExperimentalActivitiesAuthorizationManagerImpl> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_StringEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default extends Factory<StringEditableColumnGenerator> { }
  public abstract class Type_factory__o_k_w_c_s_s_c_f_PictureProvidersManager__quals__j_e_i_Any_j_e_i_Default extends Factory<PictureProvidersManager> { }
  public abstract class Type_factory__o_u_e_l_e_c_i_DragHelperComponentColumn__quals__j_e_i_Any_j_e_i_Default extends Factory<DragHelperComponentColumn> { }
  public abstract class Type_factory__o_k_w_c_w_c_p_a_AlertPopupViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<AlertPopupViewImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_p_h_ItemDefinitionDestroyHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<ItemDefinitionDestroyHandler> { }
  public abstract class ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<Anchor> { }
  public abstract class Type_factory__o_u_e_p_e_c_PropertyEditorWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<PropertyEditorWidget> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_c_ItemDefinitionUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<ItemDefinitionUtils> { }
  public abstract class Type_factory__o_u_e_p_c_c_t_TreeHierarchyLeafItemPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeHierarchyLeafItemPresenter> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_i_t_TreeListItem__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeListItem> { }
  public abstract class Type_factory__o_u_c_v_p_m_UserMenuViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<UserMenuViewImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_d_c_DMNDocumentationFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationFactory> { }
  public abstract class JsType_factory__o_u_c_m_PlaceStatus__quals__Universal extends Factory<PlaceStatus> { }
  public abstract class Type_factory__o_k_w_c_f_p_e_h_i_FormValidatorImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FormValidatorImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientTranslationService> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_d_a_b_ClientBindableAdapterFunctions__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientBindableAdapterFunctions> { }
  public abstract class ExtensionProvided_factory__e_d_HTMLImageElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<HTMLImageElement> { }
  public abstract class Type_factory__o_u_e_l_e_c_i_LayoutEditorFocusController__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutEditorFocusController> { }
  public abstract class JsType_factory__o_u_s_Resource__quals__Universal extends Factory<Resource> { }
  public abstract class Type_factory__o_k_w_c_f_s_s_m_p_i_p_FieldPlaceHolderEntryProcessor__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldPlaceHolderEntryProcessor> { }
  public abstract class Type_factory__o_u_e_p_c_c_PreferencesCentralNavBarScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<PreferencesCentralNavBarScreenActivity> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_f_i_c_ConfigErrorDisplayerViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ConfigErrorDisplayerViewImpl> { }
  public abstract class Type_factory__o_u_c_w_p_i_LayoutPanelPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutPanelPresenter> { }
  public abstract class Type_factory__o_k_w_c_s_c_r_h_i_DockingEvaluationHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<DockingEvaluationHandler> { }
  public abstract class Type_factory__o_u_c_v_p_w_Modal__quals__j_e_i_Any_j_e_i_Default extends Factory<Modal> { }
  public abstract class Type_factory__o_u_c_a_DefaultWorkbenchController__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultWorkbenchController> { }
  public abstract class ExtensionProvided_factory__o_s_Logger__quals__Universal_2 extends Factory<Logger> { }
  public abstract class ExtensionProvided_factory__o_s_Logger__quals__Universal_1 extends Factory<Logger> { }
  public abstract class Type_factory__o_k_w_c_s_c_r_h_i_ConnectorCardinalityEvaluationHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<ConnectorCardinalityEvaluationHandler> { }
  public abstract class Type_factory__o_k_w_c_d_c_r_DMNSVGViewFactoryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNSVGViewFactoryImpl> { }
  public abstract class JsType_factory__o_u_w_m_m_MenuItemPlain__quals__Universal extends Factory<MenuItemPlain> { }
  public abstract class Type_factory__o_k_w_c_f_a_e_s_f_p_f_f_HasPlaceHolderFieldInitializer__quals__j_e_i_Any_j_e_i_Default extends Factory<HasPlaceHolderFieldInitializer> { }
  public abstract class Type_factory__o_k_w_c_f_s_s_m_p_i_p_FieldLabelEntryProcessor__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldLabelEntryProcessor> { }
  public abstract class Type_factory__o_k_w_c_f_p_e_h_i_FieldStateValidatorImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldStateValidatorImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_p_s_i_SessionViewerPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<SessionViewerPresenter> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_l_s_i_MultipleSelectorInput__quals__j_e_i_Any_j_e_i_Default extends Factory<MultipleSelectorInput> { }
  public abstract class Type_factory__o_k_w_c_f_c_c_c_CrudComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<CrudComponent> { }
  public abstract class Type_factory__o_u_e_p_c_a_i_AdminPageItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<AdminPageItemView> { }
  public abstract class Type_factory__o_u_e_e_c_c_f_p_RestorePopUpPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<RestorePopUpPresenter> { }
  public abstract class Type_factory__o_k_w_c_s_c_d_c_DefaultCloneProcess__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultCloneProcess> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_s_c_i_PerformAutomaticLayoutCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<PerformAutomaticLayoutCommand> { }
  public abstract class Type_factory__o_k_w_c_s_c_g_p_l_s_s_VerticesTransposer__quals__j_e_i_Any_j_e_i_Default extends Factory<VerticesTransposer> { }
  public abstract class Type_factory__o_k_w_c_d_c_p_d_DocumentationLinksFieldProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<DocumentationLinksFieldProvider> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_i_g_DMNCardComponentContentView__quals__j_e_i_Any_o_k_w_c_d_c_e_i_g_DMNCard extends Factory<DMNCardComponentContentView> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_i_m_d_DMNAssetsDropdown__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNAssetsDropdown> { }
  public abstract class Producer_factory__o_u_e_e_c_c_f_e_s_SvgFileExport__quals__j_e_i_Any_j_e_i_Default extends Factory<SvgFileExport> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_s_i_DefaultViewerSession__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultViewerSession> { }
  public abstract class ExtensionProvided_factory__o_s_Logger__quals__Universal extends Factory<Logger> { }
  public abstract class Type_factory__o_u_e_c_s_i_ClientExperimentalFeaturesRegistryServiceImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientExperimentalFeaturesRegistryServiceImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DefinitionPaletteCategoryWidgetViewImpl> { }
  public abstract class Type_factory__o_u_c_m_PlaceRequestHistoryMapperImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<PlaceRequestHistoryMapperImpl> { }
  public abstract class JsType_factory__o_u_w_m_i_PanelDefinitionImpl__quals__Universal extends Factory<PanelDefinitionImpl> { }
  public abstract class Producer_factory__o_u_c_v_p_w_JQueryProducer_JQuery__quals__j_e_i_Any_j_e_i_Default extends Factory<org.uberfire.client.views.pfly.widgets.JQueryProducer.JQuery> { }
  public abstract class Type_factory__o_j_e_u_c_w_ListWidgetProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<ListWidgetProvider> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_i_b_CanvasBoundsIndexerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<CanvasBoundsIndexerImpl> { }
  public abstract class Type_factory__o_u_e_w_c_c_s_i_IFrameScreenView__quals__j_e_i_Any_j_e_i_Default extends Factory<IFrameScreenView> { }
  public abstract class Type_factory__o_u_e_l_e_c_w_LayoutDragComponentWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutDragComponentWidget> { }
  public abstract class Type_factory__o_u_e_w_c_c_s_i_IFrameScreenPresenterActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<IFrameScreenPresenterActivity> { }
  public abstract class Type_factory__o_k_w_c_s_c_f_i_NodeFactoryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<NodeFactoryImpl> { }
  public abstract class Type_factory__o_u_c_v_p_m_Bs3Modal__quals__j_e_i_Any_j_e_i_Default extends Factory<Bs3Modal> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_v_s_EmptyStateView__quals__j_e_i_Any_j_e_i_Default extends Factory<EmptyStateView> { }
  public abstract class Type_factory__o_u_e_e_c_c_f_RestoreUtil__quals__j_e_i_Any_j_e_i_Default extends Factory<RestoreUtil> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeSelector__quals__j_e_i_Any_j_e_i_Default extends Factory<DayTimeSelector> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_DateValueFormatter__quals__j_e_i_Any_j_e_i_Default extends Factory<DateValueFormatter> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_TypedValueComponentSelector__quals__j_e_i_Any_j_e_i_Default extends Factory<TypedValueComponentSelector> { }
  public abstract class Type_factory__o_k_w_c_s_c_g_u_GraphUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<GraphUtils> { }
  public abstract class Type_factory__o_u_e_l_e_c_c_c_ContainerView__quals__j_e_i_Any_j_e_i_Default extends Factory<ContainerView> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_t_a_MorphActionsToolboxFactory__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_MorphActionsToolbox extends Factory<MorphActionsToolboxFactory> { }
  public abstract class Type_factory__o_k_w_c_d_w_k_c_c_DMNKogitoCommonEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNKogitoCommonEntryPoint> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_t_a_MorphActionsToolboxView__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_MorphActionsToolbox extends Factory<MorphActionsToolboxView> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_t_a_FlowActionsToolboxView__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_FlowActionsToolbox extends Factory<FlowActionsToolboxView> { }
  public abstract class ExtensionProvided_factory__o_j_e_c_c_d_Label__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<Label> { }
  public abstract class Provider_factory__j_v_Validator__quals__j_e_i_Any_j_e_i_Default extends Factory<Validator> { }
  public abstract class Type_factory__o_u_c_w_LayoutSelection__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutSelection> { }
  public abstract class ExtensionProvided_factory__e_d_HTMLUListElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<HTMLUListElement> { }
  public abstract class Type_factory__o_k_w_c_d_s_c_e_DMNDiagramEditor__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_a_DiagramEditor extends Factory<DMNDiagramEditor> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_d_DRDContextMenuService__quals__j_e_i_Any_j_e_i_Default extends Factory<DRDContextMenuService> { }
  public abstract class Type_factory__o_k_w_c_d_c_v_DiagramElementNameProviderImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DiagramElementNameProviderImpl> { }
  public abstract class ExtensionProvided_factory__o_g_c_u_g_FlowPanel__quals__j_e_i_Any_j_e_i_Default extends Factory<FlowPanel> { }
  public abstract class Type_factory__o_k_w_c_d_c_d_n_t_DecisionNavigatorTreeView__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionNavigatorTreeView> { }
  public abstract class Type_factory__o_k_w_c_w_c_d_DockPlaceHolder__quals__j_e_i_Any_j_e_i_Default extends Factory<DockPlaceHolder> { }
  public abstract class Type_factory__o_k_w_c_s_c_g_c_GraphCommandManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<GraphCommandManagerImpl> { }
  public abstract class Type_factory__o_k_w_c_s_k_c_d_DiagramEditorPreviewAndExplorerDock__quals__j_e_i_Any_j_e_i_Default extends Factory<DiagramEditorPreviewAndExplorerDock> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_DataTypeSelect__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeSelect> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeValueConverter__quals__j_e_i_Any_j_e_i_Default extends Factory<DayTimeValueConverter> { }
  public abstract class Type_factory__o_u_e_w_c_c_CoreEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<CoreEntryPoint> { }
  public abstract class Type_factory__o_u_e_e_c_c_m_c_CurrentBranchProducer__quals__j_e_i_Any_j_e_i_Default extends Factory<CurrentBranchProducer> { }
  public abstract class Type_factory__o_u_e_p_e_c_f_SecretTextField__quals__j_e_i_Any_j_e_i_Default extends Factory<SecretTextField> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_s_DataTypeListShortcuts__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeListShortcuts> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_d_DocumentationLinksWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<DocumentationLinksWidget> { }
  public abstract class Type_factory__o_u_e_e_c_c_h_VersionMenuDropDownButtonViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<VersionMenuDropDownButtonViewImpl> { }
  public abstract class Type_factory__o_u_e_e_c_c_h_VersionHistoryPresenterViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<VersionHistoryPresenterViewImpl> { }
  public abstract class Type_factory__o_u_c_v_p_w_InputAutocomplete__quals__j_e_i_Any_j_e_i_Default extends Factory<InputAutocomplete> { }
  public abstract class Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorObserver__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionNavigatorObserver> { }
  public abstract class Type_factory__o_j_e_i_c_a_b_IOCBeanManagerProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<IOCBeanManagerProvider> { }
  public abstract class ExtensionProvided_factory__o_u_c_w_w_l_ResizeFlowPanel__quals__j_e_i_Any_j_e_i_Default extends Factory<ResizeFlowPanel> { }
  public abstract class Type_factory__o_u_c_v_p_w_KebabMenu__quals__j_e_i_Any_j_e_i_Default extends Factory<KebabMenu> { }
  public abstract class Type_factory__o_k_w_c_s_c_StunnerCoreEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerCoreEntryPoint> { }
  public abstract class Type_factory__o_u_e_p_c_c_a_PreferencesCentralActionsScreen__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferencesCentralActionsScreen> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_p_h_ItemDefinitionCreateHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<ItemDefinitionCreateHandler> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_m_d_i_LogBoundsDevCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<LogBoundsDevCommand> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeListItemView> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_DataTypeSelectView__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeSelectView> { }
  public abstract class Type_factory__o_k_w_c_s_s_m_MessageConsoleWhiteListImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<MessageConsoleWhiteListImpl> { }
  public abstract class JsType_factory__o_a_k_b_c_g_t_GuidedTourCustomSelectorPositionProvider__quals__Universal extends Factory<GuidedTourCustomSelectorPositionProvider> { }
  public abstract class Type_factory__o_u_c_m_ActivityLifecycleErrorHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<ActivityLifecycleErrorHandler> { }
  public abstract class ExtensionProvided_factory__o_j_e_c_c_d_TextInput__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<TextInput> { }
  public abstract class Type_factory__o_k_w_c_s_c_v_i_GraphValidatorImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<GraphValidatorImpl> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_p_PictureFieldRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<PictureFieldRenderer> { }
  public abstract class Type_factory__o_u_e_e_c_c_h_HtmlResourceType__quals__j_e_i_Any_j_e_i_Default extends Factory<HtmlResourceType> { }
  public abstract class Type_factory__o_k_w_c_d_s_c_e_DMNDiagramEditorActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named_o_k_w_c_s_c_c_a_DiagramEditor_o_u_c_m_IsClientEditor extends Factory<DMNDiagramEditorActivity> { }
  public abstract class Type_factory__o_a_k_b_c_k_KeyboardShortcutsServiceProducer__quals__j_e_i_Any_j_e_i_Default extends Factory<KeyboardShortcutsServiceProducer> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_e_n_i_NavigatorThumbnailItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<NavigatorThumbnailItemView> { }
  public abstract class Type_factory__o_u_e_p_e_c_f_ComboField__quals__j_e_i_Any_j_e_i_Default extends Factory<ComboField> { }
  public abstract class Type_factory__o_u_s_c_a_t_i_DefaultPermissionTreeFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultPermissionTreeFactory> { }
  public abstract class JsType_factory__o_u_s_a_RuntimeResource__quals__Universal extends Factory<RuntimeResource> { }
  public abstract class Type_factory__o_u_c_m_ActivityBeansCache__quals__j_e_i_Any_j_e_i_Default extends Factory<ActivityBeansCache> { }
  public abstract class Type_factory__o_u_c_w_w_m_m_m_ChildMenuItemPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<ChildMenuItemPresenter> { }
  public abstract class Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponentsView__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionComponentsView> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_m_d_i_LogBoundingBoxDevCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<LogBoundingBoxDevCommand> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_e_DataTypeConstraintExpressionView__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeConstraintExpressionView> { }
  public abstract class Type_factory__o_k_w_c_d_a_g_DMNDiagramUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDiagramUtils> { }
  public abstract class Type_factory__o_u_e_e_c_c_m_DownloadMenuItemBuilder__quals__j_e_i_Any_j_e_i_Default extends Factory<DownloadMenuItemBuilder> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_f_i_n_f_FieldSetFormGroup__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldSetFormGroup> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_r_StunnerCommonIconsStrip__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerCommonIconsStrip> { }
  public abstract class Type_factory__o_k_w_c_w_c_w_KieSelectOptionElement__quals__j_e_i_Any_j_e_i_Default extends Factory<KieSelectOptionElement> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_t_d_h_HitPolicyPopoverImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<HitPolicyPopoverImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_DateSelectorView__quals__j_e_i_Any_j_e_i_Default extends Factory<DateSelectorView> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_f_l_l_FieldLabel__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldLabel> { }
  public abstract class Type_factory__o_u_c_v_p_m_MultiPageEditorImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<MultiPageEditorImpl> { }
  public abstract class Type_factory__o_u_e_e_c_c_f_p_RenamePopUpPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<RenamePopUpPresenter> { }
  public abstract class Type_factory__o_g_c_s_p_c_p_SpaceScopedResolutionStrategySupplier__quals__j_e_i_Any_j_e_i_Default extends Factory<SpaceScopedResolutionStrategySupplier> { }
  public abstract class Type_factory__o_u_e_l_e_c_i_LayoutDragComponentHelper__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutDragComponentHelper> { }
  public abstract class Type_factory__o_k_w_c_s_c_g_c_i_GraphCommandFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<GraphCommandFactory> { }
  public abstract class Type_factory__o_u_c_w_w_m_m_m_GroupMenuItemPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<GroupMenuItemPresenter> { }
  public abstract class Type_factory__o_u_e_c_e_ExperimentalFeaturesEditorScreen__quals__j_e_i_Any_j_e_i_Default extends Factory<ExperimentalFeaturesEditorScreen> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_d_c_HTMLDownloadHelper__quals__j_e_i_Any_j_e_i_Default extends Factory<HTMLDownloadHelper> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_m_ZoomLevelSelector__quals__j_e_i_Any_j_e_i_Default extends Factory<ZoomLevelSelector> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_s_c_i_PasteSelectionSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<PasteSelectionSessionCommand> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_n_i_ByteEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default extends Factory<ByteEditableColumnGenerator> { }
  public abstract class Type_factory__o_u_e_l_e_c_LayoutEditorView__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutEditorView> { }
  public abstract class Type_factory__o_k_w_c_k_w_b_c_w_KogitoResourceContentService__quals__j_e_i_Any_j_e_i_Default extends Factory<KogitoResourceContentService> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_t_f_s_p_m_LiteralExpressionPMMLDocumentModelEditorDefinition__quals__j_e_i_Any_j_e_i_Default extends Factory<LiteralExpressionPMMLDocumentModelEditorDefinition> { }
  public abstract class Type_factory__o_u_c_p_Promises__quals__j_e_i_Any_j_e_i_Default extends Factory<Promises> { }
  public abstract class Type_factory__o_u_c_v_p_l_ListBarWidgetImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ListBarWidgetImpl> { }
  public abstract class Type_factory__o_u_c_m_WorkbenchViewModeSwitcherPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchViewModeSwitcherPresenter> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_e_DataTypeConstraintEnumeration__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeConstraintEnumeration> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_a_ClientProfileManager__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientProfileManager> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_i_g_DefaultCardComponentContentView__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultCardComponentContentView> { }
  public abstract class Type_factory__o_g_c_s_p_c_t_POMResourceType__quals__j_e_i_Any_j_e_i_Default extends Factory<POMResourceType> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_t_c_SwitchGridToolbarCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<SwitchGridToolbarCommand> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_t_a_GeneralCreateNodeAction__quals__j_e_i_Any_j_e_i_Default extends Factory<GeneralCreateNodeAction> { }
  public abstract class Type_factory__o_u_e_e_c_c_h_HtmlEditorLibraryLoader__quals__j_e_i_Any_j_e_i_Default extends Factory<HtmlEditorLibraryLoader> { }
  public abstract class Type_factory__o_j_e_u_c_l_p_Elemental2HTMLElementProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<Elemental2HTMLElementProvider> { }
  public abstract class ExtensionProvided_factory__o_g_c_u_h_Span__quals__j_e_i_Any_j_e_i_Default extends Factory<org.gwtbootstrap3.client.ui.html.Span> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_c_LienzoSelectionControl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_c_c_s_SingleSelection extends Factory<LienzoSelectionControl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_g_GenericSelector__quals__j_e_i_Any_j_e_i_Default extends Factory<GenericSelector> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchEngine__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeSearchEngine> { }
  public abstract class Type_factory__o_u_e_p_c_c_a_PreferencesCentralActionsScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<PreferencesCentralActionsScreenActivity> { }
  public abstract class Type_factory__o_u_e_w_c_c_t_FilterPagedTable__quals__j_e_i_Any_j_e_i_Default extends Factory<FilterPagedTable> { }
  public abstract class Type_factory__o_u_c_v_p_m_UtilityMenuBarView__quals__j_e_i_Any_j_e_i_Default extends Factory<UtilityMenuBarView> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_s_c_i_CopySelectionSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<CopySelectionSessionCommand> { }
  public abstract class Type_factory__o_u_e_l_e_c_g_BootstrapLayoutGeneratorDriver__quals__j_e_i_Any_j_e_i_Default extends Factory<BootstrapLayoutGeneratorDriver> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_r_s_w_SubFormWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<SubFormWidget> { }
  public abstract class Type_factory__o_k_w_c_d_w_k_c_c_s_DMNClientDiagramServiceImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNClientDiagramServiceImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DateTimeSelectorView__quals__j_e_i_Any_j_e_i_Default extends Factory<DateTimeSelectorView> { }
  public abstract class ContextualProvider_factory__o_j_e_u_c_w_ListWidget__quals__Universal extends Factory<ListWidget> { }
  public abstract class Type_factory__o_k_w_c_d_w_k_c_c_e_DMNEditorMenuItemsBuilder__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNEditorMenuItemsBuilder> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_e_i_DataTypeConstraintEnumerationItem__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeConstraintEnumerationItem> { }
  public abstract class Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponentFilter__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionComponentFilter> { }
  public abstract class Type_factory__o_u_e_w_c_c_d_f_LiveSearchFooterViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<LiveSearchFooterViewImpl> { }
  public abstract class ExtensionProvided_factory__c_g_g_d_c_InputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<InputElement> { }
  public abstract class Type_factory__o_u_e_l_e_c_w_LayoutElementPropertiesPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutElementPropertiesPresenter> { }
  public abstract class Type_factory__o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItem__quals__j_e_i_Any_j_e_i_Default extends Factory<LiveSearchSelectorDropDownItem> { }
  public abstract class Type_factory__o_k_w_c_f_f_s_f_b_s_l_p_ListBoxFieldProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<ListBoxFieldProvider> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_i_i_p_ItemDefinitionHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<ItemDefinitionHandler> { }
  public abstract class Type_factory__o_k_w_c_f_a_e_s_f_p_f_f_HasMaxLengthFieldInitializer__quals__j_e_i_Any_j_e_i_Default extends Factory<HasMaxLengthFieldInitializer> { }
  public abstract class Type_factory__o_k_w_c_s_f_c_w_c_d_FormDisplayerViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FormDisplayerViewImpl> { }
  public abstract class Type_factory__o_u_c_w_p_i_SplitLayoutPanelPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<SplitLayoutPanelPresenter> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_y_m_YearsMonthsValueConverter__quals__j_e_i_Any_j_e_i_Default extends Factory<YearsMonthsValueConverter> { }
  public abstract class Type_factory__o_u_c_w_w_m_m_c_GroupContextMenuItemPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<GroupContextMenuItemPresenter> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_p_ConnectorProxy__quals__j_e_i_Any_j_e_i_Default extends Factory<ConnectorProxy> { }
  public abstract class Type_factory__o_u_e_w_c_c_e_d_DefaultFileEditorView__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultFileEditorView> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_c_e_DataTypeNameIsBlankErrorMessage__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeNameIsBlankErrorMessage> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_c_LienzoCanvasCommandFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoCanvasCommandFactory> { }
  public abstract class Type_factory__o_k_w_c_d_c_c_c_a_DRGElementTextPropertyProviderImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DRGElementTextPropertyProviderImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_c_ControlPointControlImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ControlPointControlImpl> { }
  public abstract class JsType_factory__o_u_w_m_m_MenuCustom__quals__Universal extends Factory<MenuCustom> { }
  public abstract class Type_factory__o_k_w_c_d_c_m_i_DMNMarshallerImportsClientHelper__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNMarshallerImportsClientHelper> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_i_g_DefaultCardComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultCardComponent> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerPresenterViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DateTimePickerPresenterViewImpl> { }
  public abstract class JsType_factory__o_j_e_e_c_c_WindowEventObservers__quals__Universal extends Factory<WindowEventObservers> { }
  public abstract class Type_factory__o_k_w_c_f_a_e_s_f_p_f_f_s_IntegerSliderFieldInitializer__quals__j_e_i_Any_j_e_i_Default extends Factory<IntegerSliderFieldInitializer> { }
  public abstract class Type_factory__o_u_c_w_w_d_WorkbenchPickupDragController__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchPickupDragController> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_i_g_PMMLCardComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<PMMLCardComponent> { }
  public abstract class Type_factory__o_u_e_w_c_c_e_t_TextResourceType__quals__j_e_i_Any_j_e_i_Default extends Factory<TextResourceType> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_g_ShapeGlyphDragHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<ShapeGlyphDragHandler> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_p_s_i_SessionPreviewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<SessionPreviewImpl> { }
  public abstract class Producer_factory__o_a_k_b_c_i_I18nApi__quals__j_e_i_Any_j_e_i_Default extends Factory<I18nApi> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<DefinitionPaletteCategoryWidget> { }
  public abstract class Type_factory__o_u_c_v_p_w_DropdownMenuItem__quals__j_e_i_Any_j_e_i_Default extends Factory<DropdownMenuItem> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesRegistries__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerPreferencesRegistries> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_s_DataTypeShortcuts__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeShortcuts> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_t_c_ExportToBpmnToolbarCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<ExportToBpmnToolbarCommand> { }
  public abstract class Type_factory__o_k_w_c_d_a_r_AcyclicDirectedGraphRule__quals__j_e_i_Any_j_e_i_Default extends Factory<AcyclicDirectedGraphRule> { }
  public abstract class Type_factory__o_g_c_s_p_p_GeneralPreferencesBeanGeneratedImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<GeneralPreferencesBeanGeneratedImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesRegistryLoader__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerPreferencesRegistryLoader> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_t_c_ExportToPdfToolbarCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<ExportToPdfToolbarCommand> { }
  public abstract class Type_factory__o_u_e_p_c_c_t_TreeHierarchyLeafItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeHierarchyLeafItemView> { }
  public abstract class Type_factory__o_u_c_w_w_m_m_WorkbenchMegaMenuProducer__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchMegaMenuProducer> { }
  public abstract class Type_factory__o_u_c_m_CategoriesManagerCache__quals__j_e_i_Any_j_e_i_Default extends Factory<CategoriesManagerCache> { }
  public abstract class ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<Div> { }
  public abstract class Type_factory__o_k_w_c_d_c_w_t_DMNViewerToolbar__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNViewerToolbar> { }
  public abstract class Type_factory__o_k_w_c_s_k_c_s_DiagramEditorExplorerScreenView__quals__j_e_i_Any_j_e_i_Default extends Factory<DiagramEditorExplorerScreenView> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_r_m_b_StaticBindingHelper__quals__j_e_i_Any_j_e_i_Default extends Factory<StaticBindingHelper> { }
  public abstract class Type_factory__o_u_c_m_ResourceTypeManagerCache__quals__j_e_i_Any_j_e_i_Default extends Factory<ResourceTypeManagerCache> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_s_c_i_SaveDiagramSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<org.kie.workbench.common.stunner.core.client.session.command.impl.SaveDiagramSessionCommand> { }
  public abstract class JsType_factory__j_u_Collection__quals__Universal extends Factory<Collection> { }
  public abstract class JsType_factory__o_u_w_t_ResourceTypeDefinition__quals__Universal extends Factory<ResourceTypeDefinition> { }
  public abstract class Type_factory__o_k_w_c_w_c_m_FileMenuBuilderImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FileMenuBuilderImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_s_f_DelegateShapeFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DelegateShapeFactory> { }
  public abstract class Type_factory__o_g_c_s_p_c_Model__quals__j_e_i_Any_j_e_i_Default extends Factory<Model> { }
  public abstract class Type_factory__o_k_w_c_d_c_g_DMNGraphUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNGraphUtils> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_s_r_StringRadioGroupFieldRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<StringRadioGroupFieldRenderer> { }
  public abstract class Type_factory__o_u_e_p_c_a_AdminPagePresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<AdminPagePresenter> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_c_p_DMNPageView__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNPageView> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_p_ProfileSelector__quals__j_e_i_Any_j_e_i_Default extends Factory<ProfileSelector> { }
  public abstract class Producer_factory__o_j_e_s_s_a_i_User__quals__j_e_i_Any_j_e_i_Default extends Factory<User> { }
  public abstract class Type_factory__o_k_w_c_k_w_b_s_PreferenceScopeTypesMock__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferenceScopeTypesMock> { }
  public abstract class Type_factory__o_j_e_d_c_DataBinderProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<DataBinderProvider> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_v_AnimatedFloatingWidgetView__quals__j_e_i_Any_j_e_i_Default extends Factory<AnimatedFloatingWidgetView> { }
  public abstract class Type_factory__o_u_c_w_p_n_p_WorkbenchPartPresenterDefault__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchPartPresenterDefault> { }
  public abstract class Type_factory__o_k_w_c_s_f_c_s_c_GenerateProcessFormsSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<GenerateProcessFormsSessionCommand> { }
  public abstract class Type_factory__o_u_e_e_c_c_f_p_SavePopUpView__quals__j_e_i_Any_j_e_i_Default extends Factory<SavePopUpView> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_t_i_ManagedViewerToolbar__quals__j_e_i_Any_j_e_i_Default extends Factory<ManagedViewerToolbar> { }
  public abstract class Type_factory__o_k_w_c_k_w_b_c_w_IsKogito__quals__j_e_i_Any_j_e_i_Default extends Factory<IsKogito> { }
  public abstract class Type_factory__o_k_w_c_d_c_d_p_PreviewDiagramScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<PreviewDiagramScreenActivity> { }
  public abstract class JsType_factory__o_u_w_m_t_ToolBarItem__quals__Universal extends Factory<ToolBarItem> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_LienzoLayer__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoLayer> { }
  public abstract class Type_factory__o_u_c_v_p_m_WorkbenchMenuCompactNavBarView__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchMenuCompactNavBarView> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_r_DataTypeConstraintRange__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeConstraintRange> { }
  public abstract class ExtensionProvided_factory__o_g_c_u_TextArea__quals__j_e_i_Any_j_e_i_Default extends Factory<TextArea> { }
  public abstract class ExtensionProvided_factory__e_d_HTMLLabelElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<HTMLLabelElement> { }
  public abstract class Type_factory__o_u_c_v_p_p_PopupViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<PopupViewImpl> { }
  public abstract class Type_factory__o_u_c_v_p_w_ConfirmPopup__quals__j_e_i_Any_j_e_i_Default extends Factory<ConfirmPopup> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_t_a_DeleteNodeToolboxAction__quals__j_e_i_Any_j_e_i_Default extends Factory<DeleteNodeToolboxAction> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_t_f_k_KindPopoverImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<KindPopoverImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_c_p_DMNPaletteDefinitionBuilder__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNPaletteDefinitionBuilder> { }
  public abstract class Type_factory__o_j_e_e_c_c_JsTypeEventObserver__quals__j_e_i_Any_j_e_i_Default extends Factory<JsTypeEventObserver> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_v_SelectorImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<SelectorImpl> { }
  public abstract class Type_factory__o_u_e_l_e_c_c_c_ColumnWithComponents__quals__j_e_i_Any_j_e_i_Default extends Factory<ColumnWithComponents> { }
  public abstract class Type_factory__o_u_e_c_d_c_DisabledFeatureComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<DisabledFeatureComponent> { }
  public abstract class JsType_factory__o_u_c_m_LockTarget__quals__Universal extends Factory<LockTarget> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_t_c_ClearToolbarCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<ClearToolbarCommand> { }
  public abstract class Type_factory__o_k_w_c_s_f_c_w_c_d_FormDisplayer__quals__j_e_i_Any_j_e_i_Default extends Factory<FormDisplayer> { }
  public abstract class Type_factory__o_u_e_l_e_c_i_LayoutEditorCssHelper__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutEditorCssHelper> { }
  public abstract class Type_factory__o_u_e_w_c_c_d_n_NoItemsComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<NoItemsComponent> { }
  public abstract class JsType_factory__o_u_w_m_PanelDefinition__quals__Universal extends Factory<PanelDefinition> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_c_k_KeyboardControlImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<KeyboardControlImpl> { }
  public abstract class Type_factory__o_u_e_e_c_c_f_CopyPopUpViewProducer__quals__j_e_i_Any_j_e_i_Default extends Factory<CopyPopUpViewProducer> { }
  public abstract class Type_factory__o_k_w_c_d_a_p_d_d_ConstraintTypeDataProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<ConstraintTypeDataProvider> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_d_a_b_ClientBindableMorphAdapter__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientBindableMorphAdapter> { }
  public abstract class ExtensionProvided_factory__o_j_e_c_c_d_Heading__quals__j_e_i_Any_j_e_i_Default_j_i_Named_1 extends Factory<Heading> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_e_t_TreeExplorerView__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeExplorerView> { }
  public abstract class Type_factory__o_k_w_c_w_c_h_w_c_WorkbenchConfigurationMenu__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchConfigurationMenu> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_f_i_s_SliderFormGroup__quals__j_e_i_Any_j_e_i_Default extends Factory<SliderFormGroup> { }
  public abstract class Type_factory__o_j_e_e_c_c_WindowEventObservers__quals__j_e_i_Any_j_e_i_Default extends Factory<WindowEventObservers> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_n_d_FloatEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default extends Factory<FloatEditableColumnGenerator> { }
  public abstract class Type_factory__o_u_e_w_c_c_c_BusyIndicatorView__quals__j_e_i_Any_j_e_i_Default extends Factory<BusyIndicatorView> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_p_s_i_SessionCardinalityStateHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<SessionCardinalityStateHandler> { }
  public abstract class Type_factory__o_u_e_w_c_c_m_MenuItemDividerView__quals__j_e_i_Any_o_u_e_w_c_c_m_MenuItemDivider extends Factory<MenuItemDividerView> { }
  public abstract class Type_factory__o_k_w_c_f_c_c_c_f_m_ModalFormDisplayer__quals__j_e_i_Any_j_e_i_Default extends Factory<ModalFormDisplayer> { }
  public abstract class Type_factory__o_k_w_c_f_c_c_c_f_e_EmbeddedFormDisplayer__quals__j_e_i_Any_j_e_i_Default extends Factory<EmbeddedFormDisplayer> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerPreferencesEntryPoint> { }
  public abstract class JsType_factory__o_j_e_i_c_JsArray__quals__Universal extends Factory<JsArray> { }
  public abstract class Type_factory__o_u_e_c_p_ExperimentalFeaturesPerspective__quals__j_e_i_Any_j_e_i_Default extends Factory<ExperimentalFeaturesPerspective> { }
  public abstract class Type_factory__o_k_w_c_w_c_m_AboutCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<AboutCommand> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_EditableColumnGeneratorManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<EditableColumnGeneratorManagerImpl> { }
  public abstract class Type_factory__o_u_e_e_c_c_h_VersionMenuDropDownButton__quals__j_e_i_Any_j_e_i_Default extends Factory<VersionMenuDropDownButton> { }
  public abstract class Type_factory__o_g_c_s_p_c_GAVEditorViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<GAVEditorViewImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_m_u_n_NodeConnector__quals__j_e_i_Any_j_e_i_Default extends Factory<NodeConnector> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_p_h_DataTypeCreateHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeCreateHandler> { }
  public abstract class Type_factory__o_a_k_b_c_g_t_GuidedTourCustomSelectorPositionProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<GuidedTourCustomSelectorPositionProvider> { }
  public abstract class Type_factory__o_u_c_w_p_i_TemplatedWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<TemplatedWorkbenchPanelPresenter> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_DynamicFormRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<DynamicFormRenderer> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_u_SvgDataUriGenerator__quals__j_e_i_Any_j_e_i_Default extends Factory<SvgDataUriGenerator> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_SliderFieldRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<SliderFieldRenderer> { }
  public abstract class Type_factory__o_k_w_c_w_c_p_t_TextBoxFormPopupViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<TextBoxFormPopupViewImpl> { }
  public abstract class Type_factory__o_j_e_i_c_l_i_StateChangeImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<StateChangeImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_w_t_DMNLayoutHelper__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNLayoutHelper> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_StunnerWidgetsEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerWidgetsEntryPoint> { }
  public abstract class Type_factory__o_u_e_p_c_u_PreferenceFormBeansInfo__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferenceFormBeansInfo> { }
  public abstract class Type_factory__o_u_e_l_e_c_w_KebabWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<KebabWidget> { }
  public abstract class Type_factory__o_u_c_v_p_w_ErrorPopup__quals__j_e_i_Any_j_e_i_Default extends Factory<ErrorPopup> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_i_g_PMMLCardComponentContentView__quals__j_e_i_Any_o_k_w_c_d_c_e_i_g_PMMLCard extends Factory<PMMLCardComponentContentView> { }
  public abstract class Type_factory__o_j_e_u_c_l_s_TranslationServiceProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<TranslationServiceProvider> { }
  public abstract class Type_factory__o_u_p_c_s_ClientUsernameProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientUsernameProvider> { }
  public abstract class Type_factory__o_k_w_c_d_c_s_DMNEditorSession__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNEditorSession> { }
  public abstract class Type_factory__o_u_c_w_p_i_AdaptiveWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<AdaptiveWorkbenchPanelPresenter> { }
  public abstract class Type_factory__o_k_w_c_w_c_w_KieSelectElementView__quals__j_e_i_Any_j_e_i_Default extends Factory<KieSelectElementView> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_m_d_MenuDevCommandsBuilder__quals__j_e_i_Any_j_e_i_Default extends Factory<MenuDevCommandsBuilder> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_f_i_c_CheckBoxFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<CheckBoxFormGroupViewImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_p_s_i_SessionViewerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<SessionViewerImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_d_c_CloneManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<CloneManagerImpl> { }
  public abstract class JsType_factory__o_u_c_w_t_ClientResourceType__quals__Universal extends Factory<ClientResourceType> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_v_LoadingBoxView__quals__j_e_i_Any_j_e_i_Default extends Factory<LoadingBoxView> { }
  public abstract class Type_factory__o_k_w_c_w_c_a_d_KogitoKieAssetsDropdownView__quals__j_e_i_Any_j_e_i_Default extends Factory<KogitoKieAssetsDropdownView> { }
  public abstract class Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<DefinitionUtils> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_i_i_p_DMNIncludedModelHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNIncludedModelHandler> { }
  public abstract class JsType_factory__o_u_w_m_ActivityResourceType__quals__Universal extends Factory<ActivityResourceType> { }
  public abstract class Type_factory__o_u_c_w_p_u_p_UnanchoredWorkbenchPartPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<UnanchoredWorkbenchPartPresenter> { }
  public abstract class Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default extends Factory<SyncBeanManager> { }
  public abstract class Type_factory__o_u_e_l_e_c_c_r_Row__quals__j_e_i_Any_j_e_i_Default extends Factory<Row> { }
  public abstract class Type_factory__o_k_w_c_w_c_h_PackageListBoxViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<PackageListBoxViewImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_p_DefaultPreferencesRegistry__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultPreferencesRegistry> { }
  public abstract class Type_factory__o_u_e_w_c_c_w_WizardViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<WizardViewImpl> { }
  public abstract class ExtensionProvided_factory__o_u_c_w_w_p_StaticFocusedResizePanel__quals__j_e_i_Any_j_e_i_Default extends Factory<StaticFocusedResizePanel> { }
  public abstract class Type_factory__o_u_c_w_p_n_p_WorkbenchPartView__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchPartView> { }
  public abstract class Type_factory__o_u_c_w_w_m_m_c_ChildContextMenuItemPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<ChildContextMenuItemPresenter> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_t_f_s_j_JavaFunctionEditorDefinition__quals__j_e_i_Any_o_k_w_c_d_c_e_e_t_f_FunctionGridSupplementaryEditor extends Factory<JavaFunctionEditorDefinition> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_d_DMNDocumentationViewButtonsVisibilitySupplier__quals__j_e_i_Any_j_e_i_Default extends Factory<org.kie.workbench.common.dmn.client.editors.documentation.DMNDocumentationViewButtonsVisibilitySupplier> { }
  public abstract class Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default extends Factory<TranslationService> { }
  public abstract class Type_factory__o_u_c_v_p_m_m_m_IconMenuItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<IconMenuItemView> { }
  public abstract class Type_factory__o_u_c_w_d_UberfireDocksContainer__quals__j_e_i_Any_j_e_i_Default extends Factory<UberfireDocksContainer> { }
  public abstract class Type_factory__o_k_w_c_k_w_b_c_p_AuthoringPerspective__quals__j_e_i_Any_j_e_i_Default extends Factory<AuthoringPerspective> { }
  public abstract class Type_factory__o_k_w_c_s_k_c_s_EditorSessionCommands__quals__j_e_i_Any_j_e_i_Default extends Factory<EditorSessionCommands> { }
  public abstract class Type_factory__o_u_e_l_e_c_c_c_ComponentColumnView__quals__j_e_i_Any_j_e_i_Default extends Factory<ComponentColumnView> { }
  public abstract class Type_factory__o_k_w_c_k_w_b_c_p_PerspectiveConfiguration__quals__j_e_i_Any_j_e_i_Default extends Factory<PerspectiveConfiguration> { }
  public abstract class Type_factory__o_k_w_c_d_c_m_u_DMNUnmarshaller__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNUnmarshaller> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_t_r_RelationEditorDefinition__quals__j_e_i_Any_j_e_i_Default extends Factory<RelationEditorDefinition> { }
  public abstract class Type_factory__o_k_w_c_w_c_p_a_AboutPopupView__quals__j_e_i_Any_j_e_i_Default extends Factory<AboutPopupView> { }
  public abstract class Producer_factory__o_a_k_b_c_r_ResourceContentService__quals__j_e_i_Any_j_e_i_Default extends Factory<ResourceContentService> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_s_r_IntegerRadioGroupFieldRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<IntegerRadioGroupFieldRenderer> { }
  public abstract class Type_factory__o_k_w_c_d_c_w_g_c_c_CellEditorControlsViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<CellEditorControlsViewImpl> { }
  public abstract class ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal extends Factory<Caller> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_s_c_i_ExportToPngSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<ExportToPngSessionCommand> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_i_FormHandlerGeneratorManager__quals__j_e_i_Any_j_e_i_Default extends Factory<FormHandlerGeneratorManager> { }
  public abstract class JsType_factory__o_u_s_ResourceAction__quals__Universal extends Factory<ResourceAction> { }
  public abstract class Type_factory__o_u_c_w_VFSServiceProxyClientImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<VFSServiceProxyClientImpl> { }
  public abstract class Type_factory__o_u_e_l_e_c_LayoutEditorEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutEditorEntryPoint> { }
  public abstract class JsType_factory__o_u_s_a_ResourceActionRef__quals__Universal extends Factory<ResourceActionRef> { }
  public abstract class Type_factory__o_g_c_s_p_c_Optimization__quals__j_e_i_Any_j_e_i_Default extends Factory<Optimization> { }
  public abstract class Type_factory__o_k_w_c_f_a_e_s_f_p_f_f_s_DoubleSliderFieldInitializer__quals__j_e_i_Any_j_e_i_Default extends Factory<DoubleSliderFieldInitializer> { }
  public abstract class Type_factory__o_k_w_c_s_f_s_f_ColorPickerFieldProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<ColorPickerFieldProvider> { }
  public abstract class Type_factory__o_u_e_w_c_c_m_MenuItemWithIconView__quals__j_e_i_Any_o_u_e_w_c_c_m_MenuItemWithIcon extends Factory<MenuItemWithIconView> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_DataTypeList__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeList> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_DynamicFormRendererViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DynamicFormRendererViewImpl> { }
  public abstract class Type_factory__o_u_e_p_c_a_c_AdminPageCategoryPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<AdminPageCategoryPresenter> { }
  public abstract class Type_factory__o_u_e_c_UberfireExperimentalEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<UberfireExperimentalEntryPoint> { }
  public abstract class Type_factory__o_u_e_l_e_c_c_c_ComponentColumnPart__quals__j_e_i_Any_j_e_i_Default extends Factory<ComponentColumnPart> { }
  public abstract class Producer_factory__o_u_p_s_PreferenceScopeTypes__quals__j_e_i_Any_o_u_a_Customizable extends Factory<PreferenceScopeTypes> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_t_f_s_p_PMMLDocumentMetadataProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<PMMLDocumentMetadataProvider> { }
  public abstract class Type_factory__o_u_e_l_e_c_w_LayoutDragComponentGroupView__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutDragComponentGroupView> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_p_d_i_DefaultDiagramViewer__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultDiagramViewer> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_c_ScrollableLienzoPanel__quals__j_e_i_Any_j_e_i_Default extends Factory<ScrollableLienzoPanel> { }
  public abstract class Type_factory__o_u_w_m_i_PanelDefinitionImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<PanelDefinitionImpl> { }
  public abstract class Type_factory__o_k_w_c_s_f_c_f_c_ColorPickerFieldRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<ColorPickerFieldRenderer> { }
  public abstract class Type_factory__o_u_e_e_c_f_e_FileExportsPreferencesBeanGeneratedImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FileExportsPreferencesBeanGeneratedImpl> { }
  public abstract class Type_factory__o_k_w_c_f_f_s_f_b_t_p_TextAreaFieldProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<TextAreaFieldProvider> { }
  public abstract class Type_factory__o_k_w_c_d_w_k_c_c_t_GuidedTourBridgeInitializer__quals__j_e_i_Any_j_e_i_Default extends Factory<GuidedTourBridgeInitializer> { }
  public abstract class ExtensionProvided_factory__o_g_c_u_SimpleCheckBox__quals__j_e_i_Any_j_e_i_Default extends Factory<SimpleCheckBox> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_r_ClientRuleManager__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientRuleManager> { }
  public abstract class Type_factory__o_k_w_c_k_w_b_s_PreferenceScopeResolutionStrategyMock__quals__j_e_i_Any_o_u_a_Customizable extends Factory<PreferenceScopeResolutionStrategyMock> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_f_l_r_FieldRequiredViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldRequiredViewImpl> { }
  public abstract class Type_factory__o_k_w_c_s_f_c_s_c_GenerateSelectedFormsSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<GenerateSelectedFormsSessionCommand> { }
  public abstract class Type_factory__o_k_w_c_d_w_k_c_c_s_DMNEditorSessionCommands__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNEditorSessionCommands> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_t_a_ToolboxDomainLookups__quals__j_e_i_Any_j_e_i_Default extends Factory<ToolboxDomainLookups> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_t_i_ManagedEditorToolbar__quals__j_e_i_Any_j_e_i_Default extends Factory<ManagedEditorToolbar> { }
  public abstract class Type_factory__o_u_e_p_c_c_t_TreeHierarchyInternalItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeHierarchyInternalItemView> { }
  public abstract class Type_factory__o_u_c_v_p_m_MultiScreenView__quals__j_e_i_Any_j_e_i_Default extends Factory<MultiScreenView> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_LienzoImageStrips__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoImageStrips> { }
  public abstract class Type_factory__o_u_e_c_e_g_f_ExperimentalFeatureEditorViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ExperimentalFeatureEditorViewImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_i_t_TreeListItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeListItemView> { }
  public abstract class Type_factory__o_k_w_c_w_c_d_DefaultEditorDock__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultEditorDock> { }
  public abstract class Type_factory__o_k_w_c_s_c_r_e_RuleExtensionMultiHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<RuleExtensionMultiHandler> { }
  public abstract class Type_factory__o_u_c_w_p_i_AdaptiveWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<AdaptiveWorkbenchPanelView> { }
  public abstract class Type_factory__o_u_c_w_w_d_WorkbenchDragAndDropManager__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchDragAndDropManager> { }
  public abstract class Type_factory__o_k_w_c_d_c_s_NodeTextSetter__quals__j_e_i_Any_j_e_i_Default extends Factory<NodeTextSetter> { }
  public abstract class Type_factory__o_u_e_l_e_c_LayoutEditorPropertiesScreen__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutEditorPropertiesScreen> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_m_d_i_LogCommandHistoryDevCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<LogCommandHistoryDevCommand> { }
  public abstract class Type_factory__o_g_c_s_p_e_t_POMResourceTypeDefinition__quals__j_e_i_Any_j_e_i_Default extends Factory<POMResourceTypeDefinition> { }
  public abstract class Type_factory__o_u_c_m_LockManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<LockManagerImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_t_u_s_UndefinedExpressionSelectorPopoverImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<UndefinedExpressionSelectorPopoverImpl> { }
  public abstract class Type_factory__o_u_e_w_c_c_s_SelectComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<SelectComponent> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsIndex__quals__j_e_i_Any_j_e_i_Default extends Factory<IncludedModelsIndex> { }
  public abstract class Type_factory__o_u_c_m_PerspectiveManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<PerspectiveManagerImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_d_c_NoneCloneProcess__quals__j_e_i_Any_j_e_i_Default extends Factory<NoneCloneProcess> { }
  public abstract class Type_factory__o_k_w_c_f_f_s_f_b_i_p_IntegerBoxFieldProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<IntegerBoxFieldProvider> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_FormGeneratorDriver__quals__j_e_i_Any_j_e_i_Default extends Factory<FormGeneratorDriver> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_m_LienzoCanvasMediators__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoCanvasMediators> { }
  public abstract class Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorItemsProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionNavigatorItemsProvider> { }
  public abstract class Type_factory__o_k_w_c_d_c_w_t_DMNEditorToolbar__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNEditorToolbar> { }
  public abstract class JsType_factory__o_a_c_k_KeyboardShortcutsApiOpts__quals__Universal extends Factory<KeyboardShortcutsApiOpts> { }
  public abstract class Type_factory__o_u_c_w_StandaloneEditorPerspective__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<StandaloneEditorPerspective> { }
  public abstract class Type_factory__o_u_e_e_c_c_f_p_CopyPopUpView__quals__j_e_i_Any_o_u_a_FallbackImplementation extends Factory<CopyPopUpView> { }
  public abstract class ContextualProvider_factory__o_j_e_i_c_a_InitBallot__quals__Universal extends Factory<InitBallot> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_p_c_CollapsedDefinitionPaletteItemWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<CollapsedDefinitionPaletteItemWidgetViewImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_g_p_t_c_ViewTraverseProcessorImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ViewTraverseProcessorImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_e_DiagramClientErrorHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<DiagramClientErrorHandler> { }
  public abstract class Type_factory__o_k_w_c_d_c_p_d_DocumentationLinksFieldRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<DocumentationLinksFieldRenderer> { }
  public abstract class Type_factory__o_u_c_w_p_i_ImmutableWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<ImmutableWorkbenchPanelView> { }
  public abstract class ExtensionProvided_factory__o_j_e_c_c_d_Input__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<Input> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_c_DataTypeManagerStackStore__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeManagerStackStore> { }
  public abstract class Producer_factory__o_a_k_b_c_w_WorkspaceService__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkspaceService> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<DateTimePickerPresenter> { }
  public abstract class Type_factory__o_u_c_u_Clipboard__quals__j_e_i_Any_j_e_i_Default extends Factory<Clipboard> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_s_DMNGridSubIndex__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNGridSubIndex> { }
  public abstract class Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelectorViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ListSelectorViewImpl> { }
  public abstract class Type_factory__o_u_c_v_p_t_TabPanelWithDropdowns__quals__j_e_i_Any_j_e_i_Default extends Factory<TabPanelWithDropdowns> { }
  public abstract class Type_factory__o_u_e_l_e_c_c_r_RowView__quals__j_e_i_Any_j_e_i_Default extends Factory<RowView> { }
  public abstract class Type_factory__o_k_w_c_d_a_d_m_f_p_ModuleFormGenerationResourcesProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<ModuleFormGenerationResourcesProvider> { }
  public abstract class Type_factory__o_k_w_c_d_c_d_n_c_CanvasFocusUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<CanvasFocusUtils> { }
  public abstract class Type_factory__o_k_w_c_f_c_r_c_w_p_w_PictureWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<PictureWidget> { }
  public abstract class Type_factory__o_k_w_c_w_c_k_IsKogito__quals__j_e_i_Any_j_e_i_Default extends Factory<org.kie.workbench.common.widgets.client.kogito.IsKogito> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_s_c_i_CutSelectionSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<CutSelectionSessionCommand> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_s_DMNDataTypesSubIndex__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDataTypesSubIndex> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_s_c_i_DeleteSelectionSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<DeleteSelectionSessionCommand> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_v_AlertView__quals__j_e_i_Any_j_e_i_Default extends Factory<AlertView> { }
  public abstract class Type_factory__o_k_w_c_w_c_h_w_c_LanguageConfigurationHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<LanguageConfigurationHandler> { }
  public abstract class Type_factory__o_u_e_p_c_a_p_AdminPageImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<AdminPageImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_p_PopupUtil__quals__j_e_i_Any_j_e_i_Default extends Factory<PopupUtil> { }
  public abstract class Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelectorDividerItemViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ListSelectorDividerItemViewImpl> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_FieldRendererManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldRendererManagerImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_s_c_i_SwitchGridSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<SwitchGridSessionCommand> { }
  public abstract class Type_factory__o_u_e_p_c_a_AdminPagePerspective__quals__j_e_i_Any_j_e_i_Default extends Factory<AdminPagePerspective> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_c_g_DOMGlyphRenderers__quals__j_e_i_Any_j_e_i_Default extends Factory<DOMGlyphRenderers> { }
  public abstract class Type_factory__o_u_e_l_e_c_c_c_Container__quals__j_e_i_Any_j_e_i_Default extends Factory<Container> { }
  public abstract class Type_factory__o_k_w_c_d_c_d_n_f_DecisionNavigatorItemFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionNavigatorItemFactory> { }
  public abstract class Type_factory__o_u_c_w_w_m_m_m_IconMenuItemPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<IconMenuItemPresenter> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_m_d_i_LogDefinitionDevCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<LogDefinitionDevCommand> { }
  public abstract class Type_factory__o_u_e_l_e_c_c_r_EmptyDropRowView__quals__j_e_i_Any_j_e_i_Default extends Factory<EmptyDropRowView> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_s_l_CharacterListBoxFieldRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<CharacterListBoxFieldRenderer> { }
  public abstract class Type_factory__o_k_w_c_f_f_s_f_b_s_p_SliderFieldProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<SliderFieldProvider> { }
  public abstract class Type_factory__o_u_c_DocksEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<DocksEntryPoint> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_v_DataTypeNameFormatValidator__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeNameFormatValidator> { }
  public abstract class Type_factory__o_u_e_c_e_ExperimentalFeaturesEditorScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<ExperimentalFeaturesEditorScreenActivity> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_f_l_r_FieldRequired__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldRequired> { }
  public abstract class Producer_factory__o_u_e_e_c_c_f_e_ImageFileExport__quals__j_e_i_Any_j_e_i_Default extends Factory<ImageFileExport> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_s_ClientFieldManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientFieldManagerImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_m_d_ShapeSetsMenuItemsBuilder__quals__j_e_i_Any_j_e_i_Default extends Factory<ShapeSetsMenuItemsBuilder> { }
  public abstract class JsType_factory__j_u_Set__quals__Universal extends Factory<Set> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_e_i_DataTypeConstraintEnumerationItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeConstraintEnumerationItemView> { }
  public abstract class Producer_factory__e_d_HTMLDocument__quals__j_e_i_Any_j_e_i_Default extends Factory<HTMLDocument> { }
  public abstract class Type_factory__o_k_w_c_d_c_c_c_t_DMNEditDecisionToolboxAction__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNEditDecisionToolboxAction> { }
  public abstract class Type_factory__o_k_w_c_s_k_r_c_s_c_i_KogitoRedoSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<KogitoRedoSessionCommand> { }
  public abstract class Type_factory__o_k_w_c_d_a_r_DMNDefinitionSetResourceType__quals__j_e_i_Any_j_e_i_Default_o_u_w_a_VisibleAsset extends Factory<DMNDefinitionSetResourceType> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_e_s_ScreenEventPublisher__quals__j_e_i_Any_j_e_i_Default extends Factory<ScreenEventPublisher> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsPageStateProviderImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<IncludedModelsPageStateProviderImpl> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_f_i_c_ConfigErrorDisplayer__quals__j_e_i_Any_j_e_i_Default extends Factory<ConfigErrorDisplayer> { }
  public abstract class Type_factory__o_u_e_w_c_c_w_WizardPageTitle__quals__j_e_i_Any_j_e_i_Default extends Factory<WizardPageTitle> { }
  public abstract class Type_factory__o_u_e_e_c_c_h_SaveButton__quals__j_e_i_Any_j_e_i_Default extends Factory<SaveButton> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPagePresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<IncludedModelsPagePresenter> { }
  public abstract class Type_factory__o_k_w_c_w_c_p_l_FormListPopupViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FormListPopupViewImpl> { }
  public abstract class Type_factory__o_k_w_c_s_f_c_f_c_ModuleFieldRendererTypesProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<ModuleFieldRendererTypesProvider> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_v_LienzoCanvasNotificationView__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoCanvasNotificationView> { }
  public abstract class Type_factory__o_u_e_c_d_s_DisabledFeatureActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<DisabledFeatureActivity> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_s_ClientDiagramServiceImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientDiagramServiceImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_t_f_FunctionEditorDefinition__quals__j_e_i_Any_j_e_i_Default extends Factory<FunctionEditorDefinition> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_c_LocationControlImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<LocationControlImpl> { }
  public abstract class Type_factory__o_k_w_c_w_c_p_l_FormListPopup__quals__j_e_i_Any_j_e_i_Default extends Factory<FormListPopup> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_t_a_CommonActionsToolboxView__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_CommonActionsToolbox extends Factory<CommonActionsToolboxView> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_r_m_c_i_StringColumnGenerator__quals__j_e_i_Any_j_e_i_Default extends Factory<StringColumnGenerator> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_w_StunnerWiresHandlerFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerWiresHandlerFactory> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_s_c_i_ExportToRawFormatSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<ExportToRawFormatSessionCommand> { }
  public abstract class Type_factory__o_u_e_w_c_c_w_w_p_a_n_ActivityNotFoundPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<ActivityNotFoundPresenter> { }
  public abstract class Type_factory__o_j_e_i_c_l_i_AccessImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<AccessImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_p_s_i_SessionPresenterView__quals__j_e_i_Any_j_e_i_Default extends Factory<SessionPresenterView> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_t_i_ToolbarItem__quals__j_e_i_Any_j_e_i_Default extends Factory<ToolbarItem> { }
  public abstract class Type_factory__o_u_c_v_p_t_WorkbenchToolBarView__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchToolBarView> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraintView__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeConstraintView> { }
  public abstract class ExtensionProvided_factory__e_d_HTMLOptionElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<HTMLOptionElement> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_TextBoxFieldRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<TextBoxFieldRenderer> { }
  public abstract class Type_factory__o_k_w_c_w_c_d_AsyncPackageDataModelOracleImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<AsyncPackageDataModelOracleImpl> { }
  public abstract class Type_factory__o_u_e_w_c_c_t_PagedTable__quals__j_e_i_Any_j_e_i_Default extends Factory<PagedTable> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_s_c_i_ExportToPdfSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<ExportToPdfSessionCommand> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_i_i_p_ImportRecordEngine__quals__j_e_i_Any_j_e_i_Default extends Factory<ImportRecordEngine> { }
  public abstract class Type_factory__o_k_w_c_w_c_p_v_ValidationPopup__quals__j_e_i_Any_j_e_i_Default extends Factory<ValidationPopup> { }
  public abstract class Type_factory__o_k_w_c_f_s_s_m_p_i_p_FieldRequiredEntryProcessor__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldRequiredEntryProcessor> { }
  public abstract class Type_factory__o_k_w_c_f_c_c_c_CrudComponentViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<CrudComponentViewImpl> { }
  public abstract class Type_factory__o_u_c_v_p_n_ActivityNotFoundView__quals__j_e_i_Any_j_e_i_Default extends Factory<org.uberfire.client.views.pfly.notfound.ActivityNotFoundView> { }
  public abstract class Type_factory__o_k_w_c_f_c_r_c_w_t_BindableTypeAhead__quals__j_e_i_Any_j_e_i_Default extends Factory<BindableTypeAhead> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_f_i_s_SliderFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<SliderFormGroupViewImpl> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroup__quals__j_e_i_Any_j_e_i_Default extends Factory<CollapsibleFormGroup> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListView__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeListView> { }
  public abstract class Type_factory__o_u_c_v_p_w_KebabMenuItem__quals__j_e_i_Any_j_e_i_Default extends Factory<KebabMenuItem> { }
  public abstract class JsType_factory__j_u_Map__quals__Universal extends Factory<Map> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_v_LoadingBox__quals__j_e_i_Any_j_e_i_Default extends Factory<LoadingBox> { }
  public abstract class Type_factory__o_u_e_e_c_c_f_p_c_ToggleCommentView__quals__j_e_i_Any_j_e_i_Default extends Factory<ToggleCommentView> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_f_l_h_FieldHelpViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldHelpViewImpl> { }
  public abstract class Type_factory__o_u_c_v_p_s_SplashModalFooter__quals__j_e_i_Any_j_e_i_Default extends Factory<SplashModalFooter> { }
  public abstract class Type_factory__o_u_e_w_c_c_e_d_DefaultFileEditorPresenterActivity__quals__j_e_i_Any_j_i_Named_o_u_c_w_a_AssociatedResources_o_u_c_w_a_Priority extends Factory<DefaultFileEditorPresenterActivity> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default extends Factory<GlobalSessionManager> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_t_c_UndoToolbarCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<UndoToolbarCommand> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_s_c_i_ClearSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<ClearSessionCommand> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_g_LienzoImageStripGlyphRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoImageStripGlyphRenderer> { }
  public abstract class Type_factory__o_k_w_c_f_a_e_s_f_p_f_f_s_ListBoxFieldInitializer__quals__j_e_i_Any_j_e_i_Default extends Factory<ListBoxFieldInitializer> { }
  public abstract class Type_factory__o_k_w_c_f_p_e_h_i_FieldChangeHandlerManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldChangeHandlerManagerImpl> { }
  public abstract class Type_factory__o_k_w_c_w_c_h_NewResourcePresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<NewResourcePresenter> { }
  public abstract class Type_factory__o_k_w_c_d_w_k_c_c_t_o_GuidedTourGraphObserver__quals__j_e_i_Any_j_e_i_Default extends Factory<GuidedTourGraphObserver> { }
  public abstract class Type_factory__o_k_w_c_s_f_c_s_c_GenerateDiagramFormsSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<GenerateDiagramFormsSessionCommand> { }
  public abstract class ExtensionProvided_factory__o_j_e_c_c_d_Paragraph__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<Paragraph> { }
  public abstract class JsType_factory__o_u_w_m_m_MenuItemCommand__quals__Universal extends Factory<MenuItemCommand> { }
  public abstract class Type_factory__o_u_c_a_EditorTreeProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<EditorTreeProvider> { }
  public abstract class Provider_factory__c_g_g_u_c_u_RootPanel__quals__j_e_i_Any_j_e_i_Default extends Factory<RootPanel> { }
  public abstract class Type_factory__o_u_p_s_i_PreferenceScopeTypesProducer__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferenceScopeTypesProducer> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerCell__quals__j_e_i_Any_j_e_i_Default extends Factory<DateTimePickerCell> { }
  public abstract class Type_factory__o_k_w_c_k_w_b_c_p_AuthoringPerspectiveActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<AuthoringPerspectiveActivity> { }
  public abstract class Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetPropertyAdapterImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDefinitionSetPropertyAdapterImpl> { }
  public abstract class Type_factory__o_u_c_w_p_i_StaticWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<StaticWorkbenchPanelView> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_QNameConverter__quals__j_e_i_Any_j_e_i_Default extends Factory<QNameConverter> { }
  public abstract class Type_factory__o_u_c_w_p_i_MultiScreenWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<MultiScreenWorkbenchPanelPresenter> { }
  public abstract class Producer_factory__o_u_c_w_w_m_WorkbenchMenuBarPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchMenuBarPresenter> { }
  public abstract class JsType_factory__o_u_w_m_t_ToolBar__quals__Universal extends Factory<ToolBar> { }
  public abstract class Type_factory__o_k_w_c_w_c_c_f_CardFrameComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<CardFrameComponent> { }
  public abstract class Type_factory__o_g_c_s_p_p_GAVPreferencesBeanGeneratedImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<GAVPreferencesBeanGeneratedImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_c_c_t_DMNEditBusinessKnowledgeModelToolboxAction__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNEditBusinessKnowledgeModelToolboxAction> { }
  public abstract class Type_factory__o_k_w_c_d_w_k_c_c_t_t_DMNTutorial__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNTutorial> { }
  public abstract class Type_factory__o_u_c_v_p_m_SplashScreenMenuView__quals__j_e_i_Any_j_e_i_Default extends Factory<SplashScreenMenuView> { }
  public abstract class Type_factory__o_k_w_c_s_k_r_c_s_c_i_KogitoUndoSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<KogitoUndoSessionCommand> { }
  public abstract class Type_factory__o_j_e_i_c_l_i_DestructionImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DestructionImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_c_ContextMenuView__quals__j_e_i_Any_j_e_i_Default extends Factory<ContextMenuView> { }
  public abstract class ExtensionProvided_factory__e_d_HTMLHeadingElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named_1 extends Factory<HTMLHeadingElement> { }
  public abstract class Type_factory__o_u_e_e_c_c_h_HtmlEditorView__quals__j_e_i_Any_j_e_i_Default extends Factory<HtmlEditorView> { }
  public abstract class Type_factory__o_u_c_w_w_d_CompassDropController__quals__j_e_i_Any_j_e_i_Default extends Factory<CompassDropController> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_t_i_DefaultToolbarView__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultToolbarView> { }
  public abstract class Type_factory__o_g_c_s_p_c_c_WorkspaceProjectContext__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkspaceProjectContext> { }
  public abstract class Type_factory__o_a_k_b_c_g_t_GuidedTourBridge__quals__j_e_i_Any_j_e_i_Default extends Factory<GuidedTourBridge> { }
  public abstract class Type_factory__o_u_e_p_c_c_PreferencesCentralNavBarScreen__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferencesCentralNavBarScreen> { }
  public abstract class Type_factory__o_k_w_c_d_c_m_u_n_StunnerConverter__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerConverter> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_d_DNDListComponentView__quals__j_e_i_Any_j_e_i_Default extends Factory<DNDListComponentView> { }
  public abstract class Type_factory__o_k_w_c_w_c_p_l_AppLauncherPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<AppLauncherPresenter> { }
  public abstract class Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorView__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionNavigatorView> { }
  public abstract class Type_factory__o_k_w_c_w_c_m_CurrentBranchImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<CurrentBranchImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponents__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionComponents> { }
  public abstract class Type_factory__o_k_w_c_d_c_m_u_n_NodeEntriesFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<NodeEntriesFactory> { }
  public abstract class Type_factory__o_u_c_v_p_m_m_c_ChildContextMenuItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<ChildContextMenuItemView> { }
  public abstract class Type_factory__o_k_w_c_w_c_p_a_AboutPopup__quals__j_e_i_Any_j_e_i_Default extends Factory<AboutPopup> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_s_c_ManagedClientSessionCommands__quals__j_e_i_Any_j_e_i_Default extends Factory<ManagedClientSessionCommands> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_t_a_CommonActionsToolboxFactory__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_CommonActionsToolbox extends Factory<CommonActionsToolboxFactory> { }
  public abstract class Type_factory__o_u_e_w_c_c_m_MenuItemFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<MenuItemFactory> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default extends Factory<DateEditableColumnGenerator> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_r_m_c_i_BooleanColumnGenerator__quals__j_e_i_Any extends Factory<BooleanColumnGenerator> { }
  public abstract class Type_factory__o_k_w_c_f_f_s_f_b_s_r_p_RadioGroupFieldProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<RadioGroupFieldProvider> { }
  public abstract class Type_factory__o_k_w_c_d_c_s_DMNCanvasShortcutsControlProxy__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNCanvasShortcutsControlProxy> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_c_c_LocalClipboardControl__quals__j_e_i_Any_j_e_i_Default extends Factory<LocalClipboardControl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_y_m_YearsMonthsSelector__quals__j_e_i_Any_j_e_i_Default extends Factory<YearsMonthsSelector> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_m_LienzoPanelMediators__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoPanelMediators> { }
  public abstract class Type_factory__o_u_e_e_c_c_f_p_RestorePopUpView__quals__j_e_i_Any_j_e_i_Default extends Factory<RestorePopUpView> { }
  public abstract class JsType_factory__o_u_m_Command__quals__Universal extends Factory<Command> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_e_LienzoCanvasExport__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoCanvasExport> { }
  public abstract class Type_factory__o_u_e_p_c_PreferencesEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferencesEntryPoint> { }
  public abstract class Type_factory__o_u_e_w_c_c_e_m_MetaFileEditorPresenterActivity__quals__j_e_i_Any_j_i_Named_o_u_c_w_a_AssociatedResources_o_u_c_w_a_Priority extends Factory<MetaFileEditorPresenterActivity> { }
  public abstract class Type_factory__o_k_w_c_s_f_c_FormsClientEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<FormsClientEntryPoint> { }
  public abstract class Type_factory__o_u_p_c_s_PreferenceBeanStoreClientImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferenceBeanStoreClientImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_g_p_t_c_ChildrenTraverseProcessorImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ChildrenTraverseProcessorImpl> { }
  public abstract class Type_factory__o_u_e_p_c_a_AdminPageView__quals__j_e_i_Any_j_e_i_Default extends Factory<AdminPageView> { }
  public abstract class Type_factory__o_a_k_b_c_i_I18nServiceProducer__quals__j_e_i_Any_j_e_i_Default extends Factory<I18nServiceProducer> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_v_WidgetWrapperViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<WidgetWrapperViewImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_c_ContainmentAcceptorControlImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ContainmentAcceptorControlImpl> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_n_i_IntegerEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default extends Factory<IntegerEditableColumnGenerator> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_v_WidgetElementRendererViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<WidgetElementRendererViewImpl> { }
  public abstract class Type_factory__o_g_m_c_c_MessageConsoleViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<MessageConsoleViewImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_w_StunnerWiresControlFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerWiresControlFactory> { }
  public abstract class Type_factory__o_k_w_c_f_a_e_c_f_ClientFormGenerator__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientFormGenerator> { }
  public abstract class Type_factory__o_k_w_c_f_a_e_c_f_u_ClientPropertuValueExtractor__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientPropertuValueExtractor> { }
  public abstract class Type_factory__o_k_w_c_w_c_d_DiscussionWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DiscussionWidgetViewImpl> { }
  public abstract class Type_factory__o_k_w_c_s_k_c_e_DiagramEditorView__quals__j_e_i_Any_j_e_i_Default extends Factory<DiagramEditorView> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_c_PreviewLienzoPanel__quals__j_e_i_Any_j_e_i_Default extends Factory<PreviewLienzoPanel> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_s_StringSelector__quals__j_e_i_Any_j_e_i_Default extends Factory<StringSelector> { }
  public abstract class Type_factory__o_k_w_c_w_c_m_ResetPerspectivesMenuBuilder__quals__j_e_i_Any_j_e_i_Default extends Factory<ResetPerspectivesMenuBuilder> { }
  public abstract class Type_factory__o_u_c_w_w_n_ActivityNotFoundPresenter__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<org.uberfire.client.workbench.widgets.notfound.ActivityNotFoundPresenter> { }
  public abstract class Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelectorHeaderItemViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ListSelectorHeaderItemViewImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_t_i_ToolbarItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<ToolbarItemView> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_c_ScalableLienzoPanel__quals__j_e_i_Any_j_e_i_Default extends Factory<ScalableLienzoPanel> { }
  public abstract class Type_factory__o_u_c_w_p_i_ClosableSimpleWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<ClosableSimpleWorkbenchPanelPresenter> { }
  public abstract class ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<HTMLDivElement> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_p_ItemDefinitionRecordEngine__quals__j_e_i_Any_j_e_i_Default extends Factory<ItemDefinitionRecordEngine> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_t_c_ExportToJpgToolbarCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<ExportToJpgToolbarCommand> { }
  public abstract class Type_factory__o_u_e_w_c_c_b_h_UberfireBreadcrumbsContainerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<UberfireBreadcrumbsContainerImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_c_ScrollHelper__quals__j_e_i_Any_j_e_i_Default extends Factory<ScrollHelper> { }
  public abstract class Type_factory__o_k_w_c_s_s_c_v_ShapeViewFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<ShapeViewFactory> { }
  public abstract class JsType_factory__o_j_e_i_c_c_JsTypeProvider__quals__Universal extends Factory<JsTypeProvider> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_w_WiresManagerFactoryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<WiresManagerFactoryImpl> { }
  public abstract class Type_factory__o_u_c_w_p_i_SimpleDnDWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default extends Factory<SimpleDnDWorkbenchPanelView> { }
  public abstract class Type_factory__o_k_w_c_d_w_k_c_c_t_p_GraphElementsPositionProviderFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<GraphElementsPositionProviderFactory> { }
  public abstract class Type_factory__o_u_e_w_c_c_d_f_LiveSearchFooter__quals__j_e_i_Any_j_e_i_Default extends Factory<LiveSearchFooter> { }
  public abstract class ExtensionProvided_factory__e_d_HTMLTextAreaElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<HTMLTextAreaElement> { }
  public abstract class Type_factory__o_u_c_v_p_m_WorkbenchMenuBarView__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchMenuBarView> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_s_i_SessionLoader__quals__j_e_i_Any_j_e_i_Default extends Factory<SessionLoader> { }
  public abstract class Type_factory__o_j_e_e_c_c_EventProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<EventProvider> { }
  public abstract class Type_factory__o_k_w_c_s_s_p_c_WorkbenchPreferenceScopeResolutionStrategiesImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchPreferenceScopeResolutionStrategiesImpl> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_n_d_DoubleEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default extends Factory<DoubleEditableColumnGenerator> { }
  public abstract class Type_factory__o_u_c_v_p_w_SanitizedNumberInput__quals__j_e_i_Any_j_e_i_Default extends Factory<SanitizedNumberInput> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_c_g_SvgElementGlyphRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<SvgElementGlyphRenderer> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_s_r_DecimalRadioGroupFieldRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<DecimalRadioGroupFieldRenderer> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_m_d_i_LogSelectedItemsDevCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<LogSelectedItemsDevCommand> { }
  public abstract class Type_factory__o_u_e_l_e_c_w_LayoutComponentPalettePresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutComponentPalettePresenter> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_l_LayoutHelper__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutHelper> { }
  public abstract class Type_factory__o_a_k_b_c_g_t_o_GlobalHTMLObserver__quals__j_e_i_Any_j_e_i_Default extends Factory<GlobalHTMLObserver> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_p_h_c_PropertiesPanelNotifier__quals__j_e_i_Any_j_e_i_Default extends Factory<PropertiesPanelNotifier> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_m_d_i_LogNodeEdgesDevCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<LogNodeEdgesDevCommand> { }
  public abstract class ExtensionProvided_factory__o_j_e_c_c_d_Heading__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<Heading> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_f_l_h_FieldHelp__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldHelp> { }
  public abstract class Type_factory__o_k_w_c_s_f_c_w_c_FormsContainer__quals__j_e_i_Any_j_e_i_Default extends Factory<FormsContainer> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_t_a_CreateConnectorToolboxAction__quals__j_e_i_Any_j_e_i_Default extends Factory<CreateConnectorToolboxAction> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_f_i_d_DefaultFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultFormGroupViewImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_r_i_ClientRegistryFactoryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientRegistryFactoryImpl> { }
  public abstract class Type_factory__o_u_c_v_p_m_m_m_GroupMenuItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<GroupMenuItemView> { }
  public abstract class Type_factory__o_k_w_c_f_p_e_h_ProcessingEngineEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<ProcessingEngineEntryPoint> { }
  public abstract class Type_factory__o_u_c_w_p_i_ClosableSimpleWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<ClosableSimpleWorkbenchPanelView> { }
  public abstract class Type_factory__o_k_w_c_d_c_a_d_DMNDataObjectsClient__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDataObjectsClient> { }
  public abstract class Type_factory__o_k_w_c_s_c_r_e_i_ConnectorParentsMatchConnectionHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<ConnectorParentsMatchConnectionHandler> { }
  public abstract class Type_factory__o_u_w_m_AppFormerActivitiesProducer__quals__j_e_i_Any_j_e_i_Default extends Factory<AppFormerActivitiesProducer> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_p_NodeProxy__quals__j_e_i_Any_j_e_i_Default extends Factory<NodeProxy> { }
  public abstract class Type_factory__o_k_w_c_w_c_h_w_c_WorkbenchConfigurationViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchConfigurationViewImpl> { }
  public abstract class ContextualProvider_factory__o_u_p_c_s_PreferenceStore__quals__Universal extends Factory<PreferenceStore> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_DynamicRendererEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<DynamicRendererEntryPoint> { }
  public abstract class Type_factory__o_k_w_c_s_c_r_h_i_ConnectionEvaluationHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<ConnectionEvaluationHandler> { }
  public abstract class ExtensionProvided_factory__e_d_HTMLOListElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<HTMLOListElement> { }
  public abstract class Type_factory__o_k_w_c_s_c_d_a_AdapterManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<AdapterManagerImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_d_a_b_BootstrapAdapterFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<BootstrapAdapterFactory> { }
  public abstract class Type_factory__o_u_e_p_c_a_i_AdminPageItemPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<AdminPageItemPresenter> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_c_b_i_ElementBuilderControlImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_c_c_b_i_Element extends Factory<ElementBuilderControlImpl> { }
  public abstract class JsType_factory__o_u_c_m_Activity__quals__Universal extends Factory<Activity> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_m_MarshallingResponsePopup__quals__j_e_i_Any_j_e_i_Default extends Factory<MarshallingResponsePopup> { }
  public abstract class Producer_factory__o_u_e_e_c_v_CurrentBranch__quals__j_e_i_Any_o_u_a_Customizable extends Factory<CurrentBranch> { }
  public abstract class Type_factory__o_j_e_u_c_w_LocaleSelector__quals__j_e_i_Any_j_e_i_Default extends Factory<LocaleSelector> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_w_WiresCanvas__quals__j_e_i_Any_j_e_i_Default extends Factory<WiresCanvas> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraintModal__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeConstraintModal> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_r_m_MultipleSubFormFieldRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<MultipleSubFormFieldRenderer> { }
  public abstract class Type_factory__o_k_w_c_d_c_s_f_DMNDecisionServiceShapeFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDecisionServiceShapeFactory> { }
  public abstract class Type_factory__o_k_w_c_s_c_f_i_GraphFactoryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<GraphFactoryImpl> { }
  public abstract class Type_factory__o_j_e_i_c_l_i_CreationImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<CreationImpl> { }
  public abstract class Type_factory__o_u_p_s_i_DefaultPreferenceScopeTypes__quals__j_e_i_Any_o_u_a_FallbackImplementation extends Factory<DefaultPreferenceScopeTypes> { }
  public abstract class JsType_factory__o_u_w_m_ContextDefinition__quals__Universal extends Factory<ContextDefinition> { }
  public abstract class Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelectorTextItemViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ListSelectorTextItemViewImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_c_ResizeControlImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ResizeControlImpl> { }
  public abstract class Type_factory__o_u_e_l_e_c_LayoutEditorPluginImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutEditorPluginImpl> { }
  public abstract class Type_factory__o_u_c_v_p_s_SplashViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<SplashViewImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_g_LienzoGlyphRenderers__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoGlyphRenderers> { }
  public abstract class JsType_factory__o_u_c_m_PerspectiveActivity__quals__Universal extends Factory<PerspectiveActivity> { }
  public abstract class Type_factory__o_u_e_c_e_g_ExperimentalFeaturesGroup__quals__j_e_i_Any_j_e_i_Default extends Factory<ExperimentalFeaturesGroup> { }
  public abstract class Type_factory__o_u_c_w_WorkbenchLayoutImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchLayoutImpl> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_FormLayoutGenerator__quals__j_e_i_Any extends Factory<FormLayoutGenerator> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_e_DataTypeConstraintExpression__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeConstraintExpression> { }
  public abstract class Type_factory__o_k_w_c_w_c_h_PackageListBox__quals__j_e_i_Any_j_e_i_Default extends Factory<PackageListBox> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_m_ZoomLevelSelectorItem__quals__j_e_i_Any_j_e_i_Default extends Factory<ZoomLevelSelectorItem> { }
  public abstract class Type_factory__o_k_w_c_d_c_w_t_DMNGraphProcessor__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNGraphProcessor> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_n_d_BigDecimalEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default extends Factory<BigDecimalEditableColumnGenerator> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_p_DataTypeStore__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeStore> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_p_s_i_SessionEditorPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<SessionEditorPresenter> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_p_StunnerTextPreferences__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerTextPreferences> { }
  public abstract class Type_factory__o_u_e_e_c_c_f_p_SavePopUpPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<SavePopUpPresenter> { }
  public abstract class Type_factory__o_u_c_v_p_m_m_m_ChildMenuItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<ChildMenuItemView> { }
  public abstract class JsType_factory__o_u_w_m_m_i_DefaultMenuGroup__quals__Universal extends Factory<DefaultMenuGroup> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_StunnerLienzoCore__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerLienzoCore> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_g_LienzoShapeGlyphRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoShapeGlyphRenderer> { }
  public abstract class Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetDefinitionSetAdapterImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDefinitionSetDefinitionSetAdapterImpl> { }
  public abstract class Type_factory__o_u_c_v_p_m_MultiScreenPartWidget__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<MultiScreenPartWidget> { }
  public abstract class Type_factory__o_u_c_w_p_i_SimpleNoExpandWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<SimpleNoExpandWorkbenchPanelView> { }
  public abstract class Type_factory__o_j_e_i_c_l_i_LifecycleListenerRegistrarImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<LifecycleListenerRegistrarImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_s_c_i_SessionSingletonCommandsFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<SessionSingletonCommandsFactory> { }
  public abstract class Type_factory__o_u_e_e_c_c_f_p_RenamePopUpView__quals__j_e_i_Any_j_e_i_Default extends Factory<RenamePopUpView> { }
  public abstract class Type_factory__o_u_e_c_e_ExperimentalFeaturesEditorScreenViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ExperimentalFeaturesEditorScreenViewImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListItem__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeListItem> { }
  public abstract class Type_factory__o_u_e_p_c_c_f_DefaultPreferenceFormView__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultPreferenceFormView> { }
  public abstract class Type_factory__o_k_w_c_s_c_r_h_i_CardinalityEvaluationHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<CardinalityEvaluationHandler> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_d_DatePickerFieldRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<DatePickerFieldRenderer> { }
  public abstract class Type_factory__o_k_w_c_d_c_m_DMNMarshallerService__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNMarshallerService> { }
  public abstract class ContextualProvider_factory__o_j_e_c_c_d_HTMLElement__quals__Universal extends Factory<org.jboss.errai.common.client.dom.HTMLElement> { }
  public abstract class Type_factory__o_k_w_c_s_c_g_p_i_b_GraphBoundsIndexerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<GraphBoundsIndexerImpl> { }
  public abstract class Type_factory__o_u_e_w_c_c_b_w_DefaultBreadcrumbsView__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultBreadcrumbsView> { }
  public abstract class Type_factory__o_u_e_p_c_c_PreferencesCentralPerspectiveActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<PreferencesCentralPerspectiveActivity> { }
  public abstract class Type_factory__o_k_w_c_d_c_DMNClientEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNClientEntryPoint> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_u_i_FormsElementWrapperWidgetUtilImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FormsElementWrapperWidgetUtilImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_p_BS3PaletteWidgetImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<BS3PaletteWidgetImpl> { }
  public abstract class Type_factory__o_u_e_e_c_c_h_VersionHistoryPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<VersionHistoryPresenter> { }
  public abstract class Type_factory__o_u_p_s_b_m_PortablePreferenceMockBeanGeneratedImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<PortablePreferenceMockBeanGeneratedImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_s_DMNEditorSearchIndex__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNEditorSearchIndex> { }
  public abstract class Type_factory__o_k_w_c_f_f_s_f_b_l_s_MultipleSelectorProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<MultipleSelectorProvider> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_c_g_ImageElementGlyphRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<ImageElementGlyphRenderer> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_d_a_b_ClientDefinitionBindablePropertyAdapter__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientDefinitionBindablePropertyAdapter> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_e_t_TreeExplorer__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeExplorer> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_v_LienzoCanvasNotification__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoCanvasNotification> { }
  public abstract class Type_factory__o_k_w_c_f_a_e_s_f_p_f_f_TextAreaFieldInitializer__quals__j_e_i_Any_j_e_i_Default extends Factory<TextAreaFieldInitializer> { }
  public abstract class Type_factory__o_u_c_v_p_m_ErrorPopupView__quals__j_e_i_Any_j_e_i_Default extends Factory<org.uberfire.client.views.pfly.modal.ErrorPopupView> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_i_ClientTranslationMessages__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientTranslationMessages> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_u_PrintHelper__quals__j_e_i_Any_j_e_i_Default extends Factory<PrintHelper> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchBar__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeSearchBar> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_t_c_ContextEditorDefinition__quals__j_e_i_Any_j_e_i_Default extends Factory<ContextEditorDefinition> { }
  public abstract class Type_factory__o_k_w_c_w_c_d_DockPlaceHolderActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<DockPlaceHolderActivity> { }
  public abstract class Type_factory__o_u_c_w_t_AnyResourceType__quals__j_e_i_Any_j_e_i_Default extends Factory<AnyResourceType> { }
  public abstract class Type_factory__o_k_w_c_f_c_r_c_FormWidgetsEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<FormWidgetsEntryPoint> { }
  public abstract class Type_factory__o_k_w_c_d_c_p_d_ModuleFieldRendererTypesProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<org.kie.workbench.common.dmn.client.property.dmn.ModuleFieldRendererTypesProvider> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_u_CanvasHighlight__quals__j_e_i_Any_j_e_i_Default extends Factory<CanvasHighlight> { }
  public abstract class Type_factory__o_u_c_w_w_n_NotificationManager__quals__j_e_i_Any_j_e_i_Default extends Factory<NotificationManager> { }
  public abstract class Type_factory__o_u_e_p_c_a_c_AdminPageCategoryView__quals__j_e_i_Any_j_e_i_Default extends Factory<AdminPageCategoryView> { }
  public abstract class Type_factory__o_u_e_w_c_c_b_w_DefaultBreadcrumbsPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultBreadcrumbsPresenter> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_m_d_i_LogMagnetsDevCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<LogMagnetsDevCommand> { }
  public abstract class Type_factory__o_u_e_e_c_c_m_RestoreVersionCommandProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<RestoreVersionCommandProvider> { }
  public abstract class Type_factory__o_u_e_l_e_c_LayoutEditorPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutEditorPresenter> { }
  public abstract class Type_factory__o_k_w_c_f_f_s_f_b_d_p_DatePickerFieldProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<DatePickerFieldProvider> { }
  public abstract class Type_factory__o_u_e_e_c_c_f_p_c_ToggleCommentPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<ToggleCommentPresenter> { }
  public abstract class Type_factory__o_k_w_c_d_c_c_c_t_DMNToolboxControl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNToolboxControl> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_r_s_SubFormFieldRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<SubFormFieldRenderer> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_r_m_MultipleSubFormWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<MultipleSubFormWidget> { }
  public abstract class Type_factory__o_u_c_w_t_DotResourceType__quals__j_e_i_Any_j_e_i_Default extends Factory<DotResourceType> { }
  public abstract class Producer_factory__o_k_w_c_d_c_e_e_t_ExpressionEditorDefinitions__quals__j_e_i_Any_j_e_i_Default extends Factory<ExpressionEditorDefinitions> { }
  public abstract class Type_factory__o_k_w_c_w_c_p_t_TextBoxFormPopup__quals__j_e_i_Any_j_e_i_Default extends Factory<TextBoxFormPopup> { }
  public abstract class Type_factory__o_k_w_c_d_c_w_p_DMNGridPanelControlImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNGridPanelControlImpl> { }
  public abstract class ExtensionProvided_factory__e_d_HTMLAnchorElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<HTMLAnchorElement> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_s_c_i_ValidateSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<ValidateSessionCommand> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_c_e_DataTypeNameIsDefaultTypeMessage__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeNameIsDefaultTypeMessage> { }
  public abstract class ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal extends Factory<ManagedInstance> { }
  public abstract class Type_factory__o_k_w_c_d_c_DMNShapeSet__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNShapeSet> { }
  public abstract class JsType_factory__o_u_w_m_m_Menus__quals__Universal extends Factory<Menus> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_s_c_i_ExportToJpgSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<ExportToJpgSessionCommand> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_v_s_NoCanvasView__quals__j_e_i_Any_j_e_i_Default extends Factory<NoCanvasView> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_g_LienzoSvgDataUriGlyphRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoSvgDataUriGlyphRenderer> { }
  public abstract class Type_factory__o_k_w_c_s_k_c_s_DiagramEditorPropertiesScreen__quals__j_e_i_Any_j_e_i_Default extends Factory<DiagramEditorPropertiesScreen> { }
  public abstract class Type_factory__o_a_k_b_c_g_t_s_GuidedTourServiceProducer__quals__j_e_i_Any_j_e_i_Default extends Factory<GuidedTourServiceProducer> { }
  public abstract class Type_factory__o_u_p_s_i_PreferenceScopeValidatorImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferenceScopeValidatorImpl> { }
  public abstract class Type_factory__o_k_w_c_s_f_c_w_FormsCanvasSessionHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<FormsCanvasSessionHandler> { }
  public abstract class ContextualProvider_factory__o_u_p_s_PreferenceScopeResolutionStrategy__quals__Universal extends Factory<PreferenceScopeResolutionStrategy> { }
  public abstract class Type_factory__o_u_e_e_c_c_f_p_DeletePopUpView__quals__j_e_i_Any_j_e_i_Default extends Factory<DeletePopUpView> { }
  public abstract class Type_factory__o_k_w_c_d_w_k_c_c_i_DMNMarshallerImportsContentServiceKogitoImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNMarshallerImportsContentServiceKogitoImpl> { }
  public abstract class Type_factory__o_u_e_c_w_t_ExperimentalAwareClientTypeRegistryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ExperimentalAwareClientTypeRegistryImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_d_c_DMNDocumentationDRDsFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDocumentationDRDsFactory> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_d_i_DatePickerWrapperViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DatePickerWrapperViewImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_m_i_DMNMarshallerImportsContentServiceImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNMarshallerImportsContentServiceImpl> { }
  public abstract class Type_factory__o_u_e_p_c_c_s_PreferencesRootView__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferencesRootView> { }
  public abstract class ContextualProvider_factory__o_j_e_d_c_a_DataBinder__quals__Universal extends Factory<DataBinder> { }
  public abstract class Type_factory__o_u_e_w_c_c_t_p_NewTabFilterPopup__quals__j_e_i_Any_j_e_i_Default extends Factory<NewTabFilterPopup> { }
  public abstract class Type_factory__o_k_w_c_d_w_k_c_c_t_p_HTMLElementsPositionProviderFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<HTMLElementsPositionProviderFactory> { }
  public abstract class Type_factory__o_k_w_c_f_s_s_m_p_i_p_FieldTypeEntryProcessor__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldTypeEntryProcessor> { }
  public abstract class Type_factory__o_a_c_c_DefaultEditorContextProviderImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultEditorContextProviderImpl> { }
  public abstract class Type_factory__o_u_c_w_w_m_WorkbenchMenuBarProducer__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchMenuBarProducer> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_t_d_h_BuiltinAggregatorUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<BuiltinAggregatorUtils> { }
  public abstract class Type_factory__o_k_w_c_d_w_k_c_c_t_t_DMNTutorialView__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNTutorialView> { }
  public abstract class Type_factory__o_j_e_i_c_a_b_RootPanelProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<RootPanelProvider> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimePicker__quals__j_e_i_Any_j_e_i_Default extends Factory<TimePicker> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_p_BS3PaletteWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<BS3PaletteWidgetViewImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_s_ImageStripRegistry__quals__j_e_i_Any_j_e_i_Default extends Factory<ImageStripRegistry> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_p_v_DataTypeNameValidator__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeNameValidator> { }
  public abstract class JsType_factory__j_x_XMLConstants__quals__Universal extends Factory<XMLConstants> { }
  public abstract class Type_factory__o_u_e_w_c_c_e_t_TextEditorWorkbenchEditorActivity__quals__j_e_i_Any_j_i_Named_o_u_c_w_a_AssociatedResources_o_u_c_w_a_Priority extends Factory<TextEditorWorkbenchEditorActivity> { }
  public abstract class Type_factory__o_u_c_u_GWTEditorNativeRegister__quals__j_e_i_Any_j_e_i_Default extends Factory<GWTEditorNativeRegister> { }
  public abstract class Type_factory__o_k_w_c_f_d_s_s_a_DynamicFormModelGenerator__quals__j_e_i_Any_j_e_i_Default extends Factory<DynamicFormModelGenerator> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_s_e_SessionEventObserver__quals__j_e_i_Any_j_e_i_Default extends Factory<SessionEventObserver> { }
  public abstract class Type_factory__o_u_e_w_c_c_e_m_MetaFileEditorPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<MetaFileEditorPresenter> { }
  public abstract class Type_factory__o_k_w_c_s_c_r_e_i_ConnectorParentsMatchHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<ConnectorParentsMatchHandler> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_e_n_v_BootstrapNavigatorView__quals__j_e_i_Any_j_e_i_Default extends Factory<BootstrapNavigatorView> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_c_e_DataTypeNameIsInvalidErrorMessage__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeNameIsInvalidErrorMessage> { }
  public abstract class Type_factory__o_k_w_c_w_c_p_v_ValidationMessageTranslatorUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<ValidationMessageTranslatorUtils> { }
  public abstract class Type_factory__o_k_w_c_d_c_c_f_DefaultCanvasCommandFactory__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DefaultCanvasCommandFactory> { }
  public abstract class Type_factory__o_u_e_e_c_c_t_m_ClientMustacheTemplateRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientMustacheTemplateRenderer> { }
  public abstract class Type_factory__o_k_w_c_d_c_d_p_PreviewDiagramScreenView__quals__j_e_i_Any_j_e_i_Default extends Factory<PreviewDiagramScreenView> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_c_ConnectionAcceptorControlImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ConnectionAcceptorControlImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_m_d_i_LogSessionDevCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<LogSessionDevCommand> { }
  public abstract class JsType_factory__o_u_w_m_ContextDisplayMode__quals__Universal extends Factory<ContextDisplayMode> { }
  public abstract class Type_factory__o_u_c_w_WorkbenchLayoutInfoImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchLayoutInfoImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_w_t_DMNPerformAutomaticLayoutCommand__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNPerformAutomaticLayoutCommand> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_t_c_SaveToolbarCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<SaveToolbarCommand> { }
  public abstract class Type_factory__o_k_w_c_d_w_k_c_c_s_TimeZonesProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<TimeZonesProvider> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_ApplicationCommandManager__quals__j_e_i_Any_j_e_i_Default extends Factory<ApplicationCommandManager> { }
  public abstract class JsType_factory__o_u_w_m_PerspectiveDefinition__quals__Universal extends Factory<PerspectiveDefinition> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_s_c_i_VisitGraphSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<VisitGraphSessionCommand> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_d_DefinitionLookupManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DefinitionLookupManagerImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_p_h_ItemDefinitionUpdateHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<ItemDefinitionUpdateHandler> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_c_m_FlashMessages__quals__j_e_i_Any_j_e_i_Default extends Factory<FlashMessages> { }
  public abstract class Type_factory__o_j_e_u_s_ServerTemplateProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<ServerTemplateProvider> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_c_g_ElementShapeGlyphRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<ElementShapeGlyphRenderer> { }
  public abstract class ExtensionProvided_factory__o_g_c_u_Button__quals__j_e_i_Any_j_e_i_Default extends Factory<org.gwtbootstrap3.client.ui.Button> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_c_b_i_NodeBuilderControlImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<NodeBuilderControlImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_t_c_ValidateToolbarCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<ValidateToolbarCommand> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_t_f_s_p_d_LiteralExpressionPMMLDocumentEditorDefinition__quals__j_e_i_Any_j_e_i_Default extends Factory<LiteralExpressionPMMLDocumentEditorDefinition> { }
  public abstract class Type_factory__o_k_w_c_d_c_a_ReadOnlyProviderImpl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<ReadOnlyProviderImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_t_c_CutToolbarCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<CutToolbarCommand> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_a_ShapeManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ShapeManagerImpl> { }
  public abstract class ExtensionProvided_factory__e_d_HTMLSelectElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<HTMLSelectElement> { }
  public abstract class Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<PlaceManagerImpl> { }
  public abstract class Type_factory__o_u_c_m_CustomSplashHelp__quals__j_e_i_Any_j_e_i_Default extends Factory<CustomSplashHelp> { }
  public abstract class Type_factory__o_k_w_c_d_c_p_d_NameFieldProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<NameFieldProvider> { }
  public abstract class Type_factory__o_k_w_c_f_f_s_f_b_s_d_f_p_ModuleFormGenerationResourcesProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.definition.formBuilder.provider.ModuleFormGenerationResourcesProvider> { }
  public abstract class Type_factory__o_k_w_c_w_c_h_w_c_WorkbenchConfigurationPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchConfigurationPresenter> { }
  public abstract class Type_factory__o_u_s_i_a_DefaultAuthorizationManager__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultAuthorizationManager> { }
  public abstract class Type_factory__o_k_w_c_f_c_r_c_w_p_w_PictureWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<PictureWidgetViewImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_i_g_DMNCardsGridComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNCardsGridComponent> { }
  public abstract class Type_factory__o_k_w_c_f_c_r_c_w_d_DecimalBox__quals__j_e_i_Any_j_e_i_Default extends Factory<DecimalBox> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_ModuleFieldRendererTypesProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<org.kie.workbench.common.forms.dynamic.client.rendering.renderers.ModuleFieldRendererTypesProvider> { }
  public abstract class Type_factory__o_a_c_s_r_i_DefaultRegistryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultRegistryImpl> { }
  public abstract class Type_factory__o_u_e_w_c_c_s_SelectView__quals__j_e_i_Any_j_e_i_Default extends Factory<SelectView> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_d_DocumentationLinkItem__quals__j_e_i_Any_j_e_i_Default extends Factory<DocumentationLinkItem> { }
  public abstract class ExtensionProvided_factory__o_k_w_c_w_c_s_ViewDRLSourceWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<ViewDRLSourceWidget> { }
  public abstract class JsType_factory__o_u_c_m_WorkbenchActivity__quals__Universal extends Factory<WorkbenchActivity> { }
  public abstract class Type_factory__o_k_w_c_f_c_r_c_w_i_IntegerBox__quals__j_e_i_Any_j_e_i_Default extends Factory<IntegerBox> { }
  public abstract class Type_factory__o_k_w_c_s_c_p_DomainProfileManager__quals__j_e_i_Any_j_e_i_Default extends Factory<DomainProfileManager> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_l_OpenDiagramLayoutExecutor__quals__j_e_i_Any_j_e_i_Default extends Factory<OpenDiagramLayoutExecutor> { }
  public abstract class Type_factory__o_g_m_c_c_MessageConsoleService__quals__j_e_i_Any_j_e_i_Default extends Factory<MessageConsoleService> { }
  public abstract class Type_factory__j_x_XMLConstants__quals__j_e_i_Any_j_e_i_Default extends Factory<XMLConstants> { }
  public abstract class Type_factory__o_u_c_m_WorkbenchViewModeSwitcherMenuBuilder__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchViewModeSwitcherMenuBuilder> { }
  public abstract class Type_factory__o_u_c_w_p_i_UnanchoredStaticWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<UnanchoredStaticWorkbenchPanelView> { }
  public abstract class Type_factory__o_k_w_c_w_c_w_KieSelectOptionsListPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<KieSelectOptionsListPresenter> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_r_m_ColumnGeneratorManager__quals__j_e_i_Any_j_e_i_Default extends Factory<ColumnGeneratorManager> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeStackHash__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeStackHash> { }
  public abstract class Type_factory__o_u_e_l_e_c_i_DnDManager__quals__j_e_i_Any_j_e_i_Default extends Factory<DnDManager> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_c_LienzoMediatorsControl__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoMediatorsControl> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_v_CanvasDefinitionTooltip__quals__j_e_i_Any_j_e_i_Default extends Factory<CanvasDefinitionTooltip> { }
  public abstract class Type_factory__o_u_w_m_DefaultAppFormerActivities__quals__j_e_i_Any_o_u_a_FallbackImplementation extends Factory<DefaultAppFormerActivities> { }
  public abstract class ExtensionProvided_factory__e_d_HTMLLIElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<HTMLLIElement> { }
  public abstract class Type_factory__o_k_w_c_s_c_r_e_i_ConnectorParentsMatchContainmentHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<ConnectorParentsMatchContainmentHandler> { }
  public abstract class Type_factory__o_u_c_v_p_w_InlineNotification__quals__j_e_i_Any_j_e_i_Default extends Factory<InlineNotification> { }
  public abstract class Type_factory__o_g_c_s_p_c_Process__quals__j_e_i_Any_j_e_i_Default extends Factory<Process> { }
  public abstract class Type_factory__o_k_w_c_d_c_w_c_MonacoFEELInitializer__quals__j_e_i_Any_j_e_i_Default extends Factory<MonacoFEELInitializer> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_d_DRDContextMenu__quals__j_e_i_Any_j_e_i_Default extends Factory<DRDContextMenu> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_t_c_CopyToolbarCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<CopyToolbarCommand> { }
  public abstract class Type_factory__o_k_w_c_d_c_s_RegistryProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<RegistryProvider> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_SmallSwitchComponentView__quals__j_e_i_Any_j_e_i_Default extends Factory<SmallSwitchComponentView> { }
  public abstract class Type_factory__o_k_w_c_s_k_c_s_DiagramEditorExplorerScreen__quals__j_e_i_Any_j_e_i_Default extends Factory<DiagramEditorExplorerScreen> { }
  public abstract class Type_factory__o_g_c_s_p_c_p_ProjectScopedResolutionStrategySupplier__quals__j_e_i_Any_j_e_i_Default extends Factory<ProjectScopedResolutionStrategySupplier> { }
  public abstract class Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionNavigatorPresenter> { }
  public abstract class Type_factory__o_u_c_d_v_DocksBars__quals__j_e_i_Any_j_e_i_Default extends Factory<DocksBars> { }
  public abstract class Type_factory__o_j_e_i_c_a_b_DisposerProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<DisposerProvider> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimeValueFormatter__quals__j_e_i_Any_j_e_i_Default extends Factory<TimeValueFormatter> { }
  public abstract class Producer_factory__o_u_e_e_c_c_f_e_PdfFileExport__quals__j_e_i_Any_j_e_i_Default extends Factory<PdfFileExport> { }
  public abstract class ExtensionProvided_factory__o_g_c_u_Icon__quals__j_e_i_Any_j_e_i_Default extends Factory<Icon> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_a_ReadOnlyProviderImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<org.kie.workbench.common.stunner.core.client.api.ReadOnlyProviderImpl> { }
  public abstract class Type_factory__o_u_e_l_e_c_c_r_EmptyDropRow__quals__j_e_i_Any_j_e_i_Default extends Factory<EmptyDropRow> { }
  public abstract class Type_factory__o_j_e_i_c_a_b_ManagedInstanceProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<ManagedInstanceProvider> { }
  public abstract class Type_factory__o_k_w_c_w_c_w_KieMultipleSelectElement__quals__j_e_i_Any_j_e_i_Default extends Factory<KieMultipleSelectElement> { }
  public abstract class Type_factory__o_k_w_c_w_c_w_KieMultipleSelectElementView__quals__j_e_i_Any_j_e_i_Default extends Factory<KieMultipleSelectElementView> { }
  public abstract class Type_factory__o_g_c_s_p_c_ProjectEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<ProjectEntryPoint> { }
  public abstract class Type_factory__o_k_w_c_d_c_s_f_DMNConnectorShapeFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNConnectorShapeFactory> { }
  public abstract class JsType_factory__j_u_List__quals__Universal extends Factory<List> { }
  public abstract class ExtensionProvided_factory__o_j_e_c_c_d_Image__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<Image> { }
  public abstract class Type_factory__o_k_w_c_f_f_s_f_b_i_p_PictureFieldProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<PictureFieldProvider> { }
  public abstract class Type_factory__o_k_w_c_s_c_g_p_l_s_s_ReverseEdgesCycleBreaker__quals__j_e_i_Any_j_e_i_Default extends Factory<ReverseEdgesCycleBreaker> { }
  public abstract class Type_factory__o_u_e_p_c_c_t_TreeHierarchyStructurePresenter__quals__j_e_i_Any_o_u_e_p_c_c_t_TreeView extends Factory<TreeHierarchyStructurePresenter> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_m_d_i_LogGraphDevCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<LogGraphDevCommand> { }
  public abstract class Type_factory__o_j_e_u_c_l_p_HTMLElementProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<HTMLElementProvider> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DefinitionPaletteItemWidgetViewImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_ExpressionEditorViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ExpressionEditorViewImpl> { }
  public abstract class JsType_factory__o_u_w_m_m_i_DefaultMenus__quals__Universal extends Factory<DefaultMenus> { }
  public abstract class Type_factory__o_u_e_e_c_c_v_DefaultFileNameValidator__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultFileNameValidator> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPageView__quals__j_e_i_Any_j_e_i_Default extends Factory<IncludedModelsPageView> { }
  public abstract class Type_factory__o_u_c_d_v_m_MenuBuilder__quals__j_e_i_Any_j_e_i_Default extends Factory<MenuBuilder> { }
  public abstract class Type_factory__o_k_w_c_w_c_s_ViewSourceViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ViewSourceViewImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_s_DMNGraphSubIndex__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNGraphSubIndex> { }
  public abstract class Producer_factory__o_u_e_e_c_c_f_p_CopyPopUpPresenter_View__quals__j_e_i_Any_o_u_e_e_c_c_f_Customizable extends Factory<View> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_ValueAndDataTypePopoverImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ValueAndDataTypePopoverImpl> { }
  public abstract class Type_factory__o_u_e_w_c_c_t_p_NewFilterPopup__quals__j_e_i_Any_j_e_i_Default extends Factory<NewFilterPopup> { }
  public abstract class JsType_factory__j_x_n_QName__quals__Universal extends Factory<QName> { }
  public abstract class Type_factory__o_k_w_c_d_c_p_d_QNameFieldRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<QNameFieldRenderer> { }
  public abstract class ExtensionProvided_factory__o_j_e_c_c_d_ListItem__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<ListItem> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_c_DataTypeManager__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeManager> { }
  public abstract class Type_factory__o_k_w_c_s_f_c_w_c_FormsContainerViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FormsContainerViewImpl> { }
  public abstract class Producer_factory__o_u_r_SessionInfo__quals__j_e_i_Any_j_e_i_Default extends Factory<SessionInfo> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_r_i_ClientDefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientDefinitionsCacheRegistry> { }
  public abstract class Type_factory__o_u_e_w_c_c_d_n_NoItemsComponentViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<NoItemsComponentViewImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_c_a_TextPropertyProviderFactoryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<TextPropertyProviderFactoryImpl> { }
  public abstract class Type_factory__o_u_e_l_e_c_i_e_DisabledExperimentalLayoutComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<DisabledExperimentalLayoutComponent> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_t_d_DecisionTableEditorDefinitionEnricher__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionTableEditorDefinitionEnricher> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_v_ClientModelValidator__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientModelValidator> { }
  public abstract class Type_factory__o_k_w_c_s_s_p_c_WorkbenchPreferenceScopeTypes__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchPreferenceScopeTypes> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_d_DMNDocumentationView__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNDocumentationView> { }
  public abstract class Type_factory__o_u_e_e_c_c_m_c_SaveAndRenameCommandBuilder__quals__j_e_i_Any_j_e_i_Default extends Factory<SaveAndRenameCommandBuilder> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_i_i_DynamicFormHandlerGenerator__quals__j_e_i_Any_o_k_w_c_f_d_s_s_DynamicContext extends Factory<DynamicFormHandlerGenerator> { }
  public abstract class Type_factory__o_k_w_c_f_p_e_h_i_FormHandlerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FormHandlerImpl> { }
  public abstract class Type_factory__o_u_e_c_e_g_ExperimentalFeaturesGroupViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ExperimentalFeaturesGroupViewImpl> { }
  public abstract class Type_factory__o_u_e_p_c_c_f_DefaultPreferenceFormActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<DefaultPreferenceFormActivity> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_CharacterEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default extends Factory<CharacterEditableColumnGenerator> { }
  public abstract class JsType_factory__o_k_w_c_d_w_k_m_j_m_d_JSITAssociationDirection__quals__Universal extends Factory<JSITAssociationDirection> { }
  public abstract class Type_factory__o_k_w_c_s_c_r_h_i_ContainmentEvaluationHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<ContainmentEvaluationHandler> { }
  public abstract class JsType_factory__o_u_m_i_ExternalPathPlaceRequest__quals__Universal extends Factory<ExternalPathPlaceRequest> { }
  public abstract class Type_factory__o_j_e_e_c_c_InstanceProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<InstanceProvider> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimePickerView__quals__j_e_i_Any_j_e_i_Default extends Factory<TimePickerView> { }
  public abstract class Type_factory__o_k_w_c_w_c_w_KSessionSelector__quals__j_e_i_Any_j_e_i_Default extends Factory<KSessionSelector> { }
  public abstract class Type_factory__o_k_w_c_w_c_c_CardsGridComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<CardsGridComponent> { }
  public abstract class Type_factory__o_u_c_w_p_i_MultiScreenWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<MultiScreenWorkbenchPanelView> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_c_b_i_ObserverBuilderControl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_c_c_b_i_Observer extends Factory<ObserverBuilderControl> { }
  public abstract class Type_factory__o_u_c_v_p_n_NotificationPopupsManagerView__quals__j_e_i_Any_j_e_i_Default extends Factory<NotificationPopupsManagerView> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_t_u_UndefinedExpressionEditorDefinition__quals__j_e_i_Any_j_e_i_Default extends Factory<UndefinedExpressionEditorDefinition> { }
  public abstract class Type_factory__o_k_w_c_d_c_p_d_NameFieldRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<NameFieldRenderer> { }
  public abstract class Type_factory__o_u_e_e_c_c_f_p_CopyPopUpPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<CopyPopUpPresenter> { }
  public abstract class Type_factory__o_u_c_v_p_w_DateRangePicker__quals__j_e_i_Any_j_e_i_Default extends Factory<DateRangePicker> { }
  public abstract class Type_factory__o_g_m_c_c_w_b_ViewHideAlertsButtonPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<ViewHideAlertsButtonPresenter> { }
  public abstract class Type_factory__o_g_m_c_c_w_b_ViewHideAlertsButtonView__quals__j_e_i_Any_j_e_i_Default extends Factory<ViewHideAlertsButtonView> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_p_h_DataTypeUpdateHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeUpdateHandler> { }
  public abstract class Type_factory__o_k_w_c_s_c_d_c_DeepCloneProcess__quals__j_e_i_Any_j_e_i_Default extends Factory<DeepCloneProcess> { }
  public abstract class Type_factory__o_k_w_c_w_c_d_DiscussionWidgetPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<DiscussionWidgetPresenter> { }
  public abstract class Type_factory__o_u_c_m_ForceUnlockEventObserver__quals__j_e_i_Any_j_e_i_Default extends Factory<ForceUnlockEventObserver> { }
  public abstract class Type_factory__o_k_w_c_s_f_c_n_FormGenerationNotifier__quals__j_e_i_Any_j_e_i_Default extends Factory<FormGenerationNotifier> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_c_p_LienzoConnectorProxyView__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoConnectorProxyView> { }
  public abstract class Type_factory__o_u_c_w_PanelManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<PanelManagerImpl> { }
  public abstract class Type_factory__o_u_e_w_c_c_s_SelectOptionComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<SelectOptionComponent> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_r_m_b_DynamicBindingHelper__quals__j_e_i_Any_j_e_i_Default extends Factory<DynamicBindingHelper> { }
  public abstract class ExtensionProvided_factory__o_g_c_u_CheckBox__quals__j_e_i_Any_j_e_i_Default extends Factory<CheckBox> { }
  public abstract class Type_factory__o_k_w_c_s_k_c_v_DiagramEditorScreenViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DiagramEditorScreenViewImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_c_BaseSelectorView__quals__j_e_i_Any_j_e_i_Default extends Factory<BaseSelectorView> { }
  public abstract class Type_factory__o_u_c_w_p_i_SimpleNoExpandWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<SimpleNoExpandWorkbenchPanelPresenter> { }
  public abstract class ExtensionProvided_factory__o_u_e_w_c_c_t_Tree__quals__j_e_i_Any_j_e_i_Default extends Factory<Tree> { }
  public abstract class Type_factory__o_u_c_m_SplashScreenMenuPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<SplashScreenMenuPresenter> { }
  public abstract class ExtensionProvided_factory__c_g_g_u_c_u_FlowPanel__quals__j_e_i_Any_j_e_i_Default extends Factory<com.google.gwt.user.client.ui.FlowPanel> { }
  public abstract class Type_factory__o_k_w_c_s_c_r_h_i_ElementCardinalityEvaluationHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<ElementCardinalityEvaluationHandler> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_d_DRDNameChangerView__quals__j_e_i_Any_j_e_i_Default extends Factory<DRDNameChangerView> { }
  public abstract class Type_factory__o_u_e_e_c_c_f_e_j_FileExportScriptInjector__quals__j_e_i_Any_j_e_i_Default extends Factory<FileExportScriptInjector> { }
  public abstract class Type_factory__o_k_w_c_s_s_c_f_BasicShapesFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<BasicShapesFactory> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_s_DMNGridHelper__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNGridHelper> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_t_c_RedoToolbarCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<RedoToolbarCommand> { }
  public abstract class Type_factory__o_k_w_c_s_c_l_u_LienzoPanelUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoPanelUtils> { }
  public abstract class Type_factory__o_k_w_c_s_s_r_WorkbenchActivities__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchActivities> { }
  public abstract class Type_factory__o_k_w_c_f_c_r_c_w_p_PictureInput__quals__j_e_i_Any_j_e_i_Default extends Factory<PictureInput> { }
  public abstract class Type_factory__o_u_e_w_c_c_e_t_TextEditorView__quals__j_e_i_Any_j_e_i_Default extends Factory<TextEditorView> { }
  public abstract class Type_factory__o_k_w_c_d_w_k_c_c_d_KogitoPropertiesDock__quals__j_e_i_Any_j_e_i_Default extends Factory<KogitoPropertiesDock> { }
  public abstract class ExtensionProvided_factory__o_j_e_c_c_d_TextArea__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<org.jboss.errai.common.client.dom.TextArea> { }
  public abstract class Type_factory__o_g_c_s_p_c_u_POMDefaultOptions__quals__j_e_i_Any_j_e_i_Default extends Factory<POMDefaultOptions> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_s_l_StringListBoxFieldRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<StringListBoxFieldRenderer> { }
  public abstract class Type_factory__o_k_w_c_s_c_g_p_l_s_s_MedianVertexLayerPositioning__quals__j_e_i_Any_j_e_i_Default extends Factory<MedianVertexLayerPositioning> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_t_f_p_ParameterViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ParameterViewImpl> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_f_l_l_FieldLabelViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldLabelViewImpl> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_i_i_StaticFormHandlerGenerator__quals__j_e_i_Any_o_k_w_c_f_d_s_s_StaticContext extends Factory<StaticFormHandlerGenerator> { }
  public abstract class Type_factory__o_k_w_c_w_c_w_KieSelectElement__quals__j_e_i_Any_j_e_i_Default extends Factory<KieSelectElement> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_c_ContextMenu__quals__j_e_i_Any_j_e_i_Default extends Factory<ContextMenu> { }
  public abstract class Type_factory__o_u_c_w_p_i_TemplatedWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<TemplatedWorkbenchPanelView> { }
  public abstract class Type_factory__o_u_s_c_a_AuthorizationManagerHelper__quals__j_e_i_Any_j_e_i_Default extends Factory<AuthorizationManagerHelper> { }
  public abstract class Type_factory__o_k_w_c_d_c_w_c_MonacoFEELVariableSuggestions__quals__j_e_i_Any_j_e_i_Default extends Factory<MonacoFEELVariableSuggestions> { }
  public abstract class Producer_factory__o_a_c_s_r_Registry__quals__j_e_i_Any_j_e_i_Default extends Factory<Registry> { }
  public abstract class Type_factory__o_k_w_c_f_c_r_c_w_d_DecimalBoxViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DecimalBoxViewImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_TimeSelectorView__quals__j_e_i_Any_j_e_i_Default extends Factory<TimeSelectorView> { }
  public abstract class JsType_factory__o_u_c_m_PlaceManager__quals__Universal extends Factory<PlaceManager> { }
  public abstract class Type_factory__o_k_w_c_s_c_u_ClassUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<ClassUtils> { }
  public abstract class JsType_factory__o_k_w_c_d_w_k_m_j_m_d_JSITDecisionTableOrientation__quals__Universal extends Factory<JSITDecisionTableOrientation> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_MouseRequestLifecycle__quals__j_e_i_Any_j_e_i_Default extends Factory<MouseRequestLifecycle> { }
  public abstract class Type_factory__o_k_w_c_s_c_r_CachedRuleManager__quals__j_e_i_Any_j_e_i_Default extends Factory<CachedRuleManager> { }
  public abstract class Type_factory__o_k_w_c_f_a_e_s_i_FormBuildingServiceImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FormBuildingServiceImpl> { }
  public abstract class Type_factory__o_u_c_v_p_m_UserMenu__quals__j_e_i_Any_j_e_i_Default extends Factory<UserMenu> { }
  public abstract class Type_factory__o_u_c_v_p_d_CompassWidgetImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<CompassWidgetImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_w_t_DMNPerformAutomaticLayoutToolbarCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNPerformAutomaticLayoutToolbarCommand> { }
  public abstract class Type_factory__o_k_w_c_d_c_r_DMNDecisionServiceSVGViewFactoryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDecisionServiceSVGViewFactoryImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_a_ClientFactoryManager__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientFactoryManager> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_i_g_DMNCardComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNCardComponent> { }
  public abstract class Type_factory__o_k_w_c_w_c_m_RepositoryMenu__quals__j_e_i_Any_j_e_i_Default extends Factory<RepositoryMenu> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_i_t_TreeListView__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeListView> { }
  public abstract class Type_factory__o_u_c_w_w_t_WorkbenchToolBarPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchToolBarPresenter> { }
  public abstract class Type_factory__o_k_w_c_f_c_r_c_w_i_IntegerBoxViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<IntegerBoxViewImpl> { }
  public abstract class Type_factory__o_u_e_e_c_c_BaseEditorEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<BaseEditorEntryPoint> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_RegistryAwareCommandManager__quals__j_e_i_Any_j_e_i_Default extends Factory<RegistryAwareCommandManager> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_MultipleInput__quals__j_e_i_Any_j_e_i_Default extends Factory<MultipleInput> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_IntegerBoxFieldRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<IntegerBoxFieldRenderer> { }
  public abstract class Type_factory__o_u_e_e_c_c_f_e_FileExportProducer__quals__j_e_i_Any_j_e_i_Default extends Factory<FileExportProducer> { }
  public abstract class JsType_factory__o_u_w_m_Position__quals__Universal extends Factory<Position> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_t_ExpressionEditorDefinitionsProducer__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<ExpressionEditorDefinitionsProducer> { }
  public abstract class Type_factory__o_u_e_p_c_c_s_PreferencesRootScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<PreferencesRootScreenActivity> { }
  public abstract class Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSessionState__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDiagramsSessionState> { }
  public abstract class Type_factory__o_k_w_c_s_c_g_p_t_t_TreeWalkTraverseProcessorImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeWalkTraverseProcessorImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_c_c_b_ObserverBuilderControl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor_o_k_w_c_s_c_c_c_c_b_i_Observer extends Factory<org.kie.workbench.common.dmn.client.canvas.controls.builder.ObserverBuilderControl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPageState__quals__j_e_i_Any_j_e_i_Default extends Factory<IncludedModelsPageState> { }
  public abstract class ExtensionProvided_factory__o_k_w_c_s_c_w_p_s_i_SessionContainer__quals__j_e_i_Any_j_e_i_Default extends Factory<SessionContainer> { }
  public abstract class Type_factory__o_a_k_b_c_s_r_p_CommandRegistryProducer__quals__j_e_i_Any_j_e_i_Default extends Factory<CommandRegistryProducer> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_t_c_ExportToSvgToolbarCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<ExportToSvgToolbarCommand> { }
  public abstract class JsType_factory__o_u_c_m_LockTarget_TitleProvider__quals__Universal extends Factory<TitleProvider> { }
  public abstract class Type_factory__o_u_e_w_c_c_t_SimpleTable__quals__j_e_i_Any_j_e_i_Default extends Factory<SimpleTable> { }
  public abstract class Type_factory__o_u_e_p_c_c_t_TreeHierarchyInternalItemPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeHierarchyInternalItemPresenter> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_s_l_EnumListBoxFieldRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<EnumListBoxFieldRenderer> { }
  public abstract class ExtensionProvided_factory__c_g_g_u_c_u_Label__quals__j_e_i_Any_j_e_i_Default extends Factory<com.google.gwt.user.client.ui.Label> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_c_a_DefaultTextPropertyProviderImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultTextPropertyProviderImpl> { }
  public abstract class Type_factory__o_u_e_l_e_c_w_LayoutEditorPropertiesView__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutEditorPropertiesView> { }
  public abstract class Type_factory__o_k_w_c_d_c_w_t_DMNSugiyamaLayoutService__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNSugiyamaLayoutService> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_i_InlineTextEditorBoxImpl__quals__j_e_i_Any_o_k_w_c_s_c_c_c_c_i_InlineTextEditorBox extends Factory<InlineTextEditorBoxImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_c_k_KeyEventHandlerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<KeyEventHandlerImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_t_a_MorphNodeToolboxAction__quals__j_e_i_Any_j_e_i_Default extends Factory<MorphNodeToolboxAction> { }
  public abstract class Type_factory__o_k_w_c_s_f_c_s_StunnerFormsHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerFormsHandler> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_DateSelector__quals__j_e_i_Any_j_e_i_Default extends Factory<DateSelector> { }
  public abstract class JsType_factory__o_u_w_m_CompassPosition__quals__Universal extends Factory<CompassPosition> { }
  public abstract class Type_factory__o_u_e_c_p_ExperimentalFeaturesPerspectiveActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<ExperimentalFeaturesPerspectiveActivity> { }
  public abstract class Type_factory__o_u_c_m_ActivityManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ActivityManagerImpl> { }
  public abstract class JsType_factory__o_u_w_m_m_MenuVisitor__quals__Universal extends Factory<MenuVisitor> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<IncludedModelsFactory> { }
  public abstract class Type_factory__o_k_w_c_d_c_c_c_r_DecisionServiceMoveDividerControl__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionServiceMoveDividerControl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPage__quals__j_e_i_Any_j_e_i_Default extends Factory<IncludedModelsPage> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_v_s_ScreenPanelViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ScreenPanelViewImpl> { }
  public abstract class Type_factory__o_u_p_c_s_PreferenceScopeResolutionStrategyIOCProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferenceScopeResolutionStrategyIOCProvider> { }
  public abstract class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_BooleanEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default extends Factory<BooleanEditableColumnGenerator> { }
  public abstract class Type_factory__o_k_w_c_w_c_p_v_ValidationPopupViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ValidationPopupViewImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_m_MarshallingResponsePopupView__quals__j_e_i_Any_j_e_i_Default extends Factory<MarshallingResponsePopupView> { }
  public abstract class Type_factory__o_k_w_c_s_s_p_c_WorkbenchPreferenceScopeResolutionStrategy__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchPreferenceScopeResolutionStrategy> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_i_i_p_PMMLIncludedModelHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<PMMLIncludedModelHandler> { }
  public abstract class Type_factory__o_k_w_c_d_c_p_d_QNameFieldProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<QNameFieldProvider> { }
  public abstract class Type_factory__o_u_e_w_c_c_d_PerspectiveDropDown__quals__j_e_i_Any_j_e_i_Default extends Factory<PerspectiveDropDown> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_c_b_i_EdgeBuilderControlImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<EdgeBuilderControlImpl> { }
  public abstract class Type_factory__o_k_w_c_d_w_k_c_c_e_DMNEditorMenuSessionItems__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNEditorMenuSessionItems> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_i_c_IncludedModelsContext__quals__j_e_i_Any_j_e_i_Default extends Factory<IncludedModelsContext> { }
  public abstract class Type_factory__o_k_w_c_w_c_h_NewResourcesMenu__quals__j_e_i_Any_j_e_i_Default extends Factory<NewResourcesMenu> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_i_g_e_DMNCardsEmptyStateView__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNCardsEmptyStateView> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_e_t_l_ListEditorDefinition__quals__j_e_i_Any_j_e_i_Default extends Factory<ListEditorDefinition> { }
  public abstract class Type_factory__o_k_w_c_d_a_d_f_DMNDefinitionSetModelFactoryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDefinitionSetModelFactoryImpl> { }
  public abstract class Type_factory__o_u_e_p_c_c_a_PreferencesCentralActionsView__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferencesCentralActionsView> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_DataTypesPage__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypesPage> { }
  public abstract class Type_factory__o_k_w_c_d_c_c_c_k_s_AppendDecisionShortcut__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<AppendDecisionShortcut> { }
  public abstract class Type_factory__o_u_e_p_c_c_PreferencesCentralPerspective__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferencesCentralPerspective> { }
  public abstract class ExtensionProvided_factory__e_d_HTMLParagraphElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<HTMLParagraphElement> { }
  public abstract class Type_factory__o_g_c_s_p_c_r_ConflictingRepositoriesPopupViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ConflictingRepositoriesPopupViewImpl> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_e_DataTypeConstraintEnumerationView__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeConstraintEnumerationView> { }
  public abstract class Type_factory__o_k_w_c_s_k_r_c_s_c_i_KogitoKeyEventHandlerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<KogitoKeyEventHandlerImpl> { }
  public abstract class Type_factory__o_g_c_s_p_c_Decision__quals__j_e_i_Any_j_e_i_Default extends Factory<Decision> { }
  public abstract class Type_factory__o_u_c_v_p_m_m_c_GroupContextMenuItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<GroupContextMenuItemView> { }
  public abstract class Type_factory__o_u_e_w_c_c_s_i_IFrameScreenPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<IFrameScreenPresenter> { }
  public abstract class Type_factory__o_k_w_c_f_c_c_c_f_m_ModalFormDisplayerViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ModalFormDisplayerViewImpl> { }
  public abstract class Type_factory__o_u_e_l_e_c_w_LayoutEditorPropertiesPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutEditorPropertiesPresenter> { }
  public abstract class ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<org.jboss.errai.common.client.dom.Button> { }
  public abstract class Type_factory__o_u_e_l_e_c_w_LayoutElementPropertiesView__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutElementPropertiesView> { }
  public abstract class Type_factory__o_k_w_c_d_c_w_l_MousePanMediatorControlImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<MousePanMediatorControlImpl> { }
  public abstract class Type_factory__o_k_w_c_s_c_w_m_d_i_ChangeProfileDevCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<ChangeProfileDevCommand> { }
  public abstract class Type_factory__o_k_w_c_s_c_c_c_u_CanvasFileExport__quals__j_e_i_Any_j_e_i_Default extends Factory<CanvasFileExport> { }
  public abstract class JsType_factory__o_k_w_c_d_w_k_m_j_m_d_JSITFunctionKind__quals__Universal extends Factory<JSITFunctionKind> { }
  public abstract class Type_factory__o_k_w_c_d_c_e_t_l_c_c_ConstraintPlaceholderHelper__quals__j_e_i_Any_j_e_i_Default extends Factory<ConstraintPlaceholderHelper> { }
  public abstract class Type_factory__o_k_w_c_d_c_c_c_DMNDeepCloneProcess__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDeepCloneProcess> { }
  public abstract class JsType_factory__o_k_w_c_d_w_k_m_j_m_d_JSITBuiltinAggregator__quals__Universal extends Factory<JSITBuiltinAggregator> { }
  public abstract class Type_factory__o_u_c_w_p_i_LayoutPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<LayoutPanelView> { }
  public abstract class Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetDefinitionAdapterImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDefinitionSetDefinitionAdapterImpl> { }
  private static void addLookups_0() {
    CDIEventTypeLookup.get().addLookup("org.kie.workbench.common.dmn.client.events.EditExpressionEvent", "org.kie.workbench.common.stunner.core.client.session.event.AbstractSessionEvent");
    CDIEventTypeLookup.get().addLookup("org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent", "java.lang.Object");
    CDIEventTypeLookup.get().addLookup("org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent", "org.uberfire.workbench.events.UberFireEvent");
    CDIEventTypeLookup.get().addLookup("org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent", "java.lang.Object");
    CDIEventTypeLookup.get().addLookup("org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent", "org.uberfire.workbench.events.UberFireEvent");
    CDIEventTypeLookup.get().addLookup("org.uberfire.client.views.pfly.multipage.MultiPageEditorSelectedPageEvent", "java.lang.Object");
    CDIEventTypeLookup.get().addLookup("org.uberfire.client.views.pfly.multipage.MultiPageEditorSelectedPageEvent", "org.uberfire.workbench.events.UberFireEvent");
    CDIEventTypeLookup.get().addLookup("org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent", "java.lang.Object");
    CDIEventTypeLookup.get().addLookup("org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent", "org.uberfire.workbench.events.UberFireEvent");
  }

  private void registerFactories0(final Context org_jboss_errai_ioc_client_container_ApplicationScopedContext_context, final Context org_jboss_errai_ioc_client_container_DependentScopeContext_context) {
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<MessageConsoleScreen>) GWT.create(Type_factory__o_g_m_c_c_MessageConsoleScreen__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<BigIntegerEditableColumnGenerator>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_n_i_BigIntegerEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypePickerWidget>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_DataTypePickerWidget__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Cookie>) GWT.create(Type_factory__o_u_c_u_Cookie__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ProfilePreferencesBeanGeneratedImpl>) GWT.create(Type_factory__o_k_w_c_p_a_p_ProfilePreferencesBeanGeneratedImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNAssetsDropdownItemsProvider>) GWT.create(Type_factory__o_k_w_c_d_c_e_i_m_d_DMNAssetsDropdownItemsProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SearchBarComponent>) GWT.create(Type_factory__o_k_w_c_w_c_s_c_SearchBarComponent__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ToggleSwitch>) GWT.create(ExtensionProvided_factory__o_g_e_t_c_u_ToggleSwitch__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LayerCrossingCount>) GWT.create(Type_factory__o_k_w_c_s_c_g_p_l_s_s_LayerCrossingCount__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<EmbeddedFormDisplayerViewImpl>) GWT.create(Type_factory__o_k_w_c_f_c_c_c_f_e_EmbeddedFormDisplayerViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<Workbench>) GWT.create(Type_factory__o_u_c_w_Workbench__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MultiListWorkbenchPanelPresenter>) GWT.create(Type_factory__o_u_c_w_p_i_MultiListWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeDestroyHandler>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_p_h_DataTypeDestroyHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ResourceType>) GWT.create(JsType_factory__o_u_s_ResourceType__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SugiyamaLayoutService>) GWT.create(Type_factory__o_k_w_c_s_c_g_p_l_s_SugiyamaLayoutService__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LienzoPictureGlyphRenderer>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_g_LienzoPictureGlyphRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNElementsSynchronizer>) GWT.create(Type_factory__o_k_w_c_d_c_g_DMNElementsSynchronizer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LienzoMultipleSelectionControl>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_c_LienzoMultipleSelectionControl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_c_c_s_MultipleSelection.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ActivityBeansInfo>) GWT.create(Type_factory__o_u_c_m_ActivityBeansInfo__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<UnanchoredWorkbenchPartView>) GWT.create(Type_factory__o_u_c_w_p_u_p_UnanchoredWorkbenchPartView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<GuidedTourUtils>) GWT.create(Type_factory__o_k_w_c_d_w_k_c_c_t_c_GuidedTourUtils__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<MapModelBindingHelper>) GWT.create(Type_factory__o_k_w_c_f_d_c_h_MapModelBindingHelper__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DisabledFeatureComponentViewImpl>) GWT.create(Type_factory__o_u_e_c_d_c_DisabledFeatureComponentViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<GraphProcessorImpl>) GWT.create(Type_factory__o_k_w_c_s_c_g_p_l_s_GraphProcessorImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TextBox>) GWT.create(ExtensionProvided_factory__o_g_c_u_TextBox__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Select>) GWT.create(Type_factory__o_u_c_v_p_w_Select__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ImageStripDOMGlyphRenderer>) GWT.create(Type_factory__o_k_w_c_s_c_w_c_g_ImageStripDOMGlyphRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SVGShapeFactory>) GWT.create(Type_factory__o_k_w_c_s_s_c_s_f_SVGShapeFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<IncludedModelModalView>) GWT.create(Type_factory__o_k_w_c_d_c_e_i_m_IncludedModelModalView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CopyPopupWithPackageView>) GWT.create(Type_factory__o_k_w_c_w_c_p_c_CopyPopupWithPackageView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<VisitGraphToolbarCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_t_c_VisitGraphToolbarCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<AdminPagePresenterActivity>) GWT.create(Type_factory__o_u_e_p_c_a_AdminPagePresenterActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ClientFactoryService>) GWT.create(Type_factory__o_k_w_c_s_c_c_s_ClientFactoryService__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TextAreaFieldRenderer>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_TextAreaFieldRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNIncludeModelsClient>) GWT.create(Type_factory__o_k_w_c_d_c_a_i_l_DMNIncludeModelsClient__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<BS3IconTypeGlyphRenderer>) GWT.create(Type_factory__o_k_w_c_s_c_w_c_g_BS3IconTypeGlyphRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ErrorPopupView>) GWT.create(Type_factory__o_u_c_v_p_w_ErrorPopupView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<KogitoDecisionNavigatorDock>) GWT.create(Type_factory__o_k_w_c_d_w_k_c_c_d_KogitoDecisionNavigatorDock__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DecisionNavigatorNestedItemFactory>) GWT.create(Type_factory__o_k_w_c_d_c_d_n_f_DecisionNavigatorNestedItemFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNDiagramTextPropertyProviderImpl>) GWT.create(Type_factory__o_k_w_c_d_c_c_c_a_DMNDiagramTextPropertyProviderImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<KogitoPreviewDiagramDock>) GWT.create(Type_factory__o_k_w_c_d_w_k_c_c_d_KogitoPreviewDiagramDock__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<AlertsEntryPoint>) GWT.create(Type_factory__o_g_m_c_c_AlertsEntryPoint__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNCanvasShortcutsControlImpl>) GWT.create(Type_factory__o_k_w_c_d_c_c_c_k_DMNCanvasShortcutsControlImpl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNDeleteNodeToolboxAction>) GWT.create(Type_factory__o_k_w_c_d_c_c_c_t_DMNDeleteNodeToolboxAction__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<NumberSelector>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_n_NumberSelector__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultDiagramDocumentationView>) GWT.create(Type_factory__o_k_w_c_s_c_d_DefaultDiagramDocumentationView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ColorPickerWidget>) GWT.create(Type_factory__o_k_w_c_s_f_c_f_c_ColorPickerWidget__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PMMLFunctionEditorDefinition>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_t_f_s_p_PMMLFunctionEditorDefinition__quals__j_e_i_Any_o_k_w_c_d_c_e_e_t_f_FunctionGridSupplementaryEditor.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<NodeDockingEvaluationHandler>) GWT.create(Type_factory__o_k_w_c_s_c_r_h_i_NodeDockingEvaluationHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MenuGroup>) GWT.create(JsType_factory__o_u_w_m_m_MenuGroup__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeListHighlightHelper>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListHighlightHelper__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<BasicUserCache>) GWT.create(Type_factory__o_j_e_s_s_a_i_BasicUserCache__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<AlertsButtonMenuItemBuilder>) GWT.create(Type_factory__o_g_m_c_c_w_b_AlertsButtonMenuItemBuilder__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<KogitoClientEntryPoint>) GWT.create(Type_factory__o_k_w_c_s_k_c_KogitoClientEntryPoint__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<RuleLookupManagerImpl>) GWT.create(Type_factory__o_k_w_c_s_c_l_r_RuleLookupManagerImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<StunnerLienzoBoundsPanel>) GWT.create(Type_factory__o_k_w_c_s_c_w_c_StunnerLienzoBoundsPanel__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MultipleInputComponentViewImpl>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_MultipleInputComponentViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<NotificationsObserver>) GWT.create(Type_factory__o_k_w_c_s_c_w_n_NotificationsObserver__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ErrorPopupPresenter>) GWT.create(Type_factory__o_u_c_w_w_c_ErrorPopupPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Event>) GWT.create(ContextualProvider_factory__j_e_e_Event__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CharacterRadioGroupFieldRenderer>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_s_r_CharacterRadioGroupFieldRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TextBoxFieldProvider>) GWT.create(Type_factory__o_k_w_c_f_f_s_f_b_t_p_TextBoxFieldProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ConflictingRepositoriesPopup>) GWT.create(Type_factory__o_g_c_s_p_c_r_ConflictingRepositoriesPopup__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<JSIAlignmentKind>) GWT.create(JsType_factory__o_k_w_c_d_w_k_m_j_m_d_JSIAlignmentKind__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNCanvasInlineTextEditorControl>) GWT.create(Type_factory__o_k_w_c_d_c_c_c_i_DMNCanvasInlineTextEditorControl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor_o_k_w_c_s_c_c_c_c_b_i_Observer.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ManagedSession>) GWT.create(Type_factory__o_k_w_c_s_c_c_s_i_ManagedSession__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNViewerSession>) GWT.create(Type_factory__o_k_w_c_d_c_s_DMNViewerSession__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<KogitoEditorContextProviderImpl>) GWT.create(Type_factory__o_a_k_b_c_c_i_KogitoEditorContextProviderImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ImportDataObjectModalView>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_i_ImportDataObjectModalView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ShortEditableColumnGenerator>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_n_i_ShortEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ValueAndDataTypePopoverViewImpl>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_ValueAndDataTypePopoverViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<POMEditorPanelViewImpl>) GWT.create(Type_factory__o_g_c_s_p_c_POMEditorPanelViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<Others>) GWT.create(Type_factory__o_u_w_c_Others__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNDocumentationViewButtonsVisibilitySupplier>) GWT.create(Type_factory__o_k_w_c_d_w_k_c_c_e_d_DMNDocumentationViewButtonsVisibilitySupplier__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Elemental2DomUtil>) GWT.create(Type_factory__o_j_e_c_c_d_e_Elemental2DomUtil__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<VersionRecordManager>) GWT.create(Type_factory__o_u_e_e_c_c_h_VersionRecordManager__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<WorkbenchServicesProxyClientImpl>) GWT.create(Type_factory__o_u_c_w_WorkbenchServicesProxyClientImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<StaticWorkbenchPanelPresenter>) GWT.create(Type_factory__o_u_c_w_p_i_StaticWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<UndefinedExpressionSelectorPopoverViewImpl>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_t_u_s_UndefinedExpressionSelectorPopoverViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ConfigurationComboBoxItemWidget>) GWT.create(Type_factory__o_k_w_c_w_c_h_w_c_ConfigurationComboBoxItemWidget__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Button>) GWT.create(Type_factory__o_u_c_v_p_w_Button__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<TableEntryPoint>) GWT.create(Type_factory__o_u_e_w_t_c_TableEntryPoint__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultFileEditorPresenter>) GWT.create(Type_factory__o_u_e_w_c_c_e_d_DefaultFileEditorPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MultipleSelectorInputViewImpl>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_l_s_i_MultipleSelectorInputViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<KieSelectOptionView>) GWT.create(Type_factory__o_k_w_c_w_c_w_KieSelectOptionView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ItemDefinitionStore>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_p_ItemDefinitionStore__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DockPlaceHolderBaseViewImpl>) GWT.create(Type_factory__o_k_w_c_w_c_d_DockPlaceHolderBaseViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ToolBarIcon>) GWT.create(JsType_factory__o_u_w_m_t_ToolBarIcon__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<KieAssetsDropdownView>) GWT.create(Type_factory__o_k_w_c_w_c_a_d_KieAssetsDropdownView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DecisionComponentsItem>) GWT.create(Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponentsItem__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MultipleSubFormFieldProvider>) GWT.create(Type_factory__o_k_w_c_f_f_s_f_r_m_p_MultipleSubFormFieldProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ElementProxy>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_p_ElementProxy__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LockManager>) GWT.create(JsType_factory__o_u_c_m_LockManager__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<NumberInput>) GWT.create(ExtensionProvided_factory__o_j_e_c_c_d_NumberInput__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SimpleWorkbenchPanelPresenter>) GWT.create(Type_factory__o_u_c_w_p_i_SimpleWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<NoInputNodesInImportedDecisionRule>) GWT.create(Type_factory__o_k_w_c_d_a_r_NoInputNodesInImportedDecisionRule__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HTMLOptGroupElement>) GWT.create(ExtensionProvided_factory__e_d_HTMLOptGroupElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ParametersPopoverViewImpl>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_t_f_p_ParametersPopoverViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ClientSelectorDataProviderManager>) GWT.create(Type_factory__o_k_w_c_f_d_c_c_ClientSelectorDataProviderManager__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<WiresCanvasView>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_w_WiresCanvasView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CanvasHandlerImpl>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_CanvasHandlerImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CellEditorControlImpl>) GWT.create(Type_factory__o_k_w_c_d_c_w_g_c_c_CellEditorControlImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FormLabel>) GWT.create(Type_factory__o_u_c_v_p_w_FormLabel__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TextEditorWorkbenchEditor>) GWT.create(Type_factory__o_u_e_w_c_c_e_t_TextEditorWorkbenchEditor__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<LayoutComponentPaletteScreen>) GWT.create(Type_factory__o_u_e_l_e_c_LayoutComponentPaletteScreen__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<IncludedModelErrorMessageFactory>) GWT.create(Type_factory__o_k_w_c_d_c_e_i_i_m_IncludedModelErrorMessageFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<KeyboardShortcutsApi>) GWT.create(Producer_factory__o_a_k_b_c_k_KeyboardShortcutsApi__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultPaletteFactory>) GWT.create(Type_factory__o_k_w_c_s_c_w_p_DefaultPaletteFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SaveDiagramSessionCommand>) GWT.create(Type_factory__o_k_w_c_d_w_k_c_c_s_c_SaveDiagramSessionCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNGraphFactoryImpl>) GWT.create(Type_factory__o_k_w_c_d_a_f_DMNGraphFactoryImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LienzoNodeProxyView>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_p_LienzoNodeProxyView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DefaultPermissionManager>) GWT.create(Type_factory__o_u_s_i_a_DefaultPermissionManager__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DiagramNavigatorItemImpl>) GWT.create(Type_factory__o_k_w_c_s_c_w_e_n_d_DiagramNavigatorItemImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<BooleanField>) GWT.create(Type_factory__o_u_e_p_e_c_f_BooleanField__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<GuidedTourService>) GWT.create(Producer_factory__o_a_k_b_c_g_t_s_GuidedTourService__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MenuPosition>) GWT.create(JsType_factory__o_u_w_m_m_MenuPosition__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DiagramEditorPropertiesScreenActivity>) GWT.create(Type_factory__o_k_w_c_s_k_c_s_DiagramEditorPropertiesScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ActivityNotFoundPresenterActivity>) GWT.create(Type_factory__o_u_e_w_c_c_w_w_p_a_n_ActivityNotFoundPresenterActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DiagramEditorExplorerScreenActivity>) GWT.create(Type_factory__o_k_w_c_s_k_c_s_DiagramEditorExplorerScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<WorkbenchMegaMenuView>) GWT.create(Type_factory__o_u_c_v_p_m_m_WorkbenchMegaMenuView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DiagramsNavigatorImpl>) GWT.create(Type_factory__o_k_w_c_s_c_w_e_n_d_DiagramsNavigatorImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MultiScreenMenuBuilder>) GWT.create(Type_factory__o_u_c_v_p_m_MultiScreenMenuBuilder__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<KindPopoverViewImpl>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_t_f_k_KindPopoverViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SelectOptionView>) GWT.create(Type_factory__o_u_e_w_c_c_s_SelectOptionView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<AppFormerActivities>) GWT.create(Producer_factory__o_u_w_m_AppFormerActivities__quals__j_e_i_Any_o_u_a_Customizable.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<PatternFlyEntryPoint>) GWT.create(Type_factory__o_u_c_v_p_PatternFlyEntryPoint__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<WorkbenchMenuStandardNavBarView>) GWT.create(Type_factory__o_u_c_v_p_m_WorkbenchMenuStandardNavBarView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CollapsedDefinitionPaletteItemWidget>) GWT.create(Type_factory__o_k_w_c_s_c_w_p_c_CollapsedDefinitionPaletteItemWidget__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LayoutGenerator>) GWT.create(Type_factory__o_k_w_c_f_a_e_s_f_l_LayoutGenerator__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DNDListComponent>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_d_DNDListComponent__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SelectorView>) GWT.create(Type_factory__o_k_w_c_s_c_w_v_SelectorView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CanvasInlineTextEditorControl>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_c_i_CanvasInlineTextEditorControl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_c_c_i_InlineTextEditorBox.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MenuItemHeaderView>) GWT.create(Type_factory__o_u_e_w_c_c_m_MenuItemHeaderView__quals__j_e_i_Any_o_u_e_w_c_c_m_MenuItemHeader.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DefaultBeanFactory>) GWT.create(Type_factory__o_u_c_w_DefaultBeanFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DNDDataTypesHandler>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_d_DNDDataTypesHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FieldSetFormGroupViewImpl>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_f_i_n_f_FieldSetFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PasteToolbarCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_t_c_PasteToolbarCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CollapsibleFormGroupViewImpl>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DomainObjectAwareLienzoMultipleSelectionControl>) GWT.create(Type_factory__o_k_w_c_d_c_c_c_s_DomainObjectAwareLienzoMultipleSelectionControl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor_o_k_w_c_s_c_c_c_c_s_MultipleSelection.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ImportDataObjectModal>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_i_ImportDataObjectModal__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ProjectController>) GWT.create(Type_factory__o_g_c_s_p_c_s_ProjectController__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PathLabel>) GWT.create(Type_factory__o_k_w_c_w_c_h_PathLabel__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<PartManager>) GWT.create(Type_factory__o_u_c_w_p_s_PartManager__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<Undefined>) GWT.create(Type_factory__o_u_w_c_Undefined__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DatePickerWrapper>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_d_i_DatePickerWrapper__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CommandRegistryHolder>) GWT.create(Type_factory__o_k_w_c_d_c_s_CommandRegistryHolder__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ClientDiagramValidator>) GWT.create(Type_factory__o_k_w_c_s_c_c_v_ClientDiagramValidator__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SmallSwitchComponent>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_SmallSwitchComponent__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MessageConsoleScreenActivity>) GWT.create(Type_factory__o_g_m_c_c_MessageConsoleScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNSessionPreview>) GWT.create(Type_factory__o_k_w_c_d_c_s_p_i_DMNSessionPreview__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CardFrameComponentView>) GWT.create(Type_factory__o_k_w_c_w_c_c_f_CardFrameComponentView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefinitionPaletteGroupWidget>) GWT.create(Type_factory__o_k_w_c_s_c_w_p_c_g_DefinitionPaletteGroupWidget__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNCommonActionsToolboxFactory>) GWT.create(Type_factory__o_k_w_c_d_c_c_c_t_DMNCommonActionsToolboxFactory__quals__j_e_i_Any_o_k_w_c_d_c_c_c_t_DMNCommonActionsToolbox.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<FullProfile>) GWT.create(Type_factory__o_k_w_c_s_c_p_FullProfile__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNDefinitionSetDefinitionSetProxyImpl>) GWT.create(Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetDefinitionSetProxyImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<CDIClientFeatureDefRegistry>) GWT.create(Type_factory__o_u_e_c_s_i_CDIClientFeatureDefRegistry__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultDiagramEditor>) GWT.create(Type_factory__o_k_w_c_s_c_w_p_d_i_DefaultDiagramEditor__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HtmlEditor>) GWT.create(Type_factory__o_u_e_e_c_c_h_HtmlEditor__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<VFSLockServiceProxyClientImpl>) GWT.create(Type_factory__o_u_c_w_VFSLockServiceProxyClientImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<KSessionSelectorViewImpl>) GWT.create(Type_factory__o_k_w_c_w_c_w_KSessionSelectorViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNGridLayerControlImpl>) GWT.create(Type_factory__o_k_w_c_d_c_w_l_DMNGridLayerControlImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<AppLauncherView>) GWT.create(Type_factory__o_k_w_c_w_c_p_l_AppLauncherView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FormGenerationToolboxAction>) GWT.create(Type_factory__o_k_w_c_s_f_c_c_t_FormGenerationToolboxAction__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CheckBoxFieldRenderer>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_CheckBoxFieldRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<KieWorkbenchWidgetsCommonEntryPoint>) GWT.create(Type_factory__o_k_w_c_w_c_KieWorkbenchWidgetsCommonEntryPoint__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefinitionsHandler>) GWT.create(Type_factory__o_k_w_c_d_c_e_i_i_p_DefinitionsHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<LazyCanvasFocusUtils>) GWT.create(Type_factory__o_k_w_c_d_c_d_n_c_LazyCanvasFocusUtils__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PartContextMenusView>) GWT.create(Type_factory__o_u_c_v_p_m_PartContextMenusView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FieldLayoutComponent>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_FieldLayoutComponent__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ClientMetaDataEntryManager>) GWT.create(Type_factory__o_k_w_c_f_a_e_c_f_ClientMetaDataEntryManager__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<WiresLayer>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_w_WiresLayer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ResizeTabPanel>) GWT.create(Type_factory__o_u_c_v_p_t_ResizeTabPanel__quals__j_e_i_Any_o_u_c_v_p_t_Resize.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<TextAnnotationTextPropertyProviderImpl>) GWT.create(Type_factory__o_k_w_c_d_c_c_c_a_TextAnnotationTextPropertyProviderImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DivElement>) GWT.create(ExtensionProvided_factory__c_g_g_d_c_DivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<SingleConnectorPerTypeGraphRule>) GWT.create(Type_factory__o_k_w_c_d_a_r_SingleConnectorPerTypeGraphRule__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PartDefinition>) GWT.create(JsType_factory__o_u_w_m_PartDefinition__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<IncludedModelModal>) GWT.create(Type_factory__o_k_w_c_d_c_e_i_m_IncludedModelModal__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<UnanchoredStaticWorkbenchPanelPresenter>) GWT.create(Type_factory__o_u_c_w_p_i_UnanchoredStaticWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<FormsFlushManager>) GWT.create(Type_factory__o_k_w_c_s_f_c_w_FormsFlushManager__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DateTimeValueConverter>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DateTimeValueConverter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultPreferenceForm>) GWT.create(Type_factory__o_u_e_p_c_c_f_DefaultPreferenceForm__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<UnorderedList>) GWT.create(ExtensionProvided_factory__o_j_e_c_c_d_UnorderedList__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FieldReadOnlyEntryProcessor>) GWT.create(Type_factory__o_k_w_c_f_s_s_m_p_i_p_FieldReadOnlyEntryProcessor__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<IntegerListBoxFieldRenderer>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_s_l_IntegerListBoxFieldRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DefinitionsCacheRegistry>) GWT.create(Producer_factory__o_k_w_c_s_c_r_i_DefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<HTML5DndSeleniumSupport>) GWT.create(Type_factory__o_u_c_v_p_HTML5DndSeleniumSupport__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HelpBlock>) GWT.create(ExtensionProvided_factory__o_g_c_u_HelpBlock__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<StunnerPreferencesBeanGeneratedImpl>) GWT.create(Type_factory__o_k_w_c_s_c_p_StunnerPreferencesBeanGeneratedImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CreateNodeToolboxAction>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_t_a_CreateNodeToolboxAction__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_c_t_a_FlowActionsToolbox.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LiveSearchSelectorDropDownItemViewImpl>) GWT.create(Type_factory__o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItemViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HasRowsFieldInitializer>) GWT.create(Type_factory__o_k_w_c_f_a_e_s_f_p_f_f_HasRowsFieldInitializer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<StunnerDiagramEditorPreferencesBeanGeneratedImpl>) GWT.create(Type_factory__o_k_w_c_s_c_p_StunnerDiagramEditorPreferencesBeanGeneratedImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DecimalListBoxFieldRenderer>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_s_l_DecimalListBoxFieldRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<HitPolicyPopoverViewImpl>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_t_d_h_HitPolicyPopoverViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<GAVEditor>) GWT.create(Type_factory__o_g_c_s_p_c_GAVEditor__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNCanvasHandler>) GWT.create(Type_factory__o_k_w_c_d_c_s_DMNCanvasHandler__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<JQuery>) GWT.create(Producer_factory__o_u_c_v_p_w_JQueryElementalProducer_JQuery__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<AppLauncherMenuBuilder>) GWT.create(Type_factory__o_k_w_c_w_c_m_AppLauncherMenuBuilder__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<JSITHitPolicy>) GWT.create(JsType_factory__o_k_w_c_d_w_k_m_j_m_d_JSITHitPolicy__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNDocumentationServiceImpl>) GWT.create(Type_factory__o_k_w_c_d_c_e_d_c_DMNDocumentationServiceImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MapIndexBuilder>) GWT.create(Type_factory__o_k_w_c_s_c_g_p_i_m_MapIndexBuilder__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<NodeContainmentEvaluationHandler>) GWT.create(Type_factory__o_k_w_c_s_c_r_h_i_NodeContainmentEvaluationHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<WorkbenchViewModeSwitcherView>) GWT.create(Type_factory__o_u_c_v_p_m_WorkbenchViewModeSwitcherView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<PerspectiveTreeProvider>) GWT.create(Type_factory__o_u_c_a_PerspectiveTreeProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<TimeZoneProvider>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimeZoneProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<PartContextMenusPresenter>) GWT.create(Type_factory__o_u_c_w_w_m_PartContextMenusPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNShapeFactory>) GWT.create(Type_factory__o_k_w_c_d_c_s_f_DMNShapeFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefinitionPaletteItemWidget>) GWT.create(Type_factory__o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidget__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefinitionPaletteGroupWidgetViewImpl>) GWT.create(Type_factory__o_k_w_c_s_c_w_p_c_g_DefinitionPaletteGroupWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<UberfireBreadcrumbsView>) GWT.create(Type_factory__o_u_e_w_c_c_b_UberfireBreadcrumbsView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultModelValidator>) GWT.create(Type_factory__o_k_w_c_f_p_e_h_i_DefaultModelValidator__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DayTimeSelectorView>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeSelectorView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<BootstrapLayoutGenerator>) GWT.create(Type_factory__o_u_e_l_e_c_g_BootstrapLayoutGenerator__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ImageElementRendererViewImpl>) GWT.create(Type_factory__o_k_w_c_s_c_w_v_ImageElementRendererViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<NewResourceViewImpl>) GWT.create(Type_factory__o_k_w_c_w_c_h_NewResourceViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<AppLauncherItemView>) GWT.create(Type_factory__o_k_w_c_w_c_p_l_AppLauncherItemView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FormGroup>) GWT.create(Type_factory__o_u_c_v_p_w_FormGroup__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PreferencesRootScreen>) GWT.create(Type_factory__o_u_e_p_c_c_s_PreferencesRootScreen__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultDefinitionsCacheRegistry>) GWT.create(Type_factory__o_k_w_c_s_c_r_i_DefaultDefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TreeItem>) GWT.create(Type_factory__o_k_w_c_d_c_d_n_t_DecisionNavigatorTreeView_TreeItem__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TreeHierarchyStructureView>) GWT.create(Type_factory__o_u_e_p_c_c_t_TreeHierarchyStructureView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MultiTabWorkbenchPanelView>) GWT.create(Type_factory__o_u_c_v_p_t_MultiTabWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SubFormFieldProvider>) GWT.create(Type_factory__o_k_w_c_f_f_s_f_r_s_p_SubFormFieldProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNDefinitionSetRuleAdapterImpl>) GWT.create(Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetRuleAdapterImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ExpressionGridCacheImpl>) GWT.create(Type_factory__o_k_w_c_d_c_w_g_ExpressionGridCacheImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNDocumentationFactory>) GWT.create(Type_factory__o_k_w_c_d_w_k_c_c_e_d_c_DMNDocumentationFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNConnectorShapeViewFactory>) GWT.create(Type_factory__o_k_w_c_d_c_s_f_DMNConnectorShapeViewFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ExperimentalFeatureEditor>) GWT.create(Type_factory__o_u_e_c_e_g_f_ExperimentalFeatureEditor__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<RuleManagerImpl>) GWT.create(Type_factory__o_k_w_c_s_c_r_RuleManagerImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DateTimeSelector>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DateTimeSelector__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<InitBallotProvider>) GWT.create(Type_factory__o_j_e_i_c_a_b_InitBallotProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ClipboardControlCleaner>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_c_c_ClipboardControlCleaner__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ClientFormGenerationManager>) GWT.create(Type_factory__o_k_w_c_s_f_c_g_ClientFormGenerationManager__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNMarshaller>) GWT.create(Type_factory__o_k_w_c_d_c_m_m_DMNMarshaller__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TreeListSubItemView>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_i_t_TreeListSubItemView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<InvocationEditorDefinition>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_t_i_InvocationEditorDefinition__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeListShortcutsView>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_s_DataTypeListShortcutsView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LocaleListBox>) GWT.create(Type_factory__o_j_e_u_c_w_LocaleListBox__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ImmutableWorkbenchPanelPresenter>) GWT.create(Type_factory__o_u_c_w_p_i_ImmutableWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ScreenErrorViewImpl>) GWT.create(Type_factory__o_k_w_c_s_c_w_v_s_ScreenErrorViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<NameAndUrlPopoverViewImpl>) GWT.create(Type_factory__o_k_w_c_d_c_e_d_l_NameAndUrlPopoverViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FlowActionsToolboxFactory>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_t_a_FlowActionsToolboxFactory__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_FlowActionsToolbox.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LiveSearchDropDownView>) GWT.create(Type_factory__o_u_e_w_c_c_d_LiveSearchDropDownView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<UtilityMenuBarPresenter>) GWT.create(Type_factory__o_u_c_w_w_m_UtilityMenuBarPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TextField>) GWT.create(Type_factory__o_u_e_p_e_c_f_TextField__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PlaceRequest>) GWT.create(JsType_factory__o_u_m_PlaceRequest__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DockingAcceptorControlImpl>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_c_DockingAcceptorControlImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNDiagramsSession>) GWT.create(Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSession__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LongestPathVertexLayerer>) GWT.create(Type_factory__o_k_w_c_s_c_g_p_l_s_s_LongestPathVertexLayerer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNEditDRDToolboxAction>) GWT.create(Type_factory__o_k_w_c_d_c_c_c_t_DMNEditDRDToolboxAction__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ExportToPngToolbarCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_t_c_ExportToPngToolbarCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LogSelectedEdgeDevCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_m_d_i_LogSelectedEdgeDevCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ResourceContentServiceProducer>) GWT.create(Type_factory__o_a_k_b_c_r_p_ResourceContentServiceProducer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SelectorFieldInitilizer>) GWT.create(Type_factory__o_k_w_c_f_a_e_s_f_p_f_f_s_SelectorFieldInitilizer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DeletePopUpPresenter>) GWT.create(Type_factory__o_u_e_e_c_c_f_p_DeletePopUpPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNGridPanelContainer>) GWT.create(Type_factory__o_k_w_c_d_c_w_p_DMNGridPanelContainer__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ImportFactory>) GWT.create(Type_factory__o_k_w_c_d_c_e_i_i_ImportFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeConstraint>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraint__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<WorkspaceServiceProducer>) GWT.create(Type_factory__o_a_k_b_c_w_p_WorkspaceServiceProducer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<LiteralExpressionEditorDefinition>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_t_l_LiteralExpressionEditorDefinition__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MultiListWorkbenchPanelView>) GWT.create(Type_factory__o_u_c_w_p_i_MultiListWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ClearCommandHistoryDevCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_m_d_i_ClearCommandHistoryDevCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeConfirmation>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConfirmation__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ColumnWithComponentsView>) GWT.create(Type_factory__o_u_e_l_e_c_c_c_ColumnWithComponentsView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<EdgeCardinalityEvaluationHandler>) GWT.create(Type_factory__o_k_w_c_s_c_r_h_i_EdgeCardinalityEvaluationHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DataTypeSearchBarView>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchBarView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TreeList>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_i_t_TreeList__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LockDemandDetector>) GWT.create(Type_factory__o_u_c_m_LockDemandDetector__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ColorField>) GWT.create(Type_factory__o_u_e_p_e_c_f_ColorField__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<CommonsEntryPoint>) GWT.create(Type_factory__o_u_e_w_c_c_CommonsEntryPoint__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DecimalBoxFieldProvider>) GWT.create(Type_factory__o_k_w_c_f_f_s_f_b_d_p_DecimalBoxFieldProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CanvasLayoutUtils>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_u_CanvasLayoutUtils__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LienzoImageStripLoader>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_LienzoImageStripLoader__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultEditorSession>) GWT.create(Type_factory__o_k_w_c_s_c_c_s_i_DefaultEditorSession__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ZoomLevelSelectorPresenter>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_m_ZoomLevelSelectorPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SimpleWorkbenchPanelView>) GWT.create(Type_factory__o_u_c_w_p_i_SimpleWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ExpressionEditorDefinitions>) GWT.create(Producer_factory__o_k_w_c_d_c_e_e_t_ExpressionEditorDefinitions__quals__j_e_i_Any_j_e_i_Default_1.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ZoomLevelSelectorView>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_m_ZoomLevelSelectorView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HTMLElement>) GWT.create(ContextualProvider_factory__e_d_HTMLElement__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DecisionNavigatorBaseItemFactory>) GWT.create(Type_factory__o_k_w_c_d_c_d_n_f_DecisionNavigatorBaseItemFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<PreferenceScopeFactoryImpl>) GWT.create(Type_factory__o_u_p_s_i_PreferenceScopeFactoryImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeConstraintRangeView>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_r_DataTypeConstraintRangeView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNDiagramElementsUtils>) GWT.create(Type_factory__o_k_w_c_d_c_m_c_DMNDiagramElementsUtils__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MenuItem>) GWT.create(JsType_factory__o_u_w_m_m_MenuItem__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ReferencedDataTypeWarningMessage>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_ReferencedDataTypeWarningMessage__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Instance>) GWT.create(ContextualProvider_factory__j_e_i_Instance__quals__Universal.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DynamicValidator>) GWT.create(DynamicValidatorFactory.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<NameAndUriPopoverImpl>) GWT.create(Type_factory__o_k_w_c_d_c_e_d_l_NameAndUriPopoverImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HeaderPanel>) GWT.create(ExtensionProvided_factory__c_g_g_u_c_u_HeaderPanel__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNFlowActionsToolboxFactory>) GWT.create(Type_factory__o_k_w_c_d_c_c_c_t_DMNFlowActionsToolboxFactory__quals__j_e_i_Any_o_k_w_c_d_c_c_c_t_DMNFlowActionsToolbox.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<EventBus>) GWT.create(Producer_factory__c_g_w_b_e_s_EventBus__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<POMEditorPanel>) GWT.create(Type_factory__o_g_c_s_p_c_POMEditorPanel__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MultiPageEditorViewImpl>) GWT.create(Type_factory__o_u_c_v_p_m_MultiPageEditorViewImpl__quals__j_e_i_Any_o_u_c_w_w_m_Multiple.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SwitchLogLevelDevCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_m_d_i_SwitchLogLevelDevCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultFormGroup>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_f_i_d_DefaultFormGroup__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DeleteSelectionToolbarCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_t_c_DeleteSelectionToolbarCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MultipleSelectorFieldRenderer>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_l_s_MultipleSelectorFieldRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultVertexPositioning>) GWT.create(Type_factory__o_k_w_c_s_c_g_p_l_s_s_DefaultVertexPositioning__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<WorkbenchMegaMenuPresenter>) GWT.create(Producer_factory__o_u_c_w_w_m_m_WorkbenchMegaMenuPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DiagramLoader>) GWT.create(Type_factory__o_k_w_c_s_c_w_p_d_i_DiagramLoader__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeHasFieldsWarningMessage>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeHasFieldsWarningMessage__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<BindableTypeAheadViewImpl>) GWT.create(Type_factory__o_k_w_c_f_c_r_c_w_t_BindableTypeAheadViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<CallerProvider>) GWT.create(Type_factory__o_j_e_i_c_a_b_CallerProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<PermissionTreeI18NImpl>) GWT.create(Type_factory__o_u_c_r_i_PermissionTreeI18NImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNDefinitionSet>) GWT.create(Type_factory__o_k_w_c_d_a_DMNDefinitionSet__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MultiPageEditorContainerViewImpl>) GWT.create(Type_factory__o_k_w_c_k_c_e_MultiPageEditorContainerViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SubFormFieldInitializer>) GWT.create(Type_factory__o_k_w_c_f_a_e_s_f_p_f_f_n_SubFormFieldInitializer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNMarshallerImportsService>) GWT.create(Type_factory__o_k_w_c_d_c_m_i_DMNMarshallerImportsService__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DropdownMenu>) GWT.create(Type_factory__o_u_c_v_p_w_DropdownMenu__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MultipleSubFormFieldInitializer>) GWT.create(Type_factory__o_k_w_c_f_a_e_s_f_p_f_f_n_MultipleSubFormFieldInitializer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Span>) GWT.create(ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HTMLInputElement>) GWT.create(ExtensionProvided_factory__e_d_HTMLInputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<KogitoDMNCanvasShortcutsControlImpl>) GWT.create(Type_factory__o_k_w_c_d_w_k_c_c_s_KogitoDMNCanvasShortcutsControlImpl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ObservablePathImpl>) GWT.create(Type_factory__o_u_b_v_i_ObservablePathImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<YearsMonthsSelectorView>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_y_m_YearsMonthsSelectorView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNClientServicesProxyImpl>) GWT.create(Type_factory__o_k_w_c_d_w_k_c_c_s_DMNClientServicesProxyImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<GraphConnectionEvaluationHandler>) GWT.create(Type_factory__o_k_w_c_s_c_r_h_i_GraphConnectionEvaluationHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultToolboxControl>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_c_t_DefaultToolboxControl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNDiagramFactoryImpl>) GWT.create(Type_factory__o_k_w_c_d_a_f_DMNDiagramFactoryImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DecisionComponentsItemView>) GWT.create(Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponentsItemView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SearchBarComponentView>) GWT.create(Type_factory__o_k_w_c_w_c_s_c_SearchBarComponentView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<UberTabPanel>) GWT.create(Type_factory__o_u_c_v_p_t_UberTabPanel__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CheckBoxFormGroup>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_f_i_c_CheckBoxFormGroup__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HTMLButtonElement>) GWT.create(ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ManagedToolbar>) GWT.create(Type_factory__o_k_w_c_s_c_w_t_i_ManagedToolbar__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNPictureProvider>) GWT.create(Type_factory__o_k_w_c_d_c_s_DMNPictureProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<FunctionSupplementaryGridEditorDefinitionsProducer>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_t_f_s_FunctionSupplementaryGridEditorDefinitionsProducer__quals__j_e_i_Any_o_k_w_c_d_c_e_e_t_f_FunctionGridSupplementaryEditor.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<InlineTextEditorBoxViewImpl>) GWT.create(Type_factory__o_k_w_c_s_c_w_i_InlineTextEditorBoxViewImpl__quals__j_e_i_Any_o_k_w_c_s_c_c_c_c_i_InlineTextEditorBox.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LayoutComponentPaletteView>) GWT.create(Type_factory__o_u_e_l_e_c_w_LayoutComponentPaletteView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PreviewDiagramScreen>) GWT.create(Type_factory__o_k_w_c_d_c_d_p_PreviewDiagramScreen__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeUtils>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_c_DataTypeUtils__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CardsGridComponentView>) GWT.create(Type_factory__o_k_w_c_w_c_c_CardsGridComponentView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DecimalBoxFieldRenderer>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_DecimalBoxFieldRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ListComponentProvider>) GWT.create(Type_factory__o_j_e_d_c_ListComponentProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FormPropertiesWidget>) GWT.create(Type_factory__o_k_w_c_s_f_c_w_FormPropertiesWidget__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<AdminPagePerspectiveActivity>) GWT.create(Type_factory__o_u_e_p_c_a_AdminPagePerspectiveActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MenuItemPerspective>) GWT.create(JsType_factory__o_u_w_m_m_MenuItemPerspective__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SessionEditorImpl>) GWT.create(Type_factory__o_k_w_c_s_c_w_p_s_i_SessionEditorImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ParametersPopoverImpl>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_t_f_p_ParametersPopoverImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<UberfireBreadcrumbs>) GWT.create(Type_factory__o_u_e_w_c_c_b_UberfireBreadcrumbs__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TimeSelector>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_TimeSelector__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNAPIEntryPoint>) GWT.create(Type_factory__o_k_w_c_d_a_DMNAPIEntryPoint__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DefaultPermissionTypeRegistry>) GWT.create(Type_factory__o_u_s_i_a_DefaultPermissionTypeRegistry__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<JsTypeEventObserver>) GWT.create(JsType_factory__o_j_e_e_c_c_JsTypeEventObserver__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FlashMessagesView>) GWT.create(Type_factory__o_k_w_c_d_c_e_c_m_FlashMessagesView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<PMMLEditorMarshallerApi>) GWT.create(Producer_factory__o_a_k_b_c_p_m_PMMLEditorMarshallerApi__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeNameIsNotUniqueErrorMessage>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_c_e_DataTypeNameIsNotUniqueErrorMessage__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HtmlEditorPresenter>) GWT.create(Type_factory__o_u_e_e_c_c_h_HtmlEditorPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ListSelector>) GWT.create(Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelector__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CheckBoxFieldProvider>) GWT.create(Type_factory__o_k_w_c_f_f_s_f_b_c_p_CheckBoxFieldProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MultipleInputFieldRenderer>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_l_c_MultipleInputFieldRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DiagramFactoryImpl>) GWT.create(Type_factory__o_k_w_c_s_c_f_i_DiagramFactoryImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MultipleInputComponent>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_MultipleInputComponent__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<CrudComponentEntryPoint>) GWT.create(Type_factory__o_k_w_c_f_c_c_CrudComponentEntryPoint__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MultipleInputProvider>) GWT.create(Type_factory__o_k_w_c_f_f_s_f_b_l_i_MultipleInputProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CanvasCommandManagerImpl>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_CanvasCommandManagerImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultVertexOrdering>) GWT.create(Type_factory__o_k_w_c_s_c_g_p_l_s_s_DefaultVertexOrdering__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CommonDomainLookups>) GWT.create(Type_factory__o_k_w_c_s_c_l_d_CommonDomainLookups__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<UberfireDocksImpl>) GWT.create(Type_factory__o_u_c_d_UberfireDocksImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HtmlEditorActivity>) GWT.create(Type_factory__o_u_e_e_c_c_h_HtmlEditorActivity__quals__j_e_i_Any_j_i_Named_o_u_c_w_a_AssociatedResources_o_u_c_w_a_Priority.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SimpleDnDWorkbenchPanelPresenter>) GWT.create(Type_factory__o_u_c_w_p_i_SimpleDnDWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PreferenceStoreIOCProvider>) GWT.create(Type_factory__o_u_p_c_s_PreferenceStoreIOCProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MultipleInputViewImpl>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_MultipleInputViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ClientDefinitionManager>) GWT.create(Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CellEditorControls>) GWT.create(Type_factory__o_k_w_c_d_c_w_g_c_c_CellEditorControls__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ExpressionEditorControlImpl>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_ExpressionEditorControlImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LiveSearchDropDown>) GWT.create(Type_factory__o_u_e_w_c_c_d_LiveSearchDropDown__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<BoxedExpressionHelper>) GWT.create(Type_factory__o_k_w_c_d_c_c_BoxedExpressionHelper__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ClientFieldElementProcessor>) GWT.create(Type_factory__o_k_w_c_f_a_e_c_f_ClientFieldElementProcessor__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PMMLEditorMarshallerServiceProducer>) GWT.create(Type_factory__o_a_k_b_c_p_m_PMMLEditorMarshallerServiceProducer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TextFileExport>) GWT.create(Producer_factory__o_u_e_e_c_c_f_e_TextFileExport__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LayoutDragComponentGroupPresenter>) GWT.create(Type_factory__o_u_e_l_e_c_w_LayoutDragComponentGroupPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LongEditableColumnGenerator>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_n_i_LongEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ExportToSvgSessionCommand>) GWT.create(Type_factory__o_k_w_c_s_c_c_s_c_i_ExportToSvgSessionCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ValidatorProvider>) GWT.create(Type_factory__o_j_e_v_c_ValidatorProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MultiTabWorkbenchPanelPresenter>) GWT.create(Type_factory__o_u_c_w_p_i_MultiTabWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CustomPanelDefinition>) GWT.create(JsType_factory__o_u_w_m_CustomPanelDefinition__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LienzoTextTooltip>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_v_LienzoTextTooltip__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Path>) GWT.create(JsType_factory__o_u_b_v_Path__quals__Universal.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<AsyncPackageDataModelOracleFactory>) GWT.create(Type_factory__o_k_w_c_w_c_d_AsyncPackageDataModelOracleFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<Elemental2Producer>) GWT.create(Type_factory__o_u_c_v_p_w_Elemental2Producer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<EdgeFactoryImpl>) GWT.create(Type_factory__o_k_w_c_s_c_f_i_EdgeFactoryImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Disposer>) GWT.create(ContextualProvider_factory__o_j_e_i_c_a_Disposer__quals__Universal.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DecisionTableEditorDefinition>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_t_d_DecisionTableEditorDefinition__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ListComponent>) GWT.create(ContextualProvider_factory__o_j_e_d_c_c_ListComponent__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SplitLayoutPanelView>) GWT.create(Type_factory__o_u_c_w_p_i_SplitLayoutPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<BasicFileMenuBuilderImpl>) GWT.create(Type_factory__o_u_e_e_c_c_m_BasicFileMenuBuilderImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeConstraintModalView>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraintModalView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HTMLHeadingElement>) GWT.create(ExtensionProvided_factory__e_d_HTMLHeadingElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ActivityNotFoundView>) GWT.create(Type_factory__o_u_e_w_c_c_w_w_p_a_n_ActivityNotFoundView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<RedoCommandHandler>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_RedoCommandHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ExpandedPaletteDefinitionBuilder>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_p_ExpandedPaletteDefinitionBuilder__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FormPropertiesWidgetViewImpl>) GWT.create(Type_factory__o_k_w_c_s_f_c_w_FormPropertiesWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HasEnabledStateChangeListeners>) GWT.create(JsType_factory__o_u_w_m_m_HasEnabledStateChangeListeners__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNIncludedNodeFactory>) GWT.create(Type_factory__o_k_w_c_d_c_m_i_DMNIncludedNodeFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<org.kie.workbench.common.kogito.client.KogitoClientEntryPoint>) GWT.create(Type_factory__o_k_w_c_k_c_KogitoClientEntryPoint__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FullContentTraverseProcessorImpl>) GWT.create(Type_factory__o_k_w_c_s_c_g_p_t_c_FullContentTraverseProcessorImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<PlaceHistoryHandler>) GWT.create(Type_factory__o_u_c_m_PlaceHistoryHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<Form>) GWT.create(Type_factory__o_g_c_s_p_c_Form__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ShapeDefFunctionalFactory>) GWT.create(Type_factory__o_k_w_c_s_c_c_s_f_ShapeDefFunctionalFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<org.jboss.errai.common.client.dom.Select>) GWT.create(ExtensionProvided_factory__o_j_e_c_c_d_Select__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<GuidedTourGridObserver>) GWT.create(Type_factory__o_k_w_c_d_w_k_c_c_t_o_GuidedTourGridObserver__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ComponentColumn>) GWT.create(Type_factory__o_u_e_l_e_c_c_c_ComponentColumn__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DecisionNavigatorTreePresenter>) GWT.create(Type_factory__o_k_w_c_d_c_d_n_t_DecisionNavigatorTreePresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ConnectorGlyphLienzoRenderer>) GWT.create(Type_factory__o_k_w_c_s_s_c_ConnectorGlyphLienzoRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TreeListSubItem>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_i_t_TreeListSubItem__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DecisionNavigatorPresenterActivity>) GWT.create(Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorPresenterActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CollapsedPaletteDefinitionBuilder>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_p_CollapsedPaletteDefinitionBuilder__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Document>) GWT.create(Producer_factory__o_j_e_c_c_d_Document__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CanvasDiagramValidator>) GWT.create(Type_factory__o_k_w_c_s_c_c_v_c_CanvasDiagramValidator__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PerformAutomaticLayoutToolbarCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_t_c_PerformAutomaticLayoutToolbarCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ExperimentalActivitiesAuthorizationManagerImpl>) GWT.create(Type_factory__o_u_e_c_s_a_ExperimentalActivitiesAuthorizationManagerImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<StringEditableColumnGenerator>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_StringEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PictureProvidersManager>) GWT.create(Type_factory__o_k_w_c_s_s_c_f_PictureProvidersManager__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DragHelperComponentColumn>) GWT.create(Type_factory__o_u_e_l_e_c_i_DragHelperComponentColumn__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<AlertPopupViewImpl>) GWT.create(Type_factory__o_k_w_c_w_c_p_a_AlertPopupViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ItemDefinitionDestroyHandler>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_p_h_ItemDefinitionDestroyHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Anchor>) GWT.create(ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PropertyEditorWidget>) GWT.create(Type_factory__o_u_e_p_e_c_PropertyEditorWidget__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ItemDefinitionUtils>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_c_ItemDefinitionUtils__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TreeHierarchyLeafItemPresenter>) GWT.create(Type_factory__o_u_e_p_c_c_t_TreeHierarchyLeafItemPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TreeListItem>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_i_t_TreeListItem__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<UserMenuViewImpl>) GWT.create(Type_factory__o_u_c_v_p_m_UserMenuViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationFactory>) GWT.create(Type_factory__o_k_w_c_d_c_e_d_c_DMNDocumentationFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PlaceStatus>) GWT.create(JsType_factory__o_u_c_m_PlaceStatus__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FormValidatorImpl>) GWT.create(Type_factory__o_k_w_c_f_p_e_h_i_FormValidatorImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ClientTranslationService>) GWT.create(Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ClientBindableAdapterFunctions>) GWT.create(Type_factory__o_k_w_c_s_c_c_d_a_b_ClientBindableAdapterFunctions__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HTMLImageElement>) GWT.create(ExtensionProvided_factory__e_d_HTMLImageElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<LayoutEditorFocusController>) GWT.create(Type_factory__o_u_e_l_e_c_i_LayoutEditorFocusController__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Resource>) GWT.create(JsType_factory__o_u_s_Resource__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FieldPlaceHolderEntryProcessor>) GWT.create(Type_factory__o_k_w_c_f_s_s_m_p_i_p_FieldPlaceHolderEntryProcessor__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PreferencesCentralNavBarScreenActivity>) GWT.create(Type_factory__o_u_e_p_c_c_PreferencesCentralNavBarScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ConfigErrorDisplayerViewImpl>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_f_i_c_ConfigErrorDisplayerViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LayoutPanelPresenter>) GWT.create(Type_factory__o_u_c_w_p_i_LayoutPanelPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DockingEvaluationHandler>) GWT.create(Type_factory__o_k_w_c_s_c_r_h_i_DockingEvaluationHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Modal>) GWT.create(Type_factory__o_u_c_v_p_w_Modal__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DefaultWorkbenchController>) GWT.create(Type_factory__o_u_c_a_DefaultWorkbenchController__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Logger>) GWT.create(ExtensionProvided_factory__o_s_Logger__quals__Universal_2.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Logger>) GWT.create(ExtensionProvided_factory__o_s_Logger__quals__Universal_1.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ConnectorCardinalityEvaluationHandler>) GWT.create(Type_factory__o_k_w_c_s_c_r_h_i_ConnectorCardinalityEvaluationHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNSVGViewFactoryImpl>) GWT.create(Type_factory__o_k_w_c_d_c_r_DMNSVGViewFactoryImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MenuItemPlain>) GWT.create(JsType_factory__o_u_w_m_m_MenuItemPlain__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HasPlaceHolderFieldInitializer>) GWT.create(Type_factory__o_k_w_c_f_a_e_s_f_p_f_f_HasPlaceHolderFieldInitializer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FieldLabelEntryProcessor>) GWT.create(Type_factory__o_k_w_c_f_s_s_m_p_i_p_FieldLabelEntryProcessor__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FieldStateValidatorImpl>) GWT.create(Type_factory__o_k_w_c_f_p_e_h_i_FieldStateValidatorImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SessionViewerPresenter>) GWT.create(Type_factory__o_k_w_c_s_c_w_p_s_i_SessionViewerPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MultipleSelectorInput>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_l_s_i_MultipleSelectorInput__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CrudComponent>) GWT.create(Type_factory__o_k_w_c_f_c_c_c_CrudComponent__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<AdminPageItemView>) GWT.create(Type_factory__o_u_e_p_c_a_i_AdminPageItemView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<RestorePopUpPresenter>) GWT.create(Type_factory__o_u_e_e_c_c_f_p_RestorePopUpPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DefaultCloneProcess>) GWT.create(Type_factory__o_k_w_c_s_c_d_c_DefaultCloneProcess__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PerformAutomaticLayoutCommand>) GWT.create(Type_factory__o_k_w_c_s_c_c_s_c_i_PerformAutomaticLayoutCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<VerticesTransposer>) GWT.create(Type_factory__o_k_w_c_s_c_g_p_l_s_s_VerticesTransposer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DocumentationLinksFieldProvider>) GWT.create(Type_factory__o_k_w_c_d_c_p_d_DocumentationLinksFieldProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNCardComponentContentView>) GWT.create(Type_factory__o_k_w_c_d_c_e_i_g_DMNCardComponentContentView__quals__j_e_i_Any_o_k_w_c_d_c_e_i_g_DMNCard.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNAssetsDropdown>) GWT.create(Type_factory__o_k_w_c_d_c_e_i_m_d_DMNAssetsDropdown__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SvgFileExport>) GWT.create(Producer_factory__o_u_e_e_c_c_f_e_s_SvgFileExport__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultViewerSession>) GWT.create(Type_factory__o_k_w_c_s_c_c_s_i_DefaultViewerSession__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Logger>) GWT.create(ExtensionProvided_factory__o_s_Logger__quals__Universal.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ClientExperimentalFeaturesRegistryServiceImpl>) GWT.create(Type_factory__o_u_e_c_s_i_ClientExperimentalFeaturesRegistryServiceImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefinitionPaletteCategoryWidgetViewImpl>) GWT.create(Type_factory__o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PlaceRequestHistoryMapperImpl>) GWT.create(Type_factory__o_u_c_m_PlaceRequestHistoryMapperImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PanelDefinitionImpl>) GWT.create(JsType_factory__o_u_w_m_i_PanelDefinitionImpl__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<org.uberfire.client.views.pfly.widgets.JQueryProducer.JQuery>) GWT.create(Producer_factory__o_u_c_v_p_w_JQueryProducer_JQuery__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ListWidgetProvider>) GWT.create(Type_factory__o_j_e_u_c_w_ListWidgetProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CanvasBoundsIndexerImpl>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_i_b_CanvasBoundsIndexerImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<IFrameScreenView>) GWT.create(Type_factory__o_u_e_w_c_c_s_i_IFrameScreenView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LayoutDragComponentWidget>) GWT.create(Type_factory__o_u_e_l_e_c_w_LayoutDragComponentWidget__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<IFrameScreenPresenterActivity>) GWT.create(Type_factory__o_u_e_w_c_c_s_i_IFrameScreenPresenterActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<NodeFactoryImpl>) GWT.create(Type_factory__o_k_w_c_s_c_f_i_NodeFactoryImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Bs3Modal>) GWT.create(Type_factory__o_u_c_v_p_m_Bs3Modal__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<EmptyStateView>) GWT.create(Type_factory__o_k_w_c_s_c_w_v_s_EmptyStateView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<RestoreUtil>) GWT.create(Type_factory__o_u_e_e_c_c_f_RestoreUtil__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DayTimeSelector>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeSelector__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DateValueFormatter>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_DateValueFormatter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TypedValueComponentSelector>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_TypedValueComponentSelector__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<GraphUtils>) GWT.create(Type_factory__o_k_w_c_s_c_g_u_GraphUtils__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ContainerView>) GWT.create(Type_factory__o_u_e_l_e_c_c_c_ContainerView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MorphActionsToolboxFactory>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_t_a_MorphActionsToolboxFactory__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_MorphActionsToolbox.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNKogitoCommonEntryPoint>) GWT.create(Type_factory__o_k_w_c_d_w_k_c_c_DMNKogitoCommonEntryPoint__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MorphActionsToolboxView>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_t_a_MorphActionsToolboxView__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_MorphActionsToolbox.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FlowActionsToolboxView>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_t_a_FlowActionsToolboxView__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_FlowActionsToolbox.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Label>) GWT.create(ExtensionProvided_factory__o_j_e_c_c_d_Label__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Validator>) GWT.create(Provider_factory__j_v_Validator__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<LayoutSelection>) GWT.create(Type_factory__o_u_c_w_LayoutSelection__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HTMLUListElement>) GWT.create(ExtensionProvided_factory__e_d_HTMLUListElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNDiagramEditor>) GWT.create(Type_factory__o_k_w_c_d_s_c_e_DMNDiagramEditor__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_a_DiagramEditor.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DRDContextMenuService>) GWT.create(Type_factory__o_k_w_c_d_c_e_d_DRDContextMenuService__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DiagramElementNameProviderImpl>) GWT.create(Type_factory__o_k_w_c_d_c_v_DiagramElementNameProviderImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FlowPanel>) GWT.create(ExtensionProvided_factory__o_g_c_u_g_FlowPanel__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DecisionNavigatorTreeView>) GWT.create(Type_factory__o_k_w_c_d_c_d_n_t_DecisionNavigatorTreeView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DockPlaceHolder>) GWT.create(Type_factory__o_k_w_c_w_c_d_DockPlaceHolder__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<GraphCommandManagerImpl>) GWT.create(Type_factory__o_k_w_c_s_c_g_c_GraphCommandManagerImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DiagramEditorPreviewAndExplorerDock>) GWT.create(Type_factory__o_k_w_c_s_k_c_d_DiagramEditorPreviewAndExplorerDock__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeSelect>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_DataTypeSelect__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DayTimeValueConverter>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeValueConverter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<CoreEntryPoint>) GWT.create(Type_factory__o_u_e_w_c_c_CoreEntryPoint__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CurrentBranchProducer>) GWT.create(Type_factory__o_u_e_e_c_c_m_c_CurrentBranchProducer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SecretTextField>) GWT.create(Type_factory__o_u_e_p_e_c_f_SecretTextField__quals__j_e_i_Any_j_e_i_Default.class));
  }

  private void registerFactories1(final Context org_jboss_errai_ioc_client_container_ApplicationScopedContext_context, final Context org_jboss_errai_ioc_client_container_DependentScopeContext_context) {
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeListShortcuts>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_s_DataTypeListShortcuts__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DocumentationLinksWidget>) GWT.create(Type_factory__o_k_w_c_d_c_e_d_DocumentationLinksWidget__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<VersionMenuDropDownButtonViewImpl>) GWT.create(Type_factory__o_u_e_e_c_c_h_VersionMenuDropDownButtonViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<VersionHistoryPresenterViewImpl>) GWT.create(Type_factory__o_u_e_e_c_c_h_VersionHistoryPresenterViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<InputAutocomplete>) GWT.create(Type_factory__o_u_c_v_p_w_InputAutocomplete__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DecisionNavigatorObserver>) GWT.create(Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorObserver__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<IOCBeanManagerProvider>) GWT.create(Type_factory__o_j_e_i_c_a_b_IOCBeanManagerProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ResizeFlowPanel>) GWT.create(ExtensionProvided_factory__o_u_c_w_w_l_ResizeFlowPanel__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<KebabMenu>) GWT.create(Type_factory__o_u_c_v_p_w_KebabMenu__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<StunnerCoreEntryPoint>) GWT.create(Type_factory__o_k_w_c_s_c_StunnerCoreEntryPoint__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PreferencesCentralActionsScreen>) GWT.create(Type_factory__o_u_e_p_c_c_a_PreferencesCentralActionsScreen__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ItemDefinitionCreateHandler>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_p_h_ItemDefinitionCreateHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LogBoundsDevCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_m_d_i_LogBoundsDevCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeListItemView>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListItemView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeSelectView>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_DataTypeSelectView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<MessageConsoleWhiteListImpl>) GWT.create(Type_factory__o_k_w_c_s_s_m_MessageConsoleWhiteListImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<GuidedTourCustomSelectorPositionProvider>) GWT.create(JsType_factory__o_a_k_b_c_g_t_GuidedTourCustomSelectorPositionProvider__quals__Universal.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ActivityLifecycleErrorHandler>) GWT.create(Type_factory__o_u_c_m_ActivityLifecycleErrorHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TextInput>) GWT.create(ExtensionProvided_factory__o_j_e_c_c_d_TextInput__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<GraphValidatorImpl>) GWT.create(Type_factory__o_k_w_c_s_c_v_i_GraphValidatorImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PictureFieldRenderer>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_p_PictureFieldRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<HtmlResourceType>) GWT.create(Type_factory__o_u_e_e_c_c_h_HtmlResourceType__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNDiagramEditorActivity>) GWT.create(Type_factory__o_k_w_c_d_s_c_e_DMNDiagramEditorActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named_o_k_w_c_s_c_c_a_DiagramEditor_o_u_c_m_IsClientEditor.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<KeyboardShortcutsServiceProducer>) GWT.create(Type_factory__o_a_k_b_c_k_KeyboardShortcutsServiceProducer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<NavigatorThumbnailItemView>) GWT.create(Type_factory__o_k_w_c_s_c_w_e_n_i_NavigatorThumbnailItemView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ComboField>) GWT.create(Type_factory__o_u_e_p_e_c_f_ComboField__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DefaultPermissionTreeFactory>) GWT.create(Type_factory__o_u_s_c_a_t_i_DefaultPermissionTreeFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<RuntimeResource>) GWT.create(JsType_factory__o_u_s_a_RuntimeResource__quals__Universal.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ActivityBeansCache>) GWT.create(Type_factory__o_u_c_m_ActivityBeansCache__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ChildMenuItemPresenter>) GWT.create(Type_factory__o_u_c_w_w_m_m_m_ChildMenuItemPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DecisionComponentsView>) GWT.create(Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponentsView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LogBoundingBoxDevCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_m_d_i_LogBoundingBoxDevCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeConstraintExpressionView>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_e_DataTypeConstraintExpressionView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNDiagramUtils>) GWT.create(Type_factory__o_k_w_c_d_a_g_DMNDiagramUtils__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DownloadMenuItemBuilder>) GWT.create(Type_factory__o_u_e_e_c_c_m_DownloadMenuItemBuilder__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FieldSetFormGroup>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_f_i_n_f_FieldSetFormGroup__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<StunnerCommonIconsStrip>) GWT.create(Type_factory__o_k_w_c_s_c_c_r_StunnerCommonIconsStrip__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<KieSelectOptionElement>) GWT.create(Type_factory__o_k_w_c_w_c_w_KieSelectOptionElement__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<HitPolicyPopoverImpl>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_t_d_h_HitPolicyPopoverImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DateSelectorView>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_DateSelectorView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FieldLabel>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_f_l_l_FieldLabel__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MultiPageEditorImpl>) GWT.create(Type_factory__o_u_c_v_p_m_MultiPageEditorImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<RenamePopUpPresenter>) GWT.create(Type_factory__o_u_e_e_c_c_f_p_RenamePopUpPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<SpaceScopedResolutionStrategySupplier>) GWT.create(Type_factory__o_g_c_s_p_c_p_SpaceScopedResolutionStrategySupplier__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LayoutDragComponentHelper>) GWT.create(Type_factory__o_u_e_l_e_c_i_LayoutDragComponentHelper__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<GraphCommandFactory>) GWT.create(Type_factory__o_k_w_c_s_c_g_c_i_GraphCommandFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<GroupMenuItemPresenter>) GWT.create(Type_factory__o_u_c_w_w_m_m_m_GroupMenuItemPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ExperimentalFeaturesEditorScreen>) GWT.create(Type_factory__o_u_e_c_e_ExperimentalFeaturesEditorScreen__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HTMLDownloadHelper>) GWT.create(Type_factory__o_k_w_c_d_c_e_d_c_HTMLDownloadHelper__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ZoomLevelSelector>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_m_ZoomLevelSelector__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PasteSelectionSessionCommand>) GWT.create(Type_factory__o_k_w_c_s_c_c_s_c_i_PasteSelectionSessionCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ByteEditableColumnGenerator>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_n_i_ByteEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LayoutEditorView>) GWT.create(Type_factory__o_u_e_l_e_c_LayoutEditorView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<KogitoResourceContentService>) GWT.create(Type_factory__o_k_w_c_k_w_b_c_w_KogitoResourceContentService__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<LiteralExpressionPMMLDocumentModelEditorDefinition>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_t_f_s_p_m_LiteralExpressionPMMLDocumentModelEditorDefinition__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Promises>) GWT.create(Type_factory__o_u_c_p_Promises__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ListBarWidgetImpl>) GWT.create(Type_factory__o_u_c_v_p_l_ListBarWidgetImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<WorkbenchViewModeSwitcherPresenter>) GWT.create(Type_factory__o_u_c_m_WorkbenchViewModeSwitcherPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeConstraintEnumeration>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_e_DataTypeConstraintEnumeration__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ClientProfileManager>) GWT.create(Type_factory__o_k_w_c_s_c_c_a_ClientProfileManager__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultCardComponentContentView>) GWT.create(Type_factory__o_k_w_c_d_c_e_i_g_DefaultCardComponentContentView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<POMResourceType>) GWT.create(Type_factory__o_g_c_s_p_c_t_POMResourceType__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SwitchGridToolbarCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_t_c_SwitchGridToolbarCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<GeneralCreateNodeAction>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_t_a_GeneralCreateNodeAction__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<HtmlEditorLibraryLoader>) GWT.create(Type_factory__o_u_e_e_c_c_h_HtmlEditorLibraryLoader__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Elemental2HTMLElementProvider>) GWT.create(Type_factory__o_j_e_u_c_l_p_Elemental2HTMLElementProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<org.gwtbootstrap3.client.ui.html.Span>) GWT.create(ExtensionProvided_factory__o_g_c_u_h_Span__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LienzoSelectionControl>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_c_LienzoSelectionControl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_c_c_s_SingleSelection.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<GenericSelector>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_g_GenericSelector__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DataTypeSearchEngine>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchEngine__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PreferencesCentralActionsScreenActivity>) GWT.create(Type_factory__o_u_e_p_c_c_a_PreferencesCentralActionsScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FilterPagedTable>) GWT.create(Type_factory__o_u_e_w_c_c_t_FilterPagedTable__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<UtilityMenuBarView>) GWT.create(Type_factory__o_u_c_v_p_m_UtilityMenuBarView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CopySelectionSessionCommand>) GWT.create(Type_factory__o_k_w_c_s_c_c_s_c_i_CopySelectionSessionCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<BootstrapLayoutGeneratorDriver>) GWT.create(Type_factory__o_u_e_l_e_c_g_BootstrapLayoutGeneratorDriver__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SubFormWidget>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_r_s_w_SubFormWidget__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNClientDiagramServiceImpl>) GWT.create(Type_factory__o_k_w_c_d_w_k_c_c_s_DMNClientDiagramServiceImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DateTimeSelectorView>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DateTimeSelectorView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ListWidget>) GWT.create(ContextualProvider_factory__o_j_e_u_c_w_ListWidget__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNEditorMenuItemsBuilder>) GWT.create(Type_factory__o_k_w_c_d_w_k_c_c_e_DMNEditorMenuItemsBuilder__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeConstraintEnumerationItem>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_e_i_DataTypeConstraintEnumerationItem__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DecisionComponentFilter>) GWT.create(Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponentFilter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LiveSearchFooterViewImpl>) GWT.create(Type_factory__o_u_e_w_c_c_d_f_LiveSearchFooterViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<InputElement>) GWT.create(ExtensionProvided_factory__c_g_g_d_c_InputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LayoutElementPropertiesPresenter>) GWT.create(Type_factory__o_u_e_l_e_c_w_LayoutElementPropertiesPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LiveSearchSelectorDropDownItem>) GWT.create(Type_factory__o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItem__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ListBoxFieldProvider>) GWT.create(Type_factory__o_k_w_c_f_f_s_f_b_s_l_p_ListBoxFieldProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ItemDefinitionHandler>) GWT.create(Type_factory__o_k_w_c_d_c_e_i_i_p_ItemDefinitionHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HasMaxLengthFieldInitializer>) GWT.create(Type_factory__o_k_w_c_f_a_e_s_f_p_f_f_HasMaxLengthFieldInitializer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FormDisplayerViewImpl>) GWT.create(Type_factory__o_k_w_c_s_f_c_w_c_d_FormDisplayerViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SplitLayoutPanelPresenter>) GWT.create(Type_factory__o_u_c_w_p_i_SplitLayoutPanelPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<YearsMonthsValueConverter>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_y_m_YearsMonthsValueConverter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<GroupContextMenuItemPresenter>) GWT.create(Type_factory__o_u_c_w_w_m_m_c_GroupContextMenuItemPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ConnectorProxy>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_p_ConnectorProxy__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultFileEditorView>) GWT.create(Type_factory__o_u_e_w_c_c_e_d_DefaultFileEditorView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeNameIsBlankErrorMessage>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_c_e_DataTypeNameIsBlankErrorMessage__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<LienzoCanvasCommandFactory>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_c_LienzoCanvasCommandFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DRGElementTextPropertyProviderImpl>) GWT.create(Type_factory__o_k_w_c_d_c_c_c_a_DRGElementTextPropertyProviderImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ControlPointControlImpl>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_c_ControlPointControlImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MenuCustom>) GWT.create(JsType_factory__o_u_w_m_m_MenuCustom__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNMarshallerImportsClientHelper>) GWT.create(Type_factory__o_k_w_c_d_c_m_i_DMNMarshallerImportsClientHelper__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultCardComponent>) GWT.create(Type_factory__o_k_w_c_d_c_e_i_g_DefaultCardComponent__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DateTimePickerPresenterViewImpl>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerPresenterViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<WindowEventObservers>) GWT.create(JsType_factory__o_j_e_e_c_c_WindowEventObservers__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<IntegerSliderFieldInitializer>) GWT.create(Type_factory__o_k_w_c_f_a_e_s_f_p_f_f_s_IntegerSliderFieldInitializer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<WorkbenchPickupDragController>) GWT.create(Type_factory__o_u_c_w_w_d_WorkbenchPickupDragController__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PMMLCardComponent>) GWT.create(Type_factory__o_k_w_c_d_c_e_i_g_PMMLCardComponent__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<TextResourceType>) GWT.create(Type_factory__o_u_e_w_c_c_e_t_TextResourceType__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ShapeGlyphDragHandler>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_g_ShapeGlyphDragHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SessionPreviewImpl>) GWT.create(Type_factory__o_k_w_c_s_c_w_p_s_i_SessionPreviewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<I18nApi>) GWT.create(Producer_factory__o_a_k_b_c_i_I18nApi__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefinitionPaletteCategoryWidget>) GWT.create(Type_factory__o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidget__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DropdownMenuItem>) GWT.create(Type_factory__o_u_c_v_p_w_DropdownMenuItem__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<StunnerPreferencesRegistries>) GWT.create(Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesRegistries__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DataTypeShortcuts>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_s_DataTypeShortcuts__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ExportToBpmnToolbarCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_t_c_ExportToBpmnToolbarCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<AcyclicDirectedGraphRule>) GWT.create(Type_factory__o_k_w_c_d_a_r_AcyclicDirectedGraphRule__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<GeneralPreferencesBeanGeneratedImpl>) GWT.create(Type_factory__o_g_c_s_p_p_GeneralPreferencesBeanGeneratedImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<StunnerPreferencesRegistryLoader>) GWT.create(Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesRegistryLoader__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ExportToPdfToolbarCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_t_c_ExportToPdfToolbarCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TreeHierarchyLeafItemView>) GWT.create(Type_factory__o_u_e_p_c_c_t_TreeHierarchyLeafItemView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<WorkbenchMegaMenuProducer>) GWT.create(Type_factory__o_u_c_w_w_m_m_WorkbenchMegaMenuProducer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<CategoriesManagerCache>) GWT.create(Type_factory__o_u_c_m_CategoriesManagerCache__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Div>) GWT.create(ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNViewerToolbar>) GWT.create(Type_factory__o_k_w_c_d_c_w_t_DMNViewerToolbar__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_d_a_q_DMNEditor.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DiagramEditorExplorerScreenView>) GWT.create(Type_factory__o_k_w_c_s_k_c_s_DiagramEditorExplorerScreenView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<StaticBindingHelper>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_r_m_b_StaticBindingHelper__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ResourceTypeManagerCache>) GWT.create(Type_factory__o_u_c_m_ResourceTypeManagerCache__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<org.kie.workbench.common.stunner.core.client.session.command.impl.SaveDiagramSessionCommand>) GWT.create(Type_factory__o_k_w_c_s_c_c_s_c_i_SaveDiagramSessionCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Collection>) GWT.create(JsType_factory__j_u_Collection__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ResourceTypeDefinition>) GWT.create(JsType_factory__o_u_w_t_ResourceTypeDefinition__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FileMenuBuilderImpl>) GWT.create(Type_factory__o_k_w_c_w_c_m_FileMenuBuilderImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DelegateShapeFactory>) GWT.create(Type_factory__o_k_w_c_s_c_c_s_f_DelegateShapeFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<Model>) GWT.create(Type_factory__o_g_c_s_p_c_Model__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNGraphUtils>) GWT.create(Type_factory__o_k_w_c_d_c_g_DMNGraphUtils__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<StringRadioGroupFieldRenderer>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_s_r_StringRadioGroupFieldRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<AdminPagePresenter>) GWT.create(Type_factory__o_u_e_p_c_a_AdminPagePresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNPageView>) GWT.create(Type_factory__o_k_w_c_d_c_e_c_p_DMNPageView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ProfileSelector>) GWT.create(Type_factory__o_k_w_c_s_c_w_p_ProfileSelector__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<User>) GWT.create(Producer_factory__o_j_e_s_s_a_i_User__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<PreferenceScopeTypesMock>) GWT.create(Type_factory__o_k_w_c_k_w_b_s_PreferenceScopeTypesMock__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DataBinderProvider>) GWT.create(Type_factory__o_j_e_d_c_DataBinderProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<AnimatedFloatingWidgetView>) GWT.create(Type_factory__o_k_w_c_s_c_w_v_AnimatedFloatingWidgetView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<WorkbenchPartPresenterDefault>) GWT.create(Type_factory__o_u_c_w_p_n_p_WorkbenchPartPresenterDefault__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<GenerateProcessFormsSessionCommand>) GWT.create(Type_factory__o_k_w_c_s_f_c_s_c_GenerateProcessFormsSessionCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SavePopUpView>) GWT.create(Type_factory__o_u_e_e_c_c_f_p_SavePopUpView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ManagedViewerToolbar>) GWT.create(Type_factory__o_k_w_c_s_c_w_t_i_ManagedViewerToolbar__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<IsKogito>) GWT.create(Type_factory__o_k_w_c_k_w_b_c_w_IsKogito__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PreviewDiagramScreenActivity>) GWT.create(Type_factory__o_k_w_c_d_c_d_p_PreviewDiagramScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ToolBarItem>) GWT.create(JsType_factory__o_u_w_m_t_ToolBarItem__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LienzoLayer>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_LienzoLayer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<WorkbenchMenuCompactNavBarView>) GWT.create(Type_factory__o_u_c_v_p_m_WorkbenchMenuCompactNavBarView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeConstraintRange>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_r_DataTypeConstraintRange__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TextArea>) GWT.create(ExtensionProvided_factory__o_g_c_u_TextArea__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HTMLLabelElement>) GWT.create(ExtensionProvided_factory__e_d_HTMLLabelElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PopupViewImpl>) GWT.create(Type_factory__o_u_c_v_p_p_PopupViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ConfirmPopup>) GWT.create(Type_factory__o_u_c_v_p_w_ConfirmPopup__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DeleteNodeToolboxAction>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_t_a_DeleteNodeToolboxAction__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<KindPopoverImpl>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_t_f_k_KindPopoverImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNPaletteDefinitionBuilder>) GWT.create(Type_factory__o_k_w_c_d_c_c_p_DMNPaletteDefinitionBuilder__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<JsTypeEventObserver>) GWT.create(Type_factory__o_j_e_e_c_c_JsTypeEventObserver__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SelectorImpl>) GWT.create(Type_factory__o_k_w_c_s_c_w_v_SelectorImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ColumnWithComponents>) GWT.create(Type_factory__o_u_e_l_e_c_c_c_ColumnWithComponents__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DisabledFeatureComponent>) GWT.create(Type_factory__o_u_e_c_d_c_DisabledFeatureComponent__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LockTarget>) GWT.create(JsType_factory__o_u_c_m_LockTarget__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ClearToolbarCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_t_c_ClearToolbarCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FormDisplayer>) GWT.create(Type_factory__o_k_w_c_s_f_c_w_c_d_FormDisplayer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<LayoutEditorCssHelper>) GWT.create(Type_factory__o_u_e_l_e_c_i_LayoutEditorCssHelper__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<NoItemsComponent>) GWT.create(Type_factory__o_u_e_w_c_c_d_n_NoItemsComponent__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PanelDefinition>) GWT.create(JsType_factory__o_u_w_m_PanelDefinition__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<KeyboardControlImpl>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_c_k_KeyboardControlImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CopyPopUpViewProducer>) GWT.create(Type_factory__o_u_e_e_c_c_f_CopyPopUpViewProducer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ConstraintTypeDataProvider>) GWT.create(Type_factory__o_k_w_c_d_a_p_d_d_ConstraintTypeDataProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ClientBindableMorphAdapter>) GWT.create(Type_factory__o_k_w_c_s_c_c_d_a_b_ClientBindableMorphAdapter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Heading>) GWT.create(ExtensionProvided_factory__o_j_e_c_c_d_Heading__quals__j_e_i_Any_j_e_i_Default_j_i_Named_1.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TreeExplorerView>) GWT.create(Type_factory__o_k_w_c_s_c_w_e_t_TreeExplorerView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<WorkbenchConfigurationMenu>) GWT.create(Type_factory__o_k_w_c_w_c_h_w_c_WorkbenchConfigurationMenu__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SliderFormGroup>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_f_i_s_SliderFormGroup__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<WindowEventObservers>) GWT.create(Type_factory__o_j_e_e_c_c_WindowEventObservers__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FloatEditableColumnGenerator>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_n_d_FloatEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<BusyIndicatorView>) GWT.create(Type_factory__o_u_e_w_c_c_c_BusyIndicatorView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SessionCardinalityStateHandler>) GWT.create(Type_factory__o_k_w_c_s_c_w_p_s_i_SessionCardinalityStateHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MenuItemDividerView>) GWT.create(Type_factory__o_u_e_w_c_c_m_MenuItemDividerView__quals__j_e_i_Any_o_u_e_w_c_c_m_MenuItemDivider.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ModalFormDisplayer>) GWT.create(Type_factory__o_k_w_c_f_c_c_c_f_m_ModalFormDisplayer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<EmbeddedFormDisplayer>) GWT.create(Type_factory__o_k_w_c_f_c_c_c_f_e_EmbeddedFormDisplayer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<StunnerPreferencesEntryPoint>) GWT.create(Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesEntryPoint__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<JsArray>) GWT.create(JsType_factory__o_j_e_i_c_JsArray__quals__Universal.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ExperimentalFeaturesPerspective>) GWT.create(Type_factory__o_u_e_c_p_ExperimentalFeaturesPerspective__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<AboutCommand>) GWT.create(Type_factory__o_k_w_c_w_c_m_AboutCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<EditableColumnGeneratorManagerImpl>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_EditableColumnGeneratorManagerImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<VersionMenuDropDownButton>) GWT.create(Type_factory__o_u_e_e_c_c_h_VersionMenuDropDownButton__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<GAVEditorViewImpl>) GWT.create(Type_factory__o_g_c_s_p_c_GAVEditorViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<NodeConnector>) GWT.create(Type_factory__o_k_w_c_d_c_m_u_n_NodeConnector__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeCreateHandler>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_p_h_DataTypeCreateHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<GuidedTourCustomSelectorPositionProvider>) GWT.create(Type_factory__o_a_k_b_c_g_t_GuidedTourCustomSelectorPositionProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TemplatedWorkbenchPanelPresenter>) GWT.create(Type_factory__o_u_c_w_p_i_TemplatedWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DynamicFormRenderer>) GWT.create(Type_factory__o_k_w_c_f_d_c_DynamicFormRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<SvgDataUriGenerator>) GWT.create(Type_factory__o_k_w_c_s_c_c_u_SvgDataUriGenerator__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SliderFieldRenderer>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_SliderFieldRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TextBoxFormPopupViewImpl>) GWT.create(Type_factory__o_k_w_c_w_c_p_t_TextBoxFormPopupViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<StateChangeImpl>) GWT.create(Type_factory__o_j_e_i_c_l_i_StateChangeImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNLayoutHelper>) GWT.create(Type_factory__o_k_w_c_d_c_w_t_DMNLayoutHelper__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<StunnerWidgetsEntryPoint>) GWT.create(Type_factory__o_k_w_c_s_c_w_StunnerWidgetsEntryPoint__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<PreferenceFormBeansInfo>) GWT.create(Type_factory__o_u_e_p_c_u_PreferenceFormBeansInfo__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<KebabWidget>) GWT.create(Type_factory__o_u_e_l_e_c_w_KebabWidget__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ErrorPopup>) GWT.create(Type_factory__o_u_c_v_p_w_ErrorPopup__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PMMLCardComponentContentView>) GWT.create(Type_factory__o_k_w_c_d_c_e_i_g_PMMLCardComponentContentView__quals__j_e_i_Any_o_k_w_c_d_c_e_i_g_PMMLCard.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<TranslationServiceProvider>) GWT.create(Type_factory__o_j_e_u_c_l_s_TranslationServiceProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ClientUsernameProvider>) GWT.create(Type_factory__o_u_p_c_s_ClientUsernameProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNEditorSession>) GWT.create(Type_factory__o_k_w_c_d_c_s_DMNEditorSession__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<AdaptiveWorkbenchPanelPresenter>) GWT.create(Type_factory__o_u_c_w_p_i_AdaptiveWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<KieSelectElementView>) GWT.create(Type_factory__o_k_w_c_w_c_w_KieSelectElementView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MenuDevCommandsBuilder>) GWT.create(Type_factory__o_k_w_c_s_c_w_m_d_MenuDevCommandsBuilder__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CheckBoxFormGroupViewImpl>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_f_i_c_CheckBoxFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SessionViewerImpl>) GWT.create(Type_factory__o_k_w_c_s_c_w_p_s_i_SessionViewerImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<CloneManagerImpl>) GWT.create(Type_factory__o_k_w_c_s_c_d_c_CloneManagerImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ClientResourceType>) GWT.create(JsType_factory__o_u_c_w_t_ClientResourceType__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LoadingBoxView>) GWT.create(Type_factory__o_k_w_c_s_c_w_v_LoadingBoxView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<KogitoKieAssetsDropdownView>) GWT.create(Type_factory__o_k_w_c_w_c_a_d_KogitoKieAssetsDropdownView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DefinitionUtils>) GWT.create(Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNIncludedModelHandler>) GWT.create(Type_factory__o_k_w_c_d_c_e_i_i_p_DMNIncludedModelHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ActivityResourceType>) GWT.create(JsType_factory__o_u_w_m_ActivityResourceType__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<UnanchoredWorkbenchPartPresenter>) GWT.create(Type_factory__o_u_c_w_p_u_p_UnanchoredWorkbenchPartPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SyncBeanManager>) GWT.create(Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Row>) GWT.create(Type_factory__o_u_e_l_e_c_c_r_Row__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PackageListBoxViewImpl>) GWT.create(Type_factory__o_k_w_c_w_c_h_PackageListBoxViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DefaultPreferencesRegistry>) GWT.create(Type_factory__o_k_w_c_s_c_c_p_DefaultPreferencesRegistry__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<WizardViewImpl>) GWT.create(Type_factory__o_u_e_w_c_c_w_WizardViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<StaticFocusedResizePanel>) GWT.create(ExtensionProvided_factory__o_u_c_w_w_p_StaticFocusedResizePanel__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<WorkbenchPartView>) GWT.create(Type_factory__o_u_c_w_p_n_p_WorkbenchPartView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ChildContextMenuItemPresenter>) GWT.create(Type_factory__o_u_c_w_w_m_m_c_ChildContextMenuItemPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<JavaFunctionEditorDefinition>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_t_f_s_j_JavaFunctionEditorDefinition__quals__j_e_i_Any_o_k_w_c_d_c_e_e_t_f_FunctionGridSupplementaryEditor.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<org.kie.workbench.common.dmn.client.editors.documentation.DMNDocumentationViewButtonsVisibilitySupplier>) GWT.create(Type_factory__o_k_w_c_d_c_e_d_DMNDocumentationViewButtonsVisibilitySupplier__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TranslationService>) GWT.create(Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<IconMenuItemView>) GWT.create(Type_factory__o_u_c_v_p_m_m_m_IconMenuItemView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<UberfireDocksContainer>) GWT.create(Type_factory__o_u_c_w_d_UberfireDocksContainer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<AuthoringPerspective>) GWT.create(Type_factory__o_k_w_c_k_w_b_c_p_AuthoringPerspective__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<EditorSessionCommands>) GWT.create(Type_factory__o_k_w_c_s_k_c_s_EditorSessionCommands__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ComponentColumnView>) GWT.create(Type_factory__o_u_e_l_e_c_c_c_ComponentColumnView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<PerspectiveConfiguration>) GWT.create(Type_factory__o_k_w_c_k_w_b_c_p_PerspectiveConfiguration__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNUnmarshaller>) GWT.create(Type_factory__o_k_w_c_d_c_m_u_DMNUnmarshaller__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<RelationEditorDefinition>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_t_r_RelationEditorDefinition__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<AboutPopupView>) GWT.create(Type_factory__o_k_w_c_w_c_p_a_AboutPopupView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ResourceContentService>) GWT.create(Producer_factory__o_a_k_b_c_r_ResourceContentService__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<IntegerRadioGroupFieldRenderer>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_s_r_IntegerRadioGroupFieldRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<CellEditorControlsViewImpl>) GWT.create(Type_factory__o_k_w_c_d_c_w_g_c_c_CellEditorControlsViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Caller>) GWT.create(ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ExportToPngSessionCommand>) GWT.create(Type_factory__o_k_w_c_s_c_c_s_c_i_ExportToPngSessionCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FormHandlerGeneratorManager>) GWT.create(Type_factory__o_k_w_c_f_d_c_i_FormHandlerGeneratorManager__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ResourceAction>) GWT.create(JsType_factory__o_u_s_ResourceAction__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<VFSServiceProxyClientImpl>) GWT.create(Type_factory__o_u_c_w_VFSServiceProxyClientImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<LayoutEditorEntryPoint>) GWT.create(Type_factory__o_u_e_l_e_c_LayoutEditorEntryPoint__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ResourceActionRef>) GWT.create(JsType_factory__o_u_s_a_ResourceActionRef__quals__Universal.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<Optimization>) GWT.create(Type_factory__o_g_c_s_p_c_Optimization__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DoubleSliderFieldInitializer>) GWT.create(Type_factory__o_k_w_c_f_a_e_s_f_p_f_f_s_DoubleSliderFieldInitializer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ColorPickerFieldProvider>) GWT.create(Type_factory__o_k_w_c_s_f_s_f_ColorPickerFieldProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MenuItemWithIconView>) GWT.create(Type_factory__o_u_e_w_c_c_m_MenuItemWithIconView__quals__j_e_i_Any_o_u_e_w_c_c_m_MenuItemWithIcon.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DataTypeList>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_DataTypeList__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DynamicFormRendererViewImpl>) GWT.create(Type_factory__o_k_w_c_f_d_c_DynamicFormRendererViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<AdminPageCategoryPresenter>) GWT.create(Type_factory__o_u_e_p_c_a_c_AdminPageCategoryPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<UberfireExperimentalEntryPoint>) GWT.create(Type_factory__o_u_e_c_UberfireExperimentalEntryPoint__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ComponentColumnPart>) GWT.create(Type_factory__o_u_e_l_e_c_c_c_ComponentColumnPart__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PreferenceScopeTypes>) GWT.create(Producer_factory__o_u_p_s_PreferenceScopeTypes__quals__j_e_i_Any_o_u_a_Customizable.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<PMMLDocumentMetadataProvider>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_t_f_s_p_PMMLDocumentMetadataProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LayoutDragComponentGroupView>) GWT.create(Type_factory__o_u_e_l_e_c_w_LayoutDragComponentGroupView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultDiagramViewer>) GWT.create(Type_factory__o_k_w_c_s_c_w_p_d_i_DefaultDiagramViewer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ScrollableLienzoPanel>) GWT.create(Type_factory__o_k_w_c_s_c_w_c_ScrollableLienzoPanel__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PanelDefinitionImpl>) GWT.create(Type_factory__o_u_w_m_i_PanelDefinitionImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ColorPickerFieldRenderer>) GWT.create(Type_factory__o_k_w_c_s_f_c_f_c_ColorPickerFieldRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FileExportsPreferencesBeanGeneratedImpl>) GWT.create(Type_factory__o_u_e_e_c_f_e_FileExportsPreferencesBeanGeneratedImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TextAreaFieldProvider>) GWT.create(Type_factory__o_k_w_c_f_f_s_f_b_t_p_TextAreaFieldProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<GuidedTourBridgeInitializer>) GWT.create(Type_factory__o_k_w_c_d_w_k_c_c_t_GuidedTourBridgeInitializer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SimpleCheckBox>) GWT.create(ExtensionProvided_factory__o_g_c_u_SimpleCheckBox__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ClientRuleManager>) GWT.create(Type_factory__o_k_w_c_s_c_c_r_ClientRuleManager__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PreferenceScopeResolutionStrategyMock>) GWT.create(Type_factory__o_k_w_c_k_w_b_s_PreferenceScopeResolutionStrategyMock__quals__j_e_i_Any_o_u_a_Customizable.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FieldRequiredViewImpl>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_f_l_r_FieldRequiredViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<GenerateSelectedFormsSessionCommand>) GWT.create(Type_factory__o_k_w_c_s_f_c_s_c_GenerateSelectedFormsSessionCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNEditorSessionCommands>) GWT.create(Type_factory__o_k_w_c_d_w_k_c_c_s_DMNEditorSessionCommands__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ToolboxDomainLookups>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_t_a_ToolboxDomainLookups__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ManagedEditorToolbar>) GWT.create(Type_factory__o_k_w_c_s_c_w_t_i_ManagedEditorToolbar__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TreeHierarchyInternalItemView>) GWT.create(Type_factory__o_u_e_p_c_c_t_TreeHierarchyInternalItemView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MultiScreenView>) GWT.create(Type_factory__o_u_c_v_p_m_MultiScreenView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<LienzoImageStrips>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_LienzoImageStrips__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ExperimentalFeatureEditorViewImpl>) GWT.create(Type_factory__o_u_e_c_e_g_f_ExperimentalFeatureEditorViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TreeListItemView>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_i_t_TreeListItemView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultEditorDock>) GWT.create(Type_factory__o_k_w_c_w_c_d_DefaultEditorDock__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<RuleExtensionMultiHandler>) GWT.create(Type_factory__o_k_w_c_s_c_r_e_RuleExtensionMultiHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<AdaptiveWorkbenchPanelView>) GWT.create(Type_factory__o_u_c_w_p_i_AdaptiveWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<WorkbenchDragAndDropManager>) GWT.create(Type_factory__o_u_c_w_w_d_WorkbenchDragAndDropManager__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<NodeTextSetter>) GWT.create(Type_factory__o_k_w_c_d_c_s_NodeTextSetter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<LayoutEditorPropertiesScreen>) GWT.create(Type_factory__o_u_e_l_e_c_LayoutEditorPropertiesScreen__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LogCommandHistoryDevCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_m_d_i_LogCommandHistoryDevCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<POMResourceTypeDefinition>) GWT.create(Type_factory__o_g_c_s_p_e_t_POMResourceTypeDefinition__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LockManagerImpl>) GWT.create(Type_factory__o_u_c_m_LockManagerImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<UndefinedExpressionSelectorPopoverImpl>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_t_u_s_UndefinedExpressionSelectorPopoverImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SelectComponent>) GWT.create(Type_factory__o_u_e_w_c_c_s_SelectComponent__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<IncludedModelsIndex>) GWT.create(Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsIndex__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<PerspectiveManagerImpl>) GWT.create(Type_factory__o_u_c_m_PerspectiveManagerImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<NoneCloneProcess>) GWT.create(Type_factory__o_k_w_c_s_c_d_c_NoneCloneProcess__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<IntegerBoxFieldProvider>) GWT.create(Type_factory__o_k_w_c_f_f_s_f_b_i_p_IntegerBoxFieldProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FormGeneratorDriver>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_FormGeneratorDriver__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LienzoCanvasMediators>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_m_LienzoCanvasMediators__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DecisionNavigatorItemsProvider>) GWT.create(Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorItemsProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNEditorToolbar>) GWT.create(Type_factory__o_k_w_c_d_c_w_t_DMNEditorToolbar__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_d_a_q_DMNEditor.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<KeyboardShortcutsApiOpts>) GWT.create(JsType_factory__o_a_c_k_KeyboardShortcutsApiOpts__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<StandaloneEditorPerspective>) GWT.create(Type_factory__o_u_c_w_StandaloneEditorPerspective__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CopyPopUpView>) GWT.create(Type_factory__o_u_e_e_c_c_f_p_CopyPopUpView__quals__j_e_i_Any_o_u_a_FallbackImplementation.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<InitBallot>) GWT.create(ContextualProvider_factory__o_j_e_i_c_a_InitBallot__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CollapsedDefinitionPaletteItemWidgetViewImpl>) GWT.create(Type_factory__o_k_w_c_s_c_w_p_c_CollapsedDefinitionPaletteItemWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ViewTraverseProcessorImpl>) GWT.create(Type_factory__o_k_w_c_s_c_g_p_t_c_ViewTraverseProcessorImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DiagramClientErrorHandler>) GWT.create(Type_factory__o_k_w_c_s_c_c_e_DiagramClientErrorHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DocumentationLinksFieldRenderer>) GWT.create(Type_factory__o_k_w_c_d_c_p_d_DocumentationLinksFieldRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ImmutableWorkbenchPanelView>) GWT.create(Type_factory__o_u_c_w_p_i_ImmutableWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Input>) GWT.create(ExtensionProvided_factory__o_j_e_c_c_d_Input__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DataTypeManagerStackStore>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_c_DataTypeManagerStackStore__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<WorkspaceService>) GWT.create(Producer_factory__o_a_k_b_c_w_WorkspaceService__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DateTimePickerPresenter>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Clipboard>) GWT.create(Type_factory__o_u_c_u_Clipboard__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNGridSubIndex>) GWT.create(Type_factory__o_k_w_c_d_c_e_s_DMNGridSubIndex__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ListSelectorViewImpl>) GWT.create(Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelectorViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TabPanelWithDropdowns>) GWT.create(Type_factory__o_u_c_v_p_t_TabPanelWithDropdowns__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<RowView>) GWT.create(Type_factory__o_u_e_l_e_c_c_r_RowView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ModuleFormGenerationResourcesProvider>) GWT.create(Type_factory__o_k_w_c_d_a_d_m_f_p_ModuleFormGenerationResourcesProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<CanvasFocusUtils>) GWT.create(Type_factory__o_k_w_c_d_c_d_n_c_CanvasFocusUtils__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PictureWidget>) GWT.create(Type_factory__o_k_w_c_f_c_r_c_w_p_w_PictureWidget__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<org.kie.workbench.common.widgets.client.kogito.IsKogito>) GWT.create(Type_factory__o_k_w_c_w_c_k_IsKogito__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CutSelectionSessionCommand>) GWT.create(Type_factory__o_k_w_c_s_c_c_s_c_i_CutSelectionSessionCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNDataTypesSubIndex>) GWT.create(Type_factory__o_k_w_c_d_c_e_s_DMNDataTypesSubIndex__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DeleteSelectionSessionCommand>) GWT.create(Type_factory__o_k_w_c_s_c_c_s_c_i_DeleteSelectionSessionCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<AlertView>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_v_AlertView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<LanguageConfigurationHandler>) GWT.create(Type_factory__o_k_w_c_w_c_h_w_c_LanguageConfigurationHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<AdminPageImpl>) GWT.create(Type_factory__o_u_e_p_c_a_p_AdminPageImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<PopupUtil>) GWT.create(Type_factory__o_k_w_c_s_c_w_p_PopupUtil__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ListSelectorDividerItemViewImpl>) GWT.create(Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelectorDividerItemViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FieldRendererManagerImpl>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_FieldRendererManagerImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SwitchGridSessionCommand>) GWT.create(Type_factory__o_k_w_c_s_c_c_s_c_i_SwitchGridSessionCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<AdminPagePerspective>) GWT.create(Type_factory__o_u_e_p_c_a_AdminPagePerspective__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DOMGlyphRenderers>) GWT.create(Type_factory__o_k_w_c_s_c_w_c_g_DOMGlyphRenderers__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Container>) GWT.create(Type_factory__o_u_e_l_e_c_c_c_Container__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DecisionNavigatorItemFactory>) GWT.create(Type_factory__o_k_w_c_d_c_d_n_f_DecisionNavigatorItemFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<IconMenuItemPresenter>) GWT.create(Type_factory__o_u_c_w_w_m_m_m_IconMenuItemPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LogDefinitionDevCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_m_d_i_LogDefinitionDevCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<EmptyDropRowView>) GWT.create(Type_factory__o_u_e_l_e_c_c_r_EmptyDropRowView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CharacterListBoxFieldRenderer>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_s_l_CharacterListBoxFieldRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SliderFieldProvider>) GWT.create(Type_factory__o_k_w_c_f_f_s_f_b_s_p_SliderFieldProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DocksEntryPoint>) GWT.create(Type_factory__o_u_c_DocksEntryPoint__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeNameFormatValidator>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_v_DataTypeNameFormatValidator__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ExperimentalFeaturesEditorScreenActivity>) GWT.create(Type_factory__o_u_e_c_e_ExperimentalFeaturesEditorScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FieldRequired>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_f_l_r_FieldRequired__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ImageFileExport>) GWT.create(Producer_factory__o_u_e_e_c_c_f_e_ImageFileExport__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ClientFieldManagerImpl>) GWT.create(Type_factory__o_k_w_c_f_d_c_s_ClientFieldManagerImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ShapeSetsMenuItemsBuilder>) GWT.create(Type_factory__o_k_w_c_s_c_w_m_d_ShapeSetsMenuItemsBuilder__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Set>) GWT.create(JsType_factory__j_u_Set__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeConstraintEnumerationItemView>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_e_i_DataTypeConstraintEnumerationItemView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HTMLDocument>) GWT.create(Producer_factory__e_d_HTMLDocument__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNEditDecisionToolboxAction>) GWT.create(Type_factory__o_k_w_c_d_c_c_c_t_DMNEditDecisionToolboxAction__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<KogitoRedoSessionCommand>) GWT.create(Type_factory__o_k_w_c_s_k_r_c_s_c_i_KogitoRedoSessionCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNDefinitionSetResourceType>) GWT.create(Type_factory__o_k_w_c_d_a_r_DMNDefinitionSetResourceType__quals__j_e_i_Any_j_e_i_Default_o_u_w_a_VisibleAsset.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ScreenEventPublisher>) GWT.create(Type_factory__o_k_w_c_s_c_c_e_s_ScreenEventPublisher__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<IncludedModelsPageStateProviderImpl>) GWT.create(Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsPageStateProviderImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ConfigErrorDisplayer>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_f_i_c_ConfigErrorDisplayer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<WizardPageTitle>) GWT.create(Type_factory__o_u_e_w_c_c_w_WizardPageTitle__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SaveButton>) GWT.create(Type_factory__o_u_e_e_c_c_h_SaveButton__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<IncludedModelsPagePresenter>) GWT.create(Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPagePresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FormListPopupViewImpl>) GWT.create(Type_factory__o_k_w_c_w_c_p_l_FormListPopupViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ModuleFieldRendererTypesProvider>) GWT.create(Type_factory__o_k_w_c_s_f_c_f_c_ModuleFieldRendererTypesProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LienzoCanvasNotificationView>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_v_LienzoCanvasNotificationView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DisabledFeatureActivity>) GWT.create(Type_factory__o_u_e_c_d_s_DisabledFeatureActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ClientDiagramServiceImpl>) GWT.create(Type_factory__o_k_w_c_s_c_c_s_ClientDiagramServiceImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<FunctionEditorDefinition>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_t_f_FunctionEditorDefinition__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LocationControlImpl>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_c_LocationControlImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FormListPopup>) GWT.create(Type_factory__o_k_w_c_w_c_p_l_FormListPopup__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CommonActionsToolboxView>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_t_a_CommonActionsToolboxView__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_CommonActionsToolbox.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<StringColumnGenerator>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_r_m_c_i_StringColumnGenerator__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<StunnerWiresHandlerFactory>) GWT.create(Type_factory__o_k_w_c_s_c_l_w_StunnerWiresHandlerFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ExportToRawFormatSessionCommand>) GWT.create(Type_factory__o_k_w_c_s_c_c_s_c_i_ExportToRawFormatSessionCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ActivityNotFoundPresenter>) GWT.create(Type_factory__o_u_e_w_c_c_w_w_p_a_n_ActivityNotFoundPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<AccessImpl>) GWT.create(Type_factory__o_j_e_i_c_l_i_AccessImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SessionPresenterView>) GWT.create(Type_factory__o_k_w_c_s_c_w_p_s_i_SessionPresenterView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ToolbarItem>) GWT.create(Type_factory__o_k_w_c_s_c_w_t_i_ToolbarItem__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<WorkbenchToolBarView>) GWT.create(Type_factory__o_u_c_v_p_t_WorkbenchToolBarView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeConstraintView>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraintView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HTMLOptionElement>) GWT.create(ExtensionProvided_factory__e_d_HTMLOptionElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TextBoxFieldRenderer>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_TextBoxFieldRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<AsyncPackageDataModelOracleImpl>) GWT.create(Type_factory__o_k_w_c_w_c_d_AsyncPackageDataModelOracleImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PagedTable>) GWT.create(Type_factory__o_u_e_w_c_c_t_PagedTable__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ExportToPdfSessionCommand>) GWT.create(Type_factory__o_k_w_c_s_c_c_s_c_i_ExportToPdfSessionCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ImportRecordEngine>) GWT.create(Type_factory__o_k_w_c_d_c_e_i_i_p_ImportRecordEngine__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ValidationPopup>) GWT.create(Type_factory__o_k_w_c_w_c_p_v_ValidationPopup__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FieldRequiredEntryProcessor>) GWT.create(Type_factory__o_k_w_c_f_s_s_m_p_i_p_FieldRequiredEntryProcessor__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CrudComponentViewImpl>) GWT.create(Type_factory__o_k_w_c_f_c_c_c_CrudComponentViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<org.uberfire.client.views.pfly.notfound.ActivityNotFoundView>) GWT.create(Type_factory__o_u_c_v_p_n_ActivityNotFoundView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<BindableTypeAhead>) GWT.create(Type_factory__o_k_w_c_f_c_r_c_w_t_BindableTypeAhead__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SliderFormGroupViewImpl>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_f_i_s_SliderFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CollapsibleFormGroup>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroup__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DataTypeListView>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<KebabMenuItem>) GWT.create(Type_factory__o_u_c_v_p_w_KebabMenuItem__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Map>) GWT.create(JsType_factory__j_u_Map__quals__Universal.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<LoadingBox>) GWT.create(Type_factory__o_k_w_c_s_c_w_v_LoadingBox__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ToggleCommentView>) GWT.create(Type_factory__o_u_e_e_c_c_f_p_c_ToggleCommentView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FieldHelpViewImpl>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_f_l_h_FieldHelpViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SplashModalFooter>) GWT.create(Type_factory__o_u_c_v_p_s_SplashModalFooter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultFileEditorPresenterActivity>) GWT.create(Type_factory__o_u_e_w_c_c_e_d_DefaultFileEditorPresenterActivity__quals__j_e_i_Any_j_i_Named_o_u_c_w_a_AssociatedResources_o_u_c_w_a_Priority.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<GlobalSessionManager>) GWT.create(Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<UndoToolbarCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_t_c_UndoToolbarCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ClearSessionCommand>) GWT.create(Type_factory__o_k_w_c_s_c_c_s_c_i_ClearSessionCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LienzoImageStripGlyphRenderer>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_g_LienzoImageStripGlyphRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ListBoxFieldInitializer>) GWT.create(Type_factory__o_k_w_c_f_a_e_s_f_p_f_f_s_ListBoxFieldInitializer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FieldChangeHandlerManagerImpl>) GWT.create(Type_factory__o_k_w_c_f_p_e_h_i_FieldChangeHandlerManagerImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<NewResourcePresenter>) GWT.create(Type_factory__o_k_w_c_w_c_h_NewResourcePresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<GuidedTourGraphObserver>) GWT.create(Type_factory__o_k_w_c_d_w_k_c_c_t_o_GuidedTourGraphObserver__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<GenerateDiagramFormsSessionCommand>) GWT.create(Type_factory__o_k_w_c_s_f_c_s_c_GenerateDiagramFormsSessionCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Paragraph>) GWT.create(ExtensionProvided_factory__o_j_e_c_c_d_Paragraph__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MenuItemCommand>) GWT.create(JsType_factory__o_u_w_m_m_MenuItemCommand__quals__Universal.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<EditorTreeProvider>) GWT.create(Type_factory__o_u_c_a_EditorTreeProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<RootPanel>) GWT.create(Provider_factory__c_g_g_u_c_u_RootPanel__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<PreferenceScopeTypesProducer>) GWT.create(Type_factory__o_u_p_s_i_PreferenceScopeTypesProducer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DateTimePickerCell>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerCell__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<AuthoringPerspectiveActivity>) GWT.create(Type_factory__o_k_w_c_k_w_b_c_p_AuthoringPerspectiveActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNDefinitionSetPropertyAdapterImpl>) GWT.create(Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetPropertyAdapterImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<StaticWorkbenchPanelView>) GWT.create(Type_factory__o_u_c_w_p_i_StaticWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<QNameConverter>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_QNameConverter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MultiScreenWorkbenchPanelPresenter>) GWT.create(Type_factory__o_u_c_w_p_i_MultiScreenWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<WorkbenchMenuBarPresenter>) GWT.create(Producer_factory__o_u_c_w_w_m_WorkbenchMenuBarPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ToolBar>) GWT.create(JsType_factory__o_u_w_m_t_ToolBar__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CardFrameComponent>) GWT.create(Type_factory__o_k_w_c_w_c_c_f_CardFrameComponent__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<GAVPreferencesBeanGeneratedImpl>) GWT.create(Type_factory__o_g_c_s_p_p_GAVPreferencesBeanGeneratedImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNEditBusinessKnowledgeModelToolboxAction>) GWT.create(Type_factory__o_k_w_c_d_c_c_c_t_DMNEditBusinessKnowledgeModelToolboxAction__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNTutorial>) GWT.create(Type_factory__o_k_w_c_d_w_k_c_c_t_t_DMNTutorial__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SplashScreenMenuView>) GWT.create(Type_factory__o_u_c_v_p_m_SplashScreenMenuView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<KogitoUndoSessionCommand>) GWT.create(Type_factory__o_k_w_c_s_k_r_c_s_c_i_KogitoUndoSessionCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DestructionImpl>) GWT.create(Type_factory__o_j_e_i_c_l_i_DestructionImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ContextMenuView>) GWT.create(Type_factory__o_k_w_c_d_c_e_c_ContextMenuView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HTMLHeadingElement>) GWT.create(ExtensionProvided_factory__e_d_HTMLHeadingElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named_1.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HtmlEditorView>) GWT.create(Type_factory__o_u_e_e_c_c_h_HtmlEditorView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CompassDropController>) GWT.create(Type_factory__o_u_c_w_w_d_CompassDropController__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultToolbarView>) GWT.create(Type_factory__o_k_w_c_s_c_w_t_i_DefaultToolbarView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<WorkspaceProjectContext>) GWT.create(Type_factory__o_g_c_s_p_c_c_WorkspaceProjectContext__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<GuidedTourBridge>) GWT.create(Type_factory__o_a_k_b_c_g_t_GuidedTourBridge__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PreferencesCentralNavBarScreen>) GWT.create(Type_factory__o_u_e_p_c_c_PreferencesCentralNavBarScreen__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<StunnerConverter>) GWT.create(Type_factory__o_k_w_c_d_c_m_u_n_StunnerConverter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DNDListComponentView>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_d_DNDListComponentView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<AppLauncherPresenter>) GWT.create(Type_factory__o_k_w_c_w_c_p_l_AppLauncherPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DecisionNavigatorView>) GWT.create(Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CurrentBranchImpl>) GWT.create(Type_factory__o_k_w_c_w_c_m_CurrentBranchImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DecisionComponents>) GWT.create(Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponents__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<NodeEntriesFactory>) GWT.create(Type_factory__o_k_w_c_d_c_m_u_n_NodeEntriesFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ChildContextMenuItemView>) GWT.create(Type_factory__o_u_c_v_p_m_m_c_ChildContextMenuItemView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<AboutPopup>) GWT.create(Type_factory__o_k_w_c_w_c_p_a_AboutPopup__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ManagedClientSessionCommands>) GWT.create(Type_factory__o_k_w_c_s_c_c_s_c_ManagedClientSessionCommands__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CommonActionsToolboxFactory>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_t_a_CommonActionsToolboxFactory__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_CommonActionsToolbox.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<MenuItemFactory>) GWT.create(Type_factory__o_u_e_w_c_c_m_MenuItemFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DateEditableColumnGenerator>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<BooleanColumnGenerator>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_r_m_c_i_BooleanColumnGenerator__quals__j_e_i_Any.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<RadioGroupFieldProvider>) GWT.create(Type_factory__o_k_w_c_f_f_s_f_b_s_r_p_RadioGroupFieldProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNCanvasShortcutsControlProxy>) GWT.create(Type_factory__o_k_w_c_d_c_s_DMNCanvasShortcutsControlProxy__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<LocalClipboardControl>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_c_c_LocalClipboardControl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<YearsMonthsSelector>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_y_m_YearsMonthsSelector__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LienzoPanelMediators>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_m_LienzoPanelMediators__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<RestorePopUpView>) GWT.create(Type_factory__o_u_e_e_c_c_f_p_RestorePopUpView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Command>) GWT.create(JsType_factory__o_u_m_Command__quals__Universal.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<LienzoCanvasExport>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_e_LienzoCanvasExport__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<PreferencesEntryPoint>) GWT.create(Type_factory__o_u_e_p_c_PreferencesEntryPoint__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MetaFileEditorPresenterActivity>) GWT.create(Type_factory__o_u_e_w_c_c_e_m_MetaFileEditorPresenterActivity__quals__j_e_i_Any_j_i_Named_o_u_c_w_a_AssociatedResources_o_u_c_w_a_Priority.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<FormsClientEntryPoint>) GWT.create(Type_factory__o_k_w_c_s_f_c_FormsClientEntryPoint__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<PreferenceBeanStoreClientImpl>) GWT.create(Type_factory__o_u_p_c_s_PreferenceBeanStoreClientImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ChildrenTraverseProcessorImpl>) GWT.create(Type_factory__o_k_w_c_s_c_g_p_t_c_ChildrenTraverseProcessorImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<AdminPageView>) GWT.create(Type_factory__o_u_e_p_c_a_AdminPageView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<I18nServiceProducer>) GWT.create(Type_factory__o_a_k_b_c_i_I18nServiceProducer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<WidgetWrapperViewImpl>) GWT.create(Type_factory__o_k_w_c_s_c_w_v_WidgetWrapperViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ContainmentAcceptorControlImpl>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_c_ContainmentAcceptorControlImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<IntegerEditableColumnGenerator>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_n_i_IntegerEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<WidgetElementRendererViewImpl>) GWT.create(Type_factory__o_k_w_c_s_c_w_v_WidgetElementRendererViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<MessageConsoleViewImpl>) GWT.create(Type_factory__o_g_m_c_c_MessageConsoleViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<StunnerWiresControlFactory>) GWT.create(Type_factory__o_k_w_c_s_c_l_w_StunnerWiresControlFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ClientFormGenerator>) GWT.create(Type_factory__o_k_w_c_f_a_e_c_f_ClientFormGenerator__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ClientPropertuValueExtractor>) GWT.create(Type_factory__o_k_w_c_f_a_e_c_f_u_ClientPropertuValueExtractor__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DiscussionWidgetViewImpl>) GWT.create(Type_factory__o_k_w_c_w_c_d_DiscussionWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DiagramEditorView>) GWT.create(Type_factory__o_k_w_c_s_k_c_e_DiagramEditorView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PreviewLienzoPanel>) GWT.create(Type_factory__o_k_w_c_s_c_w_c_PreviewLienzoPanel__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<StringSelector>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_s_StringSelector__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ResetPerspectivesMenuBuilder>) GWT.create(Type_factory__o_k_w_c_w_c_m_ResetPerspectivesMenuBuilder__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<org.uberfire.client.workbench.widgets.notfound.ActivityNotFoundPresenter>) GWT.create(Type_factory__o_u_c_w_w_n_ActivityNotFoundPresenter__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ListSelectorHeaderItemViewImpl>) GWT.create(Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelectorHeaderItemViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ToolbarItemView>) GWT.create(Type_factory__o_k_w_c_s_c_w_t_i_ToolbarItemView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ScalableLienzoPanel>) GWT.create(Type_factory__o_k_w_c_s_c_w_c_ScalableLienzoPanel__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ClosableSimpleWorkbenchPanelPresenter>) GWT.create(Type_factory__o_u_c_w_p_i_ClosableSimpleWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HTMLDivElement>) GWT.create(ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ItemDefinitionRecordEngine>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_p_ItemDefinitionRecordEngine__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ExportToJpgToolbarCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_t_c_ExportToJpgToolbarCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<UberfireBreadcrumbsContainerImpl>) GWT.create(Type_factory__o_u_e_w_c_c_b_h_UberfireBreadcrumbsContainerImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ScrollHelper>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_c_ScrollHelper__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ShapeViewFactory>) GWT.create(Type_factory__o_k_w_c_s_s_c_v_ShapeViewFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<JsTypeProvider>) GWT.create(JsType_factory__o_j_e_i_c_c_JsTypeProvider__quals__Universal.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<WiresManagerFactoryImpl>) GWT.create(Type_factory__o_k_w_c_s_c_l_w_WiresManagerFactoryImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SimpleDnDWorkbenchPanelView>) GWT.create(Type_factory__o_u_c_w_p_i_SimpleDnDWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<GraphElementsPositionProviderFactory>) GWT.create(Type_factory__o_k_w_c_d_w_k_c_c_t_p_GraphElementsPositionProviderFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LiveSearchFooter>) GWT.create(Type_factory__o_u_e_w_c_c_d_f_LiveSearchFooter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HTMLTextAreaElement>) GWT.create(ExtensionProvided_factory__e_d_HTMLTextAreaElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<WorkbenchMenuBarView>) GWT.create(Type_factory__o_u_c_v_p_m_WorkbenchMenuBarView__quals__j_e_i_Any_j_e_i_Default.class));
  }

  private void registerFactories2(final Context org_jboss_errai_ioc_client_container_ApplicationScopedContext_context, final Context org_jboss_errai_ioc_client_container_DependentScopeContext_context) {
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SessionLoader>) GWT.create(Type_factory__o_k_w_c_s_c_c_s_i_SessionLoader__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<EventProvider>) GWT.create(Type_factory__o_j_e_e_c_c_EventProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<WorkbenchPreferenceScopeResolutionStrategiesImpl>) GWT.create(Type_factory__o_k_w_c_s_s_p_c_WorkbenchPreferenceScopeResolutionStrategiesImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DoubleEditableColumnGenerator>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_n_d_DoubleEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SanitizedNumberInput>) GWT.create(Type_factory__o_u_c_v_p_w_SanitizedNumberInput__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SvgElementGlyphRenderer>) GWT.create(Type_factory__o_k_w_c_s_c_w_c_g_SvgElementGlyphRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DecimalRadioGroupFieldRenderer>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_s_r_DecimalRadioGroupFieldRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LogSelectedItemsDevCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_m_d_i_LogSelectedItemsDevCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<LayoutComponentPalettePresenter>) GWT.create(Type_factory__o_u_e_l_e_c_w_LayoutComponentPalettePresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LayoutHelper>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_l_LayoutHelper__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<GlobalHTMLObserver>) GWT.create(Type_factory__o_a_k_b_c_g_t_o_GlobalHTMLObserver__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<PropertiesPanelNotifier>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_p_h_c_PropertiesPanelNotifier__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LogNodeEdgesDevCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_m_d_i_LogNodeEdgesDevCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Heading>) GWT.create(ExtensionProvided_factory__o_j_e_c_c_d_Heading__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FieldHelp>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_f_l_h_FieldHelp__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FormsContainer>) GWT.create(Type_factory__o_k_w_c_s_f_c_w_c_FormsContainer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CreateConnectorToolboxAction>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_t_a_CreateConnectorToolboxAction__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultFormGroupViewImpl>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_f_i_d_DefaultFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ClientRegistryFactoryImpl>) GWT.create(Type_factory__o_k_w_c_s_c_c_r_i_ClientRegistryFactoryImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<GroupMenuItemView>) GWT.create(Type_factory__o_u_c_v_p_m_m_m_GroupMenuItemView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ProcessingEngineEntryPoint>) GWT.create(Type_factory__o_k_w_c_f_p_e_h_ProcessingEngineEntryPoint__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ClosableSimpleWorkbenchPanelView>) GWT.create(Type_factory__o_u_c_w_p_i_ClosableSimpleWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNDataObjectsClient>) GWT.create(Type_factory__o_k_w_c_d_c_a_d_DMNDataObjectsClient__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ConnectorParentsMatchConnectionHandler>) GWT.create(Type_factory__o_k_w_c_s_c_r_e_i_ConnectorParentsMatchConnectionHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<AppFormerActivitiesProducer>) GWT.create(Type_factory__o_u_w_m_AppFormerActivitiesProducer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<NodeProxy>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_p_NodeProxy__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<WorkbenchConfigurationViewImpl>) GWT.create(Type_factory__o_k_w_c_w_c_h_w_c_WorkbenchConfigurationViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PreferenceStore>) GWT.create(ContextualProvider_factory__o_u_p_c_s_PreferenceStore__quals__Universal.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DynamicRendererEntryPoint>) GWT.create(Type_factory__o_k_w_c_f_d_c_DynamicRendererEntryPoint__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ConnectionEvaluationHandler>) GWT.create(Type_factory__o_k_w_c_s_c_r_h_i_ConnectionEvaluationHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HTMLOListElement>) GWT.create(ExtensionProvided_factory__e_d_HTMLOListElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<AdapterManagerImpl>) GWT.create(Type_factory__o_k_w_c_s_c_d_a_AdapterManagerImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<BootstrapAdapterFactory>) GWT.create(Type_factory__o_k_w_c_s_c_d_a_b_BootstrapAdapterFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<AdminPageItemPresenter>) GWT.create(Type_factory__o_u_e_p_c_a_i_AdminPageItemPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ElementBuilderControlImpl>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_c_b_i_ElementBuilderControlImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_c_c_b_i_Element.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Activity>) GWT.create(JsType_factory__o_u_c_m_Activity__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MarshallingResponsePopup>) GWT.create(Type_factory__o_k_w_c_s_c_w_m_MarshallingResponsePopup__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CurrentBranch>) GWT.create(Producer_factory__o_u_e_e_c_v_CurrentBranch__quals__j_e_i_Any_o_u_a_Customizable.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<LocaleSelector>) GWT.create(Type_factory__o_j_e_u_c_w_LocaleSelector__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<WiresCanvas>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_w_WiresCanvas__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeConstraintModal>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraintModal__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MultipleSubFormFieldRenderer>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_r_m_MultipleSubFormFieldRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNDecisionServiceShapeFactory>) GWT.create(Type_factory__o_k_w_c_d_c_s_f_DMNDecisionServiceShapeFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<GraphFactoryImpl>) GWT.create(Type_factory__o_k_w_c_s_c_f_i_GraphFactoryImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CreationImpl>) GWT.create(Type_factory__o_j_e_i_c_l_i_CreationImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DefaultPreferenceScopeTypes>) GWT.create(Type_factory__o_u_p_s_i_DefaultPreferenceScopeTypes__quals__j_e_i_Any_o_u_a_FallbackImplementation.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ContextDefinition>) GWT.create(JsType_factory__o_u_w_m_ContextDefinition__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ListSelectorTextItemViewImpl>) GWT.create(Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelectorTextItemViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ResizeControlImpl>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_c_ResizeControlImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LayoutEditorPluginImpl>) GWT.create(Type_factory__o_u_e_l_e_c_LayoutEditorPluginImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SplashViewImpl>) GWT.create(Type_factory__o_u_c_v_p_s_SplashViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<LienzoGlyphRenderers>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_g_LienzoGlyphRenderers__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PerspectiveActivity>) GWT.create(JsType_factory__o_u_c_m_PerspectiveActivity__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ExperimentalFeaturesGroup>) GWT.create(Type_factory__o_u_e_c_e_g_ExperimentalFeaturesGroup__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<WorkbenchLayoutImpl>) GWT.create(Type_factory__o_u_c_w_WorkbenchLayoutImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FormLayoutGenerator>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_FormLayoutGenerator__quals__j_e_i_Any.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeConstraintExpression>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_e_DataTypeConstraintExpression__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PackageListBox>) GWT.create(Type_factory__o_k_w_c_w_c_h_PackageListBox__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ZoomLevelSelectorItem>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_m_ZoomLevelSelectorItem__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNGraphProcessor>) GWT.create(Type_factory__o_k_w_c_d_c_w_t_DMNGraphProcessor__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<BigDecimalEditableColumnGenerator>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_n_d_BigDecimalEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DataTypeStore>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_p_DataTypeStore__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SessionEditorPresenter>) GWT.create(Type_factory__o_k_w_c_s_c_w_p_s_i_SessionEditorPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<StunnerTextPreferences>) GWT.create(Type_factory__o_k_w_c_s_c_c_p_StunnerTextPreferences__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SavePopUpPresenter>) GWT.create(Type_factory__o_u_e_e_c_c_f_p_SavePopUpPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ChildMenuItemView>) GWT.create(Type_factory__o_u_c_v_p_m_m_m_ChildMenuItemView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultMenuGroup>) GWT.create(JsType_factory__o_u_w_m_m_i_DefaultMenuGroup__quals__Universal.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<StunnerLienzoCore>) GWT.create(Type_factory__o_k_w_c_s_c_l_StunnerLienzoCore__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LienzoShapeGlyphRenderer>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_g_LienzoShapeGlyphRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNDefinitionSetDefinitionSetAdapterImpl>) GWT.create(Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetDefinitionSetAdapterImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MultiScreenPartWidget>) GWT.create(Type_factory__o_u_c_v_p_m_MultiScreenPartWidget__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SimpleNoExpandWorkbenchPanelView>) GWT.create(Type_factory__o_u_c_w_p_i_SimpleNoExpandWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<LifecycleListenerRegistrarImpl>) GWT.create(Type_factory__o_j_e_i_c_l_i_LifecycleListenerRegistrarImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<SessionSingletonCommandsFactory>) GWT.create(Type_factory__o_k_w_c_s_c_c_s_c_i_SessionSingletonCommandsFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<RenamePopUpView>) GWT.create(Type_factory__o_u_e_e_c_c_f_p_RenamePopUpView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ExperimentalFeaturesEditorScreenViewImpl>) GWT.create(Type_factory__o_u_e_c_e_ExperimentalFeaturesEditorScreenViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeListItem>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListItem__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultPreferenceFormView>) GWT.create(Type_factory__o_u_e_p_c_c_f_DefaultPreferenceFormView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<CardinalityEvaluationHandler>) GWT.create(Type_factory__o_k_w_c_s_c_r_h_i_CardinalityEvaluationHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DatePickerFieldRenderer>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_d_DatePickerFieldRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNMarshallerService>) GWT.create(Type_factory__o_k_w_c_d_c_m_DMNMarshallerService__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<org.jboss.errai.common.client.dom.HTMLElement>) GWT.create(ContextualProvider_factory__o_j_e_c_c_d_HTMLElement__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<GraphBoundsIndexerImpl>) GWT.create(Type_factory__o_k_w_c_s_c_g_p_i_b_GraphBoundsIndexerImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultBreadcrumbsView>) GWT.create(Type_factory__o_u_e_w_c_c_b_w_DefaultBreadcrumbsView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PreferencesCentralPerspectiveActivity>) GWT.create(Type_factory__o_u_e_p_c_c_PreferencesCentralPerspectiveActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNClientEntryPoint>) GWT.create(Type_factory__o_k_w_c_d_c_DMNClientEntryPoint__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<FormsElementWrapperWidgetUtilImpl>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_u_i_FormsElementWrapperWidgetUtilImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<BS3PaletteWidgetImpl>) GWT.create(Type_factory__o_k_w_c_s_c_w_p_BS3PaletteWidgetImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<VersionHistoryPresenter>) GWT.create(Type_factory__o_u_e_e_c_c_h_VersionHistoryPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PortablePreferenceMockBeanGeneratedImpl>) GWT.create(Type_factory__o_u_p_s_b_m_PortablePreferenceMockBeanGeneratedImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNEditorSearchIndex>) GWT.create(Type_factory__o_k_w_c_d_c_e_s_DMNEditorSearchIndex__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MultipleSelectorProvider>) GWT.create(Type_factory__o_k_w_c_f_f_s_f_b_l_s_MultipleSelectorProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ImageElementGlyphRenderer>) GWT.create(Type_factory__o_k_w_c_s_c_w_c_g_ImageElementGlyphRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ClientDefinitionBindablePropertyAdapter>) GWT.create(Type_factory__o_k_w_c_s_c_c_d_a_b_ClientDefinitionBindablePropertyAdapter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TreeExplorer>) GWT.create(Type_factory__o_k_w_c_s_c_w_e_t_TreeExplorer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LienzoCanvasNotification>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_v_LienzoCanvasNotification__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TextAreaFieldInitializer>) GWT.create(Type_factory__o_k_w_c_f_a_e_s_f_p_f_f_TextAreaFieldInitializer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<org.uberfire.client.views.pfly.modal.ErrorPopupView>) GWT.create(Type_factory__o_u_c_v_p_m_ErrorPopupView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ClientTranslationMessages>) GWT.create(Type_factory__o_k_w_c_s_c_c_i_ClientTranslationMessages__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PrintHelper>) GWT.create(Type_factory__o_k_w_c_s_c_c_u_PrintHelper__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DataTypeSearchBar>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchBar__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ContextEditorDefinition>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_t_c_ContextEditorDefinition__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DockPlaceHolderActivity>) GWT.create(Type_factory__o_k_w_c_w_c_d_DockPlaceHolderActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<AnyResourceType>) GWT.create(Type_factory__o_u_c_w_t_AnyResourceType__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<FormWidgetsEntryPoint>) GWT.create(Type_factory__o_k_w_c_f_c_r_c_FormWidgetsEntryPoint__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<org.kie.workbench.common.dmn.client.property.dmn.ModuleFieldRendererTypesProvider>) GWT.create(Type_factory__o_k_w_c_d_c_p_d_ModuleFieldRendererTypesProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CanvasHighlight>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_u_CanvasHighlight__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<NotificationManager>) GWT.create(Type_factory__o_u_c_w_w_n_NotificationManager__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<AdminPageCategoryView>) GWT.create(Type_factory__o_u_e_p_c_a_c_AdminPageCategoryView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultBreadcrumbsPresenter>) GWT.create(Type_factory__o_u_e_w_c_c_b_w_DefaultBreadcrumbsPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LogMagnetsDevCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_m_d_i_LogMagnetsDevCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<RestoreVersionCommandProvider>) GWT.create(Type_factory__o_u_e_e_c_c_m_RestoreVersionCommandProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LayoutEditorPresenter>) GWT.create(Type_factory__o_u_e_l_e_c_LayoutEditorPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DatePickerFieldProvider>) GWT.create(Type_factory__o_k_w_c_f_f_s_f_b_d_p_DatePickerFieldProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ToggleCommentPresenter>) GWT.create(Type_factory__o_u_e_e_c_c_f_p_c_ToggleCommentPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNToolboxControl>) GWT.create(Type_factory__o_k_w_c_d_c_c_c_t_DMNToolboxControl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SubFormFieldRenderer>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_r_s_SubFormFieldRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MultipleSubFormWidget>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_r_m_MultipleSubFormWidget__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DotResourceType>) GWT.create(Type_factory__o_u_c_w_t_DotResourceType__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ExpressionEditorDefinitions>) GWT.create(Producer_factory__o_k_w_c_d_c_e_e_t_ExpressionEditorDefinitions__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TextBoxFormPopup>) GWT.create(Type_factory__o_k_w_c_w_c_p_t_TextBoxFormPopup__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNGridPanelControlImpl>) GWT.create(Type_factory__o_k_w_c_d_c_w_p_DMNGridPanelControlImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HTMLAnchorElement>) GWT.create(ExtensionProvided_factory__e_d_HTMLAnchorElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ValidateSessionCommand>) GWT.create(Type_factory__o_k_w_c_s_c_c_s_c_i_ValidateSessionCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeNameIsDefaultTypeMessage>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_c_e_DataTypeNameIsDefaultTypeMessage__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ManagedInstance>) GWT.create(ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNShapeSet>) GWT.create(Type_factory__o_k_w_c_d_c_DMNShapeSet__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Menus>) GWT.create(JsType_factory__o_u_w_m_m_Menus__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ExportToJpgSessionCommand>) GWT.create(Type_factory__o_k_w_c_s_c_c_s_c_i_ExportToJpgSessionCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<NoCanvasView>) GWT.create(Type_factory__o_k_w_c_s_c_w_v_s_NoCanvasView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LienzoSvgDataUriGlyphRenderer>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_g_LienzoSvgDataUriGlyphRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DiagramEditorPropertiesScreen>) GWT.create(Type_factory__o_k_w_c_s_k_c_s_DiagramEditorPropertiesScreen__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<GuidedTourServiceProducer>) GWT.create(Type_factory__o_a_k_b_c_g_t_s_GuidedTourServiceProducer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<PreferenceScopeValidatorImpl>) GWT.create(Type_factory__o_u_p_s_i_PreferenceScopeValidatorImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FormsCanvasSessionHandler>) GWT.create(Type_factory__o_k_w_c_s_f_c_w_FormsCanvasSessionHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PreferenceScopeResolutionStrategy>) GWT.create(ContextualProvider_factory__o_u_p_s_PreferenceScopeResolutionStrategy__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DeletePopUpView>) GWT.create(Type_factory__o_u_e_e_c_c_f_p_DeletePopUpView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNMarshallerImportsContentServiceKogitoImpl>) GWT.create(Type_factory__o_k_w_c_d_w_k_c_c_i_DMNMarshallerImportsContentServiceKogitoImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ExperimentalAwareClientTypeRegistryImpl>) GWT.create(Type_factory__o_u_e_c_w_t_ExperimentalAwareClientTypeRegistryImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNDocumentationDRDsFactory>) GWT.create(Type_factory__o_k_w_c_d_c_e_d_c_DMNDocumentationDRDsFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DatePickerWrapperViewImpl>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_d_i_DatePickerWrapperViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNMarshallerImportsContentServiceImpl>) GWT.create(Type_factory__o_k_w_c_d_c_m_i_DMNMarshallerImportsContentServiceImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PreferencesRootView>) GWT.create(Type_factory__o_u_e_p_c_c_s_PreferencesRootView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataBinder>) GWT.create(ContextualProvider_factory__o_j_e_d_c_a_DataBinder__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<NewTabFilterPopup>) GWT.create(Type_factory__o_u_e_w_c_c_t_p_NewTabFilterPopup__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HTMLElementsPositionProviderFactory>) GWT.create(Type_factory__o_k_w_c_d_w_k_c_c_t_p_HTMLElementsPositionProviderFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FieldTypeEntryProcessor>) GWT.create(Type_factory__o_k_w_c_f_s_s_m_p_i_p_FieldTypeEntryProcessor__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DefaultEditorContextProviderImpl>) GWT.create(Type_factory__o_a_c_c_DefaultEditorContextProviderImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<WorkbenchMenuBarProducer>) GWT.create(Type_factory__o_u_c_w_w_m_WorkbenchMenuBarProducer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<BuiltinAggregatorUtils>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_t_d_h_BuiltinAggregatorUtils__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNTutorialView>) GWT.create(Type_factory__o_k_w_c_d_w_k_c_c_t_t_DMNTutorialView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<RootPanelProvider>) GWT.create(Type_factory__o_j_e_i_c_a_b_RootPanelProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TimePicker>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimePicker__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<BS3PaletteWidgetViewImpl>) GWT.create(Type_factory__o_k_w_c_s_c_w_p_BS3PaletteWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ImageStripRegistry>) GWT.create(Type_factory__o_k_w_c_s_c_c_s_ImageStripRegistry__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeNameValidator>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_p_v_DataTypeNameValidator__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<XMLConstants>) GWT.create(JsType_factory__j_x_XMLConstants__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TextEditorWorkbenchEditorActivity>) GWT.create(Type_factory__o_u_e_w_c_c_e_t_TextEditorWorkbenchEditorActivity__quals__j_e_i_Any_j_i_Named_o_u_c_w_a_AssociatedResources_o_u_c_w_a_Priority.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<GWTEditorNativeRegister>) GWT.create(Type_factory__o_u_c_u_GWTEditorNativeRegister__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DynamicFormModelGenerator>) GWT.create(Type_factory__o_k_w_c_f_d_s_s_a_DynamicFormModelGenerator__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<SessionEventObserver>) GWT.create(Type_factory__o_k_w_c_s_c_c_s_e_SessionEventObserver__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MetaFileEditorPresenter>) GWT.create(Type_factory__o_u_e_w_c_c_e_m_MetaFileEditorPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ConnectorParentsMatchHandler>) GWT.create(Type_factory__o_k_w_c_s_c_r_e_i_ConnectorParentsMatchHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<BootstrapNavigatorView>) GWT.create(Type_factory__o_k_w_c_s_c_w_e_n_v_BootstrapNavigatorView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeNameIsInvalidErrorMessage>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_c_e_DataTypeNameIsInvalidErrorMessage__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ValidationMessageTranslatorUtils>) GWT.create(Type_factory__o_k_w_c_w_c_p_v_ValidationMessageTranslatorUtils__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DefaultCanvasCommandFactory>) GWT.create(Type_factory__o_k_w_c_d_c_c_f_DefaultCanvasCommandFactory__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ClientMustacheTemplateRenderer>) GWT.create(Type_factory__o_u_e_e_c_c_t_m_ClientMustacheTemplateRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PreviewDiagramScreenView>) GWT.create(Type_factory__o_k_w_c_d_c_d_p_PreviewDiagramScreenView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ConnectionAcceptorControlImpl>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_c_ConnectionAcceptorControlImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LogSessionDevCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_m_d_i_LogSessionDevCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ContextDisplayMode>) GWT.create(JsType_factory__o_u_w_m_ContextDisplayMode__quals__Universal.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<WorkbenchLayoutInfoImpl>) GWT.create(Type_factory__o_u_c_w_WorkbenchLayoutInfoImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNPerformAutomaticLayoutCommand>) GWT.create(Type_factory__o_k_w_c_d_c_w_t_DMNPerformAutomaticLayoutCommand__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SaveToolbarCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_t_c_SaveToolbarCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<TimeZonesProvider>) GWT.create(Type_factory__o_k_w_c_d_w_k_c_c_s_TimeZonesProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ApplicationCommandManager>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_ApplicationCommandManager__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PerspectiveDefinition>) GWT.create(JsType_factory__o_u_w_m_PerspectiveDefinition__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<VisitGraphSessionCommand>) GWT.create(Type_factory__o_k_w_c_s_c_c_s_c_i_VisitGraphSessionCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DefinitionLookupManagerImpl>) GWT.create(Type_factory__o_k_w_c_s_c_l_d_DefinitionLookupManagerImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ItemDefinitionUpdateHandler>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_p_h_ItemDefinitionUpdateHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FlashMessages>) GWT.create(Type_factory__o_k_w_c_d_c_e_c_m_FlashMessages__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ServerTemplateProvider>) GWT.create(Type_factory__o_j_e_u_s_ServerTemplateProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ElementShapeGlyphRenderer>) GWT.create(Type_factory__o_k_w_c_s_c_w_c_g_ElementShapeGlyphRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<org.gwtbootstrap3.client.ui.Button>) GWT.create(ExtensionProvided_factory__o_g_c_u_Button__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<NodeBuilderControlImpl>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_c_b_i_NodeBuilderControlImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ValidateToolbarCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_t_c_ValidateToolbarCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<LiteralExpressionPMMLDocumentEditorDefinition>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_t_f_s_p_d_LiteralExpressionPMMLDocumentEditorDefinition__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ReadOnlyProviderImpl>) GWT.create(Type_factory__o_k_w_c_d_c_a_ReadOnlyProviderImpl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CutToolbarCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_t_c_CutToolbarCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ShapeManagerImpl>) GWT.create(Type_factory__o_k_w_c_s_c_c_a_ShapeManagerImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HTMLSelectElement>) GWT.create(ExtensionProvided_factory__e_d_HTMLSelectElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<PlaceManagerImpl>) GWT.create(Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<CustomSplashHelp>) GWT.create(Type_factory__o_u_c_m_CustomSplashHelp__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<NameFieldProvider>) GWT.create(Type_factory__o_k_w_c_d_c_p_d_NameFieldProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.definition.formBuilder.provider.ModuleFormGenerationResourcesProvider>) GWT.create(Type_factory__o_k_w_c_f_f_s_f_b_s_d_f_p_ModuleFormGenerationResourcesProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<WorkbenchConfigurationPresenter>) GWT.create(Type_factory__o_k_w_c_w_c_h_w_c_WorkbenchConfigurationPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DefaultAuthorizationManager>) GWT.create(Type_factory__o_u_s_i_a_DefaultAuthorizationManager__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PictureWidgetViewImpl>) GWT.create(Type_factory__o_k_w_c_f_c_r_c_w_p_w_PictureWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNCardsGridComponent>) GWT.create(Type_factory__o_k_w_c_d_c_e_i_g_DMNCardsGridComponent__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DecimalBox>) GWT.create(Type_factory__o_k_w_c_f_c_r_c_w_d_DecimalBox__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<org.kie.workbench.common.forms.dynamic.client.rendering.renderers.ModuleFieldRendererTypesProvider>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_ModuleFieldRendererTypesProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultRegistryImpl>) GWT.create(Type_factory__o_a_c_s_r_i_DefaultRegistryImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SelectView>) GWT.create(Type_factory__o_u_e_w_c_c_s_SelectView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DocumentationLinkItem>) GWT.create(Type_factory__o_k_w_c_d_c_e_d_DocumentationLinkItem__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ViewDRLSourceWidget>) GWT.create(ExtensionProvided_factory__o_k_w_c_w_c_s_ViewDRLSourceWidget__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<WorkbenchActivity>) GWT.create(JsType_factory__o_u_c_m_WorkbenchActivity__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<IntegerBox>) GWT.create(Type_factory__o_k_w_c_f_c_r_c_w_i_IntegerBox__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DomainProfileManager>) GWT.create(Type_factory__o_k_w_c_s_c_p_DomainProfileManager__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<OpenDiagramLayoutExecutor>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_l_OpenDiagramLayoutExecutor__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<MessageConsoleService>) GWT.create(Type_factory__o_g_m_c_c_MessageConsoleService__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<XMLConstants>) GWT.create(Type_factory__j_x_XMLConstants__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<WorkbenchViewModeSwitcherMenuBuilder>) GWT.create(Type_factory__o_u_c_m_WorkbenchViewModeSwitcherMenuBuilder__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<UnanchoredStaticWorkbenchPanelView>) GWT.create(Type_factory__o_u_c_w_p_i_UnanchoredStaticWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<KieSelectOptionsListPresenter>) GWT.create(Type_factory__o_k_w_c_w_c_w_KieSelectOptionsListPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ColumnGeneratorManager>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_r_m_ColumnGeneratorManager__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DataTypeStackHash>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeStackHash__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DnDManager>) GWT.create(Type_factory__o_u_e_l_e_c_i_DnDManager__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LienzoMediatorsControl>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_c_LienzoMediatorsControl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CanvasDefinitionTooltip>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_v_CanvasDefinitionTooltip__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DefaultAppFormerActivities>) GWT.create(Type_factory__o_u_w_m_DefaultAppFormerActivities__quals__j_e_i_Any_o_u_a_FallbackImplementation.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HTMLLIElement>) GWT.create(ExtensionProvided_factory__e_d_HTMLLIElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ConnectorParentsMatchContainmentHandler>) GWT.create(Type_factory__o_k_w_c_s_c_r_e_i_ConnectorParentsMatchContainmentHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<InlineNotification>) GWT.create(Type_factory__o_u_c_v_p_w_InlineNotification__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<Process>) GWT.create(Type_factory__o_g_c_s_p_c_Process__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<MonacoFEELInitializer>) GWT.create(Type_factory__o_k_w_c_d_c_w_c_MonacoFEELInitializer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DRDContextMenu>) GWT.create(Type_factory__o_k_w_c_d_c_e_d_DRDContextMenu__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CopyToolbarCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_t_c_CopyToolbarCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<RegistryProvider>) GWT.create(Type_factory__o_k_w_c_d_c_s_RegistryProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SmallSwitchComponentView>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_SmallSwitchComponentView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DiagramEditorExplorerScreen>) GWT.create(Type_factory__o_k_w_c_s_k_c_s_DiagramEditorExplorerScreen__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ProjectScopedResolutionStrategySupplier>) GWT.create(Type_factory__o_g_c_s_p_c_p_ProjectScopedResolutionStrategySupplier__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DecisionNavigatorPresenter>) GWT.create(Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DocksBars>) GWT.create(Type_factory__o_u_c_d_v_DocksBars__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DisposerProvider>) GWT.create(Type_factory__o_j_e_i_c_a_b_DisposerProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TimeValueFormatter>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimeValueFormatter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PdfFileExport>) GWT.create(Producer_factory__o_u_e_e_c_c_f_e_PdfFileExport__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Icon>) GWT.create(ExtensionProvided_factory__o_g_c_u_Icon__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<org.kie.workbench.common.stunner.core.client.api.ReadOnlyProviderImpl>) GWT.create(Type_factory__o_k_w_c_s_c_c_a_ReadOnlyProviderImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<EmptyDropRow>) GWT.create(Type_factory__o_u_e_l_e_c_c_r_EmptyDropRow__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ManagedInstanceProvider>) GWT.create(Type_factory__o_j_e_i_c_a_b_ManagedInstanceProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<KieMultipleSelectElement>) GWT.create(Type_factory__o_k_w_c_w_c_w_KieMultipleSelectElement__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<KieMultipleSelectElementView>) GWT.create(Type_factory__o_k_w_c_w_c_w_KieMultipleSelectElementView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ProjectEntryPoint>) GWT.create(Type_factory__o_g_c_s_p_c_ProjectEntryPoint__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNConnectorShapeFactory>) GWT.create(Type_factory__o_k_w_c_d_c_s_f_DMNConnectorShapeFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<List>) GWT.create(JsType_factory__j_u_List__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Image>) GWT.create(ExtensionProvided_factory__o_j_e_c_c_d_Image__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PictureFieldProvider>) GWT.create(Type_factory__o_k_w_c_f_f_s_f_b_i_p_PictureFieldProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ReverseEdgesCycleBreaker>) GWT.create(Type_factory__o_k_w_c_s_c_g_p_l_s_s_ReverseEdgesCycleBreaker__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TreeHierarchyStructurePresenter>) GWT.create(Type_factory__o_u_e_p_c_c_t_TreeHierarchyStructurePresenter__quals__j_e_i_Any_o_u_e_p_c_c_t_TreeView.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LogGraphDevCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_m_d_i_LogGraphDevCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HTMLElementProvider>) GWT.create(Type_factory__o_j_e_u_c_l_p_HTMLElementProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefinitionPaletteItemWidgetViewImpl>) GWT.create(Type_factory__o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ExpressionEditorViewImpl>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_ExpressionEditorViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultMenus>) GWT.create(JsType_factory__o_u_w_m_m_i_DefaultMenus__quals__Universal.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DefaultFileNameValidator>) GWT.create(Type_factory__o_u_e_e_c_c_v_DefaultFileNameValidator__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<IncludedModelsPageView>) GWT.create(Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPageView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<MenuBuilder>) GWT.create(Type_factory__o_u_c_d_v_m_MenuBuilder__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ViewSourceViewImpl>) GWT.create(Type_factory__o_k_w_c_w_c_s_ViewSourceViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNGraphSubIndex>) GWT.create(Type_factory__o_k_w_c_d_c_e_s_DMNGraphSubIndex__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<View>) GWT.create(Producer_factory__o_u_e_e_c_c_f_p_CopyPopUpPresenter_View__quals__j_e_i_Any_o_u_e_e_c_c_f_Customizable.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ValueAndDataTypePopoverImpl>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_ValueAndDataTypePopoverImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<NewFilterPopup>) GWT.create(Type_factory__o_u_e_w_c_c_t_p_NewFilterPopup__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<QName>) GWT.create(JsType_factory__j_x_n_QName__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<QNameFieldRenderer>) GWT.create(Type_factory__o_k_w_c_d_c_p_d_QNameFieldRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ListItem>) GWT.create(ExtensionProvided_factory__o_j_e_c_c_d_ListItem__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeManager>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_c_DataTypeManager__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FormsContainerViewImpl>) GWT.create(Type_factory__o_k_w_c_s_f_c_w_c_FormsContainerViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<SessionInfo>) GWT.create(Producer_factory__o_u_r_SessionInfo__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ClientDefinitionsCacheRegistry>) GWT.create(Type_factory__o_k_w_c_s_c_c_r_i_ClientDefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<NoItemsComponentViewImpl>) GWT.create(Type_factory__o_u_e_w_c_c_d_n_NoItemsComponentViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<TextPropertyProviderFactoryImpl>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_c_a_TextPropertyProviderFactoryImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DisabledExperimentalLayoutComponent>) GWT.create(Type_factory__o_u_e_l_e_c_i_e_DisabledExperimentalLayoutComponent__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DecisionTableEditorDefinitionEnricher>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_t_d_DecisionTableEditorDefinitionEnricher__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ClientModelValidator>) GWT.create(Type_factory__o_k_w_c_s_c_c_v_ClientModelValidator__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<WorkbenchPreferenceScopeTypes>) GWT.create(Type_factory__o_k_w_c_s_s_p_c_WorkbenchPreferenceScopeTypes__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNDocumentationView>) GWT.create(Type_factory__o_k_w_c_d_c_e_d_DMNDocumentationView__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SaveAndRenameCommandBuilder>) GWT.create(Type_factory__o_u_e_e_c_c_m_c_SaveAndRenameCommandBuilder__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DynamicFormHandlerGenerator>) GWT.create(Type_factory__o_k_w_c_f_d_c_i_i_DynamicFormHandlerGenerator__quals__j_e_i_Any_o_k_w_c_f_d_s_s_DynamicContext.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FormHandlerImpl>) GWT.create(Type_factory__o_k_w_c_f_p_e_h_i_FormHandlerImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ExperimentalFeaturesGroupViewImpl>) GWT.create(Type_factory__o_u_e_c_e_g_ExperimentalFeaturesGroupViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DefaultPreferenceFormActivity>) GWT.create(Type_factory__o_u_e_p_c_c_f_DefaultPreferenceFormActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CharacterEditableColumnGenerator>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_CharacterEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<JSITAssociationDirection>) GWT.create(JsType_factory__o_k_w_c_d_w_k_m_j_m_d_JSITAssociationDirection__quals__Universal.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ContainmentEvaluationHandler>) GWT.create(Type_factory__o_k_w_c_s_c_r_h_i_ContainmentEvaluationHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ExternalPathPlaceRequest>) GWT.create(JsType_factory__o_u_m_i_ExternalPathPlaceRequest__quals__Universal.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<InstanceProvider>) GWT.create(Type_factory__o_j_e_e_c_c_InstanceProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TimePickerView>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimePickerView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<KSessionSelector>) GWT.create(Type_factory__o_k_w_c_w_c_w_KSessionSelector__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CardsGridComponent>) GWT.create(Type_factory__o_k_w_c_w_c_c_CardsGridComponent__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MultiScreenWorkbenchPanelView>) GWT.create(Type_factory__o_u_c_w_p_i_MultiScreenWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ObserverBuilderControl>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_c_b_i_ObserverBuilderControl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_c_c_b_i_Observer.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<NotificationPopupsManagerView>) GWT.create(Type_factory__o_u_c_v_p_n_NotificationPopupsManagerView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<UndefinedExpressionEditorDefinition>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_t_u_UndefinedExpressionEditorDefinition__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<NameFieldRenderer>) GWT.create(Type_factory__o_k_w_c_d_c_p_d_NameFieldRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CopyPopUpPresenter>) GWT.create(Type_factory__o_u_e_e_c_c_f_p_CopyPopUpPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DateRangePicker>) GWT.create(Type_factory__o_u_c_v_p_w_DateRangePicker__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ViewHideAlertsButtonPresenter>) GWT.create(Type_factory__o_g_m_c_c_w_b_ViewHideAlertsButtonPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ViewHideAlertsButtonView>) GWT.create(Type_factory__o_g_m_c_c_w_b_ViewHideAlertsButtonView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeUpdateHandler>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_p_h_DataTypeUpdateHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DeepCloneProcess>) GWT.create(Type_factory__o_k_w_c_s_c_d_c_DeepCloneProcess__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DiscussionWidgetPresenter>) GWT.create(Type_factory__o_k_w_c_w_c_d_DiscussionWidgetPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ForceUnlockEventObserver>) GWT.create(Type_factory__o_u_c_m_ForceUnlockEventObserver__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<FormGenerationNotifier>) GWT.create(Type_factory__o_k_w_c_s_f_c_n_FormGenerationNotifier__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LienzoConnectorProxyView>) GWT.create(Type_factory__o_k_w_c_s_c_l_c_p_LienzoConnectorProxyView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<PanelManagerImpl>) GWT.create(Type_factory__o_u_c_w_PanelManagerImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SelectOptionComponent>) GWT.create(Type_factory__o_u_e_w_c_c_s_SelectOptionComponent__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DynamicBindingHelper>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_r_m_b_DynamicBindingHelper__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CheckBox>) GWT.create(ExtensionProvided_factory__o_g_c_u_CheckBox__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DiagramEditorScreenViewImpl>) GWT.create(Type_factory__o_k_w_c_s_k_c_v_DiagramEditorScreenViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<BaseSelectorView>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_c_BaseSelectorView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SimpleNoExpandWorkbenchPanelPresenter>) GWT.create(Type_factory__o_u_c_w_p_i_SimpleNoExpandWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Tree>) GWT.create(ExtensionProvided_factory__o_u_e_w_c_c_t_Tree__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<SplashScreenMenuPresenter>) GWT.create(Type_factory__o_u_c_m_SplashScreenMenuPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<com.google.gwt.user.client.ui.FlowPanel>) GWT.create(ExtensionProvided_factory__c_g_g_u_c_u_FlowPanel__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ElementCardinalityEvaluationHandler>) GWT.create(Type_factory__o_k_w_c_s_c_r_h_i_ElementCardinalityEvaluationHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DRDNameChangerView>) GWT.create(Type_factory__o_k_w_c_d_c_e_d_DRDNameChangerView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<FileExportScriptInjector>) GWT.create(Type_factory__o_u_e_e_c_c_f_e_j_FileExportScriptInjector__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<BasicShapesFactory>) GWT.create(Type_factory__o_k_w_c_s_s_c_f_BasicShapesFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNGridHelper>) GWT.create(Type_factory__o_k_w_c_d_c_e_s_DMNGridHelper__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<RedoToolbarCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_t_c_RedoToolbarCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<LienzoPanelUtils>) GWT.create(Type_factory__o_k_w_c_s_c_l_u_LienzoPanelUtils__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<WorkbenchActivities>) GWT.create(Type_factory__o_k_w_c_s_s_r_WorkbenchActivities__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PictureInput>) GWT.create(Type_factory__o_k_w_c_f_c_r_c_w_p_PictureInput__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TextEditorView>) GWT.create(Type_factory__o_u_e_w_c_c_e_t_TextEditorView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<KogitoPropertiesDock>) GWT.create(Type_factory__o_k_w_c_d_w_k_c_c_d_KogitoPropertiesDock__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<org.jboss.errai.common.client.dom.TextArea>) GWT.create(ExtensionProvided_factory__o_j_e_c_c_d_TextArea__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<POMDefaultOptions>) GWT.create(Type_factory__o_g_c_s_p_c_u_POMDefaultOptions__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<StringListBoxFieldRenderer>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_s_l_StringListBoxFieldRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MedianVertexLayerPositioning>) GWT.create(Type_factory__o_k_w_c_s_c_g_p_l_s_s_MedianVertexLayerPositioning__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ParameterViewImpl>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_t_f_p_ParameterViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<FieldLabelViewImpl>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_f_l_l_FieldLabelViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<StaticFormHandlerGenerator>) GWT.create(Type_factory__o_k_w_c_f_d_c_i_i_StaticFormHandlerGenerator__quals__j_e_i_Any_o_k_w_c_f_d_s_s_StaticContext.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<KieSelectElement>) GWT.create(Type_factory__o_k_w_c_w_c_w_KieSelectElement__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ContextMenu>) GWT.create(Type_factory__o_k_w_c_d_c_e_c_ContextMenu__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TemplatedWorkbenchPanelView>) GWT.create(Type_factory__o_u_c_w_p_i_TemplatedWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<AuthorizationManagerHelper>) GWT.create(Type_factory__o_u_s_c_a_AuthorizationManagerHelper__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<MonacoFEELVariableSuggestions>) GWT.create(Type_factory__o_k_w_c_d_c_w_c_MonacoFEELVariableSuggestions__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Registry>) GWT.create(Producer_factory__o_a_c_s_r_Registry__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DecimalBoxViewImpl>) GWT.create(Type_factory__o_k_w_c_f_c_r_c_w_d_DecimalBoxViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TimeSelectorView>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_TimeSelectorView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PlaceManager>) GWT.create(JsType_factory__o_u_c_m_PlaceManager__quals__Universal.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ClassUtils>) GWT.create(Type_factory__o_k_w_c_s_c_u_ClassUtils__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<JSITDecisionTableOrientation>) GWT.create(JsType_factory__o_k_w_c_d_w_k_m_j_m_d_JSITDecisionTableOrientation__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MouseRequestLifecycle>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_MouseRequestLifecycle__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CachedRuleManager>) GWT.create(Type_factory__o_k_w_c_s_c_r_CachedRuleManager__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<FormBuildingServiceImpl>) GWT.create(Type_factory__o_k_w_c_f_a_e_s_i_FormBuildingServiceImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<UserMenu>) GWT.create(Type_factory__o_u_c_v_p_m_UserMenu__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<CompassWidgetImpl>) GWT.create(Type_factory__o_u_c_v_p_d_CompassWidgetImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNPerformAutomaticLayoutToolbarCommand>) GWT.create(Type_factory__o_k_w_c_d_c_w_t_DMNPerformAutomaticLayoutToolbarCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNDecisionServiceSVGViewFactoryImpl>) GWT.create(Type_factory__o_k_w_c_d_c_r_DMNDecisionServiceSVGViewFactoryImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ClientFactoryManager>) GWT.create(Type_factory__o_k_w_c_s_c_c_a_ClientFactoryManager__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNCardComponent>) GWT.create(Type_factory__o_k_w_c_d_c_e_i_g_DMNCardComponent__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<RepositoryMenu>) GWT.create(Type_factory__o_k_w_c_w_c_m_RepositoryMenu__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TreeListView>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_i_t_TreeListView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<WorkbenchToolBarPresenter>) GWT.create(Type_factory__o_u_c_w_w_t_WorkbenchToolBarPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<IntegerBoxViewImpl>) GWT.create(Type_factory__o_k_w_c_f_c_r_c_w_i_IntegerBoxViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<BaseEditorEntryPoint>) GWT.create(Type_factory__o_u_e_e_c_c_BaseEditorEntryPoint__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<RegistryAwareCommandManager>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_RegistryAwareCommandManager__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MultipleInput>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_MultipleInput__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<IntegerBoxFieldRenderer>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_IntegerBoxFieldRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<FileExportProducer>) GWT.create(Type_factory__o_u_e_e_c_c_f_e_FileExportProducer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<Position>) GWT.create(JsType_factory__o_u_w_m_Position__quals__Universal.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ExpressionEditorDefinitionsProducer>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_t_ExpressionEditorDefinitionsProducer__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PreferencesRootScreenActivity>) GWT.create(Type_factory__o_u_e_p_c_c_s_PreferencesRootScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNDiagramsSessionState>) GWT.create(Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSessionState__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TreeWalkTraverseProcessorImpl>) GWT.create(Type_factory__o_k_w_c_s_c_g_p_t_t_TreeWalkTraverseProcessorImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<org.kie.workbench.common.dmn.client.canvas.controls.builder.ObserverBuilderControl>) GWT.create(Type_factory__o_k_w_c_d_c_c_c_b_ObserverBuilderControl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor_o_k_w_c_s_c_c_c_c_b_i_Observer.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<IncludedModelsPageState>) GWT.create(Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPageState__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SessionContainer>) GWT.create(ExtensionProvided_factory__o_k_w_c_s_c_w_p_s_i_SessionContainer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CommandRegistryProducer>) GWT.create(Type_factory__o_a_k_b_c_s_r_p_CommandRegistryProducer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ExportToSvgToolbarCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_t_c_ExportToSvgToolbarCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TitleProvider>) GWT.create(JsType_factory__o_u_c_m_LockTarget_TitleProvider__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<SimpleTable>) GWT.create(Type_factory__o_u_e_w_c_c_t_SimpleTable__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<TreeHierarchyInternalItemPresenter>) GWT.create(Type_factory__o_u_e_p_c_c_t_TreeHierarchyInternalItemPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<EnumListBoxFieldRenderer>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_s_l_EnumListBoxFieldRenderer__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<com.google.gwt.user.client.ui.Label>) GWT.create(ExtensionProvided_factory__c_g_g_u_c_u_Label__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DefaultTextPropertyProviderImpl>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_c_a_DefaultTextPropertyProviderImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LayoutEditorPropertiesView>) GWT.create(Type_factory__o_u_e_l_e_c_w_LayoutEditorPropertiesView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNSugiyamaLayoutService>) GWT.create(Type_factory__o_k_w_c_d_c_w_t_DMNSugiyamaLayoutService__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<InlineTextEditorBoxImpl>) GWT.create(Type_factory__o_k_w_c_s_c_w_i_InlineTextEditorBoxImpl__quals__j_e_i_Any_o_k_w_c_s_c_c_c_c_i_InlineTextEditorBox.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<KeyEventHandlerImpl>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_c_k_KeyEventHandlerImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MorphNodeToolboxAction>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_t_a_MorphNodeToolboxAction__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<StunnerFormsHandler>) GWT.create(Type_factory__o_k_w_c_s_f_c_s_StunnerFormsHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DateSelector>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_DateSelector__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<CompassPosition>) GWT.create(JsType_factory__o_u_w_m_CompassPosition__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ExperimentalFeaturesPerspectiveActivity>) GWT.create(Type_factory__o_u_e_c_p_ExperimentalFeaturesPerspectiveActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ActivityManagerImpl>) GWT.create(Type_factory__o_u_c_m_ActivityManagerImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MenuVisitor>) GWT.create(JsType_factory__o_u_w_m_m_MenuVisitor__quals__Universal.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<IncludedModelsFactory>) GWT.create(Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsFactory__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DecisionServiceMoveDividerControl>) GWT.create(Type_factory__o_k_w_c_d_c_c_c_r_DecisionServiceMoveDividerControl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<IncludedModelsPage>) GWT.create(Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPage__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ScreenPanelViewImpl>) GWT.create(Type_factory__o_k_w_c_s_c_w_v_s_ScreenPanelViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PreferenceScopeResolutionStrategyIOCProvider>) GWT.create(Type_factory__o_u_p_c_s_PreferenceScopeResolutionStrategyIOCProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<BooleanEditableColumnGenerator>) GWT.create(Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_BooleanEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ValidationPopupViewImpl>) GWT.create(Type_factory__o_k_w_c_w_c_p_v_ValidationPopupViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MarshallingResponsePopupView>) GWT.create(Type_factory__o_k_w_c_s_c_w_m_MarshallingResponsePopupView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<WorkbenchPreferenceScopeResolutionStrategy>) GWT.create(Type_factory__o_k_w_c_s_s_p_c_WorkbenchPreferenceScopeResolutionStrategy__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PMMLIncludedModelHandler>) GWT.create(Type_factory__o_k_w_c_d_c_e_i_i_p_PMMLIncludedModelHandler__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<QNameFieldProvider>) GWT.create(Type_factory__o_k_w_c_d_c_p_d_QNameFieldProvider__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PerspectiveDropDown>) GWT.create(Type_factory__o_u_e_w_c_c_d_PerspectiveDropDown__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<EdgeBuilderControlImpl>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_c_b_i_EdgeBuilderControlImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNEditorMenuSessionItems>) GWT.create(Type_factory__o_k_w_c_d_w_k_c_c_e_DMNEditorMenuSessionItems__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<IncludedModelsContext>) GWT.create(Type_factory__o_k_w_c_d_c_e_i_c_IncludedModelsContext__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<NewResourcesMenu>) GWT.create(Type_factory__o_k_w_c_w_c_h_NewResourcesMenu__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DMNCardsEmptyStateView>) GWT.create(Type_factory__o_k_w_c_d_c_e_i_g_e_DMNCardsEmptyStateView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<ListEditorDefinition>) GWT.create(Type_factory__o_k_w_c_d_c_e_e_t_l_ListEditorDefinition__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNDefinitionSetModelFactoryImpl>) GWT.create(Type_factory__o_k_w_c_d_a_d_f_DMNDefinitionSetModelFactoryImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<PreferencesCentralActionsView>) GWT.create(Type_factory__o_u_e_p_c_c_a_PreferencesCentralActionsView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypesPage>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_DataTypesPage__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<AppendDecisionShortcut>) GWT.create(Type_factory__o_k_w_c_d_c_c_c_k_s_AppendDecisionShortcut__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<PreferencesCentralPerspective>) GWT.create(Type_factory__o_u_e_p_c_c_PreferencesCentralPerspective__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<HTMLParagraphElement>) GWT.create(ExtensionProvided_factory__e_d_HTMLParagraphElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ConflictingRepositoriesPopupViewImpl>) GWT.create(Type_factory__o_g_c_s_p_c_r_ConflictingRepositoriesPopupViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<DataTypeConstraintEnumerationView>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_e_DataTypeConstraintEnumerationView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<KogitoKeyEventHandlerImpl>) GWT.create(Type_factory__o_k_w_c_s_k_r_c_s_c_i_KogitoKeyEventHandlerImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<Decision>) GWT.create(Type_factory__o_g_c_s_p_c_Decision__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<GroupContextMenuItemView>) GWT.create(Type_factory__o_u_c_v_p_m_m_c_GroupContextMenuItemView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<IFrameScreenPresenter>) GWT.create(Type_factory__o_u_e_w_c_c_s_i_IFrameScreenPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ModalFormDisplayerViewImpl>) GWT.create(Type_factory__o_k_w_c_f_c_c_c_f_m_ModalFormDisplayerViewImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<LayoutEditorPropertiesPresenter>) GWT.create(Type_factory__o_u_e_l_e_c_w_LayoutEditorPropertiesPresenter__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<org.jboss.errai.common.client.dom.Button>) GWT.create(ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LayoutElementPropertiesView>) GWT.create(Type_factory__o_u_e_l_e_c_w_LayoutElementPropertiesView__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<MousePanMediatorControlImpl>) GWT.create(Type_factory__o_k_w_c_d_c_w_l_MousePanMediatorControlImpl__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ChangeProfileDevCommand>) GWT.create(Type_factory__o_k_w_c_s_c_w_m_d_i_ChangeProfileDevCommand__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<CanvasFileExport>) GWT.create(Type_factory__o_k_w_c_s_c_c_c_u_CanvasFileExport__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<JSITFunctionKind>) GWT.create(JsType_factory__o_k_w_c_d_w_k_m_j_m_d_JSITFunctionKind__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<ConstraintPlaceholderHelper>) GWT.create(Type_factory__o_k_w_c_d_c_e_t_l_c_c_ConstraintPlaceholderHelper__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNDeepCloneProcess>) GWT.create(Type_factory__o_k_w_c_d_c_c_c_DMNDeepCloneProcess__quals__j_e_i_Any_j_e_i_Default.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<JSITBuiltinAggregator>) GWT.create(JsType_factory__o_k_w_c_d_w_k_m_j_m_d_JSITBuiltinAggregator__quals__Universal.class));
    org_jboss_errai_ioc_client_container_DependentScopeContext_context.registerFactory((Factory<LayoutPanelView>) GWT.create(Type_factory__o_u_c_w_p_i_LayoutPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named.class));
    org_jboss_errai_ioc_client_container_ApplicationScopedContext_context.registerFactory((Factory<DMNDefinitionSetDefinitionAdapterImpl>) GWT.create(Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetDefinitionAdapterImpl__quals__j_e_i_Any_j_e_i_Default.class));
  }

  private void registerFactories(final Context org_jboss_errai_ioc_client_container_ApplicationScopedContext_context, final Context org_jboss_errai_ioc_client_container_DependentScopeContext_context) {
    registerFactories0(org_jboss_errai_ioc_client_container_ApplicationScopedContext_context, org_jboss_errai_ioc_client_container_DependentScopeContext_context);
    registerFactories1(org_jboss_errai_ioc_client_container_ApplicationScopedContext_context, org_jboss_errai_ioc_client_container_DependentScopeContext_context);
    registerFactories2(org_jboss_errai_ioc_client_container_ApplicationScopedContext_context, org_jboss_errai_ioc_client_container_DependentScopeContext_context);
  }

  // The main IOC bootstrap method.
  public ContextManager bootstrapContainer() {
    final Context org_jboss_errai_ioc_client_container_ApplicationScopedContext_context = new ApplicationScopedContext();
    final Context org_jboss_errai_ioc_client_container_DependentScopeContext_context = new DependentScopeContext();
    logger.debug("Registering factories with contexts.");
    long start = System.currentTimeMillis();
    registerFactories(org_jboss_errai_ioc_client_container_ApplicationScopedContext_context, org_jboss_errai_ioc_client_container_DependentScopeContext_context);
    logger.debug("Registered 1432 factories in {}ms", System.currentTimeMillis() - start);
    logger.debug("Adding contexts to context manager...");
    start = System.currentTimeMillis();
    contextManager.addContext(org_jboss_errai_ioc_client_container_ApplicationScopedContext_context);
    contextManager.addContext(org_jboss_errai_ioc_client_container_DependentScopeContext_context);
    logger.debug("Added 2 contexts in {}ms", System.currentTimeMillis() - start);
    logger.debug("Calling finishInit on ContextManager");
    start = System.currentTimeMillis();
    contextManager.finishInit();
    logger.debug("ContextManager#finishInit ran in {}ms", System.currentTimeMillis() - start);
    return contextManager;
  }
}