package com.example.sleuthmultipleoutputstreams;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.function.Function;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SleuthMultipleOutputStreamsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SleuthMultipleOutputStreamsApplication.class, args);
	}

	@Bean
	public Function<Flux<Integer>, Tuple2<Flux<String>, Flux<String>>> scatter() {
		return flux -> {
			Flux<Integer> connectedFlux = flux.publish().autoConnect(2);
			Sinks.Many<String> even = Sinks.many().unicast().onBackpressureBuffer(new LinkedBlockingQueue<>(1));
			Sinks.Many<String> odd = Sinks.many().unicast().onBackpressureBuffer(new LinkedBlockingQueue<>(1));
			Flux<Integer> evenFlux = connectedFlux.filter(number -> number % 2 == 0).doOnNext(number -> even.emitNext("EVEN: " + number, Sinks.EmitFailureHandler.FAIL_FAST));
			Flux<Integer> oddFlux = connectedFlux.filter(number -> number % 2 != 0).doOnNext(number -> odd.emitNext("ODD: " + number, Sinks.EmitFailureHandler.FAIL_FAST));

			return Tuples.of(even.asFlux().doOnSubscribe(x -> evenFlux.subscribe()), odd.asFlux().doOnSubscribe(x -> oddFlux.subscribe()));
		};
	}

}
