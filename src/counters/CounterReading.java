package counters;

import java.time.Month;
import java.util.HashMap;
import java.util.Map;
/**
 * Класс, представляющий сущность для хранения и управления показаниями счетчика за разные месяцы.
 */
public class CounterReading {
    private Map<Month, Integer> readings = new HashMap<>();
    /**
     * Метод для подачи показания счетчика для указанного месяца.
     *
     * @param month Месяц, для которого подается показание.
     * @param value Показание счетчика.
     */
    public void submitReading(Month month, int value) {
        readings.put(month, value);
    }
    /**
     * Получение показания счетчика для указанного месяца.
     *
     * @param month Месяц, для которого запрашивается показание.
     * @return Показание счетчика для указанного месяца или 0, если показание отсутствует.
     */
    public int getReadingForMonth(Month month) {
        return readings.getOrDefault(month, 0);
    }
    /**
     * Получение истории показаний счетчика за все месяцы.
     *
     * @return Копия карты с показаниями за разные месяцы.
     */
    public Map<Month, Integer> getReadingsHistory() {
        return new HashMap<>(readings);
    }

    /**
     * Получение последнего зарегистрированного показания счетчика.
     *
     * @return Последнее зарегистрированное показание счетчика.
     */
    public int getLatestReading() {
        Month latestMonth = Month.JANUARY; // начальное значение
        for (Month month : readings.keySet()) {
            if (month.compareTo(latestMonth) > 0) {
                latestMonth = month;
            }
        }
        return getReadingForMonth(latestMonth);
    }
}
