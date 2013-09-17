# Force.com Web Service Connector (WSC)

The Force.com Web Service Connector (WSC) is a high performing web service client stack implemented using a streaming parser. WSC also makes it much easier to use the Force.com API (Web Services/SOAP or Asynchronous/BULK API). 

## Building WSC
    git clone git@github.com:vitkin/sfdc-wsc.git
    mvn clean package

## Generating Stubs From WSDLs
Use or replace the WSDL files located under the sub-modules folders `sfdc-wsc-ws-*` and build those sub-modules or the rebuild the entire project.

## Write Application Code
The following sample illustrates creating a connection and creating a new Account SObject.  Login is automatically handled by the Connector.

```java
    import com.sforce.soap.partner.*;
    import com.sforce.soap.partner.sobject.*;
    import com.sforce.ws.*;

    public static void main(String args) {
        ConnectorConfig config = new ConnectorConfig();
        config.setUsername("username");
        config.setPassword("password");

        PartnerConnection connection = Connector.newConnection(config);
        SObject account = new SObject();
        account.setType("Account");
        account.setField("Name", "My Account");
        connection.create(new SObject[]{account});
    }
