package users;





import exceptions.AuthorizationException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
/**
 * Репозиторий для управления пользователями.
 */

public class UserRepository {
    private Map<String, User> usersByUsername = new HashMap<>();
    private Map<Integer, User> usersById = new HashMap<>();
    /**
     * Регистрирует нового пользователя.
     *
     * @param username Имя пользователя.
     * @param password Пароль пользователя.
     * @param role     Роль пользователя (USER или ADMIN).
     */

    public void registerUser(String username, String password, Role role) {
        if (usersByUsername.containsKey(username)) {
            throw new AuthorizationException("Пользователь с таким именем уже зарегистрирован.");
        }

        User user = new User(generateUserId(), username, password, role);
        usersByUsername.put(username, user);
        usersById.put(user.getId(), user);
    }
    /**
     * Авторизует пользователя по имени пользователя и паролю.
     *
     * @param username Имя пользователя.
     * @param password Пароль пользователя.
     * @return Авторизованный пользователь.
     * @throws AuthorizationException Исключение, если авторизация не удалась.
     */

    public User authenticateUser(String username, String password) {
        User user = usersByUsername.get(username);
        if (user != null && user.authenticate(password)) {
            return user;
        } else {
            throw new AuthorizationException("Неверные учетные данные.");
        }
    }
    /**
     * Возвращает пользователя по его идентификатору.
     *
     * @param userId Идентификатор пользователя.
     * @return Пользователь с указанным идентификатором.
     */

    public User getUserById(int userId) {
        return usersById.get(userId);
    }

    /**
     * Генерирует уникальный идентификатор пользователя на основе случайного UUID.
     *
     * @return Уникальный идентификатор пользователя в виде хэш-кода UUID.
     */
    private int generateUserId() {
        return UUID.randomUUID().hashCode();
    }
    /**
     * Получает пользователя по его имени пользователя (логину).
     *
     * @param username Имя пользователя (логин), по которому производится поиск.
     * @return Объект пользователя с указанным именем пользователя или {@code null},
     *         если пользователь с указанным именем не найден.
     */
    public User getUserByUsername(String username) {
        return usersByUsername.get(username);
    }
}