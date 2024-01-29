package api;

import audit.AuditService;
import counters.CounterService;
import counters.CounterType;
import exceptions.AuthorizationException;
import users.Role;
import users.User;
import users.UserRepository;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Веб-сервис для подачи показаний счетчиков отопления, горячей и холодной воды.
 */
public class ApiService {
    private UserRepository userRepository;
    private CounterService counterService;
    private User currentUser;
    /**
     * Конструктор для инициализации ApiService.
     *
     * @param userRepository Репозиторий пользователей.
     * @param counterService Сервис счетчиков.
     */

    public ApiService(UserRepository userRepository, CounterService counterService) {
        this.userRepository = userRepository;
        this.counterService = counterService;
    }

    /**
     * Запускает веб-сервис для взаимодействия с пользователем.
     */
    public void start() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            printMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    registerUser(scanner);
                    break;
                case 2:
                    authenticateUser(scanner);
                    break;
                case 3:
                    submitCounterReading(scanner);
                    break;
                case 4:
                    viewLatestReading();
                    break;
                case 5:
                    viewCounterHistory(scanner);
                    break;
                case 6:
                    viewAuditLog();
                    break;
                case 7:
                    exit = true;
                    break;
                default:
                    System.out.println("Некорректный выбор. Попробуйте снова.");
            }
        }
    }

    /**
     * Вывод меню.
     */
    private void printMenu() {
        System.out.println("1. Регистрация");
        System.out.println("2. Авторизация");
        System.out.println("3. Подача показаний");
        System.out.println("4. Просмотр актуальных показаний");
        System.out.println("5. Просмотр истории показаний");
        System.out.println("6. Просмотр аудита");
        System.out.println("7. Выход");
        System.out.print("Выберите действие: ");
    }
    /**
     * Регистрация User.
     */

    public void registerUser(Scanner scanner) {
        System.out.print("Введите логин: ");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();
        System.out.print("Выберите роль (USER или ADMIN): ");
        String roleStr = scanner.nextLine();

        try {
            Role role = Role.valueOf(roleStr.toUpperCase());
            userRepository.registerUser(username, password, role);
            System.out.println("Пользователь зарегистрирован успешно.");
            AuditService.log("Регистрация нового пользователя: " + username);
        } catch (IllegalArgumentException e) {
            System.out.println("Некорректная роль. Регистрация не выполнена.");
        }
    }

    /**
     * Авторизация User.
     */
    public void authenticateUser(Scanner scanner) {
        System.out.print("Введите логин: ");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        try {
            currentUser = userRepository.authenticateUser(username, password);
            System.out.println("Авторизация успешна.");
            AuditService.log("Пользователь авторизован: " + username);
        } catch (AuthorizationException e) {
            System.out.println("Неверные учетные данные.");
        }
    }
    /**
     * Метод для подачи показаний счетчика. Пользователь выбирает тип счетчика (отопление, горячая вода, холодная вода),
     * месяц и вводит соответствующее показание. Затем вызывается сервис подачи показаний, и в случае успешной подачи,
     * производится запись в аудит-лог.
     *
     * @param scanner Сканер для считывания данных от пользователя.
     */

    public void submitCounterReading(Scanner scanner) {
        if (currentUser == null) {
            System.out.println("Необходима авторизация.");
            return;
        }

        System.out.print("Выберите тип счетчика (HEATING, HOT_WATER, COLD_WATER): ");
        String counterTypeStr = scanner.nextLine();

        try {
            CounterType counterType = CounterType.valueOf(counterTypeStr.toUpperCase());

            System.out.print("Введите месяц (1-12): ");
            int monthValue = scanner.nextInt();
            Month month = Month.of(monthValue);

            System.out.print("Введите показание счетчика: ");
            int value = scanner.nextInt();

            counterService.submitCounterReading(currentUser, counterType, month, value);
            System.out.println("Показания успешно поданы.");
            AuditService.log("Пользователь подал показания для " + counterType + " за " + month);
        } catch (IllegalArgumentException e) {
            System.out.println("Некорректный тип счетчика. Подача показаний не выполнена.");
        }
    }

    /**
     * Метод для просмотра актуального показания счетчика. Пользователь выбирает тип счетчика (отопление, горячая вода, холодная вода),
     * и метод вызывает соответствующий сервис для получения актуального показания. Результат выводится на экран.
     */
    public void viewLatestReading() {
        if (currentUser == null) {
            System.out.println("Необходима авторизация.");
            return;
        }

        System.out.print("Выберите тип счетчика (HEATING, HOT_WATER, COLD_WATER): ");
        String counterTypeStr = new Scanner(System.in).nextLine();

        try {
            CounterType counterType = CounterType.valueOf(counterTypeStr.toUpperCase());
            int latestReading = counterService.getLatestCounterReading(currentUser, counterType);
            System.out.println("Актуальное показание: " + latestReading);
        } catch (IllegalArgumentException e) {
            System.out.println("Некорректный тип счетчика. Просмотр не выполнен.");
        }
    }

    /**
     * Метод для просмотра истории показаний счетчика за разные месяцы. Пользователь выбирает тип счетчика (отопление, горячая вода, холодная вода),
     * и метод вызывает соответствующий сервис для получения истории показаний. Результат выводится на экран.
     *
     * @param scanner Сканер для считывания данных от пользователя.
     */
    public void viewCounterHistory(Scanner scanner) {
        if (currentUser == null) {
            System.out.println("Необходима авторизация.");
            return;
        }

        System.out.print("Выберите тип счетчика (HEATING, HOT_WATER, COLD_WATER): ");
        String counterTypeStr = scanner.nextLine();

        try {
            CounterType counterType = CounterType.valueOf(counterTypeStr.toUpperCase());
            Map<Month, Integer> history = counterService.getCounterHistory(currentUser, counterType);

            if (history.isEmpty()) {
                System.out.println("История показаний пуста.");
            } else {
                System.out.println("История показаний:");
                for (Map.Entry<Month, Integer> entry : history.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Некорректный тип счетчика. Просмотр не выполнен.");
        }
    }
    /**
     * Метод для просмотра записей аудит-лога. Выводит на экран все записи, если они есть.
     * Если аудит-лог пуст, выводится соответствующее сообщение.
     */

    public void viewAuditLog() {
        List<String> auditLogEntries = retrieveAuditLog();

        if (auditLogEntries.isEmpty()) {
            System.out.println("Аудит-лог пуст.");
        } else {
            System.out.println("Просмотр аудита:");
            for (String entry : auditLogEntries) {
                System.out.println(entry);
            }
        }
    }
    /**
     * Метод для получения записей аудит-лога. Реализация может быть разной в зависимости от того,
     * каким образом ведется логирование действий пользователей. В данном примере используется
     * фиктивный метод retrieveAuditLog(), который возвращает фиктивные записи.
     *
     * @return Список строк, представляющих записи аудит-лога.
     */

    private List<String> retrieveAuditLog() {

        List<String> auditLog = new ArrayList<>();
        auditLog.add("2022-01-28 12:30: Пользователь 'john_doe' зарегистрирован.");
        auditLog.add("2022-01-29 14:45: Пользователь 'alice' авторизован.");
        auditLog.add("2022-01-30 16:00: Подача показаний счетчика воды для пользователя 'bob'.");

        return auditLog;
    }

}
