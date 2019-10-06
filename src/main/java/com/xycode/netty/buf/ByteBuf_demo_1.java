package com.xycode.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ByteProcessor;

public class ByteBuf_demo_1 {

    public static void main(String[] args) {
        ByteBuf byteBuf=Unpooled.buffer(1024);//�ǳػ�Buf
        for(int i=0;i<10;++i) {//д��10��byte,��ʱwriteIndex��䶯,readIndex����
            byteBuf.writeByte(i);
        }

        //Nettyʵ�ֵ�Buf��������index��,һ��readerIndex,һ��writerIndex
        //���Java NIOԭ����Bufferֻ��һ��index,��дת��ʱ����Ҫflip����
        for(int i=byteBuf.readerIndex();i<byteBuf.readableBytes();++i) {
            System.out.println(byteBuf.getByte(i));
        }

        System.out.println("-------slice()-----------");
        ByteBuf sliceBuf=byteBuf.slice(0,5);
        while(sliceBuf.isReadable()){
            System.out.println(sliceBuf.readByte());//��һ���ֽڲ�readIndex++,getByte(index)ֻ���ȡָ�������ϵ��ֽ�,index�����ᷢ���ı�
        }
        //slice()��ȡ����ԭ��buf��һ����,����ԭ��buf����,�����޸�sliceBuf�ᵼ��ԭ�е�buf���Ÿı�
        //���Դ���sliceBuf��Ч���ǱȽϸߵ�
        System.out.println("------------change sliceBuf----------------");
        sliceBuf.clear();
        while(sliceBuf.isWritable()){
            sliceBuf.writeByte(-1);//write��ͬʱ,writeIndex++
        }
        for(int i=byteBuf.readerIndex();i<byteBuf.readableBytes();++i) {
            System.out.println(byteBuf.getByte(i));
        }

        //��slice()��ͬ����,copy()�ǻḴ��һ��,�����ǹ���,�����޸�copyBuf������Ӱ�쵽ԭ�е�buf
        System.out.println("----------------copyBuf--------------------");
        ByteBuf copyBuf=byteBuf.copy(0,5);
        copyBuf.clear();
        while(copyBuf.isWritable()){
            copyBuf.writeByte(1);
        }
        for(int i=byteBuf.readerIndex();i<byteBuf.readableBytes();++i) {
            System.out.println(byteBuf.getByte(i));
        }
        //����������,��ȻbyteBuf��û����copyBuf�ı���ı��

        System.out.println("��Buf�н�������");
        ByteBuf byteBuf2=Unpooled.copiedBuffer("this is xycode , test ! ".getBytes());
        System.out.println("capacity: "+byteBuf2.capacity());
        //���ݷ��ص�index,����д��ѭ��,�������Buf
        int p=0,q=byteBuf2.forEachByte(ByteProcessor.FIND_ASCII_SPACE);
        while(q!=-1){
            System.out.println("space index: "+q);
            p=q;
            q=byteBuf2.forEachByte(p+1,byteBuf2.capacity()-p-1,ByteProcessor.FIND_ASCII_SPACE);
        }
    }

}
