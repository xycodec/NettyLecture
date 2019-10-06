package com.example.tutorial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import com.example.tutorial.AddressBookProtos.AddressBook;
import com.example.tutorial.AddressBookProtos.Person;
public class Protobuf_demo_1 {
	public static void print(AddressBook addressBook) {
	    for (Person person: addressBook.getPeopleList()) {
	        System.out.println("Person ID: " + person.getId());
	        System.out.println("  Name: " + person.getName());
	        if (person.hasEmail()) {
	          System.out.println("  E-mail address: " + person.getEmail());
	        }

	        for (Person.PhoneNumber phoneNumber : person.getPhonesList()) {
	          switch (phoneNumber.getType()) {
	            case MOBILE:
	              System.out.print("  Mobile phone #: ");
	              break;
	            case HOME:
	              System.out.print("  Home phone #: ");
	              break;
	            case WORK:
	              System.out.print("  Work phone #: ");
	              break;
	          }
	          System.out.println(phoneNumber.getNumber());
	        }
	    }
	}

	public static void main(String[] args) throws IOException {
		AddressBook.Builder addressBookBuilder=AddressBook.newBuilder();
		for(int i=0;i<10;++i) {
			Person.Builder personBuilder=Person.newBuilder();
			Person.PhoneNumber.Builder phoneNumber=Person.PhoneNumber.newBuilder()
					.setNumber(UUID.randomUUID().toString())
					.setType(i%2==0?Person.PhoneType.MOBILE:Person.PhoneType.WORK);
			
			personBuilder.setId(1000+i).setName(UUID.randomUUID().toString()).setEmail(UUID.randomUUID()+"@gmail.com")
			.addPhones(phoneNumber);
			
			addressBookBuilder.addPeople(personBuilder);
		}
		
		
		print(addressBookBuilder.build());
		System.out.println();
		FileOutputStream fileOutputStream=new FileOutputStream(new File("addressbook.serialbytes"));
		fileOutputStream.write(addressBookBuilder.build().toByteArray());
		fileOutputStream.close();
		
		FileInputStream fileInputStream=new FileInputStream(new File("addressbook.serialbytes"));
		AddressBook addressBook=AddressBook.parseFrom(fileInputStream);
		fileInputStream.close();
		print(addressBook);
	}

}
