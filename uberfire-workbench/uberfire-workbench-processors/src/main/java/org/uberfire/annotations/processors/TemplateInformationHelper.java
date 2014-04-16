package org.uberfire.annotations.processors;

import static org.uberfire.annotations.processors.GeneratorUtils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleAnnotationValueVisitor6;

import org.uberfire.annotations.processors.exceptions.GenerationException;
import org.uberfire.annotations.processors.facades.ClientAPIModule;

public class TemplateInformationHelper {

    public static final String VALUE = "value";
    public static final String PANEL_TYPE = "panelType";
    public static final String IS_DEFAULT = "isDefault";
    public static final String DEFAULT_PANEL_TYPE = "TEMPLATE";
    public static final String PART = "part";
    public static final String PARAMETERS = "parameters";
    public static final String PARAMETERS_NAME_PARAM = "name";
    public static final String PARAMETERS_VAL_PARAM = "val";

    public static TemplateInformation extractWbTemplatePerspectiveInformation( Elements elementUtils, TypeElement classElement ) throws GenerationException {

        TemplateInformation template = new TemplateInformation();

        for ( Element element : classElement.getEnclosedElements() ) {
            if ( isAWorkbenchPanel( elementUtils, element ) ) {
                extractInformationFromWorkbenchPanel( elementUtils, template, element );
            } else if ( onlyWorkbenchPartWithoutPanel( elementUtils, element ) ) {
                extractInformationFromGeneratedWorkbenchPanel( elementUtils, template, element );
            }

        }
        if ( template.thereIsTemplateFields() ) {
            return template;
        }
        throw new GenerationException( "The Template WorkbenchPerspective must provide a @WorkbenchPanel or @WorkbenchPart annotated field." );
    }

    private static boolean onlyWorkbenchPartWithoutPanel( Elements elementUtils, Element element ) {
        boolean ufPart = getAnnotation( elementUtils, element, ClientAPIModule.getWorkbenchPart() ) != null;
        boolean ufParts = getAnnotation( elementUtils, element, ClientAPIModule.getWorkbenchParts() ) != null;
        return !isAWorkbenchPanel( elementUtils, element ) && ( ufPart || ufParts );
    }

    private static boolean isAWorkbenchPanel( Elements elementUtils, Element element ) {
        return getAnnotation( elementUtils, element, ClientAPIModule.getWorkbenchPanel() ) != null;
    }

    private static void extractInformationFromWorkbenchPanel( Elements elementUtils,
                                                              TemplateInformation template,
                                                              Element element ) throws GenerationException {
        WorkbenchPanelInformation wbPanel = new WorkbenchPanelInformation();
        if ( workbenchPanelIsDefault( elementUtils, element ) ) {
            wbPanel.setDefault( true );
        }
        wbPanel.setFieldName( element.getSimpleName().toString() );
        wbPanel.setWbParts( getWorkbenchPartsFrom( elementUtils, element ) );
        wbPanel.setPanelType( extractPanelType( elementUtils, element ) );
        if ( wbPanel.isDefault() ) {
            if ( shouldHaveOnlyOneDefaultPanel( template ) ) {
                throw new GenerationException( "The Template WorkbenchPerspective must provide only one @WorkbenchPanel annotated field." );
            }
            template.setDefaultPanel( wbPanel );
        } else {
            template.addTemplateField( wbPanel );
        }
    }

    private static void extractInformationFromGeneratedWorkbenchPanel( Elements elementUtils,
                                                                       TemplateInformation template,
                                                                       Element element ) throws GenerationException {
        WorkbenchPanelInformation generatedWbPanel = new WorkbenchPanelInformation();

        generatedWbPanel.setFieldName( element.getSimpleName().toString() );
        generatedWbPanel.setWbParts( getWorkbenchPartsFrom( elementUtils, element ) );
        generatedWbPanel.setPanelType( DEFAULT_PANEL_TYPE );
        template.addTemplateField( generatedWbPanel );
    }

    private static boolean shouldHaveOnlyOneDefaultPanel( TemplateInformation template ) {
        return template.getDefaultPanel() != null;
    }

    private static String extractPanelType( Elements elementUtils, Element element ) throws GenerationException {
        AnnotationMirror am = getAnnotation( elementUtils, element, ClientAPIModule.getWorkbenchPanel() );
        return extractAnnotationStringValue( elementUtils, am, PANEL_TYPE );
    }

    private static boolean workbenchPanelIsDefault( Elements elementUtils, Element element ) throws GenerationException {
        AnnotationMirror am = getAnnotation( elementUtils, element, ClientAPIModule.getWorkbenchPanel() );
        return Boolean.valueOf( extractAnnotationStringValue( elementUtils, am, IS_DEFAULT ) );
    }

    private static List<PartInformation> getWorkbenchPartsFrom( Elements elementUtils, Element wbPanel ) throws GenerationException {
        List<PartInformation> parts = new ArrayList<PartInformation>();
        if ( thereIsWbParts( elementUtils, wbPanel ) ) {
            extractWbPartFromWbParts( elementUtils, wbPanel, parts );
        } else {
            AnnotationMirror wbPartAnnotation = getAnnotation( elementUtils, wbPanel, ClientAPIModule.getWorkbenchPart() );
            String partName = extractAnnotationStringValue( elementUtils, wbPartAnnotation, PART );
            Map<String, String> parameters = extractParametersFromPart( elementUtils, wbPartAnnotation );
            parts.add( new PartInformation( partName, parameters ) );
        }
        return parts;
    }

    private static boolean thereIsWbParts( Elements elementUtils, Element element ) {
        if ( getAnnotation( elementUtils, element, ClientAPIModule.getWorkbenchParts() ) != null ) {
            return true;
        }
        return false;
    }

    private static AnnotationValue extractAnnotationPropertyValue( Elements elementUtils,
                                                          AnnotationMirror annotation,
                                                          CharSequence annotationProperty ) {

        Map<? extends ExecutableElement, ? extends AnnotationValue> annotationParams =
                elementUtils.getElementValuesWithDefaults( annotation );

        for ( Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> param : annotationParams.entrySet() ) {
            if (param.getKey().getSimpleName().contentEquals( annotationProperty )) {
                return param.getValue();
            }
        }
        return null;
    }

    private static void extractWbPartFromWbParts( Elements elementUtils,
                                                  Element ufPanel,
                                                  List<PartInformation> parts ) {
        List<AnnotationMirror> annotations = extractAnnotationsFromAnnotation( elementUtils, ufPanel, ClientAPIModule.getWorkbenchParts(), VALUE );
        for ( AnnotationMirror annotation : annotations ) {
            final AnnotationValue av = extractAnnotationPropertyValue( elementUtils, annotation, PART );
            String partName = av.getValue().toString();
            parts.add( new PartInformation( partName ) );
        }
    }

    private static Map<String, String> extractParametersFromPart( final Elements elementUtils,
                                                                  final AnnotationMirror wbPartAnnotation ) {
        final Map<String, String> map = new TreeMap<String, String>();
        AnnotationValue mappingAnnotations = extractAnnotationPropertyValue( elementUtils, wbPartAnnotation, PARAMETERS );
        mappingAnnotations.accept( new SimpleAnnotationValueVisitor6<Void, Void>() {
            @Override
            public Void visitArray( List<? extends AnnotationValue> vals, Void p ) {
                for ( AnnotationValue val : vals ) {
                    val.accept( new SimpleAnnotationValueVisitor6<Void, Void>() {
                        @Override
                        public Void visitAnnotation( AnnotationMirror a, Void p ) {
                            map.put( extractAnnotationStringValue( elementUtils, a, PARAMETERS_NAME_PARAM ),
                                     extractAnnotationStringValue( elementUtils, a, PARAMETERS_VAL_PARAM ) );
                            return null;
                        }
                    }, null );
                }
                return null;
            }
        }, null);
        return map;
    }

    private static String extractAnnotationStringValue( Elements elementUtils, AnnotationMirror annotation, CharSequence paramName ) {
        final AnnotationValue av = extractAnnotationPropertyValue( elementUtils, annotation, paramName );
        if ( av != null && av.getValue() != null ) {
            return av.getValue().toString();
        }
        return null;
    }

    private static List<AnnotationMirror> extractAnnotationsFromAnnotation( Elements elementUtils,
                                                                        Element element,
                                                                        String annotationName,
                                                                        String methodName ) {
        final AnnotationMirror am = getAnnotation( elementUtils, element, annotationName );
        AnnotationValue nestedAnnotations = extractAnnotationPropertyValue( elementUtils, am, methodName );
        if ( nestedAnnotations == null ) {
            return Collections.emptyList();
        }
        final List<AnnotationMirror> result = new ArrayList<AnnotationMirror>();
        nestedAnnotations.accept( new SimpleAnnotationValueVisitor6<Void, Void>() {
            @Override
            public Void visitArray( List<? extends AnnotationValue> vals, Void x ) {
                for ( AnnotationValue av : vals ) {
                    av.accept( new SimpleAnnotationValueVisitor6<Void, Void>() {
                        @Override
                        public Void visitAnnotation( AnnotationMirror am, Void x ) {
                            result.add( am );
                            return null;
                        }
                    }, null );
                }
                return null;
            }
        }, null );
        return result;
    }

}
