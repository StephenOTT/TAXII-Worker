package io.digitalstate.taxii.graal.python;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;


public class PolymorphicExecutor {

    public PolymorphicExecutor(String language, String scriptSource, String scriptName) throws PolyglotException, IllegalArgumentException {

        Context context = Context.newBuilder().allowIO(true).build();

        Source source = Source.newBuilder(language, scriptSource, scriptName)
                .buildLiteral();

        Value response = context.eval(source);

        String responseString = response.toString();

        System.out.println("RESPONSE---> " + responseString + " using lang: " + source.getLanguage());
    }
}
