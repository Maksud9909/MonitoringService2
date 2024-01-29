package exceptions;
/**
 * Исключение, выбрасываемое при возникновении ошибки в процессе подачи показаний счетчика.
 */
public class CounterSubmissionException extends RuntimeException {
    /**
     * Конструктор исключения с указанием сообщения об ошибке.
     *
     * @param message Сообщение об ошибке.
     */
    public CounterSubmissionException(String message) {
        super(message);
    }
}
