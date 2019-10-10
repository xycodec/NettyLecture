package com.xycode.protobuf;

import java.util.UUID;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ProtobufClientHandler extends SimpleChannelInboundHandler<AddressBookProtos.AddressBook>{

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, AddressBookProtos.AddressBook msg) throws Exception {
		
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		AddressBookProtos.AddressBook.Builder addressBookBuilder= AddressBookProtos.AddressBook.newBuilder();
		for(int i=0;i<10;++i) {
			AddressBookProtos.Person.Builder personBuilder= AddressBookProtos.Person.newBuilder();
			AddressBookProtos.Person.PhoneNumber.Builder phoneNumber= AddressBookProtos.Person.PhoneNumber.newBuilder()
					.setNumber(UUID.randomUUID().toString())
					.setType(i%2==0? AddressBookProtos.Person.PhoneType.MOBILE: AddressBookProtos.Person.PhoneType.WORK);
			
			personBuilder.setId(1000+i).setName(UUID.randomUUID().toString()).setEmail(UUID.randomUUID()+"@gmail.com")
			.addPhones(phoneNumber);
			
			addressBookBuilder.addPeople(personBuilder);
			
			ctx.channel().writeAndFlush(addressBookBuilder.build());
		}
	}
	
}
