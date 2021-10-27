package us.byteb.metrics;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import io.javalin.Javalin;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import java.time.Duration;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class App {

  private static final PrometheusMeterRegistry REGISTRY =
      new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
  private static final Random RANDOM = new Random();
  public static final Duration UPDATE_INTERVAL = Duration.ofSeconds(3);

  public static void main(String[] args) {
    final Javalin app = Javalin.create().start(8080);
    app.get("/metrics", ctx -> ctx.result(REGISTRY.scrape()));

    final Counter randomCounter = REGISTRY.counter("randomCount");
    final AtomicInteger randomGaugeValueFirst = new AtomicInteger();
    final AtomicInteger randomGaugeValueSecond = new AtomicInteger();
    REGISTRY.gauge("randomGauge", Set.of(Tag.of("type", "first")), randomGaugeValueFirst);
    REGISTRY.gauge("randomGauge", Set.of(Tag.of("type", "second")), randomGaugeValueSecond);
    final Timer executionTimer =
        Timer.builder("randomTimer").publishPercentiles(.5, .95).register(REGISTRY);

    Executors.newScheduledThreadPool(1)
        .scheduleAtFixedRate(
            () -> {
              randomCounter.increment(randomInt());
              randomGaugeValueFirst.set(randomInt());
              randomGaugeValueSecond.set(randomInt());
              executionTimer.record(randomInt(), MILLISECONDS);
            },
            0,
            UPDATE_INTERVAL.toMillis(),
            MILLISECONDS);
  }

  private static int randomInt() {
    return Math.abs(RANDOM.nextInt()) % 100;
  }
}
