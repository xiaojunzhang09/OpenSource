package com.ibeetl.code.ch04;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 5)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Threads(1)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class HeavyweightObjectTest {
	/*重用*/
	ObjectMapper objectMapper = new ObjectMapper();
	Data data = null;
	@Benchmark
	public Data createNew() throws IOException {
		/*每次都构造*/
		ObjectMapper encodeMapper = new ObjectMapper();
		String json = encodeMapper.writeValueAsString(data);
		ObjectMapper decodeMapper = new ObjectMapper();
		Data myData = decodeMapper.readValue(json,Data.class);
		return myData;
	}

	@Benchmark
	public Data reused() throws IOException {
		String json = objectMapper.writeValueAsString(data);
		Data myData = objectMapper.readValue(json,Data.class);
		return myData;
	}

	@Setup
	public void init(){
		data = new Data();
		data.setAge(18);
		data.setId(123);
		data.setName("lijiazhi");
	}



	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(HeavyweightObjectTest.class.getSimpleName())
				.forks(1)
				.build();
		new Runner(opt).run();
	}
	static class Data{
		String name;
		Integer age;
		Integer id;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getAge() {
			return age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}
	}
}
