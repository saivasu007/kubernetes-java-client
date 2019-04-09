package com.learn.test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StreamTest {

	public StreamTest() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) {
		Integer[] intArray = {23,45,12,8,1,6,2,785};
		List<Integer> intList = Arrays.asList(intArray);
		System.out.println(intList.stream().filter(num -> num < 100).collect(Collectors.toList()));
		Optional.ofNullable(intList);
		System.out.println(intList.stream().findAny().orElse(100));
	}

}
