# KinalApp
KinalApp es una aplicación para manejar ventas, donde se pueden gestionar clientes, usuarios, productos y ventas, incluyendo los detalles de cada venta. Está hecha en Java con Spring Boot y usa MySQL para guardar toda la información. También se pueden probar las funciones usando Postman con JSON de manera sencilla.

## Teconologías Utilizadas
* **Java 17**
* **Spring Boot 4.0.2**
* **Maven** (Gestor de dependencias)
* **MySQL** (Sistema Gestor de Base de datos)

## Requisitos Previos
Antes de ejecutar la aplicación, debe tener instalado:
* JDK 17 o superior
* Maven Instalado
* Una instancia activa en MySQL
* Postman para probar los endpoints

## Instalación y Ejecución
1. Clonar repositorio
2. Cambiar a la rama de desarrollo por ejemplo para trabajar con la versión más actualizada
3. Abrir el proyecto en su IDE
4. Ejecutar la aplicación con Maven
5. Acceder a la interfaz web: http://localhost:8090/web/clientes
6. Abrir Postman y probar los endpoints usando los JSON proporcionados para Clientes, Usuarios, Productos, Ventas y DetalleVentas.


## Interfaz Web - Thymeleaf
La aplicación cuenta con una interfaz web completa:
- **Clientes**: http://localhost:8090/web/clientes
- **Productos**: http://localhost:8090/web/productos
- **Usuarios**: http://localhost:8090/web/usuarios
- **Ventas**: http://localhost:8090/web/ventas

## Endpoints
### Clientes
- **Listar todos los clientes**: GET http://localhost:8090/clientes
- **Listar clientes por estado**: GET http://localhost:8090/clientes/estado/0
- **Listar cliente por DPI**: GET http://localhost:8090/clientes/{DPICliente}
- **Agregar cliente**: POST http://localhost:8090/clientes
- **Actualizar cliente**: PUT http://localhost:8090/clientes/{DPICliente}
- **Eliminar cliente**: DELETE http://localhost:8090/clientes/{DPICliente}

### Usuarios
- **Listar todos los usuarios**: GET http://localhost:8090/usuarios
- **Listar usuario por código**: GET http://localhost:8090/usuarios/{codigoUsuario}
- **Agregar usuario**: POST http://localhost:8090/usuarios
- **Actualizar usuario**: PUT http://localhost:8090/usuarios/{codigoUsuario}
- **Eliminar usuario**: DELETE http://localhost:8090/usuarios/{codigoUsuario}

### Productos
- **Listar todos los productos**: GET http://localhost:8090/productos
- **Listar producto por ID**: GET http://localhost:8090/productos/{id}
- **Agregar producto**: POST http://localhost:8090/productos
- **Actualizar producto**: PUT http://localhost:8090/productos/{id}
- **Eliminar producto**: DELETE http://localhost:8090/productos/{id}

### Ventas
- **Listar todas las ventas**: GET http://localhost:8090/ventas
- **Listar venta por ID**: GET http://localhost:8090/ventas/{id}
- **Agregar venta**: POST http://localhost:8090/ventas
- **Actualizar venta**: PUT http://localhost:8090/ventas/{id}
- **Eliminar venta**: `DELETE http://localhost:8090/ventas/{id}

### Detalle de Ventas
- **Listar todos los detalles**: GET http://localhost:8090/detalle-ventas
- **Listar detalle por ID**: GET http://localhost:8090/detalle-ventas/{id}
- **Agregar detalle**: POST http://localhost:8090/detalle-ventas
- **Actualizar detalle**: PUT http://localhost:8090/detalle-ventas/{id}
- **Eliminar detalle**: DELETE http://localhost:8090/detalle-ventas/{id}