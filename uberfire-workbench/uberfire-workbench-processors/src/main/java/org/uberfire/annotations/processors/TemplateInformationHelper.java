package org.uberfire.annotations.processors;

import static org.uberfire.annotations.processors.GeneratorUtils.getAnnotation;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import org.uberfire.annotations.processors.exceptions.GenerationException;
import org.uberfire.annotations.processors.facades.ClientAPIModule;

public class TemplateInformationHelper {

    public static final String VALUE = "value";
    public static final String PANEL_TYPE = "panelType";
    public static final String IS_DEFAULT = "isDefault";
    public static final String PARENT_CHOOSES_PANEL_TYPE = "PARENT_CHOOSES_TYPE"; // must match PanelDefinition.PARENT_CHOOSES_TYPE
    public static final String PARTS = "parts";

    public static TemplateInformation extractWbTemplatePerspectiveInformation( Elements elementUtils, TypeElement classElement ) throws GenerationException {

        TemplateInformation template = new TemplateInformation();

        for ( Element element : classElement.getEnclosedElements() ) {
            extractInformationFromWorkbenchPanel( elementUtils, template, element );
        }
        return template;
    }

    private static void extractInformationFromWorkbenchPanel( Elements elementUtils,
                                                              TemplateInformation template,
                                                              Element element ) throws GenerationException {

        if ( GeneratorUtils.getAnnotation( elementUtils, element, ClientAPIModule.getWorkbenchPanel() ) == null ) {
            // this element is not of interest
            return;
        }

        WorkbenchPanelInformation wbPanel = new WorkbenchPanelInformation();
        if ( workbenchPanelIsDefault( elementUtils, element ) ) {
            wbPanel.setDefault( true );
        }
        wbPanel.setFieldName( element.getSimpleName().toString() );
        wbPanel.setWbParts( getWorkbenchPartsFrom( elementUtils, element ) );
        wbPanel.setPanelType( extractPanelType( elementUtils, element ) );
        if ( wbPanel.isDefault() ) {
            if ( template.getDefaultPanel() != null ) {
                throw new GenerationException( "Found more than one @WorkbenchPanel with isDefault=true." );
            }
            template.setDefaultPanel( wbPanel );
        } else {
            template.addTemplateField( wbPanel );
        }
    }

    private static String extractPanelType( Elements elementUtils, Element element ) throws GenerationException {
        AnnotationMirror am = getAnnotation( elementUtils, element, ClientAPIModule.getWorkbenchPanel() );
        String panelPresenterClassName = GeneratorUtils.extractAnnotationStringValue( elementUtils, am, PANEL_TYPE );
        if ( panelPresenterClassName.equals( "java.lang.Void" ) ) {
            return PARENT_CHOOSES_PANEL_TYPE;
        }
        return panelPresenterClassName;
    }

    private static boolean workbenchPanelIsDefault( Elements elementUtils, Element element ) throws GenerationException {
        AnnotationMirror am = getAnnotation( elementUtils, element, ClientAPIModule.getWorkbenchPanel() );
        return Boolean.valueOf( GeneratorUtils.extractAnnotationStringValue( elementUtils, am, IS_DEFAULT ) );
    }

    private static List<PartInformation> getWorkbenchPartsFrom( Elements elementUtils, Element wbPanel ) throws GenerationException {
        AnnotationMirror wbPartAnnotation = getAnnotation( elementUtils, wbPanel, ClientAPIModule.getWorkbenchPanel() );
        AnnotationValue partsParam = GeneratorUtils.extractAnnotationPropertyValue( elementUtils, wbPartAnnotation, PARTS );

        List<PartInformation> partInfos = new ArrayList<PartInformation>();
        for ( String partNameAndParams : GeneratorUtils.extractValue( partsParam ) ) {
            partInfos.add( new PartInformation( partNameAndParams ) );
        }
        return partInfos;
    }

}
