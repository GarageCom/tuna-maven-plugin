package com.garage.tuna;

import com.sun.jmx.snmp.Timestamp;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.io.IOException;
import java.util.Date;

@Mojo( name = "generate")
public class GeerateFileMojo extends BaseMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {

        try {

            String fileName = migrationPath + "/" + new Date().getTime() + "-short-description-here.cql";
            File file = new File(fileName);

            if (file.createNewFile()){
                System.out.println("Empty migration is created!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
