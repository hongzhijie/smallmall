package com.smallmall.controller.rest.wxuser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.smallmall.model.AccountUser;
import com.smallmall.service.AccountUserService;
import com.smallmall.utils.MdzwUtils;
import com.smallmall.utils.RedisClient;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/*
 * @Author hzj
 * @ClassName WxUserRest
 * @Description 获取微信小程序用户信息相关接口实现
 * @Date 11:00 2019/3/27
 **/
@RestController
@RequestMapping("ws/wxUser")
public class WxUserRest {

    //日志
    private Logger logger = Logger.getLogger(WxUserRest.class);

    //微信小程序appid
    @Value("${pay.wxpay.appID}")
    private static String APPID;
    //微信小程序秘钥
    @Value("${pay.wxpay.appSecret}")
    private static String APPSecret;

    @Autowired
    private RedisClient redisClient;

    //会员用户服务
    @Autowired
    private AccountUserService accountUserService;


    //获取小程序用户openid
    @RequestMapping("getWxUserOpenid")
    public static Map<String, Object> getWxUserOpenid(String code) {
        //拼接url
        StringBuilder url = new StringBuilder("https://api.weixin.qq.com/sns/jscode2session?");
        url.append("appid=");//appid设置
        url.append(APPID);
        url.append("&secret=");//secret设置
        url.append(APPSecret);
        url.append("&js_code=");//code设置
        url.append(code);
        url.append("&grant_type=authorization_code");
        Map<String, Object> map = null;
        try {
            HttpClient client = HttpClientBuilder.create().build();//构建一个Client
            HttpGet get = new HttpGet(url.toString());    //构建一个GET请求
            HttpResponse response = client.execute(get);//提交GET请求
            HttpEntity result = response.getEntity();//拿到返回的HttpResponse的"实体"
            String content = EntityUtils.toString(result);
            System.out.println(content);//打印返回的信息
            net.sf.json.JSONObject res = net.sf.json.JSONObject.fromObject(content);//把信息封装为json
            //把信息封装到map
            map = MdzwUtils.parseJSON2Map(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /*
     * @Author hzj
     * @ClassName WxUserRest
     * @Description 微信小程序登陆接口
     * @Date 11:50 2019/3/27
     * @Param 
     * @return 
     **/
    @ResponseBody
    @RequestMapping("/login")
    public Map<String,Object> doLogin(@RequestParam(value = "code",required = false) String code,
                                      @RequestParam(value = "rawData",required = false) String rawData,
                                      @RequestParam(value = "signature",required = false) String signature,
                                      @RequestParam(value = "encrypteData",required = false) String encrypteData,
                                      @RequestParam(value = "iv",required = false) String iv){
        logger.info( "Start get SessionKey" );


        Map<String,Object> map = new HashMap<String, Object>(  );
        System.out.println("用户非敏感信息"+rawData);

        com.alibaba.fastjson.JSONObject rawDataJson = JSON.parseObject( rawData );

        System.out.println("签名"+signature);
        JSONObject SessionKeyOpenId = getSessionKeyOrOpenId( code );
        System.out.println("post请求获取的SessionAndopenId="+SessionKeyOpenId);

        String openid = SessionKeyOpenId.getString("openid" );
        //会话密钥
        String sessionKey = SessionKeyOpenId.getString( "session_key" );

        System.out.println("openid="+openid+",session_key="+sessionKey);

        AccountUser user = accountUserService.getAccountUserByOpenId( openid );
        //uuid生成唯一key
        String skey = UUID.randomUUID().toString();
        if(user==null){
            //入库
            String nickName = rawDataJson.getString( "nickName" );
            String avatarUrl = rawDataJson.getString( "avatarUrl" );
            String gender  = rawDataJson.getString( "gender" );
            String city = rawDataJson.getString( "city" );
            String country = rawDataJson.getString( "country" );
            String province = rawDataJson.getString( "province" );


            user = new AccountUser();
            user.setWxOpenid( openid );
            user.setSessionKey( sessionKey );
            user.setUbalance( 0 );
            user.setsKey( skey );
            user.setUaddress( country+" "+province+" "+city );
            user.setAccountPic( avatarUrl );
            user.setAccountSex( Integer.parseInt( gender ) );
            user.setAccountName( nickName );
            accountUserService.insertAccountUser( user );
        }else {
            //已存在
            logger.info( "用户openid已存在,不需要插入" );
        }
        //根据openid查询skey是否存在
        String skey_redis = (String) redisClient.get( openid );
        if(StringUtils.isNotBlank( skey_redis )){
            //存在 删除 skey 重新生成skey 将skey返回
            redisClient.del( skey_redis );
        }
        //  缓存一份新的
        JSONObject sessionObj = new JSONObject(  );
        sessionObj.put( "openId",openid );
        sessionObj.put( "sessionKey",sessionKey );
        redisClient.set( skey,sessionObj.toJSONString() );
        redisClient.set( openid,skey );
        //把新的sessionKey和oppenid返回给小程序
        map.put( "skey",skey );
        map.put( "result","0" );
        JSONObject userInfo = getUserInfo( encrypteData, sessionKey, iv );
        System.out.println("根据解密算法获取的userInfo="+userInfo);
        userInfo.put( "balance",user.getUbalance() );
        map.put( "userInfo",userInfo );
        return map;
    }

    /**
     * @Author hzj
     * @ClassName WxUserRest
     * @Description 根据code获取openid等信息
     * @Date 16:05 2019/3/27
     * @Param [code]
     * @return com.alibaba.fastjson.JSONObject
     **/
    public static JSONObject getSessionKeyOrOpenId(String code) {
        //微信端登录code
//        String wxCode = code;
//        String requestUrl = "https://api.weixin.qq.com/sns/jscode2session";
//        Map<String,String> requestUrlParam = new HashMap<String, String>(  );
//        requestUrlParam.put( "appid","你的小程序appId" );//小程序appId
//        requestUrlParam.put( "secret","你的小程序appSecret" );
//        requestUrlParam.put( "js_code",wxCode );//小程序端返回的code
//        requestUrlParam.put( "grant_type","authorization_code" );//默认参数
//        //发送post请求读取调用微信接口获取openid用户唯一标识
//        JSONObject jsonObject = JSON.parseObject(UrlUtil.sendPost( requestUrl,requestUrlParam ));
        JSONObject jsonObject = null;
        try {
            //拼接url
            StringBuilder url = new StringBuilder("https://api.weixin.qq.com/sns/jscode2session?");
            url.append("appid=");//appid设置
            url.append(APPID);
            url.append("&secret=");//secret设置
            url.append(APPSecret);
            url.append("&js_code=");//code设置
            url.append(code);
            url.append("&grant_type=authorization_code");
            Map<String, Object> map = null;
            HttpClient client = HttpClientBuilder.create().build();//构建一个Client
            HttpGet get = new HttpGet(url.toString());    //构建一个GET请求
            HttpResponse response = client.execute(get);//提交GET请求
            HttpEntity result = response.getEntity();//拿到返回的HttpResponse的"实体"
            String content = EntityUtils.toString(result);
            System.out.println(content);//打印返回的信息
            jsonObject = JSON.parseObject(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject getUserInfo(String encryptedData,String sessionKey,String iv){
        // 被加密的数据
        byte[] dataByte = Base64.decode(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decode(sessionKey);
        // 偏移量
        byte[] ivByte = Base64.decode(iv);
        try {
            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding","BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init( Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, "UTF-8");
                return JSON.parseObject(result);
            }
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(),e);
        } catch (NoSuchPaddingException e) {
            logger.error(e.getMessage(),e);
        } catch (InvalidParameterSpecException e) {
            logger.error(e.getMessage(),e);
        } catch (IllegalBlockSizeException e) {
            logger.error(e.getMessage(),e);
        } catch (BadPaddingException e) {
            logger.error(e.getMessage(),e);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(),e);
        } catch (InvalidKeyException e) {
            logger.error(e.getMessage(),e);
        } catch (InvalidAlgorithmParameterException e) {
            logger.error(e.getMessage(),e);
        } catch (NoSuchProviderException e) {
            logger.error(e.getMessage(),e);
        }
        return null;
    }

}
