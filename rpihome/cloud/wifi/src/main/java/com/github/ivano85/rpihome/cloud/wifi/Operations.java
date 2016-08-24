package com.github.ivano85.rpihome.cloud.wifi;

import com.waveinformatica.ocean.core.annotations.Operation;
import com.waveinformatica.ocean.core.annotations.OperationProvider;
import com.waveinformatica.ocean.core.annotations.Param;
import com.waveinformatica.ocean.core.annotations.SkipAuthorization;
import com.waveinformatica.ocean.core.controllers.ObjectFactory;
import com.waveinformatica.ocean.core.controllers.results.RedirectResult;
import com.waveinformatica.ocean.core.controllers.results.WebPageResult;
import com.waveinformatica.ocean.core.util.OceanSession;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;

@OperationProvider(namespace = "")
public class Operations {
    
    @Inject
    private ObjectFactory factory;
    
    @Inject
    private OceanSession session;
    
    @Operation("")
    @SkipAuthorization
    public WebPageResult home(
            @Param("add") String add,
            @Param("m") String mac,
            @Param("c") String reqCode
    ) {
        
        session.put("mac", mac);
        session.put("req_code", reqCode);
        session.put("redirect", add);
        
        WebPageResult result = factory.newInstance(WebPageResult.class);
        
        result.setTemplate("/templates/home.tpl");
        
        return result;
        
    }
    
    @Operation("send-code")
    @SkipAuthorization
    public WebPageResult sendCode(
            @Param("accept-terms") boolean acceptTerms,
            @Param("prefix") String prefix,
            @Param("phone") String phone
    ) {
        
        WebPageResult result = factory.newInstance(WebPageResult.class);
        
        result.setTemplate("/templates/code.tpl");
        
        int codeVal = (int) Math.round(Math.random() * 999999.0);
        String code = StringUtils.leftPad("" + codeVal, 6, '0');
        
        session.put("code", code);
        
        String msg = "Il codice per accedere alla rete Wi-fi e': " + code;
        
        SmsDao sms = factory.newInstance(SmsDao.class);
        sms.send(prefix.trim() + phone.trim(), msg);
        
        Data data = new Data();
        data.setPhone("+" + prefix + " " + phone);
        
        result.setData(data);
        
        return result;
        
    }
    
    @Operation("activate")
    @SkipAuthorization
    public RedirectResult activate(@Param("code") String code) throws NoSuchAlgorithmException, MalformedURLException {
        
        if (session.get("code", String.class).equals(code)) {
            String verify = session.get("mac", String.class) + ":" + session.get("req_code", String.class) + "villettaculmine1";
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] verCode = digest.digest(verify.getBytes(Charset.forName("UTF-8")));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < verCode.length; ++i) {
                sb.append(Integer.toHexString((verCode[i] & 0xFF) | 0x100).substring(1,3));
            }
            verify = sb.toString();
            
            session.remove("req_code");
            session.remove("mac");
            session.remove("code");
            
            String redirect = session.get("redirect", String.class);
            URL url = new URL(redirect);
            redirect = url.getProtocol() + "://" + url.getHost() + "/";
            
            return new RedirectResult(redirect + "wifi.php?code=" + verify);
        }
        
        return null;
        
    }
    
    public static class Data {
        
        private String phone;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
        
    }
    
}