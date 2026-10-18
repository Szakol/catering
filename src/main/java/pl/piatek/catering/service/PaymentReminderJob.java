package pl.piatek.catering.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentReminderJob {

    private final PaymentReminderService service;

    /** Co godzinę w godzinach 9-20, żeby nie wysyłać SMS-ów w nocy. */
    @Scheduled(cron = "0 0 9-20 * * *", zone = "Europe/Warsaw")
    public void run() {
        service.sendDueReminders();
    }
}