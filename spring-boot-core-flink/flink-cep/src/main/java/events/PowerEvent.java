package events;

/**
 * 功耗事件，包含电压
 */
public class PowerEvent extends MonitoringEvent {
    private double voltage;

    public PowerEvent(int rackID, double voltage) {
        super(rackID);

        this.voltage = voltage;
    }

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PowerEvent) {
            PowerEvent powerEvent = (PowerEvent) obj;
            return powerEvent.canEquals(this) && super.equals(powerEvent) && voltage == powerEvent.voltage;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return 41 * super.hashCode() + Double.hashCode(voltage);
    }

    @Override
    public boolean canEquals(Object obj) {
        return obj instanceof PowerEvent;
    }

    @Override
    public String toString() {
        return "PowerEvent(" + getRackID() + ", " + voltage + ")";
    }
}
