package pl.piatek.catering.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.piatek.catering.entity.PricingSettings;

public interface PricingSettingsRepository extends JpaRepository<PricingSettings, Long> {
}