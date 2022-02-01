package top.jingbh.zhixuehelper.data

import org.junit.Assert.assertEquals
import org.junit.Test
import top.jingbh.zhixuehelper.data.subject.Subject

class SubjectUnitTest {
    @Test
    fun subject_chinese_codeIsCorrect() {
        val subject = Subject.CHINESE

        assertEquals("01", subject.getSubjectCode())
    }

    @Test
    fun subject_chinese_parsingIsCorrect() {
        val subject = Subject.ofSubjectCode("01")

        assertEquals(Subject.CHINESE, subject)
        assertEquals("", subject.variation)
    }

    @Test
    fun subject_generalTechnology_codeIsCorrect() {
        val subject = Subject.GENERAL_TECHNOLOGY

        assertEquals("102", subject.getSubjectCode())
    }

    @Test
    fun subject_generalTechnology_parsingIsCorrect() {
        val subject = Subject.ofSubjectCode("102")

        assertEquals(Subject.GENERAL_TECHNOLOGY, subject)
        assertEquals("", subject.variation)
    }

    @Test
    fun subject_englishVariation_codeIsCorrect() {
        val subject = Subject.ENGLISH
        subject.variation = "B"

        assertEquals("03B", subject.getSubjectCode())
    }

    @Test
    fun subject_englishVariation_parsingIsCorrect() {
        val subject = Subject.ofSubjectCode("03B")

        assertEquals(Subject.ENGLISH, subject)
        assertEquals("B", subject.variation)
        assertEquals("03B", subject.getSubjectCode())
    }

    @Test
    fun subject_all_codeIsCorrect() {
        val subject = Subject.ALL_

        assertEquals("", subject.getSubjectCode())
    }

    @Test
    fun subject_all_parsingIsCorrect() {
        val subject = Subject.ofSubjectCode("")

        assertEquals(Subject.ALL_, subject)
    }

    @Test
    fun subject_unknown_parsingIsCorrect() {
        val subject = Subject.ofSubjectCode("99999")

        assertEquals(Subject.UNKNOWN_, subject)
    }
}
