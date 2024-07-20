package com.example.archunit.architecture;

public class ArchitectureConstants {

    // Suffixes
    public static final String CONTROLLER_SUFFIX = "Controller";
    public static final String REPOSITORY_SUFFIX = "Repository";
    public static final String SERVICE_SUFFIX = "Service";

    // Packages
    public static final String CONTROLLER_PACKAGE = "..controllers..";
    public static final String MODEL_PACKAGE = "..model..";
    public static final String ENTITY_PACKAGE = "..entities..";
    public static final String MAPPER_PACKAGE = "..mapper..";
    public static final String REPOSITORY_PACKAGE = "..repositories..";
    public static final String SERVICE_PACKAGE = "..services..";

    // Package to scan
    public static final String DEFAULT_PACKAGE = "com.example.archunit";

    // Explanations
    public static final String ANNOTATED_EXPLANATION = "Classes in %s package should be annotated with %s";

    private ArchitectureConstants() {}
}
