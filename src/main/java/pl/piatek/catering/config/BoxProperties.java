package pl.piatek.catering.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "box")
public record BoxProperties(int minItems, int maxItems) {
}