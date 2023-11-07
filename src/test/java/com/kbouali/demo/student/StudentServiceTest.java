package com.kbouali.demo.student;

import com.kbouali.demo.student.exception.BadRequestException;
import com.kbouali.demo.student.exception.StudentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
class StudentServiceTest {

    @Mock
    private StudentRepository mockedStudentRepository;
    @Autowired
    private StudentRepository wiredStudentRepository;
    private StudentService mockedUnderTest;
    private StudentService wiredUnderTest;

    @BeforeEach
    void setUp() {
        mockedUnderTest = new StudentService(mockedStudentRepository);
        wiredUnderTest = new StudentService(wiredStudentRepository);
    }

    @Test
    void canGetAllStudents() {
        // when
        mockedUnderTest.getAllStudents();
        // then
        verify(mockedStudentRepository).findAll();
    }

    @Test
    void canAddStudent() {
        // given
        Student student = new Student("Khalil", "khalil@mail.com", Gender.FEMALE);
        // when
        mockedUnderTest.addStudent(student);
        // then
        ArgumentCaptor<Student> studentArgumentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(mockedStudentRepository).save(studentArgumentCaptor.capture());
        Student capturedStudent = studentArgumentCaptor.getValue();
        assertThat(capturedStudent).isEqualTo(student);
    }

    @Test
    void willThrowWhenEmailIsTaken() {
        // given
        Student student = new Student("Khalil", "khalil@mail.com", Gender.FEMALE);

        given(mockedStudentRepository.selectExistsEmail(student.getEmail()))
                .willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> mockedUnderTest.addStudent(student))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + student.getEmail() + " taken");

        verify(mockedStudentRepository, never()).save(any());
    }

    @Test
    void canDeleteStudent() {
        // given
        Student student = new Student("Khalil", "khalil@mail.com", Gender.FEMALE);
        wiredUnderTest.addStudent(student);
        // when
        wiredUnderTest.deleteStudent(student.getId());
        // then
        Optional<Student> fetchedStudent = wiredStudentRepository.findById(student.getId());
        assertThat(fetchedStudent.isPresent()).isFalse();
    }

    @Test
    void willThrowWhenIdDoesNotExist() {
        // given
        given(mockedStudentRepository.existsById(0L))
                .willReturn(false);
        // when
        // then
        assertThatThrownBy(() -> mockedUnderTest.deleteStudent(0L))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student with id " + 0L + " does not exists");

        verify(mockedStudentRepository, never()).deleteById(any());
    }
}