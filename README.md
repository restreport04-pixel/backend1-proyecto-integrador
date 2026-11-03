# Proyecto integrador B1-2026-2 G2

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Vaadin](https://img.shields.io/badge/Vaadin-00B4F0?style=for-the-badge&logo=vaadin&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)

## Resumen Ejecutivo
Esta es una plataforma web para gestionar los usuarios, espacios y reservas de un coworking.
Sirve para llevar un control de los registros desde una interfaz gráfica sencilla que se conecta directamente a la base de datos.

## Arquitectura
El proyecto se divide en varias capas para mantener el orden:
* Frontend: la interfaz visual (Vaadin).
* Backend: la lógica y el acceso a datos (Spring Boot).
* Base de datos: almacenamiento de la información (PostgreSQL).

## Diagrama MER
A continuación se muestra el modelo entidad-relación de la base de datos:

![Diagrama MER](assets/diagrama-mer.png)
