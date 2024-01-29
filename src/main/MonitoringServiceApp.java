package main;

import api.ApiService;
import counters.CounterService;
import users.UserRepository;
/**
 * Класс, представляющий приложение мониторинга, которое запускает веб-сервис {@link api.ApiService}
 * для взаимодействия с пользователями, подачи показаний счетчиков и просмотра соответствующей информации.
 */

public class MonitoringServiceApp {
    public static void main(String[] args) {
        UserRepository userRepository = new UserRepository();
        CounterService counterService = new CounterService();
        ApiService apiService = new ApiService(userRepository, counterService);


        apiService.start();
    }
}
