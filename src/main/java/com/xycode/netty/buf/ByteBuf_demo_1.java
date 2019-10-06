package com.xycode.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ByteProcessor;

public class ByteBuf_demo_1 {

    public static void main(String[] args) {
        ByteBuf byteBuf=Unpooled.buffer(1024);//非池化Buf
        for(int i=0;i<10;++i) {//写入10个byte,此时writeIndex会变动,readIndex不变
            byteBuf.writeByte(i);
        }

        //Netty实现的Buf是有两个index的,一个readerIndex,一个writerIndex
        //相比Java NIO原生的Buffer只有一个index,读写转换时就需要flip操作
        for(int i=byteBuf.readerIndex();i<byteBuf.readableBytes();++i) {
            System.out.println(byteBuf.getByte(i));
        }

        System.out.println("-------slice()-----------");
        ByteBuf sliceBuf=byteBuf.slice(0,5);
        while(sliceBuf.isReadable()){
            System.out.println(sliceBuf.readByte());//读一个字节并readIndex++,getByte(index)只会读取指定索引上的字节,index并不会发生改变
        }
        //slice()获取的是原有buf的一部分,并与原有buf共享,所以修改sliceBuf会导致原有的buf跟着改变
        //所以创建sliceBuf的效率是比较高的
        System.out.println("------------change sliceBuf----------------");
        sliceBuf.clear();
        while(sliceBuf.isWritable()){
            sliceBuf.writeByte(-1);//write的同时,writeIndex++
        }
        for(int i=byteBuf.readerIndex();i<byteBuf.readableBytes();++i) {
            System.out.println(byteBuf.getByte(i));
        }

        //与slice()不同的是,copy()是会复制一份,而不是共享,所以修改copyBuf并不会影响到原有的buf
        System.out.println("----------------copyBuf--------------------");
        ByteBuf copyBuf=byteBuf.copy(0,5);
        copyBuf.clear();
        while(copyBuf.isWritable()){
            copyBuf.writeByte(1);
        }
        for(int i=byteBuf.readerIndex();i<byteBuf.readableBytes();++i) {
            System.out.println(byteBuf.getByte(i));
        }
        //从输出结果看,显然byteBuf是没有因copyBuf改变而改变的

        System.out.println("在Buf中进行搜索");
        ByteBuf byteBuf2=Unpooled.copiedBuffer("this is xycode , test ! ".getBytes());
        System.out.println("capacity: "+byteBuf2.capacity());
        //根据返回的index,可以写成循环,检测整个Buf
        int p=0,q=byteBuf2.forEachByte(ByteProcessor.FIND_ASCII_SPACE);
        while(q!=-1){
            System.out.println("space index: "+q);
            p=q;
            q=byteBuf2.forEachByte(p+1,byteBuf2.capacity()-p-1,ByteProcessor.FIND_ASCII_SPACE);
        }
    }

}
