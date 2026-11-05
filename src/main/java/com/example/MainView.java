package com.example;

// Modelos del proyecto de Coworking
import com.example.model.Espacio;
import com.example.model.Reserva;
import com.example.model.TipoEspacio;
import com.example.model.Usuario;
import com.example.service.CrudService;
import com.example.service.ExcelReportService;
import com.example.service.PdfReportService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
// Importaciones para descargas de archivos
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

@PageTitle("Gestión Coworking - CRUD")
@Route("")
public class MainView extends VerticalLayout {

    // 1. Declaración de las 3 dependencias
    private final CrudService crudService;
    private final ExcelReportService excelReportService;
    private final PdfReportService pdfReportService;

    // Inyección de dependencias por constructor: Spring suministra CrudService automáticamente
    public MainView(CrudService crudService,ExcelReportService excelReportService, 
                    PdfReportService pdfReportService) {
        this.crudService = crudService;
        this.excelReportService = excelReportService;
        this.pdfReportService = pdfReportService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 titulo = new H2("Sistema de Gestión de Coworking");

        TabSheet tabSheet = new TabSheet();
        tabSheet.setWidthFull();

        tabSheet.add("Usuarios", crearSeccionUsuarios());
        tabSheet.add("Tipos de Espacio", crearSeccionTipoEspacio());
        tabSheet.add("Espacios", crearSeccionEspacios());
        tabSheet.add("Reservas", crearSeccionReservas());

        add(titulo, tabSheet);
    }

    // --- SECCIÓN 1: USUARIOS ---
    private Component crearSeccionUsuarios() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        Grid<Usuario> grid = new Grid<>(Usuario.class, false);
        grid.addColumn(u -> u.getId()).setHeader("ID").setAutoWidth(true);
        grid.addColumn(u -> u.getNombre()).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(u -> u.getEmail()).setHeader("Email").setAutoWidth(true);
        grid.addColumn(u -> u.getTelefono()).setHeader("Teléfono").setAutoWidth(true);  

        Runnable refrescar = () -> grid.setItems(crudService.listarTodos(Usuario.class));
        refrescar.run();
        grid.setSizeFull();

        TextField txtNombre = new TextField("Nombre Completo");
        EmailField txtEmail = new EmailField("Correo Electrónico");
        TextField txtTelefono = new TextField("Teléfono");

        txtNombre.setRequired(true);
        txtEmail.setErrorMessage("Ingrese un correo válido (ejemplo@dominio.com)");

        Button btnGuardar = new Button("Guardar");
        Button btnActualizar = new Button("Actualizar");
        Button btnEliminar = new Button("Eliminar");
        Button btnLimpiar = new Button("Limpiar Formulario");

        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnActualizar.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

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

        btnGuardar.addClickListener(e -> {
            if (txtNombre.isEmpty() || txtEmail.isEmpty()) {
                Notification.show("Por favor complete los campos requeridos");
                return;
            }
            Usuario u = new Usuario(null, txtNombre.getValue(), txtEmail.getValue(), txtTelefono.getValue());
            crudService.guardar(u);
            refrescar.run();
            grid.asSingleSelect().clear();
            txtNombre.clear();
            txtEmail.clear();
            txtTelefono.clear();
            Notification.show("Usuario guardado con éxito");
        });

        btnActualizar.addClickListener(e -> {
            Usuario seleccionado = grid.asSingleSelect().getValue();
            if (seleccionado == null) {
                Notification.show("Seleccione un usuario de la tabla para actualizar");
                return;
            }
            if (txtNombre.isEmpty() || txtEmail.isEmpty()) {
                Notification.show("Por favor complete los campos requeridos");
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
            Notification.show("Usuario actualizado con éxito");
        });

        btnEliminar.addClickListener(e -> {
            Usuario seleccionado = grid.asSingleSelect().getValue();
            if (seleccionado != null) {
                crudService.eliminar(Usuario.class, seleccionado.getId());
                refrescar.run();
                grid.asSingleSelect().clear();
                txtNombre.clear();
                txtEmail.clear();
                txtTelefono.clear();
                Notification.show("Usuario eliminado con éxito");
            } else {
                Notification.show("Seleccione un usuario de la tabla para eliminar");
            }
        });

        btnLimpiar.addClickListener(e -> {
            grid.asSingleSelect().clear();
            txtNombre.clear();
            txtEmail.clear();
            txtTelefono.clear();
        });

        HorizontalLayout acciones = new HorizontalLayout(btnGuardar, btnActualizar, btnEliminar, btnLimpiar);

        FormLayout formulario = new FormLayout();
        formulario.add(txtNombre, txtEmail, txtTelefono, acciones);

        HorizontalLayout mainContent = new HorizontalLayout(grid, formulario);
        mainContent.setSizeFull();
        mainContent.setFlexGrow(2, grid);
        mainContent.setFlexGrow(1, formulario);

        layout.add(mainContent);
        return layout;
    }

    // --- SECCIÓN 2: TIPOS DE ESPACIO ---
    private Component crearSeccionTipoEspacio() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

       Grid<TipoEspacio> grid = new Grid<>(TipoEspacio.class, false);
        grid.addColumn(t -> t.getId()).setHeader("ID").setAutoWidth(true);
        grid.addColumn(t -> t.getNombre()).setHeader("Nombre del Tipo").setAutoWidth(true);
        grid.addColumn(t -> t.getDescripcion()).setHeader("Descripción").setAutoWidth(true);

        Runnable refrescar = () -> grid.setItems(crudService.listarTodos(TipoEspacio.class));
        refrescar.run();
        grid.setSizeFull();

        TextField txtNombre = new TextField("Nombre del Tipo de Espacio");
        TextArea txtDescripcion = new TextArea("Descripción / Detalles");

        txtNombre.setRequired(true);

        Button btnGuardar = new Button("Guardar");
        Button btnActualizar = new Button("Actualizar");
        Button btnEliminar = new Button("Eliminar");
        Button btnLimpiar = new Button("Limpiar Formulario");

        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnActualizar.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

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
                Notification.show("Ingrese el nombre del tipo de espacio");
                return;
            }
            TipoEspacio t = new TipoEspacio(null, txtNombre.getValue(), txtDescripcion.getValue());
            crudService.guardar(t);
            refrescar.run();
            grid.asSingleSelect().clear();
            txtNombre.clear();
            txtDescripcion.clear();
            Notification.show("Tipo de espacio guardado con éxito");
        });

        btnActualizar.addClickListener(e -> {
            TipoEspacio seleccionado = grid.asSingleSelect().getValue();
            if (seleccionado == null) {
                Notification.show("Seleccione un tipo de espacio de la tabla para actualizar");
                return;
            }
            if (txtNombre.isEmpty()) {
                Notification.show("Ingrese el nombre del tipo de espacio");
                return;
            }
            seleccionado.setNombre(txtNombre.getValue());
            seleccionado.setDescripcion(txtDescripcion.getValue());
            crudService.actualizar(seleccionado);
            refrescar.run();
            grid.asSingleSelect().clear();
            txtNombre.clear();
            txtDescripcion.clear();
            Notification.show("Tipo de espacio actualizado con éxito");
        });

        btnEliminar.addClickListener(e -> {
            TipoEspacio seleccionado = grid.asSingleSelect().getValue();
            if (seleccionado != null) {
                crudService.eliminar(TipoEspacio.class, seleccionado.getId());
                refrescar.run();
                grid.asSingleSelect().clear();
                txtNombre.clear();
                txtDescripcion.clear();
                Notification.show("Tipo de espacio eliminado con éxito");
            } else {
                Notification.show("Seleccione un elemento de la tabla para eliminar");
            }
        });

        btnLimpiar.addClickListener(e -> {
            grid.asSingleSelect().clear();
            txtNombre.clear();
            txtDescripcion.clear();
        });

        HorizontalLayout acciones = new HorizontalLayout(btnGuardar, btnActualizar, btnEliminar, btnLimpiar);

        FormLayout formulario = new FormLayout();
        formulario.add(txtNombre, txtDescripcion, acciones);

        HorizontalLayout mainContent = new HorizontalLayout(grid, formulario);
        mainContent.setSizeFull();
        mainContent.setFlexGrow(2, grid);
        mainContent.setFlexGrow(1, formulario);

        layout.add(mainContent);
        return layout;
    }

    // --- SECCIÓN 3: ESPACIOS ---
    private Component crearSeccionEspacios() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        Grid<Espacio> grid = new Grid<>(Espacio.class, false);
        grid.addColumn(e -> e.getId()).setHeader("ID").setAutoWidth(true);
        grid.addColumn(e -> e.getNombre()).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(e -> e.getCapacidad()).setHeader("Capacidad").setAutoWidth(true);
        grid.addColumn(e -> e.getPrecioHora()).setHeader("Precio/Hora").setAutoWidth(true);
        grid.addColumn(e -> e.getTipoEspacioId()).setHeader("Tipo Espacio ID").setAutoWidth(true);

        Runnable refrescar = () -> grid.setItems(crudService.listarTodos(Espacio.class));
        refrescar.run();
        grid.setSizeFull();

        TextField txtNombre = new TextField("Nombre del Espacio");
        IntegerField txtCapacidad = new IntegerField("Capacidad (personas)");
        NumberField txtPrecio = new NumberField("Precio por Hora ($)");
        NumberField txtTipoEspacioId = new NumberField("Tipo Espacio ID");

        Button btnGuardar = new Button("Guardar");
        Button btnActualizar = new Button("Actualizar");
        Button btnEliminar = new Button("Eliminar");
        Button btnLimpiar = new Button("Limpiar");

        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnActualizar.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                Espacio esp = crudService.buscarPorId(Espacio.class, event.getValue().getId());
                if (esp != null) {
                    txtNombre.setValue(esp.getNombre() != null ? esp.getNombre() : "");
                    txtCapacidad.setValue(esp.getCapacidad());
                    txtPrecio.setValue(esp.getPrecioHora());
                    txtTipoEspacioId.setValue(esp.getTipoEspacioId() != null ? esp.getTipoEspacioId().doubleValue() : null);
                }
            }
        });

        btnGuardar.addClickListener(e -> {
            if (txtNombre.isEmpty()) {
                Notification.show("Ingrese el nombre del espacio");
                return;
            }
            Long tipoId = txtTipoEspacioId.getValue() != null ? txtTipoEspacioId.getValue().longValue() : null;
            Espacio esp = new Espacio(null, txtNombre.getValue(), txtCapacidad.getValue(), txtPrecio.getValue(), tipoId);
            crudService.guardar(esp);
            refrescar.run();
            grid.asSingleSelect().clear();
            txtNombre.clear();
            txtCapacidad.clear();
            txtPrecio.clear();
            txtTipoEspacioId.clear();
            Notification.show("Espacio guardado con éxito");
        });

        btnActualizar.addClickListener(e -> {
            Espacio seleccionado = grid.asSingleSelect().getValue();
            if (seleccionado == null) {
                Notification.show("Seleccione un espacio de la tabla para actualizar");
                return;
            }
            if (txtNombre.isEmpty()) {
                Notification.show("Ingrese el nombre del espacio");
                return;
            }
            Long tipoId = txtTipoEspacioId.getValue() != null ? txtTipoEspacioId.getValue().longValue() : null;
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
            txtTipoEspacioId.clear();
            Notification.show("Espacio actualizado con éxito");
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
                txtTipoEspacioId.clear();
                Notification.show("Espacio eliminado con éxito");
            } else {
                Notification.show("Seleccione un espacio de la tabla para eliminar");
            }
        });

        btnLimpiar.addClickListener(e -> {
            grid.asSingleSelect().clear();
            txtNombre.clear();
            txtCapacidad.clear();
            txtPrecio.clear();
            txtTipoEspacioId.clear();
        });

        HorizontalLayout acciones = new HorizontalLayout(btnGuardar, btnActualizar, btnEliminar, btnLimpiar);

        FormLayout formulario = new FormLayout();
        formulario.add(txtNombre, txtCapacidad, txtPrecio, txtTipoEspacioId, acciones);

        HorizontalLayout mainContent = new HorizontalLayout(grid, formulario);
        mainContent.setSizeFull();
        mainContent.setFlexGrow(2, grid);
        mainContent.setFlexGrow(1, formulario);

        layout.add(mainContent);
        return layout;
    }

    // --- SECCIÓN 4: RESERVAS ---
    private Component crearSeccionReservas() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        Grid<Reserva> grid = new Grid<>(Reserva.class, false);
        grid.addColumn(r -> r.getId()).setHeader("ID").setAutoWidth(true);
        grid.addColumn(r -> r.getUsuarioId()).setHeader("Usuario ID").setAutoWidth(true);
        grid.addColumn(r -> r.getEspacioId()).setHeader("Espacio ID").setAutoWidth(true);
        grid.addColumn(r -> r.getFechaInicio()).setHeader("Fecha Inicio").setAutoWidth(true);
        grid.addColumn(r -> r.getFechaFin()).setHeader("Fecha Fin").setAutoWidth(true);
        grid.addColumn(r -> r.getTotalPagar()).setHeader("Total Pagar ($)").setAutoWidth(true);
        Runnable refrescar = () -> grid.setItems(crudService.listarTodos(Reserva.class));
        refrescar.run();
        grid.setSizeFull();

        IntegerField txtUsuarioId = new IntegerField("ID del Usuario");
        IntegerField txtEspacioId = new IntegerField("ID del Espacio");
        DateTimePicker txtFechaInicio = new DateTimePicker("Fecha y Hora de Inicio");
        DateTimePicker txtFechaFin = new DateTimePicker("Fecha y Hora de Fin");
        NumberField txtTotalPagar = new NumberField("Total Pagar ($)");

        Button btnGuardar = new Button("Guardar");
        Button btnActualizar = new Button("Actualizar");
        Button btnEliminar = new Button("Eliminar");
        Button btnLimpiar = new Button("Limpiar");

        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnActualizar.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        // BOTONES INTERACTIVOS DE EXPORTACIÓN (EXCEL Y PDF)
        Button btnExportarExcel = new Button("Exportar Excel (POI)");
        btnExportarExcel.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        Anchor anchorExcel = new Anchor(generarExcelReservas(), "");
        anchorExcel.setTarget("_blank");
        anchorExcel.add(btnExportarExcel);

        Button btnExportarPdf = new Button("Exportar PDF (iText)");
        btnExportarPdf.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Anchor anchorPdf = new Anchor(generarPdfReservas(), "");
        anchorPdf.setTarget("_blank");
        anchorPdf.add(btnExportarPdf);

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                Reserva r = crudService.buscarPorId(Reserva.class, event.getValue().getId());
                if (r != null) {
                    txtUsuarioId.setValue(r.getUsuarioId() != null ? r.getUsuarioId().intValue() : null);
                    txtEspacioId.setValue(r.getEspacioId() != null ? r.getEspacioId().intValue() : null);
                    txtFechaInicio.setValue(r.getFechaInicio());
                    txtFechaFin.setValue(r.getFechaFin());
                    txtTotalPagar.setValue(r.getTotalPagar());
                }
            }
        });

        btnGuardar.addClickListener(e -> {
            if (txtUsuarioId.getValue() == null || txtEspacioId.getValue() == null) {
                Notification.show("Ingrese los ID de Usuario y Espacio obligatorios");
                return;
            }
            Long usuarioId = txtUsuarioId.getValue().longValue();
            Long espacioId = txtEspacioId.getValue().longValue();

            Reserva r = new Reserva(null, usuarioId, espacioId, txtFechaInicio.getValue(), txtFechaFin.getValue(), txtTotalPagar.getValue());
            crudService.guardar(r);
            refrescar.run();
            grid.asSingleSelect().clear();
            txtUsuarioId.clear();
            txtEspacioId.clear();
            txtFechaInicio.clear();
            txtFechaFin.clear();
            txtTotalPagar.clear();
            Notification.show("Reserva guardada con éxito");
        });

        btnActualizar.addClickListener(e -> {
            Reserva seleccionada = grid.asSingleSelect().getValue();
            if (seleccionada == null) {
                Notification.show("Seleccione una reserva de la tabla para actualizar");
                return;
            }
            if (txtUsuarioId.getValue() == null || txtEspacioId.getValue() == null) {
                Notification.show("Ingrese los ID de Usuario y Espacio obligatorios");
                return;
            }
            seleccionada.setUsuarioId(txtUsuarioId.getValue().longValue());
            seleccionada.setEspacioId(txtEspacioId.getValue().longValue());
            seleccionada.setFechaInicio(txtFechaInicio.getValue());
            seleccionada.setFechaFin(txtFechaFin.getValue());
            seleccionada.setTotalPagar(txtTotalPagar.getValue());
            crudService.actualizar(seleccionada);
            refrescar.run();
            grid.asSingleSelect().clear();
            txtUsuarioId.clear();
            txtEspacioId.clear();
            txtFechaInicio.clear();
            txtFechaFin.clear();
            txtTotalPagar.clear();
            Notification.show("Reserva actualizada con éxito");
        });

        btnEliminar.addClickListener(e -> {
            Reserva seleccionada = grid.asSingleSelect().getValue();
            if (seleccionada != null) {
                crudService.eliminar(Reserva.class, seleccionada.getId());
                refrescar.run();
                grid.asSingleSelect().clear();
                txtUsuarioId.clear();
                txtEspacioId.clear();
                txtFechaInicio.clear();
                txtFechaFin.clear();
                txtTotalPagar.clear();
                Notification.show("Reserva eliminada con éxito");
            } else {
                Notification.show("Seleccione una reserva de la tabla para eliminar");
            }
        });

        btnLimpiar.addClickListener(e -> {
            grid.asSingleSelect().clear();
            txtUsuarioId.clear();
            txtEspacioId.clear();
            txtFechaInicio.clear();
            txtFechaFin.clear();
            txtTotalPagar.clear();
        });

        HorizontalLayout acciones = new HorizontalLayout(
            btnGuardar, btnActualizar, btnEliminar, btnLimpiar, anchorExcel, anchorPdf
        );
        acciones.getStyle().set("flex-wrap", "wrap");

        FormLayout formulario = new FormLayout();
        formulario.add(txtUsuarioId, txtEspacioId, txtFechaInicio, txtFechaFin, txtTotalPagar, acciones);

        HorizontalLayout mainContent = new HorizontalLayout(grid, formulario);
        mainContent.setSizeFull();
        mainContent.setFlexGrow(2, grid);
        mainContent.setFlexGrow(1, formulario);

        layout.add(mainContent);
        return layout;
    }

    // ==========================================
    // MÉTODOS DE EXPORTACIÓN (DELEGAN A LOS SERVICIOS)
    // ==========================================
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


