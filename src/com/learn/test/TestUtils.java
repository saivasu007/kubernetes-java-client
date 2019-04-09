package com.learn.test;

import java.util.Stack;

public class TestUtils {

	public TestUtils() {
		// TODO Auto-generated constructor stub
	}
	
	
	public static void armStrongNum(int inputNum) {
		System.out.println("Armstrong Number calculation");
		int q,r,finalNum=0;
		int actualInput = inputNum;
		while(inputNum > 0) {
			q = inputNum%10;
			inputNum = inputNum/10;
			finalNum = finalNum+(q*q*q);
		}
		System.out.println("Armstrong Number is : "+finalNum);
		if(actualInput == finalNum) {
			System.out.println("Yes, Given input is Armstrong number");
		} else {
			System.out.println("No, Given number is not Armstrong number");
		}
		
	}

	public static void main(String[] args) {
		int fact=1,num=5;
		for(int i=1;i<=num;i++) {
			fact = fact*i;
			System.out.println(fact);
		}
		System.out.println("Factorial for "+num+" is : "+fact);
		armStrongNum(153);
		reverseStrStack("HelloWorld");

	}
	
	private static void reverseStrStack(String str) {
		Stack stack = new Stack();
		char[] strChar = str.toCharArray();
		char reverseChar;
		StringBuffer buffer = new StringBuffer();
		for(int i=0;i<strChar.length;i++) stack.push(i);
		System.out.println(stack.toString());
		System.out.println(stack.peek());
		while(stack.size() > 0) {
			buffer.append(str.charAt((int) stack.pop()));
		}
		System.out.println(stack.toString());
		System.out.println(buffer.toString());
	}

}
