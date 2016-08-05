package com.garage.tuna;

import java.util.Date;
import java.util.UUID;

public class TunaModel {
    private UUID id;
    private String fileName;
    private String checksum;
    private Date executedDate;

    public TunaModel(UUID id, String fileName, String checksum, Date executedDate) {
        this.id = id;
        this.fileName = fileName;
        this.checksum = checksum;
        this.executedDate = executedDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public Date getExecutedDate() {
        return executedDate;
    }

    public void setExecutedDate(Date executedDate) {
        this.executedDate = executedDate;
    }
}
