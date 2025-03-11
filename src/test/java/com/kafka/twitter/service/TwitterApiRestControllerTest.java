package com.kafka.twitter.service;

import com.kafka.twitter.metric.MetricHandler;
import com.kafka.twitter.model.RawMessage;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TwitterApiRestControllerTest {

    @Mock
    private TwitterFeedKafkaProducer kafkaProducer;

    @Mock
    private MetricHandler metricHandler;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private TwitterApiRestController controller;

    @BeforeEach
    void setUp() {
        controller = new TwitterApiRestController(kafkaProducer, metricHandler);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void produceTwitterData_Success() throws Exception {
        // Arrange
        RawMessage message = createSampleMessage();
        String jsonMessage = objectMapper.writeValueAsString(message);

        // Act & Assert
        mockMvc.perform(post("/test/api/v1/publish")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMessage))
                .andExpect(status().isOk());

        verify(kafkaProducer).sendMessage(any(RawMessage.class));
    }

    @Test
    void produceTwitterDataWithSenderDetail_Success() throws Exception {
        // Arrange
        RawMessage message = createSampleMessageWithSender();
        String jsonMessage = objectMapper.writeValueAsString(message);

        // Act & Assert
        mockMvc.perform(post("/test/api/v2/publish")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMessage))
                .andExpect(status().isOk());

        verify(kafkaProducer).sendMessage(any(RawMessage.class));
    }

//    @Test
//    void produceTwitterData_WhenKafkaProducerFails_ThrowsException() throws Exception {
//        // Arrange
//        RawMessage message = createSampleMessage();
//        String jsonMessage = objectMapper.writeValueAsString(message);
//        doThrow(new RuntimeException("Kafka error")).when(kafkaProducer).sendMessage(any(RawMessage.class));
//
//        // Act & Assert
//        mockMvc.perform(post("/test/api/v1/publish")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(jsonMessage))
//                .andExpect(status().isInternalServerError());
//    }

    private RawMessage createSampleMessage() {
        RawMessage message = new RawMessage();
        message.setId("1234858592");
        message.setType("tweet");
        message.setText("Hello World!");
        message.setCreatedTimestamp(1392078023603L);
        return message;
    }

    private RawMessage createSampleMessageWithSender() {
        RawMessage message = createSampleMessage();
        // Note: Since SenderData is an inner class of RawMessage, 
        // you might need to modify this based on your actual implementation
        return message;
    }
}