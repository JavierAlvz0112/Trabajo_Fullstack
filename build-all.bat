@echo off
FOR /f %%i IN ('docker ps -aq') DO docker rm -f %%i
FOR /f %%i IN ('docker images -aq') DO docker rmi -f %%i

cd api-gateway
call .\mvnw clean package -DskipTests
cd ..

cd auth-service
call .\mvnw clean package -DskipTests
cd ..

cd usuarios-service\usuarios-service
call .\mvnw clean package -DskipTests
cd ..\..

cd pagos-service
call .\mvnw clean package -DskipTests
cd ..

cd pedidos-service
call .\mvnw clean package -DskipTests
cd ..

cd productos-service
call .\mvnw clean package -DskipTests
cd ..

cd almacen-service
call .\mvnw clean package -DskipTests
cd ..

cd envios-service
call .\mvnw clean package -DskipTests
cd ..
