package com.learn.test;

public class PrimeNumTest {

	public PrimeNumTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
		int input = 101;
		if(input == 0 || input == 1) System.out.println("oooops, not a prime number");
		else {
			if(input % 2 == 0) System.out.println("oooops, not a prime number");
			else System.out.println("It's a prime number");
		}
	}

}
