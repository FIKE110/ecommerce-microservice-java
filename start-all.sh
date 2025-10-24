#!/bin/bash
services=("user-service" "order-service" "notification-service")

for service in "${services[@]}"
do
  echo "Starting $service..."
  (cd $service && mvn spring-boot:run &)   # run in background
done

wait
