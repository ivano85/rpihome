/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.github.ivano85.rpihome.cloud.wifi;

import com.waveinformatica.ocean.core.Configuration;
import com.waveinformatica.ocean.core.annotations.CoreEventListener;
import com.waveinformatica.ocean.core.annotations.Operation;
import com.waveinformatica.ocean.core.annotations.OperationProvider;
import com.waveinformatica.ocean.core.annotations.Param;
import com.waveinformatica.ocean.core.controllers.IEventListener;
import com.waveinformatica.ocean.core.controllers.ObjectFactory;
import com.waveinformatica.ocean.core.controllers.events.BeforeResultEvent;
import com.waveinformatica.ocean.core.controllers.events.CoreEventName;
import com.waveinformatica.ocean.core.controllers.events.Event;
import com.waveinformatica.ocean.core.controllers.results.MenuResult;
import com.waveinformatica.ocean.core.controllers.results.TableResult;
import com.waveinformatica.ocean.core.util.PersistedProperties;
import java.io.IOException;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author ivano
 */
@OperationProvider(namespace = "admin/sms")
public class AdminOperations {
    
    @Inject
    private ObjectFactory factory;
    
    @Inject
    private Configuration configuration;
    
    @Operation("")
    public TableResult main(
            @Param("pk") String pk,
            @Param("value") String value
    ) throws IOException {
        
        TableResult result = factory.newInstance(TableResult.class);
        result.setParentOperation("admin/");
        
        PersistedProperties props = configuration.getCustomProperties("sms");
        
        boolean persist = false;
        
        if (StringUtils.isBlank(props.getProperty("plivo.auth-id"))) {
            props.setProperty("plivo.auth-id", "");
            persist = true;
        }
        if (StringUtils.isBlank(props.getProperty("plivo.auth-token"))) {
            props.setProperty("plivo.auth-token", "");
            persist = true;
        }
        if (StringUtils.isBlank(props.getProperty("plivo.source-phone"))) {
            props.setProperty("plivo.source-phone", "");
            persist = true;
        }
        
        if (StringUtils.isNotBlank(pk)) {
            props.setProperty(pk, value);
            persist = true;
        }
        
        if (persist) {
            props.store("");
        }
        
        result.setTableData(props);
        result.getColumn("value").setEditable(TableResult.CellEditType.TEXT, "admin/sms/");
        
        return result;
        
    }
    
    @CoreEventListener(eventNames = CoreEventName.BEFORE_RESULT,
            flags = "com.waveinformatica.ocean.core.controllers.results.MenuResult")
    public static class RootLinkInjector implements IEventListener {
        
        @Override
        public void performAction(Event event) {
            
            BeforeResultEvent e = (BeforeResultEvent) event;
            
            if (e.getOperationInfo().getProvider().namespace().equals("/admin") && e.getOperationInfo().getOperation().value().equals("")) {
                
                MenuResult res = (MenuResult) e.getResult();
                
                MenuResult.MenuSection section = res.addSection("RPIHome Wifi");
                
                section.add("envelope-o", "SMS", "admin/sms/");
                
            }
            
        }
        
    }
    
}
