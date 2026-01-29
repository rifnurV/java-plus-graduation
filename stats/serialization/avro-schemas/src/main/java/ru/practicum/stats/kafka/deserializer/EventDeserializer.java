package ru.practicum.stats.kafka.deserializer;

import ru.practicum.client.avro.EventAvro;

public class EventDeserializer extends BaseAvroDeserializer<EventAvro> {
    public EventDeserializer() {super(EventAvro.getClassSchema());}
}
