1. Git PushNotification API
server port : DEFAULT 8080 port
run by : java -jar <jarName>
http://127.0.0.1:8190/gitpushnotification

Body
{
    "repoPath":"/contact-ms/src/main/java/com/digite/ms/contact/ContactDelete.java",
    "projectName":"Contact-MS"
}
file from where data is matched : repositorydetails.json

Sample Response
{
  "projectName": "Contact-MS",
  "id": 1237,
  "repositoryPath": "/contact-ms/src/main/java/com/digite/ms/contact/ContactDelete.java"
}


This API will find the data from file in classpath if present or not based on repoPath
  If present, will add data to kafka
  KafkaTopic :digiteprojectdetail


2. AddProject Details to DB
  Server port :8085
  run by : java -jar <jarName>
  Added a polling consumer on kafka topic digiteprojectdetail

  Will fetch the record from kafka topic and check on mongo if already present will not add.
  In case data is not present with kafka message on mongo, will add the same .
  Added an onException clause in case failure occurs for providing faultTolerance


  3 RetrieveProject Details from DB
    server port : 8086

    run by : java -jar <jarName>

    This API will call mongo with the body sent in req .
    Will respond with the mongo data

    http://127.0.0.1:8190/gitpushnotification
    Body
    {
    "repositoryPath":"/contact-ms/src/main/java/com/digite/ms/contact/ContactDelete.java",
    "id":1237,
    "projectName":"Contact-MS"
    }

    Mongo data : Document{{_id=60cf288b96c003060f72a366, projectName=Contact-MS, id=1237, repositoryPath=/contact-ms/src/main/java/com/digite/ms/contact/ContactDelete.java}}
     Sample Response :

     {
    "_id": "60cf288b96c003060f72a366",
    " projectName": "Contact-MS",
    " id": "1237",
    " repositoryPath": "/contact-ms/src/main/java/com/digite/ms/contact/ContactDelete.java",
    "message": "success"
    }
