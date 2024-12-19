package ru.nikita22007.synchronization;

import ru.nikita22007.synchronization.processors.server.*;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.CtModel;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        SpoonAPI spoon = new Launcher();
        spoon.getEnvironment().setAutoImports(true);
        //System.out.println(new File(".").getAbsolutePath() + File.separator + "core" +  File.separator + "src" + File.separator + "main" + File.separator + "java") ;
        System.out.println("Adding resources");
        spoon.addInputResource(new File(".").getAbsolutePath() + File.separator + "core" +  File.separator + "src" + File.separator + "main" + File.separator+ "java");
        spoon.addInputResource(new File(".").getAbsolutePath() + File.separator + "SPD-classes" +  File.separator + "src" + File.separator + "main" + File.separator+ "java");

        System.out.println("Adding processors");
        spoon.addProcessor(new DeleteClientSideElements());
        spoon.addProcessor(new AddJsonSerializableImplementation());
        spoon.addProcessor(new AddIdField());
        spoon.addProcessor(new AddJsonPackMethods());


        System.out.println("Building model");
        CtModel model = spoon.buildModel();
        /*model.filterChildren((el) -> el instanceof CtClass<?>).forEach((CtClass<?> cl) -> {
            CtComment comment = cl.getFactory().createComment("Copyright(c) 2023 etc", CtComment.CommentType.JAVADOC);
            cl.addComment(comment);
        });*/

        spoon.setSourceOutputDirectory("./server/src/main/java/");
        System.out.println("Processing");
        spoon.process();
        System.out.println("Printing target");
        spoon.prettyprint();
        //spoon.run();
    }
}
