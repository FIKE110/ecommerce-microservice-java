package com.fortune;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private String id;
    private EventType eventType;
    private Map<String,String> message=new HashMap<>();

    public String mapMessageString(){
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String,String> entry : message.entrySet()) {
            stringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString(){
        return String.format("Event [id=%s, eventType=%s, message=%s]", id, eventType, mapMessageString());
     }
}
