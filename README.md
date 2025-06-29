# Botica_API

Botica_API es una API RESTful desarrollada en Java que proporciona las funcionalidades esenciales para la gestión de una botica (farmacia). Incluye control de usuarios, autenticación segura con JWT, manejo de caja, gestión de productos y stock bajo lógica FIFO, todo trabajando sobre una base de datos local. El despliegue se realiza fácilmente a través de un archivo `.jar`.

## Descarga rápida

[Descargar Botica_API.jar](https://github.com/LoP-1/Botica_API/releases/tag/api-rest)

## Funcionalidades principales

- **Gestión de usuarios:**  
  - Registro de nuevos usuarios.
  - Autenticación (login) segura utilizando JWT.
  - Rutas protegidas mediante tokens JWT para garantizar la seguridad de la API.

- **Caja:**  
  - Control de operaciones de caja (apertura, cierre, movimientos, etc.).

- **Productos y stock:**  
  - Alta, baja y modificación de productos.
  - Gestión de stock con lógica FIFO para salidas.
  - Consulta y control de existencias.

- **Base de datos local:**  
  - Persistencia de la información en una base de datos local.

## Despliegue

1. **Compilación:**  
   Asegúrate de tener Java instalado. Compila el proyecto y genera el archivo `.jar`.

2. **Ejecución:**  
   Ejecuta el archivo `.jar` con el siguiente comando:
   ```bash
   java -jar Botica_API.jar
   ```

3. **Configuración:**  
   Modifica los parámetros de conexión a la base de datos en el archivo de configuración según tu entorno local.

## Autenticación y Seguridad

- El acceso a la mayoría de los endpoints requiere autenticación con JWT.
- Obtén un token realizando login y úsalo en la cabecera `Authorization: Bearer <token>` para acceder a rutas protegidas.

## Estructura de la API (resumida)

- `/usuarios`  
  - POST `/registro`: Crear usuario  
  - POST `/login`: Autenticación y obtención de JWT

- `/productos`  
  - CRUD de productos  
  - Gestión de stock

- `/caja`  
  - Manejo de movimientos de caja

> **Nota:** Consulta la documentación interna de los endpoints o el código fuente para más detalles sobre las rutas y parámetros disponibles.

## Acerca del proyecto

Este proyecto busca ofrecer una solución sencilla y segura para la gestión de una botica tradicional, enfocándose en la facilidad de uso, seguridad y control de stock eficiente.

---
