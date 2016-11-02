# Leitura de uma URL e salvar em arquivo
# Objetivo
Acessar uma camera IP, obter a imagem de um quadro e salvar o arquivo da imagem.

#Arquitetura
Camera IP conectada a um Roteador
Programa roda em um computador com acesso ao Roteador
Camera IP pede autenticação basica HTTP

#Solução Desenvolvida
Programa ConnectToUrlUsingBasicAuthentication.java 
- acessa a url da camera ip
- autentica na camera ip
- recebe/le a stream de dados disponibilizada pela camera
- grava em um arquivo jpg essa stream de dados
- é uma prova de conceito, não é uma aplicação completa

#TO-DO
criar pom.xml
indicar dependencias externas
remover classes que não possibilitaram cumprir o objetivo
remover hardcode de parametros de acesso a camera e gravação do arquivo

