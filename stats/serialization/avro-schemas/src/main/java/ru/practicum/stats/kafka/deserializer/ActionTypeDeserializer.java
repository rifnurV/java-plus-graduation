package ru.practicum.stats.kafka.deserializer;

import ru.practicum.client.avro.UserActionAvro;

public class ActionTypeDeserializer extends BaseAvroDeserializer<UserActionAvro> {
    public ActionTypeDeserializer() {super(UserActionAvro.getClassSchema());}
}
