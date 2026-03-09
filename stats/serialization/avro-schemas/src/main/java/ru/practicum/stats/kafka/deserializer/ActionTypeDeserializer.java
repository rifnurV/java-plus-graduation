package ru.practicum.stats.kafka.deserializer;


import ru.practicum.ewm.stats.avro.UserActionAvro;

public class ActionTypeDeserializer extends BaseAvroDeserializer<UserActionAvro> {
    public ActionTypeDeserializer() {super(UserActionAvro.getClassSchema());}
}
