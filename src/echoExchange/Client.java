/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echoExchange;

import appechoexchange.Combo;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import static java.lang.Integer.parseInt;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Client {

    private final String host;
    private final int port;
    private EchoClientHandler echo = null;
    private List<String> listServ;
    String request;
    ByteBuf response;
    String  sresponse;
    private ArrayList <Client> arcl;
    private ObservableList <Combo> arCombo;
    private ArrayList<String> listIpServ;
    int rcvBuf;
    int sndBuf;


    public Client(String host, int port,String req) {
        this.host = host;
        this.port = port;
        this.request = req;
    }


    public void start() throws Exception {
        rcvBuf = 64;
        sndBuf = 64;
        Bootstrap b;

        EventLoopGroup group = new NioEventLoopGroup();
        
        try {
            b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                          .remoteAddress(new InetSocketAddress(host, port))
                          .handler(new ChannelInitializer<SocketChannel>() {

                              @Override
                              public void initChannel(SocketChannel ch) throws Exception {
                                  echo =new EchoClientHandler(request);
                                  ch.pipeline().addLast(echo);
//                                    System.out.println("senbuf:"+ ch.config().getSendBufferSize());
//                                    System.out.println("waterhigh:"+ ch.config().getWriteBufferWaterMark().high());
//                                    System.out.println("waterlow:"+ ch.config().getWriteBufferWaterMark().low());
//                                    System.out.println("recbuf:"+ ch.config().getReceiveBufferSize());

                              }
                          });
            b.option(ChannelOption.SO_RCVBUF, rcvBuf * 1024);
            b.option(ChannelOption.SO_SNDBUF, sndBuf * 1024);
            ChannelFuture f = b.connect().sync();

            f.channel().closeFuture().sync();
            response = echo.getResponse();
            sresponse = echo.getResponses();
            if ((request.indexOf("file")<0)&(request.indexOf("proxy")<0)){
               parseStrinCombo(sresponse); 
            }
            if ((request.indexOf("proxy")>=0)){
               parseStrGw(sresponse); 
            }
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    
    
    // Список серверов из строки полученной с сервера ЭХО
    // преобразуем в массив LIST
    private void parseStr(String s){
        listIpServ = new ArrayList<>();
        String ts="";
        for (int i=s.indexOf(":")+1;i<=s.length()-1;i++){
            if ((s.charAt(i) != ',')&&(s.charAt(i) != ';')) {
                ts=ts+s.charAt(i);
            } else {
                if (ts.length()>0){
                    listIpServ.add(ts);
                    ts="";
            }
            }
        }
    }
    
    // Получаем список серверов
    public List<String> getListServIp (){
        return listIpServ;
    }    
    
    /* Список Регионов, Подсетей 
        из строки полученной с сервера ЭХО
        преобразуем в массив LIST 
        для ComboBox id:value
    */
    private void parseStrinCombo(String s){
        Combo listCombo;
        arCombo = FXCollections.observableArrayList();
        if (request.indexOf("region")>=0){
            arCombo.add(new Combo(0,"Select Region"));
        }
        if (request.indexOf("network")>=0){
            arCombo.add(new Combo(0,"Select Network"));
        }
        
        String ts="";
        String tts = s;
        for (int i=0;i<=s.length()-1;i++){
            if (tts.charAt(i) == ';'){
                // Записываем в массив
                listCombo = new Combo(
                        parseInt(ts.substring(0, ts.indexOf(":"))),
                       ts.substring(ts.indexOf(":")+1,ts.length()));
                arCombo.add(listCombo);
                ts="";
                //tts = tts.substring(tts.indexOf(";")+1,tts.length());
            }else{
                ts=ts+s.charAt(i);
            }
        }
    }
    
     // Получаем список серверов
    public ObservableList <Combo> getListCombo (){
        return (ObservableList <Combo>) arCombo;

    }

// Список серверов из строки полученной с сервера ЭХО
    // преобразуем в массив LIST
    private void parseStrGw(String s){
        listIpServ = new ArrayList<>();
        String ts="";
        for (int i=0;i<=s.length()-1;i++){
            if (s.charAt(i) != ';') {
                ts=ts+s.charAt(i);
            } else {
                if (ts.length()>0){
                    listIpServ.add(ts);
                    ts="";
            }
            }
        }
    }

}
