package org.example.hogwartsartifactsonline.artifact.exception;

public class ArtifcatNotFoundException extends RuntimeException{
    public ArtifcatNotFoundException(String id) {
        super("Could not find artifact with Id " + id + " :(");
    }
}
