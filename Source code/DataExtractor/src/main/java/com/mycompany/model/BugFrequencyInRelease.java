package com.mycompany.model;

public class BugFrequencyInRelease {

    private int concFrequency;
    private int perfFrequency;
    private int optimFrequency;
    private int errorhandlingFrequency;
    private int hangFrequency;
    private int configFrequency;
    private String projectname;
    private String releasetag;

    public BugFrequencyInRelease(){
        concFrequency = 0;
        perfFrequency = 0;
        optimFrequency = 0;
        errorhandlingFrequency = 0;
        hangFrequency = 0;
        configFrequency = 0;
    }

    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }

    public String getReleasetag() {
        return releasetag;
    }

    public void setReleasetag(String releasetag) {
        this.releasetag = releasetag;
    }

    public int getConcFrequency() {
        return concFrequency;
    }

    public void setConcFrequency(int concFrequency) {
        this.concFrequency = concFrequency;
    }

    public int getConfigFrequency() {
        return configFrequency;
    }

    public void setConfigFrequency(int configFrequency) {
        this.configFrequency = configFrequency;
    }

    public int getPerfFrequency() {
        return perfFrequency;
    }

    public void setPerfFrequency(int perfFrequency) {
        this.perfFrequency = perfFrequency;
    }

    public int getOptimFrequency() {
        return optimFrequency;
    }

    public void setOptimFrequency(int optimFrequency) {
        this.optimFrequency = optimFrequency;
    }

    public int getErrorhandlingFrequency() {
        return errorhandlingFrequency;
    }

    public void setErrorhandlingFrequency(int errorhandlingFrequency) {
        this.errorhandlingFrequency = errorhandlingFrequency;
    }

    public int getHangFrequency() {
        return hangFrequency;
    }

    public void setHangFrequency(int hangFrequency) {
        this.hangFrequency = hangFrequency;
    }




}
