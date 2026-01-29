package ru.practicum.stats.kafka.deserializer;

import ru.practicum.client.avro.EventSimilarityAvro;

public class EventsSimilarityDeserializer extends BaseAvroDeserializer<EventSimilarityAvro> {
    public EventsSimilarityDeserializer() {super(EventSimilarityAvro.getClassSchema());}
}
