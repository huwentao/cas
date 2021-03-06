package org.apereo.cas.support.wsfederation.authentication.principal;

import org.apereo.cas.support.wsfederation.AbstractWsFederationTests;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;

import static org.junit.Assert.*;


/**
 * Test cases for {@link WsFederationCredential}.
 * @author John Gasper
 * @since 4.2.0
 */
public class WsFederationCredentialTests extends AbstractWsFederationTests {

    @Autowired
    private HashMap<String, String> testTokens;
    
    private WsFederationCredential standardCred;

    @Before
    public void setUp() {
        standardCred = new WsFederationCredential();
        standardCred.setNotBefore(ZonedDateTime.now(ZoneOffset.UTC));
        standardCred.setNotOnOrAfter(ZonedDateTime.now(ZoneOffset.UTC).plusHours(1));
        standardCred.setIssuedOn(ZonedDateTime.now(ZoneOffset.UTC));
        standardCred.setIssuer("http://adfs.example.com/adfs/services/trust");
        standardCred.setAudience("urn:federation:cas");
        standardCred.setId("_6257b2bf-7361-4081-ae1f-ec58d4310f61");
        standardCred.setRetrievedOn(ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(1));
    }

    @Test
    public void verifyIsValidAllGood() throws Exception {
        final boolean result = standardCred.isValid("urn:federation:cas", "http://adfs.example.com/adfs/services/trust", 2000);
        assertTrue("testIsValidAllGood() - True", result);
    }

    @Test
    public void verifyIsValidBadAudience() throws Exception {
        standardCred.setAudience("urn:NotUs");
        final boolean result = standardCred.isValid("urn:federation:cas", "http://adfs.example.com/adfs/services/trust", 2000);
        assertFalse("testIsValidBadAudeience() - False", result);
    }

    @Test
    public void verifyIsValidBadIssuer() throws Exception {
        standardCred.setIssuer("urn:NotThem");
        final boolean result = standardCred.isValid("urn:federation:cas", "http://adfs.example.com/adfs/services/trust", 2000);
        assertFalse("testIsValidBadIssuer() - False", result);
    }

    @Test
    public void verifyIsValidEarlyToken() throws Exception {
        standardCred.setNotBefore(ZonedDateTime.now(ZoneOffset.UTC).plusDays(1));
        standardCred.setNotOnOrAfter(ZonedDateTime.now(ZoneOffset.UTC).plusHours(1).plusDays(1));
        standardCred.setIssuedOn(ZonedDateTime.now(ZoneOffset.UTC).plusDays(1));
        
        final boolean result = standardCred.isValid("urn:federation:cas", "http://adfs.example.com/adfs/services/trust", 2000);
        assertFalse("testIsValidEarlyToken() - False", result);
    }

    @Test
    public void verifyIsValidOldToken() throws Exception {
        standardCred.setNotBefore(ZonedDateTime.now(ZoneOffset.UTC).minusDays(1));
        standardCred.setNotOnOrAfter(ZonedDateTime.now(ZoneOffset.UTC).plusHours(1).minusDays(1));
        standardCred.setIssuedOn(ZonedDateTime.now(ZoneOffset.UTC).minusDays(1));
        
        final boolean result = standardCred.isValid("urn:federation:cas", "http://adfs.example.com/adfs/services/trust", 2000);
        assertFalse("testIsValidOldToken() - False", result);
    }

    @Test
    public void verifyIsValidExpiredIssuedOn() throws Exception {
        standardCred.setIssuedOn(ZonedDateTime.now(ZoneOffset.UTC).minusSeconds(3));
        
        final boolean result = standardCred.isValid("urn:federation:cas", "http://adfs.example.com/adfs/services/trust", 2000);
        assertFalse("testIsValidOldToken() - False", result);
    }

    public void setTestTokens(final HashMap<String, String> testTokens) {
        this.testTokens = testTokens;
    }
}
