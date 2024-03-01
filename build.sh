cd manager

mvn clean package

cp target/*.jar src/main/docker

docker build -t crack-hash-manager src/main/docker

cd ..

cd worker

mvn clean package

cp target/*.jar src/main/docker

docker build -t crack-hash-worker src/main/docker


