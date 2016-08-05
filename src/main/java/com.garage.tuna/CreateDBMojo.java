package com.garage.tuna;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo( name = "create")
public class CreateDBMojo extends BaseMojo {

    public static final String KEYSPACE_CREATION_QUERY = "CREATE KEYSPACE IF NOT EXISTS :keyspace WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': '1' };";



    public void execute() throws MojoExecutionException, MojoFailureException {
        final Cluster cluster = Cluster.builder().addContactPoints(contactpoints).withPort(port).build();
        Session session = cluster.connect();
        session.execute(KEYSPACE_CREATION_QUERY.replace(":keyspace", keyspace));
        System.out.println("Keyspace " + keyspace + " created!");

        session.close();

    }
}
