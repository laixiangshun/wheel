package sources;

import events.MonitoringEvent;
import events.PowerEvent;
import events.TemperatureEvent;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 监控告警事件source
 */
public class MonitoringEventSource extends RichParallelSourceFunction<MonitoringEvent> {
    private boolean running = true;

    private final int maxRackId;

    private final long pause;

    private final double temperatureRatio;

    private final double powerStd;

    private final double powerMean;

    private final double temperatureStd;

    private final double temperatureMean;

    private int shard;

    private int offset;

    public MonitoringEventSource(
            int maxRackId,
            long pause,
            double temperatureRatio,
            double powerStd,
            double powerMean,
            double temperatureStd,
            double temperatureMean) {
        this.maxRackId = maxRackId;
        this.pause = pause;
        this.temperatureRatio = temperatureRatio;
        this.powerMean = powerMean;
        this.powerStd = powerStd;
        this.temperatureMean = temperatureMean;
        this.temperatureStd = temperatureStd;
    }

    @Override
    public void open(Configuration configuration) throws Exception {
        int numberTasks = getRuntimeContext().getNumberOfParallelSubtasks();
        int index = getRuntimeContext().getIndexOfThisSubtask();
        offset = (int) ((double) maxRackId / numberTasks * index);
        shard = (int) ((double) maxRackId / numberTasks * (index + 1)) - offset;
    }

    @Override
    public void run(SourceContext<MonitoringEvent> ctx) throws Exception {
        while (running) {
            MonitoringEvent monitoringEvent;

            final ThreadLocalRandom random = ThreadLocalRandom.current();
            if (shard > 0) {
                int rackId = random.nextInt(shard) + offset;
                if (random.nextDouble() >= temperatureRatio) {
                    double power = random.nextGaussian() * powerStd + powerMean;
                    monitoringEvent = new PowerEvent(rackId, power);
                } else {
                    double temperature = random.nextGaussian() * temperatureStd + temperatureMean;
                    monitoringEvent = new TemperatureEvent(rackId, temperature);
                }
                ctx.collect(monitoringEvent);
            }
            Thread.sleep(pause);
        }
    }

    @Override
    public void cancel() {
        this.running = false;
    }
}
