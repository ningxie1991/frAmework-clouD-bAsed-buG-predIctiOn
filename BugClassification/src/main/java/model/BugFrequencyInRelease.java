package model;

/**
 * To set frequencies of cloud bugs in a cloud project, on each release
 */
public class BugFrequencyInRelease {

    private int concFrequency;
    private int perfFrequency;
    private int optimFrequency;
    private int errorhandlingFrequency;
    private int hangFrequency;
    private int configFrequency;
    private int secFrequency;
    private String projectName;
    private String releaseTag;

    public String getProjectname() {
        return projectName;
    }

    public void setProjectname(String projectname) {
        this.projectName = projectname;
    }

    public String getReleasetag() {
        return releaseTag;
    }

    public void setReleasetag(String releasetag) {
        this.releaseTag = releasetag;
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

    public int getSecFrequency() {
        return secFrequency;
    }

    public void setSecFrequency(int secFrequency) {
        this.secFrequency = secFrequency;
    }
}
