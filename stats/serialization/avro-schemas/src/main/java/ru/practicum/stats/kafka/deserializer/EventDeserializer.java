package ru.practicum.stats.kafka.deserializer;


import ru.practicum.ewm.stats.avro.EventAvro;

public class EventDeserializer extends BaseAvroDeserializer<EventAvro> {
    public EventDeserializer() {super(EventAvro.getClassSchema());}
}
