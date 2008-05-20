
Salesforce Webservice Connector (WSC)
-------------------------------------

Webservice Connector is a high performing web service client  stack
implemented using a streaming parser. WSC also makes it much easier
to use Salesforce.com APIs.

Documents:
   Quickstart - doc\quickstart.html
   Javadoc    - doc\javadoc\index.html

Samples:
   samples\partner    -  shows how to use partner WSDL
   samples\enterprise -  shows how to use enterprise WSDL
   samples\custom     -  enterprise WSDL with custom objects


TODO:

    * P1
          o - Support response headers
          o - Fix Type Mapping?.writeDouble
          o - Clean up header handeling in the connections
          o - Way to customize package name for the generated types
          o - Fix addChild method
          o - Add user agent HTTP header so that we can track usage
          o - generate equals and hashCode method on types
          o - Return Iterator for getXXX methods that returns array
          o - Expose setting username/password for http proxy
          o - Remove enterprise classes from wsc.jar
          o - Refresh session ID from the response header
          o - Modify wsdlc to generate a standalone jar file
          o - Generate JDK 1.5 Enum for simpleType
          o - Make connector thread safe
          o - Set service endpoint on existing connection 

    * P2
          o - JDK 1.5 ... for arrays
          o - Message logging
          o - Use Apache Http Client? as transport
          o - Generate add, insert, remove method for array types
          o - Verify xml request with schema
          o - Lazy XML parsing for large arrays
          o - Test with J 2 ME?
          o - Performance test with Fog Horn?
          o - Consider adding activeConnection
          o - generate isXXX method for boolean
          o - need a way to pass in Call Options? to login() 

Changes:

    * Beta 1.3:
          o - Generate Partner and Enterprise connection
          o - Different factory for partner and enterprise
          o - Use existing URL, session ID
          o - Rename Xml Object?.addChild() to Xml Object?.setField()
          o - Use auth URL defined in the WSDL 

    * Beta 1.2:
          o - Default to 144 build
          o - Change content-type to text/xml; charset=UTF-8
          o - Remove xml pretty printing
          o - Set Accept header to text/xml
          o - Remove Fast Byte Array Output Stream? & Xml Writer? 

    * Beta 1.1:
          o - support gzip compression. see Connector Config?.setCompression(true)
          o - Fix exception.toString() to print more info
          o - Message tracing, use Conector Config?.setMessageTrace(true)
          o - added WSC UI test client
          o - added command line tool to exec SOQL 

    * Beta 1:
          o - Initial version of WSC
          o - Tested with build 142 of salesforce.com
          o - Supports partner and enterprise wsdl 
