package avro;


import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.commons.lang3.Validate;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 自定义AVRO反序列化工具
 *
 * @param <T>
 */
public class AvroDeserializationSchema<T> implements DeserializationSchema<T> {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(AvroDeserializationSchema.class);

    private final Class<T> avroType;

    private transient Schema schema;

    private transient DatumReader<T> reader;

    public AvroDeserializationSchema(Class<T> avroType, Schema schema) {
        Validate.notNull("avroType not be null");
        Validate.notNull("schema must not be null");
        this.avroType = avroType;
        this.schema = schema;
    }

    @Override
    public T deserialize(byte[] message) throws IOException {
        LOGGER.info("Will deserialize message to " + avroType.getSimpleName());
        try {
            if (reader == null) {
                if (SpecificRecordBase.class.isAssignableFrom(avroType)) {
                    reader = new SpecificDatumReader<>(avroType);
                } else {
                    reader = new ReflectDatumReader<>(avroType);
                }
            }
            if (schema == null) {
                schema = new Schema.Parser().parse(this.getClass().getResourceAsStream("./WeatherData.avsc"));
            }
            Decoder decoder = DecoderFactory.get().jsonDecoder(schema, new String(message));
            return reader.read(null, decoder);
        } catch (IOException e) {
            LOGGER.error("Failed to deserialize message to " + avroType.getSimpleName());
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isEndOfStream(T nextElement) {
        return false;
    }

    @Override
    public TypeInformation<T> getProducedType() {
        return TypeInformation.of(avroType);
//        return TypeExtractor.getForClass(avroType);
    }
}
