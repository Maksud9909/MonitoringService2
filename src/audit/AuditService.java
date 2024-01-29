package audit;

/**
 * Сервис для аудита действий пользователей.
 */
public class AuditService {
    /**
     * Записывает действие в аудит-лог.
     *
     * @param action Действие пользователя.
     */
    public static void log(String action) {
        System.out.println("Аудит: " + action);
    }
}
