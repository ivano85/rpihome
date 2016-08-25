== Capitve Portal with rpihome ==

This captive portal implementation makes use of
- openvpn
- iptables
- nginx
- php
- remote server with a java ee application

In this folder you can find a sample configuration implemented in the following way:

# The raspberry's Wi-fi is configured in AP mode using hostapd
# The user connects to Wi-fi without password
# The user makes a web request (to port 80) e.g. http://www.google.com
# Through **iptables** the user is redirected to localhost:80 where i finds nginx with a welcome php page
# The welcome php page redirects the user to the remote server reachable through vpn, with user's mac address and a random code as parameters for the application
# The java ee application asks to the user his phone number
# The java ee application sends a SMS to the user (at the moment through plivo) with a verification code
# The user inputs the verification code
# The java ee aplication redirects the user to nginx again with a generated code to verify user's information
# The final php script uses iptables to grant internet access to the user

