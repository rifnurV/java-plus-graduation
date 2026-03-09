package ru.practicum.stats.kafka.deserializer;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

public class EventsSimilarityDeserializer extends BaseAvroDeserializer<EventSimilarityAvro> {
    public EventsSimilarityDeserializer() {super(EventSimilarityAvro.getClassSchema());}
}
