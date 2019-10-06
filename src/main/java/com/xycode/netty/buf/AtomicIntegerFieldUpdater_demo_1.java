package com.xycode.netty.buf;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class AtomicIntegerFieldUpdater_demo_1 {

	static class Person{
		volatile int age=0;
	}
	
	public static void main(String[] args) {
		Person person=new Person();
		AtomicIntegerFieldUpdater<Person> atomicIntegerFieldUpdater
		=AtomicIntegerFieldUpdater.newUpdater(Person.class, "age");//反射+偏移量,获取age字段

		//20个线程去更新person的age字段,AtomicIntegerFieldUpdater保证其原子性,即线程安全,一定会输出一个20(可能乱序)
		for(int i=0;i<20;++i) {
			Thread t=new Thread(()->{
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(atomicIntegerFieldUpdater.incrementAndGet(person));
			});
			
			t.start();
		}

	}
}
