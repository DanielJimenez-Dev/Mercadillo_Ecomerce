# 🛒 El Mercadillo — E-Commerce Cross-Border, Fulfillment & On-Demand Logistics Platform

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.2-green?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16+-blue?style=flat-square&logo=postgresql)
![Hibernate](https://img.shields.io/badge/Hibernate-6.5.2.Final-59666C?style=flat-square&logo=hibernate)
![Next.js](https://img.shields.io/badge/Next.js-19-black?style=flat-square&logo=nextdotjs)
![Tailwind CSS](https://img.shields.io/badge/Tailwind%20CSS-v4-38bdf8?style=flat-square&logo=tailwindcss)
![Architecture](https://img.shields.io/badge/Architecture-Modular%20Monolith%20(DDD)-purple?style=flat-square)
![Status](https://img.shields.io/badge/Status-Fase%203%20en%20Proceso-yellow?style=flat-square)

Plataforma integral de comercio electrónico de grado empresarial que combina **comercio transfronterizo (Cross-Border)**, **operaciones de fulfillment/depósito**, **retail media (publicidad)** y una **red de logística crowdsourced on-demand (drivers)** con geolocalización y seguimiento en tiempo real.

---

## 📐 Enfoque de Arquitectura (ADR-001)

El proyecto está diseñado bajo el patrón **Monolito Modular guiado por el Dominio (Domain-Driven Design - DDD)**, estructurado para permitir una transición limpia hacia Microservicios a medida que la escala de negocio lo requiera.

### Stack Tecnológico Oficial

| Capa / Dominio | Tecnología Seleccionada | Justificación Técnica |
| :--- | :--- | :--- |
| **Backend (Core)** | Java 21 & Spring Boot 3.3+ | Estándar de la industria para alta concurrencia, tipado fuerte y ecosistema robusto. |
| **Seguridad & Auth** | Spring Security + JWT + OAuth2 | Autenticación stateless mediante tokens firmados y control de acceso RBAC. |
| **ORM / Persistencia** | Spring Data JPA & Hibernate 6 | Mapeo objeto-relacional eficiente con repositorio de consultas optimizado. |
| **Base de Datos Relacional** | PostgreSQL 16+ | Cumplimiento ACID estricto (fundamental para transacciones financieras, stock y fletes). |
| **Base de Datos Caché & Real-Time**| Redis | Reducción de latencia para carritos temporales, sesiones y rastreo geográfico de repartidores. |
| **Mensajería en Vivo** | Spring WebSockets (STOMP) | Emisión instantánea de ofertas de fletes a conductores cercanos y tracking en vivo. |
| **Frontend Framework** | Next.js (React 19) | Server-Side Rendering (SSR) indispensable para SEO en productos y carga ultra rápida. |
| **Estilos & UI** | Tailwind CSS v4 + Framer Motion | Diseño futurista, estados vidriados (Glassmorphism) y microinteracciones fluidas. |
| **Documentación API** | OpenAPI 3.0 / Swagger UI | Contrato de interfaz interactivo para pruebas y desacoplamiento Front/Back. |
| **Control de Versiones** | Git & GitHub | Trazabilidad de código y flujos de trabajo (GitFlow). |

---

## 🗂️ Dominios del Sistema (8 Dominios Unificados)

1. **Dominio 1: Usuarios, Seguridad y Direcciones:** `users`, `roles`, `user_roles`, `user_addresses`.
2. **Dominio 2: Catálogo y Tiendas:** `shops`, `categories`, `products`, `product_images`, `customs_tariffs`.
3. **Dominio 3: Transacciones y Carrito:** `carts`, `cart_items`, `orders`, `order_items`.
4. **Dominio 4: Finanzas y Publicidad (Retail Media):** `seller_wallets`, `wallet_transactions`, `ad_campaigns`.
5. **Dominio 5: Logística, Transporte Híbrido y Tracking:** `shipments`, `shipment_tracking_events`.
6. **Dominio 6: Red de Conductores On-Demand (Crowdsourced Logistics):** `drivers`, `driver_wallets`, `delivery_offers`, `driver_assignments`.
7. **Dominio 7: Warehouse & Fulfillment:** `warehouses`, `warehouse_inventories`, `fulfillment_rates`.
8. **Dominio 8: Aduanas e Importaciones (Cross-Border):** `inbound_shipments`, `customs_declarations`.

---

## 🔐 Matriz de Permisos (RBAC - 5 Roles)

| Módulo / Acción | Comprador | Vendedor | Repartidor (Driver) | Operador Depósito | Admin |
| :--- | :---: | :---: | :---: | :---: | :---: |
| Autenticación y Registro | ✅ | ✅ | ✅ | ❌ (Solo Admin) | ✅ |
| Ver Catálogo y Buscar | ✅ | ✅ | ✅ | ✅ | ✅ |
| Checkout / Comprar | ✅ | ❌ | ❌ | ❌ | ❌ |
| Publicar / Modificar Productos | ❌ | ✅ (Propios) | ❌ | ❌ | ✅ (Todos) |
| Crear Campañas Ads | ❌ | ✅ | ❌ | ❌ | ✅ |
| Aceptar Fletes y Ver Rutas | ❌ | ❌ | ✅ | ❌ | ✅ |
| Gestionar Depósito / Inbound | ❌ | ✅ (Sus envíos)| ❌ | ✅ | ✅ |
| Ver Billetera y Pedir Retiros | ❌ | ✅ | ✅ | ❌ | ✅ |
| Aprobar Drivers y Ajustar Fees | ❌ | ❌ | ❌ | ❌ | ✅ |

---

## 📊 Estado de Avance y Control del Proyecto

### 🟢 Lo que YA hemos realizado (Completado)

* [x] **Fase 1: Especificación y Arquitectura (ADR-001)**
  * Definición formal de los 8 dominios de negocio.
  * Matriz RBAC para los 5 roles del sistema (`BUYER`, `SELLER`, `DRIVER`, `WAREHOUSE_OPERATOR`, `ADMIN`).
  * Selección y registro formal del Stack Tecnológico.

* [x] **Fase 2: Configuración de Entorno e Inicialización de BD**
  * Proyecto Spring Boot 3.3.2 iniciado con Java 21 SDK.
  * Conexión exitosa del ORM (Hibernate 6.5.2) a la base de datos PostgreSQL `ecommerce_db` en el puerto `5433`.
  * Mapeo JPA inicial e inserción automática del catálogo de roles en base de datos.
  * Estrategia DDL ajustada de `create-drop` a `update` para preservar la persistencia de datos.

---

### 🟡 En Proceso Actual (Fase 3: Backend Core)

* [ ] **Módulo de Autenticación & Seguridad JWT**
  * [ ] Configuración de `SecurityFilterChain` en Spring Security.
  * [ ] Servicio de generación y validación de JSON Web Tokens (JWT).
  * [ ] Endpoints de Autenticación (`POST /api/v1/auth/register`, `POST /api/v1/auth/login`).
  * [ ] DTOs de Request/Response de autenticación y manejo de errores globales (`@ControllerAdvice`).

---

### 🔴 Lo que QUEDA PENDIENTE (Roadmap Futuro)

#### **Fase 3 (Cont.): Desarrollo Backend Restante**
* [ ] Mapeo completo de Entidades JPA faltantes (Billeteras, Fletes, Inbound Cross-Border, Depósitos).
* [ ] Repositorios, Servicios y Controladores REST para el Catálogo y Tiendas.
* [ ] Integración de Spring WebSockets (STOMP) para notificaciones y asignación de drivers en tiempo real.
* [ ] Configuración de Swagger / OpenAPI 3.0 para la documentación interactiva de endpoints.

#### **Fase 4: Frontend Futurista (Next.js 19 + Tailwind CSS v4)**
* [ ] Configuración del proyecto Next.js en la carpeta `frontend/`.
* [ ] Integración de Tailwind CSS v4, Framer Motion y componentes Glassmorphism.
* [ ] Vistas UI: Landing Page, Catálogo SSR, Dashboard Vendedor, Panel de Repartidor/Driver, Panel de Depósito y Panel Admin.

#### **Fase 5: Módulos Complejos y Lógica Avanzada**
* [ ] Algoritmo de simulación de aranceles de aduana para compras Cross-Border.
* [ ] Algoritmo de asignación geográfica de choferes On-Demand.
* [ ] Sistema de Billetera (Wallets) con Split de Pagos (Comisión de plataforma vs. Ganancia de Vendedor/Driver).

#### **Fase 6: Despliegue, Branding y Publicación**
* [ ] Diseño del Isologotipo e identidad visual.
* [ ] Despliegue en la nube (AWS / Render / Vercel).
* [ ] Elaboración del informe técnico y publicación de caso de estudio en LinkedIn.

---

## 🛠️ Requisitos e Instalación Local

### Requisitos Previos
* **Java Development Kit (JDK):** Version 21+
* **PostgreSQL:** Version 16+ (escuchando en el puerto 5433)
* **VS Code:** Con extensiones de Java & Maven instaladas

### Pasos para Ejecutar el Backend

1. **Clonar el repositorio:**
   ```bash
   git clone [https://github.com/tu-usuario/elmercadillo_ecommerce.git](https://github.com/tu-usuario/elmercadillo_ecommerce.git)
   cd elmercadillo_ecommerce/backend

### esta es una linea de prueba