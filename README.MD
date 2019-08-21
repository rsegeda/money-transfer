 # Money Transfer App
 
 Built with:
 1. sparkjava
 2. guice
 3. lombok
 4. junit
 5. mockito
 6. gradle
 
 ## Installation
 
 To obtain an executable script:
 
 Run `./gradlew clean build installDist`. 
 Application starting 
 scripts are going to be available under the [build/install/money-transfer/bin](build/install/money-transfer/bin).
 
 ## Project structure
 
 Application exposes Restful API on Account resource.
 For the sake of simplicity of this task, very simple objects definitions have been created.
 
 
 ## How to use
 
 GET/PUT/POST/DELETE
 /accounts
 /accounts/:uuid
 
 as well as restful manipulations on accounts are possible, there is transfer resource.
 
 POST
 /transfers
 
 > accounts_postman_collection.json contains query templates
 
 The following service contains Jetty embedded, which means it is necessary to start it only by 
 running a script that resides in the path mentioned before.
 