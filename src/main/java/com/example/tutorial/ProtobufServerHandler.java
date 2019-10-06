package com.example.tutorial;


import com.example.tutorial.AddressBookProtos.AddressBook;
import com.example.tutorial.AddressBookProtos.Person;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ProtobufServerHandler extends SimpleChannelInboundHandler<AddressBookProtos.AddressBook>{

	private static void print(AddressBook addressBook) {
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
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, AddressBook msg) throws Exception {
		print(msg);
	}

	
}
