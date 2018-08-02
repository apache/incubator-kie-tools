package org.kie.workbench.common.services.backend.compiler.rest;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import org.kie.soup.project.datamodel.commons.util.MVELEvaluator;
import org.kie.soup.project.datamodel.commons.util.RawMVELEvaluator;

@Dependent
public class MVELEvaluatorProducer {

    @Produces
    public final MVELEvaluator evaluator = new RawMVELEvaluator();
}
