package pl.piatek.catering.service.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "sms", name = "provider", havingValue = "log", matchIfMissing = true)
public class LoggingSmsSender implements SmsSender {

    @Override
    public void send(String phone, String message) {
        log.info("[SMS -> {}] {}", phone, message);
    }
}
