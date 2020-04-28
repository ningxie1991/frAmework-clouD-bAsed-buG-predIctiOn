package com.mycompany.model;

public class MatrixData {
    private String className;
    private String isBug;

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

    // Change Distiller Change-Type metrics
    private int PARAMETER_DELETE; //
    private int PARAMETER_INSERT; //
    private int REMOVED_FUNCTIONALITY; //
    private int RETURN_TYPE_CHANGE; //
    private int RETURN_TYPE_DELETE; //
    private int RETURN_TYPE_INSERT; //
    private int STATEMENT_DELETE; //
    private int STATEMENT_INSERT; //
    private int STATEMENT_ORDERING_CHANGE; //
    private int STATEMENT_PARENT_CHANGE; //
    private int STATEMENT_UPDATE; //
    private int ALTERNATIVE_PART_INSERT; //
    private int DOC_DELETE; //
    private int ADDING_ATTRIBUTE_MODIFIABILITY;
    private int ADDING_CLASS_DERIVABILITY;
    private int ADDING_METHOD_OVERRIDABILITY;
    private int ADDITIONAL_CLASS;
    private int ADDITIONAL_FUNCTIONALITY;
    private int ADDITIONAL_OBJECT_STATE;
    private int ALTERNATIVE_PART_DELETE;
    private int ATTRIBUTE_RENAMING;
    private int ATTRIBUTE_TYPE_CHANGE;
    private int CLASS_RENAMING;
    private int COMMENT_DELETE;
    private int COMMENT_INSERT;
    private int COMMENT_MOVE;
    private int COMMENT_UPDATE;
    private int CONDITION_EXPRESSION_CHANGE;
    private int DECREASING_ACCESSIBILITY_CHANGE;
    private int DOC_INSERT;
    private int DOC_UPDATE;
    private int INCREASING_ACCESSIBILITY_CHANGE;
    private int METHOD_RENAMING;
    private int PARAMETER_ORDERING_CHANGE;
    private int PARAMETER_RENAMING;
    private int PARAMETER_TYPE_CHANGE;
    private int PARENT_CLASS_CHANGE;
    private int PARENT_CLASS_DELETE;
    private int PARENT_CLASS_INSERT;
    private int PARENT_INTERFACE_CHANGE;
    private int PARENT_INTERFACE_DELETE;
    private int PARENT_INTERFACE_INSERT;
    private int REMOVED_CLASS;
    private int REMOVED_OBJECT_STATE;
    private int REMOVING_ATTRIBUTE_MODIFIABILITY;
    private int REMOVING_CLASS_DERIVABILITY;
    private int REMOVING_METHOD_OVERRIDABILITY;
    private int UNCLASSIFIED_CHANGE;

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

    public int getPARAMETER_DELETE() {
        return PARAMETER_DELETE;
    }

    public void setPARAMETER_DELETE(int PARAMETER_DELETE) {
        this.PARAMETER_DELETE = PARAMETER_DELETE;
    }

    public int getPARAMETER_INSERT() {
        return PARAMETER_INSERT;
    }

    public void setPARAMETER_INSERT(int PARAMETER_INSERT) {
        this.PARAMETER_INSERT = PARAMETER_INSERT;
    }

    public int getREMOVED_FUNCTIONALITY() {
        return REMOVED_FUNCTIONALITY;
    }

    public void setREMOVED_FUNCTIONALITY(int REMOVED_FUNCTIONALITY) {
        this.REMOVED_FUNCTIONALITY = REMOVED_FUNCTIONALITY;
    }

    public int getRETURN_TYPE_CHANGE() {
        return RETURN_TYPE_CHANGE;
    }

    public void setRETURN_TYPE_CHANGE(int RETURN_TYPE_CHANGE) {
        this.RETURN_TYPE_CHANGE = RETURN_TYPE_CHANGE;
    }

    public int getRETURN_TYPE_DELETE() {
        return RETURN_TYPE_DELETE;
    }

    public void setRETURN_TYPE_DELETE(int RETURN_TYPE_DELETE) {
        this.RETURN_TYPE_DELETE = RETURN_TYPE_DELETE;
    }

    public int getRETURN_TYPE_INSERT() {
        return RETURN_TYPE_INSERT;
    }

    public void setRETURN_TYPE_INSERT(int RETURN_TYPE_INSERT) {
        this.RETURN_TYPE_INSERT = RETURN_TYPE_INSERT;
    }

    public int getSTATEMENT_DELETE() {
        return STATEMENT_DELETE;
    }

    public void setSTATEMENT_DELETE(int STATEMENT_DELETE) {
        this.STATEMENT_DELETE = STATEMENT_DELETE;
    }

    public int getSTATEMENT_INSERT() {
        return STATEMENT_INSERT;
    }

    public void setSTATEMENT_INSERT(int STATEMENT_INSERT) {
        this.STATEMENT_INSERT = STATEMENT_INSERT;
    }

    public int getSTATEMENT_ORDERING_CHANGE() {
        return STATEMENT_ORDERING_CHANGE;
    }

    public void setSTATEMENT_ORDERING_CHANGE(int STATEMENT_ORDERING_CHANGE) {
        this.STATEMENT_ORDERING_CHANGE = STATEMENT_ORDERING_CHANGE;
    }

    public int getSTATEMENT_PARENT_CHANGE() {
        return STATEMENT_PARENT_CHANGE;
    }

    public void setSTATEMENT_PARENT_CHANGE(int STATEMENT_PARENT_CHANGE) {
        this.STATEMENT_PARENT_CHANGE = STATEMENT_PARENT_CHANGE;
    }

    public int getSTATEMENT_UPDATE() {
        return STATEMENT_UPDATE;
    }

    public void setSTATEMENT_UPDATE(int STATEMENT_UPDATE) {
        this.STATEMENT_UPDATE = STATEMENT_UPDATE;
    }

    public int getALTERNATIVE_PART_INSERT() {
        return ALTERNATIVE_PART_INSERT;
    }

    public void setALTERNATIVE_PART_INSERT(int ALTERNATIVE_PART_INSERT) {
        this.ALTERNATIVE_PART_INSERT = ALTERNATIVE_PART_INSERT;
    }

    public int getDOC_DELETE() {
        return DOC_DELETE;
    }

    public void setDOC_DELETE(int DOC_DELETE) {
        this.DOC_DELETE = DOC_DELETE;
    }

    public int getADDING_ATTRIBUTE_MODIFIABILITY() {
        return ADDING_ATTRIBUTE_MODIFIABILITY;
    }

    public void setADDING_ATTRIBUTE_MODIFIABILITY(int ADDING_ATTRIBUTE_MODIFIABILITY) {
        this.ADDING_ATTRIBUTE_MODIFIABILITY = ADDING_ATTRIBUTE_MODIFIABILITY;
    }

    public int getADDING_CLASS_DERIVABILITY() {
        return ADDING_CLASS_DERIVABILITY;
    }

    public void setADDING_CLASS_DERIVABILITY(int ADDING_CLASS_DERIVABILITY) {
        this.ADDING_CLASS_DERIVABILITY = ADDING_CLASS_DERIVABILITY;
    }

    public int getADDING_METHOD_OVERRIDABILITY() {
        return ADDING_METHOD_OVERRIDABILITY;
    }

    public void setADDING_METHOD_OVERRIDABILITY(int ADDING_METHOD_OVERRIDABILITY) {
        this.ADDING_METHOD_OVERRIDABILITY = ADDING_METHOD_OVERRIDABILITY;
    }

    public int getADDITIONAL_CLASS() {
        return ADDITIONAL_CLASS;
    }

    public void setADDITIONAL_CLASS(int ADDITIONAL_CLASS) {
        this.ADDITIONAL_CLASS = ADDITIONAL_CLASS;
    }

    public int getADDITIONAL_FUNCTIONALITY() {
        return ADDITIONAL_FUNCTIONALITY;
    }

    public void setADDITIONAL_FUNCTIONALITY(int ADDITIONAL_FUNCTIONALITY) {
        this.ADDITIONAL_FUNCTIONALITY = ADDITIONAL_FUNCTIONALITY;
    }

    public int getADDITIONAL_OBJECT_STATE() {
        return ADDITIONAL_OBJECT_STATE;
    }

    public void setADDITIONAL_OBJECT_STATE(int ADDITIONAL_OBJECT_STATE) {
        this.ADDITIONAL_OBJECT_STATE = ADDITIONAL_OBJECT_STATE;
    }

    public int getALTERNATIVE_PART_DELETE() {
        return ALTERNATIVE_PART_DELETE;
    }

    public void setALTERNATIVE_PART_DELETE(int ALTERNATIVE_PART_DELETE) {
        this.ALTERNATIVE_PART_DELETE = ALTERNATIVE_PART_DELETE;
    }

    public int getATTRIBUTE_RENAMING() {
        return ATTRIBUTE_RENAMING;
    }

    public void setATTRIBUTE_RENAMING(int ATTRIBUTE_RENAMING) {
        this.ATTRIBUTE_RENAMING = ATTRIBUTE_RENAMING;
    }

    public int getATTRIBUTE_TYPE_CHANGE() {
        return ATTRIBUTE_TYPE_CHANGE;
    }

    public void setATTRIBUTE_TYPE_CHANGE(int ATTRIBUTE_TYPE_CHANGE) {
        this.ATTRIBUTE_TYPE_CHANGE = ATTRIBUTE_TYPE_CHANGE;
    }

    public int getCLASS_RENAMING() {
        return CLASS_RENAMING;
    }

    public void setCLASS_RENAMING(int CLASS_RENAMING) {
        this.CLASS_RENAMING = CLASS_RENAMING;
    }

    public int getCOMMENT_DELETE() {
        return COMMENT_DELETE;
    }

    public void setCOMMENT_DELETE(int COMMENT_DELETE) {
        this.COMMENT_DELETE = COMMENT_DELETE;
    }

    public int getCOMMENT_INSERT() {
        return COMMENT_INSERT;
    }

    public void setCOMMENT_INSERT(int COMMENT_INSERT) {
        this.COMMENT_INSERT = COMMENT_INSERT;
    }

    public int getCOMMENT_MOVE() {
        return COMMENT_MOVE;
    }

    public void setCOMMENT_MOVE(int COMMENT_MOVE) {
        this.COMMENT_MOVE = COMMENT_MOVE;
    }

    public int getCOMMENT_UPDATE() {
        return COMMENT_UPDATE;
    }

    public void setCOMMENT_UPDATE(int COMMENT_UPDATE) {
        this.COMMENT_UPDATE = COMMENT_UPDATE;
    }

    public int getCONDITION_EXPRESSION_CHANGE() {
        return CONDITION_EXPRESSION_CHANGE;
    }

    public void setCONDITION_EXPRESSION_CHANGE(int CONDITION_EXPRESSION_CHANGE) {
        this.CONDITION_EXPRESSION_CHANGE = CONDITION_EXPRESSION_CHANGE;
    }

    public int getDECREASING_ACCESSIBILITY_CHANGE() {
        return DECREASING_ACCESSIBILITY_CHANGE;
    }

    public void setDECREASING_ACCESSIBILITY_CHANGE(int DECREASING_ACCESSIBILITY_CHANGE) {
        this.DECREASING_ACCESSIBILITY_CHANGE = DECREASING_ACCESSIBILITY_CHANGE;
    }

    public int getDOC_INSERT() {
        return DOC_INSERT;
    }

    public void setDOC_INSERT(int DOC_INSERT) {
        this.DOC_INSERT = DOC_INSERT;
    }

    public int getDOC_UPDATE() {
        return DOC_UPDATE;
    }

    public void setDOC_UPDATE(int DOC_UPDATE) {
        this.DOC_UPDATE = DOC_UPDATE;
    }

    public int getINCREASING_ACCESSIBILITY_CHANGE() {
        return INCREASING_ACCESSIBILITY_CHANGE;
    }

    public void setINCREASING_ACCESSIBILITY_CHANGE(int INCREASING_ACCESSIBILITY_CHANGE) {
        this.INCREASING_ACCESSIBILITY_CHANGE = INCREASING_ACCESSIBILITY_CHANGE;
    }

    public int getMETHOD_RENAMING() {
        return METHOD_RENAMING;
    }

    public void setMETHOD_RENAMING(int METHOD_RENAMING) {
        this.METHOD_RENAMING = METHOD_RENAMING;
    }

    public int getPARAMETER_ORDERING_CHANGE() {
        return PARAMETER_ORDERING_CHANGE;
    }

    public void setPARAMETER_ORDERING_CHANGE(int PARAMETER_ORDERING_CHANGE) {
        this.PARAMETER_ORDERING_CHANGE = PARAMETER_ORDERING_CHANGE;
    }

    public int getPARAMETER_RENAMING() {
        return PARAMETER_RENAMING;
    }

    public void setPARAMETER_RENAMING(int PARAMETER_RENAMING) {
        this.PARAMETER_RENAMING = PARAMETER_RENAMING;
    }

    public int getPARAMETER_TYPE_CHANGE() {
        return PARAMETER_TYPE_CHANGE;
    }

    public void setPARAMETER_TYPE_CHANGE(int PARAMETER_TYPE_CHANGE) {
        this.PARAMETER_TYPE_CHANGE = PARAMETER_TYPE_CHANGE;
    }

    public int getPARENT_CLASS_CHANGE() {
        return PARENT_CLASS_CHANGE;
    }

    public void setPARENT_CLASS_CHANGE(int PARENT_CLASS_CHANGE) {
        this.PARENT_CLASS_CHANGE = PARENT_CLASS_CHANGE;
    }

    public int getPARENT_CLASS_DELETE() {
        return PARENT_CLASS_DELETE;
    }

    public void setPARENT_CLASS_DELETE(int PARENT_CLASS_DELETE) {
        this.PARENT_CLASS_DELETE = PARENT_CLASS_DELETE;
    }

    public int getPARENT_CLASS_INSERT() {
        return PARENT_CLASS_INSERT;
    }

    public void setPARENT_CLASS_INSERT(int PARENT_CLASS_INSERT) {
        this.PARENT_CLASS_INSERT = PARENT_CLASS_INSERT;
    }

    public int getPARENT_INTERFACE_CHANGE() {
        return PARENT_INTERFACE_CHANGE;
    }

    public void setPARENT_INTERFACE_CHANGE(int PARENT_INTERFACE_CHANGE) {
        this.PARENT_INTERFACE_CHANGE = PARENT_INTERFACE_CHANGE;
    }

    public int getPARENT_INTERFACE_DELETE() {
        return PARENT_INTERFACE_DELETE;
    }

    public void setPARENT_INTERFACE_DELETE(int PARENT_INTERFACE_DELETE) {
        this.PARENT_INTERFACE_DELETE = PARENT_INTERFACE_DELETE;
    }

    public int getPARENT_INTERFACE_INSERT() {
        return PARENT_INTERFACE_INSERT;
    }

    public void setPARENT_INTERFACE_INSERT(int PARENT_INTERFACE_INSERT) {
        this.PARENT_INTERFACE_INSERT = PARENT_INTERFACE_INSERT;
    }

    public int getREMOVED_CLASS() {
        return REMOVED_CLASS;
    }

    public void setREMOVED_CLASS(int REMOVED_CLASS) {
        this.REMOVED_CLASS = REMOVED_CLASS;
    }

    public int getREMOVED_OBJECT_STATE() {
        return REMOVED_OBJECT_STATE;
    }

    public void setREMOVED_OBJECT_STATE(int REMOVED_OBJECT_STATE) {
        this.REMOVED_OBJECT_STATE = REMOVED_OBJECT_STATE;
    }

    public int getREMOVING_ATTRIBUTE_MODIFIABILITY() {
        return REMOVING_ATTRIBUTE_MODIFIABILITY;
    }

    public void setREMOVING_ATTRIBUTE_MODIFIABILITY(int REMOVING_ATTRIBUTE_MODIFIABILITY) {
        this.REMOVING_ATTRIBUTE_MODIFIABILITY = REMOVING_ATTRIBUTE_MODIFIABILITY;
    }

    public int getREMOVING_CLASS_DERIVABILITY() {
        return REMOVING_CLASS_DERIVABILITY;
    }

    public void setREMOVING_CLASS_DERIVABILITY(int REMOVING_CLASS_DERIVABILITY) {
        this.REMOVING_CLASS_DERIVABILITY = REMOVING_CLASS_DERIVABILITY;
    }

    public int getREMOVING_METHOD_OVERRIDABILITY() {
        return REMOVING_METHOD_OVERRIDABILITY;
    }

    public void setREMOVING_METHOD_OVERRIDABILITY(int REMOVING_METHOD_OVERRIDABILITY) {
        this.REMOVING_METHOD_OVERRIDABILITY = REMOVING_METHOD_OVERRIDABILITY;
    }

    public int getUNCLASSIFIED_CHANGE() {
        return UNCLASSIFIED_CHANGE;
    }

    public void setUNCLASSIFIED_CHANGE(int UNCLASSIFIED_CHANGE) {
        this.UNCLASSIFIED_CHANGE = UNCLASSIFIED_CHANGE;
    }
}
