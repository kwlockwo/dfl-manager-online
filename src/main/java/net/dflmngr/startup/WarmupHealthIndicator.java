package net.dflmngr.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import net.dflmngr.services.LadderService;
import net.dflmngr.services.ResultService;

@Component
public class WarmupHealthIndicator implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(WarmupHealthIndicator.class);

    private final LadderService ladderService;
    private final ResultService resultService;

    private volatile boolean warmedUp = false;

    public WarmupHealthIndicator(LadderService ladderService, ResultService resultService) {
        this.ladderService = ladderService;
        this.resultService = resultService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void warmUp() {
        logger.info("Starting warmup...");
        try {
            ladderService.getLadder();
            ladderService.getLiveLadder();
            resultService.getCurrentResults();
            logger.info("Warmup complete");
        } catch (Exception e) {
            logger.warn("Warmup encountered an error: {}", e.getMessage());
        } finally {
            warmedUp = true;
        }
    }

    @Override
    public Health health() {
        if (warmedUp) {
            return Health.up().build();
        }
        return Health.down().withDetail("reason", "warming up").build();
    }
}
