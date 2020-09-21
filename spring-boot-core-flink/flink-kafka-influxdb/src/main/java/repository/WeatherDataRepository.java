package repository;

import org.apache.commons.lang3.Validate;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;

public class WeatherDataRepository {

    private final InfluxDB influxDB;

    public WeatherDataRepository() {
        this.influxDB = InfluxDBFactory.connect("http://localhost:8086", "root", "root");
    }

    private static WeatherDataRepository instance;

    public static WeatherDataRepository getInstance() {
        if (instance == null) {
            synchronized (WeatherDataRepository.class) {
                if (instance == null) {
                    instance = new WeatherDataRepository();
                }
            }
        }
        return instance;
    }

    public void createDataBase(String databaseName) {
        Validate.notNull(databaseName, "database must not be null");
        influxDB.createDatabase(databaseName);
    }

    public void write(String databaseName, String records) {
        Validate.notNull(databaseName, "databaseName must not be null");
        Validate.notEmpty(records, "records must not be empty");
        influxDB.write(databaseName, "default", InfluxDB.ConsistencyLevel.ALL, records);
    }
}
