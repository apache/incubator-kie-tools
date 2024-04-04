//REPOS mavencentral,apache=https://repository.apache.org/content/groups/public/
//DEPS com.github.lalyos:jfiglet:0.0.8
//DEPS org.kie:kie-dmn-validation:999-20240331-SNAPSHOT

import com.github.lalyos.jfiglet.FigletFont;
import java.io.StringReader;
import java.util.List;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.core.compiler.profiles.ExtendedDMNProfile;
import org.kie.dmn.validation.DMNValidator;
import org.kie.dmn.validation.DMNValidatorFactory;

class dmnValidation {

    public static void main(String... args) throws Exception {
        // System.out.println(FigletFont.convertOneLine(
        //        "Happy Birthday " + ((args.length>0)?args[0]:"jbang")));
        int exitCode = validate(args[0]);
        System.exit(exitCode);
    }

    public static int validate(String xml) throws Exception {
        DMNValidator dmnValidator = DMNValidatorFactory.newValidator(List.of(new ExtendedDMNProfile()));
        // System.out.println("===");
        // System.out.println(xml.trim());
        // System.out.println("===");

        List<DMNMessage> messages = dmnValidator.validate(new StringReader(xml));
        messages.forEach(message -> System.out.println("MESSAGE: " + message.getText()));
        if (messages.size() == 0) {
            //System.out.println("MESSAGE: IS FINE!!!");
            return 0;
        } else {
            return 1;
        }
    }
}