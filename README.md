Netty学习

参考书籍:

《Netty in Action》
 
 
com.xycode.netty.http,增加SSL加密协议

1.生成服务端密钥：
keytool -genkey -alias securechat -keysize 2048 -validity 90 -keyalg RSA -dname "CN=xycode" -keypass serverPass -storepass serverPass -keystore server.jks

2.生成服务端证书:
keytool -export -alias securechat -keystore server.jks -storepass serverPass -file server.cer

3.生成客户端密钥:
keytool -genkey -alias smcc -keysize 2048 -validity 90 -keyalg RSA -dname "CN=xycode" -keypass clientPass -storepass clientPass -keystore client.jks

4.注册:
keytool -import -trustcacerts -alias securechat -file server.cer -storepass clientPass -keystore client.jks