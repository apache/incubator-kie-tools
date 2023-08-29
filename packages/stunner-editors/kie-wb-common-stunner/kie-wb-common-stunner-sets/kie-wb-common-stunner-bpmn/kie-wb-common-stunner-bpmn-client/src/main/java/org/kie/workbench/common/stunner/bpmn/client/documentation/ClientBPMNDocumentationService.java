/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.documentation;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.bpmn.client.components.palette.BPMNCategoryDefinitionProvider;
import org.kie.workbench.common.stunner.bpmn.client.documentation.decorator.PropertyDecorator;
import org.kie.workbench.common.stunner.bpmn.client.documentation.decorator.PropertyDecorators;
import org.kie.workbench.common.stunner.bpmn.client.documentation.template.BPMNDocumentationTemplateSource;
import org.kie.workbench.common.stunner.bpmn.client.shape.factory.BPMNShapeFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNCategories;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BgColor;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BorderColor;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BorderSize;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.AdHoc;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Executable;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.GlobalVariables;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Id;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Package;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Version;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.ImportsValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Height;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Radius;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Width;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontBorderColor;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontBorderSize;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontColor;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontFamily;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSize;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.Currency;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.DistributionType;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.Max;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.Mean;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.Min;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.Quantity;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.StandardDeviation;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.TimeUnit;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.UnitCost;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.WorkingHours;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.DecisionName;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.DmnModelName;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsMultipleInstance;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Namespace;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.BaseProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.BaseProcessVariables;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.HasProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariableSerializer;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.RootProcessAdvancedData;
import org.kie.workbench.common.stunner.bpmn.documentation.BPMNDocumentationService;
import org.kie.workbench.common.stunner.bpmn.documentation.model.BPMNDocumentation;
import org.kie.workbench.common.stunner.bpmn.documentation.model.element.Element;
import org.kie.workbench.common.stunner.bpmn.documentation.model.element.ElementDetails;
import org.kie.workbench.common.stunner.bpmn.documentation.model.element.ElementTotal;
import org.kie.workbench.common.stunner.bpmn.documentation.model.general.General;
import org.kie.workbench.common.stunner.bpmn.documentation.model.general.Imports;
import org.kie.workbench.common.stunner.bpmn.documentation.model.general.ProcessOverview;
import org.kie.workbench.common.stunner.bpmn.documentation.model.general.ProcessVariablesTotal;
import org.kie.workbench.common.stunner.bpmn.workitem.CustomTask;
import org.kie.workbench.common.stunner.bpmn.workitem.IconDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.DOMGlyphRenderers;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.shape.ImageStripGlyph;
import org.kie.workbench.common.stunner.core.client.util.js.JsConverter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.documentation.model.DocumentationOutput;
import org.kie.workbench.common.stunner.core.documentation.model.HTMLDocumentationTemplate;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.uberfire.ext.editor.commons.client.template.mustache.ClientMustacheTemplateRenderer;

@Dependent
public class ClientBPMNDocumentationService implements BPMNDocumentationService {

    private static final Logger LOGGER = Logger.getLogger(ClientBPMNDocumentationService.class.getName());

    private static final Map<String, Boolean> ignoredPropertiesIds = buildIgnoredPropertiesIds();
    public static final int ICON_HEIGHT = 20;
    public static final int ICON_WIDTH = 20;

    private final ClientMustacheTemplateRenderer mustacheTemplateRenderer;
    private final DefinitionManager definitionManager;
    private final DefinitionUtils definitionUtils;
    private final BPMNShapeFactory shapeFactory;
    private final CanvasFileExport canvasFileExport;
    private final SessionManager sessionManager;
    private final BPMNCategoryDefinitionProvider categoryDefinitionProvider;
    private final DOMGlyphRenderers glyphRenderer;
    private final ClientTranslationService translationService;
    private final ManagedInstance<WorkItemDefinitionRegistry> workItemDefinitionRegistry;
    protected final DefinitionHelper definitionHelper;
    private final PropertyDecorators propertyDecorators;

    @Inject
    public ClientBPMNDocumentationService(final ClientMustacheTemplateRenderer mustacheTemplateRenderer,
                                          final DefinitionManager definitionManager,
                                          final DefinitionUtils definitionUtils,
                                          final BPMNShapeFactory shapeFactory,
                                          final CanvasFileExport canvasFileExport,
                                          final SessionManager sessionManager,
                                          final BPMNCategoryDefinitionProvider categoryDefinitionProvider,
                                          final DOMGlyphRenderers glyphRenderer,
                                          final ClientTranslationService translationService,
                                          final @Default ManagedInstance<WorkItemDefinitionRegistry> workItemDefinitionRegistry,
                                          final PropertyDecorators propertyDecorators

    ) {
        this.mustacheTemplateRenderer = mustacheTemplateRenderer;
        this.definitionManager = definitionManager;
        this.definitionUtils = definitionUtils;
        this.shapeFactory = shapeFactory;
        this.canvasFileExport = canvasFileExport;
        this.sessionManager = sessionManager;
        this.categoryDefinitionProvider = categoryDefinitionProvider;
        this.glyphRenderer = glyphRenderer;
        this.translationService = translationService;
        this.workItemDefinitionRegistry = workItemDefinitionRegistry;
        this.propertyDecorators = propertyDecorators;
        definitionHelper = new DefinitionHelper();
    }

    @Override
    public BPMNDocumentation processDocumentation(Diagram diagram) {
        final Graph<?, Node> graph = diagram.getGraph();

        final Optional<BPMNDiagramImpl> diagramModel = getDiagramModel(graph).findFirst();

        return BPMNDocumentation.create(getProcessOverview(diagramModel, graph),
                                        getElementsDetails(graph),
                                        getDiagramImage(Optional.ofNullable(sessionManager.getCurrentSession())
                                                                .map(s -> ((ClientSession) s).getCanvasHandler())
                                                                .filter(c -> c instanceof AbstractCanvasHandler)
                                                                .map(c -> (AbstractCanvasHandler) c)));
    }

    private Stream<BPMNDiagramImpl> getDiagramModel(Graph<?, Node> graph) {
        return StreamSupport.stream(graph.nodes().spliterator(), false)
                .map(Node::getContent)
                .filter(c -> c instanceof Definition)
                .map(c -> (Definition) c)
                .map(Definition::getDefinition)
                .filter(d -> d instanceof BPMNDiagramImpl)
                .map(d -> (BPMNDiagramImpl) d);
    }

    private ProcessOverview getProcessOverview(final Optional<BPMNDiagramImpl> diagramModel,
                                               final Graph<?, Node> graph) {
        return ProcessOverview.create(getGeneral(diagramModel), getAllImports(graph), getAllProcessVariables(graph));
    }

    private General getGeneral(Optional<BPMNDiagramImpl> diagramModel) {
        final Optional<DiagramSet> diagramSet = diagramModel
                .map(BPMNDiagram::getDiagramSet);

        final String documentation = diagramSet
                .map(DiagramSet::getDocumentation)
                .map(Documentation::getValue)
                .map(this::encodeLineBreak)
                .orElse(null);

        final String version = diagramSet
                .map(DiagramSet::getVersion)
                .map(Version::getValue)
                .orElse(null);

        final String pkg = diagramSet
                .map(DiagramSet::getPackageProperty)
                .map(Package::getValue)
                .orElse(null);

        final String adhoc = diagramSet
                .map(DiagramSet::getAdHoc)
                .map(AdHoc::getValue)
                .map(String::valueOf)
                .orElse(null);

        final String executable = diagramSet
                .map(DiagramSet::getExecutable)
                .map(Executable::getValue)
                .map(String::valueOf)
                .orElse(null);

        final String id = diagramSet
                .map(DiagramSet::getId)
                .map(Id::getValue)
                .orElse(null);

        final String name = diagramSet
                .map(DiagramSet::getName)
                .map(Name::getValue)
                .orElse(null);

        final String description = diagramSet
                .map(DiagramSet::getProcessInstanceDescription)
                .map(d -> d.getValue())
                .map(this::encodeLineBreak)
                .orElse(null);

        return new General.Builder()
                .id(id)
                .name(name)
                .isAdhoc(adhoc)
                .isExecutable(executable)
                .documentation(documentation)
                .version(version)
                .pkg(pkg)
                .description(description)
                .build();
    }

    private Imports getAllImports(final Graph<?, Node> graph) {
        final List<Imports.DefaultImport> defaultImports = getDiagramModel(graph)
                .map(BPMNDiagramImpl::getDiagramSet)
                .map(DiagramSet::getImports)
                .filter(Objects::nonNull)
                .map(org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.Imports::getValue)
                .map(ImportsValue::getDefaultImports)
                .flatMap(Collection::stream)
                .map(DefaultImport::getClassName)
                .map(Imports.DefaultImport::create)
                .collect(Collectors.toList());

        final List<Imports.WSDLImport> wsdlImports = getDiagramModel(graph)
                .map(BPMNDiagram::getDiagramSet)
                .map(DiagramSet::getImports)
                .filter(Objects::nonNull)
                .map(org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.Imports::getValue)
                .map(ImportsValue::getWSDLImports)
                .flatMap(Collection::stream)
                .map(wsdlImport -> Imports.WSDLImport.create(wsdlImport.getLocation(), wsdlImport.getNamespace()))
                .collect(Collectors.toList());

        return Imports.create(defaultImports, wsdlImports);
    }

    private ProcessVariablesTotal getAllProcessVariables(final Graph<?, Node> graph) {
        //Computing Global, Process, Sub-Processes variables
        final List<Map.Entry> variables = Stream.concat(
                getDiagramModel(graph)
                        .map(BPMNDiagram::getAdvancedData)
                        .map(RootProcessAdvancedData::getGlobalVariables)
                        .map(GlobalVariables::getValue),
                StreamSupport.stream(graph.nodes().spliterator(), false)
                        .map(Node::getContent)
                        .filter(c -> c instanceof Definition)
                        .map(c -> (Definition) c)
                        .map(Definition::getDefinition)
                        .filter(o -> o instanceof HasProcessData)
                        .map(o -> (HasProcessData<BaseProcessData>) o)
                        .map(HasProcessData::getProcessData)
                        .map(BaseProcessData::getProcessVariables)
                        .map(BaseProcessVariables::getValue))
                .map(ProcessVariableSerializer::deserialize)
                .flatMap(v -> v.entrySet().stream())
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .collect(Collectors.toList());

        return ProcessVariablesTotal.create(variables.size(), variables.size(), JsConverter.fromEntries(variables));
    }

    @SuppressWarnings("all")
    private ElementDetails getElementsDetails(final Graph<?, Node> graph) {
        final List<ElementTotal> elementsTotals = StreamSupport.stream(graph.nodes().spliterator(), false)
                .map(Node::getContent)
                .filter(c -> c instanceof Definition)
                .map(c -> (Definition) c)
                .map(Definition::getDefinition)
                .filter(e -> !(e instanceof BPMNDiagram))
                .map(this::processElement)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Element::getTitle))
                .collect(Collectors.groupingBy(Element::getType))
                .entrySet()
                .stream()
                .map(entry -> ElementTotal.create(entry.getValue(),
                                                  getCategoryName(entry.getKey()),
                                                  getCategoryIcon(entry.getKey())))
                .sorted(Comparator.comparing(ElementTotal::getType))
                .collect(Collectors.toList());

        return ElementDetails.create(elementsTotals);
    }

    private Element processElement(Object def) {
        String name = getElementName(def);
        try {
            return Element.create(name,
                                  getElementCategory(def),
                                  getElementTitle(def),
                                  getDefinitionIcon(def),
                                  getElementProperties(def));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing documentation properties for [" + name + "]", e);
            return null;
        }
    }

    private String getElementTitle(Object def) {
        final DefinitionId definitionId = definitionManager.adapters().forDefinition().getId(def);
        return Optional.ofNullable(definitionId)
                .map(DefinitionId::value)
                .map(definitionUtils::getTitle)
                .orElse("");
    }

    private String getElementName(Object def) {
        return definitionUtils.getName(def);
    }

    private String getElementCategory(Object def) {
        return definitionHelper.getDefinitionCategory(def).orElse("");
    }

    private String getCategoryName(String category) {
        return Optional.ofNullable(translationService.getValue(BPMNCategories.class.getName() + "." + category))
                .filter(StringUtils::nonEmpty)
                .orElse(category);
    }

    @Override
    public HTMLDocumentationTemplate getDocumentationTemplate() {
        final BPMNDocumentationTemplateSource source = GWT.create(BPMNDocumentationTemplateSource.class);
        return new HTMLDocumentationTemplate(source.documentationTemplate().getText());
    }

    @Override
    public DocumentationOutput buildDocumentation(HTMLDocumentationTemplate template, BPMNDocumentation diagramDocumentation) {
        final String rendered = mustacheTemplateRenderer.render(template.getTemplate(), diagramDocumentation);
        return new DocumentationOutput(rendered);
    }

    @Override
    public DocumentationOutput generate(Diagram diagram) {
        return Optional.ofNullable(diagram)
                .map(this::processDocumentation)
                .map(d -> buildDocumentation(getDocumentationTemplate(), d))
                .orElse(DocumentationOutput.EMPTY);
    }

    private String getCategoryIcon(String category) {
        return definitionHelper.getCategoryIcon(category);
    }

    private String getDefinitionIcon(Object definition) {
        return definitionHelper.getDefinitionIcon(definition).orElse("");
    }

    private Map<String, String> getElementProperties(Object definition) {
        final DefinitionAdapter<Object> definitionAdapter = definitionManager.adapters().registry().getDefinitionAdapter(definition.getClass());
        final PropertyAdapter<Object, Object> propertyAdapter = definitionManager.adapters().forProperty();
        return Arrays.stream(definitionAdapter.getPropertyFields(definition))
                .map(field -> definitionAdapter.getProperty(definition, field))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(prop -> !ignoredPropertiesIds.containsKey(propertyAdapter.getId(prop)))
                .filter(prop -> StringUtils.nonEmpty(propertyAdapter.getCaption(prop)))
                .filter(prop -> Objects.nonNull(propertyAdapter.getValue(prop)))
                .collect(Collectors.toMap(propertyAdapter::getCaption, this::getElementValue));
    }

    private String getElementValue(Object prop) {
        return Optional.ofNullable(
                propertyDecorators.getDecorator(prop)
                        .map(PropertyDecorator::getValue)
                        .orElse(String.valueOf(definitionManager.adapters().forProperty().getValue(prop))))
                .map(this::encodeLineBreak)
                .orElse(null);
    }

    /**
     * Basically replace all \n with <br> html tag.
     *
     * @param input
     * @return
     */
    private String encodeLineBreak(String input) {
        return Optional.ofNullable(input)
                .map(str -> str.replaceAll("(\r\n|\n)", "<br/>"))
                .orElse(input);
    }

    private String getDiagramImage(Optional<AbstractCanvasHandler> canvasHandler) {
        return canvasHandler.map(canvasFileExport::exportToSvg).orElse(null);
    }

    /**
     * Properties that should be ignored on the documentation
     *
     * @return
     */
    private static Map<String, Boolean> buildIgnoredPropertiesIds() {
        return Stream.of(BindableAdapterUtils.getPropertyId(FontColor.class),
                         BindableAdapterUtils.getPropertyId(FontBorderColor.class),
                         BindableAdapterUtils.getPropertyId(FontBorderSize.class),
                         BindableAdapterUtils.getPropertyId(FontFamily.class),
                         BindableAdapterUtils.getPropertyId(FontSize.class),
                         BindableAdapterUtils.getPropertyId(BgColor.class),
                         BindableAdapterUtils.getPropertyId(BorderColor.class),
                         BindableAdapterUtils.getPropertyId(BorderSize.class),
                         BindableAdapterUtils.getPropertyId(Radius.class),
                         BindableAdapterUtils.getPropertyId(Height.class),
                         BindableAdapterUtils.getPropertyId(Width.class),
                         BindableAdapterUtils.getPropertyId(ProcessVariables.class),
                         BindableAdapterUtils.getPropertyId(DmnModelName.class),
                         BindableAdapterUtils.getPropertyId(DecisionName.class),
                         BindableAdapterUtils.getPropertyId(Namespace.class),
                         BindableAdapterUtils.getPropertyId(IsMultipleInstance.class),
                         BindableAdapterUtils.getPropertyId(Min.class),
                         BindableAdapterUtils.getPropertyId(Max.class),
                         BindableAdapterUtils.getPropertyId(Mean.class),
                         BindableAdapterUtils.getPropertyId(Currency.class),
                         BindableAdapterUtils.getPropertyId(DistributionType.class),
                         BindableAdapterUtils.getPropertyId(Quantity.class),
                         BindableAdapterUtils.getPropertyId(StandardDeviation.class),
                         BindableAdapterUtils.getPropertyId(TimeUnit.class),
                         BindableAdapterUtils.getPropertyId(UnitCost.class),
                         BindableAdapterUtils.getPropertyId(WorkingHours.class))
                .collect(Collectors.toMap(id -> id, id -> Boolean.TRUE));
    }

    protected class DefinitionHelper {

        final Map<Class, Function<Object, Optional<String>>> iconFactory;
        final Map<Class, Function<Object, Optional<String>>> categoryFactory;

        private DefinitionHelper() {

            iconFactory = Stream.of(
                    new AbstractMap.SimpleEntry<Class, Function<Object, Optional<String>>>(CustomTask.class, def -> getServiceTaskIcon(def)))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            categoryFactory = Stream.of(
                            new AbstractMap.SimpleEntry<Class, Function<Object, Optional<String>>>(CustomTask.class, def -> getServiceTaskCategory(def)))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        public Optional<String> getDefinitionIcon(Object definition) {
            return Optional.ofNullable(definition)
                    .map(Object::getClass)
                    .map(iconFactory::get)
                    .filter(Objects::nonNull)
                    .map(factory -> factory.apply(definition))
                    .filter(Objects::nonNull)
                    .orElseGet(() -> getDefaultDefinitionIcon(definition));
        }

        private Optional<String> getDefaultDefinitionIcon(Object definition) {
            final DefinitionId definitionId = definitionManager.adapters().forDefinition().getId(definition);
            return Optional.ofNullable(definitionId)
                    .map(DefinitionId::value)
                    .map(shapeFactory::getGlyph)
                    .filter(glyphImg -> glyphImg instanceof ImageStripGlyph)
                    .map(glyphImg -> (ImageStripGlyph) glyphImg)
                    .map(glyphImg -> glyphRenderer.render(glyphImg, ICON_WIDTH, ICON_HEIGHT))
                    .map(IsElement::getElement)
                    .map(HTMLElement::getInnerHTML);
        }

        private Optional<String> getServiceTaskIcon(Object definition) {
            return Optional.ofNullable(definition)
                    .filter(def -> def instanceof CustomTask)
                    .map(def -> (CustomTask) def)
                    .map(org.kie.workbench.common.stunner.bpmn.workitem.CustomTask::getName)
                    .map(name -> Optional.ofNullable(workItemDefinitionRegistry
                                                             .get()
                                                             .get(name))
                            .map(WorkItemDefinition::getIconDefinition)
                            .map(IconDefinition::getIconData).orElse(null))
                    .map(data -> createImageTag(data));
        }

        public Optional<String> getDefinitionCategory(Object definition) {
            return Optional.ofNullable(definition)
                    .map(Object::getClass)
                    .map(categoryFactory::get)
                    .filter(Objects::nonNull)
                    .map(factory -> factory.apply(definition))
                    .filter(Objects::nonNull)
                    .orElseGet(() -> getDefaultDefinitionCategory(definition));
        }

        private Optional<String> getServiceTaskCategory(Object definition) {
            return Optional.ofNullable(definition)
                    .filter(def -> def instanceof CustomTask)
                    .map(def -> (CustomTask) def)
                    .map(org.kie.workbench.common.stunner.bpmn.workitem.CustomTask::getName)
                    .map(name -> Optional.ofNullable(workItemDefinitionRegistry
                                                             .get()
                                                             .get(name))
                            .map(WorkItemDefinition::getCategory)
                            .orElse(null));
        }

        private Optional<String> getDefaultDefinitionCategory(Object definition) {
            return Optional.ofNullable(definitionManager.adapters().forDefinition().getCategory(definition));
        }

        public String getCategoryIcon(String category) {
            return Optional.ofNullable(categoryDefinitionProvider.glyphProvider().apply(category))
                    .map(glyph -> glyphRenderer.render(glyph, ICON_WIDTH, ICON_HEIGHT))
                    .map(IsElement::getElement)
                    .map(HTMLElement::getInnerHTML)
                    //try to get the service task icon if category icon is not found
                    .orElseGet(() -> Optional.ofNullable(category)
                            .map(name -> workItemDefinitionRegistry
                                    .get()
                                    .items()
                                    .stream()
                                    .filter(wid -> Objects.equals(wid.getCategory(), category))
                                    .findFirst()
                                    .orElse(null))
                            .filter(Objects::nonNull)
                            .map(WorkItemDefinition::getIconDefinition)
                            .map(IconDefinition::getIconData)
                            .map(this::createImageTag).orElse(""));
        }

        protected String createImageTag(String data) {
            return "<div style=\"width:" + ICON_WIDTH + "px; height: " + ICON_HEIGHT + "px\">" +
                    "<img src=\"" + data + "\" style=\"max-width: 100%; max-height: 100%\">" +
                    "</div>";
        }
    }
}
