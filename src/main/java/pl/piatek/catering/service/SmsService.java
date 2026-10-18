package pl.piatek.catering.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.piatek.catering.dto.sms.SmsStatus;
import pl.piatek.catering.dto.sms.SmsType;
import pl.piatek.catering.entity.SmsLog;
import pl.piatek.catering.repository.SmsLogRepository;
import pl.piatek.catering.service.sender.PhoneNumbers;
import pl.piatek.catering.service.sender.SmsSender;

@Service
@RequiredArgsConstructor
public class SmsService {

    private final SmsSender sender;
    private final SmsLogRepository logRepository;

    /** Osobna transakcja: log zapisze się niezależnie od tego, co dzieje się z zamówieniem. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void send(Long orderId, String phone, SmsType type, String message) {
        String normalized = PhoneNumbers.normalize(phone);

        SmsLog entry = new SmsLog();
        entry.setOrderId(orderId);
        entry.setPhone(normalized);
        entry.setType(type);
        entry.setMessage(message);

        try {
            sender.send(normalized, message);
            entry.setStatus(SmsStatus.SENT);
        } catch (Exception e) {
            entry.setStatus(SmsStatus.FAILED);
            String error = String.valueOf(e.getMessage());
            entry.setError(error.length() > 500 ? error.substring(0, 500) : error);
        }
        logRepository.save(entry);
    }
}