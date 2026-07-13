@echo off
cd api-gateway
call .\mvnw clean
cd ..

cd auth-service
call .\mvnw clean
cd ..

cd usuarios-service\usuarios-service
call .\mvnw clean
cd ..\..

cd pagos-service
call .\mvnw clean
cd ..

cd pedidos-service
call .\mvnw clean
cd ..

cd productos-service
call .\mvnw clean
cd ..

cd almacen-service
call .\mvnw clean
cd ..

cd envios-service
call .\mvnw clean
cd ..
