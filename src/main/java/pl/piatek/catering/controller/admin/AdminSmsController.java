package pl.piatek.catering.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.piatek.catering.dto.sms.SmsLogResponse;
import pl.piatek.catering.repository.SmsLogRepository;
import pl.piatek.catering.service.PaymentReminderService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminSmsController {

    private final SmsLogRepository logRepository;
    private final PaymentReminderService reminderService;

    @GetMapping("/orders/{id}/sms")
    public List<SmsLogResponse> history(@PathVariable Long id) {
        return logRepository.findByOrderIdOrderByCreatedAtDesc(id).stream()
                .map(SmsLogResponse::from).toList();
    }

    @PostMapping("/sms/payment-reminders/run")
    public Map<String, Integer> runReminders() {
        return Map.of("sent", reminderService.sendDueReminders());
    }
}