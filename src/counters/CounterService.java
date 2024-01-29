package counters;

import exceptions.CounterSubmissionException;
import users.User;

import java.time.Month;
import java.util.HashMap;
import java.util.Map;

/**
 * Сервис для управления счетчиками.
 */
public class CounterService {
    private Map<User, Map<CounterType, CounterReading>> userCounters = new HashMap<>();

    /**
     * Подает показания счетчика для указанного пользователя, типа счетчика, месяца и значения.
     *
     * @param user        Пользователь.
     * @param counterType Тип счетчика.
     * @param month       Месяц.
     * @param value       Значение счетчика.
     * @throws CounterSubmissionException Исключение, если подача показаний не удалась.
     */
    public void submitCounterReading(User user, CounterType counterType, Month month, int value) {
        if (!userCounters.containsKey(user)) {
            userCounters.put(user, new HashMap<>());
        }

        Map<CounterType, CounterReading> userReadings = userCounters.get(user);

        if (!userReadings.containsKey(counterType)) {
            userReadings.put(counterType, new CounterReading());
        }

        CounterReading counterReading = userReadings.get(counterType);

        if (counterReading.getReadingForMonth(month) > 0) {
            throw new CounterSubmissionException("Показания за этот месяц уже были поданы.");
        }

        counterReading.submitReading(month, value);
    }
    /**
     * Возвращает последнее показание счетчика для указанного пользователя и типа счетчика.
     *
     * @param user        Пользователь.
     * @param counterType Тип счетчика.
     * @return Последнее показание счетчика.
     */

    public int getLatestCounterReading(User user, CounterType counterType) {
        if (userCounters.containsKey(user) && userCounters.get(user).containsKey(counterType)) {
            return userCounters.get(user).get(counterType).getLatestReading();
        }
        return 0;
    }
    /**
     * Возвращает историю показаний счетчика для указанного пользователя и типа счетчика.
     *
     * @param user        Пользователь.
     * @param counterType Тип счетчика.
     * @return История показаний счетчика.
     */

    public Map<Month, Integer> getCounterHistory(User user, CounterType counterType) {
        if (userCounters.containsKey(user) && userCounters.get(user).containsKey(counterType)) {
            return userCounters.get(user).get(counterType).getReadingsHistory();
        }
        return new HashMap<>();
    }
}