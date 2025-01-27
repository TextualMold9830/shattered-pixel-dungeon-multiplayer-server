package ru.nikita22007.synchronization;

import ru.nikita22007.synchronization.processors.server.*;
import spoon.IncrementalLauncher;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.CtModel;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        System.out.println("Adding resources");
        SpoonAPI spoon = new Launcher();
        Set<File> sourceList = new HashSet<File>();
        sourceList.add(new File("./core/src/main/java"));
        sourceList.add(new File("./SPD-Classes/src/main/java"));
        spoon = new IncrementalLauncher(sourceList, new HashSet<>(),new File("./cache/"), false);
        spoon.getEnvironment().setAutoImports(true);
        //System.out.println(new File(".").getAbsolutePath() + File.separator + "core" +  File.separator + "src" + File.separator + "main" + File.separator + "java") ;
        //spoon.addInputResource(new File(".").getAbsolutePath() + File.separator + "core" +  File.separator + "src" + File.separator + "main" + File.separator+ "java");
        //spoon.addInputResource(new File(".").getAbsolutePath() + File.separator + "SPD-classes" +  File.separator + "src" + File.separator + "main" + File.separator+ "java");

        System.out.println("Adding processors");
        spoon.addProcessor(new DeleteClientSideElements());
        spoon.addProcessor(new AddJsonSerializableImplementation());
        spoon.addProcessor(new AddIdField());
        spoon.addProcessor(new MarkSetters());
        spoon.addProcessor(new processSynchronizedSetters());
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
