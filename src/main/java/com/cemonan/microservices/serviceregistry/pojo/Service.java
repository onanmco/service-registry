package com.cemonan.microservices.serviceregistry.pojo;

public class Service {
    private String name;
    private String version;
    private String ip;
    private String port;
    private long timestamp;

    public Service(String name, String version, String ip, String port, long timestamp) {
        this.name = name;
        this.version = version;
        this.ip = ip;
        this.port = port;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
