package com.xycode.netty.buf;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class AtomicIntegerFieldUpdater_demo_1 {

	static class Person{
		volatile int age=0;
	}
	
	public static void main(String[] args) {
		Person person=new Person();
		AtomicIntegerFieldUpdater<Person> atomicIntegerFieldUpdater
		=AtomicIntegerFieldUpdater.newUpdater(Person.class, "age");//����+ƫ����,��ȡage�ֶ�

		//20���߳�ȥ����person��age�ֶ�,AtomicIntegerFieldUpdater��֤��ԭ����,���̰߳�ȫ,һ�������һ��20(��������)
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
