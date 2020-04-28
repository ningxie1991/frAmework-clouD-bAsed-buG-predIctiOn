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

    // Security vulnerabilities
    private int randomBug;  // Random
    private int defaultHttpClientBug;   // DefaultHttpClient
    private int weakSSLBug; // SSLContext.getInstance("SSL")
    private int customMessageDigestBug; //extends MessageDigest
    private int nullCipherBug;   //NullCipher()
    private int unencryptedSocketBug;   //Socket
    private int unencryptedServerSocketBug;   // ServerSocket
    private int unsafeHashEqualsBug;   //.equals  // UNSAFE_HASH_EQUALS
    private int externalFileDirBug;    //new File(getExternalFilesDir(
    private int userInputUrlBug;    // new URL(String url).openConnection()
    private int stackTraceBug;  // printStackTrace
    private int pathTraversalBug; // File && !FilenameUtils
    private int weakHashFunctionsBug;   // MessageDigest.getInstance || DigestUtils.getMd5Digest().digest // MD2, MD3, MD5 and SHA-1
    // Following security bug patterns have not been used
    private int xxe_xmlReaderBug;   //XMLReader && !setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
    private int xxe_documentBug;    // DocumentBuilderFactory.newInstance().newDocumentBuilder() && setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
    private int badHexConcatenationBug; // for(byte && append( Integer.toHexString
    private int blowfishShortKeyBug;    //KeyGenerator.getInstance("Blowfish") && .init(64)
    private int xxsServletBug;  //  doGet( && .getWriter().write(Encode.forHtml(
    private int insecureCookieBug;  // Cookie && !.setSecure(true)

    // New Security bug patterns added
    private int sqlInjectionBug;    // createQuery && Encoder.encodeForSQL
    private int pathTraversalInBug;
    private int commandInjectionBug;
    private int xxe_xmlStreamReaderBug;
    private int sqlInjectionJDBCBug;
    private int crlfInjectionBug;


    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

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

    public int getRandomBug() {
        return randomBug;
    }

    public void setRandomBug(int randomBug) {
        this.randomBug = randomBug;
    }

    public int getDefaultHttpClientBug() {
        return defaultHttpClientBug;
    }

    public void setDefaultHttpClientBug(int defaultHttpClientBug) {
        this.defaultHttpClientBug = defaultHttpClientBug;
    }


    public int getWeakSSLBug() {
        return weakSSLBug;
    }

    public void setWeakSSLBug(int weakSSLBug) {
        this.weakSSLBug = weakSSLBug;
    }

    public int getCustomMessageDigestBug() {
        return customMessageDigestBug;
    }

    public void setCustomMessageDigestBug(int customMessageDigestBug) {
        this.customMessageDigestBug = customMessageDigestBug;
    }

    public int getXxe_xmlReaderBug() {
        return xxe_xmlReaderBug;
    }

    public void setXxe_xmlReaderBug(int xxe_xmlReaderBug) {
        this.xxe_xmlReaderBug = xxe_xmlReaderBug;
    }

    public int getXxe_documentBug() {
        return xxe_documentBug;
    }

    public void setXxe_documentBug(int xxe_documentBug) {
        this.xxe_documentBug = xxe_documentBug;
    }

    public int getSqlInjectionBug() {
        return sqlInjectionBug;
    }

    public void setSqlInjectionBug(int sqlInjectionBug) {
        this.sqlInjectionBug = sqlInjectionBug;
    }

    public int getBadHexConcatenationBug() {
        return badHexConcatenationBug;
    }

    public void setBadHexConcatenationBug(int badHexConcatenationBug) {
        this.badHexConcatenationBug = badHexConcatenationBug;
    }

    public int getNullCipherBug() {
        return nullCipherBug;
    }

    public void setNullCipherBug(int nullCipherBug) {
        this.nullCipherBug = nullCipherBug;
    }

    public int getUnencryptedSocketBug() {
        return unencryptedSocketBug;
    }

    public void setUnencryptedSocketBug(int unencryptedSocketBug) {
        this.unencryptedSocketBug = unencryptedSocketBug;
    }

    public int getUnencryptedServerSocketBug() {
        return unencryptedServerSocketBug;
    }

    public void setUnencryptedServerSocketBug(int unencryptedServerSocketBug) {
        this.unencryptedServerSocketBug = unencryptedServerSocketBug;
    }

    public int getUnsafeHashEqualsBug() {
        return unsafeHashEqualsBug;
    }

    public void setUnsafeHashEqualsBug(int unsafeHashEqualsBug) {
        this.unsafeHashEqualsBug = unsafeHashEqualsBug;
    }

    public int getBlowfishShortKeyBug() {
        return blowfishShortKeyBug;
    }

    public void setBlowfishShortKeyBug(int blowfishShortKeyBug) {
        this.blowfishShortKeyBug = blowfishShortKeyBug;
    }

    public int getXxsServletBug() {
        return xxsServletBug;
    }

    public void setXxsServletBug(int xxsServletBug) {
        this.xxsServletBug = xxsServletBug;
    }

    public int getExternalFileDirBug() {
        return externalFileDirBug;
    }

    public void setExternalFileDirBug(int externalFileDirBug) {
        this.externalFileDirBug = externalFileDirBug;
    }

    public int getInsecureCookieBug() {
        return insecureCookieBug;
    }

    public void setInsecureCookieBug(int insecureCookieBug) {
        this.insecureCookieBug = insecureCookieBug;
    }

    public int getUserInputUrlBug() {
        return userInputUrlBug;
    }

    public void setUserInputUrlBug(int userInputUrlBug) {
        this.userInputUrlBug = userInputUrlBug;
    }

    public int getStackTraceBug() {
        return stackTraceBug;
    }

    public void setStackTraceBug(int stackTraceBug) {
        this.stackTraceBug = stackTraceBug;
    }

    public int getPathTraversalBug() {
        return pathTraversalBug;
    }

    public void setPathTraversalBug(int pathTraversalBug) {
        this.pathTraversalBug = pathTraversalBug;
    }

    public int getWeakHashFunctionsBug() {
        return weakHashFunctionsBug;
    }

    public void setWeakHashFunctionsBug(int weakHashFunctionsBug) {
        this.weakHashFunctionsBug = weakHashFunctionsBug;
    }

    public int getPathTraversalInBug() {
        return pathTraversalInBug;
    }

    public void setPathTraversalInBug(int pathTraversalInBug) {
        this.pathTraversalInBug = pathTraversalInBug;
    }

    public int getCommandInjectionBug() {
        return commandInjectionBug;
    }

    public void setCommandInjectionBug(int commandInjectionBug) {
        this.commandInjectionBug = commandInjectionBug;
    }

    public int getXxe_xmlStreamReaderBug() {
        return xxe_xmlStreamReaderBug;
    }

    public void setXxe_xmlStreamReaderBug(int xxe_xmlStreamReaderBug) {
        this.xxe_xmlStreamReaderBug = xxe_xmlStreamReaderBug;
    }

    public int getSqlInjectionJDBCBug() {
        return sqlInjectionJDBCBug;
    }

    public void setSqlInjectionJDBCBug(int sqlInjectionJDBCBug) {
        this.sqlInjectionJDBCBug = sqlInjectionJDBCBug;
    }

    public int getCrlfInjectionBug() {
        return crlfInjectionBug;
    }

    public void setCrlfInjectionBug(int crlfInjectionBug) {
        this.crlfInjectionBug = crlfInjectionBug;
    }

}
