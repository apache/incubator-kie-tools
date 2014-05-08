package org.uberfire.annotations.processors;

import java.util.Locale;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * A wrapper for ProcessingEnvironment that returns a wrapped Types instance which works around bug 434378 in Eclipse.
 */
public class EclipseWorkaroundProcessingEnvironment implements ProcessingEnvironment {

    private final ProcessingEnvironment realImpl;
    private final Types wrappedTypeUtils;

    public EclipseWorkaroundProcessingEnvironment(ProcessingEnvironment realImpl) {
        this.realImpl = realImpl;
        this.wrappedTypeUtils = new EclipseWorkaroundTypeUtils( realImpl.getTypeUtils() );
    }

    @Override
    public Elements getElementUtils() {
        return realImpl.getElementUtils();
    }

    @Override
    public Filer getFiler() {
        return realImpl.getFiler();
    }

    @Override
    public Locale getLocale() {
        return realImpl.getLocale();
    }

    @Override
    public Messager getMessager() {
        return realImpl.getMessager();
    }

    @Override
    public Map<String, String> getOptions() {
        return realImpl.getOptions();
    }

    @Override
    public SourceVersion getSourceVersion() {
        return realImpl.getSourceVersion();
    }

    @Override
    public Types getTypeUtils() {
        return wrappedTypeUtils;
    }

}
