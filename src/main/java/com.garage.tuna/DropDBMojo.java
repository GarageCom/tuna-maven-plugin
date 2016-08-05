package com.garage.tuna;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo( name = "drop")
public class DropDBMojo extends BaseMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

    }
}
