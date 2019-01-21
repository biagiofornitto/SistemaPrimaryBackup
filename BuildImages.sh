#!/bin/sh

sudo docker build -f DockerfileMySQL.yaml  -t mymysql_img .
sudo docker build -f DockerfileReaderFE.yaml  -t readerfe_img .
sudo docker build -f DockerfileReplica1.yaml  -t replicaprimary_img .
sudo docker build -f DockerfileReplica2.yaml -t replica2_img .
sudo docker build -f DockerfileReplica3.yaml -t replica3_img .
sudo docker build -f DockerfileReplica4.yaml  -t replica4_img .
sudo docker build -f DockerfileSW.yaml  -t subscriberfe_img .


