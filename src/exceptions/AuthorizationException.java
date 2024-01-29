package exceptions;
/**
 * Исключение, выбрасываемое при возникновении ошибки в процессе авторизации пользователя.
 */
public class AuthorizationException extends RuntimeException {
    /**
            * Конструктор исключения с указанием сообщения об ошибке.
     *
             * @param message Сообщение об ошибке.
            */
    public AuthorizationException(String message) {
        super(message);
    }
}