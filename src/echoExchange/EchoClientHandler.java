package echoExchange;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Sharable 
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private static List<String> listIpServ;
    private final String request;
    private ByteBuf response ;
    private String sresponse;
    private final static  String fconfPath = 
            System.getProperty("user.dir");
    private final static  String fconf = 
            fconfPath+File.separator+"ovpn/client.ovpn" ;
    private final static  String fcacrt = 
            fconfPath+File.separator+"ovpn/ca.crt" ;
    private final static  String fclcrt = 
            fconfPath+File.separator+"ovpn/client3.crt" ;
    private final static  String fclkey = 
            fconfPath+File.separator+"ovpn/client3.key" ;  
    

    EchoClientHandler(String req) {
        request = req;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
               ctx.writeAndFlush(Unpooled.copiedBuffer(request, CharsetUtil.UTF_8));

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        //ctx.close();
        ctx.flush();
        String tstr = request.substring(0,4);
        if ("file".equals(tstr)){
            saveToFile(in); 
        }else{
            response = in;
            sresponse = in.toString(CharsetUtil.UTF_8);
        }
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
    
    public ByteBuf getResponse(){
        return response;
    }
    
    public String getResponses(){
        return sresponse;
    }
    
    public void saveToFile(ByteBuf fil) {
        //ByteBuf res = null ;
        String ffile;
        String id = request.substring(4,request.indexOf(":"));
        switch (id){
            case("1"):
                ffile = fconf;
                break;
            case("2"):
                ffile = fcacrt;
                break;
            case("3"):
                ffile = fclcrt ;
                break;
            case("4"):
                ffile = fclkey ;
                break;
            default:
                ffile = fconf;
                break;    
                
        }
        
        
        File file ;
        FileOutputStream fileoutputStream = null;
        FileChannel fileChannel = null;
        ByteBuffer byteBuffer = null;
        try {
            file = new File(ffile);
            fileoutputStream = new FileOutputStream(file,true);
            fileChannel = fileoutputStream.getChannel();
            //fileChannel.write(fil.nioBuffers());
            
            int length = fil.readableBytes();
            int rc = fil.refCnt();
            int written = 0;
            if (fil.nioBufferCount() == 1) {
                try{
                    byteBuffer = fil.nioBuffer();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                while (written < length) {
                    written += fileChannel.write(byteBuffer);
                }
            } else {
                ByteBuffer[] byteBuffers = fil.nioBuffers();
                while (written < length) {
                    written += fileChannel.write(byteBuffers);
                }
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                fileChannel.force(false);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                fileoutputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                fileChannel.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    }
}