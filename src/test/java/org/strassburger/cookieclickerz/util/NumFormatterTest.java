package org.strassburger.cookieclickerz.util;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class NumFormatterTest {

    @Test
    public void testStringToBigInteger() {
        assertEquals(new BigInteger("123000"), NumFormatter.stringToBigInteger("123K"));
        assertEquals(new BigInteger("123000000"), NumFormatter.stringToBigInteger("123M"));
        assertEquals(new BigInteger("123000000000"), NumFormatter.stringToBigInteger("123B"));
        assertEquals(new BigInteger("123000000000000000000000"), NumFormatter.stringToBigInteger("123S"));
        assertEquals(new BigInteger("123000000000000000000000000"), NumFormatter.stringToBigInteger("123SS"));
        assertNull(NumFormatter.stringToBigInteger("invalid"));
        assertEquals(new BigInteger("123456789"), NumFormatter.stringToBigInteger("123456789"));
    }

    @Test
    public void testStringToBigDecimal() {
        assertEquals(new BigDecimal("123000"), NumFormatter.stringToBigDecimal("123K"));
        assertEquals(new BigDecimal("123000000"), NumFormatter.stringToBigDecimal("123M"));
        assertEquals(new BigDecimal("123000000000"), NumFormatter.stringToBigDecimal("123B"));
        assertEquals(new BigDecimal("123000000000000000000000"), NumFormatter.stringToBigDecimal("123S"));
        assertEquals(new BigDecimal("123000000000000000000000000"), NumFormatter.stringToBigDecimal("123SS"));
        assertNull(NumFormatter.stringToBigDecimal("invalid"));
        assertEquals(new BigDecimal("123456789"), NumFormatter.stringToBigDecimal("123456789"));
    }

    @Test
    public void testFormatBigInt() {
        assertEquals("123K", NumFormatter.formatBigInt(new BigInteger("123000")));
        assertEquals("123M", NumFormatter.formatBigInt(new BigInteger("123000000")));
        assertEquals("123B", NumFormatter.formatBigInt(new BigInteger("123000000000")));
        assertEquals("123S", NumFormatter.formatBigInt(new BigInteger("123000000000000000000000")));
        assertEquals("123SS", NumFormatter.formatBigInt(new BigInteger("123000000000000000000000000")));
        assertEquals("123D", NumFormatter.formatBigInt(new BigInteger("123000000000000000000000000000000000")));
    }

    @Test
    public void testFormatBigDecimal() {
        assertEquals("123K", NumFormatter.formatBigDecimal(new BigDecimal("123000")));
        assertEquals("123M", NumFormatter.formatBigDecimal(new BigDecimal("123000000")));
        assertEquals("123B", NumFormatter.formatBigDecimal(new BigDecimal("123000000000")));
        assertEquals("123S", NumFormatter.formatBigDecimal(new BigDecimal("123000000000000000000000")));
        assertEquals("123SS", NumFormatter.formatBigDecimal(new BigDecimal("123000000000000000000000000")));
        assertEquals("123D", NumFormatter.formatBigDecimal(new BigDecimal("123000000000000000000000000000000000")));
    }
}