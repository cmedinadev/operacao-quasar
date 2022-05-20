# Operação Fogo de Quasar

Operação Fogo de Quasar é um desafio da Empresa Mercado Livre para os candidatos que se aplicam a posição na empresa.

A missão é criar um programa em que retorne a fonte e o conteúdo da mensagem de auxílio. Para isso, contará com 3 satélites que te permitirão triangular a posição, mas cuidado, a mensagem não estará completa em cada satélite devido ao campo de asteroides em frente a nave.

# Sobre o projeto 

Este projeto foi desenvolvido na tecnologia Java como um serviço REST/API que processa as informções recebidas de 3 satélites para determinar a localização do emissor e o conteúdo da mensagem recebida.

## O Problema 

Determinar a localização do emissor da mensagem com base nas distâncias.

## Solução

Para solucionar o problema foi utilizado um pouco de geometria analítica para determinar a posição do emissor da mensagem com base no sistema de triangulação. Dizer que o receptorestá a uma distância X, significa dizer que ele pode estar em qualquer lugar da esfera imaginária, em qualquer direção. 

Então, para determinarmos a localização exata, utilizamos a interseção de 3 esferas (círculos) com base nas distâncias do emissor com o receptor. O ponto de interseção entre as 3 esferas é a localização do emissor.

## Execução 

Para rodar o projeto é necessário ter instalado:

* Maven 3.6+
* JRE 17+

Rode na pasta raiz os seguintes comandos:

    mvn clean package
    mvn spring-boot:run

## Recursos

**HTTP POST /topsecret/**

***Payload***

    {
        "satellites": [
            {
            "name": "kenobi",
            "distance": 721.11,
            "message": ["this", "", "", "secret", ""]
            },
            {
            "name": "skywalker",
            "distance": 300,
            "message": ["", "is", "", "", "message"]
            },
            {
            "name": "sato",
            "distance": 412.31,
            "message": ["this", "", "a", "", ""]
            }
        ]
    }

***Resposta***

    RESPONSE CODE: 200 
    {
        "position": { "x": 100.0, "y": 200.0 },
        "message": "this is a secret message" 
    }

**HTTP POST /topsecret_split/{satelliteName}**

Este recurso armazena em memória as informações do satélite que chegam com defasagem. 

***Payload***

    {
        "distance": 300,
        "message": ["", "is", "", "", "message"]
    }

***Resposta***

    RESPONSE CODE: 202


**HTTP GET /topsecret_split/**

Este recurso apenas retorna informações quando os dados dos 3 satélites estão disponíveis, caso contrário, retorna o status code 500 e a mensagem "Não há informações suficiente". Ao processar as informações dos 3 satélites, os dados armazenados em memória são limpos e um novo fluxo é reinicado.

***Resposta***

    RESPONSE CODE: 200 
    {
        "position": { "x": 100.0, "y": 200.0 },
        "message": "this is a secret message" 
    }


## Implantação e Testes

### Implantação
O deploy foi realizado na plataforma de computação em nuvem Microsoft Azure e utilizou o recurso Azure Web Apps.

Para realizar o deploy de forma automática foi adicionado ao pom.xml o plugin "azure-webapp-maven-plugin". 

Passos para implantar a solução no Azure Web Apps.

    1. Instalar o azure cli.
    2. Realizar o login: az login
    3. Criar um grupo de recurso chamado "rsg_meli" 
       Exemplo: az group create -l brazilsouth -n rsg_meli
    4. Realizar o deploy: mvn package azure-webapp:deploy

Depois que a implantação for concluída, seu aplicativo estará pronto em http://<appName>.azurewebsites.net/.

O nome do aplicativo está definido no pom.xml, nas configurações do plugin de deploy em: <appName>meli-topsecret</appName>

## Testes

Para testar o serviço implantado no Azure Web Apps, execute os recursos com a URL base: http://meli-topsecret.azurewebsites.net/.

Caso o serviço retorne um Code Status 504, aguarde uns minutos e tente novamente, o serviço poderá estar adormecido para liberação de recursos que não estão em uso.

Exemplos: 

**HTTP POST /topsecret/**

    curl -X POST -H "Content-type: application/json" -d '{
        "satellites": [
            {
                "name": "kenobi",
                "distance": 721.11,
                "message": ["this", "", "", "secret", ""]
            },
            {
                "name": "skywalker",
                "distance": 300,
                "message": ["", "is", "", "", "message"]
            },
            {
                "name": "sato",
                "distance": 412.31,
                "message": ["this", "", "a", "", ""]
            }
        ]
    }' 'https://meli-topsecret.azurewebsites.net/topsecret/'



**HTTP POST /topsecret_split/{satelliteName}**

    curl -X POST -H "Content-type: application/json" -d '{
        "distance": 721.11,
        "message": ["this", "", "", "secret", ""]
    }' 'http://meli-topsecret.azurewebsites.net/topsecret_split/kenobi'    

**HTTP GET /topsecret_split/**

    curl -X GET -H "Content-type: application/json" 'http://meli-topsecret.azurewebsites.net/topsecret_split/'

