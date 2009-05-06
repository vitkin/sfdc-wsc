package com.sforce.async;

/**
 * Generated class, please do not edit.
 */
public class JobInfo implements com.sforce.ws.bind.XMLizable {

  /**
   * Constructor
   */
  public JobInfo() {
  }
    
  
  /**
   * element  : id of type {http://www.w3.org/2001/XMLSchema}string
   * java type: java.lang.String
   */
  private static final com.sforce.ws.bind.TypeInfo id__typeInfo =
    new com.sforce.ws.bind.TypeInfo("http://www.force.com/2009/06/asyncapi/dataload","id","http://www.w3.org/2001/XMLSchema","string",0,1,true);

  private boolean id__is_set = false;

  private java.lang.String id;

  public java.lang.String getId() {
    return id;
  }

  

  public void setId(java.lang.String id) {
    this.id = id;
    id__is_set = true;
  }
  
  /**
   * element  : operation of type {http://www.force.com/2009/06/asyncapi/dataload}OperationEnum
   * java type: com.sforce.async.OperationEnum
   */
  private static final com.sforce.ws.bind.TypeInfo operation__typeInfo =
    new com.sforce.ws.bind.TypeInfo("http://www.force.com/2009/06/asyncapi/dataload","operation","http://www.force.com/2009/06/asyncapi/dataload","OperationEnum",0,1,true);

  private boolean operation__is_set = false;

  private com.sforce.async.OperationEnum operation;

  public com.sforce.async.OperationEnum getOperation() {
    return operation;
  }

  

  public void setOperation(com.sforce.async.OperationEnum operation) {
    this.operation = operation;
    operation__is_set = true;
  }
  
  /**
   * element  : object of type {http://www.w3.org/2001/XMLSchema}string
   * java type: java.lang.String
   */
  private static final com.sforce.ws.bind.TypeInfo object__typeInfo =
    new com.sforce.ws.bind.TypeInfo("http://www.force.com/2009/06/asyncapi/dataload","object","http://www.w3.org/2001/XMLSchema","string",0,1,true);

  private boolean object__is_set = false;

  private java.lang.String object;

  public java.lang.String getObject() {
    return object;
  }

  

  public void setObject(java.lang.String object) {
    this.object = object;
    object__is_set = true;
  }
  
  /**
   * element  : createdById of type {http://www.w3.org/2001/XMLSchema}string
   * java type: java.lang.String
   */
  private static final com.sforce.ws.bind.TypeInfo createdById__typeInfo =
    new com.sforce.ws.bind.TypeInfo("http://www.force.com/2009/06/asyncapi/dataload","createdById","http://www.w3.org/2001/XMLSchema","string",0,1,true);

  private boolean createdById__is_set = false;

  private java.lang.String createdById;

  public java.lang.String getCreatedById() {
    return createdById;
  }

  

  public void setCreatedById(java.lang.String createdById) {
    this.createdById = createdById;
    createdById__is_set = true;
  }
  
  /**
   * element  : createdDate of type {http://www.w3.org/2001/XMLSchema}dateTime
   * java type: java.util.Calendar
   */
  private static final com.sforce.ws.bind.TypeInfo createdDate__typeInfo =
    new com.sforce.ws.bind.TypeInfo("http://www.force.com/2009/06/asyncapi/dataload","createdDate","http://www.w3.org/2001/XMLSchema","dateTime",0,1,true);

  private boolean createdDate__is_set = false;

  private java.util.Calendar createdDate;

  public java.util.Calendar getCreatedDate() {
    return createdDate;
  }

  

  public void setCreatedDate(java.util.Calendar createdDate) {
    this.createdDate = createdDate;
    createdDate__is_set = true;
  }
  
  /**
   * element  : systemModstamp of type {http://www.w3.org/2001/XMLSchema}dateTime
   * java type: java.util.Calendar
   */
  private static final com.sforce.ws.bind.TypeInfo systemModstamp__typeInfo =
    new com.sforce.ws.bind.TypeInfo("http://www.force.com/2009/06/asyncapi/dataload","systemModstamp","http://www.w3.org/2001/XMLSchema","dateTime",0,1,true);

  private boolean systemModstamp__is_set = false;

  private java.util.Calendar systemModstamp;

  public java.util.Calendar getSystemModstamp() {
    return systemModstamp;
  }

  

  public void setSystemModstamp(java.util.Calendar systemModstamp) {
    this.systemModstamp = systemModstamp;
    systemModstamp__is_set = true;
  }
  
  /**
   * element  : state of type {http://www.force.com/2009/06/asyncapi/dataload}JobStateEnum
   * java type: com.sforce.async.JobStateEnum
   */
  private static final com.sforce.ws.bind.TypeInfo state__typeInfo =
    new com.sforce.ws.bind.TypeInfo("http://www.force.com/2009/06/asyncapi/dataload","state","http://www.force.com/2009/06/asyncapi/dataload","JobStateEnum",0,1,true);

  private boolean state__is_set = false;

  private com.sforce.async.JobStateEnum state;

  public com.sforce.async.JobStateEnum getState() {
    return state;
  }

  

  public void setState(com.sforce.async.JobStateEnum state) {
    this.state = state;
    state__is_set = true;
  }
  
  /**
   * element  : externalIdFieldName of type {http://www.w3.org/2001/XMLSchema}string
   * java type: java.lang.String
   */
  private static final com.sforce.ws.bind.TypeInfo externalIdFieldName__typeInfo =
    new com.sforce.ws.bind.TypeInfo("http://www.force.com/2009/06/asyncapi/dataload","externalIdFieldName","http://www.w3.org/2001/XMLSchema","string",0,1,true);

  private boolean externalIdFieldName__is_set = false;

  private java.lang.String externalIdFieldName;

  public java.lang.String getExternalIdFieldName() {
    return externalIdFieldName;
  }

  

  public void setExternalIdFieldName(java.lang.String externalIdFieldName) {
    this.externalIdFieldName = externalIdFieldName;
    externalIdFieldName__is_set = true;
  }

  /**
   */
  @Override
  public void write(javax.xml.namespace.QName __element,
      com.sforce.ws.parser.XmlOutputStream __out, com.sforce.ws.bind.TypeMapper __typeMapper)
      throws java.io.IOException {
    __out.writeStartTag(__element.getNamespaceURI(), __element.getLocalPart());
    
    writeFields(__out, __typeMapper);
    __out.writeEndTag(__element.getNamespaceURI(), __element.getLocalPart());
  }

  protected void writeFields(com.sforce.ws.parser.XmlOutputStream __out,
      com.sforce.ws.bind.TypeMapper __typeMapper) throws java.io.IOException {
   
    __typeMapper.writeString(__out, id__typeInfo, id, id__is_set);
    __typeMapper.writeObject(__out, operation__typeInfo, operation, operation__is_set);
    __typeMapper.writeString(__out, object__typeInfo, object, object__is_set);
    __typeMapper.writeString(__out, createdById__typeInfo, createdById, createdById__is_set);
    __typeMapper.writeObject(__out, createdDate__typeInfo, createdDate, createdDate__is_set);
    __typeMapper.writeObject(__out, systemModstamp__typeInfo, systemModstamp, systemModstamp__is_set);
    __typeMapper.writeObject(__out, state__typeInfo, state, state__is_set);
    __typeMapper.writeObject(__out, externalIdFieldName__typeInfo, externalIdFieldName, externalIdFieldName__is_set);
  }


  @Override
  public void load(com.sforce.ws.parser.XmlInputStream __in,
      com.sforce.ws.bind.TypeMapper __typeMapper) throws java.io.IOException, com.sforce.ws.ConnectionException {
    __typeMapper.consumeStartTag(__in);
    loadFields(__in, __typeMapper);
    __typeMapper.consumeEndTag(__in);
  }

  protected void loadFields(com.sforce.ws.parser.XmlInputStream __in,
      com.sforce.ws.bind.TypeMapper __typeMapper) throws java.io.IOException, com.sforce.ws.ConnectionException {
   
    __in.peekTag();
    if (__typeMapper.isElement(__in, id__typeInfo)) {
      setId(__typeMapper.readString(__in, id__typeInfo, String.class));
    }
    __in.peekTag();
    if (__typeMapper.isElement(__in, operation__typeInfo)) {
      setOperation((com.sforce.async.OperationEnum)__typeMapper.readObject(__in, operation__typeInfo, com.sforce.async.OperationEnum.class));
    }
    __in.peekTag();
    if (__typeMapper.isElement(__in, object__typeInfo)) {
      setObject(__typeMapper.readString(__in, object__typeInfo, String.class));
    }
    __in.peekTag();
    if (__typeMapper.isElement(__in, createdById__typeInfo)) {
      setCreatedById(__typeMapper.readString(__in, createdById__typeInfo, String.class));
    }
    __in.peekTag();
    if (__typeMapper.isElement(__in, createdDate__typeInfo)) {
      setCreatedDate((java.util.Calendar)__typeMapper.readObject(__in, createdDate__typeInfo, java.util.Calendar.class));
    }
    __in.peekTag();
    if (__typeMapper.isElement(__in, systemModstamp__typeInfo)) {
      setSystemModstamp((java.util.Calendar)__typeMapper.readObject(__in, systemModstamp__typeInfo, java.util.Calendar.class));
    }
    __in.peekTag();
    if (__typeMapper.isElement(__in, state__typeInfo)) {
      setState((com.sforce.async.JobStateEnum)__typeMapper.readObject(__in, state__typeInfo, com.sforce.async.JobStateEnum.class));
    }
    __in.peekTag();
    if (__typeMapper.isElement(__in, externalIdFieldName__typeInfo)) {
      setExternalIdFieldName(__typeMapper.readString(__in, externalIdFieldName__typeInfo, String.class));
    }
  }

  @Override
  public String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder();
    sb.append("[JobInfo ");
    
    sb.append(" id=");
    sb.append("'").append(com.sforce.ws.util.Verbose.toString(id)).append("'\n");
    sb.append(" operation=");
    sb.append("'").append(com.sforce.ws.util.Verbose.toString(operation)).append("'\n");
    sb.append(" object=");
    sb.append("'").append(com.sforce.ws.util.Verbose.toString(object)).append("'\n");
    sb.append(" createdById=");
    sb.append("'").append(com.sforce.ws.util.Verbose.toString(createdById)).append("'\n");
    sb.append(" createdDate=");
    sb.append("'").append(com.sforce.ws.util.Verbose.toString(createdDate)).append("'\n");
    sb.append(" systemModstamp=");
    sb.append("'").append(com.sforce.ws.util.Verbose.toString(systemModstamp)).append("'\n");
    sb.append(" state=");
    sb.append("'").append(com.sforce.ws.util.Verbose.toString(state)).append("'\n");
    sb.append(" externalIdFieldName=");
    sb.append("'").append(com.sforce.ws.util.Verbose.toString(externalIdFieldName)).append("'\n");
    sb.append("]\n");
    return sb.toString();
  }
}