package com.learn.test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class SimpleTest {

	public SimpleTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		System.out.println(testPalin("dimpmid"));
		IntStream.range(1, 10).filter(i -> i > 6).forEach(System.out::println);
		List<String> str = Arrays.asList("hello", "world", "of", "stream");
		str.forEach(System.out::println);
		str.stream().map(st -> st.toUpperCase() + " ").forEach(System.out::print);
		printFib(10);

	}

	private static void printFib(int count) {
		int n1 = 0, n2 = 1, n3;
		int temp;
		System.out.print(n1 + "," + n2 + ",");
		while (count > 0) {
			for (int i = 2; i < count; i++) {
				n3 = n1 + n2;
				n1 = n2;
				n2 = n3;
				System.out.print(n3 + ",");
				count--;
			}
		}
	}

	public static boolean testPalin(String name) {
		System.out.println("Hello Palin");
		int length = name.length();
		System.out.println(length);
		int n = length / 2;
		for (int i = 0; i < n; i++) {
			if (name.charAt(i) != name.charAt(length - i - 1)) {
				return false;
			}
		}
		return true;
	}
	
	private static void annonymous() {
		Runnable run = new Runnable() {
			@Override
			public void run() {
				System.out.println();
			}
		};
	}

}
