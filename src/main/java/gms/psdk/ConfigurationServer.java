package gms.psdk;

import com.genesyslab.platform.commons.protocol.*;
import com.genesyslab.platform.configuration.protocol.ConfServerProtocol;
import com.genesyslab.platform.configuration.protocol.types.CfgAppType;

import java.util.EventObject;

public class ConfigurationServer {
	 private final String configHost;
	    private final String username;
	    private final String password;
	    private String applicationName;

	    private int configPort = 2020;

	    public ConfServerProtocol protocol = null;

	    public ConfigurationServer(String configName, int configPort, String configHost, String username, String password) {
	        this.configPort = configPort;
	        this.configHost = configHost;
	        this.username = username;
	        this.password = password;

	        initializeConfigProtocol();

	    }
	    private void initializeConfigProtocol() {

	        Endpoint endpoint = new Endpoint(configHost, configPort);
	        protocol = new ConfServerProtocol(endpoint);
	        protocol.setUserName(username);
	        protocol.setClientName(username);
	        protocol.setTimeout(60000);
	        protocol.setUserPassword(password);
	        protocol.setClientApplicationType(CfgAppType.CFGSCE.asInteger());

	        protocol.addChannelListener(new ChannelListener() {
	            @Override
	            public void onChannelOpened(EventObject eventObject) {
	                System.out.println("ConfigServer[" + applicationName + "]Opened");
	            }
	            @Override
	            public void onChannelClosed(ChannelClosedEvent channelClosedEvent) {
	                System.out.println("ConfigServer[" + applicationName + "] Closed");
	            }
	            @Override
	            public void onChannelError(ChannelErrorEvent channelErrorEvent) {
	                System.out.println("ConfigServer[" + applicationName + "] Error: " + channelErrorEvent.getCause());

	            }
	        });
	    }

	    /**
	     * Connect to Configuration Server
	     */
	    public void connect() {
	        try {
	            protocol.open();
	        } catch (ProtocolException e) {
	            e.printStackTrace();
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	        if (protocol.getState() != ChannelState.Opened) {

	            return;
	        }

	    }

	    /**
	     * Disconnect from Configuration Server
	     */
	    public void disconnect() {
	        try {
	            protocol.close();
	        } catch (ProtocolException e) {
	            e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	    }

	

}
