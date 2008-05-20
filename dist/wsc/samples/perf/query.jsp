<% response.setContentType("text/xml");%><?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="urn:partner.soap.sforce.com" xmlns:sf="urn:sobject.partner.soap.sforce.com">
<soapenv:Body>
<queryResponse>
<result xsi:type="QueryResult">
<done>true</done>
<queryLocator xsi:nil="true"/>
<% int MAX = 100000; for(int i=0; i<MAX; i++) { %>
<records xsi:type="sf:sObject">
<sf:type>Account</sf:type>
<sf:Id>001D0000008WxYYIA0</sf:Id>
<sf:Id>001D0000008WxYYIA0</sf:Id>
<sf:Name>My Account</sf:Name>
<sf:Phone>123 244 3455</sf:Phone>
</records>
<% } %>
<size><%=""+MAX%></size>
</result>
</queryResponse>
</soapenv:Body>
</soapenv:Envelope>
