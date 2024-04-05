//REPOS mavencentral,apache=https://repository.apache.org/content/groups/public/
//DEPS org.kie:kie-dmn-validation:999-20240331-SNAPSHOT

import java.io.StringReader;
import java.util.List;
import java.util.stream.Stream;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.core.compiler.profiles.ExtendedDMNProfile;
import org.kie.dmn.validation.DMNValidator;
import org.kie.dmn.validation.DMNValidator.Validation;
import org.kie.dmn.validation.DMNValidatorFactory;

/**
 * JBang script that performs DMN files' XML (in string format) validation relying on KIE DMN Validator
 * (https://github.com/apache/incubator-kie-drools/tree/main/kie-dmn/kie-dmn-validation).
 * The script can manage one or two (in case of imported model) DMN Files.
 * The XSD SCHEMA, DMN COMPLIANCE and DMN COMPILATION are validated.
 */
class dmnValidation {

    public static void main(String... args) throws Exception {
        int exitCode = validate(args);
        System.exit(exitCode);
    }

    public static int validate(String... args) throws Exception {
        if (args.length > 2 || args.length <= 0) {
            throw new IllegalArgumentException("Validation requires 1 or 2 xmls");
        }

        System.out.println("============== XML ================================")
        System.out.println(args[0]);
        System.out.println("============== END ================================")


        StringReader[] models = Stream.of(args)
                                      .map(arg -> removeSurroundingDoubleQuotes(arg))
                                      .map(StringReader::new)
                                      .toArray(StringReader[]::new);

        DMNValidator dmnValidator = DMNValidatorFactory.newValidator(List.of(new ExtendedDMNProfile()));

        final List<DMNMessage> messages = dmnValidator
            .validateUsing(Validation.VALIDATE_SCHEMA,
                           Validation.VALIDATE_MODEL,
                           Validation.VALIDATE_COMPILATION)
            .theseModels(models);

        if (messages.size() == 0) {
            return 0;
        } else {
            System.out.println("=== DMN VALIDATION FAILED ===");
            messages.forEach(message -> System.out.println(message.getText()));
            System.out.println("=============================");
            return 1;
        }
    }

    private static String removeSurroundingDoubleQuotes(String xml) {
        if (xml.startsWith("\"") && xml.endsWith("\"")) {
            return xml.substring(1, xml.length() - 1);
        } 
        return xml;
        // Pattern pattern = Pattern.compile("^\"|\"$");
        // Matcher matcher = pattern.matcher(xml);
        // return matcher.replaceAll("");
    }
}