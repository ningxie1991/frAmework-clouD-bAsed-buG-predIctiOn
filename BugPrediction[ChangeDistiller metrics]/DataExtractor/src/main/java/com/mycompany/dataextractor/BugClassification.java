package com.mycompany.dataextractor;

import org.eclipse.jgit.api.errors.GitAPIException;
import java.io.IOException;
import java.text.ParseException;


/**
 * To classify bugs in cloud systems (Bug classification is based on github commit messages)
 */
public class BugClassification {

    //Types of bugs in Cloud Systems(Keywords for security bug)
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

    public static void main(String[] args) throws ParseException, IOException, GitAPIException {

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
