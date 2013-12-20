# Force.com Web Service Connector (WSC)

The Force.com Web Service Connector (WSC) is a high performing web service
client stack implemented using a streaming parser. WSC also makes it much easier
to use the Force.com API (Web Services/SOAP or Asynchronous/BULK API). 

## Building WSC
```bash
git clone git@github.com:vitkin/sfdc-wsc.git
mvn clean package
```

## Generating Stubs From WSDLs
Use or replace the WSDL files located under the sub-modules folders
`sfdc-wsc-client-*` and build those sub-modules or rebuild the entire project.

## Downloading WSDLs automatically
WSDL files can be automatically downloaded by using the profile `download-wsdl`.  
Existing WSDL files will be overwritten by the downloaded ones.

To use that profile make sure you've created a `build.properties` file located
under the base directory of the sub-module `sfdc-wsc-client`.  
That `build.properties` has to contain at least your login and password as in
the hereunder example:
```INI
sfdc.username = user.name@domain.tld
sfdc.password = 123456
```

If you want to download WSDL files from the sandbox environment
(`https://test.salesforce.com`), then also add the property:
```properties
sfdc.useSandbox = true
```

## Versions management for the enterprise WSDLs based clients

For any enterprise WSDL, you're invited to add a sub-version to the version
defined in the corresponding `pom.xml`, for example `29.0.0-myorganization-1.0`
since your WSDL will be different from the one provided in this project sources.
 
If there is any changes compare to the previous one, then you must increase the
sub-version to reflect that change.

A good practice would be to have a branch proper to your organization and
commit there the changes to your own WSDL files and corresponding `pom.xml`.

## Write Application Code
The following sample illustrates creating a connection and creating a new
Account SObject.  
Login is automatically handled by the Connector.

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
```