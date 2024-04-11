///usr/bin/env jbang "$0" "$@" ; exit $?
//REPOS mavencentral,apache=https://repository.apache.org/content/groups/public/
//DEPS ch.qos.logback:logback-classic:1.2.9
//DEPS info.picocli:picocli:4.7.5
//DEPS org.kie:kie-dmn-validation:${kogito-runtime.version:LATEST}
//DEPS org.slf4j:slf4j-simple:2.0.12

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.core.compiler.profiles.ExtendedDMNProfile;
import org.kie.dmn.validation.DMNValidator;
import org.kie.dmn.validation.DMNValidator.Validation;
import org.kie.dmn.validation.DMNValidatorFactory;

/**
 * JBang script that performs DMN files' XML (in string format) validation relying on KIE DMN Validator
 * (https://github.com/apache/incubator-kie-drools/tree/main/kie-dmn/kie-dmn-validation).
 * The script can manage one or two (in case of imported model) DMN file paths.
 * The XSD SCHEMA, DMN COMPLIANCE and DMN COMPILATION are validated.
 */
@Command(name = "DmnValidation", mixinStandardHelpOptions = true, version = "DmnValidation 0.1", description = "It validates given DMN files")
class DmnValidation implements Callable<Integer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DmnValidation.class);

    @Option(names = {"-d", "--dmnFilePath"}, description = "Path of DMN file to be validated", required = true)
    private String dmnFilePath;

    @Option(names = {"-i", "--importedDmnFilesPaths"} , description = "Paths of the DMN files imported by the DMN file to validate", required = false, split = ",")
    private String[] importedDmnFilesPath;

    public static void main(String... args) {
        int exitCode = new CommandLine(new DmnValidation()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        List<File> models = new ArrayList<>();
        models.add(new File(dmnFilePath));

        if (importedDmnFilesPath != null && importedDmnFilesPath.length > 0) {
            models.addAll(Stream.of(importedDmnFilesPath)
                                .map(File::new)
                                .collect(Collectors.toList()));
        }

        return this.validate(models);
    }

    public static int validate(List<File> models) {
        DMNValidator dmnValidator = DMNValidatorFactory.newValidator(List.of(new ExtendedDMNProfile()));

        final List<DMNMessage> messages = dmnValidator.validateUsing(Validation.VALIDATE_SCHEMA,
                                                                     Validation.VALIDATE_MODEL,
                                                                     Validation.VALIDATE_COMPILATION)
                                                      .theseModels(models.toArray(File[]::new));

        if (messages.size() == 0) {
            System.out.println("RESULT: Following files have been successfully validated!");
            models.forEach(model -> System.out.println(model.getName()));
            return 0;
        } else {
            System.out.println("ERROR: Validation failed for the following files");
            models.forEach(model -> System.out.println(model.getName()));
            System.out.println("Validation Errors:");
            messages.forEach(message -> System.out.println(message.getText()));
            return 1;
        }
    }
}