package com.xycode.protobuf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class Protobuf_demo_1 {
	public static void print(AddressBookProtos.AddressBook addressBook) {
	    for (AddressBookProtos.Person person: addressBook.getPeopleList()) {
	        System.out.println("Person ID: " + person.getId());
	        System.out.println("  Name: " + person.getName());
	        if (person.hasEmail()) {
	          System.out.println("  E-mail address: " + person.getEmail());
	        }

	        for (AddressBookProtos.Person.PhoneNumber phoneNumber : person.getPhonesList()) {
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
		AddressBookProtos.AddressBook.Builder addressBookBuilder= AddressBookProtos.AddressBook.newBuilder();
		for(int i=0;i<10;++i) {
			AddressBookProtos.Person.Builder personBuilder= AddressBookProtos.Person.newBuilder();
			AddressBookProtos.Person.PhoneNumber.Builder phoneNumber= AddressBookProtos.Person.PhoneNumber.newBuilder()
					.setNumber(UUID.randomUUID().toString())
					.setType(i%2==0? AddressBookProtos.Person.PhoneType.MOBILE: AddressBookProtos.Person.PhoneType.WORK);
			
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
		AddressBookProtos.AddressBook addressBook= AddressBookProtos.AddressBook.parseFrom(fileInputStream);
		fileInputStream.close();
		print(addressBook);
	}

}
