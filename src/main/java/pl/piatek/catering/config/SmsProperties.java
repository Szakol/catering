package pl.piatek.catering.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sms")
public record SmsProperties(String provider, int paymentReminderAfterHours) {
}