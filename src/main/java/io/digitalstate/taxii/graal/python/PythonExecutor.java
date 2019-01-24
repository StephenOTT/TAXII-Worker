package io.digitalstate.taxii.graal.python;

import org.graalvm.polyglot.*;

public class PythonExecutor {

    public PythonExecutor() {
        Context context = Context.newBuilder().allowIO(true).build();
        Value array = context.eval("python", "[1,2,42,4]");
        int result = array.getArrayElement(2).asInt();
        System.out.println("Python Result:");
        System.out.println(result);
    }
}
