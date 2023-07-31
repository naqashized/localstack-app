# localstack-app
This is a springboot app to upload card details along with images. It has integration with AWS S3 bucket but locally it utilizes an interesting docker container project called localstack.

## Localstack (https://localstack.cloud/)
We would never like to upload images directly to AWS cloud's S3 bucket during development phase for it has no business value. We can simply utilize localstack project running on docker
as container. Localstack emulates cloud locally on your machine and let you upload images using S3 client on the same machine. Localstack supports many AWS technologies like S3, Congnito, SNS etc.


Please follow below instructions how you can run app and upload image while taking advantage of docker container;


1- Localstack container is setup in docker-compose file. Run file using below command;

docker-compose up

This will run localstack container inside docker on port 4566. Docker shall already be running in your machine.


2- Please setup AWS CLI on your machine first so you can create S3 bucket with below command;


aws --endpoint-url=http://localhost:4566 s3 mb s3://cars-storage


This will create cars-storage bucket which I have setup in the app's local profile.


3-  And finally run localstack-app in your IDE using local profile. 


There are two API endpoints;


1- Post method to persist car details http://localhost:8080/v1/cars


``    {
"model":2022,
"series":"M1",
"image":"iVBORw0KG.....valid base64 code"
}``

2- Same API endpoint using GET method to fetch all cars- http://localhost:8080/v1/cars


Sample API response:-


```[
  {
    "id": 1,
    "model": 2022,
    "series": "M1",
    "imageUrl": "http://localhost:4566/cars-storage/cars/5f1473b2-f458-4541-a14e-107f77854e1d.png"
  }
]```
