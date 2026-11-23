package com.example;

// Modelos del proyecto de Coworking (exactamente alineados a SQL Server)
import com.example.model.Espacio;
import com.example.model.Reserva;
import com.example.model.TipoEspacio;
import com.example.model.Usuario;

// Servicios del proyecto (Lógica de negocio y reportes)
import com.example.service.CrudService;
import com.example.service.ExcelReportService;
import com.example.service.PdfReportService;

// Componentes visuales de Vaadin Flow
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Coworking Studio - Plataforma de Reservas")
@Route("")
public class MainView extends VerticalLayout {

    // 1. Declaración de las 3 dependencias inyectadas por Spring
    private final CrudService crudService;
    private final ExcelReportService excelReportService;
    private final PdfReportService pdfReportService;

    // Formato amigable para mostrar fechas y horas en las tablas
    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Etiquetas dinámicas para las tarjetas de resumen del Administrador (KPIs)
    private final Span lblTotalUsuarios = new Span("0");
    private final Span lblTotalEspacios = new Span("0");
    private final Span lblTotalReservas = new Span("0");
    private final Span lblIngresosTotales = new Span("$ 0");

    // Inyección de dependencias por constructor: Spring suministra los servicios automáticamente
    public MainView(CrudService crudService, ExcelReportService excelReportService,
                    PdfReportService pdfReportService) {
        this.crudService = crudService;
        this.excelReportService = excelReportService;
        this.pdfReportService = pdfReportService;

        // Configuración base del contenedor principal
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("background-color", "#F1F5F9");
        getStyle().set("overflow-y", "auto");

        // Iniciamos siempre mostrando la pantalla de Inicio de Sesión / Registro
        mostrarVistaAcceso();
    }

    // =========================================================================
    // PANTALLA 0: INICIO DE SESIÓN Y REGISTRO (CONTROL DE ROLES)
    // =========================================================================
    private void mostrarVistaAcceso() {
        removeAll();
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setAlignItems(FlexComponent.Alignment.CENTER);
        getStyle().set("background-image",
                "linear-gradient(135deg, rgba(15, 23, 42, 0.90) 0%, rgba(30, 41, 59, 0.85) 100%), " +
                "url('https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=1600&q=80')");
        getStyle().set("background-size", "cover");
        getStyle().set("background-position", "center");

        // Tarjeta central dividida en panel visual izquierdo y formulario derecho
        HorizontalLayout contenedorLogin = new HorizontalLayout();
        contenedorLogin.setMaxWidth("920px");
        contenedorLogin.setWidthFull();
        contenedorLogin.setSpacing(false);
        contenedorLogin.getStyle().set("background-color", "#FFFFFF");
        contenedorLogin.getStyle().set("border-radius", "16px");
        contenedorLogin.getStyle().set("overflow", "hidden");
        contenedorLogin.getStyle().set("box-shadow", "0 20px 40px rgba(0, 0, 0, 0.35)");
        contenedorLogin.getStyle().set("flex-wrap", "wrap");

        // --- LADO IZQUIERDO: PRESENTACIÓN VISUAL ---
        VerticalLayout panelVisual = new VerticalLayout();
        panelVisual.setPadding(true);
        panelVisual.setSpacing(true);
        panelVisual.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        panelVisual.getStyle().set("flex", "1 1 340px");
        panelVisual.getStyle().set("padding", "36px");
        panelVisual.getStyle().set("background-image",
                "linear-gradient(180deg, rgba(15, 23, 42, 0.78) 0%, rgba(15, 23, 42, 0.94) 100%), " +
                "url('https://images.unsplash.com/photo-1524758631624-e2822e304c36?auto=format&fit=crop&w=900&q=80')");
        panelVisual.getStyle().set("background-size", "cover");
        panelVisual.getStyle().set("background-position", "center");

        Icon logo = VaadinIcon.BUILDING.create();
        logo.getStyle().set("width", "36px");
        logo.getStyle().set("height", "36px");
        logo.getStyle().set("color", "#38BDF8");

        H2 marca = new H2("Coworking Studio");
        marca.getStyle().set("color", "#FFFFFF");
        marca.getStyle().set("margin", "10px 0 4px 0");

        Span lema = new Span("Espacios diseñados para impulsar tu productividad. Reserva salas de juntas, oficinas privadas o puestos flexibles en segundos.");
        lema.getStyle().set("color", "#CBD5E1");
        lema.getStyle().set("font-size", "0.92rem");
        lema.getStyle().set("line-height", "1.5");

        VerticalLayout topVisual = new VerticalLayout(logo, marca, lema);
        topVisual.setPadding(false);
        topVisual.setSpacing(false);

        Span pieVisual = new Span("Acceso dual: Portal de Clientes & Panel Administrativo");
        pieVisual.getStyle().set("color", "#7DD3FC");
        pieVisual.getStyle().set("font-size", "0.78rem");
        pieVisual.getStyle().set("font-weight", "600");

        panelVisual.add(topVisual, pieVisual);

        // --- LADO DERECHO: PESTAÑAS DE LOGIN CLIENTE, REGISTRO Y ADMIN ---
        VerticalLayout panelFormularios = new VerticalLayout();
        panelFormularios.setPadding(true);
        panelFormularios.setSpacing(true);
        panelFormularios.getStyle().set("flex", "1 1 420px");
        panelFormularios.getStyle().set("padding", "32px");

        TabSheet tabsAcceso = new TabSheet();
        tabsAcceso.setWidthFull();

        // 1. TAB INGRESO CLIENTE (Valida contra la tabla Usuario de SQL Server)
        VerticalLayout tabCliente = new VerticalLayout();
        tabCliente.setPadding(false);
        tabCliente.setSpacing(true);
        tabCliente.getStyle().set("padding-top", "16px");

        Span infoCliente = new Span("Ingresa con el correo electrónico con el que te registraste:");
        infoCliente.getStyle().set("font-size", "0.85rem");
        infoCliente.getStyle().set("color", "#64748B");

        EmailField txtLoginEmail = new EmailField("Correo Electrónico");
        txtLoginEmail.setWidthFull();
        txtLoginEmail.setPrefixComponent(VaadinIcon.ENVELOPE.create());
        txtLoginEmail.setPlaceholder("ejemplo@correo.com");

        Button btnEntrarCliente = new Button("Ingresar como Cliente", VaadinIcon.SIGN_IN.create());
        btnEntrarCliente.setWidthFull();
        btnEntrarCliente.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);

        btnEntrarCliente.addClickListener(e -> {
            String correo = txtLoginEmail.getValue() != null ? txtLoginEmail.getValue().trim() : "";
            if (correo.isEmpty()) {
                mostrarNotificacion("Por favor escribe tu correo electrónico", NotificationVariant.LUMO_ERROR);
                return;
            }
            // Buscamos el usuario en la base de datos por su correo
            Usuario encontrado = null;
            for (Usuario u : crudService.listarTodos(Usuario.class)) {
                if (u.getEmail() != null && u.getEmail().equalsIgnoreCase(correo)) {
                    encontrado = u;
                    break;
                }
            }
            if (encontrado != null) {
                mostrarNotificacion("¡Bienvenido de nuevo, " + encontrado.getNombre() + "!", NotificationVariant.LUMO_SUCCESS);
                mostrarVistaCliente(encontrado);
            } else {
                mostrarNotificacion("No encontramos ese correo. Regístrate en la pestaña 'Crear Cuenta'", NotificationVariant.LUMO_ERROR);
            }
        });

        tabCliente.add(infoCliente, txtLoginEmail, btnEntrarCliente);

        // 2. TAB REGISTRO DE NUEVO CLIENTE (Guarda directo en tabla Usuario)
        VerticalLayout tabRegistro = new VerticalLayout();
        tabRegistro.setPadding(false);
        tabRegistro.setSpacing(true);
        tabRegistro.getStyle().set("padding-top", "16px");

        TextField txtRegNombre = new TextField("Nombre Completo");
        txtRegNombre.setWidthFull();
        txtRegNombre.setPrefixComponent(VaadinIcon.USER.create());
        txtRegNombre.setPlaceholder("Ej: Thomas Restrepo");

        EmailField txtRegEmail = new EmailField("Correo Electrónico");
        txtRegEmail.setWidthFull();
        txtRegEmail.setPrefixComponent(VaadinIcon.ENVELOPE.create());
        txtRegEmail.setPlaceholder("thomas@correo.com");

        TextField txtRegTelefono = new TextField("Teléfono de Contacto");
        txtRegTelefono.setWidthFull();
        txtRegTelefono.setPrefixComponent(VaadinIcon.PHONE.create());
        txtRegTelefono.setPlaceholder("Ej: 3001234567");

        Button btnRegistrar = new Button("Crear Cuenta y Entrar", VaadinIcon.USER_CHECK.create());
        btnRegistrar.setWidthFull();
        btnRegistrar.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);

        btnRegistrar.addClickListener(e -> {
            if (txtRegNombre.isEmpty() || txtRegEmail.isEmpty()) {
                mostrarNotificacion("Nombre y correo son obligatorios para registrarte", NotificationVariant.LUMO_ERROR);
                return;
            }
            // Verificamos que no exista ya ese correo
            for (Usuario existente : crudService.listarTodos(Usuario.class)) {
                if (existente.getEmail() != null && existente.getEmail().equalsIgnoreCase(txtRegEmail.getValue().trim())) {
                    mostrarNotificacion("Este correo ya está registrado. Inicia sesión en la primera pestaña.", NotificationVariant.LUMO_CONTRAST);
                    return;
                }
            }
            Usuario nuevo = new Usuario(null, txtRegNombre.getValue().trim(), txtRegEmail.getValue().trim(), txtRegTelefono.getValue().trim());
            crudService.guardar(nuevo);

            // Recuperamos el usuario recién guardado con su ID generado por SQL Server
            Usuario recienCreado = nuevo;
            for (Usuario u : crudService.listarTodos(Usuario.class)) {
                if (u.getEmail() != null && u.getEmail().equalsIgnoreCase(nuevo.getEmail())) {
                    recienCreado = u;
                    break;
                }
            }
            mostrarNotificacion("¡Cuenta creada con éxito! Bienvenido, " + recienCreado.getNombre(), NotificationVariant.LUMO_SUCCESS);
            mostrarVistaCliente(recienCreado);
        });

        tabRegistro.add(txtRegNombre, txtRegEmail, txtRegTelefono, btnRegistrar);

        // 3. TAB ACCESO ADMINISTRADOR (BACKOFFICE)
        VerticalLayout tabAdmin = new VerticalLayout();
        tabAdmin.setPadding(false);
        tabAdmin.setSpacing(true);
        tabAdmin.getStyle().set("padding-top", "16px");

        Span infoAdmin = new Span("Acceso exclusivo para gestión de espacios, tarifas y reportes globales.");
        infoAdmin.getStyle().set("font-size", "0.85rem");
        infoAdmin.getStyle().set("color", "#64748B");

        TextField txtAdminUser = new TextField("Usuario Administrador");
        txtAdminUser.setWidthFull();
        txtAdminUser.setPrefixComponent(VaadinIcon.SHIELD.create());
        txtAdminUser.setValue("admin@coworking.com");

        PasswordField txtAdminPass = new PasswordField("Contraseña");
        txtAdminPass.setWidthFull();
        txtAdminPass.setPrefixComponent(VaadinIcon.LOCK.create());
        txtAdminPass.setPlaceholder("Clave por defecto: admin123");

        Button btnEntrarAdmin = new Button("Entrar al Backoffice", VaadinIcon.COG.create());
        btnEntrarAdmin.setWidthFull();
        btnEntrarAdmin.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);

        btnEntrarAdmin.addClickListener(e -> {
            if ("admin@coworking.com".equalsIgnoreCase(txtAdminUser.getValue().trim())
                    && "admin123".equals(txtAdminPass.getValue())) {
                mostrarNotificacion("Sesión de Administrador iniciada", NotificationVariant.LUMO_SUCCESS);
                mostrarVistaAdmin();
            } else {
                mostrarNotificacion("Credenciales de administrador incorrectas (usa admin123)", NotificationVariant.LUMO_ERROR);
            }
        });

        tabAdmin.add(infoAdmin, txtAdminUser, txtAdminPass, btnEntrarAdmin);

        tabsAcceso.add("Iniciar Sesión", tabCliente);
        tabsAcceso.add("Registrarse", tabRegistro);
        tabsAcceso.add("Administrador", tabAdmin);

        panelFormularios.add(tabsAcceso);
        contenedorLogin.add(panelVisual, panelFormularios);
        add(contenedorLogin);
    }

    // =========================================================================
    // ROL 1: PORTAL EXCLUSIVO DEL CLIENTE (EXPLORAR Y RESERVAR SIN BACKOFFICE)
    // =========================================================================
    private void mostrarVistaCliente(Usuario cliente) {
        removeAll();
        setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        setAlignItems(FlexComponent.Alignment.STRETCH);
        getStyle().remove("background-image");
        getStyle().set("background-color", "#F1F5F9");

        // --- BANNER SUPERIOR DEL CLIENTE ---
        HorizontalLayout headerCliente = crearEncabezado(
                "PORTAL DE CLIENTES • COWORKING STUDIO",
                "Hola, " + cliente.getNombre(),
                "Explora nuestros espacios disponibles, consulta tarifas por hora y agenda tu próxima reserva.",
                VaadinIcon.USER_CHECK,
                true
        );

        TabSheet tabsCliente = new TabSheet();
        tabsCliente.setWidthFull();
        tabsCliente.addThemeVariants(TabSheetVariant.LUMO_BORDERED);
        tabsCliente.getStyle().set("background-color", "#FFFFFF");
        tabsCliente.getStyle().set("border-radius", "14px");
        tabsCliente.getStyle().set("box-shadow", "0 4px 15px rgba(0, 0, 0, 0.05)");

        // Selector de espacio del formulario de reserva (lo declaramos aquí para conectarlo con el catálogo)
        ComboBox<Espacio> cmbEspacioReserva = new ComboBox<>("Espacio Seleccionado");
        cmbEspacioReserva.setWidthFull();
        cmbEspacioReserva.setItemLabelGenerator(esp -> esp.getNombre() + " — $" + String.format("%,.0f", esp.getPrecioHora()) + "/hora (Máx. " + esp.getCapacidad() + " pers.)");

        // --- PESTAÑA A: CATÁLOGO VISUAL DE ESPACIOS + RESERVA RÁPIDA ---
        VerticalLayout vistaExplorar = new VerticalLayout();
        vistaExplorar.setWidthFull();
        vistaExplorar.setPadding(true);
        vistaExplorar.setSpacing(true);

        // Cuadrícula de tarjetas visuales con fotos reales de los espacios
        HorizontalLayout galeriaTarjetas = new HorizontalLayout();
        galeriaTarjetas.setWidthFull();
        galeriaTarjetas.setSpacing(true);
        galeriaTarjetas.getStyle().set("flex-wrap", "wrap");

        List<Espacio> listaEspacios = crudService.listarTodos(Espacio.class);
        if (listaEspacios.isEmpty()) {
            Span vacio = new Span("Aún no hay espacios configurados por el administrador.");
            vacio.getStyle().set("color", "#64748B");
            galeriaTarjetas.add(vacio);
        } else {
            for (Espacio esp : listaEspacios) {
                TipoEspacio tipo = esp.getTipoEspacioId() != null
                        ? crudService.buscarPorId(TipoEspacio.class, esp.getTipoEspacioId()) : null;
                String nombreCategoria = tipo != null ? tipo.getNombre() : "Espacio Coworking";
                String descripcionCategoria = (tipo != null && tipo.getDescripcion() != null && !tipo.getDescripcion().isEmpty())
                        ? tipo.getDescripcion()
                        : "Espacio equipado con internet de alta velocidad, climatización y acceso a zonas comunes.";

                VerticalLayout cardEspacio = new VerticalLayout();
                cardEspacio.setPadding(false);
                cardEspacio.setSpacing(false);
                cardEspacio.getStyle().set("flex", "1 1 280px");
                cardEspacio.getStyle().set("max-width", "360px");
                cardEspacio.getStyle().set("background-color", "#FFFFFF");
                cardEspacio.getStyle().set("border", "1px solid #E2E8F0");
                cardEspacio.getStyle().set("border-radius", "14px");
                cardEspacio.getStyle().set("overflow", "hidden");
                cardEspacio.getStyle().set("box-shadow", "0 4px 12px rgba(0, 0, 0, 0.05)");

                Image foto = new Image(obtenerImagenEspacio(nombreCategoria + " " + esp.getNombre()), esp.getNombre());
                foto.setWidthFull();
                foto.setHeight("165px");
                foto.getStyle().set("object-fit", "cover");

                VerticalLayout cuerpoCard = new VerticalLayout();
                cuerpoCard.setPadding(true);
                cuerpoCard.setSpacing(true);

                Span badgeCat = crearBadge(nombreCategoria, "#1E40AF", "#DBEAFE");

                H4 tituloEspacio = new H4(esp.getNombre());
                tituloEspacio.getStyle().set("margin", "4px 0 0 0");
                tituloEspacio.getStyle().set("color", "#0F172A");

                Span descSpan = new Span(descripcionCategoria);
                descSpan.getStyle().set("font-size", "0.84rem");
                descSpan.getStyle().set("color", "#64748B");
                descSpan.getStyle().set("line-height", "1.4");

                HorizontalLayout filaDatos = new HorizontalLayout();
                filaDatos.setWidthFull();
                filaDatos.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
                filaDatos.setAlignItems(FlexComponent.Alignment.CENTER);
                filaDatos.getStyle().set("border-top", "1px solid #F1F5F9");
                filaDatos.getStyle().set("padding-top", "10px");

                Span capSpan = new Span("Capacidad: " + esp.getCapacidad() + " pers.");
                capSpan.getStyle().set("font-size", "0.82rem");
                capSpan.getStyle().set("font-weight", "600");
                capSpan.getStyle().set("color", "#475569");

                Span precioSpan = crearBadge(String.format("$ %,.0f /h", esp.getPrecioHora()), "#065F46", "#D1FAE5");

                filaDatos.add(capSpan, precioSpan);

                Button btnElegir = new Button("Seleccionar para Reservar", VaadinIcon.CALENDAR.create());
                btnElegir.setWidthFull();
                btnElegir.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                btnElegir.addClickListener(ev -> {
                    cmbEspacioReserva.setValue(esp);
                    mostrarNotificacion("Espacio '" + esp.getNombre() + "' seleccionado. Completa las fechas abajo.", NotificationVariant.LUMO_CONTRAST);
                });

                cuerpoCard.add(badgeCat, tituloEspacio, descSpan, filaDatos, btnElegir);
                cardEspacio.add(foto, cuerpoCard);
                galeriaTarjetas.add(cardEspacio);
            }
        }

        // --- FORMULARIO DE RESERVA DEL CLIENTE (TARIFA BLOQUEADA / CALCULADA) ---
        cmbEspacioReserva.setItems(listaEspacios);

        DateTimePicker pickerInicio = new DateTimePicker("Fecha y Hora de Inicio");
        pickerInicio.setWidthFull();

        DateTimePicker pickerFin = new DateTimePicker("Fecha y Hora de Fin");
        pickerFin.setWidthFull();

        NumberField txtTotalCliente = new NumberField("Total Calculado ($)");
        txtTotalCliente.setWidthFull();
        txtTotalCliente.setPrefixComponent(VaadinIcon.CASH.create());
        txtTotalCliente.setReadOnly(true); // El cliente no puede modificar la tarifa oficial

        // Cálculo automático del precio según horas y tarifa del espacio
        Runnable recalcularPrecio = () -> {
            if (cmbEspacioReserva.getValue() != null && pickerInicio.getValue() != null && pickerFin.getValue() != null) {
                long minutos = Duration.between(pickerInicio.getValue(), pickerFin.getValue()).toMinutes();
                if (minutos > 0) {
                    double horas = Math.ceil(minutos / 60.0);
                    double tarifa = cmbEspacioReserva.getValue().getPrecioHora() != null ? cmbEspacioReserva.getValue().getPrecioHora() : 0.0;
                    txtTotalCliente.setValue(horas * tarifa);
                } else {
                    txtTotalCliente.clear();
                }
            }
        };
        cmbEspacioReserva.addValueChangeListener(e -> recalcularPrecio.run());
        pickerInicio.addValueChangeListener(e -> recalcularPrecio.run());
        pickerFin.addValueChangeListener(e -> recalcularPrecio.run());

        // Tabla de "Mis Reservas" (filtrada exclusivamente para el usuario logueado)
        Grid<Reserva> gridMisReservas = new Grid<>(Reserva.class, false);
        gridMisReservas.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);
        gridMisReservas.addColumn(r -> r.getId()).setHeader("Reserva #").setWidth("100px").setFlexGrow(0);
        gridMisReservas.addColumn(r -> {
            Espacio esp = r.getEspacioId() != null ? crudService.buscarPorId(Espacio.class, r.getEspacioId()) : null;
            return esp != null ? esp.getNombre() : "Espacio #" + r.getEspacioId();
        }).setHeader("Espacio Reservado").setAutoWidth(true).setFlexGrow(1);
        gridMisReservas.addColumn(r -> r.getFechaInicio() != null ? r.getFechaInicio().format(formatoFecha) : "")
                .setHeader("Inicio").setAutoWidth(true);
        gridMisReservas.addColumn(r -> r.getFechaFin() != null ? r.getFechaFin().format(formatoFecha) : "")
                .setHeader("Fin").setAutoWidth(true);
        gridMisReservas.addComponentColumn(r -> crearBadge(
                String.format("$ %,.0f", r.getTotalPagar() != null ? r.getTotalPagar() : 0.0),
                "#065F46", "#D1FAE5"
        )).setHeader("Total Pagado").setAutoWidth(true);
        gridMisReservas.addComponentColumn(r -> {
            Button btnCancelar = new Button("Cancelar", VaadinIcon.CLOSE_SMALL.create());
            btnCancelar.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            btnCancelar.addClickListener(ev -> {
                crudService.eliminar(Reserva.class, r.getId());
                mostrarNotificacion("Reserva #" + r.getId() + " cancelada", NotificationVariant.LUMO_CONTRAST);
                mostrarVistaCliente(cliente);
            });
            return btnCancelar;
        }).setHeader("Acción").setAutoWidth(true);

        // Filtramos solo las reservas del cliente actual
        List<Reserva> reservasDelCliente = new ArrayList<>();
        for (Reserva r : crudService.listarTodos(Reserva.class)) {
            if (r.getUsuarioId() != null && r.getUsuarioId().equals(cliente.getId())) {
                reservasDelCliente.add(r);
            }
        }
        gridMisReservas.setItems(reservasDelCliente);
        gridMisReservas.setAllRowsVisible(true);

        Button btnConfirmarReserva = new Button("Confirmar Reserva Ahora", VaadinIcon.CHECK_CIRCLE.create());
        btnConfirmarReserva.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_LARGE);

        btnConfirmarReserva.addClickListener(e -> {
            if (cmbEspacioReserva.getValue() == null || pickerInicio.getValue() == null || pickerFin.getValue() == null) {
                mostrarNotificacion("Selecciona el espacio y las fechas de inicio y fin", NotificationVariant.LUMO_ERROR);
                return;
            }
            if (!pickerFin.getValue().isAfter(pickerInicio.getValue())) {
                mostrarNotificacion("La fecha de fin debe ser posterior a la fecha de inicio", NotificationVariant.LUMO_ERROR);
                return;
            }
            Reserva nuevaReserva = new Reserva(
                    null,
                    cliente.getId(),
                    cmbEspacioReserva.getValue().getId(),
                    pickerInicio.getValue(),
                    pickerFin.getValue(),
                    txtTotalCliente.getValue()
            );
            crudService.guardar(nuevaReserva);
            mostrarNotificacion("¡Reserva confirmada con éxito!", NotificationVariant.LUMO_SUCCESS);
            mostrarVistaCliente(cliente);
        });

        FormLayout formReservaCliente = new FormLayout(cmbEspacioReserva, txtTotalCliente, pickerInicio, pickerFin);
        VerticalLayout bloqueFormulario = new VerticalLayout(formReservaCliente, btnConfirmarReserva);
        bloqueFormulario.setPadding(false);

        VerticalLayout cardAgendar = crearTarjeta("Agendar Nueva Reserva", VaadinIcon.CALENDAR, bloqueFormulario);
        cardAgendar.setHeight(null);

        VerticalLayout cardMisReservas = crearTarjeta("Mis Reservas Activas (" + reservasDelCliente.size() + ")", VaadinIcon.LIST, gridMisReservas);
        cardMisReservas.setHeight(null);

        vistaExplorar.add(galeriaTarjetas, cardAgendar);

        VerticalLayout vistaHistorialCliente = new VerticalLayout(cardMisReservas);
        vistaHistorialCliente.setWidthFull();
        vistaHistorialCliente.setPadding(true);

        tabsCliente.add("Explorar y Reservar Espacios", vistaExplorar);
        tabsCliente.add("Mis Reservas (" + reservasDelCliente.size() + ")", vistaHistorialCliente);

        add(headerCliente, tabsCliente);
    }

    // =========================================================================
    // ROL 2: PANEL DE ADMINISTRACIÓN / BACKOFFICE (GESTIÓN TOTAL DEL NEGOCIO)
    // =========================================================================
    private void mostrarVistaAdmin() {
        removeAll();
        setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        setAlignItems(FlexComponent.Alignment.STRETCH);
        getStyle().remove("background-image");
        getStyle().set("background-color", "#F1F5F9");

        // --- 1. ENCABEZADO PRINCIPAL CON FOTOGRAFÍA (HERO BANNER) ---
        HorizontalLayout header = crearEncabezado(
                "MODO ADMINISTRADOR • BACKOFFICE",
                "Sistema de Gestión de Coworking",
                "Configuración de categorías, espacios físicos, tarifas, clientes y reportes globales",
                VaadinIcon.BUILDING,
                true
        );

        // --- 2. PANEL DE ESTADÍSTICAS RÁPIDAS (TARJETAS KPI) ---
        HorizontalLayout panelKpis = crearPanelKpis();
        actualizarKpis();

        // --- 3. CONTENEDOR DE PESTAÑAS (TABS) ---
        TabSheet tabSheet = new TabSheet();
        tabSheet.setWidthFull();
        tabSheet.addThemeVariants(TabSheetVariant.LUMO_BORDERED);
        tabSheet.getStyle().set("background-color", "#FFFFFF");
        tabSheet.getStyle().set("border-radius", "14px");
        tabSheet.getStyle().set("box-shadow", "0 4px 15px rgba(0, 0, 0, 0.05)");

        // Agregamos cada módulo administrativo como una pestaña independiente
        tabSheet.add("Reservas Globales", crearSeccionReservas());
        tabSheet.add("Espacios Físicos", crearSeccionEspacios());
        tabSheet.add("Categorías (Tipos)", crearSeccionTipoEspacio());
        tabSheet.add("Clientes", crearSeccionUsuarios());

        // Refresca las estadísticas cada vez que el administrador cambia de pestaña
        tabSheet.addSelectedChangeListener(e -> actualizarKpis());

        add(header, panelKpis, tabSheet);
    }

    // =========================================================================
    // MÉTODOS AUXILIARES DE DISEÑO (BANNER, KPIs, TARJETAS, BADGES E IMÁGENES)
    // =========================================================================

    // Crea la barra superior con imagen real de coworking, filtro oscuro y botón de Salir
    private HorizontalLayout crearEncabezado(String etiquetaSuperior, String tituloPrincipal,
                                             String subtituloTexto, VaadinIcon icono, boolean mostrarSalir) {
        Icon iconoLogo = icono.create();
        iconoLogo.getStyle().set("width", "32px");
        iconoLogo.getStyle().set("height", "32px");
        iconoLogo.getStyle().set("color", "#38BDF8");
        iconoLogo.getStyle().set("background", "rgba(56, 189, 248, 0.15)");
        iconoLogo.getStyle().set("padding", "10px");
        iconoLogo.getStyle().set("border-radius", "12px");

        Span badgeSistema = new Span(etiquetaSuperior);
        badgeSistema.getStyle().set("background-color", "rgba(56, 189, 248, 0.2)");
        badgeSistema.getStyle().set("color", "#7DD3FC");
        badgeSistema.getStyle().set("font-size", "0.72rem");
        badgeSistema.getStyle().set("font-weight", "700");
        badgeSistema.getStyle().set("letter-spacing", "1px");
        badgeSistema.getStyle().set("padding", "3px 10px");
        badgeSistema.getStyle().set("border-radius", "20px");
        badgeSistema.getStyle().set("width", "fit-content");

        H2 titulo = new H2(tituloPrincipal);
        titulo.getStyle().set("margin", "4px 0 2px 0");
        titulo.getStyle().set("color", "#FFFFFF");
        titulo.getStyle().set("font-size", "1.6rem");

        Span subtitulo = new Span(subtituloTexto);
        subtitulo.getStyle().set("color", "#E2E8F0");
        subtitulo.getStyle().set("font-size", "0.9rem");

        VerticalLayout textos = new VerticalLayout(badgeSistema, titulo, subtitulo);
        textos.setPadding(false);
        textos.setSpacing(false);

        HorizontalLayout izquierda = new HorizontalLayout(iconoLogo, textos);
        izquierda.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout header = new HorizontalLayout(izquierda);
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle().set("padding", "20px 26px");
        header.getStyle().set("background-image",
                "linear-gradient(135deg, rgba(15, 23, 42, 0.92) 0%, rgba(30, 41, 59, 0.85) 100%), " +
                "url('https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=1400&q=80')");
        header.getStyle().set("background-size", "cover");
        header.getStyle().set("background-position", "center");
        header.getStyle().set("border-radius", "14px");
        header.getStyle().set("box-shadow", "0 6px 18px rgba(15, 23, 42, 0.18)");

        if (mostrarSalir) {
            Button btnCerrarSesion = new Button("Cerrar Sesión", VaadinIcon.SIGN_OUT.create());
            btnCerrarSesion.getStyle().set("background-color", "rgba(255, 255, 255, 0.14)");
            btnCerrarSesion.getStyle().set("color", "#FFFFFF");
            btnCerrarSesion.getStyle().set("border", "1px solid rgba(255, 255, 255, 0.25)");
            btnCerrarSesion.getStyle().set("cursor", "pointer");
            btnCerrarSesion.addClickListener(e -> mostrarVistaAcceso());
            header.add(btnCerrarSesion);
        }

        return header;
    }

    // Construye las 4 tarjetas superiores de métricas (KPIs) con iconos grandes
    private HorizontalLayout crearPanelKpis() {
        HorizontalLayout filaKpis = new HorizontalLayout();
        filaKpis.setWidthFull();
        filaKpis.setSpacing(true);
        filaKpis.getStyle().set("flex-wrap", "wrap");

        filaKpis.add(
            crearTarjetaKpi("Clientes Registrados", lblTotalUsuarios, VaadinIcon.USERS, "#2563EB", "#EFF6FF"),
            crearTarjetaKpi("Espacios Activos", lblTotalEspacios, VaadinIcon.OFFICE, "#7C3AED", "#F5F3FF"),
            crearTarjetaKpi("Reservas Agendadas", lblTotalReservas, VaadinIcon.CALENDAR, "#D97706", "#FFFBEB"),
            crearTarjetaKpi("Ingresos Totales", lblIngresosTotales, VaadinIcon.TRENDING_UP, "#059669", "#ECFDF5")
        );

        return filaKpis;
    }

    // Diseña cada tarjeta individual del panel de estadísticas
    private HorizontalLayout crearTarjetaKpi(String titulo, Span valorSpan, VaadinIcon icono, String colorIcono, String colorFondoIcono) {
        Icon ic = icono.create();
        ic.getStyle().set("width", "26px");
        ic.getStyle().set("height", "26px");
        ic.getStyle().set("color", colorIcono);
        ic.getStyle().set("background-color", colorFondoIcono);
        ic.getStyle().set("padding", "12px");
        ic.getStyle().set("border-radius", "12px");

        Span lblTitulo = new Span(titulo);
        lblTitulo.getStyle().set("font-size", "0.82rem");
        lblTitulo.getStyle().set("color", "#64748B");
        lblTitulo.getStyle().set("font-weight", "600");

        valorSpan.getStyle().set("font-size", "1.45rem");
        valorSpan.getStyle().set("font-weight", "800");
        valorSpan.getStyle().set("color", "#0F172A");

        VerticalLayout info = new VerticalLayout(lblTitulo, valorSpan);
        info.setPadding(false);
        info.setSpacing(false);

        HorizontalLayout card = new HorizontalLayout(ic, info);
        card.setAlignItems(FlexComponent.Alignment.CENTER);
        card.setPadding(true);
        card.getStyle().set("background-color", "#FFFFFF");
        card.getStyle().set("border", "1px solid #E2E8F0");
        card.getStyle().set("border-radius", "12px");
        card.getStyle().set("box-shadow", "0 2px 6px rgba(0, 0, 0, 0.03)");
        card.getStyle().set("flex", "1 1 220px");

        return card;
    }

    // Calcula y actualiza los valores de las 4 tarjetas KPI usando los servicios CRUD
    private void actualizarKpis() {
        List<Usuario> usuarios = crudService.listarTodos(Usuario.class);
        List<Espacio> espacios = crudService.listarTodos(Espacio.class);
        List<Reserva> reservas = crudService.listarTodos(Reserva.class);

        double ingresos = 0.0;
        for (Reserva r : reservas) {
            if (r.getTotalPagar() != null) {
                ingresos += r.getTotalPagar();
            }
        }

        lblTotalUsuarios.setText(String.valueOf(usuarios.size()));
        lblTotalEspacios.setText(String.valueOf(espacios.size()));
        lblTotalReservas.setText(String.valueOf(reservas.size()));
        lblIngresosTotales.setText(String.format("$ %,.0f", ingresos));
    }

    // Envuelve cualquier tabla o formulario dentro de una tarjeta blanca con sombra
    private VerticalLayout crearTarjeta(String tituloSeccion, VaadinIcon icono, Component contenido) {
        Icon ic = icono.create();
        ic.getStyle().set("width", "18px");
        ic.getStyle().set("height", "18px");
        ic.getStyle().set("color", "#2563EB");

        H3 titulo = new H3(tituloSeccion);
        titulo.getStyle().set("margin", "0");
        titulo.getStyle().set("font-size", "1.08rem");
        titulo.getStyle().set("color", "#1E293B");

        HorizontalLayout cabeceraTarjeta = new HorizontalLayout(ic, titulo);
        cabeceraTarjeta.setAlignItems(FlexComponent.Alignment.CENTER);
        cabeceraTarjeta.setWidthFull();
        cabeceraTarjeta.getStyle().set("border-bottom", "1px solid #E2E8F0");
        cabeceraTarjeta.getStyle().set("padding-bottom", "10px");

        VerticalLayout card = new VerticalLayout(cabeceraTarjeta, contenido);
        card.setWidthFull();
        card.setPadding(true);
        card.setSpacing(true);
        card.getStyle().set("background-color", "#FFFFFF");
        card.getStyle().set("border", "1px solid #E2E8F0");
        card.getStyle().set("border-radius", "12px");
        card.getStyle().set("box-shadow", "0 2px 8px rgba(0, 0, 0, 0.03)");

        return card;
    }

    // Crea una etiqueta tipo píldora (Badge) para resaltar datos dentro de las tablas
    private Span crearBadge(String texto, String colorTexto, String colorFondo) {
        Span badge = new Span(texto);
        badge.getStyle().set("color", colorTexto);
        badge.getStyle().set("background-color", colorFondo);
        badge.getStyle().set("padding", "4px 10px");
        badge.getStyle().set("border-radius", "999px");
        badge.getStyle().set("font-size", "0.78rem");
        badge.getStyle().set("font-weight", "600");
        return badge;
    }

    // Devuelve una URL de imagen acorde al nombre o categoría del espacio (sin modificar la BD)
    private String obtenerImagenEspacio(String textoReferencia) {
        if (textoReferencia == null) {
            return "https://images.unsplash.com/photo-1497366811353-6870744d04b2?auto=format&fit=crop&w=800&q=80";
        }
        String texto = textoReferencia.toLowerCase();
        if (texto.contains("junta") || texto.contains("reunion") || texto.contains("reunión")) {
            return "https://images.unsplash.com/photo-1517502884422-41eaead166d4?auto=format&fit=crop&w=800&q=80";
        } else if (texto.contains("auditorio") || texto.contains("evento") || texto.contains("conferencia")) {
            return "https://images.unsplash.com/photo-1505373877841-8d25f7d46678?auto=format&fit=crop&w=800&q=80";
        } else if (texto.contains("oficina") || texto.contains("privada")) {
            return "https://images.unsplash.com/photo-1524758631624-e2822e304c36?auto=format&fit=crop&w=800&q=80";
        } else if (texto.contains("computo") || texto.contains("lab") || texto.contains("estudio")) {
            return "https://images.unsplash.com/photo-1519389950473-47ba0277781c?auto=format&fit=crop&w=800&q=80";
        }
        return "https://images.unsplash.com/photo-1497366811353-6870744d04b2?auto=format&fit=crop&w=800&q=80";
    }

    // Muestra notificaciones modernas con color según el tipo de mensaje
    private void mostrarNotificacion(String mensaje, NotificationVariant variante) {
        Notification notification = Notification.show(mensaje, 3000, Notification.Position.BOTTOM_END);
        notification.addThemeVariants(variante);
    }

    // =========================================================================
    // SECCIÓN ADMIN 1: GESTIÓN DE CLIENTES / USUARIOS
    // =========================================================================
    private Component crearSeccionUsuarios() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setPadding(true);

        // Configuración de la tabla (Grid) de Usuarios con avatar de iniciales
        Grid<Usuario> grid = new Grid<>(Usuario.class, false);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);
        grid.addColumn(u -> u.getId()).setHeader("ID").setWidth("65px").setFlexGrow(0);
        grid.addComponentColumn(u -> {
            String nombre = u.getNombre() != null && !u.getNombre().isEmpty() ? u.getNombre() : "U";
            Span avatar = new Span(nombre.substring(0, 1).toUpperCase());
            avatar.getStyle().set("display", "inline-flex");
            avatar.getStyle().set("align-items", "center");
            avatar.getStyle().set("justify-content", "center");
            avatar.getStyle().set("width", "28px");
            avatar.getStyle().set("height", "28px");
            avatar.getStyle().set("border-radius", "50%");
            avatar.getStyle().set("background-color", "#DBEAFE");
            avatar.getStyle().set("color", "#1D4ED8");
            avatar.getStyle().set("font-weight", "700");
            avatar.getStyle().set("font-size", "0.8rem");

            HorizontalLayout celda = new HorizontalLayout(avatar, new Span(nombre));
            celda.setAlignItems(FlexComponent.Alignment.CENTER);
            return celda;
        }).setHeader("Nombre").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(u -> u.getEmail()).setHeader("Email").setAutoWidth(true).setFlexGrow(1);
        grid.addComponentColumn(u -> crearBadge(
                u.getTelefono() != null && !u.getTelefono().isEmpty() ? u.getTelefono() : "Sin teléfono",
                "#1E40AF", "#DBEAFE"
        )).setHeader("Teléfono").setAutoWidth(true);

        // Función lambda para recargar los datos desde la base de datos/servicio
        Runnable refrescar = () -> {
            grid.setItems(crudService.listarTodos(Usuario.class));
            actualizarKpis();
        };
        refrescar.run();
        grid.setAllRowsVisible(true);

        // Campos del formulario de captura
        TextField txtNombre = new TextField("Nombre Completo");
        txtNombre.setPrefixComponent(VaadinIcon.USER.create());
        txtNombre.setPlaceholder("Ej: Thomas Restrepo");

        EmailField txtEmail = new EmailField("Correo Electrónico");
        txtEmail.setPrefixComponent(VaadinIcon.ENVELOPE.create());
        txtEmail.setPlaceholder("correo@ejemplo.com");
        txtEmail.setErrorMessage("Ingrese un correo válido (ejemplo@dominio.com)");

        TextField txtTelefono = new TextField("Teléfono");
        txtTelefono.setPrefixComponent(VaadinIcon.PHONE.create());
        txtTelefono.setPlaceholder("Ej: 3001234567");

        txtNombre.setRequired(true);

        // Botones de acción con iconos y estilos Lumo
        Button btnGuardar = new Button("Guardar", VaadinIcon.CHECK.create());
        Button btnActualizar = new Button("Actualizar", VaadinIcon.REFRESH.create());
        Button btnEliminar = new Button("Eliminar", VaadinIcon.TRASH.create());
        Button btnLimpiar = new Button("Limpiar", VaadinIcon.ERASER.create());

        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnActualizar.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);
        btnLimpiar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        // Evento al seleccionar una fila de la tabla: carga los datos en el formulario
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                Usuario u = crudService.buscarPorId(Usuario.class, event.getValue().getId());
                if (u != null) {
                    txtNombre.setValue(u.getNombre() != null ? u.getNombre() : "");
                    txtEmail.setValue(u.getEmail() != null ? u.getEmail() : "");
                    txtTelefono.setValue(u.getTelefono() != null ? u.getTelefono() : "");
                }
            }
        });

        // Evento Guardar nuevo usuario
        btnGuardar.addClickListener(e -> {
            if (txtNombre.isEmpty() || txtEmail.isEmpty()) {
                mostrarNotificacion("Por favor complete los campos requeridos", NotificationVariant.LUMO_ERROR);
                return;
            }
            Usuario u = new Usuario(null, txtNombre.getValue(), txtEmail.getValue(), txtTelefono.getValue());
            crudService.guardar(u);
            refrescar.run();
            grid.asSingleSelect().clear();
            txtNombre.clear();
            txtEmail.clear();
            txtTelefono.clear();
            mostrarNotificacion("Usuario guardado con éxito", NotificationVariant.LUMO_SUCCESS);
        });

        // Evento Actualizar usuario seleccionado
        btnActualizar.addClickListener(e -> {
            Usuario seleccionado = grid.asSingleSelect().getValue();
            if (seleccionado == null) {
                mostrarNotificacion("Seleccione un usuario de la tabla para actualizar", NotificationVariant.LUMO_CONTRAST);
                return;
            }
            if (txtNombre.isEmpty() || txtEmail.isEmpty()) {
                mostrarNotificacion("Por favor complete los campos requeridos", NotificationVariant.LUMO_ERROR);
                return;
            }
            seleccionado.setNombre(txtNombre.getValue());
            seleccionado.setEmail(txtEmail.getValue());
            seleccionado.setTelefono(txtTelefono.getValue());
            crudService.actualizar(seleccionado);
            refrescar.run();
            grid.asSingleSelect().clear();
            txtNombre.clear();
            txtEmail.clear();
            txtTelefono.clear();
            mostrarNotificacion("Usuario actualizado con éxito", NotificationVariant.LUMO_SUCCESS);
        });

        // Evento Eliminar usuario seleccionado
        btnEliminar.addClickListener(e -> {
            Usuario seleccionado = grid.asSingleSelect().getValue();
            if (seleccionado != null) {
                crudService.eliminar(Usuario.class, seleccionado.getId());
                refrescar.run();
                grid.asSingleSelect().clear();
                txtNombre.clear();
                txtEmail.clear();
                txtTelefono.clear();
                mostrarNotificacion("Usuario eliminado con éxito", NotificationVariant.LUMO_SUCCESS);
            } else {
                mostrarNotificacion("Seleccione un usuario de la tabla para eliminar", NotificationVariant.LUMO_CONTRAST);
            }
        });

        // Evento Limpiar formulario
        btnLimpiar.addClickListener(e -> {
            grid.asSingleSelect().clear();
            txtNombre.clear();
            txtEmail.clear();
            txtTelefono.clear();
        });

        // Agrupación de botones con ajuste automático de línea
        HorizontalLayout acciones = new HorizontalLayout(btnGuardar, btnActualizar, btnEliminar, btnLimpiar);
        acciones.getStyle().set("flex-wrap", "wrap");
        acciones.getStyle().set("margin-top", "12px");

        FormLayout formulario = new FormLayout();
        formulario.add(txtNombre, txtEmail, txtTelefono);
        formulario.setColspan(txtNombre, 2);
        formulario.setColspan(txtEmail, 2);
        formulario.setColspan(txtTelefono, 2);

        VerticalLayout contenidoForm = new VerticalLayout(formulario, acciones);
        contenidoForm.setPadding(false);
        contenidoForm.setSpacing(true);

        // Envolvemos tabla y formulario en sus tarjetas visuales
        VerticalLayout cardTabla = crearTarjeta("Listado de Clientes Registrados", VaadinIcon.USERS, grid);
        VerticalLayout cardForm = crearTarjeta("Datos del Cliente", VaadinIcon.USER_CARD, contenidoForm);

        HorizontalLayout mainContent = new HorizontalLayout(cardTabla, cardForm);
        mainContent.setWidthFull();
        mainContent.setFlexGrow(2, cardTabla);
        mainContent.setFlexGrow(1, cardForm);

        layout.add(mainContent);
        return layout;
    }

    // =========================================================================
    // SECCIÓN ADMIN 2: GESTIÓN DE CATEGORÍAS (TIPOS DE ESPACIO)
    // =========================================================================
    private Component crearSeccionTipoEspacio() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setPadding(true);

        // Tabla de Tipos de Espacio (las categorías generales del coworking)
        Grid<TipoEspacio> grid = new Grid<>(TipoEspacio.class, false);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);
        grid.addColumn(t -> t.getId()).setHeader("ID").setWidth("65px").setFlexGrow(0);
        grid.addComponentColumn(t -> crearBadge(
                t.getNombre() != null ? t.getNombre() : "Sin nombre",
                "#5B21B6", "#EDE9FE"
        )).setHeader("Categoría").setAutoWidth(true);
        grid.addColumn(t -> t.getDescripcion()).setHeader("Equipamiento / Detalles").setFlexGrow(1);

        Runnable refrescar = () -> grid.setItems(crudService.listarTodos(TipoEspacio.class));
        refrescar.run();
        grid.setAllRowsVisible(true);

        // Selector de plantillas sugeridas para agilizar la creación de categorías estándar
        ComboBox<String> cmbPlantilla = new ComboBox<>("Plantilla Rápida (Opcional)");
        cmbPlantilla.setItems("Sala de Juntas", "Oficina Privada", "Escritorio Compartido", "Auditorio / Eventos");
        cmbPlantilla.setPlaceholder("Elige una categoría sugerida...");

        TextField txtNombre = new TextField("Nombre de la Categoría");
        txtNombre.setPrefixComponent(VaadinIcon.TAG.create());
        txtNombre.setPlaceholder("Ej: Sala de Juntas");

        TextArea txtDescripcion = new TextArea("Descripción / Equipamiento Incluido");
        txtDescripcion.setPlaceholder("Ej: Incluye Smart TV 65'', tablero acrílico, Wi-Fi dedicado y estación de café.");
        txtDescripcion.setMinHeight("100px");

        // Autocompleta nombre y descripción profesional al elegir una plantilla
        cmbPlantilla.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                txtNombre.setValue(e.getValue());
                if ("Sala de Juntas".equals(e.getValue())) {
                    txtDescripcion.setValue("Sala cerrada para reuniones ejecutivas. Incluye pantalla 4K HDMI, pizarra acrílica y café ilimitado.");
                } else if ("Oficina Privada".equals(e.getValue())) {
                    txtDescripcion.setValue("Espacio independiente y silencioso con cerradura privada, escritorios ergonómicos y alta velocidad.");
                } else if ("Escritorio Compartido".equals(e.getValue())) {
                    txtDescripcion.setValue("Puesto de trabajo flexible en zona abierta colaborativa con acceso a áreas comunes y networking.");
                } else if ("Auditorio / Eventos".equals(e.getValue())) {
                    txtDescripcion.setValue("Salón amplio equipado con sistema de sonido, micrófonos inalámbricos y proyector para charlas.");
                }
            }
        });

        txtNombre.setRequired(true);

        Button btnGuardar = new Button("Guardar", VaadinIcon.CHECK.create());
        Button btnActualizar = new Button("Actualizar", VaadinIcon.REFRESH.create());
        Button btnEliminar = new Button("Eliminar", VaadinIcon.TRASH.create());
        Button btnLimpiar = new Button("Limpiar", VaadinIcon.ERASER.create());

        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnActualizar.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);
        btnLimpiar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        // Cargar fila seleccionada al formulario
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                TipoEspacio t = crudService.buscarPorId(TipoEspacio.class, event.getValue().getId());
                if (t != null) {
                    txtNombre.setValue(t.getNombre() != null ? t.getNombre() : "");
                    txtDescripcion.setValue(t.getDescripcion() != null ? t.getDescripcion() : "");
                }
            }
        });

        btnGuardar.addClickListener(e -> {
            if (txtNombre.isEmpty()) {
                mostrarNotificacion("Ingrese el nombre de la categoría", NotificationVariant.LUMO_ERROR);
                return;
            }
            TipoEspacio t = new TipoEspacio(null, txtNombre.getValue(), txtDescripcion.getValue());
            crudService.guardar(t);
            refrescar.run();
            grid.asSingleSelect().clear();
            cmbPlantilla.clear();
            txtNombre.clear();
            txtDescripcion.clear();
            mostrarNotificacion("Categoría guardada con éxito", NotificationVariant.LUMO_SUCCESS);
        });

        btnActualizar.addClickListener(e -> {
            TipoEspacio seleccionado = grid.asSingleSelect().getValue();
            if (seleccionado == null) {
                mostrarNotificacion("Seleccione una categoría de la tabla para actualizar", NotificationVariant.LUMO_CONTRAST);
                return;
            }
            if (txtNombre.isEmpty()) {
                mostrarNotificacion("Ingrese el nombre de la categoría", NotificationVariant.LUMO_ERROR);
                return;
            }
            seleccionado.setNombre(txtNombre.getValue());
            seleccionado.setDescripcion(txtDescripcion.getValue());
            crudService.actualizar(seleccionado);
            refrescar.run();
            grid.asSingleSelect().clear();
            cmbPlantilla.clear();
            txtNombre.clear();
            txtDescripcion.clear();
            mostrarNotificacion("Categoría actualizada con éxito", NotificationVariant.LUMO_SUCCESS);
        });

        btnEliminar.addClickListener(e -> {
            TipoEspacio seleccionado = grid.asSingleSelect().getValue();
            if (seleccionado != null) {
                crudService.eliminar(TipoEspacio.class, seleccionado.getId());
                refrescar.run();
                grid.asSingleSelect().clear();
                cmbPlantilla.clear();
                txtNombre.clear();
                txtDescripcion.clear();
                mostrarNotificacion("Categoría eliminada con éxito", NotificationVariant.LUMO_SUCCESS);
            } else {
                mostrarNotificacion("Seleccione un elemento de la tabla para eliminar", NotificationVariant.LUMO_CONTRAST);
            }
        });

        btnLimpiar.addClickListener(e -> {
            grid.asSingleSelect().clear();
            cmbPlantilla.clear();
            txtNombre.clear();
            txtDescripcion.clear();
        });

        HorizontalLayout acciones = new HorizontalLayout(btnGuardar, btnActualizar, btnEliminar, btnLimpiar);
        acciones.getStyle().set("flex-wrap", "wrap");
        acciones.getStyle().set("margin-top", "12px");

        FormLayout formulario = new FormLayout();
        formulario.add(cmbPlantilla, txtNombre, txtDescripcion);
        formulario.setColspan(cmbPlantilla, 2);
        formulario.setColspan(txtNombre, 2);
        formulario.setColspan(txtDescripcion, 2);

        VerticalLayout contenidoForm = new VerticalLayout(formulario, acciones);
        contenidoForm.setPadding(false);
        contenidoForm.setSpacing(true);

        VerticalLayout cardTabla = crearTarjeta("Categorías de Espacios Configuradas", VaadinIcon.LIST_UL, grid);
        VerticalLayout cardForm = crearTarjeta("Configurar Categoría", VaadinIcon.EDIT, contenidoForm);

        HorizontalLayout mainContent = new HorizontalLayout(cardTabla, cardForm);
        mainContent.setWidthFull();
        mainContent.setFlexGrow(2, cardTabla);
        mainContent.setFlexGrow(1, cardForm);

        layout.add(mainContent);
        return layout;
    }

    // =========================================================================
    // SECCIÓN ADMIN 3: GESTIÓN DE ESPACIOS FÍSICOS (SALAS Y TARIFAS OFICIALES)
    // =========================================================================
    private Component crearSeccionEspacios() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setPadding(true);

        Grid<Espacio> grid = new Grid<>(Espacio.class, false);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);
        grid.addColumn(e -> e.getId()).setHeader("ID").setWidth("60px").setFlexGrow(0);
        // Miniatura fotográfica del espacio físico
        grid.addComponentColumn(e -> {
            TipoEspacio tipo = e.getTipoEspacioId() != null ? crudService.buscarPorId(TipoEspacio.class, e.getTipoEspacioId()) : null;
            String ref = (tipo != null ? tipo.getNombre() : "") + " " + e.getNombre();
            Image thumb = new Image(obtenerImagenEspacio(ref), e.getNombre());
            thumb.setWidth("54px");
            thumb.setHeight("36px");
            thumb.getStyle().set("object-fit", "cover");
            thumb.getStyle().set("border-radius", "6px");
            return thumb;
        }).setHeader("Foto").setWidth("80px").setFlexGrow(0);
        grid.addColumn(e -> e.getNombre()).setHeader("Sala / Espacio Físico").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(e -> e.getCapacidad() + " pers.").setHeader("Capacidad").setAutoWidth(true);
        grid.addComponentColumn(e -> crearBadge(
                String.format("$ %,.0f /h", e.getPrecioHora() != null ? e.getPrecioHora() : 0.0),
                "#065F46", "#D1FAE5"
        )).setHeader("Tarifa Oficial").setAutoWidth(true);
        grid.addComponentColumn(e -> {
            String nombreCat = "Sin tipo";
            if (e.getTipoEspacioId() != null) {
                TipoEspacio tipo = crudService.buscarPorId(TipoEspacio.class, e.getTipoEspacioId());
                nombreCat = tipo != null ? tipo.getNombre() : "ID: " + e.getTipoEspacioId();
            }
            return crearBadge(nombreCat, "#1E40AF", "#DBEAFE");
        }).setHeader("Categoría").setAutoWidth(true);

        // Vista previa visual con imagen dinámica del espacio seleccionado
        Image imgVistaPrevia = new Image(obtenerImagenEspacio(null), "Vista previa del espacio");
        imgVistaPrevia.setWidthFull();
        imgVistaPrevia.setHeight("145px");
        imgVistaPrevia.getStyle().set("object-fit", "cover");
        imgVistaPrevia.getStyle().set("border-radius", "10px");
        imgVistaPrevia.getStyle().set("border", "1px solid #E2E8F0");

        // ComboBox para elegir la Categoría (TipoEspacio)
        ComboBox<TipoEspacio> cmbTipoEspacio = new ComboBox<>("Categoría (Tipo de Espacio)");
        cmbTipoEspacio.setItemLabelGenerator(t -> t.getNombre() != null ? t.getNombre() : "Sin nombre");
        cmbTipoEspacio.setPlaceholder("Seleccione una categoría...");

        cmbTipoEspacio.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                imgVistaPrevia.setSrc(obtenerImagenEspacio(e.getValue().getNombre()));
            }
        });

        // Al refrescar, actualiza tanto la tabla como las opciones del ComboBox y los KPIs
        Runnable refrescar = () -> {
            grid.setItems(crudService.listarTodos(Espacio.class));
            cmbTipoEspacio.setItems(crudService.listarTodos(TipoEspacio.class));
            actualizarKpis();
        };
        refrescar.run();
        grid.setAllRowsVisible(true);

        TextField txtNombre = new TextField("Nombre Específico de la Sala");
        txtNombre.setPrefixComponent(VaadinIcon.OFFICE.create());
        txtNombre.setPlaceholder("Ej: Sala Ejecutiva Norte (Piso 2)");

        IntegerField txtCapacidad = new IntegerField("Capacidad Máxima (personas)");
        txtCapacidad.setPrefixComponent(VaadinIcon.GROUP.create());
        txtCapacidad.setMin(1);

        NumberField txtPrecio = new NumberField("Tarifa Oficial por Hora ($)");
        txtPrecio.setPrefixComponent(VaadinIcon.DOLLAR.create());
        txtPrecio.setMin(0);

        Button btnGuardar = new Button("Guardar", VaadinIcon.CHECK.create());
        Button btnActualizar = new Button("Actualizar", VaadinIcon.REFRESH.create());
        Button btnEliminar = new Button("Eliminar", VaadinIcon.TRASH.create());
        Button btnLimpiar = new Button("Limpiar", VaadinIcon.ERASER.create());

        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnActualizar.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);
        btnLimpiar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        // Evento al seleccionar un espacio en la tabla
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                Espacio esp = crudService.buscarPorId(Espacio.class, event.getValue().getId());
                if (esp != null) {
                    txtNombre.setValue(esp.getNombre() != null ? esp.getNombre() : "");
                    txtCapacidad.setValue(esp.getCapacidad());
                    txtPrecio.setValue(esp.getPrecioHora());
                    imgVistaPrevia.setSrc(obtenerImagenEspacio(esp.getNombre()));
                    if (esp.getTipoEspacioId() != null) {
                        TipoEspacio tipo = crudService.buscarPorId(TipoEspacio.class, esp.getTipoEspacioId());
                        cmbTipoEspacio.setValue(tipo);
                        if (tipo != null) {
                            imgVistaPrevia.setSrc(obtenerImagenEspacio(tipo.getNombre() + " " + esp.getNombre()));
                        }
                    } else {
                        cmbTipoEspacio.clear();
                    }
                }
            }
        });

        btnGuardar.addClickListener(e -> {
            if (txtNombre.isEmpty() || cmbTipoEspacio.getValue() == null) {
                mostrarNotificacion("Ingrese el nombre específico de la sala y su categoría", NotificationVariant.LUMO_ERROR);
                return;
            }
            Long tipoId = cmbTipoEspacio.getValue().getId();
            Espacio esp = new Espacio(null, txtNombre.getValue(), txtCapacidad.getValue(), txtPrecio.getValue(), tipoId);
            crudService.guardar(esp);
            refrescar.run();
            grid.asSingleSelect().clear();
            txtNombre.clear();
            txtCapacidad.clear();
            txtPrecio.clear();
            cmbTipoEspacio.clear();
            imgVistaPrevia.setSrc(obtenerImagenEspacio(null));
            mostrarNotificacion("Espacio guardado con éxito", NotificationVariant.LUMO_SUCCESS);
        });

        btnActualizar.addClickListener(e -> {
            Espacio seleccionado = grid.asSingleSelect().getValue();
            if (seleccionado == null) {
                mostrarNotificacion("Seleccione un espacio de la tabla para actualizar", NotificationVariant.LUMO_CONTRAST);
                return;
            }
            if (txtNombre.isEmpty() || cmbTipoEspacio.getValue() == null) {
                mostrarNotificacion("Ingrese el nombre específico de la sala y su categoría", NotificationVariant.LUMO_ERROR);
                return;
            }
            Long tipoId = cmbTipoEspacio.getValue().getId();
            seleccionado.setNombre(txtNombre.getValue());
            seleccionado.setCapacidad(txtCapacidad.getValue());
            seleccionado.setPrecioHora(txtPrecio.getValue());
            seleccionado.setTipoEspacioId(tipoId);
            crudService.actualizar(seleccionado);
            refrescar.run();
            grid.asSingleSelect().clear();
            txtNombre.clear();
            txtCapacidad.clear();
            txtPrecio.clear();
            cmbTipoEspacio.clear();
            imgVistaPrevia.setSrc(obtenerImagenEspacio(null));
            mostrarNotificacion("Espacio actualizado con éxito", NotificationVariant.LUMO_SUCCESS);
        });

        btnEliminar.addClickListener(e -> {
            Espacio seleccionado = grid.asSingleSelect().getValue();
            if (seleccionado != null) {
                crudService.eliminar(Espacio.class, seleccionado.getId());
                refrescar.run();
                grid.asSingleSelect().clear();
                txtNombre.clear();
                txtCapacidad.clear();
                txtPrecio.clear();
                cmbTipoEspacio.clear();
                imgVistaPrevia.setSrc(obtenerImagenEspacio(null));
                mostrarNotificacion("Espacio eliminado con éxito", NotificationVariant.LUMO_SUCCESS);
            } else {
                mostrarNotificacion("Seleccione un espacio de la tabla para eliminar", NotificationVariant.LUMO_CONTRAST);
            }
        });

        btnLimpiar.addClickListener(e -> {
            grid.asSingleSelect().clear();
            txtNombre.clear();
            txtCapacidad.clear();
            txtPrecio.clear();
            cmbTipoEspacio.clear();
            imgVistaPrevia.setSrc(obtenerImagenEspacio(null));
        });

        HorizontalLayout acciones = new HorizontalLayout(btnGuardar, btnActualizar, btnEliminar, btnLimpiar);
        acciones.getStyle().set("flex-wrap", "wrap");
        acciones.getStyle().set("margin-top", "10px");

        FormLayout formulario = new FormLayout();
        formulario.add(cmbTipoEspacio, txtNombre, txtCapacidad, txtPrecio);
        formulario.setColspan(cmbTipoEspacio, 2);
        formulario.setColspan(txtNombre, 2);

        VerticalLayout contenidoForm = new VerticalLayout(imgVistaPrevia, formulario, acciones);
        contenidoForm.setPadding(false);
        contenidoForm.setSpacing(true);

        VerticalLayout cardTabla = crearTarjeta("Inventario de Espacios Físicos", VaadinIcon.WORKPLACE, grid);
        VerticalLayout cardForm = crearTarjeta("Configurar Sala y Tarifa", VaadinIcon.SLIDERS, contenidoForm);

        HorizontalLayout mainContent = new HorizontalLayout(cardTabla, cardForm);
        mainContent.setWidthFull();
        mainContent.setFlexGrow(2, cardTabla);
        mainContent.setFlexGrow(1, cardForm);

        layout.add(mainContent);
        return layout;
    }

    // =========================================================================
    // SECCIÓN ADMIN 4: GESTIÓN DE RESERVAS GLOBALES Y REPORTES (EXCEL / PDF)
    // =========================================================================
    private Component crearSeccionReservas() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setPadding(true);

        // Tabla de Reservas mostrando nombres legibles, badges y fechas formateadas
        Grid<Reserva> grid = new Grid<>(Reserva.class, false);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);
        grid.addColumn(r -> r.getId()).setHeader("ID").setWidth("65px").setFlexGrow(0);
        grid.addColumn(r -> {
            Usuario u = r.getUsuarioId() != null ? crudService.buscarPorId(Usuario.class, r.getUsuarioId()) : null;
            return u != null ? u.getNombre() : "ID: " + r.getUsuarioId();
        }).setHeader("Cliente").setAutoWidth(true);
        grid.addColumn(r -> {
            Espacio esp = r.getEspacioId() != null ? crudService.buscarPorId(Espacio.class, r.getEspacioId()) : null;
            return esp != null ? esp.getNombre() : "ID: " + r.getEspacioId();
        }).setHeader("Espacio").setAutoWidth(true);
        grid.addColumn(r -> r.getFechaInicio() != null ? r.getFechaInicio().format(formatoFecha) : "")
                .setHeader("Inicio").setAutoWidth(true);
        grid.addColumn(r -> r.getFechaFin() != null ? r.getFechaFin().format(formatoFecha) : "")
                .setHeader("Fin").setAutoWidth(true);
        grid.addComponentColumn(r -> crearBadge(
                String.format("$ %,.0f", r.getTotalPagar() != null ? r.getTotalPagar() : 0.0),
                "#065F46", "#D1FAE5"
        )).setHeader("Total ($)").setAutoWidth(true);

        // Selectores desplegables (ComboBox)
        ComboBox<Usuario> cmbUsuario = new ComboBox<>("Cliente / Usuario");
        cmbUsuario.setItemLabelGenerator(u -> u.getNombre() != null ? u.getNombre() : "Sin nombre");
        cmbUsuario.setPlaceholder("Seleccione el cliente...");

        ComboBox<Espacio> cmbEspacio = new ComboBox<>("Espacio a Reservar");
        cmbEspacio.setItemLabelGenerator(esp -> esp.getNombre() + " ($" + String.format("%,.0f", esp.getPrecioHora()) + "/h)");
        cmbEspacio.setPlaceholder("Seleccione el espacio...");

        // Refresca la tabla, las listas desplegables y las tarjetas KPI
        Runnable refrescar = () -> {
            grid.setItems(crudService.listarTodos(Reserva.class));
            cmbUsuario.setItems(crudService.listarTodos(Usuario.class));
            cmbEspacio.setItems(crudService.listarTodos(Espacio.class));
            actualizarKpis();
        };
        refrescar.run();
        grid.setAllRowsVisible(true);

        DateTimePicker txtFechaInicio = new DateTimePicker("Fecha y Hora de Inicio");
        DateTimePicker txtFechaFin = new DateTimePicker("Fecha y Hora de Fin");

        NumberField txtTotalPagar = new NumberField("Total Calculado ($)");
        txtTotalPagar.setPrefixComponent(VaadinIcon.CASH.create());
        txtTotalPagar.setReadOnly(true); // Se calcula automáticamente según tarifa oficial

        // Cálculo automático del total a pagar cuando cambian el espacio o las fechas
        Runnable calcularTotal = () -> {
            if (cmbEspacio.getValue() != null && txtFechaInicio.getValue() != null && txtFechaFin.getValue() != null) {
                long minutos = Duration.between(txtFechaInicio.getValue(), txtFechaFin.getValue()).toMinutes();
                if (minutos > 0) {
                    double horas = Math.ceil(minutos / 60.0);
                    double precioHora = cmbEspacio.getValue().getPrecioHora() != null ? cmbEspacio.getValue().getPrecioHora() : 0.0;
                    txtTotalPagar.setValue(horas * precioHora);
                }
            }
        };
        cmbEspacio.addValueChangeListener(e -> calcularTotal.run());
        txtFechaInicio.addValueChangeListener(e -> calcularTotal.run());
        txtFechaFin.addValueChangeListener(e -> calcularTotal.run());

        // Botones CRUD con iconos
        Button btnGuardar = new Button("Guardar", VaadinIcon.CHECK.create());
        Button btnActualizar = new Button("Actualizar", VaadinIcon.REFRESH.create());
        Button btnEliminar = new Button("Eliminar", VaadinIcon.TRASH.create());
        Button btnLimpiar = new Button("Limpiar", VaadinIcon.ERASER.create());

        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnActualizar.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);
        btnLimpiar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        // BOTONES INTERACTIVOS DE EXPORTACIÓN (EXCEL Y PDF)
        Button btnExportarExcel = new Button("Exportar Excel (POI)", VaadinIcon.FILE_TABLE.create());
        btnExportarExcel.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_TERTIARY);
        Anchor anchorExcel = new Anchor(generarExcelReservas(), "");
        anchorExcel.setTarget("_blank");
        anchorExcel.add(btnExportarExcel);

        Button btnExportarPdf = new Button("Exportar PDF (iText)", VaadinIcon.FILE_TEXT.create());
        btnExportarPdf.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        Anchor anchorPdf = new Anchor(generarPdfReservas(), "");
        anchorPdf.setTarget("_blank");
        anchorPdf.add(btnExportarPdf);

        // Evento al seleccionar una reserva de la tabla
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                Reserva r = crudService.buscarPorId(Reserva.class, event.getValue().getId());
                if (r != null) {
                    cmbUsuario.setValue(r.getUsuarioId() != null ? crudService.buscarPorId(Usuario.class, r.getUsuarioId()) : null);
                    cmbEspacio.setValue(r.getEspacioId() != null ? crudService.buscarPorId(Espacio.class, r.getEspacioId()) : null);
                    txtFechaInicio.setValue(r.getFechaInicio());
                    txtFechaFin.setValue(r.getFechaFin());
                    txtTotalPagar.setValue(r.getTotalPagar());
                }
            }
        });

        btnGuardar.addClickListener(e -> {
            if (cmbUsuario.getValue() == null || cmbEspacio.getValue() == null) {
                mostrarNotificacion("Seleccione el Usuario y el Espacio obligatorios", NotificationVariant.LUMO_ERROR);
                return;
            }
            Long usuarioId = cmbUsuario.getValue().getId();
            Long espacioId = cmbEspacio.getValue().getId();

            Reserva r = new Reserva(null, usuarioId, espacioId, txtFechaInicio.getValue(), txtFechaFin.getValue(), txtTotalPagar.getValue());
            crudService.guardar(r);
            refrescar.run();
            grid.asSingleSelect().clear();
            cmbUsuario.clear();
            cmbEspacio.clear();
            txtFechaInicio.clear();
            txtFechaFin.clear();
            txtTotalPagar.clear();
            mostrarNotificacion("Reserva guardada con éxito", NotificationVariant.LUMO_SUCCESS);
        });

        btnActualizar.addClickListener(e -> {
            Reserva seleccionada = grid.asSingleSelect().getValue();
            if (seleccionada == null) {
                mostrarNotificacion("Seleccione una reserva de la tabla para actualizar", NotificationVariant.LUMO_CONTRAST);
                return;
            }
            if (cmbUsuario.getValue() == null || cmbEspacio.getValue() == null) {
                mostrarNotificacion("Seleccione el Usuario y el Espacio obligatorios", NotificationVariant.LUMO_ERROR);
                return;
            }
            seleccionada.setUsuarioId(cmbUsuario.getValue().getId());
            seleccionada.setEspacioId(cmbEspacio.getValue().getId());
            seleccionada.setFechaInicio(txtFechaInicio.getValue());
            seleccionada.setFechaFin(txtFechaFin.getValue());
            seleccionada.setTotalPagar(txtTotalPagar.getValue());
            crudService.actualizar(seleccionada);
            refrescar.run();
            grid.asSingleSelect().clear();
            cmbUsuario.clear();
            cmbEspacio.clear();
            txtFechaInicio.clear();
            txtFechaFin.clear();
            txtTotalPagar.clear();
            mostrarNotificacion("Reserva actualizada con éxito", NotificationVariant.LUMO_SUCCESS);
        });

        btnEliminar.addClickListener(e -> {
            Reserva seleccionada = grid.asSingleSelect().getValue();
            if (seleccionada != null) {
                crudService.eliminar(Reserva.class, seleccionada.getId());
                refrescar.run();
                grid.asSingleSelect().clear();
                cmbUsuario.clear();
                cmbEspacio.clear();
                txtFechaInicio.clear();
                txtFechaFin.clear();
                txtTotalPagar.clear();
                mostrarNotificacion("Reserva eliminada con éxito", NotificationVariant.LUMO_SUCCESS);
            } else {
                mostrarNotificacion("Seleccione una reserva de la tabla para eliminar", NotificationVariant.LUMO_CONTRAST);
            }
        });

        btnLimpiar.addClickListener(e -> {
            grid.asSingleSelect().clear();
            cmbUsuario.clear();
            cmbEspacio.clear();
            txtFechaInicio.clear();
            txtFechaFin.clear();
            txtTotalPagar.clear();
        });

        // Fila 1 de botones: Operaciones CRUD
        HorizontalLayout accionesCrud = new HorizontalLayout(btnGuardar, btnActualizar, btnEliminar, btnLimpiar);
        accionesCrud.getStyle().set("flex-wrap", "wrap");

        // Fila 2 de botones: Exportación de Reportes
        HorizontalLayout accionesReportes = new HorizontalLayout(anchorExcel, anchorPdf);
        accionesReportes.getStyle().set("border-top", "1px solid #E2E8F0");
        accionesReportes.getStyle().set("padding-top", "10px");
        accionesReportes.setWidthFull();

        FormLayout formulario = new FormLayout();
        formulario.add(cmbUsuario, cmbEspacio, txtFechaInicio, txtFechaFin, txtTotalPagar);
        formulario.setColspan(cmbUsuario, 2);
        formulario.setColspan(cmbEspacio, 2);
        formulario.setColspan(txtFechaInicio, 2);
        formulario.setColspan(txtFechaFin, 2);
        formulario.setColspan(txtTotalPagar, 2);

        VerticalLayout contenidoForm = new VerticalLayout(formulario, accionesCrud, accionesReportes);
        contenidoForm.setPadding(false);
        contenidoForm.setSpacing(true);

        VerticalLayout cardTabla = crearTarjeta("Historial Global de Reservas", VaadinIcon.CALENDAR_CLOCK, grid);
        VerticalLayout cardForm = crearTarjeta("Agendar / Editar Reserva", VaadinIcon.CALENDAR_BRIEFCASE, contenidoForm);

        HorizontalLayout mainContent = new HorizontalLayout(cardTabla, cardForm);
        mainContent.setWidthFull();
        mainContent.setFlexGrow(2, cardTabla);
        mainContent.setFlexGrow(1, cardForm);

        layout.add(mainContent);
        return layout;
    }

    // =========================================================================
    // MÉTODOS DE EXPORTACIÓN (DELEGAN A LOS SERVICIOS DE EXCEL Y PDF)
    // =========================================================================
    private StreamResource generarExcelReservas() {
        return new StreamResource("Reporte_Reservas.xlsx", () ->
                excelReportService.generarReporteReservasExcel(crudService.listarTodos(Reserva.class))
        );
    }

    private StreamResource generarPdfReservas() {
        return new StreamResource("Reporte_Reservas.pdf", () ->
                pdfReportService.generarReporteReservasPdf(crudService.listarTodos(Reserva.class))
        );
    }
}