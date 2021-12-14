package com.example.sleuthmultipleoutputstreams;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
class SleuthMultipleOutputStreamsApplicationTests {

	@Autowired
	private InputDestination input;

	@Autowired
	private OutputDestination output;

	@Test
	void contextLoads() {
		input.send(MessageBuilder.withPayload("1").build());
		await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
			Message<byte[]> received = output.receive(200L, "odd");
			assertThat(received).isNotNull();
		});
	}

	@TestConfiguration
	@Import({ TestChannelBinderConfiguration.class })
	static class Testconfig {

	}

}
