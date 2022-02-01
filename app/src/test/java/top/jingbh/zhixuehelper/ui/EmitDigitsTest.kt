package top.jingbh.zhixuehelper.ui

import org.junit.Assert.assertEquals
import org.junit.Test
import top.jingbh.zhixuehelper.ui.util.emitDigits

class EmitDigitsTest {
    @Test
    fun testNoMoreDigits() {
        assertEquals("10.5", 10.5.emitDigits())
    }

    @Test
    fun testOneMoreZeroAfter() {
        assertEquals("10.5", 10.50.emitDigits())
    }

    @Test
    fun testMoreNumbersAfter() {
        assertEquals("10.5", 10.505.emitDigits())
    }

    @Test
    fun testOneMoreZeroBefore() {
        assertEquals("10.1", 010.1.emitDigits())
    }

    @Test
    fun testMoreNumbersBefore() {
        assertEquals("1110.5", 1110.5.emitDigits())
    }

    @Test
    fun testEmitAllDigits() {
        assertEquals("10", 10.0.emitDigits())
    }
}
