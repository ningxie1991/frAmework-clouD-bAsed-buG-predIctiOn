package com.util;


/**
 * To classify bugs in cloud systems (Bug classification is based on github commit messages)
 */
public class BugClassification {

    //Types of bugs in Cloud Systems(Keywords for each bug type)
    private static String[] configurationKeywords = new String[]{"configuration"};
    private static String[] optimizationKeywords = new String[]{"optimization", "optimize"};
    private static String[] error_handlingKeywords = new String[]{"error handling", "exception", "exceptions"};
    private static String[] performanceKeywords = new String[]{"performance", "load balancing", "cloud bursting",
            "performance implications", "delay",
    };
    private static String[] hangKeywords = new String[]{"hang", "freeze", "unresponsive", "blocking", "deadlock",
            "infinite loop", "user operation error",
    };
    private static String[] concurrencyKeywords = new String[]{"synchronize", "synchronous", "synchronization",
            "thread", "blocked", "locked", "race", "dead-lock",
            "deadlock", "concurrent", "concurrency", "atomic",
            "starvation", "suspension", "live-lock", "livelock",
            "multithreading", "single variable atomicity violation",
            "multi variable atomicity violation", "order violation",
            "multi-threaded", "atomicity violation", "multi-thread",
    };
    private static String[] securityKeywords = new String[]{"security threats", "dos", "ddos", "replay", "hyperjacking",
            "distributed-denial-of-service", "denial of service",
            "vulnerability", "repudiation", "spoofing", "tempering",
            "eavesdropping", "man in middle", "cross-site scripting",
            "illegally tampered", "maliciously fabricated",
            "side channel attacks", "virtualization vulnerabilities",
            "abuse of cloud services", "hypervisor-based attack",
            "vm-based attack", "vm image attack", "xss scripting attack",
            "data loss", "vm sprawl", "illegal invasion", "vm escape",
            "incorrect vm isolation", "insufficient authorization",
            "elevation of privilege", "buffer overrun", "timing attack",
            "xml parser attack", "information leakage", "cache attack",
            "unsecured vm migration", "predictable pseudorandom number generator",
            "potential crlf injection for logs", "potential path traversal",
            "unencrypted socket", "potential command injection",
            "md2, md2 and md5 are weak hash functions", "found jax-rs rest endpoint",
            "xml parsing vulnerable to xxe (documentbuilder)", "static iv",
            "cipher with no integrity", "cipher is susceptible to padding oracle",
            "trustmanager that accept any certificates", "des/desede is insecure",
            "ecb mode is insecure", "a prepared statement is generated from a nonconstant string",
            "potential jdbc injection", "potential xpath injection",
            "nonconstant string passed to execute or addBatch method on an sql statement",
            "object deserialization is used", "xml parsing vulnerable to xxe (saxparser)",
            "hostnameverifier that accept any signed certificates", "potential ldap injection",
            "filenameutils not filtering null bytes",
            "trust boundary violation", "cookie without the httponly flag",
            "potential xss in servlet", "unvalidated redirect",
            "untrusted servlet parameter", "cipher with no integrity",
            "potential http response splitting", "cookie without the secure flag",
            "http headers untrusted", "untrusted query string", "hard coded key",
            "ecb mode is insecure", "potentially sensitive data in a cookie",
            "found struts 2 endpoint", "regex dos (redos)",
    };




    /**
     * Check for concurrency bugs in a commit message
     */
    private static Boolean isConcurrencyBug(String commitMessage) {

        for (int i = 0; i < concurrencyKeywords.length; i++) {
            if (commitMessage.contains(concurrencyKeywords[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check for performance bugs in a commit message
     */
    private static Boolean isPerformanceBug(String commitMessage) {

        for (int i = 0; i < performanceKeywords.length; i++) {
            if (commitMessage.contains(performanceKeywords[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check for configuration bugs in a commit message
     */
    private static Boolean isConfigBug(String commitMessage) {

        for (int i = 0; i < configurationKeywords.length; i++) {
            if (commitMessage.contains(configurationKeywords[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check for optimization bugs in a commit message
     */
    private static Boolean isOptimizationBug(String commitMessage) {

        for (int i = 0; i < optimizationKeywords.length; i++) {
            if (commitMessage.contains(optimizationKeywords[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check for error handling bugs in a commit message
     */
    private static Boolean isErrorHandlingBug(String commitMessage) {

        for (int i = 0; i < error_handlingKeywords.length; i++) {
            if (commitMessage.contains(error_handlingKeywords[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check for hang bugs in a commit message
     */
    private static Boolean isHangBug(String commitMessage) {

        for (int i = 0; i < hangKeywords.length; i++) {
            if (commitMessage.contains(hangKeywords[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check for security bugs in a commit message
     */
    public static Boolean isSecurityBug(String commitMessage) {

        for (int i = 0; i < securityKeywords.length; i++) {
            if (commitMessage.contains(securityKeywords[i])) {
                return true;
            }
        }
        return false;

    }

}
