# Spring Boot + AWS S3
Web app that lists, uploads and downloads objects from an S3 bucket

## Credits
This application is based on three (3) sample apps:
1) [AWS S3 samples](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/java/example_code/s3) from the AWS SDK for Java github repo
1) [Uploading files](https://spring.io/guides/gs/uploading-files/) from the Spring Guides
1) [Spring Cloud AWS S3](https://github.com/eugenp/tutorials/tree/master/spring-cloud/spring-cloud-aws/src/main/java/com/baeldung/spring/cloud/aws/s3) from Baeldung's Tutorials Github repo

## Increase file size limit for uploads
Currently, the app only uploads files of up to 100 MB. If you want to increase this limit, Edit the `application.properties` files and change these 2 variables:
```
spring.servlet.multipart.max-file-size=102400KB
spring.servlet.multipart.max-request-size=102400KB
```

## Configure the app to run locally
To run the app on your laptop, you need to:
1) Edit the `application.properties` files and replace theses placeholders `YOUR_ACCESS_KEY`, `YOUR_SECRET_KEY`, `YOUR_REGION` and `YOUR_BUCKET`  with your AWS information:

```
cloud.aws.credentials.accessKey=${vcap.services.gt-secure-bucket-1.credentials.access_key_id:YOUR_ACCESS_KEY}
cloud.aws.credentials.secretKey=${vcap.services.gt-secure-bucket-1.credentials.secret_access_key:YOUR_SECRET_KEY}
cloud.aws.region.static=${vcap.services.gt-secure-bucket-1.credentials.region:YOUR_REGION}
s3Bucket=${vcap.services.gt-secure-bucket-1.credentials.bucket:YOUR_BUCKET}
```

## Configure the app to run on Cloud Foundry
You can run this app on any Cloud Foundry installation such as [Pivotal Web Services](https://run.pivotal.io/) or your own [Pivotal Cloud Foundry](https://network.pivotal.io/products/elastic-runtime/).

### Assumption
This sample uses the [Pivotal Cloud Foundry Service Broker for AWS](https://network.pivotal.io/products/aws-services/)

### Create the AWS S3 service
[Here are the instructions to create an AWS S3 bucket](https://docs.pivotal.io/aws-services/creating.html#s3) via the `cf cli`.

For example:
```
cf create-service aws-s3 standard gt-secure-bucket-1
```

## Build the project

After you configure the app, run this command:
```
mvn clean package
```

It will generate an Über JAR file in the `target` folder called `spring-cloud-s3-gui-0.1.0.jar`

## Run the app locally
Use this command to run the app locally

```
java -jar target/spring-cloud-s3-gui-0.1.0.jar
```

After the application starts, open a browser window and type: [http://localhost:8080](http://localhost:8080)

## Run the app on Cloud Foundry
Use this command to run on Cloud Foundry

```
cf push
```

After the application starts, open a browser window and type the URL that Cloud Foundry assigned to your app, which you can find by typing:

```
cf app spring-cloud-s3-gui
```
