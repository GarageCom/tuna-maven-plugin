package com.garage.tuna;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;


public abstract class BaseMojo extends AbstractMojo {

    @Parameter(property = "keyspace", required = true)
    protected String keyspace;

    @Parameter(property = "port", required = true)
    protected int port;

    @Parameter(property = "contact-points", required = true)
    protected String contactpoints;

    @Parameter(property = "migration-path", required = true)
    protected String migrationPath;
}
