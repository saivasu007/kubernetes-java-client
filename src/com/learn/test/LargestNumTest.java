package com.learn.test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LargestNumTest {

	public LargestNumTest() {
		// TODO Auto-generated constructor stub
	}
	
	//Find 3rd Largest Number from Array
	public static void main(String... args) {
		Integer[] inputArray = {25,5,2,55,12,89};
		Integer temp;
		Arrays.sort(inputArray,Collections.reverseOrder());
		for(Integer i : inputArray) System.out.println(i);
		System.out.println("3rd Largest Num is : "+inputArray[2]);
		List<Integer> inputArrayList = Arrays.asList(inputArray);
		Collections.sort(inputArrayList,Collections.reverseOrder());
		for(Integer i : inputArray) System.out.println(i);
		System.out.println("3rd Largest Num is : "+inputArray[2]);
		for(int i=0;i<inputArray.length;i++) {
			for(int j=i;j<inputArray.length;j++) {
				if(inputArray[j] > inputArray[i]) {
					temp = inputArray[i];
					inputArray[i] = inputArray[j];
					inputArray[j] = temp;
				}
			}
		}
		for(Integer i : inputArray) System.out.println(i);
		System.out.println("3rd Largest Num is : "+inputArray[2]);
	}

}
