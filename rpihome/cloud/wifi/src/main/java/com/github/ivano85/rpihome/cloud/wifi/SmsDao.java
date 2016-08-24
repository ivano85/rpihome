/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.ivano85.rpihome.cloud.wifi;

import com.plivo.helper.api.client.RestAPI;
import com.plivo.helper.api.response.message.MessageResponse;
import com.plivo.helper.exception.PlivoException;
import com.waveinformatica.ocean.core.Configuration;
import com.waveinformatica.ocean.core.util.LoggerUtils;
import com.waveinformatica.ocean.core.util.PersistedProperties;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import javax.inject.Inject;

/**
 *
 * @author ivano
 */
public class SmsDao {
    
    @Inject
    private Configuration configuration;
    
    public void send(String destination, String message) {
        
        PersistedProperties props = configuration.getCustomProperties("sms");
        
        RestAPI api = new RestAPI(props.getProperty("plivo.auth-id"), props.getProperty("plivo.auth-token"), "v1");
        
        LinkedHashMap<String, String> args = new LinkedHashMap<>();
        args.put("src", props.getProperty("plivo.source-phone"));
        args.put("dst", destination);
        args.put("text", message);
        args.put("url", "http://liferame.com/wifi/notify-sms");
        args.put("method", "POST");
        
        try {
            
            MessageResponse msgResponse = api.sendMessage(args);
            
            if (msgResponse.serverCode == 202) {
                msgResponse.messageUuids.get(0);
            }
            else {
                LoggerUtils.getLogger(SmsDao.class).log(Level.SEVERE, "Error sending SMS to " + destination + ": " + msgResponse.error);
            }
            
        } catch (PlivoException e) {
            LoggerUtils.getLogger(SmsDao.class).log(Level.SEVERE, "Unexpected error sending SMS to " + destination, e);
        }
        
    }
    
}
