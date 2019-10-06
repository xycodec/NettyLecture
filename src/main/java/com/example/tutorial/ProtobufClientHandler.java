package com.example.tutorial;

import java.util.UUID;

import com.example.tutorial.AddressBookProtos.AddressBook;
import com.example.tutorial.AddressBookProtos.Person;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ProtobufClientHandler extends SimpleChannelInboundHandler<AddressBookProtos.AddressBook>{

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, AddressBook msg) throws Exception {
		
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		AddressBook.Builder addressBookBuilder=AddressBook.newBuilder();
		for(int i=0;i<10;++i) {
			Person.Builder personBuilder=Person.newBuilder();
			Person.PhoneNumber.Builder phoneNumber=Person.PhoneNumber.newBuilder()
					.setNumber(UUID.randomUUID().toString())
					.setType(i%2==0?Person.PhoneType.MOBILE:Person.PhoneType.WORK);
			
			personBuilder.setId(1000+i).setName(UUID.randomUUID().toString()).setEmail(UUID.randomUUID()+"@gmail.com")
			.addPhones(phoneNumber);
			
			addressBookBuilder.addPeople(personBuilder);
			
			ctx.channel().writeAndFlush(addressBookBuilder.build());
		}
	}
	
}
