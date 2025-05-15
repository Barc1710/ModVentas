Create database ModuloVentas;
go
use ModuloVentas;
go
-- Tabla CLIENTES
CREATE TABLE clientes (
    id INT IDENTITY(1,1) PRIMARY KEY,
    nombres VARCHAR(100),
    apellidos VARCHAR(100),
    telefono VARCHAR(20),
    direccion VARCHAR(200),
    DNI VARCHAR(20)
);
go
-- Tabla ARTICULOS
CREATE TABLE articulos (
    id INT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(100),
    stock INT,
    precio DECIMAL(10,2),
    categoria VARCHAR(50)
);
go
-- Tabla VENTAS
CREATE TABLE ventas (
    id_venta INT IDENTITY(1,1) PRIMARY KEY,
    cliente_id INT,
    fecha DATE,
    total DECIMAL(10,2),
    documento VARCHAR(50),
    numero_serie VARCHAR(50),
    CONSTRAINT FK_ventas_clientes FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);

-- Tabla DETALLE_VENTAS
CREATE TABLE ventas (
    id_venta INT IDENTITY(1,1) PRIMARY KEY,
    cliente_id INT,
    fecha DATETIME DEFAULT GETDATE(),  -- Fecha se asigna automáticamente
    total DECIMAL(10,2),
    documento VARCHAR(50),
    numero_serie VARCHAR(50),
    CONSTRAINT FK_ventas_clientes FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);

go
-- Tabla USUARIO
CREATE TABLE Usuario (
    id INT IDENTITY(1,1) PRIMARY KEY,
    usuario VARCHAR(50),
    contraseña VARCHAR(100),
    nombre VARCHAR(100),
    apellido VARCHAR(100),
    rol VARCHAR(50)
);
