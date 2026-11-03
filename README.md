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

## Stack Tecnológico
* Lenguaje: Java 21.
* Framework principal: Spring Boot.
* Interfaz: Vaadin.
* Base de datos: PostgreSQL.
* Gestor de paquetes: Maven.
* Control de versiones: Git y GitHub.



## Flujo de trabajo
El equipo usa "Conventional Commits" y trabaja en ramas secundarias antes de unir a la rama `main`.

* feat: nuevas funcionalidades. (Ej: feat(reservas): agregar botón)
* fix: solucionar errores. (Ej: fix(login): corregir acceso)
* docs: documentación. (Ej: docs(readme): actualizar texto)
* style: diseño. (Ej: style(ui): aplicar colores lumo)

## Licencia y créditos del equipo de trabajo
Este proyecto se distribuye bajo los términos del archivo LICENSE.md incluido en la raíz del repositorio.
* Integrante 1: Jacob Vanegas Martinez - Base de datos
* Integrante 2: Thomas Restrepo Rodriguez - Frontend
* Integrante 3: Brenda Sofia Rivero Reyes - Documentación
