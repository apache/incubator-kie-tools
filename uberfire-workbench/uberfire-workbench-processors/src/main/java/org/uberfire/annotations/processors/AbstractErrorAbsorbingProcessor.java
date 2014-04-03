package org.uberfire.annotations.processors;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

public abstract class AbstractErrorAbsorbingProcessor extends AbstractProcessor {

	private Throwable rememberedInitError;

	protected AbstractErrorAbsorbingProcessor() {
	    try {
            freemarker.log.Logger.selectLoggerLibrary( freemarker.log.Logger.LIBRARY_NONE );
        } catch (ClassNotFoundException e) {
            rememberedInitError = e;
        }
    }

	@Override
	public final boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		try {
			if (rememberedInitError != null) {
				throw rememberedInitError;
			}
			return processWithExceptions(annotations, roundEnv);
		} catch (Throwable e) {
			// eclipse JDT goes into an infinite loop when the annotation processor throws any exception
			// so we have to catch EVERYTHING, even Errors.

		    StringWriter stringWriter = new StringWriter();
            e.printStackTrace( new PrintWriter( stringWriter ) );
		    final String errorMessage = "Internal error in " + getClass().getName() + stringWriter.toString();

		    boolean emittedSpecificError = false;
		    for (TypeElement annotation : annotations) {
		        for (Element annotationTarget : roundEnv.getElementsAnnotatedWith( annotation )) {
                    processingEnv.getMessager().printMessage(
		                    Kind.ERROR,
		                    errorMessage,
		                    annotationTarget,
		                    findAnnotationMirror( annotationTarget, annotation ));
		            emittedSpecificError = true;
		        }
		    }

		    // if the above loop caught nothing, the type we were called for didn't contain an annotation
		    // we handle (maybe it was inherited). In this case, we'll just emit a non-location-specific error
		    // so there is at least some sort of diagnostic message for the user to go on!
		    if (!emittedSpecificError) {
		        processingEnv.getMessager().printMessage(
                        Kind.ERROR,
                        errorMessage);
		    }

			return false;
		}
	}

    private static AnnotationMirror findAnnotationMirror( Element target, TypeElement annotationType ) {
        final String annotationTypeName = annotationType.getQualifiedName().toString();
        for (AnnotationMirror am : target.getAnnotationMirrors()) {
            if (((TypeElement) am.getAnnotationType().asElement()).getQualifiedName().toString().equals( annotationTypeName )) {
                return am;
            }
        }
        return null;
    }

	/**
	 * Subclasses must call this from their constructors if something throws an
	 * exception during initialization of the instance. Once this method has
	 * been called with a non-null throwable, the
	 * {@link #processWithExceptions(Set, RoundEnvironment)} method will not be
	 * called on this instance.
	 *
	 * @param t
	 *            the exception that occurred (and was caught) during instance
	 *            creation of this annotation processor instance.
	 */
	protected void rememberInitializationError(Throwable t) {
		rememberedInitError = t;
	}

	/**
	 * Same contract as {@link #process(Set, RoundEnvironment)}, except that any
	 * exceptions thrown are caught and printed as messages of type
	 * {@link Kind#ERROR}. This is done to keep Eclipse JDT from going into an
	 * infinite processing loop.
	 */
	protected abstract boolean processWithExceptions(
			Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws Exception;

    /** Writes the given code to javac's Filer. */
	protected final void writeCode( final String packageName,
                            final String className,
                            final StringBuffer code ) throws IOException {
        JavaFileObject jfo = processingEnv.getFiler().createSourceFile( packageName + "." + className );
        Writer w = jfo.openWriter();
        BufferedWriter bw = new BufferedWriter( w );
        bw.append( code );
        bw.close();
        w.close();
    }

}
