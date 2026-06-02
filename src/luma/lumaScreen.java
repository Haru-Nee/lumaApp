package luma;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.BorderFactory;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import javax.swing.ListCellRenderer;
import java.awt.Component;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

class Usuario implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private String usuario;
    private int password;
    private String nombre;
    private String archivo;

    public Usuario(String usuario, int password, String nombre) {
        this.usuario = usuario;
        this.password = password;
        this.nombre = nombre;
        this.archivo = usuario + "_tareas.bin";
    }

    public String getUsuario() { return usuario; }
    public int getPassword()   { return password; }
    public String getNombre()  { return nombre; }
    public String getArchivo() { return archivo; }
}


class Tarea implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    protected String titulo;
    protected String descripcion;
    protected boolean completada;
    protected String fechaCreacion;
    protected String jcombo;
	protected String categoria;
    protected String fechaCompletada;
    
    public Tarea(String titulo, String descripcion, String categoria) {
        this.titulo       = titulo;
        this.descripcion  = descripcion;
        this.completada   = false;
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        this.fechaCreacion = java.time.LocalDate.now().format(formato);
        this.categoria = categoria;
    }

    public String getTitulo()       { return titulo; }
    public void   setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion()  { return descripcion; }
    public void   setDescripcion(String d) { this.descripcion = d; }
    public boolean isCompletada()   { return completada; }
    public void   setCompletada(boolean c) {
        this.completada = c;
        if (c) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            this.fechaCompletada = java.time.LocalDate.now().format(fmt);
        } else {
            this.fechaCompletada = null;
        }
    }
    public String getFechaCompletada() { return fechaCompletada; }
    public String getFechaCreacion(){ return fechaCreacion; }
    public String getCategoria()    { return categoria; }

    public String getTipo() { return "Tarea"; }
    
    public String getEstado() { return completada ? "FINALIZADA" : "PENDIENTE"; }

    @Override
    public String toString() { return titulo + " [" + getTipo() + "]"; }
}


class TareaPrioridad extends Tarea {
    private int    prioridad;
    private String fechaLimite;

    public TareaPrioridad(String titulo, String descripcion, String categoria,
                          int prioridad, String fechaLimite) {
        super(titulo, descripcion, categoria);  
        this.prioridad   = prioridad;
        this.fechaLimite = fechaLimite;
    }

    @Override
    public String getTipo() { return "Tarea Prioritaria"; }

    public int    getPrioridad()   { return prioridad; }
    public void   setPrioridad(int p) { this.prioridad = p; }
    public String getFechaLimite() { return fechaLimite; }
    public void   setFechaLimite(String f) { this.fechaLimite = f; }

    @Override
    public String toString() {
        String cat  = (categoria  != null && !categoria.isEmpty())  ? " | Cat: " + categoria  : "";
        String fLim = (fechaLimite != null && !fechaLimite.isEmpty()) ? " | Límite: " + fechaLimite : "";
        String estado = completada ? " ✓ FINALIZADA" : "";
        return titulo + " [" + getTipo() + "]" + cat + fLim + estado;
    }
}


class ManejadorArchivos {
    private static String ARCHIVO_USUARIOS = "usuarios.bin";

    public static void guardarUsuarios(ArrayList<Usuario> usuarios) {
        try (ObjectOutputStream oo = new ObjectOutputStream(
                new FileOutputStream(ARCHIVO_USUARIOS))) {
            oo.writeObject(usuarios);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Usuario> cargarUsuarios() {
        File archivo = new File(ARCHIVO_USUARIOS);
        if (!archivo.exists()) return new ArrayList<>();
        try (ObjectInputStream oi = new ObjectInputStream(
                new FileInputStream(ARCHIVO_USUARIOS))) {
            return (ArrayList<Usuario>) oi.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public static Usuario buscarUsuario(String nombreUsuario) {
        for (Usuario u : cargarUsuarios())
            if (u.getUsuario().equals(nombreUsuario)) return u;
        return null;
    }

    public static void guardarTareas(ArrayList<Tarea> tareas, String archivoUsuario) {
        try (ObjectOutputStream oo = new ObjectOutputStream(
                new FileOutputStream(archivoUsuario))) {
            oo.writeObject(tareas);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Tarea> cargarTareas(String archivoUsuario) {
        File archivo = new File(archivoUsuario);
        if (!archivo.exists()) return new ArrayList<>();
        try (ObjectInputStream oi = new ObjectInputStream(
                new FileInputStream(archivoUsuario))) {
            return (ArrayList<Tarea>) oi.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
}


class GradientPanel extends JPanel {
    private Color color1, color2;
    public GradientPanel(Color color1, Color color2) {
        this.color1 = color1; this.color2 = color2; setOpaque(false);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setPaint(new GradientPaint(0, 0, color1, 0, getHeight(), color2));
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}


class RoundedPanel extends JPanel {
    private Color borderColor;
    public RoundedPanel(Color borderColor) {
        this.borderColor = borderColor; setOpaque(false);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(20, 20, 35, 220));
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        g2d.setColor(borderColor);
        g2d.setStroke(new java.awt.BasicStroke(1.5f));
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
    }
}

class TareaRenderer extends JLabel implements ListCellRenderer<Tarea>{
    public TareaRenderer() {
        setOpaque(true);
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }
    
    @Override
    public Component getListCellRendererComponent(
            JList<? extends Tarea> list,
            Tarea tarea,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        
        String tiempo = "";
        String estado = tarea.isCompletada() ? "FINALIZADA" : "";
        
        if (tarea.isCompletada()) {
            // Mostrar fecha de completado en lugar del tiempo restante
            String fc = tarea.getFechaCompletada();
            if (fc != null && !fc.isEmpty()) {
                tiempo = "Completada el " + fc;
            } else {
                tiempo = "Completada";
            }
        } else if (tarea instanceof TareaPrioridad) {
            try{
                String fechaTexto = ((TareaPrioridad) tarea).getFechaLimite();
                java.time.format.DateTimeFormatter formato = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate limite = LocalDate.parse(fechaTexto, formato);
                long dias = ChronoUnit.DAYS.between(LocalDate.now(), limite);
                tiempo = fechaTexto + " (" + dias + " días)";
            }
            catch(Exception e) {
                tiempo = "";
            }
        }
        //cambia los colores segun el estado actual de la tarea
        if (tarea.isCompletada()) {
            setBackground(new Color(30, 50, 30));
            setForeground(new Color(150, 200, 150));
        } else if (isSelected) {
            setBackground(new Color(0,100,200));
            setForeground(Color.WHITE);
        } else {
            setBackground(new Color(15,15,30));
            setForeground(Color.WHITE);
        }
        
        Color colorTiempo = Color.WHITE;
        Color colorEstado = new Color(100, 200, 100);
        
        if (tarea.isCompletada()) {
            colorTiempo = new Color(100, 150, 100);
            colorEstado = new Color(100, 255, 100);
        } else if (tarea instanceof TareaPrioridad) {
            try {
                String fechaTexto = ((TareaPrioridad)tarea).getFechaLimite();
                java.time.format.DateTimeFormatter formato = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate limite = LocalDate.parse(fechaTexto, formato);
                long dias = ChronoUnit.DAYS.between(LocalDate.now(), limite);
                if (dias <= 2) {
                    colorTiempo = Color.RED;
                }
                else if (dias <= 7) {
                    colorTiempo = Color.YELLOW;
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        
        String colorFecha = "#FFFFFF";
        if (colorTiempo == Color.RED) {
            colorFecha = "#FF4040";
        }
        else if (colorTiempo == Color.YELLOW) {
            colorFecha = "#FFD700";
        }
        else if (tarea.isCompletada()) {
            colorFecha = "#66AA66";
        }
        
        String colorEstadoHex = tarea.isCompletada() ? "#66FF66" : "#66AA66";
        
        setText(
            "<html><table width='100%'><tr>" +
            "<td><font color='" + (tarea.isCompletada() ? "#AACCAA" : "#FFFFFF") + "'>" +
            tarea.getTitulo() +
            "</font></td>" +
            "<td align='right'><font color='" + colorEstadoHex + "'>" +
            estado +
            "</font> <font color='" + colorFecha + "'>" +
            tiempo +
            "</font></td>" +
            "</tr></table></html>"
        );
        return this;
    }
}


class NeonButton extends JButton {
    private Color normalColor, hoverColor, textColor;

    public NeonButton(String text, Color normalColor, Color hoverColor, Color textColor) {
        super(text);
        this.normalColor = normalColor; this.hoverColor = hoverColor; this.textColor = textColor;
        setContentAreaFilled(false); setFocusPainted(false); setBorderPainted(false);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setForeground(textColor);
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        setBackground(normalColor);
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { setBackground(hoverColor); repaint(); }
            public void mouseExited(MouseEvent e)  { setBackground(normalColor); repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(getBackground() != null ? getBackground() : normalColor);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2d.setFont(getFont());
        g2d.setColor(textColor);
        java.awt.FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth()  - fm.stringWidth(getText())) / 2;
        int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(getText(), x, y);
    }
}


public class lumaScreen extends JFrame {

    private static final long serialVersionUID = 1L;

    // Paleta de colores
    private Color bgDark     = new Color(10, 10, 20);
    private Color bgMedium   = new Color(15, 15, 30);
    private Color azulNeon   = new Color(0, 200, 255);
    private Color azulOscuro = new Color(0, 100, 200);
    private Color rojoNeon   = new Color(255, 50, 80);
    private Color rojoOscuro = new Color(200, 0, 50);
    private Color textoBlanco = new Color(230, 230, 240);
    private Color textoGris  = new Color(160, 160, 180);
    private Color bordeSuave = new Color(60, 60, 100);
    private Color verdeNeon  = new Color(0, 220, 120);
    private Color verdeOscuro = new Color(0, 120, 60);

    // Layout y paneles
    private CardLayout cardLayout;
    private JPanel mainPanel, loginPanel, registroPanel, appPanel;

    // Login
    private JTextField    textUsuario;
    private JPasswordField passwordField;

    // Registro
    private JTextField     regUsuario, regNombre;
    private JPasswordField regPassword;

    // App
    private JLabel             tituloApp, subtituloApp;
    private RoundedPanel       panelTareas;
    private NeonButton         btnAgregarTarea, btnCerrarSesion;
    private JList<Tarea>       listaTareas;
    private DefaultListModel<Tarea> modeloTareas;

    // Filtros
    private JComboBox<String> comboCategoria;
    private JTextField        txtFiltroFecha;
    private NeonButton        btnFiltrar, btnLimpiarFiltro,btnEliminarTarea;

    // Dimensiones
    private final int VENTANA_ANCHO = 1060;
    private final int VENTANA_ALTO  = 700;

   
    private Usuario         actual;
    private ArrayList<Tarea> tareas;

    // Categorías predefinidas
    private static final String[] CATEGORIAS = {
        "Todas", "Matematicas", "Redes", "Programacion", "Ensamblador", "UNIX", "Fisica", "Laboratorio", "Español", "Finalizadas" };

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                lumaScreen frame = new lumaScreen();
                frame.setVisible(true);
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    public lumaScreen() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, VENTANA_ANCHO, VENTANA_ALTO);
        setTitle("LUMA - Task Manager");
        setResizable(false);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel  = new JPanel(cardLayout);

        construirPanelLogin();
        construirPanelRegistro();
        construirPanelApp();

        mainPanel.add(loginPanel,    "Login");
        mainPanel.add(registroPanel, "Registro");
        mainPanel.add(appPanel,      "App");

        cardLayout.show(mainPanel, "Login");
        setContentPane(mainPanel);
        addWindowListener(new java.awt.event.WindowAdapter() {
        	@Override
        	public void windowClosing(java.awt.event.WindowEvent e) {
        		if(actual != null &&
        	    tareas != null) {
        			ManejadorArchivos.guardarTareas(tareas, actual.getArchivo());
        	    }
        	}
        });
    }

    private void construirPanelLogin() {
        loginPanel = new GradientPanel(bgDark, bgMedium);
        loginPanel.setLayout(null);

        JLabel tituloLogin = new JLabel("L U M A");
        tituloLogin.setBounds(0, 60, VENTANA_ANCHO, 70);
        tituloLogin.setHorizontalAlignment(SwingConstants.CENTER);
        tituloLogin.setFont(new Font("Segoe UI", Font.BOLD, 52));
        tituloLogin.setForeground(azulNeon);
        loginPanel.add(tituloLogin);

        JLabel subtituloLogin = new JLabel("GESTOR DE TAREAS");
        subtituloLogin.setBounds(0, 130, VENTANA_ANCHO, 30);
        subtituloLogin.setHorizontalAlignment(SwingConstants.CENTER);
        subtituloLogin.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtituloLogin.setForeground(textoGris);
        loginPanel.add(subtituloLogin);

        RoundedPanel cardLogin = new RoundedPanel(azulNeon);
        cardLogin.setBounds(280, 190, 500, 320);
        cardLogin.setLayout(null);
        loginPanel.add(cardLogin);

        JLabel lblUsuario = new JLabel("USUARIO");
        lblUsuario.setBounds(50, 35, 400, 25);
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsuario.setForeground(azulNeon);
        cardLogin.add(lblUsuario);

        textUsuario = new JTextField();
        textUsuario.setBounds(50, 65, 400, 50);
        estilizarCampo(textUsuario, azulNeon);
        cardLogin.add(textUsuario);

        JLabel lblPassword = new JLabel("CONTRASEÑA");
        lblPassword.setBounds(50, 130, 400, 25);
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPassword.setForeground(azulNeon);
        cardLogin.add(lblPassword);

        passwordField = new JPasswordField();
        passwordField.setBounds(50, 160, 400, 50);
        estilizarCampo(passwordField, azulNeon);
        cardLogin.add(passwordField);

        NeonButton loginBtn = new NeonButton("INICIAR SESIÓN", azulOscuro, azulNeon, textoBlanco);
        loginBtn.setBounds(50, 250, 400, 45);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        cardLogin.add(loginBtn);

        JLabel lblRegistro = new JLabel("¿No tienes cuenta? Regístrate");
        lblRegistro.setBounds(0, 560, VENTANA_ANCHO, 30);
        lblRegistro.setHorizontalAlignment(SwingConstants.CENTER);
        lblRegistro.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblRegistro.setForeground(rojoNeon);
        lblRegistro.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        loginPanel.add(lblRegistro);

        loginBtn.addActionListener(e -> login());
        passwordField.addActionListener(e -> login());
        lblRegistro.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { mostrarPanel("Registro"); }
            public void mouseEntered(MouseEvent e) { lblRegistro.setForeground(new Color(255, 100, 120)); }
            public void mouseExited(MouseEvent e)  { lblRegistro.setForeground(rojoNeon); }
        });
    }

    private void construirPanelRegistro() {
        registroPanel = new GradientPanel(bgDark, bgMedium);
        registroPanel.setLayout(null);

        JLabel tituloRegistro = new JLabel("CREAR CUENTA");
        tituloRegistro.setBounds(0, 40, VENTANA_ANCHO, 60);
        tituloRegistro.setHorizontalAlignment(SwingConstants.CENTER);
        tituloRegistro.setFont(new Font("Segoe UI", Font.BOLD, 42));
        tituloRegistro.setForeground(rojoNeon);
        registroPanel.add(tituloRegistro);

        RoundedPanel cardRegistro = new RoundedPanel(rojoNeon);
        cardRegistro.setBounds(280, 120, 500, 400);
        cardRegistro.setLayout(null);
        registroPanel.add(cardRegistro);

        JLabel lblNombreReg = new JLabel("NOMBRE COMPLETO");
        lblNombreReg.setBounds(50, 30, 400, 25);
        lblNombreReg.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombreReg.setForeground(rojoNeon);
        cardRegistro.add(lblNombreReg);

        regNombre = new JTextField();
        regNombre.setBounds(50, 60, 400, 45);
        estilizarCampo(regNombre, rojoNeon);
        cardRegistro.add(regNombre);

        JLabel lblUsuarioReg = new JLabel("NOMBRE DE USUARIO");
        lblUsuarioReg.setBounds(50, 125, 400, 25);
        lblUsuarioReg.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsuarioReg.setForeground(rojoNeon);
        cardRegistro.add(lblUsuarioReg);

        regUsuario = new JTextField();
        regUsuario.setBounds(50, 155, 400, 45);
        estilizarCampo(regUsuario, rojoNeon);
        cardRegistro.add(regUsuario);

        JLabel lblPasswordReg = new JLabel("CONTRASEÑA");
        lblPasswordReg.setBounds(50, 220, 400, 25);
        lblPasswordReg.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPasswordReg.setForeground(rojoNeon);
        cardRegistro.add(lblPasswordReg);

        regPassword = new JPasswordField();
        regPassword.setBounds(50, 250, 400, 45);
        estilizarCampo(regPassword, rojoNeon);
        cardRegistro.add(regPassword);

        NeonButton registroBtn = new NeonButton("CREAR CUENTA", rojoOscuro, rojoNeon, textoBlanco);
        registroBtn.setBounds(50, 330, 400, 45);
        registroBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        cardRegistro.add(registroBtn);

        JLabel lblVolverLogin = new JLabel("¿Ya tienes cuenta? Inicia sesión");
        lblVolverLogin.setBounds(0, 560, VENTANA_ANCHO, 30);
        lblVolverLogin.setHorizontalAlignment(SwingConstants.CENTER);
        lblVolverLogin.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblVolverLogin.setForeground(azulNeon);
        lblVolverLogin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        registroPanel.add(lblVolverLogin);

       
        registroBtn.addActionListener(e -> registrarUsuario());
        lblVolverLogin.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { mostrarPanel("Login"); }
            public void mouseEntered(MouseEvent e) { lblVolverLogin.setForeground(new Color(100, 220, 255)); }
            public void mouseExited(MouseEvent e)  { lblVolverLogin.setForeground(azulNeon); }
        });
    }

    private void construirPanelApp() {
        appPanel = new GradientPanel(bgDark, bgMedium);
        appPanel.setLayout(null);

        tituloApp = new JLabel("MIS TAREAS");
        tituloApp.setBounds(0, 25, VENTANA_ANCHO, 60);
        tituloApp.setHorizontalAlignment(SwingConstants.CENTER);
        tituloApp.setFont(new Font("Segoe UI", Font.BOLD, 44));
        tituloApp.setForeground(azulNeon);
        appPanel.add(tituloApp);

        subtituloApp = new JLabel("Bienvenido de vuelta");
        subtituloApp.setBounds(0, 85, VENTANA_ANCHO, 28);
        subtituloApp.setHorizontalAlignment(SwingConstants.CENTER);
        subtituloApp.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        subtituloApp.setForeground(textoGris);
        appPanel.add(subtituloApp);

        //panel de filtros
        RoundedPanel panelFiltros = new RoundedPanel(verdeNeon);
        panelFiltros.setBounds(40,122,980,60);
        panelFiltros.setLayout(null);
        appPanel.add(panelFiltros);

        JLabel lblCat = new JLabel("Categoría:");
        lblCat.setBounds(15, 17, 90, 25);
        lblCat.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCat.setForeground(verdeNeon);
        panelFiltros.add(lblCat);

        comboCategoria = new JComboBox<>(CATEGORIAS);
        comboCategoria.setBounds(110, 15, 160, 30);
        comboCategoria.setBackground(new Color(20, 30, 40));
        comboCategoria.setForeground(textoBlanco);
        comboCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelFiltros.add(comboCategoria);

        JLabel lblFecha = new JLabel("Fecha límite:");
        lblFecha.setBounds(290, 17, 105, 25);
        lblFecha.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblFecha.setForeground(verdeNeon);
        panelFiltros.add(lblFecha);

        txtFiltroFecha = new JTextField();
        txtFiltroFecha.setBounds(400, 15, 130, 30);
        txtFiltroFecha.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtFiltroFecha.setBackground(new Color(20, 30, 40));
        txtFiltroFecha.setForeground(textoBlanco);
        txtFiltroFecha.setCaretColor(verdeNeon);
        txtFiltroFecha.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(bordeSuave, 1, true), new EmptyBorder(0, 8, 0, 8)));
        txtFiltroFecha.setToolTipText("Formato: dd-MM-yyyy");
        panelFiltros.add(txtFiltroFecha);

        btnFiltrar = new NeonButton("FILTRAR", verdeOscuro, verdeNeon, textoBlanco);
        btnFiltrar.setBounds(545, 12, 120, 36);
        btnFiltrar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelFiltros.add(btnFiltrar);

        btnLimpiarFiltro = new NeonButton("LIMPIAR", new Color(50,50,80), textoGris, textoBlanco);
        btnLimpiarFiltro.setBounds(675, 12, 120, 36);
        btnLimpiarFiltro.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelFiltros.add(btnLimpiarFiltro);
        
        btnEliminarTarea = new NeonButton("ELIMINAR", rojoOscuro, rojoNeon, textoBlanco);
        btnEliminarTarea.setBounds(800, 12, 90, 36);
        btnEliminarTarea.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelFiltros.add(btnEliminarTarea);
        
        NeonButton btnModificarTarea = new NeonButton("MODIFICAR", azulOscuro, azulNeon, textoBlanco);
        btnModificarTarea.setBounds(900, 12, 110, 36);
        btnModificarTarea.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelFiltros.add(btnModificarTarea);
        
        btnModificarTarea.addActionListener(e -> modificarTarea());
        


        // ---- PANEL DE TAREAS ----
        panelTareas = new RoundedPanel(azulNeon);
        panelTareas.setBounds(80, 200, 900, 340);
        panelTareas.setLayout(null);
        appPanel.add(panelTareas);

        modeloTareas = new DefaultListModel<>();
        listaTareas  = new JList<>(modeloTareas);
        listaTareas.setCellRenderer(new TareaRenderer());
        listaTareas.setBackground(new Color(15, 15, 30));
        listaTareas.setForeground(textoBlanco);
        listaTareas.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        listaTareas.setSelectionBackground(azulOscuro);
        listaTareas.setFixedCellHeight(36);

        JScrollPane scroll = new JScrollPane(listaTareas);
        scroll.setBounds(20, 20, 860, 300);
        scroll.getViewport().setBackground(new Color(15, 15, 30));
        scroll.setBorder(BorderFactory.createLineBorder(bordeSuave, 1));
        panelTareas.add(scroll);

        // ---- BOTONES INFERIORES ----
        btnAgregarTarea = new NeonButton("+ NUEVA TAREA", azulOscuro, azulNeon, textoBlanco);
        btnAgregarTarea.setBounds(280, 560, 500, 45);
        btnAgregarTarea.setFont(new Font("Segoe UI", Font.BOLD, 19));
        appPanel.add(btnAgregarTarea);

        btnCerrarSesion = new NeonButton("CERRAR SESIÓN", rojoOscuro, rojoNeon, textoBlanco);
        btnCerrarSesion.setBounds(380, 615, 300, 35);
        btnCerrarSesion.setFont(new Font("Segoe UI", Font.BOLD, 13));
        appPanel.add(btnCerrarSesion);
        
        
        
        //evento de que cuando se hace clic sobre la tarea se muestran detalles
        listaTareas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Tarea seleccionada =
                            listaTareas.getSelectedValue();
                    if (seleccionada != null) {
                        mostrarDetallesTarea(seleccionada);
                    }
                }
            }
        });

        // ---- EVENTOS DEL PANEL APP ----
        btnAgregarTarea.addActionListener(e -> agregarTarea());

        btnCerrarSesion.addActionListener(e -> {
            int op = JOptionPane.showConfirmDialog(null,
                "¿Estás seguro de que quieres cerrar sesión?",
                "Cerrar sesión", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                if(actual != null && tareas != null) {
                    ManejadorArchivos.guardarTareas(tareas, actual.getArchivo());
                }
                actual = null;
                tareas = null;
                mostrarPanel("Login");
            }
        });

        btnFiltrar.addActionListener(e -> aplicarFiltros());

        btnLimpiarFiltro.addActionListener(e -> {
            comboCategoria.setSelectedIndex(0);
            txtFiltroFecha.setText("");
            actualizarListaTareas();
        });
        
        btnEliminarTarea.addActionListener(
        	    e -> eliminarTarea()
        	);
    }

    private void estilizarCampo(javax.swing.text.JTextComponent campo, Color acent) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        campo.setBackground(new Color(30, 30, 50));
        campo.setForeground(textoBlanco);
        campo.setCaretColor(acent);
        campo.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(bordeSuave, 1, true), new EmptyBorder(0, 15, 0, 15)));
    }
    
    //muestra los detalles de cada tarea al ahcer clic sobre una 
    private void mostrarDetallesTarea(Tarea tarea) {
        String estado = tarea.isCompletada() ? "FINALIZADA" : "PENDIENTE";
        String mensaje = "Estado: " + estado + "\n\nTítulo: " + tarea.getTitulo() +
                		 "\n\nDescripción:\n" + tarea.getDescripcion() +
                		 "\n\nCategoría: " + tarea.getCategoria();
        if (tarea instanceof TareaPrioridad) {
            mensaje += "\n\nFecha límite: " + ((TareaPrioridad)tarea).getFechaLimite();
        }
        //cambia las opciones segun el estado
        Object[] opciones;
        if (tarea.isCompletada()) {
            opciones = new Object[] { "Reabrir", "Modificar", "Cerrar" };
        } else {
            opciones = new Object[] { "Finalizar", "Modificar", "Cerrar" };
        }
        int opcion = JOptionPane.showOptionDialog(
                this,
                mensaje,
                "Detalles de tarea",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opciones,
                opciones[2]);
        if (opcion == 0) {
            if (tarea.isCompletada()) {
                //reabrir tarea
                tarea.setCompletada(false);
                ManejadorArchivos.guardarTareas(tareas, actual.getArchivo());
                actualizarListaTareas();
                JOptionPane.showMessageDialog(this, "Tarea reabierta");
            } else {
                //finalizar tarea
                tarea.setCompletada(true);
                ManejadorArchivos.guardarTareas(tareas, actual.getArchivo());
                actualizarListaTareas();
                JOptionPane.showMessageDialog(this, "¡Tarea finalizada! ✓");
            }
        } else if (opcion == 1) {
            //modificar tarea
            modificarTareaSeleccionada(tarea);
        }
    }

    private void modificarTareaSeleccionada(Tarea seleccionada) {
        String nuevoTitulo = JOptionPane.showInputDialog(this, "Nuevo nombre de la tarea:", seleccionada.getTitulo());
        if (nuevoTitulo == null || nuevoTitulo.trim().isEmpty()) return;
        String nuevaDescripcion = JOptionPane.showInputDialog(this, "Nueva descripción:", seleccionada.getDescripcion());
        if (nuevaDescripcion == null) nuevaDescripcion = seleccionada.getDescripcion();
        String[] categorias = {"Tareas finalizadas","Matematicas", "Redes", "Programacion", "Ensamblador", 
                              "UNIX", "Fisica", "Laboratorio", "Español"};
        String nuevaCategoria = (String) JOptionPane.showInputDialog(this, "Selecciona una categoría:", "Modificar Categoría",
        															 JOptionPane.PLAIN_MESSAGE, null,
        															 categorias, seleccionada.getCategoria());
        if (nuevaCategoria == null) nuevaCategoria = seleccionada.getCategoria();
        String nuevaFechaLimite = "";
        if (seleccionada instanceof TareaPrioridad) {
            TareaPrioridad tp = (TareaPrioridad) seleccionada;
            nuevaFechaLimite = JOptionPane.showInputDialog(this,
                "Nueva fecha límite (dd-MM-yyyy):", tp.getFechaLimite());
            if (nuevaFechaLimite == null) nuevaFechaLimite = tp.getFechaLimite();
        }
        seleccionada.setTitulo(nuevoTitulo.trim());
        seleccionada.setDescripcion(nuevaDescripcion.trim());
        seleccionada.categoria = nuevaCategoria;
        if (seleccionada instanceof TareaPrioridad && !nuevaFechaLimite.isEmpty()) {
            ((TareaPrioridad) seleccionada).setFechaLimite(nuevaFechaLimite.trim());
        }
        
        ManejadorArchivos.guardarTareas(tareas, actual.getArchivo());
        actualizarListaTareas();
        
        JOptionPane.showMessageDialog(this, 
            "Tarea modificada exitosamente", "Modificación exitosa", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    //mostrar panel
    public void mostrarPanel(String nombrePanel) {
        cardLayout.show(mainPanel, nombrePanel);
    }

    // ==========================================
    // LOGIN
    // ==========================================
    public void login() {
        String usuario  = textUsuario.getText().trim();
        String password = new String(passwordField.getPassword());

        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor, completa todos los campos.", "Campos vacíos",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario encontrado = ManejadorArchivos.buscarUsuario(usuario);
        if (encontrado != null && encontrado.getPassword() == password.hashCode()) {
            actual = encontrado;
            tareas = ManejadorArchivos.cargarTareas(actual.getArchivo());
            subtituloApp.setText("Bienvenido de vuelta, " + encontrado.getNombre());
            JOptionPane.showMessageDialog(this,
                "¡Bienvenido de vuelta, " + encontrado.getNombre() + "!");
            textUsuario.setText("");
            passwordField.setText("");
            actualizarListaTareas();
            mostrarPanel("App");
        } else {
            JOptionPane.showMessageDialog(this,
                "Usuario o contraseña incorrectos.", "Error de inicio de sesión",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    //actualiza las tareas que existan en el archivo (solo muestra pendientes)
    public void actualizarListaTareas() {
        modeloTareas.clear();
        if (tareas == null) return;
        for (Tarea t : tareas) {
            if (!t.isCompletada()) modeloTareas.addElement(t);
        }
    }

    //filtrar por categoria
    public void aplicarFiltros() {
        if (tareas == null) return;
        String catSeleccionada = (String) comboCategoria.getSelectedItem();
        String fechaBuscada    = txtFiltroFecha.getText().trim();
        modeloTareas.clear();
        boolean filtrarFinalizadas = "Finalizadas".equals(catSeleccionada);
        for (Tarea t : tareas) {
            if (filtrarFinalizadas) {
                // Mostrar solo tareas completadas
                if (t.isCompletada()) modeloTareas.addElement(t);
                continue;
            }
            // Para el resto de filtros, excluir completadas
            if (t.isCompletada()) continue;
            boolean pasaCat   = catSeleccionada == null
                             || catSeleccionada.equals("Todas")
                             || catSeleccionada.equalsIgnoreCase(t.getCategoria());
            boolean pasaFecha = true;
            if (!fechaBuscada.isEmpty() && t instanceof TareaPrioridad) {
                String fl = ((TareaPrioridad) t).getFechaLimite();
                pasaFecha = fl != null && fl.equals(fechaBuscada);
            } else if (!fechaBuscada.isEmpty() && !(t instanceof TareaPrioridad)) {
                pasaFecha = false;
            }
            if (pasaCat && pasaFecha) modeloTareas.addElement(t);
        }
        
        if (modeloTareas.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No se encontraron tareas.", "Sin resultados",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void eliminarTarea(){
        int indice = listaTareas.getSelectedIndex();
        if (indice == -1){
            JOptionPane.showMessageDialog(this,"Seleccione una tarea");
            return;
        }
        Tarea seleccionada = listaTareas.getSelectedValue();
        int opc = JOptionPane.showConfirmDialog(this,
            "¿Desea eliminar la tarea?" + seleccionada.getTitulo(), "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION
        );
        if (opc == JOptionPane.YES_OPTION) {
            tareas.remove(seleccionada);
            actualizarListaTareas();
            JOptionPane.showMessageDialog(this,"Tarea eliminada");
        }
    }

    public void agregarTarea() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre de la tarea:");
        if (nombre == null || nombre.trim().isEmpty()) return;

        String descripcion = JOptionPane.showInputDialog(this, "Descripción:");
        if (descripcion == null) descripcion = "";

        // Selector de categoría mediante lista
        String categoria = (String) JOptionPane.showInputDialog(
            this, "Selecciona una categoría:", "Categoría",
            JOptionPane.PLAIN_MESSAGE, null,
            new String[]{"Matematicas", "Redes", "Programacion", "Ensamblador", "UNIX", "Fisica", "Laboratorio", "Español" }, "Categoria");
        if (categoria == null) categoria = "Otro";

        String fechaLimite = JOptionPane.showInputDialog(this,
            "Fecha límite (dd-MM-yyyy). Deja vacío si no aplica:");
        if (fechaLimite == null) fechaLimite = "";

        TareaPrioridad nueva = new TareaPrioridad(
            nombre.trim(), descripcion.trim(), categoria, 1, fechaLimite.trim());
        tareas.add(nueva);
        ManejadorArchivos.guardarTareas(tareas, actual.getArchivo());
        actualizarListaTareas();

        JOptionPane.showMessageDialog(this,
            "Tarea \"" + nombre + "\" agregada correctamente.", "Tarea creada",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void modificarTarea() {
        int indice = listaTareas.getSelectedIndex();
        if (indice == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una tarea para modificar");
            return;
        }
        Tarea seleccionada = listaTareas.getSelectedValue();
        //cambiar titulo
        String nuevoTitulo = JOptionPane.showInputDialog(this, 
            "Nuevo nombre de la tarea:", seleccionada.getTitulo());
        if (nuevoTitulo == null) return;
        if (nuevoTitulo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío");
            return;
        }
        
        //cambiar descripcion
        String nuevaDescripcion = JOptionPane.showInputDialog(this, 
            "Nueva descripción:", seleccionada.getDescripcion());
        if (nuevaDescripcion == null) nuevaDescripcion = seleccionada.getDescripcion();
        
        //categoria
        String[] categorias = {"Matematicas", "Redes", "Programacion", "Ensamblador", 
                              "UNIX", "Fisica", "Laboratorio", "Español"};
        String nuevaCategoria = (String) JOptionPane.showInputDialog(
            this, "Selecciona una categoría:", "Modificar Categoría",
            JOptionPane.PLAIN_MESSAGE, null,
            categorias, seleccionada.getCategoria());
        if (nuevaCategoria == null) nuevaCategoria = seleccionada.getCategoria();
        
        //modificar fecha limite
        String nuevaFechaLimite = "";
        if (seleccionada instanceof TareaPrioridad) {
        	TareaPrioridad tp = (TareaPrioridad) seleccionada;
            nuevaFechaLimite = JOptionPane.showInputDialog(this, "Nueva fecha límite (dd-MM-yyyy):", tp.getFechaLimite());
            if (nuevaFechaLimite == null) nuevaFechaLimite = tp.getFechaLimite();
        }
        
        seleccionada.setTitulo(nuevoTitulo.trim());
        seleccionada.setDescripcion(nuevaDescripcion.trim());
        seleccionada.categoria = nuevaCategoria;
        
        if (seleccionada instanceof TareaPrioridad && !nuevaFechaLimite.isEmpty()) {
            ((TareaPrioridad) seleccionada).setFechaLimite(nuevaFechaLimite.trim());
        }
        
        //guardar cambios
        ManejadorArchivos.guardarTareas(tareas, actual.getArchivo());
        actualizarListaTareas();
        
        JOptionPane.showMessageDialog(this, 
            "Tarea modificada exitosamente", "Modificación exitosa", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    public void registrarUsuario() {
        String nombre   = regNombre.getText().trim();
        String usuario  = regUsuario.getText().trim();
        String password = new String(regPassword.getPassword());

        if (nombre.isEmpty() || usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.",
                "Campos vacíos", JOptionPane.WARNING_MESSAGE); return;
        }
        if (usuario.length() < 4) {
            JOptionPane.showMessageDialog(this, "El nombre de usuario debe tener al menos 4 caracteres.",
                "Usuario inválido", JOptionPane.WARNING_MESSAGE); return;
        }
        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this, "La contraseña debe tener al menos 4 caracteres.",
                "Contraseña insegura", JOptionPane.WARNING_MESSAGE); return;
        }
        if (ManejadorArchivos.buscarUsuario(usuario) != null) {
            JOptionPane.showMessageDialog(this, "El nombre de usuario ya está en uso.",
                "Usuario existente", JOptionPane.WARNING_MESSAGE); return;
        }

        ArrayList<Usuario> usuarios = ManejadorArchivos.cargarUsuarios();
        usuarios.add(new Usuario(usuario, password.hashCode(), nombre));
        ManejadorArchivos.guardarUsuarios(usuarios);

        JOptionPane.showMessageDialog(this,
            "¡Cuenta creada exitosamente!\nBienvenido/a " + nombre,
            "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);

        regNombre.setText(""); regUsuario.setText(""); regPassword.setText("");
        mostrarPanel("Login");
    }
}