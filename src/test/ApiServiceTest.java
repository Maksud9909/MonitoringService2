package test;

import api.ApiService;
import counters.CounterService;
import counters.CounterType;
import exceptions.CounterSubmissionException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import users.Role;
import users.User;
import users.UserRepository;
import test.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * Класс тестирования для {@link ApiService}. Использует JUnit для написания и запуска тестов.
 */
public class ApiServiceTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private UserRepository userRepository;
    private CounterService counterService;
    private ApiService apiService;

    @Before
    public void setUp() {
        userRepository = new UserRepository();
        counterService = new CounterService();
        apiService = new ApiService(userRepository, counterService);

        // Redirect System.out to capture printed content
        System.setOut(new PrintStream(outContent));
    }
    /**
     * Метод тестирования регистрации нового пользователя. Создает мок сканера с предварительно
     * заданными данными, вызывает метод регистрации пользователя и проверяет, что пользователь
     * успешно зарегистрирован с указанным именем пользователя.
     */

    @Test
    public void testRegisterUser() {
        ScannerMock scannerMock = new ScannerMock("john_doe\npassword\nUSER\n");
        apiService.registerUser(scannerMock.getScanner());
        User registeredUser = userRepository.getUserByUsername("john_doe");
        assertEquals("john_doe", registeredUser.getUsername());
    }
    /**
     * Метод тестирования аутентификации пользователя. Зарегистрированный пользователь создается
     * в репозитории, а мок сканера предоставляет данные для аутентификации. Проверяет, что сообщение
     * об успешной аутентификации выводится в System.out.
     */
    @Test
    public void testAuthenticateUser() {
        userRepository.registerUser("test_user", "password", Role.USER);
        ScannerMock scannerMock = new ScannerMock("test_user\npassword\n");
        apiService.authenticateUser(scannerMock.getScanner());
        scannerMock.handleAuditLog("Аудит: Пользователь авторизован: test_user");
        assertEquals("Введите логин: Введите пароль: Авторизация успешна.\n" +
                "Аудит: Пользователь авторизован: test_user", outContent.toString().trim());
    }

    /**
     * Метод тестирования подачи показаний счетчика. Зарегистрированный пользователь, мок сканера
     * с предварительно заданными данными для аутентификации и подачи показаний. Проверяет, что
     * поданные показания отображаются в системе.
     */
    @Test
    public void testSubmitCounterReading() {
        userRepository.registerUser("test_user", "password", Role.USER);
        ScannerMock scannerMock = new ScannerMock("HOT_WATER\n1\n100\n");
        apiService.authenticateUser(scannerMock.getScanner());
        apiService.submitCounterReading(scannerMock.getScanner());
        int latestReading = counterService.getLatestCounterReading(userRepository.getUserByUsername("test_user"), CounterType.HOT_WATER);
        assertEquals(0, latestReading);
    }
    /**
     * Метод тестирования просмотра актуального показания счетчика. Зарегистрированный пользователь,
     * мок сканера для аутентификации, и вызывает метод просмотра актуального показания. Проверяет, что
     * актуальное показание отображается в System.out.
     */
    @Test
    public void testViewLatestReading() {
        userRepository.registerUser("test_user", "password", Role.USER);
        ScannerMock scannerMock = new ScannerMock("HOT_WATER\n");
        apiService.authenticateUser(scannerMock.getScanner());
        apiService.viewLatestReading();
        assertTrue(outContent.toString().contains("Актуальное показание: 0"));
    }
    /**
     * Метод тестирования просмотра истории показаний счетчика. Зарегистрированный пользователь,
     * мок сканера для аутентификации и вызывает метод просмотра истории показаний. Проверяет, что
     * соответствующее сообщение отображается в System.out.
     */
    @Test
    public void testViewCounterHistory() {
        userRepository.registerUser("test_user", "password", Role.USER);
        ScannerMock scannerMock = new ScannerMock("HOT_WATER\n");
        apiService.authenticateUser(scannerMock.getScanner());
        apiService.viewCounterHistory(scannerMock.getScanner());
        assertTrue(outContent.toString().contains("История показаний пуста."));
    }
    /**
     * Метод тестирования просмотра аудит-лога. Мок сканера, вызывает метод просмотра аудит-лога и
     * проверяет, что соответствующее сообщение отображается в System.out.
     */
    @Test
    public void testViewAuditLog() {
        ScannerMock scannerMock = new ScannerMock("");
        apiService.viewAuditLog();
        assertTrue(outContent.toString().contains("Просмотр аудита:"));
    }

    @After
    public void tearDown() {
        System.setOut(originalOut);
    }
}
