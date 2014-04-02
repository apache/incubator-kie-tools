package org.uberfire.annotations.processors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.uberfire.annotations.processors.exceptions.GenerationException;
import org.uberfire.annotations.processors.facades.ClientAPIModule;

public class TemplateInformationHelper {

    public static final String VALUE = "value";
    public static final String PANEL_TYPE = "panelType";
    public static final String IS_DEFAULT = "isDefault";
    public static final String DEFAULT_PANEL_TYPE = "TEMPLATE";
    public static final String PART = "part";
    public static final String PARAMETERS = "parameters";

    public static TemplateInformation extractWbTemplatePerspectiveInformation( TypeElement classElement ) throws GenerationException {

        TemplateInformation template = new TemplateInformation();

        for ( Element element : classElement.getEnclosedElements() ) {
            if ( isAWorkbenchPanel( element ) ) {
                extractInformationFromWorkbenchPanel( template, element );
            } else if ( onlyWorkbenchPartWithoutPanel( element ) ) {
                extractInformationFromGeneratedWorkbenchPanel( template, element );
            }

        }
        if ( template.thereIsTemplateFields() ) {
            return template;
        }
        throw new GenerationException( "The Template WorkbenchPerspective must provide a @WorkbenchPanel or @WorkbenchPart annotated field." );
    }

    private static boolean onlyWorkbenchPartWithoutPanel( Element element ) {
        boolean ufPart = element.getAnnotation( ClientAPIModule.getWorkbenchPart() ) != null;
        boolean ufParts = element.getAnnotation( ClientAPIModule.getWorkbenchParts() ) != null;
        return !isAWorkbenchPanel( element ) && ( ufPart || ufParts );
    }

    private static boolean isAWorkbenchPanel( Element element ) {
        return element.getAnnotation( ClientAPIModule.getWorkbenchPanel() ) != null;
    }

    private static void extractInformationFromWorkbenchPanel( TemplateInformation template,
                                                              Element element ) throws GenerationException {
        WorkbenchPanelInformation wbPanel = new WorkbenchPanelInformation();
        if ( workbenchPanelIsDefault( element ) ) {
            wbPanel.setDefault( true );
        }
        wbPanel.setFieldName( element.getSimpleName().toString() );
        wbPanel.setWbParts( getWorkbenchPartsFrom( element ) );
        wbPanel.setPanelType( extractPanelType( element ) );
        if ( wbPanel.isDefault() ) {
            if ( shouldHaveOnlyOneDefaultPanel( template ) ) {
                throw new GenerationException( "The Template WorkbenchPerspective must provide only one @WorkbenchPanel annotated field." );
            }
            template.setDefaultPanel( wbPanel );
        } else {
            template.addTemplateField( wbPanel );
        }
    }

    private static void extractInformationFromGeneratedWorkbenchPanel( TemplateInformation template,
                                                                       Element element ) throws GenerationException {
        WorkbenchPanelInformation generatedWbPanel = new WorkbenchPanelInformation();

        generatedWbPanel.setFieldName( element.getSimpleName().toString() );
        generatedWbPanel.setWbParts( getWorkbenchPartsFrom( element ) );
        generatedWbPanel.setPanelType( DEFAULT_PANEL_TYPE );
        template.addTemplateField( generatedWbPanel );
    }

    private static boolean shouldHaveOnlyOneDefaultPanel( TemplateInformation template ) {
        return template.getDefaultPanel() != null;
    }

    private static String extractPanelType( Element element ) throws GenerationException {
        Annotation annotation = element.getAnnotation( ClientAPIModule.getWorkbenchPanel() );
        return extractAnnotationPropertyValue( annotation, PANEL_TYPE );
    }

    private static boolean workbenchPanelIsDefault( Element element ) throws GenerationException {
        Annotation annotation = element.getAnnotation( ClientAPIModule.getWorkbenchPanel() );
        return Boolean.valueOf( extractAnnotationPropertyValue( annotation, IS_DEFAULT ) );
    }

    private static List<PartInformation> getWorkbenchPartsFrom( Element wbPanel ) throws GenerationException {
        List<PartInformation> parts = new ArrayList<PartInformation>();
        if ( thereIsWbParts( wbPanel ) ) {
            extractWbPartFromWbParts( wbPanel, parts );
        } else {
            String partName = extractMethodValueFromAnnotation( wbPanel, ClientAPIModule.getWorkbenchPart(), PART );
            Map<String, String> parameters = extractParametersFromPart( wbPanel );
            parts.add( new PartInformation( partName, parameters ) );
        }
        return parts;
    }

    private static boolean thereIsWbParts( Element element ) {
        if ( element.getAnnotation( ClientAPIModule.getWorkbenchParts() ) != null ) {
            return true;
        }
        return false;
    }

    private static String extractAnnotationPropertyValue( Annotation annotation,
                                                          String annotationProperty ) throws GenerationException {
        String value;
        try {
            Class<? extends Annotation> aClass = annotation.annotationType();
            Method identifier = aClass.getDeclaredMethod( annotationProperty );
            value = String.valueOf( identifier.invoke( annotation ) );
        } catch ( Exception e ) {
            throw new GenerationException( e.getMessage(), e.getCause() );
        }
        return value;
    }

    private static void extractWbPartFromWbParts( Element ufPanel,
                                                  List<PartInformation> parts ) throws GenerationException {
        Annotation[] annotations = extractAnnotationsFromAnnotation( ufPanel, ClientAPIModule.getWorkbenchParts(), VALUE );
        for ( Annotation annotation : annotations ) {
            String partName = extractAnnotationPropertyValue( annotation, PART );
            parts.add( new PartInformation( partName ) );
        }
    }

    private static String extractMethodValueFromAnnotation( Element element,

                                                            Class<? extends Annotation> annotation,
                                                            String methodName ) throws GenerationException {
        String identifierValue = "";
        if ( element.getAnnotation( annotation ) != null ) {
            identifierValue = getElementAnnotationStringValue( annotation, methodName, element );
        }
        return identifierValue;
    }

    private static Map<String, String> extractParametersFromPart(
            Element element ) throws GenerationException {
        Map<String, String> map = new HashMap<String, String>();
        Class<? extends Annotation> wpPart = ClientAPIModule.getWorkbenchPart();
        Class<? extends Annotation> parameterMapping = ClientAPIModule.getParameterMapping();
        try {
            Method parametersMethod = wpPart.getDeclaredMethod( PARAMETERS );
            Object parameters[] = (Object[]) parametersMethod.invoke( element.getAnnotation( wpPart ) );
            for ( Object parameter: parameters ){
                Method name = parameterMapping.getDeclaredMethod( "name" );
                Method val = parameterMapping.getDeclaredMethod( "val" );
                map.put( String.valueOf( name.invoke( parameter ) ), String.valueOf( val.invoke( parameter ) ) );
            }

        } catch ( Exception e ) {
            throw new GenerationException( e.getMessage(), e.getCause() );
        }
        return map;
    }

    private static String getElementAnnotationStringValue( Class<? extends Annotation> annotation,
                                                           String methodName,
                                                           Element element ) throws GenerationException {
        String identifierValue;
        try {
            Method identifier = annotation.getDeclaredMethod( methodName );
            identifierValue = String.valueOf( identifier.invoke( element.getAnnotation( annotation ) ) );
        } catch ( Exception e ) {
            throw new GenerationException( e.getMessage(), e.getCause() );
        }
        return identifierValue;
    }

    private static String extractAnnotationStringValue( Annotation annotation ) throws GenerationException {
        return extractAnnotationPropertyValue( annotation, VALUE );
    }

    private static Annotation[] extractAnnotationsFromAnnotation( Element element,
                                                                  Class<? extends Annotation> annotation,
                                                                  String methodName ) throws GenerationException {
        Annotation[] annotations = { };

        if ( element.getAnnotation( annotation ) != null ) {
            try {
                Method identifier = annotation.getDeclaredMethod( methodName );
                annotations = (Annotation[]) identifier.invoke( element.getAnnotation( annotation ) );
            } catch ( Exception e ) {
                throw new GenerationException( e.getMessage(), e.getCause() );
            }
        }
        return annotations;
    }

}
