package com.mycompany.model;

public class MatrixData {
    private String className;
    private String isBug;
    private String loc;
    private String wmc;
    private String dit;
    private String cbo;
    private String rfc;
    private String lcom;
    private String name;
    private String namePr;
    private String version;
    //Added new metrics
    private String numOfFields;
    private String numOfMethods;
    private String nosi;
    private String returnsQty;
    private String loopQty;
    private String comparisonsQty;
    private String tryCatchQty;
    private String parenthesizedExpsQty;
    private String stringLiteralsQty;
    private String numbersQty;
    private String mathOperationsQty;
    private String variablesQty;
    private String maxNestedBlocks;
    private String lambdasQty;
    private String uniqueWordsQty;
    private String modifiersQty;
    private String assignmentsQty;
    //class metrics
    private String subClassesQty;
    private String anonymousClassesQty;
    //Fields metrics
    private String numOfFinalFields;
    private String numOfDefaultFields;
    private String numOfPrivateFields;
    private String numOfProtectedFields;
    private String numOfPublicFields;
    private String numOfStaticFields;
    private String numOfSynchronizedFields;
    //Methods
    private String numOfAbstractMethods;
    private String numOfDefaultMethods;
    private String numOfFinalMethods;
    private String numOfSynchronizedMethods;
    private String numOfPrivateMethods;
    private String numOfProtectedMethods;
    private String numOfPublicMethods;


    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

//    public Boolean getBug() {
//        return isBug;
//    }
//
//    public void setBug(Boolean bug) {
//        isBug = bug;
//    }
    public String getBug() {
        return isBug;
    }

    public void setBug(String bug) {
        isBug = bug;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public void setWmc(String wmc) {
        this.wmc = wmc;
    }

    public String getWmc() {
        return wmc;
    }

    public void setDit(String dit) {
        this.dit = dit;
    }

    public String getDit() {
        return dit;
    }

    public void setCbo(String cbo) {
        this.cbo = cbo;
    }

    public String getCbo() {
        return cbo;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getRfc() {
        return rfc;
    }

    public void setLcom(String lcom) { this.lcom = lcom; }

    public String getLcom() {
        return lcom;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamePr() {
        return namePr;
    }

    public void setNamePr(String namePr) {
        this.namePr = namePr;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    // Added new metrics properties
    public String getNumOfFields() {
        return numOfFields;
    }

    public void setNumOfFields(String numOfFields) { this.numOfFields = numOfFields; }

    public String getNumOfMethods() {
        return numOfMethods;
    }

    public void setNumOfMethods(String numOfMethods) {
        this.numOfMethods = numOfMethods;
    }

    public String getNOSI() {
        return nosi;
    }

    public void setNOSI(String nosi) {
        this.nosi = nosi;
    }

    public String getReturnQty() {
        return returnsQty;
    }

    public void setReturnQty(String returnsQty) {
        this.returnsQty = returnsQty;
    }

    public String getLoopQty() {
        return loopQty;
    }

    public void setLoopQty(String loopQty) {
        this.loopQty = loopQty;
    }

    public String getComparisonsQty() {
        return comparisonsQty;
    }

    public void setComparisonsQty(String comparisonsQty) {
        this.comparisonsQty = comparisonsQty;
    }

    public String getTryCatchQty() {
        return tryCatchQty;
    }

    public void setTryCatchQty(String tryCatchQty) {
        this.tryCatchQty = tryCatchQty;
    }

    public String getParenthesizedExpsQty() {
        return parenthesizedExpsQty;
    }

    public void setParenthesizedExpsQty(String parenthesizedExpsQty) {
        this.parenthesizedExpsQty = parenthesizedExpsQty;
    }

    public String getStringLiteralsQty() {
        return stringLiteralsQty;
    }

    public void setStringLiteralsQty(String stringLiteralsQty) {
        this.stringLiteralsQty = stringLiteralsQty;
    }

    public String getNumbersQty() {
        return numbersQty;
    }

    public void setNumbersQty(String numbersQty) {
        this.numbersQty = numbersQty;
    }

    public String getMathOperationsQty() {
        return mathOperationsQty;
    }

    public void setMathOperationsQty(String mathOperationsQty) {
        this.mathOperationsQty = mathOperationsQty;
    }

    public String getVariablesQty() {
        return variablesQty;
    }

    public void setVariablesQty(String variablesQty) {
        this.variablesQty = variablesQty;
    }

    public String getMaxNestedBlocks() {
        return maxNestedBlocks;
    }

    public void setMaxNestedBlocks(String maxNestedBlocks) {
        this.maxNestedBlocks = maxNestedBlocks;
    }

    public String getLambdasQty() {
        return lambdasQty;
    }

    public void setLambdasQty(String lambdasQty) {
        this.lambdasQty = lambdasQty;
    }

    public String getUniqueWordsQty() {
        return uniqueWordsQty;
    }

    public void setUniqueWordsQty(String uniqueWordsQty) {
        this.uniqueWordsQty = uniqueWordsQty;
    }

    public String getModifiers() {
        return modifiersQty;
    }

    public void setModifiers(String modifiersQty) {
        this.modifiersQty = modifiersQty;
    }

    public String getAssignmentsQty() {
        return assignmentsQty;
    }

    public void setAssignmentsQty(String assignmentsQty) {
        this.assignmentsQty = assignmentsQty;
    }

    public String getSubClassesQty() {
        return subClassesQty;
    }

    public void setSubClassesQty(String subClassesQty) {
        this.subClassesQty = subClassesQty;
    }

    public String getAnonymousClassesQty() {
        return anonymousClassesQty;
    }

    public void setAnonymousClassesQty(String anonymousClassesQty) {
        this.anonymousClassesQty = anonymousClassesQty;
    }
    //Fields

    public String getNumberOfFinalFields() {
        return numOfFinalFields;
    }

    public void setNumberOfFinalFields(String numOfFinalFields) {
        this.numOfFinalFields = numOfFinalFields;
    }

    public String getNumberOfDefaultFields() {
        return numOfDefaultFields;
    }

    public void setNumberOfDefaultFields(String numOfDefaultFields) {
        this.numOfDefaultFields = numOfDefaultFields;
    }

    public String getNumberOfPrivateFields() {
        return numOfPrivateFields;
    }

    public void setNumberOfPrivateFields(String numOfPrivateFields) {
        this.numOfPrivateFields = numOfPrivateFields;
    }

    public String getNumberOfProtectedFields() { return numOfProtectedFields; }

    public void setNumberOfProtectedFields(String numOfProtectedFields) {
        this.numOfProtectedFields = numOfProtectedFields;
    }

    public String getNumberOfPublicFields() {
        return numOfPublicFields;
    }

    public void setNumberOfPublicFields(String numOfPublicFields) {
        this.numOfPublicFields = numOfPublicFields;
    }

    public String getNumberOfStaticFields() {
        return numOfStaticFields;
    }

    public void setNumberOfStaticFields(String numOfStaticFields) {
        this.numOfStaticFields = numOfStaticFields;
    }

    public String getNumberOfSynchronizedFields() {
        return numOfSynchronizedFields;
    }

    public void setNumberOfSynchronizedFields(String numOfSynchronizedFields) {
        this.numOfSynchronizedFields = numOfSynchronizedFields;
    }
    // Methods

    public String getNumberOfAbstractMethods() {
        return numOfAbstractMethods;
    }

    public void setNumberOfAbstractMethods(String numOfAbstractMethods) {
        this.numOfAbstractMethods = numOfAbstractMethods;
    }

    public String getNumberOfDefaultMethods() {
        return numOfDefaultMethods;
    }

    public void setNumberOfDefaultMethods(String numOfDefaultMethods) {
        this.numOfDefaultMethods = numOfDefaultMethods;
    }

    public String getNumberOfFinalMethods() {
        return numOfFinalMethods;
    }

    public void setNumberOfFinalMethods(String numOfFinalMethods) {
        this.numOfFinalMethods = numOfFinalMethods;
    }

    public String getNumberOfSynchronizedMethods() {
        return numOfSynchronizedMethods;
    }

    public void setNumberOfSynchronizedMethods(String numOfSynchronizedMethods) {
        this.numOfSynchronizedMethods = numOfSynchronizedMethods;
    }

    public String getNumberOfPrivateMethods() {
        return numOfPrivateMethods;
    }

    public void setNumberOfPrivateMethods(String numOfPrivateMethods) {
        this.numOfPrivateMethods = numOfPrivateMethods;
    }

    public String getNumberOfProtectedMethods() {
        return numOfProtectedMethods;
    }

    public void setNumberOfProtectedMethods(String numOfProtectedMethods) {
        this.numOfProtectedMethods = numOfProtectedMethods;
    }

    public String getNumberOfPublicMethods() {
        return numOfPublicMethods;
    }

    public void setNumberOfPublicMethods(String numOfPublicMethods) {
        this.numOfPublicMethods = numOfPublicMethods;
    }





}
