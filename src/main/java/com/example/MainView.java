package com.example;

// Modelos del proyecto de Coworking
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.model.Espacio;
import com.example.model.Reserva;
import com.example.model.TipoEspacio;
import com.example.model.Usuario;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
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

@PageTitle("Gestión Coworking - CRUD")
@Route("")
public class MainView extends VerticalLayout {

    private final Usuario usuario;
    private final TipoEspacio tipoEspacio;
    private final Espacio espacio;
    private final Reserva reserva;

    @Autowired
    public MainView(JdbcTemplate jdbcTemplate) {
        this.usuario = new Usuario(jdbcTemplate);
        this.tipoEspacio = new TipoEspacio(jdbcTemplate);
        this.espacio = new Espacio(jdbcTemplate);
        this.reserva = new Reserva(jdbcTemplate);

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
        grid.addColumn(Usuario::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Usuario::getNombre).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(Usuario::getEmail).setHeader("Email").setAutoWidth(true);
        grid.addColumn(Usuario::getTelefono).setHeader("Teléfono").setAutoWidth(true);

        grid.setItems(usuario.listarTodos());
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
                Usuario u = usuario.obtenerPorId(event.getValue().getId());
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
            usuario.guardar(u);
            grid.setItems(usuario.listarTodos());
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
            usuario.actualizar(seleccionado);
            grid.setItems(usuario.listarTodos());
            grid.asSingleSelect().clear();
            txtNombre.clear();
            txtEmail.clear();
            txtTelefono.clear();
            Notification.show("Usuario actualizado con éxito");
        });

        btnEliminar.addClickListener(e -> {
            Usuario seleccionado = grid.asSingleSelect().getValue();
            if (seleccionado != null) {
                usuario.eliminar(seleccionado.getId());
                grid.setItems(usuario.listarTodos());
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
        grid.addColumn(TipoEspacio::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(TipoEspacio::getNombre).setHeader("Nombre del Tipo").setAutoWidth(true);
        grid.addColumn(TipoEspacio::getDescripcion).setHeader("Descripción").setAutoWidth(true);

        grid.setItems(tipoEspacio.listarTodos());
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
                TipoEspacio t = tipoEspacio.obtenerPorId(event.getValue().getId());
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
            tipoEspacio.guardar(t);
            grid.setItems(tipoEspacio.listarTodos());
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
            tipoEspacio.actualizar(seleccionado);
            grid.setItems(tipoEspacio.listarTodos());
            grid.asSingleSelect().clear();
            txtNombre.clear();
            txtDescripcion.clear();
            Notification.show("Tipo de espacio actualizado con éxito");
        });

        btnEliminar.addClickListener(e -> {
            TipoEspacio seleccionado = grid.asSingleSelect().getValue();
            if (seleccionado != null) {
                tipoEspacio.eliminar(seleccionado.getId());
                grid.setItems(tipoEspacio.listarTodos());
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
        grid.addColumn(Espacio::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Espacio::getNombre).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(Espacio::getCapacidad).setHeader("Capacidad").setAutoWidth(true);
        grid.addColumn(Espacio::getPrecioHora).setHeader("Precio/Hora").setAutoWidth(true);
        grid.addColumn(Espacio::getTipoEspacioId).setHeader("Tipo Espacio ID").setAutoWidth(true);

        grid.setItems(espacio.listarTodos());
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
                Espacio esp = espacio.obtenerPorId(event.getValue().getId());
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
            espacio.guardar(esp);
            grid.setItems(espacio.listarTodos());
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
            espacio.actualizar(seleccionado);
            grid.setItems(espacio.listarTodos());
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
                espacio.eliminar(seleccionado.getId());
                grid.setItems(espacio.listarTodos());
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
        grid.addColumn(Reserva::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Reserva::getUsuarioId).setHeader("Usuario ID").setAutoWidth(true);
        grid.addColumn(Reserva::getEspacioId).setHeader("Espacio ID").setAutoWidth(true);
        grid.addColumn(Reserva::getFechaInicio).setHeader("Fecha Inicio").setAutoWidth(true);
        grid.addColumn(Reserva::getFechaFin).setHeader("Fecha Fin").setAutoWidth(true);
        grid.addColumn(Reserva::getTotalPagar).setHeader("Total Pagar ($)").setAutoWidth(true);

        grid.setItems(reserva.listarTodos());
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

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                Reserva r = reserva.obtenerPorId(event.getValue().getId());
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
            reserva.guardar(r);
            grid.setItems(reserva.listarTodos());
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
            reserva.actualizar(seleccionada);
            grid.setItems(reserva.listarTodos());
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
                reserva.eliminar(seleccionada.getId());
                grid.setItems(reserva.listarTodos());
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

        HorizontalLayout acciones = new HorizontalLayout(btnGuardar, btnActualizar, btnEliminar, btnLimpiar);

        FormLayout formulario = new FormLayout();
        formulario.add(txtUsuarioId, txtEspacioId, txtFechaInicio, txtFechaFin, txtTotalPagar, acciones);

        HorizontalLayout mainContent = new HorizontalLayout(grid, formulario);
        mainContent.setSizeFull();
        mainContent.setFlexGrow(2, grid);
        mainContent.setFlexGrow(1, formulario);

        layout.add(mainContent);
        return layout;
    }

}