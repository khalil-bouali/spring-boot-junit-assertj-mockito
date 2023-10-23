package com.kbouali.demo.student;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void itShouldCheckIfStudentEmailExists() {
        // given
        String email = "khalil@mail.com";
        Student student = new Student("Khalil", email, Gender.FEMALE);
        underTest.save(student);
        // when
        boolean expected = underTest.selectExistsEmail(email);
        // then
        assertThat(expected).isTrue();
    }

    @Test
    void itShouldCheckIfStudentEmailDoesNotExists() {
        // given
        String email = "khalil@mail.com";
        // when
        boolean expected = underTest.selectExistsEmail(email);
        // then
        assertThat(expected).isFalse();
    }
}