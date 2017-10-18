
This is a web application used to validate the patient metadata csv files obtained from ENIGMA researchers.

Steps to deploy application:
1. The entry point to the application is the 'LandingPage.jsp' file
2. Ensure you have some sort of localhost web server setup on your device, I use Apache Tomcat v9 which can be found here: https://tomcat.apache.org/download-90.cgi
3. Add the server to your IDE of choice (I use Eclipse)
  *(Note the following instructions are specific to installing Tomcat on Eclipse, if you choose to use another server and/or 
  IDE that could work too)*
  a. Go to Window > Show View > Servers
  b. Now navigate to the "Servers" tab and use the clickable link to add Apache Tomcat and its folder path to Eclipse
4. With a web server installed you can now run the application by right clicking on 'LandingPage.jsp' selecting Run As > Run on Server
