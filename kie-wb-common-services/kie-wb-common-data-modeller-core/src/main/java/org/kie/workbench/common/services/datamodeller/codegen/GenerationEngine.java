/**
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.datamodeller.codegen;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.HasAnnotations;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;

/**
 * Simple velocity based code generation engine.
 */
//TODO Eventually, weed out everything that is no longer needed (unused templates, listener, unused context attributes - currentDataObject for example, or context alltoghether, ... )
public class GenerationEngine {
    
    private static final Logger logger = LoggerFactory.getLogger(GenerationEngine.class);

    private static GenerationEngine singleton;

    private VelocityEngine velocityEngine = new VelocityEngine();

    private static boolean inited = false;

    public static GenerationEngine getInstance() throws Exception {
        if (singleton == null) {
            singleton = new GenerationEngine();
            singleton.init();
        }
        return singleton;
    }

    /**
     * Initializes the code generation engine
     */
    private void init() throws Exception {
        if (!inited) {
            // Init velocity engine
            Properties properties = new Properties();

            properties.setProperty("resource.loader", "class");
            properties.setProperty("class.resource.loader.description", "Velocity Classpath Resource Loader");
            properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

            //TODO REVIEW THIS
            properties.setProperty( RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.JdkLogChute");

            // init velocity engine
            velocityEngine.init(properties);
            inited = true;
        }
    }

    /**
     * Runs the code generation.
     *
     * @param generationContext Context information for the generation.
     *
     * @throws Exception
     *
     */
    public void generate(GenerationContext generationContext) throws Exception {

        VelocityContext context = buildContext(generationContext);
        String templatesPath = generationContext.getTemplatesPath();
        String initialTemplate = generationContext.getInitialTemplate();

        if (logger.isDebugEnabled()) {
            logger.debug("Starting code generation with templatesPath: " + templatesPath + ", initialTemplate: " + initialTemplate);
        }
        // Always start by the initial template
        String templatePath = getFullVelocityPath(templatesPath, initialTemplate);
        if (logger.isDebugEnabled()) logger.debug("Initial templatePath: " + templatePath);

        StringWriter writer = new StringWriter();
        Template t = velocityEngine.getTemplate(templatePath);
        t.merge(context, writer);
    }

    /**
     * Creates a VelocityContext and inject common variables into it.
     *
     * @param generationContext Generation context provided by user.
     *
     * @return A properly initialized VelocityContext.
     */
    private VelocityContext buildContext(GenerationContext generationContext) {
        VelocityContext context = new VelocityContext();

        // Add main objects to velocity context
        context.put("engine", this);
        context.put("context", generationContext);
        context.put("dataModel", generationContext.getDataModel());
        context.put("nameTool", new GenerationTools());
        generationContext.setVelocityContext(context);

        return context;
    }

    /**
     * Invoked from template files when a new asset has to be generated.
     *
     * @param generationContext The context currently executing.
     *
     * @param template The template id to use.
     *
     * @param filePath The file to be generated.
     *
     * @throws java.io.IOException
     *
     */
    public void generateAsset(GenerationContext generationContext, String template, String filePath) throws Exception {

        //read the template to use
        String templatePath = getFullVelocityPath(generationContext.getTemplatesPath(), template);
        VelocityContext context = buildContext(generationContext);
        Template t = velocityEngine.getTemplate(templatePath);  //obs, templates are already cached by Velocity

        //generate asset content.
        StringWriter writer = new StringWriter();
        generationContext.setCurrentOutput(writer);
        t.merge(context, writer);

        if (generationContext.getOutputPath() != null) {
            //generate the java file in the filesystem only if the output path was set in the generation context.
            File fout = new File(generationContext.getOutputPath(), filePath);
            fout.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(fout, false);
            IOUtils.write(writer.toString(), fos);
        }

        if (generationContext.getGenerationListener() != null) {
            generationContext.getGenerationListener().assetGenerated(filePath, writer.toString());
        }
    }

    public void generateConstructors(GenerationContext generationContext, String template) throws Exception {
        generateSubTemplate(generationContext, template);
    }

    public void generateAttribute(GenerationContext generationContext, ObjectProperty attribute, String template) throws Exception {
        generateSubTemplate(generationContext, template);
    }

    public void generateSetterGetter(GenerationContext generationContext, ObjectProperty attribute, String template) throws Exception {
        generateSubTemplate(generationContext, template);
    }

    public void generateEquals(GenerationContext generationContext, String template) throws Exception {
        generateSubTemplate(generationContext, template);
    }

    public void generateHashCode(GenerationContext generationContext, String template) throws Exception {
        generateSubTemplate(generationContext, template);
    }

    public void generateTypeAnnotation(GenerationContext generationContext, Annotation annotation, String template) throws Exception {
        generateSubTemplate(generationContext, template);
    }

    public void generateFieldAnnotation(GenerationContext generationContext, Annotation annotation, String template) throws Exception {
        generateSubTemplate(generationContext, template);
    }

    public void generateSubTemplate(GenerationContext generationContext, String template) throws Exception {
        //read the template to use
        String templatePath = null;
        try {         
            templatePath = getFullVelocityPath(generationContext.getTemplatesPath(), template);
            Template t = velocityEngine.getTemplate(templatePath);
            t.merge(generationContext.getVelocityContext(), generationContext.getCurrentOutput());
        } catch (Exception e) {
            logger.error("An error was produced during template generation: template: " + template + ", templatePath: " + templatePath, e);
        }
    }

    public String generateDefaultConstructorString(GenerationContext generationContext, DataObject dataObject) throws Exception {
        VelocityContext vc = buildContext(generationContext);
        vc.put("currentDataObject", dataObject);
        return generateSubTemplateString(generationContext, "java_default_constructor");
    }

    public String generateAllFieldsConstructorString(GenerationContext generationContext, DataObject dataObject) throws Exception {
        VelocityContext vc = buildContext(generationContext);
        vc.put("currentDataObject", dataObject);
        return generateSubTemplateString(generationContext, "java_allfields_constructor");
    }

    public String generateKeyFieldsConstructorString(GenerationContext generationContext, DataObject dataObject) throws Exception {
        VelocityContext vc = buildContext(generationContext);
        vc.put("currentDataObject", dataObject);
        return generateSubTemplateString(generationContext, "java_keyfields_constructor");
    }

    public String generateAnnotationString(GenerationContext generationContext, Annotation annotation) throws Exception {
        VelocityContext vc = buildContext(generationContext);
        vc.put("annotation", annotation);
        return generateSubTemplateString(generationContext, "java_annotation");
    }

    public String generateFieldString(GenerationContext generationContext, ObjectProperty attribute) throws Exception {
        VelocityContext vc = buildContext(generationContext);
        vc.put("attr", attribute);
        return generateSubTemplateString(generationContext, "java_attribute_2");
    }

    public String generateFieldGetterString(GenerationContext generationContext, ObjectProperty attribute) throws Exception {
        VelocityContext vc = buildContext(generationContext);
        vc.put("attr", attribute);
        return generateSubTemplateString(generationContext, "java_getter");
    }

    public String generateFieldSetterString(GenerationContext generationContext, ObjectProperty attribute) throws Exception {
        VelocityContext vc = buildContext(generationContext);
        vc.put("attr", attribute);
        return generateSubTemplateString(generationContext, "java_setter");
    }

    public String generateEqualsString(GenerationContext generationContext, DataObject dataObject) throws Exception {
        VelocityContext vc = buildContext(generationContext);
        vc.put("currentDataObject", dataObject);
        return generateSubTemplateString(generationContext, "java_equals2");
    }

    public String generateHashCodeString(GenerationContext generationContext, DataObject dataObject) throws Exception {
        VelocityContext vc = buildContext(generationContext);
        vc.put("currentDataObject", dataObject);
        return generateSubTemplateString(generationContext, "java_hashCode2");
    }

    // Shortcuts

    /**
     * Generate all constructors
     */
    public String generateAllConstructorsString(GenerationContext generationContext, DataObject dataObject) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append( generateDefaultConstructorString( generationContext, dataObject ) ).append(GenerationTools.EOL).append( GenerationTools.EOL );
        sb.append( generateAllFieldsConstructorString( generationContext, dataObject ) ).append(GenerationTools.EOL).append(GenerationTools.EOL);
        sb.append( generateKeyFieldsConstructorString( generationContext, dataObject ) ).append(GenerationTools.EOL);
        return sb.toString();
    }

    /**
     * Generate all annotations for a specific element (field or class)
     */
    public String generateAllAnnotationsString(GenerationContext generationContext, HasAnnotations hasAnnotations) throws Exception {
        VelocityContext vc = buildContext(generationContext);
        StringBuilder sb = new StringBuilder();
        List<Annotation> annotations = ( (GenerationTools) vc.get( "nameTool" ) ).sortedAnnotations( hasAnnotations );
        for ( Annotation a : annotations) {
            sb.append( generateAnnotationString( generationContext, a ) ).append(GenerationTools.EOL);
        }
        return sb.toString();
    }

    /**
     * Generate the complete code fragment for a field (annotations + field declaration)
     */
    public String generateCompleteFieldString(GenerationContext generationContext, ObjectProperty attribute) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append( generateAllAnnotationsString( generationContext, attribute ) );
        sb.append( generateFieldString( generationContext, attribute ) ).append(GenerationTools.EOL).append(GenerationTools.EOL);
        return sb.toString();
    }

    /**
     * Generate getter + setter for a field
     */
    public String generateFieldGetterSetterString(GenerationContext generationContext, ObjectProperty attribute) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append( generateFieldGetterString( generationContext, attribute ) ).append(GenerationTools.EOL).append(GenerationTools.EOL);
        sb.append( generateFieldSetterString( generationContext, attribute ) ).append(GenerationTools.EOL);
        return sb.toString();
    }

    /**
     * Generate the complete java class code
     */
    // TODO indentation
    public String generateJavaClassString(GenerationContext generationContext, DataObject dataObject) throws Exception {
        VelocityContext vc = buildContext(generationContext);
        vc.put("currentDataObject", dataObject);
        return generateSubTemplateString(generationContext, "java_class2");
    }

    //TODO We could dispense with the use of templates alltogether
    public String generateSubTemplateString(GenerationContext generationContext, String template) throws Exception {
        StringWriter writer = new StringWriter();
        // This is necessary to cover possible included sub-templates
        generationContext.setCurrentOutput(writer);
        //read the template to use
        String templatePath = null;
        try {
            templatePath = getFullVelocityPath(generationContext.getTemplatesPath(), template);
            Template t = velocityEngine.getTemplate(templatePath);
            t.merge(generationContext.getVelocityContext(), writer);
        } catch (Exception e) {
            logger.error("An error was produced during template generation: template: " + template + ", templatePath: " + templatePath, e);
        }
        return writer.toString();
    }

    public static String indentLines(String source, String indent) throws Exception {
        BufferedReader reader = new BufferedReader( new StringReader( source ) );
        StringBuilder out = new StringBuilder( );
        String line;
        String lineSeparator = System.getProperty( "line.separator" );
        line = reader.readLine( );
        if ( line != null ) {
            out.append( indent );
            out.append( line );
            while ( ( line = reader.readLine( ) ) != null ) {
                out.append( lineSeparator );
                out.append( indent );
                out.append( line );
            }
        }
        return out.toString();
    }

    /**
     * Returns the path for a given template name.
     *
     * @param templatesPath Templates path location.
     *
     * @param template The template name.
     *
     * @return a full path to the given template.
     */
    private String getFullVelocityPath(String templatesPath, String template) {
        return "/" + templatesPath + "/" + template + ".vm";
    }
}