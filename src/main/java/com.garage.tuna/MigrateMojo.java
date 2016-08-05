package com.garage.tuna;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@Mojo( name = "migrate")
public class MigrateMojo extends BaseMojo {

    private static final String EXTENSION = "cql";

    public static final String MIGRATIONS_TABLE_CREATION_QUERY = "CREATE TABLE IF NOT EXISTS :keyspace.tuna_migrations (id UUID, filename VARCHAR, checksum VARCHAR, executed_date TIMESTAMP, PRIMARY KEY (id, executed_date, filename));";

    public void execute() throws MojoExecutionException, MojoFailureException {

        List<TunaModel> migrations = getExecutedMigrations();

        try {
            checkModifications(migrations);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> checksums = migrations.stream().map(r -> r.getChecksum()).collect(Collectors.toList());

        try {
            Files.walk(Paths.get(migrationPath)).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    String path = filePath.toAbsolutePath().toString();
                    try {
                        if (EXTENSION.equals(FilenameUtils.getExtension(path)) && !checksums.contains(getChecksum(path))) {
                            String cql = readFileByLine(path);
                            runScript(cql);
                            try {
                                saveMetadata(path);
                                System.out.println("Migration " + path + " completed");
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }

                        }
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveMetadata(String path) throws IOException, NoSuchAlgorithmException {
        final Cluster cluster = Cluster.builder().addContactPoints(contactpoints).withPort(port).build();
        final Session session = cluster.connect();
        session.execute("INSERT INTO :keyspace.tuna_migrations (id, filename, checksum, executed_date) VALUES (?, ?, ?, ?)".replace(":keyspace", keyspace), UUID.randomUUID(), path, getChecksum(path), new java.util.Date());
        cluster.close();
    }

    private void checkModifications(List<TunaModel> migrations) throws IOException {
        for (TunaModel tunaModel : migrations) {
            Files.walk(Paths.get(migrationPath)).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    String path = filePath.toAbsolutePath().toString();
                    try {
                        String checksum = getChecksum(path);
                        if (!checksum.equals(tunaModel.getChecksum())) {
                            throw new RuntimeException("File " + tunaModel.getFileName() + " was modified!!!");
                        }
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private String getChecksum(String path) throws NoSuchAlgorithmException, IOException {

        MessageDigest md = MessageDigest.getInstance("SHA1");
        FileInputStream fis = new FileInputStream(path);
        byte[] dataBytes = new byte[1024];

        int nread = 0;

        while ((nread = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        };

        byte[] mdbytes = md.digest();

        //convert the byte to hex format
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();

    }

    private List<TunaModel> getExecutedMigrations() {
        List<TunaModel> tunaModels = new ArrayList<>();
        final Cluster cluster = Cluster.builder().addContactPoints(contactpoints).withPort(port).build();
        final Session session = cluster.connect();
        session.execute(MIGRATIONS_TABLE_CREATION_QUERY.replace(":keyspace", keyspace));
        ResultSet result = session.execute("SELECT * FROM :keyspace.tuna_migrations;".replace(":keyspace", keyspace));

        for (Row rs : result) {
            TunaModel tuna = new TunaModel(rs.getUUID("id"), rs.getString("filename"), rs.getString("checksum"), new Date(rs.getString("executed_date")));
            tunaModels.add(tuna);
        }

        session.close();
        return tunaModels;
    }

    private void runScript(String cql) {
        final Cluster cluster = Cluster.builder().addContactPoints(contactpoints).withPort(port).build();
        final Session session = cluster.connect(keyspace);
        session.execute(cql);
        cluster.close();
    }

    private String readFileByLine(String fileName) {
        StringBuilder r = new StringBuilder();
        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                r.append(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return r.toString();
    }
}
