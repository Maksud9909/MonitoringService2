package test;


import java.util.Scanner;
/**
 * Мок-класс для замены стандартного {@link java.util.Scanner}, используемого для ввода данных в консоль.
 * Предназначен для использования в тестах для имитации ввода пользовательских данных.
 */
public class ScannerMock   {
    private final Scanner scanner;

    /**
     * Получает объект мок-сканера.
     *
     * @return Объект {@link java.util.Scanner}, представляющий мок-сканер с предварительно заданным входным потоком данных.
     */
    public ScannerMock(String input) {
        this.scanner = new Scanner(input);
    }

    public Scanner getScanner() {
        return scanner;
    }

    /**
     * Обработка записи в аудит-логе. Метод позволяет проверять, была ли вызвана операция добавления записи в аудит-лог.
     *
     * @param logEntry Запись, которую нужно обработать или проверить.
     */
    public void handleAuditLog(String logEntry) {
    }

}
