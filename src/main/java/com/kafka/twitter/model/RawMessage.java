package com.kafka.twitter.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RawMessage {

    private String type;
    private String id;
    @JsonProperty("created_timestamp")
    private long createdTimestamp;
    private String text;
    private String ingestionDateTime;

    private SenderData senderData;

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
        this.ingestionDateTime = getFormattedTimestamp();
    }

    public String getFormattedTimestamp() {
        Instant instant = Instant.ofEpochMilli(createdTimestamp);
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(instant);
    }

   @Override
    public String toString() {
        return String.format("{type='%s', id='%s', createdTimestamp=%d, text='%s', ingestionDateTime='%s'%s}",
                type, id, createdTimestamp, text, ingestionDateTime,
                senderData != null ? ", senderData=" + senderData : "");
    }


    @Data
    private class SenderData {
        private String id;
        private String name;
        @JsonProperty("screen_name")
        private String screenName;

        @Override
        public String toString() {
            return String.format("{id='%s', name='%s', screenName='%s'}", id, name, screenName);
        }
    }
}
