package pl.piatek.catering.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "business")
public record BusinessProperties(String name, String paymentInfo) {
}
